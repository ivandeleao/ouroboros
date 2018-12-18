/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.fiscal;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ivand
 */
public class IbptDAOTest {
    
    public IbptDAOTest() {
    }

    @Test
    public void testBootStrap() {
        
        new IbptDAO().bootstrap();
        
    }
    
}
