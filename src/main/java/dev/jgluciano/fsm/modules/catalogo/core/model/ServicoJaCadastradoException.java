package dev.jgluciano.fsm.modules.catalogo.core.model;

/** O dono já possui um serviço com este nome. */
public class ServicoJaCadastradoException extends RuntimeException {

    public ServicoJaCadastradoException(String nome) {
        super("Já existe um serviço cadastrado com o nome '" + nome + "'");
    }
}
