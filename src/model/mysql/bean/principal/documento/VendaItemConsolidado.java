/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.documento;

import model.mysql.bean.principal.catalogo.Produto;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author ivand
 */
public class VendaItemConsolidado implements Serializable {

    private Produto produto;

    private BigDecimal quantidade;
    
    private BigDecimal total;
    
    
    public VendaItemConsolidado(){}
    
    public VendaItemConsolidado(Produto produto, BigDecimal quantidade, BigDecimal total) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.total = total;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public BigDecimal getQuantidade() {
        return quantidade != null ? quantidade : BigDecimal.ZERO;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorMedio() {
        return total.divide(getQuantidade(), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotal() {
        return total != null ? total : BigDecimal.ZERO;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    

}
