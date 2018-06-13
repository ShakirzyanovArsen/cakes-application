package com.cakes.cakes.exception.handler;

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
@Order(Ordered.LOWEST_PRECEDENCE)
public class CommonExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorDetails> handleException(EntityNotFoundException e, WebRequest r) {
        ErrorDetails details = new ErrorDetails(new Date(), e.getMessage(), r.getDescription(true));
        return new ResponseEntity<>(details, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
