package com.cakes.cakes.exception.handler;

import com.cakes.cakes.exception.CakeNotStaleableException;
import com.cakes.cakes.exception.EntityNotFoundException;
import com.cakes.cakes.exception.ErrorDetails;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ErrorDetails> handleEntityNotFoundException(EntityNotFoundException e, WebRequest r) {
        ErrorDetails details = new ErrorDetails(new Date(), e.getMessage(), r.getDescription(true));
        return new ResponseEntity<>(details, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CakeNotStaleableException.class)
    @ResponseBody
    public ResponseEntity<ErrorDetails> handleCakeNotStaleableException(CakeNotStaleableException e, WebRequest r) {
        ErrorDetails details = new ErrorDetails(new Date(), e.getMessage(), r.getDescription(true));
        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

}
