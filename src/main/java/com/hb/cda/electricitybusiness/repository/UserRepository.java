package com.hb.cda.electricitybusiness.repository;

import com.hb.cda.electricitybusiness.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndCodeCheck(String email, String codeCheck);
}
