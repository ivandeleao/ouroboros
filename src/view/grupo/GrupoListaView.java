/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.grupo;

import java.awt.Dimension;
import java.util.List;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.pessoa.Grupo;
import model.mysql.dao.principal.pessoa.GrupoDAO;
import model.jtable.pessoa.GrupoJTableModel;
import static ouroboros.Constants.*;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.JSwing;

/**
 *
 * @author ivand
 */
public class GrupoListaView extends javax.swing.JInternalFrame {
    private static GrupoListaView singleInstance = null;
    GrupoJTableModel grupoJTableModel = new GrupoJTableModel();
    GrupoDAO grupoDAO = new GrupoDAO();

    List<Grupo> grupos;

    public static GrupoListaView getSingleInstance(){
        /*if(!USUARIO.autorizarAcesso(Recurso.PESSOAS)) {
            return null;
        }*/
        
        if(singleInstance == null){
            singleInstance = new GrupoListaView();
        }
        return singleInstance;
    }
    
    /**
     * Creates new form ClienteCadastroView
     */
    private GrupoListaView() {
        initComponents();
        JSwing.startComponentsBehavior(this);
        
        formatarTabela();
        
        carregarTabela();
        
        
    }


    private void formatarTabela() {
        tblGrupo.setModel(grupoJTableModel);

        tblGrupo.setRowHeight(24);
        tblGrupo.setIntercellSpacing(new Dimension(10, 10));
        
        tblGrupo.getColumn("Id").setPreferredWidth(40);
        tblGrupo.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblGrupo.getColumn("Nome").setPreferredWidth(1000);
    }
    
    private void carregarTabela() {
        long start = System.currentTimeMillis();
        
        String nome = txtBuscaRapida.getText();
        
        grupos = grupoDAO.findByNome(nome, false);
        
        grupoJTableModel.clear();
        grupoJTableModel.addList(grupos);

        long elapsed = System.currentTimeMillis() - start;
        lblMensagem.setText("Consulta realizada em " + elapsed + "ms");

        lblRegistrosExibidos.setText(String.valueOf(grupos.size()));
    }
    
    private void excluir() {
        int rowIndex = tblGrupo.getSelectedRow();
        
        
        if(rowIndex >= 0) {
            Grupo grupo = grupoJTableModel.getRow(rowIndex);
            
            int resposta = JOptionPane.showConfirmDialog(MAIN_VIEW, "Excluir o item: " + grupo.getNome() + "?", "Atenção", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

            if(resposta == JOptionPane.OK_OPTION) {
                grupoDAO.delete(grupo);
                carregarTabela();
            }
        }
    }
    
    private void novo() {
        GrupoCadastroView grupoCadastroView = new GrupoCadastroView(new Grupo()); 
        
        carregarTabela();
    }
    
    private void editar() {
        if(tblGrupo.getSelectedRow() > -1) {
            Grupo grupo = grupoJTableModel.getRow(tblGrupo.getSelectedRow());
            GrupoCadastroView grupoCadastroView = new GrupoCadastroView(grupo); 
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblGrupo = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        lblMensagem = new javax.swing.JLabel();
        lblRegistrosExibidos = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        txtBuscaRapida = new javax.swing.JTextField();
        btnFiltrar = new javax.swing.JButton();
        btnRemoverFiltro = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnNovo = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();

        setClosable(true);
        setTitle("Pessoas");
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameActivated(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

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
        tblGrupo.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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
        tblGrupo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblGrupoKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblGrupo);

        jLabel1.setText("Editar: duplo clique");

        lblMensagem.setText("...");

        lblRegistrosExibidos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRegistrosExibidos.setText("0");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtBuscaRapida.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtBuscaRapida.setToolTipText("");
        txtBuscaRapida.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscaRapidaKeyReleased(evt);
            }
        });

        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnRemoverFiltro.setText("Remover Filtro");
        btnRemoverFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverFiltroActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Busca rápida");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtBuscaRapida))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnRemoverFiltro)
                        .addGap(18, 18, 18)
                        .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBuscaRapida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFiltrar)
                    .addComponent(btnRemoverFiltro))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setText("Registros exibidos:");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnNovo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/add.png"))); // NOI18N
        btnNovo.setText("Novo");
        btnNovo.setContentAreaFilled(false);
        btnNovo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNovo.setIconTextGap(10);
        btnNovo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoActionPerformed(evt);
            }
        });

        btnExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/delete.png"))); // NOI18N
        btnExcluir.setText("Excluir");
        btnExcluir.setContentAreaFilled(false);
        btnExcluir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExcluir.setPreferredSize(new java.awt.Dimension(120, 23));
        btnExcluir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 9, Short.MAX_VALUE))
        );

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
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(lblRegistrosExibidos, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1264, Short.MAX_VALUE)
                    .addComponent(lblMensagem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblMensagem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblRegistrosExibidos)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        txtBuscaRapida.requestFocus();
    }//GEN-LAST:event_formComponentShown

    private void btnNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoActionPerformed
        novo();
    }//GEN-LAST:event_btnNovoActionPerformed

    private void tblGrupoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblGrupoMouseClicked
        if(evt.getClickCount() == 2) {
            editar();
        }
    }//GEN-LAST:event_tblGrupoMouseClicked

    private void tblGrupoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblGrupoKeyReleased
    }//GEN-LAST:event_tblGrupoKeyReleased

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        MAIN_VIEW.removeTab(this.getName());
    }//GEN-LAST:event_formInternalFrameClosing

    private void txtBuscaRapidaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscaRapidaKeyReleased
        carregarTabela();
    }//GEN-LAST:event_txtBuscaRapidaKeyReleased

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnRemoverFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverFiltroActionPerformed
        txtBuscaRapida.setText("");
        carregarTabela();
        txtBuscaRapida.requestFocus();
    }//GEN-LAST:event_btnRemoverFiltroActionPerformed

    private void tblGrupoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblGrupoFocusGained
        //tableClientesUpdateRow();
        
    }//GEN-LAST:event_tblGrupoFocusGained

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        System.out.println("focus");
        //tableClientesUpdateRow();
    }//GEN-LAST:event_formFocusGained

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated

    }//GEN-LAST:event_formInternalFrameActivated

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        singleInstance = null;
    }//GEN-LAST:event_formInternalFrameClosed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        excluir();
    }//GEN-LAST:event_btnExcluirActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton btnRemoverFiltro;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMensagem;
    private javax.swing.JLabel lblRegistrosExibidos;
    private javax.swing.JTable tblGrupo;
    private javax.swing.JTextField txtBuscaRapida;
    // End of variables declaration//GEN-END:variables
}
