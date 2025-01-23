package com.app.two.factor.auth.infra;

import com.app.two.factor.auth.exception.BadCredentialsException;
import com.app.two.factor.auth.exception.EntityNotFoundException;
import com.app.two.factor.auth.exception.FileException;
import com.app.two.factor.auth.exception.RecognizingFaceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<RestErrorMessage> handleNotFoundStatus(RuntimeException exception,
                                                                 HttpServletRequest request) {
        log.error("API ERROR - ", exception);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new RestErrorMessage(request, HttpStatus.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler({FileException.class, RecognizingFaceException.class})
    public ResponseEntity<RestErrorMessage> handleFileException(RuntimeException exception,
                                                                 HttpServletRequest request) {
        log.error("API ERROR - ", exception);

        if (exception instanceof FileException) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RestErrorMessage(request, HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado"));
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RestErrorMessage(request, HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage()));

    }

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<RestErrorMessage> handleBadCredentials(RuntimeException exception,
                                                                HttpServletRequest request) {
        log.error("API ERROR - ", exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new RestErrorMessage(request, HttpStatus.BAD_REQUEST, exception.getMessage()));
    }
}