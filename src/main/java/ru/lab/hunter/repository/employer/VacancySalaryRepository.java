package ru.lab.hunter.repository.employer;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lab.hunter.model.employer.VacancySalary;

public interface VacancySalaryRepository extends JpaRepository<VacancySalary, Long> {
}
