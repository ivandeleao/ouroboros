/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package printing;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import model.bean.principal.Venda;
import static ouroboros.Ouroboros.EMPRESA_ENDERECO;
import static ouroboros.Ouroboros.EMPRESA_NOME_FANTASIA;
import util.MwString;

/**
 *
 * @author ivand
 */
public class Generica {

    public static String gerarCupom(Venda venda) {
        String negrito = (char) 27 + (char) 69 + "Texto negrito" + (char) 27 + (char) 70;

        ArrayList<String> linhas = new ArrayList<>();

        linhas.add(EMPRESA_NOME_FANTASIA);
        linhas.add(EMPRESA_ENDERECO);
        linhas.add("----------------------------------------");
        linhas.add(negrito);

        return linhas.toString();
    }

    public static void print() {
        try {
            //Abertura da impressora 
            FileOutputStream fos = new FileOutputStream("COM3");
            PrintWriter ps = new PrintWriter(fos);

            String texto = "DANILA SABADINI - CIA DE ARTES "
                    + "DANILA SABADINI - CIA DE ARTES "
                    + "DANILA SABADINI - CIA DE ARTES "
                    + "DANILA SABADINI - CIA DE ARTES ";
            
            String teste = "Danila Sabadini - Cia de Artes ";

            //Impressao
            ps.print("\n" + (char) 27 + (char) 69 + MwString.removeAccents(texto) + (char) 27 + (char) 70);
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
