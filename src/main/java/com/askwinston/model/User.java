package com.askwinston.model;

import com.askwinston.notification.Notifiable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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

        private Product.ProblemCategory[] categories;
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

}
