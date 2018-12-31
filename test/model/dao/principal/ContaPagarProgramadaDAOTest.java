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
import model.bean.principal.ContaPagarProgramada;
import model.bean.principal.ContaPagarView;
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
        ContaPagarProgramada cpp = new ContaPagarProgramada();
        ContaPagarProgramadaDAO cppDAO = new ContaPagarProgramadaDAO();
        /*
        cpp.setNome("aluguel");
        cpp.setVencimento(LocalDate.now());
        cpp.setValor(BigDecimal.TEN);
        
        cppDAO.save(cpp);
        */
        
        LocalDate dataInicial = DateTime.fromStringDateLDT("01/07/2018");
        LocalDate dataFinal = DateTime.fromStringDateLDT("15/08/2018");
        
        List<ContaPagarView> cpvs = new ContaPagarViewDAO().findPorPeriodo(dataInicial, dataFinal);
        
        for(ContaPagarView cpv : cpvs) {
            System.out.println("conta: " + cpv.getNome());
            System.out.println("vencimento: " + cpv.getVencimento());
            System.out.println("valor: " + cpv.getValor());
        }
        
    }
    
}
