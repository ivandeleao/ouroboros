package printing.documento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.mysql.bean.principal.documento.OSTransporte;
import model.mysql.bean.principal.documento.OSTransporteItem;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.nosql.relatorio.CaixaPeriodoPorMeioDePagamentoReport;
import model.nosql.relatorio.MovimentoFisicoToStringAdapter;
import model.nosql.relatorio.OstItemToStringAdapter;
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

public class OSTransportePrint {

    
    public static void gerarA4(OSTransporte ost) {    
        try {
            String relatorio = (APP_PATH + "\\reports\\OSTransporte.jasper");
            
            //Itens
            List<OstItemToStringAdapter> ostItensAdapter = new ArrayList<>();
            
            for(OSTransporteItem ostItem : ost.getOsTranporteItens()) {
                OstItemToStringAdapter itemAdapter = new OstItemToStringAdapter();
                
                itemAdapter.setDescricao(ostItem.getDescricao());
                
                String destinatarioNome = !ostItem.getDestinatario().getNomeFantasia().isEmpty() ? ostItem.getDestinatario().getNomeFantasia() : ostItem.getDestinatario().getNome();
                
                itemAdapter.setLocal(destinatarioNome + " " + ostItem.getEndereco() + " " + ostItem.getDestinatario().getTelefone1());
                itemAdapter.setCidade(ostItem.getCidade() + " " + ostItem.getUf());
                itemAdapter.setValor(Decimal.toString(ostItem.getValor()));
                itemAdapter.setPedagio(Decimal.toString(ostItem.getPedagioValor()));
                itemAdapter.setAdicional(Decimal.toString(ostItem.getAdicionalValor()));
                itemAdapter.setSubtotal(Decimal.toString(ostItem.getSubtotal()));
                
                ostItensAdapter.add(itemAdapter);
            }
            
            JRBeanCollectionDataSource ostItensReport = new JRBeanCollectionDataSource(ostItensAdapter);
            JRBeanCollectionDataSource ostItensReport2via = new JRBeanCollectionDataSource(ostItensAdapter);
            
            HashMap map = new HashMap();  
            map.put("id", ost.getId().toString());
            map.put("data", DateTime.toStringDate(ost.getCriacao()) + " " + DateTime.toStringHoraMinuto(ost.getCriacao()));
            
            map.put("empresaLogo", APP_PATH + "\\custom\\empresa_logo.jpg");
            map.put("empresaNome", Ouroboros.EMPRESA_NOME_FANTASIA);
            map.put("empresaCnpj", Ouroboros.EMPRESA_CNPJ);
            map.put("empresaEndereco", Sistema.getEnderecoCompleto());
            map.put("empresaTelefone", Ouroboros.EMPRESA_TELEFONE);
            map.put("empresaEmail", Ouroboros.EMPRESA_EMAIL);
            
            map.put("remetenteNome", ost.getRemetente().getId() + " - " + ost.getRemetente().getNome());
            map.put("remetenteCpfCnpj", ost.getRemetente().getCpfOuCnpj());
            map.put("remetenteTelefone", ost.getRemetente().getTelefone1());
            map.put("remetenteEndereco", ost.getRemetente().getEnderecoCompleto());
            map.put("remetenteCidadeUf", ost.getRemetente().getMunicipio() + " " + ost.getRemetente().getUf());
            
            map.put("solicitanteNome", ost.getSolicitanteNome());
            map.put("solicitanteSetor", ost.getSolicitanteSetor());
            
            map.put("itens", ostItensReport);
            map.put("itens2via", ostItensReport2via);
            
            map.put("observacao", "Observação: " + ost.getObservacao());
            
            String motoristaNome = ost.getMotorista() != null ? ost.getMotorista().getNome() : "";
            map.put("motorista", motoristaNome);
            String veiculoPlaca = ost.getVeiculo() != null ? ost.getVeiculo().getPlaca() : "";
            map.put("placa", veiculoPlaca);
            String veiculoModelo = ost.getVeiculo() != null ? ost.getVeiculo().getModelo() : "";
            map.put("veiculo", veiculoModelo);

            map.put("totalItens", Decimal.toString(ost.getTotalItens()));
            map.put("desconto", Decimal.toString(ost.getDesconto()));
            map.put("total", Decimal.toString(ost.getTotal()));
            
            
            JasperPrint jp = JasperFillManager.fillReport(relatorio, map);
            JasperViewer jv = new JasperViewer(jp, false);

            jv.setTitle("OS Transporte");  
            jv.setVisible(true);   
            
            
        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório " + e);
        }
    } 
    
}
