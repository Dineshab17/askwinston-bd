package com.askwinston.service;

import com.askwinston.exception.PromoCodeException;
import com.askwinston.model.Product;
import com.askwinston.model.PromoCode;
import com.askwinston.model.PurchaseOrder;
import com.askwinston.model.User;

import java.time.LocalDate;
import java.util.List;

public interface PromoCodeService {
    PromoCode getByCode(String code);

    boolean isPromoCodeUsedByPatient(User patient, PromoCode promoCode);

    void savePromoCodeUsageByPatient(User patient, PurchaseOrder purchaseOrder, PromoCode promoCode);

    void deletePromoCode(String code);

    PromoCode savePromoCode(String code, LocalDate fromDate, LocalDate toDate, Long value, List<Product.ProblemCategory> category, PromoCode.Type type);

}

