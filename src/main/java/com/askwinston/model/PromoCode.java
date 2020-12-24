package com.askwinston.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class PromoCode {
    public enum Type {
        AMOUNT, PERCENT, GIFT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private LocalDate fromDate;
    private LocalDate toDate;
    private long value = 0;

    @Enumerated(EnumType.STRING)
    @ElementCollection
    private List<Product.ProblemCategory> problemCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;
}
