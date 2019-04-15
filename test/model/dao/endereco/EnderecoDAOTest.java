/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.endereco;

import model.mysql.dao.endereco.EnderecoDAO;
import model.mysql.bean.endereco.Endereco;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ivand
 */
public class EnderecoDAOTest {
    
    public EnderecoDAOTest() {
    }

    @Test
    public void testFindByCep() {
        EnderecoDAO eDAO = new EnderecoDAO();
        Endereco e = eDAO.findByCep("01003000");
        
        System.out.println("endereço: " + e.getCep() + " " + e.getEndereco());
    }
    
}
