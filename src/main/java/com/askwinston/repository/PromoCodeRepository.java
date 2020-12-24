package com.askwinston.repository;

import com.askwinston.model.PromoCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromoCodeRepository extends CrudRepository<PromoCode, Long> {
    PromoCode getByCode(String code);
}
