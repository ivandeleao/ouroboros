/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoSaida.geral;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.KeyStroke;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.dao.principal.VendaDAO;
import static ouroboros.Ouroboros.MAIN_VIEW;
import printing.documento.BonificacoesPorCidadePorVendedorReport;
import printing.documento.VendasDiariasPorVendedorReport;
import printing.documento.VendasFaturamentoPorVendedorPrint;
import printing.documento.VendasProdutosPorCidadeReport;
import printing.documento.VendasProdutosPorVendedorPrint;
import printing.documento.VendasVendedoresPorProdutoPrint;
import util.DateTime;
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
public class DocumentosSaidaRelatorios extends javax.swing.JDialog {
    private final VendaDAO vendaDAO = new VendaDAO();
    
    private DocumentosSaidaRelatorios(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public DocumentosSaidaRelatorios() {
        super(MAIN_VIEW, true);
        initComponents();
        JSwing.startComponentsBehavior(this);
        definirAtalhos();
        
        carregarDados();
        
        carregarTipo();
            
        this.setLocationRelativeTo(this);
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
        txtDataInicial.setText(DateTime.toString(LocalDate.now().minusMonths(1).withDayOfMonth(1)));
        txtDataFinal.setText(DateTime.toString(LocalDate.now().minusMonths(1).withDayOfMonth(LocalDate.now().minusMonths(1).lengthOfMonth())));

        //txtDataInicial.setText(DateTime.toString(LocalDate.now().withDayOfMonth(1)));
        //txtDataFinal.setText(DateTime.toString(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())));
        
        mhcMes.setMonth(LocalDate.now().getMonthValue());
        yrcAno.setYear(LocalDate.now().getYear() - 1);
        cboMeses.setSelectedIndex(11);
    }

    private void carregarTipo() {
        cboTipo.addItem("Vendas - Produtos por Vendedor");
        cboTipo.addItem("Vendas - Vendedores por Produto");
        cboTipo.addItem("Vendas - Faturamento por Período por Vendedor");
        cboTipo.addItem("Vendas - Produtos por Cidade");
        cboTipo.addItem("Vendas - Diárias por Vendedor");
        cboTipo.addItem("Vendas - Bonificações por Cidade por Vendedor");
    }
    
    private void selecionar() {
        switch(cboTipo.getSelectedIndex()) {
            case 0:
            case 1:
            case 2:
                pnlDatas.setVisible(true);
                pnlMeses.setVisible(false);
                txtDataFinal.setEditable(true);
                
                txtDataInicial.setText(DateTime.toString(LocalDate.now().minusMonths(1).withDayOfMonth(1)));
                txtDataFinal.setText(DateTime.toString(LocalDate.now().minusMonths(1).withDayOfMonth(LocalDate.now().minusMonths(1).lengthOfMonth())));

                break;
                
            case 3:
                pnlDatas.setVisible(false);
                pnlMeses.setVisible(true);
                break;
                
            case 4:
                pnlDatas.setVisible(true);
                pnlMeses.setVisible(false);
                txtDataFinal.setEditable(false);
                
                txtDataInicial.setText(DateTime.toString(LocalDate.now()));
                txtDataFinal.setText("");
                
                //txtDataInicial.setText(DateTime.toString(LocalDate.of(2020, 6, 23)));

                break;
                
            case 5:
                pnlDatas.setVisible(true);
                pnlMeses.setVisible(false);
                txtDataFinal.setEditable(true);
                
                txtDataInicial.setText(DateTime.toString(LocalDate.now().minusMonths(1).withDayOfMonth(1)));
                txtDataFinal.setText(DateTime.toString(LocalDate.now().minusMonths(1).withDayOfMonth(LocalDate.now().minusMonths(1).lengthOfMonth())));

                break;
        }
        
    }
    
    private void ok() {
        LocalDate dataInicial = DateTime.fromStringToLocalDate(txtDataInicial.getText());
        LocalDate dataFinal = DateTime.fromStringToLocalDate(txtDataFinal.getText());
        
        //System.out.println("wtf: " + mhcMes.getMonth());
        
        YearMonth anoMes = YearMonth.of(yrcAno.getYear(), mhcMes.getMonth() + 1);
        int meses = Integer.parseInt(cboMeses.getSelectedItem().toString());
        
        //System.out.println("anoMes: " + anoMes);
        
        switch (cboTipo.getSelectedIndex()) {
            case 0:
                dispose();
                VendasProdutosPorVendedorPrint.gerarA4(
                        vendaDAO.findByIntervalo(TipoOperacao.SAIDA, dataInicial.atTime(LocalTime.MIN), dataFinal.atTime(LocalTime.MAX)),
                        dataInicial, 
                        dataFinal
                );
                break;

            case 1:
                dispose();
                VendasVendedoresPorProdutoPrint.gerarA4(
                        vendaDAO.findByIntervalo(TipoOperacao.SAIDA, dataInicial.atTime(LocalTime.MIN), dataFinal.atTime(LocalTime.MAX)),
                        dataInicial, 
                        dataFinal
                );
                break;
                
            case 2:
                dispose();
                VendasFaturamentoPorVendedorPrint.gerarA4(
                        vendaDAO.findByIntervalo(TipoOperacao.SAIDA, dataInicial.atTime(LocalTime.MIN), dataFinal.atTime(LocalTime.MAX)),
                        dataInicial, 
                        dataFinal
                );
                break;
                
            case 3:
                dispose();
                VendasProdutosPorCidadeReport.gerarA4(anoMes, meses);
                break;
                
            case 4:
                dispose();
                VendasDiariasPorVendedorReport.gerarA4(dataInicial);
                break;
                
            case 5:
                dispose();
                BonificacoesPorCidadePorVendedorReport.gerarA4(
                        vendaDAO.findByIntervalo(TipoOperacao.SAIDA, dataInicial.atTime(LocalTime.MIN), dataFinal.atTime(LocalTime.MAX)),
                        dataInicial, 
                        dataFinal
                );
                break;
                
                
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

        btnFechar = new javax.swing.JButton();
        cboTipo = new javax.swing.JComboBox<>();
        btnFechar1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        pnlDatas = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtDataInicial = new javax.swing.JFormattedTextField();
        lblDataFinal = new javax.swing.JLabel();
        txtDataFinal = new javax.swing.JFormattedTextField();
        pnlMeses = new javax.swing.JPanel();
        yrcAno = new com.toedter.calendar.JYearChooser();
        mhcMes = new com.toedter.calendar.JMonthChooser();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cboMeses = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Relatórios");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        btnFechar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnFechar.setText("Fechar");
        btnFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFecharActionPerformed(evt);
            }
        });

