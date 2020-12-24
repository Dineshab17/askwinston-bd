package com.askwinston.model;

import com.askwinston.notification.Notifiable;
import com.askwinston.subscription.ProductSubscription;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class PurchaseOrder implements Notifiable {
    public enum Status {
        NEW(true),
        IN_PROGRESS(true),
        WAITING_PHARMACY_RX_CHECK(true),
        WAITING_QUESTIONNAIRE(true),
        WAITING_DOCTOR(true),
        REJECTED(false),
        CANCELLED(false),
        WAITING_PHARMACIST(true),
        PACKAGING(false),
        DELIVERING(false),
        PAUSED(true),
        PAUSED_RX_TRANSFER(true),
        PAUSED_BY_PATIENT(true);

        private boolean cancelable;

        Status(boolean cancelable) {
            this.cancelable = cancelable;
        }

        public boolean isCancelable() {
            return cancelable;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long number;
    private Long subNumber;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "subscription_id")
    private ProductSubscription subscription = null;

    @OneToMany(
            mappedBy = "purchaseOrder",
            cascade = CascadeType.ALL)
    private List<PurchaseOrderItem> items;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    private Date date;
    private LocalDate shippingDate;

    private String shippingAddressLine1;
    private String shippingAddressLine2;
    private String shippingAddressCity;

    @Enumerated(EnumType.STRING)
    private Province shippingAddressProvince;
    private String shippingAddressPostalCode;
    private String shippingAddressCountry;

    private String billingInfo;

    private String billingAddressLine1;
    private String billingAddressLine2;
    private String billingAddressCity;

    @Enumerated(EnumType.STRING)
    private Province billingAddressProvince;
    private String billingAddressPostalCode;
    private String billingAddressCountry;

    private String transactionId;

    private long cartPrice;
    private long shippingPrice;
    private long taxes;
    private long discount;
    private long orderPrice;
    private Long coPay = null;

    @Enumerated(EnumType.STRING)
    private Courier courier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private String shippingTrackingNumber;
    private String shippingTrackingUrl;
    private String pharmacyRxId;

    public String calculateBillingAddressLine3() {
        return getBillingAddressCity() + ", " +
                getBillingAddressProvince() + ", " +
                getBillingAddressPostalCode() + ", " +
                getBillingAddressCountry();
    }

    public String calculateShippingAddressLine3() {
        return getShippingAddressCity() + ", " +
                getShippingAddressProvince() + ", " +
                getShippingAddressPostalCode() + ", " +
                getShippingAddressCountry();
    }

    private int salt = 0;
    private String promoCode;
}
