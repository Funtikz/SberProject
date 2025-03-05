package org.example.sberproject.exceptions;

public class MappingException extends RuntimeException {
    MappingException (){
        super("Не удалось преобразовать объект");
    }
    public MappingException(String message){
        super(message);
    }

}
