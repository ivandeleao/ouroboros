/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.pessoa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JCheckBox;
import javax.swing.table.AbstractTableModel;
import model.mysql.bean.principal.catalogo.Categoria;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.bean.fiscal.UnidadeComercial;
import util.DateTime;

/**
 *
 * @author ivand
 */
public class PessoaJTableModel extends AbstractTableModel {

    private final List<Pessoa> dados;
    private final String[] colunas = {"Id", "Nome", "Nome Fantasia", "Endereço", "Telefone", "CPF/CNPJ", "Cliente", "Fornecedor"};

    public PessoaJTableModel() {
        dados = new ArrayList<>();
    }

    public PessoaJTableModel(List<Pessoa> clientes) {
        dados = clientes;
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
        Pessoa cliente = dados.get(rowIndex);

        
        switch (columnIndex) {
            case 0:
                return cliente.getId();
            case 1:
                return cliente.getNome();
            case 2:
                return cliente.getNomeFantasia();
            case 3:
                return cliente.getEnderecoCompleto();
            case 4:
                return cliente.getTelefone1();
            case 5:
                return cliente.getCpf() + cliente.getCnpj();
            case 6:
                return  cliente.isCliente();
            case 7:
                return  cliente.isFornecedor();
                
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Pessoa cliente = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                cliente.setId((int) aValue);
                break;
            case 1:
                //cliente.setNome((String) aValue);
                break;
            case 2:
                //cliente.setDescricao((String) aValue);
                break;
            case 3:
                //cliente.setValorVenda((BigDecimal) aValue);
                break;
            case 4:
                //cliente.setCodigo((String) aValue);
                break;
            case 5:
                //cliente.setUnidadeComercialVenda((UnidadeComercial) aValue);
                break;
            case 6:
                break;
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(Pessoa aValue, int rowIndex) {
        Pessoa cliente = dados.get(rowIndex);

        cliente = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Pessoa getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(Pessoa cliente) {
        dados.add(cliente);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(Pessoa oldCliente, Pessoa newCliente) {
        int index = dados.indexOf(oldCliente);
        dados.set(index, newCliente);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<Pessoa> clientes) {
        int oldCount = getRowCount();

        dados.addAll(clientes);

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
