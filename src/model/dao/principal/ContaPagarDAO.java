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
import model.bean.principal.ContaProgramada;
import model.bean.principal.ContaProgramadaBaixa;
import model.bean.principal.ContaPagar;
import static ouroboros.Ouroboros.em;
import util.DateTime;

/**
 *
 * @author ivand
 */
public class ContaPagarDAO {

    /**
     *
     * @param dataInicial
     * @param dataFinal
     * @return view das contas programadas
     */
    public List<ContaPagar> findPorPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        List<ContaPagar> contasPagarView = new ArrayList<>();

        List<ContaProgramada> contasPagarProgramadas = new ContarProgramadaDAO().findPorPeriodo(dataInicial, dataFinal);

        for (LocalDate date = dataInicial; date.isBefore(dataFinal.plusDays(1)); date = date.plusDays(1)) {
            //System.out.println("////////////////////////////////////////////////////////////");
            //System.out.println("date: " + DateTime.toStringDate(date));

            for (ContaProgramada cPP : contasPagarProgramadas) {

                //System.out.println("cpp.getInicio: " + cPP.getInicio());
                //System.out.println("dia: " + Boolean.toString(cPP.getInicio().getDayOfMonth() == date.getDayOfMonth()));
                //System.out.println("mês inicio: " + Boolean.toString(date.getMonthValue() >= cPP.getInicio().getMonthValue()));
                //System.out.println("mês termino: " + Boolean.toString(date.getMonthValue() <= cPP.getTermino().getMonthValue()));
                //Se o dia (dd) for o mesmo e a data estiver no intervalo válido
                if ((cPP.getInicio().getDayOfMonth() == date.getDayOfMonth())
                        && date.isAfter(cPP.getInicio().minusDays(1))
                        && date.isBefore(cPP.getTermino().plusDays(1))) {
                    ContaPagar cPView = new ContaPagar();

                    cPView.setContaProgramada(cPP);
                    cPView.setVencimento(date); //usa a data do período
                    
                    for(ContaProgramadaBaixa baixa : cPP.getBaixas()) {
                        System.out.println("\t baixas: " + baixa.getVencimento());
                        System.out.println("\t date: " + date);
                        if(baixa.getVencimento().isEqual(date)) {
                            cPView.setContaProgramadaBaixa(baixa);
                        }
                    }

                    contasPagarView.add(cPView);

                    //System.out.println("\t conta: " + cPView.getNome());
                }

            }
        }
        return contasPagarView;
    }

}
