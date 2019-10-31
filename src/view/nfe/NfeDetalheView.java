/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.nfe;

import br.com.swconsultoria.nfe.dom.enuns.AmbienteEnum;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import model.jtable.documento.NfeDocumentosReferenciadosJTableModel;
import model.mysql.bean.endereco.Cidade;
import model.mysql.bean.endereco.Endereco;
import model.mysql.bean.fiscal.nfe.ConsumidorFinal;
import model.mysql.bean.fiscal.nfe.DestinoOperacao;
import model.mysql.bean.fiscal.nfe.DocumentoReferenciado;
import model.mysql.bean.fiscal.nfe.FinalidadeEmissao;
import model.mysql.bean.fiscal.nfe.ModalidadeFrete;
import model.mysql.bean.fiscal.nfe.NaturezaOperacao;
import model.mysql.bean.fiscal.nfe.RegimeTributario;
import model.mysql.bean.fiscal.nfe.TipoAtendimento;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.endereco.CidadeDAO;
import model.mysql.dao.endereco.EnderecoDAO;
import model.mysql.dao.fiscal.nfe.ConsumidorFinalDAO;
import model.mysql.dao.fiscal.nfe.DestinoOperacaoDAO;
import model.mysql.dao.fiscal.nfe.DocumentoReferenciadoDAO;
import model.mysql.dao.fiscal.nfe.FinalidadeEmissaoDAO;
import model.mysql.dao.fiscal.nfe.ModalidadeFreteDAO;
import model.mysql.dao.fiscal.nfe.NaturezaOperacaoDAO;
import model.mysql.dao.fiscal.nfe.RegimeTributarioDAO;
import model.mysql.dao.fiscal.nfe.TipoAtendimentoDAO;
import model.nosql.NfeStatusEnum;
import nfe.MontarXml;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.NFE_PATH;
import printing.DanfePrint;
import util.Cor;
import util.DateTime;
import util.Decimal;
import util.FiscalUtil;
import util.JSwing;
import util.Numero;
import util.Texto;
import view.endereco.EnderecoPesquisaView;

/**
 *
 * @author ivand
 *
 * ESTE FOI CRIADO COMO JDIALOG DESDE O INÍCIO - USAR ESTE COMO REFERÊNCIA PARA
 * OS DEMAIS
 *
 *
 */
public class NfeDetalheView extends javax.swing.JDialog {

    Venda documento;
    DocumentoReferenciadoDAO documentoReferenciadoDAO = new DocumentoReferenciadoDAO();
    NfeDocumentosReferenciadosJTableModel nfeDocumentosReferenciadosJTableModel = new NfeDocumentosReferenciadosJTableModel();
    boolean salvar = false;

