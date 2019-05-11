/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoEntrada;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import model.mysql.dao.principal.MovimentoFisicoDAO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import model.mysql.bean.principal.financeiro.Caixa;
import model.mysql.bean.principal.MovimentoFisicoTipo;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.fiscal.UnidadeComercial;
import model.mysql.bean.principal.ImpressoraFormato;
import model.mysql.bean.principal.pessoa.PessoaTipo;
import model.mysql.bean.principal.Recurso;
import model.mysql.bean.principal.documento.VendaTipo;
import model.mysql.dao.principal.CaixaDAO;
import model.mysql.dao.principal.VendaDAO;
import model.mysql.dao.principal.ProdutoDAO;
import model.jtable.documento.VendaJTableModel;
import model.mysql.bean.principal.documento.TipoOperacao;
import static ouroboros.Constants.*;
import static ouroboros.Ouroboros.IMPRESSORA_A4;
import static ouroboros.Ouroboros.VENDA_INSERCAO_DIRETA;
import printing.CriarPDF;
import util.DateTime;
import util.Decimal;
import util.JSwing;
import view.Toast;
import view.produto.ProdutoPesquisaView;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.TO_PRINTER_PATH;
import static ouroboros.Ouroboros.em;
import printing.CriarPdfA4;
import printing.PrintPDFBox;
import view.produto.item.ConfirmarEntregaDevolucaoView;
import static ouroboros.Ouroboros.IMPRESSORA_FORMATO_PADRAO;
import static ouroboros.Ouroboros.USUARIO;
import printing.RelatorioPdf;

/**
 *
 * @author ivand
 */
import view.documentoSaida.CancelarDocumentoView;
import view.documentoSaida.EntregaDevolucaoView;
import view.documentoSaida.ParcelamentoView;
import view.documentoSaida.RecebimentoListaView;
import view.documentoSaida.RecebimentoView;
import view.funcionario.FuncionarioPesquisaView;
import view.pessoa.PessoaPesquisaView;
import view.produto.ProdutoEntradaPesquisaView;

public class DocumentoEntradaView extends javax.swing.JInternalFrame {

    private int id;
    private Integer comanda;
    private static List<DocumentoEntradaView> vendaViews = new ArrayList<>(); //instâncias

    //private static VendaView vendaView;
    private Venda documento;
    private VendaDAO vendaDAO = new VendaDAO();
    private MovimentoFisicoDAO movimentoFisicoDAO = new MovimentoFisicoDAO();
    private ProdutoDAO produtoDAO = new ProdutoDAO();

    //private List<MovimentoFisico> vendaItens = new ArrayList<>();
    private List<Parcela> parcelas = new ArrayList<>();

    private final VendaJTableModel vendaJTableModel = new VendaJTableModel();

    private Produto produto = null;

    /**
     * Creates new form Venda
     */
    protected DocumentoEntradaView() {
        initComponents();
        JSwing.startComponentsBehavior(this);
    }

    public DocumentoEntradaView(Venda venda, boolean orcamento) {
        initComponents();
        JSwing.startComponentsBehavior(this);

        if (venda.getId() != null) {
            em.refresh(venda); //para uso em várias estações
        }

        venda.setOrcamento(orcamento);

        configurarPorTipo();

        exibirTipo();

        formatarTabela();

        definirAtalhos();
    }

    public DocumentoEntradaView(Venda venda, Integer comanda) {
        if (USUARIO.autorizarAcesso(Recurso.COMPRA)) {

            initComponents();
            JSwing.startComponentsBehavior(this);

            if (venda.getId() != null) {
                em.refresh(venda); //para uso em várias estações
            }
            this.documento = venda;
            this.comanda = venda.getComanda();

            if (venda.getId() != null && venda.getId() != 0) {

                if (venda.getComanda() != null) {
                    this.comanda = venda.getComanda();
                }

                txtDocumentoId.setText(venda.getId().toString());
                txtAbertura.setText(DateTime.toString(venda.getCriacao()));

                txtObservacao.setText(venda.getObservacao());

                parcelas = venda.getParcelas();

                exibirTotais();

                carregarTabela();

            }

            configurarPorTipo();

            exibirTipo();

            formatarTabela();

            exibirPessoa();

            definirAtalhos();
        }

    }

    private void configurarPorTipo() {
        txtTipo.setText(documento.getVendaTipo().getNome());

        txtInativo.setVisible(false);
        btnPagar.setEnabled(false);
        btnAceitarOrçamento.setEnabled(false);
        pnlEntregaDevolucao.setVisible(false);

        if (documento.getCancelamento() != null) {
            txtInativo.setText("CANCELADO");
            txtInativo.setVisible(true);
            btnAceitarOrçamento.setEnabled(true);

        } else if (documento.isOrcamento()) {
            txtInativo.setText("ORÇAMENTO");
            txtInativo.setVisible(true);
            btnAceitarOrçamento.setEnabled(true);

        } else {
            btnPagar.setEnabled(true);
            //pnlEntregaDevolucao.setVisible(true);
        }
    }

