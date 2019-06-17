/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package printing;

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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.ImpressoraFormato;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.MovimentoFisico;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.IMPRESSORA_FORMATO_PADRAO;
import static ouroboros.Ouroboros.SAT_MARGEM_DIREITA;
import static ouroboros.Ouroboros.SAT_MARGEM_ESQUERDA;
import static ouroboros.Ouroboros.SAT_MARGEM_INFERIOR;
import static ouroboros.Ouroboros.SAT_MARGEM_SUPERIOR;
import util.DateTime;
import util.Decimal;
import util.Sistema;

/**
 *
 * @author ivand
 */
public class CriarPDF {
    static float cupomLargura;
    
    private static Float getLargura() {
        if(IMPRESSORA_FORMATO_PADRAO.equals(ImpressoraFormato.CUPOM_58.toString())) {
            return cupomLargura = 58;
        } else {
            return cupomLargura = 80;
        }
    }
    
    
    public static void gerarVenda(Venda venda, String pdfFilePath) {
        //Ajustar altura de acordo com conteúdo
        //https://developers.itextpdf.com/examples/columntext-examples-itext5/adjust-page-size-based-amount-html-data
        com.itextpdf.text.Rectangle rect = new Rectangle(Utilities.millimetersToPoints(getLargura()), Utilities.millimetersToPoints(300));

        final com.itextpdf.text.Font FONT_NORMAL = new Font(FontFamily.UNDEFINED, 8, Font.BOLD, null);
        final com.itextpdf.text.Font FONT_BOLD = new Font(FontFamily.UNDEFINED, 8, Font.BOLD, null);

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
            
            Paragraph vendaId = new Paragraph("VENDA ID. " + venda.getId());
            vendaId.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(vendaId);

            Paragraph cupomTitulo = new Paragraph("TICKET SEM VALOR FISCAL", FONT_BOLD);
            cupomTitulo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(cupomTitulo);

            pdfDocument.add(linebreak);
            
            //Dados do Cliente
            if(venda.getPessoa() != null) {
                Pessoa pessoa = venda.getPessoa();
                Paragraph parClienteNome = new Paragraph("CLIENTE: " + pessoa.getId() + " - " + pessoa.getNome(), FONT_BOLD);
                pdfDocument.add(parClienteNome);
                Paragraph parClienteCpfCnpj = new Paragraph("CPF/CNPJ: " + pessoa.getCpfOuCnpj(), FONT_NORMAL);
                pdfDocument.add(parClienteCpfCnpj);
                Paragraph parClienteEndereco = new Paragraph("ENDEREÇO: " + pessoa.getEnderecoCompleto(), FONT_NORMAL);
                pdfDocument.add(parClienteEndereco);
                Paragraph parClienteTelefone = new Paragraph("TELEFONE: " + pessoa.getTelefone1(), FONT_NORMAL);
                pdfDocument.add(parClienteTelefone);
            }
            //Fim Dados do Cliente

            /**
             * **************
             * INÍCIO DOS ITENS
             */
            Paragraph cabecalhoItens = new Paragraph("#|COD|QTD|UN|VL UN R$|DESC|VL ITEM R$", FONT_NORMAL);
            cabecalhoItens.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
            pdfDocument.add(cabecalhoItens);

            pdfDocument.add(linebreak);

            List<MovimentoFisico> vendaItens = venda.getMovimentosFisicosSaida();
            //List<Map<String,String>> impostos = MwXML.getPairs(doc, "imposto");
            
            //int n = 0;
            for(MovimentoFisico movimentoFisico : vendaItens){
                
                Paragraph itemValores = new Paragraph(
                        String.valueOf(venda.getMovimentosFisicosSaida().indexOf(movimentoFisico) + 1) + 
                        " " + movimentoFisico.getProduto().getCodigo() + 
                        " " + Decimal.toString(movimentoFisico.getSaida(), 3) +  
                        " " + movimentoFisico.getUnidadeComercialVenda() +  
                        " x " + Decimal.toString(movimentoFisico.getValor()),
                        FONT_NORMAL);
                itemValores.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                pdfDocument.add(itemValores);
                
                //desconto sobre item
                if(movimentoFisico.getDescontoPercentual().compareTo(BigDecimal.ZERO) > 0) {
                    Paragraph parItemDesconto = new Paragraph("desconto sobre item", FONT_NORMAL);
                    parItemDesconto.add(glue);
                    parItemDesconto.add("-" + Decimal.toString(movimentoFisico.getDescontoPercentual()) + "%");
                    pdfDocument.add(parItemDesconto);
                }

                String descricao = movimentoFisico.getProduto().getNome();
                
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
            totalBruto.add(Decimal.toString(venda.getTotalItens()));
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
            
            //Acréscimo sobre subtotal
            /*
            String vAcresSubtot = MwXML.getText(doc, "DescAcrEntr", "vAcresSubtot");
            if(vAcresSubtot != null){
                Paragraph acrescimoSubtotal = new Paragraph("Acréscimo sobre subtotal", FONT_NORMAL);
                acrescimoSubtotal.add(new Chunk(new VerticalPositionMark()));
                acrescimoSubtotal.add(new DecimalFormat("0.00").format(Double.parseDouble(vAcresSubtot)));
                pdfDocument.add(acrescimoSubtotal);
            }*/
            
            //Total
            Paragraph total = new Paragraph("TOTAL R$", FONT_BOLD);
            total.add(new Chunk(new VerticalPositionMark()));
            total.add(Decimal.toString(venda.getTotal()));
            pdfDocument.add(total);

            pdfDocument.add(Chunk.NEWLINE);

            //MP - Meios de pagamento
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
            Paragraph troco = new Paragraph("Troco", FONT_NORMAL);
            troco.add(new Chunk(new VerticalPositionMark()));
            troco.add(Decimal.toString(venda.getTroco()));
            pdfDocument.add(troco);

            pdfDocument.add(Chunk.NEWLINE);

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

            Paragraph parAssinaturaB3 = new Paragraph(Ouroboros.SISTEMA_ASSINATURA, FONT_NORMAL);
            parAssinaturaB3.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parAssinaturaB3);
            
        } catch (DocumentException | FileNotFoundException e) {
            System.err.println(e);
            
        } finally {
            pdfDocument.close();
            
        }

    }
    
    public static void gerarRecibo(List<Parcela> parcelas, String pdfFilePath) {
        //Ajustar altura de acordo com conteúdo
        //https://developers.itextpdf.com/examples/columntext-examples-itext5/adjust-page-size-based-amount-html-data
        com.itextpdf.text.Rectangle rect = new Rectangle(Utilities.millimetersToPoints(getLargura()), Utilities.millimetersToPoints(300));

        final com.itextpdf.text.Font FONT_NORMAL = new Font(FontFamily.UNDEFINED, 8, Font.BOLD, null);
        final com.itextpdf.text.Font FONT_BOLD = new Font(FontFamily.UNDEFINED, 8, Font.BOLD, null);

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
            Pessoa cliente = parcelas.get(0).getCliente();
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
            
            for(Parcela parcela : parcelas) {
                //Parcelas
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
                
                for(CaixaItem recebimento : parcela.getRecebimentos()){
                    //Recebimentos
                    Paragraph itemRecebimento = new Paragraph(
                            DateTime.toStringDataAbreviadaLDT(recebimento.getCriacao()) +
                                    " " + recebimento.getMeioDePagamento().getNome()
                    , FONT_NORMAL);
                    
                    itemRecebimento.add(new Chunk(new VerticalPositionMark()));
                    itemRecebimento.add(Decimal.toString(recebimento.getSaldoLinear()));

                    itemRecebimento.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                    pdfDocument.add(itemRecebimento);
                    
                }
            }

            /**
             * **************
             * FIM DOS ITENS
             */
            pdfDocument.add(Chunk.NEWLINE);
            pdfDocument.add(linebreak);
            
            
            //Total
            BigDecimal totalRecebimentos = parcelas.stream().map(Parcela::getValorQuitado).reduce(BigDecimal::add).get();
            
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


            Paragraph rodape = new Paragraph(Ouroboros.MW_WEBSITE, FONT_NORMAL);
            rodape.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(rodape);
        } catch (DocumentException | FileNotFoundException e) {
            System.err.println(e);
        } finally {
            pdfDocument.close();
        }

    }
    
    public static void gerarTicketComanda(Venda venda, String pdfFilePath) {
        //Ajustar altura de acordo com conteúdo
        //https://developers.itextpdf.com/examples/columntext-examples-itext5/adjust-page-size-based-amount-html-data
        com.itextpdf.text.Rectangle rect = new Rectangle(Utilities.millimetersToPoints(getLargura()), Utilities.millimetersToPoints(300));

        final com.itextpdf.text.Font FONT_NORMAL = new Font(FontFamily.UNDEFINED, 8, Font.BOLD, null);
        final com.itextpdf.text.Font FONT_BOLD = new Font(FontFamily.UNDEFINED, 8, Font.BOLD, null);

        com.itextpdf.text.Document pdfDocument = new com.itextpdf.text.Document();

        pdfDocument.setPageSize(rect);
        pdfDocument.setMargins(SAT_MARGEM_ESQUERDA, SAT_MARGEM_DIREITA, SAT_MARGEM_SUPERIOR, SAT_MARGEM_INFERIOR);

        try {
            //Criar PDF
            PdfWriter writer = PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfFilePath));
            
            pdfDocument.open();

            Paragraph parComanda = new Paragraph("COMANDA " + venda.getComanda().toString());
            parComanda.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parComanda);

            Chunk linebreak = new Chunk(new LineSeparator());
            //pdfDocument.add(linebreak);
            
            Paragraph parMensagem = new Paragraph("APRESENTE ESTE TICKET PARA RETIRAR O PEDIDO ");
            parMensagem.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parMensagem);


            String dataHoraEmissao = DateTime.toString(DateTime.getNow());
            Paragraph dataHora = new Paragraph(dataHoraEmissao, FONT_NORMAL);
            dataHora.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(dataHora);

            pdfDocument.add(Chunk.NEWLINE);
            
            Paragraph vendaId = new Paragraph("ID " + venda.getId(), FONT_BOLD);
            vendaId.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(vendaId);


            Paragraph observacoesFisco = new Paragraph(Ouroboros.SISTEMA_ASSINATURA, FONT_NORMAL);
            observacoesFisco.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(observacoesFisco);
        } catch (DocumentException | FileNotFoundException e) {
            System.err.println(e);
        } finally {
            pdfDocument.close();
        }

    }
}
