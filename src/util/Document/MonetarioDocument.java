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
public class MonetarioDocument extends PlainDocument {

    public static int NUMERO_DIGITOS_MAXIMO = 12;
    private int digitos = 3;

    public MonetarioDocument() {
    }

    public MonetarioDocument(int digitos) {
        this.digitos = digitos;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        String texto = getText(0, getLength());
        
        boolean temVirgula = texto.chars().filter(ch -> ch == ',').count() > 0;
        
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if ((!Character.isDigit(c) && c != ',') || (temVirgula && c == ',')) {
                return;
            }
        }

        if (texto.length() < this.NUMERO_DIGITOS_MAXIMO) {
            super.remove(0, getLength());
            StringBuffer s = new StringBuffer(texto + str);

            
            super.insertString(0, s.toString(), a);

        }
    }
    
    

}
