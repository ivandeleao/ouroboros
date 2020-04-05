/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.catalogo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.catalogo.Marca;
import model.mysql.bean.fiscal.UnidadeComercial;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class MarcaJTableModel extends AbstractTableModel {

    private final List<Marca> dados;
    private final String[] colunas = {"Id", "Nome", "Produtos"};

    public MarcaJTableModel() {
        dados = new ArrayList<>();
    }

    public MarcaJTableModel(List<Marca> marcaList) {
        dados = marcaList;
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
        Marca marca = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return marca.getId();
            case 1:
                return marca.getNome();
            case 2:
                return marca.getProdutos().size();
            
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Marca marca = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                marca.setId((int) aValue);
                break;
            case 1:
                marca.setNome((String) aValue);
                break;
            case 2:
                //marca.setDescricao((String) aValue);
                break;
            
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(Marca aValue, int rowIndex) {
        Marca marca = dados.get(rowIndex);

        marca = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Marca getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Marca marca) {
        dados.add(marca);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Marca oldMarca, Marca newMarca) {
        int index = dados.indexOf(oldMarca);
        dados.set(index, newMarca);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Marca> marcas) {
        int oldCount = getRowCount();

        dados.addAll(marcas);

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
