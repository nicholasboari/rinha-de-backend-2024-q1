package com.nicholasboari.springproject.repository;

import com.nicholasboari.springproject.model.Cliente;
import com.nicholasboari.springproject.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findTop10ByClienteIdOrderByRealizadaEmDesc(Long clienteId);
}
