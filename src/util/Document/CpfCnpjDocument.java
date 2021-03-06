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
 * formato: (19) 98888-0000 ou (19) 8888-0000 formato: 999.999.999-99 ou
 * 99.999.999/9999-99
 *
 * @author ivand
 */
public class CpfCnpjDocument extends PlainDocument {

    public static final int NUMERO_DIGITOS_MAXIMO = 18;

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

        if (texto.length() < NUMERO_DIGITOS_MAXIMO) {
            super.remove(0, getLength());
            texto = texto.replaceAll("[^0-9]", "");
            StringBuilder s = new StringBuilder(texto + str);

            if (str.length() > 11 || texto.length() >= 11) { //2020-07-02 str é o texto inserido (digitado ou colado)
                //CNPJ
                texto = texto.replaceAll("[^0-9]", "");
                s = new StringBuilder(texto + str);

                s.insert(2, ".");
                s.insert(6, ".");
                s.insert(10, "/");

                if (s.length() >= 15) {
                    s.insert(15, "-");
                }

            } else {
                //CPF
                if (s.length() >= 3) {
                    s.insert(3, ".");
                }

                if (s.length() >= 7) {
                    s.insert(7, ".");
                }

                if (s.length() >= 11) {
                    s.insert(11, "-");
                }
            }

            super.insertString(0, s.toString(), a);

        }
    }

    @Override
    public void remove(int offset, int length) throws BadLocationException {
        super.remove(offset, length);
        String texto = getText(0, getLength());
        texto = texto.replaceAll("[^0-9]", "");
        //texto = texto.replace(".", "");
        //texto = texto.replace("-", "");
        //texto = texto.replace("/", "");
        StringBuilder s = new StringBuilder(texto);

        if (texto.length() <= 11) { //CPF
            if (s.length() > 3) {
                s.insert(3, ".");
            }
            if (s.length() > 7) {
                s.insert(7, ".");
            }
            if (s.length() > 11) {
                s.insert(11, "-");
            }

        } else {
            if (s.length() > 2) {
                s.insert(2, ".");
            }
            if (s.length() > 6) {
                s.insert(6, ".");
            }
            if (s.length() > 10) {
                s.insert(10, "/");
            }
            if (s.length() > 15) {
                s.insert(15, "-");
            }
        }

        super.remove(0, getLength());
        super.insertString(0, s.toString(), null);
    }

}
