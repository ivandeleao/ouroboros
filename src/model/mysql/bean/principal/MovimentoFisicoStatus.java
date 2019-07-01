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
    
    AGUARDANDO, //Antes de qualquer processo
    ANDAMENTO, //Realizando o serviço ou produção

    PREPARAÇÃO_PREVISTA, //Não usado por enquanto. A ideia é indicar quando é prevista a conclusão
    PREPARAÇÃO_ATRASADA, 
    PREPARAÇÃO_CONCLUÍDA,

    ENTREGA_PREVISTA, //saída da mercadoria
    ENTREGA_ATRASADA, 
    ENTREGA_CONCLUÍDA,

    DEVOLUÇÃO_PREVISTA, //Aluguel
    DEVOLUÇÃO_ATRASADA,
    DEVOLUÇÃO_CONCLUÍDA,

    RECEBIMENTO_PREVISTO, //entrada da mercadoria
    RECEBIMENTO_ATRASADO,
    RECEBIMENTO_CONCLUÍDO,
    
    ESTORNADO,
    ESTORNO
}
