package printing.financeiro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.nosql.relatorio.ContaReceberReportBean;
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
public class ContasReceberReport {

    public static void gerar(List<Parcela> itens, String status, LocalDate dataInicial, LocalDate dataFinal, BigDecimal total, BigDecimal totalRecebido, BigDecimal totalAtualizado, Pessoa pessoa) {
        try {
            if(itens.isEmpty()) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem registros para gerar o relatório", "Atenção", JOptionPane.WARNING_MESSAGE);
            
            } else {
            
                String relatorio = APP_PATH + "/reports/ContasReceber.jasper";

                HashMap map = new HashMap();
                map.put("sistemaAssinatura", Ouroboros.SISTEMA_ASSINATURA);

                map.put("status", status);
                map.put("dataInicial", DateTime.toString(dataInicial));
                map.put("dataFinal", DateTime.toString(dataFinal));
                if (pessoa != null) {
                    map.put("pessoaNome", pessoa.getId() + " - " + pessoa.getNome());
                }
                map.put("total", Decimal.toString(total));
                map.put("totalRecebido", Decimal.toString(totalRecebido));
                map.put("totalAtualizado", Decimal.toString(totalAtualizado));

                List<ContaReceberReportBean> elementos = new ArrayList<>();

                for (Parcela i : itens) {
                    ContaReceberReportBean elemento = new ContaReceberReportBean();

                    elemento.setStatus(i.getStatus().toString());
                    elemento.setVencimento(DateTime.toStringDataAbreviada(i.getVencimento()));
                    elemento.setDescricao(i.getDescricao());
                    elemento.setValor(Decimal.toString(i.getValor()));
                    elemento.setDataRecebimento(DateTime.toStringDataAbreviada(i.getUltimoRecebimento()));
                    elemento.setValorRecebido(Decimal.toString(i.getValorQuitado()));
                    elemento.setMeioPagamento(i.getMeioDePagamento().getSigla());
                    elemento.setObservacao("-");

                    elementos.add(elemento);
                }
                JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(elementos);

                JasperPrint jp = JasperFillManager.fillReport(relatorio, map, jr);

                JasperViewer jv = new JasperViewer(jp, false);

                jv.setTitle("Contas a Receber");

                jv.setVisible(true);
            }

        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório. " + e.getMessage());
        }
    }
    
}
