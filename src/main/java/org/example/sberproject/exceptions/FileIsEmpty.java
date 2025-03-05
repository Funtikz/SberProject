package org.example.sberproject.exceptions;

public class FileIsEmpty extends RuntimeException {
    public FileIsEmpty(){
        super("Файл пустой!");
    }

    public FileIsEmpty(String message){
        super(message);
    }
}
