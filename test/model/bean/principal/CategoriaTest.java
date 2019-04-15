/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean.principal;

import model.mysql.bean.principal.Categoria;
import model.mysql.bean.principal.catalogo.Produto;
import java.util.List;
import model.mysql.dao.principal.CategoriaDAO;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ivand
 */
public class CategoriaTest {
    
    public CategoriaTest() {
    }

    @Test
    public void testGetProdutoList() {
        Categoria c = new CategoriaDAO().findById(1);

        System.out.println("Categoria: " + c.getNome());
        
        List<Produto> produtoList = c.getProdutoList();
        
        for(Produto p : produtoList) {
            System.out.println("produto: " + p.getNome());
        }
    }
    
}
