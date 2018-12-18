/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.venda;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import model.bean.principal.Caixa;
import model.bean.fiscal.MeioDePagamento;
import model.bean.principal.Parcela;
import model.bean.principal.CaixaItem;
import model.bean.principal.CaixaItemTipo;
import model.bean.principal.Venda;
import model.dao.principal.CaixaDAO;
import model.dao.fiscal.MeioDePagamentoDAO;
import model.dao.principal.ParcelaDAO;
import model.dao.principal.CaixaItemDAO;
import model.dao.principal.VendaDAO;
import static ouroboros.Ouroboros.IMPRESSORA_PADRAO;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.PARCELA_JUROS_MONETARIO_MENSAL;
import static ouroboros.Ouroboros.PARCELA_JUROS_PERCENTUAL_MENSAL;
import static ouroboros.Ouroboros.PARCELA_MULTA;
import static ouroboros.Ouroboros.TO_PRINTER_PATH;
import printing.CriarPDF;
import printing.PrintPDFBox;
import util.Decimal;
import util.JSwing;
import view.Toast;

/**
 *
 * @author ivand
 */
public class RecebimentoView extends javax.swing.JDialog {
    Venda venda;
    Caixa caixa = new CaixaDAO().getLastCaixa();
    Parcela parcela = new Parcela();
    CaixaItemDAO recebimentoDAO = new CaixaItemDAO();
    List<JFormattedTextField> txtRecebimentoList = new ArrayList<>();
    
    BigDecimal totalRecebido = new BigDecimal(BigInteger.ZERO);
    
    public RecebimentoView(java.awt.Frame parent, boolean modal, Venda venda) {
        super(MAIN_VIEW, modal);
        initComponents();
    }
    
    public RecebimentoView(Venda venda) {
        super(MAIN_VIEW, true);
        initComponents();
        
        definirAtalhos();
        
        gerarCamposDePagamento();
        
        
        JSwing.startComponentsBehavior(this);
        
        this.venda = venda;
        
        txtTotal.setText(Decimal.toString(venda.getTotal()));
        txtEmAberto.setText(Decimal.toString(venda.getTotalEmAberto()));
        
        
        
        this.setLocationRelativeTo(this);
        this.setVisible(true);
        
        
    }
    
    public Parcela getParcela(){
        return parcela;
    }
    
