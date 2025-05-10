package com.globsest.testworkcreditcards.dto;

import com.globsest.testworkcreditcards.entity.Role;
import lombok.Data;

import java.util.Date;

@Data
public class RegisterRequest {

    private String email;
    private String firstName;
    private String middleName;
    private Role role;
    private boolean active = true;
    private String lastName;
    private String password;


}
