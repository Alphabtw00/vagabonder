package com.vagabonder.advice;

import com.vagabonder.exception.StorageException;
import com.vagabonder.exception.UnauthorizedException;
import com.vagabonder.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)      //409
    public @ResponseBody String userAlreadyExists(UserAlreadyExistsException ex) {
        return ex.getMessage();
    }


    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)  //401
    public @ResponseBody String handleAuthenticationFailed(AuthenticationException ex) {
        return ex.getMessage();
    }


    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public @ResponseBody String handleIllegalArgumentException(IllegalArgumentException ex) {
        return ex.getMessage();
    }


    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT) //409
    public @ResponseBody String handleIllegalStateException(IllegalStateException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) //401
    public @ResponseBody String handleUnauthorizedException(UnauthorizedException ex) {
        return ex.getMessage();
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)   //400
    public @ResponseBody String handleException(Exception ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(StorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //500
    public @ResponseBody String handleStorageException(StorageException ex) {
        return ex.getMessage();
    }
}