    private void definirAtalhos() {
        //JRootPane rootPane = this.getRootPane();
        InputMap im = rootPane.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = rootPane.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "fechar");
        am.put("fechar", new RecebimentoView.FormKeyStroke("ESC"));
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "confirmarComRecibo");
        am.put("confirmarComRecibo", new RecebimentoView.FormKeyStroke("F11"));
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "confirmar");
        am.put("confirmar", new RecebimentoView.FormKeyStroke("F12"));
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
                case "F11":
                    confirmar(true);
                    break;
                case "F12":
                    confirmar(false);
                    break;
            }
        }
    }
    
    KeyListener keyListenerRecebido = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {
            //do nothing
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                dispose();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            somarPagamentos();
        }

    };
    
    private void proximoCampo() {
        if(totalRecebido.compareTo(venda.getTotalEmAberto()) >= 0) {
            confirmar(false);
        }
        System.out.println("recebido: " + totalRecebido);
    }
    
    
    private void gerarCamposDePagamento(){
        MeioDePagamentoDAO mpDAO = new MeioDePagamentoDAO();
        List<MeioDePagamento> mps = mpDAO.findAllEnabled();
        
        int x = 0;
        int y = 0;
        
        int width = 500;
        int height = 52;
        
        for(MeioDePagamento mp : mps){
            
            JLabel label = new JLabel(mp.getNome());
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setPreferredSize(new Dimension(150, 30));
            //label.setBorder(BorderFactory.createEtchedBorder());
            Font labelFont = new Font("Tahoma", Font.BOLD, 14);
            label.setFont(labelFont);
            
            JFormattedTextField text = new JFormattedTextField();
            text.setName("decimal");
            text.setToolTipText(mp.getId().toString()); //usado ao gravar os pagamentos
            text.setPreferredSize(new Dimension(300, 48));
            Font font = new Font("Tahoma", Font.BOLD, 36);
            text.setFont(font);
            text.setText("0");
            txtRecebimentoList.add(text);
            
            

            text.addKeyListener(keyListenerRecebido);
            
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT)); //Alinhar elementos a esquerda
            //panel.setBorder(BorderFactory.createEtchedBorder());
            panel.setBounds(5, 2 + y * height, width, height);
            panel.add(label);
            panel.add(text);
            
            pnlMPs.add(panel);

            y++;

        }
        
        txtRecebimentoList.get(0).requestFocus();
    }
    
    private void somarPagamentos(){
        totalRecebido = BigDecimal.ZERO;
        for(JFormattedTextField txtRecebimento : txtRecebimentoList){
            BigDecimal valorRecebido = Decimal.fromString(txtRecebimento.getText());
            
            
            totalRecebido = totalRecebido.add(valorRecebido);
        }
        txtTotalRecebido.setText(Decimal.toString(totalRecebido));
        
        BigDecimal troco = totalRecebido.subtract(venda.getTotalEmAberto());
        
        txtTroco.setText(Decimal.toString(troco));
    }
    
    
    private void confirmar(boolean imprimir){
        somarPagamentos();
        if(totalRecebido.compareTo(BigDecimal.ZERO) > 0 ) {
            
            parcela = new Parcela(null, BigDecimal.ZERO, PARCELA_MULTA, PARCELA_JUROS_MONETARIO_MENSAL, PARCELA_JUROS_PERCENTUAL_MENSAL, null);

            //BigDecimal totalRecebido = BigDecimal.ZERO;
            for(JFormattedTextField txtRecebimento : txtRecebimentoList){
                BigDecimal valorRecebido = Decimal.fromString(txtRecebimento.getText());
                if(valorRecebido.compareTo(BigDecimal.ZERO) > 0){
                    //totalRecebido = totalRecebido.add(valorRecebido);

                    MeioDePagamento mp = new MeioDePagamentoDAO().findById(Integer.valueOf(txtRecebimento.getToolTipText()));

                    CaixaItem r = new CaixaItem(caixa, CaixaItemTipo.RECEBIMENTO_DE_VENDA, mp, null, valorRecebido, BigDecimal.ZERO);

                    r = recebimentoDAO.save(r);
                    //recebimentos.add(r);
                    parcela.addRecebimento(r);
                }
            }
            
            parcela = new ParcelaDAO().save(parcela); //tem que salvar antes para conseguir calcular o saldo na sequência

            BigDecimal troco = Decimal.fromString(txtTroco.getText());
            if(troco.compareTo(BigDecimal.ZERO) > 0){
                totalRecebido = totalRecebido.subtract(troco);

                CaixaItem r = new CaixaItem(caixa, CaixaItemTipo.TROCO_DE_VENDA, MeioDePagamento.DINHEIRO, null, BigDecimal.ZERO, troco);

                r = recebimentoDAO.save(r);
                //recebimentos.add(r);
                parcela.addRecebimento(r);
            }
            /*
            foi corrigido recebimento colocando em cascadeType All a relação do recebimento com a parcela
                    adicionando os recebimentos na mesma
                            adicionando a parcela na venda
                                    e finalmente salvando a venda!!!
            */

            //parcela.setRecebimentos(recebimentos);
            parcela.setValor(totalRecebido);
            parcela = new ParcelaDAO().save(parcela); //sem o save, ao imprimir, a data de criação estava nula

            venda.addParcela(parcela);

            venda = new VendaDAO().save(venda);

            
            
            if(imprimir) {
                imprimir();
            }
            
         
            dispose();
        }
        
    }
    
    private void imprimir() {
        String pdfFilePath = TO_PRINTER_PATH + "RECIBO DE PAGAMENTO_" + System.currentTimeMillis() + ".pdf";
        List<Parcela> parcelaList = new ArrayList<>();
        parcelaList.add(parcela);
        //System.out.println("parcela: " + parcela.getValor());
        CriarPDF.criarRecibo80mm(parcelaList, pdfFilePath);

        new Toast("Imprimindo...");

        PrintPDFBox pPDF = new PrintPDFBox();
        pPDF.print(pdfFilePath, IMPRESSORA_PADRAO);
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMPs = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtEmAberto = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txtTotalRecebido = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        txtTroco = new javax.swing.JFormattedTextField();
        txtTotal = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        btnConfirmarComRecibo = new javax.swing.JButton();
        btnConfirmar1 = new javax.swing.JButton();
        btnConfirmar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Recebimento");

        pnlMPs.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout pnlMPsLayout = new javax.swing.GroupLayout(pnlMPs);
        pnlMPs.setLayout(pnlMPsLayout);
        pnlMPsLayout.setHorizontalGroup(
            pnlMPsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 668, Short.MAX_VALUE)
        );
        pnlMPsLayout.setVerticalGroup(
            pnlMPsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 575, Short.MAX_VALUE)
        );

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel7.setText("EM ABERTO");

        txtEmAberto.setEditable(false);
        txtEmAberto.setForeground(java.awt.Color.red);
        txtEmAberto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtEmAberto.setText("0,00");
        txtEmAberto.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel8.setText("RECEBIDO");

        txtTotalRecebido.setEditable(false);
        txtTotalRecebido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalRecebido.setText("0,00");
        txtTotalRecebido.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel9.setText("TOTAL");

        txtTroco.setEditable(false);
        txtTroco.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTroco.setText("0,00");
        txtTroco.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N

        txtTotal.setEditable(false);
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotal.setText("0,00");
        txtTotal.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel11.setText("TROCO");

        btnConfirmarComRecibo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/printer.png"))); // NOI18N
        btnConfirmarComRecibo.setText("F11  RECIBO");
        btnConfirmarComRecibo.setContentAreaFilled(false);
        btnConfirmarComRecibo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnConfirmarComRecibo.setIconTextGap(10);
        btnConfirmarComRecibo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnConfirmarComRecibo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarComReciboActionPerformed(evt);
            }
        });

        btnConfirmar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/flag_red.png"))); // NOI18N
        btnConfirmar1.setText("ESC CANCELAR");
        btnConfirmar1.setContentAreaFilled(false);
        btnConfirmar1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnConfirmar1.setIconTextGap(10);
        btnConfirmar1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnConfirmar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmar1ActionPerformed(evt);
            }
        });

        btnConfirmar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/flag_green.png"))); // NOI18N
        btnConfirmar.setText("F12 CONFIRMAR");
        btnConfirmar.setContentAreaFilled(false);
        btnConfirmar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnConfirmar.setIconTextGap(10);
        btnConfirmar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlMPs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTotalRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTroco, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnConfirmar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtEmAberto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnConfirmarComRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnConfirmar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlMPs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtEmAberto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(204, 204, 204)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTotalRecebido)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTroco)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnConfirmarComRecibo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnConfirmar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnConfirmar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmarComReciboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarComReciboActionPerformed
        confirmar(true);
    }//GEN-LAST:event_btnConfirmarComReciboActionPerformed

    private void btnConfirmar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmar1ActionPerformed
        dispose();
    }//GEN-LAST:event_btnConfirmar1ActionPerformed

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        confirmar(false);
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
            java.util.logging.Logger.getLogger(RecebimentoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RecebimentoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RecebimentoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RecebimentoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RecebimentoView dialog = new RecebimentoView(new javax.swing.JFrame(), true, null);
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
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnConfirmar1;
    private javax.swing.JButton btnConfirmarComRecibo;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel pnlMPs;
    private javax.swing.JFormattedTextField txtEmAberto;
    private javax.swing.JFormattedTextField txtTotal;
    private javax.swing.JFormattedTextField txtTotalRecebido;
    private javax.swing.JFormattedTextField txtTroco;
    // End of variables declaration//GEN-END:variables
}
