/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.Diretiva;

/**
 *
 * @author ivand
 */
public class DiretivaJTableModel extends AbstractTableModel {
    private final List<Diretiva> dados;
    private final String[] colunas = {"Id", "Recurso", "Status"};

    public DiretivaJTableModel() {
        dados = new ArrayList<>();
    }

    public DiretivaJTableModel(List<Diretiva> listDiretiva) {
        dados = listDiretiva;
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
        Diretiva diretiva = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return diretiva.getId();
            case 1:
                return diretiva.getRecurso().getNome();
            case 2:
                return diretiva.getStatus();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Diretiva diretiva = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                diretiva.setId((int) aValue);
                break;
            case 1:
                break;
            case 2:
                break;
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(Diretiva aValue, int rowIndex) {
        Diretiva diretiva = dados.get(rowIndex);

        diretiva = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Diretiva getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Diretiva diretiva) {
        dados.add(diretiva);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Diretiva oldItem, Diretiva newItem) {
        int index = dados.indexOf(oldItem);
        dados.set(index, newItem);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(Set<Diretiva> listDiretiva) {
        int oldCount = getRowCount();

        dados.addAll(listDiretiva);

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
