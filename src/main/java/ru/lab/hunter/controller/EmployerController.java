package ru.lab.hunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.lab.hunter.model.ResponseMessage;
import ru.lab.hunter.model.employer.Vacancy;
import ru.lab.hunter.service.EmployerService;
import ru.lab.hunter.service.exception.VacancyNotFoundException;
import java.util.Set;

@RestController
@RequestMapping("/api/employer")
public class EmployerController {
    private final EmployerService employerService;

    public EmployerController(EmployerService employerService) {
        this.employerService = employerService;
    }

    @PostMapping(path="/vacancy/create")
    public ResponseEntity<?> createVacancy(@RequestHeader("${jwt.header}") String authHeader,
                                           @RequestBody Vacancy vacancy) {
        try {
            employerService.createVacancy(authHeader, vacancy);
            return new ResponseEntity<>(new ResponseMessage("Successfully created vacancy."), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path="/vacancy")
    public ResponseEntity<?> getAllVacancies(@RequestHeader("${jwt.header}") String authHeader) {
        try {
            Set<Vacancy> vacancies = employerService.getVacanciesByEmployer(authHeader);
            return new ResponseEntity<>(vacancies, HttpStatus.OK);
        } catch (VacancyNotFoundException e) {
            return new ResponseEntity<>(new ResponseMessage("There is no vacancies for this employer."), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path="/vacancy", params = "id")
    public ResponseEntity<?> deleteVacancyById(@RequestHeader("${jwt.header}") String authHeader, @RequestParam("id") Long id) {
        try {
            employerService.deleteVacancyById(authHeader, id);
            return new ResponseEntity<>(new ResponseMessage("Successfully deleted vacancy"), HttpStatus.OK);
        } catch (VacancyNotFoundException e) {
            return new ResponseEntity<>(new ResponseMessage("There is no such vacancy."), HttpStatus.BAD_REQUEST);
        }
    }
}
