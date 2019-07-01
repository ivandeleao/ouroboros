/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.financeiro;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.financeiro.ContaProgramada;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class ContasProgramadasJTableModel extends AbstractTableModel {
    private final List<ContaProgramada> dados;
    private final String[] colunas = {"Nome", "Início", "Término", "Valor"};

    public ContasProgramadasJTableModel() {
        dados = new ArrayList<>();
    }

    public ContasProgramadasJTableModel(List<ContaProgramada> contas) {
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
        ContaProgramada conta = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return conta.getNome();
            case 1:
                return DateTime.toString(conta.getInicio());
            case 2:
                return DateTime.toString(conta.getTermino());
            case 3:
                return Decimal.toString(conta.getValor());
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        ContaProgramada conta = dados.get(rowIndex);

        //--

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(ContaProgramada aValue, int rowIndex) {
        ContaProgramada conta = dados.get(rowIndex);

        conta = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public ContaProgramada getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(ContaProgramada conta) {
        dados.add(conta);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(ContaProgramada oldItem, ContaProgramada newItem) {
        int index = dados.indexOf(oldItem);
        dados.set(index, newItem);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<ContaProgramada> contas) {
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
