/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.produto.item;

import view.sistema.AjudaView;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.catalogo.Categoria;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.fiscal.Cest;
import model.mysql.bean.fiscal.Cfop;
import model.mysql.bean.fiscal.Icms;
import model.mysql.bean.fiscal.Ncm;
import model.mysql.bean.fiscal.ProdutoOrigem;
import model.mysql.bean.fiscal.UnidadeComercial;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.MovimentoFisicoTipo;
import model.mysql.bean.principal.catalogo.ProdutoTipo;
import model.mysql.dao.principal.catalogo.CategoriaDAO;
import model.mysql.dao.principal.catalogo.ProdutoDAO;
import model.mysql.dao.fiscal.CestDAO;
import model.mysql.dao.fiscal.CfopDAO;
import model.mysql.dao.fiscal.IcmsDAO;
import model.mysql.dao.fiscal.NcmDAO;
import model.mysql.dao.fiscal.ProdutoOrigemDAO;
import model.mysql.dao.fiscal.UnidadeComercialDAO;
import model.mysql.dao.principal.MovimentoFisicoDAO;
import model.mysql.dao.principal.catalogo.ProdutoTipoDAO;
import util.Decimal;
import util.JSwing;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.Numero;
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
    private static boolean apenasNovo;

    public static ProdutoCadastroView getInstanceApenasNovo(Produto produto) {
        apenasNovo = true;
        return getInstance(produto);
    }
    
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
        
        configurarTela();
        
        carregarDados();

        cboCategoriaLoad();
        cboUnidadeVendaLoad();
        carregarConteudoUnidade();
        carregarTipo();
        cboOrigemLoad();
        cboCfopSaidaDentroDoEstadoLoad();
        cboCfopSaidaForaDoEstadoLoad();
        cboIcmsLoad();

        txtCodigo.requestFocus();

    }
    
    private void configurarTela() {
        btnSalvarECopiar.setVisible(!apenasNovo);
        btnSalvarENovo.setVisible(!apenasNovo);
    }
    
    private void carregarDados() {
        if (produto.getNome() != null) {
            if (produto.getId() != null) {
                txtId.setText(produto.getId().toString());
                lblEstoqueInicial.setVisible(false);
                txtEstoqueInicial.setVisible(false);
            } else {
                lblEstoqueInicial.setVisible(true);
                txtEstoqueInicial.setVisible(true);
            }

            txtCodigo.setText(produto.getCodigo());
            txtNome.setText(produto.getNome());
            txtNome.setCaretPosition(0);
            txtDescricao.setText(produto.getDescricao());
            txtValorCompra.setText(Decimal.toString(produto.getValorCompra()));
            txtMargemLucro.setText(Decimal.toString(produto.getMargemLucro()));
            txtValorVenda.setText(Decimal.toString(produto.getValorVenda()));
            txtOutrosCodigos.setText(produto.getOutrosCodigos());
            txtLocalizacao.setText(produto.getLocalizacao());
            txtConteudoQuantidade.setText(Decimal.toString(produto.getConteudoQuantidade(), 3));
            txtObservacao.setText(produto.getObservacao());

            if (produto.getNcm() != null) {
                txtNcm.setText(produto.getNcm().getCodigo());
                txtNcmDescricao.setText(produto.getNcm().getDescricao());
            }
            
            txtCest.setText(produto.getCest());

            txtAliquotaIcms.setText(Decimal.toString(produto.getAliquotaIcms()));
            
            chkBalanca.setSelected(produto.getBalanca());
            
            txtDiasValidade.setText(produto.getDiasValidade().toString());
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
    
    private void carregarConteudoUnidade() {
        List<UnidadeComercial> unidades = new UnidadeComercialDAO().findAll();

        cboConteudoUnidade.addItem(null);
        for (UnidadeComercial uc : unidades) {
            cboConteudoUnidade.addItem(uc);
        }
        if (produto != null && produto.getUnidadeComercialVenda() != null) {
            cboConteudoUnidade.setSelectedItem(produto.getConteudoUnidade());
        }
    }
    
    private void carregarTipo() {
        List<ProdutoTipo> tipos = new ProdutoTipoDAO().findAll();

        for (ProdutoTipo t : tipos) {
            cboTipo.addItem(t);
        }
        if (produto != null && produto.getProdutoTipo()!= null) {
            cboTipo.setSelectedItem(produto.getProdutoTipo());
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
                if(margemLucro.compareTo(new BigDecimal(100)) >= 0) {
                    margemLucro = BigDecimal.ZERO;
                    txtMargemLucro.setText(Decimal.toString(margemLucro));
                }
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
        UnidadeComercial unidade = (UnidadeComercial) cboUnidadeVenda.getSelectedItem();

        if (nome.length() < 3) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Nome deve ter no mínimo 3 caracteres", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtNome.requestFocus();
            return false;
            
        } else if(unidade == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Informe a unidade do produto", "Atenção", JOptionPane.WARNING_MESSAGE);
            return false;
            
        } else if (!txtNcm.getText().isEmpty()) {
            Ncm ncm = new NcmDAO().findByCodigo(txtNcm.getText());
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
        String outrosCodigos = txtOutrosCodigos.getText();
        String localizacao = txtLocalizacao.getText();
        
        ProdutoTipo produtoTipo = (ProdutoTipo) cboTipo.getSelectedItem();
        String observacao = txtObservacao.getText();

        UnidadeComercial unidadeVenda = (UnidadeComercial) cboUnidadeVenda.getSelectedItem();
        
        BigDecimal conteudoQuantidade = Decimal.fromString(txtConteudoQuantidade.getText());
        UnidadeComercial conteudoUnidade = (UnidadeComercial) cboConteudoUnidade.getSelectedItem();

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
        
        Integer diasValidade = Numero.fromStringToInteger(txtDiasValidade.getText());
        
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
        produto.setOutrosCodigos(outrosCodigos);
        produto.setLocalizacao(localizacao);
        produto.setProdutoTipo(produtoTipo);
        produto.setObservacao(observacao);

        produto.setUnidadeComercialVenda(unidadeVenda);
        produto.setConteudoQuantidade(conteudoQuantidade);
        produto.setConteudoUnidade(conteudoUnidade);
        produto.setOrigem(origem);
        produto.setCfopSaidaDentroDoEstado(cfopSaidaDentroDoEstado);
        produto.setCfopSaidaForaDoEstado(cfopSaidaForaDoEstado);
        produto.setIcms(icms);
        produto.setNcm(ncm);
        produto.setCest(cest);
        produto.setAliquotaIcms(aliquotaIcms);
        
        produto.setBalanca(balanca);
        
        produto.setDiasValidade(diasValidade);
        
        produto = produtoDAO.save(produto);

        txtId.setText(produto.getId().toString());
        
        BigDecimal entrada = Decimal.fromString(txtEstoqueInicial.getText());
        
        if(entrada.compareTo(BigDecimal.ZERO) > 0) {
            MovimentoFisicoDAO movimentoFisicoDAO = new MovimentoFisicoDAO();
            MovimentoFisico movimentoFisico = new MovimentoFisico(produto, 
                    produto.getCodigo(), 
                    produto.getNome(),
                    entrada, 
                    BigDecimal.ZERO, 
                    BigDecimal.ZERO, 
                    BigDecimal.ZERO, 
                    produto.getUnidadeComercialVenda(), 
                    MovimentoFisicoTipo.LANCAMENTO_MANUAL, 
                    "Estoque Inicial");
            movimentoFisico.setDataEntrada(LocalDateTime.now());

            movimentoFisico = movimentoFisicoDAO.save(movimentoFisico);

            produto.addMovimentoFisico(movimentoFisico);
        }
        
        txtEstoqueInicial.setText("0");
        lblEstoqueInicial.setVisible(false);
        txtEstoqueInicial.setVisible(false);
        
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
        MAIN_VIEW.addView(ProdutoContainerView.getInstance(produto.copiar()));
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
    
    private void validarCodigo() {
        txtCodigo.setText(txtCodigo.getText().trim());
        String codigo = txtCodigo.getText();
        List<Produto> produtos = produtoDAO.findByCodigo(codigo);
        
        produtos.remove(produto);
        
        if(!produtos.isEmpty()) {
            String mensagem = "Já Exite(m) produto(s) com este código: \n";
            for(Produto p : produtos) {
                mensagem += p.getNome() + " \n";
            }
            
            JOptionPane.showMessageDialog(MAIN_VIEW, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
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
        jLabel18 = new javax.swing.JLabel();
        txtOutrosCodigos = new javax.swing.JTextField();
        txtLocalizacao = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        lblEstoqueInicial = new javax.swing.JLabel();
        txtEstoqueInicial = new javax.swing.JFormattedTextField();
        jLabel34 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        cboTipo = new javax.swing.JComboBox<>();
        cboConteudoUnidade = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        txtConteudoQuantidade = new javax.swing.JFormattedTextField();
        jLabel23 = new javax.swing.JLabel();
        txtDiasValidade = new javax.swing.JFormattedTextField();
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
        jLabel35 = new javax.swing.JLabel();
        btnSalvar = new javax.swing.JButton();
        btnSalvarENovo = new javax.swing.JButton();
        btnSalvarECopiar = new javax.swing.JButton();
        btnAjuda = new javax.swing.JButton();

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

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Id");

        txtId.setEditable(false);
        txtId.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtId.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("Código");

        txtCodigo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCodigo.setToolTipText("Preencha livremente ou o sistema preencherá com o Id");
        txtCodigo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCodigoFocusLost(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Nome");

        txtNome.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNome.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel10.setText("Aplicação");

        txtDescricao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Valor de Compra");

        txtValorCompra.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorCompra.setText("0,00");
        txtValorCompra.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
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

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("Margem de lucro %");

        txtMargemLucro.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMargemLucro.setText("9,9999");
        txtMargemLucro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtMargemLucro.setName("decimal"); // NOI18N
        txtMargemLucro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMargemLucroKeyReleased(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Valor de Venda");

        txtValorVenda.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorVenda.setText("0,00");
        txtValorVenda.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorVenda.setName("decimal"); // NOI18N
        txtValorVenda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorVendaKeyReleased(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Unidade de Venda");

        cboUnidadeVenda.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("Categoria");

        cboCategoria.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("Observação");

        txtObservacao.setColumns(20);
        txtObservacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtObservacao.setLineWrap(true);
        txtObservacao.setRows(5);
        jScrollPane1.setViewportView(txtObservacao);

        chkBalanca.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkBalanca.setText("Item de balança");

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel18.setText("Outros Códigos");

        txtOutrosCodigos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtOutrosCodigos.setToolTipText("Escreva separado por vírgula. Ex: 12AA, 333, XYC");

        txtLocalizacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel19.setText("Localização");

        lblEstoqueInicial.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblEstoqueInicial.setText("Estoque Inicial");

        txtEstoqueInicial.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtEstoqueInicial.setName("decimal(3)"); // NOI18N

        jLabel34.setBackground(new java.awt.Color(122, 138, 153));
        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel34.setForeground(java.awt.Color.white);
        jLabel34.setText("Dados Principais");
        jLabel34.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel34.setOpaque(true);

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel20.setText("Tipo");

        cboTipo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        cboConteudoUnidade.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel22.setText("Conteúdo");

        txtConteudoQuantidade.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtConteudoQuantidade.setName("decimal(3)"); // NOI18N

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel23.setText("Dias Validade");

        txtDiasValidade.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDiasValidade.setName("inteiro"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel18)
                        .addGap(18, 18, 18)
                        .addComponent(txtOutrosCodigos, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(txtValorCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(txtMargemLucro, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(txtValorVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel10)
                                .addGap(18, 18, 18)
                                .addComponent(txtDescricao))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addGap(18, 18, 18)
                                        .addComponent(cboUnidadeVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(chkBalanca)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel23)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtDiasValidade)))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel22)
                                .addGap(18, 18, 18)
                                .addComponent(txtConteudoQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboConteudoUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addGap(18, 18, 18)
                                .addComponent(cboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addComponent(cboCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel19)
                                .addGap(18, 18, 18)
                                .addComponent(txtLocalizacao, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblEstoqueInicial)
                                .addGap(18, 18, 18)
                                .addComponent(txtEstoqueInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(10, 10, 10))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel8)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(txtOutrosCodigos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtValorCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtMargemLucro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtValorVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cboCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(txtLocalizacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEstoqueInicial)
                    .addComponent(txtEstoqueInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(cboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel6)
                            .addComponent(cboUnidadeVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22)
                            .addComponent(txtConteudoQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboConteudoUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(txtDiasValidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkBalanca)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        txtValorCompra.getAccessibleContext().setAccessibleName("");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setText("Origem");

        cboOrigem.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel12.setText("CFOP dentro do Estado");

        cboCfopSaidaDentroDoEstado.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel13.setText("CFOP fora do Estado");

        cboCfopSaidaForaDoEstado.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel14.setText("ICMS");

        cboIcms.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel15.setText("NCM");

        txtNcm.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNcm.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNcmFocusLost(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel16.setText("CEST");

        txtAliquotaIcms.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAliquotaIcms.setName("decimal"); // NOI18N

        txtNcmDescricao.setEditable(false);
        txtNcmDescricao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        btnPesquisarNcm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/resource/img/zoom.png"))); // NOI18N
        btnPesquisarNcm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarNcmActionPerformed(evt);
            }
        });

        txtCest.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel17.setText("Alíquota de ICMS %");

        btnPesquisarCest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/resource/img/zoom.png"))); // NOI18N
        btnPesquisarCest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarCestActionPerformed(evt);
            }
        });

        jLabel35.setBackground(new java.awt.Color(122, 138, 153));
        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel35.setForeground(java.awt.Color.white);
        jLabel35.setText("Dados Fiscais");
        jLabel35.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel35.setOpaque(true);

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
                            .addComponent(jLabel14))
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
                    .addComponent(cboOrigem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtCest, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel17)
                        .addGap(18, 18, 18)
                        .addComponent(txtAliquotaIcms, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(cboCfopSaidaDentroDoEstado, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboCfopSaidaForaDoEstado, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboIcms, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtNcm, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtNcmDescricao)))
                .addContainerGap())
            .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel35)
                .addGap(18, 18, 18)
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
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAliquotaIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17))
                    .addComponent(txtCest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPesquisarCest, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel16))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnSalvar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnSalvarENovo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnSalvarENovo.setText("Salvar e Novo");
        btnSalvarENovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarENovoActionPerformed(evt);
            }
        });

        btnSalvarECopiar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnSalvarECopiar.setText("Salvar e Copiar");
        btnSalvarECopiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarECopiarActionPerformed(evt);
            }
        });

        btnAjuda.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-help-20.png"))); // NOI18N
        btnAjuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAjudaActionPerformed(evt);
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
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnAjuda)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSalvarECopiar)
                        .addGap(18, 18, 18)
                        .addComponent(btnSalvarENovo, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvar)
                    .addComponent(btnSalvarENovo)
                    .addComponent(btnSalvarECopiar)
                    .addComponent(btnAjuda))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void txtCodigoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCodigoFocusLost
        validarCodigo();
    }//GEN-LAST:event_txtCodigoFocusLost

    private void btnAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAjudaActionPerformed
        AjudaView ajuda = new AjudaView("produto_cadastro.html");
    }//GEN-LAST:event_btnAjudaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAjuda;
    private javax.swing.JButton btnPesquisarCest;
    private javax.swing.JButton btnPesquisarNcm;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnSalvarECopiar;
    private javax.swing.JButton btnSalvarENovo;
    private javax.swing.JComboBox<Object> cboCategoria;
    private javax.swing.JComboBox<Object> cboCfopSaidaDentroDoEstado;
    private javax.swing.JComboBox<Object> cboCfopSaidaForaDoEstado;
    private javax.swing.JComboBox<Object> cboConteudoUnidade;
    private javax.swing.JComboBox<Object> cboIcms;
    private javax.swing.JComboBox<Object> cboOrigem;
    private javax.swing.JComboBox<Object> cboTipo;
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
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblEstoqueInicial;
    private javax.swing.JFormattedTextField txtAliquotaIcms;
    private javax.swing.JTextField txtCest;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JFormattedTextField txtConteudoQuantidade;
    private javax.swing.JTextField txtDescricao;
    private javax.swing.JFormattedTextField txtDiasValidade;
    private javax.swing.JFormattedTextField txtEstoqueInicial;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtLocalizacao;
    private javax.swing.JFormattedTextField txtMargemLucro;
    private javax.swing.JTextField txtNcm;
    private javax.swing.JTextField txtNcmDescricao;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextArea txtObservacao;
    private javax.swing.JTextField txtOutrosCodigos;
    private javax.swing.JFormattedTextField txtValorCompra;
    private javax.swing.JFormattedTextField txtValorVenda;
    // End of variables declaration//GEN-END:variables
}
