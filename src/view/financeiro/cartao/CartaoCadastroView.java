/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.financeiro.cartao;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import model.jtable.financeiro.cartao.CartaoTaxasJTableModel;
import model.mysql.bean.principal.financeiro.Cartao;
import model.mysql.bean.principal.financeiro.CartaoTaxa;
import model.mysql.dao.principal.financeiro.CartaoDAO;
import model.mysql.dao.principal.financeiro.CartaoTaxaDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.Decimal;
import util.Numero;

/**
 *
 * @author ivand
 */
public class CartaoCadastroView extends javax.swing.JDialog {
    CartaoDAO cartaoDAO = new CartaoDAO();
    Cartao cartao;
    CartaoTaxaDAO cartaoTaxaDAO = new CartaoTaxaDAO();
    
    CartaoTaxasJTableModel cartaoTaxasJTableModel = new CartaoTaxasJTableModel();
    
    private CartaoCadastroView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public CartaoCadastroView(Cartao cartao) {
        super(MAIN_VIEW, true);
        initComponents();
        definirAtalhos();
        
        this.cartao = cartao;
        
        carregarDados();
        
        formatarTabelaTaxas();
        carregarTabelaTaxas();
        
        txtNome.requestFocus();
        
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
        if(cartao != null) {
            txtNome.setText(cartao.getNome());
            txtDiasRecebimento.setText(Numero.toStringTROCAR_PELO_INTEIRO(cartao.getDiasRecebimento()));
        }
    }
    
    private void formatarTabelaTaxas() {
        tblTaxas.setModel(cartaoTaxasJTableModel);

        tblTaxas.setRowHeight(24);
        tblTaxas.setIntercellSpacing(new Dimension(10, 10));
        
        tblTaxas.getColumn("Parcelas").setPreferredWidth(200);
        tblTaxas.getColumn("Parcelas").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblTaxas.getColumn("Taxa").setPreferredWidth(200);
        tblTaxas.getColumn("Taxa").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblTaxas.getColumn("Incluir Taxa").setPreferredWidth(200);
        
    }
    
    private void carregarTabelaTaxas() {
        cartaoTaxasJTableModel.clear();
        cartaoTaxasJTableModel.addList(cartao.getCartaoTaxas());
        
        if(tblTaxas.getRowCount() > 0 && tblTaxas.getSelectedRow() > -1) {
            int index = tblTaxas.getSelectedRow();
            tblTaxas.setRowSelectionInterval(index, index);
        }
    }
    
    private void adicionarCartaoTaxa() {
        if(cartao.getId() == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Salve antes de adicionar taxas.", "Atenção", JOptionPane.WARNING_MESSAGE);
            
        } else {
            new CartaoTaxaCadastroView(cartao, new CartaoTaxa());

            carregarTabelaTaxas();
            
        }
    }
    
