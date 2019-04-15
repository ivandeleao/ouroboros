/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.pessoa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.pessoa.GrupoItem;
import model.mysql.bean.fiscal.UnidadeComercial;

/**
 *
 * @author ivand
 */
public class GrupoItemJTableModel extends AbstractTableModel {

    private final List<GrupoItem> dados;
    private final String[] colunas = {"Id", "Produto"};

    public GrupoItemJTableModel() {
        dados = new ArrayList<>();
    }

    public GrupoItemJTableModel(List<GrupoItem> grupoItens) {
        dados = grupoItens;
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
        GrupoItem grupoItem = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return grupoItem.getId();
            case 1:
                return grupoItem.getProduto().getNome();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        GrupoItem grupoItem = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                grupoItem.setId((int) aValue);
                break;
            
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(GrupoItem aValue, int rowIndex) {
        GrupoItem grupoItem = dados.get(rowIndex);

        grupoItem = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public GrupoItem getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(GrupoItem grupoItem) {
        dados.add(grupoItem);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(GrupoItem oldGrupoItem, GrupoItem newGrupoItem) {
        int index = dados.indexOf(oldGrupoItem);
        dados.set(index, newGrupoItem);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<GrupoItem> grupoItens) {
        int oldCount = getRowCount();

        dados.addAll(grupoItens);

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
