/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Utilities;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import model.bean.fiscal.Ibpt;
import model.bean.principal.Venda;
import model.bean.principal.MovimentoFisico;
import model.bean.fiscal.MeioDePagamento;
import model.bean.principal.ImpressoraFormato;
import model.bean.principal.Produto;
import model.dao.fiscal.IbptDAO;
import model.dao.fiscal.MeioDePagamentoDAO;
import util.MwXML;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import static ouroboros.Ouroboros.*;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class MwSat {
    static float cupomLargura;
    
    private static Float getLargura() {
        if(IMPRESSORA_FORMATO_PADRAO.equals(ImpressoraFormato.CUPOM_58.toString())) {
            return cupomLargura = 58;
        } else {
            return cupomLargura = 80;
        }
    }
    
    public static List<String> validar(Venda venda) {
        List<String> msgItens = new ArrayList<>();
        
        //validar itens
        for(MovimentoFisico mf : venda.getMovimentosFisicosSaida()) {
            Produto p = mf.getProduto();
            String erro = "";
            
            if(p.getUnidadeComercialVenda() == null) {
                if(erro.equals("")) { erro = p.getNome() + ": "; }
                erro += "Unidade de Venda, ";
            }
            
            if(p.getIcms() == null) {
                if(erro.equals("")) { erro = p.getNome() + ": "; }
                erro += "ICMS, ";
            }
            
            if(p.getNcm() == null) {
                if(erro.equals("")) { erro = p.getNome() + ": "; }
                erro += "NCM, ";
            }
            
            if(p.getOrigem() == null) {
                if(erro.equals("")) { erro = p.getNome() + ": "; }
                erro += "Origem, ";
            }
            
            if(p.getCfopSaidaDentroDoEstado() == null) {
                if(erro.equals("")) { erro = p.getNome() + ": "; }
                erro += "CFOP, ";
            }
            
            if(!erro.equals("")) {
                msgItens.add(erro);
            }
        }
        
        if(!msgItens.isEmpty()) {
            msgItens.add(0, "Produtos devem ser corrigidos e lançados novamente: ");
        }
        
        return msgItens;
    }
    
    /**
     * 
     * @param venda
     * @return 
     */
    public static Document prepareDocument(Venda venda) {
        BigDecimal totalIbpt = BigDecimal.ZERO;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            //Document
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element CFe = doc.createElement("CFe");
            Element infCFe = doc.createElement("infCFe");
            infCFe.setAttribute("versaoDadosEnt", "0.07");
            Element ide = doc.createElement("ide");
            Element CNPJ = doc.createElement("CNPJ");
            CNPJ.appendChild(doc.createTextNode(SOFTWARE_HOUSE_CNPJ));
            ide.appendChild(CNPJ);

            Element signAC = doc.createElement("signAC");
            signAC.appendChild(doc.createTextNode(SAT_SIGN_AC));
            ide.appendChild(signAC);

            Element numeroCaixa = doc.createElement("numeroCaixa");
            numeroCaixa.appendChild(doc.createTextNode("001"));
            ide.appendChild(numeroCaixa);

            infCFe.appendChild(ide);

            Element emit = doc.createElement("emit");
            
                Element emitCNPJ = doc.createElement("CNPJ");
                emitCNPJ.appendChild(doc.createTextNode(EMPRESA_CNPJ));
                emit.appendChild(emitCNPJ);

                /*
                Alguns dados da empresa vêm do SAT, não devem ser enviados pelo AC
                
                Element emitXNome = xdoc.createElement("xNome");
                emitXNome.appendChild(xdoc.createTextNode(sistemaPersist.getValor("empresa_razao_social")));
                emit.appendChild(emitXNome);
                
                Element emitXFant = xdoc.createElement("xFant");
                emitXFant.appendChild(xdoc.createTextNode(sistemaPersist.getValor("empresa_nome")));
                emit.appendChild(emitXFant);
                
                
                //Endereço do emitente
                Element enderEmit = xdoc.createElement("enderEmit");
                    Element xLgr = xdoc.createElement("xLgr");
                    xLgr.appendChild(xdoc.createTextNode(sistemaPersist.getValor("empresa_endereco")));
                    enderEmit.appendChild(xLgr);
                
                emit.appendChild(enderEmit);
                //Fim - Endereço do emitente
                */
                
                
                Element emitIE = doc.createElement("IE");
                emitIE.appendChild(doc.createTextNode(EMPRESA_IE));
                emit.appendChild(emitIE);
                
                //2019-03-07
                if(!EMPRESA_IM.trim().isEmpty()) {
                    Element emitIM = doc.createElement("IM");
                    emitIM.appendChild(doc.createTextNode(EMPRESA_IM));
                    emit.appendChild(emitIM);
                }//

                Element indRatISSQN = doc.createElement("indRatISSQN");
                indRatISSQN.appendChild(doc.createTextNode("N"));
                emit.appendChild(indRatISSQN);

            infCFe.appendChild(emit);

            Element dest = doc.createElement("dest");

                if(venda.getDestCpfCnpj().length() == 11){
                    Element destCPF = doc.createElement("CPF");
                    destCPF.appendChild(doc.createTextNode(venda.getDestCpfCnpj()));
                    dest.appendChild(destCPF);
                }else if(venda.getDestCpfCnpj().length() == 14){
                    Element destCNPJ = doc.createElement("CNPJ");
                    destCNPJ.appendChild(doc.createTextNode(venda.getDestCpfCnpj()));
                    dest.appendChild(destCNPJ);
                }

            infCFe.appendChild(dest);

            
            //venda.getVendaItens().forEach((item) -> {
            for (MovimentoFisico item : venda.getMovimentosFisicosSaida()) {
                
                
                
                Element det = doc.createElement("det");
                det.setAttribute("nItem", String.valueOf(venda.getMovimentosFisicosSaida().indexOf(item) + 1));
                Element prod = doc.createElement("prod");

                Element cProd = doc.createElement("cProd");
                cProd.appendChild(doc.createTextNode(item.getProduto().getCodigo()));
                prod.appendChild(cProd);

                Element xProd = doc.createElement("xProd");
                xProd.appendChild(doc.createTextNode(item.getProduto().getNome()));
                prod.appendChild(xProd);

                Element NCM = doc.createElement("NCM");
                NCM.appendChild(doc.createTextNode(item.getProduto().getNcm().getCodigo()));
                prod.appendChild(NCM);
                
                //2018-10-04 sat versão 0.08
                /*
                Element cest = doc.createElement("CEST");
                NCM.appendChild(doc.createTextNode(item.getProduto().getCest()));
                prod.appendChild(cest);
*/
                Element CFOP = doc.createElement("CFOP");
                CFOP.appendChild(doc.createTextNode(item.getProduto().getCfopSaidaDentroDoEstado().getCodigo().toString()));
                prod.appendChild(CFOP);

                Element uCom = doc.createElement("uCom");
                uCom.appendChild(doc.createTextNode(item.getProduto().getUnidadeComercialVenda().getNome()));
                prod.appendChild(uCom);

                Element qCom = doc.createElement("qCom");
                qCom.appendChild(doc.createTextNode(Decimal.toStringComPonto(item.getSaida(), 4)));
                prod.appendChild(qCom);

                Element vUnCom = doc.createElement("vUnCom");
                vUnCom.appendChild(doc.createTextNode(Decimal.toStringComPonto(item.getValor())));
                prod.appendChild(vUnCom);

                Element indRegra = doc.createElement("indRegra");
                indRegra.appendChild(doc.createTextNode("T"));
                prod.appendChild(indRegra);
                
                //No SAT existem dois tipos de desconto: por item e rateado do subtotal (vRatDesc)
                /*
                Element vDesc = xdoc.createElement("vDesc");
                vDesc.appendChild(xdoc.createTextNode(MwFormat.numberDecimalToString(item.getValor() * ( venda.getDescontoPorcentagem() / 100))));
                prod.appendChild(vDesc);
                */
                //No SAT existem dois tipos de acréscimo: por item e rateado do subtotal (vRatAcr)
                /*
                Element vOutro = xdoc.createElement("vOutro"); //Valor de acréscimos sobre valor do item 
                vOutro.appendChild(xdoc.createTextNode(MwFormat.numberDecimalToString(item.getValor() * ( venda.getAcrescimoPorcentagem() / 100))));
                prod.appendChild(vOutro);
                */
                
                det.appendChild(prod);

                Element imposto = doc.createElement("imposto");

                Element vItem12741 = doc.createElement("vItem12741");
                //2019-02-04 para ncm genérico não existente na tabela ibpt
                BigDecimal ibptAliqNac = BigDecimal.ZERO;
                Ibpt ibpt = new IbptDAO().findByCodigo(item.getProduto().getNcm().getCodigo());
                if(ibpt != null) {
                    ibptAliqNac = new IbptDAO().findByCodigo(item.getProduto().getNcm().getCodigo()).getAliqNac();
                }
                
                BigDecimal valorIbpt = item.getSubtotal().multiply(ibptAliqNac).divide(new BigDecimal(100));
                totalIbpt = totalIbpt.add(valorIbpt);
                vItem12741.appendChild(doc.createTextNode(Decimal.toStringComPonto(valorIbpt)));
                imposto.appendChild(vItem12741);

                Element ICMS = doc.createElement("ICMS");
                switch (item.getProduto().getIcms().getCodigo()) {
                    case "00":
                    case "20":
                    case "90":
                        Element ICMS00 = doc.createElement("ICMS00");

                        Element Orig = doc.createElement("Orig");
                        Orig.appendChild(doc.createTextNode(item.getProduto().getOrigem().getId().toString()));
                        ICMS00.appendChild(Orig);

                        Element CST = doc.createElement("CST");
                        CST.appendChild(doc.createTextNode(item.getProduto().getIcms().getCodigo()));
                        ICMS00.appendChild(CST);

                        Element pICMS = doc.createElement("pICMS");
                        pICMS.appendChild(doc.createTextNode(Decimal.toStringComPonto(item.getProduto().getAliquotaIcms())));
                        ICMS00.appendChild(pICMS);

                        ICMS.appendChild(ICMS00);

                        break;
                    case "40":
                    case "41":
                    case "60":
                        Element ICMS40 = doc.createElement("ICMS40");

                        Element ICMS40_Orig = doc.createElement("Orig");
                        ICMS40_Orig.appendChild(doc.createTextNode(item.getProduto().getOrigem().getId().toString()));
                        ICMS40.appendChild(ICMS40_Orig);

                        Element ICMS40_CST = doc.createElement("CST");
                        ICMS40_CST.appendChild(doc.createTextNode(item.getProduto().getIcms().getCodigo()));
                        ICMS40.appendChild(ICMS40_CST);

                        ICMS.appendChild(ICMS40);

                        break;

                    case "102":
                    case "300":
                    case "400":
                    case "500":
                        Element ICMSSN102 = doc.createElement("ICMSSN102");

                        Element ICMSSN102_Orig = doc.createElement("Orig");
                        ICMSSN102_Orig.appendChild(doc.createTextNode(item.getProduto().getOrigem().getId().toString()));
                        ICMSSN102.appendChild(ICMSSN102_Orig);

                        Element ICMSSN102_CSOSN = doc.createElement("CSOSN");
                        ICMSSN102_CSOSN.appendChild(doc.createTextNode(item.getProduto().getIcms().getCodigo()));
                        ICMSSN102.appendChild(ICMSSN102_CSOSN);

                        ICMS.appendChild(ICMSSN102);

                        break;

                    case "900":
                        Element ICMSSN900 = doc.createElement("ICMSSN900");

                        Element ICMSSN900_Orig = doc.createElement("Orig");
                        ICMSSN900_Orig.appendChild(doc.createTextNode(item.getProduto().getOrigem().getId().toString()));
                        ICMSSN900.appendChild(ICMSSN900_Orig);

                        Element CSOSN = doc.createElement("CSOSN");
                        CSOSN.appendChild(doc.createTextNode(item.getProduto().getIcms().getCodigo()));
                        ICMSSN900.appendChild(CSOSN);

                        Element ICMSSN900_pICMS = doc.createElement("pICMS");
                        ICMSSN900_pICMS.appendChild(doc.createTextNode(Decimal.toStringComPonto(item.getProduto().getAliquotaIcms())));
                        ICMSSN900.appendChild(ICMSSN900_pICMS);

                        ICMS.appendChild(ICMSSN900);

                        break;
                }

                imposto.appendChild(ICMS);

                Element PIS = doc.createElement("PIS");
                Element PISSN = doc.createElement("PISSN");

                Element PISSNCST = doc.createElement("CST");
                PISSNCST.appendChild(doc.createTextNode("49"));
                PISSN.appendChild(PISSNCST);

                PIS.appendChild(PISSN);

                imposto.appendChild(PIS);

                Element COFINS = doc.createElement("COFINS");
                Element COFINSSN = doc.createElement("COFINSSN");

                Element COFINSSNCST = doc.createElement("CST");
                COFINSSNCST.appendChild(doc.createTextNode("49"));
                COFINSSN.appendChild(COFINSSNCST);

                COFINS.appendChild(COFINSSN);

                imposto.appendChild(COFINS);

                det.appendChild(imposto);

                infCFe.appendChild(det);
            }

            Element total = doc.createElement("total");

                Element DescAcrEntr = doc.createElement("DescAcrEntr");
                    //Mutuamente exclusivos
                    BigDecimal diferencaDescAcr = venda.getAcrescimoConsolidado().subtract(venda.getDescontoConsolidado());
                    if(diferencaDescAcr.compareTo(BigDecimal.ZERO) < 0){
                        Element vDescSubtot = doc.createElement("vDescSubtot");
                        vDescSubtot.appendChild(doc.createTextNode(Decimal.toStringComPonto(diferencaDescAcr.abs()))); //a diferença traz valor negativo por isso o abs
                        DescAcrEntr.appendChild(vDescSubtot);
                        total.appendChild(DescAcrEntr);
                    }
                    else if(diferencaDescAcr.compareTo(BigDecimal.ZERO) > 0){
                        Element vAcresSubtot  = doc.createElement("vAcresSubtot");
                        vAcresSubtot .appendChild(doc.createTextNode(Decimal.toStringComPonto(diferencaDescAcr)));
                        DescAcrEntr.appendChild(vAcresSubtot);
                        total.appendChild(DescAcrEntr);
                    }
                    
                  
            
                Element vCFeLei12741 = doc.createElement("vCFeLei12741");
                vCFeLei12741.appendChild(doc.createTextNode(Decimal.toStringComPonto(totalIbpt)));
                total.appendChild(vCFeLei12741);

            infCFe.appendChild(total);

            Element pgto = doc.createElement("pgto");

            //Meios de pagamento
            for (Map.Entry<MeioDePagamento, BigDecimal> entry : venda.getRecebimentosAgrupadosPorMeioDePagamento().entrySet()) {
                Element MP = doc.createElement("MP");

                Element cMP = doc.createElement("cMP");
                cMP.appendChild(doc.createTextNode(entry.getKey().getCodigoSAT()));
                MP.appendChild(cMP);

                Element vMP = doc.createElement("vMP");

                vMP.appendChild(doc.createTextNode(Decimal.toStringComPonto(entry.getValue())));
                MP.appendChild(vMP);

                pgto.appendChild(MP);
            };
            
            //MP cannot be less than total - Add (99 Outros) to fulfill its value
            BigDecimal diferenca = venda.getTotalReceber();
            if(diferenca.compareTo(BigDecimal.ZERO) > 0){
                Element MP = doc.createElement("MP");

                Element cMP = doc.createElement("cMP");
                cMP.appendChild(doc.createTextNode("99"));
                MP.appendChild(cMP);

                Element vMP = doc.createElement("vMP");

                vMP.appendChild(doc.createTextNode(Decimal.toStringComPonto(diferenca)));
                MP.appendChild(vMP);

                pgto.appendChild(MP);
            }
            
            

            infCFe.appendChild(pgto);

            CFe.appendChild(infCFe);

            doc.appendChild(CFe);

            return doc;

        } catch (ParserConfigurationException e) {
            System.err.println(e);
        }

        return null;
    }


    
    
    public static void gerarCupom(String xmlFilePath, String pdfFilePath) throws BadElementException, IOException {
        //Ajustar altura de acordo com conteúdo
        //https://developers.itextpdf.com/examples/columntext-examples-itext5/adjust-page-size-based-amount-html-data
        com.itextpdf.text.Rectangle rect = new Rectangle(Utilities.millimetersToPoints(getLargura()), Utilities.millimetersToPoints(300));

        final com.itextpdf.text.Font FONT_NORMAL = new Font(FontFamily.COURIER, 8, Font.BOLD, null);
        final com.itextpdf.text.Font FONT_BOLD = new Font(FontFamily.COURIER, 8, Font.BOLD, null);

        com.itextpdf.text.Document pdfDocument = new com.itextpdf.text.Document();

        pdfDocument.setPageSize(rect);
        pdfDocument.setMargins(SAT_MARGEM_ESQUERDA, SAT_MARGEM_DIREITA, SAT_MARGEM_SUPERIOR, SAT_MARGEM_INFERIOR);

        try {
            //Abrir XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = dbf.newDocumentBuilder();
            File f = new File(xmlFilePath);
            Document doc = builder.parse(f);
            //Extrair dados do xml
            /*
            NodeList parametros = xdoc.getElementsByTagName("parametros");
            Element element = (Element) parametros.item(0);
            NodeList dadosVenda = element.getElementsByTagName("dadosVenda");
            Element line = (Element) dadosVenda.item(0);
            */
            //Extrair xml do CDATA
            /*
            String xml = getCharacterDataFromElement(line);
            Document doc = MwXML.convertStringToDocument(xml);
            System.out.println(xml);
            */
            
            //Criar PDF
            PdfWriter writer = PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfFilePath));
            
            pdfDocument.open();

            Paragraph nomeFantasia = new Paragraph(MwXML.getText(doc, "emit", "xFant"), FONT_BOLD);
            nomeFantasia.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(nomeFantasia);

            Paragraph razaoSocial = new Paragraph(MwXML.getText(doc, "emit", "xNome"), FONT_BOLD);
            razaoSocial.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(razaoSocial);

            String enderEmitXCpl = "";
            if(!MwXML.getText(doc, "enderEmit", "xCpl").equals("Nao Informado")) {
                enderEmitXCpl = MwXML.getText(doc, "enderEmit", "xCpl");
            }
            
            String enderecoCompleto = MwXML.getText(doc, "enderEmit", "xLgr") + ", "
                    + MwXML.getText(doc, "enderEmit", "nro") + " "
                    + enderEmitXCpl + " "
                    + MwXML.getText(doc, "enderEmit", "xBairro") + " "
                    + MwXML.getText(doc, "enderEmit", "xMun") + " "
                    + MwXML.getText(doc, "enderEmit", "cUF");
            
            
            Paragraph endereco = new Paragraph(enderecoCompleto, FONT_BOLD);
            endereco.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(endereco);

            String cnpj = "CNPJ: " + MwXML.getText(doc, "emit", "CNPJ");
            String ie = "IE: " + MwXML.getText(doc, "emit", "IE");
            String im = "";
            if(!MwXML.getText(doc, "emit", "IM").isEmpty()) { //2019-03-07
                im = "IM: " + MwXML.getText(doc, "emit", "IM");
            }
            Paragraph cnpjIeIm = new Paragraph(cnpj + " " + ie + " " + im, FONT_NORMAL);
            cnpjIeIm.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(cnpjIeIm);

            

            Chunk linebreak = new Chunk(new LineSeparator());
            pdfDocument.add(linebreak);

            
            //nserieSat ausente nos XMLs que verifiquei - Extraindo da tag resultado
            /*
            String resultado = MwXML.getText(xdoc, "resultado");
            String[] resultadoSplit = resultado.split("\\|");
            */
            String rawChaveConsulta = MwXML.getAttributeValue(doc, "infCFe", "Id").substring(3);
            String nCFe = rawChaveConsulta.substring(31, 37);
            
            Paragraph extratoNumero = new Paragraph("EXTRATO NO." + nCFe, FONT_BOLD);
            extratoNumero.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(extratoNumero);

            Paragraph cupomFiscalEletronico = new Paragraph("CUPOM FISCAL ELETRÔNICO - SAT", FONT_BOLD);
            cupomFiscalEletronico.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(cupomFiscalEletronico);

            String destCpfCnpj = MwXML.getText(doc, "dest", "CPF");
            if(destCpfCnpj.isEmpty()){
                destCpfCnpj = MwXML.getText(doc, "dest", "CNPJ");
            }
            
            Paragraph consumidor = new Paragraph("CPF/CNPJ DO CONSUMIDOR: " + destCpfCnpj, FONT_BOLD);
            consumidor.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
            pdfDocument.add(consumidor);

            pdfDocument.add(linebreak);

            /**
             * **************
             * INÍCIO DOS ITENS
             */
            Paragraph cabecalhoItens = new Paragraph("#|COD| QTD| UN|VL UN R$(VL TR R$)*| DESC| VL ITEM R$", FONT_NORMAL);
            cabecalhoItens.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
            pdfDocument.add(cabecalhoItens);

            pdfDocument.add(linebreak);

            List<Map<String,String>> produtos = MwXML.getPairs(doc, "prod");
            List<Map<String,String>> impostos = MwXML.getPairs(doc, "imposto");
            
            int n = 0;
            for(int x = 0; x < produtos.size(); x++){
                
                Map<String,String> produto = produtos.get(x);
                Map<String,String> imposto = impostos.get(x);
                
                //System.out.println(produto);
                
                n++;
                Paragraph itemValores = new Paragraph(n + 
                        " " + produto.get("cProd") + 
                        " " + new DecimalFormat("0.0000").format( Double.parseDouble(produto.get("qCom")) ) +  
                        " " + produto.get("uCom") +  
                        " x " + new DecimalFormat("0.00").format( Double.parseDouble(produto.get("vUnCom")) ) + 
                        " ("  + new DecimalFormat("0.00").format( Double.parseDouble(imposto.get("vItem12741")) ) + ")"
                        , FONT_NORMAL);
                itemValores.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                pdfDocument.add(itemValores);

                String descricao = produto.get("xProd");
                Chunk glue = new Chunk(new VerticalPositionMark());
                Double subtotal = Double.valueOf(produto.get("qCom")) * Double.parseDouble(produto.get("vUnCom"));
                //total += subtotal;
                Paragraph itemDescricao = new Paragraph(null, FONT_NORMAL);
                itemDescricao.add(descricao);
                itemDescricao.add(glue);
                itemDescricao.add(new DecimalFormat("0.00").format(subtotal));

                pdfDocument.add(itemDescricao);
                
                //desconto de item
                String vDesc = produto.get("vDesc");
                if(vDesc != null){
                    String descricaoDesconto = "desconto sobre item";
                    Paragraph itemDesconto = new Paragraph(null, FONT_NORMAL);
                    itemDesconto.add(descricaoDesconto);
                    itemDesconto.add(glue);
                    itemDesconto.add("-" + new DecimalFormat("0.00").format(Double.parseDouble(vDesc)));

                    pdfDocument.add(itemDesconto);
                }
                
                //acréscimo de item
                String vOutro = produto.get("vOutro");
                if(vOutro != null){
                    String descricaoAcrescimo = "acréscimo sobre item";
                    Paragraph itemAcrescimo = new Paragraph(null, FONT_NORMAL);
                    itemAcrescimo.add(descricaoAcrescimo);
                    itemAcrescimo.add(glue);
                    itemAcrescimo.add("+" + new DecimalFormat("0.00").format(Double.parseDouble(vOutro)));

                    pdfDocument.add(itemAcrescimo);
                }
                
                //desconto de subtotal
                String vRatDesc = produto.get("vRatDesc");
                if(vRatDesc != null){
                    String descricaoDesconto = "rateio de desconto sobre subtotal";
                    Paragraph itemDesconto = new Paragraph(null, FONT_NORMAL);
                    itemDesconto.add(descricaoDesconto);
                    itemDesconto.add(glue);
                    itemDesconto.add("-" + new DecimalFormat("0.00").format(Double.parseDouble(vRatDesc)));

                    pdfDocument.add(itemDesconto);
                }
                
                //acréscimo de subtotal
                String vRatAcr = produto.get("vRatAcr");
                if(vRatAcr != null){
                    String descricaoAcrescimo = "rateio de acréscimo sobre subtotal";
                    Paragraph itemAcrescimo = new Paragraph(null, FONT_NORMAL);
                    itemAcrescimo.add(descricaoAcrescimo);
                    itemAcrescimo.add(glue);
                    itemAcrescimo.add("+" + new DecimalFormat("0.00").format(Double.parseDouble(vRatAcr)));

                    pdfDocument.add(itemAcrescimo);
                }
                
            }

            /**
             * **************
             * FIM DOS ITENS
             */
            pdfDocument.add(Chunk.NEWLINE);
            
            //Total bruto de itens
            String vProd = MwXML.getText(doc, "ICMSTot", "vProd");
            Paragraph totalBruto = new Paragraph("Total bruto de itens", FONT_NORMAL);
            totalBruto.add(new Chunk(new VerticalPositionMark()));
            totalBruto.add(new DecimalFormat("0.00").format(Double.parseDouble(vProd)));
            pdfDocument.add(totalBruto);
            
            //Total descontos/acréscimos sobre item
            String vDescTotal = MwXML.getText(doc, "ICMSTot", "vDesc");
            String vAcresTotal = MwXML.getText(doc, "ICMSTot", "vOutro");
            Double vDiferencaDescAcres = Double.parseDouble(vAcresTotal) - Double.parseDouble(vDescTotal);
            
            if(vDiferencaDescAcres > 0){
                Paragraph totalDescAcres = new Paragraph("Total descontos/acréscimos sobre item", FONT_NORMAL);
                totalDescAcres.add(new Chunk(new VerticalPositionMark()));
                totalDescAcres.add("+" + new DecimalFormat("0.00").format(vDiferencaDescAcres));
                pdfDocument.add(totalDescAcres);
            }
            else if(vDiferencaDescAcres < 0){
                Paragraph totalDescAcres = new Paragraph("Total descontos/acréscimos sobre item", FONT_NORMAL);
                totalDescAcres.add(new Chunk(new VerticalPositionMark()));
                totalDescAcres.add("-" + new DecimalFormat("0.00").format(Math.abs(vDiferencaDescAcres)));
                pdfDocument.add(totalDescAcres);
            }
            
            //Desconto sobre subtotal
            String vDescSubtot = MwXML.getText(doc, "DescAcrEntr", "vDescSubtot");
            if(!vDescSubtot.isEmpty()){
                Paragraph descontoSubtotal = new Paragraph("Desconto sobre subtotal", FONT_NORMAL);
                descontoSubtotal.add(new Chunk(new VerticalPositionMark()));
                descontoSubtotal.add(new DecimalFormat("0.00").format(Double.parseDouble(vDescSubtot)));
                pdfDocument.add(descontoSubtotal);
            }
            
            //Acréscimo sobre subtotal
            String vAcresSubtot = MwXML.getText(doc, "DescAcrEntr", "vAcresSubtot");
            if(!vAcresSubtot.isEmpty()){
                Paragraph acrescimoSubtotal = new Paragraph("Acréscimo sobre subtotal", FONT_NORMAL);
                acrescimoSubtotal.add(new Chunk(new VerticalPositionMark()));
                acrescimoSubtotal.add(new DecimalFormat("0.00").format(Double.parseDouble(vAcresSubtot)));
                pdfDocument.add(acrescimoSubtotal);
            }
            
            //Total
            String vCFe = MwXML.getText(doc, "vCFe");
            Paragraph total = new Paragraph("TOTAL R$", FONT_BOLD);
            total.add(new Chunk(new VerticalPositionMark()));
            total.add(new DecimalFormat("0.00").format(Double.parseDouble(vCFe)));
            pdfDocument.add(total);

            pdfDocument.add(Chunk.NEWLINE);

            //MP - Meios de pagamento
            List<Map<String,String>> meiosPagamento = MwXML.getPairs(doc, "MP");
            for (Map<String, String> mp : meiosPagamento) {
                String cMP = new MeioDePagamentoDAO().findByCodigoSAT(mp.get("cMP")).getNome();
                Paragraph meioPagamento = new Paragraph( cMP , FONT_NORMAL);
                meioPagamento.add(new Chunk(new VerticalPositionMark()));
                meioPagamento.add(Decimal.toString(new BigDecimal(mp.get("vMP"))));
                pdfDocument.add(meioPagamento);
            }
            
            //Troco
            String vTroco = MwXML.getText(doc, "vTroco");
            if(!vTroco.isEmpty()){
                Paragraph troco = new Paragraph("Troco", FONT_NORMAL);
                troco.add(new Chunk(new VerticalPositionMark()));
                troco.add(new DecimalFormat("0.00").format(Double.parseDouble(vTroco)));
                pdfDocument.add(troco);
            }

            pdfDocument.add(Chunk.NEWLINE);

            pdfDocument.add(new Paragraph("OBSERVAÇÕES", FONT_BOLD));
            //observações do fisco

            //dados para entrega
            //observações do contribuinte
            Paragraph observacoesContribuinte1 = new Paragraph("*Valor aproximado dos tributos do item", FONT_NORMAL);
            pdfDocument.add(observacoesContribuinte1);
            
            String vCFeLei12741 = new DecimalFormat("0.00").format(Double.valueOf(MwXML.getText(doc, "vCFeLei12741") ));
            Paragraph observacoesContribuinte2 = new Paragraph("Valor aproximado dos tributos deste cupom R$ " + vCFeLei12741, FONT_NORMAL);
            pdfDocument.add(observacoesContribuinte2);

            Paragraph observacoesContribuinte3 = new Paragraph("(conforme Lei Fed. 12.741/2012)", FONT_NORMAL);
            pdfDocument.add(observacoesContribuinte3);

            pdfDocument.add(linebreak);

            //Rodapé
            //nCFe, dEmi, hEmi ausentes nos XMLs que verifiquei - Extraindo estes dados da tag resultado
            /*
            for(int i=0; i<resultadoSplit.length; i++){
                System.out.println("i: " + resultadoSplit[i]);
            }
            
            String rawTimeStamp = resultadoSplit[7];
            
            String nserieSat = rawChaveConsulta.substring(22, 31);
            */
            
            String nserieSAT = MwXML.getText(doc, "nserieSAT");
            
            Paragraph satNumero = new Paragraph("SAT No. " + nserieSAT, FONT_BOLD);
            satNumero.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(satNumero);
            
            //2019-03-07
            String rawDEmi = MwXML.getText(doc, "dEmi");
            String dEmi = rawDEmi.subSequence(6, 8) + "/" + rawDEmi.subSequence(4, 6) + "/" + rawDEmi.subSequence(0, 4);
            String rawHEmi = MwXML.getText(doc, "hEmi");
            String hEmi = rawHEmi.subSequence(0, 2) + ":" + rawHEmi.subSequence(2, 4) + ":" + rawHEmi.subSequence(4, 6);
            
            String timeStamp = dEmi + " " + hEmi;
            System.out.println("timeStamp: " + timeStamp );

            String dataHoraEmissao = timeStamp;
            Paragraph dataHora = new Paragraph(dataHoraEmissao, FONT_NORMAL);
            dataHora.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(dataHora);

            pdfDocument.add(Chunk.NEWLINE);

            //ChaveConsulta 44 dígitos - sem o prefixo CFe
            //formatar em grupos de 4 dígitos
            String[] chaveConsultaPedacos = rawChaveConsulta.split("(?<=\\G.{4})");
            String chaveConsultaComEspacos = String.join(" ", chaveConsultaPedacos);
            Paragraph chaveConsulta = new Paragraph(chaveConsultaComEspacos, FONT_NORMAL);
            chaveConsulta.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(chaveConsulta);
            
            //Código de barras
            //https://developers.itextpdf.com/examples/miscellaneous/bar-codes
            Barcode128 code128 = new Barcode128();
            code128.setCodeType(Barcode128.CODE128);
            code128.setCode(rawChaveConsulta);
            code128.setFont(null);
            PdfContentByte cb = writer.getDirectContent();
            Image imageCode128 = code128.createImageWithBarcode(cb, null, null);
            imageCode128.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            imageCode128.scaleAbsoluteWidth(Utilities.millimetersToPoints(68));
            pdfDocument.add(imageCode128);
            
            //QRCode
            String assinaturaQRCODE = MwXML.getText(doc, "assinaturaQRCODE");
            try{
                String qrCodeData = assinaturaQRCODE;
                String filePath = "qrcodeTemp.png";
                String charset = "UTF-8"; //or "ISO-8859-1"
                Map <EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap <>();
                hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
                BitMatrix matrix = new MultiFormatWriter().encode(
                        new String(qrCodeData.getBytes(charset), charset),
                        BarcodeFormat.QR_CODE, 150, 150, hintMap);
                MatrixToImageWriter.writeToFile(matrix, filePath.substring(
                        filePath.lastIndexOf(".") + 1), new File(filePath));
            }catch(WriterException | IOException e){
                System.err.println(e);
            }
            
            Image imgQRCode = Image.getInstance("qrcodeTemp.png");
            imgQRCode.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(imgQRCode);

            Paragraph observacoesFisco = new Paragraph("Consulte o QRCode deste extrato através do App DeOlhoNaNota", FONT_NORMAL);
            observacoesFisco.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(observacoesFisco);
        } catch (FileNotFoundException | DocumentException | SAXException | ParserConfigurationException ex) {
            System.out.println("Error: " + ex);
        } finally {
            pdfDocument.close();
        }

    }

    public static void gerarCupomCancelamento(String xmlFilePath, String pdfFilePath){
        com.itextpdf.text.Rectangle rect = new Rectangle(Utilities.millimetersToPoints(getLargura()), Utilities.millimetersToPoints(300));

        final com.itextpdf.text.Font FONT_NORMAL = new Font(FontFamily.COURIER, 8, Font.BOLD, null);
        final com.itextpdf.text.Font FONT_BOLD = new Font(FontFamily.COURIER, 8, Font.BOLD, null);

        com.itextpdf.text.Document pdfDocument = new com.itextpdf.text.Document();

        pdfDocument.setPageSize(rect);
        pdfDocument.setMargins(SAT_MARGEM_ESQUERDA, SAT_MARGEM_DIREITA, SAT_MARGEM_SUPERIOR, SAT_MARGEM_INFERIOR);
        
        
        try {
            //Abrir XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = dbf.newDocumentBuilder();
            File f = new File(xmlFilePath);
            Document doc = builder.parse(f);
            
            PdfWriter writer = PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfFilePath));
            
            pdfDocument.open();

            Paragraph nomeFantasia = new Paragraph(MwXML.getText(doc, "emit", "xFant"), FONT_BOLD);
            nomeFantasia.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(nomeFantasia);

            Paragraph razaoSocial = new Paragraph(MwXML.getText(doc, "emit", "xNome"), FONT_BOLD);
            razaoSocial.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(razaoSocial);

            
            String enderEmitXCpl = "";
            if(!MwXML.getText(doc, "enderEmit", "xCpl").equals("Nao Informado")) {
                enderEmitXCpl = MwXML.getText(doc, "enderEmit", "xCpl");
            }
            
            String enderecoCompleto = MwXML.getText(doc, "enderEmit", "xLgr") + ", "
                    + MwXML.getText(doc, "enderEmit", "nro") + " "
                    + enderEmitXCpl + " "
                    + MwXML.getText(doc, "enderEmit", "xBairro") + " "
                    + MwXML.getText(doc, "enderEmit", "xMun") + " "
                    + MwXML.getText(doc, "enderEmit", "cUF");
            
            Paragraph endereco = new Paragraph(enderecoCompleto, FONT_BOLD);
            endereco.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(endereco);

            String cnpj = "CNPJ: " + MwXML.getText(doc, "emit", "CNPJ");
            String ie = "IE: " + MwXML.getText(doc, "emit", "IE");
            //2019-03-07 Aparentemente não existe a tag IM no xml de cancelamento ????
            String im = "";
            if(!MwXML.getText(doc, "emit", "IM").isEmpty()) { //2019-03-07
                im = "IM: " + MwXML.getText(doc, "emit", "IM");
            }
            Paragraph cnpjIeIm = new Paragraph(cnpj + " " + ie + " " + im, FONT_NORMAL);
            cnpjIeIm.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(cnpjIeIm);


            Chunk linebreak = new Chunk(new LineSeparator());
            pdfDocument.add(linebreak);
            
            
            String rawChaveConsulta = MwXML.getAttributeValue(doc, "infCFe", "Id").substring(3);
            String nCFe = rawChaveConsulta.substring(31, 37);
            
            Paragraph extratoNumero = new Paragraph("EXTRATO NO." + nCFe, FONT_BOLD);
            extratoNumero.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(extratoNumero);

            Paragraph cupomFiscalEletronico = new Paragraph("CUPOM FISCAL ELETRÔNICO - SAT", FONT_BOLD);
            cupomFiscalEletronico.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(cupomFiscalEletronico);
            
            Paragraph cupomFiscalEletronicoCancelamento = new Paragraph("CANCELAMENTO", FONT_BOLD);
            cupomFiscalEletronicoCancelamento.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(cupomFiscalEletronicoCancelamento);
            
            pdfDocument.add(linebreak);
            
            
            Paragraph dadosCupomFiscalEletronicoCancelado = new Paragraph("DADOS DO CUPOM FISCAL ELETRÔNICO CANCELADO", FONT_BOLD);
            dadosCupomFiscalEletronicoCancelado.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(dadosCupomFiscalEletronicoCancelado);
            
            pdfDocument.add(Chunk.NEWLINE);
            
            String destCpfCnpj = MwXML.getText(doc, "dest", "CPF");
            if(destCpfCnpj.isEmpty()){
                destCpfCnpj = MwXML.getText(doc, "dest", "CNPJ");
            }
            if(destCpfCnpj.isEmpty()){
                destCpfCnpj = "";
            }
            Paragraph consumidor = new Paragraph("CPF/CNPJ DO CONSUMIDOR: " + destCpfCnpj, FONT_BOLD);
            consumidor.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
            pdfDocument.add(consumidor);

            //Total
            String vCFe = MwXML.getText(doc, "vCFe");
            Paragraph total = new Paragraph("TOTAL R$", FONT_BOLD);
            total.add(new Chunk(new VerticalPositionMark()));
            total.add(new DecimalFormat("0.00").format(Double.parseDouble(vCFe)));
            pdfDocument.add(total);

            pdfDocument.add(Chunk.NEWLINE);
            
            
            String nserieSAT = MwXML.getText(doc, "nserieSAT");
            
            
            Paragraph satNumero = new Paragraph("SAT No. " + nserieSAT, FONT_BOLD);
            satNumero.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(satNumero);

            //2019-03-07
            String rawDEmi = MwXML.getText(doc, "infCFe", "dEmi");
            String dEmi = rawDEmi.subSequence(6, 8) + "/" + rawDEmi.subSequence(4, 6) + "/" + rawDEmi.subSequence(0, 4);
            String rawHEmi = MwXML.getText(doc, "infCFe", "hEmi");
            String hEmi = rawHEmi.subSequence(0, 2) + ":" + rawHEmi.subSequence(2, 4) + ":" + rawHEmi.subSequence(4, 6);
            
            String timeStamp = dEmi + " " + hEmi;
            System.out.println("timeStamp: " + timeStamp );

            String dataHoraEmissao = timeStamp;
            Paragraph dataHora = new Paragraph(dataHoraEmissao, FONT_NORMAL);
            dataHora.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(dataHora);

            pdfDocument.add(Chunk.NEWLINE);

            //ChaveConsulta 44 dígitos - sem o prefixo CFe
            //formatar em grupos de 4 dígitos
            String[] chaveConsultaPedacos = rawChaveConsulta.split("(?<=\\G.{4})");
            String chaveConsultaComEspacos = String.join(" ", chaveConsultaPedacos);
            Paragraph chaveConsulta = new Paragraph(chaveConsultaComEspacos, FONT_NORMAL);
            chaveConsulta.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(chaveConsulta);
            
            //Código de barras
            //https://developers.itextpdf.com/examples/miscellaneous/bar-codes
            Barcode128 code128 = new Barcode128();
            code128.setCodeType(Barcode128.CODE128);
            code128.setCode(rawChaveConsulta);
            code128.setFont(null);
            PdfContentByte cb = writer.getDirectContent();
            Image imageCode128 = code128.createImageWithBarcode(cb, null, null);
            imageCode128.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            imageCode128.scaleAbsoluteWidth(Utilities.millimetersToPoints(68));
            pdfDocument.add(imageCode128);
            
            //QRCode
            String assinaturaQRCODE = MwXML.getText(doc, "assinaturaQRCODE");
            try{
                String qrCodeData = assinaturaQRCODE;
                String filePath = "qrcodeTemp.png";
                String charset = "UTF-8"; //or "ISO-8859-1"
                Map <EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap <>();
                hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
                BitMatrix matrix = new MultiFormatWriter().encode(
                        new String(qrCodeData.getBytes(charset), charset),
                        BarcodeFormat.QR_CODE, 150, 150, hintMap);
                MatrixToImageWriter.writeToFile(matrix, filePath.substring(
                        filePath.lastIndexOf(".") + 1), new File(filePath));
            }catch(WriterException | IOException e){
                System.err.println(e);
            }
            
            Image imgQRCode = Image.getInstance("qrcodeTemp.png");
            imgQRCode.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(imgQRCode);
            
            
            
            pdfDocument.add(linebreak);
            
            
            
            //Footer -----------------------------------------------------------
            Paragraph dadosCupomFiscalEletronicoCancelamento = new Paragraph("DADOS DO CUPOM FISCAL ELETRÔNICO DE CANCELAMENTO", FONT_BOLD);
            dadosCupomFiscalEletronicoCancelamento.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(dadosCupomFiscalEletronicoCancelamento);
            
            pdfDocument.add(Chunk.NEWLINE);
            
            pdfDocument.add(satNumero);

            String dEmiCancel = MwXML.getText(doc, "ide", "dEmi");
            String hEmiCancel = MwXML.getText(doc, "ide", "hEmi");
            String rawTimeStampCancel = dEmiCancel + hEmiCancel;
            
            String dataHoraCancel = null; //MwFormat.dateTimeStampToBrazilian(rawTimeStampCancel);
            Paragraph dataHoraCancelParagrapph = new Paragraph(dataHoraCancel, FONT_NORMAL);
            dataHoraCancelParagrapph.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(dataHoraCancelParagrapph);

            pdfDocument.add(Chunk.NEWLINE);

            //ChaveConsulta Cancelamento 44 dígitos - sem o prefixo CFe
            String rawChaveConsultaCancel = MwXML.getAttributeValue(doc, "infCFe", "chCanc").substring(3);
            String chCanc = rawChaveConsultaCancel.substring(31, 37);
            //formatar em grupos de 4 dígitos
            String[] chaveConsultaCancelPedacos = rawChaveConsultaCancel.split("(?<=\\G.{4})");
            String chaveConsultaCancelComEspacos = String.join(" ", chaveConsultaCancelPedacos);
            Paragraph chaveConsultaCancel = new Paragraph(chaveConsultaCancelComEspacos, FONT_NORMAL);
            chaveConsultaCancel.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(chaveConsultaCancel);
            
            //Código de barras
            //https://developers.itextpdf.com/examples/miscellaneous/bar-codes
            Barcode128 code128Cancel = new Barcode128();
            code128Cancel.setCodeType(Barcode128.CODE128);
            code128Cancel.setCode(rawChaveConsultaCancel);
            code128Cancel.setFont(null);
            //PdfContentByte cb = writer.getDirectContent();
            Image imageCode128Cancel = code128Cancel.createImageWithBarcode(cb, null, null);
            imageCode128Cancel.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            imageCode128Cancel.scaleAbsoluteWidth(Utilities.millimetersToPoints(68));
            pdfDocument.add(imageCode128Cancel);
            
            
            pdfDocument.add(imgQRCode);
            
            pdfDocument.add(linebreak);
            
        } catch (DocumentException | IOException | NumberFormatException | ParserConfigurationException | SAXException ex) {
            System.err.println("Erro ao gerar cupom de cancelamento. " + ex);
        } finally {
            pdfDocument.close();
        }
    }

    
    
    public static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }
    
}
