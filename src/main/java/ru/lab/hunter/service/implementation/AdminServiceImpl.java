package ru.lab.hunter.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.lab.hunter.model.ResponseMessage;
import ru.lab.hunter.model.User;
import ru.lab.hunter.model.employee.Cv;
import ru.lab.hunter.repository.employee.CvRepository;
import ru.lab.hunter.repository.UserRepository;
import ru.lab.hunter.security.Role;
import ru.lab.hunter.security.Status;
import ru.lab.hunter.security.jwt.JwtTokenProvider;
import ru.lab.hunter.service.AdminService;
import ru.lab.hunter.service.exception.AffectOnAdminException;
import ru.lab.hunter.service.exception.AffectOnYourselfException;
import ru.lab.hunter.service.exception.AlreadyAffectedOnThisUserException;
import ru.lab.hunter.service.exception.GetUserFromTokenException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository        userRepository;
    private final CvRepository          cvRepository;
    private final JwtTokenProvider      jwtTokenProvider;

    public AdminServiceImpl(UserRepository userRepository,
                            CvRepository cvRepository,
                            JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.cvRepository = cvRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Override
    public ResponseEntity<?> findAllCvs() {
        List<Cv> cvs = cvRepository.findAll();
        return cvs.isEmpty() ? new ResponseEntity<>(new ResponseMessage("There is no Cvs."), HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(cvs, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> findAllEmployees() {
        Set<User> employees = userRepository.findAllByRoleEquals(Role.EMPLOYEE);
        return employees.isEmpty() ? new ResponseEntity<>(new ResponseMessage("There is no employees."), HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> findAllEmployers() {
        Set<User> employers = userRepository.findAllByRoleEquals(Role.EMPLOYER);
        return employers.isEmpty() ? new ResponseEntity<>(new ResponseMessage("There is no employers."), HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(employers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> findAllAdmins() {
        Set<User> admins = userRepository.findAllByRoleEquals(Role.ADMIN);
        return admins.isEmpty() ? new ResponseEntity<>(new ResponseMessage("There is no admins. Oh my God!"), HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(admins, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.isEmpty() ? new ResponseEntity<>(new ResponseMessage("There is no users. Oh my God!"), HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> findUser(String email) {
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() ->
                    new UsernameNotFoundException("There is no such user."));
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(new ResponseMessage("There is no user with such email."), HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> findUser(Long id) {
        try {
            User user = Optional.of(userRepository.getOne(id)).orElseThrow(
                    () -> new UsernameNotFoundException("There is no such user."));
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(new ResponseMessage("There is no user with such id"), HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void checkDontAffectYourself(String authHeader, Long id) {
        String email = jwtTokenProvider.getEmail(authHeader);
        User admin = userRepository.findByEmail(email).orElseThrow(() -> new GetUserFromTokenException("Can't get user from token."));
        if (admin.getId().equals(id)) {
            throw new AffectOnYourselfException();
        }
    }

    @Override
    public void checkDontAffectYourself(String authHeader, String email) {
        String emailFromToken = jwtTokenProvider.getEmail(authHeader);
        if (emailFromToken.equals(email)) {
            throw new AffectOnYourselfException();
        }
    }

    @Override
    public void checkDontAffectAdmin(User user) {
        if (user.getRole().equals(Role.ADMIN)) {
            throw new AffectOnAdminException();
        }
    }

    @Override
    public void checkDontAlreadyBanned(User user) {
        if (user.getStatus().equals(Status.BANNED)) {
            throw new AlreadyAffectedOnThisUserException();
        }
    }

    @Override
    public void checkDontAlreadyUnbanned(User user) {
        if (user.getStatus().equals(Status.ACTIVE)) {
            throw new AlreadyAffectedOnThisUserException();
        }
    }

    @Override
    public void checkDontAlreadyVerificated(User user) {
        if (user.getIs_verificated()) {
            throw new AlreadyAffectedOnThisUserException();
        }
    }

    @Override
    public void checkDontAlreadyUnverificated(User user) {
        if (!user.getIs_verificated()) {
            throw new AlreadyAffectedOnThisUserException();
        }
    }


    @Override
    public ResponseEntity<?> banUser(String authHeader, Long id) {
        String adminEmail = jwtTokenProvider.getEmail(authHeader);
        try {
            checkDontAffectYourself(authHeader, id);
            User user = Optional.of(userRepository.getOne(id)).orElseThrow(
                    () -> new UsernameNotFoundException("There is no such user"));
            checkDontAffectAdmin(user);
            checkDontAlreadyBanned(user);
            user.setStatus(Status.BANNED);
            userRepository.save(user);
            log.info(String.format("Successfully banned user with id = %d by admin with email = %s.", id, adminEmail));
            return new ResponseEntity<>(new ResponseMessage(String.format("Successfully banned user with id = %d", id)), HttpStatus.OK);

        } catch (UsernameNotFoundException e) {
            log.info(String.format("Can't ban user with id = %d because user doesn't exists. Admin email: %s", id, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("There is no user with such id"), HttpStatus.NOT_FOUND);

        } catch (AffectOnYourselfException e) {
            log.info(String.format("Admin can't ban himself. Admin email: %s, request id: %d.", adminEmail, id));
            return new ResponseEntity<>(new ResponseMessage("You can't ban yourself"), HttpStatus.BAD_REQUEST);

        } catch (AffectOnAdminException e) {
            log.info(String.format("Admin can't ban another admin. Request id: %d, admin email: %s.", id, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("You can't ban admin"), HttpStatus.FORBIDDEN);

        } catch (AlreadyAffectedOnThisUserException e) {
            log.info(String.format("User with id = %d is already banned. Admin email: %s.", id, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("This user is already banned."), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> banUser(String authHeader, String email) {
        String adminEmail = jwtTokenProvider.getEmail(authHeader);
        try {
            checkDontAffectYourself(authHeader, email);
            User user = userRepository.findByEmail(email).orElseThrow(
                    () -> new UsernameNotFoundException("There is no such user"));
            checkDontAffectAdmin(user);
            checkDontAlreadyBanned(user);
            user.setStatus(Status.BANNED);
            log.info(String.format("Successfully banned user with email = %s by admin with email = %s.", email, adminEmail));
            userRepository.save(user);
            return new ResponseEntity<>(new ResponseMessage(String.format("Successfully banned user with email = %s.", email)), HttpStatus.OK);

        } catch (UsernameNotFoundException e) {
            log.info(String.format("Can't ban user with email = %s because user doesn't exists. Admin email: %s.", email, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("There is no user with such email."), HttpStatus.NOT_FOUND);

        } catch (AffectOnYourselfException e) {
            log.info(String.format("Admin can't ban himself. Admin email: %s, request email: %s.", adminEmail, email));
            return new ResponseEntity<>(new ResponseMessage("You can't ban yourself."), HttpStatus.BAD_REQUEST);

        } catch (AffectOnAdminException e) {
            log.info(String.format("Admin can't ban another admin. Request email: %s, admin email: %s.", email, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("You can't ban admin"), HttpStatus.FORBIDDEN);

        } catch (AlreadyAffectedOnThisUserException e) {
            log.info(String.format("User with email = %s is already banned. Admin email: %s.", email, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("This user is already banned."), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> unbanUser(String authHeader, Long id) {
        String adminEmail = jwtTokenProvider.getEmail(authHeader);
        try {
            checkDontAffectYourself(authHeader, id);
            User user = Optional.of(userRepository.getOne(id)).orElseThrow(
                    () -> new UsernameNotFoundException("There is no user with such id."));
            checkDontAffectAdmin(user);
            checkDontAlreadyUnbanned(user);
            user.setStatus(Status.ACTIVE);
            userRepository.save(user);
            log.info(String.format("Successfully unbanned user with id = %d by admin with email = %s.", id, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("Successfully unbanned user."), HttpStatus.OK);

        } catch (UsernameNotFoundException e) {
            log.info(String.format("Can't unban user with id = %d because user doesn't exists. Admin email: %s.", id, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("There is no user with such id"), HttpStatus.NOT_FOUND);

        } catch (AffectOnYourselfException e) {
            log.info(String.format("Admin can't unban himself. Admin email: %s, request id: %d.", adminEmail, id));
            return new ResponseEntity<>(new ResponseMessage("You can't ban yourself."), HttpStatus.BAD_REQUEST);

        } catch (AffectOnAdminException e) {
            log.info(String.format("Admin can't unban another admin. Request id: %d, admin email: %s.", id, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("You can't affect on another admin."), HttpStatus.FORBIDDEN);

        } catch (AlreadyAffectedOnThisUserException e) {
            log.info(String.format("User with id = %d is already unbanned. Admin email: %s.", id, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("This user is already active."), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> unbanUser(String authHeader, String email) {
        String adminEmail = jwtTokenProvider.getEmail(authHeader);
        try {
            checkDontAffectYourself(authHeader, email);
            User user = userRepository.findByEmail(email).orElseThrow(
                    () -> new UsernameNotFoundException("There is no user with such id."));
            checkDontAffectAdmin(user);
            checkDontAlreadyUnbanned(user);
            user.setStatus(Status.ACTIVE);
            userRepository.save(user);
            log.info(String.format("Successfully banned user with email = %s by admin with email = %s.", email, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("Successfully unbanned user."), HttpStatus.OK);

        } catch (UsernameNotFoundException e) {
            log.info(String.format("Can't unban user with email = %s because user doesn't exists. Admin email: %s.", email, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("There is no user with such email"), HttpStatus.NOT_FOUND);

        } catch (AffectOnYourselfException e) {
            log.info(String.format("Admin can't unban himself. Admin email: %s, request email: %s.", adminEmail, email));
            return new ResponseEntity<>(new ResponseMessage("You can't ban yourself."), HttpStatus.BAD_REQUEST);

        } catch (AffectOnAdminException e) {
            log.info(String.format("Admin can't unban another admin. Request email: %s, admin email: %s.", email, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("You can't affect on another admin."), HttpStatus.FORBIDDEN);

        } catch (AlreadyAffectedOnThisUserException e) {
            log.info(String.format("User with email = %s is already unbanned. Admin email: %s.", email, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("This user is already active."), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> verificateUser(String authHeader, Long id) {
        String adminEmail = jwtTokenProvider.getEmail(authHeader);
        try {
            User user = userRepository.findById(id).orElseThrow(
                    () -> new UsernameNotFoundException("There is no such user."));
            checkDontAffectAdmin(user);
            checkDontAlreadyVerificated(user);
            user.setIs_verificated(true);
            userRepository.save(user);
            log.info(String.format("Successfully verificated user with id = %d by admin with email = %s.", id, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("Successfully verificated user."), HttpStatus.OK);

        } catch (UsernameNotFoundException e) {
            log.info(String.format("Can't verificate user with id = %d because user doesn't exists. Admin email: %s.", id, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("There is no user with such id."), HttpStatus.NOT_FOUND);

        } catch (AffectOnAdminException e) {
            log.info(String.format("Admin can't verificate another admin. Request id: %d, admin email: %s.", id, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("You can't affect on another admin."), HttpStatus.FORBIDDEN);

        } catch (AlreadyAffectedOnThisUserException e) {
            log.info(String.format("User with id = %d is already verificated. Admin email: %s.", id, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("This user is already verificated."), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> verificateUser(String authHeader, String email) {
        String adminEmail = jwtTokenProvider.getEmail(authHeader);
        try {
            User user = userRepository.findByEmail(email).orElseThrow(
                    () -> new UsernameNotFoundException("There is no such user."));
            checkDontAffectAdmin(user);
            checkDontAlreadyVerificated(user);// TODO: запихнуть эти 2 метода в одну функцию checkForValidity()
            user.setIs_verificated(true);
            userRepository.save(user);
            log.info(String.format("Successfully verificated user with email = %s by admin with email = %s.", email, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("Successfully verificated user."), HttpStatus.OK);

        } catch (UsernameNotFoundException e) {
            log.info(String.format("Can't verificate user with email = %s because user doesn't exists. Admin email: %s.", email, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("There is no user with such email."), HttpStatus.NOT_FOUND);

        } catch (AffectOnAdminException e) {
            log.info(String.format("Admin can't verificate another admin. Request email: %s, admin email: %s.", email, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("You can't affect on another admin."), HttpStatus.FORBIDDEN);

        } catch (AlreadyAffectedOnThisUserException e) {
            log.info(String.format("User with email = %s is already verificated. Admin email: %s.", email, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("This user is already verificated."), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> unverificateUser(String authHeader, Long id) {
        String adminEmail = jwtTokenProvider.getEmail(authHeader);
        try {
            User user = userRepository.findById(id).orElseThrow(
                    () -> new UsernameNotFoundException("There is no such user."));
            checkDontAffectAdmin(user);
            checkDontAlreadyUnverificated(user);
            user.setIs_verificated(false);
            userRepository.save(user);
            log.info(String.format("Successfully unverificated user with id = %d by admin with email = %s.", id, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("Successfully verificated user."), HttpStatus.OK);

        } catch (UsernameNotFoundException e) {
            log.info(String.format("Can't unverificate user with id = %d because user doesn't exists. Admin email: %s.", id, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("There is no user with such id."), HttpStatus.NOT_FOUND);

        } catch (AffectOnAdminException e) {
            log.info(String.format("Admin can't unverificate another admin. Request id: %d, admin email: %s.", id, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("You can't affect on another admin."), HttpStatus.FORBIDDEN);

        } catch (AlreadyAffectedOnThisUserException e) {
            log.info(String.format("User with id = %d is already unverificated. Admin email: %s.", id, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("This user is already unverificated."), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> unverificateUser(String authHeader, String email) {
        String adminEmail = jwtTokenProvider.getEmail(authHeader);
        try {
            User user = userRepository.findByEmail(email).orElseThrow(
                    () -> new UsernameNotFoundException("There is no such user."));
            checkDontAffectAdmin(user);
            checkDontAlreadyUnverificated(user);
            user.setIs_verificated(false);
            userRepository.save(user);
            log.info(String.format("Successfully unverificated user with email = %s by admin with email = %s.", email, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("Successfully verificated user."), HttpStatus.OK);

        } catch (UsernameNotFoundException e) {
            log.info(String.format("Can't unverificate user with email = %s because user doesn't exists. Admin email: %s.", email, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("There is no user with such email."), HttpStatus.NOT_FOUND);

        } catch (AffectOnAdminException e) {
            log.info(String.format("Admin can't unverificate another admin. Request email: %s, admin email: %s.", email, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("You can't affect on another admin."), HttpStatus.FORBIDDEN);

        } catch (AlreadyAffectedOnThisUserException e) {
            log.info(String.format("User with email = %s is already unverificated. Admin email: %s.", email, adminEmail));
            return new ResponseEntity<>(new ResponseMessage("This user is already unverificated."), HttpStatus.BAD_REQUEST);
        }
    }
}
