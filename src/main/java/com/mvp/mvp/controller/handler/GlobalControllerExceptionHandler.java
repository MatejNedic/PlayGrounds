package com.mvp.mvp.controller.handler;

import com.mvp.mvp.model.NoMoneyException;
import com.mvp.mvp.model.NoRowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ValidationException;
import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);
    private static final String CONST_STATUS_VALIDATION_ERROR_OCCURRED = "validation error occurred";
    private static final String CONST_STATUS_INTERNAL_ERROR_OCCURRED = "internal error occurred";
    private static final String CONST_STATUS_NOT_AUTHORIZED = "not authorized";
    private static final String CONST_STATUS_NO_USER = "no user found";
    private static final String CONST_NO_SUCH_ROW_EXCEPTION = "no row with exception";
    private static final String CONST_NO_MONEY_EXCEPTION = "you don't have enough money for this request";


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {ValidationException.class})
    public void handleValidationException(ValidationException e) {
        log.warn("Global error handler received ValidationException", e);
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, CONST_STATUS_VALIDATION_ERROR_OCCURRED);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = {AccessDeniedException.class})
    public void handleValidationException(AccessDeniedException e) {
        log.warn("Global error handler received AccessDeniedException", e);
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, CONST_STATUS_NOT_AUTHORIZED);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = {UsernameNotFoundException.class})
    public void handleValidationException(UsernameNotFoundException e) {
        log.warn("Global error handler received UsernameNotFoundException", e);
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, CONST_STATUS_NO_USER);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {NullPointerException.class})
    public void handleValidationException(RuntimeException e) {
        log.warn("Global error handler received NullPointerException", e);
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, CONST_STATUS_INTERNAL_ERROR_OCCURRED);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {NoRowException.class})
    public void handleNoRowException(RuntimeException e) {
        log.warn("Global error handler received NoRowException", e);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, CONST_NO_SUCH_ROW_EXCEPTION);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ExceptionHandler(value = {NoMoneyException.class})
    public void handleNoMoneyException(RuntimeException e) {
        log.warn("Global error handler received NoRowException", e);
        throw new ResponseStatusException(HttpStatus.NO_CONTENT, CONST_NO_MONEY_EXCEPTION);
    }
}
