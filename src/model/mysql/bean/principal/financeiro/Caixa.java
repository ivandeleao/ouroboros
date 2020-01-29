/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.financeiro;

import java.io.Serializable;
import java.time.LocalDateTime;
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
 * Esta entidade é um turno e não um caixa
 */
@Entity
public class Caixa implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;
    
    private LocalDateTime encerramento;

    @ManyToOne
    @JoinColumn(name = "contaId", nullable = true, columnDefinition = "int default 1") //2019-12-09 entidade Caixa é um turno de uma conta do tipo Caixa
    private Conta conta;
    
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

    public LocalDateTime getEncerramento() {
        return encerramento;
    }

    public void setEncerramento(LocalDateTime encerramento) {
        this.encerramento = encerramento;
    }

    public List<CaixaItem> getCaixaItens() {
        return caixaItens;
    }

    public void setCaixaItens(List<CaixaItem> caixaItens) {
        this.caixaItens = caixaItens;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
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
