package printing.documento;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Utilities;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.MovimentoFisicoStatus;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.documento.VendaItemConsolidado;
import model.mysql.bean.principal.documento.VendaTipo;
import model.nosql.ImpressoraFormatoEnum;
import model.nosql.relatorio.VendaItemReportBean;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.APP_PATH;
import static ouroboros.Ouroboros.IMPRESSORA_CUPOM;
import static ouroboros.Ouroboros.IMPRESSORA_FORMATO_PADRAO;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.SAT_MARGEM_DIREITA;
import static ouroboros.Ouroboros.SAT_MARGEM_ESQUERDA;
import static ouroboros.Ouroboros.SAT_MARGEM_INFERIOR;
import static ouroboros.Ouroboros.SAT_MARGEM_SUPERIOR;
import static ouroboros.Ouroboros.TO_PRINTER_PATH;
import printing.PrintPDFBox;
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
public class DocumentoSaidaItensReport {

    static float cupomLargura;

    private static Float getLargura() {
        if (IMPRESSORA_FORMATO_PADRAO.equals(ImpressoraFormatoEnum.CUPOM_58.toString())) {
            return cupomLargura = 58;
        } else {
            return cupomLargura = 80;
        }
    }

    public static void imprimirBobina(List<VendaItemConsolidado> itensConsolidados, LocalDate dataInicial, LocalDate dataFinal) {
        String pdfFilePath = TO_PRINTER_PATH + "ITENS VENDIDOS - TIME " + System.currentTimeMillis() + ".pdf";
        
        gerarBobina(itensConsolidados, dataInicial, dataFinal, pdfFilePath);

        PrintPDFBox.print(pdfFilePath, IMPRESSORA_CUPOM);
        
    }
    
    private static void gerarBobina(List<VendaItemConsolidado> itensConsolidados, LocalDate dataInicial, LocalDate dataFinal, String pdfFilePath) {
        //Ajustar altura de acordo com conteúdo
        //https://developers.itextpdf.com/examples/columntext-examples-itext5/adjust-page-size-based-amount-html-data
        com.itextpdf.text.Rectangle rect = new Rectangle(Utilities.millimetersToPoints(getLargura()), Utilities.millimetersToPoints(300));

        final com.itextpdf.text.Font FONTE_PEQUENA = new Font(Font.FontFamily.UNDEFINED, Ouroboros.IMPRESSORA_CUPOM_TAMANHO_FONTE, Font.NORMAL, null);
        final com.itextpdf.text.Font FONTE_GRANDE = new Font(Font.FontFamily.UNDEFINED, Ouroboros.IMPRESSORA_CUPOM_TAMANHO_FONTE, Font.BOLD, null);

        com.itextpdf.text.Document pdfDocument = new com.itextpdf.text.Document();

        pdfDocument.setPageSize(rect);
        pdfDocument.setMargins(SAT_MARGEM_ESQUERDA, SAT_MARGEM_DIREITA, SAT_MARGEM_SUPERIOR, SAT_MARGEM_INFERIOR);

        
        try {
            //Criar PDF
            PdfWriter writer = PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfFilePath));
            
            pdfDocument.open();

            
            

            Chunk linebreak = new Chunk(new LineSeparator());
            pdfDocument.add(linebreak);
            
            String identificacao = "ITENS VENDIDOS";
            
