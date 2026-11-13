package dev.jgluciano.fsm.modules.clientes.infrastructure.web;

import dev.jgluciano.fsm.PostgresIntegrationSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Fase 2b, RED. Ainda não existem: controller, DTOs de cliente, ClienteInvalidoException no tratamento de erros
 * e o wiring dos casos de uso. Mesmos códigos de erro do catálogo (ADR 0008): 400 formato, 422 domínio.
 */
@AutoConfigureMockMvc
class ClienteApiIT extends PostgresIntegrationSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void limparTabela() {
        jdbc.execute("DELETE FROM cliente");
    }

    private ResultActions enviarPost(String json) throws Exception {
        return mockMvc.perform(post("/api/v1/clientes").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    @DisplayName("POST /clientes cria o cliente com dados normalizados e responde 201")
    void criaCliente() throws Exception {
        enviarPost("""
                {"nome": "  João da Silva ", "telefone": "(11) 98765-4321", "email": "Joao@Exemplo.com",
                 "endereco": "Rua das Flores, 100"}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nome").value("João da Silva"))
                .andExpect(jsonPath("$.telefone").value("11987654321"))
                .andExpect(jsonPath("$.email").value("joao@exemplo.com"))
                .andExpect(jsonPath("$.endereco").value("Rua das Flores, 100"));
    }

    @Test
    @DisplayName("POST /clientes aceita só o nome; opcionais voltam nulos")
    void criaClienteSoComNome() throws Exception {
        enviarPost("""
                {"nome": "Maria"}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.telefone").value(nullValue()))
                .andExpect(jsonPath("$.email").value(nullValue()))
                .andExpect(jsonPath("$.endereco").value(nullValue()));
    }

    @Test
    @DisplayName("POST /clientes permite nomes repetidos")
    void permiteNomesRepetidos() throws Exception {
        var json = """
                {"nome": "João Silva"}""";
        enviarPost(json).andExpect(status().isCreated());
        enviarPost(json).andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /clientes lista em ordem alfabética")
    void listaClientes() throws Exception {
        enviarPost("""
                {"nome": "Bruno"}""").andExpect(status().isCreated());
        enviarPost("""
                {"nome": "Ana", "telefone": "11 3456-7890"}""").andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome").value("Ana"))
                .andExpect(jsonPath("$[0].telefone").value("1134567890"))
                .andExpect(jsonPath("$[1].nome").value("Bruno"));
    }

    @Test
    @DisplayName("POST /clientes sem nome responde 400 (Bean Validation)")
    void rejeitaClienteSemNome() throws Exception {
        enviarPost("""
                {"telefone": "11987654321"}""")
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    @DisplayName("POST /clientes com telefone inválido responde 422")
    void rejeitaTelefoneInvalido() throws Exception {
        enviarPost("""
                {"nome": "Maria", "telefone": "123"}""")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(containsString("Telefone")));
    }

    @Test
    @DisplayName("POST /clientes com e-mail inválido responde 422")
    void rejeitaEmailInvalido() throws Exception {
        enviarPost("""
                {"nome": "Maria", "email": "maria@"}""")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail").value(containsString("E-mail")));
    }

    @Test
    @DisplayName("POST /clientes com e-mail acima de 254 caracteres responde 400 (limite da coluna)")
    void rejeitaEmailLongoDemais() throws Exception {
        var email = "a".repeat(250) + "@b.co";
        enviarPost("{\"nome\": \"Maria\", \"email\": \"" + email + "\"}")
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("OpenAPI publica o endpoint de clientes")
    void publicaOpenApi() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v1/clientes']").exists());
    }
}
