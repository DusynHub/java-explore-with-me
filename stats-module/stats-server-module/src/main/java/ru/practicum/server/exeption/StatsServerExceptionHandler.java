package ru.practicum.server.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class StatsServerExceptionHandler {


    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException (Throwable e){
        log.warn("[HTTP STATUS 500] {} ", e.getMessage(), e);

        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleValidationException(final RuntimeException e) {
        log.warn("[HTTP STATUS 500] {} ", e.getMessage(), e);

        return new ErrorResponse(e.getMessage());
    }


}
