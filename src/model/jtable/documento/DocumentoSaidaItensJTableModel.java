/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.documento;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.MovimentoFisico;
import util.Decimal;
import util.FiscalUtil;

/**
 *
 * @author ivand
 */
public class DocumentoSaidaItensJTableModel extends AbstractTableModel {
    private final List<MovimentoFisico> dados;
    private final String[] colunas = {"", "#", "Código", "Descrição", "Funcionário", "Quantidade", "Tipo", "Valor", "Acréscimo", "Desconto", "Subtotal", "Editar"};

    public DocumentoSaidaItensJTableModel() {
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
    public Class getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex) != null ? getValueAt(0, columnIndex).getClass() : null;
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
                return movimentoFisico.getDescricaoItemMontado();
            case 4:
                return movimentoFisico.getFuncionario() != null ? movimentoFisico.getFuncionario() : "";
            case 5:
                return Decimal.toStringDescarteDecimais(movimentoFisico.getSaldoLinearAbsoluto(), 3);
            /*case 6:
                return movimentoFisico.getUnidadeComercialVenda();*/
            case 6:
                return movimentoFisico.getProdutoTipo().getSigla();
            case 7:
                return Decimal.toString(movimentoFisico.getValor());
            case 8:
                return movimentoFisico.getAcrescimoFormatado();
            case 9:
                return movimentoFisico.getDescontoFormatado();
            case 10:
                return Decimal.toString(movimentoFisico.getSubtotal());
            case 11:
                return new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-pencil-drawing-20.png"));
                
        }
        return null;
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        MovimentoFisico movimentoFisico = dados.get(rowIndex);
        System.out.println("setValueAt... " + rowIndex);
        switch (this.getColumnName(columnIndex)) {
            case "Descrição":
                movimentoFisico.setDescricao((String) aValue);
                break;
            case "Quantidade":
                movimentoFisico.setSaida(Decimal.fromString((String) aValue));
                FiscalUtil.ajustarTributavel(movimentoFisico);
                break;
            case "Valor":
                movimentoFisico.setValor(Decimal.fromString((String) aValue));
                FiscalUtil.ajustarTributavel(movimentoFisico);
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
        switch (this.getColumnName(columnIndex)) {
            case "Descrição":
            case "Quantidade":
            case "Valor":
                return true;
                //return !dados.get(rowIndex).isAgrupado(); //não editar se for agrupado
                
            default:
                return false;
        }
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
