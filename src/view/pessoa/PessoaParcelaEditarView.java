/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.pessoa;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.JOptionPane;
import model.bean.principal.Parcela;
import model.bean.fiscal.MeioDePagamento;
import model.bean.principal.ParcelaStatus;
import model.dao.principal.ParcelaDAO;
import model.dao.fiscal.MeioDePagamentoDAO;
import model.jtable.RecebimentoListaJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.PARCELA_JUROS_MONETARIO_MENSAL;
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
public class PessoaParcelaEditarView extends javax.swing.JDialog {

    private Parcela parcela;
    ParcelaDAO parcelaDAO = new ParcelaDAO();
    RecebimentoListaJTableModel recebimentoListaJTableModel = new RecebimentoListaJTableModel();

    /**
     * Creates new form ParcelamentoView
     */
    public PessoaParcelaEditarView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public PessoaParcelaEditarView(java.awt.Frame parent, Parcela parcela) {
        super(parent, true);
        initComponents();
        JSwing.startComponentsBehavior(this);

        this.parcela = parcela;
        
        if(parcela.getStatus() == ParcelaStatus.QUITADO) {
            JSwing.setComponentesHabilitados(pnlDados, false);
        }
        

        formatarTabela();
        
        carregarDados();

        txtVencimento.requestFocus();

        this.setLocationRelativeTo(this); //centralizar
        this.setVisible(true);
    }

    private void carregarDados() {
        txtVenda.setText(parcela.getVenda().getId().toString());
        txtNumero.setText(parcela.getNumeroDeTotal());
        txtValor.setText(Decimal.toString(parcela.getValor()));

        txtVencimento.setText(DateTime.toStringDate(parcela.getVencimento()));
        txtMulta.setText(Decimal.toString(parcela.getMulta()));
        txtJuros.setText(Decimal.toString(parcela.getJuros()));

        carregarMeioDePagamento();
        
        carregarTabela();
    }

    private void carregarMeioDePagamento() {
        List<MeioDePagamento> mpList = new MeioDePagamentoDAO().findAllEnabled();

        cboMeioDePagamento.addItem(MeioDePagamento.CREDITO_LOJA); //garantir que haja este meio de pagamento
        for (MeioDePagamento mp : mpList) {
            cboMeioDePagamento.addItem(mp);
        }
        cboMeioDePagamento.setSelectedItem(parcela.getMeioDePagamento());
    }
    
    private void formatarTabela() {
        //Formatar tabela
        tblRecebimentos.setModel(recebimentoListaJTableModel);
        
        tblRecebimentos.setRowHeight(24);
        tblRecebimentos.setIntercellSpacing(new Dimension(10, 10));
        //id
        tblRecebimentos.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblRecebimentos.getColumnModel().getColumn(0).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //data hora
        tblRecebimentos.getColumnModel().getColumn(1).setPreferredWidth(400);
        tblRecebimentos.getColumnModel().getColumn(1).setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        //tipo
        tblRecebimentos.getColumnModel().getColumn(2).setPreferredWidth(400);
        //observacao
        tblRecebimentos.getColumnModel().getColumn(3).setPreferredWidth(400);
        
        //meio de pagamento
        tblRecebimentos.getColumnModel().getColumn(4).setPreferredWidth(200);
        
        //crédito
        tblRecebimentos.getColumnModel().getColumn(5).setPreferredWidth(120);
        tblRecebimentos.getColumnModel().getColumn(5).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //débito
        tblRecebimentos.getColumnModel().getColumn(6).setPreferredWidth(120);
        tblRecebimentos.getColumnModel().getColumn(6).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
    }
    
    private void carregarTabela(){
        recebimentoListaJTableModel.addList(parcela.getRecebimentos());
        
        tblRecebimentos.requestFocus();
        //tblRecebimentos.setRowSelectionInterval(tblRecebimentos.getRowCount() - 1, tblRecebimentos.getRowCount() -1 );
    }

    private boolean validar() {
        boolean valido = true;
        java.sql.Date vencimento = DateTime.toSqlDate(txtVencimento.getText());
        BigDecimal multa = Decimal.fromString(txtMulta.getText());
        BigDecimal juros = Decimal.fromString(txtJuros.getText());

        if (vencimento == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Data inválida.", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtVencimento.requestFocus();
            valido = false;
        }

        return valido;
    }

