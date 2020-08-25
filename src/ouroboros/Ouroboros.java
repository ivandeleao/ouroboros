/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ouroboros;

import br.com.swconsultoria.nfe.dom.enuns.AmbienteEnum;
import connection.ConnectionFactory;
import static java.awt.Frame.MAXIMIZED_BOTH;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.EntityManager;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import model.mysql.bean.principal.Constante;
import model.mysql.bean.principal.Usuario;
import model.bootstrap.dao.NcmBsDAO;
import model.mysql.bean.fiscal.nfe.ConsumidorFinal;
import model.mysql.bean.fiscal.nfe.DestinoOperacao;
import model.mysql.bean.fiscal.nfe.NaturezaOperacao;
import model.mysql.bean.fiscal.nfe.RegimeTributario;
import model.mysql.bean.fiscal.nfe.TipoAtendimento;
import model.mysql.bean.principal.documento.VendaStatus;
import model.mysql.dao.fiscal.CofinsDAO;
import model.mysql.dao.fiscal.IbptDAO;
import model.mysql.dao.fiscal.IcmsDAO;
import model.mysql.dao.fiscal.MeioDePagamentoDAO;
import model.mysql.dao.fiscal.NcmDAO;
import model.mysql.dao.fiscal.PisDAO;
import model.mysql.dao.fiscal.SatCupomTipoDAO;
import model.mysql.dao.fiscal.nfe.ConsumidorFinalDAO;
import model.mysql.dao.fiscal.nfe.DestinoOperacaoDAO;
import model.mysql.dao.fiscal.nfe.FinalidadeEmissaoDAO;
import model.mysql.dao.fiscal.nfe.ModalidadeBcIcmsDAO;
import model.mysql.dao.fiscal.nfe.ModalidadeBcIcmsStDAO;
import model.mysql.dao.fiscal.nfe.ModalidadeFreteDAO;
import model.mysql.dao.fiscal.nfe.MotivoDesoneracaoDAO;
import model.mysql.dao.fiscal.nfe.NaturezaOperacaoDAO;
import model.mysql.dao.fiscal.nfe.RegimeTributarioDAO;
import model.mysql.dao.fiscal.nfe.TipoAtendimentoDAO;
import model.mysql.dao.fiscal.nfe.TipoContribuinteDAO;
import model.mysql.dao.fiscal.nfe.TipoEmissaoDAO;
import model.mysql.dao.principal.financeiro.CaixaItemTipoDAO;
import model.mysql.dao.principal.ConstanteDAO;
import model.mysql.dao.principal.TipoOperacaoDAO;
import model.mysql.dao.principal.RecursoDAO;
import model.mysql.dao.principal.UsuarioDAO;
import model.mysql.dao.principal.VendaDAO;
import model.mysql.dao.principal.VendaTipoDAO;
import model.mysql.dao.principal.catalogo.ProdutoTipoDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_LEFT;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import util.Atualizacao;
import util.DateTime;
import util.Decimal;
import util.MwConfig;
import util.Numero;
import util.Sistema;
import util.entities.DocumentoUtil;
import view.LoginView;
import view.MainView;
import view.Toast;
import view.sistema.AtivarView;
import java.util.TimeZone;
import model.mysql.bean.principal.financeiro.Conta;
import model.mysql.dao.fiscal.AnpDAO;
import model.mysql.dao.fiscal.IpiDAO;
import model.mysql.dao.principal.financeiro.ContaDAO;

/**
 *
 * @author ivand
 */
public class Ouroboros {
    public static String SISTEMA_ID;
    public static LocalDate SISTEMA_VERSAO;
    public static String SISTEMA_CHAVE; //validade id - dv
    public static Boolean SISTEMA_REVALIDAR_ADMINISTRADOR;
    
    public static String APP_VERSION = Atualizacao.getUltimaData().toString(); //"20190528";
    public static String APP_PATH = new File(".").getAbsolutePath();
    
    public static String SERVER = MwConfig.getValue("server");
    
    public static String DATABASE_NAME = "ouroboros";
    public static String DATABASE_USER = "b3";
    public static String DATABASE_PASSWORD = "m1ndw4r3!";
    
    public static final String MW_NOME_FANTASIA = "Mindware";
    public static final String MW_WEBSITE = "mwdesenvolvimento.com.br";
    public static final String MW_FONES = "(19)3913.5762 / Whatsapp (19)99887.4389";
    public static final String SISTEMA_NOME = "Mindware B3";
    public static final String SISTEMA_ASSINATURA = SISTEMA_NOME + " " + MW_WEBSITE;
    
    public static final int MENU_MIN_WIDTH = 50;
    public static final int MENU_MAX_WIDTH = 300;
    public static final int TOOLBAR_HEIGHT = 30;
    
    public static int SCREEN_WIDTH = 0;
    public static int SCREEN_HEIGHT = 0;
    
    public static MainView MAIN_VIEW;
    
    public static ConnectionFactory CONNECTION_FACTORY;
    
    public static EntityManager em;
    public static EntityManager emBs;
    
    public static String EMPRESA_NOME_FANTASIA;
    public static String EMPRESA_RAZAO_SOCIAL;
    public static String EMPRESA_CNPJ;
    public static String EMPRESA_IE;
    public static String EMPRESA_IM;
    public static String EMPRESA_TELEFONE;
    public static String EMPRESA_TELEFONE2;
    public static String EMPRESA_EMAIL;
    
    public static String EMPRESA_ENDERECO_CEP;
    public static String EMPRESA_ENDERECO;
    public static String EMPRESA_ENDERECO_NUMERO;
    public static String EMPRESA_ENDERECO_COMPLEMENTO;
    public static String EMPRESA_ENDERECO_BAIRRO;
    public static String EMPRESA_ENDERECO_CODIGO_MUNICIPIO;
    
    
    public static String IMPRESSORA_CUPOM;
    public static Float IMPRESSORA_CUPOM_TAMANHO_FONTE;
    public static Boolean IMPRESSORA_CUPOM_EXIBIR_CABECALHO_ITEM;
    public static Boolean IMPRESSORA_CUPOM_EXIBIR_NUMERO_ITEM;
    public static Boolean IMPRESSORA_CUPOM_EXIBIR_CODIGO_ITEM;
    public static Boolean IMPRESSORA_CUPOM_EXIBIR_UNIDADE_MEDIDA_ITEM;
    public static Boolean IMPRESSORA_CUPOM_EXIBIR_ACRESCIMO_DESCONTO_ITEM;
    public static Boolean IMPRESSORA_CUPOM_EXIBIR_ASSINATURA_CLIENTE;
    public static Boolean IMPRESSORA_CUPOM_EXIBIR_MEIOS_PAGAMENTO;
    public static String IMPRESSORA_A4;
    public static Boolean IMPRESSORA_A4_EXIBIR_ACRESCIMO;
    public static Boolean IMPRESSORA_A4_EXIBIR_OBSERVACAO;
    public static String IMPRESSORA_ETIQUETA;
    public static String IMPRESSORA_FORMATO_PADRAO;
    public static Boolean IMPRESSORA_DESATIVAR;
    public static String IMPRESSAO_RODAPE;
    public static Integer IMPRESSORA_CUPOM_MARGEM_CORTE;
    public static Integer IMPRESSORA_RECIBO_VIAS;
    
