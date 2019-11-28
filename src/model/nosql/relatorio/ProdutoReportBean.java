package model.nosql.relatorio;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author User
 */
public class ProdutoReportBean {
    
    private String codigo;
    private String nome;
    private String estoque;
    private String valorCompra;
    private String estoqueCompra;
    private String valorVenda;
    private String estoqueVenda;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEstoque() {
        return estoque;
    }

    public void setEstoque(String estoque) {
        this.estoque = estoque;
    }

    public String getValorCompra() {
        return valorCompra;
    }

    public void setValorCompra(String valorCompra) {
        this.valorCompra = valorCompra;
    }

    public String getEstoqueCompra() {
        return estoqueCompra;
    }

    public void setEstoqueCompra(String estoqueCompra) {
        this.estoqueCompra = estoqueCompra;
    }

    public String getValorVenda() {
        return valorVenda;
    }

    public void setValorVenda(String valorVenda) {
        this.valorVenda = valorVenda;
    }

    public String getEstoqueVenda() {
        return estoqueVenda;
    }

    public void setEstoqueVenda(String estoqueVenda) {
        this.estoqueVenda = estoqueVenda;
    }

}
