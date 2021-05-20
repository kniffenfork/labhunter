package ru.lab.hunter.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.lab.hunter.config.SecurityConfig;
import ru.lab.hunter.model.ResponseMessage;
import ru.lab.hunter.model.User;
import ru.lab.hunter.model.builder.UserBuilder;
import ru.lab.hunter.repository.UserRepository;
import ru.lab.hunter.security.request.RegistrationRequestDTO;
import ru.lab.hunter.security.Role;
import ru.lab.hunter.security.Status;
import ru.lab.hunter.service.RegistrationService;
import ru.lab.hunter.service.exception.NullFieldException;
import ru.lab.hunter.service.exception.PasswordsDontMatchException;
import ru.lab.hunter.service.exception.TooSmallLengthFieldException;

import java.util.Optional;

@Slf4j
@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final SecurityConfig        securityConfig;
    private final UserRepository        userRepository;

    public RegistrationServiceImpl(SecurityConfig securityConfig,
                                   UserRepository userRepository) {
        this.securityConfig = securityConfig;
        this.userRepository = userRepository;
    }

    @Override
    public void checkRegistrationRequestForValidity(RegistrationRequestDTO request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());

        if (request.hasNullField()) {
            throw new NullFieldException("Some field of request body is null.");
        }
        else if (user.isPresent()) {
            throw new DuplicateKeyException("User with such email already exists");
        }
        else if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordsDontMatchException("Passwords don't match");
        }
        else if (!request.hasTooSmallField()) {
            throw new TooSmallLengthFieldException("Some field has too small length. It should be at least 3.");
        }

    }

    @Override
    public User makeEmployeeFromRegistrationRequest(RegistrationRequestDTO request) {
        UserBuilder builder = new UserBuilder();
        return builder.createBuilder()
                .setEmail(request.getEmail())
                .setFirstname(request.getFirstName())
                .setLastname(request.getLastName())
                .setPassword(securityConfig.passwordEncoder().encode(request.getPassword()))
                .setRole(Role.EMPLOYEE)
                .setStatus(Status.ACTIVE)
                .getUser();
    }

    @Override
    public User makeEmployerFromRegistrationRequest(RegistrationRequestDTO request) {
        UserBuilder builder = new UserBuilder();
        return builder.createBuilder()
                .setEmail(request.getEmail())
                .setFirstname(request.getFirstName())
                .setLastname(request.getLastName())
                .setPassword(securityConfig.passwordEncoder().encode(request.getPassword()))
                .setRole(Role.EMPLOYER)
                .setStatus(Status.ACTIVE)
                .getUser();
    }

    @Override
    public User makeAdminFromRegistrationRequest(RegistrationRequestDTO request) {
        UserBuilder builder = new UserBuilder();
        return builder.createBuilder()
                .setEmail(request.getEmail())
                .setFirstname(request.getFirstName())
                .setLastname(request.getLastName())
                .setPassword(securityConfig.passwordEncoder().encode(request.getPassword()))
                .setRole(Role.ADMIN)
                .setStatus(Status.ACTIVE)
                .getUser();
    }

    @Override
    public ResponseEntity<?> registrationEmployee(RegistrationRequestDTO request) { //TODO: Разбить по-человечески на функции
        try {
            checkRegistrationRequestForValidity(request);
            User employee = makeEmployeeFromRegistrationRequest(request);
            userRepository.save(employee);
            log.info(String.format("Registrated new user. Info: %s.", employee.toString()));
            return new ResponseEntity<>(new ResponseMessage("Successfully registrated new employee."), HttpStatus.OK);

        } catch (DuplicateKeyException e) {
            log.info(String.format("Can't registrate new employee, because employee with same id already exists. Request: %s.", request.toString()));
            return new ResponseEntity<>(new ResponseMessage("User with such email already exists."), HttpStatus.UNAUTHORIZED);

        } catch (PasswordsDontMatchException e) {
            log.info(String.format("Can't registrate new employee, because passwords don't match. Request: %s.", request.toString()));
            return new ResponseEntity<>(new ResponseMessage("Can't registrate new employee. Passwords don't match."), HttpStatus.BAD_REQUEST);
        }
        catch (NullFieldException e) {
            log.info(String.format("Can't registrate new employee, because some field in registration request is null. Request: %s.", request.toString()));
            return new ResponseEntity<>(new ResponseMessage("Can't create new employee. Some field in request is null."), HttpStatus.BAD_REQUEST);
        }
        catch (TooSmallLengthFieldException e) {
            log.info(String.format("Can't registrate new employee, because some field in registration request is too small. Request: %s", request.toString()));
            return new ResponseEntity<>(new ResponseMessage("Some field has too small length. It should be at lest 3."), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> registrationEmployer(RegistrationRequestDTO request) {
        try {
            checkRegistrationRequestForValidity(request);
            User employer = makeEmployerFromRegistrationRequest(request);
            userRepository.save(employer);
            log.info(String.format("Successfully registrated new user. Info: %s.", employer.toString()));
            return new ResponseEntity<>(new ResponseMessage("Successfully registrated new user."), HttpStatus.OK);

        } catch (DuplicateKeyException e) {
            log.info(String.format("Can't registrate new employee, because employer with same id already exists. Request: %s.", request.toString()));
            return new ResponseEntity<>(new ResponseMessage("User with such email already exists."), HttpStatus.UNAUTHORIZED);

        } catch (PasswordsDontMatchException e) {
            log.info(String.format("Can't registrate new employer, because passwords don't match. Request: %s.", request.toString()));
            return new ResponseEntity<>(new ResponseMessage("Passwords don't match."), HttpStatus.BAD_REQUEST);
        }
        catch (NullFieldException e) {
            log.info(String.format("Can't registrate new employer, because some field in registration request is null. Request: %s.", request.toString()));
            return new ResponseEntity<>(new ResponseMessage("Can't create new user. Some field in request is null."), HttpStatus.BAD_REQUEST);
        }
        catch (TooSmallLengthFieldException e) {
            log.info(String.format("Can't registrate new employer, because some field in registration request is too small. Request: %s", request.toString()));
            return new ResponseEntity<>(new ResponseMessage("Can't create new user. " +
                    "Length of fields should be at least 3."), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> registrationAdmin(RegistrationRequestDTO request) {
        try {
            checkRegistrationRequestForValidity(request);
            User admin = makeAdminFromRegistrationRequest(request);
            userRepository.save(admin);
            log.info(String.format("Registrated new admin. Info: %s", admin.toString()));
            return new ResponseEntity<>(new ResponseMessage("Successfully registrated new admin."), HttpStatus.OK);

        } catch (DuplicateKeyException e) {
            log.info(String.format("Can't registrate new admin, because employer with same id already exists. Request: %s.", request.toString()));
            return new ResponseEntity<>(new ResponseMessage("Admin with such email already exists."), HttpStatus.UNAUTHORIZED);

        } catch (PasswordsDontMatchException e) {
            log.info(String.format("Can't registrate new admin, because passwords don't match. Request: %s.", request.toString()));
            return new ResponseEntity<>(new ResponseMessage("Passwords don't match."), HttpStatus.BAD_REQUEST);
        }
        catch (NullFieldException e) {
            log.info(String.format("Can't registrate new admin, because some field in registration request is null. Request: %s.", request.toString()));
            return new ResponseEntity<>(new ResponseMessage("Can't create new admin. Some field in request is null."), HttpStatus.BAD_REQUEST);
        }
        catch (TooSmallLengthFieldException e) {
            log.info(String.format("Can't registrate new admin, because some field in registration request is too small. Request: %s", request.toString()));
            return new ResponseEntity<>(new ResponseMessage("Can't create new user. " +
                    "Length of fields should be at least 3."), HttpStatus.BAD_REQUEST);
        }
    }
    //TODO: Нагенерить тестов
}
