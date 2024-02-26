package com.nicholasboari.springproject.controller;

import com.nicholasboari.springproject.dto.ExtratoResponseDTO;
import com.nicholasboari.springproject.dto.TransacaoRequestDTO;
import com.nicholasboari.springproject.dto.TransacaoResponseDTO;
import com.nicholasboari.springproject.service.TransacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class TransacaoController {

    private final TransacaoService service;

    @PostMapping("/{id}/transacoes")
    public ResponseEntity<TransacaoResponseDTO> transferir(@PathVariable Long id, @RequestBody TransacaoRequestDTO request){
        return ResponseEntity.ok(service.tranferir(id, request));
    }

    @GetMapping("/{id}/extrato")
    public ResponseEntity<ExtratoResponseDTO> gerarExtrato(@PathVariable Long id){
        return ResponseEntity.ok(service.buscarUltimosExtratos(id));
    }
}
