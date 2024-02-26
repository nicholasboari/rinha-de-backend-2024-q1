package com.nicholasboari.springproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;

@Data
@Entity(name = "tb_transacao")
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Positive
    private Long valor;
    @Enumerated(EnumType.STRING)
    private TipoTransacaoEnum tipo;
    @Size(min = 1, max = 10)
    private String descricao;
    @JsonProperty("realizada_em")
    private Instant realizadaEm;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
}
