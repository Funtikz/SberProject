package org.example.sberproject.exceptions;

public class UserAlreadyExistsException extends RuntimeException {

    public  UserAlreadyExistsException(){
        super("Данный пользователь зарегистрирован");
    }

    public UserAlreadyExistsException(String string) {
        super(string);
    }
}
