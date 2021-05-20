package ru.lab.hunter.model.builder;

import ru.lab.hunter.model.employee.CvKeySkill;

public class CvKeySkillBuilder {

    private CvKeySkill cvKeySkill;

    public CvKeySkillBuilder createBuilder() {
        this.cvKeySkill = new CvKeySkill();
        return this;
    }

    public CvKeySkillBuilder setId(Long id) {
        cvKeySkill.setId(id);
        return this;
    }

    public CvKeySkillBuilder setCvId(Long cvId) {
        cvKeySkill.setCvId(cvId);
        return this;
    }

    public CvKeySkillBuilder setName(String name) {
        cvKeySkill.setName(name);
        return this;
    }

    public CvKeySkill getCvKeySkill() {
        return cvKeySkill;
    }
}
