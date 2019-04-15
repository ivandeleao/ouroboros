/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.catalogo;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.MovimentoFisico;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class EstoqueDetalheJTableModel extends AbstractTableModel {

    private final List<MovimentoFisico> dados;
    private final String[] colunas = {"Id", "Componente", "Status", "Data", "Tipo", "Observação", "Entrada", "Saída", "Saldo"};

    public EstoqueDetalheJTableModel() {
        dados = new ArrayList<>();
    }

    public EstoqueDetalheJTableModel(List<MovimentoFisico> VendaItemList) {
        dados = VendaItemList;
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
        MovimentoFisico movimentoFisico = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return movimentoFisico.getId();
            case 1:
                return movimentoFisico.getProduto().getNome();
            case 2:
                return movimentoFisico.getStatus();
            case 3:
                return DateTime.toString(movimentoFisico.getDataRelevante());
            case 4:
                return movimentoFisico.getMovimentoFisicoTipo();
            case 5:
                return movimentoFisico.getObservacao();
            case 6:
                return Decimal.toString(movimentoFisico.getEntrada(), 3);
            case 7:
                return Decimal.toString(movimentoFisico.getSaida(), 3);
            case 8:
                return Decimal.toString(movimentoFisico.getSaldoAcumulado(), 3);
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        MovimentoFisico movimentoFisico = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                movimentoFisico.setId((int) aValue);
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(MovimentoFisico aValue, int rowIndex) {
        MovimentoFisico movimentoFisico = dados.get(rowIndex);

        movimentoFisico = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public MovimentoFisico getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(MovimentoFisico movimentoFisico) {
        dados.add(movimentoFisico);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(MovimentoFisico oldItem, MovimentoFisico newItem) {
        int index = dados.indexOf(oldItem);
        dados.set(index, newItem);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<MovimentoFisico> VendaItemList) {
        int oldCount = getRowCount();

        dados.addAll(VendaItemList);

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
