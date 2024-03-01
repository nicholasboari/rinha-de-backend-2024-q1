package com.nicholasboari.springproject.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;
import java.util.Objects;

@Data
public class TransacaoRequestDTO {

    @Positive
    @NotNull
    private Double valor;
    @Pattern(regexp = "[cd]")
    @NotNull
    private String tipo;
    @NotNull
    @NotEmpty
    @Size(min = 1, max = 10)
    private String descricao;
    private Instant realizadaEm;

    @AssertTrue
    boolean isAmountIntegerValue() {
        return valor % 1 == 0;
    }
}