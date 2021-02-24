package com.askwinston.model;

import com.askwinston.notification.Notifiable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactUsRecord implements Notifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotNull
    private String email;

    @NotNull
    private String name;

    @NotNull
    @Pattern(regexp = "[\\d\\+\\s-\\(\\)]{5,64}")
    private String phone;

    @NotNull
    @Column( length = 100000 )
    private String message;
    private String utmSource;

}

