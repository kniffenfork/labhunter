package ru.lab.hunter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.lab.hunter.service.request.CvDeleteRequest;
import ru.lab.hunter.service.request.CvEditRequest;
import ru.lab.hunter.service.request.CvRegistrationRequest;
import ru.lab.hunter.service.EmployeeService;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping(path="/cv")
    public ResponseEntity<?> findCvsForEmployee(@RequestHeader("${jwt.header}") String authHeader) {
        return employeeService.findCvsForEmployee(authHeader);
    }

    @PostMapping(path="/cv/create")
    public ResponseEntity<?> createNewCv(@RequestHeader("${jwt.header}") String authHeader,
                                         @RequestBody CvRegistrationRequest request) {
        return employeeService.createNewCv(authHeader, request);
    }

    @PutMapping(path="/cv/edit/{id}")
    public ResponseEntity<?> editCv(@RequestHeader("${jwt.header}") String authHeader,
                                    @RequestBody CvEditRequest request, @PathVariable("id") Long id) {
        return employeeService.editCv(authHeader, request, id);
    }

    @DeleteMapping(path="/cv/delete/{id}")
    public ResponseEntity<?> deleteCv(@RequestHeader("${jwt.header}") String authHeader,
                                      @PathVariable("id") Long id) {
        return employeeService.deleteCv(authHeader, id);
    }
}
