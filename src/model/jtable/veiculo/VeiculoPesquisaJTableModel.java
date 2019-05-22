/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.veiculo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.Veiculo;
import model.mysql.bean.fiscal.UnidadeComercial;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class VeiculoPesquisaJTableModel extends AbstractTableModel {

    private final List<Veiculo> dados;
    private final String[] colunas = {"Id", "Placa", "Modelo", "Cor", "Observação"};

    public VeiculoPesquisaJTableModel() {
        dados = new ArrayList<>();
    }

    public VeiculoPesquisaJTableModel(List<Veiculo> veiculos) {
        dados = veiculos;
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
        Veiculo veiculo = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return veiculo.getId();
            case 1:
                return veiculo.getPlaca();
            case 2:
                return veiculo.getModelo();
            case 3:
                return veiculo.getCor();
            case 4:
                return veiculo.getObservacao();
        }
        return null;
    }

    

    public void setValueAt(Veiculo aValue, int rowIndex) {
        Veiculo veiculo = dados.get(rowIndex);

        veiculo = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Veiculo getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Veiculo veiculo) {
        dados.add(veiculo);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Veiculo oldVeiculo, Veiculo newVeiculo) {
        int index = dados.indexOf(oldVeiculo);
        dados.set(index, newVeiculo);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Veiculo> veiculos) {
        int oldCount = getRowCount();

        dados.addAll(veiculos);

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
