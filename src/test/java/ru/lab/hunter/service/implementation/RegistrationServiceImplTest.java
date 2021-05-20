package ru.lab.hunter.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.lab.hunter.model.User;
import ru.lab.hunter.model.builder.UserBuilder;
import ru.lab.hunter.security.Role;
import ru.lab.hunter.security.Status;
import ru.lab.hunter.security.builder.RegistrationRequestBuilder;
import ru.lab.hunter.security.request.RegistrationRequestDTO;
import ru.lab.hunter.service.RegistrationService;
import ru.lab.hunter.service.exception.NullFieldException;
import ru.lab.hunter.service.exception.TooSmallLengthFieldException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RegistrationServiceImplTest {

    private static final String         TEST_EMAIL = "testEmail@gmail.com";
    private static final String         TEST_PASSWORD = "testPassword";
    private static final String         TEST_CONFIRM_PASSWORD = "testPassword";
    private static final String         TEST_FIRST_NAME = "Ivan";
    private static final String         TEST_LAST_NAME = "Ivanov";

    private RegistrationRequestDTO testRequest;
    private User testUser;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RegistrationService registrationService;


    @BeforeEach
    void setUp() {
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        RegistrationRequestBuilder builder = new RegistrationRequestBuilder();
        testRequest = builder.createBuilder()
                .setEmail(TEST_EMAIL)
                .setFirstName(TEST_FIRST_NAME)
                .setLastName(TEST_LAST_NAME)
                .setPassword(TEST_PASSWORD)
                .setConfirmPassword(TEST_CONFIRM_PASSWORD)
                .getRequest();

        UserBuilder userBuilder = new UserBuilder();
        testUser = userBuilder.createBuilder()
                .setFirstname(TEST_FIRST_NAME)
                .setLastname(TEST_LAST_NAME)
                .setEmail(TEST_EMAIL)
                .setPassword(TEST_PASSWORD)
                .setRole(Role.EMPLOYEE)
                .setStatus(Status.ACTIVE)
                .getUser();

    }

    @Test
    void checkRegistrationRequestForValidityWithEmptyEmail() {
        testRequest.setEmail(" ");
        assertThrows(NullFieldException.class, () -> registrationService.checkRegistrationRequestForValidity(testRequest));
    }

    @Test
    void checkRegistrationRequestForValidityWithEmptyFirstName() {
        testRequest.setFirstName(" ");
        assertThrows(NullFieldException.class, () -> registrationService.checkRegistrationRequestForValidity(testRequest));
    }

    @Test
    void checkRegistrationRequestForValidityWithEmptyLastName() {
        testRequest.setLastName(" ");
        assertThrows(NullFieldException.class, () -> registrationService.checkRegistrationRequestForValidity(testRequest));
    }

    @Test
    void checkRegistrationRequestForValidityWithEmptyPassword() {
        testRequest.setPassword(" ");
        assertThrows(NullFieldException.class, () -> registrationService.checkRegistrationRequestForValidity(testRequest));
    }

    @Test
    void checkRegistrationRequestForValidityWithTooSmallEmail() {
        testRequest.setEmail("ab");
        assertThrows(TooSmallLengthFieldException.class, () -> registrationService.checkRegistrationRequestForValidity(testRequest));
    }

    @Test
    void checkRegistrationRequestForValidityWithTooSmallFirstName() {
        testRequest.setFirstName("ab");
        assertThrows(TooSmallLengthFieldException.class, () -> registrationService.checkRegistrationRequestForValidity(testRequest));
    }

    @Test
    void checkRegistrationRequestForValidityWithTooSmallLastName() {
        testRequest.setLastName("ab");
        assertThrows(TooSmallLengthFieldException.class, () -> registrationService.checkRegistrationRequestForValidity(testRequest));
    }

/*    @Test
    void makeEmployeeFromRegistrationRequest() {
        User testUserFromRequest = registrationService.makeEmployeeFromRegistrationRequest(testRequest);
        assert(bCryptPasswordEncoder.matches(testRequest.getPassword(), testUserFromRequest.getPassword()));
        testUserFromRequest.setPassword(TEST_PASSWORD);
        assertEquals(testUser, testUserFromRequest);
    }

    @Test
    void makeEmployerFromRegistrationRequest() {
        testUser.setRole(Role.EMPLOYER);
        User testUserFromRequest = registrationService.makeEmployerFromRegistrationRequest(testRequest);
        assert(bCryptPasswordEncoder.matches(testRequest.getPassword(), testUserFromRequest.getPassword()));
        testUserFromRequest.setPassword(TEST_PASSWORD);
        assertEquals(testUser, testUserFromRequest);
    }

    @Test
    void makeAdminFromRegistrationRequest() {
        testUser.setRole(Role.ADMIN);
        User testUserFromRequest = registrationService.makeAdminFromRegistrationRequest(testRequest);
        assert(bCryptPasswordEncoder.matches(testRequest.getPassword(), testUserFromRequest.getPassword()));
        testUserFromRequest.setPassword(TEST_PASSWORD);
        assertEquals(testUser, testUserFromRequest);
    }*/
}