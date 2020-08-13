/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import model.nosql.TipoCalculoEnum;
import model.mysql.bean.fiscal.Ibpt;
import model.mysql.bean.fiscal.Ncm;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.endereco.CidadeDAO;
import model.mysql.dao.fiscal.IbptDAO;
import ouroboros.Ouroboros;

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
        
        if (valorNota.compareTo(BigDecimal.ZERO) == 0) {
            return "";
        }
        
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
        mf = calcularIcms(mf);
        mf = calcularIcmsSt(mf);
        mf = calcularIpi(mf);
        mf = calcularPis(mf);
        mf = calcularPisSt(mf);
        mf = calcularCofins(mf);
        mf = calcularCofinsSt(mf);
        mf = ajustarTributavel(mf);
        mf = preencherCombustivel(mf);
        
        return mf;
    }
    
    public static MovimentoFisico calcularIcms(MovimentoFisico mf) {
        
        
        
        return mf;
    }
    
    public static MovimentoFisico calcularIcmsSt(MovimentoFisico mf) {
        /*
        Base do ICMS Inter = (Valor do produto + Frete + Seguro + Outras Despesas Acessórias - Descontos) 
        Valor do ICMS Inter = Base ICMS Inter * (Alíquota ICMS Inter / 100)
        */
        
        BigDecimal icmsInter = mf.getValor().add(mf.getValorFrete()).add(mf.getAcrescimoConsolidado()).subtract(mf.getDescontoConsolidado());
        
        
        BigDecimal icmsStValorBc = mf.getValorBcIcmsSt();
        BigDecimal icmsStAliquota = mf.getAliquotaIcmsSt();
        BigDecimal icmsStValor = icmsStValorBc.multiply(icmsStAliquota).divide(new BigDecimal(100), RoundingMode.HALF_UP);
        
        mf.setValorIcmsSt(icmsStValor);
        
        return mf;
    }
    
    public static MovimentoFisico calcularIpi(MovimentoFisico mf) {
        if(mf.getIpiTipoCalculo() != null) {
            if(mf.getIpiTipoCalculo().equals(TipoCalculoEnum.PERCENTUAL)) {
                mf.setIpiValorBc(mf.getSubtotalItem());
                mf.setIpiValor(mf.getIpiValorBc().multiply(mf.getIpiAliquota()).divide(new BigDecimal(100), RoundingMode.HALF_UP));
            } else {
                mf.setIpiQuantidadeTotalUnidadePadrao(mf.getSaida());
                mf.setIpiValor( mf.getIpiQuantidadeTotalUnidadePadrao().multiply(mf.getIpiValorUnidadeTributavel()));
            }
        }
        
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
    
    public static MovimentoFisico ajustarTributavel(MovimentoFisico mf) {
        if(mf.getUnidadeTributavel() == null) {
            mf.setUnidadeTributavel(mf.getUnidadeComercialVenda());
        }
        if(mf.getUnidadeTributavel().equals(mf.getUnidadeComercialVenda())) {
            mf.setQuantidadeTributavel(mf.getSaida());
            mf.setValorTributavel(mf.getValor());
            //System.out.println("igual");
        } else {
            //System.out.println("diferente");
            if(mf.getValorTributavel().compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("valor trib menor ou igual 0...");
                mf.setValorTributavel(mf.getValor());
            }
            mf.setQuantidadeTributavel(mf.getSaida().multiply(mf.getValor()).divide(mf.getValorTributavel(), 2, RoundingMode.HALF_UP));
        }
        
        return mf;
    }
    
    public static MovimentoFisico preencherCombustivel(MovimentoFisico mf) {
        Produto produto = mf.getProduto();
        
        mf.setAnp(produto.getAnp());
        mf.setCodif(produto.getCodif());
        mf.setCombustivelUf(new CidadeDAO().findByCodigoIbge(Ouroboros.EMPRESA_ENDERECO_CODIGO_MUNICIPIO).getEstado().getSigla());
        
        return mf;
    }
}
