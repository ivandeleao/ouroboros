/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.documento;

import java.awt.Color;
import util.Cor;

/**
 * 
 * @author ivand
 */
public enum VendaStatus {
    //ORÇAMENTO, AGUARDANDO, ANDAMENTO, PRONTO, ENTREGUE, RECEBIDO, CANCELADO
    
    ORÇAMENTO("ORÇAMENTO"),
    AGUARDANDO("AGUARDANDO"), 
    ANDAMENTO("ANDAMENTO"), 

    PREPARAÇÃO_PREVISTA("PP"), //
    PREPARAÇÃO_ATRASADA("PA"), 
    PREPARAÇÃO_CONCLUÍDA("PRONTO"),

    ENTREGA_PREVISTA("EP"), //
    ENTREGA_ATRASADA("EA"), 
    ENTREGA_CONCLUÍDA("ENTREGUE"),

    DEVOLUÇÃO_PREVISTA("DP"), 
    DEVOLUÇÃO_ATRASADA("DA"),
    DEVOLUÇÃO_CONCLUÍDA("DEVOLVIDO"),

    RECEBIMENTO_PREVISTO("RP"), //
    RECEBIMENTO_ATRASADO("RA"),
    RECEBIMENTO_CONCLUÍDO("RECEBIDO"),
    
    CANCELADO("CANCELADO")
    //ESTORNADO,
    //ESTORNO
    ;
    
    private String sigla;
    
    private VendaStatus(String sigla) {
        this.sigla = sigla;
    }

    @Override
    public String toString() {
        return sigla;
    }
    
    public Color getCor() {
        switch(this){
            case ORÇAMENTO:
                return Cor.AMARELO;
                
            case ANDAMENTO:
                return Cor.LARANJA;
                
            case PREPARAÇÃO_CONCLUÍDA:
                return Cor.AZUL;
                
            case ENTREGA_CONCLUÍDA:
                return Cor.VERDE;
                
            default:
                return Cor.CINZA;
        }
    }
    
    public Integer getId() {
        return this.ordinal();
    }
    
    public static VendaStatus getById(Integer id){
        return VendaStatus.values()[id];
    }
    
    
}

