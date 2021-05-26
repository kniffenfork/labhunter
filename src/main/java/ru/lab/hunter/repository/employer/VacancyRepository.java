package ru.lab.hunter.repository.employer;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lab.hunter.model.employer.Vacancy;
import java.util.Set;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    Set<Vacancy> getVacanciesByEmployerId(Long EmployerId);
}
