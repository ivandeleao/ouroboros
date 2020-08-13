/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 *
 * @author ivand
 */
public class AnoMes {

    /**
     * 
     * @param anoMes
     * @return mês curto / ano curto. Ex: jul/20
     */
    public static String toString(YearMonth anoMes) {
        return anoMes.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault()) + "/" + String.valueOf(anoMes.getYear()).substring(2);
    }
    
}
