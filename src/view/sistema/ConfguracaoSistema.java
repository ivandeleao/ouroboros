
package view.sistema;

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
import model.nosql.ImpressoraFormato;
import model.mysql.bean.principal.Recurso;
import model.mysql.dao.endereco.CidadeDAO;
import model.mysql.dao.endereco.EnderecoDAO;
import model.mysql.dao.principal.CaixaItemTipoDAO;
import model.mysql.dao.principal.CaixaPeriodoDAO;
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
import model.nosql.LayoutComandas;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.PARCELA_MULTA;
import static ouroboros.Ouroboros.USUARIO;
import util.Decimal;
import util.JSwing;
import view.Toast;
import view.sat.SATSetup;
import view.sat.SATStatusOperacionalView;
import static ouroboros.Ouroboros.IMPRESSORA_CUPOM;
import static ouroboros.Ouroboros.IMPRESSORA_A4;
import static ouroboros.Ouroboros.IMPRESSORA_FORMATO_PADRAO;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.SISTEMA_REVALIDAR_ADMINISTRADOR;
import static ouroboros.Ouroboros.VENDA_BLOQUEAR_CREDITO_EXCEDIDO;
import static ouroboros.Ouroboros.VENDA_LAYOUT_COMANDAS;
import util.MwConfig;
import util.MwString;
import view.endereco.EnderecoPesquisaView;

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
        
        buscarMunicipio();
        
        
        //Pessoa----------------------------------------------------------------
        txtLimiteCredito.setText(Decimal.toString(Ouroboros.CLIENTE_LIMITE_CREDITO));
        
        
        
        
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
        
        for(LayoutComandas layout : LayoutComandas.values()) {
            cboLayoutComandas.addItem(layout.toString());
            if(layout.toString().equals(VENDA_LAYOUT_COMANDAS)) {
                cboLayoutComandas.setSelectedItem(layout.toString());
            }
        }
        
        chkBloquearParcelasEmAtraso.setSelected(Ouroboros.VENDA_BLOQUEAR_PARCELAS_EM_ATRASO);
        
        chkBloquearCreditoExcedido.setSelected(Ouroboros.VENDA_BLOQUEAR_CREDITO_EXCEDIDO);
        
        chkExibirVeiculo.setSelected(Ouroboros.VENDA_EXIBIR_VEICULO);
        
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
        
        //Impressora A4
        cboImpressoraA4.addItem("Não definida");
        for (PrintService ps : pservices) {
            cboImpressoraA4.addItem(ps.getName());
            if (ps.getName().equals(IMPRESSORA_A4)) {
                cboImpressoraA4.setSelectedItem(ps.getName());
            }
        }
        
        //Impressora etiqueta
        cboImpressoraEtiqueta.addItem("Não definida");
        for (PrintService ps : pservices) {
            cboImpressoraEtiqueta.addItem(ps.getName());
            if (ps.getName().equals(Ouroboros.IMPRESSORA_ETIQUETA)) {
                cboImpressoraEtiqueta.setSelectedItem(ps.getName());
            }
        }
        
        //Impressora Formato Padrão
        for(ImpressoraFormato formato : ImpressoraFormato.values()) {
            cboImpressoraFormatoPadrao.addItem(formato.toString());
            if(formato.toString().equals(IMPRESSORA_FORMATO_PADRAO)) {
                cboImpressoraFormatoPadrao.setSelectedItem(formato.toString());
            }
        }
        
        txtImpressaoRodape.setText(Ouroboros.IMPRESSAO_RODAPE);
        
        chkDesativarImpressao.setSelected(Ouroboros.IMPRESSORA_DESATIVAR);
        
        //Fim Impressão---------------------------------------------------------
        
        
        //Sistema---------------------------------------------------------------
        chkHabilitarSat.setSelected(Ouroboros.SAT_HABILITAR);
        chkRevalidarAdministrador.setSelected(Ouroboros.SISTEMA_REVALIDAR_ADMINISTRADOR);
        
        //NFe-------------------------------------------------------------------
        chkHabilitarNFe.setSelected(Ouroboros.NFE_HABILITAR);
        txtNfeProximoNumero.setText(Ouroboros.NFE_PROXIMO_NUMERO.toString());
        
        carregarRegimeTributario();
        carregarDestinoOperacao();
        carregarNaturezaOperacao();
        carregarTipoAtendimento();
        carregarConsumidorFinal();
    }
    
    private void carregarRegimeTributario() {

        for (RegimeTributario r : new RegimeTributarioDAO().findAll()) {
            cboRegimeTributario.addItem(r);
        }
        /*if (produto != null && produto.getRegimeTributario() != null) {
            cboRegimeTributario.setSelectedItem(produto.getRegimeTributario());
        }*/
    }
    
    private void carregarDestinoOperacao() {

        for (DestinoOperacao d : new DestinoOperacaoDAO().findAll()) {
            cboDestinoOperacao.addItem(d);
        }
        /*if (produto != null && produto.getTipoAtendimento() != null) {
            cboTipoAtendimento.setSelectedItem(produto.getTipoAtendimento());
        }*/
    }
    
    private void carregarNaturezaOperacao() {

        for (NaturezaOperacao n : new NaturezaOperacaoDAO().findAll()) {
            cboNaturezaOperacao.addItem(n);
        }
        /*if (produto != null && produto.getTipoAtendimento() != null) {
            cboTipoAtendimento.setSelectedItem(produto.getTipoAtendimento());
        }*/
    }
    
    private void carregarTipoAtendimento() {

        for (TipoAtendimento t : new TipoAtendimentoDAO().findAll()) {
            cboTipoAtendimento.addItem(t);
        }
        /*if (produto != null && produto.getTipoAtendimento() != null) {
            cboTipoAtendimento.setSelectedItem(produto.getTipoAtendimento());
        }*/
    }
    
    private void carregarConsumidorFinal() {

        for (ConsumidorFinal t : new ConsumidorFinalDAO().findAll()) {
            cboConsumidorFinal.addItem(t);
        }
        /*if (produto != null && produto.getConsumidorFinal() != null) {
            cboConsumidorFinal.setSelectedItem(produto.getConsumidorFinal());
        }*/
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
            
            
            
            //Pessoa
            Ouroboros.CLIENTE_LIMITE_CREDITO = Decimal.fromString(txtLimiteCredito.getText());
            cDAO.save(new Constante("CLIENTE_LIMITE_CREDITO", String.valueOf(Ouroboros.CLIENTE_LIMITE_CREDITO)));
            
            
            
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
            
            Ouroboros.VENDA_BLOQUEAR_PARCELAS_EM_ATRASO = chkBloquearParcelasEmAtraso.isSelected();
            cDAO.save(new Constante("VENDA_BLOQUEAR_PARCELAS_EM_ATRASO", String.valueOf(Ouroboros.VENDA_BLOQUEAR_PARCELAS_EM_ATRASO)));
            
            Ouroboros.VENDA_BLOQUEAR_CREDITO_EXCEDIDO = chkBloquearCreditoExcedido.isSelected();
            cDAO.save(new Constante("VENDA_BLOQUEAR_CREDITO_EXCEDIDO", String.valueOf(Ouroboros.VENDA_BLOQUEAR_CREDITO_EXCEDIDO)));
            
            Ouroboros.VENDA_EXIBIR_VEICULO = chkExibirVeiculo.isSelected();
            cDAO.save(new Constante("VENDA_EXIBIR_VEICULO", String.valueOf(Ouroboros.VENDA_EXIBIR_VEICULO)));
            
            Ouroboros.SISTEMA_MODO_BALCAO = chkModoBalcao.isSelected();
            MwConfig.setValue("SISTEMA_MODO_BALCAO", String.valueOf(Ouroboros.SISTEMA_MODO_BALCAO));
            
            Ouroboros.VENDA_ABRIR_COMANDAS_AO_INICIAR = chkAbrirComandasIniciar.isSelected();
            MwConfig.setValue("VENDA_ABRIR_COMANDAS_AO_INICIAR", String.valueOf(Ouroboros.VENDA_ABRIR_COMANDAS_AO_INICIAR));
            
            //Fim Venda---------------------------------------------------------
            
            
            //Impressão---------------------------------------------------------
            //Alterado para config local
            Ouroboros.IMPRESSORA_CUPOM = cboImpressoraCupom.getSelectedItem().toString();
            MwConfig.setValue("IMPRESSORA_CUPOM", Ouroboros.IMPRESSORA_CUPOM);
            
            Ouroboros.IMPRESSORA_A4 = cboImpressoraA4.getSelectedItem().toString();
            MwConfig.setValue("IMPRESSORA_A4", Ouroboros.IMPRESSORA_A4);
            
            Ouroboros.IMPRESSORA_ETIQUETA = cboImpressoraEtiqueta.getSelectedItem().toString();
            MwConfig.setValue("IMPRESSORA_ETIQUETA", Ouroboros.IMPRESSORA_ETIQUETA);
            
            Ouroboros.IMPRESSORA_FORMATO_PADRAO = cboImpressoraFormatoPadrao.getSelectedItem().toString();
            MwConfig.setValue("IMPRESSORA_FORMATO_PADRAO", Ouroboros.IMPRESSORA_FORMATO_PADRAO);
            
            Ouroboros.IMPRESSORA_DESATIVAR = chkDesativarImpressao.isSelected();
            MwConfig.setValue("IMPRESSORA_DESATIVAR", String.valueOf(Ouroboros.IMPRESSORA_DESATIVAR));
            
            
            Ouroboros.IMPRESSAO_RODAPE = txtImpressaoRodape.getText().trim();
            cDAO.save(new Constante("IMPRESSAO_RODAPE", String.valueOf(Ouroboros.IMPRESSAO_RODAPE)));
            
            
            //Diversos----------------------------------------------------------
            Ouroboros.SAT_HABILITAR = chkHabilitarSat.isSelected();
            cDAO.save(new Constante("SAT_HABILITAR", String.valueOf(Ouroboros.SAT_HABILITAR)));

            Ouroboros.SISTEMA_REVALIDAR_ADMINISTRADOR = chkRevalidarAdministrador.isSelected();
            cDAO.save(new Constante("SISTEMA_REVALIDAR_ADMINISTRADOR", String.valueOf(Ouroboros.SISTEMA_REVALIDAR_ADMINISTRADOR)));
            
            
            //NFe---------------------------------------------------------------
            Ouroboros.NFE_HABILITAR = chkHabilitarNFe.isSelected();
            cDAO.save(new Constante("NFE_HABILITAR", String.valueOf(Ouroboros.NFE_HABILITAR)));
            
            
            Ouroboros.NFE_PROXIMO_NUMERO = Integer.valueOf(txtNumeroComandas.getText());
            cDAO.save(new Constante("VENDA_NUMERO_COMANDAS", String.valueOf(Ouroboros.VENDA_NUMERO_COMANDAS)));
            
            JOptionPane.showMessageDialog(rootPane, "Dados salvos", null, JOptionPane.INFORMATION_MESSAGE);
        } catch(HeadlessException e){
            JOptionPane.showMessageDialog(rootPane, e, "Erro", JOptionPane.ERROR_MESSAGE);
        }
        
        
    }
    
    private void buscarEndereco() {
        String cep = MwString.soNumeros(txtCep.getText());
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

    private void buscarMunicipio() {
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
        jPanel3 = new javax.swing.JPanel();
        chkInsercaoDireta = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        txtMulta = new javax.swing.JFormattedTextField();
        txtJuros = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        cboJurosTipo = new javax.swing.JComboBox<>();
        txtNumeroComandas = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        chkBloquearParcelasEmAtraso = new javax.swing.JCheckBox();
        chkBloquearCreditoExcedido = new javax.swing.JCheckBox();
        chkExibirVeiculo = new javax.swing.JCheckBox();
        jPanel12 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        chkModoBalcao = new javax.swing.JCheckBox();
        chkAbrirComandasIniciar = new javax.swing.JCheckBox();
        jLabel39 = new javax.swing.JLabel();
        cboLayoutComandas = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        cboImpressoraCupom = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        chkDesativarImpressao = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        cboImpressoraA4 = new javax.swing.JComboBox<>();
        cboImpressoraFormatoPadrao = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        cboImpressoraEtiqueta = new javax.swing.JComboBox<>();
        jLabel38 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        txtImpressaoRodape = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        btnSat = new javax.swing.JButton();
        btnStatuSat = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        txtCNAE = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtCNPJ1 = new javax.swing.JFormattedTextField();
        jLabel21 = new javax.swing.JLabel();
        cboRegimeTributario = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        txtCNAE1 = new javax.swing.JFormattedTextField();
        txtNfeProximoNumero = new javax.swing.JFormattedTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
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
        chkHabilitarNFe = new javax.swing.JCheckBox();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtObservacao = new javax.swing.JTextArea();
        jLabel32 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtObservacao1 = new javax.swing.JTextArea();
        jLabel33 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        chkRevalidarAdministrador = new javax.swing.JCheckBox();
        jPanel13 = new javax.swing.JPanel();
        btnPatch = new javax.swing.JButton();
        btnBootstrap = new javax.swing.JButton();
        chkHabilitarSat = new javax.swing.JCheckBox();
        btnAtivar = new javax.swing.JButton();

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
                .addContainerGap(35, Short.MAX_VALUE))
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
                .addContainerGap(205, Short.MAX_VALUE))
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
                .addContainerGap(507, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Pessoa", jPanel11);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(javax.swing.UIManager.getDefaults().getColor("TextField.disabledBackground"));
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("De acordo com o artigo 52, parágrafo primeiro do Código de Defesa do Consumidor, Lei 8.078/90, a cobrança da multa não pode ser maior que 2%.\nSegundo o art. 406 do Código Civil e o artigo 161, parágrafo primeiro, do Código Tributário Nacional, os juros de mora devem ser cobrados a, no máximo, 1% ao mês.");
        jTextArea1.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane1.setViewportView(jTextArea1);

        chkBloquearParcelasEmAtraso.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkBloquearParcelasEmAtraso.setText("Bloquear faturamento com parcelas em atraso");

        chkBloquearCreditoExcedido.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkBloquearCreditoExcedido.setText("Bloquear faturamento com limite de crédito excedido");

        chkExibirVeiculo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkExibirVeiculo.setText("Exibir Veículo");

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
                .addGap(0, 46, Short.MAX_VALUE))
        );

        jLabel39.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel39.setText("Layout Comandas");

        cboLayoutComandas.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkInsercaoDireta)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel39))
                                .addGap(41, 41, 41)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cboLayoutComandas, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtMulta, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtNumeroComandas, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtJuros, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addComponent(cboJurosTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 724, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkBloquearParcelasEmAtraso)
                            .addComponent(chkBloquearCreditoExcedido)
                            .addComponent(chkExibirVeiculo))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(chkInsercaoDireta)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtMulta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(cboJurosTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtJuros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNumeroComandas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel39)
                            .addComponent(cboLayoutComandas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addComponent(chkBloquearParcelasEmAtraso)
                .addGap(18, 18, 18)
                .addComponent(chkBloquearCreditoExcedido)
                .addGap(13, 13, 13)
                .addComponent(chkExibirVeiculo)
                .addGap(18, 18, 18)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane.addTab("Venda", jPanel3);

        cboImpressoraCupom.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("Cupom Não Fiscal");

        chkDesativarImpressao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkDesativarImpressao.setText("Desativar impressão para testes");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel10.setText("A4");

        cboImpressoraA4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        cboImpressoraFormatoPadrao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setText("Formato");

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel31.setText("Etiqueta Térmica");

        cboImpressoraEtiqueta.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel38.setForeground(java.awt.Color.red);
        jLabel38.setText("Impressora SAT é configurada junto ao SAT");

        jLabel40.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel40.setText("Texto de Rodapé");

        txtImpressaoRodape.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addContainerGap(1081, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel38)
                            .addComponent(chkDesativarImpressao)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel31)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel40))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(cboImpressoraA4, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                        .addComponent(cboImpressoraCupom, javax.swing.GroupLayout.PREFERRED_SIZE, 466, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel11)
                                        .addGap(18, 18, 18)
                                        .addComponent(cboImpressoraFormatoPadrao, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(cboImpressoraEtiqueta, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtImpressaoRodape))))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(cboImpressoraCupom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(cboImpressoraFormatoPadrao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(cboImpressoraA4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(cboImpressoraEtiqueta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtImpressaoRodape, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40))
                .addGap(99, 99, 99)
                .addComponent(chkDesativarImpressao)
                .addGap(18, 18, 18)
                .addComponent(jLabel38)
                .addContainerGap(225, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Impressão", jPanel4);

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
                .addContainerGap(462, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Sat", jPanel14);

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel17.setText("CNAE");

        txtCNAE.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCNAE.setName("inteiro"); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("IE (subst. tributária)");

        txtCNPJ1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCNPJ1.setName("inteiro"); // NOI18N

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel21.setText("Regime Tributário");

        cboRegimeTributario.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel22.setText("Série");

        txtCNAE1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCNAE1.setName("inteiro"); // NOI18N

        txtNfeProximoNumero.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNfeProximoNumero.setName("inteiro"); // NOI18N

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel23.setText("Próxima número");

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel24.setText("Tipo de ambiente");

        jComboBox2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

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

        chkHabilitarNFe.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkHabilitarNFe.setText("Habilitar NFe");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addComponent(jLabel22)
                                        .addGap(23, 23, 23)
                                        .addComponent(txtCNAE1))
                                    .addComponent(jLabel24))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                                        .addComponent(cboNaturezaOperacao, 0, 121, Short.MAX_VALUE))))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel28)
                                .addGap(18, 18, 18)
                                .addComponent(cboConsumidorFinal, 0, 143, Short.MAX_VALUE)
                                .addGap(21, 21, 21)
                                .addComponent(jLabel27)
                                .addGap(18, 18, 18)
                                .addComponent(cboDestinoOperacao, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(122, 122, 122)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addGap(18, 18, 18)
                                .addComponent(txtCNAE, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(txtCNPJ1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addGap(18, 18, 18)
                                .addComponent(cboTipoAtendimento, 0, 360, Short.MAX_VALUE))))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(btnStatusNfe)
                        .addGap(18, 18, 18)
                        .addComponent(chkHabilitarNFe)
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(txtCNAE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCNPJ1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtCNAE1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStatusNfe)
                    .addComponent(chkHabilitarNFe))
                .addContainerGap())
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtObservacao.setColumns(20);
        txtObservacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtObservacao.setRows(5);
        txtObservacao.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jScrollPane2.setViewportView(txtObservacao);

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
            .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtObservacao1.setColumns(20);
        txtObservacao1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtObservacao1.setRows(5);
        txtObservacao1.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jScrollPane3.setViewportView(txtObservacao1);

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
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(34, 34, 34))
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
                .addContainerGap(509, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Diversos", jPanel2);

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

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBootstrap)
                    .addComponent(btnPatch)
                    .addComponent(chkHabilitarSat))
                .addContainerGap(1088, Short.MAX_VALUE))
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
                .addContainerGap(419, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Mindware", jPanel13);

        btnAtivar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnAtivar.setText("Informar Chave de Ativação");
        btnAtivar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtivarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1204, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAtivar)
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
                .addComponent(jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 572, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvar)
                    .addComponent(btnCancelar)
                    .addComponent(btnAtivar))
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
        buscarMunicipio();
    }//GEN-LAST:event_txtCodigoMunicipioActionPerformed

    private void txtTelefone2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelefone2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTelefone2ActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    private void btnAtivarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtivarActionPerformed
        AtivarView ativar = new AtivarView();
    }//GEN-LAST:event_btnAtivarActionPerformed

    private void btnStatusNfeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStatusNfeActionPerformed
        NfeStatusServico status = new NfeStatusServico();
    }//GEN-LAST:event_btnStatusNfeActionPerformed

    private void btnPatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPatchActionPerformed
        PatchView patch = new PatchView();
    }//GEN-LAST:event_btnPatchActionPerformed

    private void jTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPaneStateChanged
        JTabbedPane tab = (JTabbedPane) evt.getSource();
        System.out.println("change: " + tab.getTitleAt(tab.getSelectedIndex()) );
        String tabAtual = tab.getTitleAt(tab.getSelectedIndex());
        if(tabAtual.equals("Mindware")) {
            String senha = JOptionPane.showInputDialog(MAIN_VIEW, "Geben Sie das Passwort ein", "Halt!", JOptionPane.WARNING_MESSAGE);
        
            if(senha != null && !senha.equals("753951")){
                jTabbedPane.setSelectedIndex(0);
            }
        }
    }//GEN-LAST:event_jTabbedPaneStateChanged



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Telefone;
    private javax.swing.JLabel Telefone2;
    private javax.swing.JLabel Telefone3;
    private javax.swing.JButton btnAtivar;
    private javax.swing.JButton btnBootstrap;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnCep;
    private javax.swing.JButton btnPatch;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnSat;
    private javax.swing.JButton btnStatuSat;
    private javax.swing.JButton btnStatusNfe;
    private javax.swing.JComboBox<Object> cboConsumidorFinal;
    private javax.swing.JComboBox<Object> cboDestinoOperacao;
    private javax.swing.JComboBox<String> cboImpressoraA4;
    private javax.swing.JComboBox<String> cboImpressoraCupom;
    private javax.swing.JComboBox<String> cboImpressoraEtiqueta;
    private javax.swing.JComboBox<String> cboImpressoraFormatoPadrao;
    private javax.swing.JComboBox<String> cboJurosTipo;
    private javax.swing.JComboBox<String> cboLayoutComandas;
    private javax.swing.JComboBox<Object> cboNaturezaOperacao;
    private javax.swing.JComboBox<Object> cboRegimeTributario;
    private javax.swing.JComboBox<Object> cboTipoAtendimento;
    private javax.swing.JCheckBox chkAbrirComandasIniciar;
    private javax.swing.JCheckBox chkBloquearCreditoExcedido;
    private javax.swing.JCheckBox chkBloquearParcelasEmAtraso;
    private javax.swing.JCheckBox chkDesativarImpressao;
    private javax.swing.JCheckBox chkExibirVeiculo;
    private javax.swing.JCheckBox chkHabilitarNFe;
    private javax.swing.JCheckBox chkHabilitarSat;
    private javax.swing.JCheckBox chkInsercaoDireta;
    private javax.swing.JCheckBox chkModoBalcao;
    private javax.swing.JCheckBox chkRevalidarAdministrador;
    private javax.swing.JComboBox<String> jComboBox2;
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
    private javax.swing.JLabel jLabel5;
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
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField txtBairro;
    private javax.swing.JFormattedTextField txtCNAE;
    private javax.swing.JFormattedTextField txtCNAE1;
    private javax.swing.JFormattedTextField txtCNPJ;
    private javax.swing.JFormattedTextField txtCNPJ1;
    private javax.swing.JFormattedTextField txtCep;
    private javax.swing.JTextField txtCodigoMunicipio;
    private javax.swing.JTextField txtComplemento;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtEndereco;
    private javax.swing.JFormattedTextField txtIe;
    private javax.swing.JFormattedTextField txtIm;
    private javax.swing.JTextField txtImpressaoRodape;
    private javax.swing.JFormattedTextField txtJuros;
    private javax.swing.JFormattedTextField txtLimiteCredito;
    private javax.swing.JFormattedTextField txtMulta;
    private javax.swing.JTextField txtMunicipio;
    private javax.swing.JFormattedTextField txtNfeProximoNumero;
    private javax.swing.JTextField txtNomeFantasia;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JFormattedTextField txtNumeroComandas;
    private javax.swing.JTextArea txtObservacao;
    private javax.swing.JTextArea txtObservacao1;
    private javax.swing.JTextField txtRazaoSocial;
    private javax.swing.JFormattedTextField txtTelefone;
    private javax.swing.JFormattedTextField txtTelefone2;
    private javax.swing.JTextField txtUF;
    // End of variables declaration//GEN-END:variables
}
