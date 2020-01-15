/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.catalogo.geral;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import model.mysql.bean.principal.catalogo.Categoria;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.fiscal.UnidadeComercial;
import model.mysql.dao.principal.catalogo.CategoriaDAO;
import model.mysql.dao.principal.catalogo.ProdutoDAO;
import model.mysql.dao.fiscal.UnidadeComercialDAO;
import model.jtable.catalogo.ProdutoJTableModel;
import model.mysql.bean.principal.catalogo.ProdutoTipo;
import model.mysql.dao.principal.catalogo.ProdutoTipoDAO;
import static ouroboros.Constants.*;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.JSwing;
import util.jTableFormat.LineWrapCellRenderer;
import view.catalogo.item.ProdutoContainerView;
import view.catalogo.item.ProdutoEstoqueLancamentoView;

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
        carregarTipos();
        carregarBalancaFiltro();
        
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

        tblProdutos.setRowHeight(30);
        tblProdutos.setIntercellSpacing(new Dimension(10, 10));
        tblProdutos.setDefaultRenderer(String.class, new LineWrapCellRenderer());
        
        //id
        tblProdutos.getColumnModel().getColumn(0).setPreferredWidth(120);
        tblProdutos.getColumnModel().getColumn(0).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblProdutos.getColumn("Descrição").setPreferredWidth(700);
        tblProdutos.getColumn("Descrição").setCellRenderer(CELL_RENDERER_ALIGN_LEFT);
        
        tblProdutos.getColumn("Aplicação").setPreferredWidth(400);
        tblProdutos.getColumn("Aplicação").setCellRenderer(CELL_RENDERER_ALIGN_LEFT);
        
        tblProdutos.getColumn("Valor").setPreferredWidth(120);
        
        
        tblProdutos.getColumn("Código").setPreferredWidth(200);
        tblProdutos.getColumn("Código").setCellRenderer(CELL_RENDERER_ALIGN_LEFT);
        
        tblProdutos.getColumn("Unidade").setPreferredWidth(100);
        
        tblProdutos.getColumn("Tipo").setPreferredWidth(60);
        tblProdutos.getColumn("Tipo").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblProdutos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                carregarDetalhes();
            }

        });
    }
    
    private void carregarDetalhes() {
        
        if (tblProdutos.getSelectedRow() > -1) {
            int index = tblProdutos.getSelectedRow();

            Produto p = produtoJTableModel.getRow(index);
            txtEstoqueAtual.setText(p.getEstoqueAtualComUnidade());
            txtLocalizacao.setText(p.getLocalizacao());
            
        } else {
            txtEstoqueAtual.setText("");
            txtLocalizacao.setText("");
        }
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
        ProdutoTipo produtoTipo = (ProdutoTipo) cboTipo.getSelectedItem();
        boolean apenasItemBalanca = cboBalanca.getSelectedIndex() == 1;
        
        Optional<Boolean> necessidadeCompra = cboNecessidadeCompra.getSelectedIndex() == 0 ? Optional.empty() : 
                (cboNecessidadeCompra.getSelectedIndex() == 1 ? Optional.of(true) : Optional.of(false));
        
        Optional<Boolean> estoqueMinimo = cboEstoqueMinimo.getSelectedIndex() == 0 ? Optional.empty() : 
                (cboEstoqueMinimo.getSelectedIndex() == 1 ? Optional.of(true) : Optional.of(false));
        
        listProduto = produtoDAO.findByCriteria(buscaRapida, categoria, unidadeVenda, produtoTipo, apenasItemBalanca, necessidadeCompra, estoqueMinimo, false);
        
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
                    //System.out.println("diferente");

                    produtoJTableModel.updateRow(oldProduto, produto);

                }else{
                    //System.out.println("igual");
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
        }
    }
    
    private void carregarTipos(){
        List<ProdutoTipo> tipos = new ProdutoTipoDAO().findAll();
        ProdutoTipo noFilter = new ProdutoTipo();
        noFilter.setId(0);
        noFilter.setNome("Todos");
        cboTipo.addItem(noFilter);
        for (ProdutoTipo t : tipos) {
            cboTipo.addItem(t);
        }
    }
    
    private void carregarBalancaFiltro() {
        cboBalanca.addItem("Todos");
        cboBalanca.addItem("Apenas balança");
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
            
            try {
                tblProdutos.setRowSelectionInterval(rowIndex, rowIndex);
                tblProdutos.scrollRectToVisible(tblProdutos.getCellRect(rowIndex, 0, true));
                tblProdutos.requestFocus();
            } catch(Exception e) {
                //nothing
            }
        }
    }
    
    private void imprimirEtiqueta() {
        List<Produto> listEtiqueta = new ArrayList<>();
        
        Set<Integer> setIds = new HashSet<>();
        for(int rowIndex : tblProdutos.getSelectedRows()) {
            listEtiqueta.add(produtoJTableModel.getRow(rowIndex));
        }
        
        new ProdutoEtiqueta(listEtiqueta);
    }
    
    private void imprimirLista() {
        new ProdutoListaImprimirView(listProduto);
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
        btnRemoverFiltro = new javax.swing.JButton();
        cboCategoria = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cboTipo = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        cboBalanca = new javax.swing.JComboBox<>();
        cboNecessidadeCompra = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        cboEstoqueMinimo = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txtEstoqueAtual = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtLocalizacao = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        btnImprimir = new javax.swing.JButton();
        btnEntrada = new javax.swing.JButton();
        btnEntrada1 = new javax.swing.JButton();
        btnEntrada2 = new javax.swing.JButton();
        btnEntrada3 = new javax.swing.JButton();
        btnImprimir1 = new javax.swing.JButton();

        setClosable(true);
        setTitle("Catálogo");
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

        tblProdutos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
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

        lblMensagem.setText("Consulta realizada em Xs");

        lblRegistrosExibidos.setText("0");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cboUnidadeVenda.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboUnidadeVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboUnidadeVendaActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Unidade");

        txtBuscaRapida.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtBuscaRapida.setToolTipText("");
        txtBuscaRapida.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscaRapidaKeyReleased(evt);
            }
        });

        btnRemoverFiltro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnRemoverFiltro.setText("Limpar");
        btnRemoverFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverFiltroActionPerformed(evt);
            }
        });

        cboCategoria.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCategoriaActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Categoria");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Busca rápida (descrição, aplicação ou código)");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("Tipo");

        cboTipo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTipoActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("Balança");

        cboBalanca.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboBalanca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboBalancaActionPerformed(evt);
            }
        });

        cboNecessidadeCompra.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboNecessidadeCompra.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "---", "Sim", "Não" }));
        cboNecessidadeCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboNecessidadeCompraActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel10.setText("Necessidade de Compra");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setText("Estoque Mínimo");

        cboEstoqueMinimo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboEstoqueMinimo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "---", "Sim", "Não" }));
        cboEstoqueMinimo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboEstoqueMinimoActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel13.setText("OU");

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
                        .addComponent(cboCategoria, 0, 172, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(cboUnidadeVenda, 0, 172, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(cboTipo, 0, 172, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(cboBalanca, 0, 172, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btnRemoverFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(txtBuscaRapida))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(18, 18, 18)
                                .addComponent(cboNecessidadeCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel13)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel11)
                                .addGap(18, 18, 18)
                                .addComponent(cboEstoqueMinimo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(10, 10, 10))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtBuscaRapida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(cboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)
                        .addComponent(cboBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(cboCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)
                        .addComponent(cboUnidadeVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnRemoverFiltro)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(cboNecessidadeCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(cboEstoqueMinimo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setText("Registros exibidos:");

        jLabel1.setText("Duplo clique no item para editar");

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtEstoqueAtual.setEditable(false);
        txtEstoqueAtual.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtEstoqueAtual.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Estoque");

        jLabel35.setBackground(new java.awt.Color(122, 138, 153));
        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel35.setForeground(java.awt.Color.white);
        jLabel35.setText("Informações do item selecionado");
        jLabel35.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel35.setOpaque(true);

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("Localização");

        txtLocalizacao.setEditable(false);
        txtLocalizacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtLocalizacao.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtEstoqueAtual, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(txtLocalizacao)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtLocalizacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtEstoqueAtual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-printer-20.png"))); // NOI18N
        btnImprimir.setText("Etiqueta");
        btnImprimir.setContentAreaFilled(false);
        btnImprimir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnImprimir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        btnEntrada.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-add-20.png"))); // NOI18N
        btnEntrada.setText("Novo");
        btnEntrada.setContentAreaFilled(false);
        btnEntrada.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEntrada.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntradaActionPerformed(evt);
            }
        });

        btnEntrada1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-trolley-with-boxes-20.png"))); // NOI18N
        btnEntrada1.setText("Estoque");
        btnEntrada1.setContentAreaFilled(false);
        btnEntrada1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEntrada1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEntrada1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntrada1ActionPerformed(evt);
            }
        });

        btnEntrada2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-industrial-scales-20.png"))); // NOI18N
        btnEntrada2.setText("Balança");
        btnEntrada2.setContentAreaFilled(false);
        btnEntrada2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEntrada2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEntrada2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntrada2ActionPerformed(evt);
            }
        });

        btnEntrada3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-close-button-20.png"))); // NOI18N
        btnEntrada3.setText("Excluir");
        btnEntrada3.setContentAreaFilled(false);
        btnEntrada3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEntrada3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEntrada3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntrada3ActionPerformed(evt);
            }
        });

        btnImprimir1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-printer-20.png"))); // NOI18N
        btnImprimir1.setText("Lista");
        btnImprimir1.setContentAreaFilled(false);
        btnImprimir1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnImprimir1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimir1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimir1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnEntrada)
                .addGap(18, 18, 18)
                .addComponent(btnEntrada1)
                .addGap(18, 18, 18)
                .addComponent(btnEntrada2)
                .addGap(18, 18, 18)
                .addComponent(btnEntrada3)
                .addGap(18, 18, 18)
                .addComponent(btnImprimir)
                .addGap(18, 18, 18)
                .addComponent(btnImprimir1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnImprimir)
                    .addComponent(btnEntrada)
                    .addComponent(btnEntrada1)
                    .addComponent(btnEntrada2)
                    .addComponent(btnEntrada3)
                    .addComponent(btnImprimir1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblMensagem)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRegistrosExibidos, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMensagem)
                    .addComponent(jLabel4)
                    .addComponent(lblRegistrosExibidos)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        txtBuscaRapida.requestFocus();
    }//GEN-LAST:event_formComponentShown

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

    private void btnRemoverFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverFiltroActionPerformed
        txtBuscaRapida.setText("");
        cboCategoria.setSelectedIndex(0);
        cboUnidadeVenda.setSelectedIndex(0);
        cboTipo.setSelectedIndex(0);
        cboBalanca.setSelectedIndex(0);
        cboNecessidadeCompra.setSelectedIndex(0);
        cboEstoqueMinimo.setSelectedIndex(0);
        
        carregarTabela();
        
        txtBuscaRapida.requestFocus();
    }//GEN-LAST:event_btnRemoverFiltroActionPerformed

    private void tblProdutosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblProdutosFocusGained
        tableProdutosUpdateRow();
        txtBuscaRapida.requestFocus();
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

    private void cboNecessidadeCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboNecessidadeCompraActionPerformed
        carregarTabela();
    }//GEN-LAST:event_cboNecessidadeCompraActionPerformed

    private void cboCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCategoriaActionPerformed
        carregarTabela();
    }//GEN-LAST:event_cboCategoriaActionPerformed

    private void cboTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTipoActionPerformed
        carregarTabela();
    }//GEN-LAST:event_cboTipoActionPerformed

    private void cboBalancaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboBalancaActionPerformed
        carregarTabela();
    }//GEN-LAST:event_cboBalancaActionPerformed

    private void cboEstoqueMinimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboEstoqueMinimoActionPerformed
        carregarTabela();
    }//GEN-LAST:event_cboEstoqueMinimoActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        imprimirEtiqueta();
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void btnEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntradaActionPerformed
        novo();
    }//GEN-LAST:event_btnEntradaActionPerformed

    private void btnEntrada1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntrada1ActionPerformed
        estoqueManual();
    }//GEN-LAST:event_btnEntrada1ActionPerformed

    private void btnEntrada2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntrada2ActionPerformed
        gerarArquivoBalanca();
    }//GEN-LAST:event_btnEntrada2ActionPerformed

    private void btnEntrada3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntrada3ActionPerformed
        excluir();
    }//GEN-LAST:event_btnEntrada3ActionPerformed

    private void btnImprimir1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimir1ActionPerformed
        imprimirLista();
    }//GEN-LAST:event_btnImprimir1ActionPerformed

    private void cboUnidadeVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboUnidadeVendaActionPerformed
        carregarTabela();
    }//GEN-LAST:event_cboUnidadeVendaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEntrada;
    private javax.swing.JButton btnEntrada1;
    private javax.swing.JButton btnEntrada2;
    private javax.swing.JButton btnEntrada3;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnImprimir1;
    private javax.swing.JButton btnRemoverFiltro;
    private javax.swing.JComboBox<String> cboBalanca;
    private javax.swing.JComboBox<Object> cboCategoria;
    private javax.swing.JComboBox<String> cboEstoqueMinimo;
    private javax.swing.JComboBox<String> cboNecessidadeCompra;
    private javax.swing.JComboBox<Object> cboTipo;
    private javax.swing.JComboBox<Object> cboUnidadeVenda;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMensagem;
    private javax.swing.JLabel lblRegistrosExibidos;
    private javax.swing.JTable tblProdutos;
    private javax.swing.JTextField txtBuscaRapida;
    private javax.swing.JTextField txtEstoqueAtual;
    private javax.swing.JTextField txtLocalizacao;
    // End of variables declaration//GEN-END:variables
}
