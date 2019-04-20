/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.financeiro;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.financeiro.ContaPagar;
import model.mysql.bean.principal.documento.Parcela;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;
import util.Decimal;
import view.venda.VendaView;

/**
 *
 * @author ivand
 */
public class ContasPagarJTableModel extends AbstractTableModel {
    private final List<ContaPagar> dados;
    private final String[] colunas = {"Status", "Vencimento", "Nome", "Valor", "Data Pagto", "Valor Pago", "Observação"};

    public ContasPagarJTableModel() {
        dados = new ArrayList<>();
    }

    public ContasPagarJTableModel(List<ContaPagar> contas) {
        dados = contas;
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
        ContaPagar conta = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return conta.getStatus();
            case 1:
                return DateTime.toStringDate(conta.getVencimento());
            case 2:
                return conta.getContaProgramada().getNome();
            case 3:
                return conta.getValor();
            case 4:
                return DateTime.toStringDate(conta.getDataPago());
            case 5:
                return Decimal.toString(conta.getValorPago());
            case 6:
                return conta.getObservacao();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        ContaPagar conta = dados.get(rowIndex);

        //--

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(ContaPagar aValue, int rowIndex) {
        ContaPagar conta = dados.get(rowIndex);

        conta = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public ContaPagar getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(ContaPagar conta) {
        dados.add(conta);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(ContaPagar oldItem, ContaPagar newItem) {
        int index = dados.indexOf(oldItem);
        dados.set(index, newItem);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<ContaPagar> contas) {
        int oldCount = getRowCount();

        dados.addAll(contas);

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
