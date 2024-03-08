package com.nicholasboari.rinha.db;


import com.nicholasboari.rinha.model.Transacao;
import com.sun.net.httpserver.HttpExchange;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseConnector.class);
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

    public static JSONObject saveTransacao(int clientId, int valor, String tipo, String descricao, Object realizadaEm, HttpExchange exchange) {
        String sql1 = "SELECT * FROM tb_cliente WHERE cliente_id = ? FOR UPDATE;";
        LOG.info(sql1);

        String sql2 = "INSERT INTO tb_transacao (valor, tipo, descricao, cliente_id, realizada_em) VALUES (?, ?, ?, ?, ?);";
        LOG.info(sql2);

        String sql3 = "UPDATE tb_cliente SET saldo = ? WHERE cliente_id = ?;";
        LOG.info(sql3);

        try (Connection connection = DatabaseConnector.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement selectClientStmt = connection.prepareStatement(sql1);
                 PreparedStatement insertTransactiontStmt = connection.prepareStatement(sql2);
                 PreparedStatement updateClientStmt = connection.prepareStatement(sql3, Statement.RETURN_GENERATED_KEYS)) {

                selectClientStmt.setLong(1, clientId);
                insertTransactiontStmt.setLong(1, valor);
                insertTransactiontStmt.setString(2, tipo);
                insertTransactiontStmt.setString(3, descricao);
                insertTransactiontStmt.setLong(4, clientId);
                insertTransactiontStmt.setTimestamp(5, Timestamp.from((Instant) realizadaEm));

                ResultSet resultSet = selectClientStmt.executeQuery();

                if (resultSet.next()) {
                    long limite = resultSet.getInt("limite");
                    long saldo = resultSet.getInt("saldo");

                    if (tipo.equals("d") && saldo + limite < valor) {
                        exchange.sendResponseHeaders(422, -1);
                        exchange.close();
                        return null;
                    }

                    switch (tipo) {
                        case "c" -> saldo += valor;
                        case "d" -> saldo -= valor;
                    }
                    updateClientStmt.setLong(1, saldo);
                    updateClientStmt.setLong(2, clientId);
                    updateClientStmt.executeUpdate();
                    insertTransactiontStmt.executeUpdate();

                    ResultSet generatedKeys = updateClientStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.accumulate("limite", generatedKeys.getInt(2));
                        jsonObject.accumulate("saldo", generatedKeys.getInt(3));
                        connection.commit();
                        return jsonObject;
                    } else {
                        connection.rollback();
                        return null;
                    }
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                try {
                    connection.rollback();
                } catch (SQLException sqlException2) {
                    sqlException2.printStackTrace();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    public static JSONObject findTenById(int clientId) {
        String sql1 = "SELECT * FROM tb_cliente WHERE cliente_id = ? FOR UPDATE;";
        String sql2 = "SELECT * FROM tb_transacao WHERE cliente_id = ? ORDER BY realizada_em DESC LIMIT 10 FOR UPDATE;";
        LOG.info(sql1);
        LOG.info(sql2);

        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement selectClientStmt = connection.prepareStatement(sql1);
                 PreparedStatement selectTransactionsStmt = connection.prepareStatement(sql2)) {
                selectClientStmt.setLong(1, clientId);
                selectTransactionsStmt.setLong(1, clientId);
                ResultSet ClientResultSet = selectClientStmt.executeQuery();
                ResultSet transactionsResultSet = selectTransactionsStmt.executeQuery();
                List<Transacao> transactionList = new ArrayList<>();
                while (transactionsResultSet.next()) {
                    Transacao transaction = new Transacao();
                    transaction.setValor(transactionsResultSet.getDouble("valor"));
                    transaction.setTipo(transactionsResultSet.getString("tipo"));
                    transaction.setDescricao(transactionsResultSet.getString("descricao"));
                    transactionList.add(transaction);
                }
                if (ClientResultSet.next()) {
                    JSONObject saldo = new JSONObject();
                    saldo.accumulate("total", ClientResultSet.getInt("saldo"));
                    saldo.accumulate("limite", ClientResultSet.getInt("limite"));
                    saldo.accumulate("data_extrato", Instant.now());
                    JSONObject saldoObj = new JSONObject();
                    saldoObj.accumulate("saldo", saldo);
                    saldoObj.accumulate("ultimas_transacoes", transactionList);
                    connection.commit();
                    return saldoObj;
                }

            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                try {
                    connection.rollback();
                } catch (SQLException sqlException2) {
                    sqlException2.printStackTrace();
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }
}