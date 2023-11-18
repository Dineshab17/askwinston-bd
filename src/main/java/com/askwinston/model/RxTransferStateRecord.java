package com.askwinston.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class RxTransferStateRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productCategoryLabel;
    private String productCategoryValue;
    private String productLabel;
    private String productValue;
    private String quantityLabel;
    private String quantityValue;
    private Long userId;
    private int step;
    private String rxNumber;
    private String pharmacyName;
    private String pharmacyPhone;
    private String bottleImageId;
}
