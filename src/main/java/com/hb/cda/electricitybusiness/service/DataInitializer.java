package com.hb.cda.electricitybusiness.service;

import com.hb.cda.electricitybusiness.enums.BookingStatus;
import com.hb.cda.electricitybusiness.enums.ChargingStationStatus;
import com.hb.cda.electricitybusiness.enums.DayOfWeek;
import com.hb.cda.electricitybusiness.enums.PaymentMethod;
import com.hb.cda.electricitybusiness.model.*;
import com.hb.cda.electricitybusiness.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final LocationStationRepository locationStationRepository;
    private final ChargingStationRepository chargingStationRepository;
    private final TimeslotRepository timeslotRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, LocationStationRepository locationStationRepository, ChargingStationRepository chargingStationRepository, TimeslotRepository timeslotRepository, BookingRepository bookingRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.locationStationRepository = locationStationRepository;
        this.chargingStationRepository = chargingStationRepository;
        this.timeslotRepository = timeslotRepository;
        this.bookingRepository = bookingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            System.out.println("La base de donnée dispose déja de données.");
            return;
        }

        System.out.println("Initialisation des données de fixture...");

        //Fixture pour l'ajout de users
        User user1 = new User();
        user1.setEmail("user1@test.com");
        user1.setPassword(passwordEncoder.encode("password123")); // Mot de passe crypté
        user1.setFirstName("Jean");
        user1.setName("Dupont");
        user1.setOwnsStation(false);
        user1.setPhoneNumber("0612345678");
        user1.setAddress("123 Rue de la Gare");
        user1.setPostaleCode("75019");
        user1.setCity("Paris");
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("owner1@test.com");
        user2.setPassword(passwordEncoder.encode("password123"));
        user2.setFirstName("Marie");
        user2.setName("Curie");
        user2.setOwnsStation(true); // Propriétaire de station
        user2.setPhoneNumber("0698765432");
        user2.setAddress("456 Avenue des Champs");
        user2.setPostaleCode("69001");
        user2.setCity("Lyon");
        userRepository.save(user2);

        //Fixture pour l'ajout de bornes
        //LocationStation
        LocationStation location1 = new LocationStation();
        location1.setLocationName("Place de la Mairie");
        location1.setAddress("1 Rue de la Mairie");
        location1.setCity("Paris");
        location1.setPostaleCode("75001");
        location1.setLatitude(48.8566);
        location1.setLongitude(2.3522);
        locationStationRepository.save(location1);

        LocationStation location2 = new LocationStation();
        location2.setLocationName("Gare Part-Dieu");
        location2.setAddress("1 Place de la Gare");
        location2.setCity("Lyon");
        location2.setPostaleCode("69003");
        location2.setLatitude(45.7597);
        location2.setLongitude(4.8424);
        locationStationRepository.save(location2);

        //ChargingStation
        ChargingStation station1 = new ChargingStation();
        station1.setNameStation("Borne de Test 1");
        station1.setDescription("Station rapide type 1");
        station1.setPower(BigDecimal.valueOf(22.0));
        station1.setPricePerHour(BigDecimal.valueOf(6.00));
        station1.setStatus(ChargingStationStatus.PENDING);
        station1.setIsAvailable(true);
        station1.setPlugType("TYPE2");
        station1.setCreatedAt(LocalDateTime.now());
        station1.setLocationStation(location1);
        station1.setUser(user2);
        chargingStationRepository.save(station1);

        ChargingStation station2 = new ChargingStation();
        station2.setNameStation("Borne de Test 2");
        station2.setDescription("Station standard");
        station2.setPower(BigDecimal.valueOf(7.4));
        station2.setPricePerHour(BigDecimal.valueOf(3.50));
        station2.setStatus(ChargingStationStatus.PENDING);
        station2.setIsAvailable(true);
        station2.setPlugType("TYPE2");
        station2.setCreatedAt(LocalDateTime.now());
        station2.setLocationStation(location2);
        station2.setUser(user2);
        chargingStationRepository.save(station2);

        //Timeslots
        Timeslot timeslot1 = new Timeslot();
        timeslot1.setDayOfWeek(DayOfWeek.MONDAY);
        timeslot1.setStartTime(LocalDateTime.now().withHour(9).withMinute(0).withSecond(0));
        timeslot1.setEndTime(LocalDateTime.now().withHour(10).withMinute(0).withSecond(0));
        timeslot1.setIsAvailable(true);
        timeslot1.setChargingStation(station1);
        timeslotRepository.save(timeslot1);

        Timeslot timeslot2 = new Timeslot();
        timeslot2.setDayOfWeek(DayOfWeek.MONDAY);
        timeslot2.setStartTime(LocalDateTime.now().withHour(10).withMinute(0).withSecond(0));
        timeslot2.setEndTime(LocalDateTime.now().withHour(11).withMinute(0).withSecond(0));
        timeslot2.setIsAvailable(true);
        timeslot2.setChargingStation(station1);
        timeslotRepository.save(timeslot2);

        //Fixture pour une reservation de borne
        Booking booking1 = new Booking();
        booking1.setCreatedAt(LocalDateTime.now());
        booking1.setStartDate(LocalDateTime.now().plusDays(1).withHour(14));
        booking1.setEndDate(LocalDateTime.now().plusDays(1).withHour(15));
        booking1.setTotalAmount(BigDecimal.valueOf(6.00));
        booking1.setStatus(BookingStatus.PENDING);
        booking1.setUser(user1);
        booking1.setChargingStation(station1);
        booking1.setPaymentType(PaymentMethod.PAYPAL);
        bookingRepository.save(booking1);

        System.out.println("Data initialization complete.");
    }


}
