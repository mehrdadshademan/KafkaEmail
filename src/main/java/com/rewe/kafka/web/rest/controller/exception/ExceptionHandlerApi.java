package com.rewe.kafka.web.rest.controller.exception;

import com.rewe.kafka.dto.ExceptionDto;
import com.rewe.kafka.exceptions.EmailRandomException;
import com.rewe.kafka.exceptions.EmailRandomInvalidInputException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlerApi {

    @ExceptionHandler(EmailRandomException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto emailRandomExceptionHandler(EmailRandomException e) {
        return new ExceptionDto(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailRandomInvalidInputException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto invalidInputExceptionHandler(EmailRandomInvalidInputException e) {
        return new ExceptionDto(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
