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
import model.mysql.bean.principal.catalogo.Categoria;
import model.mysql.bean.fiscal.UnidadeComercial;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class CategoriaJTableModel extends AbstractTableModel {

    private final List<Categoria> dados;
    private final String[] colunas = {"Id", "Nome", "Produtos"};

    public CategoriaJTableModel() {
        dados = new ArrayList<>();
    }

    public CategoriaJTableModel(List<Categoria> categoriaList) {
        dados = categoriaList;
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
        Categoria categoria = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return categoria.getId();
            case 1:
                return categoria.getNome();
            case 2:
                return categoria.getProdutoList().size();
            
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Categoria categoria = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                categoria.setId((int) aValue);
                break;
            case 1:
                categoria.setNome((String) aValue);
                break;
            case 2:
                //categoria.setDescricao((String) aValue);
                break;
            
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(Categoria aValue, int rowIndex) {
        Categoria categoria = dados.get(rowIndex);

        categoria = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Categoria getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Categoria categoria) {
        dados.add(categoria);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Categoria oldCategoria, Categoria newCategoria) {
        int index = dados.indexOf(oldCategoria);
        dados.set(index, newCategoria);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Categoria> categorias) {
        int oldCount = getRowCount();

        dados.addAll(categorias);

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
