package ru.lab.hunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.lab.hunter.model.ResponseMessage;
import ru.lab.hunter.security.Role;
import ru.lab.hunter.service.AdminService;
import ru.lab.hunter.service.request.BanUserRequest;
import ru.lab.hunter.service.request.VerificateUserRequest;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/cv")
    public ResponseEntity<?> findAllCvs() {
        return adminService.findAllCvs();
    }

    @GetMapping(path = "/user", params = "role")
    public ResponseEntity<?> findAllUsers(@RequestParam("role") String role) {
        switch (role) {
            case ("admin"):
                return adminService.findAllUsers();
            case ("employee"):
                return adminService.findAllEmployees();
            case ("employer"):
                return adminService.findAllEmployers();
        }
        return new ResponseEntity<>(new ResponseMessage("There is no such role"), HttpStatus.BAD_REQUEST);
    }

    @GetMapping(path = "/user")
    public ResponseEntity<?> findAllUsers() {
        return adminService.findAllUsers();
    }

    @GetMapping(path = "/user", params = "id")
    public ResponseEntity<?> findUserById(@RequestParam("id") Long id) {
        return adminService.findUser(id);
    }

    @GetMapping(path = "/user", params = "email")
    public ResponseEntity<?> findUserByEmail(@RequestParam("email") String email) {
        return adminService.findUser(email);
    }

    @PutMapping(path ="/ban/id")
    public ResponseEntity<?> banUserById(@RequestHeader("${jwt.header}") String authHeader,
                                         @RequestBody BanUserRequest request) {
        return adminService.banUser(authHeader, request.getId());
    }

    @PutMapping(path = "/ban/email")
    public ResponseEntity<?> banUserByEmail(@RequestHeader("${jwt.header}") String authHeader,
                                            @RequestBody BanUserRequest request) {
        return adminService.banUser(authHeader, request.getEmail());
    }

    @PutMapping(path = "/unban/id")
    public ResponseEntity<?> unbanUserById(@RequestHeader("${jwt.header}") String authHeader,
                                           @RequestBody BanUserRequest request) {
        return adminService.unbanUser(authHeader, request.getId());
    }

    @PutMapping(path = "/unban/email")
    public ResponseEntity<?> unbanUserByEmail(@RequestHeader("${jwt.header}") String authHeader,
                                              @RequestBody BanUserRequest request) {
        return adminService.unbanUser(authHeader, request.getEmail());
    }

    @PutMapping(path = "/verificate/email")
    public ResponseEntity<?> verificateUserByEmail(@RequestHeader("${jwt.header}") String authHeader,
                                                   @RequestBody VerificateUserRequest request) {
        return adminService.verificateUser(authHeader, request.getEmail());
    }

    @PutMapping(path = "/verificate/id")
    public ResponseEntity<?> verificateUserById(@RequestHeader("${jwt.header}") String authHeader,
                                                @RequestBody VerificateUserRequest request) {
        return adminService.verificateUser(authHeader, request.getId());
    }

    @PutMapping(path = "/unverificate/id")
    public ResponseEntity<?> unverificateUserById(@RequestHeader("${jwt.header}") String authHeader,
                                                  @RequestBody VerificateUserRequest request) {
        return adminService.unverificateUser(authHeader, request.getId());
    }

    @PutMapping(path = "/unverificate/email")
    public ResponseEntity<?> unverificateUserByEmail(@RequestHeader("${jwt.header}") String authHeader,
                                                     @RequestBody VerificateUserRequest request) {
        return adminService.unverificateUser(authHeader, request.getEmail());
    }
}
