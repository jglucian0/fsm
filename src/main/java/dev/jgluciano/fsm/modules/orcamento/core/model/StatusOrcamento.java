package dev.jgluciano.fsm.modules.orcamento.core.model;

/** RASCUNHO, ENVIADO, APROVADO, FINALIZADO; ou ENVIADO, REJEITADO. REJEITADO e FINALIZADO são finais (ADR 0011). */
public enum StatusOrcamento {
    RASCUNHO,
    ENVIADO,
    APROVADO,
    REJEITADO,
    FINALIZADO
}
