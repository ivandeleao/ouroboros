package printing.catalogo;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.mysql.bean.principal.catalogo.Produto;
import model.nosql.relatorio.ProdutoEtiquetaReportBean;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import static ouroboros.Ouroboros.APP_PATH;
import static ouroboros.Ouroboros.EMPRESA_NOME_FANTASIA;
import util.Decimal;
import util.Texto;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author User
 */
public class Tag48x36Report {
    public static void gerar(List<Produto> produtos){
        try {
            String relatorio = APP_PATH + "\\reports\\Tag48x36.jasper";
            
            HashMap mapa = new HashMap();
            
            List<ProdutoEtiquetaReportBean> elementos = new ArrayList<>();
            
            
            for(Produto produto : produtos) {
                ProdutoEtiquetaReportBean elemento = new ProdutoEtiquetaReportBean();
                
                elemento.setTitulo(EMPRESA_NOME_FANTASIA);
                elemento.setDescricao(produto.getNome());
                elemento.setCodigo(produto.getCodigo());
                
                String codigoPad = Texto.padLeftAndCut(produto.getCodigo(), 12).replace(" ", "_");
                
                elemento.setCodigoBarras("*" + codigoPad + "*");
                elemento.setValor(Decimal.toString(produto.getValorVenda()));

                elementos.add(elemento);
            }
            JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(elementos);
            
                            
            JasperPrint jp = JasperFillManager.fillReport(relatorio, mapa, jr);     

            JasperViewer jv = new JasperViewer(jp, false);    
                
            jv.setVisible(true);   
            
        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório. " + e.getMessage());
        }
    }
}
