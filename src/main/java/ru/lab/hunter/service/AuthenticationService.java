package ru.lab.hunter.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import ru.lab.hunter.security.request.AuthenticationRequestDTO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface AuthenticationService {
    Map<Object, Object>         createAuthenticationResponse(AuthenticationRequestDTO request);
    ResponseEntity<?>           authenticate(@RequestBody AuthenticationRequestDTO request);
    ResponseEntity<?>           logout(HttpServletRequest request, HttpServletResponse response);
}
