
package view.sistema;

import view.nfe.NfeStatusServico;
import br.com.swconsultoria.certificado.TipoCertificadoA3;
import br.com.swconsultoria.nfe.dom.enuns.AmbienteEnum;
import java.awt.HeadlessException;
import java.awt.print.PrinterJob;
import java.math.BigDecimal;
import javax.print.PrintService;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import model.mysql.bean.endereco.Cidade;
import model.mysql.bean.endereco.Endereco;
import model.mysql.bean.fiscal.nfe.ConsumidorFinal;
import model.mysql.bean.fiscal.nfe.DestinoOperacao;
import model.mysql.bean.fiscal.nfe.NaturezaOperacao;
import model.mysql.bean.fiscal.nfe.RegimeTributario;
import model.mysql.bean.fiscal.nfe.TipoAtendimento;
import model.mysql.bean.principal.Constante;
import model.nosql.ImpressoraFormatoEnum;
import model.mysql.bean.principal.documento.VendaStatus;
import model.mysql.bean.principal.financeiro.Conta;
import model.mysql.dao.endereco.CidadeDAO;
import model.mysql.dao.endereco.EnderecoDAO;
import model.mysql.dao.principal.financeiro.CaixaItemTipoDAO;
import model.mysql.dao.principal.financeiro.CaixaPeriodoDAO;
import model.mysql.dao.principal.ConstanteDAO;
import model.mysql.dao.principal.MovimentoFisicoTipoDAO;
import model.mysql.dao.fiscal.CfopDAO;
import model.mysql.dao.fiscal.IbptDAO;
import model.mysql.dao.fiscal.IcmsDAO;
import model.mysql.dao.fiscal.MeioDePagamentoDAO;
import model.mysql.dao.fiscal.NcmDAO;
import model.mysql.dao.fiscal.ProdutoOrigemDAO;
import model.mysql.dao.fiscal.SatErroOuAlertaDAO;
import model.mysql.dao.fiscal.SatEstadoDAO;
import model.mysql.dao.fiscal.UnidadeComercialDAO;
import model.mysql.dao.fiscal.nfe.ConsumidorFinalDAO;
import model.mysql.dao.fiscal.nfe.DestinoOperacaoDAO;
import model.mysql.dao.fiscal.nfe.NaturezaOperacaoDAO;
import model.mysql.dao.fiscal.nfe.RegimeTributarioDAO;
import model.mysql.dao.fiscal.nfe.TipoAtendimentoDAO;
import model.mysql.dao.principal.UsuarioDAO;
import model.mysql.dao.principal.VendaTipoDAO;
import model.mysql.dao.principal.financeiro.ContaDAO;
import model.nosql.CertificadoTipoEnum;
import model.nosql.ContaTipoEnum;
import model.nosql.LayoutComandasEnum;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.PARCELA_MULTA;
import util.Decimal;
import util.JSwing;
import view.Toast;
import view.sat.SATSetup;
import view.sat.SATStatusOperacionalView;
import static ouroboros.Ouroboros.IMPRESSORA_CUPOM;
import static ouroboros.Ouroboros.IMPRESSORA_A4;
import static ouroboros.Ouroboros.IMPRESSORA_FORMATO_PADRAO;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.VENDA_LAYOUT_COMANDAS;
import util.MwConfig;
import util.Texto;
import view.endereco.EnderecoPesquisaView;
import view.nfe.NfeCertificadoView;
import view.nfe.NfeInutilizacaoView;

/**
 *
 * @author ivand
 */
public class ConfguracaoSistema extends javax.swing.JInternalFrame {
    private static ConfguracaoSistema singleInstance = null;
    
    public static ConfguracaoSistema getSingleInstance(){
        //if(!USUARIO.autorizarAcesso(Recurso.SISTEMA)) {
        if(!UsuarioDAO.validarAdministradorComLogin()) {
            return null;
        }
        
        if(singleInstance == null){
            singleInstance = new ConfguracaoSistema();
        }
        return singleInstance;
    }
    /**
     * Creates new form DadosEmpresa
     */
    public ConfguracaoSistema() {
        initComponents();
        JSwing.startComponentsBehavior(this);
        
        carregarDados();
    }
    
    private void carregarDados() {
        txtNomeFantasia.setText(Ouroboros.EMPRESA_NOME_FANTASIA);
        txtRazaoSocial.setText(Ouroboros.EMPRESA_RAZAO_SOCIAL);
        txtCNPJ.setText(Ouroboros.EMPRESA_CNPJ);
        txtIe.setText(Ouroboros.EMPRESA_IE);
        txtIm.setText(Ouroboros.EMPRESA_IM);
        txtTelefone.setText(Ouroboros.EMPRESA_TELEFONE);
        txtTelefone2.setText(Ouroboros.EMPRESA_TELEFONE2);
        txtEmail.setText(Ouroboros.EMPRESA_EMAIL);
        
        txtCep.setText(Ouroboros.EMPRESA_ENDERECO_CEP);
        txtEndereco.setText(Ouroboros.EMPRESA_ENDERECO);
        txtNumero.setText(Ouroboros.EMPRESA_ENDERECO_NUMERO);
        txtComplemento.setText(Ouroboros.EMPRESA_ENDERECO_COMPLEMENTO);
        txtBairro.setText(Ouroboros.EMPRESA_ENDERECO_BAIRRO);
        txtCodigoMunicipio.setText(Ouroboros.EMPRESA_ENDERECO_CODIGO_MUNICIPIO);
        
        carregarMunicipio();
        
        
        //Pessoa----------------------------------------------------------------
        txtLimiteCredito.setText(Decimal.toString(Ouroboros.CLIENTE_LIMITE_CREDITO));
        //Fim Pessoa------------------------------------------------------------
        
        
        //Funcionário-----------------------------------------------------------
        chkVendaFuncionarioPorItem.setSelected(Ouroboros.VENDA_FUNCIONARIO_POR_ITEM);
        chkVendaFuncionarioPorItemProduto.setSelected(Ouroboros.VENDA_FUNCIONARIO_POR_ITEM_PRODUTO);
        chkVendaFuncionarioPorItemServico.setSelected(Ouroboros.VENDA_FUNCIONARIO_POR_ITEM_SERVICO);
        
        chkVendaFuncionarioPorItemProduto.setEnabled(chkVendaFuncionarioPorItem.isSelected());
        chkVendaFuncionarioPorItemServico.setEnabled(chkVendaFuncionarioPorItem.isSelected());
        //Fim Funcionário-------------------------------------------------------
        
        
        //Venda-----------------------------------------------------------------
        chkInsercaoDireta.setSelected(Ouroboros.VENDA_INSERCAO_DIRETA);
        txtMulta.setText(Decimal.toString(PARCELA_MULTA));
        
        if(Ouroboros.PARCELA_JUROS_MONETARIO_MENSAL.compareTo(BigDecimal.ZERO) > 0) {
            cboJurosTipo.setSelectedIndex(1);
            txtJuros.setText(Decimal.toString(Ouroboros.PARCELA_JUROS_MONETARIO_MENSAL));
        } else {
            cboJurosTipo.setSelectedIndex(0);
            txtJuros.setText(Decimal.toString(Ouroboros.PARCELA_JUROS_PERCENTUAL_MENSAL));
        }
        
        txtNumeroComandas.setText(Ouroboros.VENDA_NUMERO_COMANDAS.toString());
        
        for(LayoutComandasEnum layout : LayoutComandasEnum.values()) {
            cboLayoutComandas.addItem(layout.toString());
            if(layout.toString().equals(VENDA_LAYOUT_COMANDAS)) {
                cboLayoutComandas.setSelectedItem(layout.toString());
            }
        }
        
        cboVendaStatusInicial.addItem(VendaStatus.AGUARDANDO);
        cboVendaStatusInicial.addItem(VendaStatus.ANDAMENTO);
        cboVendaStatusInicial.addItem(VendaStatus.PREPARAÇÃO_CONCLUÍDA);
        cboVendaStatusInicial.addItem(VendaStatus.ENTREGA_CONCLUÍDA);
        cboVendaStatusInicial.setSelectedItem(Ouroboros.VENDA_STATUS_INICIAL);
        
        
        chkBloquearParcelasEmAtraso.setSelected(Ouroboros.VENDA_BLOQUEAR_PARCELAS_EM_ATRASO);
        
        chkBloquearCreditoExcedido.setSelected(Ouroboros.VENDA_BLOQUEAR_CREDITO_EXCEDIDO);
        
        chkValidarEstoque.setSelected(Ouroboros.VENDA_VALIDAR_ESTOQUE);
        
        chkAlertarGarantiaPorVeiculo.setSelected(Ouroboros.VENDA_ALERTAR_GARANTIA_POR_VEICULO);
        
        chkVendaFuncionarioObrigatorio.setSelected(Ouroboros.VENDA_FUNCIONARIO_OBRIGATORIO);
        
        chkVendaImprimirProdutosServicosSeparados.setSelected(Ouroboros.VENDA_IMPRIMIR_PRODUTOS_SERVICOS_SEPARADOS);
        
        cboVendaPromissoriaTipo.setSelectedItem(Ouroboros.VENDA_PROMISSORIA_TIPO);
        
        chkVendaBonificacaoHabilitar.setSelected(Ouroboros.VENDA_BONIFICACAO_HABILITAR);
        
        chkModoBalcao.setSelected(Ouroboros.SISTEMA_MODO_BALCAO);
        
        chkAbrirComandasIniciar.setSelected(Ouroboros.VENDA_ABRIR_COMANDAS_AO_INICIAR);
        
        
        
        //Fim Venda-------------------------------------------------------------
        
        
        //Impressão-------------------------------------------------------------
        
        //Impressora cupom
        cboImpressoraCupom.addItem("Não definida");
        PrintService[] pservices = PrinterJob.lookupPrintServices();
        for (PrintService ps : pservices) {
            cboImpressoraCupom.addItem(ps.getName());
            if (ps.getName().equals(IMPRESSORA_CUPOM)) {
                cboImpressoraCupom.setSelectedItem(ps.getName());
            }
        }
        
        chkImpressoraCupomCabecalhoItem.setSelected(Ouroboros.IMPRESSORA_CUPOM_EXIBIR_CABECALHO_ITEM);
        chkImpressoraCupomNumeroItem.setSelected(Ouroboros.IMPRESSORA_CUPOM_EXIBIR_NUMERO_ITEM);
        chkImpressoraCupomCodigoItem.setSelected(Ouroboros.IMPRESSORA_CUPOM_EXIBIR_CODIGO_ITEM);
        chkImpressoraCupomUnidadeMedidaItem.setSelected(Ouroboros.IMPRESSORA_CUPOM_EXIBIR_UNIDADE_MEDIDA_ITEM);
        chkImpressoraCupomAcrescimoDescontoItem.setSelected(Ouroboros.IMPRESSORA_CUPOM_EXIBIR_ACRESCIMO_DESCONTO_ITEM);
        chkImpressoraCupomAssinaturaCliente.setSelected(Ouroboros.IMPRESSORA_CUPOM_EXIBIR_ASSINATURA_CLIENTE);
        chkImpressoraCupomMeiosPagamento.setSelected(Ouroboros.IMPRESSORA_CUPOM_EXIBIR_MEIOS_PAGAMENTO);
        
        //Impressora A4
        cboImpressoraA4.addItem("Não definida");
        for (PrintService ps : pservices) {
            cboImpressoraA4.addItem(ps.getName());
            if (ps.getName().equals(IMPRESSORA_A4)) {
                cboImpressoraA4.setSelectedItem(ps.getName());
            }
        }
        
        chkImpressoraA4Acrescimo.setSelected(Ouroboros.IMPRESSORA_A4_EXIBIR_ACRESCIMO);
        chkImpressoraA4Observacao.setSelected(Ouroboros.IMPRESSORA_A4_EXIBIR_OBSERVACAO);
        
        //Impressora etiqueta
        cboImpressoraEtiqueta.addItem("Não definida");
        for (PrintService ps : pservices) {
            cboImpressoraEtiqueta.addItem(ps.getName());
            if (ps.getName().equals(Ouroboros.IMPRESSORA_ETIQUETA)) {
                cboImpressoraEtiqueta.setSelectedItem(ps.getName());
            }
        }
        
        //Impressora Formato Padrão
        for(ImpressoraFormatoEnum formato : ImpressoraFormatoEnum.values()) {
            cboImpressoraFormatoPadrao.addItem(formato.toString());
            if(formato.toString().equals(IMPRESSORA_FORMATO_PADRAO)) {
                cboImpressoraFormatoPadrao.setSelectedItem(formato.toString());
            }
        }
        
        txtImpressaoRodape.setText(Ouroboros.IMPRESSAO_RODAPE);
        
        cboImpressoraCupomMargemCorte.setSelectedItem(Ouroboros.IMPRESSORA_CUPOM_MARGEM_CORTE.toString());
        
        cboImpressoraReciboVias.setSelectedItem(Ouroboros.IMPRESSORA_RECIBO_VIAS.toString());
        
        chkDesativarImpressao.setSelected(Ouroboros.IMPRESSORA_DESATIVAR);
        //Fim Impressão---------------------------------------------------------
        
        //NFSe------------------------------------------------------------------
        txtNfseAliquota.setText(Decimal.toString(Ouroboros.NFSE_ALIQUOTA));
        txtNfseCodigoServico.setText(Ouroboros.NFSE_CODIGO_SERVICO);
        //Fim NFSe--------------------------------------------------------------
        
        
        //NFe-------------------------------------------------------------------
        txtNfeSerie.setText(Ouroboros.NFE_SERIE.toString());
        //txtNfeProximoNumero.setText(Ouroboros.NFE_PROXIMO_NUMERO.toString()); 2020-02-11
        txtNfeProximoNumero.setText(ConstanteDAO.getValor("NFE_PROXIMO_NUMERO"));
        
        
        carregarTipoAmbiente();
        cboTipoAmbiente.setSelectedItem(Ouroboros.NFE_TIPO_AMBIENTE.toString());
        
        carregarRegimeTributario();
        cboRegimeTributario.setSelectedItem(Ouroboros.NFE_REGIME_TRIBUTARIO);
        
        carregarDestinoOperacao();
        cboDestinoOperacao.setSelectedItem(Ouroboros.NFE_DESTINO_OPERACAO);
        
        carregarNaturezaOperacao();
        cboNaturezaOperacao.setSelectedItem(Ouroboros.NFE_NATUREZA_OPERACAO);
        
        carregarTipoAtendimento();
        cboTipoAtendimento.setSelectedItem(Ouroboros.NFE_TIPO_ATENDIMENTO);
        
        carregarConsumidorFinal();
        cboConsumidorFinal.setSelectedItem(Ouroboros.NFE_CONSUMIDOR_FINAL);
        
        txtInformacoesAdicionaisFisco.setText(Ouroboros.NFE_INFORMACOES_ADICIONAIS_FISCO);
        txtInformacoesComplementaresContribuinte.setText(Ouroboros.NFE_INFORMACOES_COMPLEMENTARES_CONTRIBUINTE);
        
        carregarCertificadoTipo();
        cboCertificadoTipo.setSelectedItem(Ouroboros.NFE_CERTIFICADO_TIPO);
        
        txtCertificadoPin.setText(Ouroboros.NFE_CERTIFICADO_PIN);
        
        carregarCertificadoMarca();
        cboCertificadoMarca.setSelectedItem(Ouroboros.NFE_CERTIFICADO_MARCA);
        
        //Fim NFe---------------------------------------------------------------
        
        //Sistema---------------------------------------------------------------
        chkRevalidarAdministrador.setSelected(Ouroboros.SISTEMA_REVALIDAR_ADMINISTRADOR);
        //Fim Sistema-----------------------------------------------------------
        
        //Financeiro------------------------------------------------------------
        carregarCaixas();
        cboCaixaPrincipal.setSelectedItem(Ouroboros.FINANCEIRO_CAIXA_PRINCIPAL);
        //Fim Financeiro--------------------------------------------------------
        
        //Mindware
        chkHabilitarSat.setSelected(Ouroboros.SAT_HABILITAR);
        chkHabilitarNfe.setSelected(Ouroboros.NFE_HABILITAR);
        chkHabilitarVeiculo.setSelected(Ouroboros.VEICULO_HABILITAR);
        
        chkHabilitarOst.setSelected(Ouroboros.OST_HABILITAR);
        chkHabilitarVendaPorFicha.setSelected(Ouroboros.VENDA_POR_FICHA_HABILITAR);
    }
    
