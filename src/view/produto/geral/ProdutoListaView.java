/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.produto.geral;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import model.bean.principal.Categoria;
import model.bean.principal.Produto;
import model.bean.fiscal.UnidadeComercial;
import model.dao.principal.CategoriaDAO;
import model.dao.principal.ProdutoDAO;
import model.dao.fiscal.UnidadeComercialDAO;
import model.jtable.ProdutoJTableModel;
import static ouroboros.Constants.*;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.JSwing;
import view.produto.item.ProdutoContainerView;
import view.produto.item.ProdutoEstoqueLancamentoView;
import view.venda.VendaView;

/**
 *
 * @author ivand
 */
public class ProdutoListaView extends javax.swing.JInternalFrame {
    private static ProdutoListaView singleInstance = null;
    ProdutoJTableModel produtoJTableModel = new ProdutoJTableModel();
    ProdutoDAO produtoDAO = new ProdutoDAO();

    List<Produto> listProduto = new ArrayList<>();

    public static ProdutoListaView getSingleInstance(){
        if(singleInstance == null){
            singleInstance = new ProdutoListaView();
        }
        return singleInstance;
    }
    
    /**
     * Creates new form ProdutoCadastroView
     */
    private ProdutoListaView() {
        initComponents();
        JSwing.startComponentsBehavior(this);
        
        formatarTabela();

        cboCategoriaLoad();
        cboUnidadeVendaLoad();
        
        carregarTabela();
        
        definirAtalhos();

    }
    
