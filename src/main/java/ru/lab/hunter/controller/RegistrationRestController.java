package ru.lab.hunter.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.lab.hunter.security.request.RegistrationRequestDTO;
import ru.lab.hunter.service.RegistrationService;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class RegistrationRestController {

    private final RegistrationService registrationService;

    public RegistrationRestController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping(path="/registration/employee")
    ResponseEntity<?> registrationEmployee(@RequestBody RegistrationRequestDTO request) {
        return registrationService.registrationEmployee(request);
    }

    @PostMapping(path="/registration/employer")
    ResponseEntity<?> registrationEmployer(@RequestBody RegistrationRequestDTO request) {
        return registrationService.registrationEmployer(request);
    }

    @PostMapping(path="/registration/admin")
    ResponseEntity<?> registrationAdmin(@RequestBody RegistrationRequestDTO request) {
        return registrationService.registrationAdmin(request);
    }
}
