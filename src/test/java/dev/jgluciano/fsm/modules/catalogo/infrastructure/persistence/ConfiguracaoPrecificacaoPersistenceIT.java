package dev.jgluciano.fsm.modules.catalogo.infrastructure.persistence;

import dev.jgluciano.fsm.PostgresIntegrationSupport;
import dev.jgluciano.fsm.modules.catalogo.core.model.ConfiguracaoPrecificacao;
import dev.jgluciano.fsm.modules.catalogo.core.ports.ConfiguracaoPrecificacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/** Fase 1d, RED. Ainda não existem: adaptador de ConfiguracaoPrecificacao e a migration V2. */
class ConfiguracaoPrecificacaoPersistenceIT extends PostgresIntegrationSupport {

    private final UUID ownerId = UUID.randomUUID();

    @Autowired
    private ConfiguracaoPrecificacaoRepository repository;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void limparTabela() {
        jdbc.execute("DELETE FROM configuracao_precificacao");
    }

    private static BigDecimal bd(String valor) {
        return new BigDecimal(valor);
    }

    private ConfiguracaoPrecificacao config(UUID owner, String proLabore, String margem) {
        return new ConfiguracaoPrecificacao(owner, bd("3000.00"), bd(proLabore), bd("160"),
                bd("6.5"), bd("3.25"), bd(margem));
    }

    private static void assertMesmosValores(ConfiguracaoPrecificacao atual, ConfiguracaoPrecificacao esperada) {
        assertThat(atual).usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .isEqualTo(esperada);
    }

    @Test
    @DisplayName("devolve vazio quando o dono não tem configuração")
    void devolveVazioSemConfiguracao() {
        assertThat(repository.buscarPorOwner(ownerId)).isEmpty();
    }

    @Test
    @DisplayName("salva e recupera a configuração com percentuais fracionados")
    void salvaERecupera() {
        var configuracao = config(ownerId, "5000.00", "15.25");

        repository.salvar(configuracao);

        var recuperada = repository.buscarPorOwner(ownerId);
        assertThat(recuperada).isPresent();
        assertMesmosValores(recuperada.get(), configuracao);
    }

    @Test
    @DisplayName("salvar de novo substitui a configuração do dono (uma por dono)")
    void salvarDeNovoSubstitui() {
        repository.salvar(config(ownerId, "5000.00", "15.25"));
        var nova = config(ownerId, "6000.00", "20");

        repository.salvar(nova);

        assertMesmosValores(repository.buscarPorOwner(ownerId).orElseThrow(), nova);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM configuracao_precificacao WHERE owner_id = ?",
                Integer.class, ownerId)).isEqualTo(1);
    }

    @Test
    @DisplayName("isola a configuração por dono")
    void isolaPorOwner() {
        var outro = UUID.randomUUID();
        repository.salvar(config(ownerId, "5000.00", "15.25"));
        repository.salvar(config(outro, "7000.00", "10"));

        assertMesmosValores(repository.buscarPorOwner(ownerId).orElseThrow(), config(ownerId, "5000.00", "15.25"));
        assertMesmosValores(repository.buscarPorOwner(outro).orElseThrow(), config(outro, "7000.00", "10"));
    }
}
