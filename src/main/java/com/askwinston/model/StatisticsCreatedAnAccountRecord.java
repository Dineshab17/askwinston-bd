package com.askwinston.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsCreatedAnAccountRecord {

    private long userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userBirthday;
    private String userRegistrationDate;
    private String userProvince;
    private String utmSource;
}
