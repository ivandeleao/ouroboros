/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.financeiro;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.temp.CaixaResumoPorMeioDePagamento;
import model.mysql.dao.principal.CaixaDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class CaixaResumoPorMeioDePagamentoView extends javax.swing.JDialog {
    CaixaDAO caixaDAO = new CaixaDAO();
    List<CaixaItem> caixaItens = new ArrayList<>();
    
    /**
     * Creates new form CaixaEncerrarView
     */
    public CaixaResumoPorMeioDePagamentoView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public CaixaResumoPorMeioDePagamentoView(List<CaixaItem> caixaItens) {
        super(MAIN_VIEW, true);
        initComponents();
        
        this.caixaItens = caixaItens;
        
        /*System.out.println("resumo--------------------------------------------");
        for(CaixaItem ci : caixaItens) {
            System.out.println("ci: " + ci.getId() + " credito: " + ci.getCredito());
        }*/
        
        formatarTabela();
        carregarTabela();
        
        this.setLocationRelativeTo(this);
        this.setVisible(true);
    }
    
    private void formatarTabela() {
        tblResumo.setRowHeight(24);
        tblResumo.setIntercellSpacing(new Dimension(10, 10));
        
        tblResumo.getColumn("Crédito").setPreferredWidth(120);
        tblResumo.getColumn("Crédito").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblResumo.getColumn("Débito").setPreferredWidth(120);
        tblResumo.getColumn("Débito").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblResumo.getColumn("Saldo CD").setPreferredWidth(120);
        tblResumo.getColumn("Saldo CD").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblResumo.getColumn("Suprimento").setPreferredWidth(120);
        tblResumo.getColumn("Suprimento").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblResumo.getColumn("Sangria").setPreferredWidth(120);
        tblResumo.getColumn("Sangria").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblResumo.getColumn("Saldo SS").setPreferredWidth(120);
        tblResumo.getColumn("Saldo SS").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblResumo.getColumn("Saldo Final").setPreferredWidth(120);
        tblResumo.getColumn("Saldo Final").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
    }
    
    private void carregarTabela(){
        DefaultTableModel model = (DefaultTableModel) tblResumo.getModel();
        
        for(CaixaResumoPorMeioDePagamento resumo : caixaDAO.getResumoPorMeioDePagamento(caixaItens)) {
            model.addRow(new Object[]{
                resumo.getMeioDePagamento(),
                Decimal.toString(resumo.getCreditoTotal()),
                Decimal.toString(resumo.getDebitoTotal()),
                Decimal.toString(resumo.getSaldoCreditoDebito()),
                Decimal.toString(resumo.getSuprimentoTotal()),
                Decimal.toString(resumo.getSangriaTotal()),
                Decimal.toString(resumo.getSaldoSuprimentoSangria()),
                Decimal.toString(resumo.getSaldoFinal())
            });
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
        tblResumo = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Resumo por Meio de Pagamento");
        setResizable(false);

        tblResumo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Meio de Pagamento", "Crédito", "Débito", "Saldo CD", "Suprimento", "Sangria", "Saldo SS", "Saldo Final"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblResumo.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblResumo);
        if (tblResumo.getColumnModel().getColumnCount() > 0) {
            tblResumo.getColumnModel().getColumn(0).setPreferredWidth(400);
            tblResumo.getColumnModel().getColumn(1).setPreferredWidth(120);
        }

        jLabel1.setForeground(java.awt.Color.blue);
        jLabel1.setText("Saldo SS = Suprimento - Sangria (movimentação de troco)");

        jLabel3.setForeground(java.awt.Color.blue);
        jLabel3.setText("Saldo CD = Crédito - Débito (recebimentos e pagamentos)");

        jLabel4.setForeground(java.awt.Color.blue);
        jLabel4.setText("Saldo Final = Saldo CD + Saldo SS");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 980, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(CaixaResumoPorMeioDePagamentoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CaixaResumoPorMeioDePagamentoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CaixaResumoPorMeioDePagamentoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CaixaResumoPorMeioDePagamentoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CaixaResumoPorMeioDePagamentoView dialog = new CaixaResumoPorMeioDePagamentoView(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblResumo;
    // End of variables declaration//GEN-END:variables
}
