package org.example.sberproject.exceptions;

public class AuthenticationException extends RuntimeException {

    public AuthenticationException(){
        super("Пользователь не аутентифицированный");
    }

    public AuthenticationException(String message) {
        super(message);
    }
}
