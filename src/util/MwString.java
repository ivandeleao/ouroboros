/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.text.Normalizer;

/**
 *
 * @author ivand
 */
public class MwString {

    public static String removeAccents(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public static String soNumeros(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^0-9]", "");
    }

    // pad with " " to the right to the given length
    public static String padRight(String s, int length) {
        return String.format("%1$-" + length + "s", s);
    }

    // pad with " " to the left to the given length
    public static String padLeft(String s, int length) {
        return String.format("%1$" + length + "s", s);
    }

    
    /**
     * 
     * @param value
     * @param beginIndex
     * @param endIndex
     * @return subtring ignorando erros por index range
     */
    public static String substring(String value, int beginIndex, int endIndex) {
        if (value.length() < beginIndex) {
            return "";
        } else if (value.length() > endIndex) {
            return value.substring(beginIndex, endIndex);
        } else {
            return value.substring(beginIndex, value.length());
        }
    }

}
