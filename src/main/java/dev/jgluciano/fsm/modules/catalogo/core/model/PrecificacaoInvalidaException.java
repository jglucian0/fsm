package dev.jgluciano.fsm.modules.catalogo.core.model;

/** Violação de uma invariante de precificação (valor negativo, horas zero, percentuais somando 100% ou mais). */
public class PrecificacaoInvalidaException extends RuntimeException {

    public PrecificacaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
