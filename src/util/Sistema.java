/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import javax.swing.JOptionPane;
import model.bean.principal.Constante;
import model.dao.principal.ConstanteDAO;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.SISTEMA_CHAVE;

/**
 *
 * @author ivand
 */
public class Sistema {
    
    public static Integer getId() {
        String sistemaId = ConstanteDAO.getValor("SISTEMA_ID");
        if(sistemaId == null || sistemaId.equals("")) {
            return null;
        }
        
        return Integer.valueOf(sistemaId);
    }
    
    public static void setId(Integer id) {
        new ConstanteDAO().save(new Constante("SISTEMA_ID", id.toString()));
    }
    
    public static boolean checkValidade() {
        if(getId() == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Erro ao validar. Sistema sem Id.", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if(getValidade() == null) {
            return false;
        }
        
        //comparar id
        String chaveHex = new ConstanteDAO().findByNome("SISTEMA_CHAVE").getValor();

        String chave = chaveHex.split("-")[0];
        String dvEntrada = chaveHex.split("-")[1];

        String decode = new BigInteger(chave, 16).toString();
        
        int sistemaId = Integer.valueOf(decode.substring(8));
        
        if(sistemaId != getId()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Chave não corresponde ao id!", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public static LocalDate getValidade() {
        LocalDate validade = null;
        
        try{
        
            String chaveHex = ConstanteDAO.getValor("SISTEMA_CHAVE");
            
            if(chaveHex == null || chaveHex.equals("")) {
                return null;
            }
            
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
    
    public static void setChave(String chave) {
        new ConstanteDAO().save(new Constante("SISTEMA_CHAVE", chave));
        Ouroboros.SISTEMA_CHAVE = chave;
    }
    
    private static String gerarDV(String base) {
        Integer dv = 0;
        for(int n = 0; n < base.length(); n++) {
            String s = base.substring(n, n+1);
            System.out.println("s: " + s);
            dv += Integer.valueOf(s);
            System.out.println("dv: " + dv);
        }
        
        return dv.toString();
    }
    
    public static long getValidadeEmDias() {
        if(getValidade() == null) {
            return -999;
        }
        long dias = ChronoUnit.DAYS.between(LocalDate.now(), getValidade());
        
        return dias;
    }

    public static String getEnderecoCompleto() {
        String endereco = Ouroboros.EMPRESA_ENDERECO;
        if(!Ouroboros.EMPRESA_ENDERECO_NUMERO.isEmpty()) {
            endereco += ", " + Ouroboros.EMPRESA_ENDERECO_NUMERO;
        }
        
        if(!Ouroboros.EMPRESA_ENDERECO_COMPLEMENTO.isEmpty()) {
            endereco += " - " + Ouroboros.EMPRESA_ENDERECO_COMPLEMENTO;
        }
        
        if(!Ouroboros.EMPRESA_ENDERECO_BAIRRO.isEmpty()) {
            endereco += " - " + Ouroboros.EMPRESA_ENDERECO_BAIRRO;
        }
        
        return endereco;
    }
}
