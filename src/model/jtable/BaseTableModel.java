/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author ivand
 * @param <E>
 */
public class BaseTableModel <E> extends AbstractTableModel {
    protected final List<E> dados = new ArrayList<>();
    protected final String[] colunas;

    public BaseTableModel(String[] colunas) {
        this.colunas = colunas;
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
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        
    }

    public void setValueAt(E aValue, int rowIndex) {
        E e = dados.get(rowIndex);

        e = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public E getRow(int rowIndex) {
        return dados.get(rowIndex);
    }
    
    public List<E> getRows(int[] rowIndice) {
        List<E> es = new ArrayList<>();
        
        for (int rowIndex : rowIndice) {
            es.add(dados.get(rowIndex));
        }
        
        return es;
    }

    public void addRow(E e) {
        dados.add(e);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(E antigo, E novo) {
        int index = dados.indexOf(antigo);
        dados.set(index, novo);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<E> es) {
        int oldCount = getRowCount();
        System.out.println("es size: " + es.size());
        dados.addAll(es);

        fireTableRowsInserted(oldCount, getRowCount() - 1);
    }

    public void clear() {
        dados.clear();
        fireTableDataChanged();
    }

    public boolean isEmpty() {
        return dados.isEmpty();
    }
    
    public void refreshRows(int[] rowIndice) {
        for (int rowIndex : rowIndice){
            fireTableRowsUpdated(rowIndex, rowIndex);
        }
    }
}
