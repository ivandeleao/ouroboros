/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import model.TipoCalculoEnum;
import model.mysql.bean.fiscal.Ibpt;
import model.mysql.bean.fiscal.Ncm;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.fiscal.IbptDAO;

/**
 *
 * @author ivand
 */
public class FiscalUtil {
    
    public static BigDecimal calcularValorAproximadoTributosFederais(Ncm ncm, BigDecimal valor) {
        BigDecimal ibptAliqNac = BigDecimal.ZERO;
        Ibpt ibpt = new IbptDAO().findByCodigo(ncm.getCodigo());
        if(ibpt != null) {
            ibptAliqNac = new IbptDAO().findByCodigo(ncm.getCodigo()).getAliqNac();
        }

        BigDecimal valorIbpt = valor.multiply(ibptAliqNac).divide(new BigDecimal(100));
        
        return valorIbpt;
    }
    
    public static BigDecimal calcularValorAproximadoTributosEstaduais(Ncm ncm, BigDecimal valor) {
        BigDecimal ibptAliqEst = BigDecimal.ZERO;
        Ibpt ibpt = new IbptDAO().findByCodigo(ncm.getCodigo());
        if(ibpt != null) {
            ibptAliqEst = new IbptDAO().findByCodigo(ncm.getCodigo()).getAliqEst();
        }

        BigDecimal valorIbpt = valor.multiply(ibptAliqEst).divide(new BigDecimal(100));
        
        return valorIbpt;
    }
    
    
    public static String getMensagemValorAproximadoTributos(Venda documento) {
        return getMensagemValorAproximadoTributos(documento.getTotal(), documento.getTotalValorAproximadoTributosFederais(), documento.getTotalValorAproximadoTributosEstaduais());
    }
    
    
    public static String getMensagemValorAproximadoTributos(BigDecimal valorNota, BigDecimal valorFederal, BigDecimal valorEstadual) {
        
        BigDecimal valorTotal = valorFederal.add(valorEstadual);
        
        String total = Decimal.toString(valorTotal);
        String federal = Decimal.toString(valorFederal);
        String estadual = Decimal.toString(valorEstadual);
        
        String percentualTotal = Decimal.toString(valorTotal.divide(valorNota, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
        String percentualFederal = Decimal.toString(valorFederal.divide(valorNota, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
        String percentualEstadual = Decimal.toString(valorEstadual.divide(valorNota, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
        
        
        return "Total aproximado de tributos: R$ " + total + " (" + percentualTotal + "%), "
                + "Federais R$ " + federal + " (" + percentualFederal + "%), "
                + "Estaduais R$ " + estadual + " (" + percentualEstadual + "%). "
                + "Fonte IBPT.";
    }
    
    public static MovimentoFisico calcularTributos(MovimentoFisico mf) {
        mf = calcularPis(mf);
        mf = calcularPisSt(mf);
        mf = calcularCofins(mf);
        mf = calcularCofinsSt(mf);
        
        return mf;
    }
    
    public static MovimentoFisico calcularPis(MovimentoFisico mf) {
        if(mf.getPisTipoCalculo() != null) {
            if(mf.getPisTipoCalculo().equals(TipoCalculoEnum.PERCENTUAL)) {
                mf.setValorBcPis(mf.getSubtotalItem());
                mf.setValorPis(mf.getValorBcPis().multiply(mf.getAliquotaPis()).divide(new BigDecimal(100), RoundingMode.HALF_UP));
            } else {
                mf.setQuantidadeVendidaPis(mf.getSaida());
                mf.setValorPis( mf.getQuantidadeVendidaPis().multiply(mf.getAliquotaPisReais()));
            }
        }
        
        return mf;
    }
    
    public static MovimentoFisico calcularPisSt(MovimentoFisico mf) {
        if(mf.getPisStTipoCalculo() != null) {
            if(mf.getPisStTipoCalculo().equals(TipoCalculoEnum.PERCENTUAL)) {
                mf.setValorBcPisSt(mf.getSubtotalItem());
                mf.setValorPisSt(mf.getValorBcPisSt().multiply(mf.getAliquotaPisSt()).divide(new BigDecimal(100), RoundingMode.HALF_UP));
            } else {
                mf.setQuantidadeVendidaPisSt(mf.getSaida());
                mf.setValorPisSt( mf.getQuantidadeVendidaPisSt().multiply(mf.getAliquotaPisStReais()));
            }
        }
        return mf;
    }
    
    public static MovimentoFisico calcularCofins(MovimentoFisico mf) {
        if(mf.getCofinsTipoCalculo() != null) {
            if(mf.getCofinsTipoCalculo().equals(TipoCalculoEnum.PERCENTUAL)) {
                mf.setValorBcCofins(mf.getSubtotalItem());
                mf.setValorCofins(mf.getValorBcCofins().multiply(mf.getAliquotaCofins()).divide(new BigDecimal(100), RoundingMode.HALF_UP));
            } else {
                mf.setQuantidadeVendidaCofins(mf.getSaida());
                mf.setValorCofins( mf.getQuantidadeVendidaCofins().multiply(mf.getAliquotaCofinsReais()));
            }
        }
        
        return mf;
    }
    
    public static MovimentoFisico calcularCofinsSt(MovimentoFisico mf) {
        if(mf.getCofinsStTipoCalculo() != null) {
            if(mf.getCofinsStTipoCalculo().equals(TipoCalculoEnum.PERCENTUAL)) {
                mf.setValorBcCofinsSt(mf.getSubtotalItem());
                mf.setValorCofinsSt(mf.getValorBcCofinsSt().multiply(mf.getAliquotaCofinsSt()).divide(new BigDecimal(100), RoundingMode.HALF_UP));
            } else {
                mf.setQuantidadeVendidaCofinsSt(mf.getSaida());
                mf.setValorCofinsSt( mf.getQuantidadeVendidaCofinsSt().multiply(mf.getAliquotaCofinsStReais()));
            }
        }
        
        return mf;
    }
}
