package ru.lab.hunter.model.builder;

import ru.lab.hunter.service.request.CvRegistrationRequest;

public class CvRegistrationRequestBuilder {
    private CvRegistrationRequest request;

    public CvRegistrationRequestBuilder createBuilder() {
        this.request = new CvRegistrationRequest();
        return this;
    }

    public CvRegistrationRequestBuilder setDescription(String description) {
        request.setDescription(description);
        return this;
    }

    public CvRegistrationRequestBuilder setSchedule(String schedule) {
        request.setSchedule(schedule);
        return this;
    }

    public CvRegistrationRequestBuilder setExperience(String experience) {
        request.setExperience(experience);
        return this;
    }

    public CvRegistrationRequestBuilder setSalary(Integer salary) {
        request.setSalary(salary);
        return this;
    }

    public CvRegistrationRequestBuilder setName(String name) {
        request.setName(name);
        return this;
    }

    public CvRegistrationRequest getRequest() {
        return request;
    }
}
