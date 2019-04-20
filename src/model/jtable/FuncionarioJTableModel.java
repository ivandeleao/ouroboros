/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JCheckBox;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.catalogo.Categoria;
import model.mysql.bean.principal.Funcionario;
import model.mysql.bean.fiscal.UnidadeComercial;
import util.DateTime;

/**
 *
 * @author ivand
 */
public class FuncionarioJTableModel extends AbstractTableModel {

    private final List<Funcionario> dados;
    private final String[] colunas = {"Id", "Nome", "Endereço", "Telefone", "CPF"};

    public FuncionarioJTableModel() {
        dados = new ArrayList<>();
    }

    public FuncionarioJTableModel(List<Funcionario> funcionarios) {
        dados = funcionarios;
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
        Funcionario funcionario = dados.get(rowIndex);

        
        switch (columnIndex) {
            case 0:
                return funcionario.getId();
            case 1:
                return funcionario.getNome();
            case 2:
                return funcionario.getEnderecoCompleto();
            case 3:
                return funcionario.getTelefone1();
            case 4:
                return funcionario.getCpf();
                
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Funcionario funcionario = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                funcionario.setId((int) aValue);
                break;
            case 1:
                //funcionario.setNome((String) aValue);
                break;
            case 2:
                //funcionario.setDescricao((String) aValue);
                break;
            case 3:
                //funcionario.setValorVenda((BigDecimal) aValue);
                break;
            case 4:
                //funcionario.setCodigo((String) aValue);
                break;
            case 5:
                //funcionario.setUnidadeComercialVenda((UnidadeComercial) aValue);
                break;
            case 6:
                break;
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(Funcionario aValue, int rowIndex) {
        Funcionario funcionario = dados.get(rowIndex);

        funcionario = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Funcionario getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Funcionario funcionario) {
        dados.add(funcionario);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Funcionario oldCliente, Funcionario newCliente) {
        int index = dados.indexOf(oldCliente);
        dados.set(index, newCliente);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Funcionario> funcionarios) {
        int oldCount = getRowCount();

        dados.addAll(funcionarios);

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
