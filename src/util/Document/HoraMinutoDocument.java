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
 *
 * @author ivand Resources:
 * http://javafree.uol.com.br/topic-855866-JFormattedTextField-com-formato-decimal.html
 */
public class HoraMinutoDocument extends PlainDocument {

    public static final int NUMERO_DIGITOS_MAXIMO = 5;
    private int digitos = 4;

    public HoraMinutoDocument() {
    }

    public HoraMinutoDocument(int casasDecimais) {
        digitos = casasDecimais + 1; // 0,00 -> 3 dígitos
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        str = str.replace(":", "");

        String texto = getText(0, getLength());

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isDigit(c)) {
                return;
            }
        }

        if (texto.length() <= this.NUMERO_DIGITOS_MAXIMO) {
            super.remove(0, getLength());
            texto = texto.replace(":", "");
            StringBuffer s = new StringBuffer(texto + str);

            while (s.length() > 0 && s.charAt(0) == '0') {
                s.deleteCharAt(0);
            }

            String zeros = "";
            for (int i = digitos; i > s.length(); i--) {
                zeros += "0";
            }
            s.insert(0, zeros);

            s.insert(s.length() - digitos + 2, ":");


            super.insertString(0, s.toString(), a);

        }
    }

    @Override
    public void remove(int offset, int length) throws BadLocationException {
        super.remove(offset, length);
        String texto = getText(0, getLength());
        texto = texto.replace(":", "");
        super.remove(0, getLength());
        insertString(0, texto, null);
    }
    
    

}
