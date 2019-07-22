/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.produto.geral;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.documento.Venda;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import printing.PrintString;
import util.Decimal;
import util.JSwing;
import util.MwString;
import view.Toast;
import printing.Tag48x36Report;

/**
 *
 * @author ivand
 *
 * ESTE FOI CRIADO COMO JDIALOG DESDE O INÍCIO - USAR ESTE COMO REFERÊNCIA PARA
 * OS DEMAIS
 *
 *
 */
public class ProdutoEtiquetaNew extends javax.swing.JDialog {

    Venda compra;

    private ProdutoEtiquetaNew(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public ProdutoEtiquetaNew(Venda compra) {
        super(MAIN_VIEW, true);
        initComponents();
        JSwing.startComponentsBehavior(this);
        
        this.compra = compra;
        
        carregarTipos();
        
        this.setLocationRelativeTo(this);
        this.setVisible(true);
        
    }
    
    private void carregarTipos() {
        cboTipo.addItem("Zebra TLP2844");
        cboTipo.addItem("A4 - Tag 48mm x 36mm");
        cboTipo.addItem("GPrinter G2120 - 50mm x 30mm");
    }


    private void confirmar() {
        
        switch(cboTipo.getSelectedIndex()) {
            case 0:
                zebra();
                break;
            case 1:
                tag48x36();
                break;
            case 2:
                gp2120();
                break;
        }
        
        Toast toast = new Toast("Dados enviados para a impressora...");
        
        dispose();
    }
    
    private void zebra() {
        String etiquetas = "";
        
        for (MovimentoFisico mf : compra.getMovimentosFisicosEntrada()) {
            Produto produto = mf.getProduto();
            
            for(int n = 1; n <= mf.getEntrada().setScale(0, RoundingMode.UP).intValue(); n++) {
                etiquetas += "I8,A,001\n" +
                "\n" +
                "\n" +
                "Q176,024\n" +
                "q831\n" +
                "rN\n" +
                "S4\n" +
                "D7\n" +
                "ZT\n" +
                "JF\n" +
                "OD\n" +
                "R95,0\n" +
                "f100\n" +
                "N\n" +
                "A610,146,2,4,1,1,N,\"" + produto.getNome() + "\"\n" +
                "B610,110,2,1,4,12,48,B,\"" + produto.getCodigo() + "\"\n" +
                "P1\n" +
                "";

                etiquetas = MwString.removeAccents(etiquetas);
            }
            
        }

        PrintString.print(etiquetas, Ouroboros.IMPRESSORA_ETIQUETA);
    }
    
    private void gp2120() {
        String etiquetas = "";
        
        for (MovimentoFisico mf : compra.getMovimentosFisicosEntrada()) {
            Produto produto = mf.getProduto();
            
            for(int n = 1; n <= mf.getEntrada().setScale(0, RoundingMode.UP).intValue(); n++) {
                
                List<String> produtoFatias = MwString.fatiar(produto.getNome(), 21, 3);
                
                String descricao = "";
                int y = 10;
                for(String fatia : produtoFatias) {
                    descricao += "TEXT 0," + y + ",\"3\",0,1,1,\"" + fatia + "\"\r\n";
                    y += 30;
                }
                
                String compraId = "";
                compraId = "C:" + compra.getId();
                
                etiquetas += "SIZE 50 mm,30 mm\r\n" +
                "GAP 2 mm,0 mm\r\n" +
                "SPEED 4\r\n" +
                "DENSITY 15\r\n" +
                "CLS\r\n" +
                //"TEXT 0,10,\"3\",0,1,1,\"" + produto.getNome() + "\"\r\n" +
                descricao +
                "TEXT 0,100,\"4\",0,1,1,\"R$ " + Decimal.toString(produto.getValorVenda()) + "\"\r\n" +
                "TEXT 0,140,\"3\",0,1,1,\"" + produto.getCodigo() + " " + compraId + "\"\r\n" +
                "BARCODE 0,170,\"128\",48,0,0,2,2,\"" + produto.getCodigo() + "\"\r\n" +
                
                "PRINT 1,1\r\n";
                
                

                //etiquetas = MwString.removeAccents(etiquetas);
                System.out.println(etiquetas);
            }
            
        }

        PrintString.printByteArray(etiquetas, Ouroboros.IMPRESSORA_ETIQUETA);
    }
    
    private void tag48x36() {
        List<Produto> produtos = new ArrayList<>();
        
        //multiplicar
        for (MovimentoFisico mf : compra.getMovimentosFisicosEntrada()) {
            Produto produto = mf.getProduto();
            
            for(int n = 1; n <= mf.getEntrada().setScale(0, RoundingMode.UP).intValue(); n++) {
                produtos.add(produto);
            }
        }
        
        Tag48x36Report.gerar(produtos);
        
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
        btnOk = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        cboTipo = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Imprimir Etiquetas");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        btnCancelar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnOk.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnOk.setText("Ok");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Tipo");

        cboTipo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Imprimir etiquetas de todos os itens desta compra com a quantidade de cada");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(cboTipo, 0, 448, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk)
                    .addComponent(btnCancelar))
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleName("Confirmação de Entrega ou Devolução");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    }//GEN-LAST:event_formWindowClosing

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        confirmar();
    }//GEN-LAST:event_btnOkActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

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
            java.util.logging.Logger.getLogger(ProdutoEtiquetaNew.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProdutoEtiquetaNew.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProdutoEtiquetaNew.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProdutoEtiquetaNew.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                ProdutoEtiquetaNew dialog = new ProdutoEtiquetaNew(new javax.swing.JFrame(), true);
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
    private javax.swing.JComboBox<String> cboTipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
}