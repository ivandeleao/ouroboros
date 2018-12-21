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
import javax.swing.SwingConstants;
import model.bean.principal.Usuario;
import model.bean.principal.VendaTipo;
import model.dao.principal.ConstanteDAO;
import model.dao.principal.VendaTipoDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import util.Decimal;
import util.MwConfig;
import view.LoginView;
import view.MainView;
import view.Toast;

/**
 *
 * @author ivand
 */
public class Ouroboros {
    public static String APP_PATH = new File(".").getAbsolutePath();
    
    public static String SERVER = MwConfig.read("server");
    
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
    public static String EMPRESA_ENDERECO;
    
    public static String IMPRESSORA_PADRAO;
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

        
        //SERVER = MwConfig.read("server");
        
        //JOptionPane.showMessageDialog(MAIN_VIEW, SERVER);
        
        //em = new ConnectionFactory().getConnection();
        emBs = new ConnectionFactory().getConnectionBootstrap();
        
        LoginView loginView = new LoginView();
        
        USUARIO = loginView.getUsuario();
        if(USUARIO != null) {
            USUARIO = loginView.getUsuario();
        } else {
            System.exit(0);
        }
        MAIN_VIEW.setTitle(MAIN_VIEW.getTitle() + " | Usuário: " + USUARIO.getLogin());
        MAIN_VIEW.setExtendedState(MAXIMIZED_BOTH);
        MAIN_VIEW.setVisible(true);
        
        
        
        new ConstanteDAO().bootstrap();
        
        EMPRESA_NOME_FANTASIA = ConstanteDAO.getValor("EMPRESA_NOME_FANTASIA");
        EMPRESA_RAZAO_SOCIAL = ConstanteDAO.getValor("EMPRESA_RAZAO_SOCIAL");
        EMPRESA_CNPJ = ConstanteDAO.getValor("EMPRESA_CNPJ");
        EMPRESA_IE = ConstanteDAO.getValor("EMPRESA_IE");
        EMPRESA_ENDERECO = ConstanteDAO.getValor("EMPRESA_ENDERECO");
        
        IMPRESSORA_PADRAO = ConstanteDAO.getValor("IMPRESSORA_PADRAO");
        IMPRESSORA_DESATIVAR = Boolean.parseBoolean(ConstanteDAO.getValor("IMPRESSORA_DESATIVAR"));
        
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
        
        
        //Bootstrap automático
        VendaTipoDAO vendaTipoDAO = new VendaTipoDAO();
        if(vendaTipoDAO.findById(5) == null) {
            new Toast("Criando tipos de venda...");
            vendaTipoDAO.bootstrap();
        }
        
        
    }
    
}
