package ru.lab.hunter.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import ru.lab.hunter.model.User;
import ru.lab.hunter.security.request.RegistrationRequestDTO;

public interface RegistrationService {
    // key functions
    ResponseEntity<?>       registrationEmployee(@RequestBody RegistrationRequestDTO request);
    ResponseEntity<?>       registrationEmployer(@RequestBody RegistrationRequestDTO request);
    ResponseEntity<?>       registrationAdmin(@RequestBody RegistrationRequestDTO request);

    // utility functions
    void                    checkRegistrationRequestForValidity(RegistrationRequestDTO request);
    User                    makeEmployeeFromRegistrationRequest(RegistrationRequestDTO request);
    User                    makeEmployerFromRegistrationRequest(RegistrationRequestDTO request);
    User                    makeAdminFromRegistrationRequest(RegistrationRequestDTO request);

}
