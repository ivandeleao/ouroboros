/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoSaida;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.KeyStroke;
import model.nosql.TipoCalculoEnum;
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
import model.mysql.bean.principal.documento.Venda;
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
import model.mysql.dao.principal.VendaDAO;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.Cor;
import util.Decimal;
import util.JSwing;
import view.produto.CestPesquisaView;
import view.produto.NcmPesquisaView;
import view.sistema.AjudaView;

/**
 *
 * @author ivand
 *
 * ESTE FOI CRIADO COMO JDIALOG DESDE O INÍCIO - USAR ESTE COMO REFERÊNCIA PARA
 * OS DEMAIS
 *
 *
 */
public class VendaItemView extends javax.swing.JDialog {
    
    Venda documento;
    MovimentoFisico movimentoFisico;
    
    VendaDAO vendaDAO = new VendaDAO();
    MovimentoFisicoDAO movimentoFisicoDAO = new MovimentoFisicoDAO();
    

    private VendaItemView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public VendaItemView(MovimentoFisico movimentoFisico) {
        super(MAIN_VIEW, true);
        initComponents();
        JSwing.startComponentsBehavior(this);
        definirAtalhos();

        this.documento = movimentoFisico.getVenda();
        this.movimentoFisico = movimentoFisico;
        
        pnlIcmsEfetivo.setVisible(false);

        carregarCfop();
        carregarUnidadeComercial();
        carregarUnidadeTributavel();
        
        //Icms------------------------------------------------------------------
        carregarOrigem();
        carregarIcms();
        carregarModalidadeBcIcms();
        carregarMotivoDesoneracao();
        carregarModalidadeBcIcmsSt();
        //Fim Icms--------------------------------------------------------------
        
        //Pis-------------------------------------------------------------------
        carregarPis();
        carregarPisTipoCalculo();
        carregarPisStTipoCalculo();
        chavearPis();
        //Fim Pis---------------------------------------------------------------
        
        //Cofins-------------------------------------------------------------------
        carregarCofins();
        carregarCofinsTipoCalculo();
        carregarCofinsStTipoCalculo();
        chavearCofins();
        //Fim Cofins---------------------------------------------------------------
        
        carregarDados();
        
        configurarTela();

        this.setLocationRelativeTo(this);
        this.setVisible(true);
    }

