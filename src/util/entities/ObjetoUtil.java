/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.entities;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.mysql.dao.principal.financeiro.CaixaItemDAO;

/**
 *
 * @author ivand
 */
public class ObjetoUtil {
    
    public static <T> Object copiar(Object origem) {
        
        try {
            T destino = (T) origem.getClass().newInstance();
            
            for(Field field : origem.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    System.out.println(field.toString() + field.get(origem));
                    
                    //ignorar bags
                    if (!field.getName().equals("id") && 
                            !Collection.class.isAssignableFrom(field.getType())) {
                        field.set(destino, field.get(origem));
                    }
                    
                    /*if (field.getName().equals("id")) {
                        field.set(destino, null);
                    }*/
                    
                    
                    
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(CaixaItemDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            return destino;
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(ObjetoUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /*
    public static Object copiar(Object objeto, Object destino) {
        
        
        for(Field field : objeto.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                System.out.println(field.toString() + field.get(objeto));
                
                //ignorar bags
                if (!Collection.class.isAssignableFrom(field.getType())) {
                    field.set(destino, field.get(objeto));
                }
                
                
                
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(CaixaItemDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return destino;
    }
    */
    
}
