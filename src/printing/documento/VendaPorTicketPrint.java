package printing.documento;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Utilities;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import model.nosql.ImpressoraFormatoEnum;
import model.mysql.bean.principal.catalogo.Produto;
import ouroboros.Ouroboros;
import printing.PrintPDFBox;
import static ouroboros.Ouroboros.IMPRESSORA_CUPOM;
import static ouroboros.Ouroboros.IMPRESSORA_FORMATO_PADRAO;
import static ouroboros.Ouroboros.SAT_MARGEM_DIREITA;
import static ouroboros.Ouroboros.SAT_MARGEM_ESQUERDA;
import static ouroboros.Ouroboros.SAT_MARGEM_INFERIOR;
import static ouroboros.Ouroboros.SAT_MARGEM_SUPERIOR;
import static ouroboros.Ouroboros.TO_PRINTER_PATH;
import util.DateTime;
import util.Decimal;

public class VendaPorTicketPrint {

    static float cupomLargura;
    
    private static Float getLargura() {
        if(IMPRESSORA_FORMATO_PADRAO.equals(ImpressoraFormatoEnum.CUPOM_58.toString())) {
            return cupomLargura = 58;
        } else {
            return cupomLargura = 80;
        }
    }
    
    public static void imprimirCupom(Produto produto) {
        String pdfFilePath = TO_PRINTER_PATH + "VENDA POR FICHA - PRODUTO ID " + produto.getId() + " - TIME " + System.currentTimeMillis() + ".pdf";
        
        gerarCupom(produto, pdfFilePath);

        PrintPDFBox.print(pdfFilePath, IMPRESSORA_CUPOM);
        
    }
    
    private static void gerarCupom(Produto produto, String pdfFilePath) {
        
        com.itextpdf.text.Rectangle rect = new Rectangle(Utilities.millimetersToPoints(getLargura()), Utilities.millimetersToPoints(300));

        final com.itextpdf.text.Font FONTE_MICRO = new Font(Font.FontFamily.UNDEFINED, Ouroboros.IMPRESSORA_CUPOM_TAMANHO_FONTE, Font.NORMAL, null);
        final com.itextpdf.text.Font FONTE_PEQUENA = new Font(Font.FontFamily.UNDEFINED, Ouroboros.IMPRESSORA_CUPOM_TAMANHO_FONTE + 2, Font.NORMAL, null);
        final com.itextpdf.text.Font FONTE_MEDIA = new Font(Font.FontFamily.UNDEFINED, Ouroboros.IMPRESSORA_CUPOM_TAMANHO_FONTE + 10, Font.BOLD, null);
        final com.itextpdf.text.Font FONTE_GRANDE = new Font(Font.FontFamily.UNDEFINED, Ouroboros.IMPRESSORA_CUPOM_TAMANHO_FONTE + 16, Font.BOLD, null);

        com.itextpdf.text.Document pdfDocument = new com.itextpdf.text.Document();

        pdfDocument.setPageSize(rect);
        pdfDocument.setMargins(SAT_MARGEM_ESQUERDA, SAT_MARGEM_DIREITA, SAT_MARGEM_SUPERIOR, SAT_MARGEM_INFERIOR);

        Chunk glue = new Chunk(new VerticalPositionMark());
        
        try {
            PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfFilePath));
            
            pdfDocument.open();

            Paragraph parTitulo = new Paragraph(Ouroboros.IMPRESSAO_RODAPE, FONTE_PEQUENA);
            parTitulo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parTitulo);
            
            Paragraph parItemDescricao = new Paragraph(produto.getNome(), FONTE_GRANDE);
            parItemDescricao.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parItemDescricao);
                
            Paragraph parItemValor = new Paragraph(Decimal.toString(produto.getValorVenda()), FONTE_MEDIA);
            parItemValor.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parItemValor);

            pdfDocument.add(Chunk.NEWLINE);

            String dataHoraEmissao = DateTime.toString(DateTime.getNow());
            Paragraph dataHora = new Paragraph(dataHoraEmissao, FONTE_PEQUENA);
            dataHora.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(dataHora);

            Paragraph parRodape = new Paragraph(Ouroboros.SISTEMA_ASSINATURA, FONTE_MICRO);
            parRodape.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parRodape);
            
            String margemCorte = "";
            for (int i=0; i < Ouroboros.IMPRESSORA_CUPOM_MARGEM_CORTE; i++) {
                margemCorte += System.lineSeparator();
            }
            margemCorte += "-";
            Paragraph parMargemCorte = new Paragraph(margemCorte, FONTE_PEQUENA);
            parMargemCorte.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parMargemCorte);
            
        } catch (DocumentException | FileNotFoundException e) {
            System.err.println(e);
            
        } finally {
            pdfDocument.close();
        }

    }
    
}
