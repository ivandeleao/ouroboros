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
public enum TipoCalculoEnum {
    
    PERCENTUAL("Percentual", "%", Cor.AZUL), VALOR("Valor", "$", Cor.LARANJA);
    
    private final String nome;       
    private final String simbolo;
    private final Color cor;

    private TipoCalculoEnum(String nome, String simbolo, Color cor) {
        this.nome = nome;
        this.simbolo = simbolo;
        this.cor = cor;
    }

    public boolean equalsName(String otherName) {
        return nome.equals(otherName);
    }

    public String toString() {
       return this.nome;
    }
    
    public String getSimbolo() {
        return this.simbolo;
    }
    
    public Color getCor() {
        return this.cor;
    }
    
    public static TipoCalculoEnum fromSimbolo(String simbolo) {
        if (simbolo.equals(TipoCalculoEnum.VALOR.getSimbolo())) {
            return TipoCalculoEnum.VALOR;
            
        } else if (simbolo.equals(TipoCalculoEnum.PERCENTUAL.getSimbolo())) {
            return TipoCalculoEnum.PERCENTUAL;
            
        } else {
            return null;
        }
    }
    
    
}
