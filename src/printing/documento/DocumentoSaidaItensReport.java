package printing.documento;

import java.awt.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.documento.VendaItemConsolidado;
import model.nosql.relatorio.VendaItemReportBean;
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
public class DocumentoSaidaItensReport {

    public static void gerar(List<VendaItemConsolidado> itensConsolidados, LocalDate dataInicial, LocalDate dataFinal) {
        try {
            if(itensConsolidados.isEmpty()) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem registros para gerar o relatório", "Atenção", JOptionPane.WARNING_MESSAGE);
            
            } else {
            
                String relatorio = APP_PATH + "\\reports\\DocumentoSaidaItens.jasper";

                HashMap map = new HashMap();
                map.put("sistemaAssinatura", Ouroboros.SISTEMA_ASSINATURA);

                map.put("sistemaAssinatura", Ouroboros.SISTEMA_ASSINATURA);
                map.put("dataInicial", DateTime.toString(dataInicial));
                map.put("dataFinal", DateTime.toString(dataFinal));
                map.put("total", Decimal.toString(itensConsolidados.stream().map(VendaItemConsolidado::getTotal).reduce(BigDecimal::add).get()));

                List<VendaItemReportBean> elementos = new ArrayList<>();

                for (VendaItemConsolidado i : itensConsolidados) {
                    VendaItemReportBean elemento = new VendaItemReportBean();

                    elemento.setCodigo(i.getProduto().getCodigo());
                    elemento.setDescricao(i.getProduto().getNome());
                    elemento.setQuantidade(Decimal.toString(i.getQuantidade(), 3));
                    elemento.setValorMedio(Decimal.toString(i.getValorMedio()));
                    elemento.setTotal(Decimal.toString(i.getTotal()));

                    elementos.add(elemento);
                }
                JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(elementos);

                JasperPrint jp = JasperFillManager.fillReport(relatorio, map, jr);

                JasperViewer jv = new JasperViewer(jp, false);

                jv.setTitle("Itens Vendidos");

                jv.setVisible(true);
            }

        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório. " + e.getMessage());
        }
    }
    
}
