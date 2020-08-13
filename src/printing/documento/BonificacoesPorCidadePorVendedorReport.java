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
import model.nosql.relatorio.documento.VendaItemPorCidadePorVendedorReportBean;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.APP_PATH;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;

public class BonificacoesPorCidadePorVendedorReport {

    public static void gerarA4(List<Venda> documentos, LocalDate dataInicial, LocalDate dataFinal) {
        try {
            if (documentos.isEmpty()) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem registros no período selecionado", "Atenção", JOptionPane.INFORMATION_MESSAGE);

            } else {

                String relatorio = (APP_PATH + "\\reports\\BonificacoesPorCidadePorVendedor.jasper");

                HashMap map = new HashMap();

                map.put("sistemaAssinatura", Ouroboros.SISTEMA_ASSINATURA);
                map.put("dataInicial", DateTime.toString(dataInicial));
                map.put("dataFinal", DateTime.toString(dataFinal));

                List<VendaItemPorCidadePorVendedorReportBean> itens = new ArrayList<>();

                //Vendedor Cidade ProdutoBonificado Cliente Quantidade
                //Soma quantidade por Cidade
                //Soma quantidade por Vendedor
                //Soma quantidade geral
                for (Venda doc : documentos) {
                    //System.out.println("documento id: " + doc.getId());
                    for (MovimentoFisico mf : doc.getMovimentosFisicos().stream().filter(mf -> mf.isBonificacao()).collect(Collectors.toList())) {

                        VendaItemPorCidadePorVendedorReportBean i = new VendaItemPorCidadePorVendedorReportBean();
                        //System.out.println("vendedor: " + doc.getFuncionario());
                        String vendedor = doc.getFuncionario() != null ? doc.getFuncionario().getNome() : "--VENDEDOR NÃO INFORMADO--";
                        i.setVendedor(vendedor);

                        String cidade = doc.getPessoa() != null ? (!doc.getPessoa().getMunicipio().isEmpty() ? doc.getPessoa().getMunicipio() : "--CIDADE NÃO INFORMADA--") : "--CIDADE NÃO INFORMADA--";
                        i.setCidade(cidade);

                        i.setProduto(mf.getProduto().getCodigo() + " - " + mf.getProduto().getNome());

                        String cliente = doc.getPessoa() != null ? doc.getPessoa().getId() + " - " + doc.getPessoa().getNomeConfigurado(): "--CLIENTE NÃO INFORMADO--";
                        i.setCliente(cliente);

                        i.setQuantidade(mf.getSaida());

                        itens.add(i);
                    }
                }

                itens.sort(
                        Comparator.comparing(VendaItemPorCidadePorVendedorReportBean::getVendedor)
                                .thenComparing(VendaItemPorCidadePorVendedorReportBean::getCidade)
                                .thenComparing(VendaItemPorCidadePorVendedorReportBean::getProduto)
                                .thenComparing(VendaItemPorCidadePorVendedorReportBean::getCliente)
                );
                //itens.sort(Comparator.comparing(VendaItemPorCidadePorVendedorReportBean::getCidade));
                //itens.sort(Comparator.comparing(VendaItemPorCidadePorVendedorReportBean::getProduto));

                for (VendaItemPorCidadePorVendedorReportBean i : itens) {
                    System.out.print(i.getVendedor());
                    System.out.print("\t" + i.getCidade());
                    System.out.print("\t" + i.getProduto());
                    System.out.print("\t" + i.getQuantidade());
                    System.out.println();
                }

                JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(itens);

                JasperPrint jp = JasperFillManager.fillReport(relatorio, map, jr);

                JasperViewer jv = new JasperViewer(jp, false);

                jv.setVisible(true);

            }

        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório " + e);
        }
    }

}
