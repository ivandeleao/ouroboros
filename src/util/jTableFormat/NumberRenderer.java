/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.jTableFormat;

import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.SwingConstants;

/**
 * 
 * @author ivand
 * 
 * Resource: https://tips4java.wordpress.com/2008/10/11/table-format-renderers/
 *              http://www.camick.com/java/source/NumberRenderer.java
 */

public class NumberRenderer extends FormatRenderer {

    /*
	 *  Use the specified number formatter and right align the text
     */
    public NumberRenderer(NumberFormat formatter) {
        super(formatter);
        setHorizontalAlignment(SwingConstants.RIGHT);
    }

    /*
	 *  Use the default currency formatter for the default locale
     */
    public static NumberRenderer getCurrencyRenderer() {
        //return new NumberRenderer(NumberFormat.getCurrencyInstance());
        return new NumberRenderer(NumberFormat.getCurrencyInstance());
    }

    /*
	 *  Use the default integer formatter for the default locale
     */
    public static NumberRenderer getIntegerRenderer() {
        return new NumberRenderer(NumberFormat.getIntegerInstance());
    }

    /*
	 *  Use the default percent formatter for the default locale
     */
    public static NumberRenderer getPercentRenderer() {
        return new NumberRenderer(NumberFormat.getPercentInstance());
    }
    
    public static NumberRenderer getNumberRenderer() {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(2);
        return new NumberRenderer(format);
    }
}

