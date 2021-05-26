package ru.lab.hunter.repository.employer;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lab.hunter.model.employer.VacancyAddress;

public interface VacancyAddressRepository extends JpaRepository<VacancyAddress, Long> {
}
