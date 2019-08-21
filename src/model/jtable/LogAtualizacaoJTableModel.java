/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.nosql.LogAtualizacaoItem;
import util.DateTime;

/**
 *
 * @author ivand
 */
public class LogAtualizacaoJTableModel extends AbstractTableModel {
    private final List<LogAtualizacaoItem> dados;
    private final String[] colunas = {"Data", "Descrição"};

    public LogAtualizacaoJTableModel() {
        dados = new ArrayList<>();
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
        LogAtualizacaoItem logAtualizacaoItem = dados.get(rowIndex);

        
        switch (columnIndex) {
            case 0:
                return DateTime.toString(logAtualizacaoItem.getData());
            case 1:
                return logAtualizacaoItem.getDescricao();
                
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        LogAtualizacaoItem logAtualizacaoItem = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                break;
            case 1:
                //logAtualizacaoItem.setNome((String) aValue);
                break;
            case 2:
                //logAtualizacaoItem.setDescricao((String) aValue);
                break;
            
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(LogAtualizacaoItem aValue, int rowIndex) {
        LogAtualizacaoItem logAtualizacaoItem = dados.get(rowIndex);

        logAtualizacaoItem = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public LogAtualizacaoItem getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(LogAtualizacaoItem logAtualizacaoItem) {
        dados.add(logAtualizacaoItem);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(LogAtualizacaoItem oldCliente, LogAtualizacaoItem newCliente) {
        int index = dados.indexOf(oldCliente);
        dados.set(index, newCliente);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<LogAtualizacaoItem> logAtualizacaoItems) {
        int oldCount = getRowCount();

        dados.addAll(logAtualizacaoItems);

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
