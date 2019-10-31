/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.nosql;

import java.awt.Color;
import util.Cor;

/**
 *
 * @author ivand
 */
public enum NfeStatusEnum {
    DIGITAÇÃO, 
    EMITIDA, 
    CANCELADA;
    
    public Color getCor() {
        switch(this) {
            case EMITIDA:
                return Cor.VERDE;
                
            case CANCELADA:
                return Cor.VERMELHO_CLARO;
                
            default:
                return Cor.CINZA;
        }
    }
    
}
