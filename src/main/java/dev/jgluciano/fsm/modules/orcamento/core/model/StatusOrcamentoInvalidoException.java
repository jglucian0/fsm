package dev.jgluciano.fsm.modules.orcamento.core.model;

/** Operação não permitida para o status atual do orçamento (transição inválida ou edição fora de RASCUNHO). */
public class StatusOrcamentoInvalidoException extends RuntimeException {

    public StatusOrcamentoInvalidoException(StatusOrcamento status, String operacao) {
        super("Operação '" + operacao + "' não é permitida para orçamento em " + status);
    }
}
