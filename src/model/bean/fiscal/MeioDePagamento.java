/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean.fiscal;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 *
 * @author ivand
 */
@Entity
@Table(indexes = {@Index(columnList = "nome"), @Index(columnList = "codigoSAT")})
public class MeioDePagamento implements Serializable{
    @Id
    private Integer id;
    private String nome;
    @Column(unique = true)
    private String codigoSAT;
    private Integer ordem; //ordem de exibição
    @Column(columnDefinition = "boolean default true")
    private Boolean habilitado; 
    
    //Especificacao_SAT_v_ER_2_24_04.pdf - página 99
    public static final MeioDePagamento DINHEIRO = new MeioDePagamento(1, "Dinheiro", "01", 1, true);
    public static final MeioDePagamento CHEQUE = new MeioDePagamento(2, "Cheque", "02", 2, true);
    public static final MeioDePagamento CARTAO_DE_CREDITO = new MeioDePagamento(3, "Cartão de Crédito", "03", 3, true);
    public static final MeioDePagamento CARTAO_DE_DEBITO = new MeioDePagamento(4, "Cartão de Débito", "04", 4, true);
    public static final MeioDePagamento CREDITO_LOJA = new MeioDePagamento(5, "Crédito Loja", "05", 5, false);
    public static final MeioDePagamento VALE_ALIMENTACAO = new MeioDePagamento(6, "Vale Alimentação", "10", 6, false);
    public static final MeioDePagamento VALE_REFEICAO = new MeioDePagamento(7, "Vale Refeição", "11", 7, false);
    public static final MeioDePagamento VALE_PRESENTE = new MeioDePagamento(8, "Vale Presente", "12", 8, false);
    public static final MeioDePagamento VALE_COMBUSTIVEL = new MeioDePagamento(9, "Vale Combustível", "13", 9, false);
    public static final MeioDePagamento OUTROS = new MeioDePagamento(10, "Outros", "99", 10, true);
    

    public MeioDePagamento(){}
    
    public MeioDePagamento(Integer id, String nome, String codigoSAT, Integer ordem, Boolean habilitado){
        this.id = id;
        this.nome = nome;
        this.codigoSAT = codigoSAT;
        this.ordem = ordem;
        this.habilitado = habilitado;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome != null ? nome : "";
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigoSAT() {
        return codigoSAT;
    }

    public void setCodigoSAT(String codigoSAT) {
        this.codigoSAT = codigoSAT;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Boolean getHabilitado() {
        return habilitado;
    }

    public void setHabilitado(Boolean habilitado) {
        this.habilitado = habilitado;
    }
    
    @Override
    public String toString() {
        return getNome();
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return Objects.equals(this.getId(), ((MeioDePagamento) obj).getId());
    }
}
