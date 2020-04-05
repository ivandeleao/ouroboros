package printing.documento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.principal.VendaDAO;
import model.nosql.relatorio.CarneCampos;
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
public class CarnePrint {

    public static void gerarCarne(List<Parcela> parcelas) {
        try {
            String relatorio = APP_PATH + "\\reports\\Carne.jasper";

            HashMap mapa = new HashMap();

            List<CarneCampos> elementos = new ArrayList<>();

            for (Parcela parcela : parcelas) {
                Venda venda = new VendaDAO().findById(parcela.getVenda().getId()); //2020-02-27 para atualizar o objeto venda pendurado nesta parcela
                
                CarneCampos carne = new CarneCampos();
                carne.setId(venda.getId() + " - " + parcela.getNumero());
                carne.setNome(venda.getPessoa().getNome());

                String itens = "";
                for (MovimentoFisico mf : venda.getMovimentosFisicosSaida()) {
                    itens += Decimal.toString(mf.getSaida(), 0) + " x "
                            + mf.getDescricao() + " "
                            + Decimal.toString(mf.getValor());

                    if (mf.getAcrescimoConsolidado().compareTo(BigDecimal.ZERO) > 0) {
                        itens += " + " + mf.getAcrescimoFormatado();
                    }

                    if (mf.getDescontoConsolidado().compareTo(BigDecimal.ZERO) > 0) {
                        itens += " - " + mf.getDescontoFormatado();
                    }

                    itens += " = " + Decimal.toString(mf.getSubtotal());

                    if (venda.getMovimentosFisicosSaida().indexOf(mf) != venda.getMovimentosFisicosSaida().size() - 1) {
                        itens += "\r\n";
                    }
                }

                carne.setProduto(itens);

                //Observação----------------------------------------------------
                String observacao = "";
                BigDecimal totalVencido = venda.getPessoa().getTotalEmAtraso();
                if (totalVencido.compareTo(BigDecimal.ZERO) > 0) {
                    observacao = "ATENÇÃO: VALORES EM ATRASO " + Decimal.toString(totalVencido) + " (até a data de impressão deste carnê)\r\n";
                }

                if (venda.getObservacao() != null && venda.getObservacao().trim().length() > 0) {
                    observacao += "Obs: " + venda.getObservacao();
                }

                carne.setObservacao(observacao);
                //Fim Observação------------------------------------------------

                carne.setValor(Decimal.toString(parcela.getValor()));
                carne.setVencimento(DateTime.toStringDataAbreviada(parcela.getVencimento()));
                carne.setTelefone(Ouroboros.EMPRESA_TELEFONE);
                carne.setEndereco(Sistema.getEnderecoCompleto());

                String multaJuros = "Multa de " + Decimal.toString(parcela.getMulta()) + "% e juros de " + parcela.getJurosFormatado() + " ao mês";
                carne.setMultaJuros(multaJuros);

                elementos.add(carne);
            }

            //mapa.put("logo", APP_PATH + "\\reports\\LogoDanilaSabadini.png");
            mapa.put("logo", APP_PATH + "\\custom\\empresa_logo.jpg");

            JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(elementos);

            JasperPrint jp = JasperFillManager.fillReport(relatorio, mapa, jr);

            JasperViewer jv = new JasperViewer(jp, false);
            jv.setTitle("Carnê");

            jv.setVisible(true);

        } catch (JRException e) {
            e.printStackTrace();
        }
    }
}
