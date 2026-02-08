package br.com.cotefacil.prova.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExeptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    private ResponseEntity<RestMensagemErro> usuarioNaoEncontrado(UsuarioNaoEncontradoException exception) {
        var mensagemUsuarioNaoEncontrado = new RestMensagemErro(HttpStatus.UNAUTHORIZED, exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mensagemUsuarioNaoEncontrado);
    }


    @ExceptionHandler(TokenException.class)
    private ResponseEntity<RestMensagemErro> tokenInvalido(TokenException exception){
        var tokenInvalido = new RestMensagemErro(HttpStatus.UNAUTHORIZED, exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED ).body(tokenInvalido);
    }

    @ExceptionHandler(UsuarioExistenteException.class)
    private ResponseEntity<RestMensagemErro> usuarioExistente(UsuarioExistenteException exception){
        var usuarioExistente = new RestMensagemErro(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST ).body(usuarioExistente);
    }

}
