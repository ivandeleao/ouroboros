/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bootstrap.dao;

import java.util.List;
import model.bootstrap.bean.IbptBs;
import model.dao.fiscal.IbptDAO;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ivand
 */
public class IbptBsDAOTest {
    
    public IbptBsDAOTest() {
    }

    //@Test
    public void testFindAll() {
        List<IbptBs> iList = new IbptBsDAO().findAll();
        
        for(IbptBs i : iList){
            System.out.println("ibpt codigo: " + i.getCodigo());
        }
    }
    
    @Test
    public void testBootstrap(){
        new IbptDAO().bootstrap();
    }
    
}
