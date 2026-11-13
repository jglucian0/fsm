package dev.jgluciano.fsm.modules.catalogo.infrastructure.persistence;

import dev.jgluciano.fsm.PostgresIntegrationSupport;
import dev.jgluciano.fsm.modules.catalogo.core.model.Servico;
import dev.jgluciano.fsm.modules.catalogo.core.ports.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Fase 1d, RED. Ainda não existem: adaptador de persistência de Servico e a migration V2. */
class ServicoPersistenceIT extends PostgresIntegrationSupport {

    private final UUID ownerId = UUID.randomUUID();
    private final UUID outroOwnerId = UUID.randomUUID();

    @Autowired
    private ServicoRepository repository;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void limparTabela() {
        jdbc.execute("DELETE FROM servico");
    }

    private Servico servico(UUID owner, String nome) {
        return Servico.novo(owner, nome, new BigDecimal("1.5"));
    }

    @Test
    @DisplayName("salva e recupera o serviço sem perder campos (inclui acentos)")
    void salvaERecupera() {
        var servico = servico(ownerId, "Instalação de tomada");

        var salvo = repository.salvar(servico);

        assertThat(salvo).isEqualTo(servico);
        assertThat(repository.listarPorOwner(ownerId)).containsExactly(servico);
    }

    @Test
    @DisplayName("lista só os serviços do dono, em ordem alfabética")
    void listaSomenteDoOwnerEmOrdemAlfabetica() {
        repository.salvar(servico(ownerId, "Troca de disjuntor"));
        repository.salvar(servico(ownerId, "Instalação de chuveiro"));
        repository.salvar(servico(outroOwnerId, "Instalação de tomada"));

        var nomes = repository.listarPorOwner(ownerId).stream().map(Servico::nome).toList();

        assertThat(nomes).containsExactly("Instalação de chuveiro", "Troca de disjuntor");
    }

    @Test
    @DisplayName("verifica existência por dono e nome, sem diferenciar maiúsculas de minúsculas")
    void verificaExistenciaSemDiferenciarCaixa() {
        repository.salvar(servico(ownerId, "Troca de disjuntor"));

        assertThat(repository.existePorOwnerENome(ownerId, "TROCA DE DISJUNTOR")).isTrue();
        assertThat(repository.existePorOwnerENome(ownerId, "Outro")).isFalse();
        assertThat(repository.existePorOwnerENome(outroOwnerId, "Troca de disjuntor")).isFalse();
    }

    @Test
    @DisplayName("o banco impede nome repetido para o mesmo dono (índice único)")
    void bancoImpedeNomeDuplicado() {
        repository.salvar(servico(ownerId, "Troca de disjuntor"));

        assertThatThrownBy(() -> repository.salvar(servico(ownerId, "troca de disjuntor")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("permite o mesmo nome para donos diferentes")
    void permiteMesmoNomeParaDonosDiferentes() {
        repository.salvar(servico(ownerId, "Troca de disjuntor"));
        repository.salvar(servico(outroOwnerId, "Troca de disjuntor"));

        assertThat(repository.listarPorOwner(ownerId)).hasSize(1);
        assertThat(repository.listarPorOwner(outroOwnerId)).hasSize(1);
    }
}
