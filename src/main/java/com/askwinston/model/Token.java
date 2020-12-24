package com.askwinston.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    public enum Type {
        PHARMACIST_CONSULT,
        PASSWORD_RESET
    }

    @Id
    private String id;

    Date creationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Type type;

    Long orderId;

    Long userId;

}
