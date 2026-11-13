package dev.jgluciano.fsm.modules.clientes.infrastructure.persistence;

import dev.jgluciano.fsm.PostgresIntegrationSupport;
import dev.jgluciano.fsm.modules.clientes.core.model.Cliente;
import dev.jgluciano.fsm.modules.clientes.core.ports.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/** Fase 2b, RED. Ainda não existem: adaptador de persistência de Cliente e a migration V3. */
class ClientePersistenceIT extends PostgresIntegrationSupport {

    private final UUID ownerId = UUID.randomUUID();
    private final UUID outroOwnerId = UUID.randomUUID();

    @Autowired
    private ClienteRepository repository;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void limparTabela() {
        jdbc.execute("DELETE FROM cliente");
    }

    @Test
    @DisplayName("salva e recupera o cliente completo sem perder campos (inclui acentos)")
    void salvaERecupera() {
        var cliente = Cliente.novo(ownerId, "João da Conceição", "(11) 98765-4321", "joao@exemplo.com",
                "Rua das Flores, 100 - São Paulo");

        var salvo = repository.salvar(cliente);

        assertThat(salvo).isEqualTo(cliente);
        assertThat(repository.listarPorOwner(ownerId)).containsExactly(cliente);
    }

    @Test
    @DisplayName("preserva campos opcionais ausentes (null)")
    void preservaCamposOpcionaisNulos() {
        var cliente = Cliente.novo(ownerId, "Maria", null, null, null);

        repository.salvar(cliente);

        var recuperado = repository.listarPorOwner(ownerId).get(0);
        assertThat(recuperado.telefone()).isNull();
        assertThat(recuperado.email()).isNull();
        assertThat(recuperado.endereco()).isNull();
        assertThat(recuperado).isEqualTo(cliente);
    }

    @Test
    @DisplayName("lista só os clientes do dono, em ordem alfabética")
    void listaSomenteDoOwnerEmOrdemAlfabetica() {
        repository.salvar(Cliente.novo(ownerId, "Bruno", null, null, null));
        repository.salvar(Cliente.novo(ownerId, "Ana", null, null, null));
        repository.salvar(Cliente.novo(outroOwnerId, "Carlos", null, null, null));

        var nomes = repository.listarPorOwner(ownerId).stream().map(Cliente::nome).toList();

        assertThat(nomes).containsExactly("Ana", "Bruno");
    }

    @Test
    @DisplayName("permite nomes repetidos para o mesmo dono (pessoas diferentes)")
    void permiteNomesRepetidos() {
        repository.salvar(Cliente.novo(ownerId, "João Silva", "11987654321", null, null));
        repository.salvar(Cliente.novo(ownerId, "João Silva", "11912345678", null, null));

        assertThat(repository.listarPorOwner(ownerId)).hasSize(2);
    }

    @Test
    @DisplayName("devolve lista vazia para dono sem clientes")
    void devolveListaVazia() {
        assertThat(repository.listarPorOwner(UUID.randomUUID())).isEmpty();
    }
}
