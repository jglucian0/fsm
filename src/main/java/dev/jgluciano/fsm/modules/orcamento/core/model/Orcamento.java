package dev.jgluciano.fsm.modules.orcamento.core.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Orçamento do eletricista (ADR 0011). Imutável: cada operação devolve um novo objeto.
 * Total = subtotal + deslocamento - desconto. Itens, deslocamento e desconto só mudam em RASCUNHO.
 * O construtor canônico também reconstitui o orçamento vindo do banco, validando as mesmas invariantes.
 */
public record Orcamento(UUID id, UUID ownerId, UUID clienteId, StatusOrcamento status, List<ItemOrcamento> itens,
                        BigDecimal deslocamento, BigDecimal desconto) {

    public Orcamento {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(clienteId, "clienteId");
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(itens, "itens");

        itens = List.copyOf(itens);
        deslocamento = Dinheiro.naoNegativo(deslocamento, "Deslocamento");
        desconto = Dinheiro.naoNegativo(desconto, "Desconto");

        if (status != StatusOrcamento.RASCUNHO && itens.isEmpty()) {
            throw new OrcamentoInvalidoException("Orçamento deve ter pelo menos um item para sair de RASCUNHO");
        }
        if (desconto.compareTo(somar(itens).add(deslocamento)) > 0) {
            throw new OrcamentoInvalidoException("Desconto não pode ser maior que subtotal + deslocamento");
        }
    }

    public static Orcamento novo(UUID ownerId, UUID clienteId) {
        return new Orcamento(UUID.randomUUID(), ownerId, clienteId, StatusOrcamento.RASCUNHO, List.of(),
                BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public BigDecimal subtotal() {
        return somar(itens);
    }

    public BigDecimal total() {
        return subtotal().add(deslocamento).subtract(desconto);
    }

    // ---------------------------------------------------------------- edição (só em RASCUNHO)

    public Orcamento adicionarItem(ItemOrcamento item) {
        Objects.requireNonNull(item, "item");
        exigirRascunho("adicionar item");
        var novos = new ArrayList<>(itens);
        novos.add(item);
        return new Orcamento(id, ownerId, clienteId, status, novos, deslocamento, desconto);
    }

    public Orcamento removerItem(int indice) {
        exigirRascunho("remover item");
        if (indice < 0 || indice >= itens.size()) {
            throw new OrcamentoInvalidoException("Item inexistente na posição " + indice);
        }
        var novos = new ArrayList<>(itens);
        novos.remove(indice);
        return new Orcamento(id, ownerId, clienteId, status, novos, deslocamento, desconto);
    }

    public Orcamento comDeslocamento(BigDecimal novoDeslocamento) {
        Objects.requireNonNull(novoDeslocamento, "deslocamento");
        exigirRascunho("alterar deslocamento");
        return new Orcamento(id, ownerId, clienteId, status, itens, novoDeslocamento, desconto);
    }

    public Orcamento comDesconto(BigDecimal novoDesconto) {
        Objects.requireNonNull(novoDesconto, "desconto");
        exigirRascunho("alterar desconto");
        return new Orcamento(id, ownerId, clienteId, status, itens, deslocamento, novoDesconto);
    }

    // ---------------------------------------------------------------- máquina de estados

    public Orcamento enviar() {
        return transicionar("enviar", StatusOrcamento.RASCUNHO, StatusOrcamento.ENVIADO);
    }

    public Orcamento aprovar() {
        return transicionar("aprovar", StatusOrcamento.ENVIADO, StatusOrcamento.APROVADO);
    }

    public Orcamento rejeitar() {
        return transicionar("rejeitar", StatusOrcamento.ENVIADO, StatusOrcamento.REJEITADO);
    }

    public Orcamento finalizar() {
        return transicionar("finalizar", StatusOrcamento.APROVADO, StatusOrcamento.FINALIZADO);
    }

    private Orcamento transicionar(String operacao, StatusOrcamento origem, StatusOrcamento destino) {
        if (status != origem) {
            throw new StatusOrcamentoInvalidoException(status, operacao);
        }
        return new Orcamento(id, ownerId, clienteId, destino, itens, deslocamento, desconto);
    }

    private void exigirRascunho(String operacao) {
        if (status != StatusOrcamento.RASCUNHO) {
            throw new StatusOrcamentoInvalidoException(status, operacao);
        }
    }

    private static BigDecimal somar(List<ItemOrcamento> itens) {
        return itens.stream().map(ItemOrcamento::subtotal).reduce(Dinheiro.normalizar(BigDecimal.ZERO), BigDecimal::add);
    }
}
