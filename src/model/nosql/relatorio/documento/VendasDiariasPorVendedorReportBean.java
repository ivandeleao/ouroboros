/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.nosql.relatorio.documento;

import java.math.BigDecimal;

/**
 *
 * @author ivand
 */
public class VendasDiariasPorVendedorReportBean {

    private String vendedor;

    private String clienteNome;
    private String clienteCidade;

    private Integer documentoId;

    private String produto;
    private BigDecimal quantidade;
    private BigDecimal subtotal;

    private BigDecimal somaQuantidade;
    private BigDecimal somaQuantidadeBonificacao;
    
    private BigDecimal vendaPrazo;
    private BigDecimal vistaCheque;
    private BigDecimal vistaDinheiro;

    private BigDecimal totalQuantidade;
    private BigDecimal totalQuantidadeBonificacao;
    private BigDecimal totalPrazo;
    private BigDecimal totalCheque;
    private BigDecimal totalDinheiro;
    
    private BigDecimal recebidoCheque;
    private BigDecimal recebidoDinheiro;
    private BigDecimal recebidoBancario;

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public String getClienteCidade() {
        return clienteCidade;
    }

    public void setClienteCidade(String clienteCidade) {
        this.clienteCidade = clienteCidade;
    }

    public Integer getDocumentoId() {
        return documentoId;
    }

    public void setDocumentoId(Integer documentoId) {
        this.documentoId = documentoId;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getSomaQuantidade() {
        return somaQuantidade;
    }

    public void setSomaQuantidade(BigDecimal somaQuantidade) {
        this.somaQuantidade = somaQuantidade;
    }

    public BigDecimal getSomaQuantidadeBonificacao() {
        return somaQuantidadeBonificacao;
    }

    public void setSomaQuantidadeBonificacao(BigDecimal somaQuantidadeBonificacao) {
        this.somaQuantidadeBonificacao = somaQuantidadeBonificacao;
    }

    public BigDecimal getVendaPrazo() {
        return vendaPrazo;
    }

    public void setVendaPrazo(BigDecimal vendaPrazo) {
        this.vendaPrazo = vendaPrazo;
    }

    public BigDecimal getVistaCheque() {
        return vistaCheque;
    }

    public void setVistaCheque(BigDecimal vistaCheque) {
        this.vistaCheque = vistaCheque;
    }

    public BigDecimal getVistaDinheiro() {
        return vistaDinheiro;
    }

    public void setVistaDinheiro(BigDecimal vistaDinheiro) {
        this.vistaDinheiro = vistaDinheiro;
    }

    public BigDecimal getTotalQuantidade() {
        return totalQuantidade;
    }

    public void setTotalQuantidade(BigDecimal totalQuantidade) {
        this.totalQuantidade = totalQuantidade;
    }

    public BigDecimal getTotalQuantidadeBonificacao() {
        return totalQuantidadeBonificacao;
    }

    public void setTotalQuantidadeBonificacao(BigDecimal totalQuantidadeBonificacao) {
        this.totalQuantidadeBonificacao = totalQuantidadeBonificacao;
    }

    public BigDecimal getTotalPrazo() {
        return totalPrazo;
    }

    public void setTotalPrazo(BigDecimal totalPrazo) {
        this.totalPrazo = totalPrazo;
    }

    public BigDecimal getTotalCheque() {
        return totalCheque;
    }

    public void setTotalCheque(BigDecimal totalCheque) {
        this.totalCheque = totalCheque;
    }

    public BigDecimal getTotalDinheiro() {
        return totalDinheiro;
    }

    public void setTotalDinheiro(BigDecimal totalDinheiro) {
        this.totalDinheiro = totalDinheiro;
    }

    public BigDecimal getRecebidoCheque() {
        return recebidoCheque;
    }

    public void setRecebidoCheque(BigDecimal recebidoCheque) {
        this.recebidoCheque = recebidoCheque;
    }

    public BigDecimal getRecebidoDinheiro() {
        return recebidoDinheiro;
    }

    public void setRecebidoDinheiro(BigDecimal recebidoDinheiro) {
        this.recebidoDinheiro = recebidoDinheiro;
    }

    public BigDecimal getRecebidoBancario() {
        return recebidoBancario;
    }

    public void setRecebidoBancario(BigDecimal recebidoBancario) {
        this.recebidoBancario = recebidoBancario;
    }

    
    
}
