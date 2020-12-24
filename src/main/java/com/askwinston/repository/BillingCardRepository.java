package com.askwinston.repository;

import com.askwinston.model.BillingCard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillingCardRepository extends CrudRepository<BillingCard, String> {
    List<BillingCard> findByUserId(Long userId);
}
