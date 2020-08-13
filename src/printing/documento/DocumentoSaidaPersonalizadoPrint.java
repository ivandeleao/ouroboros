package printing.documento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.documento.Venda;
import model.nosql.relatorio.MovimentoFisicoToStringAdapter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import static ouroboros.Ouroboros.APP_PATH;
import util.DateTime;
import util.Decimal;

public class DocumentoSaidaPersonalizadoPrint {

    
    public static void gerarA4(Venda documento) {    
        try {
            String relatorio = (APP_PATH + "\\reports\\DocumentoSaidaOriginalArt.jasper");
            
            HashMap map = new HashMap();
            map.put("id", documento.getId().toString());
            map.put("data", DateTime.toStringDate(documento.getDataHora()));
            map.put("observacao", documento.getObservacao());
            
            //System.out.println(map.get("observacao"));
            
            if(documento.getPessoa() != null) {
                map.put("clienteNome", documento.getPessoa().getId() + " - " + documento.getPessoa().getNomeConfigurado());
                map.put("clienteContato", documento.getComandaNome());
                map.put("clienteTelefone", documento.getPessoa().getTelefone1());
                map.put("clienteEndereco", documento.getPessoa().getEnderecoSimples());
            }
            
            if (!documento.getParcelasAPrazo().isEmpty()) {
                map.put("vencimento", DateTime.toString(documento.getParcelasAPrazo().get(0).getVencimento()));
            }
            
            
            //Itens da venda----------------------------------------------------
            //List<MovimentoFisicoToStringAdapter> mfsReport = MovimentoFisicoToStringAdapter.adaptList(documento.getMovimentosFisicosSaida());
            //JRBeanCollectionDataSource jrItens = new JRBeanCollectionDataSource(mfsReport);
            
            List<MovimentoFisicoToStringAdapter> mfsReport = new ArrayList<>();
            
            for (MovimentoFisico mf : documento.getMovimentosFisicos()) {
                MovimentoFisicoToStringAdapter mfReport = new MovimentoFisicoToStringAdapter();
                mfReport.setCodigo(mf.getCodigo());
                mfReport.setQuantidade(Decimal.toStringDescarteDecimais(mf.getSaida()));
                if(mf.getUnidadeComercialVenda() != null) {
                    mfReport.setUnidadeMedida(mf.getUnidadeComercialVenda().getNome());
                }
                mfReport.setValor(Decimal.toString(mf.getValor()));
                mfReport.setAcrescimo(mf.getAcrescimoFormatado());
                mfReport.setDesconto(mf.getDescontoFormatado());
                mfReport.setSubtotal(Decimal.toString(mf.getSubtotal()));
                mfReport.setDescricao(mf.getDescricao());
                
                mfsReport.add(mfReport);
            }
            
            JRBeanCollectionDataSource jrItens = new JRBeanCollectionDataSource(mfsReport);
            //Fim Itens da venda------------------------------------------------

            
            map.put("total", Decimal.toString(documento.getTotal()));
            
            JasperPrint jp = JasperFillManager.fillReport(relatorio, map, jrItens);
            JasperViewer jv = new JasperViewer(jp, false);

            jv.setVisible(true);   
            
            
        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório " + e);
        }
    } 
    
}
