/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoSaida;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.financeiro.Cheque;
import model.mysql.dao.principal.ParcelaDAO;
import model.mysql.dao.fiscal.MeioDePagamentoDAO;
import model.mysql.dao.principal.VendaDAO;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;
import util.Decimal;
import util.JSwing;
import view.financeiro.cheque.ChequeCadastroView;
import view.financeiro.cheque.ChequePesquisaView;

/**
 *
 * @author ivand
 *
 * ESTE FOI CRIADO COMO JDIALOG DESDE O INÍCIO - USAR ESTE COMO REFERÊNCIA PARA
 * OS DEMAIS
 *
 *
 */
public class ParcelamentoEditarView extends javax.swing.JDialog {

    private Parcela parcela;
    ParcelaDAO parcelaDAO = new ParcelaDAO();
    VendaDAO vendaDAO = new VendaDAO();

    /**
     * Creates new form ParcelamentoView
     */
    public ParcelamentoEditarView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public ParcelamentoEditarView(java.awt.Frame parent, Parcela parcela) {
        super(parent, true);
        initComponents();
        JSwing.startComponentsBehavior(this);

        this.parcela = parcela;
        
        if(parcela.getNumero() == parcela.getVenda().getParcelasSemCartao().size()) {
            txtValor.setEditable(false);
        }
        
        carregarDados();
        
        txtVencimento.requestFocus();

        this.setLocationRelativeTo(this); //centralizar
        this.setVisible(true);
    }
    
    private void carregarDados() {
        txtNumero.setText(parcela.getNumero().toString());
        txtVencimento.setText(DateTime.toString(parcela.getVencimento()));
        txtValor.setText(Decimal.toString(parcela.getValor()));
        
        
        carregarMeioDePagamento();
    }

    private void carregarMeioDePagamento() {
        List<MeioDePagamento> mpList = new MeioDePagamentoDAO().findAllEnabled();

        cboMeioDePagamento.addItem(MeioDePagamento.CREDITO_LOJA); //garantir que haja este meio de pagamento
        for (MeioDePagamento mp : mpList) {
            cboMeioDePagamento.addItem(mp);
        }
        cboMeioDePagamento.setSelectedItem(parcela.getMeioDePagamento());
    }
    
