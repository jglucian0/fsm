package dev.jgluciano.fsm.modules.catalogo.core.model;

/** Violação de uma invariante do catálogo (nome ou unidade vazios, nome longo demais, custo negativo). */
public class CatalogoInvalidoException extends RuntimeException {

    public CatalogoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
