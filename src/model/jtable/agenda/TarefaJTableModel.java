/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.agenda;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.agenda.Tarefa;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class TarefaJTableModel extends AbstractTableModel {
    private final List<Tarefa> dados;
    private final String[] colunas = {"Data", "Hora", "Descrição", "Funcionário", "Cliente/Fornecedor"};

    public TarefaJTableModel() {
        dados = new ArrayList<>();
    }

    public TarefaJTableModel(List<Tarefa> tarefas) {
        dados = tarefas;
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
        Tarefa tarefa = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return DateTime.toString(tarefa.getData());
            case 1:
                return DateTime.toStringHoraMinuto(tarefa.getHora());
            case 2:
                return tarefa.getDescricao();
            case 3:
                return tarefa.getFuncionario() != null ? tarefa.getFuncionario().getNome() : "--NÃO INFORMADO--";
            case 4:
                return tarefa.getPessoa() != null ? tarefa.getPessoa().getNome() : "--NÃO INFORMADO--";
            
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Tarefa tarefa = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                //tarefa.setId((int) aValue);
                break;
            case 1:
                //tarefa.setDataHora((Timestamp) aValue);
                break;
            case 2:
                //tarefa.setPessoa((Pessoa) aValue);
                break;
            case 3:
                //tarefa.setCodigo((String) aValue);
                break;
            case 4:
                //tarefa.setUnidadeComercialTarefa((UnidadeComercial) aValue);
                break;
            //case 4:
            //tarefa.setCategorias((Set<Categoria>) aValue);            //case 4:
            //tarefa.setCategorias((Set<Categoria>) aValue);            //case 4:
            //tarefa.setCategorias((Set<Categoria>) aValue);            //case 4:
            //tarefa.setCategorias((Set<Categoria>) aValue);
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(Tarefa aValue, int rowIndex) {
        //Tarefa tarefa = dados.get(rowIndex);

        dados.set(rowIndex, aValue);
        
        //tarefa = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Tarefa getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Tarefa tarefa) {
        dados.add(tarefa);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Tarefa oldPedido, Tarefa newPedido) {
        int index = dados.indexOf(oldPedido);
        dados.set(index, newPedido);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Tarefa> tarefas) {
        int oldCount = getRowCount();

        dados.addAll(tarefas);

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
