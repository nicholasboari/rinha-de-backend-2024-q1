package com.nicholasboari.springproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransacaoResponseDTO {

    private Long limite;
    private Long saldo;
}
