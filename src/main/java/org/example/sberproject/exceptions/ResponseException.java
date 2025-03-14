package org.example.sberproject.exceptions;

public class ResponseException extends RuntimeException{
    public ResponseException(String message){
        super(message);
    }

    public ResponseException(){
        super("Ошибка");
    }
}
