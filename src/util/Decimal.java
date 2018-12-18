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
     * Converts strings from brazillian format decimal values to BigDecimal
     *
     * @param value
     * @return
     */
    public static BigDecimal fromString(String value) {
        try {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator('.');
            symbols.setDecimalSeparator(',');
            String pattern = "#,##0.00";
            DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
            decimalFormat.setParseBigDecimal(true);

            return (BigDecimal) decimalFormat.parse(value);

        } catch (ParseException ex) {
            System.err.println(ex);
            return null;
        }
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
        BigDecimal bd = value.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
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
}
