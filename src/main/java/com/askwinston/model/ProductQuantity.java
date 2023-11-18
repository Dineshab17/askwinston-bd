package com.askwinston.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductQuantity {

    public enum Supply {
        PERIOD_30(1, 12),
        PERIOD_90(3, 4),
        OUTBREAK_1(1, 12),
        DEFAULT(1, 12);

        private int refillPeriod; //in month
        private int totalRefills;

        public int getRefillPeriod() {
            return refillPeriod;
        }

        public int getTotalRefills() {
            return totalRefills;
        }

        Supply(int refillPeriod, int totalRefills) {
            this.refillPeriod = refillPeriod;
            this.totalRefills = totalRefills;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private String quantity;
    private Long price;
    private int ordinal;

    private String dosage;

    @Enumerated(EnumType.STRING)
    private Supply supply;

    private Boolean isSingleTimePurchaseAllowed = false;

    private int isDefault;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductQuantity)) return false;
        ProductQuantity quantity1 = (ProductQuantity) o;
        return quantity.equals(quantity1.quantity) &&
                price.equals(quantity1.price) &&
                supply == quantity1.supply;
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity, price, dosage, supply);
    }
}
