/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.financeiro.conta;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import model.jtable.financeiro.conta.ContaListaJTableModel;
import model.mysql.bean.principal.financeiro.Conta;
import model.mysql.dao.principal.financeiro.ContaDAO;
import model.nosql.ContaTipoEnum;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class ContaListaView extends javax.swing.JDialog {
    ContaDAO contaDAO = new ContaDAO();
    
    ContaListaJTableModel contaListaJTableModel = new ContaListaJTableModel();
    
    List<Conta> contas = new ArrayList<>();
    
    private ContaListaView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public ContaListaView() {
        super(MAIN_VIEW, true);
        initComponents();
        definirAtalhos();
        
        formatarTabela();
        carregarTabela();
        
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

    private void formatarTabela() {
        tblConta.setModel(contaListaJTableModel);

        tblConta.setRowHeight(30);
        tblConta.setIntercellSpacing(new Dimension(10, 10));
        
        tblConta.getColumn("Id").setPreferredWidth(80);
        tblConta.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblConta.getColumn("Data Criação").setPreferredWidth(200);
        tblConta.getColumn("Data Criação").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblConta.getColumn("Nome").setPreferredWidth(300);
        
        tblConta.getColumn("Data").setPreferredWidth(140);
        tblConta.getColumn("Data").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblConta.getColumn("Saldo").setPreferredWidth(180);
        tblConta.getColumn("Saldo").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
    }
    
    private void carregarTabela() {
        contas = contaDAO.findByTipo(ContaTipoEnum.CONTA_CORRENTE);
        
        contaListaJTableModel.clear();
        contaListaJTableModel.addList(contas);
        
        if(tblConta.getRowCount() > 0 && tblConta.getSelectedRow() > -1) {
            int index = tblConta.getSelectedRow();
            tblConta.setRowSelectionInterval(index, index);
        }
        
        exibirTotal();
        
    }
    
    private void exibirTotal() {
        BigDecimal total = BigDecimal.ZERO;
        
        if (!contas.isEmpty()) {
            total = contas.stream().map(Conta::getSaldo).reduce(BigDecimal::add).get();
        }
        
        txtTotal.setText(Decimal.toString(total));
    }
    
    private void editarConta() {
        if(tblConta.getSelectedRow() > -1) {
            new ContaCadastroView(contaListaJTableModel.getRow(tblConta.getSelectedRow()));
            
            carregarTabela();
        }
    }
    
    private void adicionarConta() {
        new ContaCadastroView(new Conta());

        carregarTabela();
    }
    
    private void removerConta() {
        if(tblConta.getSelectedRow() > -1) {
            
            Conta conta = contaListaJTableModel.getRow(tblConta.getSelectedRow());
            
            if(JOptionPane.showConfirmDialog(MAIN_VIEW, "Confirma exclusão da conta " + conta.getNome() + "?", 
                    "Atenção", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                
                contaDAO.delete(conta);
                
                carregarTabela();
            }
            
        }
    }
    
    private void dataConta() {
        if(tblConta.getSelectedRow() > -1) {
            
            Conta conta = contaListaJTableModel.getRow(tblConta.getSelectedRow());
            
            new ContaDataView(conta);

            carregarTabela();
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

        btnFechar = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblConta = new javax.swing.JTable();
        btnNovo = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        btnData = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Contas");
        setResizable(false);

        btnFechar.setText("Fechar");
        btnFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFecharActionPerformed(evt);
            }
        });

        tblConta.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblConta.setModel(new javax.swing.table.DefaultTableModel(
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
        tblConta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblContaFocusGained(evt);
            }
        });
        tblConta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblContaMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblConta);

        btnNovo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-add-20.png"))); // NOI18N
        btnNovo.setText("Novo");
        btnNovo.setToolTipText("");
        btnNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoActionPerformed(evt);
            }
        });

        btnExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-do-not-disturb-20.png"))); // NOI18N
        btnExcluir.setText("Excluir");
        btnExcluir.setToolTipText("");
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        btnData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-calendar-20.png"))); // NOI18N
        btnData.setText("Data");
        btnData.setToolTipText("");
        btnData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDataActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Total");

        txtTotal.setEditable(false);
        txtTotal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnNovo)
                        .addGap(18, 18, 18)
                        .addComponent(btnExcluir)
                        .addGap(18, 18, 18)
                        .addComponent(btnData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFechar))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnNovo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnExcluir, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnFechar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnData, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFecharActionPerformed
        dispose();
    }//GEN-LAST:event_btnFecharActionPerformed

    private void btnNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoActionPerformed
        adicionarConta();
    }//GEN-LAST:event_btnNovoActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        removerConta();
    }//GEN-LAST:event_btnExcluirActionPerformed

    private void tblContaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblContaFocusGained

    }//GEN-LAST:event_tblContaFocusGained

    private void tblContaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblContaMouseClicked
        if(evt.getClickCount() == 2) {
            editarConta();
        }
    }//GEN-LAST:event_tblContaMouseClicked

    private void btnDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDataActionPerformed
        dataConta();
    }//GEN-LAST:event_btnDataActionPerformed

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
            java.util.logging.Logger.getLogger(ContaListaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ContaListaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ContaListaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ContaListaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
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
                ContaListaView dialog = new ContaListaView(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnData;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnFechar;
    private javax.swing.JButton btnNovo;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tblConta;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
