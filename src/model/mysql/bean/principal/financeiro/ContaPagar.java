/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.financeiro;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.nosql.FinanceiroStatusEnum;
import model.mysql.bean.principal.documento.Parcela;

/**
 * Visão da conta a pagar, seja programada ou de compra
 * @author ivand
 */
public class ContaPagar implements Serializable {

    private ContaProgramada contaProgramada;

    private LocalDate vencimento;
    
    private ContaProgramadaBaixa contaProgramadaBaixa;
    
    private Parcela parcela;
    
    

    public ContaProgramada getContaProgramada() {
        return contaProgramada;
    }

    public void setContaProgramada(ContaProgramada contaProgramada) {
        this.contaProgramada = contaProgramada;
    }

    public LocalDate getVencimento() {
        if(parcela != null) {
            return parcela.getVencimento();
        }
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

    public Parcela getParcela() {
        return parcela;
    }

    public void setParcela(Parcela parcela) {
        this.parcela = parcela;
    }
    
    
    
    //--------------------------------------------------------------------------
    
    public String getDescricao() {
        if(parcela != null) {
            return parcela.getDescricao();
            
        } else {
            return getContaProgramada().getNome();
            
        }
    }
    
    public FinanceiroStatusEnum getStatus() {
        if(parcela != null) {
            return parcela.getStatus();
            
        } else {
            if(getContaProgramadaBaixa() == null && getVencimento().isBefore(LocalDate.now())) {
                return FinanceiroStatusEnum.VENCIDO;

            } else if(getContaProgramadaBaixa() == null) {
                return FinanceiroStatusEnum.ABERTO;

            } else if(getContaProgramadaBaixa() != null && getContaProgramadaBaixa().getCaixaItem() != null) {
                return FinanceiroStatusEnum.QUITADO;
            }
        }
        
        return null;
    }
    
    public BigDecimal getValor() {
        if(parcela != null) {
            return parcela.getValor();
            
        } else {
            if(getContaProgramadaBaixa() != null) {
                return getContaProgramadaBaixa().getValor();
            }
            if(getContaProgramada() != null) {
                return getContaProgramada().getValor();
            }
        }
        
        return BigDecimal.ZERO;
    }
    
    public BigDecimal getValorPago() {
        if(parcela != null) {
            return parcela.getValorQuitado();
            
        } else {
            if(getContaProgramadaBaixa()!= null && getContaProgramadaBaixa().getCaixaItem() != null) {
                return getContaProgramadaBaixa().getCaixaItem().getDebito();
            }
        }
        
        return BigDecimal.ZERO;
    }
    
    public LocalDate getDataPago() {
        if(parcela != null && parcela.getUltimoRecebimento() != null) {
            return parcela.getUltimoRecebimento().toLocalDate();
            
        } else {
            if(getContaProgramadaBaixa()!= null && getContaProgramadaBaixa().getCaixaItem() != null) {
                return getContaProgramadaBaixa().getCaixaItem().getCriacao().toLocalDate();
            }
        }
        
        return null;
    }
    
    public MeioDePagamento getMeioDePagamento() {
        if(parcela != null) {
            return parcela.getMeioDePagamento();
        } else {
            return null;
        }
    }
    
    public String getObservacao() {
        if(parcela != null) {
            return "";
            
        } else {
            if(getContaProgramadaBaixa()!= null) {
                return getContaProgramadaBaixa().getCaixaItem().getObservacao();
            }
        }
        
        return "";
    }
}
