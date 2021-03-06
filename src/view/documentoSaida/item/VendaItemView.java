/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoSaida.item;

import br.com.swconsultoria.nfe.dom.enuns.EstadosEnum;
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
import model.mysql.bean.endereco.Estado;
import model.mysql.bean.fiscal.Anp;
import model.nosql.TipoCalculoEnum;
import model.mysql.bean.fiscal.Cest;
import model.mysql.bean.fiscal.Cfop;
import model.mysql.bean.fiscal.Cofins;
import model.mysql.bean.fiscal.Icms;
import model.mysql.bean.fiscal.Ipi;
import model.mysql.bean.fiscal.Ncm;
import model.mysql.bean.fiscal.Pis;
import model.mysql.bean.fiscal.ProdutoOrigem;
import model.mysql.bean.fiscal.UnidadeComercial;
import model.mysql.bean.fiscal.nfe.ModalidadeBcIcms;
import model.mysql.bean.fiscal.nfe.ModalidadeBcIcmsSt;
import model.mysql.bean.fiscal.nfe.MotivoDesoneracao;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.endereco.EstadoDAO;
import model.mysql.dao.fiscal.AnpDAO;
import model.mysql.dao.fiscal.CfopDAO;
import model.mysql.dao.fiscal.CofinsDAO;
import model.mysql.dao.fiscal.IcmsDAO;
import model.mysql.dao.fiscal.IpiDAO;
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
import util.entities.MovimentoFisicoUtil;
import view.catalogo.CestPesquisaView;
import view.catalogo.NcmPesquisaView;
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
    MovimentoFisico mf;
    
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
        
        pnlCombustivelCide.setVisible(false);

        this.documento = movimentoFisico.getVenda();
        this.mf = movimentoFisico;
        
        //pnlIcmsEfetivo.setVisible(false);

        carregarCfop();
        carregarUnidadeComercial();
        carregarUnidadeTributavel();
        
        //Icms------------------------------------------------------------------
        carregarOrigem();
        carregarIcms();
        carregarModalidadeBcIcms();
        carregarMotivoDesoneracao();
        carregarModalidadeBcIcmsSt();
        carregarIcmsStDevidoOperacaoUf();
        //Fim Icms--------------------------------------------------------------
        
        //Ipi-------------------------------------------------------------------
        carregarIpi();
        carregarIpiTipoCalculo();
        chavearIpi();
        //Fim Ipi---------------------------------------------------------------
        
        //Pis-------------------------------------------------------------------
        carregarPis();
        carregarPisTipoCalculo();
        carregarPisStTipoCalculo();
        chavearPis();
        //Fim Pis---------------------------------------------------------------
        
        //Cofins----------------------------------------------------------------
        carregarCofins();
        carregarCofinsTipoCalculo();
        carregarCofinsStTipoCalculo();
        chavearCofins();
        //Fim Cofins------------------------------------------------------------
        
        carregarCombustivelUf();
        
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
        txtCodigo.setText(mf.getCodigo());
        txtDescricao.setText(mf.getDescricao());
        txtDescricao.setCaretPosition(0);
        txtExTipi.setText(mf.getExTipi());
        txtNcm.setText(mf.getNcm() != null ? mf.getNcm().getCodigo() : "");
        txtCest.setText(mf.getCest());
        cboCfop.setSelectedItem(mf.getCfop());
        cboUnidadeComercial.setSelectedItem(mf.getUnidadeComercialVenda());
        txtQuantidadeComercial.setText(Decimal.toString(mf.getSaldoLinearAbsoluto()));
        txtValorUnitarioComercial.setText(Decimal.toString(mf.getValor()));
        txtEan.setText(mf.getEan());
        
        cboUnidadeTributavel.setSelectedItem(mf.getUnidadeTributavel());
        txtQuantidadeTributavel.setText(Decimal.toString(mf.getQuantidadeTributavel()));
        txtValorUnitarioTributavel.setText(Decimal.toString(mf.getValorTributavel()));
        txtEanTributavel.setText(mf.getEanTributavel());
        
        txtAcrescimo.setText(Decimal.toString(mf.getAcrescimoConsolidado()));
        txtDesconto.setText(Decimal.toString(mf.getDescontoConsolidado()));
        
        txtFrete.setText(Decimal.toString(mf.getValorFrete()));
        txtSeguro.setText(Decimal.toString(mf.getValorSeguro()));
        
        txtValorTotalBruto.setText(Decimal.toString(mf.getSubtotal()));
        
        chkValorCompoeTotal.setSelected(mf.isValorCompoeTotal());
        
        txtPedidoCompra.setText(mf.getPedidoCompra());
        txtItemPedidoCompra.setText(mf.getItemPedidoCompra().toString());
        
        calcularTotais();
        //Fim Principal---------------------------------------------------------
        
        
        
        //Tributos--------------------------------------------------------------
        txtTotalTributos.setText(Decimal.toString(mf.getTotalTributos()));
        
        //Icms
        cboOrigem.setSelectedItem(mf.getOrigem());
        cboIcms.setSelectedItem(mf.getIcms());
        
        txtAliquotaAplicavelCalculoCreditoIcms.setText(Decimal.toString(mf.getAliquotaAplicavelCalculoCreditoIcms()));
        txtValorCreditoIcms.setText(Decimal.toString(mf.getValorCreditoIcms()));
        txtAliquotaSuportadaConsumidorFinal.setText(Decimal.toString(mf.getAliquotaSuportadaConsumidorFinal()));
        
        //Icms
        System.out.println("cboModalidadeBcIcms.setSelectedItem: " + mf.getModalidadeBcIcms());
        cboModalidadeBcIcms.setSelectedItem(mf.getModalidadeBcIcms());
        txtPercentualReducaoBcIcms.setText(Decimal.toString(mf.getPercentualReducaoBcIcms()));
        txtValorBcIcms.setText(Decimal.toString(mf.getValorBcIcms()));
        txtAliquotaIcms.setText(Decimal.toString(mf.getAliquotaIcms()));
        
        txtIcmsValorOperacao.setText(Decimal.toString(mf.getIcmsValorOperacao()));
        txtIcmsPercentualDiferimento.setText(Decimal.toString(mf.getIcmsPercentualDiferimento()));
        txtIcmsValorDiferido.setText(Decimal.toString(mf.getIcmsValorDiferido()));
        
        txtValorIcms.setText(Decimal.toString(mf.getValorIcms()));
        txtPercentualBcOperacaoPropria.setText(Decimal.toString(mf.getPercentualBcOperacaoPropria()));
        txtValorIcmsDesonerado.setText(Decimal.toString(mf.getValorIcmsDesonerado()));
        cboMotivoDesoneracao.setSelectedItem(mf.getMotivoDesoneracao());
        
        //Icms St
        cboModalidadeBcIcmsSt.setSelectedItem(mf.getModalidadeBcIcmsSt());
        txtPercentualReducaoBcIcmsSt.setText(Decimal.toString(mf.getPercentualReducaoBcIcmsSt()));
        txtPercentualMargemValorAdicionadoIcmsSt.setText(Decimal.toString(mf.getPercentualMargemValorAdicionadoIcmsSt()));
        txtBcIcmsSt.setText(Decimal.toString(mf.getValorBcIcmsSt()));
        txtAliquotaIcmsSt.setText(Decimal.toString(mf.getAliquotaIcmsSt()));
        txtValorIcmsSt.setText(Decimal.toString(mf.getValorIcmsSt()));
        cboIcmsStDevidoOperacaoUf.setSelectedItem(mf.getIcmsStUf());
        txtValorBcIcmsStRetido.setText(Decimal.toString(mf.getValorBcIcmsStRetido()));
        txtValorIcmsStRetido.setText(Decimal.toString(mf.getValorIcmsStRetido()));
        txtIcmsStValorBcUfDestino.setText(Decimal.toString(mf.getIcmsStValorBcUfDestino()));
        txtIcmsStValorUfDestino.setText(Decimal.toString(mf.getIcmsStValorUfDestino()));
        txtValorIcmsProprioSubstituto.setText(Decimal.toString(mf.getValorIcmsProprioSubstituto()));
        
        //Ipi
        
        cboIpi.setSelectedItem(mf.getIpi());
        txtIpiCodigoEnquadramento.setText(mf.getIpiCodigoEnquadramento());
        txtIpiCnpjProdutor.setText(mf.getIpiCnpjProdutor());
        cboIpiTipoCalculo.setSelectedItem(mf.getIpiTipoCalculo());
        txtIpiValorBc.setText(Decimal.toString(mf.getIpiValorBc()));
        txtIpiAliquota.setText(Decimal.toString(mf.getIpiAliquota()));
        txtIpiQuantidadeTotalUnidadePadrao.setText(Decimal.toString(mf.getIpiQuantidadeTotalUnidadePadrao()));
        txtIpiValorUnidadeTributavel.setText(Decimal.toString(mf.getIpiValorUnidadeTributavel()));
        txtIpiValor.setText(Decimal.toString(mf.getIpiValor()));
        
        //Pis
        cboPis.setSelectedItem(mf.getPis());
        cboPisTipoCalculo.setSelectedItem(mf.getPisTipoCalculo());
        txtValorBcPis.setText(Decimal.toString(mf.getValorBcPis()));
        txtAliquotaPis.setText(Decimal.toString(mf.getAliquotaPis()));
        txtAliquotaPisReais.setText(Decimal.toString(mf.getAliquotaPisReais()));
        txtQuantidadeVendidaPis.setText(Decimal.toString(mf.getQuantidadeVendidaPis()));
        txtValorPis.setText(Decimal.toString(mf.getValorPis()));
        
        //Pis St
        cboPisStTipoCalculo.setSelectedItem(mf.getPisStTipoCalculo());
        txtValorBcPisSt.setText(Decimal.toString(mf.getValorBcPisSt()));
        txtAliquotaPisSt.setText(Decimal.toString(mf.getAliquotaPisSt()));
        txtAliquotaPisStReais.setText(Decimal.toString(mf.getAliquotaPisStReais()));
        txtQuantidadeVendidaPisSt.setText(Decimal.toString(mf.getQuantidadeVendidaPisSt()));
        txtValorPisSt.setText(Decimal.toString(mf.getValorPisSt()));
        
        //Cofins
        cboCofins.setSelectedItem(mf.getCofins());
        cboCofinsTipoCalculo.setSelectedItem(mf.getCofinsTipoCalculo());
        txtValorBcCofins.setText(Decimal.toString(mf.getValorBcCofins()));
        txtAliquotaCofins.setText(Decimal.toString(mf.getAliquotaCofins()));
        txtAliquotaCofinsReais.setText(Decimal.toString(mf.getAliquotaCofinsReais()));
        txtQuantidadeVendidaCofins.setText(Decimal.toString(mf.getQuantidadeVendidaCofins()));
        txtValorCofins.setText(Decimal.toString(mf.getValorCofins()));
        
        //Cofins St
        cboCofinsStTipoCalculo.setSelectedItem(mf.getCofinsStTipoCalculo());
        txtValorBcCofinsSt.setText(Decimal.toString(mf.getValorBcCofinsSt()));
        txtAliquotaCofinsSt.setText(Decimal.toString(mf.getAliquotaCofinsSt()));
        txtAliquotaCofinsStReais.setText(Decimal.toString(mf.getAliquotaCofinsStReais()));
        txtQuantidadeVendidaCofinsSt.setText(Decimal.toString(mf.getQuantidadeVendidaCofinsSt()));
        txtValorCofinsSt.setText(Decimal.toString(mf.getValorCofinsSt()));
        
        //Combustível
        if (mf.getAnp() != null) {
            txtCombustivelCodigoAnp.setText(mf.getAnp().getCodigo());
            buscarAnp();
        }
        txtCombustivelCodif.setText(mf.getCodif());
        txtCombustivelQuantidade.setText(Decimal.toString(mf.getCombustivelQuantidade(), 4));
        if (mf.getCombustivelUf().isEmpty()) {
            cboCombustivelUf.setSelectedItem("SP");
        } else {
            cboCombustivelUf.setSelectedItem(mf.getCombustivelUf());
        }
        
        //Fim Tributos----------------------------------------------------------
        
    }
    
    private void configurarTela() {
        if(mf.isAgrupado()) {
            lblInfo.setText("*Este item pertence ao documento de id " + mf.getVenda().getId());
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
        System.out.println("carregar mod bc");
        cboModalidadeBcIcms.addItem(null);
        for (ModalidadeBcIcms mod : mods) {
            cboModalidadeBcIcms.addItem(mod);
        }
    }
    
    private void carregarMotivoDesoneracao() {
        /*List<MotivoDesoneracao> mots = new MotivoDesoneracaoDAO().findAll();

        cboMotivoDesoneracao.addItem(null);
        for (MotivoDesoneracao mot : mots) {
            cboMotivoDesoneracao.addItem(mot);
        }*/
        
        MotivoDesoneracaoDAO motivoDesoneracaoDAO = new MotivoDesoneracaoDAO();
        
        cboMotivoDesoneracao.removeAllItems();
        cboMotivoDesoneracao.addItem(null);
        
        Icms icms = (Icms) cboIcms.getSelectedItem();
        
        if (icms != null) {
            switch (icms.getCodigo()) {
                case "20":
                case "70":
                case "90":
                    cboMotivoDesoneracao.addItem(motivoDesoneracaoDAO.findById(3));
                    cboMotivoDesoneracao.addItem(motivoDesoneracaoDAO.findById(9));
                    cboMotivoDesoneracao.addItem(motivoDesoneracaoDAO.findById(12));
                    break;
                    
                case "30":
                    cboMotivoDesoneracao.addItem(motivoDesoneracaoDAO.findById(6));
                    cboMotivoDesoneracao.addItem(motivoDesoneracaoDAO.findById(7));
                    cboMotivoDesoneracao.addItem(motivoDesoneracaoDAO.findById(9));
                    break;
                    
                case "40":
                case "41":
                case "50":
                    cboMotivoDesoneracao.addItem(motivoDesoneracaoDAO.findById(1));
                    cboMotivoDesoneracao.addItem(motivoDesoneracaoDAO.findById(3));
                    cboMotivoDesoneracao.addItem(motivoDesoneracaoDAO.findById(4));
                    cboMotivoDesoneracao.addItem(motivoDesoneracaoDAO.findById(5));
                    cboMotivoDesoneracao.addItem(motivoDesoneracaoDAO.findById(6));
                    cboMotivoDesoneracao.addItem(motivoDesoneracaoDAO.findById(7));
                    cboMotivoDesoneracao.addItem(motivoDesoneracaoDAO.findById(8));
                    cboMotivoDesoneracao.addItem(motivoDesoneracaoDAO.findById(9));
                    cboMotivoDesoneracao.addItem(motivoDesoneracaoDAO.findById(10));
                    cboMotivoDesoneracao.addItem(motivoDesoneracaoDAO.findById(11));
                    cboMotivoDesoneracao.addItem(motivoDesoneracaoDAO.findById(12));
                    cboMotivoDesoneracao.addItem(motivoDesoneracaoDAO.findById(16));
                    cboMotivoDesoneracao.addItem(motivoDesoneracaoDAO.findById(90));
                    break;
            }
        }
        
        //selecionar após recarregar as o
        cboMotivoDesoneracao.setSelectedItem(mf.getMotivoDesoneracao());
        
    }

    private void carregarModalidadeBcIcmsSt() {
        List<ModalidadeBcIcmsSt> mods = new ModalidadeBcIcmsStDAO().findAll();

        cboModalidadeBcIcmsSt.addItem(null);
        for (ModalidadeBcIcmsSt mod : mods) {
            cboModalidadeBcIcmsSt.addItem(mod);
        }
    }
    
    private void carregarIcmsStDevidoOperacaoUf() {
        for(Estado uf : new EstadoDAO().findAll()) {
            cboIcmsStDevidoOperacaoUf.addItem(uf.getSigla());
        }
        cboIcmsStDevidoOperacaoUf.addItem("EX");
    }
    
    private void carregarIpi() {
        cboIpi.addItem(null);
        for (Ipi ipi : new IpiDAO().findAll()) {
            cboIpi.addItem(ipi);
        }
    }
    
    private void carregarIpiTipoCalculo() {
        cboIpiTipoCalculo.addItem(TipoCalculoEnum.PERCENTUAL);
        cboIpiTipoCalculo.addItem(TipoCalculoEnum.VALOR);
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
        mf.setCodigo(txtCodigo.getText());
        mf.setDescricao(txtDescricao.getText());
        mf.setExTipi(txtExTipi.getText());
        mf.setNcm(new NcmDAO().findByCodigo(txtNcm.getText()));
        mf.setCest(txtCest.getText());
        mf.setCfop((Cfop) cboCfop.getSelectedItem());
        mf.setUnidadeComercialVenda((UnidadeComercial) cboUnidadeComercial.getSelectedItem());
        mf.setSaida(Decimal.fromString(txtQuantidadeComercial.getText())); //ajustar de acordo com o tipo de operação
        mf.setValor(Decimal.fromString(txtValorUnitarioComercial.getText()));
        mf.setEan(txtEan.getText());
        
        mf.setUnidadeTributavel((UnidadeComercial) cboUnidadeTributavel.getSelectedItem());
        mf.setQuantidadeTributavel(Decimal.fromString(txtQuantidadeTributavel.getText()));
        mf.setValorTributavel(Decimal.fromString(txtValorUnitarioTributavel.getText()));
        mf.setEanTributavel(txtEanTributavel.getText());
        
        //movimentoFisico.setAcrescimoMonetario(Decimal.fromString(txtAcrescimo.getText()));
        //movimentoFisico.setDescontoMonetario(Decimal.fromString(txtDesconto.getText()));
        
        mf.setValorFrete(Decimal.fromString(txtFrete.getText()));
        mf.setValorSeguro(Decimal.fromString(txtSeguro.getText()));
        
        mf.setValorCompoeTotal(chkValorCompoeTotal.isSelected());
        
        mf.setPedidoCompra(txtPedidoCompra.getText());
        mf.setItemPedidoCompra(Integer.valueOf(txtItemPedidoCompra.getText()));
        //Fim Principal---------------------------------------------------------
        
        
        //Tributos--------------------------------------------------------------
        mf.setOrigem((ProdutoOrigem) cboOrigem.getSelectedItem());
        mf.setIcms((Icms) cboIcms.getSelectedItem());
        
        mf.setAliquotaAplicavelCalculoCreditoIcms(Decimal.fromString(txtAliquotaAplicavelCalculoCreditoIcms.getText()));
        mf.setValorCreditoIcms(Decimal.fromString(txtValorCreditoIcms.getText()));
        mf.setAliquotaSuportadaConsumidorFinal(Decimal.fromString(txtAliquotaSuportadaConsumidorFinal.getText()));
        
        mf.setModalidadeBcIcms((ModalidadeBcIcms) cboModalidadeBcIcms.getSelectedItem());
        mf.setPercentualReducaoBcIcms(Decimal.fromString(txtPercentualReducaoBcIcms.getText()));
        mf.setValorBcIcms(Decimal.fromString(txtValorBcIcms.getText()));
        mf.setAliquotaIcms(Decimal.fromString(txtAliquotaIcms.getText()));
        
        mf.setIcmsValorOperacao(Decimal.fromString(txtIcmsValorOperacao.getText()));
        mf.setIcmsPercentualDiferimento(Decimal.fromString(txtIcmsPercentualDiferimento.getText()));
        mf.setIcmsValorDiferido(Decimal.fromString(txtIcmsValorDiferido.getText()));
        
        
        mf.setValorIcms(Decimal.fromString(txtValorIcms.getText()));
        mf.setPercentualBcOperacaoPropria(Decimal.fromString(txtPercentualBcOperacaoPropria.getText()));
        mf.setValorIcmsDesonerado(Decimal.fromString(txtValorIcmsDesonerado.getText()));
        mf.setMotivoDesoneracao((MotivoDesoneracao) cboMotivoDesoneracao.getSelectedItem());
        
        mf.setModalidadeBcIcmsSt((ModalidadeBcIcmsSt) cboModalidadeBcIcmsSt.getSelectedItem());
        mf.setPercentualReducaoBcIcmsSt(Decimal.fromString(txtPercentualReducaoBcIcmsSt.getText()));
        mf.setPercentualMargemValorAdicionadoIcmsSt(Decimal.fromString(txtPercentualMargemValorAdicionadoIcmsSt.getText()));
        mf.setValorBcIcmsSt(Decimal.fromString(txtBcIcmsSt.getText()));
        mf.setAliquotaIcmsSt(Decimal.fromString(txtAliquotaIcmsSt.getText()));
        mf.setValorIcmsSt(Decimal.fromString(txtValorIcmsSt.getText()));
        mf.setIcmsStUf((String) cboIcmsStDevidoOperacaoUf.getSelectedItem());
        mf.setValorBcIcmsStRetido(Decimal.fromString(txtValorBcIcmsStRetido.getText()));
        mf.setValorIcmsStRetido(Decimal.fromString(txtValorIcmsStRetido.getText()));
        mf.setIcmsStValorBcUfDestino(Decimal.fromString(txtIcmsStValorBcUfDestino.getText()));
        mf.setIcmsStValorUfDestino(Decimal.fromString(txtIcmsStValorUfDestino.getText()));
        mf.setValorIcmsProprioSubstituto(Decimal.fromString(txtValorIcmsProprioSubstituto.getText()));
        
        //Ipi
        mf.setIpi((Ipi) cboIpi.getSelectedItem());
        mf.setIpiCodigoEnquadramento(txtIpiCodigoEnquadramento.getText());
        mf.setIpiCnpjProdutor(txtIpiCnpjProdutor.getText());
        mf.setIpiTipoCalculo((TipoCalculoEnum) cboIpiTipoCalculo.getSelectedItem());
        mf.setIpiValorBc(Decimal.fromString(txtIpiValorBc.getText()));
        mf.setIpiAliquota(Decimal.fromString(txtIpiAliquota.getText()));
        mf.setIpiQuantidadeTotalUnidadePadrao(Decimal.fromString(txtIpiQuantidadeTotalUnidadePadrao.getText()));
        mf.setIpiValorUnidadeTributavel(Decimal.fromString(txtIpiValorUnidadeTributavel.getText()));
        mf.setIpiValor(Decimal.fromString(txtIpiValor.getText()));
        
        
        //Pis
        mf.setPis((Pis) cboPis.getSelectedItem());
        
        mf.setPisTipoCalculo((TipoCalculoEnum) cboPisTipoCalculo.getSelectedItem());
        mf.setValorBcPis(Decimal.fromString(txtValorBcPis.getText()));
        mf.setAliquotaPis(Decimal.fromString(txtAliquotaPis.getText()));
        mf.setAliquotaPisReais(Decimal.fromString(txtAliquotaPisReais.getText()));
        mf.setQuantidadeVendidaPis(Decimal.fromString(txtQuantidadeVendidaPis.getText()));
        mf.setValorPis(Decimal.fromString(txtValorPis.getText()));
        
        //Pis St
        mf.setPisStTipoCalculo((TipoCalculoEnum) cboPisStTipoCalculo.getSelectedItem());
        mf.setValorBcPisSt(Decimal.fromString(txtValorBcPisSt.getText()));
        mf.setAliquotaPisSt(Decimal.fromString(txtAliquotaPisSt.getText()));
        mf.setAliquotaPisStReais(Decimal.fromString(txtAliquotaPisStReais.getText()));
        mf.setQuantidadeVendidaPisSt(Decimal.fromString(txtQuantidadeVendidaPisSt.getText()));
        mf.setValorPisSt(Decimal.fromString(txtValorPisSt.getText()));
        
        //Cofins
        mf.setCofins((Cofins) cboCofins.getSelectedItem());
        
        mf.setCofinsTipoCalculo((TipoCalculoEnum) cboCofinsTipoCalculo.getSelectedItem());
        mf.setValorBcCofins(Decimal.fromString(txtValorBcCofins.getText()));
        mf.setAliquotaCofins(Decimal.fromString(txtAliquotaCofins.getText()));
        mf.setAliquotaCofinsReais(Decimal.fromString(txtAliquotaCofinsReais.getText()));
        mf.setQuantidadeVendidaCofins(Decimal.fromString(txtQuantidadeVendidaCofins.getText()));
        mf.setValorCofins(Decimal.fromString(txtValorCofins.getText()));
        
        //Cofins St
        mf.setCofinsStTipoCalculo((TipoCalculoEnum) cboCofinsStTipoCalculo.getSelectedItem());
        mf.setValorBcCofinsSt(Decimal.fromString(txtValorBcCofinsSt.getText()));
        mf.setAliquotaCofinsSt(Decimal.fromString(txtAliquotaCofinsSt.getText()));
        mf.setAliquotaCofinsStReais(Decimal.fromString(txtAliquotaCofinsStReais.getText()));
        mf.setQuantidadeVendidaCofinsSt(Decimal.fromString(txtQuantidadeVendidaCofinsSt.getText()));
        mf.setValorCofinsSt(Decimal.fromString(txtValorCofinsSt.getText()));
        
        //Fim Tributos----------------------------------------------------------
        
        
        //Combustível ----------------------------------------------------------
        Anp anp = new AnpDAO().findByCodigo(txtCombustivelCodigoAnp.getText());
        if (anp != null) {
            mf.setAnp(anp);
        }
        mf.setCodif(txtCombustivelCodif.getText());
        mf.setCombustivelQuantidade(Decimal.fromString(txtCombustivelQuantidade.getText()));
        mf.setCombustivelUf((String) cboCombustivelUf.getSelectedItem());
        
        //movimentoFisico.setBcCombustivel(Decimal.fromString(txtCombustivelBc.getText()));
        //movimentoFisico.setAliquotaCombustivel(Decimal.fromString(txtCombustivelAliquota.getText()));
        //movimentoFisico.setValorCombustivel(Decimal.fromString(txtCombustivelValor.getText()));
        
        //Fim Combustível ------------------------------------------------------
        
        
        MovimentoFisicoUtil.calcularComissao(mf, documento);
        
        mf = movimentoFisicoDAO.save(mf);
        
        documento.addMovimentoFisico(mf);
        
        vendaDAO.save(documento);
    }
    
    private void chavearIcms() {
        carregarMotivoDesoneracao();
        
        txtAliquotaAplicavelCalculoCreditoIcms.setEditable(false);
        txtValorCreditoIcms.setEditable(false);
        
        
        cboModalidadeBcIcms.setEnabled(false);
        txtPercentualReducaoBcIcms.setEnabled(false);
        txtValorBcIcms.setEnabled(false);

        txtAliquotaIcms.setEnabled(false);
        txtValorIcms.setEnabled(false);
        txtPercentualBcOperacaoPropria.setEnabled(false);
        txtIcmsValorOperacao.setEnabled(false);

        txtIcmsPercentualDiferimento.setEnabled(false);
        txtIcmsValorDiferido.setEnabled(false);
        txtValorIcmsDesonerado.setEnabled(false);
        cboMotivoDesoneracao.setEnabled(false);

        
        Icms icms = (Icms) cboIcms.getSelectedItem();
        
        if (icms != null) {
            switch (icms.getCodigo()) {
                case "00":
                    cboModalidadeBcIcms.setEnabled(true);
                    txtValorBcIcms.setEnabled(true);
                    txtAliquotaIcms.setEnabled(true);
                    txtValorIcms.setEnabled(true);
                    break;

                case "10":
                    cboModalidadeBcIcms.setEnabled(true);
                    txtValorBcIcms.setEnabled(true);
                    txtAliquotaIcms.setEnabled(true);
                    txtValorIcms.setEnabled(true);
                    
                    if (icms.getId() == 3) { //10 (com partilha...)
                        txtPercentualReducaoBcIcms.setEnabled(true);
                        txtPercentualBcOperacaoPropria.setEnabled(true);
                    }
                    break;

                case "20":
                    cboModalidadeBcIcms.setEnabled(true);
                    txtPercentualReducaoBcIcms.setEnabled(true);
                    txtValorBcIcms.setEnabled(true);

                    txtAliquotaIcms.setEnabled(true);
                    txtValorIcms.setEnabled(true);
                    //txtPercentualBcOperacaoPropria.setEnabled(true);
                    //txtIcmsValorOperacao.setEnabled(true);

                    //txtIcmsPercentualDiferimento.setEnabled(true);
                    //txtIcmsValorDiferido.setEnabled(true);
                    txtValorIcmsDesonerado.setEnabled(true);
                    cboMotivoDesoneracao.setEnabled(true);
                    
                    break;

                case "30":
                    txtValorIcmsDesonerado.setEnabled(true);
                    cboMotivoDesoneracao.setEnabled(true);
                    
                    break;

                case "40":
                case "41":
                case "50":
                    //cboModalidadeBcIcms.setEnabled(true);
                    //txtPercentualReducaoBcIcms.setEnabled(true);
                    //txtValorBcIcms.setEnabled(true);

                    //txtAliquotaIcms.setEnabled(true);
                    //txtValorIcms.setEnabled(true);
                    //txtPercentualBcOperacaoPropria.setEnabled(true);
                    //txtIcmsValorOperacao.setEnabled(true);

                    //txtIcmsPercentualDiferimento.setEnabled(true);
                    //txtIcmsValorDiferido.setEnabled(true);
                    txtValorIcmsDesonerado.setEnabled(true);
                    cboMotivoDesoneracao.setEnabled(true);
                    
                    if (icms.getId() == 8) { //41 - Não tributada (ICMSST devido para a UF de destino...)
                        txtValorIcmsDesonerado.setEnabled(false);
                        cboMotivoDesoneracao.setEnabled(false);
                    
                    }
                    break;

                case "51":
                    cboModalidadeBcIcms.setEnabled(true);
                    txtPercentualReducaoBcIcms.setEnabled(true);
                    txtValorBcIcms.setEnabled(true);

                    txtAliquotaIcms.setEnabled(true);
                    txtValorIcms.setEnabled(true);
                    //txtPercentualBcOperacaoPropria.setEnabled(true);
                    txtIcmsValorOperacao.setEnabled(true);

                    txtIcmsPercentualDiferimento.setEnabled(true);
                    txtIcmsValorDiferido.setEnabled(true);
                    //txtValorIcmsDesonerado.setEnabled(true);
                    //cboMotivoDesoneracao.setEnabled(true);
                    
                    break;

                case "60":
                    
                    break;

                case "70":
                    cboModalidadeBcIcms.setEnabled(true);
                    txtPercentualReducaoBcIcms.setEnabled(true);
                    txtValorBcIcms.setEnabled(true);

                    txtAliquotaIcms.setEnabled(true);
                    txtValorIcms.setEnabled(true);
                    //txtPercentualBcOperacaoPropria.setEnabled(true);
                    //txtIcmsValorOperacao.setEnabled(true);

                    //txtIcmsPercentualDiferimento.setEnabled(true);
                    //txtIcmsValorDiferido.setEnabled(true);
                    txtValorIcmsDesonerado.setEnabled(true);
                    cboMotivoDesoneracao.setEnabled(true);
                    break;
                    
                case "90":
                    cboModalidadeBcIcms.setEnabled(true);
                    txtPercentualReducaoBcIcms.setEnabled(true);
                    txtValorBcIcms.setEnabled(true);
                    
                    txtAliquotaIcms.setEnabled(true);
                    txtValorIcms.setEnabled(true);
                    txtPercentualBcOperacaoPropria.setEnabled(true);
                    txtIcmsValorOperacao.setEnabled(false);
                    
                    txtIcmsPercentualDiferimento.setEnabled(false);
                    txtIcmsValorDiferido.setEnabled(false);
                    txtValorIcmsDesonerado.setEnabled(false);
                    cboMotivoDesoneracao.setEnabled(false);
                    
                    if(icms.getId() == 15) { //Outras (sem observações)
                        txtPercentualBcOperacaoPropria.setEnabled(false);
                        
                        txtValorIcmsDesonerado.setEnabled(true);
                        cboMotivoDesoneracao.setEnabled(true);
                    
                    }
                    
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
                    
                case "202":
                case "203":
                    txtAliquotaAplicavelCalculoCreditoIcms.setText("0,00");
                    txtValorCreditoIcms.setText("0,00");
                    break;

                //case "300": Imune
                //case "400": Não tributada
                    
                case "500":
                    //txtAliquotaSuportadaConsumidorFinal.setEditable(true); colocar no ST
                    
                    txtValorBcIcmsStRetido.setEnabled(true);
                    txtValorIcmsStRetido.setEnabled(true);
                    txtValorIcmsProprioSubstituto.setEnabled(true);
                    
                    break;

                case "900":
                    txtAliquotaAplicavelCalculoCreditoIcms.setEditable(true);
                    txtValorCreditoIcms.setEditable(true);
                    
                    cboModalidadeBcIcms.setEnabled(true);
                    txtPercentualReducaoBcIcms.setEnabled(true);
                    txtValorBcIcms.setEnabled(true);
                    
                    txtAliquotaIcms.setEnabled(true);
                    //txtValorIcms.setEnabled(true);
                    //txtPercentualBcOperacaoPropria.setEnabled(true);
                    txtIcmsValorOperacao.setEnabled(true);
                    
                    //txtIcmsPercentualDiferimento.setEnabled(false);
                    //txtIcmsValorDiferido.setEnabled(false);
                    //txtValorIcmsDesonerado.setEnabled(false);
                    //cboMotivoDesoneracao.setEnabled(false);
                    
                    
                    
                    txtValorBcIcmsStRetido.setEditable(false);
                    txtValorBcIcmsStRetido.setText("0,00");
                    
                    txtValorIcmsStRetido.setEditable(false);
                    txtValorIcmsStRetido.setText("0,00");
                    
                    txtValorIcmsProprioSubstituto.setEditable(false);
                    txtValorIcmsProprioSubstituto.setText("0,00");
                    
                    
                    

            }
        }
    }
    
    private void chavearIcmsSt() {
        
        cboModalidadeBcIcmsSt.setEnabled(false);
        txtPercentualReducaoBcIcmsSt.setEnabled(false);
        
        txtPercentualMargemValorAdicionadoIcmsSt.setEnabled(false);
        txtBcIcmsSt.setEnabled(false);
        txtAliquotaIcmsSt.setEnabled(false);
        txtValorIcmsSt.setEnabled(false);
        cboIcmsStDevidoOperacaoUf.setEnabled(false);
        
        txtValorBcIcmsStRetido.setEnabled(false);
        txtValorIcmsStRetido.setEnabled(false);
        txtIcmsStValorBcUfDestino.setEnabled(false);
        txtIcmsStValorUfDestino.setEnabled(false);
        txtAliquotaSuportadaConsumidorFinal.setEnabled(false);
        txtValorIcmsProprioSubstituto.setEnabled(false);
        
        
        Icms icms = (Icms) cboIcms.getSelectedItem();
        
        if (icms != null) {
            switch (icms.getCodigo()) {
                case "00":
                    
                    break;

                case "10":
                    cboModalidadeBcIcmsSt.setEnabled(true);
                    txtPercentualReducaoBcIcmsSt.setEnabled(true);

                    txtPercentualMargemValorAdicionadoIcmsSt.setEnabled(true);
                    txtBcIcmsSt.setEnabled(true);
                    txtAliquotaIcmsSt.setEnabled(true);
                    txtValorIcmsSt.setEnabled(true);
                    //cboIcmsStDevidoOperacaoUf.setEnabled(true);

                    //txtValorBcIcmsStRetido.setEnabled(true);
                    //txtValorIcmsStRetido.setEnabled(true);
                    //txtValorIcmsProprioSubstituto.setEnabled(true);
                    
                    if (icms.getId() == 3) { //10 (com partilha...)
                        cboIcmsStDevidoOperacaoUf.setEnabled(true);
                    }
                    break;

                case "20":
                    //nenhum
                    break;

                case "30":
                    cboModalidadeBcIcmsSt.setEnabled(true);
                    txtPercentualReducaoBcIcmsSt.setEnabled(true);

                    txtPercentualMargemValorAdicionadoIcmsSt.setEnabled(true);
                    txtBcIcmsSt.setEnabled(true);
                    txtAliquotaIcmsSt.setEnabled(true);
                    txtValorIcmsSt.setEnabled(true);
                    //cboIcmsStDevidoOperacaoUf.setEnabled(true);

                    //txtValorBcIcmsStRetido.setEnabled(true);
                    //txtValorIcmsStRetido.setEnabled(true);
                    //txtValorIcmsProprioSubstituto.setEnabled(true);
                    break;

                case "40":
                case "41":
                case "50":
                    if (icms.getId() == 8) { //41 - Não tributada (ICMSST devido para a UF de destino...)
                        //cboModalidadeBcIcmsSt.setEnabled(true);
                        //txtPercentualReducaoBcIcmsSt.setEnabled(true);

                        //txtPercentualMargemValorAdicionadoIcmsSt.setEnabled(true);
                        //txtBcIcmsSt.setEnabled(true);
                        //txtAliquotaIcmsSt.setEnabled(true);
                        //txtValorIcmsSt.setEnabled(true);
                        //cboIcmsStDevidoOperacaoUf.setEnabled(true);

                        txtValorBcIcmsStRetido.setEnabled(true);
                        txtValorIcmsStRetido.setEnabled(true);
                        txtIcmsStValorBcUfDestino.setEnabled(true);
                        txtIcmsStValorUfDestino.setEnabled(true);
                        txtAliquotaSuportadaConsumidorFinal.setEnabled(true);
                        txtValorIcmsProprioSubstituto.setEnabled(true);
                    }
                    break;

                case "51":
                    
                    break;

                case "60":
                    //cboModalidadeBcIcmsSt.setEnabled(true);
                    //txtPercentualReducaoBcIcmsSt.setEnabled(true);

                    //txtPercentualMargemValorAdicionadoIcmsSt.setEnabled(true);
                    //txtBcIcmsSt.setEnabled(true);
                    //txtAliquotaIcmsSt.setEnabled(true);
                    //txtValorIcmsSt.setEnabled(true);
                    //cboIcmsStDevidoOperacaoUf.setEnabled(true);

                    txtValorBcIcmsStRetido.setEnabled(true);
                    txtValorIcmsStRetido.setEnabled(true);
                    txtIcmsStValorBcUfDestino.setEnabled(true);
                    txtIcmsStValorUfDestino.setEnabled(true);
                    //txtAliquotaSuportadaConsumidorFinal.setEnabled(true);
                    //txtValorIcmsProprioSubstituto.setEnabled(true);
                    
                    if (icms.getId() == 12) { //60 - Não tributada (ICMSST devido para a UF de destino...)
                        //cboModalidadeBcIcmsSt.setEnabled(true);
                        //txtPercentualReducaoBcIcmsSt.setEnabled(true);

                        //txtPercentualMargemValorAdicionadoIcmsSt.setEnabled(true);
                        //txtBcIcmsSt.setEnabled(true);
                        //txtAliquotaIcmsSt.setEnabled(true);
                        //txtValorIcmsSt.setEnabled(true);
                        //cboIcmsStDevidoOperacaoUf.setEnabled(true);

                        txtValorBcIcmsStRetido.setEnabled(true);
                        txtValorIcmsStRetido.setEnabled(true);
                        txtIcmsStValorBcUfDestino.setEnabled(true);
                        txtIcmsStValorUfDestino.setEnabled(true);
                        txtAliquotaSuportadaConsumidorFinal.setEnabled(true);
                        txtValorIcmsProprioSubstituto.setEnabled(true);
                    }
                    break;

                case "70":
                    cboModalidadeBcIcmsSt.setEnabled(true);
                    txtPercentualReducaoBcIcmsSt.setEnabled(true);

                    txtPercentualMargemValorAdicionadoIcmsSt.setEnabled(true);
                    txtBcIcmsSt.setEnabled(true);
                    txtAliquotaIcmsSt.setEnabled(true);
                    txtValorIcmsSt.setEnabled(true);
                    //cboIcmsStDevidoOperacaoUf.setEnabled(true);

                    //txtValorBcIcmsStRetido.setEnabled(true);
                    //txtValorIcmsStRetido.setEnabled(true);
                    //txtIcmsStValorBcUfDestino.setEnabled(true);
                    //txtIcmsStValorUfDestino.setEnabled(true);
                    //txtAliquotaSuportadaConsumidorFinal.setEnabled(true);
                    //txtValorIcmsProprioSubstituto.setEnabled(true);
                    break;
                    
                case "90":
                    cboModalidadeBcIcmsSt.setEnabled(true);
                    txtPercentualReducaoBcIcmsSt.setEnabled(true);

                    txtPercentualMargemValorAdicionadoIcmsSt.setEnabled(true);
                    txtBcIcmsSt.setEnabled(true);
                    txtAliquotaIcmsSt.setEnabled(true);
                    txtValorIcmsSt.setEnabled(true);
                    cboIcmsStDevidoOperacaoUf.setEnabled(true);

                    //txtValorBcIcmsStRetido.setEnabled(true);
                    //txtValorIcmsStRetido.setEnabled(true);
                    //txtIcmsStValorBcUfDestino.setEnabled(true);
                    //txtIcmsStValorUfDestino.setEnabled(true);
                    //txtAliquotaSuportadaConsumidorFinal.setEnabled(true);
                    //txtValorIcmsProprioSubstituto.setEnabled(true);
                    
                    if(icms.getId() == 15) { //Outras (sem observações)
                        cboIcmsStDevidoOperacaoUf.setEnabled(false);
                    }
                    
                    break;

                case "101":
                case "102":
                case "103":
                    //nenhum
                    break;

                case "201":
                case "202":
                case "203":
                    cboModalidadeBcIcmsSt.setEnabled(true);
                    txtPercentualReducaoBcIcmsSt.setEnabled(true);

                    txtPercentualMargemValorAdicionadoIcmsSt.setEnabled(true);
                    txtBcIcmsSt.setEnabled(true);
                    txtAliquotaIcmsSt.setEnabled(true);
                    txtValorIcmsSt.setEnabled(true);
                    //cboIcmsStDevidoOperacaoUf.setEnabled(true);

                    //txtValorBcIcmsStRetido.setEnabled(true);
                    //txtValorIcmsStRetido.setEnabled(true);
                    //txtIcmsStValorBcUfDestino.setEnabled(true);
                    //txtIcmsStValorUfDestino.setEnabled(true);
                    //txtAliquotaSuportadaConsumidorFinal.setEnabled(true);
                    //txtValorIcmsProprioSubstituto.setEnabled(true);
                    break;

                case "300": //Imune
                case "400": //Não tributada
                    //nenhum
                    break;
                    
                case "500":
                    //cboModalidadeBcIcmsSt.setEnabled(true);
                    //txtPercentualReducaoBcIcmsSt.setEnabled(true);

                    //txtPercentualMargemValorAdicionadoIcmsSt.setEnabled(true);
                    //txtBcIcmsSt.setEnabled(true);
                    //txtAliquotaIcmsSt.setEnabled(true);
                    //txtValorIcmsSt.setEnabled(true);
                    //cboIcmsStDevidoOperacaoUf.setEnabled(true);

                    txtValorBcIcmsStRetido.setEnabled(true);
                    txtValorIcmsStRetido.setEnabled(true);
                    //txtIcmsStValorBcUfDestino.setEnabled(true);
                    //txtIcmsStValorUfDestino.setEnabled(true);
                    //txtAliquotaSuportadaConsumidorFinal.setEnabled(true);
                    txtValorIcmsProprioSubstituto.setEnabled(true);
                    break;

                case "900":
                    cboModalidadeBcIcmsSt.setEnabled(true);
                    txtPercentualReducaoBcIcmsSt.setEnabled(true);

                    txtPercentualMargemValorAdicionadoIcmsSt.setEnabled(true);
                    txtBcIcmsSt.setEnabled(true);
                    txtAliquotaIcmsSt.setEnabled(true);
                    txtValorIcmsSt.setEnabled(true);
                    //cboIcmsStDevidoOperacaoUf.setEnabled(true);

                    //txtValorBcIcmsStRetido.setEnabled(true);
                    //txtValorIcmsStRetido.setEnabled(true);
                    //txtIcmsStValorBcUfDestino.setEnabled(true);
                    //txtIcmsStValorUfDestino.setEnabled(true);
                    //txtAliquotaSuportadaConsumidorFinal.setEnabled(true);
                    //txtValorIcmsProprioSubstituto.setEnabled(true);
                    break;
                    
                    

            }
        }
    }
    
    private void chavearIpi() {
        cboIpiTipoCalculo.setEnabled(false);

        Ipi pis = (Ipi) cboIpi.getSelectedItem();

        if (pis == null) {
            cboIpiTipoCalculo.setSelectedItem(null);

        } else {
            switch (pis.getCodigo()) {
                case "00":
                case "49":
                case "50":
                case "99":
                    cboIpiTipoCalculo.setSelectedItem(null);
                    cboIpiTipoCalculo.setEnabled(true);
                    break;
                    
                default:
                    cboIpiTipoCalculo.setSelectedItem(null);
                    cboIpiTipoCalculo.setEnabled(false);
                    break;

            }
        }

        //chavearIpiTipoCalculo(); já dispara pelo actionPerformed

    }
    
    private void chavearIpiTipoCalculo() {

        if (cboIpiTipoCalculo.getSelectedItem() == null) {
            txtIpiValorBc.setEditable(false);
            txtIpiAliquota.setEditable(false);
            txtIpiQuantidadeTotalUnidadePadrao.setEditable(false);
            txtIpiValorUnidadeTributavel.setEditable(false);
            txtIpiAliquota.setText("0,00");
            txtIpiValorUnidadeTributavel.setText("0,00");

        } else if (cboIpiTipoCalculo.getSelectedItem().equals(TipoCalculoEnum.PERCENTUAL)) {
            txtIpiValorBc.setEditable(true);
            txtIpiAliquota.setEditable(true);
            txtIpiQuantidadeTotalUnidadePadrao.setEditable(false);
            txtIpiQuantidadeTotalUnidadePadrao.setText("0,00");
            txtIpiValorUnidadeTributavel.setEditable(false);
            txtIpiValorUnidadeTributavel.setText("0,00");

        } else {
            txtIpiValorBc.setEditable(false);
            txtIpiValorBc.setText("0,00");
            txtIpiAliquota.setEditable(false);
            txtIpiQuantidadeTotalUnidadePadrao.setEditable(true);
            txtIpiValorUnidadeTributavel.setEditable(true);
            txtIpiAliquota.setText("0,00");

        }
        
        txtIpiValor.setText("0,00");
    }
    
    private void calcularIcmsSt() {
        BigDecimal icmsStValorBc = Decimal.fromString(txtBcIcmsSt.getText());
        BigDecimal icmsStAliquota = Decimal.fromString(txtAliquotaIcmsSt.getText());
        BigDecimal icmsStValor = icmsStValorBc.multiply(icmsStAliquota).divide(new BigDecimal(100), RoundingMode.HALF_UP);
        txtValorIcmsSt.setText(Decimal.toString(icmsStValor));
            
    }
    
    private void calcularIpi() {
        if(cboIpiTipoCalculo.getSelectedItem().equals(TipoCalculoEnum.PERCENTUAL)) {
            BigDecimal ipiValorBc = Decimal.fromString(txtIpiValorBc.getText());
            BigDecimal ipiAliquota = Decimal.fromString(txtIpiAliquota.getText());
            BigDecimal ipiValor = ipiValorBc.multiply(ipiAliquota).divide(new BigDecimal(100), RoundingMode.HALF_UP);
            txtIpiValor.setText(Decimal.toString(ipiValor));
            
        } else {
            BigDecimal ipiQuantidadeTotalUnidadePadrao = Decimal.fromString(txtIpiQuantidadeTotalUnidadePadrao.getText());
            BigDecimal ipiValorUnidadeTributavel = Decimal.fromString(txtIpiValorUnidadeTributavel.getText());
            BigDecimal ipiValor = ipiQuantidadeTotalUnidadePadrao.multiply(ipiValorUnidadeTributavel);
            txtIpiValor.setText(Decimal.toString(ipiValor));
            
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
    
    private void carregarCombustivelUf() {
        for(Estado uf : new EstadoDAO().findAll()) {
            cboCombustivelUf.addItem(uf.getSigla());
        }
        cboCombustivelUf.addItem("EX");
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
    
    private void buscarAnp() {
        String codigo = txtCombustivelCodigoAnp.getText();
        Anp anp = new AnpDAO().findByCodigo(codigo);
        
        if (anp == null) {
            txtCombustivelDescricaoAnp.setText("CÓDIGO ANP NÃO ENCONTRADO");
        } else {
            txtCombustivelDescricaoAnp.setText(anp.getDescricao());
            txtCombustivelCodif.requestFocus();
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
        jLabel80 = new javax.swing.JLabel();
        txtAliquotaAplicavelCalculoCreditoIcms = new javax.swing.JFormattedTextField();
        txtValorCreditoIcms = new javax.swing.JFormattedTextField();
        jLabel81 = new javax.swing.JLabel();
        txtAliquotaSuportadaConsumidorFinal = new javax.swing.JFormattedTextField();
        jLabel104 = new javax.swing.JLabel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel11 = new javax.swing.JPanel();
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
        jLabel116 = new javax.swing.JLabel();
        txtIcmsPercentualDiferimento = new javax.swing.JFormattedTextField();
        jLabel117 = new javax.swing.JLabel();
        txtIcmsValorDiferido = new javax.swing.JFormattedTextField();
        jLabel118 = new javax.swing.JLabel();
        txtIcmsValorOperacao = new javax.swing.JFormattedTextField();
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
        jLabel1 = new javax.swing.JLabel();
        cboIcmsStDevidoOperacaoUf = new javax.swing.JComboBox<>();
        jLabel119 = new javax.swing.JLabel();
        txtIcmsStValorBcUfDestino = new javax.swing.JFormattedTextField();
        jLabel120 = new javax.swing.JLabel();
        txtIcmsStValorUfDestino = new javax.swing.JFormattedTextField();
        jPanel12 = new javax.swing.JPanel();
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
        pnlTributosIPI = new javax.swing.JPanel();
        jLabel107 = new javax.swing.JLabel();
        cboIpi = new javax.swing.JComboBox<>();
        txtIpiCodigoEnquadramento = new javax.swing.JTextField();
        jLabel108 = new javax.swing.JLabel();
        jLabel109 = new javax.swing.JLabel();
        txtIpiCnpjProdutor = new javax.swing.JFormattedTextField();
        cboIpiTipoCalculo = new javax.swing.JComboBox<>();
        jLabel110 = new javax.swing.JLabel();
        txtIpiAliquota = new javax.swing.JFormattedTextField();
        jLabel111 = new javax.swing.JLabel();
        jLabel112 = new javax.swing.JLabel();
        txtIpiValorUnidadeTributavel = new javax.swing.JFormattedTextField();
        txtIpiValorBc = new javax.swing.JFormattedTextField();
        jLabel113 = new javax.swing.JLabel();
        txtIpiQuantidadeTotalUnidadePadrao = new javax.swing.JFormattedTextField();
        jLabel114 = new javax.swing.JLabel();
        txtIpiValor = new javax.swing.JFormattedTextField();
        jLabel115 = new javax.swing.JLabel();
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
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtCombustivelCodif = new javax.swing.JTextField();
        txtCombustivelDescricaoAnp = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txtCombustivelQuantidade = new javax.swing.JFormattedTextField();
        jLabel22 = new javax.swing.JLabel();
        cboCombustivelUf = new javax.swing.JComboBox<>();
        txtCombustivelCodigoAnp = new javax.swing.JTextField();
        pnlCombustivelCide = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        txtCombustivelBc = new javax.swing.JFormattedTextField();
        jLabel106 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        txtCombustivelAliquota = new javax.swing.JFormattedTextField();
        jLabel34 = new javax.swing.JLabel();
        txtCombustivelValor = new javax.swing.JFormattedTextField();
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

        txtAcrescimo.setEditable(false);
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

        txtDesconto.setEditable(false);
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
        jLabel61.setText("Valor ICMS");

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

        jLabel116.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel116.setText("% do Diferimento");

        txtIcmsPercentualDiferimento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIcmsPercentualDiferimento.setText("pDif");
        txtIcmsPercentualDiferimento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIcmsPercentualDiferimento.setName("decimal"); // NOI18N

        jLabel117.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel117.setText("Valor ICMS Diferido");

        txtIcmsValorDiferido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIcmsValorDiferido.setText("vICMSDif");
        txtIcmsValorDiferido.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIcmsValorDiferido.setName("decimal"); // NOI18N
        txtIcmsValorDiferido.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtIcmsValorDiferidoFocusLost(evt);
            }
        });

        jLabel118.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel118.setText("ICMS da Operação");

        txtIcmsValorOperacao.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIcmsValorOperacao.setText("vICMS");
        txtIcmsValorOperacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIcmsValorOperacao.setName("decimal"); // NOI18N
        txtIcmsValorOperacao.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtIcmsValorOperacaoFocusLost(evt);
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
                        .addComponent(cboModalidadeBcIcms, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                                .addComponent(txtValorBcIcms, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcmsLayout.createSequentialGroup()
                                .addComponent(jLabel116)
                                .addGap(18, 18, 18)
                                .addComponent(txtIcmsPercentualDiferimento, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel117)
                                .addGap(18, 18, 18)
                                .addComponent(txtIcmsValorDiferido, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcmsLayout.createSequentialGroup()
                                .addComponent(jLabel54)
                                .addGap(18, 18, 18)
                                .addComponent(txtPercentualBcOperacaoPropria, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel118)
                                .addGap(18, 18, 18)
                                .addComponent(txtIcmsValorOperacao, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcmsLayout.createSequentialGroup()
                                .addComponent(jLabel62)
                                .addGap(18, 18, 18)
                                .addComponent(txtValorIcmsDesonerado, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 42, Short.MAX_VALUE))
                    .addGroup(pnlIcmsLayout.createSequentialGroup()
                        .addComponent(jLabel53)
                        .addGap(18, 18, 18)
                        .addComponent(cboMotivoDesoneracao, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPercentualBcOperacaoPropria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54)
                    .addComponent(txtIcmsValorOperacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel118))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel117)
                        .addComponent(txtIcmsValorDiferido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtIcmsPercentualDiferimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel116)))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtValorIcmsDesonerado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel62))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboMotivoDesoneracao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel53))
                .addContainerGap())
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
        txtAliquotaIcmsSt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAliquotaIcmsStKeyReleased(evt);
            }
        });

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
        txtBcIcmsSt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBcIcmsStKeyReleased(evt);
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
        jLabel105.setText("ICMS próprio do Substituto");

        txtValorIcmsProprioSubstituto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorIcmsProprioSubstituto.setText("vICMSSubstituto");
        txtValorIcmsProprioSubstituto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorIcmsProprioSubstituto.setName("decimal"); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("UF do ICMS ST devido na operação");

        cboIcmsStDevidoOperacaoUf.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel119.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel119.setText("BC ICMS ST da UF destino");

        txtIcmsStValorBcUfDestino.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIcmsStValorBcUfDestino.setText("vBCSTDest");
        txtIcmsStValorBcUfDestino.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIcmsStValorBcUfDestino.setName("decimal"); // NOI18N

        jLabel120.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel120.setText("ICMS ST da UF destino");

        txtIcmsStValorUfDestino.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIcmsStValorUfDestino.setText("vICMSSTDest");
        txtIcmsStValorUfDestino.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIcmsStValorUfDestino.setName("decimal"); // NOI18N

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
                        .addComponent(jLabel56)
                        .addGap(18, 18, 18)
                        .addComponent(txtPercentualReducaoBcIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel58)
                        .addGap(18, 18, 18)
                        .addComponent(txtPercentualMargemValorAdicionadoIcmsSt))
                    .addGroup(pnlIcmsStLayout.createSequentialGroup()
                        .addComponent(jLabel63)
                        .addGap(18, 18, 18)
                        .addComponent(txtBcIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel57)
                        .addGap(18, 18, 18)
                        .addComponent(txtAliquotaIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel64)
                        .addGap(18, 18, 18)
                        .addComponent(txtValorIcmsSt))
                    .addGroup(pnlIcmsStLayout.createSequentialGroup()
                        .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlIcmsStLayout.createSequentialGroup()
                                .addGap(306, 306, 306)
                                .addComponent(jLabel105)
                                .addGap(18, 18, 18)
                                .addComponent(txtValorIcmsProprioSubstituto, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcmsStLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(cboIcmsStDevidoOperacaoUf, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcmsStLayout.createSequentialGroup()
                                .addComponent(jLabel119)
                                .addGap(18, 18, 18)
                                .addComponent(txtIcmsStValorBcUfDestino, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel120)))
                        .addGap(0, 7, Short.MAX_VALUE))
                    .addGroup(pnlIcmsStLayout.createSequentialGroup()
                        .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtIcmsStValorUfDestino, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlIcmsStLayout.createSequentialGroup()
                                .addComponent(jLabel97)
                                .addGap(18, 18, 18)
                                .addComponent(txtValorBcIcmsStRetido, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel98)))
                        .addGap(18, 18, 18)
                        .addComponent(txtValorIcmsStRetido, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))
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
                    .addComponent(txtPercentualReducaoBcIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel58)
                    .addComponent(txtPercentualMargemValorAdicionadoIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBcIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel63)
                    .addComponent(txtAliquotaIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel57)
                    .addComponent(txtValorIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel64))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cboIcmsStDevidoOperacaoUf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtValorBcIcmsStRetido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel97)
                    .addComponent(txtValorIcmsStRetido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel98))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIcmsStValorBcUfDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel119)
                    .addComponent(txtIcmsStValorUfDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel120))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtValorIcmsProprioSubstituto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel105))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlIcms, javax.swing.GroupLayout.PREFERRED_SIZE, 599, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlIcmsSt, javax.swing.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlIcmsSt, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                    .addComponent(pnlIcms, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("ICMS", jPanel11);

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

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlIcmsEfetivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(683, 683, 683))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlIcmsEfetivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("ICMS FCP e Efetivo", jPanel12);

        javax.swing.GroupLayout pnlTributosIcmsLayout = new javax.swing.GroupLayout(pnlTributosIcms);
        pnlTributosIcms.setLayout(pnlTributosIcmsLayout);
        pnlTributosIcmsLayout.setHorizontalGroup(
            pnlTributosIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTributosIcmsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTributosIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane3)
                    .addGroup(pnlTributosIcmsLayout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(18, 18, 18)
                        .addComponent(cboIcms, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel45)
                        .addGap(18, 18, 18)
                        .addComponent(cboOrigem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane3)
                .addContainerGap())
        );

        jTabTributos.addTab("ICMS", pnlTributosIcms);

        jLabel107.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel107.setText("Situação Tributária");

        cboIpi.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboIpi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboIpiActionPerformed(evt);
            }
        });

        txtIpiCodigoEnquadramento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIpiCodigoEnquadramento.setText("cEnq");
        txtIpiCodigoEnquadramento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtIpiCodigoEnquadramentoFocusLost(evt);
            }
        });

        jLabel108.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel108.setText("Código de enquadramento");

        jLabel109.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel109.setText("CNPJ do Produtor");

        txtIpiCnpjProdutor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIpiCnpjProdutor.setText("CNPJProd");
        txtIpiCnpjProdutor.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIpiCnpjProdutor.setName("cnpj"); // NOI18N
        txtIpiCnpjProdutor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtIpiCnpjProdutorFocusGained(evt);
            }
        });
        txtIpiCnpjProdutor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtIpiCnpjProdutorKeyReleased(evt);
            }
        });

        cboIpiTipoCalculo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboIpiTipoCalculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboIpiTipoCalculoActionPerformed(evt);
            }
        });

        jLabel110.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel110.setText("Tipo de Cálculo");

        txtIpiAliquota.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIpiAliquota.setText("pIPI");
        txtIpiAliquota.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIpiAliquota.setName("decimal"); // NOI18N
        txtIpiAliquota.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtIpiAliquotaFocusLost(evt);
            }
        });
        txtIpiAliquota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtIpiAliquotaKeyReleased(evt);
            }
        });

        jLabel111.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel111.setText("Alíquota (percentual)");

        jLabel112.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel112.setText("Valor por unidade");

        txtIpiValorUnidadeTributavel.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIpiValorUnidadeTributavel.setText("vUnid");
        txtIpiValorUnidadeTributavel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIpiValorUnidadeTributavel.setName("decimal"); // NOI18N
        txtIpiValorUnidadeTributavel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtIpiValorUnidadeTributavelFocusLost(evt);
            }
        });
        txtIpiValorUnidadeTributavel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtIpiValorUnidadeTributavelKeyReleased(evt);
            }
        });

        txtIpiValorBc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIpiValorBc.setText("vBC");
        txtIpiValorBc.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIpiValorBc.setName("decimal"); // NOI18N
        txtIpiValorBc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtIpiValorBcKeyReleased(evt);
            }
        });

        jLabel113.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel113.setText("Valor da Base de Cálculo");

        txtIpiQuantidadeTotalUnidadePadrao.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIpiQuantidadeTotalUnidadePadrao.setText("qUnid");
        txtIpiQuantidadeTotalUnidadePadrao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIpiQuantidadeTotalUnidadePadrao.setName("decimal"); // NOI18N
        txtIpiQuantidadeTotalUnidadePadrao.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtIpiQuantidadeTotalUnidadePadraoFocusLost(evt);
            }
        });
        txtIpiQuantidadeTotalUnidadePadrao.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtIpiQuantidadeTotalUnidadePadraoKeyReleased(evt);
            }
        });

        jLabel114.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel114.setText("Quantidade Total na Unidade Padrão");

        txtIpiValor.setEditable(false);
        txtIpiValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIpiValor.setText("vIPI");
        txtIpiValor.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIpiValor.setName("decimal"); // NOI18N

        jLabel115.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel115.setText("Valor do IPI");

        javax.swing.GroupLayout pnlTributosIPILayout = new javax.swing.GroupLayout(pnlTributosIPI);
        pnlTributosIPI.setLayout(pnlTributosIPILayout);
        pnlTributosIPILayout.setHorizontalGroup(
            pnlTributosIPILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTributosIPILayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTributosIPILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTributosIPILayout.createSequentialGroup()
                        .addComponent(jLabel107)
                        .addGap(18, 18, 18)
                        .addComponent(cboIpi, 0, 1107, Short.MAX_VALUE))
                    .addGroup(pnlTributosIPILayout.createSequentialGroup()
                        .addGroup(pnlTributosIPILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlTributosIPILayout.createSequentialGroup()
                                .addComponent(jLabel115)
                                .addGap(18, 18, 18)
                                .addComponent(txtIpiValor, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlTributosIPILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(pnlTributosIPILayout.createSequentialGroup()
                                    .addComponent(jLabel108)
                                    .addGap(18, 18, 18)
                                    .addComponent(txtIpiCodigoEnquadramento, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(pnlTributosIPILayout.createSequentialGroup()
                                    .addComponent(jLabel109)
                                    .addGap(18, 18, 18)
                                    .addComponent(txtIpiCnpjProdutor, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(pnlTributosIPILayout.createSequentialGroup()
                                    .addComponent(jLabel110)
                                    .addGap(18, 18, 18)
                                    .addComponent(cboIpiTipoCalculo, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(pnlTributosIPILayout.createSequentialGroup()
                                    .addComponent(jLabel113)
                                    .addGap(18, 18, 18)
                                    .addComponent(txtIpiValorBc, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jLabel111)
                                    .addGap(18, 18, 18)
                                    .addComponent(txtIpiAliquota, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(pnlTributosIPILayout.createSequentialGroup()
                                    .addComponent(jLabel114)
                                    .addGap(18, 18, 18)
                                    .addComponent(txtIpiQuantidadeTotalUnidadePadrao, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jLabel112)
                                    .addGap(18, 18, 18)
                                    .addComponent(txtIpiValorUnidadeTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlTributosIPILayout.setVerticalGroup(
            pnlTributosIPILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTributosIPILayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTributosIPILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboIpi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel107))
                .addGap(18, 18, 18)
                .addGroup(pnlTributosIPILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel108)
                    .addComponent(txtIpiCodigoEnquadramento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlTributosIPILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel109)
                    .addComponent(txtIpiCnpjProdutor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlTributosIPILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboIpiTipoCalculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel110))
                .addGap(18, 18, 18)
                .addGroup(pnlTributosIPILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIpiValorBc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel113)
                    .addComponent(txtIpiAliquota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel111))
                .addGap(18, 18, 18)
                .addGroup(pnlTributosIPILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIpiQuantidadeTotalUnidadePadrao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel114)
                    .addComponent(txtIpiValorUnidadeTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel112))
                .addGap(18, 18, 18)
                .addGroup(pnlTributosIPILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIpiValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel115))
                .addContainerGap(193, Short.MAX_VALUE))
        );

        jTabTributos.addTab("IPI", pnlTributosIPI);

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 128, Short.MAX_VALUE)
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 128, Short.MAX_VALUE)
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

        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel17.setText("Código ANP");

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel18.setText("CODIF");

        txtCombustivelCodif.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCombustivelCodif.setToolTipText("Preencha livremente ou o sistema preencherá com o Id");

        txtCombustivelDescricaoAnp.setEditable(false);
        txtCombustivelDescricaoAnp.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCombustivelDescricaoAnp.setToolTipText("");
        txtCombustivelDescricaoAnp.setFocusable(false);

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel19.setText("Descrição");

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel20.setText("Quantidade faturada em temperatura ambiente");

        txtCombustivelQuantidade.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCombustivelQuantidade.setText("0,0000");
        txtCombustivelQuantidade.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCombustivelQuantidade.setName("decimal(4)"); // NOI18N
        txtCombustivelQuantidade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCombustivelQuantidadeKeyReleased(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel22.setText("UF de Consumo");

        cboCombustivelUf.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtCombustivelCodigoAnp.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCombustivelCodigoAnp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCombustivelCodigoAnpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(18, 18, 18)
                        .addComponent(txtCombustivelCodigoAnp, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel19)
                        .addGap(18, 18, 18)
                        .addComponent(txtCombustivelDescricaoAnp))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addGap(18, 18, 18)
                                .addComponent(txtCombustivelCodif, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel20)
                                .addGap(18, 18, 18)
                                .addComponent(txtCombustivelQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addGap(18, 18, 18)
                                .addComponent(cboCombustivelUf, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 656, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel19)
                    .addComponent(txtCombustivelDescricaoAnp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCombustivelCodigoAnp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtCombustivelCodif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(txtCombustivelQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(cboCombustivelUf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlCombustivelCide.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel23.setText("Base de Cálculo");

        txtCombustivelBc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCombustivelBc.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCombustivelBc.setName("decimal"); // NOI18N

        jLabel106.setBackground(new java.awt.Color(122, 138, 153));
        jLabel106.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel106.setForeground(java.awt.Color.white);
        jLabel106.setText("CIDE");
        jLabel106.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel106.setOpaque(true);

        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel33.setText("Alíquota");

        txtCombustivelAliquota.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCombustivelAliquota.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCombustivelAliquota.setName("decimal"); // NOI18N

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel34.setText("Valor");

        txtCombustivelValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCombustivelValor.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCombustivelValor.setName("decimal"); // NOI18N

        javax.swing.GroupLayout pnlCombustivelCideLayout = new javax.swing.GroupLayout(pnlCombustivelCide);
        pnlCombustivelCide.setLayout(pnlCombustivelCideLayout);
        pnlCombustivelCideLayout.setHorizontalGroup(
            pnlCombustivelCideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel106, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlCombustivelCideLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCombustivelCideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCombustivelCideLayout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addGap(18, 18, 18)
                        .addComponent(txtCombustivelBc, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel33)
                        .addGap(18, 18, 18)
                        .addComponent(txtCombustivelAliquota, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlCombustivelCideLayout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addGap(18, 18, 18)
                        .addComponent(txtCombustivelValor, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlCombustivelCideLayout.setVerticalGroup(
            pnlCombustivelCideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCombustivelCideLayout.createSequentialGroup()
                .addComponent(jLabel106)
                .addGap(18, 18, 18)
                .addGroup(pnlCombustivelCideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(txtCombustivelBc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33)
                    .addComponent(txtCombustivelAliquota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlCombustivelCideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(txtCombustivelValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlCombustivelCide, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlCombustivelCide, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(304, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Combustível", jPanel6);

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

    private void txtCombustivelQuantidadeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCombustivelQuantidadeKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCombustivelQuantidadeKeyReleased

    private void txtCombustivelCodigoAnpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCombustivelCodigoAnpActionPerformed
        buscarAnp();
    }//GEN-LAST:event_txtCombustivelCodigoAnpActionPerformed

    private void txtAliquotaCofinsStReaisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsStReaisKeyReleased
        calcularCofinsSt();
    }//GEN-LAST:event_txtAliquotaCofinsStReaisKeyReleased

    private void txtAliquotaCofinsStReaisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsStReaisFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaCofinsStReaisFocusLost

    private void cboCofinsStTipoCalculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCofinsStTipoCalculoActionPerformed
        chavearCofinsStTipoCalculo();
    }//GEN-LAST:event_cboCofinsStTipoCalculoActionPerformed

    private void txtQuantidadeVendidaCofinsStKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeVendidaCofinsStKeyReleased
        calcularCofinsSt();
    }//GEN-LAST:event_txtQuantidadeVendidaCofinsStKeyReleased

    private void txtQuantidadeVendidaCofinsStFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtQuantidadeVendidaCofinsStFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQuantidadeVendidaCofinsStFocusLost

    private void txtAliquotaCofinsStKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsStKeyReleased
        calcularCofinsSt();
    }//GEN-LAST:event_txtAliquotaCofinsStKeyReleased

    private void txtAliquotaCofinsStFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsStFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaCofinsStFocusLost

    private void txtValorBcCofinsStKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorBcCofinsStKeyReleased
        calcularCofinsSt();
    }//GEN-LAST:event_txtValorBcCofinsStKeyReleased

    private void cboCofinsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCofinsActionPerformed
        chavearCofins();
    }//GEN-LAST:event_cboCofinsActionPerformed

    private void txtAliquotaCofinsReaisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsReaisKeyReleased
        calcularCofins();
    }//GEN-LAST:event_txtAliquotaCofinsReaisKeyReleased

    private void txtAliquotaCofinsReaisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsReaisFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaCofinsReaisFocusLost

    private void txtQuantidadeVendidaCofinsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeVendidaCofinsKeyReleased
        calcularCofins();
    }//GEN-LAST:event_txtQuantidadeVendidaCofinsKeyReleased

    private void txtQuantidadeVendidaCofinsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtQuantidadeVendidaCofinsFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQuantidadeVendidaCofinsFocusLost

    private void cboCofinsTipoCalculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCofinsTipoCalculoActionPerformed
        chavearCofinsTipoCalculo();
    }//GEN-LAST:event_cboCofinsTipoCalculoActionPerformed

    private void txtAliquotaCofinsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsKeyReleased
        calcularCofins();
    }//GEN-LAST:event_txtAliquotaCofinsKeyReleased

    private void txtAliquotaCofinsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaCofinsFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaCofinsFocusLost

    private void txtValorBcCofinsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorBcCofinsKeyReleased
        calcularCofins();
    }//GEN-LAST:event_txtValorBcCofinsKeyReleased

    private void txtAliquotaPisStReaisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaPisStReaisKeyReleased
        calcularPisSt();
    }//GEN-LAST:event_txtAliquotaPisStReaisKeyReleased

    private void txtAliquotaPisStReaisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaPisStReaisFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaPisStReaisFocusLost

    private void cboPisStTipoCalculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPisStTipoCalculoActionPerformed
        chavearPisStTipoCalculo();
    }//GEN-LAST:event_cboPisStTipoCalculoActionPerformed

    private void txtQuantidadeVendidaPisStKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeVendidaPisStKeyReleased
        calcularPisSt();
    }//GEN-LAST:event_txtQuantidadeVendidaPisStKeyReleased

    private void txtQuantidadeVendidaPisStFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtQuantidadeVendidaPisStFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQuantidadeVendidaPisStFocusLost

    private void txtAliquotaPisStKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaPisStKeyReleased
        calcularPisSt();
    }//GEN-LAST:event_txtAliquotaPisStKeyReleased

    private void txtAliquotaPisStFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaPisStFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaPisStFocusLost

    private void txtValorBcPisStKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorBcPisStKeyReleased
        calcularPisSt();
    }//GEN-LAST:event_txtValorBcPisStKeyReleased

    private void cboPisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPisActionPerformed
        chavearPis();
    }//GEN-LAST:event_cboPisActionPerformed

    private void txtAliquotaPisReaisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaPisReaisKeyReleased
        calcularPis();
    }//GEN-LAST:event_txtAliquotaPisReaisKeyReleased

    private void txtAliquotaPisReaisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaPisReaisFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaPisReaisFocusLost

    private void txtQuantidadeVendidaPisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeVendidaPisKeyReleased
        calcularPis();
    }//GEN-LAST:event_txtQuantidadeVendidaPisKeyReleased

    private void txtQuantidadeVendidaPisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtQuantidadeVendidaPisFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQuantidadeVendidaPisFocusLost

    private void cboPisTipoCalculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPisTipoCalculoActionPerformed
        chavearPisTipoCalculo();
    }//GEN-LAST:event_cboPisTipoCalculoActionPerformed

    private void txtAliquotaPisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaPisKeyReleased
        calcularPis();
    }//GEN-LAST:event_txtAliquotaPisKeyReleased

    private void txtAliquotaPisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaPisFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaPisFocusLost

    private void txtValorBcPisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorBcPisKeyReleased
        calcularPis();
    }//GEN-LAST:event_txtValorBcPisKeyReleased

    private void txtIpiQuantidadeTotalUnidadePadraoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtIpiQuantidadeTotalUnidadePadraoKeyReleased
        calcularIpi();
    }//GEN-LAST:event_txtIpiQuantidadeTotalUnidadePadraoKeyReleased

    private void txtIpiQuantidadeTotalUnidadePadraoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIpiQuantidadeTotalUnidadePadraoFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIpiQuantidadeTotalUnidadePadraoFocusLost

    private void txtIpiValorBcKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtIpiValorBcKeyReleased
        calcularIpi();
    }//GEN-LAST:event_txtIpiValorBcKeyReleased

    private void txtIpiValorUnidadeTributavelKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtIpiValorUnidadeTributavelKeyReleased
        calcularIpi();
    }//GEN-LAST:event_txtIpiValorUnidadeTributavelKeyReleased

    private void txtIpiValorUnidadeTributavelFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIpiValorUnidadeTributavelFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIpiValorUnidadeTributavelFocusLost

    private void txtIpiAliquotaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtIpiAliquotaKeyReleased
        calcularIpi();
    }//GEN-LAST:event_txtIpiAliquotaKeyReleased

    private void txtIpiAliquotaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIpiAliquotaFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIpiAliquotaFocusLost

    private void cboIpiTipoCalculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboIpiTipoCalculoActionPerformed
        chavearIpiTipoCalculo();
    }//GEN-LAST:event_cboIpiTipoCalculoActionPerformed

    private void txtIpiCnpjProdutorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtIpiCnpjProdutorKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIpiCnpjProdutorKeyReleased

    private void txtIpiCnpjProdutorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIpiCnpjProdutorFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIpiCnpjProdutorFocusGained

    private void txtIpiCodigoEnquadramentoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIpiCodigoEnquadramentoFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIpiCodigoEnquadramentoFocusLost

    private void cboIpiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboIpiActionPerformed
        chavearIpi();
    }//GEN-LAST:event_cboIpiActionPerformed

    private void txtValorIcms1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtValorIcms1FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValorIcms1FocusLost

    private void txtAliquotaIcms1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaIcms1FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaIcms1FocusLost

    private void txtValorBcIcms1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtValorBcIcms1FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValorBcIcms1FocusLost

    private void txtValorIcmsStFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtValorIcmsStFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValorIcmsStFocusLost

    private void txtValorIcmsDesoneradoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtValorIcmsDesoneradoFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValorIcmsDesoneradoFocusLost

    private void txtValorIcmsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtValorIcmsFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValorIcmsFocusLost

    private void txtValorBcIcmsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtValorBcIcmsFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValorBcIcmsFocusLost

    private void txtAliquotaIcmsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaIcmsFocusLost

    }//GEN-LAST:event_txtAliquotaIcmsFocusLost

    private void txtAliquotaSuportadaConsumidorFinalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaSuportadaConsumidorFinalFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAliquotaSuportadaConsumidorFinalFocusLost

    private void cboOrigemFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboOrigemFocusLost

    }//GEN-LAST:event_cboOrigemFocusLost

    private void cboIcmsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboIcmsActionPerformed
        chavearIcms();
        chavearIcmsSt();
    }//GEN-LAST:event_cboIcmsActionPerformed

    private void cboIcmsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboIcmsFocusLost

    }//GEN-LAST:event_cboIcmsFocusLost

    private void txtIcmsValorDiferidoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIcmsValorDiferidoFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIcmsValorDiferidoFocusLost

    private void txtIcmsValorOperacaoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIcmsValorOperacaoFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIcmsValorOperacaoFocusLost

    private void txtAliquotaIcmsStKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliquotaIcmsStKeyReleased
        calcularIcmsSt();
    }//GEN-LAST:event_txtAliquotaIcmsStKeyReleased

    private void txtBcIcmsStKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBcIcmsStKeyReleased
        calcularIcmsSt();
    }//GEN-LAST:event_txtBcIcmsStKeyReleased

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
    private javax.swing.JComboBox<String> cboCombustivelUf;
    private javax.swing.JComboBox<Object> cboIcms;
    private javax.swing.JComboBox<Object> cboIcmsStDevidoOperacaoUf;
    private javax.swing.JComboBox<Object> cboIpi;
    private javax.swing.JComboBox<Object> cboIpiTipoCalculo;
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel115;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel120;
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
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
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
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JTabbedPane jTabTributos;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JPanel pnlCombustivelCide;
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
    private javax.swing.JPanel pnlTributosIPI;
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
    private javax.swing.JFormattedTextField txtCombustivelAliquota;
    private javax.swing.JFormattedTextField txtCombustivelBc;
    private javax.swing.JTextField txtCombustivelCodif;
    private javax.swing.JTextField txtCombustivelCodigoAnp;
    private javax.swing.JTextField txtCombustivelDescricaoAnp;
    private javax.swing.JFormattedTextField txtCombustivelQuantidade;
    private javax.swing.JFormattedTextField txtCombustivelValor;
    private javax.swing.JFormattedTextField txtDesconto;
    private javax.swing.JTextField txtDescricao;
    private javax.swing.JTextField txtEan;
    private javax.swing.JTextField txtEanTributavel;
    private javax.swing.JTextField txtExTipi;
    private javax.swing.JFormattedTextField txtFrete;
    private javax.swing.JFormattedTextField txtIcmsPercentualDiferimento;
    private javax.swing.JFormattedTextField txtIcmsStValorBcUfDestino;
    private javax.swing.JFormattedTextField txtIcmsStValorUfDestino;
    private javax.swing.JFormattedTextField txtIcmsValorDiferido;
    private javax.swing.JFormattedTextField txtIcmsValorOperacao;
    private javax.swing.JFormattedTextField txtIpiAliquota;
    private javax.swing.JFormattedTextField txtIpiCnpjProdutor;
    private javax.swing.JTextField txtIpiCodigoEnquadramento;
    private javax.swing.JFormattedTextField txtIpiQuantidadeTotalUnidadePadrao;
    private javax.swing.JFormattedTextField txtIpiValor;
    private javax.swing.JFormattedTextField txtIpiValorBc;
    private javax.swing.JFormattedTextField txtIpiValorUnidadeTributavel;
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
