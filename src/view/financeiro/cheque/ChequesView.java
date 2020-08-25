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
import java.math.BigDecimal;
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
import javax.swing.SwingConstants;
import model.jtable.financeiro.cheque.ChequesJTableModel;
import model.mysql.bean.principal.financeiro.Cheque;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.dao.principal.financeiro.ChequeDAO;
import static ouroboros.Constants.*;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.JSwing;
import util.DateTime;
import util.Decimal;
import util.jTableFormat.FinanceiroStatusRenderer;

/**
 *
 * @author ivand
 */
public class ChequesView extends javax.swing.JInternalFrame {

    private static ChequesView singleInstance = null;
    ChequesJTableModel chequesJTableModel = new ChequesJTableModel();
    ChequeDAO chequeDAO = new ChequeDAO();

    List<Cheque> cheques = new ArrayList<>();

    private String conta, numero, correntista;
    private LocalDate dataInicial, dataFinal;
    private Optional<Boolean> utilizado;
    //private BigDecimal total, totalAtualizado, totalRecebido;

    public static ChequesView getSingleInstance() {
        if (singleInstance == null) {
            singleInstance = new ChequesView();
        }
        return singleInstance;
    }

    /**
     * Creates new form CategoriaCadastroView
     */
    private ChequesView() {
        initComponents();
        JSwing.startComponentsBehavior(this);

        txtDataInicial.setText(DateTime.toString(LocalDate.now().minusMonths(1)));
        txtDataFinal.setText(DateTime.toString(LocalDate.now().plusMonths(1)));

        formatarTabela();

        cboUtilizado.setSelectedIndex(2);
        //carregarTabela();

        definirAtalhos();

    }

    private void definirAtalhos() {
        InputMap im = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "novo");
        am.put("novo", new FormKeyStroke("F1"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "estoqueManual");
        am.put("estoqueManual", new FormKeyStroke("F2"));
    }

    protected class FormKeyStroke extends AbstractAction {

        private final String key;

        public FormKeyStroke(String key) {
            this.key = key;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (key) {
                case "F1":
                    //novo();
                    break;

            }
        }
    }

    private void formatarTabela() {
        tblCheques.setModel(chequesJTableModel);

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

        tblCheques.getColumn("Observação").setPreferredWidth(300);
        
        tblCheques.getColumn("Utilizado").setPreferredWidth(120);
        tblCheques.getColumn("Utilizado").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

    }

    private void editar() {
        Cheque cheque = chequesJTableModel.getRow(tblCheques.getSelectedRow());
        new ChequeCadastroView(cheque);
        chequesJTableModel.fireTableRowsUpdated(tblCheques.getSelectedRow(), tblCheques.getSelectedRow());
        //carregarTabela();
    }

    private void catchClick() {
        int indices[] = tblCheques.getSelectedRows();

        ArrayList<Integer> ids = new ArrayList<>();
        for (int index : indices) {
            ids.add(chequesJTableModel.getRow(index).getId());
        }
        //System.out.println("index: " + tblCrediario.getSelectedRow());
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

        chequesJTableModel.clear();
        chequesJTableModel.addList(cheques);

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

    private void novo() {
        new ChequeCadastroView(new Cheque());
        carregarTabela();
    }

    private void excluir() {
        int rowIndex = tblCheques.getSelectedRow();
        
        
        if(rowIndex >= 0) {
            Cheque cheque = chequesJTableModel.getRow(rowIndex);
            
            int resposta = JOptionPane.showConfirmDialog(MAIN_VIEW, "Excluir o cheque ID " + cheque.getId() + "?", "Atenção", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

            if(resposta == JOptionPane.OK_OPTION) {
                chequeDAO.delete(cheque);
                carregarTabela();
            }
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
        jPanel2 = new javax.swing.JPanel();
        btnExcluir = new javax.swing.JButton();
        btnNovo = new javax.swing.JButton();
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

        setClosable(true);
        setTitle("Cheques");
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameActivated(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblCheques.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
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
        tblCheques.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-close-button-20.png"))); // NOI18N
        btnExcluir.setText("Excluir");
        btnExcluir.setContentAreaFilled(false);
        btnExcluir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExcluir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        btnNovo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-add-20.png"))); // NOI18N
        btnNovo.setText("Novo");
        btnNovo.setContentAreaFilled(false);
        btnNovo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNovo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnNovo)
                .addGap(18, 18, 18)
                .addComponent(btnExcluir)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

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
                        .addGap(0, 0, Short.MAX_VALUE))
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1164, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
    }//GEN-LAST:event_formComponentShown

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        MAIN_VIEW.removeTab(this.getName());
    }//GEN-LAST:event_formInternalFrameClosing

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        //System.out.println("focus");
        //tableCategoriasUpdateRow();
    }//GEN-LAST:event_formFocusGained

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated

    }//GEN-LAST:event_formInternalFrameActivated

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        singleInstance = null;
    }//GEN-LAST:event_formInternalFrameClosed

    private void tblChequesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblChequesMouseClicked
        //catchClick();
        if (evt.getClickCount() == 2) {
            editar();
        }

    }//GEN-LAST:event_tblChequesMouseClicked

    private void tblChequesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblChequesFocusGained
        //tableCategoriasUpdateRow();

    }//GEN-LAST:event_tblChequesFocusGained

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        excluir();
    }//GEN-LAST:event_btnExcluirActionPerformed

    private void btnNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoActionPerformed
        novo();
    }//GEN-LAST:event_btnNovoActionPerformed

    private void txtNumeroKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumeroKeyReleased
        carregarTabela();
    }//GEN-LAST:event_txtNumeroKeyReleased

    private void txtContaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtContaKeyReleased
        carregarTabela();
    }//GEN-LAST:event_txtContaKeyReleased

    private void txtDataInicialKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDataInicialKeyReleased
        if (txtDataInicial.getText().length() == 0 || txtDataInicial.getText().length() == 10) {
            carregarTabela();
        }
    }//GEN-LAST:event_txtDataInicialKeyReleased

    private void txtCorrentistaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCorrentistaKeyReleased
        carregarTabela();
    }//GEN-LAST:event_txtCorrentistaKeyReleased

    private void cboUtilizadoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboUtilizadoItemStateChanged
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
            carregarTabela();
        }
    }//GEN-LAST:event_cboUtilizadoItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Conta;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnNovo;
    private javax.swing.JComboBox<String> cboUtilizado;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
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
