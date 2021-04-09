package com.askwinston.repository;

import com.askwinston.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findByEmail(String email);

    List<User> findByAuthority(User.Authority authority);

    User findByEmailAndSocialLoginSource(String email, String google);
}