    private NfeDetalheView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

    }

    public NfeDetalheView(Venda documento) {
        super(MAIN_VIEW, true);
        initComponents();
        JSwing.startComponentsBehavior(this);

        definirAtalhos();

        this.documento = documento;
        
        if(Ouroboros.NFE_TIPO_AMBIENTE.equals(AmbienteEnum.PRODUCAO)) {
            btnEmitir.setForeground(Cor.VERMELHO);
        }

        pnlTransportador.setVisible(false);

        carregarRegimeTributario();
        carregarNaturezaOperacao();
        carregarTipoAtendimento();
        carregarConsumidorFinal();
        carregarDestinoOperacao();
        carregarFinalidadeEmissao();
        carregarModalidadeFrete();
        
        carregarDocumentosReferenciados();

        carregarDados();
        
        formatarDocumentosReferenciados();

        this.setLocationRelativeTo(this); //centralizar
        this.setVisible(true);
    }

    private void definirAtalhos() {
        InputMap im = rootPane.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = rootPane.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "fechar");
        am.put("fechar", new FormKeyStroke("ESC"));
    }

    protected class FormKeyStroke extends AbstractAction {

        private final String key;

        public FormKeyStroke(String key) {
            this.key = key;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (key) {
                case "ESC":
                    dispose();
                    break;
            }
        }
    }

    public boolean getSalvar() {
        return salvar;
    }

    private void carregarDados() {

        //Principal-------------------------------------------------------------
        if (documento.getRegimeTributario() == null) {
            cboRegimeTributario.setSelectedItem(Ouroboros.NFE_REGIME_TRIBUTARIO);
        } else {
            cboRegimeTributario.setSelectedItem(documento.getRegimeTributario());
        }

        if (documento.getNaturezaOperacao() == null) {
            cboNaturezaOperacao.setSelectedItem(Ouroboros.NFE_NATUREZA_OPERACAO);
        } else {
            cboNaturezaOperacao.setSelectedItem(documento.getNaturezaOperacao());
        }

        if (documento.getDestinoOperacao() == null) {
            cboDestinoOperacao.setSelectedItem(Ouroboros.NFE_DESTINO_OPERACAO);
        } else {
            cboDestinoOperacao.setSelectedItem(documento.getDestinoOperacao());
        }

        if (documento.getTipoAtendimento() == null) {
            cboTipoAtendimento.setSelectedItem(Ouroboros.NFE_TIPO_ATENDIMENTO);
        } else {
            cboTipoAtendimento.setSelectedItem(documento.getTipoAtendimento());
        }

        if (documento.getFinalidadeEmissao() == null) {
            cboFinalidadeEmissao.setSelectedIndex(0);
        } else {
            cboFinalidadeEmissao.setSelectedItem(documento.getFinalidadeEmissao());
        }

        if (documento.getConsumidorFinal() == null) {
            cboConsumidorFinal.setSelectedItem(Ouroboros.NFE_CONSUMIDOR_FINAL);
        } else {
            cboConsumidorFinal.setSelectedItem(documento.getConsumidorFinal());
        }

        txtStatus.setText(documento.getStatusNfe().toString());
        txtStatus.setBackground(documento.getStatusNfe().getCor());
            
        txtNumero.setText(Numero.toString(documento.getNumeroNfe()));
        txtSerie.setText(Numero.toString(documento.getSerieNfe()));

        txtChaveAcesso.setText(documento.getChaveAcessoNfe());
        txtDataHoraEmissao.setText(DateTime.toString(documento.getDataHoraEmissaoNfe()));

        //Fim Principal-----------------------------------------------------
        //Totais------------------------------------------------------------
        txtBcIcms.setText(Decimal.toString(documento.getTotalBcIcms()));
        txtIcms.setText(Decimal.toString(documento.getTotalIcms()));
        txtBcIcmsSt.setText(Decimal.toString(documento.getTotalBcIcmsSt()));
        txtIcmsSt.setText(Decimal.toString(documento.getTotalIcmsSt()));

        txtProdutosServicos.setText(Decimal.toString(documento.getTotal()));
        txtFrete.setText(Decimal.toString(documento.getTotalFreteProdutos()));
        //txtIcmsPartilhaRemetente.setText(Decimal.toString(documento.getTotalIcmsPartilhaRemetente()));
        //txtIcmsPartilhaDestinatario.setText(Decimal.toString(documento.getTotalIcmsPartilhaDestinatario()));

        //txtIi.setText(Decimal.toString(documento.getTotalIi()));
        //txtIpi.setText(Decimal.toString(documento.getTotalIpi()));
        txtPis.setText(Decimal.toString(documento.getTotalPis()));
        txtCofins.setText(Decimal.toString(documento.getTotalCofins()));

        txtSeguro.setText(Decimal.toString(documento.getTotalSeguroProdutos()));
        txtDesconto.setText(Decimal.toString(documento.getTotalDescontoProdutos()));
        txtOutrasDespesas.setText(Decimal.toString(documento.getTotalOutros()));
        txtIcmsDesonerado.setText(Decimal.toString(documento.getTotalIcmsDesonerado()));

        //txtIcmsFcp.setText(Decimal.toString(documento.getTotalIcmsFcp));
        //txtIcmsStFcp.setText(Decimal.toString(documento.getTotalIcmsStFcp()));
        //txtIcmsStFcpRetido.setText(Decimal.toString(documento.getTotalIcmsStFcpRetido()));
        //txtIpiDevolvido.setText(Decimal.toString(documento.getTotalIpiDevolvido()));
        //txtIcmsFcpUfDestino.setText(Decimal.toString(documento.getTotalIcmsFcpUfDestino));
        txtTotal.setText(Decimal.toString(documento.getTotal()));

        //Fim Totais--------------------------------------------------------
        //Entrega-----------------------------------------------------------
        chkEntregaDiferente.setSelected(documento.isEntregaDiferente());
        chavearEntrega();

        txtEntregaCpfCnpj.setText(documento.getEntregaCpfOuCnpj());
        txtEntregaIe.setText(documento.getEntregaIe());
        txtEntregaNome.setText(documento.getEntregaNome());

        txtEntregaCep.setText(documento.getEntregaCep());
        txtEntregaEndereco.setText(documento.getEntregaEndereco());
        txtEntregaNumero.setText(documento.getEntregaNumero());
        txtEntregaComplemento.setText(documento.getEntregaComplemento());
        txtEntregaBairro.setText(documento.getEntregaBairro());
        txtEntregaCodigoMunicipio.setText(documento.getEntregaCodigoMunicipio());
        carregarMunicipio();

        txtEntregaTelefone.setText(documento.getEntregaTelefone());
        txtEntregaEmail.setText(documento.getEntregaEmail());
        //Fim Entrega-------------------------------------------------------

        //Transporte--------------------------------------------------------
        if (documento.getModalidadeFrete() == null) {
            cboModalidadeFrete.setSelectedIndex(5);
        } else {
            cboModalidadeFrete.setSelectedItem(documento.getModalidadeFrete());
        }
        //Fim Transporte----------------------------------------------------

        //Informações adicionais--------------------------------------------
        if (documento.getInformacoesAdicionaisFisco() == null) {
            txtInformacoesFisco.setText(Ouroboros.NFE_INFORMACOES_ADICIONAIS_FISCO);
        } else {
            txtInformacoesFisco.setText(documento.getInformacoesAdicionaisFisco());
        }

        if (documento.getInformacoesComplementaresContribuinte() == null) {
            txtInformacoesContribuinte.setText(Ouroboros.NFE_INFORMACOES_COMPLEMENTARES_CONTRIBUINTE);
        } else {
            txtInformacoesContribuinte.setText(documento.getInformacoesComplementaresContribuinte());
        }

        txtInformacoesContribuinteAutomatica.setText(FiscalUtil.getMensagemValorAproximadoTributos(documento));
        //Fim Informações adicionais--------------------------------------------

    }

    private void carregarRegimeTributario() {
        for (RegimeTributario n : new RegimeTributarioDAO().findAll()) {
            cboRegimeTributario.addItem(n);
        }
    }

    private void carregarNaturezaOperacao() {
        for (NaturezaOperacao n : new NaturezaOperacaoDAO().findAll()) {
            cboNaturezaOperacao.addItem(n);
        }
    }

    private void carregarTipoAtendimento() {
        for (TipoAtendimento t : new TipoAtendimentoDAO().findAll()) {
            cboTipoAtendimento.addItem(t);
        }
    }

    private void carregarConsumidorFinal() {
        for (ConsumidorFinal t : new ConsumidorFinalDAO().findAll()) {
            cboConsumidorFinal.addItem(t);
        }
    }

    private void carregarDestinoOperacao() {
        for (DestinoOperacao d : new DestinoOperacaoDAO().findAll()) {
            cboDestinoOperacao.addItem(d);
        }
    }

    private void carregarFinalidadeEmissao() {
        for (FinalidadeEmissao d : new FinalidadeEmissaoDAO().findAll()) {
            cboFinalidadeEmissao.addItem(d);
        }
    }

    private void carregarModalidadeFrete() {
        for (ModalidadeFrete m : new ModalidadeFreteDAO().findAll()) {
            cboModalidadeFrete.addItem(m);
        }
    }
    
    private void formatarDocumentosReferenciados() {
        tblDocumentosReferenciados.setModel(nfeDocumentosReferenciadosJTableModel);

        tblDocumentosReferenciados.setRowHeight(24);
        tblDocumentosReferenciados.setIntercellSpacing(new Dimension(10, 10));
        
        tblDocumentosReferenciados.getColumn("Chave").setPreferredWidth(800);
        
    }
    
    private void carregarDocumentosReferenciados() {
        nfeDocumentosReferenciadosJTableModel.clear();
        nfeDocumentosReferenciadosJTableModel.addList(documento.getDocumentosReferenciados());
        
        if(tblDocumentosReferenciados.getRowCount() > 0 && tblDocumentosReferenciados.getSelectedRow() > -1) {
            int index = tblDocumentosReferenciados.getSelectedRow();
            tblDocumentosReferenciados.setRowSelectionInterval(index, index);
        }
        
    }
    
    private void adicionarDocumentoReferenciado() {
        NfeDocumentoReferenciadoCadastroView nfeDocumentoReferenciadoCadastroView = new NfeDocumentoReferenciadoCadastroView(new DocumentoReferenciado());
        DocumentoReferenciado dr = nfeDocumentoReferenciadoCadastroView.getDocumentoReferenciado();
        
        if(dr.getId() != null) {
            documento.addDocumentoReferenciado(dr);
            documentoReferenciadoDAO.save(dr);
            carregarDocumentosReferenciados();
        }
    }
    
    private void removerDocumentoReferenciado() {
        if(tblDocumentosReferenciados.getSelectedRow() > -1) {
            DocumentoReferenciado dr = nfeDocumentosReferenciadosJTableModel.getRow(tblDocumentosReferenciados.getSelectedRow());
            
            //TO DO: verificar se existem produtos usando este documentoReferenciado antes de tentar excluir
            
            documento.removeDocumentoReferenciado(dr);
            documentoReferenciadoDAO.save(dr);
            carregarDocumentosReferenciados();
        }
    }
    
    private void editarDocumentoReferenciado() {
        if(tblDocumentosReferenciados.getSelectedRow() > -1) {
            NfeDocumentoReferenciadoCadastroView nfeDocumentoReferenciadoCadastroView = new NfeDocumentoReferenciadoCadastroView(nfeDocumentosReferenciadosJTableModel.getRow(tblDocumentosReferenciados.getSelectedRow()));
            DocumentoReferenciado dr = nfeDocumentoReferenciadoCadastroView.getDocumentoReferenciado();
            documento.addDocumentoReferenciado(dr);
            documentoReferenciadoDAO.save(dr);
            carregarDocumentosReferenciados();
        }
    }

    private void chavearEntrega() {
        JSwing.setComponentesHabilitados(pnlLocalEntrega, chkEntregaDiferente.isSelected());
    }

    private void buscarEndereco() {
        String cep = Texto.soNumeros(txtEntregaCep.getText());
        EnderecoDAO enderecoDAO = new EnderecoDAO();
        Endereco endereco = enderecoDAO.findByCep(cep);
        if (endereco != null) {
            txtEntregaEndereco.setText(endereco.getEnderecoCompleto());
            txtEntregaBairro.setText(endereco.getBairro().getNome());
            txtEntregaCodigoMunicipio.setText(endereco.getCidade().getCodigoIbgeCompleto());
            txtEntregaMunicipio.setText(endereco.getCidade().getNome());
            txtEntregatUF.setText(endereco.getCidade().getEstado().getSigla());
            txtEntregaNumero.requestFocus();
        } else {
            JOptionPane.showMessageDialog(rootPane, "CEP não encontrado", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            txtEntregaEndereco.setText("");
        }
    }

    private void carregarMunicipio() {
        String codigoIbge = txtEntregaCodigoMunicipio.getText().trim();
        if (!codigoIbge.isEmpty()) {
            CidadeDAO cidadeDAO = new CidadeDAO();
            Cidade cidade = cidadeDAO.findByCodigoIbge(codigoIbge);
            if (cidade != null) {
                txtEntregaMunicipio.setText(cidade.getNome());
                txtEntregatUF.setText(cidade.getEstado().getSigla());
            } else {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Município não encontrado", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                txtEntregaMunicipio.setText("");
            }
        }
    }

    private void pesquisarCep() {
        EnderecoPesquisaView enderecoPesquisaView = new EnderecoPesquisaView();
        Endereco endereco = enderecoPesquisaView.getEndereco();

        if (endereco != null) {
            txtEntregaCep.setText(endereco.getCep());
            txtEntregaEndereco.setText(endereco.getEnderecoCompleto());
            txtEntregaBairro.setText(endereco.getBairro().getNome());
            txtEntregaCodigoMunicipio.setText(endereco.getCidade().getCodigoIbgeCompleto());
            txtEntregaMunicipio.setText(endereco.getCidade().getNome());
            txtEntregatUF.setText(endereco.getCidade().getEstado().getSigla());
            txtEntregaNumero.requestFocus();
        } else {
            txtEntregaCep.requestFocus();
        }
    }

    private void salvar() {
        documento.setRegimeTributario((RegimeTributario) cboRegimeTributario.getSelectedItem());
        documento.setNaturezaOperacao((NaturezaOperacao) cboNaturezaOperacao.getSelectedItem());
        documento.setTipoAtendimento((TipoAtendimento) cboTipoAtendimento.getSelectedItem());
        documento.setConsumidorFinal((ConsumidorFinal) cboConsumidorFinal.getSelectedItem());
        documento.setDestinoOperacao((DestinoOperacao) cboDestinoOperacao.getSelectedItem());
        documento.setFinalidadeEmissao((FinalidadeEmissao) cboFinalidadeEmissao.getSelectedItem());

        //Entrega-------------------------------------------------------------------
        documento.setEntregaDiferente(chkEntregaDiferente.isSelected());
        documento.setEntregaCpfOuCnpj(txtEntregaCpfCnpj.getText());
        documento.setEntregaIe(txtEntregaIe.getText());
        documento.setEntregaNome(txtEntregaNome.getText());

        documento.setEntregaCep(txtEntregaCep.getText());
        documento.setEntregaEndereco(txtEntregaEndereco.getText());
        documento.setEntregaNumero(txtEntregaNumero.getText());
        documento.setEntregaComplemento(txtEntregaComplemento.getText());
        documento.setEntregaBairro(txtEntregaBairro.getText());
        documento.setEntregaCodigoMunicipio(txtEntregaCodigoMunicipio.getText());

        documento.setEntregaTelefone(txtEntregaTelefone.getText());
        documento.setEntregaEmail(txtEntregaEmail.getText());
        //Fim Entrega---------------------------------------------------------------

        //Transporte------------------------------------------------------------
        documento.setModalidadeFrete((ModalidadeFrete) cboModalidadeFrete.getSelectedItem());
        //Fim Transporte--------------------------------------------------------

        documento.setInformacoesAdicionaisFisco(txtInformacoesFisco.getText());
        documento.setInformacoesComplementaresContribuinte(txtInformacoesContribuinte.getText());

        salvar = true;

    }

    private void emitir() {
        if (documento.hasNfe()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Já foi emitida uma nota para este documento", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            if (MontarXml.validarDocumento(documento)) {
                salvar();

                NfeEmitirView nfeEmitirView = new NfeEmitirView(documento);

                carregarDados();
            }
        }
    }

    private void danfe() {
        if (documento.getChaveAcessoNfe().isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "O Danfe só pode ser gerado após a emissão da nota", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {
            dispose();
            DanfePrint.xmlToDanfe(NFE_PATH + "/enviados/" + documento.getChaveAcessoNfe() + "-nfe.xml");
        }

    }

    private void cartaCorrecao() {
        if (documento.getCancelamentoNfe() != null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Esta nota já foi cancelada", "Atenção", JOptionPane.WARNING_MESSAGE);
            
        } else if (documento.getChaveAcessoNfe().isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "A carta de correção só pode ser gerada após a emissão da nota", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {
            salvar();

            NfeCartaCorrecaoView nfeCartaCorrecaoView = new NfeCartaCorrecaoView(documento);
        }

    }

    private void cancelamento() {
        if (documento.getCancelamentoNfe() != null) {
            JOptionPane.showMessageDialog(MAIN_VIEW,
                    "Esta nota já foi cancelada em "
                    + DateTime.toString(documento.getCancelamentoNfe())
                    + " com o motivo " + documento.getMotivoCancelamentoNfe(),
                    "Atenção", JOptionPane.WARNING_MESSAGE);

        } else if (documento.getChaveAcessoNfe().isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "O cancelamento só pode ser feito após a emissão da nota", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {
            salvar();

            new NfeCancelamentoView(documento);
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

        btnGravar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        btnEmitir = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnlPrincipal = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel89 = new javax.swing.JLabel();
        txtStatus = new javax.swing.JTextField();
        jLabel91 = new javax.swing.JLabel();
        txtNumero = new javax.swing.JTextField();
        jLabel90 = new javax.swing.JLabel();
        txtSerie = new javax.swing.JTextField();
        jLabel88 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        txtDataHoraEmissao = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        txtChaveAcesso = new javax.swing.JTextField();
        pnlNfe = new javax.swing.JPanel();
        cboNaturezaOperacao = new javax.swing.JComboBox<>();
        jLabel25 = new javax.swing.JLabel();
        cboConsumidorFinal = new javax.swing.JComboBox<>();
        jLabel28 = new javax.swing.JLabel();
        cboDestinoOperacao = new javax.swing.JComboBox<>();
        jLabel27 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        cboFinalidadeEmissao = new javax.swing.JComboBox<>();
        cboRegimeTributario = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        cboTipoAtendimento = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jLabel60 = new javax.swing.JLabel();
        txtBcIcms = new javax.swing.JFormattedTextField();
        txtProdutosServicos = new javax.swing.JFormattedTextField();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        txtII = new javax.swing.JFormattedTextField();
        txtSeguro = new javax.swing.JFormattedTextField();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        txtIcmsFcp = new javax.swing.JFormattedTextField();
        txtIcmsFcpUfDestino = new javax.swing.JFormattedTextField();
        jLabel65 = new javax.swing.JLabel();
        txtIcms = new javax.swing.JFormattedTextField();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JFormattedTextField();
        txtIcmsStFcp = new javax.swing.JFormattedTextField();
        txtDesconto = new javax.swing.JFormattedTextField();
        txtIpi = new javax.swing.JFormattedTextField();
        txtFrete = new javax.swing.JFormattedTextField();
        jLabel72 = new javax.swing.JLabel();
        txtBcIcmsSt = new javax.swing.JFormattedTextField();
        jLabel73 = new javax.swing.JLabel();
        txtIcmsPartilhaRemetente = new javax.swing.JFormattedTextField();
        jLabel74 = new javax.swing.JLabel();
        txtPis = new javax.swing.JFormattedTextField();
        jLabel75 = new javax.swing.JLabel();
        txtOutrasDespesas = new javax.swing.JFormattedTextField();
        jLabel76 = new javax.swing.JLabel();
        txtIcmsStFcpRetido = new javax.swing.JFormattedTextField();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        txtIpiDevolvido = new javax.swing.JFormattedTextField();
        txtIcmsDesonerado = new javax.swing.JFormattedTextField();
        txtCofins = new javax.swing.JFormattedTextField();
        txtIcmsPartilhaDestinatario = new javax.swing.JFormattedTextField();
        txtIcmsSt = new javax.swing.JFormattedTextField();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        pnlLocalEntrega = new javax.swing.JPanel();
        jLabel82 = new javax.swing.JLabel();
        txtEntregaCpfCnpj = new javax.swing.JFormattedTextField();
        jLabel83 = new javax.swing.JLabel();
        txtEntregaIe = new javax.swing.JFormattedTextField();
        jLabel84 = new javax.swing.JLabel();
        btnCep = new javax.swing.JButton();
        txtEntregaCep = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        txtEntregaEndereco = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtEntregaNumero = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtEntregaComplemento = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtEntregaBairro = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtEntregaCodigoMunicipio = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtEntregaMunicipio = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtEntregatUF = new javax.swing.JTextField();
        Telefone = new javax.swing.JLabel();
        txtEntregaTelefone = new javax.swing.JFormattedTextField();
        Telefone2 = new javax.swing.JLabel();
        txtEntregaEmail = new javax.swing.JTextField();
        txtEntregaNome = new javax.swing.JTextField();
        chkEntregaDiferente = new javax.swing.JCheckBox();
        pnlTransporte = new javax.swing.JPanel();
        cboModalidadeFrete = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        pnlTransportador = new javax.swing.JPanel();
        jLabel85 = new javax.swing.JLabel();
        txtEntregaCpfCnpj1 = new javax.swing.JFormattedTextField();
        jLabel86 = new javax.swing.JLabel();
        txtEntregaIe1 = new javax.swing.JFormattedTextField();
        jLabel87 = new javax.swing.JLabel();
        btnCep1 = new javax.swing.JButton();
        txtEntregaCep1 = new javax.swing.JFormattedTextField();
        jLabel17 = new javax.swing.JLabel();
        txtEntregaEndereco1 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txtEntregaNumero1 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        txtEntregaComplemento1 = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        txtEntregaBairro1 = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        txtEntregaCodigoMunicipio1 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        txtEntregaMunicipio1 = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        txtEntregatUF1 = new javax.swing.JTextField();
        txtEntregaNome1 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        pnlRelato = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtInformacoesFisco = new javax.swing.JTextArea();
        jLabel35 = new javax.swing.JLabel();
        pnlRelato1 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtInformacoesContribuinte = new javax.swing.JTextArea();
        jLabel36 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        txtInformacoesContribuinteAutomatica = new javax.swing.JTextArea();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDocumentosReferenciados = new javax.swing.JTable();
        btnAdicionarTamanho = new javax.swing.JButton();
        btnRemoverTamanho = new javax.swing.JButton();
        btnDanfe = new javax.swing.JButton();
        btnCartaCorrecao = new javax.swing.JButton();
        btnCancelamento = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Nota Fiscal Eletrônica");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        btnGravar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnGravar.setText("Gravar");
        btnGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarActionPerformed(evt);
            }
        });

        btnCancelar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnEmitir.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnEmitir.setText("Emitir");
        btnEmitir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmitirActionPerformed(evt);
            }
        });

        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel89.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel89.setText("Status");

        txtStatus.setEditable(false);
        txtStatus.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtStatus.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtStatus.setName(""); // NOI18N
        txtStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtStatusActionPerformed(evt);
            }
        });

        jLabel91.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel91.setText("Número");

        txtNumero.setEditable(false);
        txtNumero.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNumero.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtNumero.setName(""); // NOI18N
        txtNumero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNumeroActionPerformed(evt);
            }
        });

        jLabel90.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel90.setText("Série");

        txtSerie.setEditable(false);
        txtSerie.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtSerie.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSerie.setName(""); // NOI18N
        txtSerie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSerieActionPerformed(evt);
            }
        });

        jLabel88.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel88.setText("Chave de Acesso");

        jLabel92.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel92.setText("Data Hora da Emissão");

        txtDataHoraEmissao.setEditable(false);
        txtDataHoraEmissao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDataHoraEmissao.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel38.setBackground(new java.awt.Color(122, 138, 153));
        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel38.setForeground(java.awt.Color.white);
        jLabel38.setText("Dados de Emissão");
        jLabel38.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        jLabel38.setOpaque(true);

        txtChaveAcesso.setEditable(false);
        txtChaveAcesso.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtChaveAcesso.setName(""); // NOI18N
        txtChaveAcesso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtChaveAcessoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel89)
                        .addGap(18, 18, 18)
                        .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel91)
                        .addGap(18, 18, 18)
                        .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel90)
                        .addGap(18, 18, 18)
                        .addComponent(txtSerie, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel88)
                        .addGap(18, 18, 18)
                        .addComponent(txtChaveAcesso))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel92)
                        .addGap(18, 18, 18)
                        .addComponent(txtDataHoraEmissao, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel38)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel89)
                    .addComponent(jLabel91)
                    .addComponent(jLabel90)
                    .addComponent(jLabel88)
                    .addComponent(txtSerie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtChaveAcesso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel92)
                    .addComponent(txtDataHoraEmissao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(71, 71, 71))
        );

        pnlNfe.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cboNaturezaOperacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel25.setText("Natureza da Operação");

        cboConsumidorFinal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel28.setText("Consumidor Final");

        cboDestinoOperacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel27.setText("Destino da operação");

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel29.setText("Finalidade de Emissão");

        cboFinalidadeEmissao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        cboRegimeTributario.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel21.setText("Regime Tributário");

        jLabel37.setBackground(new java.awt.Color(122, 138, 153));
        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel37.setForeground(java.awt.Color.white);
        jLabel37.setText("Perfil da NFe");
        jLabel37.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        jLabel37.setOpaque(true);

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel26.setText("Tipo de Atendimento");

        cboTipoAtendimento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboTipoAtendimento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTipoAtendimentoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlNfeLayout = new javax.swing.GroupLayout(pnlNfe);
        pnlNfe.setLayout(pnlNfeLayout);
        pnlNfeLayout.setHorizontalGroup(
            pnlNfeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlNfeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlNfeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlNfeLayout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addGap(18, 18, 18)
                        .addComponent(cboRegimeTributario, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel25)
                        .addGap(18, 18, 18)
                        .addComponent(cboNaturezaOperacao, 0, 265, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel27)
                        .addGap(18, 18, 18)
                        .addComponent(cboDestinoOperacao, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlNfeLayout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addGap(18, 18, 18)
                        .addComponent(cboTipoAtendimento, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel29)
                        .addGap(18, 18, 18)
                        .addComponent(cboFinalidadeEmissao, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel28)
                        .addGap(18, 18, 18)
                        .addComponent(cboConsumidorFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlNfeLayout.setVerticalGroup(
            pnlNfeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlNfeLayout.createSequentialGroup()
                .addComponent(jLabel37)
                .addGap(18, 18, 18)
                .addGroup(pnlNfeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlNfeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel27)
                        .addComponent(cboDestinoOperacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlNfeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel21)
                        .addComponent(cboRegimeTributario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel25)
                        .addComponent(cboNaturezaOperacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(pnlNfeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlNfeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel26)
                        .addComponent(cboTipoAtendimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlNfeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel28)
                        .addComponent(cboConsumidorFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel29)
                        .addComponent(cboFinalidadeEmissao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlPrincipalLayout = new javax.swing.GroupLayout(pnlPrincipal);
        pnlPrincipal.setLayout(pnlPrincipalLayout);
        pnlPrincipalLayout.setHorizontalGroup(
            pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlNfe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlPrincipalLayout.setVerticalGroup(
            pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlNfe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Principal", pnlPrincipal);

        jTabbedPane2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel60.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel60.setText("Base de cálculo");

        txtBcIcms.setEditable(false);
        txtBcIcms.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBcIcms.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtBcIcms.setName("decimal"); // NOI18N

        txtProdutosServicos.setEditable(false);
        txtProdutosServicos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtProdutosServicos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtProdutosServicos.setName("decimal"); // NOI18N

        jLabel61.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel61.setText("Produtos e Serviços");

        jLabel62.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel62.setText("II");

        txtII.setEditable(false);
        txtII.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtII.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtII.setName("decimal"); // NOI18N

        txtSeguro.setEditable(false);
        txtSeguro.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSeguro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtSeguro.setName("decimal"); // NOI18N

        jLabel63.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel63.setText("Seguro");

        jLabel64.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel64.setText("ICMS FCP");

        txtIcmsFcp.setEditable(false);
        txtIcmsFcp.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIcmsFcp.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIcmsFcp.setName("decimal"); // NOI18N

        txtIcmsFcpUfDestino.setEditable(false);
        txtIcmsFcpUfDestino.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIcmsFcpUfDestino.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIcmsFcpUfDestino.setName("decimal"); // NOI18N

        jLabel65.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel65.setText("ICMS FCP UF destino");

        txtIcms.setEditable(false);
        txtIcms.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIcms.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIcms.setName("decimal"); // NOI18N

        jLabel66.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel66.setText("ICMS");

        jLabel67.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel67.setText("Frete");

        jLabel68.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel68.setText("IPI");

        jLabel69.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel69.setText("Desconto");

        jLabel70.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel70.setText("ICMS-ST FCP");

        jLabel71.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel71.setText("Total da nota");

        txtTotal.setEditable(false);
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTotal.setName("decimal"); // NOI18N

        txtIcmsStFcp.setEditable(false);
        txtIcmsStFcp.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIcmsStFcp.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIcmsStFcp.setName("decimal"); // NOI18N

        txtDesconto.setEditable(false);
        txtDesconto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDesconto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDesconto.setName("decimal"); // NOI18N

        txtIpi.setEditable(false);
        txtIpi.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIpi.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIpi.setName("decimal"); // NOI18N

        txtFrete.setEditable(false);
        txtFrete.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFrete.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtFrete.setName("decimal"); // NOI18N

        jLabel72.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel72.setText("Base de cálculo do ICMS ST");

        txtBcIcmsSt.setEditable(false);
        txtBcIcmsSt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBcIcmsSt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtBcIcmsSt.setName("decimal"); // NOI18N

        jLabel73.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel73.setText("ICMS partilha remetente");

        txtIcmsPartilhaRemetente.setEditable(false);
        txtIcmsPartilhaRemetente.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIcmsPartilhaRemetente.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIcmsPartilhaRemetente.setName("decimal"); // NOI18N

        jLabel74.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel74.setText("PIS");

        txtPis.setEditable(false);
        txtPis.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPis.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPis.setName("decimal"); // NOI18N

        jLabel75.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel75.setText("Outras despesas acessórias");

        txtOutrasDespesas.setEditable(false);
        txtOutrasDespesas.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtOutrasDespesas.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtOutrasDespesas.setName("decimal"); // NOI18N

        jLabel76.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel76.setText("ICMS-ST FCP retido");

        txtIcmsStFcpRetido.setEditable(false);
        txtIcmsStFcpRetido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIcmsStFcpRetido.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIcmsStFcpRetido.setName("decimal"); // NOI18N

        jLabel77.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel77.setText("ICMS ST");

        jLabel78.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel78.setText("ICMS partilha destinatário");

        jLabel79.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel79.setText("COFINS");

        jLabel80.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel80.setText("ICMS Desonerado");

        jLabel81.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel81.setText("IPI Devolvido");

        txtIpiDevolvido.setEditable(false);
        txtIpiDevolvido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIpiDevolvido.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIpiDevolvido.setName("decimal"); // NOI18N

        txtIcmsDesonerado.setEditable(false);
        txtIcmsDesonerado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIcmsDesonerado.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIcmsDesonerado.setName("decimal"); // NOI18N

        txtCofins.setEditable(false);
        txtCofins.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCofins.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCofins.setName("decimal"); // NOI18N

        txtIcmsPartilhaDestinatario.setEditable(false);
        txtIcmsPartilhaDestinatario.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIcmsPartilhaDestinatario.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIcmsPartilhaDestinatario.setName("decimal"); // NOI18N

        txtIcmsSt.setEditable(false);
        txtIcmsSt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIcmsSt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIcmsSt.setName("decimal"); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel63)
                    .addComponent(jLabel62)
                    .addComponent(jLabel60)
                    .addComponent(jLabel61)
                    .addComponent(jLabel64)
                    .addComponent(jLabel65))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtIcmsFcpUfDestino, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIcmsFcp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSeguro, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtII, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProdutosServicos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBcIcms, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel69)
                    .addComponent(jLabel68)
                    .addComponent(jLabel66)
                    .addComponent(jLabel67)
                    .addComponent(jLabel70)
                    .addComponent(jLabel71))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIcmsStFcp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDesconto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIpi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFrete, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIcms, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel75)
                    .addComponent(jLabel74)
                    .addComponent(jLabel72)
                    .addComponent(jLabel73)
                    .addComponent(jLabel76))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtIcmsStFcpRetido, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOutrasDespesas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPis, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIcmsPartilhaRemetente, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBcIcmsSt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel80)
                    .addComponent(jLabel79)
                    .addComponent(jLabel77)
                    .addComponent(jLabel78)
                    .addComponent(jLabel81))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtIpiDevolvido, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIcmsDesonerado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCofins, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIcmsPartilhaDestinatario, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIcmsSt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel77))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel78)
                            .addComponent(txtIcmsPartilhaDestinatario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel79)
                            .addComponent(txtCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel80)
                            .addComponent(txtIcmsDesonerado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel81)
                            .addComponent(txtIpiDevolvido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtBcIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel60)
                                    .addComponent(txtIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel66))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtProdutosServicos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel61)
                                    .addComponent(jLabel67)
                                    .addComponent(txtFrete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtII, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel62)
                                    .addComponent(jLabel68)
                                    .addComponent(txtIpi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtSeguro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel63)
                                    .addComponent(jLabel69)
                                    .addComponent(txtDesconto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtIcmsFcp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel64)
                                    .addComponent(jLabel70)
                                    .addComponent(txtIcmsStFcp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtBcIcmsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel72))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel73)
                                    .addComponent(txtIcmsPartilhaRemetente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel74)
                                    .addComponent(txtPis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel75)
                                    .addComponent(txtOutrasDespesas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel76)
                                    .addComponent(txtIcmsStFcpRetido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtIcmsFcpUfDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel65)
                            .addComponent(jLabel71)
                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("ICMS", jPanel5);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1150, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 279, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("ISSQN", jPanel6);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1150, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 279, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Retenção de Tributos", jPanel7);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Totais", jPanel1);

        pnlLocalEntrega.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel82.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel82.setText("CPF/CNPJ");

        txtEntregaCpfCnpj.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtEntregaCpfCnpj.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtEntregaCpfCnpj.setName("cpfCnpj"); // NOI18N

        jLabel83.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel83.setText("Inscrição Estadual");

        txtEntregaIe.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtEntregaIe.setName(""); // NOI18N

        jLabel84.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel84.setText("Razão Social ou Nome do Expedidor");

        btnCep.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnCep.setText("Cep");
        btnCep.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCep.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnCep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCepActionPerformed(evt);
            }
        });

        txtEntregaCep.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtEntregaCep.setName("cep"); // NOI18N
        txtEntregaCep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEntregaCepActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel13.setText("Endereço");

        txtEntregaEndereco.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel14.setText("Número");

        txtEntregaNumero.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel15.setText("Complemento");

        txtEntregaComplemento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel16.setText("Bairro");

        txtEntregaBairro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel18.setText("Cód. Município");

        txtEntregaCodigoMunicipio.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtEntregaCodigoMunicipio.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtEntregaCodigoMunicipio.setName(""); // NOI18N
        txtEntregaCodigoMunicipio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEntregaCodigoMunicipioActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel19.setText("Município");

        txtEntregaMunicipio.setEditable(false);
        txtEntregaMunicipio.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel20.setText("UF");

        txtEntregatUF.setEditable(false);
        txtEntregatUF.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        Telefone.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        Telefone.setText("Telefone");

        txtEntregaTelefone.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtEntregaTelefone.setName("telefone"); // NOI18N

        Telefone2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        Telefone2.setText("E-mail");

        txtEntregaEmail.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtEntregaEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEntregaEmailActionPerformed(evt);
            }
        });

        txtEntregaNome.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtEntregaNome.setName(""); // NOI18N
        txtEntregaNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEntregaNomeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlLocalEntregaLayout = new javax.swing.GroupLayout(pnlLocalEntrega);
        pnlLocalEntrega.setLayout(pnlLocalEntregaLayout);
        pnlLocalEntregaLayout.setHorizontalGroup(
            pnlLocalEntregaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLocalEntregaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlLocalEntregaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlLocalEntregaLayout.createSequentialGroup()
                        .addGroup(pnlLocalEntregaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(btnCep, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlLocalEntregaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlLocalEntregaLayout.createSequentialGroup()
                                .addComponent(txtEntregaCep, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel13)
                                .addGap(18, 18, 18)
                                .addComponent(txtEntregaEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel14)
                                .addGap(18, 18, 18)
                                .addComponent(txtEntregaNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel15)
                                .addGap(18, 18, 18)
                                .addComponent(txtEntregaComplemento))
                            .addGroup(pnlLocalEntregaLayout.createSequentialGroup()
                                .addComponent(txtEntregaBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel18)
                                .addGap(18, 18, 18)
                                .addComponent(txtEntregaCodigoMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel19)
                                .addGap(18, 18, 18)
                                .addComponent(txtEntregaMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel20)
                                .addGap(18, 18, 18)
                                .addComponent(txtEntregatUF, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 63, Short.MAX_VALUE))))
                    .addGroup(pnlLocalEntregaLayout.createSequentialGroup()
                        .addComponent(jLabel84)
                        .addGap(18, 18, 18)
                        .addComponent(txtEntregaNome))
                    .addGroup(pnlLocalEntregaLayout.createSequentialGroup()
                        .addGroup(pnlLocalEntregaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlLocalEntregaLayout.createSequentialGroup()
                                .addComponent(Telefone)
                                .addGap(18, 18, 18)
                                .addComponent(txtEntregaTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Telefone2)
                                .addGap(18, 18, 18)
                                .addComponent(txtEntregaEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlLocalEntregaLayout.createSequentialGroup()
                                .addComponent(jLabel82)
                                .addGap(18, 18, 18)
                                .addComponent(txtEntregaCpfCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel83)
                                .addGap(18, 18, 18)
                                .addComponent(txtEntregaIe, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlLocalEntregaLayout.setVerticalGroup(
            pnlLocalEntregaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLocalEntregaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlLocalEntregaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEntregaCpfCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel82)
                    .addComponent(txtEntregaIe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel83))
                .addGap(18, 18, 18)
                .addGroup(pnlLocalEntregaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel84)
                    .addComponent(txtEntregaNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlLocalEntregaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEntregaComplemento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(txtEntregaNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEntregaCep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(btnCep)
                    .addComponent(jLabel14)
                    .addComponent(txtEntregaEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlLocalEntregaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEntregaBairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(txtEntregaCodigoMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(txtEntregaMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(txtEntregatUF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addGap(18, 18, 18)
                .addGroup(pnlLocalEntregaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Telefone)
                    .addComponent(txtEntregaTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Telefone2)
                    .addComponent(txtEntregaEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        chkEntregaDiferente.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkEntregaDiferente.setText("Local de entrega diferente do destinatário");
        chkEntregaDiferente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkEntregaDiferenteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(chkEntregaDiferente)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(pnlLocalEntrega, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkEntregaDiferente)
                .addGap(18, 18, 18)
                .addComponent(pnlLocalEntrega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(67, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Entrega", jPanel2);

        cboModalidadeFrete.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel22.setText("Modalidade do Frete");

        pnlTransportador.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel85.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel85.setText("CPF/CNPJ");

        txtEntregaCpfCnpj1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtEntregaCpfCnpj1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtEntregaCpfCnpj1.setName("cpfCnpj"); // NOI18N

        jLabel86.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel86.setText("Inscrição Estadual");

        txtEntregaIe1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtEntregaIe1.setName(""); // NOI18N

        jLabel87.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel87.setText("Razão Social ou Nome do Expedidor");

        btnCep1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnCep1.setText("Cep");
        btnCep1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCep1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnCep1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCep1ActionPerformed(evt);
            }
        });

        txtEntregaCep1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtEntregaCep1.setName("cep"); // NOI18N
        txtEntregaCep1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEntregaCep1ActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel17.setText("Endereço");

        txtEntregaEndereco1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel23.setText("Número");

        txtEntregaNumero1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel24.setText("Complemento");

        txtEntregaComplemento1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel30.setText("Bairro");

        txtEntregaBairro1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel31.setText("Cód. Município");

        txtEntregaCodigoMunicipio1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtEntregaCodigoMunicipio1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtEntregaCodigoMunicipio1.setName(""); // NOI18N
        txtEntregaCodigoMunicipio1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEntregaCodigoMunicipio1ActionPerformed(evt);
            }
        });

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel32.setText("Município");

        txtEntregaMunicipio1.setEditable(false);
        txtEntregaMunicipio1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel33.setText("UF");

        txtEntregatUF1.setEditable(false);
        txtEntregatUF1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtEntregaNome1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtEntregaNome1.setName(""); // NOI18N
        txtEntregaNome1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEntregaNome1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlTransportadorLayout = new javax.swing.GroupLayout(pnlTransportador);
        pnlTransportador.setLayout(pnlTransportadorLayout);
        pnlTransportadorLayout.setHorizontalGroup(
            pnlTransportadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTransportadorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTransportadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTransportadorLayout.createSequentialGroup()
                        .addGroup(pnlTransportadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel30)
                            .addComponent(btnCep1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlTransportadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlTransportadorLayout.createSequentialGroup()
                                .addComponent(txtEntregaCep1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel17)
                                .addGap(18, 18, 18)
                                .addComponent(txtEntregaEndereco1, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel23)
                                .addGap(18, 18, 18)
                                .addComponent(txtEntregaNumero1, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel24)
                                .addGap(18, 18, 18)
                                .addComponent(txtEntregaComplemento1))
                            .addGroup(pnlTransportadorLayout.createSequentialGroup()
                                .addComponent(txtEntregaBairro1, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel31)
                                .addGap(18, 18, 18)
                                .addComponent(txtEntregaCodigoMunicipio1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel32)
                                .addGap(18, 18, 18)
                                .addComponent(txtEntregaMunicipio1, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel33)
                                .addGap(18, 18, 18)
                                .addComponent(txtEntregatUF1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 63, Short.MAX_VALUE))))
                    .addGroup(pnlTransportadorLayout.createSequentialGroup()
                        .addComponent(jLabel87)
                        .addGap(18, 18, 18)
                        .addComponent(txtEntregaNome1))
                    .addGroup(pnlTransportadorLayout.createSequentialGroup()
                        .addComponent(jLabel85)
                        .addGap(18, 18, 18)
                        .addComponent(txtEntregaCpfCnpj1, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel86)
                        .addGap(18, 18, 18)
                        .addComponent(txtEntregaIe1, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 563, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlTransportadorLayout.setVerticalGroup(
            pnlTransportadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTransportadorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTransportadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEntregaCpfCnpj1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel85)
                    .addComponent(txtEntregaIe1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel86))
                .addGap(18, 18, 18)
                .addGroup(pnlTransportadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel87)
                    .addComponent(txtEntregaNome1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlTransportadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEntregaComplemento1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(txtEntregaNumero1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEntregaCep1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(btnCep1)
                    .addComponent(jLabel23)
                    .addComponent(txtEntregaEndereco1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlTransportadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEntregaBairro1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30)
                    .addComponent(txtEntregaCodigoMunicipio1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31)
                    .addComponent(txtEntregaMunicipio1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32)
                    .addComponent(txtEntregatUF1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33))
                .addContainerGap(52, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlTransporteLayout = new javax.swing.GroupLayout(pnlTransporte);
        pnlTransporte.setLayout(pnlTransporteLayout);
        pnlTransporteLayout.setHorizontalGroup(
            pnlTransporteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTransporteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTransporteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTransporteLayout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addGap(18, 18, 18)
                        .addComponent(cboModalidadeFrete, javax.swing.GroupLayout.PREFERRED_SIZE, 494, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(pnlTransportador, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlTransporteLayout.setVerticalGroup(
            pnlTransporteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTransporteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTransporteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(cboModalidadeFrete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(pnlTransportador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(65, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Transporte", pnlTransporte);

        pnlRelato.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtInformacoesFisco.setColumns(20);
        txtInformacoesFisco.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtInformacoesFisco.setLineWrap(true);
        txtInformacoesFisco.setRows(5);
        txtInformacoesFisco.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jScrollPane4.setViewportView(txtInformacoesFisco);

        jLabel35.setBackground(new java.awt.Color(122, 138, 153));
        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel35.setForeground(java.awt.Color.white);
        jLabel35.setText("Informações adicionais de interesse do fisco");
        jLabel35.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        jLabel35.setOpaque(true);

        javax.swing.GroupLayout pnlRelatoLayout = new javax.swing.GroupLayout(pnlRelato);
        pnlRelato.setLayout(pnlRelatoLayout);
        pnlRelatoLayout.setHorizontalGroup(
            pnlRelatoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlRelatoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 544, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlRelatoLayout.setVerticalGroup(
            pnlRelatoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRelatoLayout.createSequentialGroup()
                .addComponent(jLabel35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlRelato1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtInformacoesContribuinte.setColumns(20);
        txtInformacoesContribuinte.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtInformacoesContribuinte.setLineWrap(true);
        txtInformacoesContribuinte.setRows(5);
        txtInformacoesContribuinte.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jScrollPane5.setViewportView(txtInformacoesContribuinte);

        jLabel36.setBackground(new java.awt.Color(122, 138, 153));
        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel36.setForeground(java.awt.Color.white);
        jLabel36.setText("Informações complementares de interesse do contribuinte");
        jLabel36.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        jLabel36.setOpaque(true);

        jLabel93.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel93.setText("Mensagem adicionada automaticamente");

        txtInformacoesContribuinteAutomatica.setEditable(false);
        txtInformacoesContribuinteAutomatica.setBackground(javax.swing.UIManager.getDefaults().getColor("TextArea.disabledBackground"));
        txtInformacoesContribuinteAutomatica.setColumns(20);
        txtInformacoesContribuinteAutomatica.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtInformacoesContribuinteAutomatica.setLineWrap(true);
        txtInformacoesContribuinteAutomatica.setRows(4);
        txtInformacoesContribuinteAutomatica.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jScrollPane6.setViewportView(txtInformacoesContribuinteAutomatica);

        javax.swing.GroupLayout pnlRelato1Layout = new javax.swing.GroupLayout(pnlRelato1);
        pnlRelato1.setLayout(pnlRelato1Layout);
        pnlRelato1Layout.setHorizontalGroup(
            pnlRelato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
            .addGroup(pnlRelato1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRelato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
                    .addGroup(pnlRelato1Layout.createSequentialGroup()
                        .addComponent(jLabel93)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlRelato1Layout.setVerticalGroup(
            pnlRelato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRelato1Layout.createSequentialGroup()
                .addComponent(jLabel36)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel93)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlRelato, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(pnlRelato1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlRelato1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(pnlRelato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Informações Adicionais", jPanel4);

        tblDocumentosReferenciados.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblDocumentosReferenciados.setModel(new javax.swing.table.DefaultTableModel(
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
        tblDocumentosReferenciados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDocumentosReferenciadosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblDocumentosReferenciados);

        btnAdicionarTamanho.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnAdicionarTamanho.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/add.png"))); // NOI18N
        btnAdicionarTamanho.setText("Adicionar");
        btnAdicionarTamanho.setToolTipText("Adicionar");
        btnAdicionarTamanho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarTamanhoActionPerformed(evt);
            }
        });

        btnRemoverTamanho.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnRemoverTamanho.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/delete.png"))); // NOI18N
        btnRemoverTamanho.setText("Remover");
        btnRemoverTamanho.setToolTipText("Remover");
        btnRemoverTamanho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverTamanhoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1155, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(btnAdicionarTamanho)
                        .addGap(18, 18, 18)
                        .addComponent(btnRemoverTamanho)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAdicionarTamanho, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnRemoverTamanho, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Notas e Conhecimentos Fiscais Referenciados", jPanel8);

        btnDanfe.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnDanfe.setText("Danfe");
        btnDanfe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDanfeActionPerformed(evt);
            }
        });

        btnCartaCorrecao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnCartaCorrecao.setText("Carta Correção");
        btnCartaCorrecao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCartaCorrecaoActionPerformed(evt);
            }
        });

        btnCancelamento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnCancelamento.setText("Cancelamento");
        btnCancelamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelamentoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnEmitir, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnDanfe, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCartaCorrecao)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelamento)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnGravar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTabbedPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar)
                    .addComponent(btnGravar)
                    .addComponent(btnEmitir)
                    .addComponent(btnDanfe)
                    .addComponent(btnCartaCorrecao)
                    .addComponent(btnCancelamento))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    }//GEN-LAST:event_formWindowClosing

    private void cboTipoAtendimentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTipoAtendimentoActionPerformed

    }//GEN-LAST:event_cboTipoAtendimentoActionPerformed

    private void btnGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarActionPerformed
        salvar();
    }//GEN-LAST:event_btnGravarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnEmitirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmitirActionPerformed
        emitir();
    }//GEN-LAST:event_btnEmitirActionPerformed

    private void btnCepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCepActionPerformed
        pesquisarCep();
    }//GEN-LAST:event_btnCepActionPerformed

    private void txtEntregaCepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEntregaCepActionPerformed
        buscarEndereco();
    }//GEN-LAST:event_txtEntregaCepActionPerformed

    private void txtEntregaCodigoMunicipioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEntregaCodigoMunicipioActionPerformed
        carregarMunicipio();
    }//GEN-LAST:event_txtEntregaCodigoMunicipioActionPerformed

    private void txtEntregaEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEntregaEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEntregaEmailActionPerformed

    private void chkEntregaDiferenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkEntregaDiferenteActionPerformed
        chavearEntrega();
    }//GEN-LAST:event_chkEntregaDiferenteActionPerformed

    private void txtEntregaNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEntregaNomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEntregaNomeActionPerformed

    private void btnDanfeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDanfeActionPerformed
        danfe();
    }//GEN-LAST:event_btnDanfeActionPerformed

    private void btnCep1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCep1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCep1ActionPerformed

    private void txtEntregaCep1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEntregaCep1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEntregaCep1ActionPerformed

    private void txtEntregaCodigoMunicipio1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEntregaCodigoMunicipio1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEntregaCodigoMunicipio1ActionPerformed

    private void txtEntregaNome1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEntregaNome1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEntregaNome1ActionPerformed

    private void txtSerieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSerieActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSerieActionPerformed

    private void txtNumeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNumeroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNumeroActionPerformed

    private void txtStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStatusActionPerformed

    private void btnCartaCorrecaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCartaCorrecaoActionPerformed
        cartaCorrecao();
    }//GEN-LAST:event_btnCartaCorrecaoActionPerformed

    private void btnCancelamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelamentoActionPerformed
        cancelamento();
    }//GEN-LAST:event_btnCancelamentoActionPerformed

    private void tblDocumentosReferenciadosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDocumentosReferenciadosMouseClicked
        if (evt.getClickCount() == 2) {
            //int id = documentoEntradaListaJTableModel.getRow(tblVendas.getSelectedRow()).getId();
            //Venda venda = documentoEntradaListaJTableModel.getRow(tblDocumentosReferenciados.getSelectedRow());
            //MAIN_VIEW.addView(DocumentoEntradaView.getInstance(venda));
        }
    }//GEN-LAST:event_tblDocumentosReferenciadosMouseClicked

    private void btnAdicionarTamanhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarTamanhoActionPerformed
        adicionarDocumentoReferenciado();
    }//GEN-LAST:event_btnAdicionarTamanhoActionPerformed

    private void btnRemoverTamanhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverTamanhoActionPerformed
        removerDocumentoReferenciado();
    }//GEN-LAST:event_btnRemoverTamanhoActionPerformed

    private void txtChaveAcessoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtChaveAcessoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtChaveAcessoActionPerformed

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
            java.util.logging.Logger.getLogger(NfeDetalheView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NfeDetalheView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NfeDetalheView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NfeDetalheView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NfeDetalheView dialog = new NfeDetalheView(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel Telefone;
    private javax.swing.JLabel Telefone2;
    private javax.swing.JButton btnAdicionarTamanho;
    private javax.swing.JButton btnCancelamento;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnCartaCorrecao;
    private javax.swing.JButton btnCep;
    private javax.swing.JButton btnCep1;
    private javax.swing.JButton btnDanfe;
    private javax.swing.JButton btnEmitir;
    private javax.swing.JButton btnGravar;
    private javax.swing.JButton btnRemoverTamanho;
    private javax.swing.JComboBox<Object> cboConsumidorFinal;
    private javax.swing.JComboBox<Object> cboDestinoOperacao;
    private javax.swing.JComboBox<Object> cboFinalidadeEmissao;
    private javax.swing.JComboBox<Object> cboModalidadeFrete;
    private javax.swing.JComboBox<Object> cboNaturezaOperacao;
    private javax.swing.JComboBox<Object> cboRegimeTributario;
    private javax.swing.JComboBox<Object> cboTipoAtendimento;
    private javax.swing.JCheckBox chkEntregaDiferente;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
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
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
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
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JPanel pnlLocalEntrega;
    private javax.swing.JPanel pnlNfe;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JPanel pnlRelato;
    private javax.swing.JPanel pnlRelato1;
    private javax.swing.JPanel pnlTransportador;
    private javax.swing.JPanel pnlTransporte;
    private javax.swing.JTable tblDocumentosReferenciados;
    private javax.swing.JFormattedTextField txtBcIcms;
    private javax.swing.JFormattedTextField txtBcIcmsSt;
    private javax.swing.JTextField txtChaveAcesso;
    private javax.swing.JFormattedTextField txtCofins;
    private javax.swing.JTextField txtDataHoraEmissao;
    private javax.swing.JFormattedTextField txtDesconto;
    private javax.swing.JTextField txtEntregaBairro;
    private javax.swing.JTextField txtEntregaBairro1;
    private javax.swing.JFormattedTextField txtEntregaCep;
    private javax.swing.JFormattedTextField txtEntregaCep1;
    private javax.swing.JTextField txtEntregaCodigoMunicipio;
    private javax.swing.JTextField txtEntregaCodigoMunicipio1;
    private javax.swing.JTextField txtEntregaComplemento;
    private javax.swing.JTextField txtEntregaComplemento1;
    private javax.swing.JFormattedTextField txtEntregaCpfCnpj;
    private javax.swing.JFormattedTextField txtEntregaCpfCnpj1;
    private javax.swing.JTextField txtEntregaEmail;
    private javax.swing.JTextField txtEntregaEndereco;
    private javax.swing.JTextField txtEntregaEndereco1;
    private javax.swing.JFormattedTextField txtEntregaIe;
    private javax.swing.JFormattedTextField txtEntregaIe1;
    private javax.swing.JTextField txtEntregaMunicipio;
    private javax.swing.JTextField txtEntregaMunicipio1;
    private javax.swing.JTextField txtEntregaNome;
    private javax.swing.JTextField txtEntregaNome1;
    private javax.swing.JTextField txtEntregaNumero;
    private javax.swing.JTextField txtEntregaNumero1;
    private javax.swing.JFormattedTextField txtEntregaTelefone;
    private javax.swing.JTextField txtEntregatUF;
    private javax.swing.JTextField txtEntregatUF1;
    private javax.swing.JFormattedTextField txtFrete;
    private javax.swing.JFormattedTextField txtII;
    private javax.swing.JFormattedTextField txtIcms;
    private javax.swing.JFormattedTextField txtIcmsDesonerado;
    private javax.swing.JFormattedTextField txtIcmsFcp;
    private javax.swing.JFormattedTextField txtIcmsFcpUfDestino;
    private javax.swing.JFormattedTextField txtIcmsPartilhaDestinatario;
    private javax.swing.JFormattedTextField txtIcmsPartilhaRemetente;
    private javax.swing.JFormattedTextField txtIcmsSt;
    private javax.swing.JFormattedTextField txtIcmsStFcp;
    private javax.swing.JFormattedTextField txtIcmsStFcpRetido;
    private javax.swing.JTextArea txtInformacoesContribuinte;
    private javax.swing.JTextArea txtInformacoesContribuinteAutomatica;
    private javax.swing.JTextArea txtInformacoesFisco;
    private javax.swing.JFormattedTextField txtIpi;
    private javax.swing.JFormattedTextField txtIpiDevolvido;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JFormattedTextField txtOutrasDespesas;
    private javax.swing.JFormattedTextField txtPis;
    private javax.swing.JFormattedTextField txtProdutosServicos;
    private javax.swing.JFormattedTextField txtSeguro;
    private javax.swing.JTextField txtSerie;
    private javax.swing.JTextField txtStatus;
    private javax.swing.JFormattedTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
