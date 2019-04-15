package printing;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.Parcela;
import model.mysql.bean.principal.Venda;
import model.mysql.bean.relatorio.CarneCampos;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.APP_PATH;
import util.DateTime;
import util.Decimal;
import util.Sistema;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author User
 */
public class Carne {
    public static void gerarCarne(List<Parcela> parcelas){
        try{
            String relatorio = APP_PATH + "\\reports\\DanilaCarne.jasper"; 

            HashMap mapa = new HashMap();

            List<CarneCampos> elementos = new ArrayList<>();

            for(Parcela parcela : parcelas) {
                Venda venda = parcela.getVenda();
                CarneCampos carne = new CarneCampos();
                carne.setId(venda.getId() + " - " + parcela.getNumero());
                carne.setNome(venda.getPessoa().getNome());
                
                String itens = "";
                for(MovimentoFisico mf : venda.getMovimentosFisicosSaida()) {
                    itens += mf.getProduto().getNome() + " " +
                            Decimal.toString(mf.getValor());
                    
                    if(mf.getDescontoPercentual().compareTo(BigDecimal.ZERO) > 0) {
                        itens += " - " + Decimal.toString(mf.getDescontoPercentual()) + "% = " +
                                Decimal.toString(mf.getSubtotal());
                    }
                    
                    if(venda.getMovimentosFisicosSaida().indexOf(mf) != venda.getMovimentosFisicosSaida().size() - 1) {
                        itens += ", ";
                    }
                }
                
                carne.setProduto(itens);
                if(venda.getObservacao() != null && venda.getObservacao().trim().length() > 0) {
                    carne.setObservacao("Obs: " + venda.getObservacao());
                } else {
                    carne.setObservacao("");
                }
                
                carne.setValor(Decimal.toString(parcela.getValor()));
                carne.setVencimento(DateTime.toStringDate(parcela.getVencimento()));
                carne.setTelefone(Ouroboros.EMPRESA_TELEFONE);
                carne.setEndereco(Sistema.getEnderecoCompleto());
                
                String multaJuros = "Multa de " + Decimal.toString(parcela.getMulta()) + "% e juros de " + parcela.getJurosFormatado() + " ao mês";
                carne.setMultaJuros(multaJuros);

                elementos.add(carne);
            }
            
            mapa.put("logo", APP_PATH + "\\reports\\LogoDanilaSabadini.png");

            JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(elementos);


                JasperPrint jp = JasperFillManager.fillReport(relatorio, mapa, jr);     

                JasperViewer jv = new JasperViewer(jp, false);    
                jv.setTitle("Carnê");  

                jv.setVisible(true);   
        } catch(JRException e){
            e.printStackTrace();
        }
    }
}
