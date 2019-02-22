/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.bean.endereco.Endereco;

/**
 *
 * @author ivand
 */
public class EnderecoJTableModel extends AbstractTableModel {

    private final List<Endereco> dados;
    private final String[] colunas = {"Endereço", "Bairro", "Cidade", "CEP"};

    public EnderecoJTableModel() {
        dados = new ArrayList<>();
    }

    public EnderecoJTableModel(List<Endereco> enderecos) {
        dados = enderecos;
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
        Endereco endereco = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return endereco.getEnderecoCompleto();
            case 1:
                return endereco.getBairro().getNome();
            case 2:
                return endereco.getCidade().getNome();
            case 3:
                return endereco.getCep();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Endereco endereco = dados.get(rowIndex);

        

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(Endereco aValue, int rowIndex) {
        Endereco endereco = dados.get(rowIndex);

        endereco = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Endereco getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Endereco endereco) {
        dados.add(endereco);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Endereco oldEndereco, Endereco newEndereco) {
        int index = dados.indexOf(oldEndereco);
        dados.set(index, newEndereco);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Endereco> enderecos) {
        int oldCount = getRowCount();

        dados.addAll(enderecos);

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
