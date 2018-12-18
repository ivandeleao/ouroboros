/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.venda;

import com.sun.glass.events.KeyEvent;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import model.bean.principal.Pessoa;
import model.bean.principal.Parcela;
import model.bean.principal.Venda;
import model.bean.fiscal.MeioDePagamento;
import model.bean.principal.Caixa;
import model.bean.principal.CaixaItem;
import model.bean.principal.CaixaItemTipo;
import model.dao.principal.PessoaDAO;
import model.dao.principal.ParcelaDAO;
import model.dao.fiscal.MeioDePagamentoDAO;
import model.dao.principal.CaixaDAO;
import model.dao.principal.CaixaItemDAO;
import model.dao.principal.VendaDAO;
import model.jtable.ParcelamentoJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.PARCELA_JUROS_MONETARIO_MENSAL;
import static ouroboros.Ouroboros.PARCELA_JUROS_PERCENTUAL_MENSAL;
import static ouroboros.Ouroboros.PARCELA_MULTA;
import static ouroboros.Ouroboros.em;
import util.Decimal;
import util.JSwing;
import util.jTableFormat.TableRenderer;
import view.cliente.PessoaPesquisaView;

/**
 *
 * @author ivand
 *
 * ESTE FOI CRIADO COMO JDIALOG DESDE O INÍCIO - USAR ESTE COMO REFERÊNCIA PARA
 * OS DEMAIS
 *
 *
 */
public class TransferirComandaView extends javax.swing.JDialog {

    private Venda venda;
    VendaDAO vendaDAO = new VendaDAO();
    ParcelaDAO parcelaDAO = new ParcelaDAO();
    ParcelamentoJTableModel parcelamentoJTableModel = new ParcelamentoJTableModel();
    List<Parcela> parcelasAPrazo = new ArrayList<>();
    Map<Integer, Venda> mapVenda = new HashMap<>();
    int limite = 50; //TO DO: parametrizar

    /**
     * Creates new form ParcelamentoView
     */
    public TransferirComandaView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public TransferirComandaView(java.awt.Frame parent, Venda venda) {
        super(parent, true);
        initComponents();

        this.venda = venda;

        carregarComandas();

        this.setLocationRelativeTo(this);
        this.setVisible(true);
    }


    private void carregarComandas() {
        List<MeioDePagamento> mpList = new MeioDePagamentoDAO().findAllEnabled();
        
        List<Venda> listVenda = vendaDAO.getComandasAbertas();
        
        for (int c = 1; c <= limite; c++) {
            mapVenda.put(c, null);
        }
        
        if(listVenda != null) {
            for(Venda venda : listVenda) {
                mapVenda.replace(venda.getComanda(), venda);
            }
        }
        
        for(Map.Entry<Integer, Venda> entry : mapVenda.entrySet()) {
            Venda venda = entry.getValue();
            if(venda == null) {
                cboComandas.addItem(entry.getKey());
            }
        }
        
    }


    private void confirmar() {
        Integer comanda = (Integer) cboComandas.getSelectedItem();
        System.out.println("comanda: " + comanda);
        venda.setComanda(comanda);
        venda = vendaDAO.save(venda);
        em.refresh(venda);
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

        cboComandas = new javax.swing.JComboBox<>();
        btnCancelar = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Transferir Comanda");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        cboComandas.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cboComandas, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCancelar)
                        .addComponent(btnOk))
                    .addComponent(cboComandas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            java.util.logging.Logger.getLogger(TransferirComandaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TransferirComandaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TransferirComandaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TransferirComandaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                TransferirComandaView dialog = new TransferirComandaView(new javax.swing.JFrame(), true);
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
    private javax.swing.JComboBox<Integer> cboComandas;
    // End of variables declaration//GEN-END:variables
}