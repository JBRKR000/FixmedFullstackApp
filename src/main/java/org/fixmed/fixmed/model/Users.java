package org.fixmed.fixmed.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Builder
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false, length = 100, unique = true)
    private String username;

    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String password_hash;

    @Column(name = "first_name", nullable = false, length = 255)
    private String first_name;

    @Column(name = "last_name", nullable = false, length = 255)
    private String last_name;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Column(name = "gender", nullable = true, length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created_at;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public String getPassword() {
        return password_hash;
    }



    public enum Role {
        ADMIN, DOCTOR, PATIENT, RECEPTIONIST
    }

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}
