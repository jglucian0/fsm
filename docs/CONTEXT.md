# CONTEXT — FSM (cole este arquivo no início de uma conversa nova)

## Objetivo
App web (PWA no futuro) para uso pessoal de um eletricista autônomo, inspirado no "Meu Ajudante":
calculadora de preços, orçamento rápido, envio por WhatsApp/e-mail, agenda com lembretes, lista de materiais e recibos.
Projeto de portfólio de backend Java: o foco é arquitetura e qualidade de código.

## Stack
Java 21 (records, pattern matching, virtual threads) · Spring Boot 3.3.5 (Web, Data JPA, Validation, Actuator) ·
Maven · PostgreSQL 16 · Flyway · MapStruct 1.5.5 · Springdoc 2.6 · JUnit 5, Mockito, AssertJ 3.25, Testcontainers, ArchUnit ·
Docker Compose · GitHub Actions. RabbitMQ entra só na Fase 7. IDE: IntelliJ IDEA. Sem Lombok.

## Módulos (pacote base `dev.jgluciano.fsm.modules.<modulo>`)
| Módulo | Responsabilidade |
|---|---|
| catalogo | materiais (custo), serviços (homem-hora), calculadora de preços |
| clientes | cadastro de clientes |
| orcamento | orçamento, itens, taxa de deslocamento, desconto, status (RASCUNHO, ENVIADO, APROVADO, REJEITADO, FINALIZADO) |
| agenda | conversão em OS, anti double-booking, atrasos, lembretes |
| documentos | PDF, e-mail, link WhatsApp, recibos (assíncrono via RabbitMQ na Fase 7) |

Estrutura de cada módulo: `api/` · `core/{model,ports,usecase}` · `infrastructure/{persistence,web,...}`.
`FsmApplication` fica em `dev.jgluciano.fsm.application` com `@SpringBootApplication(scanBasePackages = "dev.jgluciano.fsm")`.

## Regras de arquitetura (validadas por `ArchitectureTest`)
1. `core` é Java puro: sem Spring, JPA ou `infrastructure`.
2. `core.model` não depende de `usecase` nem de `ports`.
3. `infrastructure.web` não acessa `infrastructure.persistence` (passa pelos casos de uso).
4. Módulos só se acessam pelo pacote `api` (eventos e contratos), nunca por `core`/`infrastructure` do outro.
5. Entidades JPA ficam só em `infrastructure.persistence`; o domínio é mapeado por MapStruct.
6. Schema só muda via Flyway (`ddl-auto: validate`). Toda tabela tem `owner_id` (preparado para multiusuário).

## Regras de precificação (ADR 0005)
- VHT = (custos fixos mensais + pró-labore) / horas produtivas mensais
- Índice de markup = 1 − (impostos% + taxas financeiras% + margem%) / 100
- Custo direto = horas × VHT + materiais + despesas extras
- Preço final = custo direto / índice de markup
- BigDecimal: cálculo em escala 10; saída em escala 2; HALF_UP

## Padrões já estabelecidos (seguir nos próximos módulos)
- **Casos de uso** são classes Java puras (sem `@Service`), com `Comando`/`Resultado` aninhados. Viram beans em `application/config/<Modulo>UseCasesConfig`.
- **Persistência:** `*Entity` (JPA), `*JpaRepository`, `*PersistenceMapper` (MapStruct `componentModel = "spring"`) e `*PersistenceAdapter` (package-private, `@Component`) implementando a porta do `core`.
- **Web:** controller package-private em `/api/v1/<recurso>`, DTOs `*Request`/`*Response` como records. Bean Validation cuida só do formato (400); regras de negócio ficam no domínio (422).
- **Dono dos dados:** cada módulo tem a sua classe `DonoAtual` (package-private), lendo `fsm.owner-id` (padrão `00000000-0000-0000-0000-000000000001`) até existir autenticação. O bean precisa de nome explícito por módulo (ex.: `@Component("clientesDonoAtual")`) para não colidir com o do catálogo. Módulos não importam `DonoAtual` uns dos outros.
- **Erros (RFC 9457, problem+json)** em `application/exceptionhandler/ApiExceptionHandler` (estende `ResponseEntityExceptionHandler`): Bean Validation 400; invariante de domínio 422 (`CatalogoInvalido`, `PrecificacaoInvalida`, `ClienteInvalido`); nome repetido 409 (`MaterialJaCadastrado`, `ServicoJaCadastrado`, `DataIntegrityViolation`); configuração ausente 404. Cada novo módulo acrescenta suas exceções nesse arquivo.
- **Domínio:** records imutáveis; construtor canônico valida as invariantes e serve também à reconstituição vinda do banco. Argumento nulo gera `NullPointerException` (`requireNonNull`); valor inválido gera exceção de domínio com mensagem em português.
- **Dinheiro:** `BigDecimal` escala 2, `HALF_UP`. No orçamento, a classe package-private `Dinheiro` concentra a normalização e a regra de não negativo.
- **Testes de integração:** `*IT` estendem `PostgresIntegrationSupport` (container Postgres singleton, `@SpringBootTest(classes = FsmApplication.class)` + `@DynamicPropertySource`), usam `@AutoConfigureMockMvc` e limpam tabelas com `DELETE FROM <tabela>` no `@BeforeEach`.
- **Convenções de teste:** `*Test` = unitário/arquitetura (surefire, `mvn test`); `*IT` = integração (failsafe, `mvn verify`). AssertJ 3.25: usar `.message().containsIgnoringCase(...)` (não existe `hasMessageContainingIgnoringCase`).

