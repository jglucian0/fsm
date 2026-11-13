package dev.jgluciano.fsm.modules.orcamento.core.usecase;

import dev.jgluciano.fsm.modules.orcamento.core.model.ItemOrcamento;
import dev.jgluciano.fsm.modules.orcamento.core.model.Orcamento;
import dev.jgluciano.fsm.modules.orcamento.core.model.OrcamentoNaoEncontradoException;
import dev.jgluciano.fsm.modules.orcamento.core.model.TipoItem;
import dev.jgluciano.fsm.modules.orcamento.core.ports.OrcamentoRepository;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class AdicionarItemOrcamentoUseCase {

    private final OrcamentoRepository orcamentoRepository;

    public AdicionarItemOrcamentoUseCase(OrcamentoRepository orcamentoRepository) {
        this.orcamentoRepository = Objects.requireNonNull(orcamentoRepository, "orcamentoRepository");
    }

    public Orcamento executar(Comando comando) {
        Objects.requireNonNull(comando, "comando");

        var orcamento = orcamentoRepository.buscarPorId(comando.ownerId(), comando.orcamentoId())
                .orElseThrow(() -> new OrcamentoNaoEncontradoException(comando.orcamentoId()));
        var item = new ItemOrcamento(comando.descricao(), comando.tipo(), comando.quantidade(),
                comando.valorUnitario());
        return orcamentoRepository.salvar(orcamento.adicionarItem(item));
    }

    public record Comando(UUID ownerId, UUID orcamentoId, String descricao, TipoItem tipo, BigDecimal quantidade,
                           BigDecimal valorUnitario) {

        public Comando {
            Objects.requireNonNull(ownerId, "ownerId");
            Objects.requireNonNull(orcamentoId, "orcamentoId");
            Objects.requireNonNull(descricao, "descricao");
            Objects.requireNonNull(tipo, "tipo");
            Objects.requireNonNull(quantidade, "quantidade");
            Objects.requireNonNull(valorUnitario, "valorUnitario");
        }
    }
}
