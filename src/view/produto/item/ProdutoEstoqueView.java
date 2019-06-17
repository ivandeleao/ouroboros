/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.produto.item;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.dao.principal.catalogo.ProdutoDAO;
import model.mysql.dao.principal.MovimentoFisicoDAO;
import model.jtable.catalogo.EstoqueProdutoJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.em;
import util.DateTime;
import util.Decimal;
import util.jTableFormat.EstoqueRenderer;
import view.documentoSaida.VendaView;

/**
 *
 * @author ivand
 */
public class ProdutoEstoqueView extends javax.swing.JInternalFrame {

    private static List<ProdutoEstoqueView> produtoEstoqueViews = new ArrayList<>(); //instâncias

    EstoqueProdutoJTableModel estoqueJTableModel = new EstoqueProdutoJTableModel();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private Produto produto;

    private List<MovimentoFisico> estoqueList = new ArrayList<>();

    public static ProdutoEstoqueView getInstance(Produto produto) {
        for (ProdutoEstoqueView produtoEstoqueView : produtoEstoqueViews) {
            if (produtoEstoqueView.produto == produto) {
                return produtoEstoqueView;
            }
        }
        produtoEstoqueViews.add(new ProdutoEstoqueView(produto));
        return produtoEstoqueViews.get(produtoEstoqueViews.size() - 1);
    }

    private ProdutoEstoqueView() {
        initComponents();
    }

    private ProdutoEstoqueView(Produto produto) {
        initComponents();
        //JSwing.startComponentsBehavior(this);

        this.produto = produto;
        
        btnConfirmarEntregaDevolucao.setVisible(false);

        formatarTabela();

        carregarTabela();
    }

