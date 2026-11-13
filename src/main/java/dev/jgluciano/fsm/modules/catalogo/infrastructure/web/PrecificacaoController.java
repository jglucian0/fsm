package dev.jgluciano.fsm.modules.catalogo.infrastructure.web;

import dev.jgluciano.fsm.modules.catalogo.core.usecase.CalcularPrecoServicoUseCase;
import dev.jgluciano.fsm.modules.catalogo.core.usecase.DefinirConfiguracaoPrecificacaoUseCase;
import dev.jgluciano.fsm.modules.catalogo.core.usecase.ObterConfiguracaoPrecificacaoUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/precificacao")
class PrecificacaoController {

    private final DefinirConfiguracaoPrecificacaoUseCase definirConfiguracao;
    private final ObterConfiguracaoPrecificacaoUseCase obterConfiguracao;
    private final CalcularPrecoServicoUseCase calcularPreco;
    private final DonoAtual dono;

    PrecificacaoController(DefinirConfiguracaoPrecificacaoUseCase definirConfiguracao,
                           ObterConfiguracaoPrecificacaoUseCase obterConfiguracao,
                           CalcularPrecoServicoUseCase calcularPreco, DonoAtual dono) {
        this.definirConfiguracao = definirConfiguracao;
        this.obterConfiguracao = obterConfiguracao;
        this.calcularPreco = calcularPreco;
        this.dono = dono;
    }

    @PutMapping("/configuracao")
    ConfiguracaoPrecificacaoResponse definirConfiguracao(@Valid @RequestBody ConfiguracaoPrecificacaoRequest request) {
        var configuracao = definirConfiguracao.executar(new DefinirConfiguracaoPrecificacaoUseCase.Comando(
                dono.id(), request.custosFixosMensais(), request.proLabore(), request.horasProdutivasMensais(),
                request.impostosPercentual(), request.taxasFinanceirasPercentual(),
                request.margemLucroPercentual()));
        return ConfiguracaoPrecificacaoResponse.de(configuracao);
    }

    @GetMapping("/configuracao")
    ConfiguracaoPrecificacaoResponse obterConfiguracao() {
        return ConfiguracaoPrecificacaoResponse.de(obterConfiguracao.executar(dono.id()));
    }

    @PostMapping("/calculo")
    CalculoPrecoResponse calcular(@Valid @RequestBody CalculoPrecoRequest request) {
        var resultado = calcularPreco.executar(new CalcularPrecoServicoUseCase.Comando(
                dono.id(), request.horasEstimadas(), request.custoMateriais(), request.despesasExtras()));
        return CalculoPrecoResponse.de(resultado);
    }
}
