package com.askwinston.service.impl;

import com.askwinston.model.Province;
import com.askwinston.service.FaxService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Base64;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RefreshScope
public class FaxServiceImpl implements FaxService {

    private static final EnumSet<Province> EAST_PROVINCES = EnumSet.of(Province.NB, Province.NL,
            Province.NS, Province.ON, Province.PE, Province.QC);
    private static final EnumSet<Province> CENTER_PROVINCES = EnumSet.of(Province.MB,
            Province.NU, Province.SK);
    private static final EnumSet<Province> WEST_PROVINCES = EnumSet.of(Province.AB, Province.BC,
            Province.NT, Province.YT);

    private static final String FAX_URL = "https://www.srfax.com/SRF_SecWebSvc.php";

    @Value("${askwinston.srfax.access-id}")
    private String accessId;

    @Value("${askwinston.srfax.access-password}")
    private String accessPassword;

    @Value("${askwinston.srfax.sender-fax-number}")
    private String senderFaxNumber;

    @Value("${askwinston.srfax.sender-email}")
    private String senderEmail;

    @Value("${askwinston.pharmacy.fax.east}")
    private String pharmacyFaxEast;

    @Value("${askwinston.pharmacy.fax.center}")
    private String pharmacyFaxCenter;

    @Value("${askwinston.pharmacy.fax.west}")
    private String pharmacyFaxWest;

    @Value("${askwinston.dev:true}")
    private boolean isTest;

    @Override
    public String send(long orderId, Province billingProvince, byte[] pdfBytes) {
        if (isTest) return null;

        Map<String, String> queueFaxBody = new HashMap<>();
        queueFaxBody.put("action", "Queue_Fax");
        queueFaxBody.put("access_id", accessId);
        queueFaxBody.put("access_pwd", accessPassword);
        queueFaxBody.put("sCallerID", senderFaxNumber);
        queueFaxBody.put("sSenderEmail", senderEmail);
        queueFaxBody.put("sFaxType", "SINGLE");
        String toNumber = choosePharmacyFaxNumber(billingProvince);
        queueFaxBody.put("sToFaxNumber", toNumber);
        queueFaxBody.put("sFileName_1", "rx-aw-oder-" + orderId + ".pdf");
        queueFaxBody.put("sFileContent_1", Base64.getEncoder().encodeToString(pdfBytes));
        queueFaxBody.put("sCPSubject", "Ask Winston Order No " + orderId);

        try {
            RestTemplate rt = new RestTemplate();
            HttpEntity<Map<String, String>> request = new HttpEntity<>(queueFaxBody, new HttpHeaders());
            ResponseEntity<String> response = rt.postForEntity(FAX_URL, request, String.class);
            String faxDetailsID = parseSRFaxResponse(response.getBody());
            log.info("Success sending fax via SRFax to [" + toNumber + "] for oder# [" + orderId +
                    "]. Returned FaxDetailsID=[" + faxDetailsID + "]");
            return faxDetailsID;
        } catch (Exception e) {
            log.error("Failed to send fax via SRFax to [" + toNumber + "] for oder# [" + orderId + "]", e);
            throw new IllegalArgumentException("Failed to send fax via SRFax to [" + toNumber + "] for oder# [" +
                    orderId + "]", e);
        }
    }

    static String parseSRFaxResponse(String response) {
        if (response == null) {
            throw new SRFaxException("Response is null");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = mapper.readValue(response, new TypeReference<Map<String, String>>() {
            });
            String status = map.get("Status");
            String result = map.get("Result");
            if (status == null || !status.equals("Success")) {
                throw new SRFaxException("SRFax responded with Status=[" + status + "], Result=[" +
                        result + "]");
            }
            return result;
        } catch (IOException e) {
            throw new SRFaxException("Error parsing response", e);
        }

    }

    private String choosePharmacyFaxNumber(Province billingProvince) {
        if (EAST_PROVINCES.contains(billingProvince)) {
            return pharmacyFaxEast;
        } else if (CENTER_PROVINCES.contains(billingProvince)) {
            return pharmacyFaxCenter;
        } else if (WEST_PROVINCES.contains(billingProvince)) {
            return pharmacyFaxWest;
        } else {
            return pharmacyFaxEast;
        }
    }

    static class SRFaxException extends RuntimeException {
        SRFaxException(String message) {
            super(message);
        }

        SRFaxException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
