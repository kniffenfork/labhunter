package ru.lab.hunter.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import ru.lab.hunter.model.ResponseMessage;
import ru.lab.hunter.model.User;
import ru.lab.hunter.repository.UserRepository;
import ru.lab.hunter.security.jwt.JwtTokenProvider;
import ru.lab.hunter.security.request.AuthenticationRequestDTO;
import ru.lab.hunter.service.AuthenticationService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager     authenticationManager;
    private final UserRepository            userRepository;
    private final JwtTokenProvider          jwtTokenProvider;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager,
                                     UserRepository userRepository,
                                     JwtTokenProvider jwtTokenProvider) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Map<Object, Object> createAuthenticationResponse(AuthenticationRequestDTO request) throws AuthenticationException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User doesn't exists."));
        String token = jwtTokenProvider.createToken(request.getEmail(), user.getRole().name());
        Map<Object, Object> response = new HashMap<>();
        response.put("email", request.getEmail()); //TODO: засунуть этот ужас в AuthenticationResponseDTO
        response.put("token", token);
        log.info(String.format("Successfully log in. User info: %s.", user.toString()));
        return response;
    }

    @Override
    public ResponseEntity<?> authenticate(AuthenticationRequestDTO request) {
        try {
            Map<Object, Object> response = createAuthenticationResponse(request);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            log.info(String.format("Error while logging in. Request info: %s.", request.toString()));
            return new ResponseEntity<>(new ResponseMessage("Invalid email/password combination"), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);
        return new ResponseEntity<>(new ResponseMessage("Successfully log out."), HttpStatus.OK);
    }
}
