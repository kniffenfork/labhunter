package ru.lab.hunter.service.implementation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.lab.hunter.security.Role;
import ru.lab.hunter.security.Status;
import ru.lab.hunter.model.User;
import ru.lab.hunter.model.builder.UserBuilder;
import ru.lab.hunter.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserDetailsServiceImplTest {

    private static final String         TEST_EMAIL = "test@gmail.com";
    private static final String         TEST_WRONG_EMAIL = "wrongEmail";
    private static final String         TEST_PASSWORD = "testPassword";
    private static final String         TEST_PHONE = "88005553535";
    private static final String         TEST_FIRST_NAME = "Ivan";
    private static final String         TEST_LAST_NAME = "Ivanov";

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;

    @BeforeEach
    public void setUp() {
        UserBuilder userBuilder = new UserBuilder();
        testUser = userBuilder.createBuilder()
                .setId(123L)
                .setFirstname(TEST_FIRST_NAME)
                .setLastname(TEST_LAST_NAME)
                .setPhoneNumber(TEST_PHONE)
                .setEmail(TEST_EMAIL)
                .setPassword(TEST_PASSWORD)
                .setRole(Role.EMPLOYEE)
                .setStatus(Status.ACTIVE)
                .getUser();
    }
    @Test
    void loadUserByUsernameIfUserInDB() {
        Mockito.when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        Assertions.assertThatCode(() -> userDetailsService.loadUserByUsername(TEST_EMAIL))
                .doesNotThrowAnyException();
    }

    @Test
    void loadUserByUsernameIfUserNotInDB() {
        Mockito.when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

        Throwable thrown = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(TEST_WRONG_EMAIL));

        assertNotNull(thrown);
    }
}