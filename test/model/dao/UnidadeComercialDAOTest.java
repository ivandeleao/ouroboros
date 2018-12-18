/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import model.dao.fiscal.UnidadeComercialDAO;
import model.bean.fiscal.UnidadeComercial;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ivand
 */
public class UnidadeComercialDAOTest {
    
    public UnidadeComercialDAOTest() {
    }

    @Test
    public void getById() {
        UnidadeComercial unidadeComercial = new UnidadeComercial();
        
        UnidadeComercialDAO unidadeComercialDAO = new UnidadeComercialDAO();
        
        //unidadeComercial = unidadeComercialDAO.findById(1);
        
        //System.out.println("unidade comercial: " + unidadeComercial.getNome());
        
    }
    
    
    
}
