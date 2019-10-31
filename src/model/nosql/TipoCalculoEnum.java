/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.nosql;

/**
 *
 * @author ivand
 */
public enum TipoCalculoEnum {
    
    PERCENTUAL("Percentual"), VALOR("Valor");
    
    private final String name;       

    private TipoCalculoEnum(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
       return this.name;
    }
    
}
