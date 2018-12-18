/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.principal;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ivand
 */
public class UsuarioDAOTest {
    
    public UsuarioDAOTest() {
    }

    @Test
    public void testBootstrap() {
        new UsuarioDAO().bootstrap();
    }
    
}
