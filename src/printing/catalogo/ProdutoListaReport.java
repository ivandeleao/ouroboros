package printing.catalogo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.mysql.bean.principal.catalogo.Produto;
import model.nosql.relatorio.ProdutoReportBean;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.APP_PATH;
import util.Decimal;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author User
 */
public class ProdutoListaReport {

    public static void gerarSimples(List<Produto> produtos) {
        try {
            String relatorio = APP_PATH + "\\reports\\ListaProdutos.jasper";

            HashMap map = new HashMap();
            map.put("sistemaAssinatura", Ouroboros.SISTEMA_ASSINATURA);

            List<ProdutoReportBean> elementos = new ArrayList<>();

            for (Produto produto : produtos) {
                ProdutoReportBean elemento = new ProdutoReportBean();

                elemento.setCodigo(produto.getCodigo());
                elemento.setNome(produto.getNome());
                elemento.setValorVenda(Decimal.toString(produto.getValorVenda()));

                elementos.add(elemento);
            }
            JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(elementos);

            JasperPrint jp = JasperFillManager.fillReport(relatorio, map, jr);

            JasperViewer jv = new JasperViewer(jp, false);

            jv.setTitle("Lista de Produtos");

            jv.setVisible(true);

        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório. " + e.getMessage());
        }
    }
    
    public static void gerarComEstoque(List<Produto> produtos) {
        try {
            String relatorio = APP_PATH + "\\reports\\ListaProdutosComEstoque.jasper";

            HashMap map = new HashMap();
            map.put("sistemaAssinatura", Ouroboros.SISTEMA_ASSINATURA);
            map.put("totalCompra", Decimal.toString(produtos.stream().map(Produto::getEstoqueAtualCompra).reduce(BigDecimal::add).get()));
            map.put("totalVenda", Decimal.toString(produtos.stream().map(Produto::getEstoqueAtualVenda).reduce(BigDecimal::add).get()));

            List<ProdutoReportBean> elementos = new ArrayList<>();

            for (Produto produto : produtos) {
                ProdutoReportBean elemento = new ProdutoReportBean();

                elemento.setCodigo(produto.getCodigo());
                elemento.setNome(produto.getNome());
                elemento.setEstoque(Decimal.toString(produto.getEstoqueAtual()));
                elemento.setValorCompra(Decimal.toString(produto.getValorCompra()));
                elemento.setEstoqueCompra(Decimal.toString(produto.getEstoqueAtualCompra()));
                elemento.setValorVenda(Decimal.toString(produto.getValorVenda()));
                elemento.setEstoqueVenda(Decimal.toString(produto.getEstoqueAtualVenda()));

                elementos.add(elemento);
            }
            JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(elementos);

            JasperPrint jp = JasperFillManager.fillReport(relatorio, map, jr);

            JasperViewer jv = new JasperViewer(jp, false);

            jv.setTitle("Lista de Produtos");

            jv.setVisible(true);

        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório. " + e.getMessage());
        }
    }
}
