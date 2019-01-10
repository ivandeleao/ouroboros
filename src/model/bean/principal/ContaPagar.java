/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean.principal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Visão da conta a pagar, seja programada ou de compra
 * @author ivand
 */
public class ContaPagar implements Serializable {

    private ContaProgramada contaProgramada;

    private LocalDate vencimento;
    
    private ContaProgramadaBaixa contaProgramadaBaixa;
    
    

    public ContaProgramada getContaProgramada() {
        return contaProgramada;
    }

    public void setContaProgramada(ContaProgramada contaProgramada) {
        this.contaProgramada = contaProgramada;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public void setVencimento(LocalDate vencimento) {
        this.vencimento = vencimento;
    }

    public ContaProgramadaBaixa getContaProgramadaBaixa() {
        return contaProgramadaBaixa;
    }

    public void setContaProgramadaBaixa(ContaProgramadaBaixa contaProgramadaBaixa) {
        this.contaProgramadaBaixa = contaProgramadaBaixa;
    }
    
    
    //--------------------------------------------------------------------------
    
    public BigDecimal getValor() {
        if(getContaProgramada() != null) {
            return getContaProgramada().getValor();
        }
        //TODO integrar parcela de compra
        
        return BigDecimal.ZERO;
    }
    
    public BigDecimal getValorPago() {
        if(getContaProgramadaBaixa()!= null && getContaProgramadaBaixa().getCaixaItem() != null) {
            return getContaProgramadaBaixa().getCaixaItem().getSaldoLinear();
        }
        //TODO integrar parcela de compra
        
        return BigDecimal.ZERO;
    }
    
    public LocalDate getDataPago() {
        if(getContaProgramadaBaixa()!= null && getContaProgramadaBaixa().getCaixaItem() != null) {
            return getContaProgramadaBaixa().getCaixaItem().getCriacao().toLocalDateTime().toLocalDate();
        }
        //TODO integrar parcela de compra
        
        return null;
    }
}
