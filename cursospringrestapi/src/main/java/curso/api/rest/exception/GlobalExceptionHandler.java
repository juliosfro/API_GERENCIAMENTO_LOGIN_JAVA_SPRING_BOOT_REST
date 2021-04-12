package curso.api.rest.exception;

import java.util.Date;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@ControllerAdvice
public class GlobalExceptionHandler {

    ErrorDetails errorDetails;
    String message;

    // handling specific exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundHandling(ResourceNotFoundException exception, WebRequest request) {

        errorDetails = new ErrorDetails(new Date(), exception.getMessage(), request.getDescription(false), HttpStatus.NOT_FOUND.toString());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    // handling global exception
    @ExceptionHandler({Exception.class})
    public ResponseEntity<?> globalExceptionHandling(Exception exception, WebRequest request) {

        if (exception instanceof DataIntegrityViolationException) {
            message = exception.getCause().getCause().getMessage();
            errorDetails = new ErrorDetails(new Date(), message, request.getDescription(false), HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
        }
        else if (exception instanceof IllegalArgumentException) {
            message = exception.getMessage();
            errorDetails = new ErrorDetails(new Date(), message, request.getDescription(false), HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
        }
        else if (exception instanceof Exception) {
            message = exception.getMessage();
            errorDetails = new ErrorDetails(new Date(), message, request.getDescription(false), HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
        }
        else {
            errorDetails = new ErrorDetails(new Date(), exception.getMessage(), request.getDescription(false), HttpStatus.INTERNAL_SERVER_ERROR.toString());
            return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}