/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package printing.documento;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Utilities;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.MovimentoFisico;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.SAT_MARGEM_DIREITA;
import static ouroboros.Ouroboros.SAT_MARGEM_ESQUERDA;
import static ouroboros.Ouroboros.SAT_MARGEM_INFERIOR;
import static ouroboros.Ouroboros.SAT_MARGEM_SUPERIOR;
import static ouroboros.Ouroboros.TO_PRINTER_PATH;
import util.DateTime;
import util.Decimal;
import util.Texto;
import util.Sistema;

/**
 *
 * @author ivand
 */
public class CriarPdfA4 {
    
    
    
    public String gerarOrdemDeServico(Venda venda) {
        String pdfFilePath = TO_PRINTER_PATH + venda.getVendaTipo().getNome() + " " + venda.getId() + "_" + System.currentTimeMillis() + ".pdf";
        
        //Ajustar altura de acordo com conteúdo
        //https://developers.itextpdf.com/examples/columntext-examples-itext5/adjust-page-size-based-amount-html-data
        //Tamanho A4
        final float DOCUMENTO_LARGURA = Utilities.millimetersToPoints(210);
        final float DOCUMENTO_ALTURA = Utilities.millimetersToPoints(270);
        
        com.itextpdf.text.Rectangle rect = new Rectangle(DOCUMENTO_LARGURA, DOCUMENTO_ALTURA);

        final com.itextpdf.text.Font FONT_BIG = new Font(FontFamily.UNDEFINED, 12, Font.BOLD, null);
        final com.itextpdf.text.Font FONT_NORMAL = new Font(FontFamily.UNDEFINED, 10, Font.BOLD, null);
        final com.itextpdf.text.Font FONT_BOLD = new Font(FontFamily.TIMES_ROMAN, 10, Font.BOLD, null);
        //BaseFont bf = BaseFont.createFont("c:/windows/fonts/arial.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED);
        //Font FONT_NORMAL = new Font(bf, 10);

        com.itextpdf.text.Document pdfDocument = new com.itextpdf.text.Document();

        pdfDocument.setPageSize(rect);
        //pdfDocument.setMargins(SAT_MARGEM_ESQUERDA, SAT_MARGEM_DIREITA, SAT_MARGEM_SUPERIOR, SAT_MARGEM_INFERIOR);
        pdfDocument.setMargins(10, 10, 10, 10);

        try {
            //Criar PDF
            PdfWriter writer = PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfFilePath));
            
            MyFooter event = new MyFooter();
            
            writer.setPageEvent(event);
            
            pdfDocument.open();

