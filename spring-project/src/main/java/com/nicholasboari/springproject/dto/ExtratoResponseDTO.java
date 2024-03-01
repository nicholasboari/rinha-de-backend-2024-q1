package com.nicholasboari.springproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nicholasboari.springproject.model.Transacao;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ExtratoResponseDTO {

    private SaldoDTO saldo;
    @JsonProperty("ultimas_transacoes")
    private List<Transacao> ultimasTransacoes;
}
