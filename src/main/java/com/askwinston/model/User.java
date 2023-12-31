package com.askwinston.model;

import com.askwinston.notification.Notifiable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Notifiable {
    public enum Authority {
        PATIENT, DOCTOR, PHARMACIST, ADMIN
    }

    public enum DoctorSpecialisation {

        GENERAL(Product.ProblemCategory.values());

        DoctorSpecialisation(Product.ProblemCategory[] categories) {
            this.categories = categories;
        }

        public Product.ProblemCategory[] getCategories() {
            return categories;
        }

        private final Product.ProblemCategory[] categories;
    }
    
    public enum LoginType {
        CUSTOM,
        GOOGLE,
        CUSTOM_GOOGLE;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private String birthday;
    private String utmSource;
    @Enumerated(EnumType.ORDINAL)
    private LoginType loginType = LoginType.CUSTOM;

    @OneToOne
    @JoinColumn(name = "id_img_document")
    private Document idDocument;

    @OneToOne
    @JoinColumn(name = "id_insurance_document")
    private Document insuranceDocument;

    @Enumerated(EnumType.STRING)
    private Province province;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.REMOVE
    )
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ShippingAddress> shippingAddresses;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.REMOVE
    )
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<BillingCard> billingCards;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority;

    private String timezone;

    // Doctors related fields

    private String doctorsLicenseNumber;

    @OneToOne
    private Cart cart = new Cart();

    private Date registrationDate;

    @Enumerated(EnumType.STRING)
    private DoctorSpecialisation specialisation;


    public BillingCard getPrimaryBillingCard() {
        AtomicReference<BillingCard> primaryCard = new AtomicReference<>();
        billingCards.forEach(billingCard -> {
            if (billingCard.isPrimary()) {
                primaryCard.set(billingCard);
            }
        });

        return primaryCard.get();
    }
}
