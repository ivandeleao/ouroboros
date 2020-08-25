/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.funcionario;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import model.jtable.funcionario.FuncionarioComissaoPagamentoHistoricoTableModel;
import model.mysql.bean.principal.ComissaoPagamento;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.dao.principal.VendaDAO;
import model.mysql.dao.principal.financeiro.CaixaItemDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.Decimal;
import util.JSwing;

/**
 *
 * @author ivand
 *
 * ESTE FOI CRIADO COMO JDIALOG DESDE O INÍCIO - USAR ESTE COMO REFERÊNCIA PARA
 * OS DEMAIS
 *
 *
 */
public class FuncionarioPagamentoComissaoHistoricoView extends javax.swing.JDialog {

    private Venda documento;
    private final CaixaItemDAO caixaItemDAO = new CaixaItemDAO();
    FuncionarioComissaoPagamentoHistoricoTableModel funcionarioComissaoPagamentoHistoricoTableModel = new FuncionarioComissaoPagamentoHistoricoTableModel();

    private FuncionarioPagamentoComissaoHistoricoView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public FuncionarioPagamentoComissaoHistoricoView(Venda documento) {
        super(MAIN_VIEW, true);
        initComponents();
        JSwing.startComponentsBehavior(this);

        this.documento = documento;

        definirAtalhos();
        
        carregarDados();
        
        formatarTabela();
        carregarTabela();

        this.setLocationRelativeTo(this);
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
        txtDocumento.setText(documento.getId().toString());
        txtTotalComissao.setText(Decimal.toString(documento.getTotalComissaoDocumento()));
    }
    
    private void formatarTabela() {
        tblLancamentos.setModel(funcionarioComissaoPagamentoHistoricoTableModel);

        tblLancamentos.setRowHeight(30);
        tblLancamentos.setIntercellSpacing(new Dimension(10, 10));

        tblLancamentos.getColumn("Id").setPreferredWidth(100);
        tblLancamentos.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblLancamentos.getColumn("Data Hora").setPreferredWidth(160);
        tblLancamentos.getColumn("Data Hora").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblLancamentos.getColumn("Conta/Caixa").setPreferredWidth(160);
        tblLancamentos.getColumn("Conta/Caixa").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblLancamentos.getColumn("MP").setPreferredWidth(100);
        tblLancamentos.getColumn("MP").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblLancamentos.getColumn("Lançamento").setPreferredWidth(100);
        tblLancamentos.getColumn("Lançamento").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblLancamentos.getColumn("Comissão").setPreferredWidth(100);
        tblLancamentos.getColumn("Comissão").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

    }

    private void carregarTabela() {
        funcionarioComissaoPagamentoHistoricoTableModel.clear();
        funcionarioComissaoPagamentoHistoricoTableModel.addList(documento.getComissaoPagamentos());
        /*funcionarioComissaoPagamentoHistoricoTableModel.addList(documento.getComissaoPagamentos().stream().map((cp) -> {
            return cp.getCaixaItem();
        }).collect(Collectors.toList()));*/
    }
    
    private void estornar() {
        int rowIndex = tblLancamentos.getSelectedRow();
        if (rowIndex < 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione um registro", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {
            CaixaItem itemEstornar = ((ComissaoPagamento) funcionarioComissaoPagamentoHistoricoTableModel.getRow(rowIndex)).getCaixaItem();
            
            if (itemEstornar.getEstorno() != null) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Este item já foi estornado.", "Atenção", JOptionPane.WARNING_MESSAGE);

            } else {
                int resposta = JOptionPane.showConfirmDialog(MAIN_VIEW, "Estornar o item selecionado?", "Atenção", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (resposta == JOptionPane.OK_OPTION) {
                    CaixaItem estorno = caixaItemDAO.estornar(itemEstornar);
                    
                    //caixaItemDAO.testReflection(itemEstornar);
                    
                    
                    funcionarioComissaoPagamentoHistoricoTableModel.refreshRows(new int[]{rowIndex});
                    //carregarTabela(); // 2020-06-03 - substituído por etapas mais rápidas:
                    /*if (estorno != null) {
                        caixaJTableModel.updateRow(itemEstornar, caixaItemDAO.findById(itemEstornar.getId()));
                        caixaItens.add(estorno);
                        caixaJTableModel.addRow(estorno);
                        posicionarTabela();
                        exibirTotais();
                    }*/
                }
            }
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

        btnCancelar = new javax.swing.JButton();
        txtDocumento = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtTotalComissao = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLancamentos = new javax.swing.JTable();
        jLabel37 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnEstornar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Histórico de Lançamentos");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        btnCancelar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnCancelar.setText("Fechar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        txtDocumento.setEditable(false);
        txtDocumento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDocumento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDocumento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDocumentoActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Documento");

        txtTotalComissao.setEditable(false);
        txtTotalComissao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalComissao.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalComissao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalComissaoActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("Total Comissão");

        tblLancamentos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblLancamentos.setModel(new javax.swing.table.DefaultTableModel(
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
        tblLancamentos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblLancamentosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblLancamentos);

        jLabel37.setBackground(new java.awt.Color(122, 138, 153));
        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel37.setForeground(java.awt.Color.white);
        jLabel37.setText("Lançamentos");
        jLabel37.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel37.setOpaque(true);

        jLabel1.setText("*Lançamento é a soma das comissões registrada em Conta/Caixa");

        jLabel2.setText("*Comissão é o valor individual");

        btnEstornar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnEstornar.setText("Estornar");
        btnEstornar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEstornarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(txtDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(txtTotalComissao, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 183, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnEstornar)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalComissao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addComponent(jLabel37)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCancelar)
                        .addComponent(btnEstornar))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    }//GEN-LAST:event_formWindowClosing

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void txtDocumentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDocumentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDocumentoActionPerformed

    private void txtTotalComissaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalComissaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalComissaoActionPerformed

    private void tblLancamentosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLancamentosMouseClicked
    }//GEN-LAST:event_tblLancamentosMouseClicked

    private void btnEstornarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEstornarActionPerformed
        estornar();
    }//GEN-LAST:event_btnEstornarActionPerformed

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
            java.util.logging.Logger.getLogger(FuncionarioPagamentoComissaoHistoricoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FuncionarioPagamentoComissaoHistoricoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FuncionarioPagamentoComissaoHistoricoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FuncionarioPagamentoComissaoHistoricoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                FuncionarioPagamentoComissaoHistoricoView dialog = new FuncionarioPagamentoComissaoHistoricoView(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEstornar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblLancamentos;
    private javax.swing.JTextField txtDocumento;
    private javax.swing.JTextField txtTotalComissao;
    // End of variables declaration//GEN-END:variables
}
