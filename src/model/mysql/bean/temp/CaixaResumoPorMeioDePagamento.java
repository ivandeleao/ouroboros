/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.temp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import model.mysql.bean.fiscal.MeioDePagamento;

/**
 *
 * @author ivand
 */
public class CaixaResumoPorMeioDePagamento implements Serializable {

    private MeioDePagamento meioDePagamento;

    private BigDecimal creditoTotal;
    private BigDecimal debitoTotal;
    private BigDecimal saldoCreditoDebito;
    
    private BigDecimal suprimentoTotal;
    private BigDecimal sangriaTotal;
    private BigDecimal saldoSuprimentoSangria;
    
    private BigDecimal saldoFinal;
    
    
    public CaixaResumoPorMeioDePagamento(MeioDePagamento meioDePagamento, BigDecimal creditoTotal, BigDecimal debitoTotal, BigDecimal saldoCreditoDebito, BigDecimal suprimentoTotal, BigDecimal sangriaTotal, BigDecimal saldoSuprimentoSangria, BigDecimal saldoFinal) {
        this.meioDePagamento = meioDePagamento;
        this.creditoTotal = creditoTotal;
        this.debitoTotal = debitoTotal;
        this.saldoCreditoDebito = saldoCreditoDebito;
        
        this.suprimentoTotal = suprimentoTotal;
        this.sangriaTotal = sangriaTotal;
        this.saldoSuprimentoSangria = saldoSuprimentoSangria;
        
        this.saldoFinal = saldoFinal;
    }

    public MeioDePagamento getMeioDePagamento() {
        return meioDePagamento;
    }

    public void setMeioDePagamento(MeioDePagamento meioDePagamento) {
        this.meioDePagamento = meioDePagamento;
    }

    public BigDecimal getCreditoTotal() {
        return creditoTotal;
    }

    public void setCreditoTotal(BigDecimal creditoTotal) {
        this.creditoTotal = creditoTotal;
    }

    public BigDecimal getDebitoTotal() {
        return debitoTotal;
    }

    public void setDebitoTotal(BigDecimal debitoTotal) {
        this.debitoTotal = debitoTotal;
    }

    public BigDecimal getSaldoCreditoDebito() {
        return saldoCreditoDebito;
    }

    public void setSaldoCreditoDebito(BigDecimal saldoCreditoDebito) {
        this.saldoCreditoDebito = saldoCreditoDebito;
    }

    public BigDecimal getSuprimentoTotal() {
        return suprimentoTotal;
    }

    public void setSuprimentoTotal(BigDecimal suprimentoTotal) {
        this.suprimentoTotal = suprimentoTotal;
    }

    public BigDecimal getSangriaTotal() {
        return sangriaTotal;
    }

    public void setSangriaTotal(BigDecimal sangriaTotal) {
        this.sangriaTotal = sangriaTotal;
    }

    public BigDecimal getSaldoSuprimentoSangria() {
        return saldoSuprimentoSangria;
    }

    public void setSaldoSuprimentoSangria(BigDecimal saldoSuprimentoSangria) {
        this.saldoSuprimentoSangria = saldoSuprimentoSangria;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }
    

    
    
    
}
