package dev.jgluciano.fsm.modules.catalogo.core.model;

/** O dono já possui um material com este nome. */
public class MaterialJaCadastradoException extends RuntimeException {

    public MaterialJaCadastradoException(String nome) {
        super("Já existe um material cadastrado com o nome '" + nome + "'");
    }
}
