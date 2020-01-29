/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.financeiro.cartao;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.financeiro.CartaoTaxa;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class CartaoTaxasJTableModel extends AbstractTableModel {
    private final List<CartaoTaxa> dados;
    private final String[] colunas = {"Parcelas", "Taxa", "Cobrar do Consumidor"};

    public CartaoTaxasJTableModel() {
        dados = new ArrayList<>();
    }

    public CartaoTaxasJTableModel(List<CartaoTaxa> cartaoTaxas) {
        dados = cartaoTaxas;
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
    public Class getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CartaoTaxa cartaoTaxa = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return cartaoTaxa.getParcelas();
            case 1:
                return Decimal.toString(cartaoTaxa.getTaxa());
            case 2:
                return cartaoTaxa.isTaxaCartaoInclusa();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        CartaoTaxa cartaoTaxa = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                cartaoTaxa.setId((int) aValue);
                break;
            case 1:
                break;
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(CartaoTaxa aValue, int rowIndex) {
        CartaoTaxa cartaoTaxa = dados.get(rowIndex);

        cartaoTaxa = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public CartaoTaxa getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(CartaoTaxa cartaoTaxa) {
        dados.add(cartaoTaxa);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(CartaoTaxa oldItem, CartaoTaxa newItem) {
        int index = dados.indexOf(oldItem);
        dados.set(index, newItem);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<CartaoTaxa> cartaoTaxas) {
        int oldCount = getRowCount();

        dados.addAll(cartaoTaxas);

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
