/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoSaida;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import model.mysql.bean.principal.documento.Venda;
import model.jtable.RecebimentoListaJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;

/**
 *
 * @author ivand
 */
public class RecebimentoListaView extends javax.swing.JDialog {
    Venda venda;
    RecebimentoListaJTableModel recebimentoListaJTableModel = new RecebimentoListaJTableModel();
    
    
    /**
     * Creates new form RecebimentoListaView
     */
    public RecebimentoListaView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public RecebimentoListaView(java.awt.Frame parent, boolean modal, Venda venda) {
        super(parent, modal);
        initComponents();
        
        this.venda = venda;
        
        //teclas de atalho
        JRootPane rootPane = this.getRootPane();
        InputMap im = rootPane.getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = rootPane.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "fechar");
        am.put("fechar", new RecebimentoListaView.FormKeyStroke("ESC"));
        
        formatarTabela();
        
        carregarTabela();
        
        
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
        recebimentoListaJTableModel.addList(venda.getRecebimentos());
        
        tblRecebimentos.requestFocus();
        tblRecebimentos.setRowSelectionInterval(tblRecebimentos.getRowCount() - 1, tblRecebimentos.getRowCount() -1 );
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
        tblRecebimentos = new javax.swing.JTable();
        btnEncerrarVenda = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Recebimentos");
        setResizable(false);

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

        btnEncerrarVenda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/flag_green.png"))); // NOI18N
        btnEncerrarVenda.setText("ESC FECHAR");
        btnEncerrarVenda.setContentAreaFilled(false);
        btnEncerrarVenda.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEncerrarVenda.setIconTextGap(10);
        btnEncerrarVenda.setPreferredSize(new java.awt.Dimension(180, 49));
        btnEncerrarVenda.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEncerrarVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEncerrarVendaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 980, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnEncerrarVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnEncerrarVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEncerrarVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEncerrarVendaActionPerformed
        dispose();
    }//GEN-LAST:event_btnEncerrarVendaActionPerformed

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
            java.util.logging.Logger.getLogger(RecebimentoListaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RecebimentoListaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RecebimentoListaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RecebimentoListaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RecebimentoListaView dialog = new RecebimentoListaView(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnEncerrarVenda;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblRecebimentos;
    // End of variables declaration//GEN-END:variables
}
