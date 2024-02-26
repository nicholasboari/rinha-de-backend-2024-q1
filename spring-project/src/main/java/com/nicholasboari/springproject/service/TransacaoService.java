package com.nicholasboari.springproject.service;

import com.nicholasboari.springproject.dto.ExtratoResponseDTO;
import com.nicholasboari.springproject.dto.SaldoDTO;
import com.nicholasboari.springproject.dto.TransacaoRequestDTO;
import com.nicholasboari.springproject.dto.TransacaoResponseDTO;
import com.nicholasboari.springproject.exception.ClienteNotFoundException;
import com.nicholasboari.springproject.exception.TaDuroDormeException;
import com.nicholasboari.springproject.model.Cliente;
import com.nicholasboari.springproject.model.TipoTransacaoEnum;
import com.nicholasboari.springproject.model.Transacao;
import com.nicholasboari.springproject.repository.ClienteRepository;
import com.nicholasboari.springproject.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final ClienteRepository clienteRepository;
    private final TransacaoRepository transacaoRepository;

    public TransacaoResponseDTO tranferir(Long id, TransacaoRequestDTO request) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado!"));
        if (request.getTipo() == TipoTransacaoEnum.c) {
            cliente.setSaldo(cliente.getSaldo() + request.getValor());
        } else {
            if (cliente.getSaldo() + cliente.getLimite() - request.getValor() < 0) {
                throw new TaDuroDormeException("sem cash, ta duro dorme!");
            }
            cliente.setSaldo(cliente.getSaldo() - request.getValor());
        }
        Transacao transacao = Transacao.builder()
                .valor(request.getValor())
                .tipo(request.getTipo())
                .descricao(request.getDescricao())
                .cliente(cliente)
                .realizadaEm(Instant.now())
                .build();
        transacaoRepository.save(transacao);
        return new TransacaoResponseDTO(cliente.getLimite(), cliente.getSaldo());
    }

    public ExtratoResponseDTO buscarUltimosExtratos(Long id) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado!"));
        SaldoDTO saldoDTO = SaldoDTO.builder().total(cliente.getSaldo())
                .dataExtrato(Instant.now())
                .limite(cliente.getLimite()).build();
        return new ExtratoResponseDTO(saldoDTO, transacaoRepository.findTop10ByClienteIdOrderByRealizadaEmDesc(id));
    }
}
