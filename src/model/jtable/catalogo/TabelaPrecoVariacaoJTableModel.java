/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.catalogo;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.catalogo.TabelaPrecoVariacao;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class TabelaPrecoVariacaoJTableModel extends AbstractTableModel {

    private final List<TabelaPrecoVariacao> dados;
    private final String[] colunas = {"Id", "Valor Inicial", "Valor Final", "Acréscimo", "Desconto"};

    public TabelaPrecoVariacaoJTableModel() {
        dados = new ArrayList<>();
    }

    public TabelaPrecoVariacaoJTableModel(List<TabelaPrecoVariacao> tabelaPrecoList) {
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
    public Object getValueAt(int rowIndex, int columnIndex) {
        TabelaPrecoVariacao tabelaPreco = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return tabelaPreco.getId();
            case 1:
                return Decimal.toString(tabelaPreco.getValorInicial());
            case 2:
                return Decimal.toString(tabelaPreco.getValorFinal());
            case 3:
                return tabelaPreco.getAcrescimoFormatado();
            case 4:
                return tabelaPreco.getDescontoFormatado();
            
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        TabelaPrecoVariacao tabelaPreco = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                tabelaPreco.setId((int) aValue);
                break;
            case 1:
                //tabelaPreco.setNome((String) aValue);
                break;
            case 2:
                //tabelaPreco.setDescricao((String) aValue);
                break;
            
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(TabelaPrecoVariacao aValue, int rowIndex) {
        TabelaPrecoVariacao tabelaPreco = dados.get(rowIndex);

        tabelaPreco = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public TabelaPrecoVariacao getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(TabelaPrecoVariacao tabelaPreco) {
        dados.add(tabelaPreco);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(TabelaPrecoVariacao oldTabelaPreco, TabelaPrecoVariacao newTabelaPreco) {
        int index = dados.indexOf(oldTabelaPreco);
        dados.set(index, newTabelaPreco);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<TabelaPrecoVariacao> tabelasPreco) {
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
