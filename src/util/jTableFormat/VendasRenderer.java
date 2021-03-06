/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.jTableFormat;

/**
 *
 * @author ivand
 */
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import model.mysql.bean.principal.MovimentoFisicoStatus;
import model.mysql.bean.principal.documento.VendaStatus;

public class VendasRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        VendaStatus vendaStatus = (VendaStatus) table.getModel().getValueAt(row, table.getColumn("Status").getModelIndex());

        comp.setBackground(vendaStatus.getCor());
        
        /*comp.setForeground(Color.BLACK);
        switch (vendaStatus) {
            case CANCELADO:
                comp.setBackground(Color.RED);
                comp.setForeground(Color.WHITE);
                break;
                
            case ORÇAMENTO:
                comp.setBackground(Color.GRAY);
                comp.setForeground(Color.WHITE);
                break;
                
            case PREPARAÇÃO_PREVISTA:
            case ENTREGA_PREVISTA:
            case RECEBIMENTO_PREVISTO:
                comp.setBackground(new Color(255, 255, 120)); //amarelo
                break;
            case PREPARAÇÃO_ATRASADA:
            case ENTREGA_ATRASADA:
            case RECEBIMENTO_ATRASADO:
                comp.setBackground(new Color(255, 204, 204)); //vermelho
                break;
            case PREPARAÇÃO_CONCLUÍDA:
            case ENTREGA_CONCLUÍDA:
            case RECEBIMENTO_CONCLUÍDO:
                comp.setBackground(new Color(210, 255, 210)); //verde
                break;
            case ANDAMENTO:
                comp.setBackground(new Color(210, 210, 255)); //azul
                break;
            case AGUARDANDO:
                comp.setBackground(new Color(248, 177, 70)); //laranja
                break;
            default:
                comp.setBackground(Color.WHITE);
        }*/

        return comp;
    }
}
