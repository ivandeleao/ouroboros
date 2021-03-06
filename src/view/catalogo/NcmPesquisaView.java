/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.catalogo;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.List;
import model.mysql.bean.fiscal.Ncm;
import model.mysql.dao.fiscal.NcmDAO;
import model.jtable.NcmJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;

/**
 *
 * @author ivand
 */
public class NcmPesquisaView extends javax.swing.JDialog {

    NcmJTableModel ncmJTableModel = new NcmJTableModel();
    NcmDAO ncmDAO = new NcmDAO();

    List<Ncm> listNcm;
    
    Ncm ncm = null;

    /**
     * Creates new form NcmPesquisa
     * @param parent
     */
    public NcmPesquisaView(java.awt.Frame parent) {
        super(parent, true);
        initComponents();

        
        
        formatarTabela();

        loadTable();
        
        this.setLocationRelativeTo(MAIN_VIEW);
        this.setVisible(true);
        
    }
    
    
    public Ncm getNcm(){
        return ncm;
    }
    
    private void formatarTabela() {
        tblNcm.setModel(ncmJTableModel);

        tblNcm.setRowHeight(24);
        tblNcm.setIntercellSpacing(new Dimension(10, 10));
        //codigo
        tblNcm.getColumnModel().getColumn(0).setPreferredWidth(120);
        tblNcm.getColumnModel().getColumn(0).setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        //nome
        tblNcm.getColumnModel().getColumn(1).setPreferredWidth(800);
    }
    
    private void confirmar() {
        ncm = ncmJTableModel.getRow(tblNcm.getSelectedRow());
        dispose();
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
        tblNcm = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setTitle("Pesquisar NCM");

        txtBuscaRapida.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtBuscaRapida.setToolTipText("");
        txtBuscaRapida.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscaRapidaKeyReleased(evt);
            }
        });

        tblNcm.setModel(new javax.swing.table.DefaultTableModel(
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
        tblNcm.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblNcmFocusGained(evt);
            }
        });
        tblNcm.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblNcmMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblNcm);

        jLabel1.setForeground(java.awt.Color.blue);
        jLabel1.setText("Rolar: PageUp e PageDown | Confirmar: Enter | Cancelar: Esc");

        jLabel2.setForeground(java.awt.Color.red);
        jLabel2.setText("NCMs genéricos não aparecem nesta lista. Devem ser digitados diretamente no cadastro do produto.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2))
                    .addComponent(txtBuscaRapida)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 964, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtBuscaRapida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscaRapidaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscaRapidaKeyReleased
        int index;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                ncm = null;
                dispose();
                break;
            case KeyEvent.VK_ENTER:
                confirmar();
                break;
            case KeyEvent.VK_DOWN:
                index = tblNcm.getSelectedRow() + 1;
                if (index < tblNcm.getRowCount()) {
                    tblNcm.setRowSelectionInterval(index, index);
                    tblNcm.scrollRectToVisible(tblNcm.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_UP:
                index = tblNcm.getSelectedRow() - 1;
                if (index > -1) {
                    tblNcm.setRowSelectionInterval(index, index);
                    tblNcm.scrollRectToVisible(tblNcm.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_PAGE_DOWN:
                index = tblNcm.getSelectedRow() + 10;
                if (index > tblNcm.getRowCount() -1) {
                    index = tblNcm.getRowCount() -1;
                }
                tblNcm.setRowSelectionInterval(index, index);
                tblNcm.scrollRectToVisible(tblNcm.getCellRect(index, 0, true));
                break;
            case KeyEvent.VK_PAGE_UP:
                index = tblNcm.getSelectedRow() - 10;
                if (index < 0) {
                    index = 0;
                }
                tblNcm.setRowSelectionInterval(index, index);
                tblNcm.scrollRectToVisible(tblNcm.getCellRect(index, 0, true));
                break;
            default:
                loadTable();
        }
    }//GEN-LAST:event_txtBuscaRapidaKeyReleased

    private void tblNcmMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblNcmMouseClicked
        if(evt.getClickCount() == 2) {
            confirmar();
        }
    }//GEN-LAST:event_tblNcmMouseClicked

    private void tblNcmFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblNcmFocusGained
        txtBuscaRapida.requestFocus();
    }//GEN-LAST:event_tblNcmFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblNcm;
    private javax.swing.JTextField txtBuscaRapida;
    // End of variables declaration//GEN-END:variables

    private void loadTable() {
        String buscaRapida = txtBuscaRapida.getText();

        listNcm = ncmDAO.findAllByCodigo(buscaRapida);

        ncmJTableModel.clear();
        ncmJTableModel.addList(listNcm);
        
        if(tblNcm.getRowCount() > 0){
            tblNcm.setRowSelectionInterval(0, 0);
        }
    }
}
