package com.vagabonder.dto;

import lombok.Data;

@Data
public class RegisterRequestDTO {

    private String fullName;
    private String email;
    private String password;
}