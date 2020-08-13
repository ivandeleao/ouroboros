/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package printing;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.Funcionario;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.Veiculo;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.documento.VendaTipo;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.dao.principal.VendaDAO;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.EMPRESA_ENDERECO;
import static ouroboros.Ouroboros.EMPRESA_NOME_FANTASIA;
import util.DateTime;
import util.Decimal;
import util.Texto;
import util.Sistema;

/**
 *
 * @author ivand
 */
public class Generica {

    public static void print(Venda documento) {
        List<String> txt = new ArrayList<>();

        int largura = 48;

        txt.add(Texto.padCenterAndCut(Ouroboros.EMPRESA_NOME_FANTASIA, largura));
        txt.add(Texto.padCenterAndCut(Ouroboros.EMPRESA_RAZAO_SOCIAL, largura));

        txt.add(Texto.padCenterAndCut(Sistema.getEnderecoCompleto(), largura));
        txt.add(Texto.padCenterAndCut(Ouroboros.EMPRESA_TELEFONE, largura));

        txt.add(Texto.padRightAndCut("-", largura, '-'));

        txt.add(Texto.padCenterAndCut("ID " + documento.getId() + " - " + DateTime.toString(documento.getDataHora()), largura));
        txt.add(Texto.padCenterAndCut("TICKET SEM VALOR FISCAL", largura));

        txt.add(Texto.padRightAndCut("-", largura, '-'));

        //Funcionário
        Funcionario funcionario = documento.getFuncionario();
        if (!documento.getVendaTipo().equals(VendaTipo.DELIVERY) && funcionario != null) {
            txt.add(Texto.padRightAndCut("FUNCIONARIO: " + documento.getFuncionario().getId() + " - " + documento.getFuncionario().getNome(), largura));
        }
        //Fim Funcionário

        //Cliente
        if (documento.getPessoa() != null) {
            Pessoa pessoa = documento.getPessoa();

            txt.add(Texto.padRightAndCut("CLIENTE: " + pessoa.getId() + " - " + pessoa.getNome(), largura));
            if (!pessoa.getCpfOuCnpj().isEmpty()) {
                txt.add(Texto.padRightAndCut("CPF/CNPJ: " + pessoa.getCpfOuCnpj(), largura));
            }
            if (!documento.getVendaTipo().equals(VendaTipo.DELIVERY) && !pessoa.getEnderecoCompleto().isEmpty()) {
                txt.add(Texto.padRightAndCut("ENDEREÇO: " + pessoa.getEnderecoCompleto(), largura));
            }
            if (!pessoa.getTelefone1().isEmpty()) {
                txt.add(Texto.padRightAndCut("TELEFONE: " + pessoa.getTelefone1(), largura));
            }
        }
        //Fim Cliente

        //Veículo
        if (documento.getVeiculo() != null) {
            Veiculo veiculo = documento.getVeiculo();
            txt.add(Texto.padRightAndCut("VEÍCULO: " + veiculo.getId() + " - " + veiculo.getPlaca() + " - " + veiculo.getModelo(), largura));
        }
        //Fim Veículo

        /**
         * **************
         * INÍCIO DOS ITENS
         */
        if (Ouroboros.IMPRESSORA_CUPOM_EXIBIR_CABECALHO_ITEM) {
            txt.add(Texto.padRightAndCut("#|COD|DESCR|QTD|UN|VL UN R$|VL ITEM R$", largura));
        }

        txt.add(Texto.padRightAndCut("-", largura, '-'));

        List<MovimentoFisico> itens = documento.getMovimentosFisicosSaida();

        for (MovimentoFisico movimentoFisico : itens) {

            String textoItem = "";

            if (Ouroboros.IMPRESSORA_CUPOM_EXIBIR_NUMERO_ITEM) {
                textoItem += String.valueOf(documento.getMovimentosFisicosSaida().indexOf(movimentoFisico) + 1);
            }

            if (Ouroboros.IMPRESSORA_CUPOM_EXIBIR_CODIGO_ITEM) {
                textoItem += " " + movimentoFisico.getCodigo();
            }

            textoItem += " " + movimentoFisico.getDescricaoItemMontado(); //2020-02-26 - penhão peixes pediu - concatenada a descrição na mesma linha

            textoItem += " " + Decimal.toStringDescarteDecimais(movimentoFisico.getSaida(), 3);

            if (Ouroboros.IMPRESSORA_CUPOM_EXIBIR_UNIDADE_MEDIDA_ITEM) {
                textoItem += " " + movimentoFisico.getUnidadeComercialVenda();
            }

            textoItem += " x " + Decimal.toString(movimentoFisico.getValor());

            //parTextoItem.add(Decimal.toString(movimentoFisico.getSubtotal()));
            List<String> listItem = Texto.fatiar(textoItem.trim(), largura, 5);
            //última parte do item concatenar com o valor à direita
            String ultima = listItem.get(listItem.size() - 1);
            String subtotal = Decimal.toString(movimentoFisico.getSubtotal());
            if (ultima.length() < largura - 10) {
                listItem.set(listItem.size() - 1, ultima + Texto.padLeftAndCut(subtotal, largura - ultima.length()));
                txt.addAll(listItem);

            } else {
                txt.addAll(listItem);
                txt.add(Texto.padLeftAndCut(subtotal, largura));
            }

        }

        /**
         * **************
         * FIM DOS ITENS
         */
        txt.add(Texto.padRightAndCut("-", largura, '-'));

        //Subtotal Bruto Produtos
        if (documento.getTotalItensProdutos().compareTo(BigDecimal.ZERO) > 0) {
            txt.add(Texto.padLeftAndCutComPrefixo("Subtotal Produtos", Decimal.toString(documento.getTotalItensProdutos()), largura));
        }
        
        if (documento.getTotalAcrescimoProdutosMonetarioOuPercentual().compareTo(BigDecimal.ZERO) > 0) {
            txt.add(Texto.padLeftAndCutComPrefixo("Acréscimo Produtos", documento.getTotalAcrescimoFormatadoProdutos(), largura));
        }
        
        if(documento.getTotalDescontoProdutosMonetarioOuPercentual().compareTo(BigDecimal.ZERO) > 0) {
            txt.add(Texto.padLeftAndCutComPrefixo("Desconto Produtos", documento.getTotalDescontoFormatadoProdutos(), largura));
        }
        
        
        
        //Subtotal Bruto Serviços
        if(documento.getTotalItensServicos().compareTo(BigDecimal.ZERO) > 0) {
            txt.add(Texto.padLeftAndCutComPrefixo("Subtotal Serviços", Decimal.toString(documento.getTotalItensServicos()), largura));
        }

        if(documento.getTotalAcrescimoServicosMonetarioOuPercentual().compareTo(BigDecimal.ZERO) > 0) {
            txt.add(Texto.padLeftAndCutComPrefixo("Acréscimo Serviços", documento.getTotalAcrescimoFormatadoServicos(), largura));
        }

        if(documento.getTotalDescontoServicosMonetarioOuPercentual().compareTo(BigDecimal.ZERO) > 0) {
            txt.add(Texto.padLeftAndCutComPrefixo("Desconto Serviços", documento.getTotalDescontoFormatadoServicos(), largura));
        }

        //Total
        txt.add(Texto.padLeftAndCutComPrefixo("TOTAL R$", Decimal.toString(documento.getTotal()), largura));
        
        
        //MP - Meios de pagamento
        if(Ouroboros.IMPRESSORA_CUPOM_EXIBIR_MEIOS_PAGAMENTO) {
            if(!documento.getRecebimentosAgrupadosPorMeioDePagamento().isEmpty()) {
                txt.add(Texto.padCenterAndCut("MEIOS DE PAGAMENTO", largura));

                for (Map.Entry<MeioDePagamento, BigDecimal> entry : documento.getRecebimentosAgrupadosPorMeioDePagamento().entrySet()) {
                    String cMP = entry.getKey().getNome();
                    txt.add(Texto.padLeftAndCutComPrefixo(cMP, Decimal.toString(entry.getValue()), largura));
                }

                txt.add("");
            }

            //Troco
            if(documento.getTroco().compareTo(BigDecimal.ZERO) > 0) {
                txt.add(Texto.padLeftAndCutComPrefixo("Troco", Decimal.toString(documento.getTroco()), largura));
                
                txt.add("");
            }
        }
        
        
        //Parcelas / Faturamento
        if(documento.getParcelasAPrazo().size() > 0) {
            txt.add(Texto.padCenterAndCut("PARCELAS", largura));

            txt.add(Texto.padLeftAndCutComPrefixo("#   |VENCIMENTO|", "VALOR", largura));

            for(Parcela parcela : documento.getParcelasAPrazo()) {
                String numero = Texto.padLeftAndCut(parcela.getNumero().toString(), 2, '0');
                String vencimento = DateTime.toStringDataAbreviada(parcela.getVencimento());
                
                txt.add(Texto.padLeftAndCutComPrefixo(numero + "    " + vencimento, Decimal.toString(parcela.getValor()), largura));
            }

            txt.add("");
        }
        
        
        //Observação
        if(!documento.getObservacao().isEmpty()) {
            txt.add(Texto.padRightAndCut("OBS: " + documento.getObservacao(), largura));
            
            txt.add(Texto.padRightAndCut("-", largura, '-'));
        }

        //Saldo devedor-----------------------------------------------------
        if (documento.getPessoa() != null) {
            BigDecimal totalEmAtraso = documento.getPessoa().getTotalEmAtraso();
            if (totalEmAtraso.compareTo(BigDecimal.ZERO) > 0) {
                txt.add(Texto.padCenterAndCut(">> TOTAL VENCIDO: " + Decimal.toString(totalEmAtraso) + " <<", largura));

                txt.add(Texto.padRightAndCut("-", largura, '-'));
            }
        }
        //Fim Saldo devedor-------------------------------------------------
        
        
        //Delivery----------------------------------------------------------
        if(documento.getValorTroco().compareTo(BigDecimal.ZERO) > 0) {
            txt.add(Texto.padRightAndCut("Pagto: " + documento.getMeioDePagamento() + " " + Decimal.toString(documento.getValorReceber()), largura));
            txt.add(Texto.padRightAndCut("Troco: " + Decimal.toString(documento.getValorTroco()), largura));
        }

        if(!documento.getComandaNome().isEmpty()) {
            txt.add(Texto.padRightAndCut("Nome: " + documento.getComandaNome(), largura));
        }

        if(documento.getVendaTipo().equals(VendaTipo.DELIVERY)) {
            txt.add(Texto.padRightAndCut("Endereço: " + documento.getEnderecoEntrega(), largura));

            if(documento.getFuncionario()!= null) {
                Funcionario entregador = documento.getFuncionario();
                txt.add(Texto.padRightAndCut("Entregador: " + entregador.getId() + " - " + entregador.getNome(), largura));
            }
        }
        //Fim Delivery------------------------------------------------------


        if(Ouroboros.IMPRESSORA_CUPOM_EXIBIR_ASSINATURA_CLIENTE) {
            txt.add("");
            txt.add("");
            txt.add("");
            txt.add(Texto.padRightAndCut("-", largura, '-'));
            txt.add(Texto.padCenterAndCut("Assinatura do Cliente", largura));
        }

        
        String dataHoraImpressao = DateTime.toString(DateTime.getNow());
        txt.add(Texto.padCenterAndCut("Impresso em " + dataHoraImpressao, largura));
        txt.add("");
        
        
        if(!Ouroboros.IMPRESSAO_RODAPE.isEmpty()) {
            
            List<String> listItem = Texto.fatiar(Ouroboros.IMPRESSAO_RODAPE, largura, 5);
            
            listItem.replaceAll(i -> Texto.padCenterAndCut(i, largura));
            
            txt.addAll(listItem);
            
            txt.add("");
        }
        
        
        txt.add(Texto.padCenterAndCut(Ouroboros.SISTEMA_ASSINATURA, largura));
        
        
        String margemCorte = "";
        for (int i=0; i < Ouroboros.IMPRESSORA_CUPOM_MARGEM_CORTE; i++) {
            margemCorte += System.lineSeparator();
        }
        margemCorte += "-";

        txt.add(Texto.padCenterAndCut(margemCorte, largura));

        
        
        documento.setUltimaImpressaoCupom(LocalDateTime.now());
        new VendaDAO().save(documento);
        
        
        

        String texto = "";
        for (String t : txt) {
            //System.out.println(t);
            texto += t + "\n";
        }

        texto = Texto.removerAcentos(texto);

        System.out.println(texto);

        PrintString.print(texto, Ouroboros.IMPRESSORA_CUPOM);
    }

