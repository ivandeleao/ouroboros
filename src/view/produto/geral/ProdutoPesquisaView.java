/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.produto.geral;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.dao.principal.catalogo.ProdutoDAO;
import model.jtable.catalogo.ProdutoPesquisaJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_LEFT;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.jTableFormat.LineWrapCellRenderer;

/**
 *
 * @author ivand
 */
public class ProdutoPesquisaView extends javax.swing.JDialog {

    //private static ProdutoPesquisaView produtoPesquisaView;
    ProdutoPesquisaJTableModel produtoPesquisaJTableModel = new ProdutoPesquisaJTableModel();
    ProdutoDAO produtoDAO = new ProdutoDAO();

    List<Produto> produtos;

    Produto produto = null;
    Boolean codigoRepetido = false; //para exibir produtos com códigos iguais apenas

    public ProdutoPesquisaView() {
        super(MAIN_VIEW, true);
        initComponents();

        formatarTabela();

        carregarTabela();

        this.setLocationRelativeTo(MAIN_VIEW);
        this.setVisible(true);

    }

    public ProdutoPesquisaView(String buscar) {
        this(buscar, false);
    }
    
    public ProdutoPesquisaView(String buscar, boolean codigoRepetido) {
        super(MAIN_VIEW, true);
        initComponents();

        this.codigoRepetido = codigoRepetido;
        
        formatarTabela();

        txtBuscaRapida.setText(buscar);

        carregarTabela();

        this.setLocationRelativeTo(MAIN_VIEW);
        this.setVisible(true);
    }

    public Produto getProduto() {
        return produto;
    }

    private void carregarTabela() {
        long start = System.currentTimeMillis();

        String buscaRapida = txtBuscaRapida.getText();

        if(codigoRepetido) {
            produtos = produtoDAO.findByCodigo(buscaRapida);
            codigoRepetido = false;
            
        } else {
            produtos = produtoDAO.findByCriteria(buscaRapida, null, null, null, false, false);
        }

        produtoPesquisaJTableModel.clear();
        produtoPesquisaJTableModel.addList(produtos);

        if (tblProduto.getRowCount() > 0) {
            tblProduto.setRowSelectionInterval(0, 0);
        }

        long elapsed = System.currentTimeMillis() - start;
        lblMensagem.setText("Consulta realizada em " + elapsed + "ms");
    }

