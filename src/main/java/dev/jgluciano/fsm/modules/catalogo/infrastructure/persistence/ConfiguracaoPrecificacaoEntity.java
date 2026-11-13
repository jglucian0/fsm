package dev.jgluciano.fsm.modules.catalogo.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "configuracao_precificacao")
public class ConfiguracaoPrecificacaoEntity {

    @Id
    @Column(name = "owner_id")
    private UUID ownerId;

    @Column(name = "custos_fixos_mensais", nullable = false)
    private BigDecimal custosFixosMensais;

    @Column(name = "pro_labore", nullable = false)
    private BigDecimal proLabore;

    @Column(name = "horas_produtivas_mensais", nullable = false)
    private BigDecimal horasProdutivasMensais;

    @Column(name = "impostos_percentual", nullable = false)
    private BigDecimal impostosPercentual;

    @Column(name = "taxas_financeiras_percentual", nullable = false)
    private BigDecimal taxasFinanceirasPercentual;

    @Column(name = "margem_lucro_percentual", nullable = false)
    private BigDecimal margemLucroPercentual;

    public ConfiguracaoPrecificacaoEntity() {
    }

    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }
    public BigDecimal getCustosFixosMensais() { return custosFixosMensais; }
    public void setCustosFixosMensais(BigDecimal v) { this.custosFixosMensais = v; }
    public BigDecimal getProLabore() { return proLabore; }
    public void setProLabore(BigDecimal v) { this.proLabore = v; }
    public BigDecimal getHorasProdutivasMensais() { return horasProdutivasMensais; }
    public void setHorasProdutivasMensais(BigDecimal v) { this.horasProdutivasMensais = v; }
    public BigDecimal getImpostosPercentual() { return impostosPercentual; }
    public void setImpostosPercentual(BigDecimal v) { this.impostosPercentual = v; }
    public BigDecimal getTaxasFinanceirasPercentual() { return taxasFinanceirasPercentual; }
    public void setTaxasFinanceirasPercentual(BigDecimal v) { this.taxasFinanceirasPercentual = v; }
    public BigDecimal getMargemLucroPercentual() { return margemLucroPercentual; }
    public void setMargemLucroPercentual(BigDecimal v) { this.margemLucroPercentual = v; }
}
