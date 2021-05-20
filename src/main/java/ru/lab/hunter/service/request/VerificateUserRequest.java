package ru.lab.hunter.service.request;

import lombok.Data;

@Data
public class VerificateUserRequest {
    private String email;
    private Long id;
}
