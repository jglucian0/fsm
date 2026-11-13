package dev.jgluciano.fsm.modules.clientes.core.model;

/** Violação de uma invariante do cadastro de clientes (nome vazio, telefone ou e-mail inválidos...). */
public class ClienteInvalidoException extends RuntimeException {

    public ClienteInvalidoException(String mensagem) {
        super(mensagem);
    }
}
