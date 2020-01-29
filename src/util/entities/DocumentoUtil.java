/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.entities;

import javax.swing.JInternalFrame;
import model.nosql.LayoutComandasEnum;
import ouroboros.Ouroboros;
import view.documentoSaida.ComandasLadrilhoView;
import view.documentoSaida.ComandasListaView;

/**
 *
 * @author ivand
 */
public class DocumentoUtil {
    
    public static JInternalFrame exibirComandas() {
        
        if(Ouroboros.VENDA_LAYOUT_COMANDAS.equals(LayoutComandasEnum.LADRILHO.toString())) {
            return ComandasLadrilhoView.getSingleInstance();
        } else {
            return ComandasListaView.getSingleInstance();
        }
        
    }
}