    public static BigDecimal NFSE_ALIQUOTA;
    public static String NFSE_CODIGO_SERVICO;
    
    public static String SOFTWARE_HOUSE_CNPJ;
    public static String TO_SAT_PATH;
    public static String FROM_SAT_PATH;
    public static Boolean SAT_HABILITAR;
    public static String SAT_DLL;
    public static String SAT_CODIGO_ATIVACAO;
    public static String SAT_SIGN_AC;
    public static String SAT_PRINTER;
    public static String SAT_LAYOUT;
    
    public static Integer SAT_MARGEM_ESQUERDA;
    public static Integer SAT_MARGEM_DIREITA;
    public static Integer SAT_MARGEM_SUPERIOR;
    public static Integer SAT_MARGEM_INFERIOR;
    
    public static Boolean NFE_HABILITAR;
    public static String NFE_PATH;
    public static Integer NFE_SERIE;
    //public static Integer NFE_PROXIMO_NUMERO; 2020-02-11 não pode ficar na sessão por conta de várias estações
    public static AmbienteEnum NFE_TIPO_AMBIENTE;
    public static RegimeTributario NFE_REGIME_TRIBUTARIO;
    public static NaturezaOperacao NFE_NATUREZA_OPERACAO;
    public static TipoAtendimento NFE_TIPO_ATENDIMENTO;
    public static ConsumidorFinal NFE_CONSUMIDOR_FINAL;
    public static DestinoOperacao NFE_DESTINO_OPERACAO;
    public static String NFE_INFORMACOES_ADICIONAIS_FISCO;
    public static String NFE_INFORMACOES_COMPLEMENTARES_CONTRIBUINTE;
    public static String NFE_CERTIFICADO_TIPO;
    public static String NFE_CERTIFICADO_PIN;
    public static String NFE_CERTIFICADO_MARCA;
    
    public static Boolean OST_HABILITAR;
    
    public static boolean VEICULO_HABILITAR;
    
    public static String TO_PRINTER_PATH;
    public static String BACKUP_PATH;
    
    public static BigDecimal CLIENTE_LIMITE_CREDITO;
    
    public static String PRODUTO_ETIQUETA_MODELO;
    public static String PRODUTO_IMAGENS_PATH;
    
    public static boolean VENDA_FUNCIONARIO_POR_ITEM;
    public static boolean VENDA_FUNCIONARIO_POR_ITEM_PRODUTO;
    public static boolean VENDA_FUNCIONARIO_POR_ITEM_SERVICO;
    
    public static boolean VENDA_INSERCAO_DIRETA;
    public static BigDecimal PARCELA_MULTA;
    public static BigDecimal PARCELA_JUROS_MONETARIO_MENSAL;
    public static BigDecimal PARCELA_JUROS_PERCENTUAL_MENSAL;
    public static Integer VENDA_NUMERO_COMANDAS;
    public static String VENDA_LAYOUT_COMANDAS;
    public static VendaStatus VENDA_STATUS_INICIAL;
    public static boolean VENDA_BLOQUEAR_PARCELAS_EM_ATRASO;
    public static boolean VENDA_BLOQUEAR_CREDITO_EXCEDIDO;
    public static boolean VENDA_VALIDAR_ESTOQUE;
    public static boolean VENDA_ALERTAR_GARANTIA_POR_VEICULO;
    public static boolean VENDA_FUNCIONARIO_OBRIGATORIO;
    public static boolean VENDA_IMPRIMIR_PRODUTOS_SERVICOS_SEPARADOS;
    public static String VENDA_PROMISSORIA_TIPO;
    public static boolean VENDA_BONIFICACAO_HABILITAR;
    
    public static boolean SISTEMA_MODO_BALCAO;
    public static boolean VENDA_ABRIR_COMANDAS_AO_INICIAR;
    
    public static Conta FINANCEIRO_CAIXA_PRINCIPAL;
    
    public static boolean VENDA_POR_FICHA_HABILITAR;
    
    public static Usuario USUARIO = new Usuario();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        APP_PATH = APP_PATH.substring(0, APP_PATH.length() - 1);
        
        
        //Na linha de comando: java -Duser.timezone=GMT-3 -jar ouroboros.jar
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-03:00"));
        
        
        //Constants for jTable
        CELL_RENDERER_ALIGN_CENTER.setHorizontalAlignment(SwingConstants.CENTER);
        CELL_RENDERER_ALIGN_RIGHT.setHorizontalAlignment(SwingConstants.RIGHT);
        CELL_RENDERER_ALIGN_LEFT.setHorizontalAlignment(SwingConstants.LEFT);

        //new LowLevel().removerForeignKey("produto", "icms");
        
        //System.exit(0);
        
        CONNECTION_FACTORY = new ConnectionFactory();
    
        
        em = CONNECTION_FACTORY.getConnection();
        
        
        emBs = new ConnectionFactory().getConnectionBootstrap();
        
        
        MAIN_VIEW = new MainView();
        
        new VendaDAO().getComandasAbertasSnapshot();
        
        
        
        //Trava - liberar sistema
        //SISTEMA_VALIDADE = Sistema.getValidade();
        
        //System.out.println("Validade: " + SISTEMA_CHAVE);
        
        if(!Sistema.checkValidade() && false) {
            AtivarView ativar = new AtivarView();
        }
        
        SISTEMA_MODO_BALCAO = Boolean.parseBoolean(MwConfig.getValue("SISTEMA_MODO_BALCAO"));
        //System.out.println("SISTEMA_MODO_BALCAO: " + SISTEMA_MODO_BALCAO);
        if(Ouroboros.SISTEMA_MODO_BALCAO) {
            USUARIO = new UsuarioDAO().findById(1);
                    
        } else {
            LoginView loginView = new LoginView();

            USUARIO = loginView.getUsuario();
            if(USUARIO != null) {
                USUARIO = loginView.getUsuario();
            } else {
                System.exit(0);
            }
        }
        
        MAIN_VIEW.setTitle(
                MAIN_VIEW.getTitle() + 
                        " | Versão " + APP_VERSION + 
                        " | Usuário: " + USUARIO.getLogin() +
                        " | Servidor: " + SERVER
        );
        
        MAIN_VIEW.setBounds(0, 0, 1280, 560);
        MAIN_VIEW.setExtendedState(MAXIMIZED_BOTH);
        
        
        
        MAIN_VIEW.setVisible(true);
        
        
        //Bootstrap automático -------------------------------------------------
        VendaTipoDAO vendaTipoDAO = new VendaTipoDAO();
        if(vendaTipoDAO.findById(6) == null) { //Até 6 - COMPRA
            new Toast("Criando tipos de venda...");
            vendaTipoDAO.bootstrap();
        }
        
        if(ConstanteDAO.getValor("IMPRESSORA_PADRAO") != null) {
            new Toast("Alterando nome da constante IMPRESSORA_PADRAO para IMPRESSORA_CUPOM...");
            ConstanteDAO.alterarNome("IMPRESSORA_PADRAO", "IMPRESSORA_CUPOM");
        }
        
        if(ConstanteDAO.getValor("IMPRESSORA_FORMATO_PADRAO") == null) {
            new Toast("Criando constante IMPRESSORA_FORMATO_PADRAO...");
            new ConstanteDAO().save(new Constante("IMPRESSORA_FORMATO_PADRAO", "CUPOM"));
        }
        
