/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal;

/**
 *
 * @author ivand
 */
public enum MovimentoFisicoStatus {
    //NORMAL, PREVISTO, EFETIVO, ATRASADO, ESTORNADO, ESTORNO
    
    AGUARDANDO, 
    ANDAMENTO, 

    PREPARAÇÃO_PREVISTA, //
    PREPARAÇÃO_ATRASADA, 
    PREPARAÇÃO_CONCLUÍDA,

    ENTREGA_PREVISTA, //
    ENTREGA_ATRASADA, 
    ENTREGA_CONCLUÍDA,

    DEVOLUÇÃO_PREVISTA, 
    DEVOLUÇÃO_ATRASADA,
    DEVOLUÇÃO_CONCLUÍDA,

    RECEBIMENTO_PREVISTO, //
    RECEBIMENTO_ATRASADO,
    RECEBIMENTO_CONCLUÍDO,
    
    ESTORNADO,
    ESTORNO
}
