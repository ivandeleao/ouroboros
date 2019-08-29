/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.catalogo;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.catalogo.Tamanho;
import model.mysql.bean.fiscal.UnidadeComercial;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class TamanhoJTableModel extends AbstractTableModel {

    private final List<Tamanho> dados;
    private final String[] colunas = {"Nome"};

    public TamanhoJTableModel() {
        dados = new ArrayList<>();
    }

    public TamanhoJTableModel(List<Tamanho> tamanhos) {
        dados = tamanhos;
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
        Tamanho tamanho = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return tamanho.getNome();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Tamanho tamanho = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                tamanho.setId((int) aValue);
                break;
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(Tamanho aValue, int rowIndex) {
        //Tamanho tamanho = dados.get(rowIndex);

        //tamanho = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Tamanho getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Tamanho tamanho) {
        dados.add(tamanho);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Tamanho oldProduto, Tamanho newProduto) {
        int index = dados.indexOf(oldProduto);
        dados.set(index, newProduto);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Tamanho> tamanhos) {
        int oldCount = getRowCount();

        dados.addAll(tamanhos);

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
