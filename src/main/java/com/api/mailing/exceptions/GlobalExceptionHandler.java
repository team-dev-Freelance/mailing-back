package com.api.mailing.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MailNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleMailNotFoundException(MailNotFoundException ex){
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
}
