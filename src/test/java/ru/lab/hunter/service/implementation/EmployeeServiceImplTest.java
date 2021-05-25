package ru.lab.hunter.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import ru.lab.hunter.model.User;
import ru.lab.hunter.model.builder.CvBuilder;
import ru.lab.hunter.model.builder.CvRegistrationRequestBuilder;
import ru.lab.hunter.model.employee.Cv;
import ru.lab.hunter.service.request.CvRegistrationRequest;
import ru.lab.hunter.repository.employee.CvRepository;
import ru.lab.hunter.repository.UserRepository;
import ru.lab.hunter.security.jwt.JwtTokenProvider;
import ru.lab.hunter.service.EmployeeService;
import ru.lab.hunter.service.exception.NullFieldException;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// TODO: исправить этот стыд
@SpringBootTest
class EmployeeServiceImplTest {

    // TEST USER SETTINGS
    private static final Long               TEST_ID = 1L;
    private static final Long               TEST_EMPLOYEE_ID = 2L;
    private static final String             TEST_DESCRIPTION = "TEST_DESCRIPTION";
    private static final String             TEST_SCHEDULE = "TEST_SCHEDULE";
    private static final String             TEST_EXPERIENCE = "TEST_EXPERIENCE";
    private static final Boolean            TEST_IS_ARCHIVED = false;
    private static final String             TEST_NAME = "TEST_NAME";
    private static final Integer            TEST_SALARY = 10000;
    private static final String             TEST_EMAIL = "test@gmail.com";
    private static final String             TEST_AUTHORIZATION_HEADER = "testHeader";

    private static Cv                       testCv;
    private static CvRegistrationRequest    testRequest;
    private static User                     mockUser;
    private static Set<Cv>                  testCvs;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CvRepository cvRepository;

    @Autowired
    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        CvBuilder cvBuilder = new CvBuilder();
        testCv = cvBuilder.createBuilder()
                .setId(TEST_ID)
                .setEmployeeId(TEST_EMPLOYEE_ID)
                .setDescription(TEST_DESCRIPTION)
                .setExperience(TEST_EXPERIENCE)
                .setSchedule(TEST_SCHEDULE)
                .setArchived(TEST_IS_ARCHIVED)
                .setName(TEST_NAME)
                .setSalary(TEST_SALARY)
                .getCv();

        CvRegistrationRequestBuilder requestBuilder = new CvRegistrationRequestBuilder();
        testRequest = requestBuilder.createBuilder()
                .setDescription(TEST_DESCRIPTION)
                .setExperience(TEST_EXPERIENCE)
                .setSalary(TEST_SALARY)
                .setName(TEST_NAME)
                .setSchedule(TEST_SCHEDULE)
                .getRequest();

        mockUser = Mockito.mock(User.class);
    }

    @Test
    void findCvsForEmployee() {
        testCvs = new HashSet<>();
        testCvs.add(testCv);
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTHORIZATION_HEADER)).thenReturn(TEST_EMAIL);
        Mockito.when(cvRepository.findCvsByUserEmail(TEST_EMAIL)).thenReturn(testCvs);
        assertEquals(HttpStatus.OK, employeeService.findCvsForEmployee(TEST_AUTHORIZATION_HEADER).getStatusCode());
    }

    @Test
    void findCvsForEmployeeWithEmptyCvList() {
        testCvs = new HashSet<>();
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTHORIZATION_HEADER)).thenReturn(TEST_EMAIL);
        Mockito.when(cvRepository.findCvsByUserEmail(TEST_EMAIL)).thenReturn(testCvs);
        assertEquals(HttpStatus.NOT_FOUND, employeeService.findCvsForEmployee(TEST_AUTHORIZATION_HEADER).getStatusCode());
    }

/*
    @Test
    void createNewCvWithCorrectData() {
        Mockito.when(jwtTokenProvider.getEmail(TEST_AUTHORIZATION_HEADER)).thenReturn(TEST_EMAIL);
        Mockito.when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));
        Mockito.when(mockUser.getId()).thenReturn(TEST_EMPLOYEE_ID);
        assertEquals(HttpStatus.OK, employeeService.createNewCv(TEST_AUTHORIZATION_HEADER, testRequest).getStatusCode());
    }
*/

    @Test
    void validateRegistrationRequestWithNullField() {
        testRequest.setDescription(null);
        assertThrows(NullFieldException.class,
                () -> employeeService.validateRegistrationRequest(testRequest));
    }

    @Test
    void validateRegistrationRequestWithBlankField() {
        testRequest.setName(" ");
        assertThrows(NullFieldException.class,
                () -> employeeService.validateRegistrationRequest(testRequest));
    }

/*    @Test
    void makeCvFromCvRegistrationRequest() {
        testCv.setId(null);
        assertEquals(testCv.toString(), employeeService.makeCvFromCvRegistrationRequest(testRequest, TEST_EMPLOYEE_ID).toString());
    }*/
}