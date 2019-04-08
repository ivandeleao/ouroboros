/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.math.BigInteger;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import model.dao.principal.ConstanteDAO;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.SISTEMA_CHAVE;

/**
 *
 * @author ivand
 */
public class Sistema {
    
    public static boolean checkValidade() {
        if(getValidade() == null) {
            return false;
        }
        
        return LocalDate.now().compareTo(getValidade()) <= 0;
    }
    
    public static LocalDate getValidade() {
        LocalDate validade = null;
        
        try{
        
            String chaveHex = ConstanteDAO.getValor("SISTEMA_CHAVE");
            
            String chave = chaveHex.split("-")[0];
            String dvEntrada = chaveHex.split("-")[1];

            String decode = new BigInteger(chave, 16).toString();
            System.out.println(decode);

            int ano = Integer.valueOf(decode.substring(0, 4));
            int mes = Integer.valueOf(decode.substring(4, 6));
            int dia = Integer.valueOf(decode.substring(6, 8));

            validade = LocalDate.of(ano, mes, dia);

        } catch(Exception e) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Erro ao validar " + e, "Erro", JOptionPane.ERROR_MESSAGE);
        }
        
        return validade;
    }
    
    public static void setValidade(LocalDate validade) {
        //SISTEMA_CHAVE = validade;
        
    }
    
    private String gerarDV(String base) {
        Integer dv = 0;
        for(int n = 0; n < base.length(); n++) {
            String s = base.substring(n, n+1);
            System.out.println("s: " + s);
            dv += Integer.valueOf(s);
            System.out.println("dv: " + dv);
        }
        
        return dv.toString();
    }

}
