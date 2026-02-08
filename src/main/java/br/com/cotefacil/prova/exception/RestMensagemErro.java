package br.com.cotefacil.prova.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@Setter
public class RestMensagemErro {

    private HttpStatus status;
    private String mensagem;


}

