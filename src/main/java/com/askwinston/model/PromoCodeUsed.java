package com.askwinston.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoCodeUsed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "promo_code_id", nullable = false)
    private PromoCode promoCode;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "order_id", nullable = false)
    private PurchaseOrder purchaseOrder;
}
