/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.financeiro.cheque;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.financeiro.Cheque;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class ChequePesquisaJTableModel extends AbstractTableModel {

    private final List<Cheque> dados;
    private final String[] colunas = {"Id", "Vencimento", "Banco", "Agência", "Conta", "Número", "Correntista", "CPF/CNPJ", "Valor", "Observação"};

    public ChequePesquisaJTableModel() {
        dados = new ArrayList<>();
    }

    public ChequePesquisaJTableModel(List<Cheque> ChequeList) {
        dados = ChequeList;
    }

    @Override
    public String getColumnName(int column) {
        return colunas[column];
    }

    @Override
    public int getRowCount() {
        return dados.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            Cheque cheque = dados.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return cheque.getId();
                case 1:
                    return DateTime.toStringDataAbreviada(cheque.getVencimento());
                case 2:
                    return cheque.getBanco();
                case 3:
                    return cheque.getAgencia();
                case 4:
                    return cheque.getConta();
                case 5:
                    return cheque.getNumero();
                case 6:
                    return cheque.getCorrentista();
                case 7:
                    return cheque.getCpfCnpj();
                case 8:
                    return Decimal.toString(cheque.getValor());
                case 9:
                    return cheque.getObservacao();
            }
        } catch (Exception e) {
            //nada
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Cheque cheque = dados.get(rowIndex);

        //--
        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(Cheque aValue, int rowIndex) {
        Cheque cheque = dados.get(rowIndex);

        cheque = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Cheque getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Cheque cheque) {
        dados.add(cheque);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Cheque oldItem, Cheque newItem) {
        int index = dados.indexOf(oldItem);
        dados.set(index, newItem);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Cheque> ChequeList) {
        int oldCount = getRowCount();

        dados.addAll(ChequeList);

        fireTableRowsInserted(oldCount, getRowCount() - 1);
    }

    public void clear() {
        dados.clear();
        fireTableDataChanged();
    }

    public boolean isEmpty() {
        return dados.isEmpty();
    }

}
