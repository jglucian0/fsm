-- Fase 1d: catálogo (ADR 0006, 0007, 0008). Toda tabela tem owner_id.

-- Uma configuração de precificação por dono.
CREATE TABLE configuracao_precificacao (
                                           owner_id                     uuid          PRIMARY KEY,
                                           custos_fixos_mensais         numeric(14,2) NOT NULL CHECK (custos_fixos_mensais >= 0),
                                           pro_labore                   numeric(14,2) NOT NULL CHECK (pro_labore >= 0),
                                           horas_produtivas_mensais     numeric(8,2)  NOT NULL CHECK (horas_produtivas_mensais > 0),
                                           impostos_percentual          numeric(7,4)  NOT NULL CHECK (impostos_percentual >= 0),
                                           taxas_financeiras_percentual numeric(7,4)  NOT NULL CHECK (taxas_financeiras_percentual >= 0),
                                           margem_lucro_percentual      numeric(7,4)  NOT NULL CHECK (margem_lucro_percentual >= 0),
                                           CONSTRAINT ck_configuracao_soma_percentuais
                                               CHECK (impostos_percentual + taxas_financeiras_percentual + margem_lucro_percentual < 100)
);

CREATE TABLE material (
                          id             uuid          PRIMARY KEY,
                          owner_id       uuid          NOT NULL,
                          nome           varchar(120)  NOT NULL,
                          unidade        text          NOT NULL,
                          custo_unitario numeric(14,2) NOT NULL CHECK (custo_unitario >= 0)
);
CREATE UNIQUE INDEX uq_material_owner_nome ON material (owner_id, lower(nome));

CREATE TABLE servico (
                         id           uuid         PRIMARY KEY,
                         owner_id     uuid         NOT NULL,
                         nome         varchar(120) NOT NULL,
                         horas_padrao numeric(8,2) NOT NULL CHECK (horas_padrao > 0)
);
CREATE UNIQUE INDEX uq_servico_owner_nome ON servico (owner_id, lower(nome));