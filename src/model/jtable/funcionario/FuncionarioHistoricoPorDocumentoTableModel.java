/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jtable.funcionario;

import model.jtable.BaseTableModel;
import model.mysql.bean.principal.documento.Venda;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class FuncionarioHistoricoPorDocumentoTableModel extends BaseTableModel {


    public FuncionarioHistoricoPorDocumentoTableModel() {
        super(new String[]{"Status", "Data", "Documento", "Cliente", "Produtos", "Serviços", "Com.Prod", "Com.Serv", "Total Com", "Pago", "Receber"});
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Venda venda = (Venda) dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return venda.getComissaoStatus();
            case 1:
                return DateTime.toStringDataAbreviada(venda.getDataHora());
            case 2:
                return venda.getId();
            case 3:
                return venda.getPessoa() != null ? venda.getPessoa().getNome() : "--NÃO INFORMADO--";
            case 4:
                return Decimal.toString(venda.getTotalProdutos());
            case 5:
                return Decimal.toString(venda.getTotalServicos());
            case 6:
                return Decimal.toString(venda.getTotalComissaoDocumentoProduto());
            case 7:
                return Decimal.toString(venda.getTotalComissaoDocumentoServico());
            case 8:
                return Decimal.toString(venda.getTotalComissaoDocumento());
            case 9:
                return Decimal.toString(venda.getTotalComissaoDocumentoPago());
            case 10:
                return Decimal.toString(venda.getTotalComissaoDocumentoReceber());
             
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Venda venda = (Venda) dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                //venda.setId((int) aValue);
                break;
            case 1:
                //venda.setDataHora((Timestamp) aValue);
                break;
            case 2:
                //venda.setPessoa((Pessoa) aValue);
                break;
            case 3:
                //venda.setCodigo((String) aValue);
                break;
            case 4:
                //venda.setUnidadeComercialVenda((UnidadeComercial) aValue);
                break;
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }


}
