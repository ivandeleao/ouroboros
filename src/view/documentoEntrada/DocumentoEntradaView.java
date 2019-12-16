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
import model.jtable.documento.DocumentoEntradaJTableModel;
import model.mysql.bean.principal.financeiro.Caixa;
import model.mysql.bean.principal.MovimentoFisicoTipo;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.pessoa.PessoaTipo;
import model.mysql.bean.principal.Recurso;
import model.mysql.bean.principal.documento.VendaTipo;
import model.mysql.dao.principal.CaixaDAO;
import model.mysql.dao.principal.VendaDAO;
import model.mysql.dao.principal.catalogo.ProdutoDAO;
import model.mysql.bean.principal.documento.TipoOperacao;
import static ouroboros.Constants.*;
import static ouroboros.Ouroboros.IMPRESSORA_CUPOM;
import static ouroboros.Ouroboros.VENDA_INSERCAO_DIRETA;
import printing.TermicaPrint;
import util.Decimal;
import util.JSwing;
import view.Toast;
import static ouroboros.Ouroboros.MAIN_VIEW;
import printing.PrintPDFBox;
import static ouroboros.Ouroboros.TO_PRINTER_PATH;
import static ouroboros.Ouroboros.USUARIO;
import printing.DocumentoSaidaPrint;
import printing.RelatorioPdf;
import util.Cor;

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
import view.catalogo.geral.ProdutoEntradaPesquisaView;
import view.catalogo.geral.ProdutoEtiquetaPorCompra;

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

    private final DocumentoEntradaJTableModel documentoEntradaJTableModel = new DocumentoEntradaJTableModel();

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
            ////em.refresh(venda); //para uso em várias estações
        }

        venda.setOrcamento(orcamento);

        configurarTela();
        formatarBotoes();

        formatarTabela();

        definirAtalhos();
    }

    public DocumentoEntradaView(Venda venda) {
        if (USUARIO.autorizarAcesso(Recurso.COMPRA)) {

            initComponents();
            JSwing.startComponentsBehavior(this);

            if (venda.getId() != null) {
                ////em.refresh(venda); //para uso em várias estações
            }
            this.documento = venda;
            System.out.println("getTotalAcrescimoProdutosTipo: " + documento.getTotalAcrescimoProdutosTipo());
            //documento.distribuirAcrescimoMonetarioProdutos();

            if (venda.getId() != null && venda.getId() != 0) {

                txtDocumentoId.setText(venda.getId().toString());

                txtObservacao.setText(venda.getObservacao());

                parcelas = venda.getParcelas();

                carregarAcrescimosDescontos();
                exibirTotais();

                carregarTabela();

            }

            configurarTela();
            formatarBotoes();

            formatarTabela();

            exibirFuncionario();
            exibirPessoa();

            definirAtalhos();
        }

    }

    private void configurarTela() {
        txtTipo.setText(documento.getVendaTipo().getNome());

        txtInativo.setVisible(false);
        btnPagar.setEnabled(false);
        btnAceitarOrcamento.setVisible(false);

        /*if (documento.getCancelamento() != null) {
            txtInativo.setText("CANCELADO");
            txtInativo.setVisible(true);
            btnAceitarOrcamento.setEnabled(true);

        } else*/ if (documento.isOrcamento()) {
            txtInativo.setText("ORÇAMENTO");
            txtInativo.setVisible(true);
            btnAceitarOrcamento.setVisible(true);

        } else {
            btnPagar.setEnabled(true);
            //pnlEntregaDevolucao.setVisible(true);
        }
    }

    private void formatarBotoes() {

        //btnProcesso.setVisible(false);
        System.out.println("formatar botões");
        if (documento.getId() == null) {
            System.out.println("venda nula");
            btnAcrescimoProdutosTipo.setBackground(Cor.AZUL);
            btnDescontoProdutosTipo.setBackground(Cor.AZUL);
            btnAcrescimoServicosTipo.setBackground(Cor.AZUL);
            btnDescontoServicosTipo.setBackground(Cor.AZUL);

        } else {

            if (documento.getTotalAcrescimoProdutosTipo().equals("$")) {
                btnAcrescimoProdutosTipo.setText("$");
                btnAcrescimoProdutosTipo.setBackground(Cor.LARANJA);
            } else {
                btnAcrescimoProdutosTipo.setText("%");
                btnAcrescimoProdutosTipo.setBackground(Cor.AZUL);
            }

            if (documento.getTotalAcrescimoServicosTipo().equals("$")) {
                btnAcrescimoServicosTipo.setText("$");
                btnAcrescimoServicosTipo.setBackground(Cor.LARANJA);
            } else {
                btnAcrescimoServicosTipo.setText("%");
                btnAcrescimoServicosTipo.setBackground(Cor.AZUL);
            }

            if (documento.getTotalDescontoProdutosTipo().equals("$")) {
                btnDescontoProdutosTipo.setText("$");
                btnDescontoProdutosTipo.setBackground(Cor.LARANJA);
            } else {
                btnDescontoProdutosTipo.setText("%");
                btnDescontoProdutosTipo.setBackground(Cor.AZUL);
            }

            if (documento.getTotalDescontoServicosTipo().equals("$")) {
                btnDescontoServicosTipo.setText("$");
                btnDescontoServicosTipo.setBackground(Cor.LARANJA);
            } else {
                btnDescontoServicosTipo.setText("%");
                btnDescontoServicosTipo.setBackground(Cor.AZUL);
            }

        }
    }
    

    private void formatarTabela() {
        tblItens.setModel(documentoEntradaJTableModel);
        tblItens.setRowHeight(30);

        tblItens.getColumnModel().getColumn(0).setPreferredWidth(1);

        tblItens.getColumn("#").setPreferredWidth(40);
        tblItens.getColumn("#").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblItens.getColumn("Código").setPreferredWidth(80);
        tblItens.getColumn("Código").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblItens.getColumn("Descrição").setPreferredWidth(300);

        tblItens.getColumn("Quantidade").setPreferredWidth(100);
        tblItens.getColumn("Quantidade").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblItens.getColumn("UM").setPreferredWidth(60);

        tblItens.getColumn("Tipo").setPreferredWidth(30);
        tblItens.getColumn("Tipo").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblItens.getColumn("Valor").setPreferredWidth(100);
        tblItens.getColumn("Valor").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblItens.getColumn("Acréscimo").setPreferredWidth(100);
        tblItens.getColumn("Acréscimo").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblItens.getColumn("Desconto").setPreferredWidth(100);
        tblItens.getColumn("Desconto").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblItens.getColumn("Frete").setPreferredWidth(100);
        tblItens.getColumn("Frete").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblItens.getColumn("Seguro").setPreferredWidth(100);
        tblItens.getColumn("Seguro").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblItens.getColumn("Subtotal").setPreferredWidth(100);
        tblItens.getColumn("Subtotal").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        if (documentoEntradaJTableModel.getRowCount() > 0) {
            tblItens.setRowSelectionInterval(0, 0);
        }
    }

    private void definirAtalhos() {
        InputMap im = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "txtCodigoRequestFocus");
        am.put("txtCodigoRequestFocus", new FormKeyStroke("F2"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "txtQuantidadeRequestFocus");
        am.put("txtQuantidadeRequestFocus", new FormKeyStroke("F3"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "pesquisarFuncionario");
        am.put("pesquisarFuncionario", new FormKeyStroke("F4"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "pesquisarPessoa");
        am.put("pesquisarPessoa", new FormKeyStroke("F5"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), "pagar");
        am.put("pagar", new FormKeyStroke("F7"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0), "parcelar");
        am.put("parcelar", new FormKeyStroke("F8"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, KeyEvent.SHIFT_DOWN_MASK), "exibirRecebimentos");
        am.put("exibirRecebimentos", new FormKeyStroke("ShiftF8"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), "pesquisarProduto");
        am.put("pesquisarProduto", new FormKeyStroke("F9"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), "imprimirA4");
        am.put("imprimirA4", new FormKeyStroke("F10"));

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
                    txtItemQuantidade.requestFocus();
                    break;
                case "F4":

                    break;
                case "F5":
                    pesquisarPessoa();
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
                    imprimirA4();
                    break;

            }
        }
    }

    public static DocumentoEntradaView getInstance(Venda documento) {
        if (!documento.getTipoOperacao().equals(TipoOperacao.ENTRADA)) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não é um documento de compra!", "Erro", JOptionPane.ERROR_MESSAGE);
            return null;

        } else {
            return getInstance(documento, null, false);

        }
    }

    public static DocumentoEntradaView getInstance(Venda documento, Integer comanda, boolean orcamento) {
        for (DocumentoEntradaView vendaView : vendaViews) {
            if (vendaView.documento.getId() != null && vendaView.documento.getId().equals(documento.getId())) {
                return vendaView;
            }
        }
        if (orcamento) {
            vendaViews.add(new DocumentoEntradaView(documento, true));

        } else {
            vendaViews.add(new DocumentoEntradaView(documento));

        }

        return vendaViews.get(vendaViews.size() - 1);
    }

    private void fechar() {
        MAIN_VIEW.removeTab(this.getName());
        MAIN_VIEW.removeView(this.getName());
    }

    private void salvar() {

        documento.setObservacao(txtObservacao.getText());

        documento = vendaDAO.save(documento);

        txtDocumentoId.setText(documento.getId().toString());

        exibirFuncionario();
        exibirPessoa();
        exibirTotais();
    }

    private void pesquisarProduto(String buscar) {
        ProdutoEntradaPesquisaView produtoEntradaPesquisaView = new ProdutoEntradaPesquisaView(buscar);

        produto = produtoEntradaPesquisaView.getProduto();
        if (produto != null) {
            preCarregarItem();
        }
    }

    private Produto validarInsercaoItem() {

        txtCodigo.setText(txtCodigo.getText().trim());
        String codigo = txtCodigo.getText();
        BigDecimal quantidade = Decimal.fromString(txtItemQuantidade.getText());

        if (produto != null && produto.getId() != null) {
            return produto;
        } else if (codigo.equals("")) {
            txtCodigo.requestFocus();
        } else if (quantidade.compareTo(BigDecimal.ZERO) == 0) {
            txtItemQuantidade.requestFocus();
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
                                txtItemQuantidade.setText(Decimal.toString(peso, 3));

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
        txtItemNome.setText(produto.getNome());
        txtValor.setText(Decimal.toString(produto.getValorCompra()));
        txtItemQuantidade.requestFocus();

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
        
        BigDecimal descontoPercentualItem = Decimal.fromString(txtItemDescontoPercentual.getText());
        MovimentoFisico movimentoFisico = new MovimentoFisico(produto,
                produto.getCodigo(),
                produto.getNome(),
                produto.getProdutoTipo(),
                quantidade,
                BigDecimal.ZERO,
                valorCompra,
                descontoPercentualItem,
                produto.getUnidadeComercialVenda(),
                MovimentoFisicoTipo.COMPRA,
                null);

        movimentoFisico.setDataEntrada(LocalDateTime.now());

        movimentoFisico = movimentoFisicoDAO.save(movimentoFisico);

        //2019-07-17 Causava centenas de consultas ao movimentoFisico
        //Aparentemente o estoque está refletindo normalmente mesmo sem isso
        //produto.addMovimentoFisico(movimentoFisico); //2019-06-10 - atualizar estoque
        documento.addMovimentoFisico(movimentoFisico);

        documento = vendaDAO.save(documento);

        carregarTabela();

        int index = tblItens.getRowCount() - 1;
        tblItens.setRowSelectionInterval(index, index);

        //rolar para o item (forçar visibilidade)
        tblItens.scrollRectToVisible(tblItens.getCellRect(index, 0, true));

        exibirTotais();
        carregarAcrescimosDescontos();
        formatarBotoes();

        //resetar campos
        txtCodigo.setText("");
        txtItemNome.setText("");
        txtItemQuantidade.setText("1,000");
        txtValor.setText("0");
        txtCodigo.requestFocus();

        produto = null;

    }

    private void excluirItem() {
        int index = tblItens.getSelectedRow();
        if (index > -1) {
            MovimentoFisico itemExcluir = documentoEntradaJTableModel.getRow(index);

            //verificar valores antes de excluir
            if (itemExcluir.getSubtotal().compareTo(documento.getTotalEmAberto()) > 0) {
                JOptionPane.showMessageDialog(rootPane, "Subtotal do item a ser excluído é maior que o total em aberto");
            } else {

                //2019-01-24 venda.setMovimentosFisicos(vendaItens);
                //Marcar item como excluído
                itemExcluir = movimentoFisicoDAO.remove(itemExcluir);

                documento.addMovimentoFisico(itemExcluir);

                documento = vendaDAO.save(documento);

                carregarTabela();
                exibirTotais();

                if (index >= tblItens.getRowCount()) {
                    index = tblItens.getRowCount() - 1;
                }

                //posicionar no último item da tabela
                if (tblItens.getRowCount() > 0) {
                    tblItens.setRowSelectionInterval(index, index);
                }
            }
        }
    }

    private void carregarTabela() {
        documentoEntradaJTableModel.clear();
        documentoEntradaJTableModel.addList(documento.getMovimentosFisicosEntrada());
    }

    private void atualizarTabela() {
        int row = tblItens.getSelectedRow();
        documentoEntradaJTableModel.fireTableDataChanged();
        if (row > -1) {
            tblItens.addRowSelectionInterval(row, row);
        }
    }

    private void carregarAcrescimosDescontos() {
        BigDecimal acrescimoProdutos = documento.getTotalAcrescimoProdutos();
        txtAcrescimoProdutos.setText(Decimal.toString(acrescimoProdutos));

        BigDecimal descontoProdutos = documento.getTotalDescontoProdutos();
        txtDescontoProdutos.setText(Decimal.toString(descontoProdutos));

        BigDecimal acrescimoServicos = documento.getTotalAcrescimoServicos();
        txtAcrescimoServicos.setText(Decimal.toString(acrescimoServicos));

        BigDecimal descontoServicos = documento.getTotalDescontoServicos();
        txtDescontoServicos.setText(Decimal.toString(descontoServicos));
    }

    private void exibirTotais() {
        txtTotalItensProdutos.setText(Decimal.toString(documento.getTotalItensProdutos()));
        txtTotalItensServicos.setText(Decimal.toString(documento.getTotalItensServicos()));

        txtTotalFreteProdutos.setText(Decimal.toString(documento.getTotalFreteProdutos()));
        txtTotalFreteServicos.setText(Decimal.toString(documento.getTotalFreteServicos()));

        txtTotalSeguroProdutos.setText(Decimal.toString(documento.getTotalSeguroProdutos()));
        txtTotalSeguroServicos.setText(Decimal.toString(documento.getTotalSeguroServicos()));

        txtTotalProdutos.setText(Decimal.toString(documento.getTotalProdutos()));
        txtTotalServicos.setText(Decimal.toString(documento.getTotalServicos()));
        txtTotal.setText(Decimal.toString(documento.getTotal()));

        txtRecebido.setText(Decimal.toString(documento.getTotalRecebidoAVista()));

        txtFaturado.setText(Decimal.toString(documento.getTotalAPrazo()));

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
        ParcelamentoView parcelamentoView = new ParcelamentoView(documento);
        exibirTotais();
        exibirPessoa();
    }

    private void imprimirA4() {
        salvar();

        if (documento.getVendaTipo().equals(VendaTipo.LOCAÇÃO)) {
            RelatorioPdf.gerarLocacaoOS(documento);

        } else {
            DocumentoSaidaPrint.gerarA4(documento);

        }
    }

    private void imprimirTermica() {
        salvar();

        PrintPDFBox pPDF = new PrintPDFBox();

        String pdfFilePath = TO_PRINTER_PATH + documento.getTitulo() + " " + documento.getId() + "_" + System.currentTimeMillis() + ".pdf";
        TermicaPrint.gerarVenda(documento, pdfFilePath);
        pPDF.print(pdfFilePath, IMPRESSORA_CUPOM);
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

    private void cancelarDocumento() {
        if (documento.getId() == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Documento vazio. Não é possível cancelar.", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {
            CancelarDocumentoView cancelarVenda = new CancelarDocumentoView(documento);
            configurarTela();

        }
    }

    private void aceitarOrcamento() {
        int resposta = JOptionPane.showConfirmDialog(MAIN_VIEW, "Aceitar orçamento? Este procedimento é irreversível.", "Atenção", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resposta == JOptionPane.OK_OPTION) {
            documento.setOrcamento(false);
            documento = vendaDAO.save(documento);
            configurarTela();
        }
    }

    private void pesquisarPessoa() {
        PessoaPesquisaView pesquisa = new PessoaPesquisaView(PessoaTipo.FORNECEDOR);

        if (pesquisa.getPessoa() != null) {
            documento.setPessoa(pesquisa.getPessoa());
            salvar();
        }
    }

    private void exibirPessoa() {
        if (documento.getPessoa() != null) {
            txtPessoaNome.setText(documento.getPessoa().getId() + " - " + documento.getPessoa().getNome());
            txtPessoaTelefone.setText(documento.getPessoa().getTelefone1());
            txtPessoaNome.setCaretPosition(0);
        } else {
            txtPessoaNome.setText("NÃO INFORMADO");
            txtPessoaTelefone.setText("");
        }
    }

    private void removerPessoa() {
        if (documento.getTotalRecebidoAPrazo().compareTo(BigDecimal.ZERO) > 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Já existem parcelas recebidas. Não é possível remover o fornecedor.", "Atenção", JOptionPane.WARNING_MESSAGE);

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
        if (documento.getFuncionario() != null) {
            txtFuncionario.setText(documento.getFuncionario().getId() + " - " + documento.getFuncionario().getNome());
        } else {
            txtFuncionario.setText("NÃO INFORMADO");
        }
    }

    private void removerFuncionario() {
        documento.setFuncionario(null);
        salvar();
    }

    private void alternarAcrescimoProdutosTipo() {

        if (btnAcrescimoProdutosTipo.getText().equals("%")) {
            btnAcrescimoProdutosTipo.setText("$");
            btnAcrescimoProdutosTipo.setBackground(Cor.LARANJA);

        } else {
            btnAcrescimoProdutosTipo.setText("%");
            btnAcrescimoProdutosTipo.setBackground(Cor.AZUL);

        }

        distribuirAcrescimoProdutos();
        btnAcrescimoProdutosTipo.repaint();

        salvar();
    }

    private void alternarDescontoProdutosTipo() {

        if (btnDescontoProdutosTipo.getText().equals("%")) {
            btnDescontoProdutosTipo.setText("$");
            btnDescontoProdutosTipo.setBackground(Cor.LARANJA);

        } else {
            btnDescontoProdutosTipo.setText("%");
            btnDescontoProdutosTipo.setBackground(Cor.AZUL);

        }

        distribuirDescontoProdutos();
        btnDescontoProdutosTipo.repaint();

        salvar();
    }

    private void alternarAcrescimoServicosTipo() {

        if (btnAcrescimoServicosTipo.getText().equals("%")) {
            btnAcrescimoServicosTipo.setText("$");
            btnAcrescimoServicosTipo.setBackground(Cor.LARANJA);

        } else {
            btnAcrescimoServicosTipo.setText("%");
            btnAcrescimoServicosTipo.setBackground(Cor.AZUL);

        }

        distribuirAcrescimoServicos();
        btnAcrescimoServicosTipo.repaint();

        salvar();
    }

    private void alternarDescontoServicosTipo() {

        if (btnDescontoServicosTipo.getText().equals("%")) {
            btnDescontoServicosTipo.setText("$");
            btnDescontoServicosTipo.setBackground(Cor.LARANJA);

        } else {
            btnDescontoServicosTipo.setText("%");
            btnDescontoServicosTipo.setBackground(Cor.AZUL);

        }

        distribuirDescontoServicos();
        btnDescontoServicosTipo.repaint();

        salvar();
    }

    private void distribuirAcrescimoProdutos() {
        if (btnAcrescimoProdutosTipo.getText().equals("%")) {
            documento.distribuirAcrescimoPercentualProdutos(Decimal.fromString(txtAcrescimoProdutos.getText()));
        } else {
            documento.distribuirAcrescimoMonetarioProdutos(Decimal.fromString(txtAcrescimoProdutos.getText()));
        }

        salvar();

        atualizarTabela();
    }

    private void distribuirDescontoProdutos() {
        if (btnDescontoProdutosTipo.getText().equals("%")) {
            documento.distribuirDescontoPercentualProdutos(Decimal.fromString(txtDescontoProdutos.getText()));
        } else {
            documento.distribuirDescontoMonetarioProdutos(Decimal.fromString(txtDescontoProdutos.getText()));
        }

        salvar();

        atualizarTabela();
    }

    private void distribuirAcrescimoServicos() {
        if (btnAcrescimoServicosTipo.getText().equals("%")) {
            documento.distribuirAcrescimoPercentualServicos(Decimal.fromString(txtAcrescimoServicos.getText()));
        } else {
            documento.distribuirAcrescimoMonetarioServicos(Decimal.fromString(txtAcrescimoServicos.getText()));
        }

        salvar();

        atualizarTabela();
    }

    private void distribuirDescontoServicos() {
        if (btnDescontoServicosTipo.getText().equals("%")) {
            documento.distribuirDescontoPercentualServicos(Decimal.fromString(txtDescontoServicos.getText()));
        } else {
            documento.distribuirDescontoMonetarioServicos(Decimal.fromString(txtDescontoServicos.getText()));
        }

        salvar();

        atualizarTabela();
    }

    private void imprimirEtiqueta() {

        ProdutoEtiquetaPorCompra imprimirEtiqueta = new ProdutoEtiquetaPorCompra(documento);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        txtTipo = new javax.swing.JTextField();
        txtInativo = new javax.swing.JTextField();
        btnAceitarOrcamento = new javax.swing.JButton();
        btnCancelarDocumento = new javax.swing.JButton();
        txtDocumentoId = new javax.swing.JTextField();
        btnImprimirEtiqueta = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblItens = new javax.swing.JTable();
        pnlInserirProduto = new javax.swing.JPanel();
        txtCodigo = new javax.swing.JTextField();
        txtItemQuantidade = new javax.swing.JFormattedTextField();
        btnInserir = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtValor = new javax.swing.JFormattedTextField();
        jLabel15 = new javax.swing.JLabel();
        txtItemDescontoPercentual = new javax.swing.JFormattedTextField();
        jLabel18 = new javax.swing.JLabel();
        txtItemNome = new javax.swing.JTextField();
        btnPesquisar = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txtPessoaNome = new javax.swing.JTextField();
        btnCliente = new javax.swing.JButton();
        btnRemoverCliente = new javax.swing.JButton();
        txtPessoaTelefone = new javax.swing.JTextField();
        btnFuncionario = new javax.swing.JButton();
        btnRemoverFuncionario = new javax.swing.JButton();
        txtFuncionario = new javax.swing.JTextField();
        pnlObservacao = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtObservacao = new javax.swing.JTextArea();
        jLabel37 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        txtRecebido = new javax.swing.JFormattedTextField();
        btnPagar = new javax.swing.JButton();
        btnReceber1 = new javax.swing.JButton();
        txtFaturado = new javax.swing.JFormattedTextField();
        jLabel45 = new javax.swing.JLabel();
        txtEmAberto = new javax.swing.JFormattedTextField();
        jLabel44 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnlValores = new javax.swing.JPanel();
        txtTotalItensProdutos = new javax.swing.JFormattedTextField();
        txtTotalItensServicos = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        btnAcrescimoServicosTipo = new javax.swing.JButton();
        btnAcrescimoProdutosTipo = new javax.swing.JButton();
        txtAcrescimoProdutos = new javax.swing.JFormattedTextField();
        txtAcrescimoServicos = new javax.swing.JFormattedTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        btnDescontoProdutosTipo = new javax.swing.JButton();
        txtDescontoProdutos = new javax.swing.JFormattedTextField();
        btnDescontoServicosTipo = new javax.swing.JButton();
        txtDescontoServicos = new javax.swing.JFormattedTextField();
        txtTotalProdutos = new javax.swing.JFormattedTextField();
        txtTotalServicos = new javax.swing.JFormattedTextField();
        jLabel39 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JFormattedTextField();
        jLabel40 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        txtTotalSeguroProdutos = new javax.swing.JFormattedTextField();
        txtTotalSeguroServicos = new javax.swing.JFormattedTextField();
        txtTotalFreteServicos = new javax.swing.JFormattedTextField();
        txtTotalFreteProdutos = new javax.swing.JFormattedTextField();
        jLabel43 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();

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

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtTipo.setEditable(false);
        txtTipo.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtTipo.setForeground(java.awt.Color.red);
        txtTipo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtTipo.setText("COMANDA 333");

        txtInativo.setEditable(false);
        txtInativo.setBackground(java.awt.Color.orange);
        txtInativo.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtInativo.setForeground(java.awt.Color.white);
        txtInativo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtInativo.setText("ORÇAMENTO");

        btnAceitarOrcamento.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnAceitarOrcamento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-checkmark-20.png"))); // NOI18N
        btnAceitarOrcamento.setToolTipText("APROVAR ORÇAMENTO");
        btnAceitarOrcamento.setContentAreaFilled(false);
        btnAceitarOrcamento.setIconTextGap(10);
        btnAceitarOrcamento.setPreferredSize(new java.awt.Dimension(180, 49));
        btnAceitarOrcamento.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAceitarOrcamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceitarOrcamentoActionPerformed(evt);
            }
        });

        btnCancelarDocumento.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnCancelarDocumento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-close-button-20.png"))); // NOI18N
        btnCancelarDocumento.setToolTipText("CANCELAR DOCUMENTO");
        btnCancelarDocumento.setContentAreaFilled(false);
        btnCancelarDocumento.setIconTextGap(10);
        btnCancelarDocumento.setPreferredSize(new java.awt.Dimension(180, 49));
        btnCancelarDocumento.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancelarDocumento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarDocumentoActionPerformed(evt);
            }
        });

        txtDocumentoId.setEditable(false);
        txtDocumentoId.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtDocumentoId.setForeground(java.awt.Color.red);
        txtDocumentoId.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtDocumentoId.setText("NOVO");

        btnImprimirEtiqueta.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnImprimirEtiqueta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-paycheque-20.png"))); // NOI18N
        btnImprimirEtiqueta.setToolTipText("IMPRIMIR ETIQUETAS");
        btnImprimirEtiqueta.setContentAreaFilled(false);
        btnImprimirEtiqueta.setIconTextGap(10);
        btnImprimirEtiqueta.setPreferredSize(new java.awt.Dimension(180, 49));
        btnImprimirEtiqueta.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimirEtiqueta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirEtiquetaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtInativo, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAceitarOrcamento, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtDocumentoId, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelarDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtTipo)
                .addGap(18, 18, 18)
                .addComponent(btnImprimirEtiqueta, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtInativo)
                    .addComponent(btnAceitarOrcamento, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtDocumentoId)
                    .addComponent(btnCancelarDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtTipo)
                    .addComponent(btnImprimirEtiqueta, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        tblItens.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tblItens.setIntercellSpacing(new java.awt.Dimension(10, 10));
        tblItens.setRowHeight(24);
        tblItens.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblItensFocusGained(evt);
            }
        });
        tblItens.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tblItensPropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(tblItens);

        pnlInserirProduto.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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

        txtItemQuantidade.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtItemQuantidade.setText("0,000");
        txtItemQuantidade.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtItemQuantidade.setName("decimal(3)"); // NOI18N
        txtItemQuantidade.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtItemQuantidadeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtItemQuantidadeFocusLost(evt);
            }
        });
        txtItemQuantidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtItemQuantidadeActionPerformed(evt);
            }
        });
        txtItemQuantidade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtItemQuantidadeKeyReleased(evt);
            }
        });

        btnInserir.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnInserir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-shopping-cart-20.png"))); // NOI18N
        btnInserir.setText("OK");
        btnInserir.setContentAreaFilled(false);
        btnInserir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInserirActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("QTD [F3]");

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
        jLabel15.setText("VALOR");

        txtItemDescontoPercentual.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtItemDescontoPercentual.setText("0,00");
        txtItemDescontoPercentual.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtItemDescontoPercentual.setName("decimal"); // NOI18N
        txtItemDescontoPercentual.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtItemDescontoPercentualKeyReleased(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel18.setText("DESCONTO %");

        txtItemNome.setEditable(false);
        txtItemNome.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        btnPesquisar.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnPesquisar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-search-button-20.png"))); // NOI18N
        btnPesquisar.setText("F9");
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

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("CÓDIGO [F2]");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("DESCRIÇÃO DO ITEM");

        javax.swing.GroupLayout pnlInserirProdutoLayout = new javax.swing.GroupLayout(pnlInserirProduto);
        pnlInserirProduto.setLayout(pnlInserirProdutoLayout);
        pnlInserirProdutoLayout.setHorizontalGroup(
            pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInserirProdutoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCodigo)
                    .addGroup(pnlInserirProdutoLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtItemNome, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtItemQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtItemDescontoPercentual))
                .addGap(18, 18, 18)
                .addComponent(btnInserir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlInserirProdutoLayout.setVerticalGroup(
            pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInserirProdutoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnInserir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlInserirProdutoLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jLabel6)
                            .addComponent(jLabel15)
                            .addComponent(jLabel18))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtItemQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtItemDescontoPercentual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtItemNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(btnPesquisar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtPessoaNome.setEditable(false);
        txtPessoaNome.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPessoaNome.setText("NÃO INFORMADO");

        btnCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/user.png"))); // NOI18N
        btnCliente.setText("F5 FORNECEDOR");
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

        btnRemoverCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-close-button-20.png"))); // NOI18N
        btnRemoverCliente.setToolTipText("Remover Cliente");
        btnRemoverCliente.setContentAreaFilled(false);
        btnRemoverCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverClienteActionPerformed(evt);
            }
        });

        txtPessoaTelefone.setEditable(false);
        txtPessoaTelefone.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        btnFuncionario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/user_red.png"))); // NOI18N
        btnFuncionario.setText("F4 FUNCIONÁRIO");
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

        btnRemoverFuncionario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-close-button-20.png"))); // NOI18N
        btnRemoverFuncionario.setToolTipText("Remover Funcionário");
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
                .addComponent(btnFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(txtFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRemoverFuncionario)
                .addGap(18, 18, 18)
                .addComponent(btnCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPessoaNome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPessoaTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRemoverCliente)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtFuncionario)
                    .addComponent(btnRemoverCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRemoverFuncionario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtPessoaNome)
                    .addComponent(btnCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtPessoaTelefone))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlObservacao.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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
        jScrollPane5.setViewportView(txtObservacao);

        jLabel37.setBackground(new java.awt.Color(122, 138, 153));
        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel37.setForeground(java.awt.Color.white);
        jLabel37.setText("Observação");
        jLabel37.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel37.setOpaque(true);

        javax.swing.GroupLayout pnlObservacaoLayout = new javax.swing.GroupLayout(pnlObservacao);
        pnlObservacao.setLayout(pnlObservacaoLayout);
        pnlObservacaoLayout.setHorizontalGroup(
            pnlObservacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlObservacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5)
                .addContainerGap())
        );
        pnlObservacaoLayout.setVerticalGroup(
            pnlObservacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlObservacaoLayout.createSequentialGroup()
                .addComponent(jLabel37)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtRecebido.setEditable(false);
        txtRecebido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecebido.setText("0,00");
        txtRecebido.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        btnPagar.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnPagar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-note-and-coin-20.png"))); // NOI18N
        btnPagar.setText("F7");
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

        btnReceber1.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnReceber1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-boleto-bankario-20.png"))); // NOI18N
        btnReceber1.setText("F8");
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

        txtFaturado.setEditable(false);
        txtFaturado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFaturado.setText("0,00");
        txtFaturado.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        jLabel45.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel45.setText("RECEBIMENTO / FATURA");
        jLabel45.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel45.setOpaque(true);

        txtEmAberto.setEditable(false);
        txtEmAberto.setForeground(java.awt.Color.red);
        txtEmAberto.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtEmAberto.setText("0,00");
        txtEmAberto.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N

        jLabel44.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setText("EM ABERTO");
        jLabel44.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel44.setOpaque(true);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnReceber1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFaturado))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(btnPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRecebido))
                    .addComponent(jLabel45, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEmAberto))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel45)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFaturado, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                            .addComponent(btnReceber1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel44)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEmAberto)))
                .addContainerGap())
        );

        pnlValores.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtTotalItensProdutos.setEditable(false);
        txtTotalItensProdutos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalItensProdutos.setText("0,00");
        txtTotalItensProdutos.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        txtTotalItensServicos.setEditable(false);
        txtTotalItensServicos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalItensServicos.setText("0,00");
        txtTotalItensServicos.setFocusable(false);
        txtTotalItensServicos.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("PRODUTOS");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("SUBTOTAIS BRUTOS");
        jLabel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel11.setText("SERVIÇOS");

        btnAcrescimoServicosTipo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnAcrescimoServicosTipo.setText("%");
        btnAcrescimoServicosTipo.setFocusable(false);
        btnAcrescimoServicosTipo.setPreferredSize(new java.awt.Dimension(55, 25));
        btnAcrescimoServicosTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAcrescimoServicosTipoActionPerformed(evt);
            }
        });

        btnAcrescimoProdutosTipo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnAcrescimoProdutosTipo.setText("%");
        btnAcrescimoProdutosTipo.setFocusable(false);
        btnAcrescimoProdutosTipo.setPreferredSize(new java.awt.Dimension(55, 25));
        btnAcrescimoProdutosTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAcrescimoProdutosTipoActionPerformed(evt);
            }
        });

        txtAcrescimoProdutos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAcrescimoProdutos.setText("0,00");
        txtAcrescimoProdutos.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtAcrescimoProdutos.setName("decimal"); // NOI18N
        txtAcrescimoProdutos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAcrescimoProdutosKeyReleased(evt);
            }
        });

        txtAcrescimoServicos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAcrescimoServicos.setText("0,00");
        txtAcrescimoServicos.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtAcrescimoServicos.setName("decimal"); // NOI18N
        txtAcrescimoServicos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAcrescimoServicosKeyReleased(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("ACRÉSCIMOS");
        jLabel22.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("DESCONTOS");
        jLabel23.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnDescontoProdutosTipo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnDescontoProdutosTipo.setText("%");
        btnDescontoProdutosTipo.setFocusable(false);
        btnDescontoProdutosTipo.setPreferredSize(new java.awt.Dimension(55, 25));
        btnDescontoProdutosTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDescontoProdutosTipoActionPerformed(evt);
            }
        });

        txtDescontoProdutos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDescontoProdutos.setText("0,00");
        txtDescontoProdutos.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtDescontoProdutos.setName("decimal"); // NOI18N
        txtDescontoProdutos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDescontoProdutosKeyReleased(evt);
            }
        });

        btnDescontoServicosTipo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnDescontoServicosTipo.setText("%");
        btnDescontoServicosTipo.setFocusable(false);
        btnDescontoServicosTipo.setPreferredSize(new java.awt.Dimension(55, 25));
        btnDescontoServicosTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDescontoServicosTipoActionPerformed(evt);
            }
        });

        txtDescontoServicos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDescontoServicos.setText("0,00");
        txtDescontoServicos.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtDescontoServicos.setName("decimal"); // NOI18N
        txtDescontoServicos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDescontoServicosKeyReleased(evt);
            }
        });

        txtTotalProdutos.setEditable(false);
        txtTotalProdutos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalProdutos.setText("0,00");
        txtTotalProdutos.setFocusable(false);
        txtTotalProdutos.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        txtTotalServicos.setEditable(false);
        txtTotalServicos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalServicos.setText("0,00");
        txtTotalServicos.setFocusable(false);
        txtTotalServicos.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        jLabel39.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("TOTAL GERAL");
        jLabel39.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel39.setOpaque(true);

        txtTotal.setEditable(false);
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtTotal.setText("0,00");
        txtTotal.setFocusable(false);
        txtTotal.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N

        jLabel40.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setText("SUBTOTAIS");
        jLabel40.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel40.setOpaque(true);

        jLabel42.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setText("SEGURO");
        jLabel42.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel42.setOpaque(true);

        txtTotalSeguroProdutos.setEditable(false);
        txtTotalSeguroProdutos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalSeguroProdutos.setText("0,00");
        txtTotalSeguroProdutos.setFocusable(false);
        txtTotalSeguroProdutos.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        txtTotalSeguroServicos.setEditable(false);
        txtTotalSeguroServicos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalSeguroServicos.setText("0,00");
        txtTotalSeguroServicos.setFocusable(false);
        txtTotalSeguroServicos.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        txtTotalFreteServicos.setEditable(false);
        txtTotalFreteServicos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalFreteServicos.setText("0,00");
        txtTotalFreteServicos.setFocusable(false);
        txtTotalFreteServicos.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        txtTotalFreteProdutos.setEditable(false);
        txtTotalFreteProdutos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalFreteProdutos.setText("0,00");
        txtTotalFreteProdutos.setFocusable(false);
        txtTotalFreteProdutos.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        jLabel43.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setText("FRETE");
        jLabel43.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel43.setOpaque(true);

        javax.swing.GroupLayout pnlValoresLayout = new javax.swing.GroupLayout(pnlValores);
        pnlValores.setLayout(pnlValoresLayout);
        pnlValoresLayout.setHorizontalGroup(
            pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlValoresLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlValoresLayout.createSequentialGroup()
                        .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(13, 13, 13)
                        .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTotalItensProdutos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(txtTotalItensServicos)))
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlValoresLayout.createSequentialGroup()
                        .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnAcrescimoServicosTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAcrescimoProdutosTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtAcrescimoServicos, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                            .addComponent(txtAcrescimoProdutos)))
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlValoresLayout.createSequentialGroup()
                        .addComponent(btnDescontoServicosTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDescontoServicos))
                    .addGroup(pnlValoresLayout.createSequentialGroup()
                        .addComponent(btnDescontoProdutosTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDescontoProdutos))
                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtTotalFreteProdutos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTotalFreteServicos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtTotalSeguroProdutos, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel42, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(txtTotalSeguroServicos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtTotalProdutos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTotalServicos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                    .addComponent(txtTotal))
                .addContainerGap())
        );
        pnlValoresLayout.setVerticalGroup(
            pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlValoresLayout.createSequentialGroup()
                .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlValoresLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTotalItensProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlValoresLayout.createSequentialGroup()
                        .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel22)
                                .addComponent(jLabel23)
                                .addComponent(jLabel16))
                            .addComponent(jLabel39))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlValoresLayout.createSequentialGroup()
                                .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnAcrescimoProdutosTipo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtAcrescimoProdutos)
                                    .addComponent(btnDescontoProdutosTipo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtDescontoProdutos))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtTotalItensServicos)
                                    .addComponent(btnDescontoServicosTipo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtAcrescimoServicos)
                                    .addComponent(txtDescontoServicos)
                                    .addComponent(btnAcrescimoServicosTipo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlValoresLayout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalProdutos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTotalServicos))
                    .addGroup(pnlValoresLayout.createSequentialGroup()
                        .addComponent(jLabel42)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalSeguroProdutos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTotalSeguroServicos))
                    .addGroup(pnlValoresLayout.createSequentialGroup()
                        .addComponent(jLabel43)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalFreteProdutos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTotalFreteServicos)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Totais", pnlValores);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1169, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 121, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Totais", jPanel2);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1169, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 121, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("+", jPanel6);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlInserirProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlObservacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1)
                    .addComponent(jTabbedPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlInserirProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlObservacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        setBounds(5, 25, 1200, 671);
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown

        txtCodigo.requestFocus();

        txtItemQuantidade.setText("1,000");
    }//GEN-LAST:event_formComponentShown


    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased

    }//GEN-LAST:event_formKeyReleased

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

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        txtCodigo.requestFocus();
    }//GEN-LAST:event_formFocusGained

    private void btnAceitarOrcamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceitarOrcamentoActionPerformed
        aceitarOrcamento();
    }//GEN-LAST:event_btnAceitarOrcamentoActionPerformed

    private void btnCancelarDocumentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarDocumentoActionPerformed
        cancelarDocumento();
    }//GEN-LAST:event_btnCancelarDocumentoActionPerformed

    private void tblItensFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblItensFocusGained
        txtCodigo.requestFocus();
    }//GEN-LAST:event_tblItensFocusGained

    private void tblItensPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tblItensPropertyChange

    }//GEN-LAST:event_tblItensPropertyChange

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void txtCodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoKeyReleased
        int index;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                if (!txtCodigo.getText().trim().equals("")) {
                    produto = null; //limpar pré carregado
                    produto = validarInsercaoItem();
                    if (produto != null) {
                        preCarregarItem();
                        if (VENDA_INSERCAO_DIRETA) {
                            inserirItem(Decimal.fromString(txtItemQuantidade.getText()));
                        }
                    }
                }
                break;
            case KeyEvent.VK_DOWN:
                index = tblItens.getSelectedRow() + 1;
                if (index < tblItens.getRowCount()) {
                    tblItens.setRowSelectionInterval(index, index);
                    tblItens.scrollRectToVisible(tblItens.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_UP:
                index = tblItens.getSelectedRow() - 1;
                if (index > -1) {
                    tblItens.setRowSelectionInterval(index, index);
                    tblItens.scrollRectToVisible(tblItens.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_DELETE:
                excluirItem();
                break;
        }

    }//GEN-LAST:event_txtCodigoKeyReleased

    private void txtItemQuantidadeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtItemQuantidadeFocusGained

    }//GEN-LAST:event_txtItemQuantidadeFocusGained

    private void txtItemQuantidadeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtItemQuantidadeFocusLost
        if (Decimal.fromString(txtItemQuantidade.getText()).compareTo(BigDecimal.ZERO) <= 0) {
            txtItemQuantidade.setText("1,000");
            //calcularSubtotal(); - copiado da venda - ainda não tem na compra
        }
    }//GEN-LAST:event_txtItemQuantidadeFocusLost

    private void txtItemQuantidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtItemQuantidadeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtItemQuantidadeActionPerformed

    private void txtItemQuantidadeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtItemQuantidadeKeyReleased
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
    }//GEN-LAST:event_txtItemQuantidadeKeyReleased

    private void btnInserirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInserirActionPerformed
        produto = validarInsercaoItem();
        if (produto != null) {
            inserirItem(Decimal.fromString(txtItemQuantidade.getText()));
        }
    }//GEN-LAST:event_btnInserirActionPerformed

    private void txtValorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorKeyReleased
        int index;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                //2019-04-02 - adicinado desconto
                /*produto = validarInsercaoItem();
            if (produto != null) {
                inserirItem(Decimal.fromString(txtQuantidade.getText()));
            }*/
                txtItemDescontoPercentual.requestFocus();
                break;
            case KeyEvent.VK_DOWN:
                index = tblItens.getSelectedRow() + 1;
                if (index < tblItens.getRowCount()) {
                    tblItens.setRowSelectionInterval(index, index);
                    tblItens.scrollRectToVisible(tblItens.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_UP:
                index = tblItens.getSelectedRow() - 1;
                if (index > -1) {
                    tblItens.setRowSelectionInterval(index, index);
                    tblItens.scrollRectToVisible(tblItens.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_DELETE:
                excluirItem();
                break;
        }
    }//GEN-LAST:event_txtValorKeyReleased

    private void txtItemDescontoPercentualKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtItemDescontoPercentualKeyReleased
        int index;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                produto = validarInsercaoItem();
                if (produto != null) {
                    inserirItem(Decimal.fromString(txtItemQuantidade.getText()));
                }
                break;
            case KeyEvent.VK_DOWN:
                index = tblItens.getSelectedRow() + 1;
                if (index < tblItens.getRowCount()) {
                    tblItens.setRowSelectionInterval(index, index);
                    tblItens.scrollRectToVisible(tblItens.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_UP:
                index = tblItens.getSelectedRow() - 1;
                if (index > -1) {
                    tblItens.setRowSelectionInterval(index, index);
                    tblItens.scrollRectToVisible(tblItens.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_DELETE:
                excluirItem();
                break;
        }
    }//GEN-LAST:event_txtItemDescontoPercentualKeyReleased

    private void btnPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarActionPerformed
        pesquisarProduto(null);
    }//GEN-LAST:event_btnPesquisarActionPerformed

    private void btnClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClienteActionPerformed
        pesquisarPessoa();
    }//GEN-LAST:event_btnClienteActionPerformed

    private void btnRemoverClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverClienteActionPerformed
        removerPessoa();
    }//GEN-LAST:event_btnRemoverClienteActionPerformed

    private void btnFuncionarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFuncionarioActionPerformed
        pesquisarFuncionario();
    }//GEN-LAST:event_btnFuncionarioActionPerformed

    private void btnRemoverFuncionarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverFuncionarioActionPerformed
        removerFuncionario();
    }//GEN-LAST:event_btnRemoverFuncionarioActionPerformed

    private void btnPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPagarActionPerformed
        pagar();
    }//GEN-LAST:event_btnPagarActionPerformed

    private void btnReceber1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReceber1ActionPerformed
        parcelar();
    }//GEN-LAST:event_btnReceber1ActionPerformed

    private void txtObservacaoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtObservacaoFocusLost
        salvar();
    }//GEN-LAST:event_txtObservacaoFocusLost

    private void txtObservacaoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtObservacaoPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtObservacaoPropertyChange

    private void btnImprimirEtiquetaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirEtiquetaActionPerformed
        imprimirEtiqueta();
    }//GEN-LAST:event_btnImprimirEtiquetaActionPerformed

    private void btnAcrescimoServicosTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAcrescimoServicosTipoActionPerformed
        alternarAcrescimoServicosTipo();
    }//GEN-LAST:event_btnAcrescimoServicosTipoActionPerformed

    private void btnAcrescimoProdutosTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAcrescimoProdutosTipoActionPerformed
        alternarAcrescimoProdutosTipo();
    }//GEN-LAST:event_btnAcrescimoProdutosTipoActionPerformed

    private void txtAcrescimoProdutosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAcrescimoProdutosKeyReleased
        distribuirAcrescimoProdutos();
    }//GEN-LAST:event_txtAcrescimoProdutosKeyReleased

    private void txtAcrescimoServicosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAcrescimoServicosKeyReleased
        distribuirAcrescimoServicos();
    }//GEN-LAST:event_txtAcrescimoServicosKeyReleased

    private void btnDescontoProdutosTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDescontoProdutosTipoActionPerformed
        alternarDescontoProdutosTipo();
    }//GEN-LAST:event_btnDescontoProdutosTipoActionPerformed

    private void txtDescontoProdutosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescontoProdutosKeyReleased
        distribuirDescontoProdutos();
    }//GEN-LAST:event_txtDescontoProdutosKeyReleased

    private void btnDescontoServicosTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDescontoServicosTipoActionPerformed
        alternarDescontoServicosTipo();
    }//GEN-LAST:event_btnDescontoServicosTipoActionPerformed

    private void txtDescontoServicosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescontoServicosKeyReleased
        distribuirDescontoServicos();
    }//GEN-LAST:event_txtDescontoServicosKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceitarOrcamento;
    private javax.swing.JButton btnAcrescimoProdutosTipo;
    private javax.swing.JButton btnAcrescimoServicosTipo;
    private javax.swing.JButton btnCancelarDocumento;
    private javax.swing.JButton btnCliente;
    private javax.swing.JButton btnDescontoProdutosTipo;
    private javax.swing.JButton btnDescontoServicosTipo;
    private javax.swing.JButton btnFuncionario;
    private javax.swing.JButton btnImprimirEtiqueta;
    private javax.swing.JButton btnInserir;
    private javax.swing.JButton btnPagar;
    private javax.swing.JButton btnPesquisar;
    private javax.swing.JButton btnReceber1;
    private javax.swing.JButton btnRemoverCliente;
    private javax.swing.JButton btnRemoverFuncionario;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel pnlInserirProduto;
    private javax.swing.JPanel pnlObservacao;
    private javax.swing.JPanel pnlValores;
    private javax.swing.JTable tblItens;
    private javax.swing.JFormattedTextField txtAcrescimoProdutos;
    private javax.swing.JFormattedTextField txtAcrescimoServicos;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JFormattedTextField txtDescontoProdutos;
    private javax.swing.JFormattedTextField txtDescontoServicos;
    private javax.swing.JTextField txtDocumentoId;
    private javax.swing.JFormattedTextField txtEmAberto;
    private javax.swing.JFormattedTextField txtFaturado;
    private javax.swing.JTextField txtFuncionario;
    private javax.swing.JTextField txtInativo;
    private javax.swing.JFormattedTextField txtItemDescontoPercentual;
    private javax.swing.JTextField txtItemNome;
    private javax.swing.JFormattedTextField txtItemQuantidade;
    private javax.swing.JTextArea txtObservacao;
    private javax.swing.JTextField txtPessoaNome;
    private javax.swing.JTextField txtPessoaTelefone;
    private javax.swing.JFormattedTextField txtRecebido;
    private javax.swing.JTextField txtTipo;
    private javax.swing.JFormattedTextField txtTotal;
    private javax.swing.JFormattedTextField txtTotalFreteProdutos;
    private javax.swing.JFormattedTextField txtTotalFreteServicos;
    private javax.swing.JFormattedTextField txtTotalItensProdutos;
    private javax.swing.JFormattedTextField txtTotalItensServicos;
    private javax.swing.JFormattedTextField txtTotalProdutos;
    private javax.swing.JFormattedTextField txtTotalSeguroProdutos;
    private javax.swing.JFormattedTextField txtTotalSeguroServicos;
    private javax.swing.JFormattedTextField txtTotalServicos;
    private javax.swing.JFormattedTextField txtValor;
    // End of variables declaration//GEN-END:variables
}
