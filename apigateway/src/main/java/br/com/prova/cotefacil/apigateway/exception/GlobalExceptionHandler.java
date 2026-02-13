package br.com.prova.cotefacil.apigateway.exception;

import br.com.prova.cotefacil.apigateway.dto.RestMensagemDTO;
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

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    private ResponseEntity<RestMensagemDTO> usuarioNaoEncontrado(UsuarioNaoEncontradoException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RestMensagemDTO(HttpStatus.UNAUTHORIZED, exception.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(TokenException.class)
    private ResponseEntity<RestMensagemDTO> tokenInvalido(TokenException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RestMensagemDTO(HttpStatus.UNAUTHORIZED, exception.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(UsuarioExistenteException.class)
    private ResponseEntity<RestMensagemDTO> usuarioExistente(UsuarioExistenteException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RestMensagemDTO(HttpStatus.BAD_REQUEST, exception.getMessage(), LocalDateTime.now()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> erros = new HashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(e -> erros.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RestMensagemDTO(HttpStatus.BAD_REQUEST, erros, LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<RestMensagemDTO> handleTipoInvalido(MethodArgumentTypeMismatchException exception) {
        return ResponseEntity.badRequest().body(new RestMensagemDTO(HttpStatus.BAD_REQUEST, "Tipos inválidos na requisição.", LocalDateTime.now()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RestMensagemDTO> handleDadoInvalido(ConstraintViolationException exception) {
        return ResponseEntity.badRequest().body(new RestMensagemDTO(HttpStatus.BAD_REQUEST, "Dados inválidos na requisição.", LocalDateTime.now()));
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RestMensagemDTO(HttpStatus.NOT_FOUND, "Endpoint incorreto.", LocalDateTime.now()));
    }
}
