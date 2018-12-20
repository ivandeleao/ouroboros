/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean.principal;

/**
 * RECEBIDO - COMPRA EFETIVADA
 * DEVOLVIDO - VENDA (ITENS) DEVOLVIDA PELO CLIENTE
 * RECEBIDO - ALUGUEL RETORNADO
 * @author ivand
 */
public enum VendaStatus {
    ORÇAMENTO, AGUARDANDO, ANDAMENTO, PRONTO, ENTREGUE, RECEBIDO, CANCELADO
}

