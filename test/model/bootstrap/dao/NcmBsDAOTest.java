/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bootstrap.dao;

import java.util.List;
import model.bootstrap.bean.NcmBs;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ivand
 */
public class NcmBsDAOTest {
    
    public NcmBsDAOTest() {
    }

    @Test
    public void testFindAll() {
        List<NcmBs> ncmBsList = new NcmBsDAO().findAll();
        
        for(NcmBs n : ncmBsList){
            System.out.println("ncm bs: " + n.getCodigo());
        }
        
    }
    
}
