/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ouroboros;

import connection.ConnectionFactory;
import static java.awt.Frame.MAXIMIZED_BOTH;
import java.io.File;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import model.mysql.bean.principal.Constante;
import model.mysql.bean.principal.Usuario;
import model.bootstrap.dao.NcmBsDAO;
import model.mysql.bean.principal.Recurso;
import model.mysql.dao.fiscal.IcmsDAO;
import model.mysql.dao.fiscal.MeioDePagamentoDAO;
import model.mysql.dao.fiscal.NcmDAO;
import model.mysql.dao.fiscal.SatCupomTipoDAO;
import model.mysql.dao.fiscal.nfe.ConsumidorFinalDAO;
import model.mysql.dao.fiscal.nfe.DestinoOperacaoDAO;
import model.mysql.dao.fiscal.nfe.FinalidadeEmissaoDAO;
import model.mysql.dao.fiscal.nfe.ModalidadeBcIcmsDAO;
import model.mysql.dao.fiscal.nfe.ModalidadeBcIcmsStDAO;
import model.mysql.dao.fiscal.nfe.NaturezaOperacaoDAO;
import model.mysql.dao.fiscal.nfe.RegimeTributarioDAO;
import model.mysql.dao.fiscal.nfe.TipoAtendimentoDAO;
import model.mysql.dao.fiscal.nfe.TipoContribuinteDAO;
import model.mysql.dao.fiscal.nfe.TipoEmissaoDAO;
import model.mysql.dao.principal.CaixaItemTipoDAO;
import model.mysql.dao.principal.ConstanteDAO;
import model.mysql.dao.principal.TipoOperacaoDAO;
import model.mysql.dao.principal.RecursoDAO;
import model.mysql.dao.principal.UsuarioDAO;
import model.mysql.dao.principal.VendaDAO;
import model.mysql.dao.principal.VendaTipoDAO;
import model.mysql.dao.principal.catalogo.ProdutoTipoDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import util.Atualizacao;
import util.DateTime;
import util.Decimal;
import util.MwConfig;
import util.Sistema;
import util.enitities.DocumentoUtil;
import view.LoginView;
import view.MainView;
import view.Toast;
import view.sistema.AtivarView;

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
    
    public static final String MW_NOME_FANTASIA = "Mindware";
    public static final String MW_WEBSITE = "mwdesenvolvimento.com.br";
    public static final String MW_FONES = "(19)3813.2888 / (19)3813.5234 / Whatsapp (19)99887.4389";
    public static final String SISTEMA_NOME = "Mindware B3";
    public static final String SISTEMA_ASSINATURA = SISTEMA_NOME + " " + MW_WEBSITE;
    
    public static final int MENU_MIN_WIDTH = 50;
    public static final int MENU_MAX_WIDTH = 300;
    public static final int TOOLBAR_HEIGHT = 30;
    
    public static int SCREEN_WIDTH = 0;
    public static int SCREEN_HEIGHT = 0;
    
    public final static MainView MAIN_VIEW = new MainView();
    
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
    public static String IMPRESSORA_A4;
    public static String IMPRESSORA_ETIQUETA;
    public static String IMPRESSORA_FORMATO_PADRAO;
    public static Boolean IMPRESSORA_DESATIVAR;
    public static String IMPRESSAO_RODAPE;
    
    
    public static String SOFTWARE_HOUSE_CNPJ;
    public static String TO_SAT_PATH;
    public static String FROM_SAT_PATH;
    public static Boolean SAT_HABILITAR;
    public static String SAT_DLL;
    public static String SAT_CODIGO_ATIVACAO;
    public static String SAT_SIGN_AC;
    public static String SAT_PRINTER;
    
    public static Integer SAT_MARGEM_ESQUERDA;
    public static Integer SAT_MARGEM_DIREITA;
    public static Integer SAT_MARGEM_SUPERIOR;
    public static Integer SAT_MARGEM_INFERIOR;
    
    public static Boolean NFE_HABILITAR;
    public static Integer NFE_PROXIMO_NUMERO;
    
    public static String TO_PRINTER_PATH;
    public static String BACKUP_PATH;
    
    public static BigDecimal CLIENTE_LIMITE_CREDITO;
    
    public static boolean VENDA_INSERCAO_DIRETA;
    public static BigDecimal PARCELA_MULTA;
    public static BigDecimal PARCELA_JUROS_MONETARIO_MENSAL;
    public static BigDecimal PARCELA_JUROS_PERCENTUAL_MENSAL;
    public static Integer VENDA_NUMERO_COMANDAS;
    public static String VENDA_LAYOUT_COMANDAS;
    public static boolean VENDA_BLOQUEAR_PARCELAS_EM_ATRASO;
    public static boolean VENDA_BLOQUEAR_CREDITO_EXCEDIDO;
    public static boolean VENDA_EXIBIR_VEICULO;
    public static boolean SISTEMA_MODO_BALCAO;
    public static boolean VENDA_ABRIR_COMANDAS_AO_INICIAR;
    
    public static Usuario USUARIO = new Usuario();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //Constants for jTable
        CELL_RENDERER_ALIGN_CENTER.setHorizontalAlignment(SwingConstants.CENTER);
        CELL_RENDERER_ALIGN_RIGHT.setHorizontalAlignment(SwingConstants.RIGHT);

        //new LowLevel().removerForeignKey("produto", "icms");
        
        //System.exit(0);
        
        CONNECTION_FACTORY = new ConnectionFactory();
    
        em = CONNECTION_FACTORY.getConnection();
        
        
        emBs = new ConnectionFactory().getConnectionBootstrap();
        
        
        new VendaDAO().getComandasAbertasSnapshot();
        
        
        
        //Trava - liberar sistema
        //SISTEMA_VALIDADE = Sistema.getValidade();
        
        //System.out.println("Validade: " + SISTEMA_CHAVE);
        
        if(!Sistema.checkValidade() && false) {
            AtivarView ativar = new AtivarView();
        }
        
        SISTEMA_MODO_BALCAO = Boolean.parseBoolean(MwConfig.getValue("SISTEMA_MODO_BALCAO"));
        System.out.println("SISTEMA_MODO_BALCAO: " + SISTEMA_MODO_BALCAO);
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
        for(Usuario usuario : new UsuarioDAO().findAll()) {
            usuario.removeDiretiva(usuario.findDiretiva(Recurso.SISTEMA));
            usuario.removeDiretiva(usuario.findDiretiva(Recurso.USUARIOS));
        }
        new RecursoDAO().delete(Recurso.SISTEMA);
        new RecursoDAO().delete(Recurso.USUARIOS);
        
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
            Atualizacao.setVersaoAtual(LocalDate.of(2019, 6, 10));
            
            new Toast("NOTA TÉCNICA: Atualizar CaixaItem -> caixaItemTipoId: trocar 8 por 2", false);
        }
        
        //2019-06-15
        if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 6, 15)) < 0) {
            new Toast("Criando tipos produto/serviço");
            new ProdutoTipoDAO().bootstrap();
            Atualizacao.setVersaoAtual(LocalDate.of(2019, 6, 15));
            
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
        
        //**********************************************************************
    /////    if(Atualizacao.getVersaoAtual().compareTo(LocalDate.of(2019, 7, 24)) < 0) {
    /////        new Toast("NOTA TÉCNICA: Copiar pasta com nfe/schemas", false);
    /////    }
        //**********************************************************************
        
        //2019-06-13
        //Registrar última versão
        Atualizacao.setVersaoAtual(Atualizacao.getUltimaData());
        
        MAIN_VIEW.setMensagem("Bootstrap automático concluído. Sistema liberado.");
        
        //Fim do Bootstrap automático ------------------------------------------
        
        if(!Sistema.checkValidade()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Sistema sem chave.", "Atenção", JOptionPane.WARNING_MESSAGE);
            AtivarView ativar = new AtivarView();
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
        
        
        new ConstanteDAO().bootstrap(); //Criar as constantes (se já existir ele ignora)
        
        
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
        IMPRESSORA_A4 = MwConfig.getValue("IMPRESSORA_A4");
        IMPRESSORA_ETIQUETA = MwConfig.getValue("IMPRESSORA_ETIQUETA");
        IMPRESSORA_FORMATO_PADRAO = MwConfig.getValue("IMPRESSORA_FORMATO_PADRAO");
        IMPRESSORA_DESATIVAR = Boolean.parseBoolean(MwConfig.getValue("IMPRESSORA_DESATIVAR"));
        
        IMPRESSAO_RODAPE = ConstanteDAO.getValor("IMPRESSAO_RODAPE");
        
        APP_PATH = APP_PATH.substring(0, APP_PATH.length() - 1);
        
        SOFTWARE_HOUSE_CNPJ = ConstanteDAO.getValor("SOFTWARE_HOUSE_CNPJ");
        TO_SAT_PATH = APP_PATH + ConstanteDAO.getValor("TO_SAT_PATH");
        System.out.println("TO_SAT_PATH: " + TO_SAT_PATH);
        
        FROM_SAT_PATH = APP_PATH + ConstanteDAO.getValor("FROM_SAT_PATH");
        SAT_HABILITAR = Boolean.parseBoolean(ConstanteDAO.getValor("SAT_HABILITAR"));
        SAT_DLL = ConstanteDAO.getValor("SAT_DLL");
        SAT_CODIGO_ATIVACAO = ConstanteDAO.getValor("SAT_CODIGO_ATIVACAO");
        SAT_SIGN_AC = ConstanteDAO.getValor("SAT_SIGN_AC");
        SAT_PRINTER = ConstanteDAO.getValor("SAT_PRINTER");
        
        SAT_MARGEM_ESQUERDA = Integer.parseInt(ConstanteDAO.getValor("SAT_MARGEM_ESQUERDA"));
        SAT_MARGEM_DIREITA = Integer.parseInt(ConstanteDAO.getValor("SAT_MARGEM_DIREITA"));
        SAT_MARGEM_SUPERIOR = Integer.parseInt(ConstanteDAO.getValor("SAT_MARGEM_SUPERIOR"));
        SAT_MARGEM_INFERIOR = Integer.parseInt(ConstanteDAO.getValor("SAT_MARGEM_INFERIOR"));
        
        NFE_HABILITAR = Boolean.parseBoolean(ConstanteDAO.getValor("NFE_HABILITAR"));
        NFE_PROXIMO_NUMERO = Integer.parseInt(ConstanteDAO.getValor("NFE_PROXIMO_NUMERO"));
        
        TO_PRINTER_PATH = ConstanteDAO.getValor("TO_PRINTER_PATH");
        
        BACKUP_PATH = ConstanteDAO.getValor("BACKUP_PATH");
        
        CLIENTE_LIMITE_CREDITO = Decimal.fromString(ConstanteDAO.getValor("CLIENTE_LIMITE_CREDITO").replace(".", ","));
        
        VENDA_INSERCAO_DIRETA = Boolean.parseBoolean(ConstanteDAO.getValor("VENDA_INSERCAO_DIRETA"));
        PARCELA_JUROS_MONETARIO_MENSAL = Decimal.fromString(ConstanteDAO.getValor("PARCELA_JUROS_MONETARIO_MENSAL").replace(".", ","));
        PARCELA_JUROS_PERCENTUAL_MENSAL = Decimal.fromString(ConstanteDAO.getValor("PARCELA_JUROS_PERCENTUAL_MENSAL").replace(".", ","));
        PARCELA_MULTA = Decimal.fromString(ConstanteDAO.getValor("PARCELA_MULTA").replace(".", ","));
        VENDA_NUMERO_COMANDAS = Integer.valueOf(ConstanteDAO.getValor("VENDA_NUMERO_COMANDAS"));
        VENDA_LAYOUT_COMANDAS = MwConfig.getValue("VENDA_LAYOUT_COMANDAS");
        VENDA_BLOQUEAR_PARCELAS_EM_ATRASO = Boolean.parseBoolean(ConstanteDAO.getValor("VENDA_BLOQUEAR_PARCELAS_EM_ATRASO"));
        VENDA_BLOQUEAR_CREDITO_EXCEDIDO = Boolean.parseBoolean(ConstanteDAO.getValor("VENDA_BLOQUEAR_CREDITO_EXCEDIDO"));
        VENDA_EXIBIR_VEICULO = Boolean.parseBoolean(ConstanteDAO.getValor("VENDA_EXIBIR_VEICULO"));
        
        VENDA_ABRIR_COMANDAS_AO_INICIAR = Boolean.parseBoolean(MwConfig.getValue("VENDA_ABRIR_COMANDAS_AO_INICIAR"));
        
        
        
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
        
        
        if(!Ouroboros.SISTEMA_MODO_BALCAO) {
            MAIN_VIEW.carregarMenu();
        }
        
        if(Ouroboros.VENDA_ABRIR_COMANDAS_AO_INICIAR) {
            MAIN_VIEW.addView(DocumentoUtil.exibirComandas());
        }
        
        
        
    }
    
}
