package printing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.ImageIcon;
import model.bean.principal.ConversaoString;
import model.bean.principal.MovimentoFisico;
import model.bean.principal.Produto;
import model.bean.principal.Venda;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import static ouroboros.Ouroboros.APP_PATH;

public class RelatorioPdf {

    /**
     * @param args the command line arguments
     */
    
    public static void geraRelatorio(Venda venda) {    
        try {  
            
                
                ConversaoString emitente = new ConversaoString(); // criei outro objeto item para construir o objeto com os valores referentes ao emitente
                List<MovimentoFisico> teste = venda.getMovimentosFisicosSaida();
                //Venda venda = new Venda();
                //VendaDAO vendaDAO = new VendaDAO();
                //venda = vendaDAO.findById(1);
                
            
            ImageIcon imagemTituloJanela = new ImageIcon(APP_PATH + "\\reports\\IconeRelatorio.png");                 
                //Caminho do arquivo .JASPER    
                String relatorio = (APP_PATH + "\\reports\\Rossi.jasper"); 
                
                //logo
                //InputStream logo = new FileInputStream("C:\\Users\\User\\Downloads\\IconeRelatorio.png");
                
                HashMap mapa = new HashMap();  
                List <ConversaoString> itens = new ArrayList<>();
                List <ConversaoString> emitentes = new ArrayList<>(); // criei outra lista para passar os valores dos campos
                
                LocalDateTime hoje = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String hojeFormatado = hoje.format(formatter);
                
                             
                emitente.setNome(venda.getCliente().getNome());
                emitente.setCnpj(venda.getCliente().getCnpj());
                emitente.setCep(venda.getCliente().getCep());
                emitente.setTelefone1(venda.getCliente().getTelefone1());
                emitente.setTelefone2(venda.getCliente().getTelefone2());
                emitente.setIe(venda.getCliente().getIe());
                emitente.setIm("5334634");
                emitente.setEmail(venda.getCliente().getEmail());
                
                
                emitentes.add(emitente);

                
                for(MovimentoFisico mf : teste){
                    ConversaoString item = new ConversaoString();
                    item.setQuantidade(mf.getSaida());
                    item.setDescricao(mf.getProduto().getNome());
                    item.setValor(mf.getValor()); 
                    item.setSubTotal(mf.getSubtotal());
                    item.setDataEntrega(mf.getDataSaidaPrevista());
                    item.setDataRetirada(mf.getDataEntradaPrevista());
                    item.setValorTotal(venda.getTotal());

                    itens.add(item);
                }
                
                JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(itens);
                JRBeanCollectionDataSource jrSource = new JRBeanCollectionDataSource(emitentes);
                
                //mapa.put("emit", emit);
                mapa.put("itens", jr);
                mapa.put("header", APP_PATH + "\\custom\\cabecalho_A4.jpg");
                mapa.put("footer", APP_PATH + "\\custom\\rodape_A4.jpg");
                
                //Gerando o relatorio (Filling) informando o caminho do relatorio, os parametros (neste caso nenhum paramentro esta sendo passado ao relatorio, por isso o HashMap esta vazio) e o objeto JRXmlDataSource configurado)    
                JasperPrint jp = JasperFillManager.fillReport(relatorio, mapa, jrSource);     
                //Utilizando o JasperView, uma classe desktop do jasper para visualização dos relatorios    
                JasperViewer jv = new JasperViewer(jp, false);    
                jv.setTitle("Relatório PDF");  
                jv.setIconImage(imagemTituloJanela.getImage());  
                jv.setVisible(true);   
                //exportando arquivo para pdf  
                //JasperExportManager.exportReportToPdfFile(jp, "C:\\Users\\User\\Downloads\\jrxml\\arquivo.pdf");  
                //Runtime.getRuntime().exec("cmd /c start C:\\Users\\User\\Downloads\\jrxml\\arquivo.pdf");  
                //deletando arquivo  
                //File file = new File("C:\\Users\\User\\Downloads\\jrxml\\arquivo.pdf");  
                //file.deleteOnExit();  
        } catch (JRException e) {    
            e.printStackTrace();    
        }    
    } 
}
