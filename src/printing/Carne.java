package printing;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.bean.principal.MovimentoFisico;
import model.bean.principal.Parcela;
import model.bean.principal.Venda;
import model.bean.relatorio.CarneCampos;
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
                    itens += mf.getProduto().getNome();
                    if(venda.getMovimentosFisicosSaida().indexOf(mf) != venda.getMovimentosFisicosSaida().size() - 1) {
                        itens += ", ";
                    }
                }
                if(venda.getObservacao() != null && venda.getObservacao().length() > 0) {
                    itens += " |OBS: " + venda.getObservacao();
                }
                carne.setProduto(itens);
                carne.setValor(Decimal.toString(parcela.getValor()));
                carne.setVencimento(DateTime.toStringDate(parcela.getVencimento()));
                carne.setTelefone(Ouroboros.EMPRESA_TELEFONE);
                carne.setEndereco(Ouroboros.EMPRESA_ENDERECO);
                
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
