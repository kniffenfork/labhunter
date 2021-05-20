package ru.lab.hunter.model.builder;

import ru.lab.hunter.model.employee.Cv;
import ru.lab.hunter.model.employee.CvKeySkill;

import java.util.Set;

public class CvBuilder {

    private Cv cv;

    public CvBuilder createBuilder() {
        this.cv = new Cv();
        return this;
    }

    public CvBuilder setId(Long id) {
        cv.setId(id);
        return this;
    }

    public CvBuilder setEmployeeId(Long id) {
        cv.setEmployeeId(id);
        return this;
    }

    public CvBuilder setDescription(String description) {
        cv.setDescription(description);
        return this;
    }

    public CvBuilder setSchedule(String schedule) {
        cv.setSchedule(schedule);
        return this;
    }

    public CvBuilder setExperience(String experience) {
        cv.setExperience(experience);
        return this;
    }

    public CvBuilder setArchived(Boolean archived) {
        cv.setArchived(archived);
        return this;
    }

    public CvBuilder setName(String name) {
        cv.setName(name);
        return this;
    }

    public CvBuilder setSalary(Integer salary) {
        cv.setSalary(salary);
        return this;
    }

    public CvBuilder setKeySkills(Set<CvKeySkill> cvKeySkills) {
        cv.setKeySkills(cvKeySkills);
        return this;
    }

    public Cv getCv() {
        return cv;
    }
}
