/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.pessoa;

import model.mysql.bean.principal.pessoa.Pessoa;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Perfil da pessoa no grupo
 *
 * @author ivand
 */
@Entity
public class Perfil implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;

    @ManyToOne
    @JoinColumn(name = "pessoaId")
    private Pessoa pessoa;

    @ManyToOne
    @JoinColumn(name = "grupoId")
    private Grupo grupo;

    private Integer diaVencimento;

    private String observacao;
    
    @OneToMany(mappedBy = "perfil", cascade = CascadeType.ALL)
    @OrderBy
    private List<PerfilItem> perfilItens = new ArrayList<>();

    public Perfil() {
    }

    public Perfil(Pessoa pessoa, Grupo grupo) {
        this.pessoa = pessoa;
        this.grupo = grupo;
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

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public Integer getDiaVencimento() {
        return diaVencimento != null ? diaVencimento : 0;
    }

    public void setDiaVencimento(Integer diaVencimento) {
        this.diaVencimento = diaVencimento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<PerfilItem> getPerfilItens() {
        return perfilItens;
    }

    public void setPerfilItens(List<PerfilItem> perfilItens) {
        this.perfilItens = perfilItens;
    }
    
    

    //--------------------------------------------------------------------------
    
    public void addPerfilItem(PerfilItem perfilItem) {
        perfilItens.remove(perfilItem);
        perfilItens.add(perfilItem);
        perfilItem.setPerfil(this);
    }
    
    public void removePerfilItem(PerfilItem perfilItem) {
        perfilItem.setPerfil(null);
        perfilItens.remove(perfilItem);
    }
}
