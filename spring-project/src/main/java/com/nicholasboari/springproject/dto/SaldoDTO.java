package com.nicholasboari.springproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class SaldoDTO {

    private Integer total;
    @JsonProperty("data_extrato")
    private Instant dataExtrato;
    private Integer limite;
}
