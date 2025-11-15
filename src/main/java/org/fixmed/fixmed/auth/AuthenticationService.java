package org.fixmed.fixmed.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.fixmed.fixmed.config.JwtService;
import org.fixmed.fixmed.exception.DuplicateResourceException;
import org.fixmed.fixmed.exception.InvalidInputException;
import org.fixmed.fixmed.model.Doctors;
import org.fixmed.fixmed.model.Patients;
import org.fixmed.fixmed.model.Users;
import org.fixmed.fixmed.repository.DoctorsRepository;
import org.fixmed.fixmed.repository.PatientsRepository;
import org.fixmed.fixmed.repository.UsersRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final DoctorsRepository doctorsRepository;
    private final PatientsRepository patientsRepository;

    public RegistrationResponse register(RegisterRequest request) {

        // Walidacja unikalności e-maila i username
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Podany adres e-mail jest już używany");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Podana nazwa użytkownika jest już zajęta");
        }

        // Walidacja i przekształcenie płci
        Users.Gender gender;
        try {
            gender = Users.Gender.valueOf(request.getGender().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Nieprawidłowa wartość płci. Dozwolone: MALE lub FEMALE");
        }

        // Tworzenie użytkownika
        Users user = Users.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password_hash(passwordEncoder.encode(request.getPassword()))
                .first_name(request.getFirstName())
                .last_name(request.getLastName())
                .gender(gender)
                .role(request.getRole())
                .enabled(true)
                .build();

        userRepository.save(user);

        // ROLA: PACJENT
        if (request.getRole() == Users.Role.PATIENT) {
            if (patientsRepository.existsByPesel(request.getPesel())) {
                throw new DuplicateResourceException("Użytkownik z podanym numerem PESEL już istnieje");
            }

            Patients patient = Patients.builder()
                    .birth_date(request.getBirthDate())
                    .pesel(request.getPesel())
                    .phone_number(request.getPhoneNumber())
                    .user(user)
                    .build();

            patientsRepository.save(patient);
        }

        // ROLA: LEKARZ
        if (request.getRole() == Users.Role.DOCTOR) {

            // Ręczna walidacja tylko dla roli DOCTOR
            if (request.getLicenseNumber() == null || request.getLicenseNumber().isBlank()) {
                throw new InvalidInputException("Numer prawa wykonywania zawodu jest wymagany");
            }

            if (request.getSpecialization() == null) {
                throw new InvalidInputException("Specjalizacja lekarza jest wymagana");
            }

            if (request.getPhoneNumber() == null || request.getPhoneNumber().isBlank()) {
                throw new InvalidInputException("Numer telefonu lekarza jest wymagany");
            }

            if (doctorsRepository.existsByLicenseNumber(request.getLicenseNumber())) {
                throw new DuplicateResourceException("Podany numer prawa wykonywania zawodu już istnieje");
            }

            Doctors doctor = new Doctors();
            doctor.setLicense_number(request.getLicenseNumber());
            doctor.setPhone_number(request.getPhoneNumber());
            doctor.setCity(request.getCity());
            doctor.setSpecialization(Doctors.Specialization.valueOf(request.getSpecialization()));
            doctor.setUser(user);

            doctorsRepository.save(doctor);
        }

        return RegistrationResponse.builder()
                .message("Rejestracja zakończona pomyślnie. Możesz się teraz zalogować.")
                .timestamp(LocalDateTime.now())
                .build();
    }


    //AUTORYZACJA UŻYTKOWNIKA
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + request.getEmail()));
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = generateRefreshToken(user); // musisz dodać tę metodę

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    //LOGOUT UŻYTKOWNIKA
    public void logout(String token) {
        String username = jwtService.extractUsername(token);
        var user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + username));

        if (jwtService.isTokenValid(token, user)) {
            jwtService.revokeToken(token);
        }
    }

    public boolean authenticateToken(String token) {
        try {
            int id = jwtService.extractUserIdFromToken(token);
            var user = userRepository.getUserById((long) id)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + id));
            return jwtService.isTokenValid(token, user);
        } catch (Exception e) {
            return false;
        }
    }

    public AuthenticationResponse refresh(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        var user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + username));
        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    private String generateRefreshToken(Users user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("userId", user.getId())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(Instant.now().plusMillis(1000L * 60 * 60 * 24 * 30))) // 30 dni
                .signWith(jwtService.getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getRoleFromToken(String token) {
        return jwtService.extractClaim(token, claims -> claims.get("role", String.class));

    }

    public Long getUserIdFromToken(String token) {
        return jwtService.extractClaim(token, claims -> claims.get("userId", Long.class));
    }

}