package printing.documento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.principal.VendaDAO;
import model.nosql.relatorio.documento.VendaProdutoPorCidadeReportBean;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.APP_PATH;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.AnoMes;
import util.Decimal;
import util.Texto;

public class VendasProdutosPorCidadeReport {

    public static void gerarA4(YearMonth anoMesInicial, int mesesQuantidade) {
        long start = System.currentTimeMillis();
        try {

            mesesQuantidade--;

            LocalDate dataInicial = LocalDate.of(anoMesInicial.getYear(), anoMesInicial.getMonth(), 1);
            LocalDate dataFinal = dataInicial.plusMonths(mesesQuantidade);
            dataFinal = dataFinal.withDayOfMonth(dataFinal.lengthOfMonth());

            List<Venda> documentos = new VendaDAO().findByIntervalo(TipoOperacao.SAIDA, dataInicial.atTime(LocalTime.MIN), dataFinal.atTime(LocalTime.MAX));

            if (documentos.isEmpty()) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem registros no período selecionado", "Atenção", JOptionPane.INFORMATION_MESSAGE);

            } else {

                List<VendaProdutoPorCidadeReportBean> itens = new ArrayList<>();

                List<YearMonth> meses = new ArrayList<>();

                for (int m = 0; m <= mesesQuantidade; m++) {
                    meses.add(anoMesInicial.plusMonths(m));
                }

                String relatorio = (APP_PATH + "\\reports\\VendasProdutosPorCidade.jasper");

                HashMap map = new HashMap();

                map.put("sistemaAssinatura", Ouroboros.SISTEMA_ASSINATURA);
                map.put("dataInicial", AnoMes.toString(meses.get(0)));
                map.put("dataFinal", AnoMes.toString(meses.get(meses.size() - 1)));

                Set<String> cidades = new HashSet<>();

                //System.out.println("docs: " + documentos.size());
                for (Venda doc : documentos) {
                    cidades.add(doc.getPessoa() != null ? doc.getPessoa().getMunicipio() : "");
                }

                for (String cidade : cidades.stream().sorted().collect(Collectors.toList())) {

                    //System.out.println("cidade: " + cidade);
                    Set<String> produtos = new LinkedHashSet<>();

                    List<MovimentoFisico> mfsCidade = new ArrayList<>();

                    for (Venda doc : documentos.stream()
                            .filter(
                                    d -> (d.getPessoa() == null && cidade.equals(""))
                                    || //sem cliente = sem cidade
                                    (d.getPessoa() != null && d.getPessoa().getMunicipio().equals(cidade))
                            ).collect(Collectors.toList())) {

                        for (MovimentoFisico mf : doc.getMovimentosFisicos()) {

                            mfsCidade.add(mf);

                            produtos.add(mf.getProduto().getNome());

                        }

                    }

                    for (String produto : produtos.stream().sorted().collect(Collectors.toList())) {
                        //System.out.println("\t" + produto);

                        //obter os totais por mês
                        List<MovimentoFisico> mfsProduto = mfsCidade.stream().filter(mf -> mf.getProduto().getNome().equals(produto)).collect(Collectors.toList());

                        List<String> valores = new ArrayList<>();
                        List<String> quantidades = new ArrayList<>();
                        BigDecimal totalValor = BigDecimal.ZERO;
                        BigDecimal totalQuantidade = BigDecimal.ZERO;

                        for (YearMonth anoMes : meses) {
                            String mesNumero = "mes" + Texto.padLeftAndCut(String.valueOf(meses.indexOf(anoMes) + 1), 2, '0') + "Nome";
                            String mesNome = AnoMes.toString(anoMes);
                            System.out.println("mesNome: " + mesNome);

                            map.put(mesNumero, mesNome);

                            LocalDate dataBase = LocalDate.of(anoMes.getYear(), anoMes.getMonth(), 1);
                            LocalDateTime dataI = LocalDateTime.of(dataBase, LocalTime.MIN);
                            LocalDateTime dataF = LocalDateTime.of(dataBase.withDayOfMonth(dataBase.lengthOfMonth()), LocalTime.MAX);

                            //System.out.println("dataI: " + dataI + "\t dataF: " + dataF);
                            List<MovimentoFisico> mfsProdutoMes = mfsProduto.stream().filter(mf -> mf.getVenda().getDataHora().compareTo(dataI) >= 0
                                    && mf.getVenda().getDataHora().compareTo(dataF) <= 0)
                                    .collect(Collectors.toList());

                            System.out.println(anoMes + "\t" + mfsProdutoMes.size());

                            BigDecimal valor = BigDecimal.ZERO;
                            BigDecimal quantidade = BigDecimal.ZERO;
                            if (!mfsProdutoMes.isEmpty()) {
                                valor = mfsProdutoMes.stream().map(MovimentoFisico::getSubtotal).reduce(BigDecimal::add).get();
                                quantidade = mfsProdutoMes.stream().map(MovimentoFisico::getSaida).reduce(BigDecimal::add).get();
                            }
                            valores.add(Decimal.toString(valor));
                            quantidades.add(Decimal.toString(quantidade));

                            totalValor = totalValor.add(valor);
                            totalQuantidade = totalQuantidade.add(quantidade);
                        }

                        VendaProdutoPorCidadeReportBean i = new VendaProdutoPorCidadeReportBean();

                        i.setCidade(cidade.equals("") ? "--NÃO INFORMADO--" : cidade);
                        i.setProduto(produto);

                        i.setMes01Valor(valores.size() >= 1 ? valores.get(0) : "");
                        i.setMes02Valor(valores.size() >= 2 ? valores.get(1) : "");
                        i.setMes03Valor(valores.size() >= 3 ? valores.get(2) : "");
                        i.setMes04Valor(valores.size() >= 4 ? valores.get(3) : "");
                        i.setMes05Valor(valores.size() >= 5 ? valores.get(4) : "");
                        i.setMes06Valor(valores.size() >= 6 ? valores.get(5) : "");
                        i.setMes07Valor(valores.size() >= 7 ? valores.get(6) : "");
                        i.setMes08Valor(valores.size() >= 8 ? valores.get(7) : "");
                        i.setMes09Valor(valores.size() >= 9 ? valores.get(8) : "");
                        i.setMes10Valor(valores.size() >= 10 ? valores.get(9) : "");
                        i.setMes11Valor(valores.size() >= 11 ? valores.get(10) : "");
                        i.setMes12Valor(valores.size() >= 12 ? valores.get(11) : "");

                        i.setTotalValor(Decimal.toString(totalValor));

                        i.setMes01Quantidade(quantidades.size() >= 1 ? quantidades.get(0) : "");
                        i.setMes02Quantidade(quantidades.size() >= 2 ? quantidades.get(1) : "");
                        i.setMes03Quantidade(quantidades.size() >= 3 ? quantidades.get(2) : "");
                        i.setMes04Quantidade(quantidades.size() >= 4 ? quantidades.get(3) : "");
                        i.setMes05Quantidade(quantidades.size() >= 5 ? quantidades.get(4) : "");
                        i.setMes06Quantidade(quantidades.size() >= 6 ? quantidades.get(5) : "");
                        i.setMes07Quantidade(quantidades.size() >= 7 ? quantidades.get(6) : "");
                        i.setMes08Quantidade(quantidades.size() >= 8 ? quantidades.get(7) : "");
                        i.setMes09Quantidade(quantidades.size() >= 9 ? quantidades.get(8) : "");
                        i.setMes10Quantidade(quantidades.size() >= 10 ? quantidades.get(9) : "");
                        i.setMes11Quantidade(quantidades.size() >= 11 ? quantidades.get(10) : "");
                        i.setMes12Quantidade(quantidades.size() >= 12 ? quantidades.get(11) : "");

                        i.setTotalQuantidade(Decimal.toString(totalQuantidade));

                        itens.add(i);
                    }

                }

                System.out.println("elapsed: " + (System.currentTimeMillis() - start));

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
