/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.principal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import model.bean.principal.Produto;
import model.bean.principal.VendaCategoriaConsolidado;
import model.bean.principal.MovimentoFisico;
import model.bean.principal.VendaItemConsolidado;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ivand
 */
public class VendaDAOTest {
    
    public VendaDAOTest() {
    }

    
    public void testFindItensConsolidado() {
        List<VendaItemConsolidado> itensConsolidados = new VendaDAO().findItensConsolidado(null, null);
        
        for (VendaItemConsolidado c : itensConsolidados) {
            String codigo = c.getProduto().getCodigo();
            String nome = c.getProduto().getNome();
            BigDecimal quantidade = c.getQuantidade();
            BigDecimal total = c.getTotal();
            System.out.println("produto: " + codigo + " - " + nome + " qtd: " + quantidade + " total: " + total);
        }
    }
    
    @Test
    public void testfindVendasConsolidadasPorCategoria() {
        List<VendaCategoriaConsolidado> itens = new VendaDAO().findVendasConsolidadasPorCategoria(null, null);
        
        for(VendaCategoriaConsolidado c: itens) {
            System.out.println(
                    c.getCategoria() + " - " +
                    c.getTotalBruto()+ " - " +
                    c.getTotalLiquido()
            );
        }
        
        
        
    }
    
}