    private void removerCartaoTaxa() {
        List<CartaoTaxa> taxas = cartao.getCartaoTaxas();
        
        if(taxas.size() > 0) {
            
            CartaoTaxa cartaoTaxa = taxas.get(taxas.size() - 1);
            
            if(JOptionPane.showConfirmDialog(MAIN_VIEW, 
                    "Confirma exclusão de " + cartaoTaxa.getParcelas() + "x : " + Decimal.toString(cartaoTaxa.getTaxa()), 
                    "Atenção", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                
                cartao.removeCartaoTaxa(cartaoTaxa);
                
                cartaoTaxaDAO.delete(cartaoTaxa);

                carregarTabelaTaxas();
            }
            
        }
    }
    
    private void editarCartaoTaxa() {
        if(tblTaxas.getSelectedRow() > -1) {
            new CartaoTaxaCadastroView(cartao, cartaoTaxasJTableModel.getRow(tblTaxas.getSelectedRow()));
            carregarTabelaTaxas();
        }
    }
    
    private void salvar() {
        String nome = txtNome.getText().trim();
        Integer diasRecebimento = Numero.fromStringToIntegerTROCAR_PELO_INTEIRO(txtDiasRecebimento.getText());
        
        if(nome.isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, "Informe o nome", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtNome.requestFocus();
            
        } else {
            cartao.setNome(nome);
            cartao.setDiasRecebimento(diasRecebimento);
            cartao = cartaoDAO.save(cartao);
            
            JOptionPane.showMessageDialog(rootPane, "Dados salvos", "Dados salvos", JOptionPane.INFORMATION_MESSAGE);
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
        btnSalvar = new javax.swing.JButton();
        btnFechar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        pnlPerfis = new javax.swing.JPanel();
        Variações = new javax.swing.JLabel();
        btnAdicionarVariacao = new javax.swing.JButton();
        btnRemoverVariacao = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTaxas = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        txtDiasRecebimento = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cadastro de Cartão");
        setResizable(false);

        txtNome.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        btnSalvar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnFechar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnFechar.setText("Fechar");
        btnFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFecharActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Nome");

        pnlPerfis.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        Variações.setBackground(new java.awt.Color(122, 138, 153));
        Variações.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        Variações.setForeground(java.awt.Color.white);
        Variações.setText("Taxas");
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

        tblTaxas.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblTaxas.setModel(new javax.swing.table.DefaultTableModel(
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
        tblTaxas.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblTaxasFocusGained(evt);
            }
        });
        tblTaxas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTaxasMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblTaxas);

        javax.swing.GroupLayout pnlPerfisLayout = new javax.swing.GroupLayout(pnlPerfis);
        pnlPerfis.setLayout(pnlPerfisLayout);
        pnlPerfisLayout.setHorizontalGroup(
            pnlPerfisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Variações, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
            .addGroup(pnlPerfisLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPerfisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAdicionarVariacao)
                    .addComponent(btnRemoverVariacao, javax.swing.GroupLayout.Alignment.TRAILING))
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
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE))
                .addContainerGap())
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Dias para recebimento");

        txtDiasRecebimento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiasRecebimento.setText("0");
        txtDiasRecebimento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDiasRecebimento.setName("inteiro"); // NOI18N

        jLabel1.setForeground(java.awt.Color.blue);
        jLabel1.setText("Alterações só afetam novos faturamentos por cartão");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(pnlPerfis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addGap(18, 18, 18)
                            .addComponent(txtNome)
                            .addGap(18, 18, 18)
                            .addComponent(btnFechar)
                            .addGap(18, 18, 18)
                            .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(txtDiasRecebimento, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnSalvar)
                        .addComponent(btnFechar))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtDiasRecebimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(pnlPerfis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFecharActionPerformed
        dispose();
    }//GEN-LAST:event_btnFecharActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        salvar();
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnAdicionarVariacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarVariacaoActionPerformed
        adicionarCartaoTaxa();
    }//GEN-LAST:event_btnAdicionarVariacaoActionPerformed

    private void btnRemoverVariacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverVariacaoActionPerformed
        removerCartaoTaxa();
    }//GEN-LAST:event_btnRemoverVariacaoActionPerformed

    private void tblTaxasFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblTaxasFocusGained

    }//GEN-LAST:event_tblTaxasFocusGained

    private void tblTaxasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTaxasMouseClicked
        if(evt.getClickCount() == 2) {
            editarCartaoTaxa();
        }
    }//GEN-LAST:event_tblTaxasMouseClicked

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
            java.util.logging.Logger.getLogger(CartaoCadastroView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CartaoCadastroView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CartaoCadastroView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CartaoCadastroView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CartaoCadastroView dialog = new CartaoCadastroView(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnFechar;
    private javax.swing.JButton btnRemoverVariacao;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel pnlPerfis;
    private javax.swing.JTable tblTaxas;
    private javax.swing.JFormattedTextField txtDiasRecebimento;
    private javax.swing.JTextField txtNome;
    // End of variables declaration//GEN-END:variables
}
