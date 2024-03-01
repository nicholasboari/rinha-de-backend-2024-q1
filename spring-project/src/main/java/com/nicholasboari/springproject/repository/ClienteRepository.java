package com.nicholasboari.springproject.repository;

import com.nicholasboari.springproject.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query(value = "SELECT * FROM tb_cliente WHERE id = :clienteId FOR UPDATE", nativeQuery = true)
    Optional<Cliente> findByIdWithLock(@Param("clienteId") Long clienteId);
}

