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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.ImpressoraFormato;
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
import util.Sistema;
import view.Toast;

public class TicketCozinhaPrint {

    static float cupomLargura;
    
    private static Float getLargura() {
        if(IMPRESSORA_FORMATO_PADRAO.equals(ImpressoraFormato.CUPOM_58.toString())) {
            return cupomLargura = 58;
        } else {
            return cupomLargura = 80;
        }
    }
    
    public static void imprimirCupom(Venda venda) {
        String pdfFilePath = TO_PRINTER_PATH + "TICKET COZINHA " + venda.getId() + " - TIME " + System.currentTimeMillis() + ".pdf";
        
        gerarCupom(venda, pdfFilePath);

        PrintPDFBox.print(pdfFilePath, IMPRESSORA_CUPOM);
        
    }
    
    private static void gerarCupom(Venda venda, String pdfFilePath) {
        //Ajustar altura de acordo com conteúdo
        //https://developers.itextpdf.com/examples/columntext-examples-itext5/adjust-page-size-based-amount-html-data
        com.itextpdf.text.Rectangle rect = new Rectangle(Utilities.millimetersToPoints(getLargura()), Utilities.millimetersToPoints(300));

        final com.itextpdf.text.Font FONT_NORMAL = new Font(Font.FontFamily.COURIER, 8, Font.BOLD, null);
        final com.itextpdf.text.Font FONT_BOLD = new Font(Font.FontFamily.COURIER, 8, Font.BOLD, null);

        com.itextpdf.text.Document pdfDocument = new com.itextpdf.text.Document();

        pdfDocument.setPageSize(rect);
        pdfDocument.setMargins(SAT_MARGEM_ESQUERDA, SAT_MARGEM_DIREITA, SAT_MARGEM_SUPERIOR, SAT_MARGEM_INFERIOR);

        Chunk glue = new Chunk(new VerticalPositionMark());
        
        try {
            //Criar PDF
            PdfWriter writer = PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfFilePath));
            
            pdfDocument.open();

            
            

            Chunk linebreak = new Chunk(new LineSeparator());
            pdfDocument.add(linebreak);
            
            Paragraph parComanda = new Paragraph("COMANDA " + venda.getComanda());
            parComanda.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parComanda);
            
            Paragraph vendaId = new Paragraph("ID " + venda.getId());
            vendaId.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(vendaId);

            pdfDocument.add(linebreak);
            
            /**
             * **************
             * INÍCIO DOS ITENS
             */
            Paragraph cabecalhoItens = new Paragraph("#|COD|QTD|DESCRICAO", FONT_NORMAL);
            cabecalhoItens.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
            pdfDocument.add(cabecalhoItens);

            pdfDocument.add(linebreak);

            List<MovimentoFisico> vendaItens = venda.getMovimentosFisicosSaida();
            //List<Map<String,String>> impostos = MwXML.getPairs(doc, "imposto");
            
            //int n = 0;
            for(MovimentoFisico movimentoFisico : vendaItens){
                
                Paragraph itemValores = new Paragraph(
                        String.valueOf(venda.getMovimentosFisicosSaida().indexOf(movimentoFisico) + 1) + 
                        " " + Decimal.toString(movimentoFisico.getSaida(), 3) + 
                        " x " + movimentoFisico.getProduto().getNome(),
                        FONT_NORMAL);
                itemValores.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                pdfDocument.add(itemValores);
                
            }

            /**
             * **************
             * FIM DOS ITENS
             */
            
            
            //Observação
            if(venda.getObservacao() != null) {
                Paragraph parObservacao = new Paragraph("OBS: " + venda.getObservacao(), FONT_NORMAL);
                parObservacao.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                pdfDocument.add(parObservacao);
            }

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
