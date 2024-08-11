package ru.danperad.effectivemobiletest.controllers;

import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.danperad.effectivemobiletest.exceptions.*;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {AuthUserNotFoundException.class, BadCredentialsException.class})
    protected ResponseEntity<Object> handleAuthUserNotFoundException(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "{\"message\":\"Неверная почта или пароль\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return handleExceptionInternal(ex, bodyOfResponse, headers, HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(value = {DuplicateUniqueKeyException.class})
    protected ResponseEntity<Object> handleDuplicateUniqueKeyException(DuplicateUniqueKeyException ex, WebRequest request) {
        String bodyOfResponse = "{\"message\":\"Пользователь с такой почтой уже существует\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return handleExceptionInternal(ex, bodyOfResponse, headers, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = {TaskNotFoundException.class})
    protected ResponseEntity<Object> handleTaskNotFoundException(TaskNotFoundException ex, WebRequest request) {
        String bodyOfResponse = "{\"message\":\"Задача с данным номером не существует\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return handleExceptionInternal(ex, bodyOfResponse, headers, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {TokenUserNotFoundException.class})
    protected ResponseEntity<Object> handleTokenUserNotFoundException(TokenUserNotFoundException ex, WebRequest request) {
        String bodyOfResponse = "Авторизованный пользователь не найден, получите новый токен доступа";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(value = {NoAccessException.class})
    protected ResponseEntity<Object> handleNoAccessException(NoAccessException ex, WebRequest request) {
        String bodyOfResponse = "Нет доступа к данному действию";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail body = ex.getBody();
        body.setDetail(ex.getBindingResult().getFieldErrors().getFirst().getDefaultMessage());
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(body, headers, HttpStatus.BAD_REQUEST);
    }
}
