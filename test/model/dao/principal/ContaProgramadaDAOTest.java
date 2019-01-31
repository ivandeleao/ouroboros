/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.principal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import model.bean.fiscal.MeioDePagamento;
import model.bean.principal.Caixa;
import model.bean.principal.CaixaItem;
import model.bean.principal.CaixaItemTipo;
import model.bean.principal.ContaProgramada;
import model.bean.principal.ContaPagar;
import org.junit.Test;
import static org.junit.Assert.*;
import util.DateTime;

/**
 *
 * @author ivand
 */
public class ContaProgramadaDAOTest {
    
    public ContaProgramadaDAOTest() {
    }

    @Test
    public void testSomeMethod() {
        LocalDate dataInicial = DateTime.fromStringDateLDT("10/01/2018");
        LocalDate dataFinal = DateTime.fromStringDateLDT("10/03/2019");
        
        List<ContaPagar> cpvs = new ContaPagarDAO().findPorPeriodo(dataInicial, dataFinal, null);
        System.out.println("----------------------------------------------------**");
        for(ContaPagar cpv : cpvs) {
            System.out.println("x conta: " + cpv.getContaProgramada().getNome());
            System.out.println("x vencimento: " + cpv.getVencimento());
            System.out.println("x valor: " + cpv.getContaProgramada().getValor());
            System.out.println("x baixa: " + Boolean.toString(cpv.getContaProgramadaBaixa() != null));
            System.out.println("x data da baixa: " + cpv.getContaProgramadaBaixa().getCaixaItem().getCriacao());
            System.out.println("------------------------");
        }
        
    }
    
}
