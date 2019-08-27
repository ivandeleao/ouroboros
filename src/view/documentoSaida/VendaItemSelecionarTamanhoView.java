/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoSaida;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import model.jtable.catalogo.ProdutoTamanhoJTableModel;
import model.mysql.bean.fiscal.UnidadeComercial;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.MovimentoFisicoTipo;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.catalogo.ProdutoTamanho;
import model.mysql.bean.principal.catalogo.ProdutoTipo;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.documento.VendaTipo;
import model.mysql.dao.principal.MovimentoFisicoDAO;
import model.mysql.dao.principal.VendaDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.Decimal;

/**
 *
 * @author ivand
 *
 * ESTE FOI CRIADO COMO JDIALOG DESDE O INÍCIO - USAR ESTE COMO REFERÊNCIA PARA
 * OS DEMAIS
 *
 *
 */
public class VendaItemSelecionarTamanhoView extends javax.swing.JDialog {

    private Venda documento;
    Produto produto;
    ProdutoTamanhoJTableModel produtoTamanhoJTableModel = new ProdutoTamanhoJTableModel();
    ProdutoTamanho produtoTamanho;

    MovimentoFisicoDAO movimentoFisicoDAO = new MovimentoFisicoDAO();
    VendaDAO vendaDAO = new VendaDAO();

    /**
     * Creates new form ParcelamentoView
     */
    private VendaItemSelecionarTamanhoView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public VendaItemSelecionarTamanhoView(Venda documento, Produto produto) {
        super(MAIN_VIEW, true);
        initComponents();

        definirAtalhos();

        this.documento = documento;
        this.produto = produto;

        txtProduto.setText(produto.getNome());
        txtQuantidade.setText("1,000");
        
        if(!produto.isMontavel()) {
            btnMontar.setEnabled(false);
        }
        
        txtQuantidade.requestFocus();

        formatarTabela();

        carregarTabela();

        this.setLocationRelativeTo(this);
        this.setVisible(true);
    }

