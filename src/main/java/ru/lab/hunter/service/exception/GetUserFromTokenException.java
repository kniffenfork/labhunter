package ru.lab.hunter.service.exception;

import org.springframework.security.core.AuthenticationException;

public class GetUserFromTokenException extends AuthenticationException {
    public GetUserFromTokenException(String msg) {
        super(msg);
    }
}
