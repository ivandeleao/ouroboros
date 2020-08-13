/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package printing.documento;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Utilities;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.Funcionario;
import model.nosql.ImpressoraFormatoEnum;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.Veiculo;
import model.mysql.bean.principal.documento.VendaTipo;
import model.mysql.dao.principal.VendaDAO;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.IMPRESSORA_CUPOM;
import static ouroboros.Ouroboros.IMPRESSORA_FORMATO_PADRAO;
import static ouroboros.Ouroboros.SAT_MARGEM_DIREITA;
import static ouroboros.Ouroboros.SAT_MARGEM_ESQUERDA;
import static ouroboros.Ouroboros.SAT_MARGEM_INFERIOR;
import static ouroboros.Ouroboros.SAT_MARGEM_SUPERIOR;
import static ouroboros.Ouroboros.TO_PRINTER_PATH;
import printing.PrintPDFBox;
import util.DateTime;
import util.Decimal;
import util.Texto;
import util.Sistema;

/**
 *
 * @author ivand
 */
public class BobinaReciboIndividualPrint {
    static float cupomLargura;
    
    private static Float getLargura() {
        if(IMPRESSORA_FORMATO_PADRAO.equals(ImpressoraFormatoEnum.CUPOM_58.toString())) {
            return cupomLargura = 58;
        } else {
            return cupomLargura = 80;
        }
    }
    
    public static void imprimirRecibo(List<CaixaItem> caixaItens) {
        String pdfFilePath = TO_PRINTER_PATH + "RECIBO DE PAGAMENTO_INDIVIDUAL" + System.currentTimeMillis() + ".pdf";
        
        gerarRecibo(caixaItens, pdfFilePath);

        PrintPDFBox.print(pdfFilePath, IMPRESSORA_CUPOM, Ouroboros.IMPRESSORA_RECIBO_VIAS);
        
    }
    
    private static void gerarRecibo(List<CaixaItem> caixaItens, String pdfFilePath) {
        //Ajustar altura de acordo com conteúdo
        //https://developers.itextpdf.com/examples/columntext-examples-itext5/adjust-page-size-based-amount-html-data
        com.itextpdf.text.Rectangle rect = new Rectangle(Utilities.millimetersToPoints(getLargura()), Utilities.millimetersToPoints(300));

        final com.itextpdf.text.Font FONT_NORMAL = new Font(FontFamily.UNDEFINED, Ouroboros.IMPRESSORA_CUPOM_TAMANHO_FONTE, Font.BOLD, null);
        final com.itextpdf.text.Font FONT_BOLD = new Font(FontFamily.UNDEFINED, Ouroboros.IMPRESSORA_CUPOM_TAMANHO_FONTE, Font.BOLD, null);

        com.itextpdf.text.Document pdfDocument = new com.itextpdf.text.Document();

        pdfDocument.setPageSize(rect);
        pdfDocument.setMargins(SAT_MARGEM_ESQUERDA, SAT_MARGEM_DIREITA, SAT_MARGEM_SUPERIOR, SAT_MARGEM_INFERIOR);

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
            /*
            String cnpj = "CNPJ: " + "cnpj";
            String ie = "IE: " + "ie";
            String im = "IM: " + "im";
            Paragraph cnpjIeIm = new Paragraph(cnpj + " " + ie + " " + im, FONT_NORMAL);
            cnpjIeIm.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(cnpjIeIm);
            */
            

            Chunk linebreak = new Chunk(new LineSeparator());
            pdfDocument.add(linebreak);
            
            Paragraph titulo = new Paragraph("RECIBO DE PAGAMENTO");
            titulo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(titulo);

            pdfDocument.add(linebreak);
            
            Paragraph pCliente = new Paragraph("CLIENTE: CONSUMIDOR", FONT_NORMAL);
            Pessoa cliente = caixaItens.get(0).getParcela().getVenda().getPessoa();
            if(cliente != null) {
                pCliente = new Paragraph("CLIENTE: " + cliente.getId() + " " + cliente.getNome(), FONT_NORMAL);
            }
            pdfDocument.add(pCliente);
            
            pdfDocument.add(linebreak);
            
            /**
             * **************
             * INÍCIO DOS RECEBIMENTOS
             */
            Paragraph cabecalhoItens = new Paragraph("VENDA|PARC|VENC|VALOR|MULTA|JUROS", FONT_NORMAL);
            cabecalhoItens.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
            pdfDocument.add(cabecalhoItens);
            
            Paragraph subCabecalhoItens = new Paragraph("RECEBIMENTOS", FONT_NORMAL);
            subCabecalhoItens.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
            pdfDocument.add(subCabecalhoItens);

            pdfDocument.add(linebreak);
            
            for(CaixaItem caixaItem : caixaItens){
                //Parcelas
                Parcela parcela = caixaItem.getParcela();
                Paragraph itemValores = new Paragraph(
                        parcela.getVenda().getId() +
                                " " + parcela.getNumeroDeTotal() +
                                " " + DateTime.toStringDataAbreviada(parcela.getVencimento()) +
                                " " + Decimal.toString(parcela.getValor()) +
                                " " + Decimal.toString(parcela.getMultaCalculada()) +
                                " " + Decimal.toString(parcela.getJurosCalculado())
                , FONT_NORMAL);

                itemValores.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                pdfDocument.add(itemValores);
                
                

                //Recebimentos
                Paragraph itemRecebimento = new Paragraph(
                        DateTime.toStringDataAbreviada(caixaItem.getCriacao()) +
                                " " + caixaItem.getMeioDePagamento().getNome()
                , FONT_NORMAL);

                itemRecebimento.add(new Chunk(new VerticalPositionMark()));
                itemRecebimento.add(Decimal.toString(caixaItem.getSaldoLinear()));

                itemRecebimento.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                pdfDocument.add(itemRecebimento);
                    
                
            }

            /**
             * **************
             * FIM DOS ITENS
             */
            pdfDocument.add(Chunk.NEWLINE);
            pdfDocument.add(linebreak);
            
            
            //Total
            BigDecimal totalRecebimentos = caixaItens.stream().map(CaixaItem::getSaldoLinear).reduce(BigDecimal::add).get();
            
            Paragraph total = new Paragraph("TOTAL RECEBIMENTOS R$", FONT_BOLD);
            total.add(new Chunk(new VerticalPositionMark()));
            total.add(Decimal.toString(totalRecebimentos));
            pdfDocument.add(total);

            pdfDocument.add(Chunk.NEWLINE);


            pdfDocument.add(linebreak);

            String dataHoraEmissao = DateTime.toString(DateTime.getNow());
            Paragraph dataHora = new Paragraph(dataHoraEmissao, FONT_NORMAL);
            dataHora.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(dataHora);

            pdfDocument.add(Chunk.NEWLINE);
            
            
            pdfDocument.add(Chunk.NEWLINE);

            Paragraph parAssinaturaB3 = new Paragraph(Ouroboros.SISTEMA_ASSINATURA, FONT_NORMAL);
            parAssinaturaB3.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parAssinaturaB3);
            
            String margemCorte = "";
            for (int i=0; i < Ouroboros.IMPRESSORA_CUPOM_MARGEM_CORTE; i++) {
                margemCorte += System.lineSeparator();
            }
            margemCorte += "-";
            Paragraph parMargemCorte = new Paragraph(margemCorte, FONT_NORMAL);
            parMargemCorte.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parMargemCorte);
            
        } catch (DocumentException | FileNotFoundException e) {
            System.err.println(e);
        } finally {
            pdfDocument.close();
        }

    }
}
