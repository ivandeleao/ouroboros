/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.produto.geral;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import model.mysql.bean.principal.catalogo.TabelaPreco;
import model.mysql.bean.principal.catalogo.TabelaPrecoVariacao;
import model.mysql.dao.principal.catalogo.TabelaPrecoVariacaoDAO;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.Cor;
import util.Decimal;
import util.JSwing;

/**
 *
 * @author ivand
 */
public class TabelaPrecoVariacaoCadastroView extends javax.swing.JDialog {

    private TabelaPreco tabelaPreco;
    private TabelaPrecoVariacao tabelaPrecoVariacao;
    private boolean comIntervalo;
    private BigDecimal valorInicial, valorFinal, acrescimoMonetario, acrescimoPercentual, descontoMonetario, descontoPercentual;
    
    private TabelaPrecoVariacaoDAO tabelaPrecoVariacaoDAO = new TabelaPrecoVariacaoDAO();

    /**
     * Creates new form CaixaSangria
     */
    protected TabelaPrecoVariacaoCadastroView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        JSwing.startComponentsBehavior(this);
    }

    public TabelaPrecoVariacaoCadastroView(TabelaPreco tabelaPreco, TabelaPrecoVariacao tabelaPrecoVariacao) {
        super(MAIN_VIEW, true);
        initComponents();
        JSwing.startComponentsBehavior(this);
        
        definirAtalhos();

        this.tabelaPreco = tabelaPreco;
        this.tabelaPrecoVariacao = tabelaPrecoVariacao;
        
        carregarDados();
        usarIntervalo();
        formatarAcrescimoTipo();
        formatarDescontoTipo();

        this.setLocationRelativeTo(MAIN_VIEW);
        this.setVisible(true);
    }
    
    private void definirAtalhos() {
        InputMap im = rootPane.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = rootPane.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "fechar");
        am.put("fechar", new FormKeyStroke("ESC"));

    }

    protected class FormKeyStroke extends AbstractAction {

        private final String key;

        public FormKeyStroke(String key) {
            this.key = key;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (key) {
                case "ESC":
                    dispose();
                    break;
            }
        }
    }

    private void carregarDados() {
        txtNome.setText(tabelaPreco.getNome());
        
        chkComIntervalo.setSelected(tabelaPrecoVariacao.isComIntervalo());
        
        txtValorInicial.setText(Decimal.toString(tabelaPrecoVariacao.getValorInicial()));
        txtValorFinal.setText(Decimal.toString(tabelaPrecoVariacao.getValorFinal()));
        
        btnAcrescimoTipo.setText(tabelaPrecoVariacao.getAcrescimoTipo());
        btnDescontoTipo.setText(tabelaPrecoVariacao.getDescontoTipo());
        
        txtAcrescimo.setText(Decimal.toString(tabelaPrecoVariacao.getAcrescimoSemTipo()));
        txtDesconto.setText(Decimal.toString(tabelaPrecoVariacao.getDescontoSemTipo()));
        
        
        
    }
    
    private void alternarAcrescimoTipo() {
        btnAcrescimoTipo.setText(btnAcrescimoTipo.getText().equals("%") ? "$" : "%");

        formatarAcrescimoTipo();
    }
    
    private void formatarAcrescimoTipo() {
        btnAcrescimoTipo.setBackground(btnAcrescimoTipo.getText().equals("%") ? Cor.AZUL : Cor.LARANJA);

        btnAcrescimoTipo.repaint();
    }
    
    
    private void alternarDescontoTipo() {
        btnDescontoTipo.setText(btnDescontoTipo.getText().equals("%") ? "$" : "%");

        formatarDescontoTipo();
    }
    
    private void formatarDescontoTipo() {
        btnDescontoTipo.setBackground(btnDescontoTipo.getText().equals("%") ? Cor.AZUL : Cor.LARANJA);

        btnDescontoTipo.repaint();
    }
    
    private void usarIntervalo() {
        comIntervalo = chkComIntervalo.isSelected();
        txtValorInicial.setEditable(comIntervalo);
        txtValorFinal.setEditable(comIntervalo);
        
        if(!comIntervalo) {
            txtValorInicial.setText("0,00");
            txtValorFinal.setText("0,00");
        }
    }
    
    private void editarAcrescimo() {
        acrescimoMonetario = Decimal.fromString(txtAcrescimo.getText());
        if (acrescimoMonetario.compareTo(BigDecimal.ZERO) > 0) {
            txtDesconto.setText("0");
        }
    }
    
    private void editarDesconto() {
        descontoMonetario = Decimal.fromString(txtDesconto.getText());
        if (descontoMonetario.compareTo(BigDecimal.ZERO) > 0) {
            txtAcrescimo.setText("0");
        }
    }

    private void confirmar() {
        comIntervalo = chkComIntervalo.isSelected();
        valorInicial = Decimal.fromString(txtValorInicial.getText());
        valorFinal = Decimal.fromString(txtValorFinal.getText());
        
        BigDecimal acrescimo = Decimal.fromString(txtAcrescimo.getText());
        BigDecimal desconto = Decimal.fromString(txtDesconto.getText());
        
        if(btnAcrescimoTipo.getText().equals("%")) {
            acrescimoMonetario = BigDecimal.ZERO;
            acrescimoPercentual = acrescimo;
        } else {
            acrescimoMonetario = acrescimo;
            acrescimoPercentual = BigDecimal.ZERO;
        }
        
        if(btnDescontoTipo.getText().equals("%")) {
            descontoMonetario = BigDecimal.ZERO;
            descontoPercentual = desconto;
        } else {
            descontoMonetario = desconto;
            descontoPercentual = BigDecimal.ZERO;
        }
        
        
        //validar
        if(!comIntervalo && tabelaPrecoVariacao.getId() == null && tabelaPreco.getTabelaPrecoVariacoes().size() > 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Esta tabela já possui variações. É obrigatório informar intervalo de valor.", "Atenção", JOptionPane.WARNING_MESSAGE);
            chkComIntervalo.requestFocus();
            
        } else if(comIntervalo && valorInicial.compareTo(valorFinal) >= 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Valor final deve ser maior que o inicial", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtValorFinal.requestFocus();
        
        } else if(!tabelaPreco.validarNovoIntervalo(tabelaPrecoVariacao, valorInicial, valorFinal)) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "O intervalo colide com outros já cadastrados", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtValorInicial.requestFocus();
            
        } else if(acrescimo.add(desconto).compareTo(BigDecimal.ZERO) == 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Informe um valor de acréscimo ou desconto", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtAcrescimo.requestFocus();
            
        } else {
        
            tabelaPrecoVariacao.setComIntervalo(comIntervalo);
            
            tabelaPrecoVariacao.setValorInicial(valorInicial);
            tabelaPrecoVariacao.setValorFinal(valorFinal);
            
            tabelaPrecoVariacao.setAcrescimoMonetario(acrescimoMonetario);
            tabelaPrecoVariacao.setAcrescimoPercentual(acrescimoPercentual);
            
            tabelaPrecoVariacao.setDescontoMonetario(descontoMonetario);
            tabelaPrecoVariacao.setDescontoPercentual(descontoPercentual);


            tabelaPreco.addTabelaPrecoVariacao(tabelaPrecoVariacao);

            tabelaPrecoVariacaoDAO.save(tabelaPrecoVariacao);

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
        jLabel3 = new javax.swing.JLabel();
        txtValorInicial = new javax.swing.JFormattedTextField();
        txtNome = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtValorFinal = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txtAcrescimo = new javax.swing.JFormattedTextField();
        btnAcrescimoTipo = new javax.swing.JButton();
        Desconto = new javax.swing.JLabel();
        btnDescontoTipo = new javax.swing.JButton();
        txtDesconto = new javax.swing.JFormattedTextField();
        chkComIntervalo = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Variação de Tabela de Preço");
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

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Valor Inicial");

        txtValorInicial.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorInicial.setText("0,00");
        txtValorInicial.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtValorInicial.setName("decimal"); // NOI18N
        txtValorInicial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorInicialKeyReleased(evt);
            }
        });

        txtNome.setEditable(false);
        txtNome.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Valor Final");

        txtValorFinal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorFinal.setText("0,00");
        txtValorFinal.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtValorFinal.setName("decimal"); // NOI18N
        txtValorFinal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorFinalKeyReleased(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("Acréscimo");

        txtAcrescimo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAcrescimo.setText("0,00");
        txtAcrescimo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAcrescimo.setName("decimal"); // NOI18N
        txtAcrescimo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAcrescimoActionPerformed(evt);
            }
        });
        txtAcrescimo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAcrescimoKeyReleased(evt);
            }
        });

        btnAcrescimoTipo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnAcrescimoTipo.setText("%");
        btnAcrescimoTipo.setFocusable(false);
        btnAcrescimoTipo.setPreferredSize(new java.awt.Dimension(55, 25));
        btnAcrescimoTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAcrescimoTipoActionPerformed(evt);
            }
        });

        Desconto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Desconto.setText("Desconto");

        btnDescontoTipo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnDescontoTipo.setText("%");
        btnDescontoTipo.setFocusable(false);
        btnDescontoTipo.setPreferredSize(new java.awt.Dimension(55, 25));
        btnDescontoTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDescontoTipoActionPerformed(evt);
            }
        });

        txtDesconto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDesconto.setText("0,00");
        txtDesconto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDesconto.setName("decimal"); // NOI18N
        txtDesconto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDescontoActionPerformed(evt);
            }
        });
        txtDesconto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDescontoKeyReleased(evt);
            }
        });

        chkComIntervalo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        chkComIntervalo.setText("Usar intervalo de valor:");
        chkComIntervalo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkComIntervaloActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Tabela");

        jLabel1.setForeground(java.awt.Color.blue);
        jLabel1.setText("Acréscimo e Desconto são mutuamente exclusivos");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAcrescimoTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAcrescimo, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Desconto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDescontoTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDesconto, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(chkComIntervalo)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtValorInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtValorFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(txtNome)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnConfirmar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtValorInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtValorFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkComIntervalo))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtDesconto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDescontoTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Desconto)
                    .addComponent(txtAcrescimo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAcrescimoTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConfirmar)
                    .addComponent(btnCancelar)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        confirmar();
    }//GEN-LAST:event_btnConfirmarActionPerformed

    private void txtValorInicialKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorInicialKeyReleased
        
    }//GEN-LAST:event_txtValorInicialKeyReleased

    private void txtValorFinalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorFinalKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValorFinalKeyReleased

    private void txtAcrescimoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAcrescimoKeyReleased
        editarAcrescimo();
    }//GEN-LAST:event_txtAcrescimoKeyReleased

    private void btnAcrescimoTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAcrescimoTipoActionPerformed
        alternarAcrescimoTipo();
    }//GEN-LAST:event_btnAcrescimoTipoActionPerformed

    private void txtAcrescimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAcrescimoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAcrescimoActionPerformed

    private void btnDescontoTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDescontoTipoActionPerformed
        alternarDescontoTipo();
    }//GEN-LAST:event_btnDescontoTipoActionPerformed

    private void txtDescontoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDescontoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDescontoActionPerformed

    private void txtDescontoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescontoKeyReleased
        editarDesconto();
    }//GEN-LAST:event_txtDescontoKeyReleased

    private void chkComIntervaloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkComIntervaloActionPerformed
        usarIntervalo();
    }//GEN-LAST:event_chkComIntervaloActionPerformed

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
            java.util.logging.Logger.getLogger(TabelaPrecoVariacaoCadastroView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TabelaPrecoVariacaoCadastroView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TabelaPrecoVariacaoCadastroView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TabelaPrecoVariacaoCadastroView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                TabelaPrecoVariacaoCadastroView dialog = new TabelaPrecoVariacaoCadastroView(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel Desconto;
    private javax.swing.JButton btnAcrescimoTipo;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnDescontoTipo;
    private javax.swing.JCheckBox chkComIntervalo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JFormattedTextField txtAcrescimo;
    private javax.swing.JFormattedTextField txtDesconto;
    private javax.swing.JTextField txtNome;
    private javax.swing.JFormattedTextField txtValorFinal;
    private javax.swing.JFormattedTextField txtValorInicial;
    // End of variables declaration//GEN-END:variables
}
