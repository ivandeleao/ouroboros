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
import javax.persistence.EntityManager;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import model.mysql.bean.principal.Constante;
import model.mysql.bean.principal.Usuario;
import model.bootstrap.dao.NcmBsDAO;
import model.mysql.dao.fiscal.NcmDAO;
import model.mysql.dao.fiscal.SatCupomTipoDAO;
import model.mysql.dao.fiscal.nfe.DestinoOperacaoDAO;
import model.mysql.dao.fiscal.nfe.FinalidadeEmissaoDAO;
import model.mysql.dao.fiscal.nfe.NaturezaOperacaoDAO;
import model.mysql.dao.fiscal.nfe.RegimeTributarioDAO;
import model.mysql.dao.fiscal.nfe.TipoAtendimentoDAO;
import model.mysql.dao.fiscal.nfe.TipoEmissaoDAO;
import model.mysql.dao.principal.CaixaItemTipoDAO;
import model.mysql.dao.principal.ConstanteDAO;
import model.mysql.dao.principal.TipoOperacaoDAO;
import model.mysql.dao.principal.RecursoDAO;
import model.mysql.dao.principal.VendaTipoDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import util.DateTime;
import util.Decimal;
import util.MwConfig;
import util.Sistema;
import view.LoginView;
import view.MainView;
import view.Toast;
import view.sistema.Ativar;

/**
 *
 * @author ivand
 */
public class Ouroboros {
    public static String SISTEMA_ID;
    public static String SISTEMA_CHAVE; //validade id - dv
    
    public static String APP_VERSION = "20190406";
    public static String APP_PATH = new File(".").getAbsolutePath();
    
    public static String SERVER = MwConfig.getValue("server");
    
    public static final String MW_NOME_FANTASIA = "Mindware";
    public static final String MW_WEBSITE = "mwdesenvolvimento.com.br";
    public static final String SISTEMA_NOME = "B3";
    
    public static final int MENU_MIN_WIDTH = 50;
    public static final int MENU_MAX_WIDTH = 300;
    public static final int TOOLBAR_HEIGHT = 30;
    
    public static int SCREEN_WIDTH = 0;
    public static int SCREEN_HEIGHT = 0;
    
    public final static MainView MAIN_VIEW = new MainView();
    
    public static ConnectionFactory CONNECTION_FACTORY = new ConnectionFactory();
    
    public static EntityManager em = CONNECTION_FACTORY.getConnection();
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
    public static String IMPRESSORA_FORMATO_PADRAO;
    public static Boolean IMPRESSORA_DESATIVAR;
    
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
    
    public static String TO_PRINTER_PATH;
    public static String BACKUP_PATH;
    
    public static boolean VENDA_INSERCAO_DIRETA;
    public static BigDecimal PARCELA_MULTA;
    public static BigDecimal PARCELA_JUROS_MONETARIO_MENSAL;
    public static BigDecimal PARCELA_JUROS_PERCENTUAL_MENSAL;
    public static Integer VENDA_NUMERO_COMANDAS;
    
    public static Usuario USUARIO = new Usuario();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Long start = System.currentTimeMillis();
        //Constants for jTable
        CELL_RENDERER_ALIGN_CENTER.setHorizontalAlignment(SwingConstants.CENTER);
        CELL_RENDERER_ALIGN_RIGHT.setHorizontalAlignment(SwingConstants.RIGHT);

        emBs = new ConnectionFactory().getConnectionBootstrap();
        
        //Trava - liberar sistema
        //SISTEMA_VALIDADE = Sistema.getValidade();
        
        //System.out.println("Validade: " + SISTEMA_CHAVE);
        
        if(!Sistema.checkValidade() && false) {
            Ativar ativar = new Ativar();
        }
        
        
        LoginView loginView = new LoginView();
        
        USUARIO = loginView.getUsuario();
        if(USUARIO != null) {
            USUARIO = loginView.getUsuario();
        } else {
            System.exit(0);
        }
        MAIN_VIEW.setTitle(
                MAIN_VIEW.getTitle() + 
                        " | Versão " + APP_VERSION + 
                        " | Usuário: " + USUARIO.getLogin() +
                        " | Servidor: " + SERVER
        );
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
        
