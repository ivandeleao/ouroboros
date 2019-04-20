/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.documento.Parcela;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class ParcelasPagarJTableModel extends AbstractTableModel {
    private final List<Parcela> dados;
    //private final String[] colunas = {"Status", "Vencimento", "Doc.Id", "Parcela", "Valor", "Dias Atraso", "Multa %", "M. Calc.", "Juros", "J. Calc.", "Valor Atual", "Acrésc %", "Desc %", "Valor Pago", "Data Pagto", "Meio Pagto", "Observação"};
    private final String[] colunas = {"Status", "Vencimento", "Doc.Id", "Parcela", "Valor", "Dias Atraso", "Valor Atual", "Acrésc %", "Desc %", "Valor Pago", "Data Pagto", "Meio Pagto", "Observação"};

    public ParcelasPagarJTableModel() {
        dados = new ArrayList<>();
    }

    public ParcelasPagarJTableModel(List<Parcela> ParcelaList) {
        dados = ParcelaList;
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
        Parcela parcela = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return parcela.getStatus();
            case 1:
                return DateTime.toStringDate(parcela.getVencimento());
            case 2:
                return parcela.getVenda().getId();
            case 3:
                return parcela.getNumeroDeTotal();
            case 4:
                return Decimal.toString(parcela.getValor());
            case 5:
                return parcela.getDiasEmAtraso();
            /*case 6:
                return Decimal.toString(parcela.getMulta());
            case 7:
                return Decimal.toString(parcela.getMultaCalculada());
            case 8:
                return parcela.getJurosFormatado();
            case 9:
                return Decimal.toString(parcela.getJurosCalculado());*/
            case 6:
                return Decimal.toString(parcela.getValorAtual());
            case 7:
                return Decimal.toString(parcela.getAcrescimoPercentual());
            case 8:
                return Decimal.toString(parcela.getDescontoPercentual());
            case 9:
                return Decimal.toString(parcela.getValorQuitado());
            case 10:
                return DateTime.toStringDate(parcela.getUltimoRecebimento());
            case 11:
                return parcela.getMeioDePagamento();
            case 12:
                return parcela.getVenda().getObservacao();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Parcela parcela = dados.get(rowIndex);
        //---

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(Parcela aValue, int rowIndex) {
        Parcela parcela = dados.get(rowIndex);

        parcela = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Parcela getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Parcela parcela) {
        dados.add(parcela);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Parcela oldItem, Parcela newItem) {
        int index = dados.indexOf(oldItem);
        dados.set(index, newItem);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Parcela> ParcelaList) {
        int oldCount = getRowCount();

        dados.addAll(ParcelaList);

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
