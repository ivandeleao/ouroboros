/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.produto.item;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.bean.principal.Categoria;
import model.bean.principal.Produto;
import model.bean.fiscal.Cest;
import model.bean.fiscal.Cfop;
import model.bean.fiscal.Icms;
import model.bean.fiscal.Ncm;
import model.bean.fiscal.ProdutoOrigem;
import model.bean.fiscal.UnidadeComercial;
import model.dao.principal.CategoriaDAO;
import model.dao.principal.ProdutoDAO;
import model.dao.fiscal.CestDAO;
import model.dao.fiscal.CfopDAO;
import model.dao.fiscal.IcmsDAO;
import model.dao.fiscal.NcmDAO;
import model.dao.fiscal.ProdutoOrigemDAO;
import model.dao.fiscal.UnidadeComercialDAO;
import util.Decimal;
import util.JSwing;
import static ouroboros.Ouroboros.MAIN_VIEW;
import view.Toast;
import view.produto.CestPesquisaView;
import view.produto.NcmPesquisaView;

/**
 *
 * @author ivand
 */
public class ProdutoCadastroView extends javax.swing.JInternalFrame {

    private static List<ProdutoCadastroView> produtoCadastroViews = new ArrayList<>(); //instâncias

    private ProdutoDAO produtoDAO = new ProdutoDAO();
    private Produto produto;

    public static ProdutoCadastroView getInstance(Produto produto) {
        for (ProdutoCadastroView produtoCadastroView : produtoCadastroViews) {
            if (produtoCadastroView.produto == produto) {
                return produtoCadastroView;
            }
        }
        produtoCadastroViews.add(new ProdutoCadastroView(produto));
        return produtoCadastroViews.get(produtoCadastroViews.size() - 1);
    }

    private ProdutoCadastroView() {
        initComponents();
        JSwing.startComponentsBehavior(this);
    }

    private ProdutoCadastroView(Produto produto) {
        initComponents();
        JSwing.startComponentsBehavior(this);

        txtCodigo.requestFocus();

        this.produto = produto;
        
        carregarDados();

        cboCategoriaLoad();
        cboUnidadeVendaLoad();
        cboOrigemLoad();
        cboCfopSaidaDentroDoEstadoLoad();
        cboCfopSaidaForaDoEstadoLoad();
        cboIcmsLoad();

        txtCodigo.requestFocus();

    }
    
    private void carregarDados() {
        if (produto.getNome() != null) {
            if (produto.getId() != null) {
                txtId.setText(produto.getId().toString());
            }

            txtCodigo.setText(produto.getCodigo());
            txtNome.setText(produto.getNome());
            txtDescricao.setText(produto.getDescricao());
            txtValorCompra.setText(Decimal.toString(produto.getValorCompra()));
            txtMargemLucro.setText(Decimal.toString(produto.getMargemLucro()));
            txtValorVenda.setText(Decimal.toString(produto.getValorVenda()));
            txtObservacao.setText(produto.getObservacao());

            if (produto.getNcm() != null) {
                txtNcm.setText(produto.getNcm().getCodigo());
                txtNcmDescricao.setText(produto.getNcm().getDescricao());
            }
            
            txtCest.setText(produto.getCest());

            txtAliquotaIcms.setText(Decimal.toString(produto.getAliquotaIcms()));
            
            chkBalanca.setSelected(produto.getBalanca());
        }
    }

    private void cboCategoriaLoad() {
        List<Categoria> listCategoria = new CategoriaDAO().findAll();

        cboCategoria.addItem(null);
        for (Categoria c : listCategoria) {
            cboCategoria.addItem(c);
        }
        if (produto != null && produto.getCategoria() != null) {
            cboCategoria.setSelectedItem(produto.getCategoria());
        }
    }

    private void cboUnidadeVendaLoad() {
        List<UnidadeComercial> listUC = new UnidadeComercialDAO().findAll();

        cboUnidadeVenda.addItem(null);
        for (UnidadeComercial uc : listUC) {
            cboUnidadeVenda.addItem(uc);
        }
        if (produto != null && produto.getUnidadeComercialVenda() != null) {
            cboUnidadeVenda.setSelectedItem(produto.getUnidadeComercialVenda());
        }
    }

