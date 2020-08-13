/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoSaida;

import java.time.LocalDateTime;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.documento.VendaStatus;
import model.mysql.dao.principal.MovimentoFisicoDAO;
import model.mysql.dao.principal.VendaDAO;
import static ouroboros.Ouroboros.MAIN_VIEW;
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
public class DocumentoStatusViewOld extends javax.swing.JDialog {

    private Venda documento;
    private final VendaDAO vendaDAO = new VendaDAO();
    private final MovimentoFisicoDAO mfDAO = new MovimentoFisicoDAO();

    private DocumentoStatusViewOld(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public DocumentoStatusViewOld(Venda documento) {
        super(MAIN_VIEW, true);
        initComponents();
        JSwing.startComponentsBehavior(this);

        this.documento = documento;

        if (documento.getMovimentosFisicos().isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Este documento não possui itens!", "Atenção", JOptionPane.WARNING_MESSAGE);
            dispose();

        } else {
            carregarDados();

            this.setLocationRelativeTo(this);
            this.setVisible(true);
        }
    }

    private void carregarDados() {

        txtAndamento.setBackground(VendaStatus.ANDAMENTO.getCor());

        txtPronto.setBackground(VendaStatus.PREPARAÇÃO_CONCLUÍDA.getCor());

        txtEntregue.setBackground(VendaStatus.ENTREGA_CONCLUÍDA.getCor());

        switch (documento.getVendaStatus()) {
            case ANDAMENTO:
                btnAndamento.setEnabled(false);
                btnPronto.setEnabled(true);
                btnEntregue.setEnabled(true);

                btnAndamentoLimpar.setEnabled(true);
                btnProntoLimpar.setEnabled(false);
                btnEntregueLimpar.setEnabled(false);
                break;

            case PREPARAÇÃO_CONCLUÍDA:
                btnAndamento.setEnabled(false);
                btnPronto.setEnabled(false);
                btnEntregue.setEnabled(true);

                btnAndamentoLimpar.setEnabled(false);
                btnProntoLimpar.setEnabled(true);
                btnEntregueLimpar.setEnabled(false);
                break;

            case ENTREGA_CONCLUÍDA:
                btnAndamento.setEnabled(false);
                btnPronto.setEnabled(false);
                btnEntregue.setEnabled(false);

                btnAndamentoLimpar.setEnabled(false);
                btnProntoLimpar.setEnabled(false);
                btnEntregueLimpar.setEnabled(true);
                break;

            default:
                btnAndamento.setEnabled(true);
                btnPronto.setEnabled(true);
                btnEntregue.setEnabled(true);

                btnAndamentoLimpar.setEnabled(false);
                btnProntoLimpar.setEnabled(false);
                btnEntregueLimpar.setEnabled(false);
                break;
        }

    }

    private void confirmar() {

        documento.getMovimentosFisicos().forEach((mf) -> {

            LocalDateTime timeStamp = LocalDateTime.now();

            //Entregue
            if (btnEntregue.isEnabled() && !btnEntregueLimpar.isEnabled()) {
                mf.setDataSaida(null);

            } else {
                if (mf.getDataSaida() == null) {
                    mf.setDataSaida(timeStamp);
                }

            }

            //Pronto
            if (btnPronto.isEnabled() && !btnProntoLimpar.isEnabled()) {
                mf.setDataPronto(null);

            } else {
                if (mf.getDataPronto() == null) {
                    mf.setDataPronto(timeStamp);
                }

            }

            //Andamento
            if (btnAndamento.isEnabled() && !btnAndamentoLimpar.isEnabled()) {
                mf.setDataAndamento(null);

            } else {
                if (mf.getDataAndamento() == null) {
                    mf.setDataAndamento(timeStamp);
                }

            }
            
            mfDAO.save(mf);

        });
        
        vendaDAO.save(documento);

        dispose();
    }

    private void marcarAndamento() {
        btnAndamento.setEnabled(false);
        btnAndamentoLimpar.setEnabled(true);
    }

    private void marcarPronto() {
        btnPronto.setEnabled(false);
        btnProntoLimpar.setEnabled(true);
        btnAndamento.setEnabled(false);
        btnAndamentoLimpar.setEnabled(false);
    }

    private void marcarEntregue() {
        btnEntregue.setEnabled(false);
        btnEntregueLimpar.setEnabled(true);
        btnPronto.setEnabled(false);
        btnProntoLimpar.setEnabled(false);
        btnAndamento.setEnabled(false);
        btnAndamentoLimpar.setEnabled(false);
    }

    private void limparAndamento() {
        documento.getMovimentosFisicos().forEach((mf) -> {
            mf.setDataAndamento(null);
        });

        btnAndamento.setEnabled(true);
        btnAndamentoLimpar.setEnabled(false);
    }

    private void limparPronto() {
        documento.getMovimentosFisicos().forEach((mf) -> {
            mf.setDataPronto(null);
        });

        btnPronto.setEnabled(true);
        btnProntoLimpar.setEnabled(false);
        btnAndamentoLimpar.setEnabled(true);
    }

    private void limparEntregue() {
        btnEntregue.setEnabled(true);
        btnEntregueLimpar.setEnabled(false);
        btnProntoLimpar.setEnabled(true);
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
        btnOk = new javax.swing.JButton();
        btnAndamento = new javax.swing.JButton();
        txtAndamento = new javax.swing.JTextField();
        txtPronto = new javax.swing.JTextField();
        btnPronto = new javax.swing.JButton();
        btnEntregue = new javax.swing.JButton();
        txtEntregue = new javax.swing.JTextField();
        btnAndamentoLimpar = new javax.swing.JButton();
        btnProntoLimpar = new javax.swing.JButton();
        btnEntregueLimpar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Status");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        btnCancelar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnOk.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnOk.setText("Ok");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        btnAndamento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnAndamento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-checkmark-20.png"))); // NOI18N
        btnAndamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAndamentoActionPerformed(evt);
            }
        });

        txtAndamento.setEditable(false);
        txtAndamento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAndamento.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtAndamento.setText("ANDAMENTO");
        txtAndamento.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        txtPronto.setEditable(false);
        txtPronto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPronto.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPronto.setText("PRONTO");
        txtPronto.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        btnPronto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnPronto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-checkmark-20.png"))); // NOI18N
        btnPronto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProntoActionPerformed(evt);
            }
        });

        btnEntregue.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnEntregue.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-checkmark-20.png"))); // NOI18N
        btnEntregue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntregueActionPerformed(evt);
            }
        });

        txtEntregue.setEditable(false);
        txtEntregue.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtEntregue.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtEntregue.setText("ENTREGUE");
        txtEntregue.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        btnAndamentoLimpar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnAndamentoLimpar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-delete-forever-20.png"))); // NOI18N
        btnAndamentoLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAndamentoLimparActionPerformed(evt);
            }
        });

        btnProntoLimpar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnProntoLimpar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-delete-forever-20.png"))); // NOI18N
        btnProntoLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProntoLimparActionPerformed(evt);
            }
        });

        btnEntregueLimpar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnEntregueLimpar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-delete-forever-20.png"))); // NOI18N
        btnEntregueLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntregueLimparActionPerformed(evt);
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPronto, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                            .addComponent(txtEntregue))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnPronto)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnProntoLimpar))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnEntregue)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEntregueLimpar))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtAndamento, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAndamento)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAndamentoLimpar))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnAndamento)
                    .addComponent(txtAndamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAndamentoLimpar))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnPronto)
                    .addComponent(txtPronto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnProntoLimpar))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEntregue)
                    .addComponent(txtEntregue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEntregueLimpar))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk)
                    .addComponent(btnCancelar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    }//GEN-LAST:event_formWindowClosing

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        confirmar();
    }//GEN-LAST:event_btnOkActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnAndamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAndamentoActionPerformed
        marcarAndamento();
    }//GEN-LAST:event_btnAndamentoActionPerformed

    private void btnProntoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProntoActionPerformed
        marcarPronto();
    }//GEN-LAST:event_btnProntoActionPerformed

    private void btnEntregueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntregueActionPerformed
        marcarEntregue();
    }//GEN-LAST:event_btnEntregueActionPerformed

    private void btnAndamentoLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAndamentoLimparActionPerformed
        limparAndamento();
    }//GEN-LAST:event_btnAndamentoLimparActionPerformed

    private void btnProntoLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProntoLimparActionPerformed
        limparPronto();
    }//GEN-LAST:event_btnProntoLimparActionPerformed

    private void btnEntregueLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntregueLimparActionPerformed
        limparEntregue();
    }//GEN-LAST:event_btnEntregueLimparActionPerformed

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
            java.util.logging.Logger.getLogger(DocumentoStatusViewOld.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DocumentoStatusViewOld.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DocumentoStatusViewOld.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DocumentoStatusViewOld.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DocumentoStatusViewOld dialog = new DocumentoStatusViewOld(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnAndamento;
    private javax.swing.JButton btnAndamentoLimpar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEntregue;
    private javax.swing.JButton btnEntregueLimpar;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnPronto;
    private javax.swing.JButton btnProntoLimpar;
    private javax.swing.JTextField txtAndamento;
    private javax.swing.JTextField txtEntregue;
    private javax.swing.JTextField txtPronto;
    // End of variables declaration//GEN-END:variables
}
