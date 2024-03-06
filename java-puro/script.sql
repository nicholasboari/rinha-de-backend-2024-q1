CREATE TABLE tb_cliente (
                            cliente_id BIGSERIAL PRIMARY KEY,
                            limite BIGINT NOT NULL,
                            saldo BIGINT NOT NULL
);

CREATE TABLE tb_transacao (
                              transacao_id SERIAL PRIMARY KEY,
                              valor BIGINT NOT NULL,
                              tipo VARCHAR(255) NOT NULL,
                              descricao VARCHAR(255) NOT NULL,
                              realizada_em TIMESTAMP NOT NULL,
                              cliente_id BIGINT NOT NULL,
                              FOREIGN KEY (cliente_id) REFERENCES tb_cliente(cliente_id)
);

CREATE INDEX idx_tb_transacao_cliente_id ON tb_transacao (cliente_id);
CREATE INDEX idx_tb_transacao_realizada_em ON tb_transacao (realizada_em);
CREATE INDEX idx_tb_cliente_cliente_id ON tb_cliente (cliente_id);

DO $$
BEGIN
INSERT INTO tb_cliente (saldo, limite) VALUES (0, 100000);
INSERT INTO tb_cliente (saldo, limite) VALUES (0, 80000);
INSERT INTO tb_cliente (saldo, limite) VALUES (0, 1000000);
INSERT INTO tb_cliente (saldo, limite) VALUES (0, 10000000);
INSERT INTO tb_cliente (saldo, limite) VALUES (0, 500000);
END $$;
