/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.principal;

import java.util.List;
import model.bean.principal.Pessoa;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ivand
 */
public class PessoaDAOTest {
    
    public PessoaDAOTest() {
    }

    @Test
    public void testFindAll() {
        
        List<Pessoa> listPessoa = new PessoaDAO().findAll();
        
        for(Pessoa p : listPessoa) {
            System.out.println("pessoa: " + p.getNome());
        }
    }
    
}
