/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.financeiro;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author ivand
 */
@Entity
public class Caixa implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private Timestamp criacao;
    @UpdateTimestamp
    private Timestamp atualizacao;
    
    private Timestamp encerramento;
    
    @ManyToOne
    @JoinColumn(name = "periodoId", nullable = true)
    private CaixaPeriodo caixaPeriodo;
    
    @OneToMany(mappedBy="caixa", cascade = CascadeType.ALL)
    private List<CaixaItem> caixaItens = new ArrayList<>();
    

    public Caixa(){}
    
    public Caixa(CaixaPeriodo caixaPeriodo){
        this.caixaPeriodo = caixaPeriodo;
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

    public Timestamp getEncerramento() {
        return encerramento;
    }

    public void setEncerramento(Timestamp encerramento) {
        this.encerramento = encerramento;
    }

    public List<CaixaItem> getCaixaItens() {
        return caixaItens;
    }

    public void setCaixaItens(List<CaixaItem> caixaItens) {
        this.caixaItens = caixaItens;
    }
    
    public CaixaPeriodo getCaixaPeriodo() {
        return caixaPeriodo;
    }

    public void setCaixaPeriodo(CaixaPeriodo caixaPeriodo) {
        this.caixaPeriodo = caixaPeriodo;
    }
    
    //--------------------------------------------------------------------------
    
    public void addCaixaItem(CaixaItem caixaItem) {
        caixaItens.remove(caixaItem);
        caixaItens.add(caixaItem);
        caixaItem.setCaixa(this);
    }
    
    public void removeCaixaItem(CaixaItem caixaItem) {
        caixaItem.setCaixa(null);
        caixaItens.remove(caixaItem);
    }
    
    
}
