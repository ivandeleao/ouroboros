/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.produto;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.List;
import model.bean.principal.Produto;
import model.dao.principal.ProdutoDAO;
import model.jtable.ProdutoJTableModel;
import model.jtable.ProdutoPesquisaJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.jTableFormat.NumberRenderer;

/**
 *
 * @author ivand
 */
public class ProdutoPesquisaView extends javax.swing.JDialog {

    //private static ProdutoPesquisaView produtoPesquisaView;
    ProdutoPesquisaJTableModel produtoPesquisaJTableModel = new ProdutoPesquisaJTableModel();
    ProdutoDAO produtoDAO = new ProdutoDAO();

    List<Produto> produtos;
    
    Produto produto = null;

    public ProdutoPesquisaView() {
        super(MAIN_VIEW, true);
        initComponents();

        formatarTabela();

        carregarTabela();
        
        this.setLocationRelativeTo(MAIN_VIEW);
        this.setVisible(true);
        
    }
    
    public ProdutoPesquisaView(String buscar) {
        super(MAIN_VIEW, true);
        initComponents();

        formatarTabela();

        txtBuscaRapida.setText(buscar);
        carregarTabela();
        
        this.setLocationRelativeTo(MAIN_VIEW);
        this.setVisible(true);
    }
    
    
    public Produto getProduto(){
        return produto;
    }
    
    private void carregarTabela() {
        String buscaRapida = txtBuscaRapida.getText();

        produtos = produtoDAO.findByCriteria(buscaRapida, null, null, false, false);

        produtoPesquisaJTableModel.clear();
        produtoPesquisaJTableModel.addList(produtos);
        
        if(tableProdutos.getRowCount() > 0){
            tableProdutos.setRowSelectionInterval(0, 0);
        }
    }
    
    private void formatarTabela() {
        tableProdutos.setModel(produtoPesquisaJTableModel);

        tableProdutos.setRowHeight(24);
        tableProdutos.setIntercellSpacing(new Dimension(10, 10));
        //id
        tableProdutos.getColumnModel().getColumn(0).setPreferredWidth(60);
        tableProdutos.getColumnModel().getColumn(0).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //nome
        tableProdutos.getColumnModel().getColumn(1).setPreferredWidth(800);
        //descrição
        tableProdutos.getColumnModel().getColumn(2).setPreferredWidth(400);
        //valor
        tableProdutos.getColumnModel().getColumn(3).setPreferredWidth(120);
        tableProdutos.getColumnModel().getColumn(3).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //código
        tableProdutos.getColumnModel().getColumn(4).setPreferredWidth(200);
        //unidade comercial
        tableProdutos.getColumnModel().getColumn(5).setPreferredWidth(120);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtBuscaRapida = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableProdutos = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        setTitle("Pesquisar Produto");

        txtBuscaRapida.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtBuscaRapida.setToolTipText("");
        txtBuscaRapida.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscaRapidaKeyReleased(evt);
            }
        });

        tableProdutos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tableProdutos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tableProdutos.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tableProdutosFocusGained(evt);
            }
        });
        tableProdutos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableProdutosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableProdutos);

        jLabel1.setForeground(java.awt.Color.blue);
        jLabel1.setText("Rolar: PageUp e PageDown | Confirmar: Enter | Cancelar: Esc");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtBuscaRapida, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 964, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtBuscaRapida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscaRapidaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscaRapidaKeyReleased
        int index;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                produto = null;
                dispose();
                break;
            case KeyEvent.VK_ENTER:
                produto = produtoPesquisaJTableModel.getRow(tableProdutos.getSelectedRow());
                dispose();
                break;
            case KeyEvent.VK_DOWN:
                index = tableProdutos.getSelectedRow() + 1;
                if (index < tableProdutos.getRowCount()) {
                    tableProdutos.setRowSelectionInterval(index, index);
                    tableProdutos.scrollRectToVisible(tableProdutos.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_UP:
                index = tableProdutos.getSelectedRow() - 1;
                if (index > -1) {
                    tableProdutos.setRowSelectionInterval(index, index);
                    tableProdutos.scrollRectToVisible(tableProdutos.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_PAGE_DOWN:
                index = tableProdutos.getSelectedRow() + 10;
                if (index > tableProdutos.getRowCount() -1) {
                    index = tableProdutos.getRowCount() -1;
                }
                tableProdutos.setRowSelectionInterval(index, index);
                tableProdutos.scrollRectToVisible(tableProdutos.getCellRect(index, 0, true));
                break;
            case KeyEvent.VK_PAGE_UP:
                index = tableProdutos.getSelectedRow() - 10;
                if (index < 0) {
                    index = 0;
                }
                tableProdutos.setRowSelectionInterval(index, index);
                tableProdutos.scrollRectToVisible(tableProdutos.getCellRect(index, 0, true));
                break;
            default:
                carregarTabela();
        }
    }//GEN-LAST:event_txtBuscaRapidaKeyReleased

    private void tableProdutosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableProdutosMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tableProdutosMouseClicked

    private void tableProdutosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tableProdutosFocusGained
        txtBuscaRapida.requestFocus();
    }//GEN-LAST:event_tableProdutosFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tableProdutos;
    private javax.swing.JTextField txtBuscaRapida;
    // End of variables declaration//GEN-END:variables

    
}
