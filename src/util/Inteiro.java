/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

public class Inteiro {

    public static Integer fromStringOrNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return Integer.valueOf(value);
    }
    
    public static Integer fromStringOrZero(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }

        return Integer.valueOf(value);
    }
    
    public static String toString(Integer valor) {
        return valor != null ? valor.toString() : "";
    }

}
