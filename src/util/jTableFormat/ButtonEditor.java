/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.jTableFormat;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author ivand
 */
public class ButtonEditor extends DefaultCellEditor {

    private String label = "teste";

    public ButtonEditor(JCheckBox checkBox) {

        super(checkBox);

    }

    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {

        //label = (value == null) ? "" : value.toString();

        JButton button = new JButton();
        
        button.setText(label);
        
        button.addActionListener(
                new ActionListener() {

            public void actionPerformed(ActionEvent event) {

                JOptionPane.showMessageDialog(null,
                        "Button Clicked in JTable Cell");

            }

        });

        return button;

    }

    public Object getCellEditorValue() {

        return new String(label);

    }
}
