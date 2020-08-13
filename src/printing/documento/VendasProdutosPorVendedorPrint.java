package printing.documento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.documento.ProdutoPorVendedorConsolidado;
import model.nosql.relatorio.VendaItemConsolidadoReportBean;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.APP_PATH;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;

public class VendasProdutosPorVendedorPrint {

    public static void gerarA4(List<Venda> documentos, LocalDate dataInicial, LocalDate dataFinal) {
        try {
            if (documentos.isEmpty()) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem registros no período selecionado", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                
            } else {

                String relatorio = (APP_PATH + "\\reports\\VendasProdutosPorVendedor.jasper");

                HashMap map = new HashMap();

                map.put("sistemaAssinatura", Ouroboros.SISTEMA_ASSINATURA);
                map.put("dataInicial", DateTime.toString(dataInicial));
                map.put("dataFinal", DateTime.toString(dataFinal));

                List<ProdutoPorVendedorConsolidado> itens = new ArrayList<>();

                for (Venda doc : documentos) {
                    //System.out.println("documento id: " + doc.getId());
                    for (MovimentoFisico mf : doc.getMovimentosFisicos().stream().filter(mf -> !mf.isBonificacao()).collect(Collectors.toList())) {

                        ProdutoPorVendedorConsolidado i = new ProdutoPorVendedorConsolidado();
                        //System.out.println("vendedor: " + doc.getFuncionario());
                        i.setVendedor(doc.getFuncionario());
                        i.setProduto(mf.getProduto());
                        i.setQuantidade(mf.getSaida());
                        i.setTotal(mf.getSubtotal());

                        List<ProdutoPorVendedorConsolidado> itensTemp;
                        if (doc.getFuncionario() == null) {
                            itensTemp = itens.stream().filter(
                                    item -> (item.getVendedor() == null
                                    && item.getProduto().equals(i.getProduto()))
                            ).collect(Collectors.toList());

                        } else {
                            itensTemp = itens.stream().filter(
                                    item -> (item.getVendedor() != null && item.getVendedor().equals(i.getVendedor())
                                    && item.getProduto().equals(i.getProduto()))
                            ).collect(Collectors.toList());
                        }

                        if (itensTemp.isEmpty()) {
                            itens.add(i);

                        } else {
                            int index = itens.indexOf(itensTemp.get(0));
                            itens.get(index).setQuantidade(
                                    itens.get(index).getQuantidade().add(mf.getSaida())
                            );
                            itens.get(index).setTotal(
                                    itens.get(index).getTotal().add(mf.getSubtotal())
                            );
                        }
                    }
                }

                //Ordem dos grupos
                itens.sort(Comparator.comparing(i -> i.getProduto().getNome()));

                List<VendaItemConsolidadoReportBean> itensPrint = new ArrayList<>();

                for (ProdutoPorVendedorConsolidado i : itens) {
                    VendaItemConsolidadoReportBean iPrint = new VendaItemConsolidadoReportBean();

                    iPrint.setVendedor(i.getVendedor() != null ? i.getVendedor().getNome() : "--NÃO INFORMADO--");
                    iPrint.setProduto(i.getProduto().getNome());
                    iPrint.setQuantidade(i.getQuantidade());
                    iPrint.setTotal(i.getTotal());

                    itensPrint.add(iPrint);
                }

                //Ordem dos itens dentro de cada grupo
                itensPrint.sort(Comparator.comparing(VendaItemConsolidadoReportBean::getVendedor));

                JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(itensPrint);

                JasperPrint jp = JasperFillManager.fillReport(relatorio, map, jr);

                JasperViewer jv = new JasperViewer(jp, false);

                //jv.setTitle("Produtos Vendidos por Vendedor");
                jv.setVisible(true);
            }

        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório " + e);
        }
    }

}
