/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.catalogo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author ivand
 */
@Entity
public class ProdutoTipo implements Serializable {

    @Id
    private Integer id;
    private String nome;
    private String sigla;

    @OneToMany(mappedBy = "produtoTipo")
    private List<Produto> produtos = new ArrayList<>();

    //CONTANTES PARA FACILITAR O USO
    public static final ProdutoTipo PRODUTO = new ProdutoTipo(1, "PRODUTO", "P");
    public static final ProdutoTipo SERVICO = new ProdutoTipo(2, "SERVIÇO", "S");

    public ProdutoTipo() {
    }

    private ProdutoTipo(Integer id, String nome, String sigla) {
        this.id = id;
        this.nome = nome;
        this.sigla = sigla;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }
    
    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public boolean equals(Object obj) {
        //Reference: https://www.sitepoint.com/implement-javas-equals-method-correctly/
        //Usei apenas o id, se depararmos com algo que exija uma comparação mais forte
        //comparar todos os campos
        if (obj == null) {
            return false;
        }
        return Objects.equals(this.getId(), ((ProdutoTipo) obj).getId());
    }

}
