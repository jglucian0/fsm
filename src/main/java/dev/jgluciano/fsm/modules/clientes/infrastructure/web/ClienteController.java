package dev.jgluciano.fsm.modules.clientes.infrastructure.web;

import dev.jgluciano.fsm.modules.clientes.core.usecase.CadastrarClienteUseCase;
import dev.jgluciano.fsm.modules.clientes.core.usecase.ListarClientesUseCase;
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
@RequestMapping("/api/v1/clientes")
class ClienteController {

    private final CadastrarClienteUseCase cadastrarCliente;
    private final ListarClientesUseCase listarClientes;
    private final DonoAtual dono;

    ClienteController(CadastrarClienteUseCase cadastrarCliente, ListarClientesUseCase listarClientes,
                      DonoAtual dono) {
        this.cadastrarCliente = cadastrarCliente;
        this.listarClientes = listarClientes;
        this.dono = dono;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ClienteResponse cadastrar(@Valid @RequestBody ClienteRequest request) {
        var cliente = cadastrarCliente.executar(new CadastrarClienteUseCase.Comando(
                dono.id(), request.nome(), request.telefone(), request.email(), request.endereco()));
        return ClienteResponse.de(cliente);
    }

    @GetMapping
    List<ClienteResponse> listar() {
        return listarClientes.executar(dono.id()).stream().map(ClienteResponse::de).toList();
    }
}
