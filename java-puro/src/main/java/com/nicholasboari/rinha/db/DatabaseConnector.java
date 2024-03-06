package com.nicholasboari.rinha.db;


import com.nicholasboari.rinha.model.Transacao;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector {
    private static final String DB_URL = "jdbc:postgresql://db:5432/rinha";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "admin";

    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setMaximumPoolSize(15);
        config.setMinimumIdle(5);
        config.setIdleTimeout(30000);
        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void saveTransacao(int clienteId, double valor, String tipo, String descricao, Object realizadaEm) {
        String sql = "INSERT INTO tb_transacao (cliente_id, valor, tipo, descricao, realizada_em) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, clienteId);
                pstmt.setDouble(2, valor);
                pstmt.setString(3, tipo);
                pstmt.setString(4, descricao);
                pstmt.setTimestamp(5, Timestamp.from((Instant) realizadaEm));
                pstmt.executeUpdate();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Deu ruim: ", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Deu ruim: ", e);
        }
    }

    public static List<Transacao> getUltimasTransacoesDoCliente(int clienteId) {
        List<Transacao> transacoes = new ArrayList<>();
        String sql = "SELECT valor, tipo, descricao, realizada_em\n" +
                "FROM tb_transacao\n" +
                "WHERE cliente_id = ?\n" +
                "ORDER BY realizada_em DESC\n" +
                "LIMIT 10 FOR UPDATE";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, clienteId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Transacao transacao = new Transacao();
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

    public static Cliente getClienteInfo(int clienteId) {
        String sql = "SELECT * FROM tb_cliente WHERE cliente_id = ? FOR UPDATE";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, clienteId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int saldo = rs.getInt("saldo");
                int limite = rs.getInt("limite");
                return new Cliente(saldo, limite);
            } else {
                throw new SQLException("Deu ruim com o id" + clienteId + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static void updateSaldoCliente(int clienteId, int saldo) {
        String sql = "UPDATE tb_cliente SET saldo = ? WHERE cliente_id = ?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, saldo);
            pstmt.setInt(2, clienteId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Deu ruim: " + e.getMessage());
        }
    }

    public static class Cliente {
        int saldo;
        int limite;

        public Cliente(int saldo, int limite) {
            this.saldo = saldo;
            this.limite = limite;
        }

        public int getSaldo() {
            return saldo;
        }

        public void setSaldo(int saldo) {
            this.saldo = saldo;
        }

        public int getLimite() {
            return limite;
        }

        public void setLimite(int limite) {
            this.limite = limite;
        }
    }
}