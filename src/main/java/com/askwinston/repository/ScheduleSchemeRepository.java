package com.askwinston.repository;

import com.askwinston.model.ScheduleScheme;
import com.askwinston.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ScheduleSchemeRepository extends CrudRepository<ScheduleScheme, Long> {

    Optional<ScheduleScheme> findByDoctor(User doctor);

    void deleteByDoctor(User doctor);
}
