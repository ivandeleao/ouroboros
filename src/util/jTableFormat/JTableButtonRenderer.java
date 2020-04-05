/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.jTableFormat;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import static ouroboros.Ouroboros.MAIN_VIEW;
import view.documentoSaida.item.VendaView;

/**
 *
 * @author ivand
 */
public class JTableButtonRenderer extends JButton implements TableCellRenderer {

    public JTableButtonRenderer() {

        setOpaque(true);

    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        setText((value == null) ? "" : value.toString()
        );

        return this;

    }
}
