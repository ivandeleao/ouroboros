package model.mysql.bean.relatorio;

import java.util.ArrayList;
import java.util.List;
import model.mysql.bean.principal.MovimentoFisico;
import util.Decimal;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author User
 */
public class MovimentoFisicoToStringAdapter {
    
    //private String numero;
    private String codigo;
    private String descricao;
    private String quantidade;
    private String unidadeMedida;
    private String valor;
    private String acrescimo;
    private String desconto;
    private String subtotal;
    
    private String ncm;
    private String cstCsosn;
    private String cfop;
    
    private String valorBcIcms;
    private String valorIcms;
    private String valorIpi;
    private String aliquotaIcms;
    private String aliquotaIpi;
    
    

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getAcrescimo() {
        return acrescimo;
    }

    public void setAcrescimo(String acrescimo) {
        this.acrescimo = acrescimo;
    }
    
    public String getDesconto() {
        return desconto;
    }

    public void setDesconto(String desconto) {
        this.desconto = desconto;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNcm() {
        return ncm;
    }

    public void setNcm(String ncm) {
        this.ncm = ncm;
    }

    public String getCstCsosn() {
        return cstCsosn;
    }

    public void setCstCsosn(String cstCsosn) {
        this.cstCsosn = cstCsosn;
    }

    public String getCfop() {
        return cfop;
    }

    public void setCfop(String cfop) {
        this.cfop = cfop;
    }

    public String getValorBcIcms() {
        return valorBcIcms;
    }

    public void setValorBcIcms(String valorBcIcms) {
        this.valorBcIcms = valorBcIcms;
    }

    public String getValorIcms() {
        return valorIcms;
    }

    public void setValorIcms(String valorIcms) {
        this.valorIcms = valorIcms;
    }

    public String getValorIpi() {
        return valorIpi;
    }

    public void setValorIpi(String valorIpi) {
        this.valorIpi = valorIpi;
    }

    public String getAliquotaIcms() {
        return aliquotaIcms;
    }

    public void setAliquotaIcms(String aliquotaIcms) {
        this.aliquotaIcms = aliquotaIcms;
    }

    public String getAliquotaIpi() {
        return aliquotaIpi;
    }

    public void setAliquotaIpi(String aliquotaIpi) {
        this.aliquotaIpi = aliquotaIpi;
    }
    
    
    public static MovimentoFisicoToStringAdapter adapt(MovimentoFisico mf) {
        MovimentoFisicoToStringAdapter mfReport = new MovimentoFisicoToStringAdapter();
        mfReport.setCodigo(mf.getCodigo());
        mfReport.setQuantidade(Decimal.toString(mf.getSaida()));
        if(mf.getUnidadeComercialVenda() != null) {
            mfReport.setUnidadeMedida(mf.getUnidadeComercialVenda().getNome());
        }
        mfReport.setValor(Decimal.toString(mf.getValor()));
        mfReport.setAcrescimo(mf.getAcrescimoFormatado());
        mfReport.setDesconto(mf.getDescontoFormatado());
        mfReport.setSubtotal(Decimal.toString(mf.getSubtotal()));
        mfReport.setDescricao(mf.getDescricao());
        
        return mfReport;
    }
    
    public static List<MovimentoFisicoToStringAdapter> adaptList(List<MovimentoFisico> mfs) {
        List<MovimentoFisicoToStringAdapter> mfsReport = new ArrayList<>();
        
        for(MovimentoFisico mf : mfs) {
            mfsReport.add(adapt(mf));
        }
        
        return mfsReport;
    }
    
}
