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
import model.mysql.bean.fiscal.UnidadeComercial;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class ProdutoJTableModel extends AbstractTableModel {

    private final List<Produto> dados;
    private final String[] colunas = {"Id", "Nome", "Descrição", "Valor", "Codigo", "Unidade Comercial"};

    public ProdutoJTableModel() {
        dados = new ArrayList<>();
    }

    public ProdutoJTableModel(List<Produto> produtos) {
        dados = produtos;
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
        Produto produto = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return produto.getId();
            case 1:
                return produto.getNome();
            case 2:
                return produto.getDescricao();
            case 3:
                return produto.getValorVenda();
            case 4:
                return produto.getCodigo();
            case 5:
                return produto.getUnidadeComercialVenda();
            //case 6:
            //    return Decimal.toString(produto.getEstoqueAtual());
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Produto produto = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                produto.setId((int) aValue);
                break;
            case 1:
                produto.setNome((String) aValue);
                break;
            case 2:
                produto.setDescricao((String) aValue);
                break;
            case 3:
                produto.setValorVenda((BigDecimal) aValue);
                break;
            case 4:
                produto.setCodigo((String) aValue);
                break;
            case 5:
                produto.setUnidadeComercialVenda((UnidadeComercial) aValue);
                break;
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(Produto aValue, int rowIndex) {
        Produto produto = dados.get(rowIndex);

        produto = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Produto getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Produto produto) {
        dados.add(produto);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Produto oldProduto, Produto newProduto) {
        int index = dados.indexOf(oldProduto);
        dados.set(index, newProduto);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Produto> produtos) {
        int oldCount = getRowCount();

        dados.addAll(produtos);

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
