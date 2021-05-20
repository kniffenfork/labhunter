package ru.lab.hunter.service.exception;

import org.springframework.security.core.AuthenticationException;

public class NullFieldException extends AuthenticationException {
    public NullFieldException(String msg) {
        super(msg);
    }
}
