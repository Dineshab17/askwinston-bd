package com.askwinston.repository;

import com.askwinston.model.PurchaseOrder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseOrderRepository extends CrudRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findByUserId(Long userId);

    List<PurchaseOrder> findByStatus(PurchaseOrder.Status status);

    List<PurchaseOrder> findByUserIdAndStatus(Long userId, PurchaseOrder.Status status);

    List<PurchaseOrder> findByDoctorIdAndStatus(Long doctorId, PurchaseOrder.Status status);

    PurchaseOrder findFirstByNumberOrderBySubNumberDesc(Long number);

    List<PurchaseOrder> findByStatusAndShippingDateBetween(PurchaseOrder.Status status, LocalDate shippingDate, LocalDate shippingDate2);

}
