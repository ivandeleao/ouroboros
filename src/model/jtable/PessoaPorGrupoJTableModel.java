/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.bean.temp.PessoaPorGrupo;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class PessoaPorGrupoJTableModel extends AbstractTableModel {

    private final List<PessoaPorGrupo> dados;
    private final String[] colunas = {"Id", "Nome", "Dia de Vencimento", "Observação", "Último Vencimento", "Último Valor"};

    public PessoaPorGrupoJTableModel() {
        dados = new ArrayList<>();
    }

    public PessoaPorGrupoJTableModel(List<PessoaPorGrupo> pessoasPorGrupo) {
        dados = pessoasPorGrupo;
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
        PessoaPorGrupo pessoaPorGrupo = dados.get(rowIndex);

        
        switch (columnIndex) {
            case 0:
                return pessoaPorGrupo.getPessoa().getId();
            case 1:
                return pessoaPorGrupo.getPessoa().getNome();
            case 2:
                return pessoaPorGrupo.getPerfil().getDiaVencimento();
            case 3:
                return "obs";
            case 4:
                if(pessoaPorGrupo.getParcela() != null && pessoaPorGrupo.getParcela().getVencimento() != null) {
                    return DateTime.toStringDataAbreviada(pessoaPorGrupo.getParcela().getVencimento());
                } else {
                    return "-";
                }
            case 5:
                if(pessoaPorGrupo.getParcela() != null) {
                    return Decimal.toString(pessoaPorGrupo.getParcela().getValor());
                } else {
                    return "-";
                }
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        PessoaPorGrupo pessoaPorGrupo = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                //pessoaPorGrupo.setId((int) aValue);
                break;
            case 1:
                //pessoaPorGrupo.setNome((String) aValue);
                break;
            case 2:
                //pessoaPorGrupo.setDescricao((String) aValue);
                break;
            case 3:
                //pessoaPorGrupo.setValorVenda((BigDecimal) aValue);
                break;
            case 4:
                //pessoaPorGrupo.setCodigo((String) aValue);
                break;
            case 5:
                //pessoaPorGrupo.setUnidadeComercialVenda((UnidadeComercial) aValue);
                break;
            case 6:
                break;
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void setValueAt(PessoaPorGrupo aValue, int rowIndex) {
        PessoaPorGrupo pessoaPorGrupo = dados.get(rowIndex);

        pessoaPorGrupo = aValue;

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public PessoaPorGrupo getRow(int rowIndex) {
        return dados.get(rowIndex);
    }

    public void addRow(PessoaPorGrupo pessoaPorGrupo) {
        dados.add(pessoaPorGrupo);
        fireTableDataChanged();
        int lastIndex = getRowCount() - 1;
        fireTableRowsInserted(lastIndex, lastIndex);
    }

    public void removeRow(int rowIndex) {
        dados.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateRow(PessoaPorGrupo oldPessoa, PessoaPorGrupo newPessoa) {
        int index = dados.indexOf(oldPessoa);
        dados.set(index, newPessoa);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addList(List<PessoaPorGrupo> pessoasPorGrupo) {
        int oldCount = getRowCount();

        dados.addAll(pessoasPorGrupo);

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
