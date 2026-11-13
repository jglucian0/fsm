-- Fase 2b: clientes (ADR 0009, 0010). Toda tabela tem owner_id.
CREATE TABLE cliente (
    id       uuid         PRIMARY KEY,
    owner_id uuid         NOT NULL,
    nome     varchar(120) NOT NULL,
    telefone varchar(11),
    email    varchar(254),
    endereco varchar(255)
);

-- Só para listagem por dono em ordem alfabética: NÃO é único, nomes repetidos são permitidos.
CREATE INDEX ix_cliente_owner_nome ON cliente (owner_id, lower(nome));