    private void cboOrigemLoad() {
        //System.out.println("produto origem: " + produto.getOrigem().getNome());
        List<ProdutoOrigem> poList = new ProdutoOrigemDAO().findAll();

        cboOrigem.addItem(null);
        for (ProdutoOrigem po : poList) {
            cboOrigem.addItem(po);
        }
        if (produto != null && produto.getOrigem() != null) {
            cboOrigem.setSelectedItem(produto.getOrigem());
        }
    }

    private void cboCfopSaidaDentroDoEstadoLoad() {
        List<Cfop> cfopList = new CfopDAO().findAllSaidaDentroDoEstado();

        cboCfopSaidaDentroDoEstado.addItem(null);
        for (Cfop cfop : cfopList) {
            cboCfopSaidaDentroDoEstado.addItem(cfop);
        }
        if (produto != null && produto.getCfopSaidaDentroDoEstado() != null) {
            cboCfopSaidaDentroDoEstado.setSelectedItem(produto.getCfopSaidaDentroDoEstado());
        }
    }

    private void cboCfopSaidaForaDoEstadoLoad() {
        List<Cfop> cfopList = new CfopDAO().findAllSaidaForaDoEstado();

        cboCfopSaidaForaDoEstado.addItem(null);
        for (Cfop cfop : cfopList) {
            cboCfopSaidaForaDoEstado.addItem(cfop);
        }
        if (produto != null && produto.getCfopSaidaForaDoEstado() != null) {
            cboCfopSaidaForaDoEstado.setSelectedItem(produto.getCfopSaidaForaDoEstado());
        }
    }

    private void cboIcmsLoad() {
        List<Icms> icmsList = new IcmsDAO().findAll();

        cboIcms.addItem(null);
        for (Icms icms : icmsList) {
            cboIcms.addItem(icms);
        }
        if (produto != null && produto.getIcms() != null) {
            cboIcms.setSelectedItem(produto.getIcms());
        }
    }

    private void calcularValores(String referencia) {
        BigDecimal valorCompra, margemLucro, valorVenda;
        valorCompra = Decimal.fromString(txtValorCompra.getText());
        margemLucro = Decimal.fromString(txtMargemLucro.getText());
        valorVenda = Decimal.fromString(txtValorVenda.getText());

        switch (referencia) {
            case "compra":
            case "margem":
                if (margemLucro.compareTo(new BigDecimal(0)) > 0) {
                    //if (new BigDecimal(100).subtract(margemLucro).compareTo(new BigDecimal(0)) != 0) {
                    valorVenda = new BigDecimal(-100).multiply(valorCompra).divide(margemLucro.subtract(new BigDecimal(100)), 2, RoundingMode.HALF_UP);
                    System.out.println("valorVenda: " + valorVenda);
                    //}
                } else {
                    valorVenda = valorCompra;
                }
                txtValorVenda.setText(Decimal.toString(valorVenda));
                break;
            case "venda":
                // (valorVenda - valorCompra) * 100 / valorVenda
                margemLucro = (valorVenda.subtract(valorCompra)).multiply(new BigDecimal(100));
                if (valorVenda.compareTo(BigDecimal.ZERO) != 0) {
                    margemLucro = margemLucro.divide(valorVenda, 2, RoundingMode.HALF_UP);
                } else {
                    margemLucro = BigDecimal.ZERO;
                }
                txtMargemLucro.setText(Decimal.toString(margemLucro));
                break;

        }
    }

