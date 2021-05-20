package ru.lab.hunter.service.request;

import lombok.Data;
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

    @Override
    public String toString() {
        return "CvEditRequest{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", schedule='" + schedule + '\'' +
                ", experience='" + experience + '\'' +
                ", salary=" + salary +
                ", name='" + name + '\'' +
                ", keySkills=" + keySkills +
                '}';
    }
}
