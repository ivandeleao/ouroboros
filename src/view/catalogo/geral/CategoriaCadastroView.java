/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.catalogo.geral;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import model.jtable.catalogo.SubcategoriaJTableModel;
import model.jtable.catalogo.TamanhoJTableModel;
import model.mysql.bean.principal.catalogo.Categoria;
import model.mysql.bean.principal.catalogo.Subcategoria;
import model.mysql.bean.principal.catalogo.Tamanho;
import model.mysql.dao.principal.catalogo.CategoriaDAO;
import static ouroboros.Ouroboros.MAIN_VIEW;

/**
 *
 * @author ivand
 */
public class CategoriaCadastroView extends javax.swing.JDialog {
    CategoriaDAO categoriaDAO = new CategoriaDAO();
    Categoria categoria;
    
    SubcategoriaJTableModel subcategoriaJTableModel = new SubcategoriaJTableModel();
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
        
        formatarSubcategorias();
        carregarSubcategorias();
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
    
    private void formatarSubcategorias() {
        tblSubcategoria.setModel(subcategoriaJTableModel);

        tblSubcategoria.setRowHeight(24);
        tblSubcategoria.setIntercellSpacing(new Dimension(10, 10));
        
        tblSubcategoria.getColumn("Nome").setPreferredWidth(800);
        
    }
    
    private void carregarSubcategorias() {
        subcategoriaJTableModel.clear();
        subcategoriaJTableModel.addList(categoria.getSubcategorias());
        
        if(tblSubcategoria.getRowCount() > 0 && tblSubcategoria.getSelectedRow() > -1) {
            int index = tblSubcategoria.getSelectedRow();
            tblSubcategoria.setRowSelectionInterval(index, index);
        }
        
    }
    
    private void adicionarSubcategoria() {
        SubcategoriaCadastroView subcategoriaCadastroView = new SubcategoriaCadastroView(categoria, new Subcategoria());
        Subcategoria s = subcategoriaCadastroView.getSubcategoria();
        
        if(s.getId() != null) {
            categoria.addSubcategoria(s);
            categoriaDAO.save(categoria);
            carregarSubcategorias();
        }
    }
    
    private void removerSubcategoria() {
        if(tblSubcategoria.getSelectedRow() > -1) {
            Subcategoria t = subcategoriaJTableModel.getRow(tblSubcategoria.getSelectedRow());
            
            //TO DO: verificar se existem produtos usando este subcategoria antes de tentar excluir
            
            categoria.removeSubcategoria(t);
            categoriaDAO.save(categoria);
            carregarSubcategorias();
        }
    }
    
    private void editarSubcategoria() {
        if(tblSubcategoria.getSelectedRow() > -1) {
            SubcategoriaCadastroView subcategoriaCadastroView = new SubcategoriaCadastroView(categoria, subcategoriaJTableModel.getRow(tblSubcategoria.getSelectedRow()));
            Subcategoria s = subcategoriaCadastroView.getSubcategoria();
            categoria.addSubcategoria(s);
            categoriaDAO.save(categoria);
            carregarSubcategorias();
        }
    }
    
    //--------------------------------------------------------------------------
    
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
        TamanhoCadastroView tamanhoCadastroView = new TamanhoCadastroView(categoria, new Tamanho());
        Tamanho t = tamanhoCadastroView.getTamanho();
        
        if(t.getId() != null) {
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
            TamanhoCadastroView tamanhoCadastroView = new TamanhoCadastroView(categoria, tamanhoJTableModel.getRow(tblTamanho.getSelectedRow()));
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
        btnFechar = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblSubcategoria = new javax.swing.JTable();
        btnAdicionarSubcategoria = new javax.swing.JButton();
        btnRemoverSubcategoria = new javax.swing.JButton();
        btnEditarSubcategoria = new javax.swing.JButton();
        pnlPerfis = new javax.swing.JPanel();
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

        btnFechar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnFechar.setText("Fechar");
        btnFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFecharActionPerformed(evt);
            }
        });

        tblSubcategoria.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblSubcategoria.setModel(new javax.swing.table.DefaultTableModel(
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
        tblSubcategoria.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblSubcategoriaFocusGained(evt);
            }
        });
        tblSubcategoria.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSubcategoriaMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblSubcategoria);

        btnAdicionarSubcategoria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/add.png"))); // NOI18N
        btnAdicionarSubcategoria.setToolTipText("Adicionar");
        btnAdicionarSubcategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarSubcategoriaActionPerformed(evt);
            }
        });

        btnRemoverSubcategoria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/delete.png"))); // NOI18N
        btnRemoverSubcategoria.setToolTipText("Remover");
        btnRemoverSubcategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverSubcategoriaActionPerformed(evt);
            }
        });

        btnEditarSubcategoria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/pencil.png"))); // NOI18N
        btnEditarSubcategoria.setToolTipText("Editar");
        btnEditarSubcategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarSubcategoriaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAdicionarSubcategoria)
                    .addComponent(btnRemoverSubcategoria, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnEditarSubcategoria, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnAdicionarSubcategoria)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverSubcategoria)
                        .addGap(9, 9, 9)
                        .addComponent(btnEditarSubcategoria)
                        .addContainerGap(135, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        jTabbedPane1.addTab("Subcategorias", jPanel1);

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
            .addGroup(pnlPerfisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPerfisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(pnlPerfisLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addGroup(pnlPerfisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAdicionarTamanho)
                            .addComponent(btnRemoverTamanho, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnEditarTamanho, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        pnlPerfisLayout.setVerticalGroup(
            pnlPerfisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPerfisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPerfisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPerfisLayout.createSequentialGroup()
                        .addComponent(btnAdicionarTamanho)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverTamanho)
                        .addGap(9, 9, 9)
                        .addComponent(btnEditarTamanho)
                        .addGap(0, 104, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Tamanhos", pnlPerfis);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtNome))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnFechar)
                        .addGap(18, 18, 18)
                        .addComponent(btnsalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnsalvar)
                    .addComponent(btnFechar))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFecharActionPerformed
        dispose();
    }//GEN-LAST:event_btnFecharActionPerformed

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

    private void tblSubcategoriaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblSubcategoriaFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_tblSubcategoriaFocusGained

    private void tblSubcategoriaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSubcategoriaMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblSubcategoriaMouseClicked

    private void btnAdicionarSubcategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarSubcategoriaActionPerformed
        adicionarSubcategoria();
    }//GEN-LAST:event_btnAdicionarSubcategoriaActionPerformed

    private void btnRemoverSubcategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverSubcategoriaActionPerformed
        removerSubcategoria();
    }//GEN-LAST:event_btnRemoverSubcategoriaActionPerformed

    private void btnEditarSubcategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarSubcategoriaActionPerformed
        editarSubcategoria();
    }//GEN-LAST:event_btnEditarSubcategoriaActionPerformed

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
    private javax.swing.JButton btnAdicionarSubcategoria;
    private javax.swing.JButton btnAdicionarTamanho;
    private javax.swing.JButton btnEditarSubcategoria;
    private javax.swing.JButton btnEditarTamanho;
    private javax.swing.JButton btnFechar;
    private javax.swing.JButton btnRemoverSubcategoria;
    private javax.swing.JButton btnRemoverTamanho;
    private javax.swing.JButton btnsalvar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel pnlPerfis;
    private javax.swing.JTable tblSubcategoria;
    private javax.swing.JTable tblTamanho;
    private javax.swing.JTextField txtNome;
    // End of variables declaration//GEN-END:variables
}
