package ru.lab.hunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lab.hunter.model.employee.CvCourse;

public interface CvCourseRepository extends JpaRepository<CvCourse, Long> {
    void deleteCvCourseByCvIdEquals(Long cvId);
}
