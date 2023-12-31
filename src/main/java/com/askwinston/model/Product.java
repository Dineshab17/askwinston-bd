package com.askwinston.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    public enum Category {
        PILLS,
        FOAM,
        CREAM,
        COMBO_PACK
    }

    public enum ProblemCategory {
        ED("Erectile Dysfunction"),
        HL("Hair Loss"),
        AC("Acne"),
        CS("Cold Sores"),
        AL("Allergies");

        private String name;

        ProblemCategory(String name) {
            this.name = name;
        }

        public static ProblemCategory fromName(String name) {
            for (ProblemCategory category : ProblemCategory.values()) {
                if (category.name.equals(name)) {
                    return category;
                }
            }
            throw new IllegalArgumentException("Problem Category with name [" + name + "] not found");
        }

        public String getName() {
            return name;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String subName;

    @Column(columnDefinition = "VARCHAR(2048)")
    private String description;

    @Column(columnDefinition = "VARCHAR(2048)")
    private String productPageText;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private String safetyInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    private ProblemCategory problemCategory;

    private boolean isCombo;

    private String startingAtPrice;

    private String startingAtForm;

    @Column(columnDefinition = "VARCHAR(4096)")
    private String dosingTips;

    @Column(columnDefinition = "VARCHAR(4096)")
    private String ingredient;

    @Column(columnDefinition = "VARCHAR(4096)")
    private String landingPageText;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ProductQuantity> quantities;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Faq> frequentlyAskedQuestions;
}
