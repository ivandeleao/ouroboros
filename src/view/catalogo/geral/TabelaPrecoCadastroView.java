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
import model.jtable.catalogo.TabelaPrecoVariacaoJTableModel;
import model.mysql.bean.principal.catalogo.TabelaPreco;
import model.mysql.bean.principal.catalogo.TabelaPrecoVariacao;
import model.mysql.dao.principal.catalogo.TabelaPrecoDAO;
import model.mysql.dao.principal.catalogo.TabelaPrecoVariacaoDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;

/**
 *
 * @author ivand
 */
public class TabelaPrecoCadastroView extends javax.swing.JDialog {
    TabelaPrecoDAO tabelaPrecoDAO = new TabelaPrecoDAO();
    TabelaPrecoVariacaoDAO tabelaPrecoVariacaoDAO = new TabelaPrecoVariacaoDAO();
    TabelaPreco tabelaPreco;
    
    TabelaPrecoVariacaoJTableModel tabelaPrecoVariacaoJTableModel = new TabelaPrecoVariacaoJTableModel();
    
    
    private TabelaPrecoCadastroView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public TabelaPrecoCadastroView(TabelaPreco categoria) {
        super(MAIN_VIEW, true);
        initComponents();
        definirAtalhos();
        
        this.tabelaPreco = categoria;
        
        carregarDados();
        
        txtNome.requestFocus();
        
        formatarTabelaPrecoVariacoes();
        carregarTabelaPrecoVariacoes();
        
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
        if(tabelaPreco != null) {
            txtNome.setText(tabelaPreco.getNome());
        }
    }
    
    private void formatarTabelaPrecoVariacoes() {
        tblVariacao.setModel(tabelaPrecoVariacaoJTableModel);

        tblVariacao.setRowHeight(24);
        tblVariacao.setIntercellSpacing(new Dimension(10, 10));
        
        tblVariacao.getColumn("Id").setPreferredWidth(200);
        tblVariacao.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblVariacao.getColumn("Valor Inicial").setPreferredWidth(200);
        tblVariacao.getColumn("Valor Inicial").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblVariacao.getColumn("Valor Final").setPreferredWidth(200);
        tblVariacao.getColumn("Valor Final").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblVariacao.getColumn("Acréscimo").setPreferredWidth(200);
        tblVariacao.getColumn("Acréscimo").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblVariacao.getColumn("Desconto").setPreferredWidth(200);
        tblVariacao.getColumn("Desconto").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
    }
    
    private void carregarTabelaPrecoVariacoes() {
        tabelaPrecoVariacaoJTableModel.clear();
        tabelaPrecoVariacaoJTableModel.addList(tabelaPreco.getTabelaPrecoVariacoes());
        
        if(tblVariacao.getRowCount() > 0 && tblVariacao.getSelectedRow() > -1) {
            int index = tblVariacao.getSelectedRow();
            tblVariacao.setRowSelectionInterval(index, index);
        }
        
    }
    
