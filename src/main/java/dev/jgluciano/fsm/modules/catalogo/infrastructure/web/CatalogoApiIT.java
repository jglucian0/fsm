package dev.jgluciano.fsm.modules.catalogo.infrastructure.web;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Fase 1d, RED. Ainda não existem: controllers, DTOs, tratamento de erros (RFC 9457) e o wiring dos casos de uso.
 *
 * Erros: Bean Validation -> 400 | invariante de domínio -> 422 | nome repetido -> 409 |
 * configuração inexistente -> 404. O dono vem de fsm.owner-id (usuário único até a fase de autenticação).
 */
@AutoConfigureMockMvc
class CatalogoApiIT extends PostgresIntegrationSupport {

    private static final String CONFIG_BASE = """
            {"custosFixosMensais": 3000.00, "proLabore": 5000.00, "horasProdutivasMensais": 160,
             "impostosPercentual": 6, "taxasFinanceirasPercentual": 4, "margemLucroPercentual": 20}""";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void limparTabelas() {
        jdbc.execute("DELETE FROM material");
        jdbc.execute("DELETE FROM servico");
        jdbc.execute("DELETE FROM configuracao_precificacao");
    }

    private ResultActions enviarPost(String url, String json) throws Exception {
        return mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions enviarPut(String url, String json) throws Exception {
        return mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    // ---------- materiais ----------

    @Test
    @DisplayName("POST /materiais cria o material e responde 201")
    void criaMaterial() throws Exception {
        enviarPost("/api/v1/materiais", """
                {"nome": "Cabo flexível 2,5mm", "unidade": "m", "custoUnitario": 3.5}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nome").value("Cabo flexível 2,5mm"))
                .andExpect(jsonPath("$.unidade").value("m"))
                .andExpect(jsonPath("$.custoUnitario").value(3.5));
    }

    @Test
    @DisplayName("GET /materiais lista em ordem alfabética")
    void listaMateriais() throws Exception {
        enviarPost("/api/v1/materiais", """
                {"nome": "Disjuntor 20A", "unidade": "un", "custoUnitario": 18.9}""").andExpect(status().isCreated());
        enviarPost("/api/v1/materiais", """
                {"nome": "Alicate", "unidade": "un", "custoUnitario": 40}""").andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/materiais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome").value("Alicate"))
                .andExpect(jsonPath("$[1].nome").value("Disjuntor 20A"));
    }

    @Test
    @DisplayName("POST /materiais com nome repetido responde 409 (problem+json)")
    void rejeitaMaterialDuplicado() throws Exception {
        var json = """
                {"nome": "Cabo", "unidade": "m", "custoUnitario": 3.5}""";
        enviarPost("/api/v1/materiais", json).andExpect(status().isCreated());

        enviarPost("/api/v1/materiais", json.replace("Cabo", "CABO"))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(containsString("CABO")));
    }

    @Test
    @DisplayName("POST /materiais sem nome responde 400 (Bean Validation)")
    void rejeitaMaterialSemNome() throws Exception {
        enviarPost("/api/v1/materiais", """
                {"unidade": "m", "custoUnitario": 3.5}""")
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    @DisplayName("POST /materiais com custo negativo responde 422 (invariante de domínio)")
    void rejeitaMaterialComCustoNegativo() throws Exception {
        enviarPost("/api/v1/materiais", """
                {"nome": "Cabo", "unidade": "m", "custoUnitario": -1}""")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(containsString("negativo")));
    }

    // ---------- serviços ----------

    @Test
    @DisplayName("POST /servicos cria o serviço e responde 201")
    void criaServico() throws Exception {
        enviarPost("/api/v1/servicos", """
                {"nome": "Instalação de tomada", "horasPadrao": 1.5}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nome").value("Instalação de tomada"))
                .andExpect(jsonPath("$.horasPadrao").value(1.5));
    }

    @Test
    @DisplayName("GET /servicos lista em ordem alfabética")
    void listaServicos() throws Exception {
        enviarPost("/api/v1/servicos", """
                {"nome": "Troca de disjuntor", "horasPadrao": 1}""").andExpect(status().isCreated());
        enviarPost("/api/v1/servicos", """
                {"nome": "Instalação de chuveiro", "horasPadrao": 2}""").andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/servicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome").value("Instalação de chuveiro"))
                .andExpect(jsonPath("$[1].nome").value("Troca de disjuntor"));
    }

    @Test
    @DisplayName("POST /servicos com nome repetido responde 409")
    void rejeitaServicoDuplicado() throws Exception {
        var json = """
                {"nome": "Troca de disjuntor", "horasPadrao": 1}""";
        enviarPost("/api/v1/servicos", json).andExpect(status().isCreated());

        enviarPost("/api/v1/servicos", json.replace("Troca", "TROCA"))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    @DisplayName("POST /servicos com horas zero responde 422")
    void rejeitaServicoComHorasZero() throws Exception {
        enviarPost("/api/v1/servicos", """
                {"nome": "Instalação", "horasPadrao": 0}""")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail").value(containsString("Horas")));
    }

    // ---------- configuração de precificação ----------

    @Test
    @DisplayName("GET /precificacao/configuracao responde 404 quando ainda não foi definida")
    void configuracaoInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/precificacao/configuracao"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    @DisplayName("PUT define a configuração, GET devolve, e novo PUT substitui")
    void definePrecificacaoEConsulta() throws Exception {
        enviarPut("/api/v1/precificacao/configuracao", CONFIG_BASE)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.proLabore").value(5000.0))
                .andExpect(jsonPath("$.horasProdutivasMensais").value(160));

        mockMvc.perform(get("/api/v1/precificacao/configuracao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.custosFixosMensais").value(3000.0))
                .andExpect(jsonPath("$.margemLucroPercentual").value(20));

        enviarPut("/api/v1/precificacao/configuracao", CONFIG_BASE.replace("5000.00", "6000.00"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/precificacao/configuracao"))
                .andExpect(jsonPath("$.proLabore").value(6000.0));
    }

    @Test
    @DisplayName("PUT com soma de percentuais >= 100 responde 422")
    void rejeitaConfiguracaoInvalida() throws Exception {
        enviarPut("/api/v1/precificacao/configuracao", CONFIG_BASE.replace("\"margemLucroPercentual\": 20",
                "\"margemLucroPercentual\": 90"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail").value(containsString("soma dos percentuais")));
    }

    // ---------- cálculo de preço ----------

    @Test
    @DisplayName("POST /precificacao/calculo usa a configuração do dono: 4h x 50 + 100 + 50 -> 500,00")
    void calculaPreco() throws Exception {
        enviarPut("/api/v1/precificacao/configuracao", CONFIG_BASE).andExpect(status().isOk());

        enviarPost("/api/v1/precificacao/calculo", """
                {"horasEstimadas": 4, "custoMateriais": 100.00, "despesasExtras": 50.00}""")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorHoraTecnica").value(50.0))
                .andExpect(jsonPath("$.custoDireto").value(350.0))
                .andExpect(jsonPath("$.indiceMarkup").value(0.7))
                .andExpect(jsonPath("$.precoFinal").value(500.0));
    }

    @Test
    @DisplayName("POST /precificacao/calculo sem configuração definida responde 404")
    void calculoSemConfiguracao() throws Exception {
        enviarPost("/api/v1/precificacao/calculo", """
                {"horasEstimadas": 4, "custoMateriais": 100, "despesasExtras": 50}""")
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /precificacao/calculo com horas negativas responde 422")
    void calculoComHorasNegativas() throws Exception {
        enviarPut("/api/v1/precificacao/configuracao", CONFIG_BASE).andExpect(status().isOk());

        enviarPost("/api/v1/precificacao/calculo", """
                {"horasEstimadas": -1, "custoMateriais": 0, "despesasExtras": 0}""")
                .andExpect(status().isUnprocessableEntity());
    }

    // ---------- documentação ----------

    @Test
    @DisplayName("OpenAPI publica os endpoints do catálogo")
    void publicaOpenApi() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v1/materiais']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/servicos']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/precificacao/calculo']").exists());
    }
}
