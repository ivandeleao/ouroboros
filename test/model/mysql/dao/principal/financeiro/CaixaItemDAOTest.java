/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.financeiro;

import java.util.List;
import model.mysql.bean.principal.financeiro.Caixa;
import model.mysql.bean.principal.financeiro.CaixaItem;
import org.junit.Test;
import static org.junit.Assert.*;
import ouroboros.Ouroboros;

/**
 *
 * @author ivand
 */
public class CaixaItemDAOTest {
    
    public CaixaItemDAOTest() {
        Ouroboros.SERVER = "192.168.0.15";
        Caixa caixa = new CaixaDAO().findById(1);
        CaixaItemDAO caixaItemDAO = new CaixaItemDAO();
        List<CaixaItem> caixaItens = caixaItemDAO.findByCaixa(caixa, null, null);
    }

    @Test
    public void testSomeMethod() {
    }
    
}
