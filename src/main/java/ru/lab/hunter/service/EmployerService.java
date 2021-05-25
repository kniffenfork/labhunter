package ru.lab.hunter.service;

import ru.lab.hunter.model.employer.Company;
import ru.lab.hunter.model.employer.Vacancy;

import java.util.Set;

public interface EmployerService {
    void                        createCompany(String authorizationHeader, Company company);
    void                        createVacancy(String authorizationHeader, Long companyId, Vacancy vacancy);
    Set<Company>                getCompaniesByEmployer(String authorizationHeader);
    Set<Vacancy>                getVacanciesByEmployer(String authorizationHeader);
    void                        deleteCompanyById(String authorizationHeader, Long companyId);
    void                        deleteVacancyById(String authorizationHeader, Long vacancyId);
}
