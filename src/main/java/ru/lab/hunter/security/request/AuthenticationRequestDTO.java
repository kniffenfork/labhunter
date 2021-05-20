package ru.lab.hunter.security.request;

import lombok.Data;

@Data
public class AuthenticationRequestDTO {
    private String      email;
    private String      password;

    public AuthenticationRequestDTO() {
    }

    public AuthenticationRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
