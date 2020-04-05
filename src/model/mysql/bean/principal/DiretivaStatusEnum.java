/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal;

/**
 *
 * @author ivand
 */
public enum DiretivaStatusEnum {
    BLOQUEADO("Bloqueado"), LIBERADO("Liberado");
    
    
    //string para enum
    private final String name;       

    private DiretivaStatusEnum(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false 
        return name.equals(otherName);
    }

    public String toString() {
       return this.name;
    }
}
