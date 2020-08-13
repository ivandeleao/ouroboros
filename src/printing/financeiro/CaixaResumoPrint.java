package printing.financeiro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.dao.fiscal.MeioDePagamentoDAO;
import model.mysql.dao.principal.VendaDAO;
import model.mysql.dao.principal.financeiro.CaixaItemDAO;
import model.nosql.relatorio.documento.MeioDePagamentoReportBean;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.APP_PATH;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;

public class CaixaResumoPrint {

    public static void gerarA4(LocalDate dataInicial, LocalDate dataFinal) {
        try {
            MeioDePagamentoDAO mpDAO = new MeioDePagamentoDAO();
            CaixaItemDAO caixaItemDAO = new CaixaItemDAO();

            List<Venda> documentos = new VendaDAO().findByIntervalo(TipoOperacao.SAIDA, dataInicial.atTime(LocalTime.MIN), dataFinal.atTime(LocalTime.MAX));

            if (documentos.isEmpty()) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem registros no período selecionado", "Atenção", JOptionPane.INFORMATION_MESSAGE);

            } else {

                String relatorio = (APP_PATH + "\\reports\\FaturamentoRecebimentoPorPeriodo.jasper");

                HashMap map = new HashMap();

                map.put("sistemaAssinatura", Ouroboros.SISTEMA_ASSINATURA);
                map.put("dataInicial", DateTime.toString(dataInicial));
                map.put("dataFinal", DateTime.toString(dataFinal));

                List<MeioDePagamentoReportBean> itensRecebimentos = new ArrayList<>();

                List<CaixaItem> cxItens = caixaItemDAO.findByRecebimento(dataInicial, dataFinal, null);

                //Recebimentos de documentos anteriores
                List<CaixaItem> cis = cxItens.stream()
                        .filter(ci -> ci.getParcela() != null)
                        .filter(ci -> ci.getParcela().getVenda().getTipoOperacao().equals(TipoOperacao.SAIDA))
                        .filter(ci -> !ci.getParcela().isAVista())
                        .collect(Collectors.toList());

                System.out.println("recebimentos----------------------------");
                for (MeioDePagamento mp : mpDAO.findAll()) {
                    BigDecimal recebidoMp = BigDecimal.ZERO;
                    //recebimento cheque
                    List<CaixaItem> cisMp = cis.stream().filter(ci -> ci.getMeioDePagamento().equals(mp)).collect(Collectors.toList());

                    if (!cisMp.isEmpty()) {
                        //System.out.println("tem recebimento de cheques");
                        recebidoMp = cisMp.stream().map(CaixaItem::getSaldoLinear).reduce(BigDecimal::add).get();
                    }

                    if (recebidoMp.compareTo(BigDecimal.ZERO) > 0) {
                        MeioDePagamentoReportBean mpBean = new MeioDePagamentoReportBean();
                        mpBean.setMeioDePagamento(mp.getNome());
                        mpBean.setValor(recebidoMp);

                        itensRecebimentos.add(mpBean);

                        System.out.println(mp.getNome() + ": " + recebidoMp);
                    }

                }
                System.out.println("----------------------------------------");

                
                
                List<MeioDePagamentoReportBean> itensAPrazo = new ArrayList<>();
                System.out.println("À prazo---------------------------------");
                List<Parcela> parcelasAPrazo = new ArrayList<>();
                for (Venda doc : documentos) {
                    parcelasAPrazo.addAll(doc.getParcelasAPrazo());
                }
                if (!parcelasAPrazo.isEmpty()) {
                    for (MeioDePagamento mp : mpDAO.findAll()) {
                        BigDecimal aPrazoMp = BigDecimal.ZERO;
                        List<Parcela> parcelasMp = parcelasAPrazo.stream()
                                .filter(p -> p.getMeioDePagamento().equals(mp)).collect(Collectors.toList());
                        if (!parcelasMp.isEmpty()) {
                            aPrazoMp = parcelasMp.stream().map(Parcela::getValor).reduce(BigDecimal::add).get();
                        }

                        if (aPrazoMp.compareTo(BigDecimal.ZERO) > 0) {
                            MeioDePagamentoReportBean mpBean = new MeioDePagamentoReportBean();
                            mpBean.setMeioDePagamento(mp.getNome());
                            mpBean.setValor(aPrazoMp);

                            itensAPrazo.add(mpBean);

                            System.out.println(mp.getNome() + ": " + aPrazoMp);
                        }
                    }
                }
                System.out.println("----------------------------------------");

                System.out.println("----------------------------------------");
                BigDecimal totalPrazo = BigDecimal.ZERO;
                if (!documentos.isEmpty()) {
                    totalPrazo = documentos.stream().map(Venda::getTotalAPrazo).reduce(BigDecimal::add).get();
                }
                System.out.println("Total À Prazo: " + totalPrazo);
                System.out.println("----------------------------------------");

                
                
                List<MeioDePagamentoReportBean> itensAVista = new ArrayList<>();
                System.out.println("À vista---------------------------------");
                List<Parcela> parcelasAVista = new ArrayList<>();
                for (Venda doc : documentos) {
                    parcelasAVista.addAll(doc.getParcelasAVista());
                }

                if (!parcelasAVista.isEmpty()) {
                    for (MeioDePagamento mp : mpDAO.findAll()) {
                        BigDecimal aVistaMp = BigDecimal.ZERO;
                        List<Parcela> parcelasMp = parcelasAVista.stream()
                                .filter(p -> p.getMeioDePagamento().equals(mp)).collect(Collectors.toList());
                        if (!parcelasMp.isEmpty()) {
                            aVistaMp = parcelasMp.stream().map(Parcela::getValorQuitado).reduce(BigDecimal::add).get();
                        }

                        if (aVistaMp.compareTo(BigDecimal.ZERO) > 0) {
                            MeioDePagamentoReportBean mpBean = new MeioDePagamentoReportBean();
                            mpBean.setMeioDePagamento(mp.getNome());
                            mpBean.setValor(aVistaMp);

                            itensAVista.add(mpBean);

                            System.out.println(mp.getNome() + ": " + aVistaMp);
                        }

                        System.out.println(mp.getNome() + ": " + aVistaMp);
                    }
                }
                System.out.println("----------------------------------------");


                JRBeanCollectionDataSource jrRecebimentos = new JRBeanCollectionDataSource(itensRecebimentos);
                map.put("recebimentos", jrRecebimentos);
                

                JRBeanCollectionDataSource jrAVista = new JRBeanCollectionDataSource(itensAVista);
                map.put("aVista", jrAVista);
                
                
                JRBeanCollectionDataSource jrAPrazo = new JRBeanCollectionDataSource(itensAPrazo);
                map.put("aPrazo", jrAPrazo);
                
                
                
                /*new JREmptyDataSource(1) - Passing the value 1 to the constructor will create a single virtual record in the datasource, so the detail band will be printed only once.
                Ajustei o subreport também -> When no data type: All sections no detail

                */
                JasperPrint jp = JasperFillManager.fillReport(relatorio, map, new JREmptyDataSource(1)); //,jr

                JasperViewer jv = new JasperViewer(jp, false);

                jv.setVisible(true);

            }

        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório " + e);
        }
    }

}
