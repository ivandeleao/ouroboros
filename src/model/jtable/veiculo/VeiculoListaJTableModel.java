/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.veiculo;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.Veiculo;

/**
 *
 * @author ivand
 */
public class VeiculoListaJTableModel extends AbstractTableModel {

    private final List<Veiculo> dados;
    private final String[] colunas = {"Id", "Placa", "Modelo", "Cor", "Ano Fabricação", "Ano Modelo", "Motor", "Chassi", "Renavam"};

    public VeiculoListaJTableModel() {
        dados = new ArrayList<>();
    }

    public VeiculoListaJTableModel(List<Veiculo> veiculos) {
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
    public Class getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
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
                return veiculo.getAnoFabricacao();
            case 5:
                return veiculo.getAnoFabricacao();
            case 6:
                return veiculo.getMotor();
            case 7:
                return veiculo.getChassi();
            case 8:
                return veiculo.getRenavam();
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

    public void updateRow(Veiculo oldCliente, Veiculo newCliente) {
        int index = dados.indexOf(oldCliente);
        dados.set(index, newCliente);
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
