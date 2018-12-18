/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.caixa;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.bean.principal.Caixa;
import model.bean.principal.CaixaItem;
import model.bean.principal.CaixaItemTipo;
import model.bean.fiscal.MeioDePagamento;
import model.bean.principal.Parcela;
import model.dao.principal.CaixaDAO;
import model.dao.principal.CaixaItemDAO;
import model.dao.fiscal.MeioDePagamentoDAO;
import model.dao.principal.ParcelaDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class CaixaEncerrarView extends javax.swing.JDialog {
    CaixaDAO caixaDAO = new CaixaDAO();
    CaixaItemDAO caixaItemDAO = new CaixaItemDAO();
    ParcelaDAO parcelaDAO = new ParcelaDAO();
    Caixa caixa;
    List<MeioDePagamento> mps;
    /**
     * Creates new form CaixaEncerrarView
     */
    public CaixaEncerrarView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public CaixaEncerrarView(Caixa caixa, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        this.caixa = caixa;
        
        txtAbertura.setText(DateTime.toString(caixa.getCriacao()));
        txtPeriodo.setText(caixa.getCaixaPeriodo().getNome());
        txtSangriaTotal.setText(Decimal.toString(caixaDAO.getSaldo(caixa)));
        
        tblSangria.setRowHeight(24);
        tblSangria.setIntercellSpacing(new Dimension(10, 10));
        //saldo
        tblSangria.getColumnModel().getColumn(1).setPreferredWidth(120);
        tblSangria.getColumnModel().getColumn(1).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        mps = new MeioDePagamentoDAO().findAll();
        
        loadTable();
    }
    
    private void loadTable(){
        DefaultTableModel model = (DefaultTableModel) tblSangria.getModel();
        
        for(MeioDePagamento mp : mps){
            BigDecimal saldo = caixaDAO.getSaldoPorMeioDePagamento(caixa, mp);
            model.addRow(new Object[]{mp.getNome(), Decimal.toString(saldo)});
        }
    }
    
    public Caixa getCaixa(){
        return caixa;
    }
    
    private void encerrar(){
        for(MeioDePagamento mp : mps){
            BigDecimal saldo = caixaDAO.getSaldoPorMeioDePagamento(caixa, mp);
            if(saldo.compareTo(BigDecimal.ZERO) > 0){
                CaixaItem caixaItem = new CaixaItem(caixa, CaixaItemTipo.SANGRIA, mp, null, BigDecimal.ZERO, saldo);
                caixaItemDAO.save(caixaItem);
            } else if(saldo.compareTo(BigDecimal.ZERO) < 0){
                CaixaItem caixaItem = new CaixaItem(caixa, CaixaItemTipo.SANGRIA, mp, null, saldo, BigDecimal.ZERO);
                caixaItemDAO.save(caixaItem);
            }
        }

        caixa.setEncerramento(DateTime.getNow());
        caixa = caixaDAO.save(caixa);

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

        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtAbertura = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtPeriodo = new javax.swing.JTextField();
        txtSangriaTotal = new javax.swing.JTextField();
        btnConfirmar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSangria = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Encerrar Caixa");
        setResizable(false);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Abertura");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Total Sangria");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Após esta operação deve ser aberto um novo caixa antes de realizar novas movimentações");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        txtAbertura.setEditable(false);
        txtAbertura.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Período");

        txtPeriodo.setEditable(false);
        txtPeriodo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtSangriaTotal.setEditable(false);
        txtSangriaTotal.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtSangriaTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSangriaTotal.setText("0,00");

        btnConfirmar.setText("Confirmar");
        btnConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarActionPerformed(evt);
            }
        });

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        tblSangria.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Meio de Pagamento", "Saldo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSangria.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblSangria);
        if (tblSangria.getColumnModel().getColumnCount() > 0) {
            tblSangria.getColumnModel().getColumn(0).setPreferredWidth(400);
            tblSangria.getColumnModel().getColumn(1).setPreferredWidth(120);
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtAbertura, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(txtPeriodo))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtSangriaTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtAbertura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtSangriaTotal)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConfirmar)
                    .addComponent(btnCancelar))
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        encerrar();
    }//GEN-LAST:event_btnConfirmarActionPerformed

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
            java.util.logging.Logger.getLogger(CaixaEncerrarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CaixaEncerrarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CaixaEncerrarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CaixaEncerrarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CaixaEncerrarView dialog = new CaixaEncerrarView(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblSangria;
    private javax.swing.JTextField txtAbertura;
    private javax.swing.JTextField txtPeriodo;
    private javax.swing.JTextField txtSangriaTotal;
    // End of variables declaration//GEN-END:variables
}
