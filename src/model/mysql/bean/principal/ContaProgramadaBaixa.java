/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author ivand
 */
@Entity
public class ContaProgramadaBaixa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;

    @ManyToOne
    @JoinColumn(name = "contaProgramadaId")
    private ContaProgramada contaProgramada;
    
    @OneToOne(mappedBy = "contaProgramadaBaixa", cascade = CascadeType.ALL)
    private CaixaItem caixaItem;

    private LocalDate vencimento;
    
    private BigDecimal valor; //armazena o valor temporal - permitindo alterar na conta programada sem interferir nos já baixados

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

    public ContaProgramada getContaProgramada() {
        return contaProgramada;
    }

    public void setContaProgramada(ContaProgramada contaProgramada) {
        this.contaProgramada = contaProgramada;
    }

    public CaixaItem getCaixaItem() {
        return caixaItem;
    }

    public void setCaixaItem(CaixaItem caixaItem) {
        this.caixaItem = caixaItem;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public void setVencimento(LocalDate vencimento) {
        this.vencimento = vencimento;
    }

    /**
     * 
     * @return valor no ato da baixa
     */
    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
    
    

    //--------------------------------------------------------------------------
    public void addCaixaItem(CaixaItem caixaItem) {
        this.caixaItem = caixaItem;
        caixaItem.setContaProgramadaBaixa(this);
    }
    
    public void removeCaixaItem(CaixaItem caixaItem) {
        if(caixaItem != null) {
            caixaItem.setContaProgramadaBaixa(null);
        }
        this.caixaItem = null;
    }

}
