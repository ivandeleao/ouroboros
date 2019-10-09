/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.produto.item;

import java.awt.Dimension;
import view.sistema.AjudaView;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.TipoCalculoEnum;
import model.jtable.catalogo.ProdutoTamanhoJTableModel;
import model.mysql.bean.principal.catalogo.Categoria;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.fiscal.Cest;
import model.mysql.bean.fiscal.Cfop;
import model.mysql.bean.fiscal.Cofins;
import model.mysql.bean.fiscal.Icms;
import model.mysql.bean.fiscal.Ncm;
import model.mysql.bean.fiscal.Pis;
import model.mysql.bean.fiscal.ProdutoOrigem;
import model.mysql.bean.fiscal.UnidadeComercial;
import model.mysql.bean.fiscal.nfe.ModalidadeBcIcms;
import model.mysql.bean.fiscal.nfe.ModalidadeBcIcmsSt;
import model.mysql.bean.fiscal.nfe.MotivoDesoneracao;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.MovimentoFisicoTipo;
import model.mysql.bean.principal.catalogo.ProdutoTamanho;
import model.mysql.bean.principal.catalogo.ProdutoTipo;
import model.mysql.bean.principal.catalogo.Tamanho;
import model.mysql.dao.principal.catalogo.CategoriaDAO;
import model.mysql.dao.principal.catalogo.ProdutoDAO;
import model.mysql.dao.fiscal.CestDAO;
import model.mysql.dao.fiscal.CfopDAO;
import model.mysql.dao.fiscal.CofinsDAO;
import model.mysql.dao.fiscal.IcmsDAO;
import model.mysql.dao.fiscal.NcmDAO;
import model.mysql.dao.fiscal.PisDAO;
import model.mysql.dao.fiscal.ProdutoOrigemDAO;
import model.mysql.dao.fiscal.UnidadeComercialDAO;
import model.mysql.dao.fiscal.nfe.ModalidadeBcIcmsDAO;
import model.mysql.dao.fiscal.nfe.ModalidadeBcIcmsStDAO;
import model.mysql.dao.fiscal.nfe.MotivoDesoneracaoDAO;
import model.mysql.dao.principal.MovimentoFisicoDAO;
import model.mysql.dao.principal.catalogo.ProdutoTipoDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
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

    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private Produto produto;
    private static boolean apenasNovo;
    private ProdutoTamanhoJTableModel produtoTamanhoJTableModel = new ProdutoTamanhoJTableModel();

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

        

        carregarCategorias();
        carregarUnidadeVenda();
        carregarUnidadeTributavel();
        carregarConteudoUnidade();
        carregarTipo();
        carregarOrigem();
        carregarCfopDentroEstado();
        cboCfopSaidaForaDoEstadoLoad();
        carregarIcms();
        carregarModalidadeBcIcms();
        carregarMotivoDesoneracao();
        carregarModalidadeBcIcmsSt();
        
        //Pis-------------------------------------------------------------------
        carregarPis();
        carregarPisTipoCalculo();
        carregarPisStTipoCalculo();
        //Fim Pis---------------------------------------------------------------
        
        //Cofins----------------------------------------------------------------
        carregarCofins();
        carregarCofinsTipoCalculo();
        carregarCofinsStTipoCalculo();
        //Fim Cofins------------------------------------------------------------
        
        carregarDados();
        
        
        formatarProdutoTamanhos();
        carregarProdutoTamanhos();
        
        if(produto.getId() == null) {
            JSwing.setComponentesHabilitados(pnlTamanhos, false);
        }

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
            
            if(produto.getCategoria() == null || produto.getCategoria().getTamanhos() == null || produto.getCategoria().getTamanhos().isEmpty()) {
                JSwing.setComponentesHabilitados(pnlTamanhos, false);
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

            txtEan.setText(produto.getEan());
            txtEanTributavel.setText(produto.getEanTributavel());
            txtExTipi.setText(produto.getExTipi());
            txtGenero.setText(produto.getGenero());
            
            if (produto.getNcm() != null) {
                txtNcmSat.setText(produto.getNcm().getCodigo());
                txtNcmNfe.setText(produto.getNcm().getCodigo());
                txtNcmDescricaoSat.setText(produto.getNcm().getDescricao());
                txtNcmDescricaoNfe.setText(produto.getNcm().getDescricao());
            }

            txtCestSat.setText(produto.getCest());
            txtCestNfe.setText(produto.getCest());
            
            txtValorUnitarioTributacao.setText(Decimal.toString(produto.getValorTributavel()));
            
            txtPercentualReducaoBcIcms.setText(Decimal.toString(produto.getPercentualReducaoBcIcms()));
            
            txtAliquotaIcmsSat.setText(Decimal.toString(produto.getAliquotaIcms()));
            txtAliquotaIcmsNfe.setText(Decimal.toString(produto.getAliquotaIcms()));
            
            txtPercentualBcOperacaoPropria.setText(Decimal.toString(produto.getPercentualBcOperacaoPropria()));
            
            txtPercentualReducaoBcIcmsSt.setText(Decimal.toString(produto.getPercentualReducaoBcIcmsSt()));
            txtPercentualMargemValorAdicionadoIcmsSt.setText(Decimal.toString(produto.getPercentualMargemValorAdicionadoIcmsSt()));
            txtAliquotaIcmsSt.setText(Decimal.toString(produto.getAliquotaIcmsSt()));

            chkBalanca.setSelected(produto.getBalanca());

            txtDiasValidade.setText(produto.getDiasValidade().toString());
            
            chkMontavel.setSelected(produto.isMontavel());
            
            
            cboPis.setSelectedItem(produto.getPis());
            cboPisTipoCalculo.setSelectedItem(produto.getPisTipoCalculo());
            txtAliquotaPis.setText(Decimal.toString(produto.getAliquotaPis()));
            txtAliquotaPisReais.setText(Decimal.toString(produto.getAliquotaPisReais()));
            
            cboPisStTipoCalculo.setSelectedItem(produto.getPisStTipoCalculo());
            txtAliquotaPisSt.setText(Decimal.toString(produto.getAliquotaPisSt()));
            txtAliquotaPisStReais.setText(Decimal.toString(produto.getAliquotaPisStReais()));
            
            cboCofins.setSelectedItem(produto.getCofins());
            cboCofinsTipoCalculo.setSelectedItem(produto.getCofinsTipoCalculo());
            txtAliquotaCofins.setText(Decimal.toString(produto.getAliquotaCofins()));
            txtAliquotaCofinsReais.setText(Decimal.toString(produto.getAliquotaCofinsReais()));
            
            cboCofinsStTipoCalculo.setSelectedItem(produto.getCofinsStTipoCalculo());
            txtAliquotaCofinsSt.setText(Decimal.toString(produto.getAliquotaCofinsSt()));
            txtAliquotaCofinsStReais.setText(Decimal.toString(produto.getAliquotaCofinsStReais()));
            
        }
    }
    
    private void formatarProdutoTamanhos() {
        tblTamanho.setModel(produtoTamanhoJTableModel);

        tblTamanho.setRowHeight(24);
        tblTamanho.setIntercellSpacing(new Dimension(10, 10));
        
        tblTamanho.getColumn("Tamanho").setPreferredWidth(200);
        
        tblTamanho.getColumn("Valor").setPreferredWidth(200);
        tblTamanho.getColumn("Valor").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
    }
    
    private void carregarProdutoTamanhos() {
        produtoTamanhoJTableModel.clear();
        produtoTamanhoJTableModel.addList(produto.getProdutoTamanhos());
        
    }

    private void carregarCategorias() {
        List<Categoria> listCategoria = new CategoriaDAO().findAll();

        cboCategoria.addItem(null);
        for (Categoria c : listCategoria) {
            cboCategoria.addItem(c);
        }
        if (produto != null && produto.getCategoria() != null) {
            cboCategoria.setSelectedItem(produto.getCategoria());
            
            if(!produto.getCategoria().getTamanhos().isEmpty()) {
                cboCategoria.setEnabled(false);
            }
        }
    }
    
    private void alertaTamanhos() {
        if(cboCategoria.getSelectedItem() != null && !((Categoria) cboCategoria.getSelectedItem()).getTamanhos().isEmpty() ) {
            
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

    private void carregarOrigem() {
        //System.out.println("produto origem: " + produto.getOrigem().getNome());
        List<ProdutoOrigem> poList = new ProdutoOrigemDAO().findAll();

        cboOrigemSat.addItem(null);
        cboOrigemNfe.addItem(null);
        for (ProdutoOrigem po : poList) {
            cboOrigemSat.addItem(po);
            cboOrigemNfe.addItem(po);
        }
        if (produto != null && produto.getOrigem() != null) {
            cboOrigemSat.setSelectedItem(produto.getOrigem());
            cboOrigemNfe.setSelectedItem(produto.getOrigem());
        }
    }

    private void carregarCfopDentroEstado() {
        List<Cfop> cfopList = new CfopDAO().findAllSaidaDentroDoEstado();

        cboCfopDentroDoEstadoSat.addItem(null);
        cboCfopDentroDoEstadoNfe.addItem(null);
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

        cboIcmsSat.addItem(null);
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
        if (produto != null && produto.getUnidadeTributavel()!= null) {
            cboUnidadeTributavel.setSelectedItem(produto.getUnidadeTributavel());
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
    
    private void carregarMotivoDesoneracao() {
        List<MotivoDesoneracao> mots = new MotivoDesoneracaoDAO().findAll();

        cboMotivoDesoneracao.addItem(null);
        for (MotivoDesoneracao mot : mots) {
            cboMotivoDesoneracao.addItem(mot);
        }
        if (produto != null && produto.getMotivoDesoneracao() != null) {
            cboMotivoDesoneracao.setSelectedItem(produto.getMotivoDesoneracao());
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
    
    private void carregarPis() {
        List<Pis> pisList;
        pisList = new PisDAO().findAll();
        
        cboPis.addItem(null);
        for (Pis pis : pisList) {
            cboPis.addItem(pis);
        }
    }
    
    private void carregarPisTipoCalculo() {
        cboPisTipoCalculo.addItem(TipoCalculoEnum.PERCENTUAL);
        cboPisTipoCalculo.addItem(TipoCalculoEnum.VALOR);
    }
    
    private void carregarPisStTipoCalculo() {
        cboPisStTipoCalculo.addItem(TipoCalculoEnum.PERCENTUAL);
        cboPisStTipoCalculo.addItem(TipoCalculoEnum.VALOR);
    }
    
    private void carregarCofins() {
        List<Cofins> cofinsList;
        cofinsList = new CofinsDAO().findAll();
        
        cboCofins.addItem(null);
        for (Cofins cofins : cofinsList) {
            cboCofins.addItem(cofins);
        }
    }
    
    private void carregarCofinsTipoCalculo() {
        cboCofinsTipoCalculo.addItem(TipoCalculoEnum.PERCENTUAL);
        cboCofinsTipoCalculo.addItem(TipoCalculoEnum.VALOR);
    }
    
    private void carregarCofinsStTipoCalculo() {
        cboCofinsStTipoCalculo.addItem(TipoCalculoEnum.PERCENTUAL);
        cboCofinsStTipoCalculo.addItem(TipoCalculoEnum.VALOR);
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
        if (cboOrigemSat.getSelectedIndex() > 0) {
            origem = (ProdutoOrigem) cboOrigemSat.getSelectedItem();
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

        BigDecimal aliquotaIcms = Decimal.fromString(txtAliquotaIcmsNfe.getText());

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
        
        produto.setEan(txtEan.getText());
        produto.setEanTributavel(txtEanTributavel.getText());
        produto.setExTipi(txtExTipi.getText());
        produto.setGenero(txtGenero.getText());
        produto.setOrigem(origem);
        produto.setCfopSaidaDentroDoEstado((Cfop) cboCfopDentroDoEstadoSat.getSelectedItem());
        produto.setCfopSaidaForaDoEstado(cfopSaidaForaDoEstado);
        produto.setIcms(icms);
        produto.setNcm(ncm);
        produto.setCest(cest);
        produto.setUnidadeTributavel((UnidadeComercial) cboUnidadeTributavel.getSelectedItem());
        produto.setValorTributavel(Decimal.fromString(txtValorUnitarioTributacao.getText()));
        
        produto.setModalidadeBcIcms((ModalidadeBcIcms) cboModalidadeBcIcms.getSelectedItem());
        produto.setPercentualReducaoBcIcms(Decimal.fromString(txtPercentualReducaoBcIcms.getText()));
        produto.setAliquotaIcms(aliquotaIcms);
        produto.setPercentualBcOperacaoPropria(Decimal.fromString(txtPercentualBcOperacaoPropria.getText()));
        produto.setMotivoDesoneracao((MotivoDesoneracao) cboMotivoDesoneracao.getSelectedItem());
        
        produto.setModalidadeBcIcmsSt((ModalidadeBcIcmsSt) cboModalidadeBcIcmsSt.getSelectedItem());
        produto.setPercentualReducaoBcIcmsSt(Decimal.fromString(txtPercentualReducaoBcIcmsSt.getText()));
        produto.setPercentualMargemValorAdicionadoIcmsSt(Decimal.fromString(txtPercentualMargemValorAdicionadoIcmsSt.getText()));
        produto.setAliquotaIcmsSt(Decimal.fromString(txtAliquotaIcmsSt.getText()));
        
        produto.setPis((Pis) cboPis.getSelectedItem());
        produto.setPisTipoCalculo((TipoCalculoEnum) cboPisTipoCalculo.getSelectedItem());
        produto.setAliquotaPis(Decimal.fromString(txtAliquotaPis.getText()));
        produto.setAliquotaPisReais(Decimal.fromString(txtAliquotaPisReais.getText()));
        
        produto.setPisStTipoCalculo((TipoCalculoEnum) cboPisStTipoCalculo.getSelectedItem());
        produto.setAliquotaPisSt(Decimal.fromString(txtAliquotaPisSt.getText()));
        produto.setAliquotaPisStReais(Decimal.fromString(txtAliquotaPisStReais.getText()));
        
        produto.setCofins((Cofins) cboCofins.getSelectedItem());
        produto.setCofinsTipoCalculo((TipoCalculoEnum) cboCofinsTipoCalculo.getSelectedItem());
        produto.setAliquotaCofins(Decimal.fromString(txtAliquotaCofins.getText()));
        produto.setAliquotaCofinsReais(Decimal.fromString(txtAliquotaCofinsReais.getText()));
        
        produto.setCofinsStTipoCalculo((TipoCalculoEnum) cboCofinsStTipoCalculo.getSelectedItem());
        produto.setAliquotaCofinsSt(Decimal.fromString(txtAliquotaCofinsSt.getText()));
        produto.setAliquotaCofinsStReais(Decimal.fromString(txtAliquotaCofinsStReais.getText()));
        
        
        produto.setBalanca(balanca);

        produto.setDiasValidade(diasValidade);
        
        produto.setMontavel(chkMontavel.isSelected());
        
        produto = produtoDAO.save(produto);
        
        //Iniciar tamanhos
        if(produto.getCategoria() != null && produto.getProdutoTamanhos().isEmpty() && !produto.getCategoria().getTamanhos().isEmpty()) {
            for(Tamanho t : categoria.getTamanhos()) {
                ProdutoTamanho pt = new ProdutoTamanho(produto, t);
                pt.setValorCompra(produto.getValorCompra());
                pt.setValorVenda(produto.getValorVenda());
                produto.addProdutoTamanho(pt);
                produto = produtoDAO.save(produto);
            }
            cboCategoria.setEnabled(false);
            carregarProdutoTamanhos();
        }

        

        txtId.setText(produto.getId().toString());

        BigDecimal entrada = Decimal.fromString(txtEstoqueInicial.getText());

        if (entrada.compareTo(BigDecimal.ZERO) > 0) {
            MovimentoFisicoDAO movimentoFisicoDAO = new MovimentoFisicoDAO();
            MovimentoFisico movimentoFisico = new MovimentoFisico(produto,
                    produto.getCodigo(),
                    produto.getNome(),
                    produto.getProdutoTipo(),
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
        
        JSwing.setComponentesHabilitados(pnlTamanhos, true);

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
    
    private void espelharParaTributavel() {
        if(cboUnidadeTributavel.getSelectedItem() == null) {
            cboUnidadeTributavel.setSelectedItem(cboUnidadeVenda.getSelectedItem());
        }
        if(Decimal.fromString(txtValorUnitarioTributacao.getText()).compareTo(BigDecimal.ZERO) == 0) {
            txtValorUnitarioTributacao.setText(txtValorVenda.getText());
        }
    }
    
    private void sincronizarOrigemNfe() {
        cboOrigemNfe.setSelectedItem(cboOrigemSat.getSelectedItem());
    }
    
    private void sincronizarOrigemSat() {
        cboOrigemSat.setSelectedItem(cboOrigemNfe.getSelectedItem());
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
    
    private void sincronizarAliquotaIcmsNfe() {
        txtAliquotaIcmsNfe.setText(txtAliquotaIcmsSat.getText());
    }
    
    private void sincronizarAliquotaIcmsSat() {
        txtAliquotaIcmsSat.setText(txtAliquotaIcmsNfe.getText());
    }
    
    private void chavearIcms() {
        JSwing.setComponentesHabilitados(pnlIcms, false);
        JSwing.setComponentesHabilitados(pnlIcmsSt, false);
        
        Icms icms = (Icms) cboIcmsNfe.getSelectedItem();
        
        if (icms != null) {
            switch (icms.getCodigo()) {
                case "00":
                    JSwing.setComponentesHabilitados(pnlIcms, true);
                    txtPercentualReducaoBcIcms.setEnabled(false);
                    txtPercentualBcOperacaoPropria.setEnabled(false);
                    cboMotivoDesoneracao.setEnabled(false);
                    break;

                case "10":
                    JSwing.setComponentesHabilitados(pnlIcms, true);
                    cboMotivoDesoneracao.setEnabled(false);
                    if (icms.getId() == 2) {
                        txtPercentualReducaoBcIcms.setEnabled(false);
                        txtPercentualBcOperacaoPropria.setEnabled(false);
                    }
                    JSwing.setComponentesHabilitados(pnlIcmsSt, true);
                    break;

                case "20":
                    JSwing.setComponentesHabilitados(pnlIcms, true);
                    txtPercentualBcOperacaoPropria.setEnabled(false);
                    cboMotivoDesoneracao.setEnabled(false);
                    break;

                case "30":
                    JSwing.setComponentesHabilitados(pnlIcmsSt, true);
                    break;

                case "40":
                    JSwing.setComponentesHabilitados(pnlIcms, true);
                    cboModalidadeBcIcms.setEnabled(false);
                    txtPercentualReducaoBcIcms.setEnabled(false);
                    txtAliquotaIcmsNfe.setEnabled(false);
                    txtPercentualBcOperacaoPropria.setEnabled(false);
                    break;
                    
                case "41":
                    if (icms.getId() == 7) {
                        JSwing.setComponentesHabilitados(pnlIcms, true);
                        cboModalidadeBcIcms.setEnabled(false);
                        txtPercentualReducaoBcIcms.setEnabled(false);
                        txtAliquotaIcmsNfe.setEnabled(false);
                        txtPercentualBcOperacaoPropria.setEnabled(false);
                    }
                    break;

                case "50":
                    JSwing.setComponentesHabilitados(pnlIcms, true);
                    cboModalidadeBcIcms.setEnabled(false);
                    txtPercentualReducaoBcIcms.setEnabled(false);
                    txtAliquotaIcmsNfe.setEnabled(false);
                    txtPercentualBcOperacaoPropria.setEnabled(false);
                    break;
                    
                    
                case "51":
                    JSwing.setComponentesHabilitados(pnlIcms, true);
                    txtPercentualBcOperacaoPropria.setEnabled(false);
                    cboMotivoDesoneracao.setEnabled(false);
                    break;

                case "60":
                    break;

                case "70":
                    JSwing.setComponentesHabilitados(pnlIcms, true);
                    txtPercentualBcOperacaoPropria.setEnabled(false);
                    cboMotivoDesoneracao.setEnabled(false);
                    JSwing.setComponentesHabilitados(pnlIcmsSt, true);
                    break;
                    
                case "90":
                    JSwing.setComponentesHabilitados(pnlIcms, true);
                    cboMotivoDesoneracao.setEnabled(false);
                    if(icms.getId() == 15) {
                        txtPercentualBcOperacaoPropria.setEnabled(false);
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
    
    private void chavearPis() {
        cboPisTipoCalculo.setEnabled(false);
        cboPisStTipoCalculo.setEnabled(false);
        
        Pis pis = (Pis) cboPis.getSelectedItem();
        
        if (pis == null) {
            cboPisTipoCalculo.setSelectedItem(null);
            
        } else {
            switch (pis.getCodigo()) {
                case "01":
                case "02":
                    cboPisTipoCalculo.setSelectedItem(TipoCalculoEnum.PERCENTUAL);
                    cboPisTipoCalculo.setEnabled(false);
                    cboPisStTipoCalculo.setSelectedItem(null);
                    cboPisStTipoCalculo.setEnabled(false);
                    break;

                case "03":
                    cboPisTipoCalculo.setSelectedItem(TipoCalculoEnum.VALOR);
                    cboPisTipoCalculo.setEnabled(false);
                    break;

                case "04":
                    cboPisTipoCalculo.setSelectedItem(null);
                    cboPisStTipoCalculo.setSelectedItem(null);
                    break;
                case "05":
                    cboPisTipoCalculo.setSelectedItem(null);
                    cboPisStTipoCalculo.setSelectedItem(null);
                    cboPisStTipoCalculo.setEnabled(true);
                    break;
                case "06":
                case "07":
                case "08":
                case "09":
                    cboPisTipoCalculo.setSelectedItem(null);
                    cboPisStTipoCalculo.setSelectedItem(null);
                    break;

                default: //49 em diante
                    cboPisTipoCalculo.setEnabled(true);
                    break;

            }
        }
        
        chavearPisStTipoCalculo();
        chavearPisTipoCalculo();
        
    }
    
    private void chavearPisTipoCalculo() {
        
        if(cboPisTipoCalculo.getSelectedItem() == null) {
            txtAliquotaPis.setEditable(false);
            txtAliquotaPisReais.setEditable(false);
            txtAliquotaPis.setText("0,00");
            txtAliquotaPisReais.setText("0,00");
        
        } else if(cboPisTipoCalculo.getSelectedItem().equals(TipoCalculoEnum.PERCENTUAL)) {
            txtAliquotaPis.setEditable(true);
            txtAliquotaPisReais.setEditable(false);
            txtAliquotaPisReais.setText("0,00");
            
        } else {
            txtAliquotaPis.setEditable(false);
            txtAliquotaPisReais.setEditable(true);
            txtAliquotaPis.setText("0,00");
            
        }
    }
    
    private void chavearPisStTipoCalculo() {
    
        if(cboPisStTipoCalculo.getSelectedItem() == null) {
            txtAliquotaPisSt.setEditable(false);
            txtAliquotaPisStReais.setEditable(false);
            txtAliquotaPisSt.setText("0,00");
            txtAliquotaPisStReais.setText("0,00");
        
        } else if(cboPisStTipoCalculo.getSelectedItem().equals(TipoCalculoEnum.PERCENTUAL)) {
            txtAliquotaPisSt.setEditable(true);
            txtAliquotaPisStReais.setEditable(false);
            txtAliquotaPisStReais.setText("0,00");
            
        } else {
            txtAliquotaPisSt.setEditable(false);
            txtAliquotaPisStReais.setEditable(true);
            txtAliquotaPisSt.setText("0,00");
            
        }
    }
    
    private void chavearCofins() {
        cboCofinsTipoCalculo.setEnabled(false);
        cboCofinsStTipoCalculo.setEnabled(false);
        
        Cofins cofins = (Cofins) cboCofins.getSelectedItem();
        
        if (cofins == null) {
            cboCofinsTipoCalculo.setSelectedItem(null);
            
        } else {
            switch (cofins.getCodigo()) {
                case "01":
                case "02":
                    cboCofinsTipoCalculo.setSelectedItem(TipoCalculoEnum.PERCENTUAL);
                    cboCofinsTipoCalculo.setEnabled(false);
                    cboCofinsStTipoCalculo.setSelectedItem(null);
                    cboCofinsStTipoCalculo.setEnabled(false);
                    break;

                case "03":
                    cboCofinsTipoCalculo.setSelectedItem(TipoCalculoEnum.VALOR);
                    cboCofinsTipoCalculo.setEnabled(false);
                    break;

                case "04":
                    cboCofinsTipoCalculo.setSelectedItem(null);
                    cboCofinsStTipoCalculo.setSelectedItem(null);
                    break;
                case "05":
                    cboCofinsTipoCalculo.setSelectedItem(null);
                    cboCofinsStTipoCalculo.setSelectedItem(null);
                    cboCofinsStTipoCalculo.setEnabled(true);
                    break;
                case "06":
                case "07":
                case "08":
                case "09":
                    cboCofinsTipoCalculo.setSelectedItem(null);
                    cboCofinsStTipoCalculo.setSelectedItem(null);
                    break;

                default: //49 em diante
                    cboCofinsTipoCalculo.setEnabled(true);
                    break;

            }
        }
        
        chavearCofinsStTipoCalculo();
        chavearCofinsTipoCalculo();
        
    }
    
    private void chavearCofinsTipoCalculo() {
        
        if(cboCofinsTipoCalculo.getSelectedItem() == null) {
            txtAliquotaCofins.setEditable(false);
            txtAliquotaCofinsReais.setEditable(false);
            txtAliquotaCofins.setText("0,00");
            txtAliquotaCofinsReais.setText("0,00");
        
        } else if(cboCofinsTipoCalculo.getSelectedItem().equals(TipoCalculoEnum.PERCENTUAL)) {
            txtAliquotaCofins.setEditable(true);
            txtAliquotaCofinsReais.setEditable(false);
            txtAliquotaCofinsReais.setText("0,00");
            
        } else {
            txtAliquotaCofins.setEditable(false);
            txtAliquotaCofinsReais.setEditable(true);
            txtAliquotaCofins.setText("0,00");
            
        }
    }
    
    private void chavearCofinsStTipoCalculo() {
    
        if(cboCofinsStTipoCalculo.getSelectedItem() == null) {
            txtAliquotaCofinsSt.setEditable(false);
            txtAliquotaCofinsStReais.setEditable(false);
            txtAliquotaCofinsSt.setText("0,00");
            txtAliquotaCofinsStReais.setText("0,00");
        
        } else if(cboCofinsStTipoCalculo.getSelectedItem().equals(TipoCalculoEnum.PERCENTUAL)) {
            txtAliquotaCofinsSt.setEditable(true);
            txtAliquotaCofinsStReais.setEditable(false);
            txtAliquotaCofinsStReais.setText("0,00");
            
        } else {
            txtAliquotaCofinsSt.setEditable(false);
            txtAliquotaCofinsStReais.setEditable(true);
            txtAliquotaCofinsSt.setText("0,00");
            
        }
    }
    
    
    private void editarTamanho() {
        ProdutoTamanho produtoTamanho = produtoTamanhoJTableModel.getRow(tblTamanho.getSelectedRow());
        
        ProdutoTamanhoEditarView tamanhoEditar = new ProdutoTamanhoEditarView(produtoTamanho);
        
        carregarProdutoTamanhos();
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
        chkMontavel = new javax.swing.JCheckBox();
        pnlTamanhos = new javax.swing.JPanel();
        jLabel47 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTamanho = new javax.swing.JTable();
        jLabel46 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        cboOrigemSat = new javax.swing.JComboBox<>();
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
        txtAliquotaIcmsSat = new javax.swing.JFormattedTextField();
        jLabel48 = new javax.swing.JLabel();
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
        jLabel45 = new javax.swing.JLabel();
        cboOrigemNfe = new javax.swing.JComboBox<>();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        pnlIcms = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        cboModalidadeBcIcms = new javax.swing.JComboBox<>();
        jLabel30 = new javax.swing.JLabel();
        txtPercentualReducaoBcIcms = new javax.swing.JFormattedTextField();
        jLabel31 = new javax.swing.JLabel();
        txtAliquotaIcmsNfe = new javax.swing.JFormattedTextField();
        jLabel37 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        cboMotivoDesoneracao = new javax.swing.JComboBox<>();
        jLabel39 = new javax.swing.JLabel();
        txtPercentualBcOperacaoPropria = new javax.swing.JFormattedTextField();
        pnlIcmsSt = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        cboModalidadeBcIcmsSt = new javax.swing.JComboBox<>();
        jLabel33 = new javax.swing.JLabel();
        txtPercentualMargemValorAdicionadoIcmsSt = new javax.swing.JFormattedTextField();
        jLabel35 = new javax.swing.JLabel();
        txtAliquotaIcmsSt = new javax.swing.JFormattedTextField();
        jLabel36 = new javax.swing.JLabel();
        txtPercentualReducaoBcIcmsSt = new javax.swing.JFormattedTextField();
        jLabel38 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel65 = new javax.swing.JLabel();
        cboPis = new javax.swing.JComboBox<>();
        pnlIcms3 = new javax.swing.JPanel();
        jLabel68 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        txtAliquotaPis = new javax.swing.JFormattedTextField();
        jLabel74 = new javax.swing.JLabel();
        cboPisTipoCalculo = new javax.swing.JComboBox<>();
        jLabel67 = new javax.swing.JLabel();
        txtAliquotaPisReais = new javax.swing.JFormattedTextField();
        pnlIcms4 = new javax.swing.JPanel();
        jLabel75 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        txtAliquotaPisSt = new javax.swing.JFormattedTextField();
        jLabel79 = new javax.swing.JLabel();
        cboPisStTipoCalculo = new javax.swing.JComboBox<>();
        jLabel73 = new javax.swing.JLabel();
        txtAliquotaPisStReais = new javax.swing.JFormattedTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel89 = new javax.swing.JLabel();
        cboCofins = new javax.swing.JComboBox<>();
        pnlIcms5 = new javax.swing.JPanel();
        jLabel83 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        txtAliquotaCofins = new javax.swing.JFormattedTextField();
        jLabel85 = new javax.swing.JLabel();
        cboCofinsTipoCalculo = new javax.swing.JComboBox<>();
        jLabel87 = new javax.swing.JLabel();
        txtAliquotaCofinsReais = new javax.swing.JFormattedTextField();
        pnlIcms6 = new javax.swing.JPanel();
        jLabel91 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        txtAliquotaCofinsSt = new javax.swing.JFormattedTextField();
        jLabel94 = new javax.swing.JLabel();
        cboCofinsStTipoCalculo = new javax.swing.JComboBox<>();
        jLabel95 = new javax.swing.JLabel();
        txtAliquotaCofinsStReais = new javax.swing.JFormattedTextField();

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
        txtValorVenda.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtValorVendaFocusLost(evt);
            }
        });
        txtValorVenda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorVendaKeyReleased(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Unidade de Venda");

        cboUnidadeVenda.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboUnidadeVenda.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cboUnidadeVendaFocusLost(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("Categoria");

        cboCategoria.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCategoriaActionPerformed(evt);
            }
        });

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

        chkMontavel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkMontavel.setText("Produto pode ser montado com outros no momento da venda");

        pnlTamanhos.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel47.setBackground(new java.awt.Color(122, 138, 153));
        jLabel47.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel47.setForeground(java.awt.Color.white);
        jLabel47.setText("Tamanhos");
        jLabel47.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel47.setOpaque(true);

        tblTamanho.setModel(new javax.swing.table.DefaultTableModel(
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
        tblTamanho.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTamanhoMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblTamanho);

        jLabel46.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel46.setText("*Selecione uma categoria com tamanhos e salve para liberar esta tabela");

        javax.swing.GroupLayout pnlTamanhosLayout = new javax.swing.GroupLayout(pnlTamanhos);
        pnlTamanhos.setLayout(pnlTamanhosLayout);
        pnlTamanhosLayout.setHorizontalGroup(
            pnlTamanhosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlTamanhosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTamanhosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jLabel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlTamanhosLayout.setVerticalGroup(
            pnlTamanhosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTamanhosLayout.createSequentialGroup()
                .addComponent(jLabel47)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel46)
                .addContainerGap())
        );

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
                        .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel18)
                        .addGap(18, 18, 18)
                        .addComponent(txtOutrosCodigos, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
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
                                .addGap(0, 319, Short.MAX_VALUE)))
                        .addGap(10, 10, 10))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkMontavel)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblEstoqueInicial)
                                .addGap(18, 18, 18)
                                .addComponent(txtEstoqueInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlTamanhos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblEstoqueInicial)
                            .addComponent(txtEstoqueInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(chkMontavel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(pnlTamanhos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        txtValorCompra.getAccessibleContext().setAccessibleName("");

        jTabPrincipal.addTab("Principal", jPanel1);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setName("sat"); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setText("Origem");

        cboOrigemSat.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboOrigemSat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cboOrigemSatFocusLost(evt);
            }
        });

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

        btnPesquisarNcmSat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-search-button-20.png"))); // NOI18N
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

        btnPesquisarCestSat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-search-button-20.png"))); // NOI18N
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

        txtAliquotaIcmsSat.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAliquotaIcmsSat.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAliquotaIcmsSat.setName("decimal"); // NOI18N
        txtAliquotaIcmsSat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAliquotaIcmsSatFocusLost(evt);
            }
        });

        jLabel48.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel48.setText("Alíquota do ICMS");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboOrigemSat, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addComponent(jLabel12))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboCfopDentroDoEstadoSat, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboIcmsSat, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(18, 18, 18)
                                .addComponent(btnPesquisarNcmSat)
                                .addGap(18, 18, 18)
                                .addComponent(txtNcmSat, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtNcmDescricaoSat, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel16)
                                .addGap(18, 18, 18)
                                .addComponent(btnPesquisarCestSat)
                                .addGap(18, 18, 18)
                                .addComponent(txtCestSat, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel48)
                                .addGap(18, 18, 18)
                                .addComponent(txtAliquotaIcmsSat, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 393, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(cboOrigemSat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtNcmSat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15)
                        .addComponent(txtNcmDescricaoSat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel16))
                    .addComponent(txtCestSat)
                    .addComponent(btnPesquisarCestSat, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPesquisarNcmSat, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(cboCfopDentroDoEstadoSat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboIcmsSat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAliquotaIcmsSat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48))
                .addContainerGap(304, Short.MAX_VALUE))
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
        jLabel27.setText("Valor Unit Tribut");

        txtValorUnitarioTributacao.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorUnitarioTributacao.setText("0,00");
        txtValorUnitarioTributacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorUnitarioTributacao.setName("decimal"); // NOI18N
        txtValorUnitarioTributacao.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtValorUnitarioTributacaoFocusGained(evt);
            }
        });
        txtValorUnitarioTributacao.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorUnitarioTributacaoKeyReleased(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel28.setText("Unidade Tributável");

        cboUnidadeTributavel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboUnidadeTributavel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboUnidadeTributavelFocusGained(evt);
            }
        });

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

        jLabel45.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel45.setText("Origem");

        cboOrigemNfe.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboOrigemNfe.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cboOrigemNfeFocusLost(evt);
            }
        });

        pnlIcms.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlIcms.setPreferredSize(new java.awt.Dimension(610, 234));

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel29.setText("Modalidade de determinação da BC ICMS");

        cboModalidadeBcIcms.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel30.setText("% redução da BC ICMS");

        txtPercentualReducaoBcIcms.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPercentualReducaoBcIcms.setText("pRedBC");
        txtPercentualReducaoBcIcms.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPercentualReducaoBcIcms.setName("decimal"); // NOI18N

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel31.setText("Alíquota do ICMS");

        txtAliquotaIcmsNfe.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAliquotaIcmsNfe.setText("pICMS");
        txtAliquotaIcmsNfe.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAliquotaIcmsNfe.setName("decimal"); // NOI18N
        txtAliquotaIcmsNfe.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAliquotaIcmsNfeFocusLost(evt);
            }
        });

        jLabel37.setBackground(new java.awt.Color(122, 138, 153));
        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel37.setForeground(java.awt.Color.white);
        jLabel37.setText("ICMS");
        jLabel37.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel37.setOpaque(true);

        jLabel40.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel40.setText("Motivo da Desoneração");

        cboMotivoDesoneracao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel39.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel39.setText("% BC da operação própria");

        txtPercentualBcOperacaoPropria.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPercentualBcOperacaoPropria.setText("pBCOp");
        txtPercentualBcOperacaoPropria.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPercentualBcOperacaoPropria.setName("decimal"); // NOI18N

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
                        .addComponent(cboModalidadeBcIcms, 0, 307, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlIcmsLayout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addGap(18, 18, 18)
                        .addComponent(cboMotivoDesoneracao, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlIcmsLayout.createSequentialGroup()
                        .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlIcmsLayout.createSequentialGroup()
                                .addComponent(jLabel39)
                                .addGap(18, 18, 18)
                                .addComponent(txtPercentualBcOperacaoPropria, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcmsLayout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addGap(18, 18, 18)
                                .addComponent(txtPercentualReducaoBcIcms, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel31)
                                .addGap(18, 18, 18)
                                .addComponent(txtAliquotaIcmsNfe, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                    .addComponent(txtPercentualReducaoBcIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30)
                    .addComponent(txtAliquotaIcmsNfe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPercentualBcOperacaoPropria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        txtPercentualMargemValorAdicionadoIcmsSt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPercentualMargemValorAdicionadoIcmsSt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPercentualMargemValorAdicionadoIcmsSt.setName("decimal"); // NOI18N

        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel35.setText("Alíquota do ICMS ST");

        txtAliquotaIcmsSt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAliquotaIcmsSt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAliquotaIcmsSt.setName("decimal"); // NOI18N

        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel36.setText("% margem de valor adic. ICMS ST");

        txtPercentualReducaoBcIcmsSt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPercentualReducaoBcIcmsSt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPercentualReducaoBcIcmsSt.setName("decimal"); // NOI18N

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
            .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlIcmsStLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcmsStLayout.createSequentialGroup()
                        .addComponent(jLabel32)
                        .addGap(18, 18, 18)
                        .addComponent(cboModalidadeBcIcmsSt, 0, 289, Short.MAX_VALUE))
                    .addGroup(pnlIcmsStLayout.createSequentialGroup()
                        .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlIcmsStLayout.createSequentialGroup()
                                .addComponent(jLabel33)
                                .addGap(18, 18, 18)
                                .addComponent(txtPercentualReducaoBcIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcmsStLayout.createSequentialGroup()
                                .addComponent(jLabel35)
                                .addGap(18, 18, 18)
                                .addComponent(txtAliquotaIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcmsStLayout.createSequentialGroup()
                                .addComponent(jLabel36)
                                .addGap(18, 18, 18)
                                .addComponent(txtPercentualMargemValorAdicionadoIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
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
                    .addComponent(jLabel33)
                    .addComponent(txtPercentualReducaoBcIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(txtPercentualMargemValorAdicionadoIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAliquotaIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlIcms, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(pnlIcmsSt, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlIcms, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                    .addComponent(pnlIcmsSt, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE))
                .addGap(52, 52, 52))
        );

        jTabbedPane1.addTab("ICMS", jPanel4);

        jLabel65.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel65.setText("Situação Tributária");

        cboPis.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboPis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPisActionPerformed(evt);
            }
        });

        pnlIcms3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlIcms3.setPreferredSize(new java.awt.Dimension(610, 234));

        jLabel68.setBackground(new java.awt.Color(122, 138, 153));
        jLabel68.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel68.setForeground(java.awt.Color.white);
        jLabel68.setText("PIS");
        jLabel68.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel68.setOpaque(true);

        jLabel71.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel71.setText("Alíquota (percentual)");

        txtAliquotaPis.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAliquotaPis.setText("pPIS");
        txtAliquotaPis.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAliquotaPis.setName("decimal"); // NOI18N
        txtAliquotaPis.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAliquotaPisFocusLost(evt);
            }
        });
        txtAliquotaPis.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAliquotaPisKeyReleased(evt);
            }
        });

        jLabel74.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel74.setText("Tipo de Cálculo");

        cboPisTipoCalculo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboPisTipoCalculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPisTipoCalculoActionPerformed(evt);
            }
        });

        jLabel67.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel67.setText("Alíquota (reais)");

        txtAliquotaPisReais.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAliquotaPisReais.setText("vAliqProd");
        txtAliquotaPisReais.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAliquotaPisReais.setName("decimal"); // NOI18N
        txtAliquotaPisReais.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAliquotaPisReaisFocusLost(evt);
            }
        });
        txtAliquotaPisReais.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAliquotaPisReaisKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout pnlIcms3Layout = new javax.swing.GroupLayout(pnlIcms3);
        pnlIcms3.setLayout(pnlIcms3Layout);
        pnlIcms3Layout.setHorizontalGroup(
            pnlIcms3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel68, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlIcms3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlIcms3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcms3Layout.createSequentialGroup()
                        .addComponent(jLabel74)
                        .addGap(18, 18, 18)
                        .addComponent(cboPisTipoCalculo, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlIcms3Layout.createSequentialGroup()
                        .addComponent(jLabel71)
                        .addGap(18, 18, 18)
                        .addComponent(txtAliquotaPis, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlIcms3Layout.createSequentialGroup()
                        .addComponent(jLabel67)
                        .addGap(18, 18, 18)
                        .addComponent(txtAliquotaPisReais, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(326, Short.MAX_VALUE))
        );
        pnlIcms3Layout.setVerticalGroup(
            pnlIcms3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIcms3Layout.createSequentialGroup()
                .addComponent(jLabel68)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlIcms3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboPisTipoCalculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel74))
                .addGap(18, 18, 18)
                .addGroup(pnlIcms3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAliquotaPis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel71))
                .addGap(18, 18, 18)
                .addGroup(pnlIcms3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAliquotaPisReais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel67))
                .addGap(52, 52, 52))
        );

        pnlIcms4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlIcms4.setPreferredSize(new java.awt.Dimension(610, 234));

        jLabel75.setBackground(new java.awt.Color(122, 138, 153));
        jLabel75.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel75.setForeground(java.awt.Color.white);
        jLabel75.setText("PIS ST");
        jLabel75.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel75.setOpaque(true);

        jLabel77.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel77.setText("Alíquota (percentual)");

        txtAliquotaPisSt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAliquotaPisSt.setText("pPIS");
        txtAliquotaPisSt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAliquotaPisSt.setName("decimal"); // NOI18N
        txtAliquotaPisSt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAliquotaPisStFocusLost(evt);
            }
        });
        txtAliquotaPisSt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAliquotaPisStKeyReleased(evt);
            }
        });

        jLabel79.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel79.setText("Tipo de Cálculo");

        cboPisStTipoCalculo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboPisStTipoCalculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPisStTipoCalculoActionPerformed(evt);
            }
        });

        jLabel73.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel73.setText("Alíquota (reais)");

        txtAliquotaPisStReais.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAliquotaPisStReais.setText("vAliqProd");
        txtAliquotaPisStReais.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAliquotaPisStReais.setName("decimal"); // NOI18N
        txtAliquotaPisStReais.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAliquotaPisStReaisFocusLost(evt);
            }
        });
        txtAliquotaPisStReais.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAliquotaPisStReaisKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout pnlIcms4Layout = new javax.swing.GroupLayout(pnlIcms4);
        pnlIcms4.setLayout(pnlIcms4Layout);
        pnlIcms4Layout.setHorizontalGroup(
            pnlIcms4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel75, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlIcms4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlIcms4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcms4Layout.createSequentialGroup()
                        .addComponent(jLabel79)
                        .addGap(18, 18, 18)
                        .addComponent(cboPisStTipoCalculo, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlIcms4Layout.createSequentialGroup()
                        .addComponent(jLabel77)
                        .addGap(18, 18, 18)
                        .addComponent(txtAliquotaPisSt, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlIcms4Layout.createSequentialGroup()
                        .addComponent(jLabel73)
                        .addGap(18, 18, 18)
                        .addComponent(txtAliquotaPisStReais, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(344, Short.MAX_VALUE))
        );
        pnlIcms4Layout.setVerticalGroup(
            pnlIcms4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIcms4Layout.createSequentialGroup()
                .addComponent(jLabel75)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlIcms4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboPisStTipoCalculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel79))
                .addGap(18, 18, 18)
                .addGroup(pnlIcms4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAliquotaPisSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel77))
                .addGap(18, 18, 18)
                .addGroup(pnlIcms4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtAliquotaPisStReais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel73))
                .addGap(52, 52, 52))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(pnlIcms3, javax.swing.GroupLayout.PREFERRED_SIZE, 589, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(pnlIcms4, javax.swing.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel65)
                        .addGap(18, 18, 18)
                        .addComponent(cboPis, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboPis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel65))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlIcms3, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                    .addComponent(pnlIcms4, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("PIS", jPanel5);

        jLabel89.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel89.setText("Situação Tributária");

        cboCofins.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboCofins.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCofinsActionPerformed(evt);
            }
        });

        pnlIcms5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlIcms5.setPreferredSize(new java.awt.Dimension(610, 234));

        jLabel83.setBackground(new java.awt.Color(122, 138, 153));
        jLabel83.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel83.setForeground(java.awt.Color.white);
        jLabel83.setText("COFINS");
        jLabel83.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel83.setOpaque(true);

        jLabel84.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel84.setText("Alíquota (percentual)");

        txtAliquotaCofins.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAliquotaCofins.setText("pCOFINS");
        txtAliquotaCofins.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAliquotaCofins.setName("decimal"); // NOI18N
        txtAliquotaCofins.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAliquotaCofinsFocusLost(evt);
            }
        });
        txtAliquotaCofins.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAliquotaCofinsKeyReleased(evt);
            }
        });

        jLabel85.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel85.setText("Tipo de Cálculo");

        cboCofinsTipoCalculo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboCofinsTipoCalculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCofinsTipoCalculoActionPerformed(evt);
            }
        });

        jLabel87.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel87.setText("Alíquota (reais)");

        txtAliquotaCofinsReais.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAliquotaCofinsReais.setText("vAliqProd");
        txtAliquotaCofinsReais.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAliquotaCofinsReais.setName("decimal"); // NOI18N
        txtAliquotaCofinsReais.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAliquotaCofinsReaisFocusLost(evt);
            }
        });
        txtAliquotaCofinsReais.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAliquotaCofinsReaisKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout pnlIcms5Layout = new javax.swing.GroupLayout(pnlIcms5);
        pnlIcms5.setLayout(pnlIcms5Layout);
        pnlIcms5Layout.setHorizontalGroup(
            pnlIcms5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel83, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlIcms5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlIcms5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcms5Layout.createSequentialGroup()
                        .addComponent(jLabel85)
                        .addGap(18, 18, 18)
                        .addComponent(cboCofinsTipoCalculo, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlIcms5Layout.createSequentialGroup()
                        .addComponent(jLabel84)
                        .addGap(18, 18, 18)
                        .addComponent(txtAliquotaCofins, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlIcms5Layout.createSequentialGroup()
                        .addComponent(jLabel87)
                        .addGap(18, 18, 18)
                        .addComponent(txtAliquotaCofinsReais, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(323, Short.MAX_VALUE))
        );
        pnlIcms5Layout.setVerticalGroup(
            pnlIcms5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIcms5Layout.createSequentialGroup()
                .addComponent(jLabel83)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlIcms5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboCofinsTipoCalculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel85))
                .addGap(18, 18, 18)
                .addGroup(pnlIcms5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAliquotaCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel84))
                .addGap(18, 18, 18)
                .addGroup(pnlIcms5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAliquotaCofinsReais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel87))
                .addGap(52, 52, 52))
        );

        pnlIcms6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlIcms6.setPreferredSize(new java.awt.Dimension(610, 234));

        jLabel91.setBackground(new java.awt.Color(122, 138, 153));
        jLabel91.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel91.setForeground(java.awt.Color.white);
        jLabel91.setText("COFINS ST");
        jLabel91.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel91.setOpaque(true);

        jLabel92.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel92.setText("Alíquota (percentual)");

        txtAliquotaCofinsSt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAliquotaCofinsSt.setText("pCOFINS");
        txtAliquotaCofinsSt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAliquotaCofinsSt.setName("decimal"); // NOI18N
        txtAliquotaCofinsSt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAliquotaCofinsStFocusLost(evt);
            }
        });
        txtAliquotaCofinsSt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAliquotaCofinsStKeyReleased(evt);
            }
        });

        jLabel94.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel94.setText("Tipo de Cálculo");

        cboCofinsStTipoCalculo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboCofinsStTipoCalculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCofinsStTipoCalculoActionPerformed(evt);
            }
        });

        jLabel95.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel95.setText("Alíquota (reais)");

        txtAliquotaCofinsStReais.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAliquotaCofinsStReais.setText("vAliqProd");
        txtAliquotaCofinsStReais.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAliquotaCofinsStReais.setName("decimal"); // NOI18N
        txtAliquotaCofinsStReais.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAliquotaCofinsStReaisFocusLost(evt);
            }
        });
        txtAliquotaCofinsStReais.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAliquotaCofinsStReaisKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout pnlIcms6Layout = new javax.swing.GroupLayout(pnlIcms6);
        pnlIcms6.setLayout(pnlIcms6Layout);
        pnlIcms6Layout.setHorizontalGroup(
            pnlIcms6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel91, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlIcms6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlIcms6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcms6Layout.createSequentialGroup()
                        .addComponent(jLabel94)
                        .addGap(18, 18, 18)
                        .addComponent(cboCofinsStTipoCalculo, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlIcms6Layout.createSequentialGroup()
                        .addComponent(jLabel92)
                        .addGap(18, 18, 18)
                        .addComponent(txtAliquotaCofinsSt, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlIcms6Layout.createSequentialGroup()
                        .addComponent(jLabel95)
                        .addGap(18, 18, 18)
                        .addComponent(txtAliquotaCofinsStReais, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(347, Short.MAX_VALUE))
        );
        pnlIcms6Layout.setVerticalGroup(
            pnlIcms6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIcms6Layout.createSequentialGroup()
                .addComponent(jLabel91)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlIcms6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboCofinsStTipoCalculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel94))
                .addGap(18, 18, 18)
                .addGroup(pnlIcms6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAliquotaCofinsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel92))
                .addGap(18, 18, 18)
                .addGroup(pnlIcms6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtAliquotaCofinsStReais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel95))
                .addGap(52, 52, 52))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(pnlIcms5, javax.swing.GroupLayout.PREFERRED_SIZE, 586, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(pnlIcms6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel89)
                        .addGap(18, 18, 18)
                        .addComponent(cboCofins, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel89))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlIcms5, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                    .addComponent(pnlIcms6, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("COFINS", jPanel6);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(18, 18, 18)
                        .addComponent(cboIcmsNfe, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel45)
                        .addGap(18, 18, 18)
                        .addComponent(cboOrigemNfe, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel43)
                        .addGap(18, 18, 18)
                        .addComponent(btnPesquisarNcmNfe)
                        .addGap(18, 18, 18)
                        .addComponent(txtNcmNfe, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtNcmDescricaoNfe)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel44)
                        .addGap(18, 18, 18)
                        .addComponent(btnPesquisarCestNfe)
                        .addGap(18, 18, 18)
                        .addComponent(txtCestNfe, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel28)
                        .addGap(18, 18, 18)
                        .addComponent(cboUnidadeTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel27)
                        .addGap(18, 18, 18)
                        .addComponent(txtValorUnitarioTributacao, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel41)
                        .addGap(21, 21, 21)
                        .addComponent(cboCfopDentroDoEstadoNfe, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel13)
                        .addGap(21, 21, 21)
                        .addComponent(cboCfopSaidaForaDoEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                        .addComponent(txtGenero, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(cboOrigemNfe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel43)
                    .addComponent(btnPesquisarNcmNfe, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNcmNfe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNcmDescricaoNfe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44)
                    .addComponent(btnPesquisarCestNfe, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtCestNfe)
                    .addComponent(jLabel28)
                    .addComponent(cboUnidadeTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27)
                    .addComponent(txtValorUnitarioTributacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(cboCfopDentroDoEstadoNfe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboCfopSaidaForaDoEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboIcmsNfe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabPrincipal.addTab("Dados Fiscais NFe", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabPrincipal, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
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
                .addContainerGap(21, Short.MAX_VALUE))
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
        txtNome.requestFocus();
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
        chavearIcms();
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

    private void cboOrigemSatFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboOrigemSatFocusLost
        sincronizarOrigemNfe();
    }//GEN-LAST:event_cboOrigemSatFocusLost

    private void cboOrigemNfeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboOrigemNfeFocusLost
        sincronizarOrigemSat();
    }//GEN-LAST:event_cboOrigemNfeFocusLost

    private void cboCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCategoriaActionPerformed
        alertaTamanhos();
    }//GEN-LAST:event_cboCategoriaActionPerformed

    private void tblTamanhoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTamanhoMouseClicked
        if(evt.getClickCount() == 2) {
            editarTamanho();
        }
    }//GEN-LAST:event_tblTamanhoMouseClicked

    private void txtAliquotaIcmsSatFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaIcmsSatFocusLost
        sincronizarAliquotaIcmsNfe();
    }//GEN-LAST:event_txtAliquotaIcmsSatFocusLost

    private void txtAliquotaIcmsNfeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaIcmsNfeFocusLost
        sincronizarAliquotaIcmsSat();
    }//GEN-LAST:event_txtAliquotaIcmsNfeFocusLost

    private void cboPisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPisActionPerformed
        chavearPis();
    }//GEN-LAST:event_cboPisActionPerformed

    private void txtAliquotaPisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaPisFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaPisFocusLost

    private void txtAliquotaPisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaPisKeyReleased
    }//GEN-LAST:event_txtAliquotaPisKeyReleased

    private void cboPisTipoCalculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPisTipoCalculoActionPerformed
        chavearPisTipoCalculo();
    }//GEN-LAST:event_cboPisTipoCalculoActionPerformed

    private void txtAliquotaPisReaisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaPisReaisFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaPisReaisFocusLost

    private void txtAliquotaPisReaisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaPisReaisKeyReleased
    }//GEN-LAST:event_txtAliquotaPisReaisKeyReleased

    private void txtAliquotaPisStFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaPisStFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaPisStFocusLost

    private void txtAliquotaPisStKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaPisStKeyReleased
    }//GEN-LAST:event_txtAliquotaPisStKeyReleased

    private void cboPisStTipoCalculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPisStTipoCalculoActionPerformed
        chavearPisStTipoCalculo();
    }//GEN-LAST:event_cboPisStTipoCalculoActionPerformed

    private void txtAliquotaPisStReaisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaPisStReaisFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaPisStReaisFocusLost

    private void txtAliquotaPisStReaisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaPisStReaisKeyReleased
    }//GEN-LAST:event_txtAliquotaPisStReaisKeyReleased

    private void cboCofinsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCofinsActionPerformed
        chavearCofins();
    }//GEN-LAST:event_cboCofinsActionPerformed

    private void txtAliquotaCofinsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaCofinsFocusLost

    private void txtAliquotaCofinsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsKeyReleased
    }//GEN-LAST:event_txtAliquotaCofinsKeyReleased

    private void cboCofinsTipoCalculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCofinsTipoCalculoActionPerformed
        chavearCofinsTipoCalculo();
    }//GEN-LAST:event_cboCofinsTipoCalculoActionPerformed

    private void txtAliquotaCofinsReaisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsReaisFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaCofinsReaisFocusLost

    private void txtAliquotaCofinsReaisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsReaisKeyReleased
    }//GEN-LAST:event_txtAliquotaCofinsReaisKeyReleased

    private void txtAliquotaCofinsStFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsStFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaCofinsStFocusLost

    private void txtAliquotaCofinsStKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsStKeyReleased
    }//GEN-LAST:event_txtAliquotaCofinsStKeyReleased

    private void cboCofinsStTipoCalculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCofinsStTipoCalculoActionPerformed
        chavearCofinsStTipoCalculo();
    }//GEN-LAST:event_cboCofinsStTipoCalculoActionPerformed

    private void txtAliquotaCofinsStReaisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsStReaisFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaCofinsStReaisFocusLost

    private void txtAliquotaCofinsStReaisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsStReaisKeyReleased
    }//GEN-LAST:event_txtAliquotaCofinsStReaisKeyReleased

    private void cboUnidadeVendaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboUnidadeVendaFocusLost
        espelharParaTributavel();
    }//GEN-LAST:event_cboUnidadeVendaFocusLost

    private void txtValorVendaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtValorVendaFocusLost
        espelharParaTributavel();
    }//GEN-LAST:event_txtValorVendaFocusLost

    private void cboUnidadeTributavelFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboUnidadeTributavelFocusGained
        espelharParaTributavel();
    }//GEN-LAST:event_cboUnidadeTributavelFocusGained

    private void txtValorUnitarioTributacaoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtValorUnitarioTributacaoFocusGained
        espelharParaTributavel();
    }//GEN-LAST:event_txtValorUnitarioTributacaoFocusGained


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
    private javax.swing.JComboBox<Object> cboCofins;
    private javax.swing.JComboBox<Object> cboCofinsStTipoCalculo;
    private javax.swing.JComboBox<Object> cboCofinsTipoCalculo;
    private javax.swing.JComboBox<Object> cboConteudoUnidade;
    private javax.swing.JComboBox<Object> cboIcmsNfe;
    private javax.swing.JComboBox<Object> cboIcmsSat;
    private javax.swing.JComboBox<Object> cboModalidadeBcIcms;
    private javax.swing.JComboBox<Object> cboModalidadeBcIcmsSt;
    private javax.swing.JComboBox<Object> cboMotivoDesoneracao;
    private javax.swing.JComboBox<Object> cboOrigemNfe;
    private javax.swing.JComboBox<Object> cboOrigemSat;
    private javax.swing.JComboBox<Object> cboPis;
    private javax.swing.JComboBox<Object> cboPisStTipoCalculo;
    private javax.swing.JComboBox<Object> cboPisTipoCalculo;
    private javax.swing.JComboBox<Object> cboTipo;
    private javax.swing.JComboBox<Object> cboUnidadeTributavel;
    private javax.swing.JComboBox<Object> cboUnidadeVenda;
    private javax.swing.JCheckBox chkBalanca;
    private javax.swing.JCheckBox chkMontavel;
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
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabPrincipal;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblEstoqueInicial;
    private javax.swing.JPanel pnlIcms;
    private javax.swing.JPanel pnlIcms3;
    private javax.swing.JPanel pnlIcms4;
    private javax.swing.JPanel pnlIcms5;
    private javax.swing.JPanel pnlIcms6;
    private javax.swing.JPanel pnlIcmsSt;
    private javax.swing.JPanel pnlTamanhos;
    private javax.swing.JTable tblTamanho;
    private javax.swing.JFormattedTextField txtAliquotaCofins;
    private javax.swing.JFormattedTextField txtAliquotaCofinsReais;
    private javax.swing.JFormattedTextField txtAliquotaCofinsSt;
    private javax.swing.JFormattedTextField txtAliquotaCofinsStReais;
    private javax.swing.JFormattedTextField txtAliquotaIcmsNfe;
    private javax.swing.JFormattedTextField txtAliquotaIcmsSat;
    private javax.swing.JFormattedTextField txtAliquotaIcmsSt;
    private javax.swing.JFormattedTextField txtAliquotaPis;
    private javax.swing.JFormattedTextField txtAliquotaPisReais;
    private javax.swing.JFormattedTextField txtAliquotaPisSt;
    private javax.swing.JFormattedTextField txtAliquotaPisStReais;
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
    private javax.swing.JFormattedTextField txtPercentualBcOperacaoPropria;
    private javax.swing.JFormattedTextField txtPercentualMargemValorAdicionadoIcmsSt;
    private javax.swing.JFormattedTextField txtPercentualReducaoBcIcms;
    private javax.swing.JFormattedTextField txtPercentualReducaoBcIcmsSt;
    private javax.swing.JFormattedTextField txtValorCompra;
    private javax.swing.JFormattedTextField txtValorUnitarioTributacao;
    private javax.swing.JFormattedTextField txtValorVenda;
    // End of variables declaration//GEN-END:variables
}
