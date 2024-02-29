package com.nicholasboari.springproject.repository;

import com.nicholasboari.springproject.model.Transacao;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    @Query(value = "SELECT * FROM tb_transacao WHERE cliente_id = :clienteId ORDER BY realizada_em DESC LIMIT 10 FOR UPDATE", nativeQuery = true)
    List<Transacao> findLast10TransactionsByClientIdWithLock(@Param("clienteId") Long clienteId);
}
