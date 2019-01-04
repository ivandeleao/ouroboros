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
import model.bean.principal.ContaProgramadaView;
import org.junit.Test;
import static org.junit.Assert.*;
import util.DateTime;

/**
 *
 * @author ivand
 */
public class ContaPagarProgramadaDAOTest {
    
    public ContaPagarProgramadaDAOTest() {
    }

    @Test
    public void testSomeMethod() {
        
        
        
        LocalDate dataInicial = DateTime.fromStringDateLDT("01/07/2018");
        LocalDate dataFinal = DateTime.fromStringDateLDT("15/08/2018");
        
        List<ContaProgramadaView> cpvs = new ContaPagarViewDAO().findPorPeriodo(dataInicial, dataFinal);
        
        for(ContaProgramadaView cpv : cpvs) {
            System.out.println("conta: " + cpv.getContaProgramada().getNome());
            System.out.println("vencimento: " + cpv.getVencimento());
            System.out.println("valor: " + cpv.getContaProgramada().getValor());
        }
        
    }
    
}
