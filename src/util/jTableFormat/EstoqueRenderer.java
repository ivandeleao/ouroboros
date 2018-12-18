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

public class EstoqueRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        MovimentoFisicoStatus movimentoFisicoStatus = (MovimentoFisicoStatus) table.getModel().getValueAt(row, table.getColumn("Status").getModelIndex());

        comp.setForeground(Color.BLACK);
        switch (movimentoFisicoStatus) {
            case ESTORNADO:
                comp.setBackground(Color.RED);
                comp.setForeground(Color.WHITE);
                break;
            case ESTORNO:
                comp.setBackground(new Color(120, 120, 120));
                comp.setForeground(Color.WHITE);
                break;
            case PREVISTO:
                comp.setBackground(new Color(255, 255, 120)); //amarelo
                break;
            case ATRASADO:
                comp.setBackground(new Color(255, 204, 204)); //vermelho
                break;
            case EFETIVO:
                comp.setBackground(new Color(230, 255, 230)); //verde
                break;
            case NORMAL:
                comp.setBackground(new Color(230, 251, 255)); //azul
                break;
            default:
                comp.setBackground(Color.WHITE);
        }

        return comp;
    }
}
