/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.documento;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.documento.OSTransporte;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class OSTransporteListaJTableModel extends AbstractTableModel {
    private final List<OSTransporte> dados;
    private final String[] colunas = {"Id", "Data", "Remetente", "Motorista", "Total", "Em aberto"};

    public OSTransporteListaJTableModel() {
        dados = new ArrayList<>();
    }

    public OSTransporteListaJTableModel(List<OSTransporte> osts) {
        dados = osts;
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
        OSTransporte ost = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return ost.getId();
            case 1:
                return DateTime.toString(ost.getCriacao());
            case 2:
                return ost.getRemetente() != null ? ost.getRemetente().getNome() : "--NÃO INFORMADO--";
            case 3:
                return ost.getMotorista() != null ? ost.getMotorista().getNome() : "--NÃO INFORMADO--";
            case 4:
                return Decimal.toString(ost.getTotal());
            case 5:
                return "--"; //Decimal.toString(ost.getTotalEmAberto());
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        OSTransporte ost = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                //ost.setId((int) aValue);
                break;
            case 1:
                //ost.setCriacao((Timestamp) aValue);
                break;
            case 2:
                //ost.setPessoa((Pessoa) aValue);
                break;
            case 3:
                //ost.setCodigo((String) aValue);
                break;
            case 4:
                //ost.setUnidadeComercialOSTransporte((UnidadeComercial) aValue);
                break;
            //case 4:
            //ost.setCategorias((Set<Categoria>) aValue);            //case 4:
            //ost.setCategorias((Set<Categoria>) aValue);            //case 4:
            //ost.setCategorias((Set<Categoria>) aValue);            //case 4:
            //ost.setCategorias((Set<Categoria>) aValue);
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(OSTransporte aValue, int rowIndex) {
        OSTransporte ost = dados.get(rowIndex);

        ost = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public OSTransporte getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(OSTransporte ost) {
        dados.add(ost);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(OSTransporte oldPedido, OSTransporte newPedido) {
        int index = dados.indexOf(oldPedido);
        dados.set(index, newPedido);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<OSTransporte> osts) {
        int oldCount = getRowCount();

        dados.addAll(osts);

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
