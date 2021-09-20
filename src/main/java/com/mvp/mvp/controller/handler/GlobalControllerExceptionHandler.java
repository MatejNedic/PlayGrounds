package com.mvp.mvp.controller.handler;

import com.mvp.mvp.model.NoMoneyException;
import com.mvp.mvp.model.NoRowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ValidationException;
import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);
    private static final String CONST_STATUS_VALIDATION_ERROR_OCCURRED = "validation error occurred";
    private static final String CONST_STATUS_INTERNAL_ERROR_OCCURRED = "internal error occurred";
    private static final String CONST_STATUS_NOT_AUTHORIZED = "not authorized";
    private static final String CONST_STATUS_NO_USER = "no user found";
    private static final String CONST_NO_SUCH_ROW_EXCEPTION = "no row with exception";
    private static final String CONST_NO_MONEY_EXCEPTION = "you don't have enough money for this request";
    private static final String CONST_NO_SUCH_ELEMENT_EXCEPTION = "No requested Element";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {ValidationException.class})
    public ResponseEntity<String> handleValidationException(ValidationException e) {
        log.warn("Global error handler received ValidationException", e);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,CONST_STATUS_VALIDATION_ERROR_OCCURRED);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<String> handleMethodArgumentValidException(MethodArgumentNotValidException e) {
        log.warn("Global error handler received MethodArgumentNotValidException", e);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,CONST_STATUS_VALIDATION_ERROR_OCCURRED);
    }
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = {NoSuchElementException.class})
    public ResponseEntity<String>  handleNoSuchElementException(NoSuchElementException e) {
        log.warn("Global error handler received NoSuchElementException", e);
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,CONST_NO_SUCH_ELEMENT_EXCEPTION);
    }
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<String>  handleValidationException(AccessDeniedException e) {
        log.warn("Global error handler received AccessDeniedException", e);
          throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,CONST_STATUS_NOT_AUTHORIZED);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = {UsernameNotFoundException.class})
    public ResponseEntity<String>  handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.warn("Global error handler received UsernameNotFoundException", e);
          throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,CONST_STATUS_NO_USER);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {NullPointerException.class})
    public ResponseEntity<String>  handleNullPointerException(RuntimeException e) {
        log.warn("Global error handler received NullPointerException", e);
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,CONST_STATUS_INTERNAL_ERROR_OCCURRED);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {NoRowException.class})
    public ResponseEntity<String>  handleNoRowException(RuntimeException e) {
        log.warn("Global error handler received NoRowException", e);
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST,CONST_NO_SUCH_ROW_EXCEPTION);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ExceptionHandler(value = {NoMoneyException.class})
    public ResponseEntity<String>  handleNoMoneyException(RuntimeException e) {
        log.warn("Global error handler received NoRowException", e);
          throw new ResponseStatusException(HttpStatus.NO_CONTENT,CONST_NO_MONEY_EXCEPTION);
    }
}
