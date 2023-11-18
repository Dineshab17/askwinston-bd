package com.askwinston.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ScheduleScheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User doctor;

    @OneToMany(mappedBy = "scheduleScheme", cascade = CascadeType.ALL)
    private List<ScheduleInterval> intervals = new ArrayList<>();
}
