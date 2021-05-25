package ru.lab.hunter.repository.employer;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lab.hunter.model.employer.VacancySkill;

public interface VacancySkillRepository extends JpaRepository<VacancySkill, Long> {
}
