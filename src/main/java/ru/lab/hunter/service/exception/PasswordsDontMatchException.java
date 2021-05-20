package ru.lab.hunter.service.exception;

import org.springframework.security.core.AuthenticationException;

public class PasswordsDontMatchException extends AuthenticationException {
    public PasswordsDontMatchException(String msg) {
        super(msg);
    }
}
