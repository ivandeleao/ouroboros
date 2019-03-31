/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.grupo;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import model.bean.principal.Grupo;
import model.dao.principal.GrupoDAO;
import model.jtable.GrupoJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;

/**
 *
 * @author ivand
 */
public class GrupoPesquisaView extends javax.swing.JDialog {

    //private static GrupoPesquisaView grupoPesquisaView;
    GrupoJTableModel grupoPesquisaJTableModel = new GrupoJTableModel();
    GrupoDAO grupoDAO = new GrupoDAO();

    List<Grupo> grupos = new ArrayList<>();
    
    Grupo grupo = null;

    public GrupoPesquisaView() {
        super(MAIN_VIEW, true);
        initComponents();

        formatarTabela();

        carregarTabela();
        
        this.setLocationRelativeTo(MAIN_VIEW);
        this.setVisible(true);
        
    }
    
    public GrupoPesquisaView(String buscar) {
        super(MAIN_VIEW, true);
        initComponents();

        formatarTabela();

        txtBuscaRapida.setText(buscar);
        carregarTabela();
        
        this.setLocationRelativeTo(MAIN_VIEW);
        this.setVisible(true);
    }
    
    
    public Grupo getGrupo(){
        return grupo;
    }
    
    private void carregarTabela() {
        String buscaRapida = txtBuscaRapida.getText();

        grupos = grupoDAO.findAll();

        grupoPesquisaJTableModel.clear();
        grupoPesquisaJTableModel.addList(grupos);
        
        if(tblGrupo.getRowCount() > 0){
            tblGrupo.setRowSelectionInterval(0, 0);
        }
    }
    
    private void formatarTabela() {
        tblGrupo.setModel(grupoPesquisaJTableModel);

        tblGrupo.setRowHeight(24);
        tblGrupo.setIntercellSpacing(new Dimension(10, 10));
        
        tblGrupo.getColumn("Id").setPreferredWidth(60);
        tblGrupo.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblGrupo.getColumn("Nome").setPreferredWidth(800);
        
        
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
        tblGrupo = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        setTitle("Pesquisar Grupo");

        txtBuscaRapida.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtBuscaRapida.setToolTipText("");
        txtBuscaRapida.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscaRapidaKeyReleased(evt);
            }
        });

        tblGrupo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblGrupo.setModel(new javax.swing.table.DefaultTableModel(
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
        tblGrupo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblGrupoFocusGained(evt);
            }
        });
        tblGrupo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblGrupoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblGrupo);

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
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtBuscaRapida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
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
                grupo = null;
                dispose();
                break;
            case KeyEvent.VK_ENTER:
                grupo = grupoPesquisaJTableModel.getRow(tblGrupo.getSelectedRow());
                dispose();
                break;
            case KeyEvent.VK_DOWN:
                index = tblGrupo.getSelectedRow() + 1;
                if (index < tblGrupo.getRowCount()) {
                    tblGrupo.setRowSelectionInterval(index, index);
                    tblGrupo.scrollRectToVisible(tblGrupo.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_UP:
                index = tblGrupo.getSelectedRow() - 1;
                if (index > -1) {
                    tblGrupo.setRowSelectionInterval(index, index);
                    tblGrupo.scrollRectToVisible(tblGrupo.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_PAGE_DOWN:
                index = tblGrupo.getSelectedRow() + 10;
                if (index > tblGrupo.getRowCount() -1) {
                    index = tblGrupo.getRowCount() -1;
                }
                tblGrupo.setRowSelectionInterval(index, index);
                tblGrupo.scrollRectToVisible(tblGrupo.getCellRect(index, 0, true));
                break;
            case KeyEvent.VK_PAGE_UP:
                index = tblGrupo.getSelectedRow() - 10;
                if (index < 0) {
                    index = 0;
                }
                tblGrupo.setRowSelectionInterval(index, index);
                tblGrupo.scrollRectToVisible(tblGrupo.getCellRect(index, 0, true));
                break;
            default:
                carregarTabela();
        }
    }//GEN-LAST:event_txtBuscaRapidaKeyReleased

    private void tblGrupoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblGrupoMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblGrupoMouseClicked

    private void tblGrupoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblGrupoFocusGained
        txtBuscaRapida.requestFocus();
    }//GEN-LAST:event_tblGrupoFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblGrupo;
    private javax.swing.JTextField txtBuscaRapida;
    // End of variables declaration//GEN-END:variables

    
}