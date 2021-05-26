package ru.lab.hunter.service;

import ru.lab.hunter.model.employer.Vacancy;

import java.util.Set;

public interface EmployerService {
    void                        createVacancy(String authorizationHeader, Vacancy vacancy);
    Set<Vacancy>                getVacanciesByEmployer(String authorizationHeader);
    void                        deleteVacancyById(String authorizationHeader, Long vacancyId);
}
