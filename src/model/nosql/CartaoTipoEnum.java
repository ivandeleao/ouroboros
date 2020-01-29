/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.nosql;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import util.Cor;

/**
 *
 * @author ivand
 */
public enum CartaoTipoEnum {
    CREDITO("Crédito"), 
    DEBITO("Débito");
    
    private final String name;       

    private CartaoTipoEnum(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
       return this.name;
    }
    
    public Color getCor() {
        switch(this) {
            case CREDITO:
                return Cor.LARANJA;
                
            case DEBITO:
                return Cor.AZUL;
                
            default:
                return Cor.CINZA;
        }
    }
    
    public static List<CartaoTipoEnum> getAll() {
        return Arrays.asList(CartaoTipoEnum.values());
    }
    
}
