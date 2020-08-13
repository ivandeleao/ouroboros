/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import model.mysql.bean.principal.Funcionario;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.catalogo.ProdutoTipo;
import model.mysql.bean.principal.documento.Venda;
import model.nosql.TipoCalculoEnum;

/**
 *
 * @author ivand
 */
public class MovimentoFisicoUtil {

    public static MovimentoFisico calcularComissao(MovimentoFisico mf, Venda documento) {
        if (documento.getFuncionario() != null) {
            mf.setComissaoDocumento( calcularComissaoDocumento(mf, documento.getFuncionario()) );
        }
        
        if (mf.getFuncionario() != null) {
            mf.setComissaoItem( calcularComissaoItem(mf, mf.getFuncionario()) );
        }
        
        return mf;
    }
    
    
    public static BigDecimal calcularComissaoDocumento(MovimentoFisico mf, Funcionario funcionario) {
        BigDecimal valorComissao;
        
        if (mf.getProdutoTipo().equals(ProdutoTipo.PRODUTO)) {
            if (funcionario.getComissaoDocumentoProdutoTipo().equals(TipoCalculoEnum.VALOR)) {
                valorComissao = funcionario.getComissaoDocumentoProduto();
                
            } else {
                valorComissao = mf.getSubtotal().multiply(funcionario.getComissaoDocumentoProduto().divide(new BigDecimal(100), 10, RoundingMode.HALF_UP));
            }
            
        } else {
            if (funcionario.getComissaoDocumentoServicoTipo().equals(TipoCalculoEnum.VALOR)) {
                valorComissao = funcionario.getComissaoDocumentoServico() ;
                
            } else {
                valorComissao = mf.getSubtotal().multiply(funcionario.getComissaoDocumentoServico().divide(new BigDecimal(100), 10, RoundingMode.HALF_UP));
            }
        }
        

        return valorComissao;

    }
    
    
    public static BigDecimal calcularComissaoItem(MovimentoFisico mf, Funcionario funcionario) {
        BigDecimal valorComissao;
        
        if (mf.getProdutoTipo().equals(ProdutoTipo.PRODUTO)) {
            if (funcionario.getComissaoItemProdutoTipo().equals(TipoCalculoEnum.VALOR)) {
                valorComissao = funcionario.getComissaoItemProduto() ;
                
            } else {
                valorComissao = mf.getSubtotal().multiply(funcionario.getComissaoItemProduto().divide(new BigDecimal(100), 10, RoundingMode.HALF_UP));
            }
            
        } else {
            if (funcionario.getComissaoItemServicoTipo().equals(TipoCalculoEnum.VALOR)) {
                valorComissao = funcionario.getComissaoItemServico() ;
                
            } else {
                valorComissao = mf.getSubtotal().multiply(funcionario.getComissaoItemServico().divide(new BigDecimal(100), 10, RoundingMode.HALF_UP));
            }
        }
        

        return valorComissao;

    }

    

}