    /*public static String gerarCupom(Venda venda) {
        String negrito = (char) 27 + (char) 69 + "Texto negrito" + (char) 27 + (char) 70;

        ArrayList<String> linhas = new ArrayList<>();

        linhas.add(EMPRESA_NOME_FANTASIA);
        linhas.add(Sistema.getEnderecoCompleto());
        linhas.add("----------------------------------------");
        linhas.add(negrito);

        return linhas.toString();
    }*/

 /*public static void print() {
        try {
            //Abertura da impressora 
            FileOutputStream fos = new FileOutputStream("COM4");
            PrintWriter ps = new PrintWriter(fos);

            String texto = "DANILA SABADINI - CIA DE ARTES "
                    + "DANILA SABADINI - CIA DE ARTES "
                    + "DANILA SABADINI - CIA DE ARTES "
                    + "DANILA SABADINI - CIA DE ARTES ";
            
            String teste = "Danila Sabadini - Cia de Artes ";

            //Impressao
            //ps.print("\n" + (char) 27 + (char) 69 + Texto.removeAccents(texto) + (char) 27 + (char) 70);
            ps.print("\n" + (char) 27 + (char) 69 + "Texto negrito" + (char) 27 + (char) 70);
            ps.print("\n" + (char) 27 + (char) 69 + "Texto negrito" + (char) 27 + (char) 70);
            ps.print("\n" + (char) 27 + (char) 69 + "Texto negrito" + (char) 27 + (char) 70);
            ps.print("\n" + (char) 27 + (char) 69 + teste + (char) 27 + (char) 70);
            
            
            ps.print("\n\n\n\n\n\n\n\n\n\n\n\n");

            //fim da impressao 
            ps.close();
        } catch (Exception e) {
            System.out.println("Erro");
        }
    }

    private static String negrito(String texto) {
        return (char) 27 + (char) 69 + texto + (char) 27 + (char) 70;
    }

    private static String condensado(String texto) {
        return (char) 27 + (char) 15 + texto + (char) 27 + (char) 80;
    }*/

 /* Códigos bematech mp-4200 th
    http://bematechpartners.com.br/wiki/index.php/2016/08/12/formatacao-de-texto-com-comando-direto-mp-4200-th-java/
    ps.print("\n" + (char) 27 + (char) 15 + "Texto condensado" + (char) 27 + (char) 80);
    ps.print("\n" + (char) 27 + (char) 69 + "Texto negrito" + (char) 27 + (char) 70);
    ps.print("\n" + (char) 27 + (char) 86 + "Texto expandido dupla-altura" + (char) 27 + (char) 86);
    ps.print("\n" + (char) 27 + (char) 14 + "Texto expandido dupla-largura" + (char) 27 + (char) 14);
    ps.print("\n" + (char) 27 + (char) 52 + "Texto italico" + (char) 27 + (char) 53);
    ps.print("\n" + (char) 27 + (char) 14 + (char) 27 + (char) 86 + "Texto expandido dupla-altura-largura" + (char) 27 + (char) 53);
    ps.print("" + (char) 13 + (char) 10 + (char) 13 + (char) 10); // pula linha (2 vezes)
    ps.print("" + (char) 27 + (char) 119); // aciona guilhotina
    
    
     */
}
