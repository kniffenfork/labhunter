package ru.lab.hunter.repository.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lab.hunter.model.employee.Cv;
import java.util.Set;

public interface CvRepository extends JpaRepository<Cv, Long> {
    Set<Cv>             findCvsByUserEmail(String email);
}
