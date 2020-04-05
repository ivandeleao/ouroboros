/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.financeiro;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author ivand
 */
@Entity
public class CartaoTaxa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;

    private LocalDateTime exclusao;

    private Integer parcelas;
    
    @Column(columnDefinition = "decimal(19,2) default 0")
    private BigDecimal taxa;
    
    private boolean cartaoTaxaInclusa; //se a taxa entra no total da venda (cobrar do cliente)
    
    @ManyToOne
    @JoinColumn(name = "cartaoId")
    private Cartao cartao;

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

    public Integer getParcelas() {
        return parcelas;
    }

    public void setParcelas(Integer parcelas) {
        this.parcelas = parcelas;
    }

    public BigDecimal getTaxa() {
        return taxa;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }

    public boolean isCartaoTaxaInclusa() {
        return cartaoTaxaInclusa;
    }

    public void setCartaoTaxaInclusa(boolean cartaoTaxaInclusa) {
        this.cartaoTaxaInclusa = cartaoTaxaInclusa;
    }

    public Cartao getCartao() {
        return cartao;
    }

    public void setCartao(Cartao cartao) {
        this.cartao = cartao;
    }



    //Bags----------------------------------------------------------------------

    //Fim Bags------------------------------------------------------------------
    
    
    //Facilitadores-------------------------------------------------------------
    
    //Fim Facilitadores---------------------------------------------------------
    
    
    @Override
    public String toString() {
        return getParcelas() + " x ";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return Objects.equals(this.getId(), ((CartaoTaxa) obj).getId());
    }

}
