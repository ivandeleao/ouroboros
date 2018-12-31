/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.principal;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import model.bean.principal.ContaPagarProgramada;
import model.bean.principal.ContaPagarView;
import static ouroboros.Ouroboros.em;
import util.DateTime;

/**
 *
 * @author ivand
 */
public class ContaPagarViewDAO {

    public List<ContaPagarView> findPorPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        List<ContaPagarView> contasPagarView = new ArrayList<>();
        
        List<ContaPagarProgramada> contasPagarProgramadas = new ContaPagarProgramadaDAO().findPorPeriodo(dataInicial, dataFinal);
        
        
        for(LocalDate date = dataInicial; date.isBefore(dataFinal.plusDays(1)); date = date.plusDays(1)) {
            //System.out.println("date: " + DateTime.toStringDate(date));
        
            for(ContaPagarProgramada cPP : contasPagarProgramadas) {
                
                if(cPP.getInicio().getDayOfMonth() == date.getDayOfMonth()) {
                    ContaPagarView cPView = new ContaPagarView();

                    cPView.setId(cPP.getId());
                    cPView.setNome(cPP.getNome());
                    cPView.setValor(cPP.getValor());
                    cPView.setVencimento(date); //usa a data do período

                    contasPagarView.add(cPView);
                    
                    //System.out.println("\t conta: " + cPView.getNome());
                }
                

            }
        }
        return contasPagarView;
    }
    
}
