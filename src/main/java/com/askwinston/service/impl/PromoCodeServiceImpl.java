package com.askwinston.service.impl;

import com.askwinston.exception.PromoCodeException;
import com.askwinston.model.*;
import com.askwinston.repository.PromoCodeRepository;
import com.askwinston.repository.PromoCodeUsedRepository;
import com.askwinston.service.PromoCodeService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDate.now;

@Service
public class PromoCodeServiceImpl implements PromoCodeService {
    private PromoCodeRepository promoCodeRepository;
    private PromoCodeUsedRepository promoCodeUsedRepository;

    public PromoCodeServiceImpl(PromoCodeRepository promoCodeRepository,
                                PromoCodeUsedRepository promoCodeUsedRepository) {
        this.promoCodeRepository = promoCodeRepository;
        this.promoCodeUsedRepository = promoCodeUsedRepository;
    }

    @Override
    public PromoCode getByCode(String code) throws PromoCodeException {
        PromoCode promoCode = promoCodeRepository.getByCode(code);
        if (promoCode == null) {
            throw new PromoCodeException("Promo code not found");
        }
        if (promoCode.getToDate().isBefore(now())) {
            throw new PromoCodeException("Promo code has expired");
        }
        if (promoCode.getFromDate().isAfter(now())) {
            throw new PromoCodeException("Promo code isn't yet available");
        }
        return promoCode;
    }

    @Override
    public boolean isPromoCodeUsedByPatient(User patient, PromoCode promoCode) {
        List<PromoCodeUsed> usersPromoCodes = promoCodeUsedRepository.findByPatientIdAndPromoCodeId(patient.getId(), promoCode.getId());
        List<PromoCodeUsed> activatedPromoCodes = usersPromoCodes.stream().filter(promoCodeUsed ->
                !promoCodeUsed.getPurchaseOrder().getStatus().equals(PurchaseOrder.Status.REJECTED)
                        && !promoCodeUsed.getPurchaseOrder().getStatus().equals(PurchaseOrder.Status.CANCELLED)
        ).collect(Collectors.toList());
        return activatedPromoCodes.size() != 0;
    }

    @Override
    public void savePromoCodeUsageByPatient(User patient, PurchaseOrder purchaseOrder, PromoCode promoCode) {
        PromoCodeUsed promoCodeUsed = PromoCodeUsed.builder()
                .patient(patient)
                .purchaseOrder(purchaseOrder)
                .promoCode(promoCode)
                .build();
        promoCodeUsedRepository.save(promoCodeUsed);
    }

    @Override
    public void deletePromoCode(String code) {
        PromoCode promoCode = promoCodeRepository.getByCode(code);
        if (promoCode == null) {
            throw new PromoCodeException("Promo code not found");
        }
        promoCodeRepository.delete(promoCode);
    }

    @Override
    public PromoCode savePromoCode(String code, LocalDate fromDate, LocalDate toDate, Long value, List<Product.ProblemCategory> category, PromoCode.Type type) {
        if (promoCodeRepository.getByCode(code) != null) {
            throw new PromoCodeException("Promo code already exists");
        }
        PromoCode promoCode = new PromoCode();
        promoCode.setCode(code);
        promoCode.setFromDate(fromDate);
        promoCode.setToDate(toDate);
        promoCode.setValue(value == null ? 0 : value);
        List<Product.ProblemCategory> categories = category == null ? null : category.isEmpty() ? null : category;
        promoCode.setProblemCategory(categories);
        promoCode.setType(type);
        return promoCodeRepository.save(promoCode);
    }
}