## Orçamento — regras do núcleo (ADR 0011, já implementado no `core.model`)
- `Orcamento` (record): id, ownerId, clienteId, status, itens, deslocamento, desconto. Guarda só `clienteId`, sem importar o módulo `clientes`.
- `ItemOrcamento` (snapshot): descrição (aparada, ≤ 200), `TipoItem` (SERVICO | MATERIAL), quantidade (escala 2, > 0 após normalizar), valor unitário (escala 2, ≥ 0).
- Subtotal do item = quantidade × valor unitário, arredondado uma vez. Total = subtotal + deslocamento − desconto. Desconto ≤ subtotal + deslocamento (invariante no construtor).
- Estados: RASCUNHO → ENVIADO (exige ≥ 1 item) → APROVADO → FINALIZADO; ENVIADO → REJEITADO. REJEITADO e FINALIZADO são finais. Itens, deslocamento e desconto só mudam em RASCUNHO.
- API do record: `Orcamento.novo(ownerId, clienteId)`, `adicionarItem`, `removerItem(int)`, `comDeslocamento`, `comDesconto`, `enviar`, `aprovar`, `rejeitar`, `finalizar`, `subtotal()`, `total()`.
- Exceções: `OrcamentoInvalidoException` (dados) e `StatusOrcamentoInvalidoException` (operação inválida no status). Ainda **não mapeadas** no `ApiExceptionHandler`: ambas devem virar 422.

## Convenções gerais
- TDD: commit do teste falhando (Red) → implementação mínima (Green) → refatoração (Refactor).
- Commits no padrão Conventional Commits (`feat:`, `test:`, `refactor:`, `chore:`, `docs:`). Sem push entre Red e Green.
- Código e nomes de domínio em português (Orcamento, ItemOrcamento); termos técnicos em inglês.
- Testcontainers fixado em 1.21.4 (`<testcontainers.version>` no pom) por compatibilidade com Docker 29+.
- Migrations: `V1__baseline`, `V2__catalogo`, `V3__cliente`. A próxima é `V4`.

## Fluxo de trabalho com o assistente
- Responder em português do Brasil.
- Cada etapa chega como um zip, extraído **da raiz do projeto** com `unzip -o`. Depois conferir que os testes caíram em `src/test` (`find src/main -name '*Test.java' -o -name '*IT.java'` deve vir vazio); se algum cair em `src/main`, mover para o `src/test` equivalente.
- O assistente não acessa a pasta do projeto: pede os arquivos de que precisa por chat. O usuário roda `mvn test` / `mvn clean verify` no WSL (`/mnt/c/Users/jgluc/IdeaProjects/fsm`) e cola o resultado. Se aparecer "JAVA_HOME is not defined", exportar `JAVA_HOME` do JDK 21 (ver `ls /usr/lib/jvm/`) e gravar no `~/.bashrc`.
- A cada etapa o assistente lista as premissas; o usuário confirma com "pode seguir" ou ajusta.

## Estado atual
- Fases 0 e 1 concluídas. Fase 2: clientes completo (domínio, persistência V3, API `/api/v1/clientes`, ADR 0009 e 0010) e núcleo do orçamento (`core.model`, ADR 0011) prontos, com testes verdes.
- Próximo passo: **2d** (casos de uso e portas do orçamento), depois **2e** (persistência V4 e API).

## Decisões em aberto
- **2d/2e:** como validar que o `clienteId` existe sem violar a regra 4 (cogitado: porta no `core.ports` do orçamento, implementada por um adaptador que usa uma `api` pública do módulo clientes, que ainda não existe).
- **2e:** modelagem das tabelas (`orcamento` + `item_orcamento` com ordem dos itens), endpoints de edição (itens, deslocamento, desconto) e de transição (enviar, aprovar, rejeitar, finalizar), listagem (com filtro por status?).
- Autenticação (Spring Security): começar com usuário único; Fase 2 ou 3.
- Validade do orçamento (dias): entra com o PDF, na Fase 3.
- Frontend (PWA): tecnologia a definir após o backend.
- Biblioteca de PDF (OpenPDF, PDFBox ou iText): Fase 3.

## ADRs
0005 precificação · 0006 a 0008 catálogo (persistência, API, erros) · 0009 domínio de clientes · 0010 persistência e API de clientes · 0011 núcleo do orçamento. Manter todos com status `aceito` ao fechar cada etapa.
