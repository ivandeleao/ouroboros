/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.bean.fiscal.Cest;

/**
 *
 * @author ivand
 */
public class CestJTableModel extends AbstractTableModel {

    private final List<Cest> dados;
    private final String[] colunas = {"Código", "NCM", "Descrição"};

    public CestJTableModel() {
        dados = new ArrayList<>();
    }

    public CestJTableModel(List<Cest> listCest) {
        dados = listCest;
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
        Cest cest = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return cest.getCodigo();
            case 1:
                return cest.getNcm();
            case 2:
                return cest.getDescricao();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Cest cest = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                cest.setCodigo((String) aValue);
                break;
            case 1:
                cest.setNcm((String) aValue);
                break;
            case 2:
                cest.setDescricao((String) aValue);
                break;
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(Cest aValue, int rowIndex) {
        Cest cest = dados.get(rowIndex);

        cest = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Cest getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Cest cest) {
        dados.add(cest);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Cest oldCest, Cest newCest) {
        int index = dados.indexOf(oldCest);
        dados.set(index, newCest);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Cest> cests) {
        int oldCount = getRowCount();

        dados.addAll(cests);

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
