package com.nicholasboari.rinha;

import com.nicholasboari.rinha.handlers.ClientesHttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App {
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("server.port", "9000"));
        InetSocketAddress address = new InetSocketAddress(port);
        HttpServer server = HttpServer.create(address, 0);

        server.createContext("/clientes", new ClientesHttpHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Server de pe");
    }
}
