/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.jTableFormat;

/**
 *
 * @author ivand
 */
import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import util.Decimal;

public class TableRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        BigDecimal recebido = Decimal.fromString(table.getModel().getValueAt(row, 4).toString());
        
        if(recebido.compareTo(BigDecimal.ZERO) > 0) {
            comp.setBackground(new Color(100, 200, 50));
        } else {
            comp.setBackground(Color.WHITE);
        }
        
        
        return comp;
    }
}