/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.pessoa;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author ivand
 */
@Entity
public class PerfilItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;

    @ManyToOne
    @JoinColumn(name = "perfilId")
    private Perfil perfil;

    @ManyToOne
    @JoinColumn(name = "grupoItemId")
    private GrupoItem grupoItem;

    @Column(columnDefinition = "decimal(20,3) default 1", nullable = false)
    private BigDecimal quantidade;

    private BigDecimal acrescimoMonetario;
    private BigDecimal descontoMonetario;

    private BigDecimal acrescimoPercentual;
    private BigDecimal descontoPercentual;

    protected PerfilItem() {
    }

    public PerfilItem(Perfil perfil, GrupoItem grupoItem) {
        this.perfil = perfil;
        this.grupoItem = grupoItem;
        this.quantidade = BigDecimal.ONE;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getCriacao() {
        return criacao;
    }

    public void setCriacao(LocalDateTime criacao) {
        this.criacao = criacao;
    }

    public LocalDateTime getAtualizacao() {
        return atualizacao;
    }

    public void setAtualizacao(LocalDateTime atualizacao) {
        this.atualizacao = atualizacao;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public GrupoItem getGrupoItem() {
        return grupoItem;
    }

    public void setGrupoItem(GrupoItem grupoItem) {
        this.grupoItem = grupoItem;
    }

    public BigDecimal getQuantidade() {
        return quantidade != null ? quantidade : BigDecimal.ZERO;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getAcrescimoMonetario() {
        return acrescimoMonetario != null ? acrescimoMonetario : BigDecimal.ZERO;
    }

    public void setAcrescimoMonetario(BigDecimal acrescimoMonetario) {
        this.acrescimoMonetario = acrescimoMonetario;
    }

    public BigDecimal getDescontoMonetario() {
        return descontoMonetario != null ? descontoMonetario : BigDecimal.ZERO;
    }

    public void setDescontoMonetario(BigDecimal descontoMonetario) {
        this.descontoMonetario = descontoMonetario;
    }

    public BigDecimal getAcrescimoPercentual() {
        return acrescimoPercentual != null ? acrescimoPercentual : BigDecimal.ZERO;
    }

    public void setAcrescimoPercentual(BigDecimal acrescimoPercentual) {
        this.acrescimoPercentual = acrescimoPercentual;
    }

    public BigDecimal getDescontoPercentual() {
        return descontoPercentual != null ? descontoPercentual : BigDecimal.ZERO;
    }

    public void setDescontoPercentual(BigDecimal descontoPercentual) {
        this.descontoPercentual = descontoPercentual;
    }

    //--------------------------------------------------------------------------
    /**
     * Valor do item de catálogo
     * @return getGrupoItem().getProduto().getValorVenda();
     */
    public BigDecimal getValor() {
        return getGrupoItem().getProduto().getValorVenda();
    }

    public BigDecimal getDescontoPercentualEmMonetario() {
        BigDecimal desconto = getValor().multiply(getDescontoPercentual().divide(new BigDecimal(100), 10, RoundingMode.HALF_UP));
        return desconto;
    }

    public BigDecimal getAcrescimoPercentualEmMonetario() {
        BigDecimal acrescimo = getValor().multiply(getAcrescimoPercentual().divide(new BigDecimal(100), 10, RoundingMode.HALF_UP));
        return acrescimo;
    }

    public BigDecimal getSubtotal() {
        //valorPerfil = quantidade.multiply(valorItem.add(acrescimoMonetario).add(acrescimoPercentualEmMonetario).subtract(descontoMonetario).subtract(descontoPercentualEmMonetario));
        return getQuantidade().multiply(
                getValor()
                .add(getAcrescimoPercentualEmMonetario())
                        .subtract(getDescontoPercentualEmMonetario())
                        .add(getAcrescimoMonetario())
                        .subtract(getDescontoMonetario())
        );
    }
}