    private void salvar() {
        MeioDePagamento mp = (MeioDePagamento) cboMeioDePagamento.getSelectedItem();
        java.sql.Date vencimento = DateTime.toSqlDate(txtVencimento.getText());
        BigDecimal multa = Decimal.fromString(txtMulta.getText());
        BigDecimal juros = Decimal.fromString(txtJuros.getText());
        
        
        parcela.setMeioDePagamento(mp);
        parcela.setVencimento(vencimento);
        parcela.setMulta(multa);
        
        if (PARCELA_JUROS_MONETARIO_MENSAL.compareTo(BigDecimal.ZERO) > 0) {
            parcela.setJurosMonetario(juros);
            parcela.setJurosPercentual(BigDecimal.ZERO);
        } else {
            parcela.setJurosMonetario(BigDecimal.ZERO);
            parcela.setJurosPercentual(juros);
        }

        parcela = parcelaDAO.save(parcela);
        em.refresh(parcela);
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

        jFormattedTextField3 = new javax.swing.JFormattedTextField();
        pnlDados = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNumero = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cboMeioDePagamento = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        txtValor = new javax.swing.JFormattedTextField();
        btnOk = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        txtVenda = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtVencimento = new javax.swing.JFormattedTextField();
        txtMulta = new javax.swing.JFormattedTextField();
        txtJuros = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRecebimentos = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();

        jFormattedTextField3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Editar Parcela");
        setResizable(false);

        pnlDados.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Venda");

        txtNumero.setEditable(false);
        txtNumero.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtNumero.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtNumero.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNumeroKeyReleased(evt);
            }
        });

        jLabel2.setText("Valor");

        cboMeioDePagamento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel3.setText("Meio de Pagamento");

        txtValor.setEditable(false);
        txtValor.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtValor.setName("decimal"); // NOI18N

        btnOk.setText("OK");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        txtVenda.setEditable(false);
        txtVenda.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel5.setText("Parcela");

        txtVencimento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtVencimento.setName("data"); // NOI18N

        txtMulta.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtMulta.setName("decimal"); // NOI18N

        txtJuros.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtJuros.setName("decimal"); // NOI18N

        jLabel6.setText("Multa %");

        jLabel4.setText("Vencimento");

        jLabel7.setText("Juros");

        javax.swing.GroupLayout pnlDadosLayout = new javax.swing.GroupLayout(pnlDados);
        pnlDados.setLayout(pnlDadosLayout);
        pnlDadosLayout.setHorizontalGroup(
            pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDadosLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlDadosLayout.createSequentialGroup()
                        .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboMeioDePagamento, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(txtVencimento, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlDadosLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtMulta))
                        .addGap(18, 18, 18)
                        .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(txtJuros, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        pnlDadosLayout.setVerticalGroup(
            pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5)
                    .addComponent(jLabel3)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboMeioDePagamento, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtVencimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtMulta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtJuros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk)
                    .addComponent(btnCancelar))
                .addContainerGap())
        );

        tblRecebimentos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblRecebimentos);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Lançamentos");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlDados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(0, 904, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNumeroKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumeroKeyReleased
    }//GEN-LAST:event_txtNumeroKeyReleased

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        if (validar()) {
            salvar();
        }
    }//GEN-LAST:event_btnOkActionPerformed

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
            java.util.logging.Logger.getLogger(PessoaParcelaEditarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PessoaParcelaEditarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PessoaParcelaEditarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PessoaParcelaEditarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                PessoaParcelaEditarView dialog = new PessoaParcelaEditarView(new javax.swing.JFrame(), true);
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
    private javax.swing.JComboBox<Object> cboMeioDePagamento;
    private javax.swing.JFormattedTextField jFormattedTextField3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlDados;
    private javax.swing.JTable tblRecebimentos;
    private javax.swing.JFormattedTextField txtJuros;
    private javax.swing.JFormattedTextField txtMulta;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JFormattedTextField txtValor;
    private javax.swing.JFormattedTextField txtVencimento;
    private javax.swing.JTextField txtVenda;
    // End of variables declaration//GEN-END:variables
}
