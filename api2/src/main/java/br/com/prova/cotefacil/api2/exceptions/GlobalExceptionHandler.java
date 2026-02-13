package br.com.prova.cotefacil.api2.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        String mensagem = ex.getMessage();
        if (mensagem == null || mensagem.isBlank()) {
            mensagem = "Erro na requisição.";
        }
        RestMensagem restMensagem = new RestMensagem(HttpStatus.valueOf(statusCode.value()), mensagem, LocalDateTime.now());
        return ResponseEntity.status(statusCode).headers(headers).body(restMensagem);
    }

    @ExceptionHandler(TokenException.class)
    private ResponseEntity<RestMensagem> tokenInvalido(TokenException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RestMensagem(HttpStatus.UNAUTHORIZED, exception.getMessage(), LocalDateTime.now()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> erros = new HashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(e -> erros.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RestMensagem(HttpStatus.BAD_REQUEST, erros, LocalDateTime.now()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<RestMensagem> handleNaoEncontrado(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RestMensagem(HttpStatus.NOT_FOUND, exception.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<RestMensagem> handleTipoInvalido(MethodArgumentTypeMismatchException exception) {
        return ResponseEntity.badRequest().body(new RestMensagem(HttpStatus.BAD_REQUEST, "Tipos inválidos na requisição.", LocalDateTime.now()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RestMensagem> handleDadoInvalido(ConstraintViolationException exception) {
        return ResponseEntity.badRequest().body(new RestMensagem(HttpStatus.BAD_REQUEST, "Dados inválidos na requisição.", LocalDateTime.now()));
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RestMensagem(HttpStatus.NOT_FOUND, "Endpoint incorreto.", LocalDateTime.now()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<RestMensagem> handleBusinessException(BusinessException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new RestMensagem(HttpStatus.CONFLICT, exception.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestMensagem> handleExceptionGenerica(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RestMensagem(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor.", LocalDateTime.now()));
    }
}
