/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.nosql.relatorio;

import java.io.Serializable;

/**
 *
 * @author ivand
 */
public class CaixaResumoPorMeioDePagamentoReport implements Serializable {

    private String meioDePagamento;

    private String creditoTotal;
    private String debitoTotal;
    private String saldoCreditoDebito;
    
    private String suprimentoTotal;
    private String sangriaTotal;
    private String saldoSuprimentoSangria;
    
    private String saldoFinal;
    
    
    public CaixaResumoPorMeioDePagamentoReport(String meioDePagamento, String creditoTotal, String debitoTotal, String saldoCreditoDebito, String suprimentoTotal, String sangriaTotal, String saldoSuprimentoSangria, String saldoFinal) {
        this.meioDePagamento = meioDePagamento;
        this.creditoTotal = creditoTotal;
        this.debitoTotal = debitoTotal;
        this.saldoCreditoDebito = saldoCreditoDebito;
        
        this.suprimentoTotal = suprimentoTotal;
        this.sangriaTotal = sangriaTotal;
        this.saldoSuprimentoSangria = saldoSuprimentoSangria;
        
        this.saldoFinal = saldoFinal;
    }

    public String getMeioDePagamento() {
        return meioDePagamento;
    }

    public void setMeioDePagamento(String meioDePagamento) {
        this.meioDePagamento = meioDePagamento;
    }

    public String getCreditoTotal() {
        return creditoTotal;
    }

    public void setCreditoTotal(String creditoTotal) {
        this.creditoTotal = creditoTotal;
    }

    public String getDebitoTotal() {
        return debitoTotal;
    }

    public void setDebitoTotal(String debitoTotal) {
        this.debitoTotal = debitoTotal;
    }

    public String getSaldoCreditoDebito() {
        return saldoCreditoDebito;
    }

    public void setSaldoCreditoDebito(String saldoCreditoDebito) {
        this.saldoCreditoDebito = saldoCreditoDebito;
    }

    public String getSuprimentoTotal() {
        return suprimentoTotal;
    }

    public void setSuprimentoTotal(String suprimentoTotal) {
        this.suprimentoTotal = suprimentoTotal;
    }

    public String getSangriaTotal() {
        return sangriaTotal;
    }

    public void setSangriaTotal(String sangriaTotal) {
        this.sangriaTotal = sangriaTotal;
    }

    public String getSaldoSuprimentoSangria() {
        return saldoSuprimentoSangria;
    }

    public void setSaldoSuprimentoSangria(String saldoSuprimentoSangria) {
        this.saldoSuprimentoSangria = saldoSuprimentoSangria;
    }

    public String getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(String saldoFinal) {
        this.saldoFinal = saldoFinal;
    }
    

    
    
    
}
