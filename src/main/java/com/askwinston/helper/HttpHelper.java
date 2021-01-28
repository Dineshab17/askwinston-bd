package com.askwinston.helper;

import com.askwinston.model.DocumentResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriUtils;

public class HttpHelper {
    private HttpHelper(){}

    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * @param resource
     * @return ResponseEntity<InputStreamResource>
     * To convert the document resource into input stream resource for download
     */
    public static ResponseEntity<InputStreamResource> fileDownload(DocumentResource resource) {
        return new ResponseEntity<>(new InputStreamResource(resource.getInputStream()), headers(resource.getFilename(), resource.getContentType(), "attachment"), HttpStatus.OK);
    }

    /**
     * @param resource
     * @return ResponseEntity<InputStreamResource>
     * To convert document resource into input stream resource to view it
     */
    public static ResponseEntity<InputStreamResource> fileView(DocumentResource resource) {
        return new ResponseEntity<>(new InputStreamResource(resource.getInputStream()), headers(resource.getFilename(), resource.getContentType(), "inline"), HttpStatus.OK);
    }

    /**
     * @param fileName
     * @param contentType
     * @param actionType
     * @return HttpHeaders
     * To add header information into httpheader
     */
    public static HttpHeaders headers(String fileName, String contentType, String actionType) {
        HttpHeaders headers = new HttpHeaders();
        if (contentType != null) headers.add("Content-Type", contentType);
        headers.add("Content-Disposition", actionType + ";filename=\"" + UriUtils.encodePath(fileName, DEFAULT_ENCODING) + "\"");
        return headers;
    }

}
