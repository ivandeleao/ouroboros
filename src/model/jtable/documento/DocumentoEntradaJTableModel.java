/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.documento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.MovimentoFisico;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class DocumentoEntradaJTableModel extends AbstractTableModel {
    private final List<MovimentoFisico> dados;
    private final String[] colunas = {"", "#", "Código", "Descrição", "Quantidade", "UM", "Tipo", "Valor", "Acréscimo", "Desconto", "Frete", "Seguro", "Subtotal"};

    public DocumentoEntradaJTableModel() {
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
        MovimentoFisico movimentoFisico = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return movimentoFisico.getId();
            case 1:
                return dados.indexOf(movimentoFisico) + 1;
            case 2:
                return movimentoFisico.getCodigo();
            case 3:
                return movimentoFisico.getDescricao();
            case 4:
                return Decimal.toString(movimentoFisico.getSaldoLinearAbsoluto(), 3);
            case 5:
                return movimentoFisico.getUnidadeComercialVenda();
            case 6:
                return movimentoFisico.getProdutoTipo().getSigla();
            case 7:
                return Decimal.toStringDescarteDecimais(movimentoFisico.getValor(), 10);
            case 8:
                return movimentoFisico.getAcrescimoFormatado();
            case 9:
                return movimentoFisico.getDescontoFormatado();
            case 10:
                return Decimal.toString(movimentoFisico.getValorFrete());
            case 11:
                return Decimal.toString(movimentoFisico.getValorSeguro());
            case 12:
                return Decimal.toString(movimentoFisico.getSubtotal());
        }
        return null;
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        MovimentoFisico movimentoFisico = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                movimentoFisico.setId((int) aValue);
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
                movimentoFisico.setEntrada((BigDecimal) aValue);
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
    
    public void setValueAt(MovimentoFisico aValue, int rowIndex) {
        MovimentoFisico movimentoFisico = dados.get(rowIndex);

        movimentoFisico = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    public MovimentoFisico getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(MovimentoFisico movimentoFisico) {
        dados.add(movimentoFisico);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }
    
    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(MovimentoFisico oldPedidoItem, MovimentoFisico newPedidoItem) {
        int index = dados.indexOf(oldPedidoItem);
        dados.set(index, newPedidoItem);
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<MovimentoFisico> vendaItens) {
        int oldCount = getRowCount();

        dados.addAll(vendaItens);

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
