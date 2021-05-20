package ru.lab.hunter.model.builder;

import ru.lab.hunter.security.Role;
import ru.lab.hunter.security.Status;
import ru.lab.hunter.model.User;

public class UserBuilder {

    private User user;

    public UserBuilder createBuilder() {
        this.user = new User();
        return this;
    }

    public UserBuilder setId(Long id) {
        user.setId(id);
        return this;
    }

    public UserBuilder setFirstname(String firstname) {
        user.setFirst_name(firstname);
        return this;
    }

    public UserBuilder setLastname(String lastname) {
        user.setLast_name(lastname);
        return this;
    }

    public UserBuilder setPhoneNumber(String phoneNumber) {
        user.setPhone_number(phoneNumber);
        return this;
    }

    public UserBuilder setEmail(String email) {
        user.setEmail(email);
        return this;
    }

    public UserBuilder setPassword(String password) {
        user.setPassword(password);
        return this;
    }

    public UserBuilder setVk(String vkUri) {
        user.setVk(vkUri);
        return this;
    }

    public UserBuilder setRole(Role role) {
        user.setRole(role);
        return this;
    }

    public UserBuilder setStatus(Status status) {
        user.setStatus(status);
        return this;
    }

    public User getUser() {
        return user;
    }
}
