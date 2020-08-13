/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.sistema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.principal.financeiro.Conta;
import model.mysql.dao.principal.VendaDAO;
import model.mysql.dao.principal.catalogo.ProdutoDAO;
import model.mysql.dao.principal.financeiro.CaixaItemDAO;
import model.mysql.dao.principal.financeiro.ContaDAO;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;
import util.JSwing;
import view.Toast;

/**
 *
 * @author ivand
 *
 * ESTE FOI CRIADO COMO JDIALOG DESDE O INÍCIO - USAR ESTE COMO REFERÊNCIA PARA
 * OS DEMAIS
 *
 *
 */
public class PatchView extends javax.swing.JDialog {

    /**
     * Creates new form ParcelamentoView
     */
    public PatchView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

    }

    public PatchView() {
        super(MAIN_VIEW, true);
        initComponents();
        JSwing.startComponentsBehavior(this);

        txtTotalVendasDataInicial.setText(DateTime.toString(LocalDate.of(2019, 1, 1)));
        txtTotalVendasDataFinal.setText(DateTime.toString(LocalDate.now()));

        this.setLocationRelativeTo(this);
        this.setVisible(true);
    }

    private void executar() {

        VendaDAO vendaDAO = new VendaDAO();

        System.out.println("Refatorando descontos e acréscimos...");

        for (Venda v : vendaDAO.findAll()) {

            if (v.getTipoOperacao() == null) {
                v.setTipoOperacao(TipoOperacao.SAIDA);
            }

            //verificar desconto em item e geral simultâneo
            boolean descontoSimultaneo = false;
            if (v.getDescontoConsolidadoProdutos().compareTo(BigDecimal.ZERO) > 0) {

                for (MovimentoFisico mf : v.getMovimentosFisicosProdutos()) {
                    if (mf.getDescontoConsolidado().compareTo(BigDecimal.ZERO) > 0) {
                        descontoSimultaneo = true;
                        break;
                    }
                }
            }

            if (v.getDescontoConsolidadoServicos().compareTo(BigDecimal.ZERO) > 0) {

                for (MovimentoFisico mf : v.getMovimentosFisicosServicos()) {
                    if (mf.getDescontoConsolidado().compareTo(BigDecimal.ZERO) > 0) {
                        descontoSimultaneo = true;
                        break;
                    }
                }
            }

            if (descontoSimultaneo) {
                System.out.println("Venda com desconto simultâneo -> id: " + v.getId());

            } else {
                if (v.getAcrescimoMonetarioProdutos().compareTo(BigDecimal.ZERO) > 0) {
                    v.distribuirAcrescimoMonetarioProdutos(v.getAcrescimoMonetarioProdutos());

                } else if (v.getAcrescimoPercentualProdutos().compareTo(BigDecimal.ZERO) > 0) {
                    v.distribuirAcrescimoPercentualProdutos(v.getAcrescimoPercentualProdutos());

                }

                if (v.getDescontoMonetarioProdutos().compareTo(BigDecimal.ZERO) > 0) {
                    v.distribuirDescontoMonetarioProdutos(v.getDescontoMonetarioProdutos());

                } else if (v.getDescontoPercentualProdutos().compareTo(BigDecimal.ZERO) > 0) {
                    v.distribuirDescontoPercentualProdutos(v.getDescontoPercentualProdutos());

                }

                if (v.getAcrescimoMonetarioServicos().compareTo(BigDecimal.ZERO) > 0) {
                    v.distribuirAcrescimoMonetarioServicos(v.getAcrescimoMonetarioServicos());

                } else if (v.getAcrescimoPercentualServicos().compareTo(BigDecimal.ZERO) > 0) {
                    v.distribuirAcrescimoPercentualServicos(v.getAcrescimoPercentualServicos());

                }

                if (v.getDescontoMonetarioServicos().compareTo(BigDecimal.ZERO) > 0) {
                    v.distribuirDescontoMonetarioServicos(v.getDescontoMonetarioServicos());

                } else if (v.getDescontoPercentualServicos().compareTo(BigDecimal.ZERO) > 0) {
                    v.distribuirDescontoPercentualServicos(v.getDescontoPercentualServicos());

                }

                //--
                if ((v.getAcrescimoMonetarioProdutos().add(v.getAcrescimoPercentualProdutos())).compareTo(v.getTotalAcrescimoProdutosMonetarioOuPercentual()) != 0) {
                    System.out.println("Acréscimo Produtos inconsistente -> id: " + v.getId());

                    if (v.getMovimentosFisicosProdutos().isEmpty()) {

                        //Se tem acréscimo monetario
                        if (v.getAcrescimoMonetarioProdutos().compareTo(BigDecimal.ZERO) > 0) {
                            //se tem acréscimo percentual tbm
                            if (v.getAcrescimoPercentualServicos().compareTo(BigDecimal.ZERO) > 0) {
                                //anota na obs o valor para corrigir maualmente
                                v.setObservacao("Patch AcrescimoPercentualServicos: " + v.getAcrescimoPercentualServicos() + " --- " + v.getObservacao());
                                //converte o desconto percentual já existente e distribui
                                v.distribuirAcrescimoMonetarioServicos(v.getAcrescimoPercentualEmMonetarioServicos().add(v.getAcrescimoMonetarioProdutos()));

                            } else {
                                //soma o desconto monetário já existente e distribui
                                v.distribuirAcrescimoMonetarioServicos(v.getAcrescimoMonetarioServicos().add(v.getAcrescimoMonetarioProdutos()));
                            }

                        }
                        if (v.getAcrescimoPercentualProdutos().compareTo(BigDecimal.ZERO) > 0) {
                            v.distribuirAcrescimoPercentualServicos(v.getAcrescimoPercentualProdutos());
                        }

                    }

                }

                if ((v.getDescontoMonetarioProdutos().add(v.getDescontoPercentualProdutos())).compareTo(v.getTotalDescontoProdutosMonetarioOuPercentual()) != 0) {
                    System.out.println("Desconto Produtos inconsistente -> id: " + v.getId());

                    //System.out.println("v.getDescontoConsolidadoProdutos(): " + v.getDescontoConsolidadoProdutos());
                    //System.out.println("v.getMovimentosFisicosProdutos(): " + v.getMovimentosFisicosProdutos());
                    //MODELO - REMOVI A O CONSOLIDADO
                    if (v.getMovimentosFisicosProdutos().isEmpty()) {
                        //System.out.println("tem algum desconto em produtos, mas não tem produtos");
                        //Se tem desconto monetario
                        if (v.getDescontoMonetarioProdutos().compareTo(BigDecimal.ZERO) > 0) {
                            //System.out.println("tem desconto monetário em produtos");
                            //se tem desconto percentual tbm
                            if (v.getDescontoPercentualServicos().compareTo(BigDecimal.ZERO) > 0) {
                                //anota na obs o valor para corrigir maualmente
                                v.setObservacao("Patch DescontoPercentualServicos: " + v.getDescontoPercentualServicos() + " --- " + v.getObservacao());
                                //converte o desconto percentual já existente e distribui
                                v.distribuirDescontoMonetarioServicos(v.getDescontoPercentualEmMonetarioServicos().add(v.getDescontoMonetarioProdutos()));

                            } else {
                                //soma o desconto monetário já existente e distribui
                                v.distribuirDescontoMonetarioServicos(v.getDescontoMonetarioServicos().add(v.getDescontoMonetarioProdutos()));
                            }

                        }
                        //System.out.println("v.getDescontoPercentualProdutos(): " + v.getDescontoPercentualProdutos());
                        if (v.getDescontoPercentualProdutos().compareTo(BigDecimal.ZERO) > 0) {
                            //System.out.println("tem descontopercentual de produtos");
                            //System.out.println("distribuir desconto percentual serviços com o valor do desconto no produto");
                            v.distribuirDescontoPercentualServicos(v.getDescontoPercentualProdutos());
                        }
                    }
                }

                if ((v.getAcrescimoMonetarioServicos().add(v.getAcrescimoPercentualServicos())).compareTo(v.getTotalAcrescimoServicosMonetarioOuPercentual()) != 0) {
                    System.out.println("Acréscimo Servicos inconsistente -> id: " + v.getId());

                    if (v.getMovimentosFisicosServicos().isEmpty()) {

                        //Se tem acréscimo monetario
                        if (v.getAcrescimoMonetarioServicos().compareTo(BigDecimal.ZERO) > 0) {
                            //se tem acréscimo percentual tbm
                            if (v.getAcrescimoPercentualProdutos().compareTo(BigDecimal.ZERO) > 0) {
                                //anota na obs o valor para corrigir maualmente
                                v.setObservacao("Patch AcrescimoPercentualProdutos: " + v.getAcrescimoPercentualProdutos() + " --- " + v.getObservacao());
                                //converte o desconto percentual já existente e distribui
                                v.distribuirAcrescimoMonetarioProdutos(v.getAcrescimoPercentualEmMonetarioProdutos().add(v.getAcrescimoMonetarioServicos()));

                            } else {
                                //soma o desconto monetário já existente e distribui
                                v.distribuirAcrescimoMonetarioProdutos(v.getAcrescimoMonetarioProdutos().add(v.getAcrescimoMonetarioServicos()));
                            }

                        }
                        if (v.getAcrescimoPercentualServicos().compareTo(BigDecimal.ZERO) > 0) {
                            v.distribuirAcrescimoPercentualProdutos(v.getAcrescimoPercentualServicos());
                        }

                    }
                }

                if ((v.getDescontoMonetarioServicos().add(v.getDescontoPercentualServicos())).compareTo(v.getTotalDescontoServicosMonetarioOuPercentual()) != 0) {
                    System.out.println("Desconto Servicos inconsistente -> id: " + v.getId());

                    //Se tem descontos em serviços mas não tem serviços
                    if (v.getMovimentosFisicosServicos().isEmpty()) {
                        //Se tem desconto monetario
                        if (v.getDescontoMonetarioServicos().compareTo(BigDecimal.ZERO) > 0) {
                            //se tem desconto percentual tbm
                            if (v.getDescontoPercentualProdutos().compareTo(BigDecimal.ZERO) > 0) {
                                //anota na obs o valor para corrigir maualmente
                                v.setObservacao("Patch DescontoPercentualProdutos: " + v.getDescontoPercentualProdutos() + " --- " + v.getObservacao());
                                //converte o desconto percentual já existente e distribui
                                v.distribuirDescontoMonetarioProdutos(v.getDescontoPercentualEmMonetarioProdutos().add(v.getDescontoMonetarioServicos()));

                            } else {
                                //soma o desconto monetário já existente e distribui
                                v.distribuirDescontoMonetarioProdutos(v.getDescontoMonetarioProdutos().add(v.getDescontoMonetarioServicos()));
                            }

                        }
                        if (v.getDescontoPercentualServicos().compareTo(BigDecimal.ZERO) > 0) {
                            v.distribuirDescontoPercentualProdutos(v.getDescontoPercentualServicos());
                        }

                    }
                }

                vendaDAO.save(v);

            }

        }

        JOptionPane.showMessageDialog(MAIN_VIEW, "Concluído");
    }

    private void executarTotais() {
        VendaDAO vendaDAO = new VendaDAO();

        new Toast("Refatorando totais das vendas... Aguarde a mensagem de fim");

        LocalDateTime dataInicial = DateTime.fromStringToLocalDate(txtTotalVendasDataInicial.getText()).atTime(LocalTime.MIN);
        LocalDateTime dataFinal = DateTime.fromStringToLocalDate(txtTotalVendasDataFinal.getText()).atTime(LocalTime.MAX);

        List<Venda> documentos = vendaDAO.findByIntervalo(TipoOperacao.SAIDA, dataInicial, dataFinal);

        System.out.println("Documentos para processar: " + documentos.size());

        for (Venda v : documentos) {
            //System.out.println("id " + v.getId());
            v.setTotalProdutos();
            v.setTotalServicos();
            vendaDAO.save(v);
        }

        new Toast("Fim", false);
    }

    private void executarEstoques() {
        ProdutoDAO produtoDAO = new ProdutoDAO();

        new Toast("Refatorando estoques dos produtos... Aguarde a mensagem de fim");

        for (Produto p : produtoDAO.findAll()) {
            p.setEstoqueAtual();
            produtoDAO.save(p);
        }

        new Toast("Fim", false);
    }

    private void executarSaldoContas() {
        ContaDAO contaDAO = new ContaDAO();

        new Toast("Refatorando saldo das contas... Aguarde a mensagem de fim");

        for (Conta c : contaDAO.findAll()) {
            c.setSaldo();
            contaDAO.save(c);
        }

        new Toast("Fim", false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnExecutar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnExecutarTotal = new javax.swing.JButton();
        btnExecutarTotal1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btnExecutarSaldoContas = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtTotalVendasDataInicial = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtTotalVendasDataFinal = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Patch");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        btnExecutar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnExecutar.setText("Executar");
        btnExecutar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExecutarActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Refatorar Acréscimos e Descontos");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Refatorar Total das Vendas (2019-11-19)");

        btnExecutarTotal.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnExecutarTotal.setText("Executar");
        btnExecutarTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExecutarTotalActionPerformed(evt);
            }
        });

        btnExecutarTotal1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnExecutarTotal1.setText("Executar");
        btnExecutarTotal1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExecutarTotal1ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Refatorar Estoque Atual (2020-01-08)");

        btnExecutarSaldoContas.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnExecutarSaldoContas.setText("Executar");
        btnExecutarSaldoContas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExecutarSaldoContasActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Refatorar Saldo de Contas (2020-04-25)");

        txtTotalVendasDataInicial.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTotalVendasDataInicial.setName("data"); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Intervalo");

        txtTotalVendasDataFinal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTotalVendasDataFinal.setName("data"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnExecutar))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(txtTotalVendasDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtTotalVendasDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnExecutarTotal)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnExecutarTotal1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnExecutarSaldoContas)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(btnExecutar))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(btnExecutarTotal)
                    .addComponent(txtTotalVendasDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtTotalVendasDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(btnExecutarTotal1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(btnExecutarSaldoContas))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExecutarActionPerformed
        executar();
    }//GEN-LAST:event_btnExecutarActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    }//GEN-LAST:event_formWindowClosing

    private void btnExecutarTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExecutarTotalActionPerformed
        executarTotais();
    }//GEN-LAST:event_btnExecutarTotalActionPerformed

    private void btnExecutarTotal1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExecutarTotal1ActionPerformed
        executarEstoques();
    }//GEN-LAST:event_btnExecutarTotal1ActionPerformed

    private void btnExecutarSaldoContasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExecutarSaldoContasActionPerformed
        executarSaldoContas();
    }//GEN-LAST:event_btnExecutarSaldoContasActionPerformed

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
            java.util.logging.Logger.getLogger(PatchView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PatchView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PatchView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PatchView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                PatchView dialog = new PatchView(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnExecutar;
    private javax.swing.JButton btnExecutarSaldoContas;
    private javax.swing.JButton btnExecutarTotal;
    private javax.swing.JButton btnExecutarTotal1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JFormattedTextField txtTotalVendasDataFinal;
    private javax.swing.JFormattedTextField txtTotalVendasDataInicial;
    // End of variables declaration//GEN-END:variables
}
