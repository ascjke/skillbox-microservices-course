package ru.borisov.users.controller.request;

import lombok.Builder;
import lombok.Data;
import ru.borisov.users.model.Gender;

import java.time.LocalDate;

@Data
@Builder
public class UpdateUserInfoRequest {

    private String lastName;
    private String firstName;
    private String middleName;
    private Gender gender;
    private LocalDate birthDate;
    private String city;
    private String profileImage;
    private String bio;
    private String phone;
}
