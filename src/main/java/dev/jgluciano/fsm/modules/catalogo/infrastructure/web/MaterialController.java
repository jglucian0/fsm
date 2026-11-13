package dev.jgluciano.fsm.modules.catalogo.infrastructure.web;

import dev.jgluciano.fsm.modules.catalogo.core.usecase.CadastrarMaterialUseCase;
import dev.jgluciano.fsm.modules.catalogo.core.usecase.ListarMateriaisUseCase;
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
@RequestMapping("/api/v1/materiais")
class MaterialController {

    private final CadastrarMaterialUseCase cadastrarMaterial;
    private final ListarMateriaisUseCase listarMateriais;
    private final DonoAtual dono;

    MaterialController(CadastrarMaterialUseCase cadastrarMaterial, ListarMateriaisUseCase listarMateriais,
                       DonoAtual dono) {
        this.cadastrarMaterial = cadastrarMaterial;
        this.listarMateriais = listarMateriais;
        this.dono = dono;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    MaterialResponse cadastrar(@Valid @RequestBody MaterialRequest request) {
        var material = cadastrarMaterial.executar(new CadastrarMaterialUseCase.Comando(
                dono.id(), request.nome(), request.unidade(), request.custoUnitario()));
        return MaterialResponse.de(material);
    }

    @GetMapping
    List<MaterialResponse> listar() {
        return listarMateriais.executar(dono.id()).stream().map(MaterialResponse::de).toList();
    }
}