    private void formatarTabela() {
        tblProduto.setModel(produtoPesquisaJTableModel);

        tblProduto.setRowHeight(30);
        tblProduto.setIntercellSpacing(new Dimension(10, 10));
        //tblProduto.setDefaultRenderer(String.class, new LineWrapCellRenderer());
        //tblProduto.setSelectionBackground((Color)UIManager.get("Table.selectionBackground"));
        
        tblProduto.getColumn("Id").setPreferredWidth(100);
        tblProduto.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblProduto.getColumn("Nome").setPreferredWidth(800);
        tblProduto.getColumn("Nome").setCellRenderer(CELL_RENDERER_ALIGN_LEFT);
        
        tblProduto.getColumn("Descrição").setPreferredWidth(400);
        tblProduto.getColumn("Descrição").setCellRenderer(CELL_RENDERER_ALIGN_LEFT);
        
        tblProduto.getColumn("Valor Venda").setPreferredWidth(160);
        tblProduto.getColumn("Valor Venda").setCellRenderer(new LineWrapCellRenderer());

        tblProduto.getColumn("Código").setPreferredWidth(200);
        tblProduto.getColumn("Código").setCellRenderer(CELL_RENDERER_ALIGN_LEFT);

        //tblProduto.getColumn("Unidade").setPreferredWidth(120);
        tblProduto.getColumn("Tipo").setPreferredWidth(60);
        tblProduto.getColumn("Tipo").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblProduto.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                carregarDetalhes();
            }

        });
    }

    private void carregarDetalhes() {
        if (tblProduto.getSelectedRow() > -1) {
            int index = tblProduto.getSelectedRow();

            txtEstoqueAtual.setText(produtoPesquisaJTableModel.getRow(index).getEstoqueAtualComUnidade());
            //txtTamanhos.setText(produtoPesquisaJTableModel.getRow(index).getValorVendaComTamanhos());
        } else {
            txtEstoqueAtual.setText("");
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

        txtBuscaRapida = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProduto = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        lblMensagem = new javax.swing.JLabel();
        txtEstoqueAtual = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();

        setTitle("Pesquisar Produto");

        txtBuscaRapida.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtBuscaRapida.setToolTipText("");
        txtBuscaRapida.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtBuscaRapidaKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscaRapidaKeyReleased(evt);
            }
        });

        tblProduto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tblProduto.setModel(new javax.swing.table.DefaultTableModel(
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
        tblProduto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblProdutoFocusGained(evt);
            }
        });
        tblProduto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProdutoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProduto);

        jLabel1.setForeground(java.awt.Color.blue);
        jLabel1.setText("Rolar: PageUp e PageDown | Confirmar: Enter | Cancelar: Esc");

        lblMensagem.setText("Consulta realizada em 0ms");

        txtEstoqueAtual.setEditable(false);
        txtEstoqueAtual.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtEstoqueAtual.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Estoque do Item Posicionado");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtBuscaRapida)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1180, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(lblMensagem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(txtEstoqueAtual, javax.swing.GroupLayout.PREFERRED_SIZE, 403, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtBuscaRapida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblMensagem)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtEstoqueAtual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscaRapidaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscaRapidaKeyReleased
        int index;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                produto = null;
                dispose();
                break;
            case KeyEvent.VK_ENTER:
                produto = produtoPesquisaJTableModel.getRow(tblProduto.getSelectedRow());
                dispose();
                break;
            case KeyEvent.VK_DOWN:
                index = tblProduto.getSelectedRow() + 1;
                if (index < tblProduto.getRowCount()) {
                    tblProduto.setRowSelectionInterval(index, index);
                    tblProduto.scrollRectToVisible(tblProduto.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_UP:
                index = tblProduto.getSelectedRow() - 1;
                if (index > -1) {
                    tblProduto.setRowSelectionInterval(index, index);
                    tblProduto.scrollRectToVisible(tblProduto.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_PAGE_DOWN:
                index = tblProduto.getSelectedRow() + 10;
                if (index > tblProduto.getRowCount() - 1) {
                    index = tblProduto.getRowCount() - 1;
                }
                tblProduto.setRowSelectionInterval(index, index);
                tblProduto.scrollRectToVisible(tblProduto.getCellRect(index, 0, true));
                break;
            case KeyEvent.VK_PAGE_UP:
                index = tblProduto.getSelectedRow() - 10;
                if (index < 0) {
                    index = 0;
                }
                tblProduto.setRowSelectionInterval(index, index);
                tblProduto.scrollRectToVisible(tblProduto.getCellRect(index, 0, true));
                break;
            //Atalhos modo balcão (uso apenas do teclado numérico)
            //Atalhos modo balcão (uso apenas do teclado numérico)
            case KeyEvent.VK_NUMPAD8: // assumir a seta para cima
            case KeyEvent.VK_NUMPAD2: // assumir a seta para baixo
            case KeyEvent.VK_DIVIDE: // barra do teclado numérico ( / )
                txtBuscaRapida.setText("");
                break;
            default:
                carregarTabela();
        }
    }//GEN-LAST:event_txtBuscaRapidaKeyReleased

    private void tblProdutoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProdutoMouseClicked
        if (evt.getClickCount() == 2) {
            produto = produtoPesquisaJTableModel.getRow(tblProduto.getSelectedRow());
            dispose();
        }
    }//GEN-LAST:event_tblProdutoMouseClicked

    private void tblProdutoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblProdutoFocusGained
        txtBuscaRapida.requestFocus();
    }//GEN-LAST:event_tblProdutoFocusGained

    private void txtBuscaRapidaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscaRapidaKeyPressed
        int index;
        switch (evt.getKeyCode()) {
            //Atalhos modo balcão (uso apenas do teclado numérico)
            case KeyEvent.VK_NUMPAD8: // assumir a seta para cima
                index = tblProduto.getSelectedRow() - 1;
                if (index > -1) {
                    tblProduto.setRowSelectionInterval(index, index);
                    tblProduto.scrollRectToVisible(tblProduto.getCellRect(index, 0, true));
                }
                txtBuscaRapida.setText("");
                break;
            case KeyEvent.VK_NUMPAD2: // assumir a seta para baixo
                
                index = tblProduto.getSelectedRow() + 1;
                if (index < tblProduto.getRowCount()) {
                    tblProduto.setRowSelectionInterval(index, index);
                    tblProduto.scrollRectToVisible(tblProduto.getCellRect(index, 0, true));
                }
                txtBuscaRapida.setText("");
                break;
            case KeyEvent.VK_DIVIDE: // barra do teclado numérico ( / )
                produto = null;
                dispose();
                break;
        }
    }//GEN-LAST:event_txtBuscaRapidaKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMensagem;
    private javax.swing.JTable tblProduto;
    private javax.swing.JTextField txtBuscaRapida;
    private javax.swing.JTextField txtEstoqueAtual;
    // End of variables declaration//GEN-END:variables

}
