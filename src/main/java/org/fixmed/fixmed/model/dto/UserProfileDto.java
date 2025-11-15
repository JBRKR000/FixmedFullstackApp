package org.fixmed.fixmed.model.dto;

import lombok.Data;
import org.fixmed.fixmed.model.Users;

@Data
public class UserProfileDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Users.Gender gender;
    private String phoneNumber;
    private String pesel; // readonly
    private String dateOfBirth;
}