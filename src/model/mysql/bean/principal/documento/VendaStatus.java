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
    
    ORÇAMENTO("ORÇAMENTO", 0),
    AGUARDANDO("AGUARDANDO", 10), 
    ANDAMENTO("ANDAMENTO", 20), 

    PREPARAÇÃO_PREVISTA("PP", 0), //
    PREPARAÇÃO_ATRASADA("PA", 0), 
    PREPARAÇÃO_CONCLUÍDA("PRONTO", 30),

    ENTREGA_PREVISTA("EP", 0), //
    ENTREGA_ATRASADA("EA", 0), 
    ENTREGA_CONCLUÍDA("ENTREGUE", 50),

    DEVOLUÇÃO_PREVISTA("DP", 0), 
    DEVOLUÇÃO_ATRASADA("DA", 0),
    DEVOLUÇÃO_CONCLUÍDA("DEVOLVIDO", 0),

    RECEBIMENTO_PREVISTO("RP", 0), //
    RECEBIMENTO_ATRASADO("RA", 0),
    RECEBIMENTO_CONCLUÍDO("RECEBIDO", 0),
    
    CANCELADO("CANCELADO", 0),
    //ESTORNADO,
    //ESTORNO
    
    LIBERADO("LIBERADO", 40)
    ;
    
    private String sigla;
    private int ordem;
    
    private VendaStatus(String sigla, int ordem) {
        this.sigla = sigla;
        this.ordem = ordem;
    }

    @Override
    public String toString() {
        return sigla;
    }
    
    public Color getCor() {
        switch(this){
            case ORÇAMENTO:
                return Cor.AMARELO;
                
            case AGUARDANDO:
                return Cor.CINZA;
                
            case ANDAMENTO:
                return Cor.LARANJA;
                
            case PREPARAÇÃO_CONCLUÍDA:
                return Cor.AZUL;
                
            case LIBERADO:
                return Cor.VERMELHO_CLARO;
                
            case ENTREGA_CONCLUÍDA:
                return Cor.VERDE;
                
            default:
                return Cor.CINZA_CLARO;
        }
    }
    
    public Integer getId() {
        return this.ordinal();
    }
    
    public static VendaStatus getById(Integer id){
        return VendaStatus.values()[id];
    }
    
    public int getOrdem() {
        return ordem;
    }
    
}