        cboTipo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cboTipo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboTipoItemStateChanged(evt);
            }
        });

        btnFechar1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnFechar1.setText("Ok");
        btnFechar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFechar1ActionPerformed(evt);
            }
        });

        jLabel3.setForeground(java.awt.Color.red);
        jLabel3.setText("*Alguns relatórios necessitam de um tempo maior para processamento");

        pnlDatas.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Data Inicial");

        txtDataInicial.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDataInicial.setName("data"); // NOI18N

        lblDataFinal.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblDataFinal.setText("Data Final");

        txtDataFinal.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDataFinal.setName("data"); // NOI18N

        javax.swing.GroupLayout pnlDatasLayout = new javax.swing.GroupLayout(pnlDatas);
        pnlDatas.setLayout(pnlDatasLayout);
        pnlDatasLayout.setHorizontalGroup(
            pnlDatasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblDataFinal)
                .addGap(18, 18, 18)
                .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlDatasLayout.setVerticalGroup(
            pnlDatasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDatasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblDataFinal)
                    .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlMeses.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        yrcAno.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        mhcMes.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        mhcMes.setYearChooser(null);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Início");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Meses");

        cboMeses.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cboMeses.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));

        javax.swing.GroupLayout pnlMesesLayout = new javax.swing.GroupLayout(pnlMeses);
        pnlMeses.setLayout(pnlMesesLayout);
        pnlMesesLayout.setHorizontalGroup(
            pnlMesesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMesesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(mhcMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(yrcAno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(cboMeses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlMesesLayout.setVerticalGroup(
            pnlMesesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMesesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlMesesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMesesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(cboMeses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlMesesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                        .addComponent(mhcMes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(yrcAno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnFechar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnFechar1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cboTipo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3)
                            .addComponent(pnlDatas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlMeses, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlDatas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(pnlMeses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFechar1)
                    .addComponent(btnFechar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    }//GEN-LAST:event_formWindowClosing

    private void btnFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFecharActionPerformed
        dispose();
    }//GEN-LAST:event_btnFecharActionPerformed

    private void btnFechar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFechar1ActionPerformed
        ok();
    }//GEN-LAST:event_btnFechar1ActionPerformed

    private void cboTipoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboTipoItemStateChanged
        selecionar();
    }//GEN-LAST:event_cboTipoItemStateChanged

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
            java.util.logging.Logger.getLogger(DocumentosSaidaRelatorios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DocumentosSaidaRelatorios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DocumentosSaidaRelatorios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DocumentosSaidaRelatorios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DocumentosSaidaRelatorios dialog = new DocumentosSaidaRelatorios(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnFechar;
    private javax.swing.JButton btnFechar1;
    private javax.swing.JComboBox<String> cboMeses;
    private javax.swing.JComboBox<String> cboTipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel lblDataFinal;
    private com.toedter.calendar.JMonthChooser mhcMes;
    private javax.swing.JPanel pnlDatas;
    private javax.swing.JPanel pnlMeses;
    private javax.swing.JFormattedTextField txtDataFinal;
    private javax.swing.JFormattedTextField txtDataInicial;
    private com.toedter.calendar.JYearChooser yrcAno;
    // End of variables declaration//GEN-END:variables
}
