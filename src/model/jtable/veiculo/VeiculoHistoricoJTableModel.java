/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.veiculo;

import model.jtable.documento.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.documento.VendaStatus;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class VeiculoHistoricoJTableModel extends AbstractTableModel {
    private final List<Venda> dados;
    private final String[] colunas = {"Id", "Data", "Cliente", "Funcionário", "Descrição", "Total"};

    public VeiculoHistoricoJTableModel() {
        dados = new ArrayList<>();
    }

    public VeiculoHistoricoJTableModel(List<Venda> vendas) {
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
    public Class getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Venda venda = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return venda.getId();
            case 1:
                return DateTime.toString(venda.getDataHora());
            case 2:
                return venda.getPessoa() != null ? venda.getPessoa().getNome() : "--NÃO INFORMADO--";
            case 3:
                return venda.getFuncionario() != null ? venda.getFuncionario().getNome() : "--NÃO INFORMADO--";
            case 4:
                return venda.getDescricaoConcatenada();
            case 5:
                return Decimal.toString(venda.getTotal());
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Venda venda = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                //venda.setId((int) aValue);
                break;
            case 1:
                //venda.setDataHora((Timestamp) aValue);
                break;
            case 2:
                //venda.setPessoa((Pessoa) aValue);
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
