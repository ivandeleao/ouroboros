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
public enum ContaTipoEnum {
    CAIXA("Caixa"), 
    CONTA_CORRENTE("Conta Corrente");
    
    private final String name;       

    private ContaTipoEnum(String s) {
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
            case CAIXA:
                return Cor.VERDE;
                
            case CONTA_CORRENTE:
                return Cor.AZUL;
                
            default:
                return Cor.CINZA;
        }
    }
    
    public static List<ContaTipoEnum> getAll() {
        return Arrays.asList(ContaTipoEnum.values());
    }
    
}
