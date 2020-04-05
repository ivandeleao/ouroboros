/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 * Format and convert decimal values for money and other types
 *
 * @author ivand
 */
public class Numero {

    /**
     * 
     * @param value
     * @return Integer, 0 se for nulo ou vazio
     */
    public static Integer fromStringToInteger(String value) {
        if (value == null) {
            return 0;
        }
        if (value.trim().isEmpty()) {
            return 0;
        }

        return Integer.valueOf(value);
    }

    public static Short fromStringToShort(String value) {
        try {
            return Short.valueOf(value);

        } catch (NumberFormatException ex) {
            return null;

        }
    }

    public static String toString(Short valor) {
        return valor != null ? valor.toString() : "";
    }

    public static String toString(Integer valor) {
        return valor != null ? valor.toString() : "";
    }

}
