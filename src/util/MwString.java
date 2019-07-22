/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * 
     * @param s
     * @param length
     * @return pad with " " to the right to the given length
     */
    public static String padRight(String s, int length) {
        return String.format("%1$-" + length + "s", s).substring(0, length);
    }

    
    /**
     * 
     * @param s
     * @param length
     * @return pad with " " to the left to the given length
     */
    public static String padLeft(String s, int length) {
        return String.format("%1$" + length + "s", s).substring(0, length);
    }
    
    /**
     * 
     * @param s
     * @param length
     * @param caracterPad
     * @return preenche a esquerda com o caracter informado no comprimento informado
     */
    public static String padLeft(String s, int length, Character caracterPad) {
        return padLeft(s, length).replace(' ', caracterPad);
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
    
    public static List<String> fatiar(String value, int largura, int partes) {
        List<String> fatias = new ArrayList<>();
        for(int i = 0; i < partes; i++) {
            String fatia = substring(value, i * largura, i * largura + largura);
            System.out.println("fatia: " + fatia);
            if(!fatia.isEmpty()) {
                fatias.add(fatia);
            }
        }
        
        return fatias;
    }

    public static String formatarCnpj(String cnpj) {
        String str = soNumeros(cnpj);
        //04.615.918/0001-04
        //04.6159.180/001-04
        return str.substring(0, 2) + "." + str.substring(2, 5) + "." + str.substring(5, 8) + "/" + str.substring(8, 12) + "-" + str.substring(12, 14);
        
    }
}