            Paragraph parIdentificacao = new Paragraph(identificacao, FONTE_GRANDE);
            parIdentificacao.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parIdentificacao);
            
            
            Paragraph parPeriodo = new Paragraph("Data Inicial: " + DateTime.toString(dataInicial)
                    + " Data Final: " + DateTime.toString(dataFinal), FONTE_GRANDE);
            //parPeriodo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parPeriodo);

            pdfDocument.add(linebreak);
            
            
            PdfPTable table = new PdfPTable(3);
            table.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{2, 2, 2});
            table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            
            //Cabeçalho Itens---------------------------------------------------
            PdfPCell cellHeaderCodigo = new PdfPCell(new Phrase("CÓDIGO", FONTE_PEQUENA));
            cellHeaderCodigo.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellHeaderCodigo.setBorder(PdfPCell.NO_BORDER);
            table.addCell(cellHeaderCodigo);

            PdfPCell cellHeaderDescricao = new PdfPCell(new Phrase("DESCRIÇÃO", FONTE_PEQUENA));
            cellHeaderDescricao.setColspan(2);
            cellHeaderDescricao.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellHeaderDescricao.setBorder(PdfPCell.NO_BORDER);
            table.addCell(cellHeaderDescricao);
            
            PdfPCell cellHeaderVazia = new PdfPCell();
            cellHeaderVazia.setBorder(PdfPCell.BOTTOM);
            table.addCell(cellHeaderVazia);
                
            PdfPCell cellHeaderQuantidade = new PdfPCell(new Phrase("QUANTIDADE", FONTE_PEQUENA));
            cellHeaderQuantidade.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellHeaderQuantidade.setBorder(PdfPCell.BOTTOM);
            table.addCell(cellHeaderQuantidade);

            PdfPCell cellHeaderTotal = new PdfPCell(new Phrase("TOTAL", FONTE_PEQUENA));
            cellHeaderTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellHeaderTotal.setBorder(PdfPCell.BOTTOM);
            table.addCell(cellHeaderTotal);
            //Fim Cabeçalho Itens-----------------------------------------------
            
            
            //Itens-------------------------------------------------------------
            for(VendaItemConsolidado itemConsolidado : itensConsolidados){
                PdfPCell cellCodigo = new PdfPCell(new Phrase(itemConsolidado.getProduto().getCodigo(), FONTE_PEQUENA));
                cellCodigo.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellCodigo.setBorder(PdfPCell.NO_BORDER);
                table.addCell(cellCodigo);
                
                PdfPCell cellDescricao = new PdfPCell(new Phrase(itemConsolidado.getProduto().getNome(), FONTE_PEQUENA));
                cellDescricao.setColspan(2);
                cellDescricao.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellDescricao.setBorder(PdfPCell.NO_BORDER);
                table.addCell(cellDescricao);
                
                table.addCell("");
                
                PdfPCell cellQuantidade = new PdfPCell(new Phrase(Decimal.toStringDescarteDecimais(itemConsolidado.getQuantidade()), FONTE_PEQUENA));
                cellQuantidade.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellQuantidade.setBorder(PdfPCell.NO_BORDER);
                table.addCell(cellQuantidade);
                
                PdfPCell cellTotal = new PdfPCell(new Phrase(Decimal.toString(itemConsolidado.getTotal()), FONTE_PEQUENA));
                cellTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellTotal.setBorder(PdfPCell.NO_BORDER);
                table.addCell(cellTotal);
                
                table.completeRow();
            }
            //Fim Itens---------------------------------------------------------
            
            //Totais------------------------------------------------------------
            PdfPCell cellFooterTotais = new PdfPCell(new Phrase("TOTAIS:", FONTE_GRANDE));
            cellFooterTotais.setBorder(PdfPCell.TOP);
            table.addCell(cellFooterTotais);
            
            String quantidade = Decimal.toStringDescarteDecimais(itensConsolidados.stream().map(VendaItemConsolidado::getQuantidade).reduce(BigDecimal::add).get());
            PdfPCell cellFooterQuantidade = new PdfPCell(new Phrase(quantidade, FONTE_PEQUENA));
            cellFooterQuantidade.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellFooterQuantidade.setBorder(PdfPCell.TOP);
            table.addCell(cellFooterQuantidade);

            String total = Decimal.toString(itensConsolidados.stream().map(VendaItemConsolidado::getTotal).reduce(BigDecimal::add).get());
            PdfPCell cellFooterTotal = new PdfPCell(new Phrase(total, FONTE_PEQUENA));
            cellFooterTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellFooterTotal.setBorder(PdfPCell.TOP);
            table.addCell(cellFooterTotal);
            //Fim Totais--------------------------------------------------------

            pdfDocument.add(table);
            
            
            
            pdfDocument.add(linebreak);

            String dataHoraEmissao = DateTime.toString(DateTime.getNow());
            Paragraph dataHora = new Paragraph(dataHoraEmissao, FONTE_GRANDE);
            dataHora.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(dataHora);

            pdfDocument.add(Chunk.NEWLINE);

            Paragraph parRodape = new Paragraph(Ouroboros.SISTEMA_ASSINATURA, FONTE_PEQUENA);
            parRodape.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parRodape);
            
        } catch (DocumentException | FileNotFoundException e) {
            System.err.println(e);
        } finally {
            pdfDocument.close();
        }

    }

    public static void gerarA4(List<VendaItemConsolidado> itensConsolidados, LocalDate dataInicial, LocalDate dataFinal) {
        try {
            if (itensConsolidados.isEmpty()) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem registros para gerar o relatório", "Atenção", JOptionPane.WARNING_MESSAGE);

            } else {

                String relatorio = APP_PATH + "\\reports\\DocumentoSaidaItens.jasper";

                HashMap map = new HashMap();
                map.put("sistemaAssinatura", Ouroboros.SISTEMA_ASSINATURA);

                map.put("sistemaAssinatura", Ouroboros.SISTEMA_ASSINATURA);
                map.put("dataInicial", DateTime.toString(dataInicial));
                map.put("dataFinal", DateTime.toString(dataFinal));
                map.put("total", Decimal.toString(itensConsolidados.stream().map(VendaItemConsolidado::getTotal).reduce(BigDecimal::add).get()));

                List<VendaItemReportBean> elementos = new ArrayList<>();

                for (VendaItemConsolidado i : itensConsolidados) {
                    VendaItemReportBean elemento = new VendaItemReportBean();

                    elemento.setCodigo(i.getProduto().getCodigo());
                    elemento.setDescricao(i.getProduto().getNome());
                    elemento.setQuantidade(Decimal.toString(i.getQuantidade(), 3));
                    elemento.setValorMedio(Decimal.toString(i.getValorMedio()));
                    elemento.setTotal(Decimal.toString(i.getTotal()));

                    elementos.add(elemento);
                }
                JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(elementos);

                JasperPrint jp = JasperFillManager.fillReport(relatorio, map, jr);

                JasperViewer jv = new JasperViewer(jp, false);

                jv.setTitle("Itens Vendidos");

                jv.setVisible(true);
            }

        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório. " + e.getMessage());
        }
    }
}
