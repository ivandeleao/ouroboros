/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.fiscal.SatCupom;
import util.DateTime;

/**
 *
 * @author ivand
 */
public class SatCupomJTableModel extends AbstractTableModel {
    private final List<SatCupom> dados;
    private final String[] colunas = {"Id", "Data", "Chave", "Tipo"};

    public SatCupomJTableModel() {
        dados = new ArrayList<>();
    }

    public SatCupomJTableModel(List<SatCupom> satCupons) {
        dados = satCupons;
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
        SatCupom satCupom = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return satCupom.getId();
            case 1:
                return DateTime.toString(satCupom.getCriacao());
            case 2:
                return satCupom.getChave();
            case 3:
                return satCupom.getSatCupomTipo();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        SatCupom satCupom = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                satCupom.setId((int) aValue);
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
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(SatCupom aValue, int rowIndex) {
        SatCupom satCupom = dados.get(rowIndex);

        satCupom = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public SatCupom getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(SatCupom satCupom) {
        dados.add(satCupom);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(SatCupom oldItem, SatCupom newItem) {
        int index = dados.indexOf(oldItem);
        dados.set(index, newItem);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<SatCupom> satCupons) {
        int oldCount = getRowCount();

        dados.addAll(satCupons);

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
