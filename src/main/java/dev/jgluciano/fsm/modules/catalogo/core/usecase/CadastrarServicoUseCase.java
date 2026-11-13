package dev.jgluciano.fsm.modules.catalogo.core.usecase;

import dev.jgluciano.fsm.modules.catalogo.core.model.Servico;
import dev.jgluciano.fsm.modules.catalogo.core.model.ServicoJaCadastradoException;
import dev.jgluciano.fsm.modules.catalogo.core.ports.ServicoRepository;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class CadastrarServicoUseCase {

    private final ServicoRepository servicoRepository;

    public CadastrarServicoUseCase(ServicoRepository servicoRepository) {
        this.servicoRepository = Objects.requireNonNull(servicoRepository, "servicoRepository");
    }

    public Servico executar(Comando comando) {
        Objects.requireNonNull(comando, "comando");

        var servico = Servico.novo(comando.ownerId(), comando.nome(), comando.horasPadrao());

        if (servicoRepository.existePorOwnerENome(servico.ownerId(), servico.nome())) {
            throw new ServicoJaCadastradoException(servico.nome());
        }
        return servicoRepository.salvar(servico);
    }

    public record Comando(UUID ownerId, String nome, BigDecimal horasPadrao) {

        public Comando {
            Objects.requireNonNull(ownerId, "ownerId");
            Objects.requireNonNull(nome, "nome");
            Objects.requireNonNull(horasPadrao, "horasPadrao");
        }
    }
}
