/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.persistence.Query;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.financeiro.ContaProgramada;
import model.mysql.bean.principal.financeiro.ContaProgramadaBaixa;
import model.mysql.bean.principal.financeiro.ContaPagar;
import model.mysql.bean.principal.documento.FinanceiroStatus;
import model.mysql.bean.principal.documento.Parcela;
import static ouroboros.Ouroboros.em;
import util.DateTime;

/**
 *
 * @author ivand
 */
public class ContaPagarDAO {

    public List<ContaPagar> findPorPeriodo(LocalDate dataInicial, LocalDate dataFinal, List<FinanceiroStatus> listStatus) {
        List<ContaPagar> contas = new ArrayList<>();
        
        contas.addAll(findPorPeriodoContasProgramadas(dataInicial, dataFinal, listStatus));
        
        contas.addAll(findPorPeriodoParcelas(dataInicial, dataFinal, listStatus));
        
        contas.sort(Comparator.comparing(ContaPagar::getVencimento));
        
        return contas;
    }
    
    
    /**
     *
     * @param dataInicial
     * @param dataFinal
     * @param listStatus
     * @return view das contas programadas
     */
    private List<ContaPagar> findPorPeriodoContasProgramadas(LocalDate dataInicial, LocalDate dataFinal, List<FinanceiroStatus> listStatus) {
        List<ContaPagar> contasPagar = new ArrayList<>();

        List<ContaProgramada> contasProgramadas = new ContaProgramadaDAO().findPorPeriodo(dataInicial, dataFinal);

        for (LocalDate date = dataInicial; date.isBefore(dataFinal.plusDays(1)); date = date.plusDays(1)) {
            //System.out.println("////////////////////////////////////////////////////////////");
            //System.out.println("date: " + DateTime.toStringDate(date));

            for (ContaProgramada contaProgramada : contasProgramadas) {

                //System.out.println("cpp.getInicio: " + contaProgramada.getInicio());
                //System.out.println("dia: " + Boolean.toString(contaProgramada.getInicio().getDayOfMonth() == date.getDayOfMonth()));
                //System.out.println("mês inicio: " + Boolean.toString(date.getMonthValue() >= contaProgramada.getInicio().getMonthValue()));
                //System.out.println("mês termino: " + Boolean.toString(date.getMonthValue() <= contaProgramada.getTermino().getMonthValue()));
                //Se o dia (dd) for o mesmo e a data estiver no intervalo válido
                if ((contaProgramada.getInicio().getDayOfMonth() == date.getDayOfMonth())
                        && date.isAfter(contaProgramada.getInicio().minusDays(1))
                        && date.isBefore(contaProgramada.getTermino().plusDays(1))) {
                    ContaPagar contaPagar = new ContaPagar();

                    contaPagar.setContaProgramada(contaProgramada);
                    contaPagar.setVencimento(date); //usa a data do período
                    
                    //Adicionar informações da baixa
                    for(ContaProgramadaBaixa baixa : contaProgramada.getBaixas()) {
                        System.out.println("\t baixas: " + baixa.getVencimento());
                        System.out.println("\t date: " + date);
                        if(baixa.getVencimento().isEqual(date)) {
                            contaPagar.setContaProgramadaBaixa(baixa);
                        }
                    }
                    
                    //Filtrar pelo status
                    if(listStatus == null || listStatus.contains(contaPagar.getStatus())) {
                        contasPagar.add(contaPagar);
                    }

                    //System.out.println("\t conta: " + contaPagar.getNome());
                }

            }
        }
        return contasPagar;
    }
    
    
    private List<ContaPagar> findPorPeriodoParcelas(LocalDate dataInicial, LocalDate dataFinal, List<FinanceiroStatus> listStatus) {
        List<ContaPagar> contasPagar = new ArrayList<>();
        
        ParcelaDAO parcelaDAO = new ParcelaDAO();
        List<Parcela> parcelas = parcelaDAO.findPorData(dataInicial, dataFinal, TipoOperacao.ENTRADA);
        
        for(Parcela parcela : parcelas) {
            ContaPagar contaPagar = new ContaPagar();
            
            //LocalDateTime dataPagamento = p.getUltimoRecebimento();
            
            contaPagar.setParcela(parcela);
            
            if(listStatus == null || listStatus.contains(contaPagar.getStatus())) {
                contasPagar.add(contaPagar);
            }
        }
        
        return contasPagar;
    }

}
