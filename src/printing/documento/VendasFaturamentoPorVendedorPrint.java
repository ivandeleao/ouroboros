package printing.documento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.Funcionario;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.dao.principal.FuncionarioDAO;
import model.mysql.dao.principal.financeiro.CaixaItemDAO;
import model.nosql.relatorio.documento.FaturamentoReportBean;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.APP_PATH;
import util.DateTime;
import util.Texto;

public class VendasFaturamentoPorVendedorPrint {

    public static void gerarA4(List<Venda> documentos, LocalDate dataInicial, LocalDate dataFinal) {
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        CaixaItemDAO caixaItemDAO = new CaixaItemDAO();

        try {
            String relatorio = (APP_PATH + "\\reports\\VendasFaturamentoPorPeriodoPorVendedor.jasper");

            HashMap map = new HashMap();

            map.put("sistemaAssinatura", Ouroboros.SISTEMA_ASSINATURA);
            map.put("dataInicial", DateTime.toString(dataInicial));
            map.put("dataFinal", DateTime.toString(dataFinal));

            ////(parcelas geradas dentro do período)
            //parcelas a prazo
            List<FaturamentoReportBean> fats = new ArrayList<>();

            for (Funcionario vendedor : funcionarioDAO.findAll(false)) {

                FaturamentoReportBean fat = new FaturamentoReportBean();

                fat.setVendedor(vendedor.getNome());

                BigDecimal vendaPrazo = BigDecimal.ZERO;
                BigDecimal vistaCheque = BigDecimal.ZERO;
                BigDecimal vistaDinheiro = BigDecimal.ZERO;
                BigDecimal recebidoCheque = BigDecimal.ZERO;
                BigDecimal recebidoDinheiro = BigDecimal.ZERO;
                BigDecimal recebidoBancario = BigDecimal.ZERO;
                
                
                List<Venda> docsVendedor = documentos.stream()
                        .filter(doc -> doc.getFuncionario() != null)
                        .filter(doc -> doc.getFuncionario().equals(vendedor)).collect(Collectors.toList());

                for (Venda doc : docsVendedor.stream().filter(doc -> !doc.getParcelas().isEmpty()).collect(Collectors.toList())) {
                    
                    
                    
                    //vendaPrazo = vendaPrazo.add(doc.getParcelasAPrazo().stream().map(Parcela::getValorAtual).reduce(BigDecimal::add).get());
                    
                    
                    
                    if (!doc.getParcelas().isEmpty()) {
                        //à prazo
                        List<Parcela> parcelasPrazo = doc.getParcelas().stream()
                                //.filter(p -> p.getVencimento() != null && p.getVencimento().compareTo(p.getVenda().getDataHora().toLocalDate()) > 0).collect(Collectors.toList());
                                .filter(p -> p.getVencimento() != null).collect(Collectors.toList());
                        
                        if (!parcelasPrazo.isEmpty()) {
                            vendaPrazo = vendaPrazo.add(parcelasPrazo.stream()
                                    //.map(Parcela::getValorAtual).reduce(BigDecimal::add).get());
                                    .map(Parcela::getValor).reduce(BigDecimal::add).get());
                        }
                        
                        
                        //à vista
                        List<Parcela> parcelasVista = doc.getParcelas().stream()
                                //.filter(p -> p.getVencimento() == null || p.getVencimento().equals(p.getVenda().getDataHora().toLocalDate()))
                                .filter(p -> p.getVencimento() == null)
                                .collect(Collectors.toList());
                        
                        if (!parcelasVista.isEmpty()) {
                            //Cheque à vista
                            List<Parcela> parcelasCheque = parcelasVista.stream().filter(p -> p.getMeioDePagamento().equals(MeioDePagamento.CHEQUE)).collect(Collectors.toList());
                            if (!parcelasCheque.isEmpty()) {
                                //vistaCheque = vistaCheque.add(parcelasCheque.stream().map(Parcela::getValor).reduce(BigDecimal::add).get());
                                vistaCheque = vistaCheque.add(parcelasCheque.stream().map(Parcela::getValorQuitado).reduce(BigDecimal::add).get());
                            }
                            
                            //Dinheiro à vista
                            List<Parcela> parcelasDinheiro = parcelasVista.stream().filter(p -> p.getMeioDePagamento().equals(MeioDePagamento.DINHEIRO)).collect(Collectors.toList());
                            if (!parcelasDinheiro.isEmpty()) {
                                //vistaDinheiro = vistaDinheiro.add(parcelasDinheiro.stream().map(Parcela::getValor).reduce(BigDecimal::add).get());
                                vistaDinheiro = vistaDinheiro.add(parcelasDinheiro.stream().map(Parcela::getValorQuitado).reduce(BigDecimal::add).get());
                            }
                        }
                            
                    }
                }
                
                //recebimentos
                //todos os caixaItens do período agrupar por vendedor
                /*List<Venda> docsVendedor = documentos.stream()
                        .filter(doc -> doc.getFuncionario() != null)
                        .filter(doc -> doc.getFuncionario().equals(vendedor)).collect(Collectors.toList());*/
                
                List<CaixaItem> cxItens = caixaItemDAO.findByRecebimento(dataInicial, dataFinal, null);
                
                List<CaixaItem> cisVendedor = cxItens.stream()
                        .filter(ci -> ci.getParcela() != null)
                        .filter(ci -> ci.getParcela().getVenda().getTipoOperacao().equals(TipoOperacao.SAIDA))
                        .filter(ci -> !ci.getParcela().isAVista())
                        //.filter(ci -> ci.getParcela().getVencimento() != null 
                        //        && ci.getParcela().getVencimento().compareTo(ci.getParcela().getVenda().getDataHora().toLocalDate()) > 0)
                        .filter(ci -> ci.getParcela().getVenda().getFuncionario() != null)
                        .filter(ci -> ci.getParcela().getVenda().getFuncionario().equals(vendedor))
                        .collect(Collectors.toList());
                
                //recebimento cheque
                List<CaixaItem> cisCheque = cisVendedor.stream().filter(ci -> ci.getMeioDePagamento().equals(MeioDePagamento.CHEQUE)).collect(Collectors.toList());
                
                if (!cisCheque.isEmpty()) {
                    System.out.println("Recebido Cheque----------------------------");
                    System.out.println(vendedor.getNome());
                    for(CaixaItem ci : cisCheque) {
                        System.out.println(ci.getId() + " - " + ci.getCredito());
                    }
                    System.out.println("----------------------------");
                    
                    
                    //System.out.println("tem recebimento de cheques");
                    recebidoCheque = recebidoCheque.add(cisCheque.stream().map(CaixaItem::getSaldoLinear).reduce(BigDecimal::add).get());
                }
                
                //recebimento dinheiro
                List<CaixaItem> cisDinheiro = cisVendedor.stream().filter(ci -> ci.getMeioDePagamento().equals(MeioDePagamento.DINHEIRO)).collect(Collectors.toList());
                
                if (!cisDinheiro.isEmpty()) {
                    System.out.println("Recebido Dinheiro----------------------------");
                    System.out.println(vendedor.getNome());
                    for(CaixaItem ci : cisDinheiro) {
                        System.out.println(ci.getId() + " - " + ci.getCredito());
                    }
                    System.out.println("----------------------------");
                    
                    
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
                
                
                

                fat.setVendaPrazo(vendaPrazo);
                fat.setVistaCheque(vistaCheque);
                fat.setVistaDinheiro(vistaDinheiro);
                fat.setRecebidoCheque(recebidoCheque);
                fat.setRecebidoDinheiro(recebidoDinheiro);
                fat.setRecebidoBancario(recebidoBancario);

                fats.add(fat);
            }

            
            System.out.println(Texto.padLeftAndCut("Prazo", 20) + "\t Prazo \t Che \t Din \t RChe \t RDin \t R Ban");
            for (FaturamentoReportBean fat : fats) {
                System.out.print(Texto.padLeftAndCut(fat.getVendedor(), 20));
                System.out.print("\t " + fat.getVendaPrazo());
                System.out.print("\t " + fat.getVistaCheque());
                System.out.print("\t " + fat.getVistaDinheiro());
                System.out.print("\t " + fat.getRecebidoCheque());
                System.out.print("\t " + fat.getRecebidoDinheiro());
                System.out.print("\t " + fat.getRecebidoBancario());
                
                
                //System.out.print("\t " + fat.getVendaPrazo().add(fat.getVistaCheque()).add(fat.getVistaDinheiro()));
                
                System.out.println();
            }

            

            JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(fats);

            JasperPrint jp = JasperFillManager.fillReport(relatorio, map, jr);

            JasperViewer jv = new JasperViewer(jp, false);
            jv.setVisible(true);
            
        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório " + e);
        }
    }

}
