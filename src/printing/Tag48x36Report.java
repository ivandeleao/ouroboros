package printing;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.bean.principal.Parcela;
import model.bean.principal.Produto;
import model.bean.principal.Venda;
import model.bean.relatorio.NotaPromissoriaCampos;
import model.bean.relatorio.Tag48x36;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.APP_PATH;
import static ouroboros.Ouroboros.EMPRESA_CNPJ;
import static ouroboros.Ouroboros.EMPRESA_NOME_FANTASIA;
import static ouroboros.Ouroboros.EMPRESA_RAZAO_SOCIAL;
import util.DateTime;
import util.Decimal;
import util.MwString;

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
            
            List<Tag48x36> elementos = new ArrayList<>();
            
            
            for(Produto produto : produtos) {
                Tag48x36 elemento = new Tag48x36();
                
                elemento.setTitulo(EMPRESA_NOME_FANTASIA);
                elemento.setNome(produto.getNome());
                elemento.setCodigo(produto.getCodigo());
                
                String codigoPad = MwString.padLeft(produto.getCodigo(), 12).replace(" ", "-");
                
                elemento.setCodigoBarras("*" + codigoPad + "*");
                elemento.setValor(Decimal.toString(produto.getValorVenda()));

                elementos.add(elemento);
            }
            JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(elementos);
            
                            
            JasperPrint jp = JasperFillManager.fillReport(relatorio, mapa, jr);     

            JasperViewer jv = new JasperViewer(jp, false);    
            jv.setTitle("Tag48x36");  
                
            jv.setVisible(true);   
            
        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório. " + e.getMessage());
        }
    }
}
