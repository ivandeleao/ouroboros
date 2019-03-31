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
        
        if(tblProduto.getRowCount() > 0){
            tblProduto.setRowSelectionInterval(0, 0);
        }
    }
    
    private void formatarTabela() {
        tblProduto.setModel(produtoPesquisaJTableModel);

        tblProduto.setRowHeight(24);
        tblProduto.setIntercellSpacing(new Dimension(10, 10));
        //id
        tblProduto.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblProduto.getColumnModel().getColumn(0).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //nome
        tblProduto.getColumnModel().getColumn(1).setPreferredWidth(800);
        //descrição
        tblProduto.getColumnModel().getColumn(2).setPreferredWidth(400);
        //valor
        tblProduto.getColumnModel().getColumn(3).setPreferredWidth(120);
        tblProduto.getColumnModel().getColumn(3).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //código
        tblProduto.getColumnModel().getColumn(4).setPreferredWidth(200);
        //unidade comercial
        tblProduto.getColumnModel().getColumn(5).setPreferredWidth(120);
        
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
        tblProduto = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        setTitle("Pesquisar Produto");

        txtBuscaRapida.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtBuscaRapida.setToolTipText("");
        txtBuscaRapida.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscaRapidaKeyReleased(evt);
            }
        });

        tblProduto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblProduto.setModel(new javax.swing.table.DefaultTableModel(
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
        tblProduto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblProdutoFocusGained(evt);
            }
        });
        tblProduto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProdutoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProduto);

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
                produto = produtoPesquisaJTableModel.getRow(tblProduto.getSelectedRow());
                dispose();
                break;
            case KeyEvent.VK_DOWN:
                index = tblProduto.getSelectedRow() + 1;
                if (index < tblProduto.getRowCount()) {
                    tblProduto.setRowSelectionInterval(index, index);
                    tblProduto.scrollRectToVisible(tblProduto.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_UP:
                index = tblProduto.getSelectedRow() - 1;
                if (index > -1) {
                    tblProduto.setRowSelectionInterval(index, index);
                    tblProduto.scrollRectToVisible(tblProduto.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_PAGE_DOWN:
                index = tblProduto.getSelectedRow() + 10;
                if (index > tblProduto.getRowCount() -1) {
                    index = tblProduto.getRowCount() -1;
                }
                tblProduto.setRowSelectionInterval(index, index);
                tblProduto.scrollRectToVisible(tblProduto.getCellRect(index, 0, true));
                break;
            case KeyEvent.VK_PAGE_UP:
                index = tblProduto.getSelectedRow() - 10;
                if (index < 0) {
                    index = 0;
                }
                tblProduto.setRowSelectionInterval(index, index);
                tblProduto.scrollRectToVisible(tblProduto.getCellRect(index, 0, true));
                break;
            default:
                carregarTabela();
        }
    }//GEN-LAST:event_txtBuscaRapidaKeyReleased

    private void tblProdutoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProdutoMouseClicked
        if(evt.getClickCount() == 2) {
            produto = produtoPesquisaJTableModel.getRow(tblProduto.getSelectedRow());
            dispose();
        }
    }//GEN-LAST:event_tblProdutoMouseClicked

    private void tblProdutoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblProdutoFocusGained
        txtBuscaRapida.requestFocus();
    }//GEN-LAST:event_tblProdutoFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblProduto;
    private javax.swing.JTextField txtBuscaRapida;
    // End of variables declaration//GEN-END:variables

    
}
