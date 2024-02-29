package com.nicholasboari.springproject.service;

import com.nicholasboari.springproject.dto.ExtratoResponseDTO;
import com.nicholasboari.springproject.dto.SaldoDTO;
import com.nicholasboari.springproject.dto.TransacaoRequestDTO;
import com.nicholasboari.springproject.dto.TransacaoResponseDTO;
import com.nicholasboari.springproject.exception.ClienteNotFoundException;
import com.nicholasboari.springproject.exception.TaDuroDormeException;
import com.nicholasboari.springproject.model.Cliente;
import com.nicholasboari.springproject.model.Transacao;
import com.nicholasboari.springproject.repository.ClienteRepository;
import com.nicholasboari.springproject.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final ClienteRepository clienteRepository;
    private final TransacaoRepository transacaoRepository;

    @Transactional
    public TransacaoResponseDTO transferir(Long id, TransacaoRequestDTO request) {
        Cliente cliente = clienteRepository.findByIdWithLock(id).orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado!"));

        switch (request.getTipo()) {
            case "c":
                cliente.setSaldo(cliente.getSaldo() + request.getValor().intValue());
                break;
            case "d":
                if (cliente.getSaldo() - request.getValor() < cliente.getLimite() * -1) {
                    throw new TaDuroDormeException("sem cash, ta duro dorme!");
                }
                cliente.setSaldo(cliente.getSaldo() - request.getValor().intValue());
                break;
        }
        Transacao transacao = Transacao.builder()
                .valor(request.getValor().intValue())
                .tipo(request.getTipo())
                .descricao(request.getDescricao())
                .cliente(cliente)
                .realizadaEm(Instant.now())
                .build();
        transacaoRepository.save(transacao);
        return new TransacaoResponseDTO(cliente.getLimite(), cliente.getSaldo());
    }

    @Transactional
    public ExtratoResponseDTO buscarUltimosExtratos(Long id) {
        Cliente cliente = clienteRepository.findByIdWithLock(id).orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado!"));
        SaldoDTO saldoDTO = SaldoDTO.builder().total(cliente.getSaldo())
                .dataExtrato(Instant.now())
                .limite(cliente.getLimite()).build();
        return new ExtratoResponseDTO(saldoDTO, transacaoRepository.findLast10TransactionsByClientIdWithLock(id));
    }
}
