package com.nicholasboari.springproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransacaoResponseDTO {

    private Integer limite;
    private Integer saldo;
}
