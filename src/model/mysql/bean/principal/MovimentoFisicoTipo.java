/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;

/**
 *
 * @author ivand
 */
@Entity
public class MovimentoFisicoTipo implements Serializable {

    @Id
    private Integer id;
    @CreationTimestamp
    private Timestamp criacao;

    private String nome;
    @Column(columnDefinition = "boolean default true")
    private Boolean habilitado;

    //CONTANTES PARA FACILITAR O USO
    public static final MovimentoFisicoTipo LANCAMENTO_MANUAL = new MovimentoFisicoTipo(1, "LANÇAMENTO MANUAL");
    public static final MovimentoFisicoTipo VENDA = new MovimentoFisicoTipo(2, "VENDA");
    public static final MovimentoFisicoTipo EXCLUSAO_VENDA = new MovimentoFisicoTipo(3, "EXCLUSÃO DE VENDA");
    public static final MovimentoFisicoTipo COMPRA = new MovimentoFisicoTipo(4, "COMPRA");
    public static final MovimentoFisicoTipo EXCLUSAO_COMPRA = new MovimentoFisicoTipo(5, "EXCLUSÃO DE COMPRA");
    public static final MovimentoFisicoTipo ALUGUEL = new MovimentoFisicoTipo(6, "ALUGUEL");
    public static final MovimentoFisicoTipo DEVOLUCAO_ALUGUEL = new MovimentoFisicoTipo(7, "DEVOLUÇÃO DE ALUGUEL");

    
    public MovimentoFisicoTipo() {
    }

    public MovimentoFisicoTipo(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
        this.habilitado = true;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getCriacao() {
        return criacao;
    }

    public void setCriacao(Timestamp criacao) {
        this.criacao = criacao;
    }

    public String getNome() {
        return nome != null ? nome : "";
    }

    public void setNome(String nome) {
        this.nome = nome;
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
    public boolean equals(Object obj){
        //Reference: https://www.sitepoint.com/implement-javas-equals-method-correctly/
        //Usei apenas o id, se depararmos com algo que exija uma comparação mais forte
        //comparar todos os campos
        return Objects.equals(this.getId(), ((MovimentoFisicoTipo) obj).getId());
    }
}
