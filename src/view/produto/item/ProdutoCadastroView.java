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
import model.mysql.bean.fiscal.nfe.ModalidadeBcIcms;
import model.mysql.bean.fiscal.nfe.ModalidadeBcIcmsSt;
import model.mysql.bean.fiscal.nfe.RegimeTributario;
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
import model.mysql.dao.fiscal.nfe.ModalidadeBcIcmsDAO;
import model.mysql.dao.fiscal.nfe.ModalidadeBcIcmsStDAO;
import model.mysql.dao.principal.MovimentoFisicoDAO;
import model.mysql.dao.principal.catalogo.ProdutoTipoDAO;
import ouroboros.Ouroboros;
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
        carregarUnidadeVenda();
        carregarUnidadeTributavel();
        carregarConteudoUnidade();
        carregarTipo();
        cboOrigemLoad();
        carregarCfopDentroEstado();
        cboCfopSaidaForaDoEstadoLoad();
        carregarIcms();
        carregarModalidadeBcIcms();
        carregarModalidadeBcIcmsSt();

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
                txtNcmSat.setText(produto.getNcm().getCodigo());
                txtNcmNfe.setText(produto.getNcm().getCodigo());
                txtNcmDescricaoSat.setText(produto.getNcm().getDescricao());
                txtNcmDescricaoNfe.setText(produto.getNcm().getDescricao());
            }

            txtCestSat.setText(produto.getCest());
            txtCestNfe.setText(produto.getCest());

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

    private void carregarUnidadeVenda() {
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
        if (produto != null && produto.getProdutoTipo() != null) {
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

    private void carregarCfopDentroEstado() {
        List<Cfop> cfopList = new CfopDAO().findAllSaidaDentroDoEstado();

        cboCfopDentroDoEstadoSat.addItem(null);
        for (Cfop cfop : cfopList) {
            cboCfopDentroDoEstadoSat.addItem(cfop);
            cboCfopDentroDoEstadoNfe.addItem(cfop);
        }
        if (produto != null && produto.getCfopSaidaDentroDoEstado() != null) {
            cboCfopDentroDoEstadoSat.setSelectedItem(produto.getCfopSaidaDentroDoEstado());
            cboCfopDentroDoEstadoNfe.setSelectedItem(produto.getCfopSaidaDentroDoEstado());
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

    private void carregarIcms() {
        List<Icms> icmsList;
        if (Ouroboros.NFE_REGIME_TRIBUTARIO.getId() == 3) { //simples nacional
            icmsList = new IcmsDAO().listarSimplesNacional();
        } else {
            icmsList = new IcmsDAO().listarTributacaoNormal();
        }

        cboIcmsNfe.addItem(null);
        for (Icms icms : icmsList) {
            cboIcmsSat.addItem(icms);
            cboIcmsNfe.addItem(icms);
        }
        if (produto != null && produto.getIcms() != null) {
            cboIcmsSat.setSelectedItem(produto.getIcms());
            cboIcmsNfe.setSelectedItem(produto.getIcms());
        }
    }
    
    private void carregarUnidadeTributavel() {
        List<UnidadeComercial> listUC = new UnidadeComercialDAO().findAll();

        cboUnidadeTributavel.addItem(null);
        for (UnidadeComercial uc : listUC) {
            cboUnidadeTributavel.addItem(uc);
        }
        if (produto != null && produto.getUnidadeComercialVenda()!= null) {
            cboUnidadeTributavel.setSelectedItem(produto.getUnidadeComercialVenda());
        }
    }

    private void carregarModalidadeBcIcms() {
        List<ModalidadeBcIcms> mods = new ModalidadeBcIcmsDAO().findAll();

        cboModalidadeBcIcms.addItem(null);
        for (ModalidadeBcIcms mod : mods) {
            cboModalidadeBcIcms.addItem(mod);
        }
        if (produto != null && produto.getModalidadeBcIcms() != null) {
            cboModalidadeBcIcms.setSelectedItem(produto.getModalidadeBcIcms());
        }
    }

    private void carregarModalidadeBcIcmsSt() {
        List<ModalidadeBcIcmsSt> mods = new ModalidadeBcIcmsStDAO().findAll();

        cboModalidadeBcIcmsSt.addItem(null);
        for (ModalidadeBcIcmsSt mod : mods) {
            cboModalidadeBcIcmsSt.addItem(mod);
        }
        if (produto != null && produto.getModalidadeBcIcmsSt() != null) {
            cboModalidadeBcIcmsSt.setSelectedItem(produto.getModalidadeBcIcmsSt());
        }
    }

    private void calcularValores(String referencia) {
        BigDecimal valorCompra, margemLucro, valorVenda;
        valorCompra = Decimal.fromString(txtValorCompra.getText());
        margemLucro = Decimal.fromString(txtMargemLucro.getText());
        valorVenda = Decimal.fromString(txtValorVenda.getText());

        switch (referencia) {
            case "compra":
                if (margemLucro.compareTo(new BigDecimal(100)) >= 0) {
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

        } else if (unidade == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Informe a unidade do produto", "Atenção", JOptionPane.WARNING_MESSAGE);
            return false;

        } else if (!txtNcmSat.getText().isEmpty()) {
            Ncm ncm = new NcmDAO().findByCodigo(txtNcmSat.getText());
            if (ncm == null) {
                JOptionPane.showMessageDialog(rootPane, "NCM inválido", "Atenção", JOptionPane.WARNING_MESSAGE);
                txtNcmSat.requestFocus();
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
/*
        Cfop cfopSaidaDentroDoEstado = null;
        if (cboCfopDentroDoEstadoSat.getSelectedIndex() > 0) {
            cfopSaidaDentroDoEstado = (Cfop) cboCfopDentroDoEstadoSat.getSelectedItem();
        }*/

        Cfop cfopSaidaForaDoEstado = null;
        if (cboCfopSaidaForaDoEstado.getSelectedIndex() > 0) {
            cfopSaidaForaDoEstado = (Cfop) cboCfopSaidaForaDoEstado.getSelectedItem();
        }

        Icms icms = null;
        if (cboIcmsNfe.getSelectedIndex() > 0) {
            icms = (Icms) cboIcmsNfe.getSelectedItem();
        }

        Ncm ncm = null;
        if (!txtNcmSat.getText().isEmpty()) {
            ncm = new NcmDAO().findByCodigo(txtNcmSat.getText());
        }

        String cest = txtCestSat.getText();

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
        produto.setCfopSaidaDentroDoEstado((Cfop) cboCfopDentroDoEstadoSat.getSelectedItem());
        produto.setCfopSaidaForaDoEstado(cfopSaidaForaDoEstado);
        produto.setIcms(icms);
        produto.setNcm(ncm);
        produto.setCest(cest);
        produto.setAliquotaIcms(aliquotaIcms);
        produto.setModalidadeBcIcms((ModalidadeBcIcms) cboModalidadeBcIcms.getSelectedItem());
        produto.setModalidadeBcIcmsSt((ModalidadeBcIcmsSt) cboModalidadeBcIcmsSt.getSelectedItem());

        produto.setBalanca(balanca);

        produto.setDiasValidade(diasValidade);

        produto = produtoDAO.save(produto);

        txtId.setText(produto.getId().toString());

        BigDecimal entrada = Decimal.fromString(txtEstoqueInicial.getText());

        if (entrada.compareTo(BigDecimal.ZERO) > 0) {
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
            txtNcmSat.setText(ncm.getCodigo());
            txtNcmNfe.setText(ncm.getCodigo());
            txtNcmDescricaoSat.setText(ncm.getDescricao());
            txtNcmDescricaoNfe.setText(ncm.getDescricao());
        }
        if(jTabPrincipal.getSelectedComponent().getName().equals("sat")) {
            txtNcmSat.requestFocus();
        } else {
            txtNcmNfe.requestFocus();
        }
    }
    
    private void preencherNcm() {
        String codigo = txtNcmSat.getText().trim();
        txtNcmSat.setText(codigo);

        Ncm ncm = new NcmDAO().findByCodigo(codigo);
        if (ncm != null) {
            txtNcmDescricaoSat.setText(ncm.getDescricao());
            txtNcmDescricaoNfe.setText(ncm.getDescricao());
            buscarCest();
            
        } else {
            txtNcmDescricaoSat.setText("NCM NÃO ENCONTRADO");
            txtNcmDescricaoNfe.setText("NCM NÃO ENCONTRADO");
        }
    }

    private void buscarCest() {
        String codigoNcm = txtNcmSat.getText().trim();
        //txtCest.setText("");
        if (!codigoNcm.isEmpty()) {
            CestDAO cestDAO = new CestDAO();
            List<Cest> listCest = cestDAO.findByCodigoNcm(codigoNcm);
            if (listCest.size() == 1) {
                txtCestSat.setText(listCest.get(0).getCodigo());
            } else if (listCest.size() > 1) {
                new Toast("Mais de um CEST correspondente ao NCM foi encontrado. Escolha na lista...");
                pesquisarCest(codigoNcm);
            }
            txtCestSat.requestFocus();
        }
    }

    private void pesquisarCest(String codigoNcm) {
        CestPesquisaView cestPesquisaView = new CestPesquisaView(codigoNcm);
        Cest cest = cestPesquisaView.getCest();
        if (cest != null) {
            txtCestSat.setText(cest.getCodigo());
            txtCestNfe.setText(cest.getCodigo());
        }
        
        if(jTabPrincipal.getSelectedComponent().getName().equals("sat")) {
            txtCestSat.requestFocus();
        } else {
            txtCestNfe.requestFocus();
        }
    }

    private void validarCodigo() {
        txtCodigo.setText(txtCodigo.getText().trim());
        String codigo = txtCodigo.getText();
        List<Produto> produtos = produtoDAO.findByCodigo(codigo);

        produtos.remove(produto);

        if (!produtos.isEmpty()) {
            String mensagem = "Já Exite(m) produto(s) com este código: \n";
            for (Produto p : produtos) {
                mensagem += p.getNome() + " \n";
            }

            JOptionPane.showMessageDialog(MAIN_VIEW, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
        }

    }
    
    private void sincronizarCfopNfe() {
        cboCfopDentroDoEstadoNfe.setSelectedItem(cboCfopDentroDoEstadoSat.getSelectedItem());
    }
    
    private void sincronizarCfopSat() {
        cboCfopDentroDoEstadoSat.setSelectedItem(cboCfopDentroDoEstadoNfe.getSelectedItem());
    }
    
    private void sincronizarIcmsNfe() {
        cboIcmsNfe.setSelectedItem(cboIcmsSat.getSelectedItem());
    }
    
    private void sincronizarIcmsSat() {
        cboIcmsSat.setSelectedItem(cboIcmsNfe.getSelectedItem());
    }
    
    private void sincronizarNcmNfe() {
        txtNcmNfe.setText(txtNcmSat.getText());
        txtNcmDescricaoNfe.setText(txtNcmDescricaoSat.getText());
    }
    
    private void sincronizarNcmSat() {
        txtNcmSat.setText(txtNcmNfe.getText());
        txtNcmDescricaoSat.setText(txtNcmDescricaoNfe.getText());
    }
    
    private void sincronizarCestNfe() {
        txtCestNfe.setText(txtCestSat.getText());
    }
    
    private void sincronizarCestSat() {
        txtCestSat.setText(txtCestNfe.getText());
    }
    
    private void exibirPaineisIcms() {
        JSwing.setComponentesHabilitados(pnlIcms, false);
        JSwing.setComponentesHabilitados(pnlIcmsSt, false);
        
        Icms icms = (Icms) cboIcmsNfe.getSelectedItem();
        
        if (icms != null) {
            switch (icms.getCodigo()) {
                case "00":
                    JSwing.setComponentesHabilitados(pnlIcms, true);
                    txtReducaoBcIcms.setEnabled(false);
                    txtBCOperacaoPropria.setEnabled(false);
                    cboMotivoDesoneracao.setEnabled(false);
                    break;

                case "10":
                    JSwing.setComponentesHabilitados(pnlIcms, true);
                    cboMotivoDesoneracao.setEnabled(false);
                    if (icms.getId() == 2) {
                        txtReducaoBcIcms.setEnabled(false);
                        txtBCOperacaoPropria.setEnabled(false);
                    }
                    JSwing.setComponentesHabilitados(pnlIcmsSt, true);
                    break;

                case "20":
                    JSwing.setComponentesHabilitados(pnlIcms, true);
                    txtBCOperacaoPropria.setEnabled(false);
                    cboMotivoDesoneracao.setEnabled(false);
                    break;

                case "30":
                    JSwing.setComponentesHabilitados(pnlIcmsSt, true);
                    break;

                case "40":
                    JSwing.setComponentesHabilitados(pnlIcms, true);
                    cboModalidadeBcIcms.setEnabled(false);
                    txtReducaoBcIcms.setEnabled(false);
                    txtAliquotaIcms.setEnabled(false);
                    txtBCOperacaoPropria.setEnabled(false);
                    break;
                    
                case "41":
                    if (icms.getId() == 7) {
                        JSwing.setComponentesHabilitados(pnlIcms, true);
                        cboModalidadeBcIcms.setEnabled(false);
                        txtReducaoBcIcms.setEnabled(false);
                        txtAliquotaIcms.setEnabled(false);
                        txtBCOperacaoPropria.setEnabled(false);
                    }
                    break;

                case "50":
                    JSwing.setComponentesHabilitados(pnlIcms, true);
                    cboModalidadeBcIcms.setEnabled(false);
                    txtReducaoBcIcms.setEnabled(false);
                    txtAliquotaIcms.setEnabled(false);
                    txtBCOperacaoPropria.setEnabled(false);
                    break;
                    
                    
                case "51":
                    JSwing.setComponentesHabilitados(pnlIcms, true);
                    txtBCOperacaoPropria.setEnabled(false);
                    cboMotivoDesoneracao.setEnabled(false);
                    break;

                case "60":
                    break;

                case "70":
                    JSwing.setComponentesHabilitados(pnlIcms, true);
                    txtBCOperacaoPropria.setEnabled(false);
                    cboMotivoDesoneracao.setEnabled(false);
                    JSwing.setComponentesHabilitados(pnlIcmsSt, true);
                    break;
                    
                case "90":
                    JSwing.setComponentesHabilitados(pnlIcms, true);
                    cboMotivoDesoneracao.setEnabled(false);
                    if(icms.getId() == 15) {
                        txtBCOperacaoPropria.setEnabled(false);
                    }
                    JSwing.setComponentesHabilitados(pnlIcmsSt, true);
                    break;

                case "101":
                case "102":
                case "103":
                    break;

                case "201":
                case "202":
                case "203":
                    JSwing.setComponentesHabilitados(pnlIcmsSt, true);
                    break;

                case "300":
                case "400":
                case "500":
                    break;

                case "900":
                    JSwing.setComponentesHabilitados(pnlIcms, true);
                    JSwing.setComponentesHabilitados(pnlIcmsSt, true);

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

        jLabel42 = new javax.swing.JLabel();
        btnPesquisarNcm1 = new javax.swing.JButton();
        txtNcm1 = new javax.swing.JTextField();
        txtNcmDescricao1 = new javax.swing.JTextField();
        btnSalvar = new javax.swing.JButton();
        btnSalvarENovo = new javax.swing.JButton();
        btnSalvarECopiar = new javax.swing.JButton();
        btnAjuda = new javax.swing.JButton();
        jTabPrincipal = new javax.swing.JTabbedPane();
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
        cboCfopDentroDoEstadoSat = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        txtNcmSat = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtNcmDescricaoSat = new javax.swing.JTextField();
        btnPesquisarNcmSat = new javax.swing.JButton();
        txtCestSat = new javax.swing.JTextField();
        btnPesquisarCestSat = new javax.swing.JButton();
        cboIcmsSat = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txtEan = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txtEanTributavel = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        txtExTipi = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        txtGenero = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        txtValorUnitarioTributacao = new javax.swing.JFormattedTextField();
        jLabel28 = new javax.swing.JLabel();
        cboUnidadeTributavel = new javax.swing.JComboBox<>();
        pnlIcms = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        cboModalidadeBcIcms = new javax.swing.JComboBox<>();
        jLabel30 = new javax.swing.JLabel();
        txtReducaoBcIcms = new javax.swing.JFormattedTextField();
        jLabel31 = new javax.swing.JLabel();
        txtAliquotaIcms = new javax.swing.JFormattedTextField();
        jLabel37 = new javax.swing.JLabel();
        txtBCOperacaoPropria = new javax.swing.JFormattedTextField();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        cboMotivoDesoneracao = new javax.swing.JComboBox<>();
        pnlIcmsSt = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        cboModalidadeBcIcmsSt = new javax.swing.JComboBox<>();
        jLabel33 = new javax.swing.JLabel();
        txtReducaoBcIcms1 = new javax.swing.JFormattedTextField();
        jLabel35 = new javax.swing.JLabel();
        txtAlíquotaIcms1 = new javax.swing.JFormattedTextField();
        jLabel36 = new javax.swing.JLabel();
        txtReducaoBcIcms2 = new javax.swing.JFormattedTextField();
        jLabel38 = new javax.swing.JLabel();
        cboIcmsNfe = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        cboCfopSaidaForaDoEstado = new javax.swing.JComboBox<>();
        jLabel41 = new javax.swing.JLabel();
        cboCfopDentroDoEstadoNfe = new javax.swing.JComboBox<>();
        jLabel43 = new javax.swing.JLabel();
        btnPesquisarNcmNfe = new javax.swing.JButton();
        txtNcmNfe = new javax.swing.JTextField();
        txtNcmDescricaoNfe = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        btnPesquisarCestNfe = new javax.swing.JButton();
        txtCestNfe = new javax.swing.JTextField();

        jLabel42.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel42.setText("NCM");

        btnPesquisarNcm1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/resource/img/zoom.png"))); // NOI18N

        txtNcm1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtNcmDescricao1.setEditable(false);
        txtNcmDescricao1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

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

        btnSalvar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnSalvarENovo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnSalvarENovo.setText("Salvar e Novo");
        btnSalvarENovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarENovoActionPerformed(evt);
            }
        });

        btnSalvarECopiar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
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
        jLabel2.setText("Descrição");

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
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
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
                        .addComponent(txtLocalizacao)
                        .addGap(10, 10, 10))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addGap(18, 18, 18)
                                .addComponent(txtConteudoQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboConteudoUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(chkBalanca)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel23)
                                .addGap(18, 18, 18)
                                .addComponent(txtDiasValidade, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel10)
                                .addGap(18, 18, 18)
                                .addComponent(txtDescricao))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(lblEstoqueInicial)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtEstoqueInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addGap(18, 18, 18)
                                        .addComponent(cboUnidadeVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
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
                                        .addComponent(txtValorVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(10, 10, 10))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel34)
                .addGap(18, 18, 18)
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
                    .addComponent(jLabel6)
                    .addComponent(cboUnidadeVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtValorCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtMargemLucro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtValorVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cboCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(txtLocalizacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(cboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10)
                        .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel22)
                        .addComponent(txtConteudoQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboConteudoUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel23)
                        .addComponent(txtDiasValidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkBalanca)))
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEstoqueInicial)
                    .addComponent(txtEstoqueInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(167, 167, 167))
        );

        txtValorCompra.getAccessibleContext().setAccessibleName("");

        jTabPrincipal.addTab("Principal", jPanel1);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setName("sat"); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setText("Origem");

        cboOrigem.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel12.setText("CFOP dentro do Estado");

        cboCfopDentroDoEstadoSat.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboCfopDentroDoEstadoSat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cboCfopDentroDoEstadoSatFocusLost(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel15.setText("NCM");

        txtNcmSat.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNcmSat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNcmSatFocusLost(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel16.setText("CEST");

        txtNcmDescricaoSat.setEditable(false);
        txtNcmDescricaoSat.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        btnPesquisarNcmSat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/resource/img/zoom.png"))); // NOI18N
        btnPesquisarNcmSat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarNcmSatActionPerformed(evt);
            }
        });

        txtCestSat.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCestSat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCestSatFocusLost(evt);
            }
        });

        btnPesquisarCestSat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/resource/img/zoom.png"))); // NOI18N
        btnPesquisarCestSat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarCestSatActionPerformed(evt);
            }
        });

        cboIcmsSat.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboIcmsSat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cboIcmsSatFocusLost(evt);
            }
        });
        cboIcmsSat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboIcmsSatActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel17.setText("Situação Tributária ICMS");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel11)
                                .addComponent(jLabel12))
                            .addGap(71, 71, 71))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(jLabel16)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnPesquisarCestSat))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(jLabel15)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnPesquisarNcmSat)))
                            .addGap(18, 18, 18)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(68, 68, 68)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboOrigem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtCestSat, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(cboCfopDentroDoEstadoSat, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtNcmSat, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtNcmDescricaoSat, javax.swing.GroupLayout.DEFAULT_SIZE, 841, Short.MAX_VALUE))
                    .addComponent(cboIcmsSat, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addComponent(cboCfopDentroDoEstadoSat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboIcmsSat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtNcmSat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15)
                        .addComponent(txtNcmDescricaoSat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnPesquisarNcmSat))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCestSat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPesquisarCestSat, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel16))
                .addContainerGap(302, Short.MAX_VALUE))
        );

        jTabPrincipal.addTab("Dados Fiscais SAT", jPanel2);

        jPanel3.setName("nfe"); // NOI18N

        txtEan.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtEan.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtEanFocusLost(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel21.setText("EAN");

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel24.setText("EAN Unid. Tributável");

        txtEanTributavel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtEanTributavel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtEanTributavelFocusLost(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel25.setText("EX TIPI");

        txtExTipi.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtExTipi.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtExTipiFocusLost(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel26.setText("Gênero");

        txtGenero.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtGenero.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtGeneroFocusLost(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel27.setText("Valor Unitário Tributação");

        txtValorUnitarioTributacao.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorUnitarioTributacao.setText("0,00");
        txtValorUnitarioTributacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorUnitarioTributacao.setName("decimal"); // NOI18N
        txtValorUnitarioTributacao.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorUnitarioTributacaoKeyReleased(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel28.setText("Unidade Tributável");

        cboUnidadeTributavel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        pnlIcms.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlIcms.setPreferredSize(new java.awt.Dimension(610, 234));

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel29.setText("Modalidade de determinação da BC ICMS");

        cboModalidadeBcIcms.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel30.setText("% redução da BC ICMS");

        txtReducaoBcIcms.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtReducaoBcIcms.setName("decimal"); // NOI18N

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel31.setText("Alíquota do ICMS");

        txtAliquotaIcms.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAliquotaIcms.setName("decimal"); // NOI18N

        jLabel37.setBackground(new java.awt.Color(122, 138, 153));
        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel37.setForeground(java.awt.Color.white);
        jLabel37.setText("ICMS");
        jLabel37.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel37.setOpaque(true);

        txtBCOperacaoPropria.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBCOperacaoPropria.setName("decimal"); // NOI18N

        jLabel39.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel39.setText("% BC da operação própria");

        jLabel40.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel40.setText("Modalidade da Desoneração");

        cboMotivoDesoneracao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout pnlIcmsLayout = new javax.swing.GroupLayout(pnlIcms);
        pnlIcms.setLayout(pnlIcmsLayout);
        pnlIcmsLayout.setHorizontalGroup(
            pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlIcmsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcmsLayout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addGap(18, 18, 18)
                        .addComponent(cboModalidadeBcIcms, 0, 320, Short.MAX_VALUE))
                    .addGroup(pnlIcmsLayout.createSequentialGroup()
                        .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlIcmsLayout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addGap(18, 18, 18)
                                .addComponent(txtReducaoBcIcms, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcmsLayout.createSequentialGroup()
                                .addComponent(jLabel31)
                                .addGap(18, 18, 18)
                                .addComponent(txtAliquotaIcms, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcmsLayout.createSequentialGroup()
                                .addComponent(jLabel39)
                                .addGap(18, 18, 18)
                                .addComponent(txtBCOperacaoPropria, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlIcmsLayout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addGap(18, 18, 18)
                        .addComponent(cboMotivoDesoneracao, 0, 398, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlIcmsLayout.setVerticalGroup(
            pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIcmsLayout.createSequentialGroup()
                .addComponent(jLabel37)
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboModalidadeBcIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtReducaoBcIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAliquotaIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBCOperacaoPropria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboMotivoDesoneracao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlIcmsSt.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlIcmsSt.setPreferredSize(new java.awt.Dimension(610, 232));

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel32.setText("Modalidade de determinação da BC ICMS ST");

        cboModalidadeBcIcmsSt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel33.setText("% redução da BC ICMS ST");

        txtReducaoBcIcms1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtReducaoBcIcms1.setName("decimal"); // NOI18N

        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel35.setText("Alíquota do ICMS ST");

        txtAlíquotaIcms1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAlíquotaIcms1.setName("decimal"); // NOI18N

        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel36.setText("% margem de valor adic. ICMS ST");

        txtReducaoBcIcms2.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtReducaoBcIcms2.setName("decimal"); // NOI18N

        jLabel38.setBackground(new java.awt.Color(122, 138, 153));
        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel38.setForeground(java.awt.Color.white);
        jLabel38.setText("ICMS ST");
        jLabel38.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel38.setOpaque(true);

        javax.swing.GroupLayout pnlIcmsStLayout = new javax.swing.GroupLayout(pnlIcmsSt);
        pnlIcmsSt.setLayout(pnlIcmsStLayout);
        pnlIcmsStLayout.setHorizontalGroup(
            pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIcmsStLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcmsStLayout.createSequentialGroup()
                        .addComponent(jLabel32)
                        .addGap(18, 18, 18)
                        .addComponent(cboModalidadeBcIcmsSt, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlIcmsStLayout.createSequentialGroup()
                        .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlIcmsStLayout.createSequentialGroup()
                                .addComponent(jLabel33)
                                .addGap(18, 18, 18)
                                .addComponent(txtReducaoBcIcms1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcmsStLayout.createSequentialGroup()
                                .addComponent(jLabel35)
                                .addGap(18, 18, 18)
                                .addComponent(txtAlíquotaIcms1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcmsStLayout.createSequentialGroup()
                                .addComponent(jLabel36)
                                .addGap(18, 18, 18)
                                .addComponent(txtReducaoBcIcms2, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlIcmsStLayout.setVerticalGroup(
            pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIcmsStLayout.createSequentialGroup()
                .addComponent(jLabel38)
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboModalidadeBcIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtReducaoBcIcms1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtReducaoBcIcms2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAlíquotaIcms1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cboIcmsNfe.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboIcmsNfe.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cboIcmsNfeFocusLost(evt);
            }
        });
        cboIcmsNfe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboIcmsNfeActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel14.setText("Situação Tributária ICMS");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel13.setText("CFOP fora do Estado");

        cboCfopSaidaForaDoEstado.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel41.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel41.setText("CFOP dentro do Estado");

        cboCfopDentroDoEstadoNfe.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboCfopDentroDoEstadoNfe.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cboCfopDentroDoEstadoNfeFocusLost(evt);
            }
        });

        jLabel43.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel43.setText("NCM");

        btnPesquisarNcmNfe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-search-button-20.png"))); // NOI18N
        btnPesquisarNcmNfe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarNcmNfeActionPerformed(evt);
            }
        });

        txtNcmNfe.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNcmNfe.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNcmNfeFocusLost(evt);
            }
        });

        txtNcmDescricaoNfe.setEditable(false);
        txtNcmDescricaoNfe.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel44.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel44.setText("CEST");

        btnPesquisarCestNfe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-search-button-20.png"))); // NOI18N
        btnPesquisarCestNfe.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                btnPesquisarCestNfeFocusLost(evt);
            }
        });
        btnPesquisarCestNfe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarCestNfeActionPerformed(evt);
            }
        });

        txtCestNfe.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCestNfe.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCestNfeFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(18, 18, 18)
                        .addComponent(cboIcmsNfe, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel41)
                            .addComponent(jLabel13))
                        .addGap(21, 21, 21)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboCfopSaidaForaDoEstado, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboCfopDentroDoEstadoNfe, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(pnlIcms, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(pnlIcmsSt, javax.swing.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel43)
                        .addGap(18, 18, 18)
                        .addComponent(btnPesquisarNcmNfe)
                        .addGap(18, 18, 18)
                        .addComponent(txtNcmNfe, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtNcmDescricaoNfe))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addGap(18, 18, 18)
                                .addComponent(txtEan, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel24)
                                .addGap(18, 18, 18)
                                .addComponent(txtEanTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel25)
                                .addGap(18, 18, 18)
                                .addComponent(txtExTipi, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel26)
                                .addGap(18, 18, 18)
                                .addComponent(txtGenero, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel44)
                                .addGap(18, 18, 18)
                                .addComponent(btnPesquisarCestNfe)
                                .addGap(18, 18, 18)
                                .addComponent(txtCestNfe, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel28)
                                .addGap(18, 18, 18)
                                .addComponent(cboUnidadeTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel27)
                                .addGap(18, 18, 18)
                                .addComponent(txtValorUnitarioTributacao, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 378, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(txtEan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(txtEanTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25)
                    .addComponent(txtExTipi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26)
                    .addComponent(txtGenero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNcmNfe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel43)
                            .addComponent(txtNcmDescricaoNfe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPesquisarNcmNfe, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel44)
                    .addComponent(btnPesquisarCestNfe, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCestNfe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel28)
                            .addComponent(cboUnidadeTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27)
                            .addComponent(txtValorUnitarioTributacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(cboCfopDentroDoEstadoNfe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboCfopSaidaForaDoEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboIcmsNfe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlIcms, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlIcmsSt, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabPrincipal.addTab("Dados Fiscais NFe", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabPrincipal)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAjuda)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSalvarECopiar)
                        .addGap(18, 18, 18)
                        .addComponent(btnSalvarENovo)
                        .addGap(18, 18, 18)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 536, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
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

    private void btnPesquisarNcmSatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarNcmSatActionPerformed
        pesquisarNcm();
    }//GEN-LAST:event_btnPesquisarNcmSatActionPerformed

    private void txtNcmSatFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNcmSatFocusLost
        sincronizarNcmNfe();
        preencherNcm();
    }//GEN-LAST:event_txtNcmSatFocusLost

    private void btnPesquisarCestSatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarCestSatActionPerformed
        pesquisarCest(null);
    }//GEN-LAST:event_btnPesquisarCestSatActionPerformed

    private void txtCodigoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCodigoFocusLost
        validarCodigo();
    }//GEN-LAST:event_txtCodigoFocusLost

    private void btnAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAjudaActionPerformed
        AjudaView ajuda = new AjudaView("produto_cadastro.html");
    }//GEN-LAST:event_btnAjudaActionPerformed

    private void txtEanFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtEanFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEanFocusLost

    private void txtEanTributavelFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtEanTributavelFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEanTributavelFocusLost

    private void txtExTipiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtExTipiFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtExTipiFocusLost

    private void txtGeneroFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtGeneroFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGeneroFocusLost

    private void txtValorUnitarioTributacaoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorUnitarioTributacaoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValorUnitarioTributacaoKeyReleased

    private void cboIcmsNfeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboIcmsNfeActionPerformed
        exibirPaineisIcms();
    }//GEN-LAST:event_cboIcmsNfeActionPerformed

    private void cboIcmsSatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboIcmsSatActionPerformed
    }//GEN-LAST:event_cboIcmsSatActionPerformed

    private void cboIcmsSatFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboIcmsSatFocusLost
        sincronizarIcmsNfe();
    }//GEN-LAST:event_cboIcmsSatFocusLost

    private void cboIcmsNfeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboIcmsNfeFocusLost
        sincronizarIcmsSat();
    }//GEN-LAST:event_cboIcmsNfeFocusLost

    private void cboCfopDentroDoEstadoSatFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboCfopDentroDoEstadoSatFocusLost
        sincronizarCfopNfe();
    }//GEN-LAST:event_cboCfopDentroDoEstadoSatFocusLost

    private void cboCfopDentroDoEstadoNfeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboCfopDentroDoEstadoNfeFocusLost
        sincronizarCfopSat();
    }//GEN-LAST:event_cboCfopDentroDoEstadoNfeFocusLost

    private void btnPesquisarNcmNfeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarNcmNfeActionPerformed
        pesquisarNcm();
    }//GEN-LAST:event_btnPesquisarNcmNfeActionPerformed

    private void txtNcmNfeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNcmNfeFocusLost
        sincronizarNcmSat();
        preencherNcm();
    }//GEN-LAST:event_txtNcmNfeFocusLost

    private void btnPesquisarCestNfeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarCestNfeActionPerformed
        pesquisarCest(null);
    }//GEN-LAST:event_btnPesquisarCestNfeActionPerformed

    private void txtCestSatFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCestSatFocusLost
        sincronizarCestNfe();
    }//GEN-LAST:event_txtCestSatFocusLost

    private void txtCestNfeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCestNfeFocusLost
        sincronizarCestSat();
    }//GEN-LAST:event_txtCestNfeFocusLost

    private void btnPesquisarCestNfeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btnPesquisarCestNfeFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPesquisarCestNfeFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAjuda;
    private javax.swing.JButton btnPesquisarCestNfe;
    private javax.swing.JButton btnPesquisarCestSat;
    private javax.swing.JButton btnPesquisarNcm1;
    private javax.swing.JButton btnPesquisarNcmNfe;
    private javax.swing.JButton btnPesquisarNcmSat;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnSalvarECopiar;
    private javax.swing.JButton btnSalvarENovo;
    private javax.swing.JComboBox<Object> cboCategoria;
    private javax.swing.JComboBox<Object> cboCfopDentroDoEstadoNfe;
    private javax.swing.JComboBox<Object> cboCfopDentroDoEstadoSat;
    private javax.swing.JComboBox<Object> cboCfopSaidaForaDoEstado;
    private javax.swing.JComboBox<Object> cboConteudoUnidade;
    private javax.swing.JComboBox<Object> cboIcmsNfe;
    private javax.swing.JComboBox<Object> cboIcmsSat;
    private javax.swing.JComboBox<Object> cboModalidadeBcIcms;
    private javax.swing.JComboBox<Object> cboModalidadeBcIcmsSt;
    private javax.swing.JComboBox<Object> cboMotivoDesoneracao;
    private javax.swing.JComboBox<Object> cboOrigem;
    private javax.swing.JComboBox<Object> cboTipo;
    private javax.swing.JComboBox<Object> cboUnidadeTributavel;
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
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabPrincipal;
    private javax.swing.JLabel lblEstoqueInicial;
    private javax.swing.JPanel pnlIcms;
    private javax.swing.JPanel pnlIcmsSt;
    private javax.swing.JFormattedTextField txtAliquotaIcms;
    private javax.swing.JFormattedTextField txtAlíquotaIcms1;
    private javax.swing.JFormattedTextField txtBCOperacaoPropria;
    private javax.swing.JTextField txtCestNfe;
    private javax.swing.JTextField txtCestSat;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JFormattedTextField txtConteudoQuantidade;
    private javax.swing.JTextField txtDescricao;
    private javax.swing.JFormattedTextField txtDiasValidade;
    private javax.swing.JTextField txtEan;
    private javax.swing.JTextField txtEanTributavel;
    private javax.swing.JFormattedTextField txtEstoqueInicial;
    private javax.swing.JTextField txtExTipi;
    private javax.swing.JTextField txtGenero;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtLocalizacao;
    private javax.swing.JFormattedTextField txtMargemLucro;
    private javax.swing.JTextField txtNcm1;
    private javax.swing.JTextField txtNcmDescricao1;
    private javax.swing.JTextField txtNcmDescricaoNfe;
    private javax.swing.JTextField txtNcmDescricaoSat;
    private javax.swing.JTextField txtNcmNfe;
    private javax.swing.JTextField txtNcmSat;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextArea txtObservacao;
    private javax.swing.JTextField txtOutrosCodigos;
    private javax.swing.JFormattedTextField txtReducaoBcIcms;
    private javax.swing.JFormattedTextField txtReducaoBcIcms1;
    private javax.swing.JFormattedTextField txtReducaoBcIcms2;
    private javax.swing.JFormattedTextField txtValorCompra;
    private javax.swing.JFormattedTextField txtValorUnitarioTributacao;
    private javax.swing.JFormattedTextField txtValorVenda;
    // End of variables declaration//GEN-END:variables
}
