/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.pessoa;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.pessoa.PerfilItem;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class PerfilItemJTableModel extends AbstractTableModel {

    private final List<PerfilItem> dados;
    private final String[] colunas = {"Id", "Item", "Quantidade", "Valor", "+%", "-%", "+$", "-$", "Subtotal"};

    public PerfilItemJTableModel() {
        dados = new ArrayList<>();
    }

    public PerfilItemJTableModel(List<PerfilItem> perfilItens) {
        dados = perfilItens;
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
        PerfilItem perfilItem = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return perfilItem.getId();
            case 1:
                return perfilItem.getGrupoItem().getProduto().getNome();
            case 2:
                return Decimal.toString(perfilItem.getQuantidade(), 3);
            case 3:
                return Decimal.toString(perfilItem.getValor());
            case 4:
                return Decimal.toString(perfilItem.getAcrescimoPercentual());
            case 5:
                return Decimal.toString(perfilItem.getDescontoPercentual());
            case 6:
                return Decimal.toString(perfilItem.getAcrescimoMonetario());
            case 7:
                return Decimal.toString(perfilItem.getDescontoMonetario());
            case 8:
                return Decimal.toString(perfilItem.getSubtotal());
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        PerfilItem perfilItem = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                perfilItem.setId((int) aValue);
                break;
            
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(PerfilItem aValue, int rowIndex) {
        PerfilItem perfilItem = dados.get(rowIndex);

        perfilItem = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public PerfilItem getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(PerfilItem perfilItem) {
        dados.add(perfilItem);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(PerfilItem oldPerfilItem, PerfilItem newPerfilItem) {
        int index = dados.indexOf(oldPerfilItem);
        dados.set(index, newPerfilItem);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<PerfilItem> perfilItens) {
        int oldCount = getRowCount();

        dados.addAll(perfilItens);

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
