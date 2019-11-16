package printing;

import br.com.swconsultoria.nfe.schema_4.enviNFe.TEnviNFe;
import br.com.swconsultoria.nfe.util.XmlNfeUtil;
import com.itextpdf.text.Element;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.relatorio.CaixaPeriodoPorMeioDePagamentoReport;
import model.mysql.bean.relatorio.MovimentoFisicoToStringAdapter;
import model.mysql.bean.relatorio.ParcelaDanfeAdapter;
import model.mysql.bean.relatorio.ParcelaToStringAdapter;
import model.mysql.dao.principal.VendaDAO;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import nfe.NfeLerXml;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.APP_PATH;
import util.DateTime;
import util.Decimal;
import util.MwXML;
import util.Sistema;

public class DanfePrint {

    public static void xmlToDanfe(String caminhoXml) {
        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = dbf.newDocumentBuilder();
            File f = new File(caminhoXml);
            Document doc = builder.parse(f);

            //String teste = MwXML.getValorPorCaminho(doc, "infNFe,prod,cProd");
            String valor = MwXML.getAttributeValue(doc, "det", "nItem");

            System.out.println("valor teste attr: " + valor);

            Map<String, Entry<String, String>> tags = new LinkedHashMap<>();

            tags.put("infNFe", new SimpleEntry(null, null));
            tags.put("det", new SimpleEntry("nItem", "1"));
            tags.put("prod", new SimpleEntry(null, null));
            tags.put("cProd", new SimpleEntry(null, null));

            ////String teste = MwXML.getValorPorCaminho(doc, tags);
            ////System.out.println("valor: " + teste);
            //Venda venda = new VendaDAO().findById(520);

            String relatorio = (APP_PATH + "\\reports\\Danfe.jasper");


            HashMap map = new HashMap();
            map.put("numero", MwXML.getValue(doc, "nNF"));
            map.put("serie", MwXML.getValue(doc, "serie"));
            map.put("tipoOperacao", MwXML.getValue(doc, "tpNF"));
            
            String chave = MwXML.getValue(doc, "chNFe");
            map.put("chaveAcesso", chave);
            map.put("chaveAcessoFormatada", String.join(" ", chave.split("(?<=\\G.{4})")));

            map.put("naturezaOperacao", MwXML.getValue(doc, "natOp"));
            String protocoloAutorizacao = MwXML.getValue(doc, "nProt") + " - " + 
                    DateTime.toStringDate(DateTime.fromStringToLDTOffsetZone(MwXML.getValue(doc, "dhRecbto"))) + " " +
                    DateTime.toStringHoraMinuto(DateTime.fromStringToLDTOffsetZone(MwXML.getValue(doc, "dhRecbto")));
            map.put("protocoloAutorizacao", protocoloAutorizacao);
            
            map.put("naturezaOperacao", MwXML.getValue(doc, "natOp"));

            map.put("dataEmissao", DateTime.toStringDate(DateTime.fromStringToLDTOffsetZone(MwXML.getValue(doc, "dhEmi"))));
            map.put("dataSaida", DateTime.toStringDate(DateTime.fromStringToLDTOffsetZone(MwXML.getValue(doc, "dhEmi"))));
            map.put("horaSaida", DateTime.toStringHoraMinuto(DateTime.fromStringToLDTOffsetZone(MwXML.getValue(doc, "dhEmi"))));

            map.put("empresaLogo", APP_PATH + "\\custom\\empresa_logo.jpg");
            map.put("empresaRazaoSocial", MwXML.getValue(doc, "emit", "xNome"));
            map.put("empresaCnpj", MwXML.getValue(doc, "emit", "CNPJ"));
            map.put("empresaIe", MwXML.getValue(doc, "emit", "IE"));

            String empresaEndereco = MwXML.getValue(doc, "enderEmit", "xLgr") + ", "
                    + MwXML.getValue(doc, "enderEmit", "nro") + " "
                    + MwXML.getValue(doc, "enderEmit", "xBairro");
            map.put("empresaEndereco", empresaEndereco);

            map.put("empresaTelefone", MwXML.getValue(doc, "enderEmit", "fone"));
            map.put("empresaEmail", MwXML.getValue(doc, "emit", "email"));

            //Dest--------------------------------------------------------------
            map.put("clienteNome", MwXML.getValue(doc, "dest", "xNome"));
            String clienteCpfCnpj = "";
            if (!MwXML.getValue(doc, "dest", "CPF").isEmpty()) {
                clienteCpfCnpj = MwXML.getValue(doc, "dest", "CPF");
            } else {
                clienteCpfCnpj = MwXML.getValue(doc, "dest", "CNPJ");
            }
            map.put("clienteCpfCnpj", clienteCpfCnpj);
            map.put("clienteIe", MwXML.getValue(doc, "dest", "IE"));

            String clienteEndereco = MwXML.getValue(doc, "enderDest", "xLgr") + ", "
                    + MwXML.getValue(doc, "enderDest", "nro");

            map.put("clienteEndereco", clienteEndereco);

            map.put("clienteBairro", MwXML.getValue(doc, "enderDest", "xBairro"));
            map.put("clienteCep", MwXML.getValue(doc, "enderDest", "CEP"));
            map.put("clienteMunicipio", MwXML.getValue(doc, "enderDest", "xMun"));
            map.put("clienteUf", MwXML.getValue(doc, "enderDest", "UF"));
            map.put("clienteTelefone", MwXML.getValue(doc, "enderDest", "fone"));
            //Fim Dest----------------------------------------------------------

            //Entrega-----------------------------------------------------------
            map.put("entregaDiferente", MwXML.getValor(doc, "//entrega") != null);
            if (MwXML.getValor(doc, "//entrega") != null) {
                map.put("entregaNome", MwXML.getValor(doc, "//entrega/xNome"));
                
                String entregaCpfCnpj = "";
                if (MwXML.getValor(doc, "//entrega/CPF") != null) {
                    entregaCpfCnpj = MwXML.getValor(doc, "//entrega/CPF");
                } else {
                    entregaCpfCnpj = MwXML.getValor(doc, "//entrega/CNPJ");
                }
                map.put("entregaCpfCnpj", entregaCpfCnpj);
                map.put("entregaIe", MwXML.getValor(doc, "//entrega/IE"));
                map.put("entregaEndereco", MwXML.getValor(doc, "//entrega/xLgr") + ", " + MwXML.getValor(doc, "//entrega/nro") + " " + MwXML.getValor(doc, "//entrega/xCpl"));
                map.put("entregaBairro", MwXML.getValor(doc, "//entrega/xBairro"));
                map.put("entregaCep", MwXML.getValor(doc, "//entrega/CEP"));
                map.put("entregaMunicipio", MwXML.getValor(doc, "//entrega/xMun"));
                map.put("entregaUf", MwXML.getValor(doc, "//entrega/UF"));
                map.put("entregaTelefone", MwXML.getValor(doc, "//entrega/fone"));
                
            }
            //Fim Entrega-------------------------------------------------------
            
            //Fatura------------------------------------------------------------
            List<ParcelaToStringAdapter> parcelas = new ArrayList<>();
            List<Map<String, String>> dups = MwXML.getPairs(doc, "dup");
            
            for (int i = 0; i < dups.size(); i++) {
                ParcelaToStringAdapter parcela = new ParcelaToStringAdapter();
                Map<String,String> dup = dups.get(i);
                
                System.out.println("nDup: " + dup.get("nDup"));
                parcela.setNumero(dup.get("nDup"));
                
                System.out.println("dVenc: " + DateTime.toString( DateTime.fromStringIsoToLocalDate(dup.get("dVenc"))) );
                parcela.setVencimento(DateTime.toString( DateTime.fromStringIsoToLocalDate(dup.get("dVenc"))));
                
                System.out.println("vDup: " + dup.get("vDup").replace('.', ','));
                parcela.setValor(dup.get("vDup").replace('.', ','));
                
                parcelas.add(parcela);
            }
            
            List<ParcelaDanfeAdapter> parcelasDanfe = new ArrayList<>();
            
            for(int p=0; p < parcelas.size(); p++) {
                
                ParcelaDanfeAdapter parcelaDanfe = new ParcelaDanfeAdapter();
                
                System.out.println("montando parcelaDanfe: " + p);
                parcelaDanfe.setNumeroA(parcelas.get(p).getNumero());
                parcelaDanfe.setVencimentoA(parcelas.get(p).getVencimento());
                parcelaDanfe.setValorA(parcelas.get(p).getValor());
                p++;
                
                if(p < parcelas.size()) {
                    System.out.println("montando parcelaDanfe: " + p);
                    parcelaDanfe.setNumeroB(parcelas.get(p).getNumero());
                    parcelaDanfe.setVencimentoB(parcelas.get(p).getVencimento());
                    parcelaDanfe.setValorB(parcelas.get(p).getValor());
                    p++;
                }
                
                if(p < parcelas.size()) {
                    System.out.println("montando parcelaDanfe: " + p);
                    parcelaDanfe.setNumeroC(parcelas.get(p).getNumero());
                    parcelaDanfe.setVencimentoC(parcelas.get(p).getVencimento());
                    parcelaDanfe.setValorC(parcelas.get(p).getValor());
                }
                
                parcelasDanfe.add(parcelaDanfe);
            }
            
            JRBeanCollectionDataSource jrParcelas = new JRBeanCollectionDataSource(parcelasDanfe);            
            
            map.put("parcelas", jrParcelas);
            
            //Fim Fatura--------------------------------------------------------

            //Cálculo do Imposto (totais)---------------------------------------
            map.put("naturezaOperacao", MwXML.getValue(doc, "natOp"));

            map.put("totalBcIcms", MwXML.getValue(doc, "ICMSTot", "vBC"));
            map.put("totalIcms", MwXML.getValue(doc, "ICMSTot", "vICMS"));
            map.put("totalBcIcmsSt", MwXML.getValue(doc, "ICMSTot", "vBCST"));
            map.put("totalIcmsSt", MwXML.getValue(doc, "ICMSTot", "vST"));

            map.put("totalFrete", MwXML.getValue(doc, "ICMSTot", "vFrete"));
            map.put("totalSeguro", MwXML.getValue(doc, "ICMSTot", "vSeg"));
            map.put("totalDesconto", MwXML.getValue(doc, "ICMSTot", "vDesc"));
            map.put("totalOutros", MwXML.getValue(doc, "ICMSTot", "vOutro"));

            map.put("totalIpi", MwXML.getValue(doc, "ICMSTot", "vIPI"));
            map.put("totalProdutos", MwXML.getValue(doc, "ICMSTot", "vProd"));
            map.put("total", MwXML.getValue(doc, "ICMSTot", "vNF"));

            //Fim Cálculo do Imposto (totais)-----------------------------------
            //Itens da venda
            List<MovimentoFisicoToStringAdapter> mfs = new ArrayList<>();

            List<Map<String, String>> dets = MwXML.getPairs(doc, "det");
            List<Map<String, String>> produtos = MwXML.getPairs(doc, "prod");
            
            
            for (int x = 0; x < dets.size(); x++) {
                //System.out.println("x: " + x);

                XPath xPath = XPathFactory.newInstance().newXPath();
                int n = x + 1;
                String detPath = "//det[@nItem = '" + n + "']";
                NodeList nodeList = (NodeList) xPath.compile(detPath).evaluate(doc, XPathConstants.NODESET);

                for (int i = 0; i < nodeList.getLength(); i++) {
                    
                    MovimentoFisicoToStringAdapter mf = new MovimentoFisicoToStringAdapter();
                    Map<String,String> produto = produtos.get(x);
                    //Map<String,String> imposto = impostos.get(x);

                    mf.setCodigo(produto.get("cProd"));
                    mf.setDescricao(produto.get("xProd"));
                    mf.setNcm(produto.get("NCM"));
                    String cstCsosn = MwXML.getValor(doc, detPath + "/imposto//CSOSN");//imposto.get("CST") + imposto.get("CSOSN");
                    mf.setCstCsosn(cstCsosn);
                    mf.setCfop(produto.get("CFOP"));
                    mf.setUnidadeMedida(produto.get("uCom"));
                    mf.setQuantidade(produto.get("qCom"));
                    mf.setValor(produto.get("vUnCom"));
                    mf.setSubtotal(produto.get("vProd"));
                    mf.setValorBcIcms(MwXML.getValueByAttributeValue(doc, "det", cstCsosn, APP_PATH, relatorio));
                    
                    String vBcIcms = MwXML.getValor(doc, detPath + "/imposto//vBC");
                    vBcIcms = vBcIcms != null ? vBcIcms : "0,00";
                    mf.setValorBcIcms(vBcIcms);
                    
                    String vIcms = MwXML.getValor(doc, detPath + "/imposto//vICMS");
                    vIcms = vIcms != null ? vIcms : "0,00";
                    mf.setValorIcms(vIcms);
                    
                    String vIpi = MwXML.getValor(doc, detPath + "/imposto//vIPI");
                    vIpi = vIpi != null ? vIpi : "0,00";
                    mf.setValorIpi(vIpi);
                    
                    String pIcms = MwXML.getValor(doc, detPath + "/imposto//pICMS");
                    pIcms = pIcms != null ? pIcms : "0,00";
                    mf.setAliquotaIcms(pIcms);
                    
                    String pIpi = MwXML.getValor(doc, detPath + "/imposto//pIPI");
                    pIpi = pIpi != null ? pIpi : "0,00";
                    mf.setAliquotaIpi(pIpi);

                    mfs.add(mf);
                    
                    

                }
            }

            JRBeanCollectionDataSource itens = new JRBeanCollectionDataSource(mfs);

            //List<MovimentoFisicoToStringAdapter> mfsReport = MovimentoFisicoToStringAdapter.adaptList(venda.getMovimentosFisicosSaida());
            //JRBeanCollectionDataSource itens = new JRBeanCollectionDataSource(mfsReport);
            

            map.put("dets", itens);

            map.put("informacoesComplementares", MwXML.getValue(doc, "infAdic", "infCpl"));
            map.put("reservadoFisco", MwXML.getValue(doc, "infAdic", "infAdFisco"));

            /*List<CaixaPeriodoPorMeioDePagamentoReport> dadosBase = new ArrayList<>();
            CaixaPeriodoPorMeioDePagamentoReport dado = new CaixaPeriodoPorMeioDePagamentoReport();
            dadosBase.add(dado);
            JRBeanCollectionDataSource jrSource = new JRBeanCollectionDataSource(dadosBase);*/

            JasperPrint jp = JasperFillManager.fillReport(relatorio, map);
            JasperViewer jv = new JasperViewer(jp, false);

            jv.setVisible(true);

        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório " + e);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(DanfePrint.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(DanfePrint.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DanfePrint.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(DanfePrint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void gerarA4(Venda venda) {
        try {
            String relatorio = (APP_PATH + "\\reports\\Danfe.jasper");

            //Itens da venda
            List<MovimentoFisicoToStringAdapter> mfsReport = MovimentoFisicoToStringAdapter.adaptList(venda.getMovimentosFisicosSaida());

            JRBeanCollectionDataSource data = new JRBeanCollectionDataSource(mfsReport);

            //Parcelas
            List<ParcelaToStringAdapter> parcelas = new ArrayList<>();

            for (Parcela parcela : venda.getParcelasAPrazo()) {
                ParcelaToStringAdapter p = new ParcelaToStringAdapter();
                p.setNumero(parcela.getNumero().toString());
                p.setVencimento(DateTime.toString(parcela.getVencimento()));
                p.setValor(Decimal.toString(parcela.getValor()));

                parcelas.add(p);
            }

            JRBeanCollectionDataSource dataParcelas = new JRBeanCollectionDataSource(parcelas);

            HashMap map = new HashMap();
            map.put("numero", "1234");
            map.put("serie", "1");
            map.put("tipoOperacao", venda.getTipoOperacao().getId().toString());
            map.put("chaveAcesso", "42-1907-08.515.145/0003-07-55-057-000.015.396-104.690.604-0");
            map.put("dataEmissao", DateTime.toStringDate(venda.getCriacao()));
            map.put("dataSaida", DateTime.toStringDate(venda.getCriacao()));
            map.put("horaSaida", DateTime.toStringHoraMinuto(venda.getCriacao()));

            map.put("empresaLogo", APP_PATH + "\\custom\\empresa_logo.jpg");
            map.put("empresaRazaoSocial", Ouroboros.EMPRESA_RAZAO_SOCIAL);
            map.put("empresaNome", Ouroboros.EMPRESA_NOME_FANTASIA);
            map.put("empresaCnpj", Ouroboros.EMPRESA_CNPJ);
            map.put("empresaEndereco", Sistema.getEnderecoCompleto());
            map.put("empresaTelefone", Ouroboros.EMPRESA_TELEFONE);
            map.put("empresaEmail", Ouroboros.EMPRESA_EMAIL);

            if (venda.getPessoa() != null) {
                map.put("clienteNome", venda.getPessoa().getId() + " - " + venda.getPessoa().getNome());
                map.put("clienteCpfCnpj", venda.getPessoa().getCpfOuCnpj());
                map.put("clienteIe", venda.getPessoa().getIe());
                map.put("clienteEndereco", venda.getPessoa().getEndereco() + ", " + venda.getPessoa().getNumero() + " " + venda.getPessoa().getComplemento());
                map.put("clienteBairro", venda.getPessoa().getBairro());
                map.put("clienteCep", venda.getPessoa().getCep());
                map.put("clienteMunicipio", venda.getPessoa().getMunicipio());
                map.put("clienteUf", venda.getPessoa().getUf());
                map.put("clienteTelefone", venda.getPessoa().getTelefone1());

            }

            map.put("entregaDiferente", venda.isEntregaDiferente());
            if (venda.isEntregaDiferente()) {
                map.put("entregaNome", venda.getEntregaNome());
                map.put("entregaCpfCnpj", venda.getEntregaCpfOuCnpj());
                map.put("entregaIe", venda.getEntregaIe());

                map.put("entregaEndereco", venda.getEntregaEndereco() + ", " + venda.getEntregaNumero() + " " + venda.getEntregaComplemento());
                map.put("entregaBairro", venda.getEntregaBairro());
                map.put("entregaCep", venda.getEntregaCep());
                map.put("entregaMunicipio", venda.getEntregaMunicipio().getNome());
                map.put("entregaUf", venda.getEntregaMunicipio().getEstado().getSigla());

                map.put("entregaTelefone", venda.getEntregaTelefone());
            }

            map.put("totalBcIcms", "0,00");
            map.put("totalIcms", "0,00");
            map.put("totalBcIcmsSt", "0,00");
            map.put("totalIcmsSt", "0,00");

            map.put("totalFrete", "0,00");
            map.put("totalSeguro", "0,00");
            map.put("totalDesconto", "0,00");
            map.put("totalOutros", "0,00");

            map.put("totalIpi", "0,00");
            map.put("totalProdutos", "0,00");
            map.put("total", "0,00");

            map.put("itens", data);

            map.put("parcelas", dataParcelas);

            map.put("informacoesComplementares", venda.getInformacoesComplementaresContribuinte());
            map.put("reservadoFisco", venda.getInformacoesAdicionaisFisco());


            /*List<CaixaPeriodoPorMeioDePagamentoReport> dadosBase = new ArrayList<>();

            CaixaPeriodoPorMeioDePagamentoReport dado = new CaixaPeriodoPorMeioDePagamentoReport();

            dadosBase.add(dado);
            JRBeanCollectionDataSource jrSource = new JRBeanCollectionDataSource(dadosBase);
             */
            JasperPrint jp = JasperFillManager.fillReport(relatorio, map);
            JasperViewer jv = new JasperViewer(jp, false);

            jv.setTitle(venda.getTitulo());
            jv.setVisible(true);

        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório " + e);
        }
    }

}
