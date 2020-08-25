/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.funcionario;

import model.jtable.BaseTableModel;
import model.mysql.bean.principal.ComissaoPagamento;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class FuncionarioComissaoPagamentoHistoricoTableModel extends BaseTableModel {


    public FuncionarioComissaoPagamentoHistoricoTableModel() {
        super(new String[]{"Id", "Data Hora", "Conta/Caixa", "MP", "Lançamento", "Comissão", "Estornado"});
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ComissaoPagamento cp = (ComissaoPagamento) dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return cp.getCaixaItem().getId();
            case 1:
                return DateTime.toString(cp.getCaixaItem().getDataHoraRecebimento());
            case 2:
                return cp.getCaixaItem().getContaCaixa();
            case 3:
                return cp.getCaixaItem().getMeioDePagamento().getSigla();
            case 4:
                return Decimal.toString(cp.getCaixaItem().getDebito());
            case 5:
                return Decimal.toString(cp.getValor());
            case 6:
                return cp.isEstornado();
                
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        /*ComissaoPagamento cp = (ComissaoPagamento) dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                cp.setId((int) aValue);
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
        }*/

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }


}
