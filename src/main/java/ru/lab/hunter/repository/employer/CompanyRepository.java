package ru.lab.hunter.repository.employer;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lab.hunter.model.User;
import ru.lab.hunter.model.employer.Company;

import java.util.Set;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Set<Company> getCompaniesByEmployers(User employer);
}
