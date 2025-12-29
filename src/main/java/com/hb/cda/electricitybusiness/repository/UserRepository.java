package com.hb.cda.electricitybusiness.repository;

import com.hb.cda.electricitybusiness.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(String email);

    @Query("""
        SELECT u FROM User u
        LEFT JOIN FETCH u.chargingStations cs
        LEFT JOIN FETCH cs.timeslots
        WHERE u.email = :email
    """)
    Optional<User> findByEmailWithChargingStationsAndTimeslots(String email);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.codeCheck = :codeCheck")
    Optional<User> findByEmailAndCodeCheck(String email, String codeCheck);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email")
    boolean existsByEmail(String email);
}
