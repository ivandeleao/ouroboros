/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.catalogo;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import model.mysql.bean.principal.pessoa.Pessoa;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author ivand
 */
@Entity
public class ProdutoFornecedor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;

    @ManyToOne
    @JoinColumn(name = "produtoId")
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "fornecedorId")
    private Pessoa fornecedor;

    private String codigoNoFornecedor;

    private ProdutoFornecedor() {
    }

    public ProdutoFornecedor(Produto produto, Pessoa fornecedor, String codigoNoFornecedor) {
        this.produto = produto;
        this.fornecedor = fornecedor;
        this.codigoNoFornecedor = codigoNoFornecedor;
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

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getCodigoNoFornecedor() {
        return codigoNoFornecedor;
    }

    public void setCodigoNoFornecedor(String codigoNoFornecedor) {
        this.codigoNoFornecedor = codigoNoFornecedor;
    }

}
