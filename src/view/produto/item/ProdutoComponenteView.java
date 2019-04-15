/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.produto.item;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.catalogo.ProdutoComponente;
import model.mysql.dao.principal.ProdutoComponenteDAO;
import model.mysql.dao.principal.ProdutoDAO;
import model.jtable.catalogo.ComponenteJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.em;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class ProdutoComponenteView extends javax.swing.JInternalFrame {

    private static List<ProdutoComponenteView> produtoComponenteViews = new ArrayList<>(); //instâncias

    ComponenteJTableModel componenteJTableModel = new ComponenteJTableModel();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private Produto produto;

    private List<ProdutoComponente> listComponente = new ArrayList<>();

    public static ProdutoComponenteView getInstance(Produto produto) {
        for (ProdutoComponenteView produtoComponenteView : produtoComponenteViews) {
            if (produtoComponenteView.produto == produto) {
                return produtoComponenteView;
            }
        }
        produtoComponenteViews.add(new ProdutoComponenteView(produto));
        return produtoComponenteViews.get(produtoComponenteViews.size() - 1);
    }

    private ProdutoComponenteView() {
        initComponents();
    }

    private ProdutoComponenteView(Produto produto) {
        initComponents();
        //JSwing.startComponentsBehavior(this);

        this.produto = produto;

        formatarTabela();

        carregarTabela();
        
        carregarDados();
    }
    
    private void carregarDados() {
        BigDecimal totalCompra = BigDecimal.ZERO;
        BigDecimal totalVenda = BigDecimal.ZERO;
        
        if(!listComponente.isEmpty()) {
            totalCompra = listComponente.stream().map(ProdutoComponente::getTotalCompra).reduce(BigDecimal::add).get();
            totalVenda = listComponente.stream().map(ProdutoComponente::getTotalVenda).reduce(BigDecimal::add).get();
        }
        
        txtTotalCompra.setText(Decimal.toString(totalCompra));
        txtTotalVenda.setText(Decimal.toString(totalVenda));
    }
    
    private void formatarTabela() {
        tblComponente.setModel(componenteJTableModel);

        tblComponente.setRowHeight(24);
        tblComponente.setIntercellSpacing(new Dimension(10, 10));
        
        tblComponente.getColumn("Componente").setPreferredWidth(200);
        
        tblComponente.getColumn("Quantidade").setPreferredWidth(120);
        tblComponente.getColumn("Quantidade").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblComponente.getColumn("Valor Compra").setPreferredWidth(120);
        tblComponente.getColumn("Valor Compra").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblComponente.getColumn("Subtotal Compra").setPreferredWidth(120);
        tblComponente.getColumn("Subtotal Compra").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblComponente.getColumn("Valor Venda").setPreferredWidth(120);
        tblComponente.getColumn("Valor Venda").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblComponente.getColumn("Subtotal Venda").setPreferredWidth(120);
        tblComponente.getColumn("Subtotal Venda").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblComponente.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
//                carregarDetalhes();
            }

        });
    }

    private void carregarTabela() {
        //em.refresh(produto);

        System.out.println("componente carregar tabela...");
        
        //listComponente = produto.getMovimentosFisicos();
        listComponente = produto.getListProdutoComponente();

        componenteJTableModel.clear();
        componenteJTableModel.addList(listComponente);

        //posicionar na última linha
        int lastRow = tblComponente.getRowCount() - 1;
        if(lastRow >= 0){
            tblComponente.setRowSelectionInterval(lastRow, lastRow);
            tblComponente.scrollRectToVisible(tblComponente.getCellRect(lastRow, 0, true));
        }
        
    }
    
    private void adicionar() {
        ComponenteAdicionarView adicionar = new ComponenteAdicionarView(produto);
        carregarTabela();
        carregarDados();
    }
    
    private void remover() {
        if(tblComponente.getSelectedRow() > -1) {
            int resposta = JOptionPane.showConfirmDialog(MAIN_VIEW, "Remover componente?", "Atenção", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(resposta == JOptionPane.OK_OPTION) {
                ProdutoComponenteDAO pcDAO = new ProdutoComponenteDAO();
                ProdutoComponente produtoComponente = componenteJTableModel.getRow(tblComponente.getSelectedRow());

                produto.getListProdutoComponente().remove(produtoComponente);
                pcDAO.remove(produtoComponente);
                
                
                //2019-02-18 - Não consegui refatorar dessa forma :<
                //produto.removeComponente(produtoComponente);
                
                //produtoDAO.save(produto);
                

                carregarTabela();
                carregarDados();
            }
        }
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
        tblComponente = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        txtTotalCompra = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtTotalVenda = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnAdicionar = new javax.swing.JButton();
        btnRemover = new javax.swing.JButton();

        setTitle("Componentes");
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

        tblComponente.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblComponente);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtTotalCompra.setEditable(false);
        txtTotalCompra.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel1.setText("Total Compra");

        txtTotalVenda.setEditable(false);
        txtTotalVenda.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel2.setText("Total Venda");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txtTotalCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(txtTotalVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(252, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotalCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(txtTotalVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnAdicionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/add.png"))); // NOI18N
        btnAdicionar.setText("Adicionar");
        btnAdicionar.setContentAreaFilled(false);
        btnAdicionar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAdicionar.setPreferredSize(new java.awt.Dimension(120, 23));
        btnAdicionar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        btnRemover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/delete.png"))); // NOI18N
        btnRemover.setText("Remover");
        btnRemover.setContentAreaFilled(false);
        btnRemover.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRemover.setPreferredSize(new java.awt.Dimension(120, 23));
        btnRemover.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnRemover, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAdicionar, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                    .addComponent(btnRemover, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1267, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown

    }//GEN-LAST:event_formComponentShown

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
    }//GEN-LAST:event_formInternalFrameActivated

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        produtoComponenteViews.remove(this);
    }//GEN-LAST:event_formInternalFrameClosed

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        adicionar();
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void btnRemoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverActionPerformed
        remover();
    }//GEN-LAST:event_btnRemoverActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JButton btnRemover;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblComponente;
    private javax.swing.JTextField txtTotalCompra;
    private javax.swing.JTextField txtTotalVenda;
    // End of variables declaration//GEN-END:variables
}
