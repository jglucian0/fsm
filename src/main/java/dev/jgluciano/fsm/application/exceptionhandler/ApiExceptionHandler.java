package dev.jgluciano.fsm.application.exceptionhandler;

import dev.jgluciano.fsm.modules.catalogo.core.model.CatalogoInvalidoException;
import dev.jgluciano.fsm.modules.catalogo.core.model.ConfiguracaoPrecificacaoNaoEncontradaException;
import dev.jgluciano.fsm.modules.catalogo.core.model.MaterialJaCadastradoException;
import dev.jgluciano.fsm.modules.catalogo.core.model.PrecificacaoInvalidaException;
import dev.jgluciano.fsm.modules.catalogo.core.model.ServicoJaCadastradoException;
import dev.jgluciano.fsm.modules.clientes.core.model.ClienteInvalidoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Erros no formato RFC 9457 (problem+json). Bean Validation (400) vem da classe base;
 * aqui traduzimos o domínio: invariante violada 422, nome repetido 409, inexistente 404.
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({CatalogoInvalidoException.class, PrecificacaoInvalidaException.class,
            ClienteInvalidoException.class})
    ProblemDetail dadosInvalidos(RuntimeException ex) {
        return problema(HttpStatus.UNPROCESSABLE_ENTITY, "Dados inválidos", ex.getMessage());
    }

    @ExceptionHandler({MaterialJaCadastradoException.class, ServicoJaCadastradoException.class})
    ProblemDetail nomeRepetido(RuntimeException ex) {
        return problema(HttpStatus.CONFLICT, "Registro já cadastrado", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail conflitoNoBanco(DataIntegrityViolationException ex) {
        // corrida entre duas requisições: o índice único do banco foi mais rápido que o caso de uso
        return problema(HttpStatus.CONFLICT, "Conflito de dados", "O registro conflita com um já existente");
    }

    @ExceptionHandler(ConfiguracaoPrecificacaoNaoEncontradaException.class)
    ProblemDetail configuracaoInexistente(ConfiguracaoPrecificacaoNaoEncontradaException ex) {
        return problema(HttpStatus.NOT_FOUND, "Configuração de precificação não encontrada", ex.getMessage());
    }

    private static ProblemDetail problema(HttpStatus status, String titulo, String detalhe) {
        var problema = ProblemDetail.forStatusAndDetail(status, detalhe);
        problema.setTitle(titulo);
        return problema;
    }
}
