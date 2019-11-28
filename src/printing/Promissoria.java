package printing;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.nosql.relatorio.NotaPromissoriaCampos;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.APP_PATH;
import static ouroboros.Ouroboros.EMPRESA_CNPJ;
import static ouroboros.Ouroboros.EMPRESA_RAZAO_SOCIAL;
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
public class Promissoria {
    public static void gerar(List<Parcela> parcelas){
        try {
            String relatorio = APP_PATH + "\\reports\\NotaPromissoria.jasper";
            
            HashMap mapa = new HashMap();
            
            List<NotaPromissoriaCampos> elementos = new ArrayList<>();
            
            
            // variaveis para o texto
            
            String nomeEmpresa = EMPRESA_RAZAO_SOCIAL;
            String cnpj = EMPRESA_CNPJ;
            
            
            for(Parcela parcela : parcelas) {
                Venda venda = parcela.getVenda();
                NotaPromissoriaCampos elemento = new NotaPromissoriaCampos();
                
                String vencimentoPorExtenso = DateTime.toStringDataPorExtenso(parcela.getVencimento());
                String valorExtenso = Decimal.porExtenso(parcela.getValor());

                elemento.setNumero(venda.getId() + "-" + parcela.getNumero());
                elemento.setTexto("Aos " + vencimentoPorExtenso + " pagarei por esta única via de NOTA PROMISSÓRIA a " + nomeEmpresa + ", CNPJ: " +
                        cnpj + ", ou a sua ordem, a quantia de " + valorExtenso + " em moeda corrente deste país.\n" + "\n");
                elemento.setNome(venda.getPessoa().getNome());
                System.out.println("endereço: " + venda.getPessoa().getEnderecoCompleto());
                elemento.setEndereco(venda.getPessoa().getEnderecoCompleto());
                elemento.setDocumento(venda.getPessoa().getCpfOuCnpj());
                elemento.setVencimento(DateTime.toString(parcela.getVencimento()));
                elemento.setValor(Decimal.toString(parcela.getValor()));
                elemento.setData(DateTime.toStringDataPorExtenso(LocalDate.now()));

                elementos.add(elemento);
            }
            JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(elementos);
            
                            
            JasperPrint jp = JasperFillManager.fillReport(relatorio, mapa, jr);     

            JasperViewer jv = new JasperViewer(jp, false);    
            jv.setTitle("NOTA PROMISSÓRIA");  
                
            jv.setVisible(true);   
            
        } catch (JRException e) {
            System.err.println("Erro ao gerar promissória. " + e.getMessage());
        }
    }
}
