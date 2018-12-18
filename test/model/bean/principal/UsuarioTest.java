/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean.principal;

import java.util.Set;
import model.dao.principal.DiretivaDAO;
import model.dao.principal.UsuarioDAO;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ivand
 */
public class UsuarioTest {
    
    public UsuarioTest() {
    }

    //@Test
    public void testDiretivas() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.findById(1);
        
        Set<Diretiva> setDiretiva = usuario.getDiretivas();
        
        Diretiva diretiva = setDiretiva.iterator().next();
        diretiva.setStatus(DiretivaStatus.BLOQUEADO);
        
        DiretivaDAO diretivaDAO = new DiretivaDAO();
        diretivaDAO.save(diretiva);
        
    }
    
    @Test
    public void testAddDiretiva() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.findById(1);
        
        usuario.normalizarDiretivas();
        
        //usuario.addDiretiva(Recurso.PRODUTOS, DiretivaStatus.SUPERVISOR);
        
        usuarioDAO.save(usuario);
        
        
    }
    
}
