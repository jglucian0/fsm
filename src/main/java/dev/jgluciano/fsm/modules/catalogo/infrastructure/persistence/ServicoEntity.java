package dev.jgluciano.fsm.modules.catalogo.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "servico")
public class ServicoEntity {

    @Id
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(name = "horas_padrao", nullable = false)
    private BigDecimal horasPadrao;

    public ServicoEntity() {
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getHorasPadrao() { return horasPadrao; }
    public void setHorasPadrao(BigDecimal horasPadrao) { this.horasPadrao = horasPadrao; }
}
