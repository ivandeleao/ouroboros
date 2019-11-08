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
import java.util.List;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.dao.principal.MovimentoFisicoDAO;
import model.jtable.catalogo.EstoqueGeralJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;
import util.Decimal;
import util.JSwing;
import util.jTableFormat.EstoqueRenderer;

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

    EstoqueGeralJTableModel estoqueGeralJTableModel = new EstoqueGeralJTableModel();
    MovimentoFisicoDAO movimentoFisicoDAO = new MovimentoFisicoDAO();
    List<MovimentoFisico> movimentosFisicos = new ArrayList();

    private ConfirmarEntregaDevolucaoView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public ConfirmarEntregaDevolucaoView(List<MovimentoFisico> MovimentosFisicos) {
        super(MAIN_VIEW, true);
        initComponents();
        JSwing.startComponentsBehavior(this);

        this.movimentosFisicos = MovimentosFisicos;

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
        //refatorar se vai usar direto no estoque, ou apenas através dos docs
        /*
        MovimentoFisicoTipo tipo = null;
        
        for (MovimentoFisico movimentoFisico : movimentosFisicos) {
            if (!movimentoFisico.getMovimentoFisicoTipo().equals(MovimentoFisicoTipo.ALUGUEL)
                    && !movimentoFisico.getMovimentoFisicoTipo().equals(MovimentoFisicoTipo.DEVOLUCAO_ALUGUEL)) {
                JOptionPane.showMessageDialog(rootPane, "Itens de tipo inválido. \n Aceitos: Aluguel e Devolução de Aluguel", "Atenção", JOptionPane.WARNING_MESSAGE);
                return false;
            } else if (!movimentoFisico.getStatus().equals(MovimentoFisicoStatus.ENTREGA_PREVISTA)
                    && !movimentoFisico.getStatus().equals(MovimentoFisicoStatus.ENTREGA_ATRASADA)) {
                JOptionPane.showMessageDialog(rootPane, "Só é possível confirmar itens de status: Previsto ou Atrasado", "Atenção", JOptionPane.WARNING_MESSAGE);
                return false;
            } else if (tipo != null && !tipo.equals(movimentoFisico.getMovimentoFisicoTipo())) {
                JOptionPane.showMessageDialog(rootPane, "Itens de tipos diferentes.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return false;
            } else {
                tipo = movimentoFisico.getMovimentoFisicoTipo();
            }
        }*/
        return true;
    }

    private void carregarDados() {
        BigDecimal quantidadeEntrada = movimentosFisicos.stream().map(MovimentoFisico::getEntrada).reduce(BigDecimal::add).get();
        BigDecimal quantidadeSaida = movimentosFisicos.stream().map(MovimentoFisico::getSaida).reduce(BigDecimal::add).get();
        
        if(quantidadeEntrada.compareTo(quantidadeSaida) > 0) {
            txtQuantidade.setText(Decimal.toString(quantidadeEntrada, 3));
        } else {
            txtQuantidade.setText(Decimal.toString(quantidadeSaida, 3));
        }
        
        txtData.setText(DateTime.toStringDate(DateTime.getNow()));
    }

    private void formatarTabela() {
        tblMovimentoFisico.setModel(estoqueGeralJTableModel);

        tblMovimentoFisico.setRowHeight(24);
        tblMovimentoFisico.setIntercellSpacing(new Dimension(10, 10));
        
        tblMovimentoFisico.getColumn("Id").setPreferredWidth(60);
        tblMovimentoFisico.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblMovimentoFisico.getColumn("Status").setPreferredWidth(220);
        tblMovimentoFisico.getColumn("Status").setCellRenderer(new EstoqueRenderer());
        
        tblMovimentoFisico.getColumn("Data").setPreferredWidth(180);
        tblMovimentoFisico.getColumn("Data").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblMovimentoFisico.getColumn("Origem").setPreferredWidth(200);
        
        tblMovimentoFisico.getColumn("Produto").setPreferredWidth(400);
        
        tblMovimentoFisico.getColumn("Observação").setPreferredWidth(180);
        
        tblMovimentoFisico.getColumn("Entrada").setPreferredWidth(100);
        tblMovimentoFisico.getColumn("Entrada").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblMovimentoFisico.getColumn("Saída").setPreferredWidth(100);
        tblMovimentoFisico.getColumn("Saída").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        
    }

    private void carregarTabela() {
        estoqueGeralJTableModel.clear();
        estoqueGeralJTableModel.addList(movimentosFisicos);

    }

    private void confirmar() {
        /*
        Registrar data de entrega (saída do estoque) 
        ou data de devolução (entrada no estoque)
         */
        LocalDateTime data = DateTime.fromStringLDT(txtData.getText());

        System.out.println("data: " + data);

        for (MovimentoFisico movimentoFisico : movimentosFisicos) {
            if(movimentoFisico.getDataSaidaPrevista() != null) {
                movimentoFisico.setDataSaida(data);
            } else {
                movimentoFisico.setDataEntrada(data);
            }
            movimentoFisicoDAO.save(movimentoFisico);
            //em.refresh(movimentoFisico);
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
