/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.produto.geral;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import model.jtable.catalogo.ProdutoTamanhoJTableModel;
import model.jtable.catalogo.TamanhoJTableModel;
import model.mysql.bean.principal.catalogo.Categoria;
import model.mysql.bean.principal.catalogo.Tamanho;
import model.mysql.dao.principal.catalogo.CategoriaDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class CategoriaCadastroView extends javax.swing.JDialog {
    CategoriaDAO categoriaDAO = new CategoriaDAO();
    Categoria categoria;
    
    TamanhoJTableModel tamanhoJTableModel = new TamanhoJTableModel();
    
    
    private CategoriaCadastroView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public CategoriaCadastroView(Categoria categoria) {
        super(MAIN_VIEW, true);
        initComponents();
        definirAtalhos();
        
        this.categoria = categoria;
        
        carregarDados();
        
        txtNome.requestFocus();
        
        formatarTamanhos();
        carregarTamanhos();
        
        this.setLocationRelativeTo(MAIN_VIEW);
        this.setVisible(true);
    }
    
    private void definirAtalhos() {
        InputMap im = rootPane.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = rootPane.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "fechar");
        am.put("fechar", new FormKeyStroke("ESC"));

    }

    protected class FormKeyStroke extends AbstractAction {

        private final String key;

        public FormKeyStroke(String key) {
            this.key = key;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (key) {
                case "ESC":
                    dispose();
                    break;
            }
        }
    }

    
    private void carregarDados() {
        if(categoria != null) {
            txtNome.setText(categoria.getNome());
        }
    }
    
    private void formatarTamanhos() {
        tblTamanho.setModel(tamanhoJTableModel);

        tblTamanho.setRowHeight(24);
        tblTamanho.setIntercellSpacing(new Dimension(10, 10));
        
        tblTamanho.getColumn("Nome").setPreferredWidth(800);
        
    }
    
    private void carregarTamanhos() {
        tamanhoJTableModel.clear();
        tamanhoJTableModel.addList(categoria.getTamanhos());
        
        if(tblTamanho.getRowCount() > 0 && tblTamanho.getSelectedRow() > -1) {
            int index = tblTamanho.getSelectedRow();
            tblTamanho.setRowSelectionInterval(index, index);
        }
        
    }
    
    private void adicionarTamanho() {
        TamanhoCadastroView tamanhoCadastroView = new TamanhoCadastroView(new Tamanho());
        Tamanho t = tamanhoCadastroView.getTamanho();
        
        if(t != null) {
            categoria.addTamanho(t);
            categoriaDAO.save(categoria);
            carregarTamanhos();
        }
    }
    
    private void removerTamanho() {
        if(tblTamanho.getSelectedRow() > -1) {
            Tamanho t = tamanhoJTableModel.getRow(tblTamanho.getSelectedRow());
            
            //TO DO: verificar se existem produtos usando este tamanho antes de tentar excluir
            
            categoria.removeTamanho(t);
            categoriaDAO.save(categoria);
            carregarTamanhos();
        }
    }
    
    private void editarTamanho() {
        if(tblTamanho.getSelectedRow() > -1) {
            TamanhoCadastroView tamanhoCadastroView = new TamanhoCadastroView(tamanhoJTableModel.getRow(tblTamanho.getSelectedRow()));
            Tamanho t = tamanhoCadastroView.getTamanho();
            categoria.addTamanho(t);
            categoriaDAO.save(categoria);
            carregarTamanhos();
        }
    }
    
    private void salvar() {
        String nome = txtNome.getText().trim();
        if(nome.isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, "Informe o nome da categoria", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtNome.requestFocus();
        } else {
            categoria.setNome(nome);
            
            categoria = categoriaDAO.save(categoria);
            dispose();
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

        txtNome = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnsalvar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        pnlPerfis = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        btnAdicionarTamanho = new javax.swing.JButton();
        btnRemoverTamanho = new javax.swing.JButton();
        btnEditarTamanho = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTamanho = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cadastro de Categoria");
        setResizable(false);

        txtNome.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Nome");

        btnsalvar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnsalvar.setText("Salvar");
        btnsalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsalvarActionPerformed(evt);
            }
        });

        btnCancelar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        pnlPerfis.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel31.setBackground(new java.awt.Color(122, 138, 153));
        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel31.setForeground(java.awt.Color.white);
        jLabel31.setText("Tamanhos");
        jLabel31.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel31.setOpaque(true);

        btnAdicionarTamanho.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/add.png"))); // NOI18N
        btnAdicionarTamanho.setToolTipText("Adicionar");
        btnAdicionarTamanho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarTamanhoActionPerformed(evt);
            }
        });

        btnRemoverTamanho.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/delete.png"))); // NOI18N
        btnRemoverTamanho.setToolTipText("Remover");
        btnRemoverTamanho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverTamanhoActionPerformed(evt);
            }
        });

        btnEditarTamanho.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/pencil.png"))); // NOI18N
        btnEditarTamanho.setToolTipText("Editar");
        btnEditarTamanho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarTamanhoActionPerformed(evt);
            }
        });

        tblTamanho.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblTamanho.setModel(new javax.swing.table.DefaultTableModel(
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
        tblTamanho.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblTamanhoFocusGained(evt);
            }
        });
        tblTamanho.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTamanhoMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblTamanho);

        jLabel2.setForeground(java.awt.Color.blue);
        jLabel2.setText("Os tamanhos aqui definidos são propagados para os produtos pertencentes a esta categoria");

        javax.swing.GroupLayout pnlPerfisLayout = new javax.swing.GroupLayout(pnlPerfis);
        pnlPerfis.setLayout(pnlPerfisLayout);
        pnlPerfisLayout.setHorizontalGroup(
            pnlPerfisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlPerfisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPerfisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPerfisLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlPerfisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAdicionarTamanho)
                            .addComponent(btnRemoverTamanho, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnEditarTamanho, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(pnlPerfisLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlPerfisLayout.setVerticalGroup(
            pnlPerfisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPerfisLayout.createSequentialGroup()
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPerfisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPerfisLayout.createSequentialGroup()
                        .addComponent(btnAdicionarTamanho)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverTamanho)
                        .addGap(9, 9, 9)
                        .addComponent(btnEditarTamanho)
                        .addGap(0, 98, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlPerfis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtNome))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCancelar)
                        .addGap(18, 18, 18)
                        .addComponent(btnsalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPerfis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnsalvar)
                    .addComponent(btnCancelar))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnsalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsalvarActionPerformed
        salvar();
    }//GEN-LAST:event_btnsalvarActionPerformed

    private void btnAdicionarTamanhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarTamanhoActionPerformed
        adicionarTamanho();
    }//GEN-LAST:event_btnAdicionarTamanhoActionPerformed

    private void btnRemoverTamanhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverTamanhoActionPerformed
        removerTamanho();
    }//GEN-LAST:event_btnRemoverTamanhoActionPerformed

    private void btnEditarTamanhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarTamanhoActionPerformed
        editarTamanho();
    }//GEN-LAST:event_btnEditarTamanhoActionPerformed

    private void tblTamanhoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblTamanhoFocusGained

    }//GEN-LAST:event_tblTamanhoFocusGained

    private void tblTamanhoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTamanhoMouseClicked
        if(evt.getClickCount() == 2) {
            editarTamanho();
        }
    }//GEN-LAST:event_tblTamanhoMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CategoriaCadastroView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CategoriaCadastroView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CategoriaCadastroView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CategoriaCadastroView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CategoriaCadastroView dialog = new CategoriaCadastroView(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionarTamanho;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEditarTamanho;
    private javax.swing.JButton btnRemoverTamanho;
    private javax.swing.JButton btnsalvar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel pnlPerfis;
    private javax.swing.JTable tblTamanho;
    private javax.swing.JTextField txtNome;
    // End of variables declaration//GEN-END:variables
}
