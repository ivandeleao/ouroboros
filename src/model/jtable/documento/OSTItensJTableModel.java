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
import model.mysql.bean.principal.documento.OSTransporteItem;
import util.Decimal;
import util.FiscalUtil;

/**
 *
 * @author ivand
 */
public class OSTItensJTableModel extends AbstractTableModel {

    private final List<OSTransporteItem> dados;
    private final String[] colunas = {"Id", "Descrição", "Valor", "Motorista", "% Motorista", "Pedágio", "% Pedágio", "Adicional", "% Adicional", "Total", "Destinatário", "Telefone", "Cidade", "Endereço", "Editar"};

    public OSTItensJTableModel() {
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
        OSTransporteItem ostItem = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return ostItem.getId();
            case 1:
                return ostItem.getDescricao();
            case 2:
                return Decimal.toString(ostItem.getValor());
            case 3:
                return Decimal.toString(ostItem.getMotoristaValor());
            case 4:
                return Decimal.toString(ostItem.getMotoristaPercentual());
            case 5:
                return Decimal.toString(ostItem.getPedagioValor());
            case 6:
                return Decimal.toString(ostItem.getPedagioPercentual());
            case 7:
                return Decimal.toString(ostItem.getAdicionalValor());
            case 8:
                return Decimal.toString(ostItem.getAdicionalPercentual());
            case 9:
                return Decimal.toString(ostItem.getSubtotal());
            case 10:
                return ostItem.getDestinatario().getNome();
            case 11:
                return ostItem.getDestinatario().getTelefone1();
            case 12:
                return ostItem.getCidade() + " " + ostItem.getUf();
            case 13:
                return ostItem.getEndereco();
            case 14:
                return new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-pencil-drawing-20.png"));
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        OSTransporteItem ostItem = dados.get(rowIndex);
        System.out.println("setValueAt... " + rowIndex);
        switch (columnIndex) {
            case 0:
                ostItem.setId((int) aValue);
                break;
            case 1:
                ostItem.setDescricao((String) aValue);
                break;
            case 2:
                ostItem.setValor(Decimal.fromString((String) aValue));
                break;
            case 3:
                ostItem.setMotoristaValor(Decimal.fromString((String) aValue));
                break;
            case 4:
                ostItem.setMotoristaPercentual(Decimal.fromString((String) aValue));
                break;
            case 5:
                ostItem.setPedagioValor(Decimal.fromString((String) aValue));
                break;
            case 6:
                ostItem.setPedagioPercentual(Decimal.fromString((String) aValue));
                break;
            case 7:
                ostItem.setAdicionalValor(Decimal.fromString((String) aValue));
                break;
            case 8:
                ostItem.setAdicionalPercentual(Decimal.fromString((String) aValue));
                break;

        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(OSTransporteItem aValue, int rowIndex) {
        OSTransporteItem ostItem = dados.get(rowIndex);

        ostItem = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public OSTransporteItem getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(OSTransporteItem ostItem) {
        dados.add(ostItem);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(OSTransporteItem oldPedidoItem, OSTransporteItem newPedidoItem) {
        int index = dados.indexOf(oldPedidoItem);
        dados.set(index, newPedidoItem);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                return true;

            default:
                return false;
        }
    }

    public void addList(List<OSTransporteItem> vendaItens) {
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
