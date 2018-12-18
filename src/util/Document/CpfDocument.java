/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.Document;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import util.MwString;

/**
 * formato: 999.999.999-99
 *
 * @author ivand
 */
public class CpfDocument extends PlainDocument {

    public static final int NUMERO_DIGITOS_MAXIMO = 14;

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        str = str.replaceAll("[^0-9]", "");
        
        String texto = getText(0, getLength());

        //ignorar não números
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isDigit(c)) {
                return;
            }
        }

        if (texto.length() < this.NUMERO_DIGITOS_MAXIMO) {
            super.remove(0, getLength());
            texto = texto.replaceAll("[^0-9]", "");
            StringBuilder s = new StringBuilder(texto + str);

            if (s.length() >= 3) {
                s.insert(3, ".");
            }

            if (s.length() >= 7) {
                s.insert(7, ".");
            }

            if (s.length() >= 11) {
                s.insert(11, "-");
            }

            super.insertString(0, s.toString(), a);

        }
    }

    @Override
    public void remove(int offset, int length) throws BadLocationException {
        super.remove(offset, length);
        String texto = getText(0, getLength());
        texto = texto.replace(".", "");
        texto = texto.replace("-", "");
        StringBuilder s = new StringBuilder(texto);
        if (s.length() > 3) {
            s.insert(3, ".");
        }
        if (s.length() > 7) {
            s.insert(7, ".");
        }
        if (s.length() > 11) {
            s.insert(11, "-");
        }
        super.remove(0, getLength());
        super.insertString(0, s.toString(), null);
    }

}
