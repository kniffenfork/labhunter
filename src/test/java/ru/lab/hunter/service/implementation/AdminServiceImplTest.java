package ru.lab.hunter.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.lab.hunter.model.User;
import ru.lab.hunter.model.employee.Cv;
import ru.lab.hunter.repository.CvRepository;
import ru.lab.hunter.repository.UserRepository;
import ru.lab.hunter.security.Role;
import ru.lab.hunter.security.Status;
import ru.lab.hunter.security.jwt.JwtTokenProvider;
import ru.lab.hunter.service.AdminService;
import ru.lab.hunter.service.exception.AffectOnAdminException;
import ru.lab.hunter.service.exception.AffectOnYourselfException;
import ru.lab.hunter.service.exception.AlreadyAffectedOnThisUserException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AdminServiceImplTest {

    private static Cv                       testCv;
    private static User                     testUser;
    private static User                     testAdmin;

    private static final String             TEST_EMAIL = "TEST_EMAIL";
    private static final String             SEC_TEST_EMAIL = "SEC_TEST_EMAIL";
    private static final Long               TEST_ID = 1L;
    private static final Long               SEC_TEST_ID = 2L;
    private static final String             TEST_AUTH_HEADER = "TEST_HEADER";

    @Autowired
    private AdminService                    adminService;

    @MockBean
    CvRepository                            cvRepository;

    @MockBean
    UserRepository                          userRepository;

    @MockBean
    JwtTokenProvider                        jwtTokenProvider;

    @BeforeEach
    void setUp() {
        testCv = Mockito.mock(Cv.class);
        testUser = Mockito.mock(User.class);
        testAdmin = Mockito.mock(User.class);
    }

    @Test
    void findAllEmployees() {
        Mockito.when(testUser.getRole()).thenReturn(Role.EMPLOYEE);
        Mockito.when(userRepository.findAllByRoleEquals(Role.EMPLOYEE)).thenReturn(Set.of(testUser));
        assertEquals(Set.of(testUser), adminService.findAllEmployees().getBody());
    }

    @Test
    void findAllEmployers() {
        Mockito.when(testUser.getRole()).thenReturn(Role.EMPLOYER);
        Mockito.when(userRepository.findAllByRoleEquals(Role.EMPLOYER)).thenReturn(Set.of(testUser));
        assertEquals(Set.of(testUser), adminService.findAllEmployers().getBody());
    }

    @Test
    void findAllAdmins() {
        Mockito.when(testUser.getRole()).thenReturn(Role.ADMIN);
        Mockito.when(userRepository.findAllByRoleEquals(Role.ADMIN)).thenReturn(Set.of(testUser));
        assertEquals(Set.of(testUser), adminService.findAllAdmins().getBody());
    }

    @Test
    void findAllUsers() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(testUser));
        assertEquals(List.of(testUser), adminService.findAllUsers().getBody());
    }

    @Test
    void findUserByEmail() {
        Mockito.when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        assertEquals(testUser, adminService.findUser(TEST_EMAIL).getBody());
    }

    @Test
    void findUserById() {
        Mockito.when(testUser.getId()).thenReturn(1L);
        Mockito.when(userRepository.getOne(TEST_ID)).thenReturn(testUser);
        assertEquals(testUser, adminService.findUser(TEST_ID).getBody());
    }

    @Test
    void checkDontAffectYourselfByEmailWithoutException() {
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(TEST_EMAIL);
        assertDoesNotThrow(() -> adminService.checkDontAffectYourself(TEST_AUTH_HEADER, " "));
    }

    @Test
    void findAllCvs() {
        Mockito.when(cvRepository.findAll()).thenReturn(List.of(testCv, testCv));
        assertEquals(HttpStatus.OK, adminService.findAllCvs().getStatusCode());
    }

    @Test
    void findAllCvsWithEmptyList() {
        Mockito.when(cvRepository.findAll()).thenReturn(List.of());
        assertEquals(HttpStatus.NOT_FOUND, adminService.findAllCvs().getStatusCode());
    }

    @Test
    void checkDontAffectYourselfByEmail() {
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(TEST_EMAIL);
        assertDoesNotThrow(() -> adminService.checkDontAffectYourself(TEST_AUTH_HEADER, " "));
    }

    @Test
    void checkDontAffectYourselfByEmailWithException() {
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(TEST_EMAIL);
        assertThrows(AffectOnYourselfException.class, () -> adminService.checkDontAffectYourself(TEST_AUTH_HEADER, TEST_EMAIL));
    }

    @Test
    void checkDontAffectYourselfById() {
        testUser.setId(TEST_ID);
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(TEST_EMAIL);
        Mockito.when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        assertDoesNotThrow(() -> adminService.checkDontAffectYourself(TEST_AUTH_HEADER, SEC_TEST_ID));
    }

    @Test
    void checkDontAffectYourselfByIdWithException() {
        testUser.setId(TEST_ID);
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(TEST_EMAIL);
        Mockito.when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        assertThrows(AffectOnYourselfException.class, () -> adminService.checkDontAffectYourself(TEST_AUTH_HEADER, TEST_EMAIL));
    }

    @Test
    void checkDontAffectAdmin() {
        Mockito.when(testUser.getRole()).thenReturn(Role.EMPLOYEE);
        assertDoesNotThrow(() -> adminService.checkDontAffectAdmin(testUser));
    }

    @Test
    void checkDontAffectAdminWithException() {
        Mockito.when(testUser.getRole()).thenReturn(Role.ADMIN);
        assertThrows(AffectOnAdminException.class, () -> adminService.checkDontAffectAdmin(testUser));
    }

    @Test
    void checkDontAlreadyBanned() {
        Mockito.when(testUser.getStatus()).thenReturn(Status.ACTIVE);
        assertDoesNotThrow(() -> adminService.checkDontAlreadyBanned(testUser));
    }

    @Test
    void checkDontAlreadyBannedWithException() {
        Mockito.when(testUser.getStatus()).thenReturn(Status.BANNED);
        assertThrows(AlreadyAffectedOnThisUserException.class, () -> adminService.checkDontAlreadyBanned(testUser));
    }

    @Test
    void checkDontAlreadyUnbanned() {
        Mockito.when(testUser.getStatus()).thenReturn(Status.BANNED);
        assertDoesNotThrow(() -> adminService.checkDontAlreadyUnbanned(testUser));
    }

    @Test
    void checkDontAlreadyUnbannedWithException() {
        Mockito.when(testUser.getStatus()).thenReturn(Status.ACTIVE);
        assertThrows(AlreadyAffectedOnThisUserException.class, () -> adminService.checkDontAlreadyUnbanned(testUser));
    }

    @Test
    void checkDontAlreadyVerificated() {
        Mockito.when(testUser.getIs_verificated()).thenReturn(false);
        assertDoesNotThrow(() -> adminService.checkDontAlreadyVerificated(testUser));
    }

    @Test
    void checkDontAlreadyVerificatedWithException() {
        Mockito.when(testUser.getIs_verificated()).thenReturn(true);
        assertThrows(AlreadyAffectedOnThisUserException.class, () -> adminService.checkDontAlreadyVerificated(testUser));
    }

    @Test
    void checkDontAlreadyUnverificated() {
        Mockito.when(testUser.getIs_verificated()).thenReturn(true);
        assertDoesNotThrow(() -> adminService.checkDontAlreadyUnverificated(testUser));
    }

    @Test
    void checkDontAlreadyUnverificatedWithException() {
        Mockito.when(testUser.getIs_verificated()).thenReturn(false);
        assertThrows(AlreadyAffectedOnThisUserException.class, () -> adminService.checkDontAlreadyUnverificated(testUser));
    }

    @Test
    void banUserById() {
        Mockito.when(userRepository.getOne(TEST_ID)).thenReturn(testUser);
        Mockito.when(testUser.getRole()).thenReturn(Role.EMPLOYEE);
        Mockito.when(testUser.getStatus()).thenReturn(Status.ACTIVE);
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(TEST_EMAIL);
        Mockito.when(testAdmin.getId()).thenReturn(SEC_TEST_ID);
        Mockito.when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testAdmin));
        assertEquals(HttpStatus.OK, adminService.banUser(TEST_AUTH_HEADER, TEST_ID).getStatusCode());
    }

    @Test
    void banUserByIdAffectsOnAdmin() {
        Mockito.when(userRepository.getOne(TEST_ID)).thenReturn(testUser);
        Mockito.when(testUser.getRole()).thenReturn(Role.ADMIN);
        Mockito.when(testUser.getStatus()).thenReturn(Status.ACTIVE);
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(TEST_EMAIL);
        Mockito.when(testAdmin.getId()).thenReturn(SEC_TEST_ID);
        Mockito.when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testAdmin));
        assertEquals(HttpStatus.FORBIDDEN, adminService.banUser(TEST_AUTH_HEADER, TEST_ID).getStatusCode());
    }

    @Test
    void banUserByIdAffectOnYourself() {
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(SEC_TEST_EMAIL);
        Mockito.when(userRepository.findByEmail(SEC_TEST_EMAIL)).thenReturn(Optional.of(testAdmin));
        Mockito.when(testAdmin.getId()).thenReturn(TEST_ID);
        assertEquals(HttpStatus.BAD_REQUEST, adminService.banUser(TEST_AUTH_HEADER, TEST_ID).getStatusCode());
    }

    @Test
    void banUserByIdWhenUserNotFound() {
        Mockito.when(userRepository.getOne(TEST_ID)).thenThrow(UsernameNotFoundException.class);
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(TEST_EMAIL);
        Mockito.when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testAdmin));
        assertEquals(HttpStatus.NOT_FOUND, adminService.banUser(TEST_AUTH_HEADER, TEST_ID).getStatusCode());
    }

    @Test
    void banUserByIdAlreadyAffected() {
        Mockito.when(userRepository.getOne(TEST_ID)).thenReturn(testUser);
        Mockito.when(testUser.getRole()).thenReturn(Role.EMPLOYEE);
        Mockito.when(testUser.getStatus()).thenReturn(Status.BANNED);
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(TEST_EMAIL);
        Mockito.when(testAdmin.getId()).thenReturn(SEC_TEST_ID);
        Mockito.when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testAdmin));
        assertEquals(HttpStatus.BAD_REQUEST, adminService.banUser(TEST_AUTH_HEADER, TEST_ID).getStatusCode());
    }

    @Test
    void banUserByEmail() {
        Mockito.when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        Mockito.when(testUser.getRole()).thenReturn(Role.EMPLOYEE);
        Mockito.when(testUser.getStatus()).thenReturn(Status.ACTIVE);
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(SEC_TEST_EMAIL);
        Mockito.when(testAdmin.getId()).thenReturn(SEC_TEST_ID);
        Mockito.when(userRepository.findByEmail(SEC_TEST_EMAIL)).thenReturn(Optional.of(testAdmin));
        assertEquals(HttpStatus.OK, adminService.banUser(TEST_AUTH_HEADER, TEST_EMAIL).getStatusCode());
    }

    @Test
    void banUserByEmailAffectOnAdmin() {
        Mockito.when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        Mockito.when(testUser.getRole()).thenReturn(Role.ADMIN);
        Mockito.when(testUser.getStatus()).thenReturn(Status.ACTIVE);
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(SEC_TEST_EMAIL);
        Mockito.when(testAdmin.getId()).thenReturn(SEC_TEST_ID);
        Mockito.when(userRepository.findByEmail(SEC_TEST_EMAIL)).thenReturn(Optional.of(testAdmin));
        assertEquals(HttpStatus.FORBIDDEN, adminService.banUser(TEST_AUTH_HEADER, TEST_EMAIL).getStatusCode());
    }

    @Test
    void banUserByEmailAffectOnYourself() {
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(TEST_EMAIL);
        assertEquals(HttpStatus.BAD_REQUEST, adminService.banUser(TEST_AUTH_HEADER, TEST_EMAIL).getStatusCode());
    }

    @Test
    void banUserByEmailUserNotFound() {
        Mockito.when(userRepository.findByEmail(TEST_EMAIL)).thenThrow(UsernameNotFoundException.class);
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(SEC_TEST_EMAIL);
        Mockito.when(userRepository.findByEmail(SEC_TEST_EMAIL)).thenReturn(Optional.of(testAdmin));
        assertEquals(HttpStatus.NOT_FOUND, adminService.banUser(TEST_AUTH_HEADER, TEST_EMAIL).getStatusCode());
    }

    @Test
    void banUserByEmailAlreadyAffected() {
        Mockito.when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        Mockito.when(testUser.getRole()).thenReturn(Role.EMPLOYEE);
        Mockito.when(testUser.getStatus()).thenReturn(Status.BANNED);
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(SEC_TEST_EMAIL);
        Mockito.when(testAdmin.getId()).thenReturn(SEC_TEST_ID);
        Mockito.when(userRepository.findByEmail(SEC_TEST_EMAIL)).thenReturn(Optional.of(testAdmin));
        assertEquals(HttpStatus.BAD_REQUEST, adminService.banUser(TEST_AUTH_HEADER, TEST_EMAIL).getStatusCode());
    }

    @Test
    void unbanUserById() {
        Mockito.when(userRepository.getOne(TEST_ID)).thenReturn(testUser);
        Mockito.when(testUser.getRole()).thenReturn(Role.EMPLOYEE);
        Mockito.when(testUser.getStatus()).thenReturn(Status.BANNED);
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(TEST_EMAIL);
        Mockito.when(testAdmin.getId()).thenReturn(SEC_TEST_ID);
        Mockito.when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testAdmin));
        assertEquals(HttpStatus.OK, adminService.unbanUser(TEST_AUTH_HEADER, TEST_ID).getStatusCode());
    }

    @Test
    void unbanUserByIdAffectsOnAdmin() {
        Mockito.when(userRepository.getOne(TEST_ID)).thenReturn(testUser);
        Mockito.when(testUser.getRole()).thenReturn(Role.ADMIN);
        Mockito.when(testUser.getStatus()).thenReturn(Status.BANNED);
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(TEST_EMAIL);
        Mockito.when(testAdmin.getId()).thenReturn(SEC_TEST_ID);
        Mockito.when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testAdmin));
        assertEquals(HttpStatus.FORBIDDEN, adminService.unbanUser(TEST_AUTH_HEADER, TEST_ID).getStatusCode());
    }

    @Test
    void unbanUserByIdAffectsOnYourself() {
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(SEC_TEST_EMAIL);
        Mockito.when(userRepository.findByEmail(SEC_TEST_EMAIL)).thenReturn(Optional.of(testAdmin));
        Mockito.when(testAdmin.getId()).thenReturn(TEST_ID);
        assertEquals(HttpStatus.BAD_REQUEST, adminService.unbanUser(TEST_AUTH_HEADER, TEST_ID).getStatusCode());
    }

    @Test
    void unbanUserByIdUserNotFound() {
        Mockito.when(userRepository.getOne(TEST_ID)).thenThrow(UsernameNotFoundException.class);
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(TEST_EMAIL);
        Mockito.when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testAdmin));
        assertEquals(HttpStatus.NOT_FOUND, adminService.unbanUser(TEST_AUTH_HEADER, TEST_ID).getStatusCode());
    }

    @Test
    void unbanUserByIdAlreadyAffected() {
        Mockito.when(userRepository.getOne(TEST_ID)).thenReturn(testUser);
        Mockito.when(testUser.getRole()).thenReturn(Role.EMPLOYEE);
        Mockito.when(testUser.getStatus()).thenReturn(Status.ACTIVE);
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(TEST_EMAIL);
        Mockito.when(testAdmin.getId()).thenReturn(SEC_TEST_ID);
        Mockito.when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testAdmin));
        assertEquals(HttpStatus.BAD_REQUEST, adminService.unbanUser(TEST_AUTH_HEADER, TEST_ID).getStatusCode());
    }

    @Test
    void unbanUserByEmail() {
        Mockito.when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        Mockito.when(testUser.getRole()).thenReturn(Role.EMPLOYEE);
        Mockito.when(testUser.getStatus()).thenReturn(Status.BANNED);
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(SEC_TEST_EMAIL);
        Mockito.when(testAdmin.getId()).thenReturn(SEC_TEST_ID);
        Mockito.when(userRepository.findByEmail(SEC_TEST_EMAIL)).thenReturn(Optional.of(testAdmin));
        assertEquals(HttpStatus.OK, adminService.unbanUser(TEST_AUTH_HEADER, TEST_EMAIL).getStatusCode());
    }

    @Test
    void unbanUserByEmailAffectsOnAdmin() {
        Mockito.when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        Mockito.when(testUser.getRole()).thenReturn(Role.ADMIN);
        Mockito.when(testUser.getStatus()).thenReturn(Status.BANNED);
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(SEC_TEST_EMAIL);
        Mockito.when(testAdmin.getId()).thenReturn(SEC_TEST_ID);
        Mockito.when(userRepository.findByEmail(SEC_TEST_EMAIL)).thenReturn(Optional.of(testAdmin));
        assertEquals(HttpStatus.FORBIDDEN, adminService.unbanUser(TEST_AUTH_HEADER, TEST_EMAIL).getStatusCode());
    }

    @Test
    void unbanUserByEmailAffectsOnYourself() {
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(TEST_EMAIL);
        assertEquals(HttpStatus.BAD_REQUEST, adminService.unbanUser(TEST_AUTH_HEADER, TEST_EMAIL).getStatusCode());
    }

    @Test
    void unbanUserByEmailAlreadyAffected() {
        Mockito.when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        Mockito.when(testUser.getRole()).thenReturn(Role.EMPLOYEE);
        Mockito.when(testUser.getStatus()).thenReturn(Status.ACTIVE);
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTH_HEADER)).thenReturn(SEC_TEST_EMAIL);
        Mockito.when(testAdmin.getId()).thenReturn(SEC_TEST_ID);
        Mockito.when(userRepository.findByEmail(SEC_TEST_EMAIL)).thenReturn(Optional.of(testAdmin));
        assertEquals(HttpStatus.BAD_REQUEST, adminService.unbanUser(TEST_AUTH_HEADER, TEST_EMAIL).getStatusCode());
    }

    @Test
    void verificateUserById() {
    }

    @Test
    void verificateUserByEmail() {
    }

    @Test
    void unverificateUserById() {
    }

    @Test
    void unverificateUserByEmail() {
    }
}