    private void definirAtalhos() {
        //JRootPane rootPane = this.getRootPane();
        InputMap im = rootPane.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = rootPane.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "fechar");
        am.put("fechar", new FormKeyStroke("ESC"));
        
    }
    
    protected class FormKeyStroke extends AbstractAction {
        private final String key;
        public FormKeyStroke(String key){
            this.key = key;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            switch(key){
                case "ESC":
                    dispose();
                    break;
            }
        }
    }

    private void carregarDados() {
        //Principal-------------------------------------------------------------
        txtCodigo.setText(movimentoFisico.getCodigo());
        txtDescricao.setText(movimentoFisico.getDescricao());
        txtDescricao.setCaretPosition(0);
        txtExTipi.setText(movimentoFisico.getExTipi());
        txtNcm.setText(movimentoFisico.getNcm() != null ? movimentoFisico.getNcm().getCodigo() : "");
        txtCest.setText(movimentoFisico.getCest());
        cboCfop.setSelectedItem(movimentoFisico.getCfop());
        cboUnidadeComercial.setSelectedItem(movimentoFisico.getUnidadeComercialVenda());
        txtQuantidadeComercial.setText(Decimal.toString(movimentoFisico.getSaldoLinearAbsoluto()));
        txtValorUnitarioComercial.setText(Decimal.toString(movimentoFisico.getValor()));
        txtEan.setText(movimentoFisico.getEan());
        
        cboUnidadeTributavel.setSelectedItem(movimentoFisico.getUnidadeTributavel());
        txtQuantidadeTributavel.setText(Decimal.toString(movimentoFisico.getQuantidadeTributavel()));
        txtValorUnitarioTributavel.setText(Decimal.toString(movimentoFisico.getValorTributavel()));
        txtEanTributavel.setText(movimentoFisico.getEanTributavel());
        
        txtAcrescimo.setText(Decimal.toString(movimentoFisico.getAcrescimo()));
        txtDesconto.setText(Decimal.toString(movimentoFisico.getDesconto()));
        
        txtFrete.setText(Decimal.toString(movimentoFisico.getValorFrete()));
        txtSeguro.setText(Decimal.toString(movimentoFisico.getValorSeguro()));
        
        txtValorTotalBruto.setText(Decimal.toString(movimentoFisico.getSubtotal()));
        
        chkValorCompoeTotal.setSelected(movimentoFisico.isValorCompoeTotal());
        
        txtPedidoCompra.setText(movimentoFisico.getPedidoCompra());
        txtItemPedidoCompra.setText(movimentoFisico.getItemPedidoCompra().toString());
        
        calcularTotais();
        //Fim Principal---------------------------------------------------------
        
        
        
        //Tributos--------------------------------------------------------------
        txtTotalTributos.setText(Decimal.toString(movimentoFisico.getTotalTributos()));
        
        //Icms
        cboOrigem.setSelectedItem(movimentoFisico.getOrigem());
        cboIcms.setSelectedItem(movimentoFisico.getIcms());
        
        txtAliquotaAplicavelCalculoCreditoIcms.setText(Decimal.toString(movimentoFisico.getAliquotaAplicavelCalculoCreditoIcms()));
        txtValorCreditoIcms.setText(Decimal.toString(movimentoFisico.getValorCreditoIcms()));
        txtAliquotaSuportadaConsumidorFinal.setText(Decimal.toString(movimentoFisico.getAliquotaSuportadaConsumidorFinal()));
        
        //Icms
        cboModalidadeBcIcms.setSelectedItem(movimentoFisico.getModalidadeBcIcms());
        txtPercentualReducaoBcIcms.setText(Decimal.toString(movimentoFisico.getPercentualReducaoBcIcms()));
        txtValorBcIcms.setText(Decimal.toString(movimentoFisico.getValorBcIcms()));
        txtAliquotaIcms.setText(Decimal.toString(movimentoFisico.getAliquotaIcms()));
        txtValorIcms.setText(Decimal.toString(movimentoFisico.getValorIcms()));
        txtPercentualBcOperacaoPropria.setText(Decimal.toString(movimentoFisico.getPercentualBcOperacaoPropria()));
        txtValorIcmsDesonerado.setText(Decimal.toString(movimentoFisico.getValorIcmsDesonerado()));
        cboMotivoDesoneracao.setSelectedItem(movimentoFisico.getMotivoDesoneracao());
        
        //Icms St
        cboModalidadeBcIcmsSt.setSelectedItem(movimentoFisico.getModalidadeBcIcmsSt());
        txtPercentualReducaoBcIcmsSt.setText(Decimal.toString(movimentoFisico.getPercentualReducaoBcIcmsSt()));
        txtPercentualMargemValorAdicionadoIcmsSt.setText(Decimal.toString(movimentoFisico.getPercentualMargemValorAdicionadoIcmsSt()));
        txtBcIcmsSt.setText(Decimal.toString(movimentoFisico.getValorBcIcmsSt()));
        txtAliquotaIcmsSt.setText(Decimal.toString(movimentoFisico.getAliquotaIcmsSt()));
        txtValorIcmsSt.setText(Decimal.toString(movimentoFisico.getValorIcmsSt()));
        txtValorBcIcmsStRetido.setText(Decimal.toString(movimentoFisico.getValorBcIcmsStRetido()));
        txtValorIcmsStRetido.setText(Decimal.toString(movimentoFisico.getValorIcmsStRetido()));
        txtValorIcmsProprioSubstituto.setText(Decimal.toString(movimentoFisico.getValorIcmsProprioSubstituto()));
        
        //Pis
        cboPis.setSelectedItem(movimentoFisico.getPis());
        cboPisTipoCalculo.setSelectedItem(movimentoFisico.getPisTipoCalculo());
        txtValorBcPis.setText(Decimal.toString(movimentoFisico.getValorBcPis()));
        txtAliquotaPis.setText(Decimal.toString(movimentoFisico.getAliquotaPis()));
        txtAliquotaPisReais.setText(Decimal.toString(movimentoFisico.getAliquotaPisReais()));
        txtQuantidadeVendidaPis.setText(Decimal.toString(movimentoFisico.getQuantidadeVendidaPis()));
        txtValorPis.setText(Decimal.toString(movimentoFisico.getValorPis()));
        
        //Pis St
        cboPisStTipoCalculo.setSelectedItem(movimentoFisico.getPisStTipoCalculo());
        txtValorBcPisSt.setText(Decimal.toString(movimentoFisico.getValorBcPisSt()));
        txtAliquotaPisSt.setText(Decimal.toString(movimentoFisico.getAliquotaPisSt()));
        txtAliquotaPisStReais.setText(Decimal.toString(movimentoFisico.getAliquotaPisStReais()));
        txtQuantidadeVendidaPisSt.setText(Decimal.toString(movimentoFisico.getQuantidadeVendidaPisSt()));
        txtValorPisSt.setText(Decimal.toString(movimentoFisico.getValorPisSt()));
        
        //Cofins
        cboCofins.setSelectedItem(movimentoFisico.getCofins());
        cboCofinsTipoCalculo.setSelectedItem(movimentoFisico.getCofinsTipoCalculo());
        txtValorBcCofins.setText(Decimal.toString(movimentoFisico.getValorBcCofins()));
        txtAliquotaCofins.setText(Decimal.toString(movimentoFisico.getAliquotaCofins()));
        txtAliquotaCofinsReais.setText(Decimal.toString(movimentoFisico.getAliquotaCofinsReais()));
        txtQuantidadeVendidaCofins.setText(Decimal.toString(movimentoFisico.getQuantidadeVendidaCofins()));
        txtValorCofins.setText(Decimal.toString(movimentoFisico.getValorCofins()));
        
        //Cofins St
        cboCofinsStTipoCalculo.setSelectedItem(movimentoFisico.getCofinsStTipoCalculo());
        txtValorBcCofinsSt.setText(Decimal.toString(movimentoFisico.getValorBcCofinsSt()));
        txtAliquotaCofinsSt.setText(Decimal.toString(movimentoFisico.getAliquotaCofinsSt()));
        txtAliquotaCofinsStReais.setText(Decimal.toString(movimentoFisico.getAliquotaCofinsStReais()));
        txtQuantidadeVendidaCofinsSt.setText(Decimal.toString(movimentoFisico.getQuantidadeVendidaCofinsSt()));
        txtValorCofinsSt.setText(Decimal.toString(movimentoFisico.getValorCofinsSt()));
        
        
        
        //Fim Tributos----------------------------------------------------------
        
        
    }
    
    private void configurarTela() {
        if(movimentoFisico.isAgrupado()) {
            lblInfo.setText("*Este item pertence ao documento de id " + movimentoFisico.getVenda().getId());
        }
    }
    
    private void carregarCfop() {
        List<Cfop> cfopList = new CfopDAO().findAll();

        cboCfop.addItem(null);
        cfopList.forEach((cfop) -> {
            cboCfop.addItem(cfop);
        });
    }
    
    private void carregarUnidadeComercial() {
        List<UnidadeComercial> listUC = new UnidadeComercialDAO().findAll();

        cboUnidadeComercial.addItem(null);
        for (UnidadeComercial uc : listUC) {
            cboUnidadeComercial.addItem(uc);
        }
    }
    
    private void carregarUnidadeTributavel() {
        List<UnidadeComercial> listUC = new UnidadeComercialDAO().findAll();

        cboUnidadeTributavel.addItem(null);
        for (UnidadeComercial uc : listUC) {
            cboUnidadeTributavel.addItem(uc);
        }
    }
    
    private void carregarOrigem() {
        List<ProdutoOrigem> poList = new ProdutoOrigemDAO().findAll();

        cboOrigem.addItem(null);
        for (ProdutoOrigem po : poList) {
            cboOrigem.addItem(po);
        }
    }

    private void carregarIcms() {
        List<Icms> icmsList;
        if (Ouroboros.NFE_REGIME_TRIBUTARIO.getId() == 3) { //simples nacional
            icmsList = new IcmsDAO().listarSimplesNacional();
        } else {
            icmsList = new IcmsDAO().listarTributacaoNormal();
        }

        cboIcms.addItem(null);
        for (Icms icms : icmsList) {
            cboIcms.addItem(icms);
        }
    }
    
    private void carregarModalidadeBcIcms() {
        List<ModalidadeBcIcms> mods = new ModalidadeBcIcmsDAO().findAll();

        cboModalidadeBcIcms.addItem(null);
        for (ModalidadeBcIcms mod : mods) {
            cboModalidadeBcIcms.addItem(mod);
        }
    }
    
    private void carregarMotivoDesoneracao() {
        List<MotivoDesoneracao> mots = new MotivoDesoneracaoDAO().findAll();

        cboMotivoDesoneracao.addItem(null);
        for (MotivoDesoneracao mot : mots) {
            cboMotivoDesoneracao.addItem(mot);
        }
    }

    private void carregarModalidadeBcIcmsSt() {
        List<ModalidadeBcIcmsSt> mods = new ModalidadeBcIcmsStDAO().findAll();

        cboModalidadeBcIcmsSt.addItem(null);
        for (ModalidadeBcIcmsSt mod : mods) {
            cboModalidadeBcIcmsSt.addItem(mod);
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

    private void pesquisarNcm() {
        NcmPesquisaView ncmPesquisaView = new NcmPesquisaView(MAIN_VIEW);

        Ncm ncm = ncmPesquisaView.getNcm();
        if (ncm != null) {
            txtNcm.setText(ncm.getCodigo());
        }
    }
    
    private void pesquisarCest(String codigoNcm) {
        CestPesquisaView cestPesquisaView = new CestPesquisaView(codigoNcm);
        Cest cest = cestPesquisaView.getCest();
        if (cest != null) {
            txtCest.setText(cest.getCodigo());
        }
        
    }
    
    
    private void salvar() {
        //Principal-------------------------------------------------------------
        movimentoFisico.setCodigo(txtCodigo.getText());
        movimentoFisico.setDescricao(txtDescricao.getText());
        movimentoFisico.setExTipi(txtExTipi.getText());
        movimentoFisico.setNcm(new NcmDAO().findByCodigo(txtNcm.getText()));
        movimentoFisico.setCest(txtCest.getText());
        movimentoFisico.setCfop((Cfop) cboCfop.getSelectedItem());
        movimentoFisico.setUnidadeComercialVenda((UnidadeComercial) cboUnidadeComercial.getSelectedItem());
        movimentoFisico.setSaida(Decimal.fromString(txtQuantidadeComercial.getText())); //ajustar de acordo com o tipo de operação
        movimentoFisico.setValor(Decimal.fromString(txtValorUnitarioComercial.getText()));
        movimentoFisico.setEan(txtEan.getText());
        
        movimentoFisico.setUnidadeTributavel((UnidadeComercial) cboUnidadeTributavel.getSelectedItem());
        movimentoFisico.setQuantidadeTributavel(Decimal.fromString(txtQuantidadeTributavel.getText()));
        movimentoFisico.setValorTributavel(Decimal.fromString(txtValorUnitarioTributavel.getText()));
        movimentoFisico.setEanTributavel(txtEanTributavel.getText());
        
        movimentoFisico.setAcrescimoMonetario(Decimal.fromString(txtAcrescimo.getText()));
        movimentoFisico.setDescontoMonetario(Decimal.fromString(txtDesconto.getText()));
        
        movimentoFisico.setValorFrete(Decimal.fromString(txtFrete.getText()));
        movimentoFisico.setValorSeguro(Decimal.fromString(txtSeguro.getText()));
        
        movimentoFisico.setValorCompoeTotal(chkValorCompoeTotal.isSelected());
        
        movimentoFisico.setPedidoCompra(txtPedidoCompra.getText());
        movimentoFisico.setItemPedidoCompra(Integer.valueOf(txtItemPedidoCompra.getText()));
        //Fim Principal---------------------------------------------------------
        
        
        //Tributos--------------------------------------------------------------
        movimentoFisico.setOrigem((ProdutoOrigem) cboOrigem.getSelectedItem());
        movimentoFisico.setIcms((Icms) cboIcms.getSelectedItem());
        
        movimentoFisico.setAliquotaAplicavelCalculoCreditoIcms(Decimal.fromString(txtAliquotaAplicavelCalculoCreditoIcms.getText()));
        movimentoFisico.setValorCreditoIcms(Decimal.fromString(txtValorCreditoIcms.getText()));
        movimentoFisico.setAliquotaSuportadaConsumidorFinal(Decimal.fromString(txtAliquotaSuportadaConsumidorFinal.getText()));
        
        movimentoFisico.setModalidadeBcIcms((ModalidadeBcIcms) cboModalidadeBcIcms.getSelectedItem());
        movimentoFisico.setPercentualReducaoBcIcms(Decimal.fromString(txtPercentualReducaoBcIcms.getText()));
        movimentoFisico.setValorBcIcms(Decimal.fromString(txtValorBcIcms.getText()));
        movimentoFisico.setAliquotaIcms(Decimal.fromString(txtAliquotaIcms.getText()));
        movimentoFisico.setValorIcms(Decimal.fromString(txtValorIcms.getText()));
        movimentoFisico.setPercentualBcOperacaoPropria(Decimal.fromString(txtPercentualBcOperacaoPropria.getText()));
        movimentoFisico.setValorIcmsDesonerado(Decimal.fromString(txtValorIcmsDesonerado.getText()));
        movimentoFisico.setMotivoDesoneracao((MotivoDesoneracao) cboMotivoDesoneracao.getSelectedItem());
        
        movimentoFisico.setModalidadeBcIcmsSt((ModalidadeBcIcmsSt) cboModalidadeBcIcmsSt.getSelectedItem());
        movimentoFisico.setPercentualReducaoBcIcmsSt(Decimal.fromString(txtPercentualReducaoBcIcmsSt.getText()));
        movimentoFisico.setPercentualMargemValorAdicionadoIcmsSt(Decimal.fromString(txtPercentualMargemValorAdicionadoIcmsSt.getText()));
        movimentoFisico.setValorBcIcmsSt(Decimal.fromString(txtBcIcmsSt.getText()));
        movimentoFisico.setAliquotaIcmsSt(Decimal.fromString(txtAliquotaIcmsSt.getText()));
        movimentoFisico.setValorIcmsSt(Decimal.fromString(txtValorIcmsSt.getText()));
        movimentoFisico.setValorBcIcmsStRetido(Decimal.fromString(txtValorBcIcmsStRetido.getText()));
        movimentoFisico.setValorIcmsStRetido(Decimal.fromString(txtValorIcmsStRetido.getText()));
        movimentoFisico.setValorIcmsProprioSubstituto(Decimal.fromString(txtValorIcmsProprioSubstituto.getText()));
        
        //Pis
        movimentoFisico.setPis((Pis) cboPis.getSelectedItem());
        
        movimentoFisico.setPisTipoCalculo((TipoCalculoEnum) cboPisTipoCalculo.getSelectedItem());
        movimentoFisico.setValorBcPis(Decimal.fromString(txtValorBcPis.getText()));
        movimentoFisico.setAliquotaPis(Decimal.fromString(txtAliquotaPis.getText()));
        movimentoFisico.setAliquotaPisReais(Decimal.fromString(txtAliquotaPisReais.getText()));
        movimentoFisico.setQuantidadeVendidaPis(Decimal.fromString(txtQuantidadeVendidaPis.getText()));
        movimentoFisico.setValorPis(Decimal.fromString(txtValorPis.getText()));
        
        //Pis St
        movimentoFisico.setPisStTipoCalculo((TipoCalculoEnum) cboPisStTipoCalculo.getSelectedItem());
        movimentoFisico.setValorBcPisSt(Decimal.fromString(txtValorBcPisSt.getText()));
        movimentoFisico.setAliquotaPisSt(Decimal.fromString(txtAliquotaPisSt.getText()));
        movimentoFisico.setAliquotaPisStReais(Decimal.fromString(txtAliquotaPisStReais.getText()));
        movimentoFisico.setQuantidadeVendidaPisSt(Decimal.fromString(txtQuantidadeVendidaPisSt.getText()));
        movimentoFisico.setValorPisSt(Decimal.fromString(txtValorPisSt.getText()));
        
        //Cofins
        movimentoFisico.setCofins((Cofins) cboCofins.getSelectedItem());
        
        movimentoFisico.setCofinsTipoCalculo((TipoCalculoEnum) cboCofinsTipoCalculo.getSelectedItem());
        movimentoFisico.setValorBcCofins(Decimal.fromString(txtValorBcCofins.getText()));
        movimentoFisico.setAliquotaCofins(Decimal.fromString(txtAliquotaCofins.getText()));
        movimentoFisico.setAliquotaCofinsReais(Decimal.fromString(txtAliquotaCofinsReais.getText()));
        movimentoFisico.setQuantidadeVendidaCofins(Decimal.fromString(txtQuantidadeVendidaCofins.getText()));
        movimentoFisico.setValorCofins(Decimal.fromString(txtValorCofins.getText()));
        
        //Cofins St
        movimentoFisico.setCofinsStTipoCalculo((TipoCalculoEnum) cboCofinsStTipoCalculo.getSelectedItem());
        movimentoFisico.setValorBcCofinsSt(Decimal.fromString(txtValorBcCofinsSt.getText()));
        movimentoFisico.setAliquotaCofinsSt(Decimal.fromString(txtAliquotaCofinsSt.getText()));
        movimentoFisico.setAliquotaCofinsStReais(Decimal.fromString(txtAliquotaCofinsStReais.getText()));
        movimentoFisico.setQuantidadeVendidaCofinsSt(Decimal.fromString(txtQuantidadeVendidaCofinsSt.getText()));
        movimentoFisico.setValorCofinsSt(Decimal.fromString(txtValorCofinsSt.getText()));
        
        //Fim Tributos----------------------------------------------------------
        
        
        movimentoFisico = movimentoFisicoDAO.save(movimentoFisico);
        
        documento.addMovimentoFisico(movimentoFisico);
        
        vendaDAO.save(documento);
    }
    
    private void chavearIcms() {
        txtAliquotaAplicavelCalculoCreditoIcms.setEditable(false);
        txtValorCreditoIcms.setEditable(false);
        txtAliquotaSuportadaConsumidorFinal.setEditable(false);
        
        JSwing.setComponentesHabilitados(pnlIcms, false);
        JSwing.setComponentesHabilitados(pnlIcmsSt, false);
        
        Icms icms = (Icms) cboIcms.getSelectedItem();
        
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
                    txtAliquotaIcms.setEnabled(false);
                    txtPercentualBcOperacaoPropria.setEnabled(false);
                    break;
                    
                case "41":
                    if (icms.getId() == 7) {
                        JSwing.setComponentesHabilitados(pnlIcms, true);
                        cboModalidadeBcIcms.setEnabled(false);
                        txtPercentualReducaoBcIcms.setEnabled(false);
                        txtAliquotaIcms.setEnabled(false);
                        txtPercentualBcOperacaoPropria.setEnabled(false);
                    }
                    break;

                case "50":
                    JSwing.setComponentesHabilitados(pnlIcms, true);
                    cboModalidadeBcIcms.setEnabled(false);
                    txtPercentualReducaoBcIcms.setEnabled(false);
                    txtAliquotaIcms.setEnabled(false);
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
                    txtAliquotaAplicavelCalculoCreditoIcms.setEditable(true);
                    txtValorCreditoIcms.setEditable(true);
                    
                case "102":
                case "103":
                    txtAliquotaAplicavelCalculoCreditoIcms.setText("0,00");
                    txtValorCreditoIcms.setText("0,00");
                    break;

                case "201":
                    txtAliquotaAplicavelCalculoCreditoIcms.setEditable(true);
                    txtValorCreditoIcms.setEditable(true);
                    
                    JSwing.setComponentesHabilitados(pnlIcmsSt, true);
                    
                case "202":
                case "203":
                    txtAliquotaAplicavelCalculoCreditoIcms.setText("0,00");
                    txtValorCreditoIcms.setText("0,00");
                    
                    JSwing.setComponentesHabilitados(pnlIcmsSt, true);
                    break;

                //case "300": Imune
                //case "400": Não tributada
                    
                case "500":
                    txtAliquotaSuportadaConsumidorFinal.setEditable(true);
                    
                    txtValorBcIcmsStRetido.setEnabled(true);
                    txtValorIcmsStRetido.setEnabled(true);
                    txtValorIcmsProprioSubstituto.setEnabled(true);
                    
                    break;

                case "900":
                    txtAliquotaAplicavelCalculoCreditoIcms.setEditable(true);
                    txtValorCreditoIcms.setEditable(true);
                    
                    JSwing.setComponentesHabilitados(pnlIcms, true);
                    JSwing.setComponentesHabilitados(pnlIcmsSt, true);
                    
                    txtPercentualBcOperacaoPropria.setEditable(false);
                    txtPercentualBcOperacaoPropria.setText("0,00");
                    
                    txtValorIcmsDesonerado.setEditable(false);
                    txtValorIcmsDesonerado.setText("0,00");
                    
                    cboMotivoDesoneracao.setEnabled(false);
                    
                    txtValorBcIcmsStRetido.setEditable(false);
                    txtValorBcIcmsStRetido.setText("0,00");
                    
                    txtValorIcmsStRetido.setEditable(false);
                    txtValorIcmsStRetido.setText("0,00");
                    
                    txtValorIcmsProprioSubstituto.setEditable(false);
                    txtValorIcmsProprioSubstituto.setText("0,00");
                    
                    
                    

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
                    //cboPisTipoCalculo.setEnabled(false);
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
            txtValorBcPis.setEditable(false);
            txtAliquotaPis.setEditable(false);
            txtQuantidadeVendidaPis.setEditable(false);
            txtAliquotaPisReais.setEditable(false);
            
            txtValorBcPis.setText("0,00");
            txtAliquotaPis.setText("0,00");
            txtQuantidadeVendidaPis.setText("0,00");
            txtAliquotaPisReais.setText("0,00");
            txtValorPis.setText("0,00");
        
        } else if(cboPisTipoCalculo.getSelectedItem().equals(TipoCalculoEnum.PERCENTUAL)) {
            txtValorBcPis.setEditable(true);
            txtAliquotaPis.setEditable(true);
            txtQuantidadeVendidaPis.setEditable(false);
            txtAliquotaPisReais.setEditable(false);
            
            txtQuantidadeVendidaPis.setText("0,00");
            txtAliquotaPisReais.setText("0,00");
            
            txtValorBcPis.requestFocus();
            
        } else {
            txtValorBcPis.setEditable(false);
            txtAliquotaPis.setEditable(false);
            txtAliquotaPisReais.setEditable(true);
            txtQuantidadeVendidaPis.setEditable(true);
            
            txtValorBcPis.setText("0,00");
            txtAliquotaPis.setText("0,00");
            
            txtQuantidadeVendidaPis.requestFocus();
            
        }
    }
    
    private void chavearPisStTipoCalculo() {
    
        if(cboPisStTipoCalculo.getSelectedItem() == null) {
            txtValorBcPisSt.setEditable(false);
            txtAliquotaPisSt.setEditable(false);
            txtQuantidadeVendidaPisSt.setEditable(false);
            txtAliquotaPisStReais.setEditable(false);
            
            txtValorBcPisSt.setText("0,00");
            txtAliquotaPisSt.setText("0,00");
            txtQuantidadeVendidaPisSt.setText("0,00");
            txtAliquotaPisStReais.setText("0,00");
            txtValorPisSt.setText("0,00");
        
        } else if(cboPisStTipoCalculo.getSelectedItem().equals(TipoCalculoEnum.PERCENTUAL)) {
            txtValorBcPisSt.setEditable(true);
            txtAliquotaPisSt.setEditable(true);
            txtQuantidadeVendidaPisSt.setEditable(false);
            txtAliquotaPisStReais.setEditable(false);
            
            txtQuantidadeVendidaPisSt.setText("0,00");
            txtAliquotaPisStReais.setText("0,00");
            
            txtValorBcPisSt.requestFocus();
            
        } else {
            txtValorBcPisSt.setEditable(false);
            txtAliquotaPisSt.setEditable(false);
            txtAliquotaPisStReais.setEditable(true);
            txtQuantidadeVendidaPisSt.setEditable(true);
            
            txtValorBcPisSt.setText("0,00");
            txtAliquotaPisSt.setText("0,00");
            
            txtQuantidadeVendidaPisSt.requestFocus();
            
        }
    }
    
    private void calcularPis() {
        if(cboPisTipoCalculo.getSelectedItem().equals(TipoCalculoEnum.PERCENTUAL)) {
            BigDecimal valorBcPis = Decimal.fromString(txtValorBcPis.getText());
            BigDecimal aliquotaPis = Decimal.fromString(txtAliquotaPis.getText());
            BigDecimal valorPis = valorBcPis.multiply(aliquotaPis).divide(new BigDecimal(100), RoundingMode.HALF_UP);
            txtValorPis.setText(Decimal.toString(valorPis));
            
        } else {
            BigDecimal quantidadeVendidaPis = Decimal.fromString(txtQuantidadeVendidaPis.getText());
            BigDecimal aliquotaPisReais = Decimal.fromString(txtAliquotaPisReais.getText());
            BigDecimal valorPis = quantidadeVendidaPis.multiply(aliquotaPisReais);
            txtValorPis.setText(Decimal.toString(valorPis));
            
        }
    }
    
    private void calcularPisSt() {
        if(cboPisStTipoCalculo.getSelectedItem().equals(TipoCalculoEnum.PERCENTUAL)) {
            BigDecimal valorBcPisSt = Decimal.fromString(txtValorBcPisSt.getText());
            BigDecimal aliquotaPisSt = Decimal.fromString(txtAliquotaPisSt.getText());
            BigDecimal valorPisSt = valorBcPisSt.multiply(aliquotaPisSt).divide(new BigDecimal(100), RoundingMode.HALF_UP);
            txtValorPisSt.setText(Decimal.toString(valorPisSt));
            
        } else {
            BigDecimal quantidadeVendidaPisSt = Decimal.fromString(txtQuantidadeVendidaPisSt.getText());
            BigDecimal aliquotaPisStReais = Decimal.fromString(txtAliquotaPisStReais.getText());
            BigDecimal valorPisSt = quantidadeVendidaPisSt.multiply(aliquotaPisStReais);
            txtValorPisSt.setText(Decimal.toString(valorPisSt));
            
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
                    //cboCofinsTipoCalculo.setEnabled(false);
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
            txtValorBcCofins.setEditable(false);
            txtAliquotaCofins.setEditable(false);
            txtQuantidadeVendidaCofins.setEditable(false);
            txtAliquotaCofinsReais.setEditable(false);
            
            txtValorBcCofins.setText("0,00");
            txtAliquotaCofins.setText("0,00");
            txtQuantidadeVendidaCofins.setText("0,00");
            txtAliquotaCofinsReais.setText("0,00");
            txtValorCofins.setText("0,00");
        
        } else if(cboCofinsTipoCalculo.getSelectedItem().equals(TipoCalculoEnum.PERCENTUAL)) {
            txtValorBcCofins.setEditable(true);
            txtAliquotaCofins.setEditable(true);
            txtQuantidadeVendidaCofins.setEditable(false);
            txtAliquotaCofinsReais.setEditable(false);
            
            txtQuantidadeVendidaCofins.setText("0,00");
            txtAliquotaCofinsReais.setText("0,00");
            
            txtValorBcCofins.requestFocus();
            
        } else {
            txtValorBcCofins.setEditable(false);
            txtAliquotaCofins.setEditable(false);
            txtAliquotaCofinsReais.setEditable(true);
            txtQuantidadeVendidaCofins.setEditable(true);
            
            txtValorBcCofins.setText("0,00");
            txtAliquotaCofins.setText("0,00");
            
            txtQuantidadeVendidaCofins.requestFocus();
            
        }
    }
    
    private void chavearCofinsStTipoCalculo() {
    
        if(cboCofinsStTipoCalculo.getSelectedItem() == null) {
            txtValorBcCofinsSt.setEditable(false);
            txtAliquotaCofinsSt.setEditable(false);
            txtQuantidadeVendidaCofinsSt.setEditable(false);
            txtAliquotaCofinsStReais.setEditable(false);
            
            txtValorBcCofinsSt.setText("0,00");
            txtAliquotaCofinsSt.setText("0,00");
            txtQuantidadeVendidaCofinsSt.setText("0,00");
            txtAliquotaCofinsStReais.setText("0,00");
            txtValorCofinsSt.setText("0,00");
        
        } else if(cboCofinsStTipoCalculo.getSelectedItem().equals(TipoCalculoEnum.PERCENTUAL)) {
            txtValorBcCofinsSt.setEditable(true);
            txtAliquotaCofinsSt.setEditable(true);
            txtQuantidadeVendidaCofinsSt.setEditable(false);
            txtAliquotaCofinsStReais.setEditable(false);
            
            txtQuantidadeVendidaCofinsSt.setText("0,00");
            txtAliquotaCofinsStReais.setText("0,00");
            
            txtValorBcCofinsSt.requestFocus();
            
        } else {
            txtValorBcCofinsSt.setEditable(false);
            txtAliquotaCofinsSt.setEditable(false);
            txtAliquotaCofinsStReais.setEditable(true);
            txtQuantidadeVendidaCofinsSt.setEditable(true);
            
            txtValorBcCofinsSt.setText("0,00");
            txtAliquotaCofinsSt.setText("0,00");
            
            txtQuantidadeVendidaCofinsSt.requestFocus();
            
        }
    }
    
    private void calcularCofins() {
        if(cboCofinsTipoCalculo.getSelectedItem().equals(TipoCalculoEnum.PERCENTUAL)) {
            BigDecimal valorBcCofins = Decimal.fromString(txtValorBcCofins.getText());
            BigDecimal aliquotaCofins = Decimal.fromString(txtAliquotaCofins.getText());
            BigDecimal valorCofins = valorBcCofins.multiply(aliquotaCofins).divide(new BigDecimal(100), RoundingMode.HALF_UP);
            txtValorCofins.setText(Decimal.toString(valorCofins));
            
        } else {
            BigDecimal quantidadeVendidaCofins = Decimal.fromString(txtQuantidadeVendidaCofins.getText());
            BigDecimal aliquotaCofinsReais = Decimal.fromString(txtAliquotaCofinsReais.getText());
            BigDecimal valorCofins = quantidadeVendidaCofins.multiply(aliquotaCofinsReais);
            txtValorCofins.setText(Decimal.toString(valorCofins));
            
        }
    }
    
    private void calcularCofinsSt() {
        if(cboCofinsStTipoCalculo.getSelectedItem().equals(TipoCalculoEnum.PERCENTUAL)) {
            BigDecimal valorBcCofinsSt = Decimal.fromString(txtValorBcCofinsSt.getText());
            BigDecimal aliquotaCofinsSt = Decimal.fromString(txtAliquotaCofinsSt.getText());
            BigDecimal valorCofinsSt = valorBcCofinsSt.multiply(aliquotaCofinsSt).divide(new BigDecimal(100), RoundingMode.HALF_UP);
            txtValorCofinsSt.setText(Decimal.toString(valorCofinsSt));
            
        } else {
            BigDecimal quantidadeVendidaCofinsSt = Decimal.fromString(txtQuantidadeVendidaCofinsSt.getText());
            BigDecimal aliquotaCofinsStReais = Decimal.fromString(txtAliquotaCofinsStReais.getText());
            BigDecimal valorCofinsSt = quantidadeVendidaCofinsSt.multiply(aliquotaCofinsStReais);
            txtValorCofinsSt.setText(Decimal.toString(valorCofinsSt));
            
        }
    }
    
    private void calcularTotais() {
        BigDecimal quantidadeComercial = Decimal.fromString(txtQuantidadeComercial.getText());
        BigDecimal valorUnitarioComercial = Decimal.fromString(txtValorUnitarioComercial.getText());
        BigDecimal quantidadeTributavel = Decimal.fromString(txtQuantidadeTributavel.getText());
        BigDecimal valorUnitarioTributavel = Decimal.fromString(txtValorUnitarioTributavel.getText());
        
        BigDecimal acrescimo = Decimal.fromString(txtAcrescimo.getText());
        BigDecimal desconto = Decimal.fromString(txtDesconto.getText());
        BigDecimal frete = Decimal.fromString(txtFrete.getText());
        BigDecimal seguro = Decimal.fromString(txtSeguro.getText());
        
        BigDecimal totalBruto = quantidadeComercial.multiply(valorUnitarioComercial).add(acrescimo).subtract(desconto).add(frete).add(seguro);
        
        BigDecimal parcialComercial = quantidadeComercial.multiply(valorUnitarioComercial);
        BigDecimal parcialTributavel = quantidadeTributavel.multiply(valorUnitarioTributavel);
        
        if(parcialComercial.compareTo(parcialTributavel) != 0) {
            txtParcialTributavel.setForeground(Cor.VERMELHO);
        } else {
            txtParcialTributavel.setForeground(Color.BLACK);
        }
        
        txtParcialComercial.setText(Decimal.toString( parcialComercial ));
        txtParcialTributavel.setText(Decimal.toString( parcialTributavel ));
        
        txtValorTotalBruto.setText(Decimal.toString(totalBruto));
    }
    
    private void espelharParaTributavel() {
        if(cboUnidadeTributavel.getSelectedItem() == null) {
            cboUnidadeTributavel.setSelectedItem(cboUnidadeComercial.getSelectedItem());
        }
        if(cboUnidadeTributavel.getSelectedItem().equals(cboUnidadeComercial.getSelectedItem())) {
            txtQuantidadeTributavel.setText(txtQuantidadeComercial.getText());
            txtValorUnitarioTributavel.setText(txtValorUnitarioComercial.getText());
        }
    }
    

    private void confirmar() {
        salvar();
        dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnCancelar = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnlPrincipal = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        cboUnidadeComercial = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        txtQuantidadeComercial = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtValorUnitarioComercial = new javax.swing.JFormattedTextField();
        jLabel21 = new javax.swing.JLabel();
        txtEan = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        txtParcialComercial = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        cboUnidadeTributavel = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        txtQuantidadeTributavel = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        txtValorUnitarioTributavel = new javax.swing.JFormattedTextField();
        jLabel24 = new javax.swing.JLabel();
        txtEanTributavel = new javax.swing.JTextField();
        txtParcialTributavel = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        txtAcrescimo = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        txtDesconto = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        txtFrete = new javax.swing.JFormattedTextField();
        jLabel14 = new javax.swing.JLabel();
        txtSeguro = new javax.swing.JFormattedTextField();
        jLabel26 = new javax.swing.JLabel();
        txtValorTotalBruto = new javax.swing.JTextField();
        chkValorCompoeTotal = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        txtPedidoCompra = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        txtItemPedidoCompra = new javax.swing.JFormattedTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        btnPesquisarNcmNfe = new javax.swing.JButton();
        txtNcm = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        btnPesquisarCestNfe = new javax.swing.JButton();
        txtCest = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        txtDescricao = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        txtExTipi = new javax.swing.JTextField();
        cboCfop = new javax.swing.JComboBox<>();
        pnlTributos = new javax.swing.JPanel();
        pnlTotalTributos = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        txtTotalTributos = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jTabTributos = new javax.swing.JTabbedPane();
        pnlTributosIcms = new javax.swing.JPanel();
        cboIcms = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        cboOrigem = new javax.swing.JComboBox<>();
        pnlIcms = new javax.swing.JPanel();
        jLabel49 = new javax.swing.JLabel();
        cboModalidadeBcIcms = new javax.swing.JComboBox<>();
        jLabel50 = new javax.swing.JLabel();
        txtPercentualReducaoBcIcms = new javax.swing.JFormattedTextField();
        jLabel51 = new javax.swing.JLabel();
        txtAliquotaIcms = new javax.swing.JFormattedTextField();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        cboMotivoDesoneracao = new javax.swing.JComboBox<>();
        jLabel54 = new javax.swing.JLabel();
        txtPercentualBcOperacaoPropria = new javax.swing.JFormattedTextField();
        jLabel60 = new javax.swing.JLabel();
        txtValorBcIcms = new javax.swing.JFormattedTextField();
        jLabel61 = new javax.swing.JLabel();
        txtValorIcms = new javax.swing.JFormattedTextField();
        jLabel62 = new javax.swing.JLabel();
        txtValorIcmsDesonerado = new javax.swing.JFormattedTextField();
        pnlIcmsSt = new javax.swing.JPanel();
        jLabel55 = new javax.swing.JLabel();
        cboModalidadeBcIcmsSt = new javax.swing.JComboBox<>();
        jLabel56 = new javax.swing.JLabel();
        txtPercentualMargemValorAdicionadoIcmsSt = new javax.swing.JFormattedTextField();
        jLabel57 = new javax.swing.JLabel();
        txtAliquotaIcmsSt = new javax.swing.JFormattedTextField();
        jLabel58 = new javax.swing.JLabel();
        txtPercentualReducaoBcIcmsSt = new javax.swing.JFormattedTextField();
        jLabel59 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        txtBcIcmsSt = new javax.swing.JFormattedTextField();
        jLabel64 = new javax.swing.JLabel();
        txtValorIcmsSt = new javax.swing.JFormattedTextField();
        jLabel97 = new javax.swing.JLabel();
        txtValorBcIcmsStRetido = new javax.swing.JFormattedTextField();
        jLabel98 = new javax.swing.JLabel();
        txtValorIcmsStRetido = new javax.swing.JFormattedTextField();
        jLabel105 = new javax.swing.JLabel();
        txtValorIcmsProprioSubstituto = new javax.swing.JFormattedTextField();
        jLabel80 = new javax.swing.JLabel();
        txtAliquotaAplicavelCalculoCreditoIcms = new javax.swing.JFormattedTextField();
        txtValorCreditoIcms = new javax.swing.JFormattedTextField();
        jLabel81 = new javax.swing.JLabel();
        pnlIcmsEfetivo = new javax.swing.JPanel();
        jLabel99 = new javax.swing.JLabel();
        txtPercentualReducaoBcIcms1 = new javax.swing.JFormattedTextField();
        jLabel100 = new javax.swing.JLabel();
        jLabel101 = new javax.swing.JLabel();
        txtValorBcIcms1 = new javax.swing.JFormattedTextField();
        jLabel102 = new javax.swing.JLabel();
        txtAliquotaIcms1 = new javax.swing.JFormattedTextField();
        jLabel103 = new javax.swing.JLabel();
        txtValorIcms1 = new javax.swing.JFormattedTextField();
        txtAliquotaSuportadaConsumidorFinal = new javax.swing.JFormattedTextField();
        jLabel104 = new javax.swing.JLabel();
        pnlTributosPis = new javax.swing.JPanel();
        pnlIcms3 = new javax.swing.JPanel();
        jLabel66 = new javax.swing.JLabel();
        txtValorBcPis = new javax.swing.JFormattedTextField();
        jLabel68 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        txtAliquotaPis = new javax.swing.JFormattedTextField();
        jLabel74 = new javax.swing.JLabel();
        cboPisTipoCalculo = new javax.swing.JComboBox<>();
        txtQuantidadeVendidaPis = new javax.swing.JFormattedTextField();
        jLabel72 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        txtAliquotaPisReais = new javax.swing.JFormattedTextField();
        txtValorPis = new javax.swing.JFormattedTextField();
        jLabel70 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        cboPis = new javax.swing.JComboBox<>();
        pnlIcms4 = new javax.swing.JPanel();
        jLabel69 = new javax.swing.JLabel();
        txtValorBcPisSt = new javax.swing.JFormattedTextField();
        jLabel75 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        txtAliquotaPisSt = new javax.swing.JFormattedTextField();
        jLabel78 = new javax.swing.JLabel();
        txtQuantidadeVendidaPisSt = new javax.swing.JFormattedTextField();
        jLabel79 = new javax.swing.JLabel();
        cboPisStTipoCalculo = new javax.swing.JComboBox<>();
        jLabel73 = new javax.swing.JLabel();
        txtAliquotaPisStReais = new javax.swing.JFormattedTextField();
        jLabel76 = new javax.swing.JLabel();
        txtValorPisSt = new javax.swing.JFormattedTextField();
        pnlTributosCofins = new javax.swing.JPanel();
        pnlIcms5 = new javax.swing.JPanel();
        jLabel82 = new javax.swing.JLabel();
        txtValorBcCofins = new javax.swing.JFormattedTextField();
        jLabel83 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        txtAliquotaCofins = new javax.swing.JFormattedTextField();
        jLabel85 = new javax.swing.JLabel();
        cboCofinsTipoCalculo = new javax.swing.JComboBox<>();
        txtQuantidadeVendidaCofins = new javax.swing.JFormattedTextField();
        jLabel86 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        txtAliquotaCofinsReais = new javax.swing.JFormattedTextField();
        txtValorCofins = new javax.swing.JFormattedTextField();
        jLabel88 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        cboCofins = new javax.swing.JComboBox<>();
        pnlIcms6 = new javax.swing.JPanel();
        jLabel90 = new javax.swing.JLabel();
        txtValorBcCofinsSt = new javax.swing.JFormattedTextField();
        jLabel91 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        txtAliquotaCofinsSt = new javax.swing.JFormattedTextField();
        jLabel93 = new javax.swing.JLabel();
        txtQuantidadeVendidaCofinsSt = new javax.swing.JFormattedTextField();
        jLabel94 = new javax.swing.JLabel();
        cboCofinsStTipoCalculo = new javax.swing.JComboBox<>();
        jLabel95 = new javax.swing.JLabel();
        txtAliquotaCofinsStReais = new javax.swing.JFormattedTextField();
        jLabel96 = new javax.swing.JLabel();
        txtValorCofinsSt = new javax.swing.JFormattedTextField();
        btnAjuda = new javax.swing.JButton();
        lblInfo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Dados do Item");
        setResizable(false);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        btnCancelar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnOk.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnOk.setText("Ok");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Unidade");

        cboUnidadeComercial.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setText("Quantidade");

        txtQuantidadeComercial.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQuantidadeComercial.setText("0,00");
        txtQuantidadeComercial.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtQuantidadeComercial.setName("decimal"); // NOI18N
        txtQuantidadeComercial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtQuantidadeComercialKeyReleased(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Valor Unitário");

        txtValorUnitarioComercial.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorUnitarioComercial.setText("0,00");
        txtValorUnitarioComercial.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtValorUnitarioComercial.setName("decimal"); // NOI18N
        txtValorUnitarioComercial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorUnitarioComercialKeyReleased(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel21.setText("EAN");

        txtEan.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel37.setBackground(javax.swing.UIManager.getDefaults().getColor("InternalFrame.activeTitleBackground"));
        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel37.setForeground(java.awt.Color.white);
        jLabel37.setText("Comercial");
        jLabel37.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel37.setOpaque(true);

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel31.setText("Parcial");

        txtParcialComercial.setEditable(false);
        txtParcialComercial.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtParcialComercial.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel37)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(cboUnidadeComercial, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(txtQuantidadeComercial, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(txtValorUnitarioComercial, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel21)
                .addGap(18, 18, 18)
                .addComponent(txtEan, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel31)
                .addGap(18, 18, 18)
                .addComponent(txtParcialComercial, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel31)
                        .addComponent(txtParcialComercial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(cboUnidadeComercial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(txtValorUnitarioComercial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)
                        .addComponent(txtQuantidadeComercial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21)
                        .addComponent(txtEan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel38.setBackground(javax.swing.UIManager.getDefaults().getColor("InternalFrame.activeTitleBackground"));
        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel38.setForeground(java.awt.Color.white);
        jLabel38.setText("Tributável");
        jLabel38.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel38.setOpaque(true);

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel13.setText("Unidade");

        cboUnidadeTributavel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setText("Quantidade");

        txtQuantidadeTributavel.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQuantidadeTributavel.setText("0,00");
        txtQuantidadeTributavel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtQuantidadeTributavel.setName("decimal"); // NOI18N
        txtQuantidadeTributavel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtQuantidadeTributavelKeyReleased(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setText("Valor Unitário");

        txtValorUnitarioTributavel.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorUnitarioTributavel.setText("0,00");
        txtValorUnitarioTributavel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtValorUnitarioTributavel.setName("decimal"); // NOI18N
        txtValorUnitarioTributavel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorUnitarioTributavelKeyReleased(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel24.setText("EAN");

        txtEanTributavel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtParcialTributavel.setEditable(false);
        txtParcialTributavel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtParcialTributavel.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtParcialTributavel.setToolTipText("Parcial Tribut-avel deve ser igual ao Parcial Comercial");

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel32.setText("Parcial");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel38)
                .addGap(18, 18, 18)
                .addComponent(jLabel13)
                .addGap(18, 18, 18)
                .addComponent(cboUnidadeTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addComponent(txtQuantidadeTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(txtValorUnitarioTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel24)
                .addGap(18, 18, 18)
                .addComponent(txtEanTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel32)
                .addGap(18, 18, 18)
                .addComponent(txtParcialTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel32)
                        .addComponent(txtParcialTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel13)
                        .addComponent(cboUnidadeTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10)
                        .addComponent(txtValorUnitarioTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12)
                        .addComponent(txtQuantidadeTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel24)
                        .addComponent(txtEanTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel15.setText("Outras Despesas Acessórias (Acréscimo)");

        txtAcrescimo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAcrescimo.setText("0,00");
        txtAcrescimo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAcrescimo.setName("decimal"); // NOI18N
        txtAcrescimo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAcrescimoActionPerformed(evt);
            }
        });
        txtAcrescimo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAcrescimoKeyReleased(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Desconto");

        txtDesconto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDesconto.setText("0,00");
        txtDesconto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDesconto.setName("decimal"); // NOI18N
        txtDesconto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDescontoKeyReleased(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setText("Frete");

        txtFrete.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFrete.setText("0,00");
        txtFrete.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtFrete.setName("decimal"); // NOI18N
        txtFrete.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFreteKeyReleased(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel14.setText("Seguro");

        txtSeguro.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSeguro.setText("0,00");
        txtSeguro.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtSeguro.setName("decimal"); // NOI18N
        txtSeguro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSeguroKeyReleased(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel26.setText("Valor Total Bruto");

        txtValorTotalBruto.setEditable(false);
        txtValorTotalBruto.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtValorTotalBruto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        chkValorCompoeTotal.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        chkValorCompoeTotal.setText("Valor Total Bruto compõe o Valor Total dos Produtos e Serviços");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(chkValorCompoeTotal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel26)
                        .addGap(18, 18, 18)
                        .addComponent(txtValorTotalBruto, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(18, 18, 18)
                        .addComponent(txtAcrescimo, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(txtDesconto, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11)
                        .addGap(18, 18, 18)
                        .addComponent(txtFrete, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel14)
                        .addGap(18, 18, 18)
                        .addComponent(txtSeguro, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtAcrescimo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtDesconto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(txtFrete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSeguro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel26)
                        .addComponent(txtValorTotalBruto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkValorCompoeTotal))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel27.setText("Pedido de Compra");

        txtPedidoCompra.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel28.setText("Nº Item do Pedido de Compra");

        txtItemPedidoCompra.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtItemPedidoCompra.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtItemPedidoCompra.setName("inteiro"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel27)
                .addGap(18, 18, 18)
                .addComponent(txtPedidoCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel28)
                .addGap(18, 18, 18)
                .addComponent(txtItemPedidoCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(txtPedidoCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28)
                    .addComponent(txtItemPedidoCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("Código");

        txtCodigo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCodigo.setToolTipText("Preencha livremente ou o sistema preencherá com o Id");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Descrição");

        jLabel43.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel43.setText("NCM");

        btnPesquisarNcmNfe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-search-button-20.png"))); // NOI18N
        btnPesquisarNcmNfe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarNcmNfeActionPerformed(evt);
            }
        });

        txtNcm.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel44.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel44.setText("CEST");

        btnPesquisarCestNfe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-search-button-20.png"))); // NOI18N
        btnPesquisarCestNfe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarCestNfeActionPerformed(evt);
            }
        });

        txtCest.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel41.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel41.setText("CFOP");

        txtDescricao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDescricao.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel25.setText("EX TIPI");

        txtExTipi.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        cboCfop.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtDescricao)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel25)
                        .addGap(18, 18, 18)
                        .addComponent(txtExTipi, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel43)
                        .addGap(18, 18, 18)
                        .addComponent(btnPesquisarNcmNfe)
                        .addGap(18, 18, 18)
                        .addComponent(txtNcm, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel44)
                        .addGap(18, 18, 18)
                        .addComponent(btnPesquisarCestNfe)
                        .addGap(18, 18, 18)
                        .addComponent(txtCest, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel41)
                        .addGap(18, 18, 18)
                        .addComponent(cboCfop, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel25)
                        .addComponent(txtExTipi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel43)
                    .addComponent(btnPesquisarNcmNfe, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNcm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44)
                    .addComponent(txtCest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41)
                    .addComponent(btnPesquisarCestNfe, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboCfop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlPrincipalLayout = new javax.swing.GroupLayout(pnlPrincipal);
        pnlPrincipal.setLayout(pnlPrincipalLayout);
        pnlPrincipalLayout.setHorizontalGroup(
            pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlPrincipalLayout.setVerticalGroup(
            pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(150, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Principal", pnlPrincipal);

        pnlTotalTributos.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel29.setText("Total dos Tributos");

        txtTotalTributos.setEditable(false);
        txtTotalTributos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTotalTributos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel30.setText("Valor aproximado total de tributos federais, estaduais e municipais conforme disposto na Lei nº 12.741/12");

        javax.swing.GroupLayout pnlTotalTributosLayout = new javax.swing.GroupLayout(pnlTotalTributos);
        pnlTotalTributos.setLayout(pnlTotalTributosLayout);
        pnlTotalTributosLayout.setHorizontalGroup(
            pnlTotalTributosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTotalTributosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel29)
                .addGap(18, 18, 18)
                .addComponent(txtTotalTributos, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel30)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlTotalTributosLayout.setVerticalGroup(
            pnlTotalTributosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTotalTributosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTotalTributosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(txtTotalTributos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addContainerGap())
        );

        jTabTributos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        cboIcms.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboIcms.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cboIcmsFocusLost(evt);
            }
        });
        cboIcms.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboIcmsActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel16.setText("Situação Tributária");

        jLabel45.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel45.setText("Origem");

        cboOrigem.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboOrigem.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cboOrigemFocusLost(evt);
            }
        });

        pnlIcms.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlIcms.setPreferredSize(new java.awt.Dimension(610, 234));

        jLabel49.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel49.setText("Modalidade de determinação da BC ICMS");

        cboModalidadeBcIcms.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel50.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel50.setText("% redução da BC ICMS");

        txtPercentualReducaoBcIcms.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPercentualReducaoBcIcms.setText("pRedBC");
        txtPercentualReducaoBcIcms.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPercentualReducaoBcIcms.setName("decimal"); // NOI18N

        jLabel51.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel51.setText("Alíquota do ICMS");

        txtAliquotaIcms.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAliquotaIcms.setText("pICMS");
        txtAliquotaIcms.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAliquotaIcms.setName("decimal"); // NOI18N
        txtAliquotaIcms.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAliquotaIcmsFocusLost(evt);
            }
        });

        jLabel52.setBackground(new java.awt.Color(122, 138, 153));
        jLabel52.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel52.setForeground(java.awt.Color.white);
        jLabel52.setText("ICMS");
        jLabel52.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel52.setOpaque(true);

        jLabel53.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel53.setText("Motivo da Desoneração");

        cboMotivoDesoneracao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel54.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel54.setText("% BC da operação própria");

        txtPercentualBcOperacaoPropria.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPercentualBcOperacaoPropria.setText("pBCOp");
        txtPercentualBcOperacaoPropria.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPercentualBcOperacaoPropria.setName("decimal"); // NOI18N

        jLabel60.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel60.setText("BC ICMS");

        txtValorBcIcms.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorBcIcms.setText("vBC");
        txtValorBcIcms.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorBcIcms.setName("decimal"); // NOI18N
        txtValorBcIcms.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtValorBcIcmsFocusLost(evt);
            }
        });

        jLabel61.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel61.setText("ICMS");

        txtValorIcms.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorIcms.setText("vICMS");
        txtValorIcms.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorIcms.setName("decimal"); // NOI18N
        txtValorIcms.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtValorIcmsFocusLost(evt);
            }
        });

        jLabel62.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel62.setText("ICMS Desonerado");

        txtValorIcmsDesonerado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorIcmsDesonerado.setText("vICMS");
        txtValorIcmsDesonerado.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorIcmsDesonerado.setName("decimal"); // NOI18N
        txtValorIcmsDesonerado.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtValorIcmsDesoneradoFocusLost(evt);
            }
        });

        javax.swing.GroupLayout pnlIcmsLayout = new javax.swing.GroupLayout(pnlIcms);
        pnlIcms.setLayout(pnlIcmsLayout);
        pnlIcmsLayout.setHorizontalGroup(
            pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel52, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlIcmsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcmsLayout.createSequentialGroup()
                        .addComponent(jLabel49)
                        .addGap(18, 18, 18)
                        .addComponent(cboModalidadeBcIcms, 0, 285, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlIcmsLayout.createSequentialGroup()
                        .addComponent(jLabel53)
                        .addGap(18, 18, 18)
                        .addComponent(cboMotivoDesoneracao, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlIcmsLayout.createSequentialGroup()
                        .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlIcmsLayout.createSequentialGroup()
                                .addComponent(jLabel51)
                                .addGap(18, 18, 18)
                                .addComponent(txtAliquotaIcms, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel61)
                                .addGap(18, 18, 18)
                                .addComponent(txtValorIcms, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcmsLayout.createSequentialGroup()
                                .addComponent(jLabel50)
                                .addGap(18, 18, 18)
                                .addComponent(txtPercentualReducaoBcIcms, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel60)
                                .addGap(18, 18, 18)
                                .addComponent(txtValorBcIcms, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlIcmsLayout.createSequentialGroup()
                        .addComponent(jLabel54)
                        .addGap(18, 18, 18)
                        .addComponent(txtPercentualBcOperacaoPropria, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel62)
                        .addGap(18, 18, 18)
                        .addComponent(txtValorIcmsDesonerado)))
                .addContainerGap())
        );
        pnlIcmsLayout.setVerticalGroup(
            pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIcmsLayout.createSequentialGroup()
                .addComponent(jLabel52)
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboModalidadeBcIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtValorBcIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel60))
                    .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPercentualReducaoBcIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel50)))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtValorIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel61))
                    .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAliquotaIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel51)))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtValorIcmsDesonerado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel62))
                    .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPercentualBcOperacaoPropria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel54)))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboMotivoDesoneracao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel53))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlIcmsSt.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlIcmsSt.setPreferredSize(new java.awt.Dimension(610, 232));

        jLabel55.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel55.setText("Modalidade de determinação da BC ICMS ST");

        cboModalidadeBcIcmsSt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel56.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel56.setText("% redução da BC ICMS ST");

        txtPercentualMargemValorAdicionadoIcmsSt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPercentualMargemValorAdicionadoIcmsSt.setText("pMVAST");
        txtPercentualMargemValorAdicionadoIcmsSt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPercentualMargemValorAdicionadoIcmsSt.setName("decimal"); // NOI18N

        jLabel57.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel57.setText("Alíquota do ICMS ST");

        txtAliquotaIcmsSt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAliquotaIcmsSt.setText("pICMSST");
        txtAliquotaIcmsSt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAliquotaIcmsSt.setName("decimal"); // NOI18N

        jLabel58.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel58.setText("% margem de valor adic. ICMS ST");

        txtPercentualReducaoBcIcmsSt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPercentualReducaoBcIcmsSt.setText("pRedBCST");
        txtPercentualReducaoBcIcmsSt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPercentualReducaoBcIcmsSt.setName("decimal"); // NOI18N

        jLabel59.setBackground(new java.awt.Color(122, 138, 153));
        jLabel59.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel59.setForeground(java.awt.Color.white);
        jLabel59.setText("ICMS ST");
        jLabel59.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel59.setOpaque(true);

        jLabel63.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel63.setText("BC ICMS ST");

        txtBcIcmsSt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBcIcmsSt.setText("vBCST");
        txtBcIcmsSt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtBcIcmsSt.setName("decimal"); // NOI18N
        txtBcIcmsSt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBcIcmsStFocusLost(evt);
            }
        });
        txtBcIcmsSt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBcIcmsStActionPerformed(evt);
            }
        });

        jLabel64.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel64.setText("ICMS ST");

        txtValorIcmsSt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorIcmsSt.setText("vICMSST");
        txtValorIcmsSt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorIcmsSt.setName("decimal"); // NOI18N
        txtValorIcmsSt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtValorIcmsStFocusLost(evt);
            }
        });

        jLabel97.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel97.setText("BC ICMS ST retido anteriormente");

        txtValorBcIcmsStRetido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorBcIcmsStRetido.setText("vBCSTRet");
        txtValorBcIcmsStRetido.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorBcIcmsStRetido.setName("decimal"); // NOI18N

        jLabel98.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel98.setText("ICMS ST retido anteriormente");

        txtValorIcmsStRetido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorIcmsStRetido.setText("vICMSSTRet");
        txtValorIcmsStRetido.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorIcmsStRetido.setName("decimal"); // NOI18N

        jLabel105.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel105.setText("ICMS prórprio do Substituto");

        txtValorIcmsProprioSubstituto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorIcmsProprioSubstituto.setText("vICMSSubstituto");
        txtValorIcmsProprioSubstituto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorIcmsProprioSubstituto.setName("decimal"); // NOI18N

        javax.swing.GroupLayout pnlIcmsStLayout = new javax.swing.GroupLayout(pnlIcmsSt);
        pnlIcmsSt.setLayout(pnlIcmsStLayout);
        pnlIcmsStLayout.setHorizontalGroup(
            pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel59, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlIcmsStLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcmsStLayout.createSequentialGroup()
                        .addComponent(jLabel55)
                        .addGap(18, 18, 18)
                        .addComponent(cboModalidadeBcIcmsSt, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlIcmsStLayout.createSequentialGroup()
                        .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlIcmsStLayout.createSequentialGroup()
                                .addComponent(jLabel57)
                                .addGap(18, 18, 18)
                                .addComponent(txtAliquotaIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel64)
                                .addGap(18, 18, 18)
                                .addComponent(txtValorIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcmsStLayout.createSequentialGroup()
                                .addComponent(jLabel56)
                                .addGap(18, 18, 18)
                                .addComponent(txtPercentualReducaoBcIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcmsStLayout.createSequentialGroup()
                                .addComponent(jLabel58)
                                .addGap(18, 18, 18)
                                .addComponent(txtPercentualMargemValorAdicionadoIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel63)
                                .addGap(18, 18, 18)
                                .addComponent(txtBcIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcmsStLayout.createSequentialGroup()
                                .addComponent(jLabel97)
                                .addGap(18, 18, 18)
                                .addComponent(txtValorBcIcmsStRetido, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcmsStLayout.createSequentialGroup()
                                .addComponent(jLabel98)
                                .addGap(18, 18, 18)
                                .addComponent(txtValorIcmsStRetido, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcmsStLayout.createSequentialGroup()
                                .addComponent(jLabel105)
                                .addGap(18, 18, 18)
                                .addComponent(txtValorIcmsProprioSubstituto, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 32, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlIcmsStLayout.setVerticalGroup(
            pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIcmsStLayout.createSequentialGroup()
                .addComponent(jLabel59)
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboModalidadeBcIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel55))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56)
                    .addComponent(txtPercentualReducaoBcIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtBcIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel63))
                    .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel58)
                        .addComponent(txtPercentualMargemValorAdicionadoIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtValorIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel64))
                    .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAliquotaIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel57)))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtValorBcIcmsStRetido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel97))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtValorIcmsStRetido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel98))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtValorIcmsProprioSubstituto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel105))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel80.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel80.setText("Alíquota aplicável de cálculo do crédito");

        txtAliquotaAplicavelCalculoCreditoIcms.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAliquotaAplicavelCalculoCreditoIcms.setText("pCredSN");
        txtAliquotaAplicavelCalculoCreditoIcms.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAliquotaAplicavelCalculoCreditoIcms.setName("decimal"); // NOI18N

        txtValorCreditoIcms.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorCreditoIcms.setText("vCredICMSSN");
        txtValorCreditoIcms.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorCreditoIcms.setName("decimal"); // NOI18N

        jLabel81.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel81.setText("Crédito ICMS que pode ser reaproveitado");

        pnlIcmsEfetivo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel99.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel99.setText("% redução da BC efetiva");

        txtPercentualReducaoBcIcms1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPercentualReducaoBcIcms1.setText("pRedBC");
        txtPercentualReducaoBcIcms1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPercentualReducaoBcIcms1.setName("decimal"); // NOI18N

        jLabel100.setBackground(new java.awt.Color(122, 138, 153));
        jLabel100.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel100.setForeground(java.awt.Color.white);
        jLabel100.setText("ICMS Efetivo");
        jLabel100.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel100.setOpaque(true);

        jLabel101.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel101.setText("BC ICMS efetiva");

        txtValorBcIcms1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorBcIcms1.setText("vBC");
        txtValorBcIcms1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorBcIcms1.setName("decimal"); // NOI18N
        txtValorBcIcms1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtValorBcIcms1FocusLost(evt);
            }
        });

        jLabel102.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel102.setText("Alíquota ICMS efetiva");

        txtAliquotaIcms1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAliquotaIcms1.setText("pICMS");
        txtAliquotaIcms1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAliquotaIcms1.setName("decimal"); // NOI18N
        txtAliquotaIcms1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAliquotaIcms1FocusLost(evt);
            }
        });

        jLabel103.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel103.setText("ICMS efetivo");

        txtValorIcms1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorIcms1.setText("vICMS");
        txtValorIcms1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorIcms1.setName("decimal"); // NOI18N
        txtValorIcms1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtValorIcms1FocusLost(evt);
            }
        });

        javax.swing.GroupLayout pnlIcmsEfetivoLayout = new javax.swing.GroupLayout(pnlIcmsEfetivo);
        pnlIcmsEfetivo.setLayout(pnlIcmsEfetivoLayout);
        pnlIcmsEfetivoLayout.setHorizontalGroup(
            pnlIcmsEfetivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel100, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlIcmsEfetivoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlIcmsEfetivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcmsEfetivoLayout.createSequentialGroup()
                        .addComponent(jLabel99)
                        .addGap(18, 18, 18)
                        .addComponent(txtPercentualReducaoBcIcms1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel101)
                        .addGap(18, 18, 18)
                        .addComponent(txtValorBcIcms1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlIcmsEfetivoLayout.createSequentialGroup()
                        .addComponent(jLabel102)
                        .addGap(18, 18, 18)
                        .addComponent(txtAliquotaIcms1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel103)
                        .addGap(18, 18, 18)
                        .addComponent(txtValorIcms1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlIcmsEfetivoLayout.setVerticalGroup(
            pnlIcmsEfetivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlIcmsEfetivoLayout.createSequentialGroup()
                .addComponent(jLabel100)
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsEfetivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPercentualReducaoBcIcms1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel99)
                    .addComponent(txtValorBcIcms1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel101))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsEfetivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcmsEfetivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtValorIcms1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel103))
                    .addGroup(pnlIcmsEfetivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAliquotaIcms1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel102)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtAliquotaSuportadaConsumidorFinal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAliquotaSuportadaConsumidorFinal.setText("pST");
        txtAliquotaSuportadaConsumidorFinal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAliquotaSuportadaConsumidorFinal.setName("decimal"); // NOI18N
        txtAliquotaSuportadaConsumidorFinal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAliquotaSuportadaConsumidorFinalFocusLost(evt);
            }
        });

        jLabel104.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel104.setText("Alíquota suportada pelo consumidor final");

        javax.swing.GroupLayout pnlTributosIcmsLayout = new javax.swing.GroupLayout(pnlTributosIcms);
        pnlTributosIcms.setLayout(pnlTributosIcmsLayout);
        pnlTributosIcmsLayout.setHorizontalGroup(
            pnlTributosIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTributosIcmsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTributosIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTributosIcmsLayout.createSequentialGroup()
                        .addGroup(pnlTributosIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlIcms, javax.swing.GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE)
                            .addComponent(pnlIcmsEfetivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(pnlIcmsSt, javax.swing.GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE))
                    .addGroup(pnlTributosIcmsLayout.createSequentialGroup()
                        .addComponent(jLabel80)
                        .addGap(18, 18, 18)
                        .addComponent(txtAliquotaAplicavelCalculoCreditoIcms, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel81)
                        .addGap(18, 18, 18)
                        .addComponent(txtValorCreditoIcms, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel104)
                        .addGap(18, 18, 18)
                        .addComponent(txtAliquotaSuportadaConsumidorFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlTributosIcmsLayout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(18, 18, 18)
                        .addComponent(cboIcms, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel45)
                        .addGap(18, 18, 18)
                        .addComponent(cboOrigem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlTributosIcmsLayout.setVerticalGroup(
            pnlTributosIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTributosIcmsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTributosIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(cboOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(18, 18, 18)
                .addGroup(pnlTributosIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTributosIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAliquotaSuportadaConsumidorFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel104))
                    .addGroup(pnlTributosIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAliquotaAplicavelCalculoCreditoIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel80)
                        .addComponent(txtValorCreditoIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel81)))
                .addGap(18, 18, 18)
                .addGroup(pnlTributosIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlTributosIcmsLayout.createSequentialGroup()
                        .addComponent(pnlIcms, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlIcmsEfetivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlIcmsSt, javax.swing.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabTributos.addTab("ICMS", pnlTributosIcms);

        pnlIcms3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlIcms3.setPreferredSize(new java.awt.Dimension(610, 234));

        jLabel66.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel66.setText("Valor da Base de Cálculo");

        txtValorBcPis.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorBcPis.setText("vBC");
        txtValorBcPis.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorBcPis.setName("decimal"); // NOI18N
        txtValorBcPis.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorBcPisKeyReleased(evt);
            }
        });

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

        txtQuantidadeVendidaPis.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQuantidadeVendidaPis.setText("qBcProd");
        txtQuantidadeVendidaPis.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtQuantidadeVendidaPis.setName("decimal"); // NOI18N
        txtQuantidadeVendidaPis.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtQuantidadeVendidaPisFocusLost(evt);
            }
        });
        txtQuantidadeVendidaPis.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtQuantidadeVendidaPisKeyReleased(evt);
            }
        });

        jLabel72.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel72.setText("Quantidade Vendida");

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

        txtValorPis.setEditable(false);
        txtValorPis.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorPis.setText("vPIS");
        txtValorPis.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorPis.setName("decimal"); // NOI18N

        jLabel70.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel70.setText("Valor do PIS");

        javax.swing.GroupLayout pnlIcms3Layout = new javax.swing.GroupLayout(pnlIcms3);
        pnlIcms3.setLayout(pnlIcms3Layout);
        pnlIcms3Layout.setHorizontalGroup(
            pnlIcms3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel68, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlIcms3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlIcms3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcms3Layout.createSequentialGroup()
                        .addGroup(pnlIcms3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlIcms3Layout.createSequentialGroup()
                                .addComponent(jLabel72)
                                .addGap(18, 18, 18)
                                .addComponent(txtQuantidadeVendidaPis, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcms3Layout.createSequentialGroup()
                                .addComponent(jLabel66)
                                .addGap(18, 18, 18)
                                .addComponent(txtValorBcPis, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(pnlIcms3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlIcms3Layout.createSequentialGroup()
                                .addComponent(jLabel71)
                                .addGap(18, 18, 18)
                                .addComponent(txtAliquotaPis, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(pnlIcms3Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel67)
                                .addGap(18, 18, 18)
                                .addComponent(txtAliquotaPisReais, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(pnlIcms3Layout.createSequentialGroup()
                        .addComponent(jLabel74)
                        .addGap(18, 18, 18)
                        .addComponent(cboPisTipoCalculo, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlIcms3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel70)
                        .addGap(18, 18, 18)
                        .addComponent(txtValorPis, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
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
                .addGroup(pnlIcms3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcms3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAliquotaPis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel71))
                    .addGroup(pnlIcms3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtValorBcPis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel66)))
                .addGap(18, 18, 18)
                .addGroup(pnlIcms3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtQuantidadeVendidaPis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel72)
                    .addComponent(txtAliquotaPisReais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel67))
                .addGap(18, 18, 18)
                .addGroup(pnlIcms3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtValorPis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel70))
                .addContainerGap())
        );

        jLabel65.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel65.setText("Situação Tributária");

        cboPis.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboPis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPisActionPerformed(evt);
            }
        });

        pnlIcms4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlIcms4.setPreferredSize(new java.awt.Dimension(610, 234));

        jLabel69.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel69.setText("Valor da Base de Cálculo");

        txtValorBcPisSt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorBcPisSt.setText("vBC");
        txtValorBcPisSt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorBcPisSt.setName("decimal"); // NOI18N
        txtValorBcPisSt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorBcPisStKeyReleased(evt);
            }
        });

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

        jLabel78.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel78.setText("Quantidade Vendida");

        txtQuantidadeVendidaPisSt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQuantidadeVendidaPisSt.setText("qBCProd");
        txtQuantidadeVendidaPisSt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtQuantidadeVendidaPisSt.setName("decimal"); // NOI18N
        txtQuantidadeVendidaPisSt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtQuantidadeVendidaPisStFocusLost(evt);
            }
        });
        txtQuantidadeVendidaPisSt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtQuantidadeVendidaPisStKeyReleased(evt);
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

        jLabel76.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel76.setText("Valor do PIS ST");

        txtValorPisSt.setEditable(false);
        txtValorPisSt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorPisSt.setText("vPIS");
        txtValorPisSt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorPisSt.setName("decimal"); // NOI18N

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
                        .addGroup(pnlIcms4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlIcms4Layout.createSequentialGroup()
                                .addComponent(jLabel78)
                                .addGap(18, 18, 18)
                                .addComponent(txtQuantidadeVendidaPisSt, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcms4Layout.createSequentialGroup()
                                .addComponent(jLabel69)
                                .addGap(18, 18, 18)
                                .addComponent(txtValorBcPisSt, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(pnlIcms4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlIcms4Layout.createSequentialGroup()
                                .addComponent(jLabel77)
                                .addGap(18, 18, 18)
                                .addComponent(txtAliquotaPisSt, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(pnlIcms4Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel73)
                                .addGap(18, 18, 18)
                                .addComponent(txtAliquotaPisStReais, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlIcms4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel76)
                        .addGap(18, 18, 18)
                        .addComponent(txtValorPisSt, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
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
                .addGroup(pnlIcms4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcms4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAliquotaPisSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel77))
                    .addGroup(pnlIcms4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtValorBcPisSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel69)))
                .addGap(18, 18, 18)
                .addGroup(pnlIcms4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtAliquotaPisStReais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel73)
                    .addGroup(pnlIcms4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtQuantidadeVendidaPisSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel78)))
                .addGap(18, 18, 18)
                .addGroup(pnlIcms4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtValorPisSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel76))
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlTributosPisLayout = new javax.swing.GroupLayout(pnlTributosPis);
        pnlTributosPis.setLayout(pnlTributosPisLayout);
        pnlTributosPisLayout.setHorizontalGroup(
            pnlTributosPisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTributosPisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTributosPisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTributosPisLayout.createSequentialGroup()
                        .addComponent(pnlIcms3, javax.swing.GroupLayout.PREFERRED_SIZE, 555, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                        .addComponent(pnlIcms4, javax.swing.GroupLayout.PREFERRED_SIZE, 555, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlTributosPisLayout.createSequentialGroup()
                        .addComponent(jLabel65)
                        .addGap(18, 18, 18)
                        .addComponent(cboPis, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlTributosPisLayout.setVerticalGroup(
            pnlTributosPisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTributosPisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTributosPisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboPis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel65))
                .addGap(18, 18, 18)
                .addGroup(pnlTributosPisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlIcms3, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                    .addComponent(pnlIcms4, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
                .addContainerGap(231, Short.MAX_VALUE))
        );

        jTabTributos.addTab("PIS", pnlTributosPis);

        pnlIcms5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlIcms5.setPreferredSize(new java.awt.Dimension(610, 234));

        jLabel82.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel82.setText("Valor da Base de Cálculo");

        txtValorBcCofins.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorBcCofins.setText("vBC");
        txtValorBcCofins.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorBcCofins.setName("decimal"); // NOI18N
        txtValorBcCofins.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorBcCofinsKeyReleased(evt);
            }
        });

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

        txtQuantidadeVendidaCofins.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQuantidadeVendidaCofins.setText("qBcProd");
        txtQuantidadeVendidaCofins.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtQuantidadeVendidaCofins.setName("decimal"); // NOI18N
        txtQuantidadeVendidaCofins.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtQuantidadeVendidaCofinsFocusLost(evt);
            }
        });
        txtQuantidadeVendidaCofins.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtQuantidadeVendidaCofinsKeyReleased(evt);
            }
        });

        jLabel86.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel86.setText("Quantidade Vendida");

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

        txtValorCofins.setEditable(false);
        txtValorCofins.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorCofins.setText("vCOFINS");
        txtValorCofins.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorCofins.setName("decimal"); // NOI18N

        jLabel88.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel88.setText("Valor do COFINS");

        javax.swing.GroupLayout pnlIcms5Layout = new javax.swing.GroupLayout(pnlIcms5);
        pnlIcms5.setLayout(pnlIcms5Layout);
        pnlIcms5Layout.setHorizontalGroup(
            pnlIcms5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel83, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlIcms5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlIcms5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcms5Layout.createSequentialGroup()
                        .addGroup(pnlIcms5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlIcms5Layout.createSequentialGroup()
                                .addComponent(jLabel86)
                                .addGap(18, 18, 18)
                                .addComponent(txtQuantidadeVendidaCofins, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcms5Layout.createSequentialGroup()
                                .addComponent(jLabel82)
                                .addGap(18, 18, 18)
                                .addComponent(txtValorBcCofins, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(pnlIcms5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlIcms5Layout.createSequentialGroup()
                                .addComponent(jLabel84)
                                .addGap(18, 18, 18)
                                .addComponent(txtAliquotaCofins, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(pnlIcms5Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel87)
                                .addGap(18, 18, 18)
                                .addComponent(txtAliquotaCofinsReais, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(pnlIcms5Layout.createSequentialGroup()
                        .addComponent(jLabel85)
                        .addGap(18, 18, 18)
                        .addComponent(cboCofinsTipoCalculo, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlIcms5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel88)
                        .addGap(18, 18, 18)
                        .addComponent(txtValorCofins, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
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
                .addGroup(pnlIcms5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcms5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAliquotaCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel84))
                    .addGroup(pnlIcms5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtValorBcCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel82)))
                .addGap(18, 18, 18)
                .addGroup(pnlIcms5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtQuantidadeVendidaCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel86)
                    .addComponent(txtAliquotaCofinsReais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel87))
                .addGap(18, 18, 18)
                .addGroup(pnlIcms5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtValorCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel88))
                .addContainerGap())
        );

        jLabel89.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel89.setText("Situação Tributária");

        cboCofins.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboCofins.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCofinsActionPerformed(evt);
            }
        });

        pnlIcms6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlIcms6.setPreferredSize(new java.awt.Dimension(610, 234));

        jLabel90.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel90.setText("Valor da Base de Cálculo");

        txtValorBcCofinsSt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorBcCofinsSt.setText("vBC");
        txtValorBcCofinsSt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorBcCofinsSt.setName("decimal"); // NOI18N
        txtValorBcCofinsSt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorBcCofinsStKeyReleased(evt);
            }
        });

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

        jLabel93.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel93.setText("Quantidade Vendida");

        txtQuantidadeVendidaCofinsSt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQuantidadeVendidaCofinsSt.setText("qBCProd");
        txtQuantidadeVendidaCofinsSt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtQuantidadeVendidaCofinsSt.setName("decimal"); // NOI18N
        txtQuantidadeVendidaCofinsSt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtQuantidadeVendidaCofinsStFocusLost(evt);
            }
        });
        txtQuantidadeVendidaCofinsSt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtQuantidadeVendidaCofinsStKeyReleased(evt);
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

        jLabel96.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel96.setText("Valor do COFINS ST");

        txtValorCofinsSt.setEditable(false);
        txtValorCofinsSt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorCofinsSt.setText("vCOFINS");
        txtValorCofinsSt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorCofinsSt.setName("decimal"); // NOI18N

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
                        .addGroup(pnlIcms6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlIcms6Layout.createSequentialGroup()
                                .addComponent(jLabel93)
                                .addGap(18, 18, 18)
                                .addComponent(txtQuantidadeVendidaCofinsSt, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcms6Layout.createSequentialGroup()
                                .addComponent(jLabel90)
                                .addGap(18, 18, 18)
                                .addComponent(txtValorBcCofinsSt, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(pnlIcms6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlIcms6Layout.createSequentialGroup()
                                .addComponent(jLabel92)
                                .addGap(18, 18, 18)
                                .addComponent(txtAliquotaCofinsSt, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(pnlIcms6Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel95)
                                .addGap(18, 18, 18)
                                .addComponent(txtAliquotaCofinsStReais, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlIcms6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel96)
                        .addGap(18, 18, 18)
                        .addComponent(txtValorCofinsSt, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
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
                .addGroup(pnlIcms6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcms6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAliquotaCofinsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel92))
                    .addGroup(pnlIcms6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtValorBcCofinsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel90)))
                .addGap(18, 18, 18)
                .addGroup(pnlIcms6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtAliquotaCofinsStReais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel95)
                    .addGroup(pnlIcms6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtQuantidadeVendidaCofinsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel93)))
                .addGap(18, 18, 18)
                .addGroup(pnlIcms6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtValorCofinsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel96))
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlTributosCofinsLayout = new javax.swing.GroupLayout(pnlTributosCofins);
        pnlTributosCofins.setLayout(pnlTributosCofinsLayout);
        pnlTributosCofinsLayout.setHorizontalGroup(
            pnlTributosCofinsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTributosCofinsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTributosCofinsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTributosCofinsLayout.createSequentialGroup()
                        .addComponent(pnlIcms5, javax.swing.GroupLayout.PREFERRED_SIZE, 555, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                        .addComponent(pnlIcms6, javax.swing.GroupLayout.PREFERRED_SIZE, 555, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlTributosCofinsLayout.createSequentialGroup()
                        .addComponent(jLabel89)
                        .addGap(18, 18, 18)
                        .addComponent(cboCofins, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlTributosCofinsLayout.setVerticalGroup(
            pnlTributosCofinsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTributosCofinsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTributosCofinsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel89))
                .addGap(18, 18, 18)
                .addGroup(pnlTributosCofinsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlIcms5, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                    .addComponent(pnlIcms6, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
                .addContainerGap(231, Short.MAX_VALUE))
        );

        jTabTributos.addTab("COFINS", pnlTributosCofins);

        javax.swing.GroupLayout pnlTributosLayout = new javax.swing.GroupLayout(pnlTributos);
        pnlTributos.setLayout(pnlTributosLayout);
        pnlTributosLayout.setHorizontalGroup(
            pnlTributosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTributosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTributosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlTotalTributos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabTributos))
                .addContainerGap())
        );
        pnlTributosLayout.setVerticalGroup(
            pnlTributosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTributosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlTotalTributos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabTributos, javax.swing.GroupLayout.PREFERRED_SIZE, 504, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Tributos", pnlTributos);

        btnAjuda.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-help-20.png"))); // NOI18N
        btnAjuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAjudaActionPerformed(evt);
            }
        });

        lblInfo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblInfo.setForeground(java.awt.Color.blue);
        lblInfo.setText(".");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnAjuda)
                        .addGap(18, 18, 18)
                        .addComponent(lblInfo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTabbedPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 615, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCancelar)
                        .addComponent(btnOk)
                        .addComponent(lblInfo))
                    .addComponent(btnAjuda))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        confirmar();
    }//GEN-LAST:event_btnOkActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnPesquisarNcmNfeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarNcmNfeActionPerformed
        pesquisarNcm();
    }//GEN-LAST:event_btnPesquisarNcmNfeActionPerformed

    private void btnPesquisarCestNfeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarCestNfeActionPerformed
        pesquisarCest(null);
    }//GEN-LAST:event_btnPesquisarCestNfeActionPerformed

    private void cboIcmsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboIcmsFocusLost
    }//GEN-LAST:event_cboIcmsFocusLost

    private void cboIcmsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboIcmsActionPerformed
        chavearIcms();
    }//GEN-LAST:event_cboIcmsActionPerformed

    private void cboOrigemFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboOrigemFocusLost
    }//GEN-LAST:event_cboOrigemFocusLost

    private void txtAliquotaIcmsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaIcmsFocusLost
    }//GEN-LAST:event_txtAliquotaIcmsFocusLost

    private void txtValorBcIcmsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtValorBcIcmsFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValorBcIcmsFocusLost

    private void txtValorIcmsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtValorIcmsFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValorIcmsFocusLost

    private void txtValorIcmsDesoneradoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtValorIcmsDesoneradoFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValorIcmsDesoneradoFocusLost

    private void txtBcIcmsStFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBcIcmsStFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBcIcmsStFocusLost

    private void txtBcIcmsStActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBcIcmsStActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBcIcmsStActionPerformed

    private void txtValorIcmsStFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtValorIcmsStFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValorIcmsStFocusLost

    private void txtAliquotaPisReaisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaPisReaisFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaPisReaisFocusLost

    private void txtAliquotaPisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaPisFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaPisFocusLost

    private void txtQuantidadeVendidaPisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtQuantidadeVendidaPisFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQuantidadeVendidaPisFocusLost

    private void txtAliquotaPisStReaisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaPisStReaisFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaPisStReaisFocusLost

    private void txtAliquotaPisStFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaPisStFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaPisStFocusLost

    private void txtQuantidadeVendidaPisStFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtQuantidadeVendidaPisStFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQuantidadeVendidaPisStFocusLost

    private void cboPisTipoCalculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPisTipoCalculoActionPerformed
        chavearPisTipoCalculo();
    }//GEN-LAST:event_cboPisTipoCalculoActionPerformed

    private void cboPisStTipoCalculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPisStTipoCalculoActionPerformed
        chavearPisStTipoCalculo();
    }//GEN-LAST:event_cboPisStTipoCalculoActionPerformed

    private void txtValorBcPisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorBcPisKeyReleased
        calcularPis();
    }//GEN-LAST:event_txtValorBcPisKeyReleased

    private void txtAliquotaPisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaPisKeyReleased
        calcularPis();
    }//GEN-LAST:event_txtAliquotaPisKeyReleased

    private void txtQuantidadeVendidaPisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeVendidaPisKeyReleased
        calcularPis();
    }//GEN-LAST:event_txtQuantidadeVendidaPisKeyReleased

    private void txtAliquotaPisReaisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaPisReaisKeyReleased
        calcularPis();
    }//GEN-LAST:event_txtAliquotaPisReaisKeyReleased

    private void txtValorBcPisStKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorBcPisStKeyReleased
        calcularPisSt();
    }//GEN-LAST:event_txtValorBcPisStKeyReleased

    private void txtAliquotaPisStKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaPisStKeyReleased
        calcularPisSt();
    }//GEN-LAST:event_txtAliquotaPisStKeyReleased

    private void txtQuantidadeVendidaPisStKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeVendidaPisStKeyReleased
        calcularPisSt();
    }//GEN-LAST:event_txtQuantidadeVendidaPisStKeyReleased

    private void txtAliquotaPisStReaisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaPisStReaisKeyReleased
        calcularPisSt();
    }//GEN-LAST:event_txtAliquotaPisStReaisKeyReleased

    private void cboPisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPisActionPerformed
        chavearPis();
    }//GEN-LAST:event_cboPisActionPerformed

    private void txtValorBcCofinsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorBcCofinsKeyReleased
        calcularCofins();
    }//GEN-LAST:event_txtValorBcCofinsKeyReleased

    private void txtAliquotaCofinsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaCofinsFocusLost

    private void txtAliquotaCofinsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsKeyReleased
        calcularCofins();
    }//GEN-LAST:event_txtAliquotaCofinsKeyReleased

    private void cboCofinsTipoCalculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCofinsTipoCalculoActionPerformed
        chavearCofinsTipoCalculo();
    }//GEN-LAST:event_cboCofinsTipoCalculoActionPerformed

    private void txtQuantidadeVendidaCofinsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtQuantidadeVendidaCofinsFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQuantidadeVendidaCofinsFocusLost

    private void txtQuantidadeVendidaCofinsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeVendidaCofinsKeyReleased
        calcularCofins();
    }//GEN-LAST:event_txtQuantidadeVendidaCofinsKeyReleased

    private void txtAliquotaCofinsReaisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsReaisFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaCofinsReaisFocusLost

    private void txtAliquotaCofinsReaisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsReaisKeyReleased
        calcularCofins();
    }//GEN-LAST:event_txtAliquotaCofinsReaisKeyReleased

    private void cboCofinsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCofinsActionPerformed
        chavearCofins();
    }//GEN-LAST:event_cboCofinsActionPerformed

    private void txtValorBcCofinsStKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorBcCofinsStKeyReleased
        calcularCofinsSt();
    }//GEN-LAST:event_txtValorBcCofinsStKeyReleased

    private void txtAliquotaCofinsStFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsStFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaCofinsStFocusLost

    private void txtAliquotaCofinsStKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsStKeyReleased
        calcularCofinsSt();
    }//GEN-LAST:event_txtAliquotaCofinsStKeyReleased

    private void txtQuantidadeVendidaCofinsStFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtQuantidadeVendidaCofinsStFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQuantidadeVendidaCofinsStFocusLost

    private void txtQuantidadeVendidaCofinsStKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeVendidaCofinsStKeyReleased
        calcularCofinsSt();
    }//GEN-LAST:event_txtQuantidadeVendidaCofinsStKeyReleased

    private void cboCofinsStTipoCalculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCofinsStTipoCalculoActionPerformed
        chavearCofinsStTipoCalculo();
    }//GEN-LAST:event_cboCofinsStTipoCalculoActionPerformed

    private void txtAliquotaCofinsStReaisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsStReaisFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaCofinsStReaisFocusLost

    private void txtAliquotaCofinsStReaisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsStReaisKeyReleased
        calcularCofinsSt();
    }//GEN-LAST:event_txtAliquotaCofinsStReaisKeyReleased

    private void txtValorBcIcms1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtValorBcIcms1FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValorBcIcms1FocusLost

    private void txtAliquotaIcms1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaIcms1FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaIcms1FocusLost

    private void txtValorIcms1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtValorIcms1FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValorIcms1FocusLost

    private void txtAliquotaSuportadaConsumidorFinalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaSuportadaConsumidorFinalFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaSuportadaConsumidorFinalFocusLost

    private void txtQuantidadeComercialKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeComercialKeyReleased
        espelharParaTributavel();
        calcularTotais();
    }//GEN-LAST:event_txtQuantidadeComercialKeyReleased

    private void txtValorUnitarioComercialKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorUnitarioComercialKeyReleased
        espelharParaTributavel();
        calcularTotais();
    }//GEN-LAST:event_txtValorUnitarioComercialKeyReleased

    private void txtAcrescimoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAcrescimoKeyReleased
        calcularTotais();
    }//GEN-LAST:event_txtAcrescimoKeyReleased

    private void txtDescontoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescontoKeyReleased
        calcularTotais();
    }//GEN-LAST:event_txtDescontoKeyReleased

    private void txtFreteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFreteKeyReleased
        calcularTotais();
    }//GEN-LAST:event_txtFreteKeyReleased

    private void txtSeguroKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSeguroKeyReleased
        calcularTotais();
    }//GEN-LAST:event_txtSeguroKeyReleased

    private void btnAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAjudaActionPerformed
        AjudaView ajuda = new AjudaView("venda_item.html");
    }//GEN-LAST:event_btnAjudaActionPerformed

    private void txtQuantidadeTributavelKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeTributavelKeyReleased
        calcularTotais();
    }//GEN-LAST:event_txtQuantidadeTributavelKeyReleased

    private void txtValorUnitarioTributavelKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorUnitarioTributavelKeyReleased
        calcularTotais();
    }//GEN-LAST:event_txtValorUnitarioTributavelKeyReleased

    private void txtAcrescimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAcrescimoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAcrescimoActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        txtCodigo.requestFocus();
    }//GEN-LAST:event_formComponentShown

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
            java.util.logging.Logger.getLogger(VendaItemView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VendaItemView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VendaItemView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VendaItemView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                VendaItemView dialog = new VendaItemView(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnAjuda;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnPesquisarCestNfe;
    private javax.swing.JButton btnPesquisarNcmNfe;
    private javax.swing.JComboBox<Object> cboCfop;
    private javax.swing.JComboBox<Object> cboCofins;
    private javax.swing.JComboBox<Object> cboCofinsStTipoCalculo;
    private javax.swing.JComboBox<Object> cboCofinsTipoCalculo;
    private javax.swing.JComboBox<Object> cboIcms;
    private javax.swing.JComboBox<Object> cboModalidadeBcIcms;
    private javax.swing.JComboBox<Object> cboModalidadeBcIcmsSt;
    private javax.swing.JComboBox<Object> cboMotivoDesoneracao;
    private javax.swing.JComboBox<Object> cboOrigem;
    private javax.swing.JComboBox<Object> cboPis;
    private javax.swing.JComboBox<Object> cboPisStTipoCalculo;
    private javax.swing.JComboBox<Object> cboPisTipoCalculo;
    private javax.swing.JComboBox<Object> cboUnidadeComercial;
    private javax.swing.JComboBox<Object> cboUnidadeTributavel;
    private javax.swing.JCheckBox chkValorCompoeTotal;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JTabbedPane jTabTributos;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JPanel pnlIcms;
    private javax.swing.JPanel pnlIcms3;
    private javax.swing.JPanel pnlIcms4;
    private javax.swing.JPanel pnlIcms5;
    private javax.swing.JPanel pnlIcms6;
    private javax.swing.JPanel pnlIcmsEfetivo;
    private javax.swing.JPanel pnlIcmsSt;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JPanel pnlTotalTributos;
    private javax.swing.JPanel pnlTributos;
    private javax.swing.JPanel pnlTributosCofins;
    private javax.swing.JPanel pnlTributosIcms;
    private javax.swing.JPanel pnlTributosPis;
    private javax.swing.JFormattedTextField txtAcrescimo;
    private javax.swing.JFormattedTextField txtAliquotaAplicavelCalculoCreditoIcms;
    private javax.swing.JFormattedTextField txtAliquotaCofins;
    private javax.swing.JFormattedTextField txtAliquotaCofinsReais;
    private javax.swing.JFormattedTextField txtAliquotaCofinsSt;
    private javax.swing.JFormattedTextField txtAliquotaCofinsStReais;
    private javax.swing.JFormattedTextField txtAliquotaIcms;
    private javax.swing.JFormattedTextField txtAliquotaIcms1;
    private javax.swing.JFormattedTextField txtAliquotaIcmsSt;
    private javax.swing.JFormattedTextField txtAliquotaPis;
    private javax.swing.JFormattedTextField txtAliquotaPisReais;
    private javax.swing.JFormattedTextField txtAliquotaPisSt;
    private javax.swing.JFormattedTextField txtAliquotaPisStReais;
    private javax.swing.JFormattedTextField txtAliquotaSuportadaConsumidorFinal;
    private javax.swing.JFormattedTextField txtBcIcmsSt;
    private javax.swing.JTextField txtCest;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JFormattedTextField txtDesconto;
    private javax.swing.JTextField txtDescricao;
    private javax.swing.JTextField txtEan;
    private javax.swing.JTextField txtEanTributavel;
    private javax.swing.JTextField txtExTipi;
    private javax.swing.JFormattedTextField txtFrete;
    private javax.swing.JFormattedTextField txtItemPedidoCompra;
    private javax.swing.JTextField txtNcm;
    private javax.swing.JTextField txtParcialComercial;
    private javax.swing.JTextField txtParcialTributavel;
    private javax.swing.JTextField txtPedidoCompra;
    private javax.swing.JFormattedTextField txtPercentualBcOperacaoPropria;
    private javax.swing.JFormattedTextField txtPercentualMargemValorAdicionadoIcmsSt;
    private javax.swing.JFormattedTextField txtPercentualReducaoBcIcms;
    private javax.swing.JFormattedTextField txtPercentualReducaoBcIcms1;
    private javax.swing.JFormattedTextField txtPercentualReducaoBcIcmsSt;
    private javax.swing.JFormattedTextField txtQuantidadeComercial;
    private javax.swing.JFormattedTextField txtQuantidadeTributavel;
    private javax.swing.JFormattedTextField txtQuantidadeVendidaCofins;
    private javax.swing.JFormattedTextField txtQuantidadeVendidaCofinsSt;
    private javax.swing.JFormattedTextField txtQuantidadeVendidaPis;
    private javax.swing.JFormattedTextField txtQuantidadeVendidaPisSt;
    private javax.swing.JFormattedTextField txtSeguro;
    private javax.swing.JTextField txtTotalTributos;
    private javax.swing.JFormattedTextField txtValorBcCofins;
    private javax.swing.JFormattedTextField txtValorBcCofinsSt;
    private javax.swing.JFormattedTextField txtValorBcIcms;
    private javax.swing.JFormattedTextField txtValorBcIcms1;
    private javax.swing.JFormattedTextField txtValorBcIcmsStRetido;
    private javax.swing.JFormattedTextField txtValorBcPis;
    private javax.swing.JFormattedTextField txtValorBcPisSt;
    private javax.swing.JFormattedTextField txtValorCofins;
    private javax.swing.JFormattedTextField txtValorCofinsSt;
    private javax.swing.JFormattedTextField txtValorCreditoIcms;
    private javax.swing.JFormattedTextField txtValorIcms;
    private javax.swing.JFormattedTextField txtValorIcms1;
    private javax.swing.JFormattedTextField txtValorIcmsDesonerado;
    private javax.swing.JFormattedTextField txtValorIcmsProprioSubstituto;
    private javax.swing.JFormattedTextField txtValorIcmsSt;
    private javax.swing.JFormattedTextField txtValorIcmsStRetido;
    private javax.swing.JFormattedTextField txtValorPis;
    private javax.swing.JFormattedTextField txtValorPisSt;
    private javax.swing.JTextField txtValorTotalBruto;
    private javax.swing.JFormattedTextField txtValorUnitarioComercial;
    private javax.swing.JFormattedTextField txtValorUnitarioTributavel;
    // End of variables declaration//GEN-END:variables
}
