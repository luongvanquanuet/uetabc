package com.abc.dto.request;
import com.abc.validator.DobConstraint;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;

import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsersCreationRequest {
@Size(min = 3, message = "USERNAME_INVALID")
     String username;
    //password must be at least 8 characters
 @Size(min = 8, message = "INVALID_PASSWORD")
    String password;
     String firstName;
     String lastName;
    //@DobConstraint(min = 10, message = "INVALID_DOB")
    @DobConstraint(min = 10)
     LocalDate dob;

}
