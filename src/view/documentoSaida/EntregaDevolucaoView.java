/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoSaida;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.MovimentoFisicoTipo;
import model.mysql.dao.principal.ParcelaDAO;
import model.mysql.dao.principal.MovimentoFisicoDAO;
import model.mysql.dao.principal.VendaDAO;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;
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
public class EntregaDevolucaoView extends javax.swing.JDialog {

    private Venda venda;
    VendaDAO vendaDAO = new VendaDAO();
    ParcelaDAO parcelaDAO = new ParcelaDAO();
    List<Parcela> parcelasAPrazo = new ArrayList<>();
    Map<Integer, Venda> mapVenda = new HashMap<>();
    int limite = 50; //TO DO: parametrizar

    /**
     * Creates new form ParcelamentoView
     */
    private EntregaDevolucaoView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public EntregaDevolucaoView(Venda venda) {
        super(MAIN_VIEW, true);
        initComponents();
        JSwing.startComponentsBehavior(this);

        this.venda = venda;

        
        
        
        if(venda.getMovimentosFisicosSaida().isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem itens para agendar", "Atenção", JOptionPane.WARNING_MESSAGE);
            dispose();
        } else {
            this.setLocationRelativeTo(this);
            this.setVisible(true);
        }
    }




    private void confirmar() {
        /*
        Registrar data de entrega (saída do estoque) 
        e criar (ou atualizar) data de devolução (entrada no estoque)
        */
        MovimentoFisicoDAO movimentoFisicoDAO = new MovimentoFisicoDAO();
        LocalDateTime entrega = DateTime.fromStringLDT(txtDataEntregaPrevista.getText());
        LocalDateTime dataDevolucao = DateTime.fromStringLDT(txtDataDevolucaoPrevista.getText());
        
        System.out.println("entrega: " + entrega);
        
        for(MovimentoFisico movimentoFisico : venda.getMovimentosFisicosSaida()) {
            System.out.println("movimentoFisico da venda - id: " + movimentoFisico.getId());
            //venda.removeMovimentoFisico(movimentoFisico);

            //Marcar datas de saída do estoque
            movimentoFisico.setMovimentoFisicoTipo(MovimentoFisicoTipo.ALUGUEL);
            movimentoFisico.setDataSaidaPrevista(entrega);
            
            //venda.addMovimentoFisico(movimentoFisico);
            
            //Gerar devolução
            
            if(dataDevolucao != null) {
                movimentoFisicoDAO.gerarDevolucaoPrevista(movimentoFisico, dataDevolucao);
                //venda.addMovimentoFisico(movimentoFisico);
            }
            //2019-02-18 Removido pois salva pelo pai(venda) ???
            movimentoFisico = movimentoFisicoDAO.save(movimentoFisico);
            venda.addMovimentoFisico(movimentoFisico);
        }
        
        venda = vendaDAO.save(venda);
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
        txtDataEntregaPrevista = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        txtDataDevolucaoPrevista = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Agendamento de Entrega e Devolução");
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

        txtDataEntregaPrevista.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDataEntregaPrevista.setText("28/10/2018");
        txtDataEntregaPrevista.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDataEntregaPrevista.setName("data"); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Entrega");

        txtDataDevolucaoPrevista.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDataDevolucaoPrevista.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDataDevolucaoPrevista.setName("data"); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Devolução");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(178, Short.MAX_VALUE)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDataDevolucaoPrevista)
                    .addComponent(txtDataEntregaPrevista, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDataEntregaPrevista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDataDevolucaoPrevista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk)
                    .addComponent(btnCancelar))
                .addContainerGap())
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
            java.util.logging.Logger.getLogger(EntregaDevolucaoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EntregaDevolucaoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EntregaDevolucaoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EntregaDevolucaoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                EntregaDevolucaoView dialog = new EntregaDevolucaoView(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnOk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JFormattedTextField txtDataDevolucaoPrevista;
    private javax.swing.JFormattedTextField txtDataEntregaPrevista;
    // End of variables declaration//GEN-END:variables
}
