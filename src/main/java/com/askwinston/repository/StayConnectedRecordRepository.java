package com.askwinston.repository;

import com.askwinston.model.StayConnectedRecord;
import org.springframework.data.repository.CrudRepository;

public interface StayConnectedRecordRepository extends CrudRepository<StayConnectedRecord, Long> {

    StayConnectedRecord findByEmail(String email);

    void deleteByEmail(String email);
}
