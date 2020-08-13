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
import model.mysql.bean.principal.Funcionario;

/**
 *
 * @author ivand
 */
public class ProdutoPorVendedorConsolidado implements Serializable {

    private Funcionario vendedor;
    
    private Produto produto;

    private BigDecimal quantidade;
    
    private BigDecimal total;
    
    
    public ProdutoPorVendedorConsolidado(){}
    
    public ProdutoPorVendedorConsolidado(Funcionario vendedor, Produto produto, BigDecimal quantidade, BigDecimal total) {
        this.vendedor = vendedor;
        this.produto = produto;
        this.quantidade = quantidade;
        this.total = total;
    }

    public Funcionario getVendedor() {
        return vendedor;
    }

    public void setVendedor(Funcionario vendedor) {
        this.vendedor = vendedor;
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
