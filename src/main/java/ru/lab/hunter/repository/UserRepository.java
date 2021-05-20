package ru.lab.hunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lab.hunter.model.User;
import ru.lab.hunter.security.Role;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User>          findByEmail(String email);
    Set<User>               findAllByRoleEquals(Role role);
}
