/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoSaida;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.KeyStroke;
import model.mysql.bean.fiscal.Cest;
import model.mysql.bean.fiscal.Cfop;
import model.mysql.bean.fiscal.Icms;
import model.mysql.bean.fiscal.Ncm;
import model.mysql.bean.fiscal.ProdutoOrigem;
import model.mysql.bean.fiscal.UnidadeComercial;
import model.mysql.bean.fiscal.nfe.ModalidadeBcIcms;
import model.mysql.bean.fiscal.nfe.ModalidadeBcIcmsSt;
import model.mysql.bean.fiscal.nfe.MotivoDesoneracao;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.fiscal.CfopDAO;
import model.mysql.dao.fiscal.IcmsDAO;
import model.mysql.dao.fiscal.NcmDAO;
import model.mysql.dao.fiscal.ProdutoOrigemDAO;
import model.mysql.dao.fiscal.UnidadeComercialDAO;
import model.mysql.dao.fiscal.nfe.ModalidadeBcIcmsDAO;
import model.mysql.dao.fiscal.nfe.ModalidadeBcIcmsStDAO;
import model.mysql.dao.fiscal.nfe.MotivoDesoneracaoDAO;
import model.mysql.dao.principal.MovimentoFisicoDAO;
import model.mysql.dao.principal.VendaDAO;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.Decimal;
import util.JSwing;
import view.produto.CestPesquisaView;
import view.produto.NcmPesquisaView;

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

        carregarCfop();
        carregarUnidadeComercial();
        carregarUnidadeTributavel();
        
        carregarOrigem();
        carregarIcms();
        carregarModalidadeBcIcms();
        carregarMotivoDesoneracao();
        carregarModalidadeBcIcmsSt();
        
        
        
        
        carregarDados();
        

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
        //Fim Principal---------------------------------------------------------
        
        
        
        //Tributos--------------------------------------------------------------
        txtTotalTributos.setText(Decimal.toString(movimentoFisico.getTotalTributos()));
        cboOrigem.setSelectedItem(movimentoFisico.getOrigem());
        cboIcms.setSelectedItem(movimentoFisico.getIcms());
        
        cboModalidadeBcIcms.setSelectedItem(movimentoFisico.getModalidadeBcIcms());
        txtPercentualReducaoBcIcms.setText(Decimal.toString(movimentoFisico.getPercentualReducaoBcIcms()));
        txtBcIcms.setText(Decimal.toString(movimentoFisico.getValorBcIcms()));
        txtAliquotaIcms.setText(Decimal.toString(movimentoFisico.getAliquotaIcms()));
        txtValorIcms.setText(Decimal.toString(movimentoFisico.getValorIcms()));
        txtPercentualBcOperacaoPropria.setText(Decimal.toString(movimentoFisico.getPercentualBcOperacaoPropria()));
        txtValorIcmsDesonerado.setText(Decimal.toString(movimentoFisico.getValorIcmsDesonerado()));
        cboMotivoDesoneracao.setSelectedItem(movimentoFisico.getMotivoDesoneracao());
        
        cboModalidadeBcIcmsSt.setSelectedItem(movimentoFisico.getModalidadeBcIcmsSt());
        txtPercentualReducaoBcIcmsSt.setText(Decimal.toString(movimentoFisico.getPercentualReducaoBcIcmsSt()));
        txtPercentualMargemValorAdicionadoIcmsSt.setText(Decimal.toString(movimentoFisico.getPercentualMargemValorAdicionadoIcmsSt()));
        txtBcIcmsSt.setText(Decimal.toString(movimentoFisico.getValorBcIcmsSt()));
        txtAliquotaIcmsSt.setText(Decimal.toString(movimentoFisico.getAliquotaIcmsSt()));
        txtValorIcmsSt.setText(Decimal.toString(movimentoFisico.getValorIcmsSt()));
        
        //Fim Tributos----------------------------------------------------------
        
        
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
        movimentoFisico.setTotalTributos(Decimal.fromString(txtTotalTributos.getText()));
        movimentoFisico.setOrigem((ProdutoOrigem) cboOrigem.getSelectedItem());
        movimentoFisico.setIcms((Icms) cboIcms.getSelectedItem());
        
        movimentoFisico.setModalidadeBcIcms((ModalidadeBcIcms) cboModalidadeBcIcms.getSelectedItem());
        movimentoFisico.setPercentualReducaoBcIcms(Decimal.fromString(txtPercentualReducaoBcIcms.getText()));
        movimentoFisico.setValorBcIcms(Decimal.fromString(txtBcIcms.getText()));
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
        
        //Fim Tributos----------------------------------------------------------
        
        
        movimentoFisico = movimentoFisicoDAO.save(movimentoFisico);
        
        documento.addMovimentoFisico(movimentoFisico);
        
        vendaDAO.save(documento);
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
        jPanel6 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        txtTotalTributos = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel8 = new javax.swing.JPanel();
        cboIcms = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        cboOrigem = new javax.swing.JComboBox<>();
        pnlIcms2 = new javax.swing.JPanel();
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
        txtBcIcms = new javax.swing.JFormattedTextField();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Dados do Item");
        setResizable(false);

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

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Unidade");

        cboUnidadeComercial.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("Quantidade");

        txtQuantidadeComercial.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQuantidadeComercial.setText("0,00");
        txtQuantidadeComercial.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtQuantidadeComercial.setName("decimal"); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Valor Unitário");

        txtValorUnitarioComercial.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorUnitarioComercial.setText("0,00");
        txtValorUnitarioComercial.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorUnitarioComercial.setName("decimal"); // NOI18N

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel21.setText("EAN");

        txtEan.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel37.setBackground(javax.swing.UIManager.getDefaults().getColor("InternalFrame.activeTitleBackground"));
        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel37.setForeground(java.awt.Color.white);
        jLabel37.setText("Comercial");
        jLabel37.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel37.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel37)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(cboUnidadeComercial, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(txtQuantidadeComercial, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(txtValorUnitarioComercial, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel21)
                .addGap(18, 18, 18)
                .addComponent(txtEan, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(89, 89, 89))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cboUnidadeComercial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtValorUnitarioComercial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txtQuantidadeComercial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(txtEan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel38.setBackground(javax.swing.UIManager.getDefaults().getColor("InternalFrame.activeTitleBackground"));
        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel38.setForeground(java.awt.Color.white);
        jLabel38.setText("Tributável");
        jLabel38.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel38.setOpaque(true);

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel13.setText("Unidade");

        cboUnidadeTributavel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel12.setText("Quantidade");

        txtQuantidadeTributavel.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQuantidadeTributavel.setText("0,00");
        txtQuantidadeTributavel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtQuantidadeTributavel.setName("decimal"); // NOI18N

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel10.setText("Valor Unitário");

        txtValorUnitarioTributavel.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorUnitarioTributavel.setText("0,00");
        txtValorUnitarioTributavel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorUnitarioTributavel.setName("decimal"); // NOI18N

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel24.setText("EAN");

        txtEanTributavel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel38)
                .addGap(18, 18, 18)
                .addComponent(jLabel13)
                .addGap(18, 18, 18)
                .addComponent(cboUnidadeTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addComponent(txtQuantidadeTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(txtValorUnitarioTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel24)
                .addGap(18, 18, 18)
                .addComponent(txtEanTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(cboUnidadeTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtValorUnitarioTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(txtQuantidadeTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(txtEanTributavel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel15.setText("Outras Despesas Acessórias (Acréscimo)");

        txtAcrescimo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAcrescimo.setText("0,00");
        txtAcrescimo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAcrescimo.setName("decimal"); // NOI18N

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("Desconto");

        txtDesconto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDesconto.setText("0,00");
        txtDesconto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDesconto.setName("decimal"); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setText("Frete");

        txtFrete.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFrete.setText("0,00");
        txtFrete.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtFrete.setName("decimal"); // NOI18N

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel14.setText("Seguro");

        txtSeguro.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSeguro.setText("0,00");
        txtSeguro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtSeguro.setName("decimal"); // NOI18N

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel26.setText("Valor Total Bruto");

        txtValorTotalBruto.setEditable(false);
        txtValorTotalBruto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorTotalBruto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        chkValorCompoeTotal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkValorCompoeTotal.setText("Valor Total Bruto compõe o Valor Total dos Produtos e Serviços");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkValorCompoeTotal)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(18, 18, 18)
                        .addComponent(txtAcrescimo, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(txtDesconto, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11)
                        .addGap(18, 18, 18)
                        .addComponent(txtFrete, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel14)
                        .addGap(18, 18, 18)
                        .addComponent(txtSeguro, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel26)
                        .addGap(18, 18, 18)
                        .addComponent(txtValorTotalBruto, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(111, Short.MAX_VALUE))
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
                    .addComponent(jLabel14)
                    .addComponent(jLabel26)
                    .addComponent(txtValorTotalBruto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(chkValorCompoeTotal)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel27.setText("Pedido de Compra");

        txtPedidoCompra.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel28.setText("Nº Item do Pedido de Compra");

        txtItemPedidoCompra.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtItemPedidoCompra.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
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
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Principal", pnlPrincipal);

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel29.setText("Total dos Tributos");

        txtTotalTributos.setEditable(false);
        txtTotalTributos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTotalTributos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel30.setText("Valor aproximado total de tributos federais, estaduais e municipais conforme disposto na Lei nº 12.741/12");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel29)
                .addGap(18, 18, 18)
                .addComponent(txtTotalTributos, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel30)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(txtTotalTributos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addContainerGap())
        );

        jTabbedPane2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

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

        pnlIcms2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlIcms2.setPreferredSize(new java.awt.Dimension(610, 234));

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

        txtBcIcms.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBcIcms.setText("vBC");
        txtBcIcms.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtBcIcms.setName("decimal"); // NOI18N
        txtBcIcms.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBcIcmsFocusLost(evt);
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

        javax.swing.GroupLayout pnlIcms2Layout = new javax.swing.GroupLayout(pnlIcms2);
        pnlIcms2.setLayout(pnlIcms2Layout);
        pnlIcms2Layout.setHorizontalGroup(
            pnlIcms2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel52, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlIcms2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlIcms2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcms2Layout.createSequentialGroup()
                        .addComponent(jLabel49)
                        .addGap(18, 18, 18)
                        .addComponent(cboModalidadeBcIcms, 0, 264, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlIcms2Layout.createSequentialGroup()
                        .addComponent(jLabel53)
                        .addGap(18, 18, 18)
                        .addComponent(cboMotivoDesoneracao, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlIcms2Layout.createSequentialGroup()
                        .addGroup(pnlIcms2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlIcms2Layout.createSequentialGroup()
                                .addComponent(jLabel51)
                                .addGap(18, 18, 18)
                                .addComponent(txtAliquotaIcms, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel61)
                                .addGap(18, 18, 18)
                                .addComponent(txtValorIcms, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlIcms2Layout.createSequentialGroup()
                                .addComponent(jLabel50)
                                .addGap(18, 18, 18)
                                .addComponent(txtPercentualReducaoBcIcms, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel60)
                                .addGap(18, 18, 18)
                                .addComponent(txtBcIcms, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlIcms2Layout.createSequentialGroup()
                        .addComponent(jLabel54)
                        .addGap(18, 18, 18)
                        .addComponent(txtPercentualBcOperacaoPropria, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel62)
                        .addGap(18, 18, 18)
                        .addComponent(txtValorIcmsDesonerado)))
                .addContainerGap())
        );
        pnlIcms2Layout.setVerticalGroup(
            pnlIcms2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIcms2Layout.createSequentialGroup()
                .addComponent(jLabel52)
                .addGap(18, 18, 18)
                .addGroup(pnlIcms2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboModalidadeBcIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49))
                .addGap(18, 18, 18)
                .addGroup(pnlIcms2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcms2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtBcIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel60))
                    .addGroup(pnlIcms2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPercentualReducaoBcIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel50)))
                .addGap(18, 18, 18)
                .addGroup(pnlIcms2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcms2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtValorIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel61))
                    .addGroup(pnlIcms2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAliquotaIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel51)))
                .addGap(18, 18, 18)
                .addGroup(pnlIcms2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIcms2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtValorIcmsDesonerado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel62))
                    .addGroup(pnlIcms2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPercentualBcOperacaoPropria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel54)))
                .addGap(18, 18, 18)
                .addGroup(pnlIcms2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
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
                                .addComponent(txtBcIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel58)
                    .addComponent(txtPercentualMargemValorAdicionadoIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtBcIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel63)))
                .addGap(18, 18, 18)
                .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAliquotaIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel57)
                    .addGroup(pnlIcmsStLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtValorIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel64)))
                .addContainerGap(52, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(18, 18, 18)
                        .addComponent(cboIcms, 0, 999, Short.MAX_VALUE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel45)
                        .addGap(18, 18, 18)
                        .addComponent(cboOrigem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(pnlIcms2, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(pnlIcmsSt, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(cboOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlIcms2, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                    .addComponent(pnlIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(138, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("ICMS", jPanel8);

        javax.swing.GroupLayout pnlTributosLayout = new javax.swing.GroupLayout(pnlTributos);
        pnlTributos.setLayout(pnlTributosLayout);
        pnlTributosLayout.setHorizontalGroup(
            pnlTributosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTributosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTributosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane2))
                .addContainerGap())
        );
        pnlTributosLayout.setVerticalGroup(
            pnlTributosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTributosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane2)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Tributos", pnlTributos);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
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
                .addComponent(jTabbedPane1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar)
                    .addComponent(btnOk))
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
        //exibirPaineisIcms();
    }//GEN-LAST:event_cboIcmsActionPerformed

    private void cboOrigemFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboOrigemFocusLost
    }//GEN-LAST:event_cboOrigemFocusLost

    private void txtAliquotaIcmsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAliquotaIcmsFocusLost
    }//GEN-LAST:event_txtAliquotaIcmsFocusLost

    private void txtBcIcmsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBcIcmsFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBcIcmsFocusLost

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
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnPesquisarCestNfe;
    private javax.swing.JButton btnPesquisarNcmNfe;
    private javax.swing.JComboBox<Object> cboCfop;
    private javax.swing.JComboBox<Object> cboIcms;
    private javax.swing.JComboBox<Object> cboModalidadeBcIcms;
    private javax.swing.JComboBox<Object> cboModalidadeBcIcmsSt;
    private javax.swing.JComboBox<Object> cboMotivoDesoneracao;
    private javax.swing.JComboBox<Object> cboOrigem;
    private javax.swing.JComboBox<Object> cboUnidadeComercial;
    private javax.swing.JComboBox<Object> cboUnidadeTributavel;
    private javax.swing.JCheckBox chkValorCompoeTotal;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JPanel pnlIcms2;
    private javax.swing.JPanel pnlIcmsSt;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JPanel pnlTributos;
    private javax.swing.JFormattedTextField txtAcrescimo;
    private javax.swing.JFormattedTextField txtAliquotaIcms;
    private javax.swing.JFormattedTextField txtAliquotaIcmsSt;
    private javax.swing.JFormattedTextField txtBcIcms;
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
    private javax.swing.JTextField txtPedidoCompra;
    private javax.swing.JFormattedTextField txtPercentualBcOperacaoPropria;
    private javax.swing.JFormattedTextField txtPercentualMargemValorAdicionadoIcmsSt;
    private javax.swing.JFormattedTextField txtPercentualReducaoBcIcms;
    private javax.swing.JFormattedTextField txtPercentualReducaoBcIcmsSt;
    private javax.swing.JFormattedTextField txtQuantidadeComercial;
    private javax.swing.JFormattedTextField txtQuantidadeTributavel;
    private javax.swing.JFormattedTextField txtSeguro;
    private javax.swing.JTextField txtTotalTributos;
    private javax.swing.JFormattedTextField txtValorIcms;
    private javax.swing.JFormattedTextField txtValorIcmsDesonerado;
    private javax.swing.JFormattedTextField txtValorIcmsSt;
    private javax.swing.JTextField txtValorTotalBruto;
    private javax.swing.JFormattedTextField txtValorUnitarioComercial;
    private javax.swing.JFormattedTextField txtValorUnitarioTributavel;
    // End of variables declaration//GEN-END:variables
}