    private void formatarTabela() {
        tableItens.setModel(vendaJTableModel);
        
        tableItens.getColumnModel().getColumn(0).setPreferredWidth(1);
        
        tableItens.getColumn("#").setPreferredWidth(40);
        tableItens.getColumn("#").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tableItens.getColumn("Código").setPreferredWidth(80);
        tableItens.getColumn("Código").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tableItens.getColumn("Nome").setPreferredWidth(300);
        
        tableItens.getColumn("Quantidade").setPreferredWidth(100);
        tableItens.getColumn("Quantidade").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tableItens.getColumn("UM").setPreferredWidth(60);
        
        tableItens.getColumn("Valor").setPreferredWidth(100);
        tableItens.getColumn("Valor").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tableItens.getColumn("-%").setPreferredWidth(100);
        tableItens.getColumn("-%").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tableItens.getColumn("Subtotal").setPreferredWidth(100);
        tableItens.getColumn("Subtotal").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tableItens.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                exibirItemAtual();
            }

        });

        if (vendaJTableModel.getRowCount() > 0) {
            tableItens.setRowSelectionInterval(0, 0);
        }
    }

    private void definirAtalhos() {
        InputMap im = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exibirComandas");
        am.put("exibirComandas", new FormKeyStroke("ESC"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "txtCodigoRequestFocus");
        am.put("txtCodigoRequestFocus", new FormKeyStroke("F2"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "txtQuantidadeRequestFocus");
        am.put("txtQuantidadeRequestFocus", new FormKeyStroke("F3"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "pesquisarFuncionario");
        am.put("pesquisarFuncionario", new FormKeyStroke("F4"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "pesquisarCliente");
        am.put("pesquisarCliente", new FormKeyStroke("F5"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), "receber");
        am.put("receber", new FormKeyStroke("F7"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0), "parcelar");
        am.put("parcelar", new FormKeyStroke("F8"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, KeyEvent.SHIFT_DOWN_MASK), "exibirRecebimentos");
        am.put("exibirRecebimentos", new FormKeyStroke("ShiftF8"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), "pesquisarProdutos");
        am.put("pesquisarProdutos", new FormKeyStroke("F9"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), "imprimir");
        am.put("imprimir", new FormKeyStroke("F10"));

        

    }

    protected class FormKeyStroke extends AbstractAction {

        private final String key;

        public FormKeyStroke(String key) {
            this.key = key;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (key) {
                case "ESC":
                    fechar();
                    break;
                case "F2":
                    txtCodigo.requestFocus();
                    break;
                case "F3":
                    txtQuantidade.requestFocus();
                    break;
                case "F4":
                    
                    break;
                case "F5":
                    pesquisarFornecedor();
                    break;
                case "F6":

                    break;
                case "F7":
                    pagar();
                    break;
                case "F8":
                    parcelar();
                    break;
                case "ShiftF8":
                    exibirPagamentos();
                    break;
                case "F9":
                    pesquisarProduto(null);
                    break;
                case "F10":
                    imprimir();
                    break;
                case "ShiftF10":
                    
                    break;
                case "CtrlF10":
                    
                    break;
                case "F11":
                    
                    break;
                case "F12":
                    
                    break;
            }
        }
    }

    public static DocumentoEntradaView getInstance(Venda documentoEntrada) {
        if( !documentoEntrada.getTipoOperacao().equals( TipoOperacao.ENTRADA )) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não é um documento de compra!", "Erro", JOptionPane.ERROR_MESSAGE);
            return null;
            
        } else {
            return getInstance(documentoEntrada, null, false);
            
        }
    }

    public static DocumentoEntradaView getInstance(Venda venda, Integer comanda, boolean orcamento) {
        for (DocumentoEntradaView vendaView : vendaViews) {
            //if (vendaView.id == id && vendaView.comanda == comanda) {
            if (vendaView.documento.getId() != null && vendaView.documento.getId().equals(venda.getId())) {
                return vendaView;
            }
        }
        if (orcamento) {
            vendaViews.add(new DocumentoEntradaView(venda, true));
        } else {
            //vendaViews.add(new VendaView(venda, comanda));
            vendaViews.add(new DocumentoEntradaView(venda, comanda));
        }

        System.out.println("Instances of VendaView: " + vendaViews.size());

        return vendaViews.get(vendaViews.size() - 1);
    }

    private void exibirTipo() {

    }

    private void fechar() {
        MAIN_VIEW.removeTab(this.getName());
        MAIN_VIEW.removeView(this.getName());
    }

    private void salvar() {
        documento.setAcrescimoPercentual(Decimal.fromString(txtAcrescimoPercentual.getText()));
        documento.setAcrescimoMonetario(Decimal.fromString(txtAcrescimo.getText()));
        documento.setDescontoPercentual(Decimal.fromString(txtDescontoPercentual.getText()));
        documento.setDescontoMonetario(Decimal.fromString(txtDesconto.getText()));
        documento.setObservacao(txtObservacao.getText());

        documento = vendaDAO.save(documento);

        txtDocumentoId.setText(documento.getId().toString());
        txtAbertura.setText(DateTime.toString(documento.getCriacao()));
        
        exibirFuncionario();
        exibirPessoa();
        exibirTotais();
    }

    private void pesquisarProduto(String buscar) {
        ProdutoEntradaPesquisaView produtoEntradaPesquisaView = new ProdutoEntradaPesquisaView(buscar);

        produto = produtoEntradaPesquisaView.getProduto();
        if (produto != null) {
            if (VENDA_INSERCAO_DIRETA) {
                preCarregarItem();
                inserirItem(Decimal.fromString(txtQuantidade.getText()));
            } else {
                preCarregarItem();
            }
        }
    }

    private Produto validarInsercaoItem() {

        txtCodigo.setText(txtCodigo.getText().trim());
        String codigo = txtCodigo.getText();
        BigDecimal quantidade = Decimal.fromString(txtQuantidade.getText());

        if (produto != null && produto.getId() != null) {
            return produto;
        } else if (codigo.equals("")) {
            txtCodigo.requestFocus();
        } else if (quantidade.compareTo(BigDecimal.ZERO) == 0) {
            txtQuantidade.requestFocus();
        } else {
            List<Produto> produtos = produtoDAO.findByCodigo(codigo);

            if (produtos.isEmpty()) {
                if (!codigo.matches("[0-9]*")) {
                    JOptionPane.showMessageDialog(rootPane, "Código não encontrado", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                    txtCodigo.setText("");
                    txtCodigo.requestFocus();
                } else {
                    produto = null;
                    if (codigo.length() < 11) {
                        produto = produtoDAO.findById(Integer.valueOf(codigo));
                    }

                    if (produto == null) {
                        //item de balança
                        if (codigo.length() == 13) {
                            String codigoBalanca = codigo.substring(1, 5);
                            String strValorProporcional = codigo.substring(5, codigo.length() - 1);
                            BigDecimal valorProporcional = Decimal.fromString(strValorProporcional).divide(new BigDecimal(100));

                            System.out.println("codigoBalanca.. " + codigoBalanca);
                            System.out.println("valorProporcional.. " + valorProporcional);

                            produto = produtoDAO.findById(Integer.valueOf(codigoBalanca));

                            if (produto != null) {
                                BigDecimal peso = valorProporcional.divide(produto.getValorVenda(), 3, RoundingMode.HALF_UP);
                                System.out.println("peso.. " + peso);
                                txtQuantidade.setText(Decimal.toString(peso, 3));

                                return produto;
                            } else {
                                JOptionPane.showMessageDialog(MAIN_VIEW, "Código não encontrado");
                                txtCodigo.setText("");
                                txtCodigo.requestFocus();
                            }
                        } else {
                            JOptionPane.showMessageDialog(MAIN_VIEW, "Código não encontrado");
                            txtCodigo.setText("");
                            txtCodigo.requestFocus();
                        }
                    }
                }

            } else if (produtos.size() > 1) {
                new Toast("Código duplicado encontrado. Escolha o produto na lista...");
                pesquisarProduto(codigo);
            } else {
                produto = produtos.get(0);
            }
        }

        return produto;
    }

    private void preCarregarItem() {
        txtCodigo.setText(produto.getCodigo());
        txtNome.setText(produto.getNome());
        txtValor.setText(Decimal.toString(produto.getValorCompra()));
        txtQuantidade.requestFocus();
        /*
        txtItemNumero.setText("");
        txtItemCodigo.setText("");
        txtItemQuantidade.setText("");
        txtItemUnidadeComercialVenda.setText("");
        txtItemValor.setText("");
        txtItemSubtotal.setText("");
         */
    }

    private void inserirItem(BigDecimal quantidade) {
        BigDecimal valorCompra = Decimal.fromString(txtValor.getText());
        /*if (valorCompra.compareTo(BigDecimal.ZERO) == 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Produto com valor igual a zero. Não é possível inserir.", "Erro", JOptionPane.ERROR_MESSAGE);
            txtCodigo.setText("");
            txtCodigo.requestFocus();
        } else {*/
        //Se não houver venda, criar nova
        if (documento.getId() == null) {
            salvar();
        }
        //inserir item
        //Integer numero = vendaItens.size() + 1;
        String codigo = produto.getCodigo();
        BigDecimal descontoPercentual = BigDecimal.ZERO;
        UnidadeComercial unidadeComercialVenda = produto.getUnidadeComercialVenda();

        MovimentoFisico movimentoFisico = new MovimentoFisico(produto, 
                codigo, 
                quantidade, 
                BigDecimal.ZERO, 
                valorCompra, 
                descontoPercentual,
                unidadeComercialVenda, 
                MovimentoFisicoTipo.COMPRA, 
                null);
        
        movimentoFisico.setDataEntrada(LocalDateTime.now());

        movimentoFisico = movimentoFisicoDAO.save(movimentoFisico);

        documento.addMovimentoFisico(movimentoFisico);

        documento = vendaDAO.save(documento);

        carregarTabela();

        int index = tableItens.getRowCount() - 1;
        tableItens.setRowSelectionInterval(index, index);

        //rolar para o item (forçar visibilidade)
        tableItens.scrollRectToVisible(tableItens.getCellRect(index, 0, true));

        exibirItemAtual();

        //resetar campos
        txtCodigo.setText("");
        txtNome.setText("");
        txtQuantidade.setText("1,000");
        txtValor.setText("0");
        txtCodigo.requestFocus();

        produto = null;

        //}
    }

    private void excluirItem() {
        int index = tableItens.getSelectedRow();
        if (index > -1) {
            MovimentoFisico itemExcluir = vendaJTableModel.getRow(index);

            //verificar valores antes de excluir
            if (itemExcluir.getSubtotal().compareTo(documento.getTotalEmAberto()) > 0) {
                JOptionPane.showMessageDialog(rootPane, "Subtotal do item a ser excluído é maior que o total em aberto");
            } else {

                //2019-01-24 venda.setMovimentosFisicos(vendaItens);
                //Marcar item como excluído
                itemExcluir = movimentoFisicoDAO.remove(itemExcluir);

                documento.addMovimentoFisico(itemExcluir);

                documento = vendaDAO.save(documento);

                //em.refresh(venda);
                //2019-01-24 vendaItens = venda.getMovimentosFisicosSaida();
                carregarTabela();

                if (index >= tableItens.getRowCount()) {
                    index = tableItens.getRowCount() - 1;
                }

                //posicionar no último item da tabela
                if (tableItens.getRowCount() > 0) {
                    tableItens.setRowSelectionInterval(index, index);
                }
            }
        }
    }

    private void carregarTabela() {
        //em.getTransaction().begin();
        vendaJTableModel.clear();
        //2019-01-24 vendaJTableModel.addList(vendaItens);
        vendaJTableModel.addList(documento.getMovimentosFisicosEntrada());
        //em.getTransaction().commit();
    }

    private void exibirItemAtual() {
        try {
            if (tableItens.getSelectedRow() > -1) {
                int index = tableItens.getSelectedRow();
                /*
                txtItemNumero.setText(vendaJTableModel.getValueAt(index, 1).toString());
                txtItemCodigo.setText(vendaJTableModel.getValueAt(index, 2).toString());
                txtItemNome.setText(vendaJTableModel.getValueAt(index, 3).toString());
                txtItemQuantidade.setText(vendaJTableModel.getValueAt(index, 4).toString());
                txtItemUnidadeComercialVenda.setText(vendaJTableModel.getValueAt(index, 5).toString());
                txtItemValor.setText(vendaJTableModel.getValueAt(index, 6).toString());
                txtItemSubtotal.setText(vendaJTableModel.getValueAt(index, 7).toString());
                 */
                String item = vendaJTableModel.getValueAt(index, 1).toString() + " "
                        + vendaJTableModel.getValueAt(index, 2).toString() + " "
                        + vendaJTableModel.getValueAt(index, 3).toString() + " "
                        + vendaJTableModel.getValueAt(index, 4).toString() + " "
                        + vendaJTableModel.getValueAt(index, 5).toString() + " X "
                        + vendaJTableModel.getValueAt(index, 6).toString() + " = "
                        + vendaJTableModel.getValueAt(index, 7).toString();
                //txtItemPosicionado.setText(item);

            } else {
                /*
                txtItemNumero.setText("");
                txtItemCodigo.setText("");
                txtItemNome.setText("");
                txtItemQuantidade.setText("");
                txtItemUnidadeComercialVenda.setText("");
                txtItemValor.setText("");
                txtItemSubtotal.setText("");
                 */
                //txtItemPosicionado.setText("");
            }

            exibirTotais();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e, "Erro ao exibir item atual", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exibirTotais() {
        //em.refresh(venda);
        txtTotalItens.setText(Decimal.toString(documento.getTotalItens()));
        txtAcrescimoPercentual.setText(Decimal.toString(documento.getAcrescimoPercentual()));
        txtAcrescimo.setText(Decimal.toString(documento.getAcrescimoMonetario()));
        txtDescontoPercentual.setText(Decimal.toString(documento.getDescontoPercentual()));
        txtDesconto.setText(Decimal.toString(documento.getDescontoMonetario()));
        txtTotal.setText(Decimal.toString(documento.getTotal()));

        txtRecebido.setText(Decimal.toString(documento.getTotalRecebido().add(documento.getTotalAPrazo())));

        txtEmAberto.setText(Decimal.toString(documento.getTotalEmAberto()));

    }

    private void pagar() {
        if (documento.isOrcamento()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não é possível pagar em ORÇAMENTO.", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            Caixa lastCaixa = new CaixaDAO().getLastCaixa();
            if (lastCaixa == null || lastCaixa.getEncerramento() != null) {
                JOptionPane.showMessageDialog(rootPane, "Não há turno de caixa aberto. Não é possível realizar recebimentos.", "Atenção", JOptionPane.WARNING_MESSAGE);
            } else if (documento.getTotalEmAberto().compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(rootPane, "Não há valor em aberto.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                RecebimentoView recebimentoView = new RecebimentoView(documento);
                exibirTotais();
            }
        }
    }

    private void parcelar() {
        if (documento.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(rootPane, "Não há valor para parcelar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            ParcelamentoView parcelamentoView = new ParcelamentoView(MAIN_VIEW, documento);
            exibirTotais();
            exibirPessoa();
        }
    }

    private void imprimir() {
        salvar();

        PrintPDFBox pPDF = new PrintPDFBox();
        //VENDA, PEDIDO, COMANDA, ORDEM_DE_SERVICO, LOCAÇÃO
        if (documento.getVendaTipo().equals(VendaTipo.ORDEM_DE_SERVICO)) {
            if (IMPRESSORA_FORMATO_PADRAO.equals(ImpressoraFormato.CUPOM_80.toString())) {
                String pdfFilePath = TO_PRINTER_PATH + "ORDEM DE SERVIÇO " + documento.getId() + "_" + System.currentTimeMillis() + ".pdf";
                CriarPDF.gerarVenda(documento, pdfFilePath);
                pPDF.print(pdfFilePath, IMPRESSORA_A4);
            } else {
                pPDF.print(new CriarPdfA4().gerarOrdemDeServico(documento), IMPRESSORA_A4);
            }

        } else if (documento.getVendaTipo().equals(VendaTipo.LOCAÇÃO)) {
            RelatorioPdf.gerarLocacaoOS(documento);
            //pPDF.print(new CriarPdfA4().gerarLocacao(venda), IMPRESSORA_A4);

        } else if (documento.getVendaTipo().equals(VendaTipo.VENDA)) {
            if (IMPRESSORA_FORMATO_PADRAO.equals(ImpressoraFormato.CUPOM_80.toString())) {
                String pdfFilePath = TO_PRINTER_PATH + "VENDA " + documento.getId() + "_" + System.currentTimeMillis() + ".pdf";
                CriarPDF.gerarVenda(documento, pdfFilePath);
                pPDF.print(pdfFilePath, IMPRESSORA_A4);
            } else {
                pPDF.print(new CriarPdfA4().gerarOrdemDeServico(documento), IMPRESSORA_A4);
            }

        } else if (documento.getVendaTipo().equals(VendaTipo.PEDIDO)) {
            if (IMPRESSORA_FORMATO_PADRAO.equals(ImpressoraFormato.CUPOM_80.toString())) {
                String pdfFilePath = TO_PRINTER_PATH + "PEDIDO " + documento.getId() + "_" + System.currentTimeMillis() + ".pdf";
                CriarPDF.gerarVenda(documento, pdfFilePath);
                pPDF.print(pdfFilePath, IMPRESSORA_A4);
            } else {
                pPDF.print(new CriarPdfA4().gerarOrdemDeServico(documento), IMPRESSORA_A4);
            }

        } else {
            if (IMPRESSORA_FORMATO_PADRAO.equals(ImpressoraFormato.CUPOM_80.toString())) {
                String pdfFilePath = TO_PRINTER_PATH + "ELSE " + documento.getId() + "_" + System.currentTimeMillis() + ".pdf";
                CriarPDF.gerarVenda(documento, pdfFilePath);
                pPDF.print(pdfFilePath, IMPRESSORA_A4);
            } else {
                pPDF.print(new CriarPdfA4().gerarOrdemDeServico(documento), IMPRESSORA_A4);
            }
        }

        new Toast("Gerando documento para impressão...");

    }


    private void exibirPagamentos() {
        if (documento == null || documento.getRecebimentos().isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, "Não há pagamentos", "Atenção", JOptionPane.INFORMATION_MESSAGE);
        } else {
            RecebimentoListaView recebimentoListaView = new RecebimentoListaView(MAIN_VIEW, true, documento);
            recebimentoListaView.setLocationRelativeTo(this);
            recebimentoListaView.setVisible(true);
        }
    }

    private void entrega() {
        EntregaDevolucaoView entregaDevolucaoView = new EntregaDevolucaoView(documento);
    }

    private void confirmarEntrega() {
        if (documento.isOrcamento()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não é possível confirmar entrega para orçamentos", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            //2019-01-24 ConfirmarEntregaDevolucaoView confirmar = new ConfirmarEntregaDevolucaoView(vendaItens);
            ConfirmarEntregaDevolucaoView confirmar = new ConfirmarEntregaDevolucaoView(documento.getMovimentosFisicosEntrada());
        }
    }

    private void confirmarDevolucao() {
        if (documento.isOrcamento()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não é possível confirmar devolução para orçamentos", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            ConfirmarEntregaDevolucaoView confirmar = new ConfirmarEntregaDevolucaoView(documento.getMovimentosFisicosDevolucao());
        }
    }

    private void cancelarDocumento() {
        CancelarDocumentoView cancelarDocumento = new CancelarDocumentoView(documento);
    }

    private void aceitarOrcamento() {
        int resposta = JOptionPane.showConfirmDialog(MAIN_VIEW, "Aceitar orçamento? Este procedimento é irreversível.", "Atenção", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resposta == JOptionPane.OK_OPTION) {
            documento.setOrcamento(false);
            documento = vendaDAO.save(documento);
            configurarPorTipo();
        }
    }

    private void pesquisarFornecedor() {
        PessoaPesquisaView pesquisa = new PessoaPesquisaView(PessoaTipo.FORNECEDOR);

        if (pesquisa.getPessoa() != null) {
            documento.setPessoa(pesquisa.getPessoa());
            salvar();
        }
    }

    private void exibirPessoa() {
        if (documento.getPessoa() != null) {
            txtPessoa.setText(documento.getPessoa().getId() + " - " + documento.getPessoa().getNome());
            txtPessoaEndereco.setText(documento.getPessoa().getEnderecoCompleto());
        } else {
            txtPessoa.setText("NÃO INFORMADO");
            txtPessoaEndereco.setText("");
        }
    }

    private void removerFornecedor() {
        if (documento.getTotalRecebidoAPrazo().compareTo(BigDecimal.ZERO) > 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Já existem parcelas quitadas. Não é possível remover o fornecedor.", "Atenção", JOptionPane.WARNING_MESSAGE);
            
        } else if (!documento.getParcelasAPrazo().isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Já existe valor faturado. Não é possível remover o fornecedor.", "Atenção", JOptionPane.WARNING_MESSAGE);
            
        } else {
            documento.setPessoa(null);
            salvar();
        }
    }
    
    private void pesquisarFuncionario() {
        FuncionarioPesquisaView pesquisa = new FuncionarioPesquisaView();

        if (pesquisa.getFuncionario() != null) {
            documento.setFuncionario(pesquisa.getFuncionario());
            salvar();
        }
    }
    
    private void exibirFuncionario() {
        if (documento.getFuncionario()!= null) {
            txtFuncionario.setText(documento.getFuncionario().getId() + " - " + documento.getFuncionario().getNome());
        } else {
            txtFuncionario.setText("NÃO INFORMADO");
        }
    }

    private void removerFuncionario() {
        documento.setFuncionario(null);
        salvar();
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel6 = new javax.swing.JPanel();
        txtCodigo = new javax.swing.JTextField();
        txtQuantidade = new javax.swing.JFormattedTextField();
        btnInserir = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtValor = new javax.swing.JFormattedTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtDescontoPercentualItem = new javax.swing.JFormattedTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtTotalItens = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JFormattedTextField();
        jLabel14 = new javax.swing.JLabel();
        txtRecebido = new javax.swing.JFormattedTextField();
        btnRecebimentoLista = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtEmAberto = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtAcrescimoPercentual = new javax.swing.JFormattedTextField();
        txtDescontoPercentual = new javax.swing.JFormattedTextField();
        txtAcrescimo = new javax.swing.JFormattedTextField();
        txtDesconto = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        pnlGeral = new javax.swing.JPanel();
        btnPesquisar = new javax.swing.JButton();
        btnPagar = new javax.swing.JButton();
        btnReceber1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        btnAceitarOrçamento = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        txtTipo = new javax.swing.JTextField();
        txtDocumentoId = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtAbertura = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtInativo = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableItens = new javax.swing.JTable();
        pnlEntregaDevolucao = new javax.swing.JPanel();
        btnEntregaDevolucao = new javax.swing.JButton();
        btnConfirmarEntrega = new javax.swing.JButton();
        btnConfirmarDevolucao = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtObservacao = new javax.swing.JTextArea();
        txtPessoa = new javax.swing.JTextField();
        btnCliente = new javax.swing.JButton();
        btnRemoverPessoa = new javax.swing.JButton();
        txtPessoaEndereco = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnFuncionario = new javax.swing.JButton();
        btnRemoverFuncionario = new javax.swing.JButton();
        txtFuncionario = new javax.swing.JTextField();

        setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setResizable(true);
        setTitle("Doc.Entrada");
        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(1060, 650));
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtCodigo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtCodigo.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtCodigo.setToolTipText("F9 PARA PESQUISAR");
        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });
        txtCodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCodigoKeyReleased(evt);
            }
        });

        txtQuantidade.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQuantidade.setText("0,000");
        txtQuantidade.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtQuantidade.setName("decimal3"); // NOI18N
        txtQuantidade.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtQuantidadeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtQuantidadeFocusLost(evt);
            }
        });
        txtQuantidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQuantidadeActionPerformed(evt);
            }
        });
        txtQuantidade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtQuantidadeKeyReleased(evt);
            }
        });

        btnInserir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/basket_put.png"))); // NOI18N
        btnInserir.setText("OK");
        btnInserir.setContentAreaFilled(false);
        btnInserir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInserirActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Código");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Quantidade");

        txtValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValor.setText("0,00");
        txtValor.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtValor.setName("decimal"); // NOI18N
        txtValor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorKeyReleased(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel15.setText("Valor Unitário");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("Nome");

        txtNome.setEditable(false);
        txtNome.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel18.setText("Desconto");

        txtDescontoPercentualItem.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDescontoPercentualItem.setText("0,00");
        txtDescontoPercentualItem.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDescontoPercentualItem.setName("decimal"); // NOI18N
        txtDescontoPercentualItem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDescontoPercentualItemKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(txtQuantidade)))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(18, 18, 18)
                        .addComponent(txtValor, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel18)
                        .addGap(18, 18, 18)
                        .addComponent(txtDescontoPercentualItem, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnInserir))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(txtNome)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel9)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15)
                        .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(txtDescontoPercentualItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnInserir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setText("TOTAL ITENS");

        txtTotalItens.setEditable(false);
        txtTotalItens.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalItens.setText("0,00");
        txtTotalItens.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("+");
        jLabel12.setToolTipText("ACRÉSCIMO");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("-");
        jLabel13.setToolTipText("DESCONTO");

        txtTotal.setEditable(false);
        txtTotal.setForeground(java.awt.Color.red);
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotal.setText("0,00");
        txtTotal.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setText("TOTAL");

        txtRecebido.setEditable(false);
        txtRecebido.setForeground(java.awt.Color.red);
        txtRecebido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecebido.setText("0,00");
        txtRecebido.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N

        btnRecebimentoLista.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/application_view_list.png"))); // NOI18N
        btnRecebimentoLista.setText("F4");
        btnRecebimentoLista.setContentAreaFilled(false);
        btnRecebimentoLista.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRecebimentoLista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRecebimentoListaActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setText("FATURADO");
        jLabel10.setToolTipText("Valores recebidos e parcelados");

        txtEmAberto.setEditable(false);
        txtEmAberto.setForeground(java.awt.Color.red);
        txtEmAberto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtEmAberto.setText("0,00");
        txtEmAberto.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setText("EM ABERTO");

        jLabel17.setText("%");

        txtAcrescimoPercentual.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAcrescimoPercentual.setText("0,00");
        txtAcrescimoPercentual.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtAcrescimoPercentual.setName("decimal"); // NOI18N
        txtAcrescimoPercentual.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAcrescimoPercentualKeyReleased(evt);
            }
        });

        txtDescontoPercentual.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDescontoPercentual.setText("0,00");
        txtDescontoPercentual.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtDescontoPercentual.setName("decimal"); // NOI18N
        txtDescontoPercentual.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDescontoPercentualKeyReleased(evt);
            }
        });

        txtAcrescimo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAcrescimo.setText("0,00");
        txtAcrescimo.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtAcrescimo.setName("decimal"); // NOI18N
        txtAcrescimo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAcrescimoKeyReleased(evt);
            }
        });

        txtDesconto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDesconto.setText("0,00");
        txtDesconto.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtDesconto.setName("decimal"); // NOI18N
        txtDesconto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDescontoKeyReleased(evt);
            }
        });

        jLabel16.setText("$");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTotalItens)
                    .addComponent(txtEmAberto)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel17)
                        .addGap(119, 119, 119)
                        .addComponent(jLabel16))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtAcrescimoPercentual, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                            .addComponent(txtDescontoPercentual))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDesconto)
                            .addComponent(txtAcrescimo)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnRecebimentoLista)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel11)
                            .addComponent(jLabel10)
                            .addComponent(jLabel14)
                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 42, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTotalItens, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17))
                .addGap(8, 8, 8)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtAcrescimo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAcrescimoPercentual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDesconto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescontoPercentual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addGap(18, 18, 18)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRecebimentoLista, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmAberto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlGeral.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnPesquisar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/zoom.png"))); // NOI18N
        btnPesquisar.setText("F9 PESQUISAR PRODUTO");
        btnPesquisar.setContentAreaFilled(false);
        btnPesquisar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPesquisar.setIconTextGap(10);
        btnPesquisar.setPreferredSize(new java.awt.Dimension(180, 49));
        btnPesquisar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarActionPerformed(evt);
            }
        });

        btnPagar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/creditcards.png"))); // NOI18N
        btnPagar.setText("F7 PAGAR");
        btnPagar.setContentAreaFilled(false);
        btnPagar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPagar.setIconTextGap(10);
        btnPagar.setPreferredSize(new java.awt.Dimension(180, 49));
        btnPagar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPagarActionPerformed(evt);
            }
        });

        btnReceber1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/date.png"))); // NOI18N
        btnReceber1.setText("F8 FATURAMENTO");
        btnReceber1.setContentAreaFilled(false);
        btnReceber1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnReceber1.setIconTextGap(10);
        btnReceber1.setPreferredSize(new java.awt.Dimension(180, 49));
        btnReceber1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnReceber1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReceber1ActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/printer.png"))); // NOI18N
        jButton3.setText("F10 IMPRIIMIR");
        jButton3.setContentAreaFilled(false);
        jButton3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton3.setIconTextGap(10);
        jButton3.setPreferredSize(new java.awt.Dimension(180, 49));
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/flag_blue.png"))); // NOI18N
        jButton4.setText("ESC FECHAR JANELA");
        jButton4.setContentAreaFilled(false);
        jButton4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton4.setIconTextGap(10);
        jButton4.setPreferredSize(new java.awt.Dimension(180, 49));
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/cancel.png"))); // NOI18N
        btnCancelar.setText("CANCELAR DOCUMENTO");
        btnCancelar.setContentAreaFilled(false);
        btnCancelar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCancelar.setIconTextGap(10);
        btnCancelar.setPreferredSize(new java.awt.Dimension(180, 49));
        btnCancelar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnAceitarOrçamento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/tick.png"))); // NOI18N
        btnAceitarOrçamento.setText("ACEITAR ORÇAMENTO");
        btnAceitarOrçamento.setContentAreaFilled(false);
        btnAceitarOrçamento.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAceitarOrçamento.setIconTextGap(10);
        btnAceitarOrçamento.setPreferredSize(new java.awt.Dimension(180, 49));
        btnAceitarOrçamento.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAceitarOrçamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceitarOrçamentoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlGeralLayout = new javax.swing.GroupLayout(pnlGeral);
        pnlGeral.setLayout(pnlGeralLayout);
        pnlGeralLayout.setHorizontalGroup(
            pnlGeralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGeralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnPesquisar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPagar, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                    .addComponent(btnReceber1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAceitarOrçamento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlGeralLayout.setVerticalGroup(
            pnlGeralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReceber1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAceitarOrçamento, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtTipo.setEditable(false);
        txtTipo.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtTipo.setForeground(java.awt.Color.red);
        txtTipo.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        txtDocumentoId.setEditable(false);
        txtDocumentoId.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtDocumentoId.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtDocumentoId.setText("NOVA");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Id");

        txtAbertura.setEditable(false);
        txtAbertura.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtAbertura.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setText("Abertura");

        txtInativo.setEditable(false);
        txtInativo.setBackground(java.awt.Color.orange);
        txtInativo.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtInativo.setForeground(java.awt.Color.white);
        txtInativo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtInativo.setText("ORÇAMENTO");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTipo)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(39, 39, 39)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDocumentoId)
                            .addComponent(txtAbertura)))
                    .addComponent(txtInativo, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtInativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDocumentoId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAbertura)
                    .addComponent(jLabel8))
                .addContainerGap())
        );

        tableItens.setIntercellSpacing(new java.awt.Dimension(10, 10));
        tableItens.setRowHeight(24);
        tableItens.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tableItensFocusGained(evt);
            }
        });
        tableItens.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tableItensPropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(tableItens);

        pnlEntregaDevolucao.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnEntregaDevolucao.setText("Agendamento");
        btnEntregaDevolucao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntregaDevolucaoActionPerformed(evt);
            }
        });

        btnConfirmarEntrega.setText("Confirmar Entrega");
        btnConfirmarEntrega.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarEntregaActionPerformed(evt);
            }
        });

        btnConfirmarDevolucao.setText("Confirmar Devolução");
        btnConfirmarDevolucao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarDevolucaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlEntregaDevolucaoLayout = new javax.swing.GroupLayout(pnlEntregaDevolucao);
        pnlEntregaDevolucao.setLayout(pnlEntregaDevolucaoLayout);
        pnlEntregaDevolucaoLayout.setHorizontalGroup(
            pnlEntregaDevolucaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEntregaDevolucaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlEntregaDevolucaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEntregaDevolucao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnConfirmarEntrega, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnConfirmarDevolucao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlEntregaDevolucaoLayout.setVerticalGroup(
            pnlEntregaDevolucaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEntregaDevolucaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnEntregaDevolucao)
                .addGap(18, 18, 18)
                .addComponent(btnConfirmarEntrega)
                .addGap(18, 18, 18)
                .addComponent(btnConfirmarDevolucao)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtObservacao.setColumns(20);
        txtObservacao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtObservacao.setLineWrap(true);
        txtObservacao.setRows(5);
        txtObservacao.setMargin(new java.awt.Insets(4, 4, 4, 4));
        txtObservacao.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtObservacaoFocusLost(evt);
            }
        });
        txtObservacao.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtObservacaoPropertyChange(evt);
            }
        });
        jScrollPane2.setViewportView(txtObservacao);

        txtPessoa.setEditable(false);
        txtPessoa.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPessoa.setText("NÃO INFORMADO");

        btnCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/user.png"))); // NOI18N
        btnCliente.setText("F5 FORNECEDOR:");
        btnCliente.setContentAreaFilled(false);
        btnCliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCliente.setIconTextGap(10);
        btnCliente.setPreferredSize(new java.awt.Dimension(180, 49));
        btnCliente.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClienteActionPerformed(evt);
            }
        });

        btnRemoverPessoa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/resource/img/cancel.png"))); // NOI18N
        btnRemoverPessoa.setToolTipText("Remover Fornecedor");
        btnRemoverPessoa.setContentAreaFilled(false);
        btnRemoverPessoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverPessoaActionPerformed(evt);
            }
        });

        txtPessoaEndereco.setEditable(false);
        txtPessoaEndereco.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel2.setText("Observação");

        btnFuncionario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/user_red.png"))); // NOI18N
        btnFuncionario.setText("F4 FUNCIONÁRIO:");
        btnFuncionario.setContentAreaFilled(false);
        btnFuncionario.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFuncionario.setIconTextGap(10);
        btnFuncionario.setPreferredSize(new java.awt.Dimension(180, 49));
        btnFuncionario.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFuncionario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFuncionarioActionPerformed(evt);
            }
        });

        btnRemoverFuncionario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/resource/img/cancel.png"))); // NOI18N
        btnRemoverFuncionario.setToolTipText("Remover Cliente");
        btnRemoverFuncionario.setContentAreaFilled(false);
        btnRemoverFuncionario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverFuncionarioActionPerformed(evt);
            }
        });

        txtFuncionario.setEditable(false);
        txtFuncionario.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtFuncionario.setText("NÃO INFORMADO");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(txtPessoaEndereco)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnFuncionario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPessoa)
                            .addComponent(txtFuncionario))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnRemoverFuncionario, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnRemoverPessoa, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnRemoverFuncionario)
                    .addComponent(btnFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPessoa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemoverPessoa))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPessoaEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlGeral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlEntregaDevolucao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(pnlGeral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(201, 201, 201)
                            .addComponent(pnlEntregaDevolucao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        setBounds(5, 25, 1300, 724);
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        

        txtCodigo.requestFocus();

        txtQuantidade.setText("1,000");
    }//GEN-LAST:event_formComponentShown


    private void btnInserirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInserirActionPerformed
        produto = validarInsercaoItem();
        if (produto != null) {
            inserirItem(Decimal.fromString(txtQuantidade.getText()));
        }
    }//GEN-LAST:event_btnInserirActionPerformed

    private void txtCodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoKeyReleased
        int index;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                if (!txtCodigo.getText().trim().equals("")) {
                    produto = validarInsercaoItem();
                    if (produto != null) {
                        preCarregarItem();
                        if (VENDA_INSERCAO_DIRETA) {
                            inserirItem(Decimal.fromString(txtQuantidade.getText()));
                        }
                    }
                }
                break;
            case KeyEvent.VK_DOWN:
                index = tableItens.getSelectedRow() + 1;
                if (index < tableItens.getRowCount()) {
                    tableItens.setRowSelectionInterval(index, index);
                    tableItens.scrollRectToVisible(tableItens.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_UP:
                index = tableItens.getSelectedRow() - 1;
                if (index > -1) {
                    tableItens.setRowSelectionInterval(index, index);
                    tableItens.scrollRectToVisible(tableItens.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_DELETE:
                excluirItem();
                break;
        }


    }//GEN-LAST:event_txtCodigoKeyReleased

    private void tableItensPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tableItensPropertyChange

    }//GEN-LAST:event_tableItensPropertyChange

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased

    }//GEN-LAST:event_formKeyReleased

    private void txtQuantidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQuantidadeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQuantidadeActionPerformed

    private void txtQuantidadeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtQuantidadeFocusGained
        txtQuantidade.setText("0");
    }//GEN-LAST:event_txtQuantidadeFocusGained

    private void txtQuantidadeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtQuantidadeFocusLost
        if (txtQuantidade.getText().equals("0,000")) {
            txtQuantidade.setText("1,000");
        }
    }//GEN-LAST:event_txtQuantidadeFocusLost

    private void txtQuantidadeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeKeyReleased
        System.out.println("quantidade enter...");
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (txtCodigo.getText().equals("")) {
                System.out.println("código em branco...");
                txtCodigo.requestFocus();
            } else {
                System.out.println("código ok...");
                produto = validarInsercaoItem();
                if (produto != null) {
                    preCarregarItem();
                    //if (VENDA_INSERCAO_DIRETA) {
                    //inserirItem(Decimal.fromString(txtQuantidade.getText()));
                    //}
                    txtValor.requestFocus();
                }
            }
        }
    }//GEN-LAST:event_txtQuantidadeKeyReleased

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed

        documento = null;
        //2019-01-24 vendaItens = null;
        vendaDAO = null;
        movimentoFisicoDAO = null;
        produtoDAO = null;

        vendaViews.remove(this);


    }//GEN-LAST:event_formInternalFrameClosed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing

    }//GEN-LAST:event_formInternalFrameClosing

    private void btnPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPagarActionPerformed
        pagar();
    }//GEN-LAST:event_btnPagarActionPerformed

    private void btnPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarActionPerformed
        pesquisarProduto(null);
    }//GEN-LAST:event_btnPesquisarActionPerformed

    private void tableItensFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tableItensFocusGained
        txtCodigo.requestFocus();
    }//GEN-LAST:event_tableItensFocusGained

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        imprimir();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void btnRecebimentoListaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecebimentoListaActionPerformed
        exibirPagamentos();
    }//GEN-LAST:event_btnRecebimentoListaActionPerformed

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void txtValorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorKeyReleased
        int index;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                produto = validarInsercaoItem();
                if (produto != null) {
                    inserirItem(Decimal.fromString(txtQuantidade.getText()));
                }
                break;
            case KeyEvent.VK_DOWN:
                index = tableItens.getSelectedRow() + 1;
                if (index < tableItens.getRowCount()) {
                    tableItens.setRowSelectionInterval(index, index);
                    tableItens.scrollRectToVisible(tableItens.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_UP:
                index = tableItens.getSelectedRow() - 1;
                if (index > -1) {
                    tableItens.setRowSelectionInterval(index, index);
                    tableItens.scrollRectToVisible(tableItens.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_DELETE:
                excluirItem();
                break;
        }
    }//GEN-LAST:event_txtValorKeyReleased

    private void txtAcrescimoPercentualKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAcrescimoPercentualKeyReleased
        System.out.println("txtAcrescimoPercentualKeyReleased...");
        if (Decimal.fromString(txtAcrescimoPercentual.getText()).compareTo(BigDecimal.ZERO) > 0) {
            txtAcrescimo.setText("0");
        }
        salvar();
    }//GEN-LAST:event_txtAcrescimoPercentualKeyReleased

    private void txtAcrescimoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAcrescimoKeyReleased
        System.out.println("txtAcrescimoKeyReleased...");
        if (Decimal.fromString(txtAcrescimo.getText()).compareTo(BigDecimal.ZERO) > 0) {
            txtAcrescimoPercentual.setText("0");
        }
        salvar();
    }//GEN-LAST:event_txtAcrescimoKeyReleased

    private void txtDescontoPercentualKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescontoPercentualKeyReleased
        System.out.println("txtDescontoPercentualKeyReleased...");
        if (Decimal.fromString(txtDescontoPercentual.getText()).compareTo(BigDecimal.ZERO) > 0) {
            txtDesconto.setText("0");
        }
        salvar();
    }//GEN-LAST:event_txtDescontoPercentualKeyReleased

    private void txtDescontoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescontoKeyReleased
        System.out.println("txtDescontoKeyReleased...");
        if (Decimal.fromString(txtDesconto.getText()).compareTo(BigDecimal.ZERO) > 0) {
            txtDescontoPercentual.setText("0");
        }
        salvar();
    }//GEN-LAST:event_txtDescontoKeyReleased

    private void btnReceber1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReceber1ActionPerformed
        parcelar();
    }//GEN-LAST:event_btnReceber1ActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        txtCodigo.requestFocus();
    }//GEN-LAST:event_formFocusGained

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        fechar();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void btnEntregaDevolucaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntregaDevolucaoActionPerformed
        entrega();
    }//GEN-LAST:event_btnEntregaDevolucaoActionPerformed

    private void btnConfirmarEntregaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarEntregaActionPerformed
        confirmarEntrega();
    }//GEN-LAST:event_btnConfirmarEntregaActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        cancelarDocumento();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnConfirmarDevolucaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarDevolucaoActionPerformed
        confirmarDevolucao();
    }//GEN-LAST:event_btnConfirmarDevolucaoActionPerformed

    private void btnAceitarOrçamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceitarOrçamentoActionPerformed
        aceitarOrcamento();
    }//GEN-LAST:event_btnAceitarOrçamentoActionPerformed

    private void txtObservacaoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtObservacaoFocusLost
        System.out.println("txtObservacaoFocusLost...");
        salvar();
    }//GEN-LAST:event_txtObservacaoFocusLost

    private void txtObservacaoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtObservacaoPropertyChange

    }//GEN-LAST:event_txtObservacaoPropertyChange

    private void btnClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClienteActionPerformed
        pesquisarFornecedor();
    }//GEN-LAST:event_btnClienteActionPerformed

    private void btnRemoverPessoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverPessoaActionPerformed
        removerFornecedor();
    }//GEN-LAST:event_btnRemoverPessoaActionPerformed

    private void btnFuncionarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFuncionarioActionPerformed
        pesquisarFuncionario();
    }//GEN-LAST:event_btnFuncionarioActionPerformed

    private void btnRemoverFuncionarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverFuncionarioActionPerformed
        removerFuncionario();
    }//GEN-LAST:event_btnRemoverFuncionarioActionPerformed

    private void txtDescontoPercentualItemKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescontoPercentualItemKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDescontoPercentualItemKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceitarOrçamento;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnCliente;
    private javax.swing.JButton btnConfirmarDevolucao;
    private javax.swing.JButton btnConfirmarEntrega;
    private javax.swing.JButton btnEntregaDevolucao;
    private javax.swing.JButton btnFuncionario;
    private javax.swing.JButton btnInserir;
    private javax.swing.JButton btnPagar;
    private javax.swing.JButton btnPesquisar;
    private javax.swing.JButton btnReceber1;
    private javax.swing.JButton btnRecebimentoLista;
    private javax.swing.JButton btnRemoverFuncionario;
    private javax.swing.JButton btnRemoverPessoa;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel pnlEntregaDevolucao;
    private javax.swing.JPanel pnlGeral;
    private javax.swing.JTable tableItens;
    private javax.swing.JTextField txtAbertura;
    private javax.swing.JFormattedTextField txtAcrescimo;
    private javax.swing.JFormattedTextField txtAcrescimoPercentual;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JFormattedTextField txtDesconto;
    private javax.swing.JFormattedTextField txtDescontoPercentual;
    private javax.swing.JFormattedTextField txtDescontoPercentualItem;
    private javax.swing.JTextField txtDocumentoId;
    private javax.swing.JFormattedTextField txtEmAberto;
    private javax.swing.JTextField txtFuncionario;
    private javax.swing.JTextField txtInativo;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextArea txtObservacao;
    private javax.swing.JTextField txtPessoa;
    private javax.swing.JTextField txtPessoaEndereco;
    private javax.swing.JFormattedTextField txtQuantidade;
    private javax.swing.JFormattedTextField txtRecebido;
    private javax.swing.JTextField txtTipo;
    private javax.swing.JFormattedTextField txtTotal;
    private javax.swing.JFormattedTextField txtTotalItens;
    private javax.swing.JFormattedTextField txtValor;
    // End of variables declaration//GEN-END:variables
}
