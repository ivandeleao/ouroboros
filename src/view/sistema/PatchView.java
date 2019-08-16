/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.sistema;

import java.math.BigDecimal;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.principal.VendaDAO;
import static ouroboros.Ouroboros.MAIN_VIEW;

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

        this.setLocationRelativeTo(this);
        this.setVisible(true);
    }

    private void executar() {

        VendaDAO vendaDAO = new VendaDAO();

        System.out.println("Refatorando descontos e acréscimos...");
        
        for (Venda v : vendaDAO.findAll()) {

            if(v.getTipoOperacao() == null) {
                v.setTipoOperacao(TipoOperacao.SAIDA);
            }

            //verificar desconto em item e geral simultâneo
            boolean descontoSimultaneo = false;
            if (v.getDescontoConsolidadoProdutos().compareTo(BigDecimal.ZERO) > 0) {

                for (MovimentoFisico mf : v.getMovimentosFisicosProdutos()) {
                    if (mf.getDesconto().compareTo(BigDecimal.ZERO) > 0) {
                        descontoSimultaneo = true;
                        break;
                    }
                }
            }
            
            if (v.getDescontoConsolidadoServicos().compareTo(BigDecimal.ZERO) > 0) {

                for (MovimentoFisico mf : v.getMovimentosFisicosServicos()) {
                    if (mf.getDesconto().compareTo(BigDecimal.ZERO) > 0) {
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
                if ((v.getAcrescimoMonetarioProdutos().add(v.getAcrescimoPercentualProdutos())).compareTo(v.getTotalAcrescimoProdutos()) != 0) {
                    System.out.println("Acréscimo Produtos inconsistente -> id: " + v.getId());
                    
                    if(v.getMovimentosFisicosProdutos().isEmpty()) {
                        
                        //Se tem acréscimo monetario
                        if(v.getAcrescimoMonetarioProdutos().compareTo(BigDecimal.ZERO) > 0) {
                            //se tem acréscimo percentual tbm
                            if(v.getAcrescimoPercentualServicos().compareTo(BigDecimal.ZERO) > 0) {
                                //anota na obs o valor para corrigir maualmente
                                v.setObservacao("Patch AcrescimoPercentualServicos: " + v.getAcrescimoPercentualServicos() + " --- " +  v.getObservacao());
                                //converte o desconto percentual já existente e distribui
                                v.distribuirAcrescimoMonetarioServicos(v.getAcrescimoPercentualEmMonetarioServicos().add(v.getAcrescimoMonetarioProdutos()));
                                
                            } else {
                                //soma o desconto monetário já existente e distribui
                                v.distribuirAcrescimoMonetarioServicos(v.getAcrescimoMonetarioServicos().add(v.getAcrescimoMonetarioProdutos()));
                            }
                            
                        }
                        if(v.getAcrescimoPercentualProdutos().compareTo(BigDecimal.ZERO) > 0) {
                            v.distribuirAcrescimoPercentualServicos(v.getAcrescimoPercentualProdutos());
                        }
                        
                    }
                    
                    
                    
                }
                
                if ((v.getDescontoMonetarioProdutos().add(v.getDescontoPercentualProdutos())).compareTo(v.getTotalDescontoProdutos()) != 0) {
                    System.out.println("Desconto Produtos inconsistente -> id: " + v.getId());
                    
                    
                    //System.out.println("v.getDescontoConsolidadoProdutos(): " + v.getDescontoConsolidadoProdutos());
                    //System.out.println("v.getMovimentosFisicosProdutos(): " + v.getMovimentosFisicosProdutos());
                    
                    //MODELO - REMOVI A O CONSOLIDADO
                    if(v.getMovimentosFisicosProdutos().isEmpty()) {
                        //System.out.println("tem algum desconto em produtos, mas não tem produtos");
                        //Se tem desconto monetario
                        if(v.getDescontoMonetarioProdutos().compareTo(BigDecimal.ZERO) > 0) {
                            //System.out.println("tem desconto monetário em produtos");
                            //se tem desconto percentual tbm
                            if(v.getDescontoPercentualServicos().compareTo(BigDecimal.ZERO) > 0) {
                                //anota na obs o valor para corrigir maualmente
                                v.setObservacao("Patch DescontoPercentualServicos: " + v.getDescontoPercentualServicos() + " --- " +  v.getObservacao());
                                //converte o desconto percentual já existente e distribui
                                v.distribuirDescontoMonetarioServicos(v.getDescontoPercentualEmMonetarioServicos().add(v.getDescontoMonetarioProdutos()));
                                
                            } else {
                                //soma o desconto monetário já existente e distribui
                                v.distribuirDescontoMonetarioServicos(v.getDescontoMonetarioServicos().add(v.getDescontoMonetarioProdutos()));
                            }
                            
                        }
                        //System.out.println("v.getDescontoPercentualProdutos(): " + v.getDescontoPercentualProdutos());
                        if(v.getDescontoPercentualProdutos().compareTo(BigDecimal.ZERO) > 0) {
                            //System.out.println("tem descontopercentual de produtos");
                            //System.out.println("distribuir desconto percentual serviços com o valor do desconto no produto");
                            v.distribuirDescontoPercentualServicos(v.getDescontoPercentualProdutos());
                        }
                    }
                }
                
                if ((v.getAcrescimoMonetarioServicos().add(v.getAcrescimoPercentualServicos())).compareTo(v.getTotalAcrescimoServicos()) != 0) {
                    System.out.println("Acréscimo Servicos inconsistente -> id: " + v.getId());
                    
                    if(v.getMovimentosFisicosServicos().isEmpty()) {
                        
                        //Se tem acréscimo monetario
                        if(v.getAcrescimoMonetarioServicos().compareTo(BigDecimal.ZERO) > 0) {
                            //se tem acréscimo percentual tbm
                            if(v.getAcrescimoPercentualProdutos().compareTo(BigDecimal.ZERO) > 0) {
                                //anota na obs o valor para corrigir maualmente
                                v.setObservacao("Patch AcrescimoPercentualProdutos: " + v.getAcrescimoPercentualProdutos() + " --- " +  v.getObservacao());
                                //converte o desconto percentual já existente e distribui
                                v.distribuirAcrescimoMonetarioProdutos(v.getAcrescimoPercentualEmMonetarioProdutos().add(v.getAcrescimoMonetarioServicos()));
                                
                            } else {
                                //soma o desconto monetário já existente e distribui
                                v.distribuirAcrescimoMonetarioProdutos(v.getAcrescimoMonetarioProdutos().add(v.getAcrescimoMonetarioServicos()));
                            }
                            
                        }
                        if(v.getAcrescimoPercentualServicos().compareTo(BigDecimal.ZERO) > 0) {
                            v.distribuirAcrescimoPercentualProdutos(v.getAcrescimoPercentualServicos());
                        }
                        
                    }
                }
                
                if ((v.getDescontoMonetarioServicos().add(v.getDescontoPercentualServicos())).compareTo(v.getTotalDescontoServicos()) != 0) {
                    System.out.println("Desconto Servicos inconsistente -> id: " + v.getId());
                    
                    //Se tem descontos em serviços mas não tem serviços
                    if(v.getMovimentosFisicosServicos().isEmpty()) {
                        //Se tem desconto monetario
                        if(v.getDescontoMonetarioServicos().compareTo(BigDecimal.ZERO) > 0) {
                            //se tem desconto percentual tbm
                            if(v.getDescontoPercentualProdutos().compareTo(BigDecimal.ZERO) > 0) {
                                //anota na obs o valor para corrigir maualmente
                                v.setObservacao("Patch DescontoPercentualProdutos: " + v.getDescontoPercentualProdutos() + " --- " +  v.getObservacao());
                                //converte o desconto percentual já existente e distribui
                                v.distribuirDescontoMonetarioProdutos(v.getDescontoPercentualEmMonetarioProdutos().add(v.getDescontoMonetarioServicos()));
                                
                            } else {
                                //soma o desconto monetário já existente e distribui
                                v.distribuirDescontoMonetarioProdutos(v.getDescontoMonetarioProdutos().add(v.getDescontoMonetarioServicos()));
                            }
                            
                        }
                        if(v.getDescontoPercentualServicos().compareTo(BigDecimal.ZERO) > 0) {
                            v.distribuirDescontoPercentualProdutos(v.getDescontoPercentualServicos());
                        }
                        
                    }
                }
                
                

                vendaDAO.save(v);

            }

        }

        JOptionPane.showMessageDialog(MAIN_VIEW, "Concluído");
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 259, Short.MAX_VALUE)
                .addComponent(btnExecutar)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(btnExecutar))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExecutarActionPerformed
        executar();
    }//GEN-LAST:event_btnExecutarActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    }//GEN-LAST:event_formWindowClosing

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
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