    private void carregarTipoAmbiente() {
        cboTipoAmbiente.addItem(AmbienteEnum.PRODUCAO.toString()); //1
        cboTipoAmbiente.addItem(AmbienteEnum.HOMOLOGACAO.toString()); //2
    }
    
    private void carregarRegimeTributario() {
        for (RegimeTributario r : new RegimeTributarioDAO().findAll()) {
            cboRegimeTributario.addItem(r);
        }
    }
    
    private void carregarNaturezaOperacao() {
        new NaturezaOperacaoDAO().findAll().forEach((n) -> {
            cboNaturezaOperacao.addItem(n);
        });
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
    
    private void carregarCertificadoTipo() {
        cboCertificadoTipo.addItem("");
        cboCertificadoTipo.addItem(CertificadoTipoEnum.A1.toString());
        cboCertificadoTipo.addItem(CertificadoTipoEnum.A3.toString());
        cboCertificadoTipo.addItem(CertificadoTipoEnum.REPOSITORIO_CNPJ.toString());
    }
    
    private void carregarCertificadoMarca() {
        cboCertificadoMarca.addItem("");
        cboCertificadoMarca.addItem(TipoCertificadoA3.LEITOR_GEMPC_PERTO.getMarca());
        cboCertificadoMarca.addItem(TipoCertificadoA3.LEITOR_SCR3310.getMarca());
        cboCertificadoMarca.addItem(TipoCertificadoA3.OBERTHUR.getMarca());
        cboCertificadoMarca.addItem(TipoCertificadoA3.TOKEN_ALADDIN.getMarca());
    }
    
    private void chavearCertificadoTipo() {
        cboCertificadoMarca.setEnabled(cboCertificadoTipo.getSelectedItem().equals("A3"));
    }
    
    private void consultarCertificado() {
        new NfeCertificadoView();
    }
    
    private void carregarCaixas() {
        for (Conta c : new ContaDAO().findByTipo(ContaTipoEnum.CAIXA)) {
            cboCaixaPrincipal.addItem(c);
        }
    }
    
    private void salvar(){
        //validar
        
        try{
            ConstanteDAO cDAO = new ConstanteDAO();
            
            //Empresa
            Ouroboros.EMPRESA_NOME_FANTASIA = txtNomeFantasia.getText();
            Ouroboros.EMPRESA_RAZAO_SOCIAL = txtRazaoSocial.getText();
            Ouroboros.EMPRESA_CNPJ = txtCNPJ.getText();
            Ouroboros.EMPRESA_IE = txtIe.getText();
            Ouroboros.EMPRESA_IM = txtIm.getText();
            Ouroboros.EMPRESA_TELEFONE = txtTelefone.getText();
            Ouroboros.EMPRESA_TELEFONE2 = txtTelefone2.getText();
            Ouroboros.EMPRESA_EMAIL = txtEmail.getText();
            
            Ouroboros.EMPRESA_ENDERECO_CEP = txtCep.getText();
            Ouroboros.EMPRESA_ENDERECO = txtEndereco.getText();
            Ouroboros.EMPRESA_ENDERECO_NUMERO = txtNumero.getText();
            Ouroboros.EMPRESA_ENDERECO_BAIRRO = txtBairro.getText();
            Ouroboros.EMPRESA_ENDERECO_COMPLEMENTO = txtComplemento.getText();
            Ouroboros.EMPRESA_ENDERECO_CODIGO_MUNICIPIO = txtCodigoMunicipio.getText();
            
            
            cDAO.save(new Constante("EMPRESA_NOME_FANTASIA", Ouroboros.EMPRESA_NOME_FANTASIA));
            cDAO.save(new Constante("EMPRESA_RAZAO_SOCIAL", Ouroboros.EMPRESA_RAZAO_SOCIAL));
            cDAO.save(new Constante("EMPRESA_CNPJ", Ouroboros.EMPRESA_CNPJ));
            cDAO.save(new Constante("EMPRESA_IE", Ouroboros.EMPRESA_IE));
            cDAO.save(new Constante("EMPRESA_IM", Ouroboros.EMPRESA_IM));
            cDAO.save(new Constante("EMPRESA_EMAIL", Ouroboros.EMPRESA_EMAIL));
            cDAO.save(new Constante("EMPRESA_TELEFONE", Ouroboros.EMPRESA_TELEFONE));
            cDAO.save(new Constante("EMPRESA_TELEFONE2", Ouroboros.EMPRESA_TELEFONE2));
            
            cDAO.save(new Constante("EMPRESA_ENDERECO_CEP", Ouroboros.EMPRESA_ENDERECO_CEP));
            cDAO.save(new Constante("EMPRESA_ENDERECO", Ouroboros.EMPRESA_ENDERECO));
            cDAO.save(new Constante("EMPRESA_ENDERECO_NUMERO", Ouroboros.EMPRESA_ENDERECO_NUMERO));
            cDAO.save(new Constante("EMPRESA_ENDERECO_BAIRRO", Ouroboros.EMPRESA_ENDERECO_BAIRRO));
            cDAO.save(new Constante("EMPRESA_ENDERECO_COMPLEMENTO", Ouroboros.EMPRESA_ENDERECO_COMPLEMENTO));
            cDAO.save(new Constante("EMPRESA_ENDERECO_CODIGO_MUNICIPIO", Ouroboros.EMPRESA_ENDERECO_CODIGO_MUNICIPIO));
            
            
            
            //Pessoa------------------------------------------------------------
            Ouroboros.CLIENTE_LIMITE_CREDITO = Decimal.fromString(txtLimiteCredito.getText());
            cDAO.save(new Constante("CLIENTE_LIMITE_CREDITO", String.valueOf(Ouroboros.CLIENTE_LIMITE_CREDITO)));
            //Fim Pessoa--------------------------------------------------------
            
            //Funcionário-------------------------------------------------------
            Ouroboros.VENDA_FUNCIONARIO_POR_ITEM = chkVendaFuncionarioPorItem.isSelected();
            cDAO.saveByNome("VENDA_FUNCIONARIO_POR_ITEM", String.valueOf(Ouroboros.VENDA_FUNCIONARIO_POR_ITEM));
            
            Ouroboros.VENDA_FUNCIONARIO_POR_ITEM_PRODUTO = chkVendaFuncionarioPorItemProduto.isSelected();
            cDAO.saveByNome("VENDA_FUNCIONARIO_POR_ITEM_PRODUTO", String.valueOf(Ouroboros.VENDA_FUNCIONARIO_POR_ITEM_PRODUTO));
            
            Ouroboros.VENDA_FUNCIONARIO_POR_ITEM_SERVICO = chkVendaFuncionarioPorItemServico.isSelected();
            cDAO.saveByNome("VENDA_FUNCIONARIO_POR_ITEM_SERVICO", String.valueOf(Ouroboros.VENDA_FUNCIONARIO_POR_ITEM_SERVICO));
            //Fim Funcionário---------------------------------------------------
            
            
            //Venda-------------------------------------------------------------
            Ouroboros.VENDA_INSERCAO_DIRETA = chkInsercaoDireta.isSelected();
            cDAO.save(new Constante("VENDA_INSERCAO_DIRETA", String.valueOf(Ouroboros.VENDA_INSERCAO_DIRETA)));
            
            Ouroboros.PARCELA_MULTA = Decimal.fromString(txtMulta.getText());
            cDAO.save(new Constante("PARCELA_MULTA", String.valueOf(Ouroboros.PARCELA_MULTA)));
            
            BigDecimal juros = Decimal.fromString(txtJuros.getText());
            if(cboJurosTipo.getSelectedIndex() == 0) {
                Ouroboros.PARCELA_JUROS_MONETARIO_MENSAL = BigDecimal.ZERO;
                Ouroboros.PARCELA_JUROS_PERCENTUAL_MENSAL = juros;
            } else {
                Ouroboros.PARCELA_JUROS_MONETARIO_MENSAL = juros;
                Ouroboros.PARCELA_JUROS_PERCENTUAL_MENSAL = BigDecimal.ZERO;
            }
            
            cDAO.save(new Constante("PARCELA_JUROS_MONETARIO_MENSAL", String.valueOf(Ouroboros.PARCELA_JUROS_MONETARIO_MENSAL)));
            cDAO.save(new Constante("PARCELA_JUROS_PERCENTUAL_MENSAL", String.valueOf(Ouroboros.PARCELA_JUROS_PERCENTUAL_MENSAL)));
            
            Ouroboros.VENDA_NUMERO_COMANDAS = Integer.valueOf(txtNumeroComandas.getText());
            cDAO.save(new Constante("VENDA_NUMERO_COMANDAS", String.valueOf(Ouroboros.VENDA_NUMERO_COMANDAS)));
            
            Ouroboros.VENDA_LAYOUT_COMANDAS = cboLayoutComandas.getSelectedItem().toString();
            MwConfig.setValue("VENDA_LAYOUT_COMANDAS", String.valueOf(VENDA_LAYOUT_COMANDAS));
            
            Ouroboros.VENDA_STATUS_INICIAL = (VendaStatus) cboVendaStatusInicial.getSelectedItem();
            cDAO.saveByNome("VENDA_STATUS_INICIAL", Ouroboros.VENDA_STATUS_INICIAL.getId().toString());
            
            Ouroboros.VENDA_BLOQUEAR_PARCELAS_EM_ATRASO = chkBloquearParcelasEmAtraso.isSelected();
            cDAO.save(new Constante("VENDA_BLOQUEAR_PARCELAS_EM_ATRASO", String.valueOf(Ouroboros.VENDA_BLOQUEAR_PARCELAS_EM_ATRASO)));
            
            Ouroboros.VENDA_BLOQUEAR_CREDITO_EXCEDIDO = chkBloquearCreditoExcedido.isSelected();
            cDAO.save(new Constante("VENDA_BLOQUEAR_CREDITO_EXCEDIDO", String.valueOf(Ouroboros.VENDA_BLOQUEAR_CREDITO_EXCEDIDO)));
            
            Ouroboros.VENDA_VALIDAR_ESTOQUE = chkValidarEstoque.isSelected();
            cDAO.save(new Constante("VENDA_VALIDAR_ESTOQUE", String.valueOf(Ouroboros.VENDA_VALIDAR_ESTOQUE)));
            
            Ouroboros.VENDA_ALERTAR_GARANTIA_POR_VEICULO = chkAlertarGarantiaPorVeiculo.isSelected();
            cDAO.saveByNome("VENDA_ALERTAR_GARANTIA_POR_VEICULO", String.valueOf(Ouroboros.VENDA_ALERTAR_GARANTIA_POR_VEICULO));
            
            Ouroboros.VENDA_FUNCIONARIO_OBRIGATORIO = chkVendaFuncionarioObrigatorio.isSelected();
            cDAO.saveByNome("VENDA_FUNCIONARIO_OBRIGATORIO", String.valueOf(Ouroboros.VENDA_FUNCIONARIO_OBRIGATORIO));
            
            Ouroboros.VENDA_IMPRIMIR_PRODUTOS_SERVICOS_SEPARADOS = chkVendaImprimirProdutosServicosSeparados.isSelected();
            cDAO.saveByNome("VENDA_IMPRIMIR_PRODUTOS_SERVICOS_SEPARADOS", String.valueOf(Ouroboros.VENDA_IMPRIMIR_PRODUTOS_SERVICOS_SEPARADOS));
            
            Ouroboros.VENDA_PROMISSORIA_TIPO = (String) cboVendaPromissoriaTipo.getSelectedItem();
            cDAO.saveByNome("VENDA_PROMISSORIA_TIPO", Ouroboros.VENDA_PROMISSORIA_TIPO);
            
            Ouroboros.VENDA_STATUS_INICIAL = (VendaStatus) cboVendaStatusInicial.getSelectedItem();
            cDAO.saveByNome("VENDA_STATUS_INICIAL", Ouroboros.VENDA_STATUS_INICIAL.getId().toString());
            
            Ouroboros.VENDA_BONIFICACAO_HABILITAR = chkVendaBonificacaoHabilitar.isSelected();
            cDAO.saveByNome("VENDA_BONIFICACAO_HABILITAR", String.valueOf(Ouroboros.VENDA_BONIFICACAO_HABILITAR));
            
            
            Ouroboros.SISTEMA_MODO_BALCAO = chkModoBalcao.isSelected();
            MwConfig.setValue("SISTEMA_MODO_BALCAO", String.valueOf(Ouroboros.SISTEMA_MODO_BALCAO));
            
            Ouroboros.VENDA_ABRIR_COMANDAS_AO_INICIAR = chkAbrirComandasIniciar.isSelected();
            MwConfig.setValue("VENDA_ABRIR_COMANDAS_AO_INICIAR", String.valueOf(Ouroboros.VENDA_ABRIR_COMANDAS_AO_INICIAR));
            
            //Fim Venda---------------------------------------------------------
            
            
            //Impressão---------------------------------------------------------
            //Alterado para config local
            Ouroboros.IMPRESSORA_CUPOM = cboImpressoraCupom.getSelectedItem().toString();
            MwConfig.setValue("IMPRESSORA_CUPOM", Ouroboros.IMPRESSORA_CUPOM);
            
            Ouroboros.IMPRESSORA_CUPOM_TAMANHO_FONTE = Float.valueOf(cboImpressoraCupomTamanhoFonte.getSelectedItem().toString());
            MwConfig.setValue("IMPRESSORA_CUPOM_TAMANHO_FONTE", String.valueOf(Ouroboros.IMPRESSORA_CUPOM_TAMANHO_FONTE));
            
            Ouroboros.IMPRESSORA_CUPOM_EXIBIR_CABECALHO_ITEM = chkImpressoraCupomCabecalhoItem.isSelected();
            MwConfig.setValue("IMPRESSORA_CUPOM_EXIBIR_CABECALHO_ITEM", String.valueOf(Ouroboros.IMPRESSORA_CUPOM_EXIBIR_CABECALHO_ITEM));
            
            Ouroboros.IMPRESSORA_CUPOM_EXIBIR_NUMERO_ITEM = chkImpressoraCupomNumeroItem.isSelected();
            MwConfig.setValue("IMPRESSORA_CUPOM_EXIBIR_NUMERO_ITEM", String.valueOf(Ouroboros.IMPRESSORA_CUPOM_EXIBIR_NUMERO_ITEM));
            
            Ouroboros.IMPRESSORA_CUPOM_EXIBIR_CODIGO_ITEM = chkImpressoraCupomCodigoItem.isSelected();
            MwConfig.setValue("IMPRESSORA_CUPOM_EXIBIR_CODIGO_ITEM", String.valueOf(Ouroboros.IMPRESSORA_CUPOM_EXIBIR_CODIGO_ITEM));
            
            Ouroboros.IMPRESSORA_CUPOM_EXIBIR_UNIDADE_MEDIDA_ITEM = chkImpressoraCupomUnidadeMedidaItem.isSelected();
            MwConfig.setValue("IMPRESSORA_CUPOM_EXIBIR_UNIDADE_MEDIDA_ITEM", String.valueOf(Ouroboros.IMPRESSORA_CUPOM_EXIBIR_UNIDADE_MEDIDA_ITEM));
            
            Ouroboros.IMPRESSORA_CUPOM_EXIBIR_ACRESCIMO_DESCONTO_ITEM = chkImpressoraCupomAcrescimoDescontoItem.isSelected();
            MwConfig.setValue("IMPRESSORA_CUPOM_EXIBIR_ACRESCIMO_DESCONTO_ITEM", String.valueOf(Ouroboros.IMPRESSORA_CUPOM_EXIBIR_ACRESCIMO_DESCONTO_ITEM));
            
            Ouroboros.IMPRESSORA_CUPOM_EXIBIR_ASSINATURA_CLIENTE = chkImpressoraCupomAssinaturaCliente.isSelected();
            MwConfig.setValue("IMPRESSORA_CUPOM_EXIBIR_ASSINATURA_CLIENTE", String.valueOf(Ouroboros.IMPRESSORA_CUPOM_EXIBIR_ASSINATURA_CLIENTE));
            
            Ouroboros.IMPRESSORA_CUPOM_EXIBIR_MEIOS_PAGAMENTO = chkImpressoraCupomMeiosPagamento.isSelected();
            MwConfig.setValue("IMPRESSORA_CUPOM_EXIBIR_MEIOS_PAGAMENTO", String.valueOf(Ouroboros.IMPRESSORA_CUPOM_EXIBIR_MEIOS_PAGAMENTO));
            
            Ouroboros.IMPRESSORA_CUPOM_MARGEM_CORTE = Integer.valueOf(cboImpressoraCupomMargemCorte.getSelectedItem().toString());
            MwConfig.setValue("IMPRESSORA_CUPOM_MARGEM_CORTE", String.valueOf(Ouroboros.IMPRESSORA_CUPOM_MARGEM_CORTE));
            
            Ouroboros.IMPRESSORA_RECIBO_VIAS = Integer.valueOf(cboImpressoraReciboVias.getSelectedItem().toString());
            MwConfig.setValue("IMPRESSORA_RECIBO_VIAS", String.valueOf(Ouroboros.IMPRESSORA_RECIBO_VIAS));
            
            
            
            Ouroboros.IMPRESSORA_A4 = cboImpressoraA4.getSelectedItem().toString();
            MwConfig.setValue("IMPRESSORA_A4", Ouroboros.IMPRESSORA_A4);
            
            Ouroboros.IMPRESSORA_A4_EXIBIR_ACRESCIMO = chkImpressoraA4Acrescimo.isSelected();
            MwConfig.setValue("IMPRESSORA_A4_EXIBIR_ACRESCIMO", String.valueOf(Ouroboros.IMPRESSORA_A4_EXIBIR_ACRESCIMO));
            
            Ouroboros.IMPRESSORA_A4_EXIBIR_OBSERVACAO = chkImpressoraA4Observacao.isSelected();
            MwConfig.setValue("IMPRESSORA_A4_EXIBIR_OBSERVACAO", String.valueOf(Ouroboros.IMPRESSORA_A4_EXIBIR_OBSERVACAO));
            
            Ouroboros.IMPRESSORA_ETIQUETA = cboImpressoraEtiqueta.getSelectedItem().toString();
            MwConfig.setValue("IMPRESSORA_ETIQUETA", Ouroboros.IMPRESSORA_ETIQUETA);
            
            Ouroboros.IMPRESSORA_FORMATO_PADRAO = cboImpressoraFormatoPadrao.getSelectedItem().toString();
            MwConfig.setValue("IMPRESSORA_FORMATO_PADRAO", Ouroboros.IMPRESSORA_FORMATO_PADRAO);
            
            
            Ouroboros.IMPRESSORA_DESATIVAR = chkDesativarImpressao.isSelected();
            MwConfig.setValue("IMPRESSORA_DESATIVAR", String.valueOf(Ouroboros.IMPRESSORA_DESATIVAR));
            
            
            Ouroboros.IMPRESSAO_RODAPE = txtImpressaoRodape.getText().trim();
            cDAO.save(new Constante("IMPRESSAO_RODAPE", String.valueOf(Ouroboros.IMPRESSAO_RODAPE)));
            
            
            //NFSe--------------------------------------------------------------
            Ouroboros.NFSE_ALIQUOTA = Decimal.fromString(txtNfseAliquota.getText());
            cDAO.saveByNome("NFSE_ALIQUOTA", String.valueOf(Ouroboros.NFSE_ALIQUOTA));
            
            Ouroboros.NFSE_CODIGO_SERVICO = txtNfseCodigoServico.getText();
            cDAO.saveByNome("NFSE_CODIGO_SERVICO", Ouroboros.NFSE_CODIGO_SERVICO);
            //Fim NFSe----------------------------------------------------------
            
            
            //NFe---------------------------------------------------------------
            Ouroboros.NFE_SERIE = Integer.valueOf(txtNfeSerie.getText());
            cDAO.saveByNome("NFE_SERIE", String.valueOf(Ouroboros.NFE_SERIE));
            
            //Ouroboros.NFE_PROXIMO_NUMERO = Integer.valueOf(txtNfeProximoNumero.getText()); 2020-02-11
            //cDAO.save(new Constante("NFE_PROXIMO_NUMERO", String.valueOf(Ouroboros.NFE_PROXIMO_NUMERO)));
            cDAO.save(new Constante("NFE_PROXIMO_NUMERO", txtNfeProximoNumero.getText()));
            
            Ouroboros.NFE_TIPO_AMBIENTE = AmbienteEnum.getByCodigo(String.valueOf(cboTipoAmbiente.getSelectedIndex() + 1));
            cDAO.saveByNome("NFE_TIPO_AMBIENTE", Ouroboros.NFE_TIPO_AMBIENTE.getCodigo());
            
            Ouroboros.NFE_REGIME_TRIBUTARIO = (RegimeTributario) cboRegimeTributario.getSelectedItem();
            cDAO.saveByNome("NFE_REGIME_TRIBUTARIO", Ouroboros.NFE_REGIME_TRIBUTARIO.getId().toString());
            
            Ouroboros.NFE_NATUREZA_OPERACAO = (NaturezaOperacao) cboNaturezaOperacao.getSelectedItem();
            cDAO.saveByNome("NFE_NATUREZA_OPERACAO", Ouroboros.NFE_NATUREZA_OPERACAO.getId().toString());
            
            Ouroboros.NFE_TIPO_ATENDIMENTO = (TipoAtendimento) cboTipoAtendimento.getSelectedItem();
            cDAO.saveByNome("NFE_TIPO_ATENDIMENTO", Ouroboros.NFE_TIPO_ATENDIMENTO.getId().toString());
            
            Ouroboros.NFE_CONSUMIDOR_FINAL = (ConsumidorFinal) cboConsumidorFinal.getSelectedItem();
            cDAO.saveByNome("NFE_CONSUMIDOR_FINAL", Ouroboros.NFE_CONSUMIDOR_FINAL.getId().toString());
            
            Ouroboros.NFE_DESTINO_OPERACAO = (DestinoOperacao) cboDestinoOperacao.getSelectedItem();
            cDAO.saveByNome("NFE_DESTINO_OPERACAO", Ouroboros.NFE_DESTINO_OPERACAO.getId().toString());
            
            Ouroboros.NFE_INFORMACOES_ADICIONAIS_FISCO = txtInformacoesAdicionaisFisco.getText();
            cDAO.saveByNome("NFE_INFORMACOES_ADICIONAIS_FISCO", Ouroboros.NFE_INFORMACOES_ADICIONAIS_FISCO);
            
            Ouroboros.NFE_INFORMACOES_COMPLEMENTARES_CONTRIBUINTE = txtInformacoesComplementaresContribuinte.getText();
            cDAO.saveByNome("NFE_INFORMACOES_COMPLEMENTARES_CONTRIBUINTE", Ouroboros.NFE_INFORMACOES_COMPLEMENTARES_CONTRIBUINTE);
            
            Ouroboros.NFE_CERTIFICADO_TIPO = cboCertificadoTipo.getSelectedItem().toString();
            MwConfig.setValue("NFE_CERTIFICADO_TIPO", String.valueOf(Ouroboros.NFE_CERTIFICADO_TIPO));
            
            Ouroboros.NFE_CERTIFICADO_PIN = txtCertificadoPin.getText();
            MwConfig.setValue("NFE_CERTIFICADO_PIN", String.valueOf(Ouroboros.NFE_CERTIFICADO_PIN));
            
            Ouroboros.NFE_CERTIFICADO_MARCA = cboCertificadoMarca.getSelectedItem().toString();
            MwConfig.setValue("NFE_CERTIFICADO_MARCA", String.valueOf(Ouroboros.NFE_CERTIFICADO_MARCA));
            
            //Fim NFe-----------------------------------------------------------
            
            //Diversos----------------------------------------------------------
            Ouroboros.SISTEMA_REVALIDAR_ADMINISTRADOR = chkRevalidarAdministrador.isSelected();
            cDAO.save(new Constante("SISTEMA_REVALIDAR_ADMINISTRADOR", String.valueOf(Ouroboros.SISTEMA_REVALIDAR_ADMINISTRADOR)));
            //Fim Diversos------------------------------------------------------
            
            
            //Financeiro------------------------------------------------------------
            Ouroboros.FINANCEIRO_CAIXA_PRINCIPAL = (Conta) cboCaixaPrincipal.getSelectedItem();
            MwConfig.setValue("FINANCEIRO_CAIXA_PRINCIPAL", Ouroboros.FINANCEIRO_CAIXA_PRINCIPAL.getId().toString());
            
            //Fim Financeiro--------------------------------------------------------

            //Mindware----------------------------------------------------------
            Ouroboros.SAT_HABILITAR = chkHabilitarSat.isSelected();
            cDAO.save(new Constante("SAT_HABILITAR", String.valueOf(Ouroboros.SAT_HABILITAR)));
            
            Ouroboros.NFE_HABILITAR = chkHabilitarNfe.isSelected();
            cDAO.save(new Constante("NFE_HABILITAR", String.valueOf(Ouroboros.NFE_HABILITAR)));
            
            Ouroboros.VEICULO_HABILITAR = chkHabilitarVeiculo.isSelected();
            cDAO.save(new Constante("VEICULO_HABILITAR", String.valueOf(Ouroboros.VEICULO_HABILITAR)));
            
            Ouroboros.OST_HABILITAR = chkHabilitarOst.isSelected();
            cDAO.save(new Constante("OST_HABILITAR", String.valueOf(Ouroboros.OST_HABILITAR)));
            
            Ouroboros.VENDA_POR_FICHA_HABILITAR = chkHabilitarVendaPorFicha.isSelected();
            cDAO.save(new Constante("VENDA_POR_FICHA_HABILITAR", String.valueOf(Ouroboros.VENDA_POR_FICHA_HABILITAR)));
            
            
            JOptionPane.showMessageDialog(rootPane, "Dados salvos", null, JOptionPane.INFORMATION_MESSAGE);
            
        } catch(HeadlessException e){
            JOptionPane.showMessageDialog(rootPane, e, "Erro", JOptionPane.ERROR_MESSAGE);
        }
        
        
    }
    
    private void buscarEndereco() {
        String cep = Texto.soNumeros(txtCep.getText());
        EnderecoDAO enderecoDAO = new EnderecoDAO();
        Endereco endereco = enderecoDAO.findByCep(cep);
        if (endereco != null) {
            txtEndereco.setText(endereco.getEnderecoCompleto());
            txtBairro.setText(endereco.getBairro().getNome());
            txtCodigoMunicipio.setText(endereco.getCidade().getCodigoIbgeCompleto());
            txtMunicipio.setText(endereco.getCidade().getNome());
            txtUF.setText(endereco.getCidade().getEstado().getSigla());
            txtNumero.requestFocus();
        } else {
            JOptionPane.showMessageDialog(rootPane, "CEP não encontrado", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            txtEndereco.setText("");
        }
    }

    private void carregarMunicipio() {
        String codigoIbge = txtCodigoMunicipio.getText().trim();
        if (!codigoIbge.isEmpty()) {
            CidadeDAO cidadeDAO = new CidadeDAO();
            Cidade cidade = cidadeDAO.findByCodigoIbge(codigoIbge);
            if (cidade != null) {
                txtMunicipio.setText(cidade.getNome());
                txtUF.setText(cidade.getEstado().getSigla());
            } else {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Município não encontrado", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                txtMunicipio.setText("");
            }
        }
    }
    
    private void pesquisarCep() {
        EnderecoPesquisaView enderecoPesquisaView = new EnderecoPesquisaView();
        Endereco endereco = enderecoPesquisaView.getEndereco();
        
        if(endereco != null) {
            txtCep.setText(endereco.getCep());
            txtEndereco.setText(endereco.getEnderecoCompleto());
            txtBairro.setText(endereco.getBairro().getNome());
            txtCodigoMunicipio.setText(endereco.getCidade().getCodigoIbgeCompleto());
            txtMunicipio.setText(endereco.getCidade().getNome());
            txtUF.setText(endereco.getCidade().getEstado().getSigla());
            txtNumero.requestFocus();
        } else {
            txtCep.requestFocus();
        }
    }
    
    private void chavearVendaFuncionarioPorItem() {
        chkVendaFuncionarioPorItemProduto.setEnabled(chkVendaFuncionarioPorItem.isSelected());
        chkVendaFuncionarioPorItemServico.setEnabled(chkVendaFuncionarioPorItem.isSelected());
        
        chkVendaFuncionarioPorItemProduto.setSelected(chkVendaFuncionarioPorItem.isSelected());
        chkVendaFuncionarioPorItemServico.setSelected(chkVendaFuncionarioPorItem.isSelected());
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnSalvar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jTabbedPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        btnCep = new javax.swing.JButton();
        txtCep = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        txtEndereco = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtBairro = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtNumero = new javax.swing.JTextField();
        txtCodigoMunicipio = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtMunicipio = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtComplemento = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtUF = new javax.swing.JTextField();
        Telefone = new javax.swing.JLabel();
        txtTelefone = new javax.swing.JFormattedTextField();
        Telefone3 = new javax.swing.JLabel();
        txtTelefone2 = new javax.swing.JFormattedTextField();
        Telefone2 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtRazaoSocial = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtNomeFantasia = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCNPJ = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtIe = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        txtIm = new javax.swing.JFormattedTextField();
        jPanel11 = new javax.swing.JPanel();
        txtLimiteCredito = new javax.swing.JFormattedTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jLabel52 = new javax.swing.JLabel();
        chkVendaFuncionarioPorItem = new javax.swing.JCheckBox();
        chkVendaFuncionarioPorItemProduto = new javax.swing.JCheckBox();
        chkVendaFuncionarioPorItemServico = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        chkInsercaoDireta = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        txtMulta = new javax.swing.JFormattedTextField();
        txtJuros = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        cboJurosTipo = new javax.swing.JComboBox<>();
        txtNumeroComandas = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        chkBloquearParcelasEmAtraso = new javax.swing.JCheckBox();
        chkBloquearCreditoExcedido = new javax.swing.JCheckBox();
        jPanel12 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        chkModoBalcao = new javax.swing.JCheckBox();
        chkAbrirComandasIniciar = new javax.swing.JCheckBox();
        jLabel39 = new javax.swing.JLabel();
        cboLayoutComandas = new javax.swing.JComboBox<>();
        cboVendaStatusInicial = new javax.swing.JComboBox<>();
        jLabel45 = new javax.swing.JLabel();
        chkValidarEstoque = new javax.swing.JCheckBox();
        chkAlertarGarantiaPorVeiculo = new javax.swing.JCheckBox();
        chkVendaImprimirProdutosServicosSeparados = new javax.swing.JCheckBox();
        chkVendaFuncionarioObrigatorio = new javax.swing.JCheckBox();
        cboVendaPromissoriaTipo = new javax.swing.JComboBox<>();
        jLabel58 = new javax.swing.JLabel();
        jPanel23 = new javax.swing.JPanel();
        chkVendaBonificacaoHabilitar = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        chkDesativarImpressao = new javax.swing.JCheckBox();
        jLabel38 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        cboImpressoraCupom = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        cboImpressoraFormatoPadrao = new javax.swing.JComboBox<>();
        jLabel49 = new javax.swing.JLabel();
        chkImpressoraCupomNumeroItem = new javax.swing.JCheckBox();
        chkImpressoraCupomCodigoItem = new javax.swing.JCheckBox();
        chkImpressoraCupomUnidadeMedidaItem = new javax.swing.JCheckBox();
        chkImpressoraCupomCabecalhoItem = new javax.swing.JCheckBox();
        jLabel50 = new javax.swing.JLabel();
        cboImpressoraCupomTamanhoFonte = new javax.swing.JComboBox<>();
        chkImpressoraCupomAcrescimoDescontoItem = new javax.swing.JCheckBox();
        jLabel40 = new javax.swing.JLabel();
        txtImpressaoRodape = new javax.swing.JTextField();
        chkImpressoraCupomAssinaturaCliente = new javax.swing.JCheckBox();
        chkImpressoraCupomMeiosPagamento = new javax.swing.JCheckBox();
        cboImpressoraCupomMargemCorte = new javax.swing.JComboBox<>();
        jLabel57 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        cboImpressoraReciboVias = new javax.swing.JComboBox<>();
        jPanel20 = new javax.swing.JPanel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        cboImpressoraA4 = new javax.swing.JComboBox<>();
        chkImpressoraA4Acrescimo = new javax.swing.JCheckBox();
        chkImpressoraA4Observacao = new javax.swing.JCheckBox();
        jPanel21 = new javax.swing.JPanel();
        jLabel55 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        cboImpressoraEtiqueta = new javax.swing.JComboBox<>();
        jPanel16 = new javax.swing.JPanel();
        txtNfseAliquota = new javax.swing.JFormattedTextField();
        jLabel48 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        txtNfseCodigoServico = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        btnSat = new javax.swing.JButton();
        btnStatuSat = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        txtCNAE = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtIESubstituicaoTributaria = new javax.swing.JFormattedTextField();
        jLabel21 = new javax.swing.JLabel();
        cboRegimeTributario = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        txtNfeSerie = new javax.swing.JFormattedTextField();
        txtNfeProximoNumero = new javax.swing.JFormattedTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        cboTipoAmbiente = new javax.swing.JComboBox<>();
        jLabel25 = new javax.swing.JLabel();
        cboNaturezaOperacao = new javax.swing.JComboBox<>();
        cboTipoAtendimento = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        cboDestinoOperacao = new javax.swing.JComboBox<>();
        cboConsumidorFinal = new javax.swing.JComboBox<>();
        jLabel28 = new javax.swing.JLabel();
        btnStatusNfe = new javax.swing.JButton();
        btnInutilizarNfe = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtInformacoesAdicionaisFisco = new javax.swing.JTextArea();
        jLabel32 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtInformacoesComplementaresContribuinte = new javax.swing.JTextArea();
        jLabel33 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        cboCertificadoTipo = new javax.swing.JComboBox<>();
        jLabel42 = new javax.swing.JLabel();
        cboCertificadoMarca = new javax.swing.JComboBox<>();
        jLabel44 = new javax.swing.JLabel();
        txtCertificadoPin = new javax.swing.JTextField();
        btnConsultarCertificado = new javax.swing.JButton();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        chkRevalidarAdministrador = new javax.swing.JCheckBox();
        jPanel22 = new javax.swing.JPanel();
        jLabel56 = new javax.swing.JLabel();
        cboCaixaPrincipal = new javax.swing.JComboBox<>();
        jPanel13 = new javax.swing.JPanel();
        btnPatch = new javax.swing.JButton();
        btnBootstrap = new javax.swing.JButton();
        chkHabilitarSat = new javax.swing.JCheckBox();
        chkHabilitarNfe = new javax.swing.JCheckBox();
        chkHabilitarOst = new javax.swing.JCheckBox();
        chkHabilitarVeiculo = new javax.swing.JCheckBox();
        chkHabilitarVendaPorFicha = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        btnAjuda = new javax.swing.JButton();

        setTitle("Configuração do Sistema");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
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

        btnSalvar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnCancelar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        jTabbedPane.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPaneStateChanged(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnCep.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnCep.setText("Cep");
        btnCep.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCep.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnCep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCepActionPerformed(evt);
            }
        });

        txtCep.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCep.setName("cep"); // NOI18N
        txtCep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCepActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel13.setText("Endereço");

        txtEndereco.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel14.setText("Número");

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel16.setText("Bairro");

        txtBairro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel18.setText("Cód. Município");

        txtNumero.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtCodigoMunicipio.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCodigoMunicipio.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCodigoMunicipio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoMunicipioActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel19.setText("Município");

        txtMunicipio.setEditable(false);
        txtMunicipio.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel15.setText("Complemento");

        txtComplemento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel20.setText("UF");

        txtUF.setEditable(false);
        txtUF.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        Telefone.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        Telefone.setText("Telefone");

        txtTelefone.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTelefone.setName("telefone"); // NOI18N

        Telefone3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        Telefone3.setText("Telefone 2");

        txtTelefone2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTelefone2.setName("telefone"); // NOI18N
        txtTelefone2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTelefone2ActionPerformed(evt);
            }
        });

        Telefone2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        Telefone2.setText("E-mail");

        txtEmail.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });

        jLabel36.setBackground(new java.awt.Color(122, 138, 153));
        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel36.setForeground(java.awt.Color.white);
        jLabel36.setText("Contato e Localização");
        jLabel36.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel36.setOpaque(true);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Telefone)
                    .addComponent(jLabel16)
                    .addComponent(btnCep, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(txtCep, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel13)
                        .addGap(18, 18, 18)
                        .addComponent(txtEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel14)
                        .addGap(18, 18, 18)
                        .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel15)
                        .addGap(18, 18, 18)
                        .addComponent(txtComplemento))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(txtBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel18)
                                .addGap(18, 18, 18)
                                .addComponent(txtCodigoMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel19)
                                .addGap(18, 18, 18)
                                .addComponent(txtMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel20)
                                .addGap(18, 18, 18)
                                .addComponent(txtUF, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(txtTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Telefone3)
                                .addGap(18, 18, 18)
                                .addComponent(txtTelefone2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Telefone2)
                                .addGap(18, 18, 18)
                                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 71, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel36)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Telefone)
                    .addComponent(txtTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Telefone3)
                    .addComponent(txtTelefone2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Telefone2)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtComplemento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(btnCep)
                    .addComponent(jLabel14)
                    .addComponent(txtEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(txtCodigoMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(txtMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(txtUF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel35.setBackground(new java.awt.Color(122, 138, 153));
        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel35.setForeground(java.awt.Color.white);
        jLabel35.setText("Principal");
        jLabel35.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel35.setOpaque(true);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Razão Social");

        txtRazaoSocial.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Nome Fantasia");

        txtNomeFantasia.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("CNPJ");

        txtCNPJ.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCNPJ.setName("cnpj"); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("IE");

        txtIe.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIe.setName("inteiro"); // NOI18N

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel12.setText("IM");

        txtIm.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIm.setName("inteiro"); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(64, 64, 64)
                        .addComponent(txtRazaoSocial, javax.swing.GroupLayout.PREFERRED_SIZE, 487, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtNomeFantasia))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(107, 107, 107)
                        .addComponent(txtCNPJ, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(txtIe, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addGap(40, 40, 40)
                        .addComponent(txtIm, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel35)
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNomeFantasia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(txtRazaoSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCNPJ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(txtIe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(txtIm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(58, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(226, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Dados da Empresa", jPanel1);

        txtLimiteCredito.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtLimiteCredito.setName("decimal"); // NOI18N

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel29.setText("Limite de Crédito Padrão");

        jLabel30.setForeground(java.awt.Color.blue);
        jLabel30.setText("Determina o limite de crédito quando o usuário logado não é administrador");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel29)
                .addGap(18, 18, 18)
                .addComponent(txtLimiteCredito, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel30)
                .addContainerGap(563, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(txtLimiteCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addContainerGap(551, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Clientes e Fornecedores", jPanel11);

        jPanel19.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel52.setBackground(new java.awt.Color(122, 138, 153));
        jLabel52.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel52.setForeground(java.awt.Color.white);
        jLabel52.setText("Documentos de Saída");
        jLabel52.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel52.setOpaque(true);

        chkVendaFuncionarioPorItem.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkVendaFuncionarioPorItem.setText("Habilitar funcionário por item em documentos de saída");
        chkVendaFuncionarioPorItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkVendaFuncionarioPorItemActionPerformed(evt);
            }
        });

        chkVendaFuncionarioPorItemProduto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkVendaFuncionarioPorItemProduto.setText("Informar funcionário ao inserir item do tipo Produto");

        chkVendaFuncionarioPorItemServico.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkVendaFuncionarioPorItemServico.setText("Informar funcionário ao inserir item do tipo Serviço");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel52, javax.swing.GroupLayout.DEFAULT_SIZE, 1175, Short.MAX_VALUE)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkVendaFuncionarioPorItem)
                    .addComponent(chkVendaFuncionarioPorItemProduto)
                    .addComponent(chkVendaFuncionarioPorItemServico))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(jLabel52)
                .addGap(18, 18, 18)
                .addComponent(chkVendaFuncionarioPorItem)
                .addGap(18, 18, 18)
                .addComponent(chkVendaFuncionarioPorItemProduto)
                .addGap(18, 18, 18)
                .addComponent(chkVendaFuncionarioPorItemServico)
                .addGap(0, 41, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(377, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Funcionário", jPanel18);

        chkInsercaoDireta.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkInsercaoDireta.setText("Inserir item direto (se desabilitado, permite alterar o valor do produto)");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("Multa %");

        txtMulta.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtMulta.setName("decimal"); // NOI18N

        txtJuros.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtJuros.setName("decimal"); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Juros (a.m.)");

        cboJurosTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "%", "R$" }));

        txtNumeroComandas.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNumeroComandas.setName("inteiro"); // NOI18N

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("Número de Comandas");

        chkBloquearParcelasEmAtraso.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkBloquearParcelasEmAtraso.setText("Bloquear faturamento com parcelas em atraso");

        chkBloquearCreditoExcedido.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkBloquearCreditoExcedido.setText("Bloquear faturamento com limite de crédito excedido");

        jPanel12.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel37.setBackground(new java.awt.Color(122, 138, 153));
        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel37.setForeground(java.awt.Color.white);
        jLabel37.setText("Configurações desta estação");
        jLabel37.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel37.setOpaque(true);

        chkModoBalcao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkModoBalcao.setText("Modo balcão (tela de venda simples, sem opções de recebimento)");

        chkAbrirComandasIniciar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkAbrirComandasIniciar.setText("Abrir comandas ao iniciar");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkModoBalcao)
                    .addComponent(chkAbrirComandasIniciar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jLabel37)
                .addGap(18, 18, 18)
                .addComponent(chkModoBalcao)
                .addGap(18, 18, 18)
                .addComponent(chkAbrirComandasIniciar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel39.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel39.setText("Layout Comandas");

        cboLayoutComandas.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        cboVendaStatusInicial.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel45.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel45.setText("Status Inicial");

        chkValidarEstoque.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkValidarEstoque.setText("Validar estoque ao inserir item");

        chkAlertarGarantiaPorVeiculo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkAlertarGarantiaPorVeiculo.setText("Alertar garantia de produto por veículo");

        chkVendaImprimirProdutosServicosSeparados.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkVendaImprimirProdutosServicosSeparados.setText("Imprimir produtos e serviços separados");

        chkVendaFuncionarioObrigatorio.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkVendaFuncionarioObrigatorio.setText("Funcionário obrigatório");

        cboVendaPromissoriaTipo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboVendaPromissoriaTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Parcela", "Documento" }));

        jLabel58.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel58.setText("Tipo de Promissória");

        jPanel23.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        chkVendaBonificacaoHabilitar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkVendaBonificacaoHabilitar.setText("Habilitar bonificação");

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkVendaBonificacaoHabilitar)
                .addContainerGap(237, Short.MAX_VALUE))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkVendaBonificacaoHabilitar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18)
                                .addComponent(txtNumeroComandas, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel39)
                                .addGap(18, 18, 18)
                                .addComponent(cboLayoutComandas, 0, 416, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(jLabel45)
                                    .addGap(18, 18, 18)
                                    .addComponent(cboVendaStatusInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(chkValidarEstoque)
                                    .addGap(18, 18, 18)
                                    .addComponent(chkAlertarGarantiaPorVeiculo)
                                    .addGap(18, 18, 18)
                                    .addComponent(chkVendaFuncionarioObrigatorio))
                                .addComponent(chkVendaImprimirProdutosServicosSeparados)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(chkBloquearParcelasEmAtraso)
                                    .addGap(18, 18, 18)
                                    .addComponent(chkBloquearCreditoExcedido))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(jLabel58)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cboVendaPromissoriaTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(284, 284, 284)))
                            .addComponent(chkInsercaoDireta)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addComponent(txtMulta, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addComponent(txtJuros, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cboJurosTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(chkInsercaoDireta)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel6)
                                .addComponent(cboJurosTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtJuros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel7)
                                .addComponent(txtMulta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNumeroComandas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel39)
                            .addComponent(cboLayoutComandas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel45)
                            .addComponent(cboVendaStatusInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(21, 21, 21)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkBloquearParcelasEmAtraso)
                            .addComponent(chkBloquearCreditoExcedido))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkValidarEstoque)
                            .addComponent(chkAlertarGarantiaPorVeiculo)
                            .addComponent(chkVendaFuncionarioObrigatorio))
                        .addGap(18, 18, 18)
                        .addComponent(chkVendaImprimirProdutosServicosSeparados)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel58)
                            .addComponent(cboVendaPromissoriaTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(119, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Documento de Saída", jPanel3);

        chkDesativarImpressao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkDesativarImpressao.setText("Desativar impressão para testes");

        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel38.setForeground(java.awt.Color.red);
        jLabel38.setText("*Impressora SAT é configurada junto ao SAT");

        jPanel17.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("Impressora");

        cboImpressoraCupom.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setText("Formato (térmica)");

        cboImpressoraFormatoPadrao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel49.setBackground(new java.awt.Color(122, 138, 153));
        jLabel49.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel49.setForeground(java.awt.Color.white);
        jLabel49.setText("Cupom Não Fiscal (Térmica e TXT)");
        jLabel49.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel49.setOpaque(true);

        chkImpressoraCupomNumeroItem.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkImpressoraCupomNumeroItem.setText("Exibir Número do Item");

        chkImpressoraCupomCodigoItem.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkImpressoraCupomCodigoItem.setText("Exibir Código do Item");

        chkImpressoraCupomUnidadeMedidaItem.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkImpressoraCupomUnidadeMedidaItem.setText("Exibir Unidade de Medida do Item");

        chkImpressoraCupomCabecalhoItem.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkImpressoraCupomCabecalhoItem.setText("Exibir Cabeçalho dos itens");

        jLabel50.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel50.setText("Fonte (térmica)");

        cboImpressoraCupomTamanhoFonte.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboImpressoraCupomTamanhoFonte.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "8", "9", "10", "11", "12" }));

        chkImpressoraCupomAcrescimoDescontoItem.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkImpressoraCupomAcrescimoDescontoItem.setText("Exibir Desconto/Acréscimo do Item");

        jLabel40.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel40.setText("Texto de Rodapé");

        txtImpressaoRodape.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        chkImpressoraCupomAssinaturaCliente.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkImpressoraCupomAssinaturaCliente.setText("Exibir Assinatura do Cliente");

        chkImpressoraCupomMeiosPagamento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkImpressoraCupomMeiosPagamento.setText("Exibir Meios de Pagamento");

        cboImpressoraCupomMargemCorte.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboImpressoraCupomMargemCorte.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));

        jLabel57.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel57.setText("Margem de corte (linhas)");

        jLabel59.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel59.setText("Vias do recibo");

        cboImpressoraReciboVias.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboImpressoraReciboVias.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3" }));

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(chkImpressoraCupomCabecalhoItem)
                        .addGap(18, 18, 18)
                        .addComponent(chkImpressoraCupomNumeroItem)
                        .addGap(18, 18, 18)
                        .addComponent(chkImpressoraCupomCodigoItem)
                        .addGap(18, 18, 18)
                        .addComponent(chkImpressoraCupomUnidadeMedidaItem)
                        .addGap(18, 18, 18)
                        .addComponent(chkImpressoraCupomAcrescimoDescontoItem))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(cboImpressoraCupom, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11)
                        .addGap(18, 18, 18)
                        .addComponent(cboImpressoraFormatoPadrao, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel50)
                        .addGap(18, 18, 18)
                        .addComponent(cboImpressoraCupomTamanhoFonte, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(chkImpressoraCupomAssinaturaCliente)
                        .addGap(18, 18, 18)
                        .addComponent(chkImpressoraCupomMeiosPagamento))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addGap(18, 18, 18)
                        .addComponent(txtImpressaoRodape, javax.swing.GroupLayout.PREFERRED_SIZE, 458, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel57)
                        .addGap(18, 18, 18)
                        .addComponent(cboImpressoraCupomMargemCorte, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel59)
                        .addGap(18, 18, 18)
                        .addComponent(cboImpressoraReciboVias, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(80, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(jLabel49)
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(cboImpressoraCupom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(cboImpressoraFormatoPadrao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50)
                    .addComponent(cboImpressoraCupomTamanhoFonte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkImpressoraCupomNumeroItem)
                    .addComponent(chkImpressoraCupomCodigoItem)
                    .addComponent(chkImpressoraCupomUnidadeMedidaItem)
                    .addComponent(chkImpressoraCupomCabecalhoItem)
                    .addComponent(chkImpressoraCupomAcrescimoDescontoItem))
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkImpressoraCupomAssinaturaCliente)
                    .addComponent(chkImpressoraCupomMeiosPagamento))
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtImpressaoRodape, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40)
                    .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel57)
                        .addComponent(cboImpressoraCupomMargemCorte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel59)
                            .addComponent(cboImpressoraReciboVias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jPanel20.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel53.setBackground(new java.awt.Color(122, 138, 153));
        jLabel53.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel53.setForeground(java.awt.Color.white);
        jLabel53.setText("A4");
        jLabel53.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel53.setOpaque(true);

        jLabel54.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel54.setText("Impressora");

        cboImpressoraA4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        chkImpressoraA4Acrescimo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkImpressoraA4Acrescimo.setText("Exibir Acréscimo");
        chkImpressoraA4Acrescimo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkImpressoraA4AcrescimoActionPerformed(evt);
            }
        });

        chkImpressoraA4Observacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkImpressoraA4Observacao.setText("Exibir Observação");
        chkImpressoraA4Observacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkImpressoraA4ObservacaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel53, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1175, Short.MAX_VALUE)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jLabel54)
                        .addGap(18, 18, 18)
                        .addComponent(cboImpressoraA4, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(chkImpressoraA4Acrescimo)
                        .addGap(18, 18, 18)
                        .addComponent(chkImpressoraA4Observacao)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addComponent(jLabel53)
                .addGap(18, 18, 18)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54)
                    .addComponent(cboImpressoraA4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkImpressoraA4Acrescimo)
                    .addComponent(chkImpressoraA4Observacao))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel21.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel55.setBackground(new java.awt.Color(122, 138, 153));
        jLabel55.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel55.setForeground(java.awt.Color.white);
        jLabel55.setText("Etiqueta");
        jLabel55.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel55.setOpaque(true);

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel31.setText("Impressora");

        cboImpressoraEtiqueta.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel55, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1175, Short.MAX_VALUE)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel31)
                .addGap(18, 18, 18)
                .addComponent(cboImpressoraEtiqueta, javax.swing.GroupLayout.PREFERRED_SIZE, 462, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addComponent(jLabel55)
                .addGap(18, 18, 18)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(cboImpressoraEtiqueta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel38)
                    .addComponent(chkDesativarImpressao)
                    .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(chkDesativarImpressao)
                .addGap(18, 18, 18)
                .addComponent(jLabel38)
                .addContainerGap())
        );

        jTabbedPane.addTab("Impressão", jPanel4);

        txtNfseAliquota.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNfseAliquota.setName("decimal"); // NOI18N

        jLabel48.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel48.setText("Alíquota do ICMS");

        jLabel51.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel51.setText("Código do Serviço");

        txtNfseCodigoServico.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel48)
                        .addGap(18, 18, 18)
                        .addComponent(txtNfseAliquota, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel51)
                        .addGap(18, 18, 18)
                        .addComponent(txtNfseCodigoServico)))
                .addContainerGap(982, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(txtNfseAliquota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51)
                    .addComponent(txtNfseCodigoServico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(510, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("NFS-e", jPanel16);

        btnSat.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnSat.setText("Configurar SAT");
        btnSat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSatActionPerformed(evt);
            }
        });

        btnStatuSat.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnStatuSat.setText("Status SAT");
        btnStatuSat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStatuSatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSat)
                    .addComponent(btnStatuSat))
                .addContainerGap(1064, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSat)
                .addGap(18, 18, 18)
                .addComponent(btnStatuSat)
                .addContainerGap(506, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Sat", jPanel14);

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel17.setText("CNAE");

        txtCNAE.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCNAE.setName("inteiro"); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("IE (subst. tributária)");

        txtIESubstituicaoTributaria.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIESubstituicaoTributaria.setName("inteiro"); // NOI18N

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel21.setText("Regime Tributário");

        cboRegimeTributario.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel22.setText("Série");

        txtNfeSerie.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNfeSerie.setName("inteiro"); // NOI18N

        txtNfeProximoNumero.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNfeProximoNumero.setName("inteiro"); // NOI18N

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel23.setText("Próximo número");

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel24.setText("Tipo de ambiente");

        cboTipoAmbiente.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel25.setText("Natureza da Operação");

        cboNaturezaOperacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        cboTipoAtendimento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel26.setText("Tipo de Atendimento");

        jLabel34.setBackground(new java.awt.Color(122, 138, 153));
        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel34.setForeground(java.awt.Color.white);
        jLabel34.setText("Perfil NF-e");
        jLabel34.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel34.setOpaque(true);

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel27.setText("Destino da operação");

        cboDestinoOperacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        cboConsumidorFinal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel28.setText("Consumidor Final");

        btnStatusNfe.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnStatusNfe.setText("Status do Serviço");
        btnStatusNfe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStatusNfeActionPerformed(evt);
            }
        });

        btnInutilizarNfe.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnInutilizarNfe.setText("Inutlizar Numeração");
        btnInutilizarNfe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInutilizarNfeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addGap(23, 23, 23)
                                .addComponent(txtNfeSerie))
                            .addComponent(jLabel24))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(cboTipoAmbiente, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel21)
                                .addGap(18, 18, 18)
                                .addComponent(cboRegimeTributario, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addGap(18, 18, 18)
                                .addComponent(txtNfeProximoNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel25)
                                .addGap(18, 18, 18)
                                .addComponent(cboNaturezaOperacao, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(btnStatusNfe)
                                .addGap(18, 18, 18)
                                .addComponent(btnInutilizarNfe)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel28)
                                .addGap(18, 18, 18)
                                .addComponent(cboConsumidorFinal, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(21, 21, 21)
                                .addComponent(jLabel27)))
                        .addGap(18, 18, 18)
                        .addComponent(cboDestinoOperacao, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(18, 18, 18)
                        .addComponent(txtCNAE, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(txtIESubstituicaoTributaria, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 81, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addGap(18, 18, 18)
                        .addComponent(cboTipoAtendimento, 0, 394, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(cboRegimeTributario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(cboTipoAmbiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(txtCNAE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIESubstituicaoTributaria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtNfeSerie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(txtNfeProximoNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25)
                    .addComponent(cboNaturezaOperacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26)
                    .addComponent(cboTipoAtendimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(cboDestinoOperacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28)
                    .addComponent(cboConsumidorFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStatusNfe)
                    .addComponent(btnInutilizarNfe))
                .addContainerGap())
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtInformacoesAdicionaisFisco.setColumns(20);
        txtInformacoesAdicionaisFisco.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtInformacoesAdicionaisFisco.setRows(5);
        txtInformacoesAdicionaisFisco.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jScrollPane2.setViewportView(txtInformacoesAdicionaisFisco);

        jLabel32.setBackground(new java.awt.Color(122, 138, 153));
        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel32.setForeground(java.awt.Color.white);
        jLabel32.setText("Informações Adicionais de Interesse do Fisco");
        jLabel32.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel32.setOpaque(true);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
            .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtInformacoesComplementaresContribuinte.setColumns(20);
        txtInformacoesComplementaresContribuinte.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtInformacoesComplementaresContribuinte.setRows(5);
        txtInformacoesComplementaresContribuinte.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jScrollPane3.setViewportView(txtInformacoesComplementaresContribuinte);

        jLabel33.setBackground(new java.awt.Color(122, 138, 153));
        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel33.setForeground(java.awt.Color.white);
        jLabel33.setText("Informações Complementares de Interesse do Contribuinte");
        jLabel33.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel33.setOpaque(true);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3)
                .addContainerGap())
            .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3)
                .addContainerGap())
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel43.setBackground(new java.awt.Color(122, 138, 153));
        jLabel43.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel43.setForeground(java.awt.Color.white);
        jLabel43.setText("Certificado");
        jLabel43.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel43.setOpaque(true);

        jLabel41.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel41.setText("Tipo");

        cboCertificadoTipo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboCertificadoTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCertificadoTipoActionPerformed(evt);
            }
        });

        jLabel42.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel42.setText("Marca");

        cboCertificadoMarca.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel44.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel44.setText("Pin");

        txtCertificadoPin.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        btnConsultarCertificado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnConsultarCertificado.setText("Consultar");
        btnConsultarCertificado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultarCertificadoActionPerformed(evt);
            }
        });

        jLabel46.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel46.setForeground(java.awt.Color.red);
        jLabel46.setText("*salve antes de consultar");

        jLabel47.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel47.setForeground(java.awt.Color.red);
        jLabel47.setText("*Para Repositório por CNPJ, o CNPJ deve estar correto nos dados da Empresa");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel43, javax.swing.GroupLayout.DEFAULT_SIZE, 1175, Short.MAX_VALUE)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel41)
                        .addGap(18, 18, 18)
                        .addComponent(cboCertificadoTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel44)
                        .addGap(18, 18, 18)
                        .addComponent(txtCertificadoPin, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel42)
                        .addGap(18, 18, 18)
                        .addComponent(cboCertificadoMarca, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnConsultarCertificado)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jLabel43)
                .addGap(18, 18, 18)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(cboCertificadoTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44)
                    .addComponent(txtCertificadoPin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42)
                    .addComponent(cboCertificadoMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConsultarCertificado)
                    .addComponent(jLabel46))
                .addGap(18, 18, 18)
                .addComponent(jLabel47)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("NF-e", jPanel7);

        chkRevalidarAdministrador.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkRevalidarAdministrador.setText("Revalidar administrador para liberar recursos");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkRevalidarAdministrador)
                .addContainerGap(898, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkRevalidarAdministrador)
                .addContainerGap(553, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Diversos", jPanel2);

        jLabel56.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel56.setText("Caixa Principal desta Estação");

        cboCaixaPrincipal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel56)
                .addGap(18, 18, 18)
                .addComponent(cboCaixaPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(808, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56)
                    .addComponent(cboCaixaPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(551, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Financeiro", jPanel22);

        btnPatch.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnPatch.setText("Patch");
        btnPatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPatchActionPerformed(evt);
            }
        });

        btnBootstrap.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnBootstrap.setText("Bootstrap");
        btnBootstrap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBootstrapActionPerformed(evt);
            }
        });

        chkHabilitarSat.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkHabilitarSat.setText("Habilitar SAT");

        chkHabilitarNfe.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkHabilitarNfe.setText("Habilitar NFe");

        chkHabilitarOst.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkHabilitarOst.setText("Habilitar OS Transporte");

        chkHabilitarVeiculo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkHabilitarVeiculo.setText("Habilitar Veículo");

        chkHabilitarVendaPorFicha.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkHabilitarVendaPorFicha.setText("Habilitar Venda por Ficha");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setForeground(java.awt.Color.red);
        jLabel10.setText("Reinicie o sistema para validar estas configurações");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkHabilitarVeiculo)
                    .addComponent(btnBootstrap)
                    .addComponent(btnPatch)
                    .addComponent(chkHabilitarSat)
                    .addComponent(chkHabilitarNfe)
                    .addComponent(chkHabilitarOst)
                    .addComponent(chkHabilitarVendaPorFicha)
                    .addComponent(jLabel10))
                .addContainerGap(792, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnBootstrap)
                .addGap(18, 18, 18)
                .addComponent(btnPatch)
                .addGap(18, 18, 18)
                .addComponent(chkHabilitarSat)
                .addGap(18, 18, 18)
                .addComponent(chkHabilitarNfe)
                .addGap(18, 18, 18)
                .addComponent(chkHabilitarVeiculo)
                .addGap(18, 18, 18)
                .addComponent(chkHabilitarOst)
                .addGap(18, 18, 18)
                .addComponent(chkHabilitarVendaPorFicha)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 258, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addContainerGap())
        );

        jTabbedPane.addTab("Mindware", jPanel13);

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAjuda)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCancelar)
                        .addGap(18, 18, 18)
                        .addComponent(btnSalvar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnSalvar)
                        .addComponent(btnCancelar))
                    .addComponent(btnAjuda))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBootstrapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBootstrapActionPerformed
        
        new Toast("Dados sendo carregados... Aguarde a mensagem de término");
        new MeioDePagamentoDAO().bootstrap();

        new CaixaItemTipoDAO().bootstrap();
        new ProdutoOrigemDAO().bootstrap();
        new CfopDAO().bootstrap();
        new IcmsDAO().bootstrap();
        new IbptDAO().bootstrap();
        new NcmDAO().bootstrap();
        new CaixaPeriodoDAO().bootstrap();
        new SatErroOuAlertaDAO().bootstrap();
        new SatEstadoDAO().bootstrap();
        new UnidadeComercialDAO().bootstrap();
        new MovimentoFisicoTipoDAO().bootstrap();
        new VendaTipoDAO().bootstrap();

        JOptionPane.showMessageDialog(MAIN_VIEW, "Concluído");
        
    }//GEN-LAST:event_btnBootstrapActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        salvar();
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        singleInstance = null;
    }//GEN-LAST:event_formInternalFrameClosed

    private void btnSatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSatActionPerformed
        SATSetup satSetup = new SATSetup(MAIN_VIEW);
    }//GEN-LAST:event_btnSatActionPerformed

    private void btnStatuSatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStatuSatActionPerformed
        SATStatusOperacionalView satStatus = new SATStatusOperacionalView(MAIN_VIEW);
    }//GEN-LAST:event_btnStatuSatActionPerformed

    private void btnCepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCepActionPerformed
        pesquisarCep();
    }//GEN-LAST:event_btnCepActionPerformed

    private void txtCepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCepActionPerformed
        buscarEndereco();
    }//GEN-LAST:event_txtCepActionPerformed

    private void txtCodigoMunicipioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoMunicipioActionPerformed
        carregarMunicipio();
    }//GEN-LAST:event_txtCodigoMunicipioActionPerformed

    private void txtTelefone2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelefone2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTelefone2ActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    private void btnStatusNfeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStatusNfeActionPerformed
        NfeStatusServico status = new NfeStatusServico();
    }//GEN-LAST:event_btnStatusNfeActionPerformed

    private void btnPatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPatchActionPerformed
        PatchView patch = new PatchView();
    }//GEN-LAST:event_btnPatchActionPerformed

    private void jTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPaneStateChanged
        JTabbedPane tab = (JTabbedPane) evt.getSource();
        String tabAtual = tab.getTitleAt(tab.getSelectedIndex());
        if(tabAtual.equals("Mindware")) {
            String senha = JOptionPane.showInputDialog(MAIN_VIEW, "Geben Sie das Passwort ein", "Halt!", JOptionPane.WARNING_MESSAGE);
        
            if(senha == null || !senha.equals("753951")){
                jTabbedPane.setSelectedIndex(0);
            }
        }
    }//GEN-LAST:event_jTabbedPaneStateChanged

    private void btnConsultarCertificadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarCertificadoActionPerformed
        consultarCertificado();
    }//GEN-LAST:event_btnConsultarCertificadoActionPerformed

    private void cboCertificadoTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCertificadoTipoActionPerformed
        chavearCertificadoTipo();
    }//GEN-LAST:event_cboCertificadoTipoActionPerformed

    private void btnAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAjudaActionPerformed
        new AjudaView("sistema.html");
    }//GEN-LAST:event_btnAjudaActionPerformed

    private void btnInutilizarNfeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInutilizarNfeActionPerformed
        new NfeInutilizacaoView();
    }//GEN-LAST:event_btnInutilizarNfeActionPerformed

    private void chkVendaFuncionarioPorItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkVendaFuncionarioPorItemActionPerformed
        chavearVendaFuncionarioPorItem();
    }//GEN-LAST:event_chkVendaFuncionarioPorItemActionPerformed

    private void chkImpressoraA4AcrescimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkImpressoraA4AcrescimoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkImpressoraA4AcrescimoActionPerformed

    private void chkImpressoraA4ObservacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkImpressoraA4ObservacaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkImpressoraA4ObservacaoActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Telefone;
    private javax.swing.JLabel Telefone2;
    private javax.swing.JLabel Telefone3;
    private javax.swing.JButton btnAjuda;
    private javax.swing.JButton btnBootstrap;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnCep;
    private javax.swing.JButton btnConsultarCertificado;
    private javax.swing.JButton btnInutilizarNfe;
    private javax.swing.JButton btnPatch;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnSat;
    private javax.swing.JButton btnStatuSat;
    private javax.swing.JButton btnStatusNfe;
    private javax.swing.JComboBox<Object> cboCaixaPrincipal;
    private javax.swing.JComboBox<Object> cboCertificadoMarca;
    private javax.swing.JComboBox<String> cboCertificadoTipo;
    private javax.swing.JComboBox<Object> cboConsumidorFinal;
    private javax.swing.JComboBox<Object> cboDestinoOperacao;
    private javax.swing.JComboBox<String> cboImpressoraA4;
    private javax.swing.JComboBox<String> cboImpressoraCupom;
    private javax.swing.JComboBox<String> cboImpressoraCupomMargemCorte;
    private javax.swing.JComboBox<String> cboImpressoraCupomTamanhoFonte;
    private javax.swing.JComboBox<String> cboImpressoraEtiqueta;
    private javax.swing.JComboBox<String> cboImpressoraFormatoPadrao;
    private javax.swing.JComboBox<String> cboImpressoraReciboVias;
    private javax.swing.JComboBox<String> cboJurosTipo;
    private javax.swing.JComboBox<String> cboLayoutComandas;
    private javax.swing.JComboBox<Object> cboNaturezaOperacao;
    private javax.swing.JComboBox<Object> cboRegimeTributario;
    private javax.swing.JComboBox<String> cboTipoAmbiente;
    private javax.swing.JComboBox<Object> cboTipoAtendimento;
    private javax.swing.JComboBox<String> cboVendaPromissoriaTipo;
    private javax.swing.JComboBox<Object> cboVendaStatusInicial;
    private javax.swing.JCheckBox chkAbrirComandasIniciar;
    private javax.swing.JCheckBox chkAlertarGarantiaPorVeiculo;
    private javax.swing.JCheckBox chkBloquearCreditoExcedido;
    private javax.swing.JCheckBox chkBloquearParcelasEmAtraso;
    private javax.swing.JCheckBox chkDesativarImpressao;
    private javax.swing.JCheckBox chkHabilitarNfe;
    private javax.swing.JCheckBox chkHabilitarOst;
    private javax.swing.JCheckBox chkHabilitarSat;
    private javax.swing.JCheckBox chkHabilitarVeiculo;
    private javax.swing.JCheckBox chkHabilitarVendaPorFicha;
    private javax.swing.JCheckBox chkImpressoraA4Acrescimo;
    private javax.swing.JCheckBox chkImpressoraA4Observacao;
    private javax.swing.JCheckBox chkImpressoraCupomAcrescimoDescontoItem;
    private javax.swing.JCheckBox chkImpressoraCupomAssinaturaCliente;
    private javax.swing.JCheckBox chkImpressoraCupomCabecalhoItem;
    private javax.swing.JCheckBox chkImpressoraCupomCodigoItem;
    private javax.swing.JCheckBox chkImpressoraCupomMeiosPagamento;
    private javax.swing.JCheckBox chkImpressoraCupomNumeroItem;
    private javax.swing.JCheckBox chkImpressoraCupomUnidadeMedidaItem;
    private javax.swing.JCheckBox chkInsercaoDireta;
    private javax.swing.JCheckBox chkModoBalcao;
    private javax.swing.JCheckBox chkRevalidarAdministrador;
    private javax.swing.JCheckBox chkValidarEstoque;
    private javax.swing.JCheckBox chkVendaBonificacaoHabilitar;
    private javax.swing.JCheckBox chkVendaFuncionarioObrigatorio;
    private javax.swing.JCheckBox chkVendaFuncionarioPorItem;
    private javax.swing.JCheckBox chkVendaFuncionarioPorItemProduto;
    private javax.swing.JCheckBox chkVendaFuncionarioPorItemServico;
    private javax.swing.JCheckBox chkVendaImprimirProdutosServicosSeparados;
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
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTextField txtBairro;
    private javax.swing.JFormattedTextField txtCNAE;
    private javax.swing.JFormattedTextField txtCNPJ;
    private javax.swing.JFormattedTextField txtCep;
    private javax.swing.JTextField txtCertificadoPin;
    private javax.swing.JTextField txtCodigoMunicipio;
    private javax.swing.JTextField txtComplemento;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtEndereco;
    private javax.swing.JFormattedTextField txtIESubstituicaoTributaria;
    private javax.swing.JFormattedTextField txtIe;
    private javax.swing.JFormattedTextField txtIm;
    private javax.swing.JTextField txtImpressaoRodape;
    private javax.swing.JTextArea txtInformacoesAdicionaisFisco;
    private javax.swing.JTextArea txtInformacoesComplementaresContribuinte;
    private javax.swing.JFormattedTextField txtJuros;
    private javax.swing.JFormattedTextField txtLimiteCredito;
    private javax.swing.JFormattedTextField txtMulta;
    private javax.swing.JTextField txtMunicipio;
    private javax.swing.JFormattedTextField txtNfeProximoNumero;
    private javax.swing.JFormattedTextField txtNfeSerie;
    private javax.swing.JFormattedTextField txtNfseAliquota;
    private javax.swing.JTextField txtNfseCodigoServico;
    private javax.swing.JTextField txtNomeFantasia;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JFormattedTextField txtNumeroComandas;
    private javax.swing.JTextField txtRazaoSocial;
    private javax.swing.JFormattedTextField txtTelefone;
    private javax.swing.JFormattedTextField txtTelefone2;
    private javax.swing.JTextField txtUF;
    // End of variables declaration//GEN-END:variables
}
