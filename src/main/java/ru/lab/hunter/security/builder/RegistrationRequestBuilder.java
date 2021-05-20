package ru.lab.hunter.security.builder;

import ru.lab.hunter.security.request.RegistrationRequestDTO;

public class RegistrationRequestBuilder {

    private RegistrationRequestDTO request;

    public RegistrationRequestBuilder createBuilder() {
        this.request = new RegistrationRequestDTO();
        return this;
    }

    public RegistrationRequestBuilder setEmail(String email) {
        request.setEmail(email);
        return this;
    }

    public RegistrationRequestBuilder setFirstName(String firstName) {
        request.setFirstName(firstName);
        return this;
    }

    public RegistrationRequestBuilder setLastName(String lastName) {
        request.setLastName(lastName);
        return this;
    }

    public RegistrationRequestBuilder setPassword(String password) {
        request.setPassword(password);
        return this;
    }

    public RegistrationRequestBuilder setConfirmPassword(String confirmPassword) {
        request.setConfirmPassword(confirmPassword);
        return this;
    }

    public RegistrationRequestDTO getRequest() {
        return request;
    }
}
