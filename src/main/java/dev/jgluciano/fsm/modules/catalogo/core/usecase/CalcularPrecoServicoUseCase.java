package dev.jgluciano.fsm.modules.catalogo.core.usecase;

import dev.jgluciano.fsm.modules.catalogo.core.model.ConfiguracaoPrecificacao;
import dev.jgluciano.fsm.modules.catalogo.core.model.ConfiguracaoPrecificacaoNaoEncontradaException;
import dev.jgluciano.fsm.modules.catalogo.core.ports.ConfiguracaoPrecificacaoRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

import static dev.jgluciano.fsm.modules.catalogo.core.model.Validacoes.exigirNaoNegativo;

/**
 * Calcula o preço de um serviço (ADR 0005):
 * custo direto = horas x VHT + materiais + despesas extras; preço final = custo direto / índice de markup.
 * Nada é arredondado para 2 casas antes da saída.
 */
public class CalcularPrecoServicoUseCase {

    private static final int ESCALA_SAIDA = 2;
    private static final RoundingMode ARREDONDAMENTO = ConfiguracaoPrecificacao.ARREDONDAMENTO;

    private final ConfiguracaoPrecificacaoRepository configuracaoRepository;

    public CalcularPrecoServicoUseCase(ConfiguracaoPrecificacaoRepository configuracaoRepository) {
        this.configuracaoRepository = Objects.requireNonNull(configuracaoRepository, "configuracaoRepository");
    }

    public Resultado executar(Comando comando) {
        Objects.requireNonNull(comando, "comando");

        var configuracao = configuracaoRepository.buscarPorOwner(comando.ownerId())
                .orElseThrow(() -> new ConfiguracaoPrecificacaoNaoEncontradaException(comando.ownerId()));

        var vht = configuracao.valorHoraTecnica();
        var indice = configuracao.indiceMarkup();

        var custoDireto = comando.horasEstimadas().multiply(vht)
                .add(comando.custoMateriais())
                .add(comando.despesasExtras())
                .setScale(ConfiguracaoPrecificacao.ESCALA_CALCULO, ARREDONDAMENTO);

        var precoFinal = custoDireto.divide(indice, ESCALA_SAIDA, ARREDONDAMENTO);

        return new Resultado(
                vht.setScale(ESCALA_SAIDA, ARREDONDAMENTO),
                custoDireto.setScale(ESCALA_SAIDA, ARREDONDAMENTO),
                indice,
                precoFinal);
    }

    public record Comando(UUID ownerId, BigDecimal horasEstimadas, BigDecimal custoMateriais,
                          BigDecimal despesasExtras) {

        public Comando {
            Objects.requireNonNull(ownerId, "ownerId");
            Objects.requireNonNull(horasEstimadas, "horasEstimadas");
            Objects.requireNonNull(custoMateriais, "custoMateriais");
            Objects.requireNonNull(despesasExtras, "despesasExtras");
            exigirNaoNegativo(horasEstimadas, "Horas estimadas");
            exigirNaoNegativo(custoMateriais, "Custo de materiais");
            exigirNaoNegativo(despesasExtras, "Despesas extras");
        }
    }

    /** Valores monetários em escala 2; o índice de markup segue em escala 10. */
    public record Resultado(BigDecimal valorHoraTecnica, BigDecimal custoDireto, BigDecimal indiceMarkup,
                            BigDecimal precoFinal) {
    }
}
