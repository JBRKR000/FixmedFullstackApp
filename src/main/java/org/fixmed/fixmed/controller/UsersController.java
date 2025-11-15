package org.fixmed.fixmed.controller;

import lombok.RequiredArgsConstructor;
import org.fixmed.fixmed.repository.UsersRepository;
import org.fixmed.fixmed.service.PatientsService;
import org.fixmed.fixmed.service.UsersService;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.fixmed.fixmed.auth.AuthenticationService;
import org.fixmed.fixmed.model.Users;
import org.fixmed.fixmed.model.dto.UserProfileDto;
import  org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {
    private final UsersService usersService;
    private final PatientsService patientsService;
    private final AuthenticationService authenticationService;

    @GetMapping("/{id}")
    public Optional<Users> getUserById(@PathVariable Long id) {
        return usersService.getUserById(id);
    }

    @GetMapping
    public Page<Users> getAllUsers(Pageable pageable) {
        return usersService.getAllUsers(pageable);
    }

    @PostMapping
    public Users saveUser(@RequestBody Users user) {
        return usersService.saveUser(user);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        Long userId = authenticationService.getUserIdFromToken(authHeader.replace("Bearer ", ""));
        Users user = usersService.getUserById(userId).orElseThrow();
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirst_name());
        dto.setLastName(user.getLast_name());
        dto.setEmail(user.getEmail());
        dto.setGender(user.getGender());
        patientsService.getPatientByUserId(userId).ifPresent(patient -> {
            dto.setPhoneNumber(patient.getPhone_number());
            dto.setPesel(patient.getPesel());
            dto.setDateOfBirth(String.valueOf(patient.getBirth_date()));
        });

        return ResponseEntity.ok(dto);
    }
    @PutMapping("/me")
    public ResponseEntity<UserProfileDto> updateCurrentUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserProfileDto updateDto) {
        Long userId = authenticationService.getUserIdFromToken(authHeader.replace("Bearer ", ""));
        Users user = usersService.getUserById(userId).orElseThrow();
        user.setFirst_name(updateDto.getFirstName());
        user.setLast_name(updateDto.getLastName());
        user.setEmail(updateDto.getEmail());
        user.setGender(updateDto.getGender());
        usersService.saveUser(user);

        patientsService.getPatientByUserId(userId).ifPresent(patient -> {
            if (updateDto.getPhoneNumber() != null) {
                patient.setPhone_number(updateDto.getPhoneNumber());
            }
            patientsService.savePatient(patient);
        });

        return getCurrentUser(authHeader);
    }
}
