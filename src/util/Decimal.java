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
public class Decimal {

    /**
     * 
     * @param valor
     * @return BigDecimal.ZERO se o valor for nulo
     */
    public static BigDecimal parse(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }
    
    /**
     * Converts strings from brazillian format decimal values to BigDecimal
     *
     * @param value
     * @param separadorPonto indica se o separador decimal é ponto. O padrão é vírgula.
     * @return
     */
    private static BigDecimal fromString(String value, boolean separadorPonto) {
        try {
            if(value == null) {
                return BigDecimal.ZERO;
            }
            
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            if(separadorPonto) {
                symbols.setGroupingSeparator(',');
                symbols.setDecimalSeparator('.');
            } else {
                symbols.setGroupingSeparator('.');
                symbols.setDecimalSeparator(',');
            }
            String pattern = "#,##0.00";
            DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
            decimalFormat.setParseBigDecimal(true);

            return (BigDecimal) decimalFormat.parse(value);

        } catch (ParseException ex) {
            //System.err.println(ex);
            return BigDecimal.ZERO;
        }
    }
    
    public static BigDecimal fromStringComPonto(String value) {
        return fromString(value, true);
    }
    
    public static BigDecimal fromString(String value) {
        return fromString(value, false);
    }

    /**
     * 
     * @param valor
     * @param maximoCasasDecimais
     * @return sem casas decimais se o valor for inteiro, ou valor com o maximoCasasDecimais
     */
    public static String toStringDescarteDecimais(BigDecimal valor, Integer maximoCasasDecimais) {
        
        if(valor.compareTo(new BigDecimal(valor.toBigInteger())) == 0) {
            maximoCasasDecimais = 0;
        }
        
        return toString(valor, maximoCasasDecimais);
    }
    
    /**
     * Represents in a brazilian-format string a Decimal value
     * @param value
     * @return 
     */
    public static String toString(BigDecimal value) {
        return toString(value, 2);
    }

    public static String toString(BigDecimal value, Integer decimalPlaces) {
        BigDecimal bd;
        try {
            bd = value.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
        } catch (Exception e) {
            bd = BigDecimal.ZERO;
        }
        
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(decimalPlaces);
        decimalFormat.setMinimumFractionDigits(decimalPlaces);
        decimalFormat.setGroupingUsed(false);
        
        return decimalFormat.format(bd);
    }
    
    public static String toStringComPonto(BigDecimal value) {
        return toString(value, 2).replace(",", ".");
    }
    
    public static String toStringComPonto(BigDecimal value, Integer decimalPlaces) {
        return toString(value, decimalPlaces).replace(",", ".");
    }
    
    public static String porExtenso(BigDecimal valor) {
        return new Extenso(valor).toString();
    }
}
