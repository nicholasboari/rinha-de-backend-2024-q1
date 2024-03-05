package com.nicholasboari.rinha.model;

import java.time.Instant;

public class Transacao {

    private Integer clienteId;
    private Double valor;
    private String tipo;
    private String descricao;
    private Instant realizadaEm;

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Instant getRealizadaEm() {
        return realizadaEm;
    }

    public void setRealizadaEm(Instant realizadaEm) {
        this.realizadaEm = realizadaEm;
    }
}
