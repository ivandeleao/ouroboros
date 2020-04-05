/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.financeiro;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author ivand
 */
@Entity
public class CaixaItemTipo implements Serializable {

    @Id
    private Integer id;
    @CreationTimestamp
    private Timestamp criacao;
    @UpdateTimestamp
    private Timestamp atualizacao;

    private String nome;
    @Column(columnDefinition = "boolean default true")
    private Boolean habilitado;

    //CONTANTES PARA FACILITAR O USO
    public static final CaixaItemTipo LANCAMENTO_MANUAL = new CaixaItemTipo(1, "LANÇAMENTO MANUAL");
    public static final CaixaItemTipo DOCUMENTO = new CaixaItemTipo(2, "DOCUMENTO");
    public static final CaixaItemTipo ESTORNO = new CaixaItemTipo(3, "ESTORNO");
    public static final CaixaItemTipo TROCO = new CaixaItemTipo(4, "TROCO");

    public static final CaixaItemTipo SUPRIMENTO = new CaixaItemTipo(5, "SUPRIMENTO");
    public static final CaixaItemTipo SANGRIA = new CaixaItemTipo(6, "SANGRIA");
    
    public static final CaixaItemTipo CONTA_PROGRAMADA = new CaixaItemTipo(7, "CONTA PROGRAMADA");
    //REMOVIDO EM 2019-06-10 - genérico com tipo 2
    //public static final CaixaItemTipo PAGAMENTO_DOCUMENTO = new CaixaItemTipo(8, "PAGAMENTO DOCUMENTO");
    public static final CaixaItemTipo TRANSFERENCIA = new CaixaItemTipo(8, "TRANFERÊNCIA");
    
    public CaixaItemTipo() {
    }

    public CaixaItemTipo(Integer id, String nome) {
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

    public Timestamp getAtualizacao() {
        return atualizacao;
    }

    public void setAtualizacao(Timestamp atualizacao) {
        this.atualizacao = atualizacao;
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
        return Objects.equals(this.getId(), ((CaixaItemTipo) obj).getId());
    }
}
