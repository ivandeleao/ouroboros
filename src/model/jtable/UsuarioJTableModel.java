/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.Usuario;
import util.DateTime;

/**
 *
 * @author ivand
 */
public class UsuarioJTableModel extends AbstractTableModel {

    private final List<Usuario> dados;
    private final String[] colunas = {"Id", "Login", "Criação", "Atualização"};

    public UsuarioJTableModel() {
        dados = new ArrayList<>();
    }

    public UsuarioJTableModel(List<Usuario> usuarios) {
        dados = usuarios;
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
        Usuario usuario = dados.get(rowIndex);

        
        switch (columnIndex) {
            case 0:
                return usuario.getId();
            case 1:
                return usuario.getLogin();
            case 2:
                return DateTime.toString(usuario.getCriacao());
            case 3:
                return DateTime.toString(usuario.getAtualizacao());
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Usuario usuario = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                usuario.setId((int) aValue);
                break;
            case 1:
                //usuario.setNome((String) aValue);
                break;
            case 2:
                //usuario.setDescricao((String) aValue);
                break;
            
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(Usuario aValue, int rowIndex) {
        Usuario usuario = dados.get(rowIndex);

        usuario = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Usuario getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Usuario usuario) {
        dados.add(usuario);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Usuario oldCliente, Usuario newCliente) {
        int index = dados.indexOf(oldCliente);
        dados.set(index, newCliente);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Usuario> usuarios) {
        int oldCount = getRowCount();

        dados.addAll(usuarios);

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
