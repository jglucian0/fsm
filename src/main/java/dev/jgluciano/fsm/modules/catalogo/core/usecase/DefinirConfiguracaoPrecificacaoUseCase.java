package dev.jgluciano.fsm.modules.catalogo.core.usecase;

import dev.jgluciano.fsm.modules.catalogo.core.model.ConfiguracaoPrecificacao;
import dev.jgluciano.fsm.modules.catalogo.core.ports.ConfiguracaoPrecificacaoRepository;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/** Cria ou substitui a configuração de precificação do dono (ADR 0005). */
public class DefinirConfiguracaoPrecificacaoUseCase {

    private final ConfiguracaoPrecificacaoRepository repository;

    public DefinirConfiguracaoPrecificacaoUseCase(ConfiguracaoPrecificacaoRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public ConfiguracaoPrecificacao executar(Comando comando) {
        Objects.requireNonNull(comando, "comando");

        var configuracao = new ConfiguracaoPrecificacao(
                comando.ownerId(),
                comando.custosFixosMensais(),
                comando.proLabore(),
                comando.horasProdutivasMensais(),
                comando.impostosPercentual(),
                comando.taxasFinanceirasPercentual(),
                comando.margemLucroPercentual());

        return repository.salvar(configuracao);
    }

    public record Comando(UUID ownerId, BigDecimal custosFixosMensais, BigDecimal proLabore,
                          BigDecimal horasProdutivasMensais, BigDecimal impostosPercentual,
                          BigDecimal taxasFinanceirasPercentual, BigDecimal margemLucroPercentual) {

        public Comando {
            Objects.requireNonNull(ownerId, "ownerId");
            Objects.requireNonNull(custosFixosMensais, "custosFixosMensais");
            Objects.requireNonNull(proLabore, "proLabore");
            Objects.requireNonNull(horasProdutivasMensais, "horasProdutivasMensais");
            Objects.requireNonNull(impostosPercentual, "impostosPercentual");
            Objects.requireNonNull(taxasFinanceirasPercentual, "taxasFinanceirasPercentual");
            Objects.requireNonNull(margemLucroPercentual, "margemLucroPercentual");
        }
    }
}
