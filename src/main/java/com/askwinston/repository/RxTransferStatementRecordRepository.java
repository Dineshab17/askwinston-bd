package com.askwinston.repository;

import com.askwinston.model.RxTransferStateRecord;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RxTransferStatementRecordRepository extends CrudRepository<RxTransferStateRecord, Long> {

    Optional<RxTransferStateRecord> findByUserId(long userId);

    void deleteByUserId(long userId);
}
