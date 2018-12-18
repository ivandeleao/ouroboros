/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.math.BigDecimal;
import java.util.List;
import view.venda.ComandasView;
import view.caixa.CaixaView;
import view.produto.geral.ProdutoListaView;
import view.venda.geral.VendaListaView;
import view.venda.VendaView;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import model.bean.fiscal.UnidadeComercial;
import model.bean.principal.MovimentoFisico;
import model.bean.principal.MovimentoFisicoTipo;
import model.bean.principal.Produto;
import model.bean.principal.Venda;
import model.dao.principal.MovimentoFisicoDAO;
import model.dao.principal.ProdutoDAO;
import model.dao.principal.VendaDAO;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

import static ouroboros.Ouroboros.MENU_MAX_WIDTH;
import static ouroboros.Ouroboros.MENU_MIN_WIDTH;
import static ouroboros.Ouroboros.SCREEN_WIDTH;
import static ouroboros.Ouroboros.SCREEN_HEIGHT;
import view.sistema.ConfguracaoSistema;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.em;
import view.caixa.FinanceiroContainerView;
import view.cliente.PessoaListaView;
import view.produto.geral.ProdutoGeralContainerView;
import view.sistema.BackupView;
import view.usuario.UsuarioListaView;
import view.venda.geral.VendaGeralContainerView;

/**
 *
 * @author ivand
 */
public class MainMenuView extends javax.swing.JInternalFrame {
    private static int counter = 0;
    //private static final int DELAY = 100000000;
    
    
    
