
package ru.lab.hunter.service;

import org.springframework.http.ResponseEntity;
import ru.lab.hunter.model.User;
import ru.lab.hunter.model.employee.Cv;
import ru.lab.hunter.service.request.CvEditRequest;
import ru.lab.hunter.service.request.CvRegistrationRequest;

public interface EmployeeService {
    // key functions
    ResponseEntity<?>       findCvsForEmployee(String authorizationHeader);
    ResponseEntity<?>       createNewCv(String authorizationHeader, CvRegistrationRequest request);
    ResponseEntity<?>       deleteCv(String authorizationHeader, Long id);
    ResponseEntity<?>       editCv(String authorizationHeader, CvEditRequest request, Long id);

    // utility functions
    void                    overrideCoursesAndSkills(Cv cv, CvEditRequest request, Long id);
    void                    delAllKeySkillsFromCv(Cv cv);
    void                    delAllCvCoursesFromCv(Cv cv);
    void                    saveCvKeySkillsFromRegRequest(Cv cv, CvRegistrationRequest request);
    void                    saveCvCoursesFromRegRequest(Cv cv, CvRegistrationRequest request);
    void                    validateRegistrationRequest(CvRegistrationRequest request);
    void                    editCvFieldsFromEditRequest(Cv cv, CvEditRequest request);
    void                    saveKeySkillsFromEditRequest(Cv cv, CvEditRequest request);
    Cv                      makeCvFromCvRegistrationRequest(CvRegistrationRequest request, Long employeeId);
}