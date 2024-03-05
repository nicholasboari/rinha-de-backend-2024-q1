package com.nicholasboari.rinha.db;


import com.nicholasboari.rinha.model.Transacao;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector {
    private static final String DB_URL = "jdbc:postgresql://db:5432/rinha";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "admin";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void saveTransaction(int clienteId, double valor, String tipo, String descricao, Object realizadaEm) {
        String sql = "INSERT INTO tb_transacao (cliente_id, valor, tipo, descricao, realizada_em) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, clienteId);
            pstmt.setDouble(2, valor);
            pstmt.setString(3, tipo);
            pstmt.setString(4, descricao);
            pstmt.setTimestamp(5, Timestamp.from((Instant) realizadaEm));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Deu ruim: " + e.getMessage());
        }
    }

    public static List<Transacao> getUltimasTransacoesDoCliente(int clienteId) {
        List<Transacao> transacoes = new ArrayList<>();
        String sql = "SELECT * FROM tb_transacao WHERE cliente_id = ? ORDER BY realizada_em DESC LIMIT 10";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, clienteId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Transacao transacao = new Transacao();
                transacao.setClienteId(rs.getInt("cliente_id"));
                transacao.setValor(rs.getDouble("valor"));
                transacao.setTipo(rs.getString("tipo"));
                transacao.setDescricao(rs.getString("descricao"));
                transacao.setRealizadaEm(rs.getTimestamp("realizada_em").toInstant());
                transacoes.add(transacao);
            }
        } catch (SQLException e) {
            System.out.println("Deu ruim: " + e.getMessage());
        }

        return transacoes;
    }

    public static Cliente getCliente(int clienteId) {
        String sql = "SELECT saldo, limite FROM tb_cliente WHERE cliente_id = ? FOR UPDATE";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, clienteId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int saldo = rs.getInt("saldo");
                int limite = rs.getInt("limite");
                return new Cliente(saldo, limite);
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Deu ruim: " + e.getMessage());
            return null;
        }
    }

    public record Cliente(int saldo, int limite) {

    }
}