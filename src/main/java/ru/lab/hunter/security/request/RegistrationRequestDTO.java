package ru.lab.hunter.security.request;

import lombok.Data;

@Data
public class RegistrationRequestDTO {
    private String      email;
    private String      firstName;
    private String      lastName;
    private String      password;
    private String      confirmPassword;

    public boolean hasNullField() {
        return this.email.isBlank() || this.firstName.isBlank() || this.lastName.isBlank() || this.password.isBlank();
    }

    public boolean hasTooSmallField() {
        return this.email.length() >= 3 && this.firstName.length() >= 3 && this.lastName.length() >= 3 && this.password.length() >= 3;
    }
}
