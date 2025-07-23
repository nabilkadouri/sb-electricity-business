package com.hb.cda.electricitybusiness.repository;

import com.hb.cda.electricitybusiness.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.chargingStations cs " +
            "LEFT JOIN FETCH cs.timeslots " +
            "WHERE u.email = :email")
    Optional<User> findByEmailWithChargingStationsAndTimeslots(String email);

    Optional<User> findByEmailAndCodeCheck(String email, String codeCheck);
}
