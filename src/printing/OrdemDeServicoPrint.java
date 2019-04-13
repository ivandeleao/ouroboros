package printing;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.ImageIcon;
import model.bean.principal.ConversaoString;
import model.bean.principal.MovimentoFisico;
import model.bean.principal.Parcela;
import model.bean.principal.Venda;
import model.bean.relatorio.CaixaPeriodoPorMeioDePagamentoReport;
import model.bean.relatorio.CaixaResumoPorMeioDePagamentoReport;
import model.bean.relatorio.MovimentoFisicoReport;
import model.bean.relatorio.ParcelaReport;
import model.bean.temp.CaixaResumoPorMeioDePagamento;
import model.dao.principal.VendaDAO;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.APP_PATH;
import static ouroboros.Ouroboros.EMPRESA_NOME_FANTASIA;
import util.DateTime;
import util.Decimal;
import util.Sistema;

public class OrdemDeServicoPrint {

    
    public static void gerarReport(Venda venda) {    
        try {
            String relatorio = (APP_PATH + "\\reports\\OS.jasper");
            
            //Itens da venda
            List<MovimentoFisicoReport> mfsReport = MovimentoFisicoReport.adaptList(venda.getMovimentosFisicosSaida());
            
            JRBeanCollectionDataSource data = new JRBeanCollectionDataSource(mfsReport);

            //Parcelas
            List<ParcelaReport> parcelas = new ArrayList<>();
            
            for(Parcela parcela : venda.getParcelasAPrazo()) {
                ParcelaReport p = new ParcelaReport();
                p.setNumero(parcela.getNumero().toString());
                p.setVencimento(DateTime.toStringDate(parcela.getVencimento()));
                p.setValor(Decimal.toString(parcela.getValor()));
                
                parcelas.add(p);
            }
            
            JRBeanCollectionDataSource dataParcelas = new JRBeanCollectionDataSource(parcelas);
            
            
            
            HashMap map = new HashMap();  
            map.put("id", venda.getId().toString());
            map.put("data", DateTime.toStringDate(venda.getCriacao()));
            
            map.put("empresaLogo", APP_PATH + "\\custom\\empresa_logo.jpg");
            map.put("empresaNome", Ouroboros.EMPRESA_NOME_FANTASIA);
            map.put("empresaCnpj", Ouroboros.EMPRESA_CNPJ);
            map.put("empresaEndereco", Sistema.getEnderecoCompleto());
            map.put("empresaTelefone", Ouroboros.EMPRESA_TELEFONE);
            
            if(venda.getFuncionario() != null) {
                map.put("funcionario", venda.getFuncionario().getNome());
            }
            
            if(venda.getPessoa() != null) {
                map.put("clienteNome", venda.getPessoa().getNome());
                map.put("clienteCpfCnpj", venda.getPessoa().getCpfOuCnpj());
                map.put("clienteTelefone", venda.getPessoa().getTelefone1());
                map.put("clienteEndereco", venda.getPessoa().getEnderecoCompleto());
            }
            
            map.put("itens", data);
            
            map.put("totalItens", Decimal.toString(venda.getTotalItens()));
            map.put("acrescimo", venda.getAcrescimoAplicado());
            map.put("desconto", venda.getDescontoAplicado());
            
            map.put("total", Decimal.toString(venda.getTotal()));
            map.put("recebido", Decimal.toString(venda.getTotalRecebido()));
            map.put("receber", Decimal.toString(venda.getTotalReceber()));
            
            map.put("observacao", venda.getObservacao());
            
            map.put("parcelas", dataParcelas);
                    
            List<CaixaPeriodoPorMeioDePagamentoReport> dadosBase = new ArrayList<>();
            
            CaixaPeriodoPorMeioDePagamentoReport dado = new CaixaPeriodoPorMeioDePagamentoReport();
            
            
            dadosBase.add(dado);
            JRBeanCollectionDataSource jrSource = new JRBeanCollectionDataSource(dadosBase);
            
            JasperPrint jp = JasperFillManager.fillReport(relatorio, map, jrSource);
            JasperViewer jv = new JasperViewer(jp, false);

            jv.setTitle("Ordem de Serviço");  
            jv.setVisible(true);   
            
            
        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório " + e);
        }
    } 
    
}
