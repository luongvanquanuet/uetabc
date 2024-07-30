package com.abc.dto.request;

import com.abc.validator.DobConstraint;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsersUpdateRequest {
    String password;
     String firstName;
     String lastName;
    @DobConstraint(min = 10,message = "INVALID_DOB")
     LocalDate dob;
     List<String> roles;



}
