/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.bean.principal.Grupo;

/**
 *
 * @author ivand
 */
public class GrupoJTableModel extends AbstractTableModel {

    private final List<Grupo> dados;
    private final String[] colunas = {"Id", "Nome"};

    public GrupoJTableModel() {
        dados = new ArrayList<>();
    }

    public GrupoJTableModel(List<Grupo> produtos) {
        dados = produtos;
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
        Grupo produto = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return produto.getId();
            case 1:
                return produto.getNome();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Grupo produto = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                produto.setId((int) aValue);
                break;
            case 1:
                produto.setNome((String) aValue);
                break;
            
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(Grupo aValue, int rowIndex) {
        Grupo produto = dados.get(rowIndex);

        produto = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Grupo getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Grupo produto) {
        dados.add(produto);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Grupo oldGrupo, Grupo newGrupo) {
        int index = dados.indexOf(oldGrupo);
        dados.set(index, newGrupo);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Grupo> produtos) {
        int oldCount = getRowCount();

        dados.addAll(produtos);

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
