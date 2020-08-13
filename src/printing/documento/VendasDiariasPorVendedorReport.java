package printing.documento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.Funcionario;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.dao.principal.FuncionarioDAO;
import model.mysql.dao.principal.VendaDAO;
import model.mysql.dao.principal.financeiro.CaixaItemDAO;
import model.nosql.relatorio.documento.VendasDiariasPorVendedorReportBean;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.APP_PATH;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;

public class VendasDiariasPorVendedorReport {

    public static void gerarA4(LocalDate data) {
        long start = System.currentTimeMillis();
        try {
            FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
            CaixaItemDAO caixaItemDAO = new CaixaItemDAO();

            List<Venda> documentos = new VendaDAO().findByIntervalo(TipoOperacao.SAIDA, data.atTime(LocalTime.MIN), data.atTime(LocalTime.MAX));

            if (documentos.isEmpty()) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem registros no período selecionado", "Atenção", JOptionPane.INFORMATION_MESSAGE);

            } else {

                String relatorio = (APP_PATH + "\\reports\\VendasDiariasPorVendedor.jasper");

                HashMap map = new HashMap();

                map.put("sistemaAssinatura", Ouroboros.SISTEMA_ASSINATURA);
                map.put("dataInicial", DateTime.toString(data));
                //map.put("dataFinal", AnoMes.toString(meses.get(meses.size() - 1)));

                List<VendasDiariasPorVendedorReportBean> itens = new ArrayList<>();

                List<Funcionario> funcionarios = new ArrayList<>();
                funcionarios.add(null);
                funcionarios.addAll(funcionarioDAO.findAll(false));
                
                for (Funcionario vendedor : funcionarios) {
                    BigDecimal totalQuantidade = BigDecimal.ZERO;
                    BigDecimal totalQuantidadeBonificacao = BigDecimal.ZERO;
                    BigDecimal totalPrazo = BigDecimal.ZERO;
                    BigDecimal totalCheque = BigDecimal.ZERO;
                    BigDecimal totalDinheiro = BigDecimal.ZERO;
                    BigDecimal recebidoCheque = BigDecimal.ZERO;
                    BigDecimal recebidoDinheiro = BigDecimal.ZERO;
                    BigDecimal recebidoBancario = BigDecimal.ZERO;

                    List<CaixaItem> cxItens = caixaItemDAO.findByRecebimento(data, data, null);

                    List<CaixaItem> cisVendedor = cxItens.stream()
                            .filter(ci -> ci.getParcela() != null)
                            .filter(ci -> ci.getParcela().getVenda().getTipoOperacao().equals(TipoOperacao.SAIDA))
                            .filter(ci -> !ci.getParcela().isAVista())
                            //.filter(ci -> ci.getParcela().getVenda().getFuncionario() != null)
                            //.filter(ci -> ci.getParcela().getVenda().getFuncionario().equals(vendedor))

                            .filter(
                                    ci -> (ci.getParcela().getVenda().getFuncionario() == null
                                    && vendedor == null)
                                    || (ci.getParcela().getVenda().getFuncionario() != null
                                    && ci.getParcela().getVenda().getFuncionario().equals(vendedor))
                            )
                            .collect(Collectors.toList());

                    //recebimento cheque
                    List<CaixaItem> cisCheque = cisVendedor.stream().filter(ci -> ci.getMeioDePagamento().equals(MeioDePagamento.CHEQUE)).collect(Collectors.toList());

                    if (!cisCheque.isEmpty()) {
                        //System.out.println("tem recebimento de cheques");
                        recebidoCheque = recebidoCheque.add(cisCheque.stream().map(CaixaItem::getSaldoLinear).reduce(BigDecimal::add).get());
                    }

                    //recebimento dinheiro
                    List<CaixaItem> cisDinheiro = cisVendedor.stream().filter(ci -> ci.getMeioDePagamento().equals(MeioDePagamento.DINHEIRO)).collect(Collectors.toList());

                    if (!cisDinheiro.isEmpty()) {
                        //System.out.println("tem recebimento de dinheiro");
                        recebidoDinheiro = recebidoDinheiro.add(cisDinheiro.stream().map(CaixaItem::getSaldoLinear).reduce(BigDecimal::add).get());
                    }

                    //recebimento bancário
                    List<CaixaItem> cisBancario = cisVendedor.stream()
                            .filter(ci -> ci.getMeioDePagamento().equals(MeioDePagamento.BOLETO_BANCARIO)
                            || ci.getMeioDePagamento().equals(MeioDePagamento.TRANSFERENCIA))
                            .collect(Collectors.toList());

                    if (!cisBancario.isEmpty()) {
                        //System.out.println("tem recebimento de dinheiro");
                        recebidoBancario = recebidoBancario.add(cisBancario.stream().map(CaixaItem::getSaldoLinear).reduce(BigDecimal::add).get());
                    }

                    List<Venda> docsVendedor = documentos.stream()
                            //.filter(doc -> doc.getFuncionario() != null)
                            //.filter(doc -> doc.getFuncionario().equals(vendedor)).collect(Collectors.toList());
                            .filter(
                                    doc -> (doc.getFuncionario() == null && vendedor == null)
                                    || (doc.getFuncionario() != null && doc.getFuncionario().equals(vendedor))
                            ).collect(Collectors.toList());

                    if (!docsVendedor.isEmpty()) {
                        totalPrazo = docsVendedor.stream().map(Venda::getTotalAPrazo).reduce(BigDecimal::add).get();
                    }

                    List<Parcela> parcelasAVista = new ArrayList<>();
                    for (Venda doc : docsVendedor) {
                        parcelasAVista.addAll(doc.getParcelasAVista());
                    }

                    if (!parcelasAVista.isEmpty()) {
                        List<Parcela> parcelasCheque = parcelasAVista.stream()
                                .filter(p -> p.getMeioDePagamento().equals(MeioDePagamento.CHEQUE)).collect(Collectors.toList());
                        if (!parcelasCheque.isEmpty()) {
                            totalCheque = parcelasCheque.stream().map(Parcela::getValor).reduce(BigDecimal::add).get();
                        }

                        List<Parcela> parcelasDinheiro = parcelasAVista.stream()
                                .filter(p -> p.getMeioDePagamento().equals(MeioDePagamento.DINHEIRO)).collect(Collectors.toList());
                        if (!parcelasDinheiro.isEmpty()) {
                            totalDinheiro = parcelasDinheiro.stream().map(Parcela::getValor).reduce(BigDecimal::add).get();
                        }

                    }

                    //totalCheque = docsVendedor.stream().map(Venda::getTotalRecebidoAVista).reduce(BigDecimal::add).get();
                    //totalDinheiro = docsVendedor.stream().map(Venda::getTotalAPrazo).reduce(BigDecimal::add).get();
                    for (Venda doc : docsVendedor) {
                        BigDecimal somaQuantidade = BigDecimal.ZERO;
                        BigDecimal somaQuantidadeBonificacao = BigDecimal.ZERO;
                        BigDecimal vistaCheque = BigDecimal.ZERO;
                        BigDecimal vistaDinheiro = BigDecimal.ZERO;

                        if (!doc.getParcelasAVista().isEmpty()) {
                            List<Parcela> parcelasCheque = doc.getParcelasAVista().stream()
                                    .filter(p -> p.getMeioDePagamento().equals(MeioDePagamento.CHEQUE)).collect(Collectors.toList());
                            if (!parcelasCheque.isEmpty()) {
                                vistaCheque = parcelasCheque.stream().map(Parcela::getValor).reduce(BigDecimal::add).get();
                            }

                            List<Parcela> parcelasDinheiro = doc.getParcelasAVista().stream()
                                    .filter(p -> p.getMeioDePagamento().equals(MeioDePagamento.DINHEIRO)).collect(Collectors.toList());
                            if (!parcelasDinheiro.isEmpty()) {
                                vistaDinheiro = parcelasDinheiro.stream().map(Parcela::getValor).reduce(BigDecimal::add).get();
                            }

                        }

                        for (MovimentoFisico mf : doc.getMovimentosFisicos()) {
                            VendasDiariasPorVendedorReportBean item = new VendasDiariasPorVendedorReportBean();

                            if (mf.isBonificacao()) {
                                somaQuantidadeBonificacao = somaQuantidadeBonificacao.add(mf.getSaida());
                                totalQuantidadeBonificacao = totalQuantidadeBonificacao.add(mf.getSaida());
                            } else {
                                somaQuantidade = somaQuantidade.add(mf.getSaida());
                                totalQuantidade = totalQuantidade.add(mf.getSaida());
                            }

                            item.setVendedor(doc.getFuncionario() != null ? doc.getFuncionario().getNome() : "--NÃO INFORMADO--");
                            item.setDocumentoId(doc.getId());
                            item.setClienteNome(doc.getPessoa() != null ? doc.getPessoa().getNomeConfigurado(): "--NÃO INFORMADO--");

                            String descricao = mf.isBonificacao() ? "BONIFICAÇÃO " : "";
                            item.setProduto(descricao + mf.getDescricao());

                            item.setQuantidade(mf.getSaida());
                            item.setSubtotal(mf.getSubtotal());

                            item.setSomaQuantidade(somaQuantidade);
                            item.setSomaQuantidadeBonificacao(somaQuantidadeBonificacao);

                            item.setVendaPrazo(doc.getTotalAPrazo());

                            item.setVistaCheque(vistaCheque);
                            item.setVistaDinheiro(vistaDinheiro);

                            item.setTotalQuantidade(totalQuantidade);
                            item.setTotalQuantidadeBonificacao(totalQuantidadeBonificacao);
                            item.setTotalPrazo(totalPrazo);
                            item.setTotalCheque(totalCheque);
                            item.setTotalDinheiro(totalDinheiro);

                            item.setRecebidoCheque(recebidoCheque);
                            item.setRecebidoDinheiro(recebidoDinheiro);
                            item.setRecebidoBancario(recebidoBancario);

                            itens.add(item);

                        }

                    }

                }

                for (VendasDiariasPorVendedorReportBean i : itens) {
                    System.out.println("item: " + i.getVendedor() + "\t" + i.getVendaPrazo().toString() + "\t" + i.getTotalPrazo().toString());
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
