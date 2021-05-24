package ru.lab.hunter.service.request;

import lombok.Data;
import ru.lab.hunter.model.employee.CvCourse;
import ru.lab.hunter.model.employee.CvKeySkill;

import java.util.Set;

@Data
public class CvEditRequest {
    private Long                    id;
    private String                  description;
    private String                  schedule;
    private String                  experience;
    private Integer                 salary;
    private String                  name;
    private Set<CvKeySkill>         keySkills;
    private Set<CvCourse>           cvCourses;
}
