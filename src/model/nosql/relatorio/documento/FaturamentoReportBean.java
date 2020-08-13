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
public class FaturamentoReportBean {

    private String vendedor;

    private BigDecimal vendaPrazo;
    private BigDecimal vistaCheque;
    private BigDecimal vistaDinheiro;
    private BigDecimal recebidoCheque;
    private BigDecimal recebidoDinheiro;
    private BigDecimal recebidoBancario;

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
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
