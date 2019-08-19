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
import model.mysql.bean.principal.catalogo.ProdutoTamanho;
import model.mysql.bean.fiscal.UnidadeComercial;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class ProdutoTamanhoJTableModel extends AbstractTableModel {

    private final List<ProdutoTamanho> dados;
    private final String[] colunas = {"Tamanho", "Valor"};

    public ProdutoTamanhoJTableModel() {
        dados = new ArrayList<>();
    }

    public ProdutoTamanhoJTableModel(List<ProdutoTamanho> tamanhos) {
        dados = tamanhos;
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
        ProdutoTamanho produtoTamanho = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return produtoTamanho.getNome();
            case 1:
                return 0;
                //return produtoTamanho.getValor();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        ProdutoTamanho produtoTamanho = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                produtoTamanho.setId((int) aValue);
                break;
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(ProdutoTamanho aValue, int rowIndex) {
        ProdutoTamanho produtoTamanho = dados.get(rowIndex);

        produtoTamanho = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public ProdutoTamanho getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(ProdutoTamanho produtoTamanho) {
        dados.add(produtoTamanho);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(ProdutoTamanho oldProduto, ProdutoTamanho newProduto) {
        int index = dados.indexOf(oldProduto);
        dados.set(index, newProduto);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<ProdutoTamanho> tamanhos) {
        int oldCount = getRowCount();

        dados.addAll(tamanhos);

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
