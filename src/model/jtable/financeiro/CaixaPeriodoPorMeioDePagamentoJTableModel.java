/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.financeiro;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.temp.CaixaResumoPorMeioDePagamento;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class CaixaPeriodoPorMeioDePagamentoJTableModel extends AbstractTableModel {
    private final List<CaixaResumoPorMeioDePagamento> dados;
    private final String[] colunas = {"Meio de Pagamento", "Crédito", "Débito", "Saldo CD", "Suprimento", "Sangria", "Saldo SS", "Saldo Final"};

    public CaixaPeriodoPorMeioDePagamentoJTableModel() {
        dados = new ArrayList<>();
    }

    public CaixaPeriodoPorMeioDePagamentoJTableModel(List<CaixaResumoPorMeioDePagamento> caixaItens) {
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
        CaixaResumoPorMeioDePagamento caixaItem = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return caixaItem.getMeioDePagamento();
            case 1:
                return Decimal.toString(caixaItem.getCreditoTotal());
            case 2:
                return Decimal.toString(caixaItem.getDebitoTotal());
            case 3:
                return Decimal.toString(caixaItem.getSaldoCreditoDebito());
            case 4:
                return Decimal.toString(caixaItem.getSuprimentoTotal());
            case 5:
                return Decimal.toString(caixaItem.getSangriaTotal());
            case 6:
                return Decimal.toString(caixaItem.getSaldoSuprimentoSangria());
            case 7:
                return Decimal.toString(caixaItem.getSaldoFinal());
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        CaixaResumoPorMeioDePagamento caixaItem = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
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

    public void setValueAt(CaixaResumoPorMeioDePagamento aValue, int rowIndex) {
        CaixaResumoPorMeioDePagamento caixaItem = dados.get(rowIndex);

        caixaItem = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public CaixaResumoPorMeioDePagamento getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(CaixaResumoPorMeioDePagamento caixaItem) {
        dados.add(caixaItem);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(CaixaResumoPorMeioDePagamento oldItem, CaixaResumoPorMeioDePagamento newItem) {
        int index = dados.indexOf(oldItem);
        dados.set(index, newItem);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<CaixaResumoPorMeioDePagamento> caixaItens) {
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
