/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean.principal;

import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.catalogo.ProdutoComponente;
import model.mysql.bean.principal.catalogo.Produto;
import java.math.BigDecimal;
import java.util.List;
import model.mysql.dao.principal.catalogo.ProdutoComponenteDAO;
import model.mysql.dao.principal.catalogo.ProdutoDAO;
import org.junit.Test;
import static org.junit.Assert.*;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class ProdutoTest {
    
    public ProdutoTest() {
    }

    @Test
    public void produtoComBags() {
        ProdutoDAO pDAO = new ProdutoDAO();
        Produto produto = pDAO.findById(1);
        
        for(ProdutoComponente produtoComponente : produto.getListProdutoComponente()) {
            System.out.println("Componente: " + produtoComponente.getComponente().getNome());
            
            for(ProdutoComponente pcComponente : produtoComponente.getProduto().getListProdutoComponente()) {
                System.out.println("--->ComponenteDoComponente: " + pcComponente.getComponente().getNome());

            }
        }
        
        for(MovimentoFisico mf : produto.getListMovimentoFisico()) {
            System.out.println("MovimentoFisico: " + mf.getCriacao());
        }
        
        
    }
    
    
    public void testGetListProdutoComposto() {
        ProdutoDAO pDAO = new ProdutoDAO();
        Produto produto = pDAO.findById(260);
        List<Produto> listProduto = produto.getListProdutoComposto();
                
        for(Produto p : listProduto) {
            System.out.println("produto: " + p.getNome());
        }
        
    }
    
    
    public void testProdutoComponente() {
        
        ProdutoComponenteDAO pcDAO = new ProdutoComponenteDAO();
        
        ProdutoComponente pc = new ProdutoComponente();
        
        ProdutoDAO produtoDAO = new ProdutoDAO();
        
        Produto produto = produtoDAO.findById(253);
        
        Produto componente = produtoDAO.findById(225);
        
        pc.setProdutoId(produto.getId());
        pc.setQuantidade(BigDecimal.ONE);
        pc.setComponenteId(componente.getId());
        
        produto.getListProdutoComponente().add(pc);
        
        produtoDAO.save(produto);
        
        pcDAO.save(pc);
        
        //--
        
        Produto pBuscar = produtoDAO.findById(225);
        
        List<ProdutoComponente> listPc = pBuscar.getListProdutoComponente();
        
        for(ProdutoComponente c : listPc) {
            System.out.println("c: " + c.getComponenteId() + " - " + c.getQuantidade());
        }
        
        
        
    }
    
}