    private void definirAtalhos() {
        InputMap im = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = getActionMap();
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "novo");
        am.put("novo", new FormKeyStroke("F1"));
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "estoqueManual");
        am.put("estoqueManual", new FormKeyStroke("F2"));
    }
    
    protected class FormKeyStroke extends AbstractAction {

        private final String key;

        public FormKeyStroke(String key) {
            this.key = key;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (key) {
                case "F1":
                    novo();
                    break;
                case "F2":
                    estoqueManual();
                    break;
            }
        }
    }
    
    private void formatarTabela() {
        tblProdutos.setModel(produtoJTableModel);

        tblProdutos.setRowHeight(24);
        tblProdutos.setIntercellSpacing(new Dimension(10, 10));
        
        //id
        tblProdutos.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblProdutos.getColumnModel().getColumn(0).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //nome
        tblProdutos.getColumnModel().getColumn(1).setPreferredWidth(800);
        //descrição
        tblProdutos.getColumnModel().getColumn(2).setPreferredWidth(400);
        //valor
        tblProdutos.getColumnModel().getColumn(3).setPreferredWidth(120);
        tblProdutos.getColumnModel().getColumn(3).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //código
        tblProdutos.getColumnModel().getColumn(4).setPreferredWidth(200);
        //unidade comercial
        tblProdutos.getColumnModel().getColumn(5).setPreferredWidth(120);
        //estoque atual
        tblProdutos.getColumnModel().getColumn(6).setPreferredWidth(120);
        tblProdutos.getColumnModel().getColumn(6).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
    }
    
    private void novo() {
        MAIN_VIEW.addView(ProdutoContainerView.getInstance(new Produto()));
    }
    
    private void editar() {
        Produto produto = produtoJTableModel.getRow(tblProdutos.getSelectedRow());
        MAIN_VIEW.addView(ProdutoContainerView.getInstance(produto));
    }

    private void catchClick() {
        int indices[] = tblProdutos.getSelectedRows();

        ArrayList<Integer> ids = new ArrayList<>();
        for (int index : indices) {
            ids.add(produtoJTableModel.getRow(index).getId());
        }
        System.out.println("index: " + tblProdutos.getSelectedRow());
    }

    private void carregarTabela() {
        long start = System.currentTimeMillis();
        
        String buscaRapida = txtBuscaRapida.getText();
        Categoria categoria = (Categoria) cboCategoria.getSelectedItem();
        UnidadeComercial unidadeVenda = (UnidadeComercial) cboUnidadeVenda.getSelectedItem();
        
        listProduto = produtoDAO.findByCriteria(buscaRapida, categoria, unidadeVenda, false, false);
        
        produtoJTableModel.clear();
        produtoJTableModel.addList(listProduto);

        long elapsed = System.currentTimeMillis() - start;
        lblMensagem.setText("Consulta realizada em " + elapsed + "ms");

        lblRegistrosExibidos.setText(String.valueOf(listProduto.size()));
        
        //posicionar na primeira linha se não tiver linha selecionada
        if(tblProdutos.getSelectedRow() == -1 && tblProdutos.getRowCount() > 0) {
            tblProdutos.setRowSelectionInterval(0, 0);
            tblProdutos.scrollRectToVisible(tblProdutos.getCellRect(0, 0, true));
        }
    }
    
    private void tableProdutosUpdateRow(){
        //try{
            int rowIndex = tblProdutos.getSelectedRow();
            if(rowIndex >= 0) {
                Produto oldProduto = produtoJTableModel.getRow(rowIndex);
                int id = oldProduto.getId();
                Timestamp atualizacao = produtoJTableModel.getRow(rowIndex).getAtualizacao();

                Produto produto = produtoDAO.findById(id);
                if(produto.getAtualizacao() != atualizacao){
                    System.out.println("diferente");

                    produtoJTableModel.updateRow(oldProduto, produto);

                }else{
                    System.out.println("igual");
                }
            }
            /*
        }catch(Exception e){
            System.err.println("Erro em tableProdutosUpdateRow. " + e);
        }*/
    }
    
    private void cboCategoriaLoad(){
        List<Categoria> listCategoria = new CategoriaDAO().findAll();
        Categoria noFilter = new Categoria();
        noFilter.setId(0);
        noFilter.setNome("Todas");
        cboCategoria.addItem(noFilter);
        for (Categoria c : listCategoria) {
            cboCategoria.addItem(c);
        }
    }
    
    private void cboUnidadeVendaLoad(){
        List<UnidadeComercial> listUC = new UnidadeComercialDAO().findAll();
        UnidadeComercial noFilter = new UnidadeComercial();
        noFilter.setId(0);
        noFilter.setNome("Todas");
        cboUnidadeVenda.addItem(noFilter);
        for (UnidadeComercial uc : listUC) {
            cboUnidadeVenda.addItem(uc);
            /*if(uc.equals(produto.getUnidadeComercialVenda())){
                cboUnidadeVenda.setSelectedItem(uc);
            }*/
        }
    }

    private void estoqueManual() {
        int rowIndex = tblProdutos.getSelectedRow();
        Produto produto = produtoJTableModel.getRow(rowIndex);
        ProdutoEstoqueLancamentoView l = new ProdutoEstoqueLancamentoView(produto);
        carregarTabela();
        if(rowIndex >= 0) {
            tblProdutos.setRowSelectionInterval(rowIndex, rowIndex);
        }
    }
    
    private void gerarArquivoBalanca() {
        ArquivoBalancaView arquivoBalancaView = new ArquivoBalancaView();
    }
    
    private void excluir() {
        int rowIndex = tblProdutos.getSelectedRow();
        
        
        if(rowIndex >= 0) {
            Produto produto = produtoJTableModel.getRow(rowIndex);
            
            int resposta = JOptionPane.showConfirmDialog(MAIN_VIEW, "Excluir o item: " + produto.getNome() + "?", "Atenção", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

            if(resposta == JOptionPane.OK_OPTION) {
                produtoDAO.delete(produto);
                carregarTabela();
            }
        }
    }
    
    private void imprimirEtiqueta() {
        List<Produto> listEtiqueta = new ArrayList<>();
        
        Set<Integer> setIds = new HashSet<>();
        for(int rowIndex : tblProdutos.getSelectedRows()) {
            listEtiqueta.add(produtoJTableModel.getRow(rowIndex));
        }
        
        EtiquetaPreco imprimirEtiqueta = new EtiquetaPreco(listEtiqueta);
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
        tblProdutos = new javax.swing.JTable();
        lblMensagem = new javax.swing.JLabel();
        lblRegistrosExibidos = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        cboUnidadeVenda = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        txtBuscaRapida = new javax.swing.JTextField();
        btnFiltrar = new javax.swing.JButton();
        btnRemoverFiltro = new javax.swing.JButton();
        cboCategoria = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnNovo = new javax.swing.JButton();
        btnLancamentoManual = new javax.swing.JButton();
        btnArquivoBalanca = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        btnImprimirEtiqueta = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setClosable(true);
        setTitle("Produtos - Lista");
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameActivated(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
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

        tblProdutos.setModel(new javax.swing.table.DefaultTableModel(
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
        tblProdutos.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblProdutos.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblProdutosFocusGained(evt);
            }
        });
        tblProdutos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProdutosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProdutos);

        lblMensagem.setText("...");

        lblRegistrosExibidos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRegistrosExibidos.setText("0");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel6.setText("Unidade de Venda");

        txtBuscaRapida.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtBuscaRapida.setToolTipText("");
        txtBuscaRapida.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscaRapidaKeyReleased(evt);
            }
        });

        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnRemoverFiltro.setText("Remover Filtro");
        btnRemoverFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverFiltroActionPerformed(evt);
            }
        });

        jLabel5.setText("Categoria");

        jLabel2.setText("Busca rápida (nome ou código)");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(cboCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(cboUnidadeVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnRemoverFiltro)
                        .addGap(18, 18, 18)
                        .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtBuscaRapida, javax.swing.GroupLayout.PREFERRED_SIZE, 677, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtBuscaRapida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cboCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(cboUnidadeVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar)
                    .addComponent(btnRemoverFiltro))
                .addGap(41, 41, 41))
        );

        jLabel4.setText("Registros exibidos:");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnNovo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/add.png"))); // NOI18N
        btnNovo.setText("Novo");
        btnNovo.setContentAreaFilled(false);
        btnNovo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNovo.setIconTextGap(10);
        btnNovo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoActionPerformed(evt);
            }
        });

        btnLancamentoManual.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/package.png"))); // NOI18N
        btnLancamentoManual.setText("F2 Estoque Manual");
        btnLancamentoManual.setContentAreaFilled(false);
        btnLancamentoManual.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLancamentoManual.setPreferredSize(new java.awt.Dimension(120, 23));
        btnLancamentoManual.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLancamentoManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLancamentoManualActionPerformed(evt);
            }
        });

        btnArquivoBalanca.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/application_view_detail.png"))); // NOI18N
        btnArquivoBalanca.setText("Arquivo Balança");
        btnArquivoBalanca.setContentAreaFilled(false);
        btnArquivoBalanca.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnArquivoBalanca.setPreferredSize(new java.awt.Dimension(120, 23));
        btnArquivoBalanca.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnArquivoBalanca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnArquivoBalancaActionPerformed(evt);
            }
        });

        btnExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/delete.png"))); // NOI18N
        btnExcluir.setText("Excluir");
        btnExcluir.setContentAreaFilled(false);
        btnExcluir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExcluir.setPreferredSize(new java.awt.Dimension(120, 23));
        btnExcluir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        btnImprimirEtiqueta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/printer.png"))); // NOI18N
        btnImprimirEtiqueta.setText("Imprimir Etiqueta");
        btnImprimirEtiqueta.setContentAreaFilled(false);
        btnImprimirEtiqueta.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnImprimirEtiqueta.setPreferredSize(new java.awt.Dimension(120, 23));
        btnImprimirEtiqueta.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimirEtiqueta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirEtiquetaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnLancamentoManual, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnArquivoBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnImprimirEtiqueta, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLancamentoManual, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                    .addComponent(btnNovo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnArquivoBalanca, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                    .addComponent(btnExcluir, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                    .addComponent(btnImprimirEtiqueta, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE))
                .addContainerGap())
        );

        jLabel1.setText("Editar: duplo clique");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1264, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRegistrosExibidos, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMensagem, javax.swing.GroupLayout.PREFERRED_SIZE, 533, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblMensagem)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblRegistrosExibidos)
                            .addComponent(jLabel4)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        txtBuscaRapida.requestFocus();
    }//GEN-LAST:event_formComponentShown

    private void btnNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoActionPerformed
        novo();
    }//GEN-LAST:event_btnNovoActionPerformed

    private void tblProdutosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProdutosMouseClicked
        if (evt.getClickCount() == 2) {
            editar();
        }
        
    }//GEN-LAST:event_tblProdutosMouseClicked

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        MAIN_VIEW.removeTab(this.getName());
    }//GEN-LAST:event_formInternalFrameClosing

    private void txtBuscaRapidaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscaRapidaKeyReleased
        int index;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                editar();
                break;
            case KeyEvent.VK_DOWN:
                index = tblProdutos.getSelectedRow() + 1;
                if (index < tblProdutos.getRowCount()) {
                    tblProdutos.setRowSelectionInterval(index, index);
                    tblProdutos.scrollRectToVisible(tblProdutos.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_UP:
                index = tblProdutos.getSelectedRow() - 1;
                if (index > -1) {
                    tblProdutos.setRowSelectionInterval(index, index);
                    tblProdutos.scrollRectToVisible(tblProdutos.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_PAGE_DOWN:
                index = tblProdutos.getSelectedRow() + 10;
                if (index > tblProdutos.getRowCount() -1) {
                    index = tblProdutos.getRowCount() -1;
                }
                tblProdutos.setRowSelectionInterval(index, index);
                tblProdutos.scrollRectToVisible(tblProdutos.getCellRect(index, 0, true));
                break;
            case KeyEvent.VK_PAGE_UP:
                index = tblProdutos.getSelectedRow() - 10;
                if (index < 0) {
                    index = 0;
                }
                tblProdutos.setRowSelectionInterval(index, index);
                tblProdutos.scrollRectToVisible(tblProdutos.getCellRect(index, 0, true));
                break;
            default:
                carregarTabela();
        }
    }//GEN-LAST:event_txtBuscaRapidaKeyReleased

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnRemoverFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverFiltroActionPerformed
        txtBuscaRapida.setText("");
        cboCategoria.setSelectedIndex(0);
        cboUnidadeVenda.setSelectedIndex(0);
        
        carregarTabela();
        
        txtBuscaRapida.requestFocus();
    }//GEN-LAST:event_btnRemoverFiltroActionPerformed

    private void tblProdutosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblProdutosFocusGained
        tableProdutosUpdateRow();
        
    }//GEN-LAST:event_tblProdutosFocusGained

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        System.out.println("focus");
        tableProdutosUpdateRow();
    }//GEN-LAST:event_formFocusGained

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated

    }//GEN-LAST:event_formInternalFrameActivated

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        singleInstance = null;
    }//GEN-LAST:event_formInternalFrameClosed

    private void btnLancamentoManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLancamentoManualActionPerformed
        estoqueManual();
    }//GEN-LAST:event_btnLancamentoManualActionPerformed

    private void btnArquivoBalancaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnArquivoBalancaActionPerformed
        gerarArquivoBalanca();
    }//GEN-LAST:event_btnArquivoBalancaActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        excluir();
    }//GEN-LAST:event_btnExcluirActionPerformed

    private void btnImprimirEtiquetaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirEtiquetaActionPerformed
        imprimirEtiqueta();
    }//GEN-LAST:event_btnImprimirEtiquetaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnArquivoBalanca;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnImprimirEtiqueta;
    private javax.swing.JButton btnLancamentoManual;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton btnRemoverFiltro;
    private javax.swing.JComboBox<Object> cboCategoria;
    private javax.swing.JComboBox<Object> cboUnidadeVenda;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMensagem;
    private javax.swing.JLabel lblRegistrosExibidos;
    private javax.swing.JTable tblProdutos;
    private javax.swing.JTextField txtBuscaRapida;
    // End of variables declaration//GEN-END:variables
}
