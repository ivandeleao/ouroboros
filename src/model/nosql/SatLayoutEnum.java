/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.nosql;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ivand
 */
public enum SatLayoutEnum {
    V007("0.07"), 
    V008("0.08");
    
    private final String name;       

    private SatLayoutEnum(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
       return this.name;
    }
    
    
    public static List<SatLayoutEnum> getAll() {
        return Arrays.asList(SatLayoutEnum.values());
    }
    
}
