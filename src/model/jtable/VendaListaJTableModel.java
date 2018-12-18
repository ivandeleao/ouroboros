/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.bean.principal.Venda;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class VendaListaJTableModel extends AbstractTableModel {
    private final List<Venda> dados;
    private final String[] colunas = {"Status", "Id", "Data", "Entrega", "Cliente", "Itens", "Total"};

    public VendaListaJTableModel() {
        dados = new ArrayList<>();
    }

    public VendaListaJTableModel(List<Venda> vendas) {
        dados = vendas;
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
        Venda venda = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return venda.getStatus();
            case 1:
                return venda.getId();
            case 2:
                return DateTime.toString(venda.getCriacao());
            case 3:
                return DateTime.toString(venda.getPrimeiraDataSaidaPrevista());
            case 4:
                return venda.getCliente() != null ? venda.getCliente().getNome() : "--VENDA AO CONSUMIDOR--";
            case 5:
                return venda.getMovimentosFisicos().size();
            case 6:
                return Decimal.toString(venda.getTotal());
            //case 4:
            //return venda.getCategorias();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Venda venda = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                venda.setId((int) aValue);
                break;
            case 1:
                //venda.setCriacao((Timestamp) aValue);
                break;
            case 2:
                //venda.setCliente((Pessoa) aValue);
                break;
            case 3:
                //venda.setCodigo((String) aValue);
                break;
            case 4:
                //venda.setUnidadeComercialVenda((UnidadeComercial) aValue);
                break;
            //case 4:
            //venda.setCategorias((Set<Categoria>) aValue);            //case 4:
            //venda.setCategorias((Set<Categoria>) aValue);            //case 4:
            //venda.setCategorias((Set<Categoria>) aValue);            //case 4:
            //venda.setCategorias((Set<Categoria>) aValue);
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(Venda aValue, int rowIndex) {
        Venda venda = dados.get(rowIndex);

        venda = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Venda getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Venda venda) {
        dados.add(venda);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Venda oldPedido, Venda newPedido) {
        int index = dados.indexOf(oldPedido);
        dados.set(index, newPedido);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Venda> vendas) {
        int oldCount = getRowCount();

        dados.addAll(vendas);

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