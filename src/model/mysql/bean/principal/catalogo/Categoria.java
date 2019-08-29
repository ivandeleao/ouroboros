/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.catalogo;

import model.mysql.bean.principal.catalogo.Produto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
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
public class Categoria implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    
    @OneToMany(mappedBy = "categoria")
    private List<Produto> produtoList = new ArrayList<>();
    
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tamanho> tamanhos = new ArrayList<>();

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

    public List<Produto> getProdutoList() {
        return produtoList;
    }

    public void setProdutoList(List<Produto> produtoList) {
        this.produtoList = produtoList;
    }

    public List<Tamanho> getTamanhos() {
        return tamanhos;
    }

    public void setTamanhos(List<Tamanho> tamanhos) {
        this.tamanhos = tamanhos;
    }
    
    //Bags----------------------------------------------------------------------
    
    public void addTamanho(Tamanho tamanho) {
        tamanhos.remove(tamanho);
        tamanhos.add(tamanho);
        tamanho.setCategoria(this);
    }
    
    public void removeTamanho(Tamanho tamanho) {
        tamanho.setCategoria(null);
        tamanhos.remove(tamanho);
    }
    
    //Fim Bags------------------------------------------------------------------
    
    @Override
    public String toString(){
        return nome;
    }
    
    @Override
    public boolean equals(Object obj) {
        //Reference: https://www.sitepoint.com/implement-javas-equals-method-correctly/
        //Usei apenas o id, se depararmos com algo que exija uma comparação mais forte
        //comparar todos os campos
        if(obj == null){
            return false;
        }
        return Objects.equals(this.getId(), ((Categoria) obj).getId());
    }
}
