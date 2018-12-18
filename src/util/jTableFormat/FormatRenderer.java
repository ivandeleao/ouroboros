/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.jTableFormat;

import java.text.Format;
import java.text.DateFormat;
import javax.swing.table.DefaultTableCellRenderer;

/*
 * Use a formatter to format the cell Object
 *
 * Resource: https://tips4java.wordpress.com/2008/10/11/table-format-renderers/
 *              http://www.camick.com/java/source/FormatRenderer.java
 */
public class FormatRenderer extends DefaultTableCellRenderer {

    private Format formatter;

    /*
	 *   Use the specified formatter to format the Object
     */
    public FormatRenderer(Format formatter) {
        this.formatter = formatter;
    }

    @Override
    public void setValue(Object value) {
        //  Format the Object before setting its value in the renderer

        try {
            if (value != null) {
                value = formatter.format(value);
            }
        } catch (IllegalArgumentException e) {
        }

        super.setValue(value);
    }

    /*
	 *  Use the default date/time formatter for the default locale
     */
    public static FormatRenderer getDateTimeRenderer() {
        return new FormatRenderer(DateFormat.getDateTimeInstance());
    }

    /*
	 *  Use the default time formatter for the default locale
     */
    public static FormatRenderer getTimeRenderer() {
        return new FormatRenderer(DateFormat.getTimeInstance());
    }
}
