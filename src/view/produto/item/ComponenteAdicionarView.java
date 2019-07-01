/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.produto.item;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.catalogo.ProdutoComponente;
import model.mysql.dao.principal.catalogo.ProdutoComponenteDAO;
import model.mysql.dao.principal.catalogo.ProdutoDAO;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.Decimal;
import util.JSwing;
import view.produto.ProdutoPesquisaView;

/**
 *
 * @author ivand
 *
 * ESTE FOI CRIADO COMO JDIALOG DESDE O INÍCIO - USAR ESTE COMO REFERÊNCIA PARA
 * OS DEMAIS
 *
 *
 */
public class ComponenteAdicionarView extends javax.swing.JDialog {
    Produto produto;
    Produto componente;
    ProdutoComponente produtoComponente = new ProdutoComponente();
    ProdutoComponenteDAO produtoComponenteDAO = new ProdutoComponenteDAO();

    private ComponenteAdicionarView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public ComponenteAdicionarView(Produto produto) {
        super(MAIN_VIEW, true);
        initComponents();
        JSwing.startComponentsBehavior(this);

        this.produto = produto;
        
        System.out.println("produto id: " + produto.getId());

        carregarDados();
        
        definirAtalhos();
        
        this.setLocationRelativeTo(this);
        this.setVisible(true);
    }
    
    private void definirAtalhos() {
        InputMap im = rootPane.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = rootPane.getActionMap();
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exibirComandas");
        am.put("exibirComandas", new FormKeyStroke("ESC"));
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "produtoPesquisaView");
        am.put("produtoPesquisaView", new FormKeyStroke("F1"));
        
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
                case "F1":
                    pesquisarProduto();
                    break;
            }
        }
    }
    
    
    private void pesquisarProduto() {
        ProdutoPesquisaView produtoPesquisaView = new ProdutoPesquisaView();
        componente = produtoPesquisaView.getProduto();
        
        if(produto != null) {
            if(produto.equals(componente)) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Não é permitido incluir um produto como componente dele próprio", "Atenção", JOptionPane.WARNING_MESSAGE);
                txtComponenteId.setText("");
                txtComponenteNome.setText("");
                txtComponenteConteudo.setText("");
                txtUnidade.setText("");
                
            } else if (!componente.hasConteudo()) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "O produto selecionado não possui conteúdo cadastrado", "Atenção", JOptionPane.WARNING_MESSAGE);
                
            } else {
                txtComponenteId.setText(componente.getId().toString());
                txtComponenteNome.setText(componente.getNome());
                txtComponenteConteudo.setText(componente.getConteudoComUnidade());
                txtUnidade.setText(componente.getConteudoUnidade().getNome());
                txtQuantidade.requestFocus();
            }
        }
    }
    


    private void carregarDados() {
        if(componente == null) {
            pesquisarProduto();
        }
    }
    
    private boolean hasComponente() {
        for(ProdutoComponente pc : produto.getListProdutoComponente()) {
            if(pc.getComponente().equals(componente)) {
                return true;
            }
        }
        return false;
    }

    private void confirmar() {
        if(hasComponente()){
            JOptionPane.showMessageDialog(MAIN_VIEW, "Componente já existente", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtComponenteId.requestFocus();
        } else {
            BigDecimal quantidade = Decimal.fromString(txtQuantidade.getText());
            //produtoComponente.setProdutoId(produto.getId());
            //produtoComponente.setComponenteId(componente.getId());
            produtoComponente.setProduto(produto);
            produtoComponente.setComponente(componente);
            produtoComponente.setQuantidade(quantidade);
            produtoComponente = produtoComponenteDAO.save(produtoComponente);
            
            produto.addComponente(produtoComponente);
            
            new ProdutoDAO().save(produto);
            
            dispose();
        }
    }
    
    private void calcularProporcao() {
        BigDecimal quantidade = Decimal.fromString(txtQuantidade.getText());
        BigDecimal proporcao = quantidade.divide(componente.getConteudoQuantidade(), 3, RoundingMode.HALF_UP);
        
        txtProporcao.setText(Decimal.toString(proporcao, 3));
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
        txtComponenteId = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtComponenteNome = new javax.swing.JTextField();
        txtQuantidade = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtComponenteConteudo = new javax.swing.JTextField();
        txtUnidade = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtProporcao = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Adicionar Componente");
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

        txtComponenteId.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtComponenteId.setName(""); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Componente");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Quantidade");

        txtComponenteNome.setEditable(false);
        txtComponenteNome.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtQuantidade.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQuantidade.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtQuantidade.setName("decimal3"); // NOI18N
        txtQuantidade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtQuantidadeKeyReleased(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Conteúdo");

        txtComponenteConteudo.setEditable(false);
        txtComponenteConteudo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtUnidade.setEditable(false);
        txtUnidade.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Proporção");

        txtProporcao.setEditable(false);
        txtProporcao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setForeground(java.awt.Color.blue);
        jLabel5.setText("A proporção é calculada dividindo a quantidade deste produto pelo conteúdo do componente. Ex: 1kg / 50kg = 0,020");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setForeground(java.awt.Color.blue);
        jLabel6.setText("A proporção será usada na movimentação de estoque do componente.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtComponenteId, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                            .addComponent(txtQuantidade))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtUnidade)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(txtProporcao, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtComponenteNome, javax.swing.GroupLayout.PREFERRED_SIZE, 445, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 23, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(txtComponenteConteudo))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtComponenteId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtComponenteNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtComponenteConteudo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOk)
                    .addComponent(btnCancelar)
                    .addComponent(txtUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtProporcao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void txtQuantidadeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeKeyReleased
        calcularProporcao();
    }//GEN-LAST:event_txtQuantidadeKeyReleased

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
            java.util.logging.Logger.getLogger(ComponenteAdicionarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ComponenteAdicionarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ComponenteAdicionarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ComponenteAdicionarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ComponenteAdicionarView dialog = new ComponenteAdicionarView(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField txtComponenteConteudo;
    private javax.swing.JFormattedTextField txtComponenteId;
    private javax.swing.JTextField txtComponenteNome;
    private javax.swing.JTextField txtProporcao;
    private javax.swing.JFormattedTextField txtQuantidade;
    private javax.swing.JTextField txtUnidade;
    // End of variables declaration//GEN-END:variables
}
