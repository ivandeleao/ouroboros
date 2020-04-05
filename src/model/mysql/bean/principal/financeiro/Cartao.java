/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.financeiro;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javax.persistence.OneToMany;

/**
 *
 * @author ivand
 */
@Entity
public class Cartao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;

    private LocalDateTime exclusao;

    private String nome;
    
    private Integer diasRecebimento;
    
    @OneToMany(mappedBy = "cartao")
    private List<CartaoTaxa> cartaoTaxas = new ArrayList<>();
    
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getDiasRecebimento() {
        return diasRecebimento;
    }

    public void setDiasRecebimento(Integer diasRecebimento) {
        this.diasRecebimento = diasRecebimento;
    }

    public List<CartaoTaxa> getCartaoTaxas() {
        cartaoTaxas.sort(Comparator.comparing(CartaoTaxa::getParcelas));
        return cartaoTaxas;
    }

    public void setCartaoTaxas(List<CartaoTaxa> cartaoTaxas) {
        this.cartaoTaxas = cartaoTaxas;
    }

    

    //Bags----------------------------------------------------------------------
    public void addCartaoTaxa(CartaoTaxa cartaoTaxa) {
        cartaoTaxas.remove(cartaoTaxa);
        cartaoTaxas.add(cartaoTaxa);
        cartaoTaxa.setCartao(this);
    }
    
    public void removeCartaoTaxa(CartaoTaxa cartaoTaxa) {
        cartaoTaxa.setCartao(null);
        cartaoTaxas.remove(cartaoTaxa);
    }
    //Fim Bags------------------------------------------------------------------
    
    //Facilitadores-------------------------------------------------------------
    
    //Fim Facilitadores---------------------------------------------------------
    
    
    @Override
    public String toString() {
        return getNome();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return Objects.equals(this.getId(), ((Cartao) obj).getId());
    }

}
