package ru.lab.hunter.security;

public enum Permission {
    EMPLOYEE_READ("employee:read"),
    EMPLOYER_READ("employer:read"),
    EMPLOYEE_WRITE("employee:write"),
    EMPLOYER_WRITE("employer:write"),
    DEVELOPERS_READ("developers:read"),
    DEVELOPERS_WRITE("developers:write");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
