/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean.principal.pessoa;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author ivand
 */
@Entity
@Table(indexes = {
    @Index(columnList = "nome")})
public class Grupo implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;
    
    private LocalDateTime exclusao;
    
    private String nome;
    
    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL)
    @OrderBy
    private List<Perfil> perfis = new ArrayList<>();
    
    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL)
    @OrderBy
    private List<GrupoItem> grupoItens = new ArrayList<>();
    
    
    public Grupo() {}
    
    public Grupo(String nome) {
        this.nome = nome;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Perfil> getPerfis() {
        return perfis;
    }

    public void setPerfis(List<Perfil> perfis) {
        this.perfis = perfis;
    }

    public List<GrupoItem> getGrupoItens() {
        return grupoItens;
    }

    public void setGrupoItens(List<GrupoItem> grupoItens) {
        this.grupoItens = grupoItens;
    }

    
    //--------------------------------------------------------------------------
    
    public void addGrupoItem(GrupoItem grupoItem) {
        grupoItens.remove(grupoItem);
        grupoItens.add(grupoItem);
        grupoItem.setGrupo(this);
    }
    
    public void removeGrupoItem(GrupoItem grupoItem) {
        grupoItem.setGrupo(null);
        grupoItens.remove(grupoItem);
    }
    
    @Override
    public String toString() {
        return getNome();
    }
    
}
