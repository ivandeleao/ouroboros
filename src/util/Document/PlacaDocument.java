/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.Document;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * formato: 99999-999
 *
 * @author ivand
 */
public class PlacaDocument extends PlainDocument {

    public static final int NUMERO_DIGITOS_MAXIMO = 8;

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        
        System.out.println("str ini: " + str);
        
        str = str.replaceAll("^-[^a-zA-Z0-9]", "");
        
        String texto = getText(0, getLength());
        
        System.out.println("texto ini: " + texto);

        if (texto.length() < NUMERO_DIGITOS_MAXIMO) {
            

            super.remove(0, getLength());
                
            StringBuilder s = new StringBuilder(texto + str);

            if (s.length() == 3) {
                s.insert(3, "-");
            }

            super.insertString(0, s.toString().toUpperCase(), a);

        }
    }

    @Override
    public void remove(int offset, int length) throws BadLocationException {
        super.remove(offset, length);
        String texto = getText(0, getLength());
        texto = texto.replace("-", "");
        StringBuilder s = new StringBuilder(texto);
        if (s.length() > 3) {
            s.insert(3, "-");
        }
        super.remove(0, getLength());
        super.insertString(0, s.toString(), null);
    }

}
