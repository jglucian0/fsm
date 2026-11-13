package dev.jgluciano.fsm.modules.clientes.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Fase 2a, RED. Ainda não existem: Cliente e ClienteInvalidoException.
 *
 * Regras: nome obrigatório (aparado, até 120); telefone, e-mail e endereço opcionais.
 * Telefone guarda só dígitos (DDD + número = 10 ou 11), aceitando o prefixo 55.
 * E-mail aparado e em minúsculas. Endereço aparado, até 255.
 */
class ClienteTest {

    private final UUID ownerId = UUID.randomUUID();

    private Cliente cliente(String nome, String telefone, String email, String endereco) {
        return Cliente.novo(ownerId, nome, telefone, email, endereco);
    }

    @Test
    @DisplayName("cria cliente válido com id gerado")
    void criaClienteValido() {
        var cliente = cliente("João da Silva", "(11) 98765-4321", "joao@exemplo.com", "Rua das Flores, 100");

        assertThat(cliente.id()).isNotNull();
        assertThat(cliente.ownerId()).isEqualTo(ownerId);
        assertThat(cliente.nome()).isEqualTo("João da Silva");
        assertThat(cliente.telefone()).isEqualTo("11987654321");
        assertThat(cliente.email()).isEqualTo("joao@exemplo.com");
        assertThat(cliente.endereco()).isEqualTo("Rua das Flores, 100");
    }

    @Test
    @DisplayName("cada cliente novo recebe um id diferente (nomes iguais são permitidos)")
    void geraIdsDiferentes() {
        var a = cliente("João da Silva", null, null, null);
        var b = cliente("João da Silva", null, null, null);

        assertThat(a.id()).isNotEqualTo(b.id());
    }

    @Test
    @DisplayName("apara espaços do nome")
    void aparaNome() {
        assertThat(cliente("  Maria  ", null, null, null).nome()).isEqualTo("Maria");
    }

    @ParameterizedTest(name = "[{index}] {0} -> {1}")
    @CsvSource(delimiter = '|', value = {
            "(11) 98765-4321|11987654321",
            "11 3456-7890|1134567890",
            "+55 11 98765-4321|11987654321",
            "55 11 3456-7890|1134567890"})
    @DisplayName("normaliza o telefone para dígitos, sem o prefixo 55")
    void normalizaTelefone(String informado, String esperado) {
        assertThat(cliente("Maria", informado, null, null).telefone()).isEqualTo(esperado);
    }

    @Test
    @DisplayName("telefone nulo ou em branco significa ausente")
    void telefoneAusente() {
        assertThat(cliente("Maria", null, null, null).telefone()).isNull();
        assertThat(cliente("Maria", "   ", null, null).telefone()).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "abc", "1198765432101"})
    @DisplayName("rejeita telefone inválido")
    void rejeitaTelefoneInvalido(String telefone) {
        assertThatThrownBy(() -> cliente("Maria", telefone, null, null))
                .isInstanceOf(ClienteInvalidoException.class)
                .message().containsIgnoringCase("telefone");
    }

    @Test
    @DisplayName("apara e coloca o e-mail em minúsculas")
    void normalizaEmail() {
        assertThat(cliente("Maria", null, "  Maria@Exemplo.COM ", null).email()).isEqualTo("maria@exemplo.com");
    }

    @Test
    @DisplayName("e-mail nulo ou em branco significa ausente")
    void emailAusente() {
        assertThat(cliente("Maria", null, null, null).email()).isNull();
        assertThat(cliente("Maria", null, "  ", null).email()).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"maria@", "maria.exemplo.com", "@exemplo.com"})
    @DisplayName("rejeita e-mail inválido")
    void rejeitaEmailInvalido(String email) {
        assertThatThrownBy(() -> cliente("Maria", null, email, null))
                .isInstanceOf(ClienteInvalidoException.class)
                .message().containsIgnoringCase("e-mail");
    }

    @Test
    @DisplayName("endereço em branco significa ausente e é aparado")
    void normalizaEndereco() {
        assertThat(cliente("Maria", null, null, "  ").endereco()).isNull();
        assertThat(cliente("Maria", null, null, " Rua A, 1 ").endereco()).isEqualTo("Rua A, 1");
    }

    @Test
    @DisplayName("rejeita endereço com mais de 255 caracteres")
    void rejeitaEnderecoLongo() {
        var endereco = "a".repeat(256);

        assertThatThrownBy(() -> cliente("Maria", null, null, endereco))
                .isInstanceOf(ClienteInvalidoException.class)
                .message().containsIgnoringCase("endereço");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t"})
    @DisplayName("rejeita nome em branco")
    void rejeitaNomeEmBranco(String nome) {
        assertThatThrownBy(() -> cliente(nome, null, null, null))
                .isInstanceOf(ClienteInvalidoException.class)
                .message().containsIgnoringCase("nome");
    }

    @Test
    @DisplayName("aceita nome com 120 caracteres e rejeita com 121")
    void limiteDoNome() {
        assertThat(cliente("a".repeat(120), null, null, null).nome()).hasSize(120);

        assertThatThrownBy(() -> cliente("a".repeat(121), null, null, null))
                .isInstanceOf(ClienteInvalidoException.class)
                .message().containsIgnoringCase("nome");
    }

    @Test
    @DisplayName("rejeita dono e nome nulos")
    void rejeitaNulos() {
        assertThatNullPointerException().isThrownBy(() -> Cliente.novo(null, "Maria", null, null, null));
        assertThatNullPointerException().isThrownBy(() -> Cliente.novo(ownerId, null, null, null, null));
    }
}
