package printing.veiculo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.mysql.bean.principal.Veiculo;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.principal.VeiculoDAO;
import model.mysql.dao.principal.VendaDAO;
import model.nosql.relatorio.ProdutoReportBean;
import model.nosql.relatorio.DocumentoReportBean;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.APP_PATH;
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
public class VeiculoHistoricoReport {

    public static void gerar(Veiculo veiculo) {
        try {
            List<Venda> documentos = new VendaDAO().findByVeiculo(veiculo);
            
            String relatorio = APP_PATH + "\\reports\\VeiculoHistorico.jasper";

            HashMap map = new HashMap();
            map.put("sistemaAssinatura", Ouroboros.SISTEMA_ASSINATURA);
            
            map.put("sistemaAssinatura", Ouroboros.SISTEMA_ASSINATURA);
            map.put("veiculo", veiculo.getPlaca() + " " + veiculo.getModelo());
            map.put("totalProdutos", Decimal.toString(documentos.stream().map(Venda::getTotalProdutos).reduce(BigDecimal::add).get()));
            map.put("totalServicos", Decimal.toString(documentos.stream().map(Venda::getTotalServicos).reduce(BigDecimal::add).get()));
            map.put("total", Decimal.toString(documentos.stream().map(Venda::getTotal).reduce(BigDecimal::add).get()));

            List<DocumentoReportBean> elementos = new ArrayList<>();

            for (Venda v : documentos) {
                DocumentoReportBean elemento = new DocumentoReportBean();

                elemento.setId(v.getId().toString());
                elemento.setData(DateTime.toString(v.getDataHora()));
                elemento.setDescricao(v.getDescricaoConcatenada());
                elemento.setTotal(Decimal.toString(v.getTotal()));

                elementos.add(elemento);
            }
            JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(elementos);

            JasperPrint jp = JasperFillManager.fillReport(relatorio, map, jr);

            JasperViewer jv = new JasperViewer(jp, false);

            jv.setTitle("Histórico de Veículo");

            jv.setVisible(true);

        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório. " + e.getMessage());
        }
    }
    
}
