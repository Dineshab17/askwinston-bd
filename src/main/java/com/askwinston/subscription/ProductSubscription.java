package com.askwinston.subscription;

import com.askwinston.model.*;
import com.askwinston.notification.Notifiable;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Data
@Entity
public class ProductSubscription implements Notifiable {

    private static Comparator<PurchaseOrder> orderSubNumberComparator = Comparator.comparing(PurchaseOrder::getSubNumber);

    public enum Status {
        WAITING_DOCTOR, //Waiting doctor consultation
        WAITING_PHARMACY_RX_CHECK,
        REJECTED,       //Rejected by doctor
        CANCELLED,
        ACTIVE,
        PAUSED,
        PAUSED_RX_TRANSFER,
        PAUSED_BY_PATIENT,
        COMPLETED,
        FINISHED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "doctor_slot_id")
    private DoctorSlot appointment;

    @OneToOne
    private Prescription prescription;

    @OneToMany(
            mappedBy = "subscription",
            cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ProductSubscriptionItem> items;

    @OneToMany(
            mappedBy = "subscription",
            cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PurchaseOrder> orders = new ArrayList<>();

    public BillingCard getBillingCard() {
        return user.getBillingCards()
                .stream()
                .filter(BillingCard::isPrimary)
                .findFirst()
                .orElse(null);
    }

    public ShippingAddress getShippingAddress() {
        return user.getShippingAddresses()
                .stream()
                .filter(ShippingAddress::isPrimary)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Patient " + user.getFirstName() + " " + user.getLastName() + " haven't primary shipping address"));
    }

    private LocalDate date;
    private LocalDateTime creationDate;
    private LocalDate nextOrderDate;
    private LocalDate pauseDate;

    private String notes = "";
    private String finishNotes = "";
    private String pharmacyId = null;

    private Integer period;

    private Integer totalRefills;

    private Long orderPrice;

    private Long discount;

    private boolean skipNext = false;

    public String calculateBillingAddressLine3() {
        BillingCard billingCard = getBillingCard();
        if (billingCard != null)
            return billingCard.getAddressCity() + ", " +
                    billingCard.getAddressProvince() + ", " +
                    billingCard.getAddressPostalCode() + ", " +
                    billingCard.getAddressCountry();
        else return "";
    }

    public String calculateShippingAddressLine3() {
        ShippingAddress shippingAddress = getShippingAddress();
        return shippingAddress.getAddressCity() + ", " +
                shippingAddress.getAddressProvince() + ", " +
                shippingAddress.getAddressPostalCode() + ", " +
                shippingAddress.getAddressCountry();
    }

    public Long getLastOrderSubNumber() {
        Optional<PurchaseOrder> order = orders.stream()
                .filter(purchaseOrder -> purchaseOrder.getSubNumber() != null)
                .max(orderSubNumberComparator);
        return order.map(purchaseOrder -> purchaseOrder.getSubNumber() + 1).orElse(1L);
    }

    @ManyToOne
    @JoinColumn(name = "promo_code_id")
    private PromoCode promoCode;

}
