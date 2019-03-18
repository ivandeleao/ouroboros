/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean.principal;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Recursos do sistema: telas, ações e opções diversas para liberar/negar ao usuário
 * @author ivand
 */
@Entity
public class Recurso implements Serializable{
    
    @Id
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;
    
    private String nome;
    
    @OneToMany(mappedBy = "recurso")
    private Set<Diretiva> setUsuarioRecurso = new HashSet<>();
    
    
    public static final Recurso SISTEMA = new Recurso(1, "SISTEMA");
    public static final Recurso USUARIOS = new Recurso(2, "USUÁRIOS");
    public static final Recurso PRODUTOS = new Recurso(3, "PRODUTOS");
    public static final Recurso FATURAMENTO = new Recurso(4, "FATURAMENTO");
    public static final Recurso COMANDAS = new Recurso(5, "COMANDAS");
    public static final Recurso FINANCEIRO = new Recurso(6, "FINANCEIRO");
    public static final Recurso PESSOAS = new Recurso(7, "PESSOAS");
    public static final Recurso BACKUP = new Recurso(8, "BACKUP");
    
    public static final Recurso ORCAMENTO = new Recurso(9, "ORÇAMENTO");
    public static final Recurso VENDA = new Recurso(10, "VENDA");
    public static final Recurso PEDIDO = new Recurso(11, "PEDIDO");
    public static final Recurso ORDEM_DE_SERVICO = new Recurso(12, "ORDEM DE SERVIÇO");
    public static final Recurso LOCACAO = new Recurso(13, "LOCAÇÃO");
    public static final Recurso COMPRA = new Recurso(14, "COMPRA");

    
    public Recurso() {
    }
    
    public Recurso(int id, String nome) {
        this.id = id;
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
    

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Set<Diretiva> getSetUsuarioRecurso() {
        return setUsuarioRecurso;
    }

    public void setSetUsuarioRecurso(Set<Diretiva> setUsuarioRecurso) {
        this.setUsuarioRecurso = setUsuarioRecurso;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Recurso)) {
            return false;
        }
        return Objects.equals(this.getId(), ((Recurso) obj).getId());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.id);
        return hash;
    }
    
    
}
