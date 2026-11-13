package dev.jgluciano.fsm.modules.catalogo.infrastructure.web;

import dev.jgluciano.fsm.modules.catalogo.core.usecase.CadastrarServicoUseCase;
import dev.jgluciano.fsm.modules.catalogo.core.usecase.ListarServicosUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/servicos")
class ServicoController {

    private final CadastrarServicoUseCase cadastrarServico;
    private final ListarServicosUseCase listarServicos;
    private final DonoAtual dono;

    ServicoController(CadastrarServicoUseCase cadastrarServico, ListarServicosUseCase listarServicos,
                      DonoAtual dono) {
        this.cadastrarServico = cadastrarServico;
        this.listarServicos = listarServicos;
        this.dono = dono;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ServicoResponse cadastrar(@Valid @RequestBody ServicoRequest request) {
        var servico = cadastrarServico.executar(
                new CadastrarServicoUseCase.Comando(dono.id(), request.nome(), request.horasPadrao()));
        return ServicoResponse.de(servico);
    }

    @GetMapping
    List<ServicoResponse> listar() {
        return listarServicos.executar(dono.id()).stream().map(ServicoResponse::de).toList();
    }
}
