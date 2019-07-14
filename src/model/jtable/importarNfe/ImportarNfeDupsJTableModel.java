/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.importarNfe;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.nosql.nfe.Dup;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class ImportarNfeDupsJTableModel extends AbstractTableModel {

    private final List<Dup> dados;
    private final String[] colunas = {"Número", "Vencimento", "Valor"};

    public ImportarNfeDupsJTableModel() {
        dados = new ArrayList<>();
    }

    public ImportarNfeDupsJTableModel(List<Dup> itens) {
        dados = itens;
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
        Dup dup = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return dup.getnDup();
            case 1:
                return DateTime.toString(LocalDate.parse(dup.getdVenc()));
            case 2:
                return Decimal.toString(Decimal.fromStringComPonto(dup.getvDup()));
        }
        return null;
    }


    public void setValueAt(Dup aValue, int rowIndex) {
        Dup dup = dados.get(rowIndex);

        dup = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Dup getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Dup dup) {
        dados.add(dup);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Dup oldDuputo, Dup newDuputo) {
        int index = dados.indexOf(oldDuputo);
        dados.set(index, newDuputo);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Dup> itens) {
        int oldCount = getRowCount();

        dados.addAll(itens);

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
