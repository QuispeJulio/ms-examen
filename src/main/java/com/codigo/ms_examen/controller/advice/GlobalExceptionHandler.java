package com.codigo.ms_examen.controller.advice;

import com.codigo.ms_examen.aggregates.constants.Constants;
import com.codigo.ms_examen.aggregates.request.ErrorRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {


    // Manejo de ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorRequest> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorRequest error = new ErrorRequest();
        error.setCode(HttpStatus.NOT_FOUND.value());
        error.setMessage(Constants.ERROR_NOT_FOUND + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Manejo de CustomIllegalArgumentException
    @ExceptionHandler(CustomIllegalArgumentException.class)
    public ResponseEntity<ErrorRequest> handleIllegalArgumentException(CustomIllegalArgumentException ex) {
        ErrorRequest error = new ErrorRequest();
        error.setCode(HttpStatus.BAD_REQUEST.value());
        error.setMessage(Constants.ERROR_NOT_VALID + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Manejo de AuthenticationException
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorRequest> handleAuthenticationException(AuthenticationException ex) {
        ErrorRequest error = new ErrorRequest();
        error.setCode(HttpStatus.UNAUTHORIZED.value());
        error.setMessage(Constants.ERROR_UNAUTHORIZED + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

//    // Manejo de cualquier otra excepción no controlada
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<String> handleGenericException(Exception ex) {
//        return new ResponseEntity<>("Ocurrió un error inesperado: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }

}
