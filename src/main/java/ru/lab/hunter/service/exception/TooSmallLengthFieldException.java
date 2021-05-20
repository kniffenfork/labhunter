package ru.lab.hunter.service.exception;

import org.springframework.security.core.AuthenticationException;

public class TooSmallLengthFieldException extends AuthenticationException {
    public TooSmallLengthFieldException(String msg) {
        super(msg);
    }
}
