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
import model.bean.principal.MovimentoFisicoStatus;
import model.bean.principal.VendaStatus;

public class VendasRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        VendaStatus vendaStatus = (VendaStatus) table.getModel().getValueAt(row, table.getColumn("Status").getModelIndex());

        comp.setForeground(Color.BLACK);
        switch (vendaStatus) {
            case ORÇAMENTO:
                comp.setBackground(Color.LIGHT_GRAY);
                comp.setForeground(Color.WHITE);
                break;
            case ANDAMENTO:
                comp.setBackground(new Color(230, 251, 255));
                break;
            case PRONTO:
                comp.setBackground(new Color(230, 255, 230)); //verde
                break;
            case ENTREGUE:
                comp.setBackground(new Color(255, 204, 204)); //vermelho
                break;
            case RECEBIDO:
                comp.setBackground(new Color(230, 251, 255)); //verde
                break;
            case CANCELADO:
                comp.setBackground(Color.RED);
                break;
            default:
                comp.setBackground(Color.WHITE);
        }

        return comp;
    }
}
