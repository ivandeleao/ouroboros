/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.documento;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.fiscal.nfe.DocumentoReferenciado;

/**
 *
 * @author ivand
 */
public class NfeDocumentosReferenciadosJTableModel extends AbstractTableModel {

    private final List<DocumentoReferenciado> dados;
    private final String[] colunas = {"Chave"};

    public NfeDocumentosReferenciadosJTableModel() {
        dados = new ArrayList<>();
    }

    public NfeDocumentosReferenciadosJTableModel(List<DocumentoReferenciado> documentosReferenciados) {
        dados = documentosReferenciados;
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
        DocumentoReferenciado documentoReferenciado = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return documentoReferenciado.getChave();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        DocumentoReferenciado documentoReferenciado = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                documentoReferenciado.setId((int) aValue);
                break;
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(DocumentoReferenciado aValue, int rowIndex) {
        //DocumentoReferenciado documentoReferenciado = dados.get(rowIndex);

        //documentoReferenciado = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public DocumentoReferenciado getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(DocumentoReferenciado documentoReferenciado) {
        dados.add(documentoReferenciado);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(DocumentoReferenciado oldProduto, DocumentoReferenciado newProduto) {
        int index = dados.indexOf(oldProduto);
        dados.set(index, newProduto);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<DocumentoReferenciado> documentosReferenciados) {
        int oldCount = getRowCount();

        dados.addAll(documentosReferenciados);

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
