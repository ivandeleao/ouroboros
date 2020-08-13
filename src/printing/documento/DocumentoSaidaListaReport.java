package printing.documento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.financeiro.ContaPagar;
import model.nosql.relatorio.ContaPagarReportBean;
import model.nosql.relatorio.DocumentoSaidaReportBean;
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
public class DocumentoSaidaListaReport {

    public static void gerar(List<Venda> itens, String status, String nfse, String sat, String nfe, String dataInicial, String dataFinal, String pessoa, String veiculo, String funcionario, String cancelados, String originais, BigDecimal total) {
        try {
            if(itens.isEmpty()) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem registros para gerar o relatório", "Atenção", JOptionPane.WARNING_MESSAGE);
            
            } else {
            
                String relatorio = APP_PATH + "/reports/DocumentoSaidaLista.jasper";

                HashMap map = new HashMap();
                map.put("sistemaAssinatura", Ouroboros.SISTEMA_ASSINATURA);

                
                map.put("dataInicial", dataInicial);
                map.put("dataFinal", dataFinal);
                map.put("status", status);
                map.put("nfse", nfse);
                map.put("sat", sat);
                map.put("nfe", nfe);
                
                map.put("cliente", pessoa);
                map.put("veiculo", veiculo);
                map.put("funcionario", funcionario);
                
                map.put("cancelados", cancelados);
                map.put("originais", originais);
                
                map.put("total", Decimal.toString(total));

                List<DocumentoSaidaReportBean> elementos = new ArrayList<>();

                for (Venda i : itens) {
                    DocumentoSaidaReportBean elemento = new DocumentoSaidaReportBean();

                    elemento.setId(i.getId().toString());
                    elemento.setData(DateTime.toString(i.getDataHora()));
                    String pessoaNome = i.getPessoa() != null ? i.getPessoa().getNome() : "--NÃO INFORMADO--";
                    elemento.setCliente(pessoaNome);
                    String funcionarioNome = i.getFuncionario()!= null ? i.getFuncionario().getNome() : "--NÃO INFORMADO--";
                    elemento.setFuncionario(funcionarioNome);
                    elemento.setTotal(Decimal.toString(i.getTotal()));

                    elementos.add(elemento);
                }
                JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(elementos);

                JasperPrint jp = JasperFillManager.fillReport(relatorio, map, jr);

                JasperViewer jv = new JasperViewer(jp, false);

                jv.setTitle("Documentos de Saída");

                jv.setVisible(true);
            }

        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório. " + e.getMessage());
        }
    }
    
}
