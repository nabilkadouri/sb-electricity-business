package com.hb.cda.electricitybusiness.config;

import com.hb.cda.electricitybusiness.enums.ChargingStationStatus;
import com.hb.cda.electricitybusiness.enums.DayOfWeek;
import com.hb.cda.electricitybusiness.model.*;
import com.hb.cda.electricitybusiness.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@Profile("dev") // Seeder uniquement en développement
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LocationStationRepository locationStationRepository;
    private final ChargingStationRepository chargingStationRepository;
    private final TimeslotRepository timeslotRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           LocationStationRepository locationStationRepository,
                           ChargingStationRepository chargingStationRepository,
                           TimeslotRepository timeslotRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.locationStationRepository = locationStationRepository;
        this.chargingStationRepository = chargingStationRepository;
        this.timeslotRepository = timeslotRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        if (chargingStationRepository.count() > 0) {
            System.out.println("Fixtures déjà présentes.");
            return;
        }

        System.out.println("Initialisation des 20 bornes autour de Saint-Priest...");

        // Création propriétaires
        User owner1 = createOwner("owner1@test.com", "Paul", "Martin");
        User owner2 = createOwner("owner2@test.com", "Sophie", "Durand");
        User owner3 = createOwner("owner3@test.com", "Lucas", "Bernard");

        // SAINT-PRIEST
        createStation(owner1,"Borne Saint-Priest Centre","Place Roger Salengro","Saint-Priest","69800",45.6958,4.9426);
        createStation(owner1,"Borne Jean Jaurès","Avenue Jean Jaurès","Saint-Priest","69800",45.7002,4.9475);
        createStation(owner1,"Borne Herriot","Boulevard Edouard Herriot","Saint-Priest","69800",45.6941,4.9368);
        createStation(owner1,"Borne Rue du Lyonnais","Rue du Lyonnais","Saint-Priest","69800",45.6995,4.9542);

        // BRON
        createStation(owner2,"Borne Bron Roosevelt","Avenue Franklin Roosevelt","Bron","69500",45.7332,4.9155);
        createStation(owner2,"Borne Bron Terray","Rue Lionel Terray","Bron","69500",45.7321,4.9059);

        // VENISSIEUX
        createStation(owner2,"Borne Vénissieux Croizat","Boulevard Ambroise Croizat","Vénissieux","69200",45.6965,4.8844);
        createStation(owner2,"Borne Vénissieux Houël","Rue Marcel Houël","Vénissieux","69200",45.6991,4.8892);

        // MEYZIEU
        createStation(owner3,"Borne Meyzieu Verdun","Avenue de Verdun","Meyzieu","69330",45.7673,5.0025);
        createStation(owner3,"Borne Meyzieu République","Rue de la République","Meyzieu","69330",45.7661,4.9998);

        // GENAS
        createStation(owner3,"Borne Genas République","Rue de la République","Genas","69740",45.7324,5.0014);
        createStation(owner3,"Borne Genas Route Lyon","Route de Lyon","Genas","69740",45.7282,4.9930);

        // CORBAS
        createStation(owner1,"Borne Corbas 8 Mai","Avenue du 8 Mai 1945","Corbas","69960",45.6674,4.9028);
        createStation(owner1,"Borne Corbas Centrale","Rue Centrale","Corbas","69960",45.6662,4.9035);

        // DECINES
        createStation(owner2,"Borne Décines Jaurès","Avenue Jean Jaurès","Décines","69150",45.7688,4.9594);
        createStation(owner2,"Borne Décines Zola","Rue Emile Zola","Décines","69150",45.7711,4.9578);

        // CHASSIEU
        createStation(owner3,"Borne Chassieu Lyon","Route de Lyon","Chassieu","69680",45.7442,4.9755);

        // MIONS
        createStation(owner1,"Borne Mions 23 Août","Rue du 23 Août 1944","Mions","69780",45.6621,4.9562);

        // VILLEURBANNE
        createStation(owner2,"Borne Villeurbanne Zola","Cours Emile Zola","Villeurbanne","69100",45.7705,4.8807);

        // LYON 8
        createStation(owner3,"Borne Lyon Berthelot","Avenue Berthelot","Lyon","69008",45.7359,4.8706);

        System.out.println("20 bornes générées avec succès.");
    }

    private User createOwner(String email, String firstName, String lastName) {
        User owner = new User();
        owner.setEmail(email);
        owner.setPassword(passwordEncoder.encode("password123"));
        owner.setFirstName(firstName);
        owner.setName(lastName);
        owner.setOwnsStation(true);
        owner.setPhoneNumber("0600000000");
        owner.setAddress("Adresse propriétaire");
        owner.setPostaleCode("69800");
        owner.setCity("Saint-Priest");
        return userRepository.save(owner);
    }

    private void createStation(User owner, String name,
                               String address, String city,
                               String postalCode,
                               double lat, double lng) {

        LocationStation location = new LocationStation();
        location.setLocationName(name);
        location.setAddress(address);
        location.setCity(city);
        location.setPostaleCode(postalCode);
        location.setLatitude(lat);
        location.setLongitude(lng);

        locationStationRepository.save(location);

        ChargingStation station = new ChargingStation();
        station.setNameStation(name);
        station.setDescription("Station électrique autour de Saint-Priest");
        station.setPower(BigDecimal.valueOf(22));
        station.setPricePerHour(BigDecimal.valueOf(4.5));
        station.setStatus(ChargingStationStatus.CONFIRMED);
        station.setIsAvailable(true);
        station.setPlugType("TYPE2");
        station.setCreatedAt(LocalDateTime.now());
        station.setLocationStation(location);
        station.setUser(owner);

        chargingStationRepository.save(station);

        Timeslot timeslot = new Timeslot();
        timeslot.setDayOfWeek(DayOfWeek.MONDAY);
        timeslot.setStartTime(LocalTime.of(8, 0));
        timeslot.setEndTime(LocalTime.of(18, 0));
        timeslot.setChargingStation(station);

        timeslotRepository.save(timeslot);
    }
}
