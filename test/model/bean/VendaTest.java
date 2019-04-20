/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean;

import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.fiscal.MeioDePagamento;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import model.mysql.dao.principal.VendaDAO;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ivand
 */
public class VendaTest {
    
    public VendaTest() {
    }

    //@Test
    public void testGetTotalRecebido() {
        VendaDAO vendaDAO = new VendaDAO();
        Venda venda = vendaDAO.findById(157);
        
        System.out.println("totalRecebido: " + venda.getTotalRecebido());
        
    }
    
    //@Test
    public void testGetRecebimentosAgrupados(){
        Venda venda = new VendaDAO().findById(199);
        Map<MeioDePagamento, BigDecimal> mapConsolidado = venda.getRecebimentosAgrupadosPorMeioDePagamento();
        
        for(Map.Entry<MeioDePagamento, BigDecimal> consolidado : mapConsolidado.entrySet()){
            System.out.println("mp: " + consolidado.getKey().getNome() + " - " + consolidado.getValue());
        }
    }
    
    //@Test
    public void testGetRecebimentos(){
        Venda venda = new VendaDAO().findById(199);
        
        for(Parcela p : venda.getParcelas()){
            System.out.println("parcelas " + p.getValor());
        }
        
        
        List<CaixaItem> recebimentos = venda.getRecebimentos();
        
        for(CaixaItem r : recebimentos){
            System.out.println("recebimento: " + r.getMeioDePagamento().getNome() + " - " + r.getCredito());
        }
    }
    
    @Test
    public void testGetDescontoConsolidado(){
        Venda venda = new VendaDAO().findById(199);
        System.out.println("desconto total: " + venda.getDescontoConsolidado());
    }
    
}
