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
public class MovimentoFisicoReport {
    
    //private String numero;
    private String codigo;
    private String quantidade;
    private String unidadeMedida;
    private String valor;
    private String desconto;
    private String subtotal;
    private String descricao;

    public static MovimentoFisicoReport adapt(MovimentoFisico mf) {
        MovimentoFisicoReport mfReport = new MovimentoFisicoReport();
        mfReport.setCodigo(mf.getCodigo());
        mfReport.setQuantidade(Decimal.toString(mf.getSaida()));
        if(mf.getUnidadeComercialVenda() != null) {
            mfReport.setUnidadeMedida(mf.getUnidadeComercialVenda().getNome());
        }
        mfReport.setValor(Decimal.toString(mf.getValor()));
        mfReport.setDesconto(Decimal.toString(mf.getDescontoPercentual()) + "%");
        mfReport.setSubtotal(Decimal.toString(mf.getSubtotal()));
        mfReport.setDescricao(mf.getDescricao());
        
        return mfReport;
    }
    
    public static List<MovimentoFisicoReport> adaptList(List<MovimentoFisico> mfs) {
        List<MovimentoFisicoReport> mfsReport = new ArrayList<>();
        
        for(MovimentoFisico mf : mfs) {
            mfsReport.add(adapt(mf));
        }
        
        return mfsReport;
    }
    
    
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
    
    
    
}
