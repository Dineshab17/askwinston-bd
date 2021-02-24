package com.askwinston.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserReportRecord {

    private String name;
    private String email;
    private String phone;
    private String province;
    private String registrationDate;
    private String utmSource;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserReportRecord)) return false;
        UserReportRecord that = (UserReportRecord) o;
        if (email != null) {
            return email.equalsIgnoreCase(that.email);
        } else {
            return that.email == null;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