            /*
            Image imgCabecalho = Image.getInstance("custom/cabecalho_A4.jpg");
            System.out.println("img width: " + imgCabecalho.getWidth());
            imgCabecalho.scaleToFit(DOCUMENTO_ALTURA, Utilities.millimetersToPoints(30));
            
            imgCabecalho.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(imgCabecalho);
*/
            Paragraph parEmpresaNome = new Paragraph(Ouroboros.EMPRESA_NOME_FANTASIA + " " + Ouroboros.EMPRESA_RAZAO_SOCIAL, FONT_BOLD);
            parEmpresaNome.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parEmpresaNome);
            
            Paragraph parEmpresaEndereco = new Paragraph(Sistema.getEnderecoCompleto(), FONT_BOLD);
            parEmpresaEndereco.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parEmpresaEndereco);
            
            Chunk linebreak = new Chunk(new LineSeparator());
            pdfDocument.add(linebreak);
            
            String orcamento = "";
            if(venda.isOrcamento()) {
                orcamento = "ORÇAMENTO ";
            }
            Paragraph docTitulo = new Paragraph(orcamento + venda.getVendaTipo().getNome() + " ID. " + venda.getId(), FONT_BIG);
            docTitulo.add(new Chunk(new VerticalPositionMark()));
            docTitulo.add(DateTime.toStringDataAbreviada(venda.getDataHora()));
            pdfDocument.add(docTitulo);
            
            pdfDocument.add(linebreak);
            
            //Dados do Cliente
            if(venda.getPessoa() != null) {
                Pessoa pessoa = venda.getPessoa();
                Paragraph parClienteNome = new Paragraph("CLIENTE: " + pessoa.getNome() + "   CPF/CNPJ: " + pessoa.getCpfOuCnpj(), FONT_BOLD);
                pdfDocument.add(parClienteNome);
                Paragraph parClienteEndereco = new Paragraph("ENDEREÇO: " + pessoa.getEnderecoCompleto()+ "   TELEFONE: " + pessoa.getTelefone1(), FONT_NORMAL);
                pdfDocument.add(parClienteEndereco);
            }
            //Fim Dados do Cliente

            pdfDocument.add(linebreak);

            /**
             * **************
             * INÍCIO DOS ITENS
             */
            Paragraph cabecalhoItens = new Paragraph(" #|       CÓDIGO|QUANTIDADE|UN.MEDIDA|VALOR UN. R$|", FONT_NORMAL);
            cabecalhoItens.add(new Chunk(new VerticalPositionMark()));
            cabecalhoItens.add("SUBTOTAL ITEM R$");
            cabecalhoItens.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
            pdfDocument.add(cabecalhoItens);
            
            Paragraph cabecalhoItens2 = new Paragraph("DESCRIÇÃO", FONT_NORMAL);
            pdfDocument.add(cabecalhoItens2);
            
            pdfDocument.add(linebreak);

            List<MovimentoFisico> vendaItens = venda.getMovimentosFisicosSaida();
            //List<Map<String,String>> impostos = MwXML.getPairs(doc, "imposto");
            
            //int n = 0;
            for(MovimentoFisico movimentoFisico : vendaItens){
                
                String unidadeComercialDeVenda = "";
                if(movimentoFisico.getUnidadeComercialVenda()!= null) {
                    unidadeComercialDeVenda = movimentoFisico.getUnidadeComercialVenda().getNome();
                }
                
                Paragraph itemValores = new Paragraph(
                        Texto.padLeftAndCut(String.valueOf(venda.getMovimentosFisicosSaida().indexOf(movimentoFisico) + 1), 2) + 
                        " " + Texto.padLeftAndCut(movimentoFisico.getCodigo(), 13) + 
                        " " + Texto.padLeftAndCut(Decimal.toString(movimentoFisico.getSaida(), 3), 10) +  
                        " " + Texto.padLeftAndCut(unidadeComercialDeVenda, 9) +  
                        " " + Texto.padLeftAndCut(Decimal.toString(movimentoFisico.getValor()), 12),
                        FONT_NORMAL);
                itemValores.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                pdfDocument.add(itemValores);

                String descricao = movimentoFisico.getDescricao();
                Chunk glue = new Chunk(new VerticalPositionMark());
                Paragraph itemDescricao = new Paragraph(null, FONT_NORMAL);
                itemDescricao.add(descricao);
                itemDescricao.add(glue);
                itemDescricao.add(Decimal.toString(movimentoFisico.getSubtotal()));

                pdfDocument.add(itemDescricao);
                
            }

            /**
             * **************
             * FIM DOS ITENS
             */
            pdfDocument.add(Chunk.NEWLINE);
            pdfDocument.add(linebreak);
            
            //Total bruto de itens
            Paragraph totalBruto = new Paragraph("Total de itens", FONT_NORMAL);
            totalBruto.add(new Chunk(new VerticalPositionMark()));
            totalBruto.add(Decimal.toString(venda.getTotal()));
            pdfDocument.add(totalBruto);
            
            //Desconto sobre subtotal
            if(venda.getDescontoMonetarioProdutos().compareTo(BigDecimal.ZERO) > 0) {
                Paragraph descontoSubtotal = new Paragraph("Desconto R$", FONT_NORMAL);
                descontoSubtotal.add(new Chunk(new VerticalPositionMark()));
                descontoSubtotal.add(Decimal.toString(venda.getDescontoMonetarioProdutos()));
                pdfDocument.add(descontoSubtotal);
            }
            
            if(venda.getDescontoPercentualProdutos().compareTo(BigDecimal.ZERO) > 0) {
                Paragraph descontoSubtotalPercentual = new Paragraph("Desconto %", FONT_NORMAL);
                descontoSubtotalPercentual.add(new Chunk(new VerticalPositionMark()));
                descontoSubtotalPercentual.add(Decimal.toString(venda.getDescontoPercentualProdutos()));
                pdfDocument.add(descontoSubtotalPercentual);
            }
            
            
            //Total
            Paragraph total = new Paragraph("TOTAL R$", FONT_BOLD);
            total.add(new Chunk(new VerticalPositionMark()));
            total.add(Decimal.toString(venda.getTotal()));
            pdfDocument.add(total);

            pdfDocument.add(Chunk.NEWLINE);

            //MP - Meios de pagamento
            if(venda.getTotalRecebidoAVista().compareTo(BigDecimal.ZERO) > 0) {
                Paragraph tituloMeiosDePagamento = new Paragraph("MEIOS DE PAGAMENTO", FONT_BOLD);
                tituloMeiosDePagamento.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                pdfDocument.add(tituloMeiosDePagamento);

                for (Map.Entry<MeioDePagamento, BigDecimal> entry : venda.getRecebimentosAgrupadosPorMeioDePagamento().entrySet()) {
                        String cMP = entry.getKey().getNome();
                        Paragraph meioPagamento = new Paragraph( cMP , FONT_NORMAL);
                        meioPagamento.add(new Chunk(new VerticalPositionMark()));
                        meioPagamento.add(Decimal.toString(entry.getValue()));
                        pdfDocument.add(meioPagamento);
                }

                pdfDocument.add(Chunk.NEWLINE);
            
                //Troco
                if(venda.getTroco().compareTo(BigDecimal.ZERO) > 0) {
                    Paragraph troco = new Paragraph("Troco", FONT_NORMAL);
                    troco.add(new Chunk(new VerticalPositionMark()));
                    troco.add(Decimal.toString(venda.getTroco()));
                    pdfDocument.add(troco);

                    pdfDocument.add(Chunk.NEWLINE);
                }
            }

            //Parcelas / Faturamento
            if(venda.getParcelasAPrazo().size() > 0) {
                Paragraph tituloParcelas = new Paragraph("PARCELAS", FONT_BOLD);
                tituloParcelas.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                pdfDocument.add(tituloParcelas);

                Paragraph cabecalhoParcelas = new Paragraph("PARCELA|VENCIMENTO|", FONT_NORMAL);
                cabecalhoParcelas.add(new Chunk(new VerticalPositionMark()));
                cabecalhoParcelas.add("VALOR");
                pdfDocument.add(cabecalhoParcelas);

                for(Parcela parcela : venda.getParcelasAPrazo()) {
                    Paragraph parParcela = new Paragraph(
                            parcela.getNumero() +
                                    "        " + DateTime.toStringDataAbreviada(parcela.getVencimento())
                    , FONT_NORMAL);

                    parParcela.add(new Chunk(new VerticalPositionMark()));
                    parParcela.add(Decimal.toString(parcela.getValor()));
                    pdfDocument.add(parParcela);

                }

                pdfDocument.add(Chunk.NEWLINE);
            }
            
            //Observação
            if(venda.getObservacao() != null && !venda.getObservacao().isEmpty()) {
                Paragraph parObservacao = new Paragraph("OBS: " + venda.getObservacao(), FONT_NORMAL);
                //parObservacao.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                pdfDocument.add(parObservacao);
            }

            pdfDocument.add(Chunk.NEWLINE);
            pdfDocument.add(Chunk.NEWLINE);
            Paragraph parAssinaturaLinha = new Paragraph("--------------------------------------------------------------------------------", FONT_NORMAL);
            parAssinaturaLinha.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parAssinaturaLinha);
            Paragraph parAssinatura = new Paragraph("CIENTE", FONT_NORMAL);
            parAssinatura.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parAssinatura);
            
            pdfDocument.add(linebreak);


            Paragraph parRodape = new Paragraph(Ouroboros.MW_WEBSITE, FONT_NORMAL);
            parRodape.add(new Chunk(new VerticalPositionMark()));
            parRodape.add("IMPRESSÃO: " + DateTime.toString(DateTime.getNow()));
            pdfDocument.add(parRodape);
        } catch (DocumentException | FileNotFoundException e) {
            System.err.println("Erro ao imprimir A4 " + e);
        } finally {
            pdfDocument.close();
        }

        return pdfFilePath;
    }
    
    
    class MyFooter extends PdfPageEventHelper {
        Font ffont = new Font(Font.FontFamily.UNDEFINED, 5, Font.ITALIC);
 
        public void onEndPage(PdfWriter writer, Document document) {
            final float DOCUMENTO_LARGURA = Utilities.millimetersToPoints(210);
            final float DOCUMENTO_ALTURA = Utilities.millimetersToPoints(270);
            Image imgCabecalho;
            try {
                PdfContentByte cb = writer.getDirectContent();
                Rectangle rect = writer.getBoxSize("art");
                Phrase header = new Phrase("this is a header", ffont);
                
                imgCabecalho = Image.getInstance("custom/cabecalho_A4.jpg");
                System.out.println("img width: " + imgCabecalho.getWidth());
                imgCabecalho.scaleToFit(DOCUMENTO_ALTURA, Utilities.millimetersToPoints(30));
                
                header.add(imgCabecalho);
                
                ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, header, rect.getRight(), rect.getTop(), 0);
                
                
            } catch (BadElementException | IOException ex) {
                Logger.getLogger(CriarPdfA4.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
    }
    
    
}
