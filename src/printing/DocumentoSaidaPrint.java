package printing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.nosql.relatorio.CaixaPeriodoPorMeioDePagamentoReport;
import model.nosql.relatorio.MovimentoFisicoToStringAdapter;
import model.nosql.relatorio.ParcelaToStringAdapter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.APP_PATH;
import util.DateTime;
import util.Decimal;
import util.Sistema;

public class DocumentoSaidaPrint {

    
    public static void gerarA4(Venda venda) {    
        try {
            String relatorio = (APP_PATH + "\\reports\\DocumentoSaida.jasper");
            
            //Itens da venda
            List<MovimentoFisicoToStringAdapter> mfsReport = MovimentoFisicoToStringAdapter.adaptList(venda.getMovimentosFisicosSaida());
            
            JRBeanCollectionDataSource data = new JRBeanCollectionDataSource(mfsReport);

            //Parcelas
            List<ParcelaToStringAdapter> parcelas = new ArrayList<>();
            
            for(Parcela parcela : venda.getParcelasAPrazo()) {
                ParcelaToStringAdapter p = new ParcelaToStringAdapter();
                p.setNumero(parcela.getNumero().toString());
                p.setVencimento(DateTime.toString(parcela.getVencimento()));
                p.setValor(Decimal.toString(parcela.getValor()));
                
                parcelas.add(p);
            }
            
            JRBeanCollectionDataSource dataParcelas = new JRBeanCollectionDataSource(parcelas);
            
            
            
            HashMap map = new HashMap();  
            map.put("titulo", venda.getTitulo());
            map.put("id", venda.getId().toString());
            map.put("data", DateTime.toStringDate(venda.getCriacao()));
            
            map.put("empresaLogo", APP_PATH + "\\custom\\empresa_logo.jpg");
            map.put("empresaNome", Ouroboros.EMPRESA_NOME_FANTASIA);
            map.put("empresaCnpj", Ouroboros.EMPRESA_CNPJ);
            map.put("empresaEndereco", Sistema.getEnderecoCompleto());
            map.put("empresaTelefone", Ouroboros.EMPRESA_TELEFONE);
            map.put("empresaEmail", Ouroboros.EMPRESA_EMAIL);
            
            if(venda.getFuncionario() != null) {
                map.put("funcionario", venda.getFuncionario().getNome());
            }
            
            if(venda.getPessoa() != null) {
                map.put("clienteNome", venda.getPessoa().getId() + " - " + venda.getPessoa().getNome());
                map.put("clienteCpfCnpj", venda.getPessoa().getCpfOuCnpj());
                map.put("clienteTelefone", venda.getPessoa().getTelefone1());
                map.put("clienteEndereco", venda.getPessoa().getEnderecoCompleto());
            }
            
            if(venda.getVeiculo() != null) {
                map.put("veiculo", venda.getVeiculo().getPlaca() + " - " + venda.getVeiculo().getModelo());
            }
            
            map.put("itens", data);
            
            map.put("totalItensProdutos", Decimal.toString(venda.getTotalItensProdutos()));
            map.put("acrescimoProdutos", venda.getTotalAcrescimoFormatadoProdutos());
            map.put("descontoProdutos", venda.getTotalDescontoFormatadoProdutos());
            map.put("totalProdutos", Decimal.toString(venda.getTotalProdutos()));
            
            map.put("totalItensServicos", Decimal.toString(venda.getTotalItensServicos()));
            map.put("acrescimoServicos", venda.getTotalAcrescimoFormatadoServicos());
            map.put("descontoServicos", venda.getTotalDescontoFormatadoServicos());
            map.put("totalServicos", Decimal.toString(venda.getTotalServicos()));
            
            
            map.put("total", Decimal.toString(venda.getTotal()));
            map.put("recebido", Decimal.toString(venda.getTotalRecebidoAVista()));
            map.put("receber", Decimal.toString(venda.getTotalReceber()));
            
            if(!venda.getRelato().isEmpty()) {
                map.put("relato", "Relato/Solicitação do Cliente: " + venda.getRelato());
            }
            
            if(!venda.getObservacao().isEmpty()) {
                map.put("observacao", "Observação: " + venda.getObservacao());
            }
            
            map.put("parcelas", dataParcelas);
                    
            List<CaixaPeriodoPorMeioDePagamentoReport> dadosBase = new ArrayList<>();
            
            CaixaPeriodoPorMeioDePagamentoReport dado = new CaixaPeriodoPorMeioDePagamentoReport();
            
            
            dadosBase.add(dado);
            JRBeanCollectionDataSource jrSource = new JRBeanCollectionDataSource(dadosBase);
            
            JasperPrint jp = JasperFillManager.fillReport(relatorio, map, jrSource);
            JasperViewer jv = new JasperViewer(jp, false);

            jv.setTitle(venda.getTitulo());  
            jv.setVisible(true);   
            
            
        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório " + e);
        }
    } 
    
}
