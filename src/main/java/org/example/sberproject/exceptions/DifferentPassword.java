package org.example.sberproject.exceptions;

import lombok.extern.slf4j.Slf4j;

public class DifferentPassword extends RuntimeException{
    public DifferentPassword(){
        super("Пароли не совпадают");
    }

    public DifferentPassword(String message){
        super(message);
    }
}
