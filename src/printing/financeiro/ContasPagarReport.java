package printing.financeiro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.financeiro.ContaPagar;
import model.nosql.relatorio.ContaPagarReportBean;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.APP_PATH;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;
import util.Decimal;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author User
 */
public class ContasPagarReport {

    public static void gerar(List<ContaPagar> itens, String status, LocalDate dataInicial, LocalDate dataFinal, BigDecimal total, BigDecimal totalPago, BigDecimal totalPagar) {
        try {
            if(itens.isEmpty()) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem registros para gerar o relatório", "Atenção", JOptionPane.WARNING_MESSAGE);
            
            } else {
            
                String relatorio = APP_PATH + "/reports/ContasPagar.jasper";

                HashMap map = new HashMap();
                map.put("sistemaAssinatura", Ouroboros.SISTEMA_ASSINATURA);

                map.put("status", status);
                map.put("dataInicial", DateTime.toString(dataInicial));
                map.put("dataFinal", DateTime.toString(dataFinal));
                map.put("total", Decimal.toString(total));
                map.put("totalPago", Decimal.toString(totalPago));
                map.put("totalPagar", Decimal.toString(totalPagar));

                List<ContaPagarReportBean> elementos = new ArrayList<>();

                for (ContaPagar i : itens) {
                    ContaPagarReportBean elemento = new ContaPagarReportBean();

                    elemento.setStatus(i.getStatus().toString());
                    elemento.setVencimento(DateTime.toStringDataAbreviada(i.getVencimento()));
                    elemento.setDescricao(i.getDescricao());
                    elemento.setValor(Decimal.toString(i.getValor()));
                    elemento.setDataPagamento(DateTime.toStringDataAbreviada(i.getDataPago()));
                    elemento.setValorPago(Decimal.toString(i.getValorPago()));
                    elemento.setMeioPagamento(i.getMeioDePagamento() != null ? i.getMeioDePagamento().getSigla() : "");
                    elemento.setObservacao(i.getObservacao());

                    elementos.add(elemento);
                }
                JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(elementos);

                JasperPrint jp = JasperFillManager.fillReport(relatorio, map, jr);

                JasperViewer jv = new JasperViewer(jp, false);

                jv.setTitle("Contas a Pagar");

                jv.setVisible(true);
            }

        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório. " + e.getMessage());
        }
    }
    
}
