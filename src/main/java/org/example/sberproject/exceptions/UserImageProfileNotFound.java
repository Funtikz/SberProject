package org.example.sberproject.exceptions;

public class UserImageProfileNotFound extends RuntimeException{

    public UserImageProfileNotFound(){
        super("У пользователя нет фота");
    }

    public UserImageProfileNotFound(String message){
        super(message);
    }
}
