package com.nicholasboari.rinha.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class ClientesHttpHandler implements HttpHandler {
    private static final String PATH_CLIENTES = "/clientes/([1-5])/";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] segments = path.split("/");

        String lastSegment = segments[segments.length - 1];

        if (lastSegment.equals("transacoes")) {
            new TransacaoHttpHandler(PATH_CLIENTES + "transacoes").handle(exchange);
        } else if (lastSegment.equals("extrato")) {
            new ExtratoHttpHandler(PATH_CLIENTES + "extrato").handle(exchange);
        } else {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        }
    }
}