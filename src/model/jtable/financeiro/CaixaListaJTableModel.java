/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.financeiro;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.bean.principal.Caixa;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class CaixaListaJTableModel extends AbstractTableModel {
    private final List<Caixa> dados;
    private final String[] colunas = {"Id", "Abertura", "Encerramento", "Período"};

    public CaixaListaJTableModel() {
        dados = new ArrayList<>();
    }

    public CaixaListaJTableModel(List<Caixa> caixas) {
        dados = caixas;
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
        Caixa caixa = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return caixa.getId();
            case 1:
                return DateTime.toString(caixa.getCriacao());
            case 2:
                return DateTime.toString(caixa.getEncerramento());
            case 3:
                return caixa.getCaixaPeriodo().getNome();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Caixa caixa = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                caixa.setId((int) aValue);
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

    public void setValueAt(Caixa aValue, int rowIndex) {
        Caixa caixa = dados.get(rowIndex);

        caixa = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Caixa getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Caixa caixa) {
        dados.add(caixa);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Caixa oldItem, Caixa newItem) {
        int index = dados.indexOf(oldItem);
        dados.set(index, newItem);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Caixa> caixas) {
        int oldCount = getRowCount();

        dados.addAll(caixas);

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
