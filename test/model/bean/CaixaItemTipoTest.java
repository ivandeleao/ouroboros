/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean;

import model.bean.principal.CaixaItemTipo;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ivand
 */
public class CaixaItemTipoTest {
    
    public CaixaItemTipoTest() {
    }

    @Test
    public void testConstant() {
        System.out.println("recebimento de venda: " + CaixaItemTipo.RECEBIMENTO_DE_VENDA.getNome());
    }
    
}
