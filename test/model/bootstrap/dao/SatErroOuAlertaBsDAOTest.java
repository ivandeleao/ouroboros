/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bootstrap.dao;

import java.util.List;
import model.bootstrap.bean.SatErroOuAlertaBs;
import org.junit.Test;

/**
 *
 * @author ivand
 */
public class SatErroOuAlertaBsDAOTest {
    
    public SatErroOuAlertaBsDAOTest() {
    }

    @Test
    public void testFindAll() {
        List<SatErroOuAlertaBs> sList = new SatErroOuAlertaBsDAO().findAll();
        
        for(SatErroOuAlertaBs s : sList){
            System.out.println("s: " + s.getCodigo());
        }
    }
    
}
