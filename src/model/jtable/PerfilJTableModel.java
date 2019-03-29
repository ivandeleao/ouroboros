/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.bean.principal.Perfil;

/**
 *
 * @author ivand
 */
public class PerfilJTableModel extends AbstractTableModel {

    private final List<Perfil> dados;
    private final String[] colunas = {"Grupo"};

    public PerfilJTableModel() {
        dados = new ArrayList<>();
    }

    public PerfilJTableModel(List<Perfil> perfis) {
        dados = perfis;
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
        Perfil perfil = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return perfil.getGrupo().getNome();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Perfil perfil = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                perfil.setId((int) aValue);
                break;
            
            
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(Perfil aValue, int rowIndex) {
        Perfil perfil = dados.get(rowIndex);

        perfil = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Perfil getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Perfil perfil) {
        dados.add(perfil);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Perfil oldPerfil, Perfil newPerfil) {
        int index = dados.indexOf(oldPerfil);
        dados.set(index, newPerfil);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Perfil> perfis) {
        int oldCount = getRowCount();

        dados.addAll(perfis);

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
