/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.jTableFormat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author ivand
 * 
 * Para funcionar tem que ter esse método no tableModel:
 * @Override
    public Class getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }
 * 
 */
public class LineWrapCellRenderer extends JTextArea implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {
        this.setText((String) value);
        this.setWrapStyleWord(true);
        this.setLineWrap(true);
        
        int fontHeight = this.getFontMetrics(this.getFont()).getHeight();
        //int textLength = this.getText().length();
        
        int count = 0, fromIndex = 0;
        
        while ((fromIndex = this.getText().indexOf("\r\n", fromIndex)) != -1 ){
            count++;
            fromIndex++;
        }
        
        int lines = count + 1; //textLength / 50 + 1;//+1, cause we need at least 1 row.           
        int height = fontHeight * lines + 10;
        //System.out.println("height: " + height);
        table.setRowHeight(row, height);
        this.setFont(table.getFont());
        
        if(isSelected) {
            this.setBackground((Color)UIManager.get("Table.selectionBackground"));
        } else {
            this.setBackground((Color)UIManager.get("Table.background"));
        }
        
        return this;
    }
}
