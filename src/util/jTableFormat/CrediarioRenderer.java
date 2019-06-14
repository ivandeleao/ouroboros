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
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import model.mysql.bean.principal.documento.FinanceiroStatus;

public class CrediarioRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        FinanceiroStatus financeiroStatus = (FinanceiroStatus) table.getModel().getValueAt(row, table.getColumn("Status").getModelIndex());
        
        //comp.setForeground(Color.BLACK);
        switch (financeiroStatus) {
            case VENCIDO:
                comp.setBackground(new Color(255, 160, 120));
                //comp.setForeground(Color.BLACK);
                break;
            case QUITADO:
                comp.setBackground(new Color(120, 255, 80));
                //comp.setForeground(Color.BLACK);
                break;
            default: //ABERTO
                comp.setBackground(Color.WHITE);
        }
        
        return comp;
    }
}