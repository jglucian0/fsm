package dev.jgluciano.fsm.modules.catalogo.infrastructure.persistence;

import dev.jgluciano.fsm.PostgresIntegrationSupport;
import dev.jgluciano.fsm.modules.catalogo.core.model.Material;
import dev.jgluciano.fsm.modules.catalogo.core.ports.MaterialRepository;
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

/** Fase 1d, RED. Ainda não existem: adaptador de persistência de Material e a migration V2. */
class MaterialPersistenceIT extends PostgresIntegrationSupport {

    private final UUID ownerId = UUID.randomUUID();
    private final UUID outroOwnerId = UUID.randomUUID();

    @Autowired
    private MaterialRepository repository;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void limparTabela() {
        jdbc.execute("DELETE FROM material");
    }

    private Material material(UUID owner, String nome) {
        return Material.novo(owner, nome, "m", new BigDecimal("3.5"));
    }

    @Test
    @DisplayName("salva e recupera o material sem perder campos (inclui acentos)")
    void salvaERecupera() {
        var material = material(ownerId, "Cabo flexível 2,5mm");

        var salvo = repository.salvar(material);

        assertThat(salvo).isEqualTo(material);
        assertThat(repository.listarPorOwner(ownerId)).containsExactly(material);
    }

    @Test
    @DisplayName("lista só os materiais do dono, em ordem alfabética")
    void listaSomenteDoOwnerEmOrdemAlfabetica() {
        repository.salvar(material(ownerId, "Disjuntor"));
        repository.salvar(material(ownerId, "Alicate"));
        repository.salvar(material(outroOwnerId, "Cabo"));

        var nomes = repository.listarPorOwner(ownerId).stream().map(Material::nome).toList();

        assertThat(nomes).containsExactly("Alicate", "Disjuntor");
    }

    @Test
    @DisplayName("verifica existência por dono e nome, sem diferenciar maiúsculas de minúsculas")
    void verificaExistenciaSemDiferenciarCaixa() {
        repository.salvar(material(ownerId, "Cabo"));

        assertThat(repository.existePorOwnerENome(ownerId, "CABO")).isTrue();
        assertThat(repository.existePorOwnerENome(ownerId, "Fita")).isFalse();
        assertThat(repository.existePorOwnerENome(outroOwnerId, "Cabo")).isFalse();
    }

    @Test
    @DisplayName("o banco impede nome repetido para o mesmo dono (índice único)")
    void bancoImpedeNomeDuplicado() {
        repository.salvar(material(ownerId, "Cabo"));

        assertThatThrownBy(() -> repository.salvar(material(ownerId, "cabo")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("permite o mesmo nome para donos diferentes")
    void permiteMesmoNomeParaDonosDiferentes() {
        repository.salvar(material(ownerId, "Cabo"));
        repository.salvar(material(outroOwnerId, "Cabo"));

        assertThat(repository.listarPorOwner(ownerId)).hasSize(1);
        assertThat(repository.listarPorOwner(outroOwnerId)).hasSize(1);
    }
}
