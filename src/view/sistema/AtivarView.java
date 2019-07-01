/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.sistema;

import java.math.BigInteger;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import model.mysql.dao.principal.ConstanteDAO;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;
import util.Sistema;

/**
 *
 * @author ivand
 *
 * ESTE FOI CRIADO COMO JDIALOG DESDE O INÍCIO - USAR ESTE COMO REFERÊNCIA PARA
 * OS DEMAIS
 *
 *
 */
public class AtivarView extends javax.swing.JDialog {

    /**
     * Creates new form ParcelamentoView
     */
    public AtivarView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
    }

    public AtivarView() {
        super(MAIN_VIEW, true);
        initComponents();

        this.setLocationRelativeTo(this); //centralizar
        this.setVisible(true);
    }
    
    private void validar() {
        try{
        
            String chaveHex = txtChave.getText().trim();

            String chave = chaveHex.split("-")[0];
            String dvEntrada = chaveHex.split("-")[1];

            String decode = new BigInteger(chave, 16).toString();
            System.out.println(decode);

            if(!dvEntrada.equals(gerarDV(decode))) {
                JOptionPane.showMessageDialog(rootPane, "Chave inválida!", "Erro", JOptionPane.ERROR_MESSAGE);
            } else {
                int ano = Integer.valueOf(decode.substring(0, 4));
                int mes = Integer.valueOf(decode.substring(4, 6));
                int dia = Integer.valueOf(decode.substring(6, 8));
                int sistemaId = Integer.valueOf(decode.substring(8));
                
                //validar id do cliente
                System.out.println("sistemaId: " + sistemaId);
                
                if(Sistema.getId() == null) {
                    JOptionPane.showMessageDialog(rootPane, "Sistema sem id!", "Erro", JOptionPane.ERROR_MESSAGE);
                    
                } else if(Sistema.getId() != sistemaId) {
                    JOptionPane.showMessageDialog(rootPane, "Id inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
                    
                }
                
                LocalDate validade = LocalDate.of(ano, mes, dia);
                
                if(validade.compareTo(LocalDate.now()) < 0) {
                    JOptionPane.showMessageDialog(MAIN_VIEW, "Chave expirada. Data de expiração: " + DateTime.toString(validade), "Chave expirada", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
                
                Sistema.setChave(chaveHex);
                
                JOptionPane.showMessageDialog(MAIN_VIEW, "Sistema validado. Data de expiração: " + DateTime.toString(validade), "Sistema validado", JOptionPane.INFORMATION_MESSAGE);
                
                String msg = "Validade do sistema: " 
                + Sistema.getValidadeEmDias() + " dias"
                + " (" + DateTime.toString(Sistema.getValidade()) + ")";
                
                MAIN_VIEW.setMensagem(msg);
                
                dispose();
            }


        } catch(Exception e) {
            JOptionPane.showMessageDialog(rootPane, "Erro ao validar " + e, "Erro", JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
    
    private String gerarDV(String base) {
        Integer dv = 0;
        for(int n = 0; n < base.length(); n++) {
            String s = base.substring(n, n+1);
            System.out.println("s: " + s);
            dv += Integer.valueOf(s);
            System.out.println("dv: " + dv);
        }
        
        return dv.toString();
    }

    private void liberar() {
        System.out.println("dias validade: " + Sistema.getValidadeEmDias());
        if(!Sistema.checkValidade() || Sistema.getValidadeEmDias() <= -5) {
            System.exit(0);
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

        txtChave = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ativação B3");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        txtChave.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Chave de Ativação");

        btnOk.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnOk.setText("ok");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txtChave, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnOk)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtChave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btnOk))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        validar();
    }//GEN-LAST:event_btnOkActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        liberar();
    }//GEN-LAST:event_formWindowClosing

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
            java.util.logging.Logger.getLogger(AtivarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AtivarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AtivarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AtivarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                AtivarView dialog = new AtivarView(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnOk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField txtChave;
    // End of variables declaration//GEN-END:variables
}
