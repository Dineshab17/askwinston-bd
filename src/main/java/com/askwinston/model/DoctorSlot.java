package com.askwinston.model;

import com.askwinston.notification.Notifiable;
import com.askwinston.subscription.ProductSubscription;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Entity
public class DoctorSlot implements Notifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private LocalTime time;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    private User patient;

    @OneToMany(mappedBy = "appointment")
    private List<ProductSubscription> subscriptions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id")
    private User doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "schedule_scheme_id")
    private ScheduleScheme scheme;

    private boolean isFree;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoctorSlot)) return false;
        DoctorSlot slot = (DoctorSlot) o;
        return date.equals(slot.date) &&
                time.equals(slot.time) &&
                Objects.equals(doctor, slot.doctor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, time, doctor);
    }
}
