package com.askwinston.service;

import com.askwinston.model.Province;

public interface FaxService {

    String send(long orderId, Province billingProvince, byte[] pdfBytes);
}
