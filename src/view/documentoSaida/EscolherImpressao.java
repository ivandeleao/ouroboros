/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoSaida;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.dao.principal.ParcelaDAO;
import model.mysql.dao.fiscal.MeioDePagamentoDAO;
import model.mysql.dao.principal.VendaDAO;
import model.jtable.ParcelamentoJTableModel;
import model.nosql.ImpressoraFormatoEnum;
import model.mysql.bean.principal.documento.VendaTipo;
import static ouroboros.Ouroboros.IMPRESSORA_A4;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.TO_PRINTER_PATH;
import static ouroboros.Ouroboros.em;
import printing.CriarPdfA4;
import printing.PrintPDFBox;
import view.Toast;
import static ouroboros.Ouroboros.IMPRESSORA_CUPOM;
import static ouroboros.Ouroboros.IMPRESSORA_FORMATO_PADRAO;
import printing.TermicaPrint;
import printing.DocumentoSaidaPrint;
import printing.RelatorioPdf;
import printing.TicketCozinhaPrint;
import sat.MwSat;
import view.sat.SatEmitirCupomView;

/**
 *
 * @author ivand
 *
 * ESTE FOI CRIADO COMO JDIALOG DESDE O INÍCIO - USAR ESTE COMO REFERÊNCIA PARA
 * OS DEMAIS
 *
 *
 */
public class EscolherImpressao extends javax.swing.JDialog {

    private Venda venda;
    PrintPDFBox pPDF = new PrintPDFBox();

    public EscolherImpressao(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public EscolherImpressao(Venda venda) {
        super(MAIN_VIEW, true);
        initComponents();

        this.venda = venda;

        definirAtalhos();
                
        this.setLocationRelativeTo(this);
        this.setVisible(true);
    }

    private void definirAtalhos() {
        InputMap im = rootPane.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = rootPane.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exibirComandas");
        am.put("exibirComandas", new FormKeyStroke("ESC"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), "imprimir");
        am.put("imprimir", new FormKeyStroke("F10"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, KeyEvent.CTRL_DOWN_MASK), "imprimirTicketComanda");
        am.put("imprimirTicketComanda", new FormKeyStroke("CtrlF10"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "cfe");
        am.put("cfe", new FormKeyStroke("F11"));
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, KeyEvent.CTRL_DOWN_MASK), "imprimirTicketCozinha");
        am.put("imprimirTicketCozinha", new FormKeyStroke("CtrlF11"));

    }

    protected class FormKeyStroke extends AbstractAction {

        private final String key;

        public FormKeyStroke(String key) {
            this.key = key;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
            switch (key) {
                case "ESC":
                    dispose();
                    break;
                case "F10":
                    imprimir();
                    break;
                case "CtrlF10":
                    imprimirTicketComanda();
                    break;
                case "F11":
                    gerarCupomSat();
                    break;
                case "CtrlF11":
                    imprimirTicketCozinha();
                    break;
            }
        }
    }
    
    public void imprimir() {

        PrintPDFBox pPDF = new PrintPDFBox();
        //VENDA, PEDIDO, COMANDA, ORDEM_DE_SERVICO, LOCAÇÃO
        
        if (venda.getVendaTipo().equals(VendaTipo.ORDEM_DE_SERVICO)) {
            DocumentoSaidaPrint.gerarA4(venda);

        } else if (venda.getVendaTipo().equals(VendaTipo.LOCAÇÃO)) {
            RelatorioPdf.gerarLocacaoOS(venda);

        } else if (venda.getVendaTipo().equals(VendaTipo.VENDA)) {
            if (IMPRESSORA_FORMATO_PADRAO.equals(ImpressoraFormatoEnum.CUPOM_80.toString())
                    || IMPRESSORA_FORMATO_PADRAO.equals(ImpressoraFormatoEnum.CUPOM_58.toString())) {
                String pdfFilePath = TO_PRINTER_PATH + "VENDA " + venda.getId() + "_" + System.currentTimeMillis() + ".pdf";
                TermicaPrint.gerarVenda(venda, pdfFilePath);
                pPDF.print(pdfFilePath, IMPRESSORA_A4);
            } else {
                //pPDF.print(new CriarPdfA4().gerarOrdemDeServico(venda), IMPRESSORA_A4);
                DocumentoSaidaPrint.gerarA4(venda);
            }

        } else if (venda.getVendaTipo().equals(VendaTipo.PEDIDO)) {
            if (IMPRESSORA_FORMATO_PADRAO.equals(ImpressoraFormatoEnum.CUPOM_80.toString())) {
                String pdfFilePath = TO_PRINTER_PATH + "PEDIDO " + venda.getId() + "_" + System.currentTimeMillis() + ".pdf";
                TermicaPrint.gerarVenda(venda, pdfFilePath);
                pPDF.print(pdfFilePath, IMPRESSORA_A4);
            } else {
                //pPDF.print(new CriarPdfA4().gerarOrdemDeServico(venda), IMPRESSORA_A4);
                DocumentoSaidaPrint.gerarA4(venda);
            }

        } else {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Tipo não programado");
            
            
        }

        
    }
    

