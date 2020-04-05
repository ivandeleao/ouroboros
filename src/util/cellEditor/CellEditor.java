/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.cellEditor;

import util.jTableFormat.*;
import java.awt.Font;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import util.Document.MonetarioDocument;

/**
 *
 * @author ivand
 */
public class CellEditor {

    public CellEditorModel decimal(Font font) {
        JFormattedTextField textField = new JFormattedTextField();
        CellEditorModel cellEditor = new CellEditorModel(textField);
        textField.setHorizontalAlignment(SwingConstants.RIGHT);
        textField.setFont(font);
        textField.setDocument(new MonetarioDocument());
        return cellEditor;
    }
    
    
    
    public class CellEditorModel extends DefaultCellEditor {
        public CellEditorModel(JFormattedTextField textField) {
            super(textField);
        }
    }
    
    
}
