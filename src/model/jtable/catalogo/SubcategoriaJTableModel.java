/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.catalogo;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.catalogo.Subcategoria;

/**
 *
 * @author ivand
 */
public class SubcategoriaJTableModel extends AbstractTableModel {

    private final List<Subcategoria> dados;
    private final String[] colunas = {"Nome"};

    public SubcategoriaJTableModel() {
        dados = new ArrayList<>();
    }

    public SubcategoriaJTableModel(List<Subcategoria> subcategorias) {
        dados = subcategorias;
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
        Subcategoria subcategoria = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return subcategoria.getNome();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Subcategoria subcategoria = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                subcategoria.setId((int) aValue);
                break;
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(Subcategoria aValue, int rowIndex) {
        //Subcategoria subcategoria = dados.get(rowIndex);

        //subcategoria = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Subcategoria getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Subcategoria subcategoria) {
        dados.add(subcategoria);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Subcategoria oldProduto, Subcategoria newProduto) {
        int index = dados.indexOf(oldProduto);
        dados.set(index, newProduto);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Subcategoria> subcategorias) {
        int oldCount = getRowCount();

        dados.addAll(subcategorias);

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
