package printing.documento;

import boleto.Boleto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.financeiro.Conta;
import model.mysql.dao.principal.ParcelaDAO;
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
public class BoletoPrint {

    public static void gerarBoleto(Conta conta, List<Parcela> parcelas) {
        try {
            ParcelaDAO parcelaDAO = new ParcelaDAO();
            
            String relatorio = APP_PATH + "\\reports\\BoletoSicrediCarne.jasper";

            HashMap mapa = new HashMap();

            mapa.put("logo", APP_PATH + "\\reports\\images\\sicredi-logo.png");

            List<BoletoReportBean> elementos = new ArrayList<>();

            for (Parcela parcela : parcelas) {
                Venda documento = new VendaDAO().findById(parcela.getVenda().getId()); //2020-02-27 para atualizar o objeto venda pendurado nesta parcela

                parcela = Boleto.prepararBoleto(conta, parcela);
                //String vencimento = Texto.soNumeros(DateTime.toString(parcela.getVencimento())); //sem barras
                //String valor = Texto.soNumeros(Decimal.toString(parcela.getValor())); //sem vírgula

                String agencia = conta.getAgencia();
                String posto = conta.getPosto();
                String contaCorrente = conta.getContaCorrente();
                String ano = parcela.getBoletoAno();
                String b = parcela.getBoletoByte();

                String sequencial = parcela.getBoletoSequencial();
                String dv = parcela.getBoletoDv();
                String nossoNumero = Boleto.gerarNossoNumero(ano, b, sequencial, dv);

                String codigoBarras = parcela.getBoletoCodigoBarras();

                BoletoReportBean boleto = new BoletoReportBean();
                boleto.setAceite("N");
                boleto.setBancoNumero("748-X");
                boleto.setBeneficiario(Ouroboros.EMPRESA_RAZAO_SOCIAL);
                boleto.setBeneficiarioAgenciaCodigo(agencia + "." + posto + "." + contaCorrente);
                boleto.setBeneficiarioCpfCnpj(Ouroboros.EMPRESA_CNPJ);
                String beneficiarioEndereco = Ouroboros.EMPRESA_ENDERECO + " " + Ouroboros.EMPRESA_ENDERECO_NUMERO + " " + Ouroboros.EMPRESA_ENDERECO_COMPLEMENTO;
                boleto.setBeneficiarioEndereco(beneficiarioEndereco);
                boleto.setCarteira("1");
                boleto.setDataProcessamento(DateTime.toString(LocalDate.now()));
                boleto.setDocumentoData(DateTime.toStringDate(parcela.getCriacao()));
                boleto.setDocumentoEspecie("DMI");
                boleto.setDocumentoNumero(documento.getId().toString());
                boleto.setDocumentoValor(Decimal.toString(parcela.getValor()));
                boleto.setInstrucoes(montarInstrucoes(parcela));
                boleto.setLinhaDigitavel(Boleto.gerarLinhaDigitavel(codigoBarras));
                boleto.setLocalPagamento("PAGÁVEL PREFERENCIALMENTE EM CANAIS DA SUA INSTITUIÇÃO FINANCEIRA");
                boleto.setMoedaEspecie("R$");
                boleto.setMoedaQuantidade("");
                boleto.setNossoNumero(Boleto.formatarNossoNumero(nossoNumero));
                boleto.setPagadorNome(documento.getPessoa().getNome());
                boleto.setPagadorCpfCnpj(documento.getPessoa().getCpfOuCnpj());
                boleto.setPagadorEndereco(documento.getPessoa().getEnderecoCompleto());
                boleto.setSacadorAvalista("");
                boleto.setSacadorAvalistaCpfCnpj("");
                boleto.setVencimento(DateTime.toString(parcela.getVencimento()));

                boleto.setCodigoBarras(codigoBarras);

                elementos.add(boleto);
                
                parcela.setBoletoImpressao(LocalDateTime.now());
                parcelaDAO.save(parcela);
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

    private static String montarInstrucoes(Parcela parcela) {
        String instrucoes = "";

        if (parcela.getMulta().compareTo(BigDecimal.ZERO) > 0) {
            instrucoes += "APÓS VENCIMENTO COBRAR MULTA DE "
                    + Decimal.toStringDescarteDecimais(parcela.getMulta()) + "%"
                    + System.lineSeparator();
        }

        if (parcela.getJuros().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal juros = parcela.getJurosEmMonetario().divide(new BigDecimal(30), 10, RoundingMode.HALF_UP);
            instrucoes += "APÓS VENCIMENTO COBRAR MORA DIÁRIA DE R$"
                    + Decimal.toStringDescarteDecimais(juros, 2) //mês comercial = 30 dias
                    + System.lineSeparator();
        }

        return instrucoes;
    }

}
