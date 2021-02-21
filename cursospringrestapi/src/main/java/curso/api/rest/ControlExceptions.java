package curso.api.rest;

import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.validation.ObjectError;

import java.sql.SQLException;
import java.util.List;

/* Criamos essa classe para controlar as excecoes */
@RestControllerAdvice
@ControllerAdvice
public class ControlExceptions extends ResponseEntityExceptionHandler {

    /* Pega os erros mais comuns no projeto. */
    @ExceptionHandler({Exception.class, RuntimeException.class, Throwable.class})
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        String msgError = "";

        if(ex instanceof MethodArgumentNotValidException){
            /* A classe ObjectError abaixo é do Spring */
            List<org.springframework.validation.ObjectError> listError = ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors();
            for (ObjectError objectError: listError) {
                msgError += objectError.getDefaultMessage() + "\n";
            }
        } else {
            msgError = ex.getMessage();
        }

        /* A classe ObjectError abaixo foi criada por mim */
        curso.api.rest.ObjectError objError = new curso.api.rest.ObjectError();
        objError.setError(msgError);
        objError.setCodeError(String.valueOf(status.value()) + " ==>" + status.getReasonPhrase());
        return new ResponseEntity<>(objError, headers, status);
    }

    /* Método para mapear os erros a nível de banco de dados. */
    @ExceptionHandler({DataIntegrityViolationException.class,
                       ConstraintViolationException.class,
                       PSQLException.class,
                       SQLException.class})
    protected ResponseEntity<Object> handleExceptionDataIntegry(Exception ex){

        String msgError = "";

        if (ex instanceof DataIntegrityViolationException){
            msgError = ((DataIntegrityViolationException) ex).getCause().getCause().getMessage();
        }
        else if(ex instanceof ConstraintViolationException){
            msgError = ((ConstraintViolationException) ex).getCause().getCause().getMessage();
        }
        else if(ex instanceof PSQLException){
            msgError = ((PSQLException) ex).getCause().getCause().getMessage();
        }
        else if(ex instanceof SQLException){
            msgError = ((SQLException) ex).getCause().getCause().getMessage();
        }
        else {
            msgError = ex.getMessage();
        }

        curso.api.rest.ObjectError objectError = new curso.api.rest.ObjectError();
        objectError.setError(msgError);
        objectError.setCodeError(HttpStatus.INTERNAL_SERVER_ERROR + " ==> " + HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        return new ResponseEntity<>(objectError, HttpStatus.INTERNAL_SERVER_ERROR);
   }
}
