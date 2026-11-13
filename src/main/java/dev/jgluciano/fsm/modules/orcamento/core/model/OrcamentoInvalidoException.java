package dev.jgluciano.fsm.modules.orcamento.core.model;

/** Violação de uma invariante de dados do orçamento ou de um item (valor negativo, desconto acima do limite...). */
public class OrcamentoInvalidoException extends RuntimeException {

    public OrcamentoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
