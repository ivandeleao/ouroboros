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
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.catalogo.ProdutoComponente;
import model.mysql.dao.principal.ProdutoDAO;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class ComponenteJTableModel extends AbstractTableModel {

    private final List<ProdutoComponente> dados;
    private final String[] colunas = {"Componente", "Quantidade", "Valor Compra", "Subtotal Compra", "Valor Venda", "Subtotal Venda"};

    public ComponenteJTableModel() {
        dados = new ArrayList<>();
    }

    public ComponenteJTableModel(List<ProdutoComponente> listComponente) {
        dados = listComponente;
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
        ProdutoComponente produtoComponente = dados.get(rowIndex);
        
        String nome = produtoComponente.getComponente().getNome();
        BigDecimal quantidade = produtoComponente.getQuantidade();
        BigDecimal valorCompra = produtoComponente.getComponente().getValorCompra();
        BigDecimal subtotalCompra = produtoComponente.getTotalCompra();
        BigDecimal valorVenda = produtoComponente.getComponente().getValorVenda();
        BigDecimal subtotalVenda = produtoComponente.getTotalVenda();
        
        switch (columnIndex) {
            case 0:
                return nome;
            case 1:
                return Decimal.toString(quantidade, 3);
            case 2:
                return Decimal.toString(valorCompra);
            case 3:
                return Decimal.toString(subtotalCompra);
            case 4:
                return Decimal.toString(valorVenda);
            case 5:
                return Decimal.toString(subtotalVenda);
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        ProdutoComponente produtoComponente = dados.get(rowIndex);

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
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(ProdutoComponente aValue, int rowIndex) {
        ProdutoComponente produtoComponente = dados.get(rowIndex);

        produtoComponente = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public ProdutoComponente getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(ProdutoComponente produtoComponente) {
        dados.add(produtoComponente);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(ProdutoComponente oldItem, ProdutoComponente newItem) {
        int index = dados.indexOf(oldItem);
        dados.set(index, newItem);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<ProdutoComponente> listComponente) {
        int oldCount = getRowCount();

        dados.addAll(listComponente);

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
