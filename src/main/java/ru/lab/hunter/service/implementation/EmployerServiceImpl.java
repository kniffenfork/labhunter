package ru.lab.hunter.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.lab.hunter.model.User;
import ru.lab.hunter.model.employer.*;
import ru.lab.hunter.repository.UserRepository;
import ru.lab.hunter.repository.employer.*;
import ru.lab.hunter.security.jwt.JwtTokenProvider;
import ru.lab.hunter.service.EmployerService;
import ru.lab.hunter.service.exception.CompanyNotFoundException;
import ru.lab.hunter.service.exception.VacancyNotFoundException;
import javax.transaction.Transactional;
import java.util.Set;

@Service
@Slf4j
public class EmployerServiceImpl implements EmployerService {
    private final VacancyRepository                 vacancyRepository;
    private final VacancySalaryRepository           vacancySalaryRepository;
    private final VacancySkillRepository            vacancySkillRepository;
    private final VacancyAddressRepository          vacancyAddressRepository;
    private final CompanyRepository                 companyRepository;
    private final UserRepository                    userRepository;
    private final JwtTokenProvider                  jwtTokenProvider;

    public EmployerServiceImpl(VacancyRepository vacancyRepository,
                               VacancySalaryRepository vacancySalaryRepository,
                               VacancySkillRepository vacancySkillRepository,
                               VacancyAddressRepository vacancyAddressRepository,
                               CompanyRepository companyRepository,
                               UserRepository userRepository,
                               JwtTokenProvider jwtTokenProvider) {
        this.vacancyRepository = vacancyRepository;
        this.vacancySalaryRepository = vacancySalaryRepository;
        this.vacancySkillRepository = vacancySkillRepository;
        this.vacancyAddressRepository = vacancyAddressRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public void createCompany(String authorizationHeader, Company company) {
        String email = jwtTokenProvider.getEmail(authorizationHeader);
        User employer = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Error while getting employer from JWT."));
        company.setEmployers(Set.of(employer));
        Set<Company> companies = employer.getCompanies();
        companies.add(company);
        userRepository.save(employer);
        companyRepository.save(company);
    }

    @Override
    @Transactional
    public void createVacancy(String authorizationHeader, Long companyId, Vacancy vacancy) {
        User employer = getEmployerFromAuthHeader(authorizationHeader);
        Company company = companyRepository.findById(companyId).orElseThrow(CompanyNotFoundException::new);
        checkForOwnershipCompany(company, employer);
        vacancy.setEmployerId(employer.getId());
        vacancy.setCompanyId(companyId);
        saveVacancy(vacancy);
    }

    @Override
    public Set<Company> getCompaniesByEmployer(String authorizationHeader) {
        User employer = getEmployerFromAuthHeader(authorizationHeader);
        Set<Company> companies = employer.getCompanies();
        if (companies.isEmpty())
            throw new CompanyNotFoundException();
        return companies;
    }

    @Override
    public Set<Vacancy> getVacanciesByEmployer(String authorizationHeader) {
        User employer = getEmployerFromAuthHeader(authorizationHeader);
        Set<Vacancy> vacancies = employer.getVacancies();
        if (vacancies.isEmpty())
            throw new VacancyNotFoundException();
        return vacancies;
    }

    @Override
    @Transactional
    public void deleteCompanyById(String authorizationHeader, Long companyId) {
        User employer = getEmployerFromAuthHeader(authorizationHeader);
        Company company = companyRepository.findById(companyId).orElseThrow(CompanyNotFoundException::new);
        checkForOwnershipCompany(company, employer);
        companyRepository.delete(company);
    }

    @Override
    @Transactional
    public void deleteVacancyById(String authorizationHeader, Long vacancyId) {
        User employer = getEmployerFromAuthHeader(authorizationHeader);
        Vacancy vacancy = vacancyRepository.findById(vacancyId).orElseThrow(VacancyNotFoundException::new);
        checkForOwnerShipVacancy(vacancy, employer);
        vacancyRepository.delete(vacancy);
    }

    public User getEmployerFromAuthHeader(String authorizationHeader) {
        String email = jwtTokenProvider.getEmail(authorizationHeader);
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Error while getting employer from JWT."));
    }

    public void saveVacancy(Vacancy vacancy) {
        VacancyAddress vacancyAddress = vacancy.getVacancyAddress();
        VacancySalary vacancySalary = vacancy.getVacancySalary();
        Set<VacancySkill> vacancySkills = vacancy.getVacancySkills();
        Long vacancyId = vacancyRepository.save(vacancy).getId();
        vacancyAddress.setVacancyId(vacancyId);
        vacancySalary.setVacancyId(vacancyId);
        vacancySkills.forEach(s -> s.setVacancyId(vacancyId));
        log.info(vacancyAddress.toString());
        vacancyAddressRepository.save(vacancyAddress);
        vacancySalaryRepository.save(vacancySalary);
        vacancySkillRepository.saveAll(vacancySkills);
    }

    public void checkForOwnershipCompany(Company company, User employer) {
        Set<User> companyEmployers = company.getEmployers();
        if (!companyEmployers.contains(employer))
            throw new CompanyNotFoundException();
    }

    public void checkForOwnerShipVacancy(Vacancy vacancy, User employer) {
        if (!vacancy.getEmployer().equals(employer)) {
            throw new VacancyNotFoundException();
        }
    }
}