    private void cheque() {
        if (!cboMeioDePagamento.getSelectedItem().equals(MeioDePagamento.CHEQUE)) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Escolha o meio de pagamento Cheque", "Atenção", JOptionPane.WARNING_MESSAGE);
            cboMeioDePagamento.requestFocus();
            
        } else {
            if (validarESalvar()) {
                if (parcela.getCheque() != null) {
                    new ChequeCadastroView(parcela.getCheque());
                    
                } else {
                    ChequePesquisaView chequePesquisaView = new ChequePesquisaView();
                    Cheque cheque = chequePesquisaView.getCheque();
                    if (cheque != null) {
                        cheque.setUtilizado(LocalDateTime.now());
                        new ChequeCadastroView(cheque, parcela);
                    }
                }
            }
        }
    }
    
    private boolean validarESalvar() {
        LocalDate vencimento = DateTime.fromStringToLocalDate(txtVencimento.getText());
        MeioDePagamento mp = (MeioDePagamento) cboMeioDePagamento.getSelectedItem();
        BigDecimal novoValor = Decimal.fromString(txtValor.getText());
        
        Venda venda = parcela.getVenda();
        ////List<Parcela> parcelasAPrazo = venda.getParcelasSemCartao();
        
        BigDecimal totalReceber = venda.getTotalReceber();
        System.out.println("totalReceber: " + totalReceber);
        
        //dividir valor pelo número de parcelas
        BigDecimal quantidade = new BigDecimal(venda.getParcelasSemCartao().size());

        Parcela parcelaInicial = parcela;
        BigDecimal somaAnteriores = BigDecimal.ZERO;
        BigDecimal qtdAnteriores = new BigDecimal(parcelaInicial.getNumero());

        //distribuir os valores
        for (Parcela parcela : venda.getParcelasSemCartao()) {
            if (parcela.getNumero() < parcelaInicial.getNumero()) {
                //somar as parcelas fixadas
                somaAnteriores = somaAnteriores.add(parcela.getValor());
            }
        }
        
        if(novoValor.compareTo(totalReceber.subtract(somaAnteriores)) > 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "O valor ultrapassa o valor restante", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtValor.requestFocus();
            return false;
            
        } else {
            //parcela selecionada
            parcela.setVencimento(vencimento);
            parcela.setMeioDePagamento(mp);
            parcelaInicial.setValor(novoValor);
            ////parcela = parcelaDAO.save(parcelaInicial); 2019-11-14
            venda.addParcela(parcela);
            parcela = parcelaDAO.save(parcelaInicial);
            ////venda = vendaDAO.save(venda); 2019-11-14
            
            //parcelas subsequentes
            if(parcela.getNumero() < parcela.getVenda().getParcelasSemCartao().size()) {
                BigDecimal qtd = quantidade.subtract(qtdAnteriores);
                System.out.println("qtd: " + qtd);
                BigDecimal valorRestante = totalReceber.subtract(somaAnteriores.add(novoValor)).divide(qtd, 2, RoundingMode.HALF_DOWN);
                System.out.println("valorRestantes: " + valorRestante);
                
                for (Parcela parcela : venda.getParcelasSemCartao()) {
                    System.out.println("redefinir valor outras: " + parcela.getNumero());
                    if (parcela.getNumero() > parcelaInicial.getNumero()) {
                        System.out.println("valor: " + valorRestante);
                        parcela.setValor(valorRestante);
                        ////parcela = parcelaDAO.save(parcela); 2019-11-14
                        venda.addParcela(parcela);
                        parcelaDAO.save(parcela);
                        ////venda = vendaDAO.save(venda); 2019-11-14
                    }
                }
            
                //Contabilizar possível resto (valor quebrado na divisão)
                BigDecimal novoTotal = venda.getParcelasSemCartao().stream().map(Parcela::getValor).reduce(BigDecimal::add).get();
                BigDecimal resto = totalReceber.subtract(novoTotal);

                Parcela proximaParcela = venda.getParcelasSemCartao().get(parcelaInicial.getNumero());
                proximaParcela.setValor(valorRestante.add(resto));
                ////parcela = parcelaDAO.save(proximaParcela); 2019-11-14
                venda.addParcela(parcela);
                parcelaDAO.save(proximaParcela);
                ////vendaDAO.save(venda); 2019-11-14
            }
            
            return true;
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

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNumero = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cboMeioDePagamento = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        txtVencimento = new javax.swing.JFormattedTextField();
        txtValor = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        btnOk = new javax.swing.JButton();
        btnFechar = new javax.swing.JButton();
        btnCheque = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Editar Parcela");
        setResizable(false);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Número");

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

        txtVencimento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtVencimento.setName("data"); // NOI18N

        txtValor.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtValor.setName("decimal"); // NOI18N

        jLabel4.setText("Vencimento");

        btnOk.setText("OK");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        btnFechar.setText("Fechar");
        btnFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFecharActionPerformed(evt);
            }
        });

        btnCheque.setText("Cheque");
        btnCheque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChequeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(txtVencimento, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(cboMeioDePagamento, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(btnCheque, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFechar, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVencimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboMeioDePagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk)
                    .addComponent(btnFechar)
                    .addComponent(btnCheque))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNumeroKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumeroKeyReleased
    }//GEN-LAST:event_txtNumeroKeyReleased

    private void btnFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFecharActionPerformed
        dispose();
    }//GEN-LAST:event_btnFecharActionPerformed

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        if (validarESalvar()) {
            dispose();
        }
    }//GEN-LAST:event_btnOkActionPerformed

    private void btnChequeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChequeActionPerformed
        cheque();
    }//GEN-LAST:event_btnChequeActionPerformed

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
            java.util.logging.Logger.getLogger(ParcelamentoEditarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ParcelamentoEditarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ParcelamentoEditarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ParcelamentoEditarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ParcelamentoEditarView dialog = new ParcelamentoEditarView(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnCheque;
    private javax.swing.JButton btnFechar;
    private javax.swing.JButton btnOk;
    private javax.swing.JComboBox<Object> cboMeioDePagamento;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JFormattedTextField txtValor;
    private javax.swing.JFormattedTextField txtVencimento;
    // End of variables declaration//GEN-END:variables
}
