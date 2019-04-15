/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean;

import model.mysql.bean.principal.Venda;
import model.mysql.bean.principal.Parcela;
import java.math.BigDecimal;
import model.mysql.dao.principal.ParcelaDAO;
import model.mysql.dao.principal.VendaDAO;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ivand
 */
public class ParcelaTest {
    
    public ParcelaTest() {
    }

    //@Test
    public void testGetRecebido() {
        ParcelaDAO parcelaDAO = new ParcelaDAO();
        Parcela parcela = parcelaDAO.findById(21);
        
        BigDecimal recebido = parcela.getValorQuitado();
        
        System.out.println("recebido: " + recebido);
    }
    
    @Test
    public void testGetTroco(){
        Venda venda = new VendaDAO().findById(200);
        System.out.println("troco: " + venda.getTroco());
    }
    
}
