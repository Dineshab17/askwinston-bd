package com.askwinston.repository;

import com.askwinston.model.Prescription;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PrescriptionRepository extends CrudRepository<Prescription, Long> {
    List<Prescription> findByToDateBetween(Date from, Date to);
}
