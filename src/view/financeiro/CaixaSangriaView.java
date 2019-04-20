/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.financeiro;

import java.math.BigDecimal;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.financeiro.Caixa;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.principal.financeiro.CaixaItemTipo;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.dao.principal.CaixaDAO;
import model.mysql.dao.principal.CaixaItemDAO;
import util.Decimal;
import util.JSwing;

/**
 *
 * @author ivand
 */
public class CaixaSangriaView extends javax.swing.JDialog {
    CaixaDAO caixaDAO = new CaixaDAO();
    CaixaItemDAO caixaItemDAO = new CaixaItemDAO();
    Caixa caixa;
    
    BigDecimal saldoDinheiro, saldoCheque, saldoCartaoCredito, 
            saldoCartaoDebito, saldoOutros, 
            sangriaCartaoCredito, sangriaCartaoDebito, 
            sangriaDinheiro, sangriaCheque, sangriaOutros;
    /**
     * Creates new form CaixaSangria
     */
    public CaixaSangriaView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        JSwing.startComponentsBehavior(this);
    }
    
    public CaixaSangriaView(java.awt.Frame parent, boolean modal, Caixa caixa) {
        super(parent, modal);
        initComponents();
        JSwing.startComponentsBehavior(this);
        
        this.caixa = caixa;
        
        saldoDinheiro = caixaDAO.getSaldoPorMeioDePagamento(caixa, MeioDePagamento.DINHEIRO);
        saldoCheque = caixaDAO.getSaldoPorMeioDePagamento(caixa, MeioDePagamento.CHEQUE);
        saldoCartaoCredito = caixaDAO.getSaldoPorMeioDePagamento(caixa, MeioDePagamento.CARTAO_DE_CREDITO);
        saldoCartaoDebito = caixaDAO.getSaldoPorMeioDePagamento(caixa, MeioDePagamento.CARTAO_DE_DEBITO);
        saldoOutros = caixaDAO.getSaldoPorMeioDePagamento(caixa, MeioDePagamento.OUTROS);
        
        
        txtSaldoDinheiro.setText(Decimal.toString(saldoDinheiro));
        txtSaldoCheque.setText(Decimal.toString(saldoCheque));
        txtSaldoCartaoCredito.setText(Decimal.toString(saldoCartaoCredito));
        txtSaldoCartaoDebito.setText(Decimal.toString(saldoCartaoDebito));
        txtSaldoOutros.setText(Decimal.toString(saldoOutros));
        
        txtSangriaDinheiro.requestFocus();
    }
    /*
    public Caixa getCaixa(){
        return caixa;
    }*/
    
    private void encerrar(){
        sangriaDinheiro = Decimal.fromString(txtSangriaDinheiro.getText());
        sangriaCheque = Decimal.fromString(txtSangriaCheque.getText());
        sangriaCartaoCredito = Decimal.fromString(txtSangriaCartaoCredito.getText());
        sangriaCartaoDebito = Decimal.fromString(txtSangriaCartaoDebito.getText());
        sangriaCheque = Decimal.fromString(txtSangriaCheque.getText());
        sangriaOutros = Decimal.fromString(txtSangriaOutros.getText());
        
        if(sangriaDinheiro.compareTo(saldoDinheiro) > 0){
            JOptionPane.showMessageDialog(rootPane, "Sangria maior que o saldo", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtSangriaDinheiro.requestFocus();
        }
        else if(sangriaCheque.compareTo(saldoCheque) > 0){
            JOptionPane.showMessageDialog(rootPane, "Sangria maior que o saldo", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtSangriaCheque.requestFocus();
        }
        else if(sangriaCartaoCredito.compareTo(saldoCartaoCredito) > 0){
            JOptionPane.showMessageDialog(rootPane, "Sangria maior que o saldo", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtSangriaCartaoCredito.requestFocus();
        }
        else if(sangriaCartaoDebito.compareTo(saldoCartaoDebito) > 0){
            JOptionPane.showMessageDialog(rootPane, "Sangria maior que o saldo", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtSangriaCartaoDebito.requestFocus();
        }
        else if(sangriaOutros.compareTo(saldoOutros) > 0){
            JOptionPane.showMessageDialog(rootPane, "Sangria maior que o saldo", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtSangriaOutros.requestFocus();
        }
        else{
            String observacao = txtObservacao.getText().trim();
            //refatorado 2019-03-23
            if(sangriaDinheiro.compareTo(BigDecimal.ZERO) > 0){
                CaixaItem caixaItem = new CaixaItem(caixa, CaixaItemTipo.SANGRIA, MeioDePagamento.DINHEIRO, observacao, BigDecimal.ZERO, sangriaDinheiro);
                caixaItem = caixaItemDAO.save(caixaItem);
                caixa.addCaixaItem(caixaItem);
                caixa = caixaDAO.save(caixa);
            }
            if(sangriaCheque.compareTo(BigDecimal.ZERO) > 0){
                CaixaItem caixaItem = new CaixaItem(caixa, CaixaItemTipo.SANGRIA, MeioDePagamento.CHEQUE, observacao, BigDecimal.ZERO, sangriaCheque);
                caixaItem = caixaItemDAO.save(caixaItem);
                caixa.addCaixaItem(caixaItem);
                caixa = caixaDAO.save(caixa);
            }
            if(sangriaCartaoCredito.compareTo(BigDecimal.ZERO) > 0){
                CaixaItem caixaItem = new CaixaItem(caixa, CaixaItemTipo.SANGRIA, MeioDePagamento.CARTAO_DE_CREDITO, observacao, BigDecimal.ZERO, sangriaCartaoCredito);
                caixaItem = caixaItemDAO.save(caixaItem);
                caixa.addCaixaItem(caixaItem);
                caixa = caixaDAO.save(caixa);
            }
            if(sangriaCartaoDebito.compareTo(BigDecimal.ZERO) > 0){
                CaixaItem caixaItem = new CaixaItem(caixa, CaixaItemTipo.SANGRIA, MeioDePagamento.CARTAO_DE_DEBITO, observacao, BigDecimal.ZERO, sangriaCartaoDebito);
                caixaItem = caixaItemDAO.save(caixaItem);
                caixa.addCaixaItem(caixaItem);
                caixa = caixaDAO.save(caixa);
            }
            if(sangriaOutros.compareTo(BigDecimal.ZERO) > 0){
                CaixaItem caixaItem = new CaixaItem(caixa, CaixaItemTipo.SANGRIA, MeioDePagamento.OUTROS, observacao, BigDecimal.ZERO, sangriaOutros);
                caixaItem = caixaItemDAO.save(caixaItem);
                caixa.addCaixaItem(caixaItem);
                caixa = caixaDAO.save(caixa);
            }
            //caixa = caixaDAO.save(caixa);
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

        btnCancelar = new javax.swing.JButton();
        btnConfirmar = new javax.swing.JButton();
        txtSaldoDinheiro = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtSaldoCheque = new javax.swing.JTextField();
        txtSaldoOutros = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtSangriaDinheiro = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtSangriaCheque = new javax.swing.JFormattedTextField();
        txtSangriaOutros = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtObservacao = new javax.swing.JTextField();
        txtSangriaCartaoCredito = new javax.swing.JFormattedTextField();
        txtSaldoCartaoCredito = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtSangriaCartaoDebito = new javax.swing.JFormattedTextField();
        txtSaldoCartaoDebito = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sangria de Caixa");
        setResizable(false);

        btnCancelar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnConfirmar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnConfirmar.setText("Confirmar");
        btnConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarActionPerformed(evt);
            }
        });

        txtSaldoDinheiro.setEditable(false);
        txtSaldoDinheiro.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtSaldoDinheiro.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSaldoDinheiro.setText("0,00");
        txtSaldoDinheiro.setName(""); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Dinheiro");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Cheque");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Outros");

        txtSaldoCheque.setEditable(false);
        txtSaldoCheque.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtSaldoCheque.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSaldoCheque.setText("0,00");
        txtSaldoCheque.setName(""); // NOI18N

        txtSaldoOutros.setEditable(false);
        txtSaldoOutros.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtSaldoOutros.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSaldoOutros.setText("0,00");
        txtSaldoOutros.setName(""); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Saldo");

        txtSangriaDinheiro.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSangriaDinheiro.setText("0,00");
        txtSangriaDinheiro.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtSangriaDinheiro.setName("decimal"); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setForeground(java.awt.Color.red);
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Sangria");

        txtSangriaCheque.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSangriaCheque.setText("0,00");
        txtSangriaCheque.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtSangriaCheque.setName("decimal"); // NOI18N

        txtSangriaOutros.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSangriaOutros.setText("0,00");
        txtSangriaOutros.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtSangriaOutros.setName("decimal"); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Observação");

        txtObservacao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtSangriaCartaoCredito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSangriaCartaoCredito.setText("0,00");
        txtSangriaCartaoCredito.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtSangriaCartaoCredito.setName("decimal"); // NOI18N

        txtSaldoCartaoCredito.setEditable(false);
        txtSaldoCartaoCredito.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtSaldoCartaoCredito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSaldoCartaoCredito.setText("0,00");
        txtSaldoCartaoCredito.setName(""); // NOI18N

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Cartão de Crédito");

        txtSangriaCartaoDebito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSangriaCartaoDebito.setText("0,00");
        txtSangriaCartaoDebito.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtSangriaCartaoDebito.setName("decimal"); // NOI18N

        txtSaldoCartaoDebito.setEditable(false);
        txtSaldoCartaoDebito.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtSaldoCartaoDebito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSaldoCartaoDebito.setText("0,00");
        txtSaldoCartaoDebito.setName(""); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("Cartão de Débito");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtSaldoCheque, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtSaldoDinheiro, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSaldoOutros, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSaldoCartaoCredito, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSaldoCartaoDebito, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtSangriaDinheiro)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtSangriaCheque)
                            .addComponent(txtSangriaOutros, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSangriaCartaoCredito, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSangriaCartaoDebito, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCancelar)
                        .addGap(18, 18, 18)
                        .addComponent(btnConfirmar))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(txtObservacao)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSaldoDinheiro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSangriaDinheiro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSaldoCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSangriaCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSaldoCartaoCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSangriaCartaoCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSaldoCartaoDebito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSangriaCartaoDebito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSaldoOutros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSangriaOutros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtObservacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConfirmar)
                    .addComponent(btnCancelar))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        encerrar();
    }//GEN-LAST:event_btnConfirmarActionPerformed

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
            java.util.logging.Logger.getLogger(CaixaSangriaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CaixaSangriaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CaixaSangriaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CaixaSangriaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CaixaSangriaView dialog = new CaixaSangriaView(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JTextField txtObservacao;
    private javax.swing.JTextField txtSaldoCartaoCredito;
    private javax.swing.JTextField txtSaldoCartaoDebito;
    private javax.swing.JTextField txtSaldoCheque;
    private javax.swing.JTextField txtSaldoDinheiro;
    private javax.swing.JTextField txtSaldoOutros;
    private javax.swing.JFormattedTextField txtSangriaCartaoCredito;
    private javax.swing.JFormattedTextField txtSangriaCartaoDebito;
    private javax.swing.JFormattedTextField txtSangriaCheque;
    private javax.swing.JFormattedTextField txtSangriaDinheiro;
    private javax.swing.JFormattedTextField txtSangriaOutros;
    // End of variables declaration//GEN-END:variables
}
