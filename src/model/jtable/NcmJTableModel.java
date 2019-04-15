/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.fiscal.Ncm;
import model.mysql.bean.fiscal.UnidadeComercial;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class NcmJTableModel extends AbstractTableModel {

    private final List<Ncm> dados;
    private final String[] colunas = {"Código", "Descrição"};

    public NcmJTableModel() {
        dados = new ArrayList<>();
    }

    public NcmJTableModel(List<Ncm> listNcm) {
        dados = listNcm;
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
        Ncm ncm = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return ncm.getCodigo();
            case 1:
                return ncm.getDescricao();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Ncm ncm = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                ncm.setCodigo((String) aValue);
                break;
            case 1:
                ncm.setDescricao((String) aValue);
                break;
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(Ncm aValue, int rowIndex) {
        Ncm ncm = dados.get(rowIndex);

        ncm = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Ncm getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Ncm ncm) {
        dados.add(ncm);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Ncm oldNcm, Ncm newNcm) {
        int index = dados.indexOf(oldNcm);
        dados.set(index, newNcm);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Ncm> listNcm) {
        int oldCount = getRowCount();

        dados.addAll(listNcm);

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
