/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.financeiro.cheque;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import model.mysql.bean.principal.financeiro.Cheque;
import model.mysql.dao.principal.financeiro.ChequeDAO;
import model.jtable.financeiro.cheque.ChequePesquisaJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;

/**
 *
 * @author ivand
 */
public class ChequePesquisaView extends javax.swing.JDialog {

    //private static ChequePesquisaView chequePesquisaView;
    ChequePesquisaJTableModel chequePesquisaJTableModel = new ChequePesquisaJTableModel();
    ChequeDAO chequeDAO = new ChequeDAO();

    List<Cheque> cheques = new ArrayList<>();
    private String conta, numero, correntista;
    private LocalDate dataInicial, dataFinal;
    private Optional<Boolean> utilizado;
    
    Cheque cheque = null;

    
    public ChequePesquisaView() {
        super(MAIN_VIEW, true);
        initComponents();
        definirAtalhos();

        formatarTabela();

        cboUtilizado.setSelectedIndex(2);
        //carregarTabela();

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

    public Cheque getCheque() {
        return cheque;
    }

    private void carregarTabela() {
        conta = txtConta.getText();
        numero = txtNumero.getText();
        correntista = txtCorrentista.getText();
        
        dataInicial = DateTime.fromStringToLocalDate(txtDataInicial.getText());
        dataFinal = DateTime.fromStringToLocalDate(txtDataFinal.getText());
        
        utilizado = cboUtilizado.getSelectedIndex() == 0 ? Optional.empty()
                : (cboUtilizado.getSelectedIndex() == 1 ? Optional.of(true) : Optional.of(false));
        
        cheques = new ChequeDAO().findByCriteria(conta, numero, correntista, dataInicial, dataFinal, utilizado, false);

        // modelo para manter posição da tabela - melhorar: caso altere o vencimento, muda a ordem! :<
        int rowIndex = tblCheques.getSelectedRow();

        chequePesquisaJTableModel.clear();
        chequePesquisaJTableModel.addList(cheques);

        //posicionar na última linha
        if (tblCheques.getRowCount() > 0) {
            if (rowIndex < 0 || rowIndex >= tblCheques.getRowCount()) {
                rowIndex = 0;
            }
            //JOptionPane.showMessageDialog(rootPane, rowIndex);
            tblCheques.setRowSelectionInterval(rowIndex, rowIndex);
            tblCheques.scrollRectToVisible(tblCheques.getCellRect(rowIndex, 0, true));
        }
        
    }

    private void formatarTabela() {
        tblCheques.setModel(chequePesquisaJTableModel);

        tblCheques.setRowHeight(30);
        tblCheques.setIntercellSpacing(new Dimension(10, 10));

        tblCheques.getColumn("Id").setPreferredWidth(60);
        tblCheques.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblCheques.getColumn("Vencimento").setPreferredWidth(120);
        tblCheques.getColumn("Vencimento").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblCheques.getColumn("Banco").setPreferredWidth(100);
        tblCheques.getColumn("Banco").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblCheques.getColumn("Agência").setPreferredWidth(100);
        tblCheques.getColumn("Agência").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblCheques.getColumn("Conta").setPreferredWidth(120);
        tblCheques.getColumn("Conta").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblCheques.getColumn("Número").setPreferredWidth(120);
        tblCheques.getColumn("Número").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblCheques.getColumn("Correntista").setPreferredWidth(300);

        tblCheques.getColumn("CPF/CNPJ").setPreferredWidth(200);
        tblCheques.getColumn("CPF/CNPJ").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblCheques.getColumn("Valor").setPreferredWidth(120);
        tblCheques.getColumn("Valor").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblCheques.getColumn("Observação").setPreferredWidth(200);
        
    }
    
    private void novo() {
        cheque = new Cheque();
        dispose();
    }
    
    private void confirmar() {
        if (tblCheques.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione um cheque", "Atenção", JOptionPane.WARNING_MESSAGE);
            
        } else {
            cheque = chequePesquisaJTableModel.getRow(tblCheques.getSelectedRow());
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblCheques = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        btnFiltrar = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtDataInicial = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtDataFinal = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        txtNumero = new javax.swing.JTextField();
        txtConta = new javax.swing.JTextField();
        Conta = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtCorrentista = new javax.swing.JTextField();
        cboUtilizado = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        btnNovo = new javax.swing.JButton();
        btnNovo1 = new javax.swing.JButton();

        setTitle("Pesquisar Cheque");

        tblCheques.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tblCheques.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCheques.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblChequesFocusGained(evt);
            }
        });
        tblCheques.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblChequesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCheques);

        jLabel1.setForeground(java.awt.Color.blue);
        jLabel1.setText("Rolar: PageUp e PageDown | Confirmar: Enter | Cancelar: Esc");

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnFiltrar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnFiltrar.setText("Atualizar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Data Inicial");

        txtDataInicial.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDataInicial.setName("data"); // NOI18N
        txtDataInicial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDataInicialKeyReleased(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Data Final");

        txtDataFinal.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDataFinal.setName("data"); // NOI18N

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Número");

        txtNumero.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtNumero.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNumeroKeyReleased(evt);
            }
        });

        txtConta.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtConta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtContaKeyReleased(evt);
            }
        });

        Conta.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Conta.setText("Conta");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setText("Correntista");

        txtCorrentista.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtCorrentista.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCorrentistaKeyReleased(evt);
            }
        });

        cboUtilizado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cboUtilizado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "S/N", "Sim", "Não" }));
        cboUtilizado.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboUtilizadoItemStateChanged(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setText("Utilizado");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(Conta)
                        .addGap(18, 18, 18)
                        .addComponent(txtConta, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(txtCorrentista, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 307, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10)
                        .addGap(18, 18, 18)
                        .addComponent(cboUtilizado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFiltrar)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtConta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Conta)
                    .addComponent(jLabel9)
                    .addComponent(txtCorrentista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(btnFiltrar)
                    .addComponent(jLabel10)
                    .addComponent(cboUtilizado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnNovo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnNovo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-add-20.png"))); // NOI18N
        btnNovo.setText("Novo");
        btnNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoActionPerformed(evt);
            }
        });

        btnNovo1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnNovo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-checkmark-20.png"))); // NOI18N
        btnNovo1.setText("Confirmar");
        btnNovo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovo1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnNovo)
                        .addGap(18, 18, 18)
                        .addComponent(btnNovo1))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNovo)
                    .addComponent(jLabel1)
                    .addComponent(btnNovo1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblChequesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblChequesMouseClicked
        if (evt.getClickCount() == 2) {
            confirmar();
        }
    }//GEN-LAST:event_tblChequesMouseClicked

    private void tblChequesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblChequesFocusGained
    }//GEN-LAST:event_tblChequesFocusGained

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void txtDataInicialKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDataInicialKeyReleased
        if (txtDataInicial.getText().length() == 0 || txtDataInicial.getText().length() == 10) {
            carregarTabela();
        }
    }//GEN-LAST:event_txtDataInicialKeyReleased

    private void txtNumeroKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumeroKeyReleased
        carregarTabela();
    }//GEN-LAST:event_txtNumeroKeyReleased

    private void txtContaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtContaKeyReleased
        carregarTabela();
    }//GEN-LAST:event_txtContaKeyReleased

    private void txtCorrentistaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCorrentistaKeyReleased
        carregarTabela();
    }//GEN-LAST:event_txtCorrentistaKeyReleased

    private void cboUtilizadoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboUtilizadoItemStateChanged
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
            carregarTabela();
        }
    }//GEN-LAST:event_cboUtilizadoItemStateChanged

    private void btnNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoActionPerformed
        novo();
    }//GEN-LAST:event_btnNovoActionPerformed

    private void btnNovo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovo1ActionPerformed
        confirmar();
    }//GEN-LAST:event_btnNovo1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Conta;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton btnNovo1;
    private javax.swing.JComboBox<String> cboUtilizado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblCheques;
    private javax.swing.JTextField txtConta;
    private javax.swing.JTextField txtCorrentista;
    private javax.swing.JFormattedTextField txtDataFinal;
    private javax.swing.JFormattedTextField txtDataInicial;
    private javax.swing.JTextField txtNumero;
    // End of variables declaration//GEN-END:variables

}
