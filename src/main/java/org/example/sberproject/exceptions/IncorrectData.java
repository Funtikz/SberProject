package org.example.sberproject.exceptions;


public class IncorrectData  extends RuntimeException {
    public IncorrectData(){
        super();
    }

    public IncorrectData(String message){
        super(message);
    }
}