    /**
     * Creates new form MainMenuView
     */
    public MainMenuView() {
        //set apearence
        ((BasicInternalFrameUI)this.getUI()).setNorthPane(null);
        this.setBorder(null);
        this.setBounds(0, 0, MENU_MIN_WIDTH, SCREEN_HEIGHT);
        
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnVenda = new javax.swing.JButton();
        btnMesas = new javax.swing.JButton();
        btnVendaLista = new javax.swing.JButton();
        btnProdutos1 = new javax.swing.JButton();
        btnFinanceiro = new javax.swing.JButton();
        btnSistema = new javax.swing.JButton();
        btnPessoas = new javax.swing.JButton();
        btnUsuarios = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnBackup = new javax.swing.JButton();
        btnOrcamento = new javax.swing.JButton();

        setBackground(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow"));
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                formMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                formMouseExited(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        btnVenda.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnVenda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/add.png"))); // NOI18N
        btnVenda.setText("Nova Venda");
        btnVenda.setContentAreaFilled(false);
        btnVenda.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnVenda.setIconTextGap(20);
        btnVenda.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnVendaMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnVendaMouseExited(evt);
            }
        });
        btnVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVendaActionPerformed(evt);
            }
        });

        btnMesas.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnMesas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/color_swatch.png"))); // NOI18N
        btnMesas.setText("Comandas");
        btnMesas.setContentAreaFilled(false);
        btnMesas.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMesas.setIconTextGap(20);
        btnMesas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnMesasMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnMesasMouseExited(evt);
            }
        });
        btnMesas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMesasActionPerformed(evt);
            }
        });

        btnVendaLista.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnVendaLista.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/resource/img/money.png"))); // NOI18N
        btnVendaLista.setText("Vendas");
        btnVendaLista.setContentAreaFilled(false);
        btnVendaLista.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnVendaLista.setIconTextGap(20);
        btnVendaLista.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnVendaListaMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnVendaListaMouseExited(evt);
            }
        });
        btnVendaLista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVendaListaActionPerformed(evt);
            }
        });

        btnProdutos1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnProdutos1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/package.png"))); // NOI18N
        btnProdutos1.setText("Produtos");
        btnProdutos1.setContentAreaFilled(false);
        btnProdutos1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnProdutos1.setIconTextGap(20);
        btnProdutos1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnProdutos1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnProdutos1MouseExited(evt);
            }
        });
        btnProdutos1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProdutos1ActionPerformed(evt);
            }
        });

        btnFinanceiro.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnFinanceiro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/creditcards.png"))); // NOI18N
        btnFinanceiro.setText("Financeiro");
        btnFinanceiro.setContentAreaFilled(false);
        btnFinanceiro.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFinanceiro.setIconTextGap(20);
        btnFinanceiro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnFinanceiroMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnFinanceiroMouseExited(evt);
            }
        });
        btnFinanceiro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinanceiroActionPerformed(evt);
            }
        });

        btnSistema.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnSistema.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/cog.png"))); // NOI18N
        btnSistema.setText("Sistema");
        btnSistema.setContentAreaFilled(false);
        btnSistema.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSistema.setIconTextGap(20);
        btnSistema.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSistemaMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSistemaMouseExited(evt);
            }
        });
        btnSistema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSistemaActionPerformed(evt);
            }
        });

        btnPessoas.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnPessoas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/user.png"))); // NOI18N
        btnPessoas.setText("Pessoas");
        btnPessoas.setContentAreaFilled(false);
        btnPessoas.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPessoas.setIconTextGap(20);
        btnPessoas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnPessoasMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnPessoasMouseExited(evt);
            }
        });
        btnPessoas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPessoasActionPerformed(evt);
            }
        });

        btnUsuarios.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnUsuarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/key.png"))); // NOI18N
        btnUsuarios.setText("Usuários");
        btnUsuarios.setContentAreaFilled(false);
        btnUsuarios.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUsuarios.setIconTextGap(20);
        btnUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnUsuariosMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnUsuariosMouseExited(evt);
            }
        });
        btnUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUsuariosActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setForeground(javax.swing.UIManager.getDefaults().getColor("Button.highlight"));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Mindware (19) 3813.2888");

        btnBackup.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnBackup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/disk.png"))); // NOI18N
        btnBackup.setText("Backup");
        btnBackup.setContentAreaFilled(false);
        btnBackup.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnBackup.setIconTextGap(20);
        btnBackup.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnBackupMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnBackupMouseExited(evt);
            }
        });
        btnBackup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackupActionPerformed(evt);
            }
        });

        btnOrcamento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnOrcamento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/folder_page_white.png"))); // NOI18N
        btnOrcamento.setText("Novo Orçamento");
        btnOrcamento.setContentAreaFilled(false);
        btnOrcamento.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOrcamento.setIconTextGap(20);
        btnOrcamento.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnOrcamentoMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnOrcamentoMouseExited(evt);
            }
        });
        btnOrcamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrcamentoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnMesas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnProdutos1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnFinanceiro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnSistema, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnPessoas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnUsuarios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnVendaLista, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnVenda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(btnBackup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnOrcamento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnVenda)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOrcamento)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnVendaLista)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMesas)
                .addGap(35, 35, 35)
                .addComponent(btnProdutos1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFinanceiro)
                .addGap(4, 4, 4)
                .addComponent(btnPessoas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSistema)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUsuarios)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBackup)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseEntered
        this.toFront();
        /*counter = 0;
        while(true){
            counter++;
            if(counter > DELAY){
                this.setBounds(0, 0, MENU_MAX_WIDTH, SCREEN_HEIGHT);
                this.toFront();
                break;
            }
        }*/
    }//GEN-LAST:event_formMouseEntered

    private void formMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseExited
        this.toBack();
        /*counter = 0;
        while(true){
            counter++;
            if(counter > DELAY / 2){
                this.setBounds(0, 0, MENU_MIN_WIDTH, SCREEN_HEIGHT);
                break;
            }
        }*/
    }//GEN-LAST:event_formMouseExited

    private void btnVendaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnVendaMouseEntered
        formMouseEntered(evt);
    }//GEN-LAST:event_btnVendaMouseEntered

    private void btnVendaMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnVendaMouseExited
        formMouseExited(evt);
    }//GEN-LAST:event_btnVendaMouseExited

    private void btnMesasMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMesasMouseEntered
        formMouseEntered(evt);
    }//GEN-LAST:event_btnMesasMouseEntered

    private void btnMesasMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMesasMouseExited
        formMouseExited(evt);
    }//GEN-LAST:event_btnMesasMouseExited

    private void btnVendaListaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnVendaListaMouseEntered
        formMouseEntered(evt);
    }//GEN-LAST:event_btnVendaListaMouseEntered

    private void btnVendaListaMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnVendaListaMouseExited
        formMouseExited(evt);
    }//GEN-LAST:event_btnVendaListaMouseExited

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        
    }//GEN-LAST:event_formComponentShown

    private void btnVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVendaActionPerformed
        MAIN_VIEW.addView(VendaView.getInstance(new Venda()));
    }//GEN-LAST:event_btnVendaActionPerformed

    private void btnVendaListaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVendaListaActionPerformed
        MAIN_VIEW.addView(VendaGeralContainerView.getSingleInstance());
    }//GEN-LAST:event_btnVendaListaActionPerformed

    private void btnMesasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMesasActionPerformed
        MAIN_VIEW.addView(ComandasView.getSingleInstance());
    }//GEN-LAST:event_btnMesasActionPerformed

    private void btnProdutos1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnProdutos1MouseEntered
        formMouseEntered(evt);
    }//GEN-LAST:event_btnProdutos1MouseEntered

    private void btnProdutos1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnProdutos1MouseExited
        formMouseExited(evt);
    }//GEN-LAST:event_btnProdutos1MouseExited

    private void btnProdutos1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProdutos1ActionPerformed
        MAIN_VIEW.addView(ProdutoGeralContainerView.getSingleInstance());
    }//GEN-LAST:event_btnProdutos1ActionPerformed

    private void btnFinanceiroMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnFinanceiroMouseEntered
        formMouseEntered(evt);
    }//GEN-LAST:event_btnFinanceiroMouseEntered

    private void btnFinanceiroMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnFinanceiroMouseExited
        formMouseExited(evt);
    }//GEN-LAST:event_btnFinanceiroMouseExited

    private void btnFinanceiroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinanceiroActionPerformed
        MAIN_VIEW.addView(FinanceiroContainerView.getSingleInstance());
    }//GEN-LAST:event_btnFinanceiroActionPerformed

    private void btnSistemaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSistemaMouseEntered
        formMouseEntered(evt);
    }//GEN-LAST:event_btnSistemaMouseEntered

    private void btnSistemaMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSistemaMouseExited
        formMouseExited(evt);
    }//GEN-LAST:event_btnSistemaMouseExited

    private void btnSistemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSistemaActionPerformed
        MAIN_VIEW.addView(ConfguracaoSistema.getSingleInstance());
    }//GEN-LAST:event_btnSistemaActionPerformed

    private void btnPessoasMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnPessoasMouseEntered
        formMouseEntered(evt);
    }//GEN-LAST:event_btnPessoasMouseEntered

    private void btnPessoasMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnPessoasMouseExited
        formMouseExited(evt);
    }//GEN-LAST:event_btnPessoasMouseExited

    private void btnPessoasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPessoasActionPerformed
        MAIN_VIEW.addView(PessoaListaView.getSingleInstance());
    }//GEN-LAST:event_btnPessoasActionPerformed

    private void btnUsuariosMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUsuariosMouseEntered
        formMouseEntered(evt);
    }//GEN-LAST:event_btnUsuariosMouseEntered

    private void btnUsuariosMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUsuariosMouseExited
        formMouseExited(evt);
    }//GEN-LAST:event_btnUsuariosMouseExited

    private void btnUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsuariosActionPerformed
        MAIN_VIEW.addView(UsuarioListaView.getSingleInstance());
    }//GEN-LAST:event_btnUsuariosActionPerformed

    private void btnBackupMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBackupMouseEntered
        formMouseEntered(evt);
    }//GEN-LAST:event_btnBackupMouseEntered

    private void btnBackupMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBackupMouseExited
        formMouseExited(evt);
    }//GEN-LAST:event_btnBackupMouseExited

    private void btnBackupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackupActionPerformed
        BackupView b = new BackupView(MAIN_VIEW);
    }//GEN-LAST:event_btnBackupActionPerformed

    private void btnOrcamentoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnOrcamentoMouseEntered
        formMouseEntered(evt);
    }//GEN-LAST:event_btnOrcamentoMouseEntered

    private void btnOrcamentoMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnOrcamentoMouseExited
        formMouseExited(evt);
    }//GEN-LAST:event_btnOrcamentoMouseExited

    private void btnOrcamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrcamentoActionPerformed
        Venda venda = new Venda();
        venda.setOrcamento(true);
        MAIN_VIEW.addView(VendaView.getInstance(venda));
    }//GEN-LAST:event_btnOrcamentoActionPerformed

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
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainMenuView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainMenuView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainMenuView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainMenuView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainMenuView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBackup;
    private javax.swing.JButton btnFinanceiro;
    private javax.swing.JButton btnMesas;
    private javax.swing.JButton btnOrcamento;
    private javax.swing.JButton btnPessoas;
    private javax.swing.JButton btnProdutos1;
    private javax.swing.JButton btnSistema;
    private javax.swing.JButton btnUsuarios;
    private javax.swing.JButton btnVenda;
    private javax.swing.JButton btnVendaLista;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
