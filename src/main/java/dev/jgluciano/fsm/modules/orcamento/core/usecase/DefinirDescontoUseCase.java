package dev.jgluciano.fsm.modules.orcamento.core.usecase;

import dev.jgluciano.fsm.modules.orcamento.core.model.Orcamento;
import dev.jgluciano.fsm.modules.orcamento.core.model.OrcamentoNaoEncontradoException;
import dev.jgluciano.fsm.modules.orcamento.core.ports.OrcamentoRepository;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class DefinirDescontoUseCase {

    private final OrcamentoRepository orcamentoRepository;

    public DefinirDescontoUseCase(OrcamentoRepository orcamentoRepository) {
        this.orcamentoRepository = Objects.requireNonNull(orcamentoRepository, "orcamentoRepository");
    }

    public Orcamento executar(Comando comando) {
        Objects.requireNonNull(comando, "comando");

        var orcamento = orcamentoRepository.buscarPorId(comando.ownerId(), comando.orcamentoId())
                .orElseThrow(() -> new OrcamentoNaoEncontradoException(comando.orcamentoId()));
        return orcamentoRepository.salvar(orcamento.comDesconto(comando.desconto()));
    }

    public record Comando(UUID ownerId, UUID orcamentoId, BigDecimal desconto) {

        public Comando {
            Objects.requireNonNull(ownerId, "ownerId");
            Objects.requireNonNull(orcamentoId, "orcamentoId");
            Objects.requireNonNull(desconto, "desconto");
        }
    }
}
