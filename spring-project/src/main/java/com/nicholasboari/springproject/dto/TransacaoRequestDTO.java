package com.nicholasboari.springproject.dto;

import com.nicholasboari.springproject.model.TipoTransacaoEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

@Data
public class TransacaoRequestDTO {

    @Positive
    private Long valor;
    @Enumerated(EnumType.STRING)
    private TipoTransacaoEnum tipo;
    @Size(min = 1, max = 10)
    private String descricao;
    private Instant realizadaEm;
}
