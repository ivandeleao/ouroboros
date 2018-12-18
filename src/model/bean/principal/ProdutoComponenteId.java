/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean.principal;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author ivand
 */
public class ProdutoComponenteId implements Serializable{
    private Integer produtoId;
    private Integer componenteId;

    public ProdutoComponenteId() {
        
    }
    
    public ProdutoComponenteId(Integer produtoId, Integer componenteId) {
        this.produtoId = produtoId;
        this.componenteId = componenteId;
    }

    public Integer getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Integer produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getComponenteId() {
        return componenteId;
    }

    public void setComponenteId(Integer componenteId) {
        this.componenteId = componenteId;
    }

    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return Objects.equals(this.getProdutoId(), ((ProdutoComponenteId) obj).getProdutoId())
                &&
                Objects.equals(this.getComponenteId(), ((ProdutoComponenteId) obj).getComponenteId())
                ;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.produtoId);
        hash = 59 * hash + Objects.hashCode(this.componenteId);
        return hash;
    }
}