        /* 2019-06-18 - Generalizado para 2-DOCUMENTO
        CaixaItemTipoDAO caixaItemTipoDAO = new CaixaItemTipoDAO();
        if(caixaItemTipoDAO.findById(8) == null || !caixaItemTipoDAO.findById(8).getNome().equals("PAGAMENTO DOCUMENTO")) {
            new Toast("Atualizando CaixaItemTipo...");
            caixaItemTipoDAO.bootstrap();
        }*/
        
        if(ConstanteDAO.getValor("EMPRESA_IM") == null) {
            new Toast("Criando constante EMPRESA_IM...");
            new ConstanteDAO().save(new Constante("EMPRESA_IM", ""));
        }
        
        if(ConstanteDAO.getValor("EMPRESA_TELEFONE") == null) {
            new Toast("Criando constante EMPRESA_TELEFONE...");
            new ConstanteDAO().save(new Constante("EMPRESA_TELEFONE", ""));
        }
        
        TipoOperacaoDAO tipoOperacaoDAO = new TipoOperacaoDAO();
        if(tipoOperacaoDAO.findById(1) == null) {
            new Toast("Criando tipos de documento...");
            tipoOperacaoDAO.bootstrap();
        }
        
        //2019-02-18 NCMs sem o zero a esquerda!!!
        //2019-03-23 refatorado
        NcmDAO ncmDAO = new NcmDAO();
        if(ncmDAO.findByCodigo("9019000") != null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "NCM sem zeros a esquerda! Necessária intervenção manual", "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            
        }
        if(ncmDAO.findByCodigo("22") == null) { //testa com NCM recente
            NcmBsDAO ncmBsDAO = new NcmBsDAO();
            if(ncmBsDAO.findByCodigo("22") == null) { //verifica arquivo sqLite
                JOptionPane.showMessageDialog(MAIN_VIEW, "Bootstrap NCM desatualizado! Necessário atualizar o arquivo sqLite!", "Erro", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            
            new Toast("Criando NCMs...");
            ncmDAO.bootstrap();
        }
        
        RecursoDAO recursoDAO = new RecursoDAO();
        if(recursoDAO.findById(14) == null) {
            new Toast("Criando recursos...");
            recursoDAO.bootstrap();
        }
        
        //2019-03-23
        SatCupomTipoDAO satCupomTipoDAO = new SatCupomTipoDAO();
        if(satCupomTipoDAO.findById(1) == null) {
            new Toast("Criando tipos de cupom...");
            satCupomTipoDAO.bootstrap();
        }
        
        //2019-04-08
        String id = ConstanteDAO.getValor("SISTEMA_ID");
        if(id == null || id.equals("")) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Sistema sem id!", "Erro", JOptionPane.ERROR_MESSAGE);
            id = JOptionPane.showInputDialog(MAIN_VIEW, "Informe o Id do sistema", "Atenção", JOptionPane.WARNING_MESSAGE);
            new ConstanteDAO().save(new Constante("SISTEMA_ID", id));
            System.exit(0);
        }
        if(ConstanteDAO.getValor("SISTEMA_CHAVE") == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Sistema sem chave!", "Erro", JOptionPane.ERROR_MESSAGE);
            new ConstanteDAO().save(new Constante("SISTEMA_CHAVE", ""));
            System.exit(0);
        }
        
        //2019-04-11
        if(ConstanteDAO.getValor("EMPRESA_TELEFONE2") == null) {
            new Toast("Criando campos detalhados de endereço da empresa...");
            new ConstanteDAO().bootstrap();
        }
        
        //2019-04-12
        RegimeTributarioDAO regimeTributarioDAO =  new RegimeTributarioDAO();
        if(regimeTributarioDAO.findById(3) == null) {
            new Toast("Criando regimes tributários...");
            regimeTributarioDAO.bootstrap();
        }
        
        TipoAtendimentoDAO tipoAtendimentoDAO =  new TipoAtendimentoDAO();
        if(tipoAtendimentoDAO.findById(9) == null) {
            new Toast("Criando tipos de atendimento...");
            tipoAtendimentoDAO.bootstrap();
        }
        
        TipoEmissaoDAO tipoEmissaoDAO =  new TipoEmissaoDAO();
        if(tipoEmissaoDAO.findById(7) == null) {
            new Toast("Criando tipos de emissão...");
            tipoEmissaoDAO.bootstrap();
        }
        
        //2019-04-15
        DestinoOperacaoDAO destinoOperacaoDAO =  new DestinoOperacaoDAO();
        if(destinoOperacaoDAO.findById(3) == null) {
            new Toast("Criando destinos de operação...");
            destinoOperacaoDAO.bootstrap();
        }
        
        
        NaturezaOperacaoDAO naturezaOperacaoDAO =  new NaturezaOperacaoDAO();
        if(naturezaOperacaoDAO.findById(1) == null) {
            new Toast("Criando naturezas de operação...");
            naturezaOperacaoDAO.bootstrap();
        }
        
        FinalidadeEmissaoDAO finalidadeEmissaoDAO =  new FinalidadeEmissaoDAO();
        if(finalidadeEmissaoDAO.findById(1) == null) {
            new Toast("Criando finalidades de emissão...");
            finalidadeEmissaoDAO.bootstrap();
        }
        
        ConsumidorFinalDAO consumidorFinalDAO =  new ConsumidorFinalDAO();
        if(consumidorFinalDAO.findById(1) == null) {
            new Toast("Criando consumidor final...");
            consumidorFinalDAO.bootstrap();
        }
        
        TipoContribuinteDAO tipoContribuinteDAO =  new TipoContribuinteDAO();
        if(tipoContribuinteDAO.findById(1) == null) {
            new Toast("Criando tipos de contribuinte...");
            tipoContribuinteDAO.bootstrap();
        }
        
        ModalidadeBcIcmsDAO modalidadeBcIcmsDAO =  new ModalidadeBcIcmsDAO();
        if(modalidadeBcIcmsDAO.findById(1) == null) {
            new Toast("Criando modalidades da base de cálculo do icms...");
            modalidadeBcIcmsDAO.bootstrap();
        }
        
        ModalidadeBcIcmsStDAO modalidadeBcIcmsStDAO =  new ModalidadeBcIcmsStDAO();
        if(modalidadeBcIcmsStDAO.findById(1) == null) {
            new Toast("Criando modalidades da base de cálculo do icms st...");
            modalidadeBcIcmsStDAO.bootstrap();
        }
        
        //2019-04-29 - Atualização do administrador
        //remover diretivas de SISTEMA e USUÁRIOS
        /*for(Usuario usuario : new UsuarioDAO().findAll()) {
            usuario.removeDiretiva(usuario.findDiretiva(Recurso.SISTEMA));
            usuario.removeDiretiva(usuario.findDiretiva(Recurso.USUARIOS));
        }
        new RecursoDAO().delete(Recurso.SISTEMA);
        new RecursoDAO().delete(Recurso.USUARIOS);
        */
        
        
        //2019-05-22
        if(ConstanteDAO.getValor("VENDA_EXIBIR_VEICULO") == null) {
            new Toast("Criando constante VENDA_EXIBIR_VEICULO...");
            new ConstanteDAO().save(new Constante("VENDA_EXIBIR_VEICULO", "false"));
        }
        
        MeioDePagamentoDAO meioDePagamentoDAO = new MeioDePagamentoDAO();
        if(meioDePagamentoDAO.findById(12) == null) {
            new Toast("Atualizando Meios de Pagamento...");
            meioDePagamentoDAO.bootstrap();
        }
        
        
        //Adicionar constante versão do sistema
        if(ConstanteDAO.getValor("SISTEMA_VERSAO") == null) {
            new Toast("Criando constante SISTEMA_VERSAO...");
            new ConstanteDAO().bootstrap();
        }
        
        
        //2019-06-05
        CaixaItemTipoDAO caixaItemTipoDAO = new CaixaItemTipoDAO();
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 6, 10)) < 0) {
            new Toast("Atualizando CaixaItemTipo TROCO e DOCUMENTO...");
            caixaItemTipoDAO.bootstrap();
            
            new Toast("NOTA TÉCNICA: Atualizar CaixaItem -> caixaItemTipoId: trocar 8 por 2", false);
        }
        
        //2019-06-15
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 6, 15)) < 0) {
            new Toast("Criando tipos produto/serviço");
            new ProdutoTipoDAO().bootstrap();
            
            new Toast("NOTA TÉCNICA: Atualizar Produto -> produtoTipoId: 1 produto, 2 serviço", false);
        }
        
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 6, 18)) < 0) {
            new Toast("NOTA TÉCNICA: Renomear os campos de acrescimo e desconto na tabela venda adicionando o sufixo Produtos\r\n"
                    + "Ex: acrescimoPercentual -> acrescimoPercentualProdutos\r\n"
                    + "NOTA TÉCNICA: Adicionar report:\r\n"
                    + "DocumentoSaida.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 6, 19)) < 0) {
            new Toast("NOTA TÉCNICA: Alimentar descrição do item de venda para o novo campo em movimentoFisico:\r\n"
                    + "update movimentofisico set descricao = (select nome from produto where id = produtoId)", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 6, 21)) < 0) {
            new Toast("NOTA TÉCNICA: Adicionar report:\r\n"
                    + "ListaProdutos.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 6, 24)) < 0) {
            new Toast("NOTA TÉCNICA: Adicionar report:\r\n"
                    + "ListaProdutosComEstoque.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 6, 25)) < 0) {
            new Toast("Atualizando tabela de ICMS...");
            new IcmsDAO().bootstrap();
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 8, 2)) < 0) {
            new Toast("NOTA TÉCNICA: Atualizar report:\r\n"
                    + "DocumentoSaida.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 8, 21)) < 0) {
            new Toast("NOTA TÉCNICA: Alimentar produtoTipo do item de venda para o novo campo em movimentoFisico:\r\n"
                    + "update movimentofisico set produtotipoId = (select produtoTipoId from produto where id = movimentofisico.produtoId)", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 8, 27)) < 0) {
            new Toast("NOTA TÉCNICA: Preencher valores nulos de unidade de medida com UNID:\r\n"
                    + "update movimentofisico set unidadeComercialVendaId = 59 where unidadeComercialVendaId is null\r\n"
                    + "update produto set unidadeComercialVendaId = 59 where unidadeComercialVendaId is null", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 8, 29)) < 0) {
            new Toast("Criando tipos de venda...");
            vendaTipoDAO.bootstrap(); //Até 7 - DELIVERY
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 9, 4)) < 0) {
            new Toast("NOTA TÉCNICA: Atualizar bootstrap e reiniciar o sistema", false);
            new Toast("Criando motivos de desoneração...");
            new MotivoDesoneracaoDAO().bootstrap();
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 9, 6)) < 0) {
            new Toast("NOTA TÉCNICA: Atualizar bootstrap e reiniciar o sistema", false);
            new Toast("Criando situações tributárias do PIS...");
            new PisDAO().bootstrap();
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 9, 10)) < 0) {
            new Toast("NOTA TÉCNICA: Atualizar bootstrap e reiniciar o sistema", false);
            new Toast("Criando situações tributárias do COFINS...");
            new CofinsDAO().bootstrap();
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 9, 19)) < 0) {
            new Toast("NOTA TÉCNICA: Atualizar bootstrap e reiniciar o sistema", false);
            new Toast("Criando modalidades de frete...");
            new ModalidadeFreteDAO().bootstrap();
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 9, 20)) < 0) {
            new Toast("Atualizando constantes para adicionar NFE_PATH...");
            new ConstanteDAO().bootstrap();
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 9, 26)) < 0) {
            new Toast("Atualizando constantes para adicionar parâmetros \r\n"
                    + "de informações adicionais e complementares da NFe...");
            new ConstanteDAO().bootstrap();
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 9, 27)) < 0) {
            new Toast("NOTA TÉCNICA: Atualizar bootstrap e reiniciar o sistema", false);
            new Toast("Atualizando tabela IBPT...");
            new IbptDAO().bootstrap();
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 10, 30)) < 0) {
            new Toast("Adicionando constante para status inicial de venda");
            new ConstanteDAO().bootstrap();
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 11, 1)) < 0) {
            new Toast("NOTA TÉCNICA: Atualizar report:\r\n"
                    + "DocumentoSaida.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 11, 4)) < 0) {
            new Toast("Alterando nome da constante VENDA_EXIBIR_VEICULO para VEICULO_HABILITAR...");
            ConstanteDAO.alterarNome("VENDA_EXIBIR_VEICULO", "VEICULO_HABILITAR");
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 11, 8)) < 0) {
            new Toast("NOTA TÉCNICA: Adicionar report:\r\n"
                    + "OSTransporte.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 11, 14)) < 0) {
            new Toast("Adicionando parâmetro de configuração NFE_CERTIFICADO_TIPO");
            MwConfig.setValue("NFE_CERTIFICADO_TIPO", "A1");
            
            new Toast("Adicionando parâmetro de configuração NFE_CERTIFICADO_PIN");
            MwConfig.setValue("NFE_CERTIFICADO_PIN", "");
            
            new Toast("Adicionando parâmetro de configuração NFE_CERTIFICADO_MARCA");
            MwConfig.setValue("NFE_CERTIFICADO_MARCA", "");
            
            new Toast("NOTA TÉCNICA: Atualizar report:\r\n"
                    + "Danfe.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 11, 18)) < 0) {
            new Toast("Adicionando parâmetro de configuração IMPRESSORA_CUPOM_TAMANHO_FONTE");
            MwConfig.setValue("IMPRESSORA_CUPOM_TAMANHO_FONTE", "8");
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 11, 19)) < 0) {
            new Toast("NOTA TÉCNICA:\r\n"
                    + "Executar patch para totais das vendas", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 11, 26)) < 0) {
            new Toast("NOTA TÉCNICA: Adicionar report:\r\n"
                    + "VeiculoHistorico.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 11, 27)) < 0) {
            new Toast("NOTA TÉCNICA: Adicionar report:\r\n"
                    + "DocumentoSaidaItens.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 11, 28)) < 0) {
            new Toast("Adicionando siglas dos meios de pagamentos...");
            meioDePagamentoDAO.bootstrap();
            
            new Toast("NOTA TÉCNICA: Adicionar report:\r\n"
                    + "CaixaPorPeriodo.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 11, 29)) < 0) {
            new Toast("NOTA TÉCNICA: Adicionar report:\r\n"
                    + "DocumentoSaidaItens.jasper", false);
            
            new Toast("Criando conta financeira...");
            new ContaDAO().bootstrap();
            
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 12, 20)) < 0) {
            new Toast("NOTA TÉCNICA: Alimentar novo campo em venda:\r\n"
                    + "update venda set dataHora = criacao", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 1, 8)) < 0) {
            new Toast("NOTA TÉCNICA: Executar patch de estoque atual", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 1, 13)) < 0) {
            new Toast("NOTA TÉCNICA: Atualizar bootstrap e reiniciar o sistema", false);
            new Toast("Criando tabela ANP (combustíveis)");
            new AnpDAO().bootstrap();
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 1, 16)) < 0) {
            new Toast("NOTA TÉCNICA: Atualizar report:\r\n"
                    + "DocumentoSaida.jasper", false);
            new Toast("Criando parâmetro em config IMPRESSORA_A4_EXIBIR_ACRESCIMO");
            MwConfig.setValue("IMPRESSORA_A4_EXIBIR_ACRESCIMO", "true");
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 1, 18)) < 0) {
            new Toast("NOTA TÉCNICA: Adicionar report:\r\n"
                    + "CaixaPorTurno.jasper", false);
            new Toast("Criando parâmetro em config IMPRESSORA_A4_EXIBIR_OBSERVACAO");
            MwConfig.setValue("IMPRESSORA_A4_EXIBIR_OBSERVACAO", "true");
            new Toast("NOTA TÉCNICA: Atualizar report:\r\n"
                    + "DocumentoSaida.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 1, 19)) < 0) {
            new Toast("NOTA TÉCNICA: Redefinir tamanhos dos campos:\r\n"
                    + "produto.conteudoQuantidade: 20,3\r\n"
                    + "movimentofisico.valor: 21,10", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 1, 20)) < 0) {
            new Toast("NOTA TÉCNICA: Redefinir tamanhos dos campos:\r\n"
                    + "movimentofisico.entrada: 20,4\r\n"
                    + "movimentofisico.saida: 20,4", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 1, 24)) < 0) {
            new Toast("NOTA TÉCNICA: Atualizar bootstrap", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 1, 27)) < 0) {
            new Toast("NOTA TÉCNICA: Redefinir tamanho do campo:\r\n"
                    + "movimentofisico.descricao: 1000", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 2, 5)) < 0) {
            new Toast("NOTA TÉCNICA: Remover campos referentes a cartão:\r\n"
                    + "movimentofisico: acrescimoCartao, taxaCartao, taxaCartaoInclusa\r\n"
                    + "parcela: acrescimoCartao", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 2, 10)) < 0) {
            new Toast("NOTA TÉCNICA: Atualizar bootstrap", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 2, 19)) < 0) {
            new Toast("Adicionando CaixaItemTipo TRANSFERÊNCIA...");
            caixaItemTipoDAO.bootstrap();
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 2, 20)) < 0) {
            new Toast("NOTA TÉCNICA: Alimentar contaId na tabela caixa:\r\n"
                    + "update caixa set contaId = 1 where contaId is null", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 2, 24)) < 0) {
            new Toast("NOTA TÉCNICA: Atualizar reports:\r\n"
                    + "ContasPagar.jasper\r\n"
                    + "ContasReceber.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 2, 26)) < 0) {
            new Toast("Adicionando parâmetro de configuração IMPRESSORA_CUPOM_EXIBIR_MEIOS_PAGAMENTO");
            MwConfig.setValue("IMPRESSORA_CUPOM_EXIBIR_MEIOS_PAGAMENTO", "true");
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 2, 27)) < 0) {
            new Toast("NOTA TÉCNICA: Remover índice UNIQUE:\r\n"
                    + "Tabela meiodepagamento\r\n"
                    + "campo codigoSAT", false);
            
            new Toast("NOTA TÉCNICA: Atualizar report:\r\n"
                    + "Excluir DanilaCarne.jasper\r\n"
                    + "Adicionar Carne.jasper", false);
            
            new Toast("Atualizando Meios de Pagamento (Transferência)...");
            meioDePagamentoDAO.bootstrap();
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 2, 28)) < 0) {
            new Toast("Adicionando parâmetro de configuração FINANCEIRO_CAIXA_PRINCIPAL");
            MwConfig.setValue("FINANCEIRO_CAIXA_PRINCIPAL", "1");
            new Toast("NOTA TÉCNICA: Definir em cada estação o Caixa Principal", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 3, 4)) < 0) {
            new Toast("Adicionando parâmetro de configuração PRODUTO_IMAGENS_PATH");
            MwConfig.setValue("PRODUTO_IMAGENS_PATH", APP_PATH + "custom/catalogo/");
            new Toast("NOTA TÉCNICA: Definir em cada estação PRODUTO_IMAGENS_PATH", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 3, 9)) < 0) {
            new Toast("NOTA TÉCNICA: Atualizar bootstrap", false);
            new IpiDAO().bootstrap();
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 3, 12)) < 0) {
            new Toast("Adicionando constante para habilitar VENDA POR FICHA");
            new ConstanteDAO().bootstrap();
            
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 3, 13)) < 0) {
            new Toast("Removendo recursos por usuário: SISTEMA E USUÁRIOS");
            if (new RecursoDAO().findById(1) != null) {
                new RecursoDAO().delete(new RecursoDAO().findById(1));
            }
            if (new RecursoDAO().findById(2) != null) {
                new RecursoDAO().delete(new RecursoDAO().findById(2));
            }
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 3, 14)) < 0) {
            new Toast("Atualizando recursos e diretivas de usuários...");
            for (Usuario u : new UsuarioDAO().findAll()) {
                u.normalizarDiretivas();
            }
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 3, 21)) < 0) {
            new Toast("NOTA TÉCNICA: Atualizar bootstrap", false);
            new Toast("Atualizando Motivos de Desoneração...");
            MotivoDesoneracaoDAO motivoDesoneracaoDAO = new MotivoDesoneracaoDAO();
            motivoDesoneracaoDAO.bootstrap();
            if (motivoDesoneracaoDAO.findById(90) == null) {
                new Toast("Atualize o bootstrap!", false);
                System.exit(0);
            }
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 3, 27)) < 0) {
            new Toast("NOTA TÉCNICA: Atualizar report:\r\n"
                    + "Danfe.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 4, 6)) < 0) {
            new Toast("Adicionando parâmetro de configuração IMPRESSORA_CUPOM_MARGEM_CORTE");
            MwConfig.setValue("IMPRESSORA_CUPOM_MARGEM_CORTE", "0");
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 4, 25)) < 0) {
            new Toast("NOTA TÉCNICA:\r\n"
                    + "Executar patch para saldo das contas", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 4, 26)) < 0) {
            new Toast("Adicionando constante VENDA_IMPRIMIR_PRODUTOS_SERVICOS_SEPARADOS");
            new ConstanteDAO().bootstrap();
            new Toast("NOTA TÉCNICA: Atualizar report:\r\n"
                    + "DocumentoSaida.jasper", false);
        }
        
        if (Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 4, 27)) < 0) {
            new Toast("Adicionando constante VENDA_FUNCIONARIO_OBRIGATORIO");
            new Toast("Adicionando constante VENDA_PROMISSORIA_TIPO");
            new ConstanteDAO().bootstrap();
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 4, 29)) < 0) {
            new Toast("NOTA TÉCNICA: Atualizar report:\r\n"
                    + "NotaPromissoria.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 5, 4)) < 0) {
            new Toast("NOTA TÉCNICA: Atualizar report:\r\n"
                    + "DocumentoSaidaLista.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 5, 21)) < 0) {
            new Toast("NOTA TÉCNICA: Adicionar report:\r\n"
                    + "VendasProdutosPorVendedor.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 5, 22)) < 0) {
            new Toast("NOTA TÉCNICA: Adicionar report:\r\n"
                    + "VendasVendedoresPorProduto.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 6, 5)) < 0) {
            new Toast("NOTA TÉCNICA: Adicionar report:\r\n"
                    + "ProdutoEtiqueta635x465.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 6, 15)) < 0) {
            new Toast("NOTA TÉCNICA: Adicionar report:\r\n"
                    + "VendasFaturamentoPorPeriodoPorVendedor.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 6, 15)) < 0) {
            new Toast("NOTA TÉCNICA: Adicionar report:\r\n"
                    + "VendasDiariasPorVendedor.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 7, 2)) < 0) {
            new Toast("Adicionando constante para habilitar VENDA_BONIFICACAO_HABILITAR");
            new ConstanteDAO().bootstrap();
        }
        
        if (Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 7, 10)) < 0) {
            new Toast("NOTA TÉCNICA: renomear campo caixaItem.dataRecebimento para caixaItem.dataHoraRecebimento\r\n"
                    + "redefinir tipo de dados para Datetime\r\n"
                    + "E atualizar os dados:"
                    + "update caixaItem set dataHoraRecebimento = dataHora where dataHoraRecebimento is null", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 7, 16)) < 0) {
            new Toast("NOTA TÉCNICA: Redefinir tamanhos dos campos:\r\n"
                    + "movimentofisico.valor: 21,10", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 7, 22)) < 0) {
            new Toast("NOTA TÉCNICA: Atualizar campo parcela.meiodepagamentoId:\r\n"
                    + "update parcela inner join caixaitem on parcela.id = caixaitem.parcelaId set parcela.meioDePagamentoId = caixaitem.meioDePagamentoId where parcela.meioDePagamentoId is null;", false);
            
            new Toast("NOTA TÉCNICA: Adicionar reports:\r\n"
                    + "FaturamentoRecebimentoPorPeriodo.jasper\r\n"
                    + "FaturamentoRecebimentoPorPeriodoSubreport.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 7, 27)) < 0) {
            new Toast("NOTA TÉCNICA: Atualizar reports:\r\n"
                    + "FaturamentoRecebimentoPorPeriodo.jasper\r\n"
                    + "FaturamentoRecebimentoPorPeriodoSubreport.jasper", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 7, 29)) < 0) {
            new Toast("NOTA TÉCNICA: Adicionar report:\r\n"
                    + "DocumentoSaidaOriginalArt.jasper\r\n", false);
            
            new Toast("Atualizando descrição do tipo de documento");
            vendaTipoDAO.bootstrap();
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 8, 3)) < 0) {
            new Toast("NOTA TÉCNICA: Adicionar report:\r\n"
                    + "ProdutoEtiqueta500x250.jasper\r\n", false);
            
            new Toast("Adicionando parâmetro de configuração IMPRESSORA_CUPOM_MARGEM_CORTE");
            MwConfig.setValue("IMPRESSORA_CUPOM_MARGEM_CORTE", "0");
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 8, 4)) < 0) {
            new Toast("Adicionando parâmetro de configuração IMPRESSORA_RECIBO_VIAS");
            MwConfig.setValue("IMPRESSORA_RECIBO_VIAS", "1");
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 8, 6)) < 0) {
            new Toast("NOTA TÉCNICA: Atualizar report:\r\n"
                    + "VendasProdutosPorCidade.jasper\r\n", false);
        }
        
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2020, 8, 17)) < 0) {
            new Toast("Atualizando CaixaItemTipo");
            caixaItemTipoDAO.bootstrap();
        }
        
        
        
        
        
        
        //Registrar última versão
        Atualizacao.setVersaoAtual(Atualizacao.getUltimaData());
        
        MAIN_VIEW.setMensagem("Bootstrap automático concluído. Sistema liberado.");
        
        //Fim do Bootstrap automático ------------------------------------------
        
        if(!Sistema.checkValidade()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Sistema sem chave.", "Atenção", JOptionPane.WARNING_MESSAGE);
            new AtivarView();
        }
        
        String msg = "";
        Long dias = Sistema.getValidadeEmDias();
        
        if(dias <= -5) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Os períodos de validade e carência para validação foram expirados. Você deve informar uma nova chave de ativação.", "Atenção", JOptionPane.WARNING_MESSAGE);
            AtivarView ativar = new AtivarView();
            dias = Sistema.getValidadeEmDias();
        }
        
        if(dias < 0) {
            msg = "ATENÇÃO: Seu sistema bloqueará em " + (dias + 5) + " dia(s)";
            
            JOptionPane.showMessageDialog(MAIN_VIEW, msg, "Atenção", JOptionPane.WARNING_MESSAGE);
            
        } else {
            msg = "Validade do sistema: " 
                + Sistema.getValidadeEmDias() + " dias"
                + " (" + DateTime.toString(Sistema.getValidade()) + ")";
            
        }
        
        MAIN_VIEW.setMensagem(msg);
        
        
        //new ConstanteDAO().bootstrap(); //Criar as constantes (se já existir ele ignora) //2020-03-12 só executar quando precisar
        
        
        SISTEMA_REVALIDAR_ADMINISTRADOR = Boolean.parseBoolean(ConstanteDAO.getValor("SISTEMA_REVALIDAR_ADMINISTRADOR"));
        
        EMPRESA_NOME_FANTASIA = ConstanteDAO.getValor("EMPRESA_NOME_FANTASIA");
        EMPRESA_RAZAO_SOCIAL = ConstanteDAO.getValor("EMPRESA_RAZAO_SOCIAL");
        EMPRESA_CNPJ = ConstanteDAO.getValor("EMPRESA_CNPJ");
        EMPRESA_IE = ConstanteDAO.getValor("EMPRESA_IE");
        EMPRESA_IM = ConstanteDAO.getValor("EMPRESA_IM");
        EMPRESA_TELEFONE = ConstanteDAO.getValor("EMPRESA_TELEFONE");
        EMPRESA_TELEFONE2 = ConstanteDAO.getValor("EMPRESA_TELEFONE2");
        EMPRESA_EMAIL = ConstanteDAO.getValor("EMPRESA_EMAIL");
        
        EMPRESA_ENDERECO_CEP = ConstanteDAO.getValor("EMPRESA_ENDERECO_CEP");
        EMPRESA_ENDERECO = ConstanteDAO.getValor("EMPRESA_ENDERECO");
        EMPRESA_ENDERECO_NUMERO = ConstanteDAO.getValor("EMPRESA_ENDERECO_NUMERO");
        EMPRESA_ENDERECO_COMPLEMENTO = ConstanteDAO.getValor("EMPRESA_ENDERECO_COMPLEMENTO");
        EMPRESA_ENDERECO_BAIRRO = ConstanteDAO.getValor("EMPRESA_ENDERECO_BAIRRO");
        EMPRESA_ENDERECO_CODIGO_MUNICIPIO = ConstanteDAO.getValor("EMPRESA_ENDERECO_CODIGO_MUNICIPIO");
        
        /*
        IMPRESSORA_CUPOM = ConstanteDAO.getValor("IMPRESSORA_CUPOM");
        IMPRESSORA_A4 = ConstanteDAO.getValor("IMPRESSORA_A4");
        IMPRESSORA_FORMATO_PADRAO = ConstanteDAO.getValor("IMPRESSORA_FORMATO_PADRAO");
        IMPRESSORA_DESATIVAR = Boolean.parseBoolean(ConstanteDAO.getValor("IMPRESSORA_DESATIVAR"));
        */
        //Alterado para config local
        IMPRESSORA_CUPOM = MwConfig.getValue("IMPRESSORA_CUPOM");
        IMPRESSORA_CUPOM_TAMANHO_FONTE = Float.valueOf(MwConfig.getValue("IMPRESSORA_CUPOM_TAMANHO_FONTE"));
        IMPRESSORA_CUPOM_EXIBIR_CABECALHO_ITEM = Boolean.parseBoolean(MwConfig.getValue("IMPRESSORA_CUPOM_EXIBIR_CABECALHO_ITEM"));
        IMPRESSORA_CUPOM_EXIBIR_NUMERO_ITEM = Boolean.parseBoolean(MwConfig.getValue("IMPRESSORA_CUPOM_EXIBIR_NUMERO_ITEM"));
        IMPRESSORA_CUPOM_EXIBIR_CODIGO_ITEM = Boolean.parseBoolean(MwConfig.getValue("IMPRESSORA_CUPOM_EXIBIR_CODIGO_ITEM"));
        IMPRESSORA_CUPOM_EXIBIR_UNIDADE_MEDIDA_ITEM = Boolean.parseBoolean(MwConfig.getValue("IMPRESSORA_CUPOM_EXIBIR_UNIDADE_MEDIDA_ITEM"));
        IMPRESSORA_CUPOM_EXIBIR_ACRESCIMO_DESCONTO_ITEM = Boolean.parseBoolean(MwConfig.getValue("IMPRESSORA_CUPOM_EXIBIR_ACRESCIMO_DESCONTO_ITEM"));
        IMPRESSORA_CUPOM_EXIBIR_ASSINATURA_CLIENTE = Boolean.parseBoolean(MwConfig.getValue("IMPRESSORA_CUPOM_EXIBIR_ASSINATURA_CLIENTE"));
        IMPRESSORA_CUPOM_EXIBIR_MEIOS_PAGAMENTO = Boolean.parseBoolean(MwConfig.getValue("IMPRESSORA_CUPOM_EXIBIR_MEIOS_PAGAMENTO"));
        IMPRESSORA_CUPOM_MARGEM_CORTE = Integer.valueOf(MwConfig.getValue("IMPRESSORA_CUPOM_MARGEM_CORTE"));
        IMPRESSORA_RECIBO_VIAS = Integer.valueOf(MwConfig.getValue("IMPRESSORA_RECIBO_VIAS"));
        
        IMPRESSORA_A4 = MwConfig.getValue("IMPRESSORA_A4");
        IMPRESSORA_A4_EXIBIR_ACRESCIMO = Boolean.parseBoolean(MwConfig.getValue("IMPRESSORA_A4_EXIBIR_ACRESCIMO"));
        IMPRESSORA_A4_EXIBIR_OBSERVACAO = Boolean.parseBoolean(MwConfig.getValue("IMPRESSORA_A4_EXIBIR_OBSERVACAO"));
        IMPRESSORA_ETIQUETA = MwConfig.getValue("IMPRESSORA_ETIQUETA");
        IMPRESSORA_FORMATO_PADRAO = MwConfig.getValue("IMPRESSORA_FORMATO_PADRAO");
        IMPRESSORA_DESATIVAR = Boolean.parseBoolean(MwConfig.getValue("IMPRESSORA_DESATIVAR"));
        
        IMPRESSAO_RODAPE = ConstanteDAO.getValor("IMPRESSAO_RODAPE");
        
        
        
        SOFTWARE_HOUSE_CNPJ = ConstanteDAO.getValor("SOFTWARE_HOUSE_CNPJ");
        TO_SAT_PATH = APP_PATH + ConstanteDAO.getValor("TO_SAT_PATH");
        //System.out.println("TO_SAT_PATH: " + TO_SAT_PATH);
        
        NFSE_ALIQUOTA = Decimal.fromString(ConstanteDAO.getValor("NFSE_ALIQUOTA").replace(".", ","));
        NFSE_CODIGO_SERVICO = ConstanteDAO.getValor("NFSE_CODIGO_SERVICO");
        FROM_SAT_PATH = APP_PATH + ConstanteDAO.getValor("FROM_SAT_PATH");
        SAT_HABILITAR = Boolean.parseBoolean(ConstanteDAO.getValor("SAT_HABILITAR"));
        SAT_DLL = ConstanteDAO.getValor("SAT_DLL");
        SAT_CODIGO_ATIVACAO = ConstanteDAO.getValor("SAT_CODIGO_ATIVACAO");
        SAT_SIGN_AC = ConstanteDAO.getValor("SAT_SIGN_AC");
        SAT_PRINTER = ConstanteDAO.getValor("SAT_PRINTER");
        SAT_LAYOUT = ConstanteDAO.getValor("SAT_LAYOUT");
        
        SAT_MARGEM_ESQUERDA = Integer.parseInt(ConstanteDAO.getValor("SAT_MARGEM_ESQUERDA"));
        SAT_MARGEM_DIREITA = Integer.parseInt(ConstanteDAO.getValor("SAT_MARGEM_DIREITA"));
        SAT_MARGEM_SUPERIOR = Integer.parseInt(ConstanteDAO.getValor("SAT_MARGEM_SUPERIOR"));
        SAT_MARGEM_INFERIOR = Integer.parseInt(ConstanteDAO.getValor("SAT_MARGEM_INFERIOR"));
        
        NFE_HABILITAR = Boolean.parseBoolean(ConstanteDAO.getValor("NFE_HABILITAR"));
        NFE_PATH = APP_PATH + ConstanteDAO.getValor("NFE_PATH");
        NFE_SERIE = Integer.parseInt(ConstanteDAO.getValor("NFE_SERIE"));
        //NFE_PROXIMO_NUMERO = Integer.parseInt(ConstanteDAO.getValor("NFE_PROXIMO_NUMERO"));
        NFE_TIPO_AMBIENTE = AmbienteEnum.getByCodigo(ConstanteDAO.getValor("NFE_TIPO_AMBIENTE"));
        NFE_REGIME_TRIBUTARIO = new RegimeTributarioDAO().findById(Integer.parseInt(ConstanteDAO.getValor("NFE_REGIME_TRIBUTARIO")));
        NFE_NATUREZA_OPERACAO = new NaturezaOperacaoDAO().findById(Integer.parseInt(ConstanteDAO.getValor("NFE_NATUREZA_OPERACAO")));
        NFE_TIPO_ATENDIMENTO = new TipoAtendimentoDAO().findById(Integer.parseInt(ConstanteDAO.getValor("NFE_TIPO_ATENDIMENTO")));
        NFE_CONSUMIDOR_FINAL = new ConsumidorFinalDAO().findById(Integer.parseInt(ConstanteDAO.getValor("NFE_CONSUMIDOR_FINAL")));
        NFE_DESTINO_OPERACAO = new DestinoOperacaoDAO().findById(Integer.parseInt(ConstanteDAO.getValor("NFE_DESTINO_OPERACAO")));
        NFE_INFORMACOES_ADICIONAIS_FISCO = ConstanteDAO.getValor("NFE_INFORMACOES_ADICIONAIS_FISCO");
        NFE_INFORMACOES_COMPLEMENTARES_CONTRIBUINTE = ConstanteDAO.getValor("NFE_INFORMACOES_COMPLEMENTARES_CONTRIBUINTE");
        NFE_CERTIFICADO_TIPO = MwConfig.getValue("NFE_CERTIFICADO_TIPO");
        NFE_CERTIFICADO_PIN = MwConfig.getValue("NFE_CERTIFICADO_PIN");
        NFE_CERTIFICADO_MARCA = MwConfig.getValue("NFE_CERTIFICADO_MARCA");
        
        OST_HABILITAR = Boolean.parseBoolean(ConstanteDAO.getValor("OST_HABILITAR"));
        
        TO_PRINTER_PATH = ConstanteDAO.getValor("TO_PRINTER_PATH");
        
        BACKUP_PATH = ConstanteDAO.getValor("BACKUP_PATH");
        
        CLIENTE_LIMITE_CREDITO = Decimal.fromString(ConstanteDAO.getValor("CLIENTE_LIMITE_CREDITO").replace(".", ","));
        
        PRODUTO_ETIQUETA_MODELO = MwConfig.getValue("PRODUTO_ETIQUETA_MODELO");
        PRODUTO_IMAGENS_PATH = MwConfig.getValue("PRODUTO_IMAGENS_PATH");
        
        VENDA_FUNCIONARIO_POR_ITEM = Boolean.parseBoolean(ConstanteDAO.getValor("VENDA_FUNCIONARIO_POR_ITEM"));
        VENDA_FUNCIONARIO_POR_ITEM_PRODUTO = Boolean.parseBoolean(ConstanteDAO.getValor("VENDA_FUNCIONARIO_POR_ITEM_PRODUTO"));
        VENDA_FUNCIONARIO_POR_ITEM_SERVICO = Boolean.parseBoolean(ConstanteDAO.getValor("VENDA_FUNCIONARIO_POR_ITEM_SERVICO"));
        
        VENDA_INSERCAO_DIRETA = Boolean.parseBoolean(ConstanteDAO.getValor("VENDA_INSERCAO_DIRETA"));
        PARCELA_JUROS_MONETARIO_MENSAL = Decimal.fromString(ConstanteDAO.getValor("PARCELA_JUROS_MONETARIO_MENSAL").replace(".", ","));
        PARCELA_JUROS_PERCENTUAL_MENSAL = Decimal.fromString(ConstanteDAO.getValor("PARCELA_JUROS_PERCENTUAL_MENSAL").replace(".", ","));
        PARCELA_MULTA = Decimal.fromString(ConstanteDAO.getValor("PARCELA_MULTA").replace(".", ","));
        VENDA_NUMERO_COMANDAS = Integer.valueOf(ConstanteDAO.getValor("VENDA_NUMERO_COMANDAS"));
        VENDA_LAYOUT_COMANDAS = MwConfig.getValue("VENDA_LAYOUT_COMANDAS");
        VENDA_STATUS_INICIAL = VendaStatus.getById(Numero.fromStringToIntegerTROCAR_PELO_INTEIRO(ConstanteDAO.getValor("VENDA_STATUS_INICIAL")));
        VENDA_BLOQUEAR_PARCELAS_EM_ATRASO = Boolean.parseBoolean(ConstanteDAO.getValor("VENDA_BLOQUEAR_PARCELAS_EM_ATRASO"));
        VENDA_BLOQUEAR_CREDITO_EXCEDIDO = Boolean.parseBoolean(ConstanteDAO.getValor("VENDA_BLOQUEAR_CREDITO_EXCEDIDO"));
        VENDA_VALIDAR_ESTOQUE = Boolean.parseBoolean(ConstanteDAO.getValor("VENDA_VALIDAR_ESTOQUE"));
        VENDA_ALERTAR_GARANTIA_POR_VEICULO = Boolean.parseBoolean(ConstanteDAO.getValor("VENDA_ALERTAR_GARANTIA_POR_VEICULO"));
        VENDA_FUNCIONARIO_OBRIGATORIO = Boolean.parseBoolean(ConstanteDAO.getValor("VENDA_FUNCIONARIO_OBRIGATORIO"));
        VENDA_IMPRIMIR_PRODUTOS_SERVICOS_SEPARADOS = Boolean.parseBoolean(ConstanteDAO.getValor("VENDA_IMPRIMIR_PRODUTOS_SERVICOS_SEPARADOS"));
        VENDA_PROMISSORIA_TIPO = ConstanteDAO.getValor("VENDA_PROMISSORIA_TIPO");
        VENDA_BONIFICACAO_HABILITAR = Boolean.parseBoolean(ConstanteDAO.getValor("VENDA_BONIFICACAO_HABILITAR"));
        
        VEICULO_HABILITAR = Boolean.parseBoolean(ConstanteDAO.getValor("VEICULO_HABILITAR"));
        
        VENDA_ABRIR_COMANDAS_AO_INICIAR = Boolean.parseBoolean(MwConfig.getValue("VENDA_ABRIR_COMANDAS_AO_INICIAR"));
        
        FINANCEIRO_CAIXA_PRINCIPAL = new ContaDAO().findById(Numero.fromStringToIntegerTROCAR_PELO_INTEIRO(MwConfig.getValue("FINANCEIRO_CAIXA_PRINCIPAL")));
        
        VENDA_POR_FICHA_HABILITAR = Boolean.parseBoolean(ConstanteDAO.getValor("VENDA_POR_FICHA_HABILITAR"));
        
        //criar diretórios
        new File(FROM_SAT_PATH + "/processados/").mkdirs();
        new File(FROM_SAT_PATH + "/rejeitados/").mkdirs();
        new File(TO_SAT_PATH + "/processados/").mkdirs();
        new File(TO_SAT_PATH + "/rejeitados/").mkdirs();
        new File(TO_PRINTER_PATH).mkdir();
        new File(BACKUP_PATH).mkdir();
        new File("balanca").mkdir();
        new File("nfse").mkdir();
        new File("custom/nfe-certs/").mkdirs();
        new File("custom/nfe/enviados/").mkdirs();
        new File("custom/boleto/remessa/").mkdirs();
        
        
        if(!Ouroboros.SISTEMA_MODO_BALCAO) {
            MAIN_VIEW.carregarMenu();
        }
        
        if(Ouroboros.VENDA_ABRIR_COMANDAS_AO_INICIAR) {
            MAIN_VIEW.addView(DocumentoUtil.exibirComandas());
        }
        
        
        
    }
    
}

