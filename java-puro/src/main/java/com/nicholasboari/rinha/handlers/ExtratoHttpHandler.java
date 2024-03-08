package com.nicholasboari.rinha.handlers;

import com.nicholasboari.rinha.db.DatabaseConnector;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
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
            JSONObject object = DatabaseConnector.findTenById(clienteId);
            exchange.getResponseHeaders().set("Content-Type", "application/json");

            exchange.sendResponseHeaders(200, object.toString().getBytes().length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(object.toString().getBytes());
            outputStream.close();
        } else {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        }
    }
}