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
    private final UserRepository                    userRepository;
    private final JwtTokenProvider                  jwtTokenProvider;

    public EmployerServiceImpl(VacancyRepository vacancyRepository,
                               VacancySalaryRepository vacancySalaryRepository,
                               VacancySkillRepository vacancySkillRepository,
                               VacancyAddressRepository vacancyAddressRepository,
                               UserRepository userRepository,
                               JwtTokenProvider jwtTokenProvider) {
        this.vacancyRepository = vacancyRepository;
        this.vacancySalaryRepository = vacancySalaryRepository;
        this.vacancySkillRepository = vacancySkillRepository;
        this.vacancyAddressRepository = vacancyAddressRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public void createVacancy(String authorizationHeader, Vacancy vacancy) {
        User employer = getEmployerFromAuthHeader(authorizationHeader);
        vacancy.setEmployerId(employer.getId());
        saveVacancy(vacancy);
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
        removeAlterDependenciesFromVacancy(vacancy);
        Long vacancyId = vacancyRepository.save(vacancy).getId();
        vacancyAddress.setVacancyId(vacancyId);
        vacancySalary.setVacancyId(vacancyId);
        vacancyAddressRepository.save(vacancyAddress);
        vacancySalaryRepository.save(vacancySalary);
        vacancySkills.forEach(skill -> {
            skill.setVacancyId(vacancyId);
            vacancySkillRepository.save(skill);
        });
    }

    public void checkForOwnerShipVacancy(Vacancy vacancy, User employer) {
        if (!vacancy.getEmployer().equals(employer)) {
            throw new VacancyNotFoundException();
        }
    }

    public void removeAlterDependenciesFromVacancy(Vacancy vacancy) {
        vacancy.setVacancySalary(null);
        vacancy.setVacancyAddress(null);
        vacancy.setVacancySkills(null);
    }
}
