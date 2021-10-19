package com.askwinston.repository;

import com.askwinston.model.PurchaseOrderItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderItemRepository extends CrudRepository<PurchaseOrderItem, Long> {
    PurchaseOrderItem findByPurchaseOrderId(Long id);
}
