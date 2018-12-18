/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.fiscal;

import java.util.List;
import model.bean.fiscal.Ncm;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ivand
 */
public class NcmDAOTest {
    
    public NcmDAOTest() {
    }

    //@Test
    public void testFindByCodigo() {
        Ncm ncm = new NcmDAO().findByCodigo("01012100");
        
        System.out.println("ncm: " + ncm.getCodigo());
    }
    
    //@Test
    public void testFindAll(){
        List<Ncm> ncmList = new NcmDAO().findAll();
        for(Ncm ncm : ncmList){
            System.out.println("ncm: " + ncm.getCodigo() + " " + ncm.getDescricao());
        }
    }
    
    @Test
    public void testBootstrap(){
        new NcmDAO().bootstrap();
    }
    
}
