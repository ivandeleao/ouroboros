/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.time.LocalDate;
import model.dao.principal.ConstanteDAO;
import static ouroboros.Ouroboros.SISTEMA_VALIDADE;

/**
 *
 * @author ivand
 */
public class Sistema {
    
    public static LocalDate getValidade() {
        
        String validade = ConstanteDAO.getValor("SISTEMA_VALIDADE");
        
        if(validade == null) {
            new ConstanteDAO().bootstrap();
        } else if(validade.equals("0000-00-00")) {
            validade = "2019-03-20";
            setValidade(validade);
        }
        
        return LocalDate.parse(validade);
        
    }
    
    public static void setValidade(String validade) {
        SISTEMA_VALIDADE = LocalDate.parse(validade);
        
    }
    
    public static String gerarValidadeCripto(LocalDate validade) {
        int hojeDia = LocalDate.now().getDayOfYear();
        System.out.println("hojeDia: " + hojeDia);
        int validadeDia = validade.getDayOfYear();
        System.out.println("validadeDia: " + validadeDia);
        int cripto = hojeDia + validadeDia;
        
        return String.valueOf(cripto);
    }

}
