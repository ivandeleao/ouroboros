/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.financeiro.CaixaItem;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author ivand
 */
@Entity
public class ComissaoPagamento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;

    private LocalDateTime exclusao;

    @ManyToOne
    @JoinColumn(name = "vendaId")
    private Venda venda;

    @ManyToOne
    @JoinColumn(name = "caixaItemId")
    private CaixaItem caixaItem;
    
    private BigDecimal valor;

    public ComissaoPagamento() {
    }
    
    public ComissaoPagamento(Venda venda, CaixaItem caixaItem, BigDecimal valor) {
        this.venda = venda;
        this.caixaItem = caixaItem;
        this.valor = valor;
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

    public LocalDateTime getExclusao() {
        return exclusao;
    }

    public void setExclusao(LocalDateTime exclusao) {
        this.exclusao = exclusao;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public CaixaItem getCaixaItem() {
        return caixaItem;
    }

    public void setCaixaItem(CaixaItem caixaItem) {
        this.caixaItem = caixaItem;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
    
    //Facilitadores-------------------------------------------------------------
    
    public boolean isEstornado() {
        if (getCaixaItem() != null) {
            return getCaixaItem().getEstorno() != null;
        }
        return false;
    }
    
    //Fim Facilitadores---------------------------------------------------------

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ComissaoPagamento other = (ComissaoPagamento) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
    
}
