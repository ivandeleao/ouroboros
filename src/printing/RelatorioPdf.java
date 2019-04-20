package printing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.ImageIcon;
import model.mysql.bean.principal.ConversaoString;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.documento.Venda;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import static ouroboros.Ouroboros.APP_PATH;

public class RelatorioPdf {

    
    public static void gerarLocacaoOS(Venda venda) {    
        try {  
                
            ConversaoString emitente = new ConversaoString(); // criei outro objeto item para construir o objeto com os valores referentes ao emitente
            List<MovimentoFisico> teste = venda.getMovimentosFisicosSaida();
            
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
                
                             
                emitente.setNome(venda.getPessoa().getNome());
                emitente.setCnpj(venda.getPessoa().getCnpj());
                emitente.setCep(venda.getPessoa().getCep());
                emitente.setTelefone1(venda.getPessoa().getTelefone1());
                emitente.setTelefone2(venda.getPessoa().getTelefone2());
                emitente.setIe(venda.getPessoa().getIe());
                emitente.setIm("5334634");
                emitente.setEmail(venda.getPessoa().getEmail());
                
                
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
    
    public static void gerarRequisicaoMaterial(Venda venda) { 
        try {  
                
            ConversaoString emitente = new ConversaoString(); // criei outro objeto item para construir o objeto com os valores referentes ao emitente
            
            
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


            emitente.setNome(venda.getPessoa().getNome());
            emitente.setCnpj(venda.getPessoa().getCnpj());
            emitente.setCep(venda.getPessoa().getCep());
            emitente.setTelefone1(venda.getPessoa().getTelefone1());
            emitente.setTelefone2(venda.getPessoa().getTelefone2());
            emitente.setIe(venda.getPessoa().getIe());
            emitente.setIm("5334634");
            emitente.setEmail(venda.getPessoa().getEmail());

            emitentes.add(emitente);

            List<MovimentoFisico> movimentosFisicos = venda.getMovimentosFisicosSaida();
            
            //itens
            for(MovimentoFisico mf : movimentosFisicos) {
                ConversaoString item = new ConversaoString();
                item.setQuantidade(mf.getSaida());
                item.setDescricao(mf.getProduto().getNome());
                item.setValor(mf.getValor()); 
                item.setSubTotal(mf.getSubtotal());
                item.setDataEntrega(mf.getDataSaidaPrevista());
                item.setDataRetirada(mf.getDataEntradaPrevista());
                //item.setValorTotal(venda.getTotal()); ?????
                //item.setAsComponente(false); //Criar propriedade e método isComponente / setAsComponente
                //usar isComponente para pintar as linhas da tabela

                itens.add(item);
                
                //itens componentes
                for(MovimentoFisico mfComponente : mf.getMovimentosFisicosComponente()) {
                    ConversaoString itemComponente = new ConversaoString();
                    itemComponente.setQuantidade(mfComponente.getSaida());
                    itemComponente.setDescricao(mfComponente.getProduto().getNome());
                    itemComponente.setValor(mfComponente.getValor()); 
                    itemComponente.setSubTotal(mfComponente.getSubtotal());
                    itemComponente.setDataEntrega(mfComponente.getDataSaidaPrevista());
                    itemComponente.setDataRetirada(mfComponente.getDataEntradaPrevista());
                    //item.setValorTotal(venda.getTotal()); ?????
                    //item.setAsComponente(true);
                    
                    itens.add(itemComponente);
                }
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
