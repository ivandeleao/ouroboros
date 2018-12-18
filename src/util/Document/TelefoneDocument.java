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
 * formato: (19) 98888-0000 ou (19) 8888-0000
 *
 * @author ivand
 */
public class TelefoneDocument extends PlainDocument {

    public static final int NUMERO_DIGITOS_MAXIMO = 15;

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

            if (s.length() >= 0) {
                s.insert(0, "(");
            }

            if (s.length() >= 3) {
                s.insert(3, ") ");
            }

            if (s.length() >= 9) {
                s.insert(9, "-");
            }

            if (s.length() >= 15) {
                s.replace(9, 10, "");
                s.insert(10, "-");
            }

            super.insertString(0, s.toString(), a);

        }
    }

    @Override
    public void remove(int offset, int length) throws BadLocationException {
        super.remove(offset, length);
        String texto = getText(0, getLength());
        texto = texto.replaceAll("[^0-9]", "");
        StringBuilder s = new StringBuilder(texto);
        if (s.length() > 0) {
            s.insert(0, "(");
        }
        if (s.length() > 3) {
            s.insert(3, ") ");
        }
        if (s.length() > 9) {
            s.insert(9, "-");
        }
        super.remove(0, getLength());
        super.insertString(0, s.toString(), null);
    }

}
