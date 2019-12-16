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
import static ouroboros.Ouroboros.IMPRESSORA_FORMATO_PADRAO;
import static ouroboros.Ouroboros.SAT_MARGEM_DIREITA;
import static ouroboros.Ouroboros.SAT_MARGEM_ESQUERDA;
import static ouroboros.Ouroboros.SAT_MARGEM_INFERIOR;
import static ouroboros.Ouroboros.SAT_MARGEM_SUPERIOR;
import util.DateTime;
import util.Decimal;
import util.Texto;
import util.Sistema;

/**
 *
 * @author ivand
 */
public class TermicaPrint {
    static float cupomLargura;
    
    private static Float getLargura() {
        if(IMPRESSORA_FORMATO_PADRAO.equals(ImpressoraFormatoEnum.CUPOM_58.toString())) {
            return cupomLargura = 58;
        } else {
            return cupomLargura = 80;
        }
    }
    
    
    public static void gerarVenda(Venda venda, String pdfFilePath) {
        //Ajustar altura de acordo com conteúdo
        //https://developers.itextpdf.com/examples/columntext-examples-itext5/adjust-page-size-based-amount-html-data
        com.itextpdf.text.Rectangle rect = new Rectangle(Utilities.millimetersToPoints(getLargura()), Utilities.millimetersToPoints(300));

        final com.itextpdf.text.Font FONTE_PEQUENA = new Font(FontFamily.UNDEFINED, 6, Font.NORMAL, null);
        final com.itextpdf.text.Font FONT_NORMAL = new Font(FontFamily.UNDEFINED, Ouroboros.IMPRESSORA_CUPOM_TAMANHO_FONTE, Font.BOLD, null);
        final com.itextpdf.text.Font FONT_BOLD = new Font(FontFamily.UNDEFINED, Ouroboros.IMPRESSORA_CUPOM_TAMANHO_FONTE, Font.BOLD, null);

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
            
            //Funcionário
            if(!venda.getVendaTipo().equals(VendaTipo.DELIVERY) && venda.getFuncionario()!= null) {
                Funcionario funcionario = venda.getFuncionario();
                Paragraph parFuncionario = new Paragraph("FUNCIONÁRIO: " + funcionario.getId() + " - " + funcionario.getNome(), FONT_BOLD);
                pdfDocument.add(parFuncionario);
            }
            //Fim Funcionário
            
            //Cliente
            if(venda.getPessoa() != null) {
                Pessoa pessoa = venda.getPessoa();
                Paragraph parClienteNome = new Paragraph("CLIENTE: " + pessoa.getId() + " - " + pessoa.getNome(), FONT_BOLD);
                pdfDocument.add(parClienteNome);
                if(!pessoa.getCpfOuCnpj().isEmpty()) {
                    Paragraph parClienteCpfCnpj = new Paragraph("CPF/CNPJ: " + pessoa.getCpfOuCnpj(), FONT_NORMAL);
                    pdfDocument.add(parClienteCpfCnpj);
                }
                if(!venda.getVendaTipo().equals(VendaTipo.DELIVERY) && !pessoa.getEnderecoCompleto().isEmpty()) {
                    Paragraph parClienteEndereco = new Paragraph("ENDEREÇO: " + pessoa.getEnderecoCompleto(), FONT_NORMAL);
                    pdfDocument.add(parClienteEndereco);
                }
                if(!pessoa.getTelefone1().isEmpty()) {
                    Paragraph parClienteTelefone = new Paragraph("TELEFONE: " + pessoa.getTelefone1(), FONT_NORMAL);
                    pdfDocument.add(parClienteTelefone);
                }
            }
            //Fim Cliente
            
            //Veículo
            if(venda.getVeiculo()!= null) {
                Veiculo veiculo = venda.getVeiculo();
                Paragraph parVeiculo = new Paragraph("VEÍCULO: " + veiculo.getId() + " - " + veiculo.getPlaca() + " - " + veiculo.getModelo(), FONT_BOLD);
                pdfDocument.add(parVeiculo);
            }
            //Fim Veículo

            /**
             * **************
             * INÍCIO DOS ITENS
             */
            if(Ouroboros.IMPRESSORA_CUPOM_EXIBIR_CABECALHO_ITEM) {
                Paragraph cabecalhoItens = new Paragraph("#|COD|QTD|UN|VL UN R$|DESCR|VL ITEM R$", FONT_NORMAL);
                cabecalhoItens.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                pdfDocument.add(cabecalhoItens);
            }
            
            pdfDocument.add(linebreak);

            List<MovimentoFisico> vendaItens = venda.getMovimentosFisicosSaida();
            //List<Map<String,String>> impostos = MwXML.getPairs(doc, "imposto");
            
            //int n = 0;
            for(MovimentoFisico movimentoFisico : vendaItens){
                
                String textoItem = "";
                
                if(Ouroboros.IMPRESSORA_CUPOM_EXIBIR_NUMERO_ITEM) {
                    textoItem += String.valueOf(venda.getMovimentosFisicosSaida().indexOf(movimentoFisico) + 1);
                }
                
                if(Ouroboros.IMPRESSORA_CUPOM_EXIBIR_CODIGO_ITEM) {
                    textoItem += " " + movimentoFisico.getCodigo();
                }
                
                textoItem += " " + Decimal.toStringDescarteDecimais(movimentoFisico.getSaida(), 3);
                
                if(Ouroboros.IMPRESSORA_CUPOM_EXIBIR_UNIDADE_MEDIDA_ITEM) {
                    textoItem += " " + movimentoFisico.getUnidadeComercialVenda();
                }
                
                textoItem += " x " + Decimal.toString(movimentoFisico.getValor());
                        
                Paragraph parTextoItem = new Paragraph(textoItem.trim(), FONT_NORMAL);
                parTextoItem.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                pdfDocument.add(parTextoItem);
                
                //acréscimo sobre item
                if(movimentoFisico.getAcrescimo().compareTo(BigDecimal.ZERO) > 0) {
                    Paragraph parItemAcrescimo = new Paragraph("acréscimo", FONT_NORMAL);
                    parItemAcrescimo.add(glue);
                    parItemAcrescimo.add("+" + movimentoFisico.getAcrescimoFormatado());
                    pdfDocument.add(parItemAcrescimo);
                }
                
                //desconto sobre item
                if(movimentoFisico.getDesconto().compareTo(BigDecimal.ZERO) > 0) {
                    Paragraph parItemDesconto = new Paragraph("desconto", FONT_NORMAL);
                    parItemDesconto.add(glue);
                    parItemDesconto.add("-" + movimentoFisico.getDescontoFormatado());
                    pdfDocument.add(parItemDesconto);
                }

                String descricao = movimentoFisico.getDescricaoItemMontado();
                
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
            
            //Subtotal Bruto Produtos
            if(venda.getTotalItensProdutos().compareTo(BigDecimal.ZERO) > 0) {
                Paragraph totalProdutos = new Paragraph("Subtotal Produtos", FONT_NORMAL);
                totalProdutos.add(new Chunk(new VerticalPositionMark()));
                totalProdutos.add(Decimal.toString(venda.getTotalItensProdutos()));
                pdfDocument.add(totalProdutos);
            }
            
            if(venda.getTotalAcrescimoProdutos().compareTo(BigDecimal.ZERO) > 0) {
                Paragraph acrescimoProdutos = new Paragraph("Acréscimo Produtos", FONT_NORMAL);
                acrescimoProdutos.add(new Chunk(new VerticalPositionMark()));
                acrescimoProdutos.add(venda.getTotalAcrescimoFormatadoProdutos());
                pdfDocument.add(acrescimoProdutos);
            }
            
            if(venda.getTotalDescontoProdutos().compareTo(BigDecimal.ZERO) > 0) {
                Paragraph descontoProdutos = new Paragraph("Desconto Produtos", FONT_NORMAL);
                descontoProdutos.add(new Chunk(new VerticalPositionMark()));
                descontoProdutos.add(venda.getTotalDescontoFormatadoProdutos());
                pdfDocument.add(descontoProdutos);
            }
            
            //Subtotal Bruto Serviços
            if(venda.getTotalItensServicos().compareTo(BigDecimal.ZERO) > 0) {
                Paragraph totalServicos = new Paragraph("Subtotal Serviços", FONT_NORMAL);
                totalServicos.add(new Chunk(new VerticalPositionMark()));
                totalServicos.add(Decimal.toString(venda.getTotalItensServicos()));
                pdfDocument.add(totalServicos);
            }
            
            if(venda.getTotalAcrescimoServicos().compareTo(BigDecimal.ZERO) > 0) {
                Paragraph acrescimoServicos = new Paragraph("Acréscimo Serviços", FONT_NORMAL);
                acrescimoServicos.add(new Chunk(new VerticalPositionMark()));
                acrescimoServicos.add(venda.getTotalAcrescimoFormatadoServicos());
                pdfDocument.add(acrescimoServicos);
            }
            
            if(venda.getTotalDescontoServicos().compareTo(BigDecimal.ZERO) > 0) {
                Paragraph descontoServicos = new Paragraph("Desconto Serviços", FONT_NORMAL);
                descontoServicos.add(new Chunk(new VerticalPositionMark()));
                descontoServicos.add(venda.getTotalDescontoFormatadoServicos());
                pdfDocument.add(descontoServicos);
            }
            
            
            //Total
            Paragraph total = new Paragraph("TOTAL R$", FONT_BOLD);
            total.add(new Chunk(new VerticalPositionMark()));
            total.add(Decimal.toString(venda.getTotal()));
            pdfDocument.add(total);

            pdfDocument.add(Chunk.NEWLINE);

            //MP - Meios de pagamento
            if(!venda.getRecebimentosAgrupadosPorMeioDePagamento().isEmpty()) {
                Paragraph tituloMeiosDePagamento = new Paragraph("MEIOS DE PAGAMENTO", FONT_BOLD);
                tituloMeiosDePagamento.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                pdfDocument.add(tituloMeiosDePagamento);
            }
            
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
            }

            pdfDocument.add(Chunk.NEWLINE);

            //Parcelas / Faturamento
            if(venda.getParcelasAPrazo().size() > 0) {
                Paragraph tituloParcelas = new Paragraph("PARCELAS", FONT_BOLD);
                tituloParcelas.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                pdfDocument.add(tituloParcelas);

                Paragraph cabecalhoParcelas = new Paragraph("#     |VENCIMENTO|", FONT_NORMAL);
                cabecalhoParcelas.add(new Chunk(new VerticalPositionMark()));
                cabecalhoParcelas.add("VALOR");
                pdfDocument.add(cabecalhoParcelas);

                for(Parcela parcela : venda.getParcelasAPrazo()) {
                    Paragraph parParcela = new Paragraph(
                            Texto.padLeft(parcela.getNumero().toString(), 2, '0') +
                                     "         " + DateTime.toStringDataAbreviada(parcela.getVencimento())
                    , FONT_NORMAL);

                    parParcela.add(new Chunk(new VerticalPositionMark()));
                    parParcela.add(Decimal.toString(parcela.getValor()));
                    pdfDocument.add(parParcela);

                }

                pdfDocument.add(Chunk.NEWLINE);
            }
            
            //Observação
            if(!venda.getObservacao().isEmpty()) {
                Paragraph parObservacao = new Paragraph("OBS: " + venda.getObservacao(), FONT_NORMAL);
                pdfDocument.add(parObservacao);
            }
            
            pdfDocument.add(linebreak);
            
            //Delivery----------------------------------------------------------
            if(venda.getValorTroco().compareTo(BigDecimal.ZERO) > 0) {
                Paragraph parDeliveryRecebimento = new Paragraph("Pagto: " + venda.getMeioDePagamento(), FONT_NORMAL);
                parDeliveryRecebimento.add(" " + Decimal.toString(venda.getValorReceber()));
                pdfDocument.add(parDeliveryRecebimento);
                
                Paragraph parDeliveryTroco = new Paragraph("Troco: " + Decimal.toString(venda.getValorTroco()), FONT_BOLD);
                pdfDocument.add(parDeliveryTroco);
            }
            
            if(!venda.getComandaNome().isEmpty()) {
                Paragraph parComandaNome = new Paragraph("Nome: " + venda.getComandaNome(), FONT_BOLD);
                pdfDocument.add(parComandaNome);
            }
            
            if(venda.getVendaTipo().equals(VendaTipo.DELIVERY)) {
                Paragraph parDeliveryEndereco = new Paragraph("Endereço: " + venda.getEnderecoEntrega(), FONT_BOLD);
                pdfDocument.add(parDeliveryEndereco);
                        
                if(venda.getFuncionario()!= null) {
                    Funcionario entregador = venda.getFuncionario();
                    Paragraph parEntregador = new Paragraph("Entregador: " + entregador.getId() + " - " + entregador.getNome(), FONT_BOLD);
                    pdfDocument.add(parEntregador);
                }
            }
            //Fim Delivery------------------------------------------------------
            
            
            if(!venda.getVendaTipo().equals(VendaTipo.DELIVERY)) {
                pdfDocument.add(Chunk.NEWLINE);
                pdfDocument.add(Chunk.NEWLINE);
                pdfDocument.add(Chunk.NEWLINE);
                pdfDocument.add(linebreak);
                Paragraph parAssinatura = new Paragraph("Assinatura do Cliente", FONT_NORMAL);
                parAssinatura.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                pdfDocument.add(parAssinatura);
            }
            

            String dataHoraEmissao = DateTime.toString(DateTime.getNow());
            Paragraph dataHora = new Paragraph(dataHoraEmissao, FONT_NORMAL);
            dataHora.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(dataHora);

            pdfDocument.add(Chunk.NEWLINE);
            
            if(!Ouroboros.IMPRESSAO_RODAPE.isEmpty()) {
                Paragraph parRodape = new Paragraph(Ouroboros.IMPRESSAO_RODAPE, FONT_NORMAL);
                parRodape.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                pdfDocument.add(parRodape);
                
                pdfDocument.add(Chunk.NEWLINE);
            }

            Paragraph parAssinaturaB3 = new Paragraph(Ouroboros.SISTEMA_ASSINATURA, FONTE_PEQUENA);
            parAssinaturaB3.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parAssinaturaB3);
            
            venda.setUltimaImpressaoCupom(LocalDateTime.now());
            
            new VendaDAO().save(venda);
            
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
                            DateTime.toStringDataAbreviada(recebimento.getCriacao()) +
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
    
    public static void gerarTicketComanda(Venda venda, String pdfFilePath) {
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


            Paragraph parAssinaturaB3 = new Paragraph(Ouroboros.SISTEMA_ASSINATURA, FONT_NORMAL);
            parAssinaturaB3.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            pdfDocument.add(parAssinaturaB3);
            
        } catch (DocumentException | FileNotFoundException e) {
            System.err.println(e);
        } finally {
            pdfDocument.close();
        }

    }
}
