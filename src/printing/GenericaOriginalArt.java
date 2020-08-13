/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package printing;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.documento.Venda;
import static ouroboros.Ouroboros.EMPRESA_ENDERECO;
import static ouroboros.Ouroboros.EMPRESA_NOME_FANTASIA;
import util.DateTime;
import util.Decimal;
import util.MwIOFile;
import util.Texto;
import util.Sistema;

/**
 *
 * @author ivand
 */
public class GenericaOriginalArt {

    public static void gerarCupom(Venda documento) {
        try {

            List<String> linhas = new ArrayList<>();

            //pedido
            linhas.add(Texto.padLeftAndCut(documento.getId().toString(), 40));

            //cliente
            String cliente = documento.getPessoa() != null ? documento.getPessoa().getNome() : "";
            linhas.add(Texto.substring(cliente, 0, 25));

            //data
            linhas.add(Texto.padLeftAndCut(DateTime.toStringDate(documento.getDataHora()), 40));

            //endereço e vencimento
            String clienteEndereco = documento.getPessoa() != null ? documento.getPessoa().getEnderecoSimples() : "";
            LocalDate vencimento = !documento.getParcelasAPrazo().isEmpty() ? documento.getParcelasAPrazo().get(0).getVencimento() : null;
            linhas.add(
                    Texto.padRightAndCut(clienteEndereco, 25)
                    + Texto.padLeftAndCut(DateTime.toString(vencimento), 15)
            );

            //espaço
            linhas.add("");
            linhas.add("");
            linhas.add("");

            //contato e fone
            //endereço e vencimento
            String clienteContato = documento.getPessoa() != null ? documento.getPessoa().getResponsavelNome() : "";
            String clienteFone = documento.getPessoa() != null ? documento.getPessoa().getTelefone1() : "";
            linhas.add(
                    Texto.padRightAndCut(clienteContato, 25)
                    + Texto.padLeftAndCut(clienteFone, 15)
            );

            //espaço
            linhas.add("");
            linhas.add("");
            linhas.add("");
            linhas.add("");
            linhas.add("");

            //observação
            linhas.addAll(Texto.fatiar(documento.getObservacao(), 40, 2));

            //espaço
            linhas.add("");
            linhas.add("");
            linhas.add("");

            //itens
            for (MovimentoFisico mf : documento.getMovimentosFisicos()) {
                linhas.add(
                        Texto.padRightAndCut(mf.getDescricao(), 20)
                        + Texto.padLeftAndCut(Decimal.toStringDescarteDecimais(mf.getSaida()), 4)
                        + Texto.padLeftAndCut(Decimal.toString(mf.getValor()), 6)
                );
            }

            String caminho = "toPrinter//VENDATXT.txt";

            MwIOFile.writeFile(linhas, caminho);

        } catch (Exception e) {
            System.out.println("Erro ao imprimir. " + e);
        }
    }

    private static String negrito(String texto) {
        return (char) 27 + (char) 69 + texto + (char) 27 + (char) 70;
    }

    private static String condensado(String texto) {
        return (char) 27 + (char) 15 + texto + (char) 27 + (char) 80;
    }

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
