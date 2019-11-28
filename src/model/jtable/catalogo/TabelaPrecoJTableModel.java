/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.catalogo;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.catalogo.TabelaPreco;

/**
 *
 * @author ivand
 */
public class TabelaPrecoJTableModel extends AbstractTableModel {

    private final List<TabelaPreco> dados;
    private final String[] colunas = {"Id", "Nome", "Variação"};

    public TabelaPrecoJTableModel() {
        dados = new ArrayList<>();
    }

    public TabelaPrecoJTableModel(List<TabelaPreco> tabelaPrecoList) {
        dados = tabelaPrecoList;
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
        TabelaPreco tabelaPreco = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return tabelaPreco.getId();
            case 1:
                return tabelaPreco.getNome();
            case 2:
                return tabelaPreco.getTabelaPrecoVariacoesFormatada();
            
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        TabelaPreco tabelaPreco = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                tabelaPreco.setId((int) aValue);
                break;
            case 1:
                tabelaPreco.setNome((String) aValue);
                break;
            case 2:
                //tabelaPreco.setDescricao((String) aValue);
                break;
            
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(TabelaPreco aValue, int rowIndex) {
        TabelaPreco tabelaPreco = dados.get(rowIndex);

        tabelaPreco = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public TabelaPreco getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(TabelaPreco tabelaPreco) {
        dados.add(tabelaPreco);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(TabelaPreco oldTabelaPreco, TabelaPreco newTabelaPreco) {
        int index = dados.indexOf(oldTabelaPreco);
        dados.set(index, newTabelaPreco);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<TabelaPreco> tabelasPreco) {
        int oldCount = getRowCount();

        dados.addAll(tabelasPreco);

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
