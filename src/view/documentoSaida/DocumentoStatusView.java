/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoSaida;

import java.awt.Color;
import java.time.LocalDateTime;
import javax.swing.JButton;
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
public class DocumentoStatusView extends javax.swing.JDialog {

    private Venda documento;
    private final VendaDAO vendaDAO = new VendaDAO();
    private final MovimentoFisicoDAO mfDAO = new MovimentoFisicoDAO();

    private VendaStatus status;

    private DocumentoStatusView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public DocumentoStatusView(Venda documento) {
        super(MAIN_VIEW, true);
        initComponents();
        JSwing.startComponentsBehavior(this);

        this.documento = documento;

        if (documento.getMovimentosFisicos().isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Este documento não possui itens!", "Atenção", JOptionPane.WARNING_MESSAGE);
            dispose();

        } else {
            status = documento.getVendaStatus();
            colorirBotoes();

            this.setLocationRelativeTo(this);
            this.setVisible(true);
        }
    }

    private void colorirBotoes() {

        btnAguardando.setBackground(Color.WHITE);
        btnAndamento.setBackground(Color.WHITE);
        btnPronto.setBackground(Color.WHITE);
        btnLiberado.setBackground(Color.WHITE);
        btnEntregue.setBackground(Color.WHITE);

        btnAguardando.setForeground(Color.GRAY);
        btnAndamento.setForeground(Color.GRAY);
        btnPronto.setForeground(Color.GRAY);
        btnLiberado.setForeground(Color.GRAY);
        btnEntregue.setForeground(Color.GRAY);

        switch (status) {
            case AGUARDANDO:
                btnAguardando.setBackground(VendaStatus.AGUARDANDO.getCor());
                btnAguardando.setForeground(Color.BLACK);
                break;

            case ANDAMENTO:
                btnAndamento.setBackground(VendaStatus.ANDAMENTO.getCor());
                btnAndamento.setForeground(Color.BLACK);
                break;

            case PREPARAÇÃO_CONCLUÍDA:
                btnPronto.setBackground(VendaStatus.PREPARAÇÃO_CONCLUÍDA.getCor());
                btnPronto.setForeground(Color.BLACK);
                break;

            case LIBERADO:
                btnLiberado.setBackground(VendaStatus.LIBERADO.getCor());
                btnLiberado.setForeground(Color.BLACK);
                break;

            case ENTREGA_CONCLUÍDA:
                btnEntregue.setBackground(VendaStatus.ENTREGA_CONCLUÍDA.getCor());
                btnEntregue.setForeground(Color.BLACK);
                break;

            default:
                btnAguardando.setBackground(VendaStatus.AGUARDANDO.getCor());
                btnAguardando.setForeground(Color.BLACK);
                break;
        }

    }

    private void confirmar() {

        documento.getMovimentosFisicos().forEach((mf) -> {

            LocalDateTime timeStamp = LocalDateTime.now();

            //Definir
            switch (status) {
                case ENTREGA_CONCLUÍDA:
                    if (mf.getDataSaida() == null) {
                        mf.setDataSaida(timeStamp);
                    }
                
                case LIBERADO:
                    if (mf.getDataLiberado()== null) {
                        mf.setDataLiberado(timeStamp);
                    }
                
                case PREPARAÇÃO_CONCLUÍDA:
                    if (mf.getDataPronto()== null) {
                        mf.setDataPronto(timeStamp);
                    }

                case ANDAMENTO:
                    if (mf.getDataAndamento()== null) {
                        mf.setDataAndamento(timeStamp);
                    }

                case AGUARDANDO:

            }
            
            //Limpar
            if (status.getOrdem() < VendaStatus.ENTREGA_CONCLUÍDA.getOrdem()) {
                mf.setDataSaida(null);
            }
            
            if (status.getOrdem() < VendaStatus.LIBERADO.getOrdem()) {
                mf.setDataLiberado(null);
            }
            
            if (status.getOrdem() < VendaStatus.PREPARAÇÃO_CONCLUÍDA.getOrdem()) {
                mf.setDataPronto(null);
            }
            
            if (status.getOrdem() < VendaStatus.ANDAMENTO.getOrdem()) {
                mf.setDataAndamento(null);
            }
            
            

            mfDAO.save(mf);

        });

        vendaDAO.save(documento);

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

        btnCancelar = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();
        btnAndamento = new javax.swing.JButton();
        btnPronto = new javax.swing.JButton();
        btnLiberado = new javax.swing.JButton();
        btnEntregue = new javax.swing.JButton();
        btnAguardando = new javax.swing.JButton();

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
        btnAndamento.setText("ANDAMENTO");
        btnAndamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAndamentoActionPerformed(evt);
            }
        });

        btnPronto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnPronto.setText("PRONTO");
        btnPronto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProntoActionPerformed(evt);
            }
        });

        btnLiberado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnLiberado.setText("LIBERADO");
        btnLiberado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLiberadoActionPerformed(evt);
            }
        });

        btnEntregue.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnEntregue.setText("ENTREGUE");
        btnEntregue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntregueActionPerformed(evt);
            }
        });

        btnAguardando.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnAguardando.setText("AGUARDANDO");
        btnAguardando.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAguardandoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnAguardando, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAndamento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPronto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLiberado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEntregue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(btnAguardando)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAndamento)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPronto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLiberado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEntregue)
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

    private void btnAguardandoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAguardandoActionPerformed
        status = VendaStatus.AGUARDANDO;
        colorirBotoes();
    }//GEN-LAST:event_btnAguardandoActionPerformed

    private void btnAndamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAndamentoActionPerformed
        status = VendaStatus.ANDAMENTO;
        colorirBotoes();
    }//GEN-LAST:event_btnAndamentoActionPerformed

    private void btnProntoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProntoActionPerformed
        status = VendaStatus.PREPARAÇÃO_CONCLUÍDA;
        colorirBotoes();
    }//GEN-LAST:event_btnProntoActionPerformed

    private void btnLiberadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLiberadoActionPerformed
        status = VendaStatus.LIBERADO;
        colorirBotoes();
    }//GEN-LAST:event_btnLiberadoActionPerformed

    private void btnEntregueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntregueActionPerformed
        status = VendaStatus.ENTREGA_CONCLUÍDA;
        colorirBotoes();
    }//GEN-LAST:event_btnEntregueActionPerformed

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
            java.util.logging.Logger.getLogger(DocumentoStatusView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DocumentoStatusView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DocumentoStatusView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DocumentoStatusView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                DocumentoStatusView dialog = new DocumentoStatusView(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnAguardando;
    private javax.swing.JButton btnAndamento;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEntregue;
    private javax.swing.JButton btnLiberado;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnPronto;
    // End of variables declaration//GEN-END:variables
}
