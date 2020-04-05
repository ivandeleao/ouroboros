/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.financeiro.conta;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.financeiro.Conta;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class ContaListaJTableModel extends AbstractTableModel {
    private final List<Conta> dados;
    private final String[] colunas = {"Id", "Data Criação", "Nome", "Tipo", "Data", "Saldo"};

    public ContaListaJTableModel() {
        dados = new ArrayList<>();
    }

    public ContaListaJTableModel(List<Conta> contas) {
        dados = contas;
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
        Conta conta = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return conta.getId();
            case 1:
                return DateTime.toString(conta.getCriacao());
            case 2:
                return conta.getNome();
            case 3:
                return conta.getContaTipo();
            case 4:
                return DateTime.toString(conta.getData());
            case 5:
                return Decimal.toString(conta.getSaldo());
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Conta conta = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                conta.setId((int) aValue);
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

    public void setValueAt(Conta aValue, int rowIndex) {
        Conta conta = dados.get(rowIndex);

        conta = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Conta getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Conta conta) {
        dados.add(conta);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Conta oldItem, Conta newItem) {
        int index = dados.indexOf(oldItem);
        dados.set(index, newItem);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Conta> contas) {
        int oldCount = getRowCount();

        dados.addAll(contas);

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
