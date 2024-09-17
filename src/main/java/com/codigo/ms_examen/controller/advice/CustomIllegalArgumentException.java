package com.codigo.ms_examen.controller.advice;

public class CustomIllegalArgumentException extends IllegalArgumentException {
    public CustomIllegalArgumentException(String message) {
        super(message);
    }
}