    public ProdutoTamanho getProdutoTamanho() {
        return produtoTamanho;
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

    private void formatarTabela() {
        tblTamanho.setModel(produtoTamanhoJTableModel);

        tblTamanho.setRowHeight(30);
        tblTamanho.setIntercellSpacing(new Dimension(10, 10));

        tblTamanho.getColumn("Tamanho").setPreferredWidth(100);

        tblTamanho.getColumn("Valor").setPreferredWidth(60);
        tblTamanho.getColumn("Valor").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

    }

    private void carregarTabela() {
        produtoTamanhoJTableModel.clear();
        produtoTamanhoJTableModel.addList(produto.getProdutoTamanhos());

        if (tblTamanho.getRowCount() > 0) {
            tblTamanho.setRowSelectionInterval(0, 0);
        }
    }

    private void confirmar() {
        produtoTamanho = produtoTamanhoJTableModel.getRow(tblTamanho.getSelectedRow());
        BigDecimal quantidade = Decimal.fromString(txtQuantidade.getText());
        
        if(quantidade.compareTo(BigDecimal.ZERO) < 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Quantidade inválida", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtQuantidade.requestFocus();
            
        } else {
        
            UnidadeComercial unidadeComercialVenda = produto.getUnidadeComercialVenda();

            MovimentoFisico movimentoFisico = new MovimentoFisico(null,
                    produto.getCodigo(), //código
                    produto.getNome(), //descrição
                    produto.getProdutoTipo(), //tipo
                    BigDecimal.ZERO, //entrada
                    quantidade, //saída
                    produtoTamanho.getValorVenda(), //valor
                    BigDecimal.ZERO, //desconto
                    produto.getUnidadeComercialVenda(),
                    MovimentoFisicoTipo.VENDA,
                    null);
            
            movimentoFisico.setTamanho(produtoTamanho.getTamanho());

            if (documento.getVendaTipo().equals(VendaTipo.VENDA)
                    || documento.getVendaTipo().equals(VendaTipo.ORDEM_DE_SERVICO)
                    || documento.getVendaTipo().equals(VendaTipo.COMANDA)) {

                //adicionar parametro de sistema
                movimentoFisico.setDataSaida(LocalDateTime.now());
                //
            }

            movimentoFisico = movimentoFisicoDAO.save(movimentoFisico);
            documento.addMovimentoFisico(movimentoFisico);

            documento = vendaDAO.save(documento);

            dispose();
        }
    }

    private void montar() {
        
        produtoTamanho = produtoTamanhoJTableModel.getRow(tblTamanho.getSelectedRow());
        
        VendaMontarItemView montarItem = new VendaMontarItemView(documento, produtoTamanho);
        
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

        btnCancelar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTamanho = new javax.swing.JTable();
        pnlQuantidade = new javax.swing.JPanel();
        txtQuantidade = new javax.swing.JFormattedTextField();
        jLabel17 = new javax.swing.JLabel();
        btnInteiro = new javax.swing.JButton();
        btnMontar = new javax.swing.JButton();
        txtProduto = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Selecionar tamanho");
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

        tblTamanho.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tblTamanho.setModel(new javax.swing.table.DefaultTableModel(
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
        tblTamanho.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblTamanhoFocusGained(evt);
            }
        });
        tblTamanho.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTamanhoMouseClicked(evt);
            }
        });
        tblTamanho.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblTamanhoKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblTamanho);

        pnlQuantidade.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtQuantidade.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQuantidade.setText("0,000");
        txtQuantidade.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtQuantidade.setName("decimal(3)"); // NOI18N
        txtQuantidade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtQuantidadeKeyReleased(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel17.setText("Quantidade");

        btnInteiro.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnInteiro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-sphere-20.png"))); // NOI18N
        btnInteiro.setText("Inteiro");
        btnInteiro.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnInteiro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInteiroActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlQuantidadeLayout = new javax.swing.GroupLayout(pnlQuantidade);
        pnlQuantidade.setLayout(pnlQuantidadeLayout);
        pnlQuantidadeLayout.setHorizontalGroup(
            pnlQuantidadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQuantidadeLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel17)
                .addGap(18, 18, 18)
                .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnInteiro, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlQuantidadeLayout.setVerticalGroup(
            pnlQuantidadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQuantidadeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlQuantidadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(btnInteiro))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnMontar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnMontar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-fraction-20.png"))); // NOI18N
        btnMontar.setText("Montar");
        btnMontar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMontar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMontarActionPerformed(evt);
            }
        });

        txtProduto.setEditable(false);
        txtProduto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtProduto.setFocusable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtProduto, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(pnlQuantidade, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(189, 189, 189)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnMontar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar)
                    .addComponent(btnMontar))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    }//GEN-LAST:event_formWindowClosing

    private void btnInteiroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInteiroActionPerformed
        confirmar();
    }//GEN-LAST:event_btnInteiroActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void tblTamanhoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblTamanhoFocusGained
        txtQuantidade.requestFocus();
    }//GEN-LAST:event_tblTamanhoFocusGained

    private void tblTamanhoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTamanhoMouseClicked
        if (evt.getClickCount() == 2) {
            confirmar();
        }
    }//GEN-LAST:event_tblTamanhoMouseClicked

    private void tblTamanhoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblTamanhoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tblTamanhoKeyReleased

    private void txtQuantidadeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeKeyReleased
        int index;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                confirmar();
                break;
            case KeyEvent.VK_DOWN:
                index = tblTamanho.getSelectedRow() + 1;
                if (index < tblTamanho.getRowCount()) {
                    tblTamanho.setRowSelectionInterval(index, index);
                    tblTamanho.scrollRectToVisible(tblTamanho.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_UP:
                index = tblTamanho.getSelectedRow() - 1;
                if (index > -1) {
                    tblTamanho.setRowSelectionInterval(index, index);
                    tblTamanho.scrollRectToVisible(tblTamanho.getCellRect(index, 0, true));
                }
                break;
                
        }
    }//GEN-LAST:event_txtQuantidadeKeyReleased

    private void btnMontarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMontarActionPerformed
        montar();
    }//GEN-LAST:event_btnMontarActionPerformed

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
            java.util.logging.Logger.getLogger(VendaItemSelecionarTamanhoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VendaItemSelecionarTamanhoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VendaItemSelecionarTamanhoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VendaItemSelecionarTamanhoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                VendaItemSelecionarTamanhoView dialog = new VendaItemSelecionarTamanhoView(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnInteiro;
    private javax.swing.JButton btnMontar;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlQuantidade;
    private javax.swing.JTable tblTamanho;
    private javax.swing.JTextField txtProduto;
    private javax.swing.JFormattedTextField txtQuantidade;
    // End of variables declaration//GEN-END:variables
}