    private void formatarTabela() {
        tblEstoque.setModel(estoqueJTableModel);

        tblEstoque.setRowHeight(24);
        tblEstoque.setIntercellSpacing(new Dimension(10, 10));

        //id
        tblEstoque.getColumn("Id").setPreferredWidth(60);
        tblEstoque.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblEstoque.getColumn("Status").setPreferredWidth(200);
        tblEstoque.getColumn("Status").setCellRenderer(new EstoqueRenderer());
        //tblEstoque.getColumn("Status").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        //data
        tblEstoque.getColumn("Data").setPreferredWidth(160);
        tblEstoque.getColumn("Data").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        //tipo
        tblEstoque.getColumn("Origem").setPreferredWidth(200);
        //observação
        tblEstoque.getColumn("Observação").setPreferredWidth(400);
        //entrada
        tblEstoque.getColumn("Entrada").setPreferredWidth(120);
        tblEstoque.getColumn("Entrada").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //saída
        tblEstoque.getColumn("Saída").setPreferredWidth(120);
        tblEstoque.getColumn("Saída").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //saldo
        //tblEstoque.getColumn("Saldo").setPreferredWidth(120);
        //tblEstoque.getColumn("Saldo").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblEstoque.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                carregarDetalhes();
            }

        });
    }
    

    private void carregarTabela() {
        //em.refresh(produto);

        System.out.println("estoque carregar tabela...");

        estoqueList = new MovimentoFisicoDAO().findTotalPorDatas(produto, null, null);

        estoqueJTableModel.clear();

        if (estoqueList != null) {
            estoqueJTableModel.addList(estoqueList);

            //posicionar na última linha
            int lastRow = tblEstoque.getRowCount() - 1;
            if (lastRow > 0) {
                tblEstoque.setRowSelectionInterval(lastRow, lastRow);
                tblEstoque.scrollRectToVisible(tblEstoque.getCellRect(lastRow, 0, true));
            }
        }
        
        //Exibir total
        txtEstoqueAtual.setText(Decimal.toString(produto.getEstoqueAtual()));

    }

    private void lancarManual() {
        ProdutoEstoqueLancamentoView l = new ProdutoEstoqueLancamentoView(produto);
        
        carregarTabela();
    }

    private void carregarDetalhes() {
        //try {
            if (tblEstoque.getSelectedRow() > -1) {
                int index = tblEstoque.getSelectedRow();

                MovimentoFisico movimentoFisico = estoqueJTableModel.getRow(index);

                if(movimentoFisico.getMovimentoFisicoOrigem() != null) {
                    txtOrigem.setText(movimentoFisico.getMovimentoFisicoOrigem().getProduto().getNome());
                    txtOrigemId.setText(movimentoFisico.getMovimentoFisicoOrigem().getId().toString());
                } else {
                    txtOrigem.setText("");
                    txtOrigemId.setText("");
                }

                txtCriacao.setText(DateTime.toString(movimentoFisico.getCriacao()));

                txtDataSaida.setText(DateTime.toString(movimentoFisico.getDataSaida()));
                txtDataSaidaPrevista.setText(DateTime.toString(movimentoFisico.getDataSaidaPrevista()));

                txtDataEntrada.setText(DateTime.toString(movimentoFisico.getDataEntrada()));
                txtDataEntradaPrevista.setText(DateTime.toString(movimentoFisico.getDataEntradaPrevista()));
                
                String componentes = "";
                if(movimentoFisico.getMovimentosFisicosComponente() != null) {
                    componentes = String.valueOf(movimentoFisico.getMovimentosFisicosComponente().size());
                }
                txtComponentes.setText(componentes);
                
                String devolucao = "";
                if(movimentoFisico.getDevolucaoOrigem() != null) {
                    devolucao = String.valueOf(movimentoFisico.getDevolucaoOrigem().getId());
                }
                txtDevolucaoOrigemId.setText(devolucao);
                
                String estornoOrigem = "";
                if(movimentoFisico.getEstornoOrigem() != null) {
                    estornoOrigem = movimentoFisico.getEstornoOrigem().getId().toString();
                }
                txtEstornoOrigem.setText(estornoOrigem);
                
                String estorno = "";
                if(movimentoFisico.getEstorno() != null) {
                    estorno = movimentoFisico.getEstorno().getId().toString();
                }
                txtEstorno.setText(estorno);
                
            }
        //} catch (Exception e) {
        //    JOptionPane.showMessageDialog(rootPane, e, "Erro ao exibir detalhes", JOptionPane.ERROR_MESSAGE);
        //}
    }

    private void confirmarEntregaDevolucao() {
        List<MovimentoFisico> movimentosFisicos = new ArrayList<>();
        Set<Integer> setIds = new HashSet<>();

        int[] rowIndices = tblEstoque.getSelectedRows();

        boolean valido = true;
        for (int rowIndex : rowIndices) {
            MovimentoFisico mf = estoqueJTableModel.getRow(rowIndex);
            if (!produto.equals(mf.getProduto())) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Não é possível confirmar um movimento derivado. Confirme através do produto composto: " + mf.getProduto().getNome(), "Atenção", JOptionPane.WARNING_MESSAGE);
                valido = false;
                break;
            }
            movimentosFisicos.add(mf);
        }

        if (valido) {
            ConfirmarEntregaDevolucaoView confirmar = new ConfirmarEntregaDevolucaoView(movimentosFisicos);
        }
    }
    
    private void detalhe() {
        MovimentoFisico movimentoFisico = estoqueJTableModel.getRow(tblEstoque.getSelectedRow());
        ProdutoEstoqueDetalheView detalhe = new ProdutoEstoqueDetalheView(movimentoFisico);
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
        tblEstoque = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        txtCriacao = new javax.swing.JTextField();
        txtDataSaidaPrevista = new javax.swing.JTextField();
        txtDataSaida = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtDataEntradaPrevista = new javax.swing.JTextField();
        txtDataEntrada = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtOrigem = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtComponentes = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtDevolucaoOrigemId = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtOrigemId = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtEstornoOrigem = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtEstorno = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        btnLancamentoManual = new javax.swing.JButton();
        btnConfirmarEntregaDevolucao = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txtEstoqueAtual = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        setTitle("Estoque");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameActivated(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblEstoque.setModel(new javax.swing.table.DefaultTableModel(
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
        tblEstoque.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblEstoqueMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblEstoque);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtCriacao.setEditable(false);

        txtDataSaidaPrevista.setEditable(false);

        txtDataSaida.setEditable(false);

        jLabel1.setText("Criação");

        jLabel2.setText("Saída Prevista");

        jLabel3.setText("Saída");

        txtDataEntradaPrevista.setEditable(false);

        txtDataEntrada.setEditable(false);

        jLabel4.setText("Entrada Prevista");

        jLabel5.setText("Entrada");

        txtOrigem.setEditable(false);

        jLabel6.setText("Origem");

        txtComponentes.setEditable(false);

        jLabel7.setText("Componentes");

        txtDevolucaoOrigemId.setEditable(false);

        jLabel8.setText("Devolução Origem");

        jLabel9.setText("Origem Id");

        txtOrigemId.setEditable(false);

        jLabel10.setText("Estorno Origem");

        txtEstornoOrigem.setEditable(false);

        jLabel11.setText("Estorno");

        txtEstorno.setEditable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel10)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtCriacao, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .addComponent(txtDataSaidaPrevista)
                    .addComponent(txtDataSaida)
                    .addComponent(txtEstornoOrigem))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel11))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDataEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                    .addComponent(txtOrigem)
                    .addComponent(txtDataEntradaPrevista)
                    .addComponent(txtEstorno, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtComponentes, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                    .addComponent(txtDevolucaoOrigemId)
                    .addComponent(txtOrigemId))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCriacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(txtOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel9)
                    .addComponent(txtOrigemId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDataSaidaPrevista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtDataEntradaPrevista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtComponentes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDataSaida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtDataEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtDevolucaoOrigemId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEstornoOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtEstorno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnLancamentoManual.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/add.png"))); // NOI18N
        btnLancamentoManual.setText("Lançamento Manual");
        btnLancamentoManual.setContentAreaFilled(false);
        btnLancamentoManual.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLancamentoManual.setPreferredSize(new java.awt.Dimension(120, 23));
        btnLancamentoManual.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLancamentoManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLancamentoManualActionPerformed(evt);
            }
        });

        btnConfirmarEntregaDevolucao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/flag_blue.png"))); // NOI18N
        btnConfirmarEntregaDevolucao.setText("Confirmar");
        btnConfirmarEntregaDevolucao.setContentAreaFilled(false);
        btnConfirmarEntregaDevolucao.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnConfirmarEntregaDevolucao.setPreferredSize(new java.awt.Dimension(120, 23));
        btnConfirmarEntregaDevolucao.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnConfirmarEntregaDevolucao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarEntregaDevolucaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnLancamentoManual, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnConfirmarEntregaDevolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLancamentoManual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnConfirmarEntregaDevolucao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jLabel12.setText("Estoque atual");

        txtEstoqueAtual.setEditable(false);
        txtEstoqueAtual.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jButton1.setText("Atualizar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addGap(18, 18, 18)
                        .addComponent(txtEstoqueAtual, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtEstoqueAtual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown

    }//GEN-LAST:event_formComponentShown

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
    }//GEN-LAST:event_formInternalFrameActivated

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        produtoEstoqueViews.remove(this);
    }//GEN-LAST:event_formInternalFrameClosed

    private void btnLancamentoManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLancamentoManualActionPerformed
        lancarManual();
    }//GEN-LAST:event_btnLancamentoManualActionPerformed

    private void btnConfirmarEntregaDevolucaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarEntregaDevolucaoActionPerformed
        confirmarEntregaDevolucao();
    }//GEN-LAST:event_btnConfirmarEntregaDevolucaoActionPerformed

    private void tblEstoqueMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblEstoqueMouseClicked
        if (evt.getClickCount() == 2) {
            detalhe();
        }
    }//GEN-LAST:event_tblEstoqueMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        carregarTabela();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirmarEntregaDevolucao;
    private javax.swing.JButton btnLancamentoManual;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblEstoque;
    private javax.swing.JTextField txtComponentes;
    private javax.swing.JTextField txtCriacao;
    private javax.swing.JTextField txtDataEntrada;
    private javax.swing.JTextField txtDataEntradaPrevista;
    private javax.swing.JTextField txtDataSaida;
    private javax.swing.JTextField txtDataSaidaPrevista;
    private javax.swing.JTextField txtDevolucaoOrigemId;
    private javax.swing.JTextField txtEstoqueAtual;
    private javax.swing.JTextField txtEstorno;
    private javax.swing.JTextField txtEstornoOrigem;
    private javax.swing.JTextField txtOrigem;
    private javax.swing.JTextField txtOrigemId;
    // End of variables declaration//GEN-END:variables
}
