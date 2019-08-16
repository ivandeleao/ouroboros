package printing;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Utilities;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.nosql.ImpressoraFormato;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.bean.relatorio.CaixaPeriodoPorMeioDePagamentoReport;
import model.mysql.bean.relatorio.MovimentoFisicoReport;
import model.mysql.bean.relatorio.ParcelaReport;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.APP_PATH;
import static ouroboros.Ouroboros.IMPRESSORA_CUPOM;
import static ouroboros.Ouroboros.IMPRESSORA_FORMATO_PADRAO;
import static ouroboros.Ouroboros.SAT_MARGEM_DIREITA;
import static ouroboros.Ouroboros.SAT_MARGEM_ESQUERDA;
import static ouroboros.Ouroboros.SAT_MARGEM_INFERIOR;
import static ouroboros.Ouroboros.SAT_MARGEM_SUPERIOR;
import static ouroboros.Ouroboros.TO_PRINTER_PATH;
import util.DateTime;
import util.Decimal;
import util.MwString;
import util.Sistema;
import view.Toast;

public class ListaParcelasPrint {

    static float cupomLargura;

    private static Float getLargura() {
        if (IMPRESSORA_FORMATO_PADRAO.equals(ImpressoraFormato.CUPOM_58.toString())) {
            return cupomLargura = 58;
        } else {
            return cupomLargura = 80;
        }
    }

    public static void imprimirCupom(Pessoa cliente, List<Parcela> parcelas) {
        String pdfFilePath = TO_PRINTER_PATH + "LISTA PARCELAS - TIME " + System.currentTimeMillis() + ".pdf";

        gerarCupom(cliente, parcelas, pdfFilePath);

        PrintPDFBox.print(pdfFilePath, IMPRESSORA_CUPOM);

    }

    private static void gerarCupom(Pessoa cliente, List<Parcela> parcelas, String pdfFilePath) {
        //Ajustar altura de acordo com conteúdo
        //https://developers.itextpdf.com/examples/columntext-examples-itext5/adjust-page-size-based-amount-html-data
        com.itextpdf.text.Rectangle rect = new Rectangle(Utilities.millimetersToPoints(getLargura()), Utilities.millimetersToPoints(300));

        final com.itextpdf.text.Font FONT_NORMAL = new Font(Font.FontFamily.UNDEFINED, 8, Font.BOLD, null);
        final com.itextpdf.text.Font FONT_BOLD = new Font(Font.FontFamily.UNDEFINED, 8, Font.BOLD, null);

        com.itextpdf.text.Document pdfDocument = new com.itextpdf.text.Document();

        pdfDocument.setPageSize(rect);
        pdfDocument.setMargins(SAT_MARGEM_ESQUERDA, SAT_MARGEM_DIREITA, SAT_MARGEM_SUPERIOR, SAT_MARGEM_INFERIOR);

        Chunk glue = new Chunk(new VerticalPositionMark());

        try {
            //Criar PDF
            PdfWriter writer = PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfFilePath));

            pdfDocument.open();

            Paragraph nomeFantasia = new Paragraph(Ouroboros.EMPRESA_NOME_FANTASIA, FONT_BOLD);
            nomeFantasia.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(nomeFantasia);

            Paragraph razaoSocial = new Paragraph(Ouroboros.EMPRESA_RAZAO_SOCIAL, FONT_BOLD);
            razaoSocial.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(razaoSocial);

            Paragraph endereco = new Paragraph(Sistema.getEnderecoCompleto(), FONT_BOLD);
            endereco.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(endereco);

            Paragraph parTelefone = new Paragraph(Ouroboros.EMPRESA_TELEFONE, FONT_BOLD);
            parTelefone.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parTelefone);

            Chunk linebreak = new Chunk(new LineSeparator());
            pdfDocument.add(linebreak);

            Paragraph cupomTitulo = new Paragraph("LISTA DE PARCELAS", FONT_BOLD);
            cupomTitulo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(cupomTitulo);

            pdfDocument.add(linebreak);

            //Cliente
            if (cliente != null) {
                Paragraph parClienteNome = new Paragraph("CLIENTE: " + cliente.getId() + " - " + cliente.getNome(), FONT_BOLD);
                pdfDocument.add(parClienteNome);
                if (!cliente.getCpfOuCnpj().isEmpty()) {
                    Paragraph parClienteCpfCnpj = new Paragraph("CPF/CNPJ: " + cliente.getCpfOuCnpj(), FONT_NORMAL);
                    pdfDocument.add(parClienteCpfCnpj);
                }
                if (!cliente.getEnderecoCompleto().isEmpty()) {
                    Paragraph parClienteEndereco = new Paragraph("ENDEREÇO: " + cliente.getEnderecoCompleto(), FONT_NORMAL);
                    pdfDocument.add(parClienteEndereco);
                }
                if (!cliente.getTelefone1().isEmpty()) {
                    Paragraph parClienteTelefone = new Paragraph("TELEFONE: " + cliente.getTelefone1(), FONT_NORMAL);
                    pdfDocument.add(parClienteTelefone);
                }
            }
            //Fim Cliente

            pdfDocument.add(linebreak);

            //Parcelas ---------------------------------------------------------
            Paragraph cabecalhoParcelas = new Paragraph("VD-PARC|VENCIMENTO|ORIGINAL|", FONT_NORMAL);
            cabecalhoParcelas.add(new Chunk(new VerticalPositionMark()));
            cabecalhoParcelas.add("ATUALIZADO");
            pdfDocument.add(cabecalhoParcelas);

            for (Parcela parcela : parcelas) {
                Paragraph parParcela = new Paragraph(
                        MwString.padLeft(parcela.getVenda().getId() +
                                " - " + parcela.getNumeroFormatado(), 11) +
                                "   " + DateTime.toStringDataAbreviada(parcela.getVencimento()), 
                        FONT_NORMAL);
                
                parParcela.add(new Chunk(new VerticalPositionMark()));
                parParcela.add(Decimal.toString(parcela.getValor()));
                
                parParcela.add(new Chunk(new VerticalPositionMark()));
                parParcela.add(Decimal.toString(parcela.getValorAtual()));
                
                pdfDocument.add(parParcela);

            }
            //Fim Parcelas -----------------------------------------------------
            
            pdfDocument.add(Chunk.NEWLINE);

            //Total ------------------------------------------------------------
            Paragraph parTotal = new Paragraph("TOTAL:", FONT_NORMAL);
            parTotal.add(new Chunk(new VerticalPositionMark()));
            parTotal.add(Decimal.toString(parcelas.stream().map(Parcela::getValorAtual).reduce(BigDecimal::add).get()));
            pdfDocument.add(parTotal);
            //Fim Total --------------------------------------------------------
            
            pdfDocument.add(linebreak);

            String dataHoraEmissao = DateTime.toString(DateTime.getNow());
            Paragraph dataHora = new Paragraph(dataHoraEmissao, FONT_NORMAL);
            dataHora.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(dataHora);

            pdfDocument.add(Chunk.NEWLINE);

            Paragraph parRodape = new Paragraph(Ouroboros.SISTEMA_ASSINATURA, FONT_NORMAL);
            parRodape.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parRodape);

        } catch (DocumentException | FileNotFoundException e) {
            System.err.println(e);
        } finally {
            pdfDocument.close();
        }

    }

}
