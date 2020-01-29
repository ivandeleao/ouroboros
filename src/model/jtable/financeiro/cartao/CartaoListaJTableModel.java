/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.financeiro.cartao;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.financeiro.Cartao;
import util.DateTime;

/**
 *
 * @author ivand
 */
public class CartaoListaJTableModel extends AbstractTableModel {
    private final List<Cartao> dados;
    private final String[] colunas = {"Id", "Data Criação", "Nome", "Dias Receb.", "Parcelamento"};

    public CartaoListaJTableModel() {
        dados = new ArrayList<>();
    }

    public CartaoListaJTableModel(List<Cartao> cartoes) {
        dados = cartoes;
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
        Cartao cartao = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return cartao.getId();
            case 1:
                return DateTime.toString(cartao.getCriacao());
            case 2:
                return cartao.getNome();
            case 3:
                return cartao.getDiasRecebimento();
            case 4:
                return cartao.getCartaoTaxas().size() + "x";
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Cartao cartao = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                cartao.setId((int) aValue);
                break;
            case 1:
                break;
            case 2:
                break;
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(Cartao aValue, int rowIndex) {
        Cartao cartao = dados.get(rowIndex);

        cartao = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Cartao getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Cartao cartao) {
        dados.add(cartao);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Cartao oldItem, Cartao newItem) {
        int index = dados.indexOf(oldItem);
        dados.set(index, newItem);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Cartao> cartoes) {
        int oldCount = getRowCount();

        dados.addAll(cartoes);

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
