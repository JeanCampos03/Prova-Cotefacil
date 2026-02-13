package br.com.prova.cotefacil.apipedidos.exceptions;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class RestMensage {

    @JsonProperty("status")
    private int status;

    @JsonProperty("mensagem")
    private Object mensagem;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty("date")
    private LocalDateTime date;

    public RestMensage(HttpStatus httpStatus, Object mensagem, LocalDateTime date) {
        this.status = httpStatus.value();
        this.mensagem = mensagem;
        this.date = date;
    }
}

