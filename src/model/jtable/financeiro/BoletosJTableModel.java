/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.financeiro;

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
public class BoletosJTableModel extends AbstractTableModel {

    private final List<Parcela> dados;
    private final String[] colunas = {"Status", "Vencimento", "Venda", "Parcela", "Cliente", "Valor", "Valor Atual", "Valor Recebido", "Data Recebido", "Observação", "Impressão", "Remessa", "Nosso Número"};

    public BoletosJTableModel() {
        dados = new ArrayList<>();
    }

    public BoletosJTableModel(List<Parcela> ParcelaList) {
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
        try {
            Parcela parcela = dados.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return parcela.getStatus();
                case 1:
                    return DateTime.toStringDataAbreviada(parcela.getVencimento());
                case 2:
                    return parcela.getVenda().getId();
                case 3:
                    return parcela.getNumeroDeTotal();
                case 4:
                    return parcela.getCliente().getNome();
                case 5:
                    return Decimal.toString(parcela.getValor());
                case 6:
                    return Decimal.toString(parcela.getValorAtual());
                case 7:
                    return Decimal.toString(parcela.getValorQuitado());
                case 8:
                    return DateTime.toStringDataAbreviada(parcela.getUltimoRecebimento());
                case 9:
                    return parcela.getVenda().getObservacao();
                case 10:
                    return DateTime.toStringDataAbreviada(parcela.getBoletoImpressao());
                case 11:
                    return DateTime.toStringDataAbreviada(parcela.getBoletoRemessa());
                case 12:
                    return parcela.getBoletoNossoNumero();
            }
        } catch (Exception e) {
            //nada
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Parcela parcela = dados.get(rowIndex);

        //--
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
