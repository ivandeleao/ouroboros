/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean.principal;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author ivand
 */
@Entity
public class DocumentoTipo implements Serializable {

    @Id
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;

    private String nome;

    //CONTANTES PARA FACILITAR O USO
    public static final DocumentoTipo ENTRADA = new DocumentoTipo(0, "ENTRADA");
    public static final DocumentoTipo SAIDA = new DocumentoTipo(1, "SAIDA");

    protected DocumentoTipo() {
    }

    public DocumentoTipo(Integer id, String nome) {
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

    //--------------------------------------------------------------------------
    @Override
    public boolean equals(Object obj) {
        //Reference: https://www.sitepoint.com/implement-javas-equals-method-correctly/
        //Usei apenas o id, se depararmos com algo que exija uma comparação mais forte
        //comparar todos os campos
        return Objects.equals(this.getId(), ((DocumentoTipo) obj).getId());
    }
}
