package com.askwinston.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class MdPostConsultNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int counseledOnSideEffects;

    private int counseledOnSymptomsForEmergency;

    private int patientsConcernsAddressed;

    private int requestedCounselByPharmacy;

    @Column(columnDefinition = "VARCHAR(2048)")
    private String doctorsCustomNotes;

    private String doctorsFullName;

    private String doctorsLicense;

    private Date date;

}
