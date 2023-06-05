package nl.inholland.bankingapplication.exceptions;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
import nl.inholland.bankingapplication.models.dto.ExceptionDTO;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;

@ControllerAdvice
@Log
public class BankAPIExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {DataIntegrityViolationException.class, JdbcSQLIntegrityConstraintViolationException.class})
    public ResponseEntity<Object> handleDataIntegrityViolation(Exception e, WebRequest webRequest) {
        log.severe(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionDTO(
                        400,
                        e.getClass().getName(),
                        e.getMessage()
                ));
    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException entityNotFoundException, WebRequest webRequest) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionDTO(
                                404,
                                entityNotFoundException.getClass().getName(),
                                entityNotFoundException.getMessage()
                        )
                );
    }

    @ExceptionHandler(value = {AuthenticationException.class})
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException authenticationException, WebRequest webRequest) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionDTO(
                                401,
                                authenticationException.getClass().getName(),
                                authenticationException.getMessage()
                        )
                );
    }

}
