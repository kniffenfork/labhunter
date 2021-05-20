package ru.lab.hunter.service.request;

import lombok.Data;
import ru.lab.hunter.model.employee.CvKeySkill;
import java.util.Set;

@Data
public class CvRegistrationRequest {
    private String                  description;
    private String                  schedule;
    private String                  experience;
    private Integer                 salary;
    private String                  name;
    private Set<CvKeySkill>         keySkills;

    public boolean havingNullFields() {
        return description == null || schedule == null || experience == null || name == null;
    }

    public boolean havingEmptyFields() {
        return description.isBlank() || schedule.isBlank() || experience.isBlank() || name.isBlank();
    }
}
