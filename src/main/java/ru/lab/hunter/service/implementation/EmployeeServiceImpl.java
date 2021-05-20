package ru.lab.hunter.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.lab.hunter.model.ResponseMessage;
import ru.lab.hunter.model.User;
import ru.lab.hunter.model.builder.CvBuilder;
import ru.lab.hunter.model.employee.Cv;
import ru.lab.hunter.model.employee.CvKeySkill;
import ru.lab.hunter.service.exception.BadRequestException;
import ru.lab.hunter.service.exception.CvNotFoundException;
import ru.lab.hunter.service.request.CvEditRequest;
import ru.lab.hunter.service.request.CvRegistrationRequest;
import ru.lab.hunter.repository.CvKeySkillRepository;
import ru.lab.hunter.repository.CvRepository;
import ru.lab.hunter.repository.UserRepository;
import ru.lab.hunter.security.jwt.JwtTokenProvider;
import ru.lab.hunter.service.EmployeeService;
import ru.lab.hunter.service.exception.NullFieldException;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final JwtTokenProvider              jwtTokenProvider;
    private final CvRepository                  cvRepository;
    private final UserRepository                userRepository;
    private final CvKeySkillRepository          cvKeySkillRepository;


    public EmployeeServiceImpl(JwtTokenProvider jwtTokenProvider,
                               CvRepository cvRepository,
                               UserRepository userRepository,
                               CvKeySkillRepository cvKeySkillsRepository) {
        this.jwtTokenProvider =     jwtTokenProvider;
        this.cvRepository =         cvRepository;
        this.userRepository =       userRepository;
        this.cvKeySkillRepository = cvKeySkillsRepository;
    }

    //key functions

    @Override
    public ResponseEntity<?> findCvsForEmployee(String authorizationHeader) {
        String email = jwtTokenProvider.getEmail(authorizationHeader);
        Set<Cv> cvs = cvRepository.findCvsByUserEmail(email);
        return cvs.isEmpty() ? new ResponseEntity<>(new ResponseMessage("There is no cvs for that user"), HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(cvs, HttpStatus.OK);
    }
    // TODO: сделать ограничение на создание двух резюме с одинаковыми названиями и 3 резюме на каждого.
    @Transactional
    @Override
    public ResponseEntity<?> createNewCv(String authorizationHeader, CvRegistrationRequest request) {
        String email = jwtTokenProvider.getEmail(authorizationHeader);
        try {
            validateRegistrationRequest(request);
            User employee = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Error while getting employee from JWT."));
            Cv cv = makeCvFromCvRegistrationRequest(request, employee.getId());
            saveCvKeySkillsFromRegRequest(cv, request);
            log.info(String.format("Added new cv for user with email = %s, info: %s.", email, request.toString()));
            return new ResponseEntity<>(new ResponseMessage("Successfully added new CV for user."), HttpStatus.OK);

        } catch (NullFieldException e) {
            log.info(String.format("Error while adding new cv. Some field of request is empty/null. Info: %s.", request.toString()));
            return new ResponseEntity<>(new ResponseMessage("Some field is unnecessary empty/null."), HttpStatus.BAD_REQUEST);

        } catch (UsernameNotFoundException e) {
            log.info(String.format("Error while adding new cv. Info: %s.", request.toString()));
            return new ResponseEntity<>(new ResponseMessage("Fatal error. You don't exist."), HttpStatus.NOT_FOUND);
        }
    }

    //TODO: убрать возможность повторяющихся скиллов
    //TODO: ограничить максимальное число скиллов
    //TODO: протестировать
    @Transactional
    @Override
    public ResponseEntity<?> deleteCv(String authorizationHeader, Long id) {
        String email = jwtTokenProvider.getEmail(authorizationHeader);
        try {
            Cv cvToDel = Optional.of(cvRepository.getOne(id)).orElseThrow(() -> new CvNotFoundException("There is no cv with such id."));
            check4AbilityToDelCv(cvToDel, email);
            //delAllKeySkillsFromCv(cvToDel);
            cvRepository.deleteById(id);
            log.info(String.format("User %s successfully deleted cv.", email));
            return new ResponseEntity<>(new ResponseMessage("Successfully deleted cv."), HttpStatus.OK);
        } catch (CvNotFoundException e) {
            log.info(String.format("Can't find cv with id = %d", id));
            return new ResponseEntity<>(new ResponseMessage("There is no cv with such id for this user."), HttpStatus.NOT_FOUND);
        } catch (BadRequestException e) {
            log.info(String.format("User %s can't delete cv with id = %d.", email, id));
            return new ResponseEntity<>(new ResponseMessage("You can't delete cv with such id."), HttpStatus.FORBIDDEN);
        }
    }

    //TODO: добавить проверки на нулевые/маленькие поля
    @Transactional
    @Override
    public ResponseEntity<?> editCv(String authorizationHeader, CvEditRequest request, Long id) {
        String email = jwtTokenProvider.getEmail(authorizationHeader);
        try {
            Cv cv = Optional.of(cvRepository.getOne(id)).orElseThrow(() -> new CvNotFoundException("Can't get cv by id."));
            checkThatNotEditingNotYoursCv(cv, email);
            editCvFieldsFromEditRequest(cv, request);
            saveKeySkillsFromEditRequest(cv, request);
            cvRepository.save(cv);
            log.info(String.format("User %s successfully edited cv. Request info: %s.", email, request.toString()));
            return new ResponseEntity<>(new ResponseMessage("Successfully edited cv."), HttpStatus.OK);
        } catch (CvNotFoundException e) {
            log.info("Can't get cv by id. Request info: " + request.toString());
            return new ResponseEntity<>(new ResponseMessage("Can't get cv with such id."), HttpStatus.NOT_FOUND);
        }
    }

    //utility functions

    public void delAllKeySkillsFromCv(Cv cv) {
        Set<CvKeySkill> cvKeySkills = cv.getKeySkills();
        log.info(String.format("cv key skills to del: %s", cvKeySkills.toString()));
        for (CvKeySkill keySkill: cvKeySkills) {
            cvKeySkillRepository.delete(keySkill);
        }
    }

     public void saveCvKeySkillsFromRegRequest(Cv cv, CvRegistrationRequest request) {
        for (CvKeySkill keySkill: request.getKeySkills()) {
            keySkill.setCvId(cv.getId());
            cvKeySkillRepository.save(keySkill);
        }
     }

    public void checkThatNotEditingNotYoursCv(Cv cv, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Can't get user from JWT."));
        if (!user.getId().equals(cv.getEmployeeId())) {
            throw new CvNotFoundException("Forbidden to get this cv.");
        }
    }

    public void check4AbilityToDelCv(Cv cv, String email) {
        User employee = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Can't get user from JWT."));
        if (!cv.getEmployeeId().equals(employee.getId())) {
            throw new BadRequestException();
        }
    }

    @Override
    public void editCvFieldsFromEditRequest(Cv cv, CvEditRequest request) {
        cv.setExperience(request.getExperience());
        cv.setSchedule(request.getSchedule());
        cv.setSalary(request.getSalary());
        cv.setDescription(request.getDescription());
        cv.setName(request.getName());
    }

    @Override
    public void saveKeySkillsFromEditRequest(Cv cv, CvEditRequest request) {
        for (CvKeySkill keySkill: request.getKeySkills()) {
            keySkill.setCvId(cv.getId());
            cvKeySkillRepository.save(keySkill);
        }
    }

    @Override
    public void validateRegistrationRequest(CvRegistrationRequest request) {
        if (request.havingNullFields() || request.havingEmptyFields()) {
            throw new NullFieldException("Some field is unnecessary null/empty.");
        }
    }

    @Override
    public Cv makeCvFromCvRegistrationRequest(CvRegistrationRequest request, Long employeeId) {
        CvBuilder builder = new CvBuilder();
        Cv cv = builder.createBuilder()
                .setName(request.getName())
                .setEmployeeId(employeeId)
                .setDescription(request.getDescription())
                .setSchedule(request.getSchedule())
                .setExperience(request.getExperience())
                .setSalary(request.getSalary())
                .setKeySkills(request.getKeySkills())
                .getCv();
        cvRepository.save(cv);
        return cv;
    }
}