/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.catalogo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import model.mysql.dao.principal.ProdutoDAO;
import org.hibernate.annotations.CreationTimestamp;

/**
 *
 * @author ivand
 */
@Entity
@IdClass(ProdutoComponenteId.class)
public class ProdutoComponente implements Serializable {

    @CreationTimestamp
    private Timestamp criacao;

    @Id
    private Integer produtoId;

    @Id
    private Integer componenteId;

    private BigDecimal quantidade;

    public Timestamp getCriacao() {
        return criacao;
    }

    public void setCriacao(Timestamp criacao) {
        this.criacao = criacao;
    }

    public Integer getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Integer produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getComponenteId() {
        return componenteId;
    }

    public void setComponenteId(Integer componenteId) {
        this.componenteId = componenteId;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    //--------------------------------------------------------------------------
    public Produto getProduto() {
        return new ProdutoDAO().findById(getProdutoId());
    }
    
    public void setProduto(Produto produto) {
        if(produto != null) {
            this.produtoId = produto.getId();
        } else {
            this.produtoId = null;
        }
    }
    
    public Produto getComponente() {
        return new ProdutoDAO().findById(getComponenteId());
    }
    
    public void setComponente(Produto produto) {
        if(produto != null) {
            this.componenteId = produto.getId();
        } else {
            this.componenteId = null;
        }
    }

    public BigDecimal getTotalCompra() {
        return getQuantidade().multiply(getComponente().getValorCompra());
    }

    public BigDecimal getTotalVenda() {
        return getQuantidade().multiply(getComponente().getValorVenda());
    }

    @Override
    public boolean equals(Object obj) {
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        return Objects.equals(this.getProdutoId(), ((ProdutoComponente) obj).getProdutoId())
                && Objects.equals(this.getComponenteId(), ((ProdutoComponente) obj).getComponenteId())
                && Objects.equals(this.getCriacao(), ((ProdutoComponente) obj).getCriacao());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.criacao);
        hash = 29 * hash + Objects.hashCode(this.produtoId);
        hash = 29 * hash + Objects.hashCode(this.componenteId);
        return hash;
    }

    

}