    private void imprimirTicketComanda() {
        String pdfFilePath = TO_PRINTER_PATH + "TICKET_COMANDA " + venda.getId() + "_" + System.currentTimeMillis() + ".pdf";
        TermicaPrint.gerarTicketComanda(venda, pdfFilePath);

        PrintPDFBox pPDF = new PrintPDFBox();
        pPDF.print(pdfFilePath, IMPRESSORA_CUPOM);
        
    }
    
    private void imprimirTicketCozinha() {
        TicketCozinhaPrint.imprimirCupom(venda);
        
    }
    
    private void gerarCupomSat() {
        if (validarCupomSat()) {
            SatEmitirCupomView satCpf = new SatEmitirCupomView(venda);
            
        }
    }

    private boolean validarCupomSat() {
        List<String> erros = MwSat.validar(venda);

        if (!erros.isEmpty()) {
            String mensagem = String.join("\r\n", erros);
            JOptionPane.showMessageDialog(MAIN_VIEW, mensagem, "Erro ao validar a venda. Verifique os erros:", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnTicketCozinha = new javax.swing.JButton();
        btnTicketComanda = new javax.swing.JButton();
        btnCupomSat = new javax.swing.JButton();
        btnCupomSat1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Escolher Impressão");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        btnTicketCozinha.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnTicketCozinha.setText("Ctrl+F11 Ticket Cozinha");
        btnTicketCozinha.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnTicketCozinha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTicketCozinhaActionPerformed(evt);
            }
        });

        btnTicketComanda.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnTicketComanda.setText("Ctrl+F10 Ticket Comanda");
        btnTicketComanda.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnTicketComanda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTicketComandaActionPerformed(evt);
            }
        });

        btnCupomSat.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnCupomSat.setText("F11 Cupom Sat");
        btnCupomSat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCupomSat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCupomSatActionPerformed(evt);
            }
        });

        btnCupomSat1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnCupomSat1.setText("F10 Padrão");
        btnCupomSat1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCupomSat1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCupomSat1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnTicketComanda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCupomSat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCupomSat1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTicketCozinha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCupomSat1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTicketComanda, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCupomSat, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTicketCozinha, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    }//GEN-LAST:event_formWindowClosing

    private void btnTicketCozinhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTicketCozinhaActionPerformed
        imprimirTicketCozinha();
    }//GEN-LAST:event_btnTicketCozinhaActionPerformed

    private void btnTicketComandaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTicketComandaActionPerformed
        imprimirTicketComanda();
    }//GEN-LAST:event_btnTicketComandaActionPerformed

    private void btnCupomSatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCupomSatActionPerformed
        gerarCupomSat();
    }//GEN-LAST:event_btnCupomSatActionPerformed

    private void btnCupomSat1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCupomSat1ActionPerformed
        dispose();
        imprimir();
    }//GEN-LAST:event_btnCupomSat1ActionPerformed

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
            java.util.logging.Logger.getLogger(EscolherImpressao.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EscolherImpressao.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EscolherImpressao.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EscolherImpressao.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                EscolherImpressao dialog = new EscolherImpressao(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnCupomSat;
    private javax.swing.JButton btnCupomSat1;
    private javax.swing.JButton btnTicketComanda;
    private javax.swing.JButton btnTicketCozinha;
    // End of variables declaration//GEN-END:variables
}
