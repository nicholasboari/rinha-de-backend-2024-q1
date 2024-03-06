package com.nicholasboari.rinha.server;

import com.nicholasboari.rinha.db.DatabaseConnector;
import com.nicholasboari.rinha.util.Validator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransacaoHttpHandler implements HttpHandler {

    private final String regexPattern;

    public TransacaoHttpHandler(String regexPattern) {
        this.regexPattern = regexPattern;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // pulei a verificacao do metodo da request 😈😈😈
        System.out.println("RECEBI REQUEST DE TRANSACAO");

        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(exchange.getRequestURI().getPath());
        if (matcher.find()) {
            int clienteId = Integer.parseInt(matcher.group(1));

            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                requestBody.append(line);
            }

            try {
                JSONObject jsonObject = new JSONObject(requestBody.toString());
                if (!Validator.validate(jsonObject)) {
                    exchange.sendResponseHeaders(422, -1);
                    exchange.close();
                    return;
                }

                DatabaseConnector.Cliente cliente = DatabaseConnector.getClienteInfo(clienteId);
                int saldo = cliente.getSaldo();
                int limite = cliente.getLimite();

                if (jsonObject.getString("tipo").equals("d") && (saldo + limite) < jsonObject.getInt("valor")) {
                    exchange.sendResponseHeaders(422, -1);
                    exchange.close();
                    return;
                }

                switch (jsonObject.getString("tipo")) {
                    case "c" -> saldo += jsonObject.getInt("valor");
                    case "d" -> saldo -= jsonObject.getInt("valor");
                }
                DatabaseConnector.updateSaldoCliente(clienteId, saldo);

                jsonObject.put("realizada_em", Instant.now());
                DatabaseConnector.saveTransacao(
                        clienteId,
                        jsonObject.getDouble("valor"),
                        jsonObject.getString("tipo"),
                        jsonObject.getString("descricao"),
                        jsonObject.get("realizada_em"));

                String response = "{\"saldo\": " + saldo + ", \"limite\": " + limite + "}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (JSONException e) {
                System.out.println(e.getMessage());
                exchange.sendResponseHeaders(400, -1);
                exchange.close();
            }
        } else {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        }
    }
}