    private void adicionarTabelaPrecoVariacao() {
        if(tabelaPreco.getId() == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Salve a tabela antes de adicionar uma variação.", "Atenção", JOptionPane.WARNING_MESSAGE);
            
        } else if(tabelaPreco.getTabelaPrecoVariacoes().size() == 1 && !tabelaPreco.getTabelaPrecoVariacoes().get(0).isComIntervalo()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "A variação já cadastrada não possui intervalo de valor.\r\nSó é possível usar múltiplas variações usando intervalos.", "Atenção", JOptionPane.WARNING_MESSAGE);
            
        } else {
            new TabelaPrecoVariacaoCadastroView(tabelaPreco, new TabelaPrecoVariacao());

            carregarTabelaPrecoVariacoes();
            
        }
    }
    
    private void removerTabelaPrecoVariacao() {
        if(tblVariacao.getSelectedRow() > -1) {
            
            if(JOptionPane.showConfirmDialog(MAIN_VIEW, "Confirma exclusão?", 
                    "Atenção", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                
                TabelaPrecoVariacao v = tabelaPrecoVariacaoJTableModel.getRow(tblVariacao.getSelectedRow());
                
                tabelaPreco.removeTabelaPrecoVariacao(v);
                
                tabelaPrecoVariacaoDAO.delete(v);

                carregarTabelaPrecoVariacoes();
            }
            
        }
    }
    
    private void editarTabelaPrecoVariacao() {
        if(tblVariacao.getSelectedRow() > -1) {
            new TabelaPrecoVariacaoCadastroView(tabelaPreco, tabelaPrecoVariacaoJTableModel.getRow(tblVariacao.getSelectedRow()));
            //TabelaPrecoVariacao t = tamanhoCadastroView.getTabelaPrecoVariacao();
            //tabelaPreco.addTabelaPrecoVariacao(t);
            //tabelaPrecoDAO.save(tabelaPreco);
            carregarTabelaPrecoVariacoes();
        }
    }
    
    private void salvar() {
        String nome = txtNome.getText().trim();
        if(nome.isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, "Informe o nome", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtNome.requestFocus();
        } else {
            tabelaPreco.setNome(nome);
            
            tabelaPreco = tabelaPrecoDAO.save(tabelaPreco);
            //dispose();
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
        pnlPerfis = new javax.swing.JPanel();
        Variações = new javax.swing.JLabel();
        btnAdicionarVariacao = new javax.swing.JButton();
        btnRemoverVariacao = new javax.swing.JButton();
        btnEditarVariacao = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblVariacao = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cadastro de Tabela de Preço");
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

        pnlPerfis.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        Variações.setBackground(new java.awt.Color(122, 138, 153));
        Variações.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        Variações.setForeground(java.awt.Color.white);
        Variações.setText("Variações");
        Variações.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        Variações.setOpaque(true);

        btnAdicionarVariacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-add-20.png"))); // NOI18N
        btnAdicionarVariacao.setToolTipText("Adicionar");
        btnAdicionarVariacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarVariacaoActionPerformed(evt);
            }
        });

        btnRemoverVariacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-do-not-disturb-20.png"))); // NOI18N
        btnRemoverVariacao.setToolTipText("Remover");
        btnRemoverVariacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverVariacaoActionPerformed(evt);
            }
        });

        btnEditarVariacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-pencil-drawing-20.png"))); // NOI18N
        btnEditarVariacao.setToolTipText("Editar");
        btnEditarVariacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarVariacaoActionPerformed(evt);
            }
        });

        tblVariacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblVariacao.setModel(new javax.swing.table.DefaultTableModel(
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
        tblVariacao.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblVariacaoFocusGained(evt);
            }
        });
        tblVariacao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVariacaoMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblVariacao);

        jLabel2.setForeground(java.awt.Color.blue);
        jLabel2.setText("Uma tabela pode possuir múltiplas variações. Em tal caso é obrigatório informar intervalo de valor");

        jLabel3.setForeground(java.awt.Color.blue);
        jLabel3.setText("Se o valor de um produto não se encaixar em um intervalo, o valor do mesmo não será alterado");

        javax.swing.GroupLayout pnlPerfisLayout = new javax.swing.GroupLayout(pnlPerfis);
        pnlPerfis.setLayout(pnlPerfisLayout);
        pnlPerfisLayout.setHorizontalGroup(
            pnlPerfisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Variações, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlPerfisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPerfisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPerfisLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlPerfisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAdicionarVariacao)
                            .addComponent(btnRemoverVariacao, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnEditarVariacao, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(pnlPerfisLayout.createSequentialGroup()
                        .addGroup(pnlPerfisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlPerfisLayout.setVerticalGroup(
            pnlPerfisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPerfisLayout.createSequentialGroup()
                .addComponent(Variações)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPerfisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPerfisLayout.createSequentialGroup()
                        .addComponent(btnAdicionarVariacao)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverVariacao)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditarVariacao)
                        .addGap(0, 55, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                        .addComponent(txtNome)
                        .addGap(18, 18, 18)
                        .addComponent(btnFechar)
                        .addGap(18, 18, 18)
                        .addComponent(btnsalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnsalvar)
                        .addComponent(btnFechar))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)))
                .addGap(18, 18, 18)
                .addComponent(pnlPerfis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void btnAdicionarVariacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarVariacaoActionPerformed
        adicionarTabelaPrecoVariacao();
    }//GEN-LAST:event_btnAdicionarVariacaoActionPerformed

    private void btnRemoverVariacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverVariacaoActionPerformed
        removerTabelaPrecoVariacao();
    }//GEN-LAST:event_btnRemoverVariacaoActionPerformed

    private void btnEditarVariacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarVariacaoActionPerformed
        editarTabelaPrecoVariacao();
    }//GEN-LAST:event_btnEditarVariacaoActionPerformed

    private void tblVariacaoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblVariacaoFocusGained

    }//GEN-LAST:event_tblVariacaoFocusGained

    private void tblVariacaoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVariacaoMouseClicked
        if(evt.getClickCount() == 2) {
            editarTabelaPrecoVariacao();
        }
    }//GEN-LAST:event_tblVariacaoMouseClicked

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
            java.util.logging.Logger.getLogger(TabelaPrecoCadastroView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TabelaPrecoCadastroView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TabelaPrecoCadastroView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TabelaPrecoCadastroView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                TabelaPrecoCadastroView dialog = new TabelaPrecoCadastroView(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel Variações;
    private javax.swing.JButton btnAdicionarVariacao;
    private javax.swing.JButton btnEditarVariacao;
    private javax.swing.JButton btnFechar;
    private javax.swing.JButton btnRemoverVariacao;
    private javax.swing.JButton btnsalvar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel pnlPerfis;
    private javax.swing.JTable tblVariacao;
    private javax.swing.JTextField txtNome;
    // End of variables declaration//GEN-END:variables
}