        CaixaItemTipoDAO caixaItemTipoDAO = new CaixaItemTipoDAO();
        if(caixaItemTipoDAO.findById(8) == null || !caixaItemTipoDAO.findById(8).getNome().equals("PAGAMENTO DOCUMENTO")) {
            new Toast("Atualizando CaixaItemTipo...");
            caixaItemTipoDAO.bootstrap();
        }
        
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
        
        MAIN_VIEW.setMensagem("Bootstrap automático concluído. Sistema liberado.");
        
        
        //Fim do Bootstrap automático ------------------------------------------
        
        if(!Sistema.checkValidade()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Sistema sem chave.", "Atenção", JOptionPane.WARNING_MESSAGE);
            Ativar ativar = new Ativar();
        }
        
        String msg = "";
        Long dias = Sistema.getValidadeEmDias();
        
        if(dias <= -5) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Os períodos de validade e carência para validação foram expirados. Você deve informar uma nova chave de ativação.", "Atenção", JOptionPane.WARNING_MESSAGE);
            Ativar ativar = new Ativar();
            dias = Sistema.getValidadeEmDias();
        }
        
        if(dias < 0) {
            msg = "ATENÇÃO: Seu sistema bloqueará em " + (dias + 5) + " dia(s)";
            
            JOptionPane.showMessageDialog(MAIN_VIEW, msg, "Atenção", JOptionPane.WARNING_MESSAGE);
            
        } else {
            msg = "Validade do sistema: " 
                + Sistema.getValidadeEmDias() + " dias"
                + " (" + DateTime.toStringDate(Sistema.getValidade()) + ")";
            
        }
        
        MAIN_VIEW.setMensagem(msg);
        
        
        new ConstanteDAO().bootstrap(); //Criar as constantes (se já existir ele ignora)
        
        
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
        IMPRESSORA_FORMATO_PADRAO = MwConfig.getValue("IMPRESSORA_FORMATO_PADRAO");
        IMPRESSORA_DESATIVAR = Boolean.parseBoolean(MwConfig.getValue("IMPRESSORA_DESATIVAR"));
        
        APP_PATH = APP_PATH.substring(0, APP_PATH.length() - 1);
        
        SOFTWARE_HOUSE_CNPJ = ConstanteDAO.getValor("SOFTWARE_HOUSE_CNPJ");
        TO_SAT_PATH = APP_PATH + ConstanteDAO.getValor("TO_SAT_PATH");
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
        
        TO_PRINTER_PATH = ConstanteDAO.getValor("TO_PRINTER_PATH");
        
        BACKUP_PATH = ConstanteDAO.getValor("BACKUP_PATH");
        
        
        VENDA_INSERCAO_DIRETA = Boolean.parseBoolean(ConstanteDAO.getValor("VENDA_INSERCAO_DIRETA"));
        PARCELA_JUROS_MONETARIO_MENSAL = Decimal.fromString(ConstanteDAO.getValor("PARCELA_JUROS_MONETARIO_MENSAL").replace(".", ","));
        PARCELA_JUROS_PERCENTUAL_MENSAL = Decimal.fromString(ConstanteDAO.getValor("PARCELA_JUROS_PERCENTUAL_MENSAL").replace(".", ","));
        PARCELA_MULTA = Decimal.fromString(ConstanteDAO.getValor("PARCELA_MULTA").replace(".", ","));
        VENDA_NUMERO_COMANDAS = Integer.valueOf(ConstanteDAO.getValor("VENDA_NUMERO_COMANDAS"));
        
        
        
        
        System.out.println("APP_PATH: " + APP_PATH);
        System.out.println("TO_SAT_PATH: " + TO_SAT_PATH);
        
        Long elapsed = System.currentTimeMillis() - start;
        
        
        //criar diretórios
        new File(FROM_SAT_PATH + "/processados/").mkdirs();
        new File(FROM_SAT_PATH + "/rejeitados/").mkdirs();
        new File(TO_SAT_PATH + "/processados/").mkdirs();
        new File(TO_SAT_PATH + "/rejeitados/").mkdirs();
        new File(TO_PRINTER_PATH).mkdir();
        new File(BACKUP_PATH).mkdir();
        new File("balanca").mkdir();
        
        
        
        
        
    }
    
}
