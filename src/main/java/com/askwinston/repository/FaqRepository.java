package com.askwinston.repository;

import com.askwinston.model.Faq;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaqRepository extends CrudRepository<Faq,Long> {
}
