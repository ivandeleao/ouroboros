/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.endereco;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.endereco.Cidade;

/**
 *
 * @author ivand
 */
public class MunicipioJTableModel extends AbstractTableModel {

    private final List<Cidade> dados;
    private final String[] colunas = {"Município", "Estado", "Código"};

    public MunicipioJTableModel() {
        dados = new ArrayList<>();
    }

    public MunicipioJTableModel(List<Cidade> cidades) {
        dados = cidades;
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
        Cidade cidade = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return cidade.getNome();
            case 1:
                return cidade.getEstado().getNome();
            case 2:
                return cidade.getCodigoIbgeCompleto();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Cidade cidade = dados.get(rowIndex);

        

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(Cidade aValue, int rowIndex) {
        Cidade cidade = dados.get(rowIndex);

        cidade = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Cidade getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Cidade cidade) {
        dados.add(cidade);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Cidade oldCidade, Cidade newCidade) {
        int index = dados.indexOf(oldCidade);
        dados.set(index, newCidade);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Cidade> cidades) {
        int oldCount = getRowCount();

        dados.addAll(cidades);

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
