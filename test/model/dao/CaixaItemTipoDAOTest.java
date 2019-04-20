/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import model.mysql.dao.principal.CaixaItemDAO;
import java.util.List;
import model.mysql.bean.principal.financeiro.CaixaItem;
import org.junit.Test;
import static org.junit.Assert.*;
import util.DateTime;

/**
 *
 * @author ivand
 */
public class CaixaItemTipoDAOTest {
    
    public CaixaItemTipoDAOTest() {
    }

    @Test
    public void testFindAll() {
        CaixaItemDAO ciDAO = new CaixaItemDAO();
        List<CaixaItem> cis = ciDAO.findByCriteria(DateTime.fromString("01/01/2018"), DateTime.getNow());
        
        for(CaixaItem ci : cis){
            System.out.println("ci: " + ci.getCriacao() + " - " + ci.getCredito());
        }
    }
    
}
