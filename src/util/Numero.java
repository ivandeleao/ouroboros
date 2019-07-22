/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

/**
 * Format and convert decimal values for money and other types
 *
 * @author ivand
 */
public class Numero {

    
    public static Integer fromStringToInteger(String value) {
        try {
            return Integer.valueOf(value);

        } catch (NumberFormatException e) {
            return null;
            
        }
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

}
