/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.pessoa;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.nosql.FinanceiroStatusEnum;
import model.mysql.dao.principal.ParcelaDAO;
import model.mysql.dao.fiscal.MeioDePagamentoDAO;
import model.jtable.RecebimentoListaJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.PARCELA_JUROS_MONETARIO_MENSAL;
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
        definirAtalhos();
        
        this.setTitle(this.getTitle() + " (" + this.getClass().getCanonicalName() + ")");
        
        JSwing.startComponentsBehavior(this);

        this.parcela = parcela;
        
        if(parcela.getValorQuitado().compareTo(BigDecimal.ZERO) > 0) {
            JSwing.setComponentesHabilitados(pnlDados, false);
        }
        

        formatarTabela();
        
        carregarDados();

        txtVencimento.requestFocus();

        this.setLocationRelativeTo(this); //centralizar
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
        public FormKeyStroke(String key){
            this.key = key;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            switch(key){
                case "ESC":
                    dispose();
                    break;
            }
        }
    }

    private void carregarDados() {
        txtDocumento.setText(parcela.getVenda().getId().toString());
        txtNumero.setText(parcela.getNumeroDeTotal());
        txtValor.setText(Decimal.toString(parcela.getValor()));

        txtVencimento.setText(DateTime.toString(parcela.getVencimento()));
        txtDiasAtraso.setText(parcela.getDiasEmAtraso().toString());
        
        txtMulta.setText(Decimal.toString(parcela.getMulta()));
        txtMultaCalculada.setText(Decimal.toString(parcela.getMultaCalculada()));
        txtJuros.setText(Decimal.toString(parcela.getJuros()));
        txtJurosCalculado.setText(Decimal.toString(parcela.getJurosCalculado()));
        txtValorAtual.setText(Decimal.toString(parcela.getValorAtual()));

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
        
        tblRecebimentos.setRowHeight(30);
        tblRecebimentos.setIntercellSpacing(new Dimension(10, 10));
        //id
        tblRecebimentos.getColumn("Id").setPreferredWidth(100);
        tblRecebimentos.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblRecebimentos.getColumn("Recebimento").setPreferredWidth(240);
        tblRecebimentos.getColumn("Recebimento").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        //tipo
        tblRecebimentos.getColumnModel().getColumn(2).setPreferredWidth(360);
        //observacao
        tblRecebimentos.getColumnModel().getColumn(3).setPreferredWidth(360);
        
        tblRecebimentos.getColumn("MP").setPreferredWidth(100);
        tblRecebimentos.getColumn("MP").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblRecebimentos.getColumn("Conta/Caixa").setPreferredWidth(200);
        
        tblRecebimentos.getColumn("Crédito").setPreferredWidth(140);
        tblRecebimentos.getColumn("Crédito").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblRecebimentos.getColumn("Débito").setPreferredWidth(140);
        tblRecebimentos.getColumn("Débito").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
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
        LocalDate vencimento = DateTime.fromStringToLocalDate(txtVencimento.getText());
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
        ////em.refresh(parcela);
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
        txtDocumento = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtVencimento = new javax.swing.JFormattedTextField();
        txtMulta = new javax.swing.JFormattedTextField();
        txtJuros = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtDiasAtraso = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtMultaCalculada = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtJurosCalculado = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtValorAtual = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRecebimentos = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();

        jFormattedTextField3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Editar Parcela");
        setResizable(false);

        pnlDados.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Documento");

        txtNumero.setEditable(false);
        txtNumero.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtNumero.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtNumero.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNumeroKeyReleased(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Valor");

        cboMeioDePagamento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
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

        txtDocumento.setEditable(false);
        txtDocumento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDocumento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Parcela");

        txtVencimento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtVencimento.setName("data"); // NOI18N

        txtMulta.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtMulta.setName("decimal"); // NOI18N

        txtJuros.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtJuros.setName("decimal"); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Multa %");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("Vencimento");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("Juros");

        txtDiasAtraso.setEditable(false);
        txtDiasAtraso.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDiasAtraso.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("Dias atraso");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel10.setText("Multa calculada");

        txtMultaCalculada.setEditable(false);
        txtMultaCalculada.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtMultaCalculada.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setText("Juros calculado");

        txtJurosCalculado.setEditable(false);
        txtJurosCalculado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtJurosCalculado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel12.setText("Valor atual");

        txtValorAtual.setEditable(false);
        txtValorAtual.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtValorAtual.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout pnlDadosLayout = new javax.swing.GroupLayout(pnlDados);
        pnlDados.setLayout(pnlDadosLayout);
        pnlDadosLayout.setHorizontalGroup(
            pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDadosLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(txtMulta, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10)
                        .addGap(18, 18, 18)
                        .addComponent(txtMultaCalculada, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(txtJuros, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11)
                        .addGap(18, 18, 18)
                        .addComponent(txtJurosCalculado, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addGap(18, 18, 18)
                        .addComponent(txtValorAtual))
                    .addGroup(pnlDadosLayout.createSequentialGroup()
                        .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlDadosLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(cboMeioDePagamento, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(txtVencimento, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18)
                                .addComponent(txtDiasAtraso, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlDadosLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(txtDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDadosLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlDadosLayout.setVerticalGroup(
            pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(cboMeioDePagamento, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtVencimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(jLabel9)
                        .addComponent(txtDiasAtraso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMulta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtMultaCalculada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtJuros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel11)
                    .addComponent(txtJurosCalculado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(txtValorAtual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk)
                    .addComponent(btnCancelar))
                .addContainerGap())
        );

        tblRecebimentos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
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
                        .addGap(0, 0, Short.MAX_VALUE))
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
                .addContainerGap())
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
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlDados;
    private javax.swing.JTable tblRecebimentos;
    private javax.swing.JTextField txtDiasAtraso;
    private javax.swing.JTextField txtDocumento;
    private javax.swing.JFormattedTextField txtJuros;
    private javax.swing.JTextField txtJurosCalculado;
    private javax.swing.JFormattedTextField txtMulta;
    private javax.swing.JTextField txtMultaCalculada;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JFormattedTextField txtValor;
    private javax.swing.JTextField txtValorAtual;
    private javax.swing.JFormattedTextField txtVencimento;
    // End of variables declaration//GEN-END:variables
}
