package com.hb.cda.electricitybusiness.testutil;

import com.hb.cda.electricitybusiness.enums.ChargingStationStatus;
import com.hb.cda.electricitybusiness.model.*;

import java.math.BigDecimal;

public class TestDataFactory {

    public static User createUser() {
        User u = new User();
        u.setName("Doe");
        u.setFirstName("John");
        u.setEmail("john@example.com");
        u.setPassword("password123");
        u.setAddress("10 rue du Test");
        u.setPostaleCode("75000");
        u.setCity("Paris");
        u.setPhoneNumber("0600000000");
        u.setOwnsStation(false);
        return u;
    }

    public static LocationStation createLocation() {
        LocationStation loc = new LocationStation();
        loc.setLocationName("Location Test");
        loc.setAddress("Rue de Paris");
        loc.setPostaleCode("75001");
        loc.setCity("Paris");
        loc.setLatitude(48.85);
        loc.setLongitude(2.34);
        return loc;
    }

    public static ChargingStation createChargingStation(User user, LocationStation location) {
        ChargingStation cs = new ChargingStation();
        cs.setNameStation("Test Station");
        cs.setDescription("Une borne de test");
        cs.setPower(new BigDecimal("22.00"));
        cs.setPricePerHour(new BigDecimal("5.00"));
        cs.setStatus(ChargingStationStatus.PENDING);
        cs.setIsAvailable(true);

        cs.setUser(user);
        cs.setLocationStation(location);

        return cs;
    }

}