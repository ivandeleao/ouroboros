/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.financeiro.CaixaItem;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class RecebimentoListaJTableModel extends AbstractTableModel {
    private final List<CaixaItem> dados;
    private final String[] colunas = {"Id", "Data Hora", "Tipo", "Observação", "Meio de Pagamento", "Crédito", "Débito"};

    public RecebimentoListaJTableModel() {
        dados = new ArrayList<>();
    }

    public RecebimentoListaJTableModel(List<CaixaItem> caixaItens) {
        dados = caixaItens;
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
        CaixaItem caixaItem = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return caixaItem.getId();
            case 1:
                return DateTime.toString(caixaItem.getCriacao());
            case 2:
                return caixaItem.getCaixaItemTipo().getNome();
            case 3:
                return caixaItem.getObservacao();
            case 4:
                return caixaItem.getMeioDePagamento().getNome();
            case 5:
                return Decimal.toString(caixaItem.getCredito());
            case 6:
                return Decimal.toString(caixaItem.getDebito());
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        CaixaItem caixaItem = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                caixaItem.setId((int) aValue);
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
            case 6:
                break;
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(CaixaItem aValue, int rowIndex) {
        CaixaItem caixaItem = dados.get(rowIndex);

        caixaItem = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public CaixaItem getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(CaixaItem caixaItem) {
        dados.add(caixaItem);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(CaixaItem oldItem, CaixaItem newItem) {
        int index = dados.indexOf(oldItem);
        dados.set(index, newItem);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<CaixaItem> caixaItens) {
        int oldCount = getRowCount();

        dados.addAll(caixaItens);

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
