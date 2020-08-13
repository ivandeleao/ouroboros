package printing.pessoa;

import java.math.BigDecimal;
import printing.documento.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.documento.ProdutoPorVendedorConsolidado;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.nosql.relatorio.VendaItemConsolidadoReportBean;
import model.nosql.relatorio.pessoa.ClienteReportBean;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.APP_PATH;
import util.DateTime;
import util.Texto;

public class ClientesSaldoPorCidadePorVendedorPrint {

    public static void gerarA4(List<Pessoa> pessoas) {
        try {
            String relatorio = (APP_PATH + "\\reports\\ClientesSaldoPorCidadePorVendedor.jasper");

            HashMap map = new HashMap();

            map.put("sistemaAssinatura", Ouroboros.SISTEMA_ASSINATURA);
            //map.put("dataInicial", DateTime.toString(dataInicial));
            //map.put("dataFinal", DateTime.toString(dataFinal));

            List<ClienteReportBean> clientes = new ArrayList<>();

            for (Pessoa pessoa : pessoas.stream().filter(p -> p.getTotalComprometido().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList())) {
                ClienteReportBean crb = new ClienteReportBean();
                
                crb.setVendedor(pessoa.getVendedor() != null ? pessoa.getVendedor().getNome() : "--Sem Vendedor--");
                crb.setCidade(!pessoa.getMunicipio().isEmpty() ? pessoa.getMunicipio() : "--Sem Cidade--");
                crb.setClienteId(pessoa.getId().toString());
                crb.setClienteNome(pessoa.getNomeConfigurado());
                crb.setSaldo(pessoa.getTotalComprometido());
                
                clientes.add(crb);
            }

            //Ordem dos grupos
            clientes.sort(Comparator
                    .comparing(ClienteReportBean::getVendedor)
                    .thenComparing(ClienteReportBean::getCidade)
                    .thenComparing(ClienteReportBean::getClienteNome)
            );
            
            
            /*for (ClienteReportBean c : clientes) {
                System.out.print(Texto.padRightAndCut(c.getVendedor(), 20));
                System.out.print("\t" + c.getCidade());
                System.out.print("\t\t" + c.getClienteNome());
                System.out.println("\t" + c.getSaldo());
            }*/
            
            JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(clientes);

            JasperPrint jp = JasperFillManager.fillReport(relatorio, map, jr);

            JasperViewer jv = new JasperViewer(jp, false);

            //jv.setTitle("Vendas Vendedores por Produto");
            jv.setVisible(true);

        } catch (Exception e) {
            System.err.println("Erro ao gerar relatório " + e);
        }
    }

}
