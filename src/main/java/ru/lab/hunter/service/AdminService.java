package ru.lab.hunter.service;

import org.springframework.http.ResponseEntity;
import ru.lab.hunter.model.User;

public interface AdminService {
    // key functions
    ResponseEntity<?>           findAllCvs();
    ResponseEntity<?>           findAllEmployees();
    ResponseEntity<?>           findAllEmployers();
    ResponseEntity<?>           findAllAdmins();
    ResponseEntity<?>           findAllUsers();
    ResponseEntity<?>           findUser(String email);
    ResponseEntity<?>           findUser(Long id);
    ResponseEntity<?>           banUser(String authHeader, Long id);
    ResponseEntity<?>           banUser(String authHeader, String email);
    ResponseEntity<?>           unbanUser(String authHeader, Long id);
    ResponseEntity<?>           unbanUser(String authHeader, String email);
    ResponseEntity<?>           verificateUser(String authHeader, Long id);
    ResponseEntity<?>           verificateUser(String authHeader, String email);
    ResponseEntity<?>           unverificateUser(String authHeader, Long id);
    ResponseEntity<?>           unverificateUser(String authHeader, String email);

    // util functions
    void                        checkDontAffectYourself(String authHeader, Long id);
    void                        checkDontAffectYourself(String authHeader, String email);
    void                        checkDontAffectAdmin(User user);
    void                        checkDontAlreadyUnbanned(User user);
    void                        checkDontAlreadyBanned(User user);
    void                        checkDontAlreadyVerificated(User user);
    void                        checkDontAlreadyUnverificated(User user);
}

