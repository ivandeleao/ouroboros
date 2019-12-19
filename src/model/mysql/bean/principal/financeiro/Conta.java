/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.financeiro;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import model.mysql.dao.principal.CaixaItemDAO;
import model.nosql.ContaTipoEnum;

/**
 *
 * @author ivand
 */
@Entity
public class Conta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;

    private LocalDateTime exclusao;

    private String nome;
    
    private ContaTipoEnum contaTipo;
    
    private LocalDate data;

    @OneToMany(mappedBy = "conta")
    private List<CaixaItem> caixaItens = new ArrayList<>();

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

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public ContaTipoEnum getContaTipo() {
        return contaTipo;
    }

    public void setContaTipo(ContaTipoEnum contaTipo) {
        this.contaTipo = contaTipo;
    }
    
    

    //Bags----------------------------------------------------------------------
    public void addCaixaItem(CaixaItem caixaItem) {
        caixaItens.remove(caixaItem);
        caixaItens.add(caixaItem);
        caixaItem.setConta(this);
    }

    public void removeCaixaItem(CaixaItem caixaItem) {
        caixaItem.setConta(null);
        caixaItens.remove(caixaItem);
    }

    //Fim Bags------------------------------------------------------------------
    
    //Facilitadores-------------------------------------------------------------
    public LocalDate getUltimaData() {
        CaixaItem caixaItem = new CaixaItemDAO().getUltimaData(this);
        if(caixaItem != null) {
            return caixaItem.getDataHora().toLocalDate();
        }
        return getData();
    }
    
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
        return Objects.equals(this.getId(), ((Conta) obj).getId());
    }

}
