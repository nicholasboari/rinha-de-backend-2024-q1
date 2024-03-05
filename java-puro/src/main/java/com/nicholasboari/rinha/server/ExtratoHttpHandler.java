package com.nicholasboari.rinha.server;

import com.nicholasboari.rinha.db.DatabaseConnector;
import com.nicholasboari.rinha.model.Transacao;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtratoHttpHandler implements HttpHandler {

    private final String regexPattern;

    public ExtratoHttpHandler(String regexPattern) {
        this.regexPattern = regexPattern;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // pulei a verificacao do metodo da request 😈😈😈

        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(exchange.getRequestURI().getPath());

        if (matcher.matches()) {
            int clienteId = Integer.parseInt(matcher.group(1));

            List<Transacao> transacoes = DatabaseConnector.getUltimasTransacoesDoCliente(clienteId);

            JSONArray transacoesJson = getTransacoesJson(transacoes);

            DatabaseConnector.Cliente cliente = DatabaseConnector.getCliente(clienteId);

            JSONObject jsonObject = new JSONObject();
            JSONObject saldoJson = new JSONObject();

            saldoJson.put("total", cliente.saldo());
            saldoJson.put("limite", cliente.limite());
            saldoJson.put("data_extrato", Instant.now());

            jsonObject.put("saldo", saldoJson);
            jsonObject.put("ultimas_transacoes", transacoesJson);

            exchange.getResponseHeaders().set("Content-Type", "application/json");

            exchange.sendResponseHeaders(200, jsonObject.toString().getBytes().length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(jsonObject.toString().getBytes());
            outputStream.close();
        }
    }

    private static JSONArray getTransacoesJson(List<Transacao> transacoes) {
        JSONArray transacoesJson = new JSONArray();
        for (Transacao transacao : transacoes) {
            JSONObject jsonTransacao = new JSONObject();
            jsonTransacao.put("valor", transacao.getValor());
            jsonTransacao.put("tipo", transacao.getTipo());
            jsonTransacao.put("descricao", transacao.getDescricao());
            jsonTransacao.put("realizada_em", transacao.getRealizadaEm().toString());
            transacoesJson.put(jsonTransacao);
        }
        return transacoesJson;
    }

}