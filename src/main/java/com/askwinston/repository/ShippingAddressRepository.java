package com.askwinston.repository;

import com.askwinston.model.ShippingAddress;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingAddressRepository extends CrudRepository<ShippingAddress, Long> {
    List<ShippingAddress> findByUserId(Long userId);
}
