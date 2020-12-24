package com.askwinston.repository;

import com.askwinston.model.PromoCodeUsed;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromoCodeUsedRepository extends CrudRepository<PromoCodeUsed, Long> {
    List<PromoCodeUsed> findByPatientIdAndPromoCodeId(Long patientId, Long promoCodeId);
}
