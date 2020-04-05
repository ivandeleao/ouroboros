package printing.documento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.principal.VendaDAO;
import model.nosql.relatorio.BoletoReportBean;
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
public class BoletoPrint {

    public static void gerarBoleto(List<Parcela> parcelas) {
        try {
            String relatorio = APP_PATH + "\\reports\\BoletoSicrediCarne.jasper";

            HashMap mapa = new HashMap();
            
            mapa.put("logo", APP_PATH + "\\reports\\images\\sicredi-logo.png");
            
            
            List<BoletoReportBean> elementos = new ArrayList<>();

            for (Parcela parcela : parcelas) {
                Venda documento = new VendaDAO().findById(parcela.getVenda().getId()); //2020-02-27 para atualizar o objeto venda pendurado nesta parcela
                
                BoletoReportBean boleto = new BoletoReportBean();
                boleto.setAceite("N");
                boleto.setBancoNumero("748-X");
                boleto.setBeneficiario(Ouroboros.EMPRESA_RAZAO_SOCIAL);
                boleto.setBeneficiarioAgenciaCodigo("12345-6");
                boleto.setBeneficiarioCpfCnpj(Ouroboros.EMPRESA_CNPJ);
                String beneficiarioEndereco = Ouroboros.EMPRESA_ENDERECO + " " + Ouroboros.EMPRESA_ENDERECO_NUMERO + " " + Ouroboros.EMPRESA_ENDERECO_COMPLEMENTO;
                boleto.setBeneficiarioEndereco(beneficiarioEndereco);
                boleto.setCarteira("1");
                boleto.setDataProcessamento("30/03/2020");
                boleto.setDocumentoData("30/03/2020");
                boleto.setDocumentoEspecie("DM");
                boleto.setDocumentoNumero("99999");
                boleto.setDocumentoValor(Decimal.toString(parcela.getValor()));
                boleto.setInstrucoes("teste 123 instruções...");
                boleto.setLinhaDigitavel("03399.49281 36981.908811.02681 801029 2 00000000000000");
                boleto.setLocalPagamento("Pagável preferencialmente no banco bla bla");
                boleto.setMoedaEspecie("R$");
                boleto.setMoedaQuantidade("?");
                boleto.setNossoNumero(documento.getId() + " - " + parcela.getNumeroFormatado());
                boleto.setPagador(documento.getPessoa().getNome());
                boleto.setPagadorCpfCnpj(documento.getPessoa().getCpfOuCnpj());
                boleto.setSacadorAvalista("");
                boleto.setSacadorAvalistaCpfCnpj("");
                boleto.setVencimento(DateTime.toString(parcela.getVencimento()));
                
                boleto.setCodigoBarras("74894819400000090001120100082907188160840102");

                elementos.add(boleto);
            }

            

            JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(elementos);

            JasperPrint jp = JasperFillManager.fillReport(relatorio, mapa, jr);

            JasperViewer jv = new JasperViewer(jp, false);
            jv.setTitle("Boleto");

            jv.setVisible(true);

        } catch (JRException e) {
            e.printStackTrace();
        }
    }
}
