package printing.financeiro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.nosql.relatorio.CaixaItemReportBean;
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
public class CaixaPorPeriodoReport {

    public static void gerar(List<CaixaItem> itens, LocalDate dataInicial, LocalDate dataFinal, BigDecimal totalCredito, BigDecimal totalDebito, BigDecimal saldo) {
        try {
            if(itens.isEmpty()) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem registros para gerar o relatório", "Atenção", JOptionPane.WARNING_MESSAGE);
            
            } else {
            
                String relatorio = APP_PATH + "/reports/CaixaPorPeriodo.jasper";

                HashMap map = new HashMap();
                map.put("sistemaAssinatura", Ouroboros.SISTEMA_ASSINATURA);

                map.put("dataInicial", DateTime.toString(dataInicial));
                map.put("dataFinal", DateTime.toString(dataFinal));
                map.put("totalCredito", Decimal.toString(totalCredito));
                map.put("totalDebito", Decimal.toString(totalDebito));
                map.put("saldo", Decimal.toString(saldo));

                List<CaixaItemReportBean> elementos = new ArrayList<>();

                for (CaixaItem i : itens) {
                    CaixaItemReportBean elemento = new CaixaItemReportBean();

                    elemento.setId(i.getId().toString());
                    elemento.setData(DateTime.toString(i.getCriacao()));
                    elemento.setDescricao(i.getDescricao());
                    elemento.setObservacao(i.getObservacao());
                    elemento.setMeioPagamento(i.getMeioDePagamento().getSigla());
                    elemento.setCredito(Decimal.toString(i.getCredito()));
                    elemento.setDebito(Decimal.toString(i.getDebito()));
                    elemento.setSaldo(Decimal.toString(i.getSaldoAcumulado()));

                    elementos.add(elemento);
                }
                JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(elementos);

                JasperPrint jp = JasperFillManager.fillReport(relatorio, map, jr);

                JasperViewer jv = new JasperViewer(jp, false);

                jv.setTitle("Caixa por Período");

                jv.setVisible(true);
            }

        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório. " + e.getMessage());
        }
    }
    
}
