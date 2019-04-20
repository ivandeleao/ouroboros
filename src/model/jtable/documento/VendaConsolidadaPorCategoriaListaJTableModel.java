/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.documento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.documento.VendaCategoriaConsolidado;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class VendaConsolidadaPorCategoriaListaJTableModel extends AbstractTableModel {
    private final List<VendaCategoriaConsolidado> dados;
    private final String[] colunas = {"Categoria", "Total Bruto", "Total Líquido"};

    public VendaConsolidadaPorCategoriaListaJTableModel() {
        dados = new ArrayList<>();
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
        VendaCategoriaConsolidado consolidado = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return consolidado.getCategoria();
            case 1:
                return Decimal.toString(consolidado.getTotalBruto());
            case 2:
                return Decimal.toString(consolidado.getTotalLiquido());
        }
        return null;
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        VendaCategoriaConsolidado consolidado = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                //movimentoFisico.setId((int) aValue);
                break;
            case 1:
                //movimentoFisico.setNumero((int) aValue);
                break;
            case 2:
                //movimentoFisico.setNome((String) aValue);
                break;
            case 3:
                //movimentoFisico.setNome((String) aValue);
                break;
            case 4:
                //movimentoFisico.setEntrada((BigDecimal) aValue);
                break;
            case 5:
                //movimentoFisico.setValor((BigDecimal) aValue);
                break;
            case 6:
                //movimentoFisico.setValor((BigDecimal) aValue);
                break;
            case 7:
                //movimentoFisico.setValor((BigDecimal) aValue);
                break;
                
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    public void setValueAt(VendaCategoriaConsolidado aValue, int rowIndex) {
        VendaCategoriaConsolidado consolidado = dados.get(rowIndex);

        consolidado = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    public VendaCategoriaConsolidado getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(VendaCategoriaConsolidado movimentoFisicoConsolidado) {
        dados.add(movimentoFisicoConsolidado);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }
    
    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(VendaCategoriaConsolidado oldItem, VendaCategoriaConsolidado newItem) {
        int index = dados.indexOf(oldItem);
        dados.set(index, newItem);
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<VendaCategoriaConsolidado> listVendaCategoriaConsolidado) {
        int oldCount = getRowCount();

        dados.addAll(listVendaCategoriaConsolidado);

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
