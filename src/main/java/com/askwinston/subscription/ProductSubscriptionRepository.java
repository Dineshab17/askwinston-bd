package com.askwinston.subscription;

import com.askwinston.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductSubscriptionRepository extends CrudRepository<ProductSubscription, Long> {

    List<ProductSubscription> findAllByUserAndStatus(User user, ProductSubscription.Status status);

    List<ProductSubscription> findAllByUser(User user);

    List<ProductSubscription> findAllByStatus(ProductSubscription.Status status);

    List<ProductSubscription> findAllByStatusIn(ProductSubscription.Status... status);

    ProductSubscription findByPrescriptionId(Long id);

    List<ProductSubscription> findAllByUserIdAndStatus(Long userId, ProductSubscription.Status status);
}