    private boolean validar() {
        boolean valido = true;

        String nome = txtNome.getText();
        Ncm ncm = null;

        if (nome.length() < 3) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Nome deve ter no mínimo 3 caracteres");
            txtNome.requestFocus();
            return false;
        } else if (!txtNcm.getText().isEmpty()) {
            ncm = new NcmDAO().findByCodigo(txtNcm.getText());
            if (ncm == null) {
                JOptionPane.showMessageDialog(rootPane, "NCM inválido", "Atenção", JOptionPane.WARNING_MESSAGE);
                txtNcm.requestFocus();
                return false;
            }
        }
        return valido;
    }

    private void salvar() {
        String codigo = txtCodigo.getText();
        String nome = txtNome.getText();
        String descricao = txtDescricao.getText();
        BigDecimal valorCompra = Decimal.fromString(txtValorCompra.getText());
        BigDecimal margemLucro = Decimal.fromString(txtMargemLucro.getText());
        BigDecimal valorVenda = Decimal.fromString(txtValorVenda.getText());
        Categoria categoria = (Categoria) cboCategoria.getSelectedItem();

        String observacao = txtObservacao.getText();

        UnidadeComercial unidadeVenda = (UnidadeComercial) cboUnidadeVenda.getSelectedItem();

        ProdutoOrigem origem = null;
        if (cboOrigem.getSelectedIndex() > 0) {
            origem = (ProdutoOrigem) cboOrigem.getSelectedItem();
        }

        Cfop cfopSaidaDentroDoEstado = null;
        if (cboCfopSaidaDentroDoEstado.getSelectedIndex() > 0) {
            cfopSaidaDentroDoEstado = (Cfop) cboCfopSaidaDentroDoEstado.getSelectedItem();
        }

        Cfop cfopSaidaForaDoEstado = null;
        if (cboCfopSaidaForaDoEstado.getSelectedIndex() > 0) {
            cfopSaidaForaDoEstado = (Cfop) cboCfopSaidaForaDoEstado.getSelectedItem();
        }

        Icms icms = null;
        if (cboIcms.getSelectedIndex() > 0) {
            icms = (Icms) cboIcms.getSelectedItem();
        }

        Ncm ncm = null;
        if (!txtNcm.getText().isEmpty()) {
            ncm = new NcmDAO().findByCodigo(txtNcm.getText());
            System.out.println("ncm: " + ncm.getDescricao());
        }
        
        String cest = txtCest.getText();

        BigDecimal aliquotaIcms = Decimal.fromString(txtAliquotaIcms.getText());

        boolean balanca = chkBalanca.isSelected();
        
        
        
        if (produto != null) {
            produto.setId(produto.getId()); //wtf ???
        }
        produto.setCodigo(codigo);
        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setValorCompra(valorCompra);
        produto.setMargemLucro(margemLucro);
        produto.setValorVenda(valorVenda);
        produto.setCategoria(categoria);
        produto.setObservacao(observacao);

        produto.setUnidadeComercialVenda(unidadeVenda);
        produto.setOrigem(origem);
        produto.setCfopSaidaDentroDoEstado(cfopSaidaDentroDoEstado);
        produto.setCfopSaidaForaDoEstado(cfopSaidaForaDoEstado);
        produto.setIcms(icms);
        produto.setNcm(ncm);
        produto.setCest(cest);
        produto.setAliquotaIcms(aliquotaIcms);
        
        produto.setBalanca(balanca);
        
        

        produto = produtoDAO.save(produto);

        txtId.setText(produto.getId().toString());

        JOptionPane.showMessageDialog(rootPane, "Dados salvos com sucesso");

        ProdutoContainerView.getInstance(produto).gerarTabs();

    }

    private void novo() {
        String nome = ProdutoContainerView.getInstance(produto).getName();
        System.out.println("nome da view: " + ProdutoContainerView.getInstance(produto).getName());
        MAIN_VIEW.removeView(nome);
        MAIN_VIEW.removeTab(nome);
        MAIN_VIEW.addView(ProdutoContainerView.getInstance(new Produto()));
    }

    private void copiar() {
        String nome = ProdutoContainerView.getInstance(produto).getName();
        System.out.println("nome da view: " + ProdutoContainerView.getInstance(produto).getName());
        MAIN_VIEW.removeView(nome);
        MAIN_VIEW.removeTab(nome);
        Produto clone = produto.deepClone();
        clone.setId(null);
        clone.setCodigo(null);
        MAIN_VIEW.addView(ProdutoContainerView.getInstance(clone));
    }

    private void pesquisarNcm() {
        NcmPesquisaView ncmPesquisaView = new NcmPesquisaView(MAIN_VIEW);

        Ncm ncm = ncmPesquisaView.getNcm();
        if (ncm != null) {
            txtNcm.setText(ncm.getCodigo());
            txtNcmDescricao.setText(ncm.getDescricao());
        }
        txtNcm.requestFocus();
    }

    private void buscarCest() {
        String codigoNcm = txtNcm.getText().trim();
        //txtCest.setText("");
        if (!codigoNcm.isEmpty()) {
            CestDAO cestDAO = new CestDAO();
            List<Cest> listCest = cestDAO.findByCodigoNcm(codigoNcm);
            if (listCest.size() == 1) {
                txtCest.setText(listCest.get(0).getCodigo());
            } else if (listCest.size() > 1) {
                new Toast("Mais de um CEST correspondente ao NCM foi encontrado. Escolha na lista...");
                pesquisarCest(codigoNcm);
            }
            txtCest.requestFocus();
        }
    }

    private void pesquisarCest(String codigoNcm) {
        CestPesquisaView cestPesquisaView = new CestPesquisaView(codigoNcm);
        Cest cest = cestPesquisaView.getCest();
        if (cest != null) {
            txtCest.setText(cest.getCodigo());
        }
        txtCest.requestFocus();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtDescricao = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtValorCompra = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtMargemLucro = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtValorVenda = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        cboUnidadeVenda = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        cboCategoria = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtObservacao = new javax.swing.JTextArea();
        chkBalanca = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        cboOrigem = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        cboCfopSaidaDentroDoEstado = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        cboCfopSaidaForaDoEstado = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        cboIcms = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        txtNcm = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtAliquotaIcms = new javax.swing.JFormattedTextField();
        txtNcmDescricao = new javax.swing.JTextField();
        btnPesquisarNcm = new javax.swing.JButton();
        txtCest = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        btnPesquisarCest = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        btnSalvarENovo = new javax.swing.JButton();
        btnSalvarECopiar = new javax.swing.JButton();

        setClosable(true);
        setTitle("Produto - Cadastro");
        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(1300, 600));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
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

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "O código pode ser informado livremente. Caso não informado, será preenchido com o Id."));

        jLabel1.setText("Id");

        txtId.setEditable(false);
        txtId.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel8.setText("Código de barras");

        jLabel2.setText("Nome");

        txtNome.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        jLabel10.setText("Descrição/Aplicação");

        jLabel3.setText("Valor de Compra");

        txtValorCompra.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorCompra.setText("0,00");
        txtValorCompra.setName("decimal"); // NOI18N
        txtValorCompra.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtValorCompraCaretUpdate(evt);
            }
        });
        txtValorCompra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorCompraKeyReleased(evt);
            }
        });

        jLabel4.setText("Margem de lucro %");

        txtMargemLucro.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMargemLucro.setText("9,9999");
        txtMargemLucro.setName("decimal"); // NOI18N
        txtMargemLucro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMargemLucroKeyReleased(evt);
            }
        });

        jLabel5.setText("Valor de Venda");

        txtValorVenda.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorVenda.setText("0,00");
        txtValorVenda.setName("decimal"); // NOI18N
        txtValorVenda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorVendaKeyReleased(evt);
            }
        });

        jLabel6.setText("Unidade de Venda");

        jLabel7.setText("Categoria");

        jLabel9.setText("Observação");

        txtObservacao.setColumns(20);
        txtObservacao.setLineWrap(true);
        txtObservacao.setRows(5);
        jScrollPane1.setViewportView(txtObservacao);

        chkBalanca.setText("Item de balança");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(jLabel6)))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(txtId)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(81, 81, 81))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtValorCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(22, 22, 22)
                                .addComponent(jLabel4))
                            .addComponent(cboUnidadeVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addComponent(cboCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtMargemLucro, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(jLabel5)
                                .addGap(26, 26, 26)
                                .addComponent(txtValorVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                            .addComponent(jLabel9))
                        .addGap(13, 13, 13))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(chkBalanca)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel8)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtValorCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3)
                                    .addComponent(txtMargemLucro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5)
                                    .addComponent(txtValorVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboUnidadeVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(cboCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkBalanca))))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        txtValorCompra.getAccessibleContext().setAccessibleName("");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Dados Fiscais"));

        jLabel11.setText("Origem");

        jLabel12.setText("CFOP dentro do Estado");

        jLabel13.setText("CFOP fora do Estado");

        jLabel14.setText("ICMS");

        jLabel15.setText("NCM");

        txtNcm.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNcmFocusLost(evt);
            }
        });

        jLabel16.setText("CEST");

        txtAliquotaIcms.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAliquotaIcms.setName("decimal"); // NOI18N

        txtNcmDescricao.setEditable(false);

        btnPesquisarNcm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/resource/img/zoom.png"))); // NOI18N
        btnPesquisarNcm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarNcmActionPerformed(evt);
            }
        });

        jLabel17.setText("Alíquota de ICMS %");

        btnPesquisarCest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/resource/img/zoom.png"))); // NOI18N
        btnPesquisarCest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarCestActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14)
                            .addComponent(jLabel17))
                        .addGap(71, 71, 71))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnPesquisarCest))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnPesquisarNcm)))
                        .addGap(18, 18, 18)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboCfopSaidaDentroDoEstado, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboCfopSaidaForaDoEstado, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboIcms, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboOrigem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtAliquotaIcms, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNcm, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(txtNcmDescricao))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtCest, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(cboOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(cboCfopSaidaDentroDoEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboCfopSaidaForaDoEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtNcm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15)
                        .addComponent(txtNcmDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnPesquisarNcm))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPesquisarCest, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel16))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAliquotaIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnSalvarENovo.setText("Salvar e Novo");
        btnSalvarENovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarENovoActionPerformed(evt);
            }
        });

        btnSalvarECopiar.setText("Salvar e Copiar");
        btnSalvarECopiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarECopiarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSalvarECopiar, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSalvarENovo, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvar)
                    .addComponent(btnSalvarENovo)
                    .addComponent(btnSalvarECopiar))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        produtoCadastroViews.remove(this);

    }//GEN-LAST:event_formInternalFrameClosed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        MAIN_VIEW.removeTab(this.getName());
    }//GEN-LAST:event_formInternalFrameClosing

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown

    }//GEN-LAST:event_formComponentShown

    private void txtMargemLucroKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMargemLucroKeyReleased
        calcularValores("margem");
    }//GEN-LAST:event_txtMargemLucroKeyReleased

    private void txtValorCompraCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtValorCompraCaretUpdate

    }//GEN-LAST:event_txtValorCompraCaretUpdate

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        if (validar()) {
            salvar();
        }
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void txtValorCompraKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorCompraKeyReleased
        calcularValores("compra");
    }//GEN-LAST:event_txtValorCompraKeyReleased

    private void txtValorVendaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorVendaKeyReleased
        calcularValores("venda");
    }//GEN-LAST:event_txtValorVendaKeyReleased

    private void btnSalvarENovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarENovoActionPerformed
        if (validar()) {
            salvar();
            novo();
        }
    }//GEN-LAST:event_btnSalvarENovoActionPerformed

    private void btnSalvarECopiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarECopiarActionPerformed
        if (validar()) {
            salvar();
            copiar();
        }
    }//GEN-LAST:event_btnSalvarECopiarActionPerformed

    private void btnPesquisarNcmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarNcmActionPerformed
        pesquisarNcm();
    }//GEN-LAST:event_btnPesquisarNcmActionPerformed

    private void txtNcmFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNcmFocusLost
        String codigo = txtNcm.getText().trim();
        txtNcm.setText(codigo);

        Ncm ncm = new NcmDAO().findByCodigo(codigo);
        if (ncm != null) {
            txtNcmDescricao.setText(ncm.getDescricao());

            buscarCest();
        } else {
            txtNcmDescricao.setText("NCM NÃO ENCONTRADO");
        }
    }//GEN-LAST:event_txtNcmFocusLost

    private void btnPesquisarCestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarCestActionPerformed
        pesquisarCest(null);
    }//GEN-LAST:event_btnPesquisarCestActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPesquisarCest;
    private javax.swing.JButton btnPesquisarNcm;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnSalvarECopiar;
    private javax.swing.JButton btnSalvarENovo;
    private javax.swing.JComboBox<Object> cboCategoria;
    private javax.swing.JComboBox<Object> cboCfopSaidaDentroDoEstado;
    private javax.swing.JComboBox<Object> cboCfopSaidaForaDoEstado;
    private javax.swing.JComboBox<Object> cboIcms;
    private javax.swing.JComboBox<Object> cboOrigem;
    private javax.swing.JComboBox<Object> cboUnidadeVenda;
    private javax.swing.JCheckBox chkBalanca;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
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
    private javax.swing.JFormattedTextField txtAliquotaIcms;
    private javax.swing.JTextField txtCest;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtDescricao;
    private javax.swing.JTextField txtId;
    private javax.swing.JFormattedTextField txtMargemLucro;
    private javax.swing.JTextField txtNcm;
    private javax.swing.JTextField txtNcmDescricao;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextArea txtObservacao;
    private javax.swing.JFormattedTextField txtValorCompra;
    private javax.swing.JFormattedTextField txtValorVenda;
    // End of variables declaration//GEN-END:variables
}
