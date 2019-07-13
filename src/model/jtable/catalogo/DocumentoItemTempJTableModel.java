/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.catalogo;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.fiscal.UnidadeComercial;
import model.nosql.nfe.Det;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class DocumentoItemTempJTableModel extends AbstractTableModel {

    private final List<Det> dados;
    private final String[] colunas = {"Código", "Descrição", "NCM", "UM", "Quantidade", "Valor", "Id Vinculado"};

    public DocumentoItemTempJTableModel() {
        dados = new ArrayList<>();
    }

    public DocumentoItemTempJTableModel(List<Det> itens) {
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
        Det det = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return det.getProd().getcProd();
            case 1:
                return det.getProd().getxProd();
            case 2:
                return det.getProd().getNcm();
            case 3:
                return det.getProd().getuCom();
            case 4:
                return det.getProd().getqCom();
            case 5:
                return det.getProd().getvUnCom();
            case 6:
                return det.getProd().isVinculado() ? det.getProd().getProduto().getId() : "-";
        }
        return null;
    }


    public void setValueAt(Det aValue, int rowIndex) {
        Det item = dados.get(rowIndex);

        item = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Det getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Det item) {
        dados.add(item);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Det oldDetuto, Det newDetuto) {
        int index = dados.indexOf(oldDetuto);
        dados.set(index, newDetuto);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Det> itens) {
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
