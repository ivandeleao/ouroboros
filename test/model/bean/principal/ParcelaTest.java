/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean.principal;

import model.mysql.bean.principal.Parcela;
import model.mysql.dao.principal.ParcelaDAO;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ivand
 */
public class ParcelaTest {
    
    public ParcelaTest() {
    }

    @Test
    public void testDiasEmAtraso() {
        Parcela p = new ParcelaDAO().findById(4);
        
        System.out.println("Valor: " + p.getValor());
        System.out.println("Vencimento: " + p.getVencimento());
        System.out.println("Dias em atraso: " + p.getDiasEmAtraso());
        System.out.println("Juros Calculado: " + p.getJurosCalculado());
        System.out.println("Multa Calculada: " + p.getMultaCalculada());
    }
    
}
