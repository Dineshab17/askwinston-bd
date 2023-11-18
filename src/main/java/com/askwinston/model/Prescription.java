package com.askwinston.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {
    public static final int DEFAULT_REFILLS = 12;
    public static final int DEFAULT_MONTHS_VALID = 12;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "md_post_consult_note_id")
    private MdPostConsultNote mdPostConsultNote;

    @OneToOne
    @JoinColumn(name = "id_rx_document")
    private Document rxDocument;

    private String rxTransferNumber;

    private String pharmacyNameAndAddress;

    private String pharmacyPhone;

    private Date date;

    private Date toDate;

    private int refills;

    private int refillsLeft;

}
