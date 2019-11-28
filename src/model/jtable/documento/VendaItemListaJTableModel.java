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
import model.mysql.bean.principal.documento.VendaItemConsolidado;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class VendaItemListaJTableModel extends AbstractTableModel {
    private final List<VendaItemConsolidado> dados;
    private final String[] colunas = {"Código", "Descrição", "Quantidade", "Valor Médio", "Total"};

    public VendaItemListaJTableModel() {
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
        VendaItemConsolidado consolidado = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return consolidado.getProduto().getCodigo();
            case 1:
                return consolidado.getProduto().getNome();
            case 2:
                return Decimal.toString(consolidado.getQuantidade(), 3);
            case 3:
                return Decimal.toString(consolidado.getValorMedio());
            case 4:
                return Decimal.toString(consolidado.getTotal());
        }
        return null;
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        VendaItemConsolidado consolidado = dados.get(rowIndex);

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
    
    public void setValueAt(VendaItemConsolidado aValue, int rowIndex) {
        VendaItemConsolidado consolidado = dados.get(rowIndex);

        consolidado = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    public VendaItemConsolidado getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(VendaItemConsolidado movimentoFisicoConsolidado) {
        dados.add(movimentoFisicoConsolidado);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }
    
    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(VendaItemConsolidado oldItem, VendaItemConsolidado newItem) {
        int index = dados.indexOf(oldItem);
        dados.set(index, newItem);
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<VendaItemConsolidado> listMovimentoFisicoConsolidado) {
        int oldCount = getRowCount();

        dados.addAll(listMovimentoFisicoConsolidado);

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
