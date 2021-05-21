package ru.lab.hunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lab.hunter.model.employee.CvKeySkill;

public interface CvKeySkillRepository extends JpaRepository<CvKeySkill, Long> {
    void deleteCvKeySkillsByCvIdEquals(Long cvId);
}
