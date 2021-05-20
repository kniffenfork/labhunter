package ru.lab.hunter.service.request;

import lombok.Data;

@Data
public class BanUserRequest {
    private String email;
    private Long id;
}
