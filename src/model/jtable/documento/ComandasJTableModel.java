/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.documento;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.documento.ComandaSnapshot;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class ComandasJTableModel extends AbstractTableModel {
    private final List<ComandaSnapshot> dados;
    private final String[] colunas = {"Id", "Número", "Início", "Itens", "Valor"};

    public ComandasJTableModel() {
        dados = new ArrayList<>();
    }

    public ComandasJTableModel(List<ComandaSnapshot> comandas) {
        dados = comandas;
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
        ComandaSnapshot comanda = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return comanda.getId();
            case 1:
                return comanda.getNumero();
            case 2:
                return DateTime.toStringHoraMinuto(comanda.getInicio());
            case 3:
                return comanda.getItens();
            case 4:
                return Decimal.toString(comanda.getValor());
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        ComandaSnapshot comanda = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                //comanda.setId((int) aValue);
                break;
            case 1:
                //comanda.setCriacao((Timestamp) aValue);
                break;
            case 2:
                //comanda.setPessoa((Pessoa) aValue);
                break;
            case 3:
                //comanda.setCodigo((String) aValue);
                break;
            case 4:
                //comanda.setUnidadeComercialComandaSnapshot((UnidadeComercial) aValue);
                break;
            //case 4:
            //comanda.setCategorias((Set<Categoria>) aValue);            //case 4:
            //comanda.setCategorias((Set<Categoria>) aValue);            //case 4:
            //comanda.setCategorias((Set<Categoria>) aValue);            //case 4:
            //comanda.setCategorias((Set<Categoria>) aValue);
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(ComandaSnapshot aValue, int rowIndex) {
        ComandaSnapshot comanda = dados.get(rowIndex);

        comanda = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public ComandaSnapshot getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(ComandaSnapshot comanda) {
        dados.add(comanda);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(ComandaSnapshot oldPedido, ComandaSnapshot newPedido) {
        int index = dados.indexOf(oldPedido);
        dados.set(index, newPedido);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<ComandaSnapshot> comandas) {
        int oldCount = getRowCount();

        dados.addAll(comandas);

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
