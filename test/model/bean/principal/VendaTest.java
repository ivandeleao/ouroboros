/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean.principal;

import java.util.List;
import model.dao.principal.MovimentoFisicoDAO;
import model.dao.principal.VendaDAO;
import org.junit.Test;

/**
 *
 * @author ivand
 */
public class VendaTest {
    
    public VendaTest() {
    }

    
    public void testGetListMovimentoFisicoEager() {
        VendaDAO vendaDAO = new VendaDAO();
        Venda venda = vendaDAO.findById(76);
        
        List<MovimentoFisico> itens = venda.getMovimentosFisicosSaida();
        
        //List<MovimentoFisico> itens = venda.getListMovimentoFisicoEager();
        /*
        for(MovimentoFisico item : itens) {
            System.out.println("Item: " + item.getId());
            System.out.println("  Produto: " + item.getProduto().getNome());
        }
        */
    }
    
    @Test
    public void testEntitiesMemory() {
        VendaDAO vendaDAO = new VendaDAO();
            
        List<Venda> vendas = vendaDAO.findAll();
        
        
        
    }
    
}
