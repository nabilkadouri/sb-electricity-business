package com.hb.cda.electricitybusiness.model;

import com.hb.cda.electricitybusiness.controller.dto.PictureDetailsDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "`users`")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "first_name", length = 255, nullable = false)
    private String firstName;

    @Column(name = "email", length = 180, unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "address", length = 255, nullable = false)
    private String address;

    @Column(name = "postale_code", length = 10, nullable = false)
    private String postaleCode;

    @Column(name = "city", length = 255, nullable = false)
    private String city;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "alt", column = @Column(name = "profile_picture_alt", length = 255)),
            @AttributeOverride(name = "src", column = @Column(name = "profile_picture_src", length = 255)),
            @AttributeOverride(name = "main", column = @Column(name = "profile_picture_is_main"))
    })
    private PictureDetailsDTO profilePicture;

    @Column(name = "code_check", length = 6)
    private String codeCheck;

    @Column(name = "owns_station")
    private Boolean ownsStation = false;

    @Column(name = "roles", nullable = false)
    private String roles = "ROLE_USER";

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ChargingStation> chargingStations = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Booking> bookings = new ArrayList<>();

    {
        this.profilePicture = new PictureDetailsDTO("Avatar par défaut de l'utilisateur", "images/default_avatar.png", true);
    }

    public User(String name, String firstName, String email, String password, String address, String postaleCode, String city) {
        this.name = name;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.address = address;
        this.postaleCode = postaleCode;
        this.city = city;
        this.ownsStation = false;
        this.roles = "ROLE_USER";
    }

    public User(Long id,String name, String firstName, String email, String password, String address, String postaleCode, String city) {
        this.id = id;
        this.name = name;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.address = address;
        this.postaleCode = postaleCode;
        this.city = city;
        this.ownsStation = false;
        this.roles = "ROLE_USER";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.roles));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
