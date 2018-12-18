/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.produto.item;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import model.bean.principal.MovimentoFisico;
import model.bean.principal.MovimentoFisicoStatus;
import model.bean.principal.MovimentoFisicoTipo;
import model.dao.principal.MovimentoFisicoDAO;
import model.jtable.EstoqueJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.em;
import util.DateTime;
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
public class ConfirmarEntregaDevolucaoView extends javax.swing.JDialog {

    EstoqueJTableModel estoqueJTableModel = new EstoqueJTableModel();
    MovimentoFisicoDAO movimentoFisicoDAO = new MovimentoFisicoDAO();
    List<MovimentoFisico> MovimentosFisicos = new ArrayList();

    private ConfirmarEntregaDevolucaoView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public ConfirmarEntregaDevolucaoView(List<MovimentoFisico> MovimentosFisicos) {
        super(MAIN_VIEW, true);
        initComponents();
        JSwing.startComponentsBehavior(this);

        this.MovimentosFisicos = MovimentosFisicos;

        formatarTabela();
        carregarTabela();
        carregarDados();
        if (!isValido()) {
            dispose();
        } else {
            this.setLocationRelativeTo(this);
            this.setVisible(true);
        }
    }

    private boolean isValido() {
        MovimentoFisicoTipo tipo = null;
        for (MovimentoFisico movimentoFisico : MovimentosFisicos) {
            if (!movimentoFisico.getMovimentoFisicoTipo().equals(MovimentoFisicoTipo.ALUGUEL)
                    && !movimentoFisico.getMovimentoFisicoTipo().equals(MovimentoFisicoTipo.DEVOLUCAO_ALUGUEL)) {
                JOptionPane.showMessageDialog(rootPane, "Itens de tipo inválido. \n Aceitos: Aluguel e Devolução de Aluguel", "Atenção", JOptionPane.WARNING_MESSAGE);
                return false;
            } else if (!movimentoFisico.getStatus().equals(MovimentoFisicoStatus.PREVISTO)
                    && !movimentoFisico.getStatus().equals(MovimentoFisicoStatus.ATRASADO)) {
                JOptionPane.showMessageDialog(rootPane, "Só é possível confirmar itens de status: Previsto ou Atrasado", "Atenção", JOptionPane.WARNING_MESSAGE);
                return false;
            } else if (tipo != null && !tipo.equals(movimentoFisico.getMovimentoFisicoTipo())) {
                JOptionPane.showMessageDialog(rootPane, "Itens de tipos diferentes.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return false;
            } else {
                tipo = movimentoFisico.getMovimentoFisicoTipo();
            }
        }
        return true;
    }

    private void carregarDados() {
        BigDecimal quantidadeEntrada = MovimentosFisicos.stream().map(MovimentoFisico::getEntrada).reduce(BigDecimal::add).get();
        BigDecimal quantidadeSaida = MovimentosFisicos.stream().map(MovimentoFisico::getSaida).reduce(BigDecimal::add).get();
        
        if(quantidadeEntrada.compareTo(quantidadeSaida) > 0) {
            txtQuantidade.setText(Decimal.toString(quantidadeEntrada, 3));
        } else {
            txtQuantidade.setText(Decimal.toString(quantidadeSaida, 3));
        }
        
        txtData.setText(DateTime.toStringDate(DateTime.getNow()));
    }

    private void formatarTabela() {
        tblMovimentoFisico.setModel(estoqueJTableModel);

        tblMovimentoFisico.setRowHeight(24);
        tblMovimentoFisico.setIntercellSpacing(new Dimension(10, 10));
        //id
        tblMovimentoFisico.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblMovimentoFisico.getColumnModel().getColumn(0).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //data
        tblMovimentoFisico.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblMovimentoFisico.getColumnModel().getColumn(1).setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        //tipo
        tblMovimentoFisico.getColumnModel().getColumn(2).setPreferredWidth(200);
        //observação
        tblMovimentoFisico.getColumnModel().getColumn(3).setPreferredWidth(400);
        //entrada
        tblMovimentoFisico.getColumnModel().getColumn(4).setPreferredWidth(120);
        tblMovimentoFisico.getColumnModel().getColumn(4).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //saída
        tblMovimentoFisico.getColumnModel().getColumn(5).setPreferredWidth(120);
        tblMovimentoFisico.getColumnModel().getColumn(5).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //saldo
        tblMovimentoFisico.getColumnModel().getColumn(6).setPreferredWidth(120);
        tblMovimentoFisico.getColumnModel().getColumn(6).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
    }

    private void carregarTabela() {
        estoqueJTableModel.clear();
        estoqueJTableModel.addList(MovimentosFisicos);

    }

    private void confirmar() {
        /*
        Registrar data de entrega (saída do estoque) 
        ou data de devolução (entrada no estoque)
         */
        LocalDateTime data = DateTime.fromStringLDT(txtData.getText());

        System.out.println("data: " + data);

        for (MovimentoFisico movimentoFisico : MovimentosFisicos) {
            if(movimentoFisico.getMovimentoFisicoTipo().equals(MovimentoFisicoTipo.ALUGUEL)) {
                movimentoFisico.setDataSaida(data);
            } else {
                movimentoFisico.setDataEntrada(data);
            }
            movimentoFisicoDAO.save(movimentoFisico);
            em.refresh(movimentoFisico);
        }

        //venda = vendaDAO.save(venda);
        //em.refresh(venda);
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
        txtData = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMovimentoFisico = new javax.swing.JTable();
        txtQuantidade = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Confirmação de Entrega e Devolução");
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

        txtData.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtData.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtData.setName("data"); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Data");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Quantidade");

        tblMovimentoFisico.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblMovimentoFisico);

        txtQuantidade.setEditable(false);
        txtQuantidade.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtQuantidade.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 980, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
                .addGap(55, 55, 55)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnOk)
                            .addComponent(btnCancelar))))
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleName("Confirmação de Entrega ou Devolução");

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
            java.util.logging.Logger.getLogger(ConfirmarEntregaDevolucaoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConfirmarEntregaDevolucaoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConfirmarEntregaDevolucaoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConfirmarEntregaDevolucaoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                ConfirmarEntregaDevolucaoView dialog = new ConfirmarEntregaDevolucaoView(new javax.swing.JFrame(), true);
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblMovimentoFisico;
    private javax.swing.JFormattedTextField txtData;
    private javax.swing.JTextField txtQuantidade;
    // End of variables declaration//GEN-END:variables
}