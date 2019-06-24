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
import model.jtable.documento.DocumentoEntradaJTableModel;
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
import model.mysql.dao.principal.catalogo.ProdutoDAO;
import model.jtable.documento.DocumentoSaidaJTableModel;
import model.mysql.bean.principal.documento.TipoOperacao;
import static ouroboros.Constants.*;
import static ouroboros.Ouroboros.IMPRESSORA_A4;
import static ouroboros.Ouroboros.IMPRESSORA_CUPOM;
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
import static ouroboros.Ouroboros.TO_PRINTER_PATH;
import static ouroboros.Ouroboros.USUARIO;
import printing.DocumentoSaidaPrint;
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
            em.refresh(venda); //para uso em várias estações
        }

        venda.setOrcamento(orcamento);

        configurarPorTipo();

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

            if (venda.getId() != null && venda.getId() != 0) {

                txtDocumentoId.setText(venda.getId().toString());

                txtObservacao.setText(venda.getObservacao());

                parcelas = venda.getParcelas();

                exibirTotais();

                carregarTabela();

            }

            configurarPorTipo();

            formatarTabela();

            exibirFuncionario();
            exibirPessoa();

            definirAtalhos();
        }

    }

    private void configurarPorTipo() {
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

    private void formatarTabela() {
        tblItens.setModel(documentoEntradaJTableModel);
        
        tblItens.getColumnModel().getColumn(0).setPreferredWidth(1);
        
        tblItens.getColumn("#").setPreferredWidth(40);
        tblItens.getColumn("#").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblItens.getColumn("Código").setPreferredWidth(80);
        tblItens.getColumn("Código").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblItens.getColumn("Descrição").setPreferredWidth(300);
        
        tblItens.getColumn("Quantidade").setPreferredWidth(100);
        tblItens.getColumn("Quantidade").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblItens.getColumn("UM").setPreferredWidth(60);
        
        tblItens.getColumn("Valor").setPreferredWidth(100);
        tblItens.getColumn("Valor").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblItens.getColumn("-%").setPreferredWidth(100);
        tblItens.getColumn("-%").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
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
                    txtQuantidade.requestFocus();
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
        if( !documento.getTipoOperacao().equals( TipoOperacao.ENTRADA )) {
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
        documento.setAcrescimoPercentualProdutos(Decimal.fromString(txtAcrescimoPercentual.getText()));
        documento.setAcrescimoMonetarioProdutos(Decimal.fromString(txtAcrescimo.getText()));
        documento.setDescontoPercentualProdutos(Decimal.fromString(txtDescontoPercentual.getText()));
        documento.setDescontoMonetarioProdutos(Decimal.fromString(txtDesconto.getText()));
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
        txtItemNome.setText(produto.getNome());
        txtValor.setText(Decimal.toString(produto.getValorCompra()));
        txtQuantidade.requestFocus();
        
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
        MovimentoFisico movimentoFisico = new MovimentoFisico(produto, 
                produto.getCodigo(), 
                produto.getNome(),
                quantidade, 
                BigDecimal.ZERO, 
                valorCompra, 
                BigDecimal.ZERO,
                produto.getUnidadeComercialVenda(), 
                MovimentoFisicoTipo.COMPRA, 
                null);
        
        movimentoFisico.setDataEntrada(LocalDateTime.now());

        movimentoFisico = movimentoFisicoDAO.save(movimentoFisico);
        
        produto.addMovimentoFisico(movimentoFisico); //2019-06-10 - atualizar estoque

        documento.addMovimentoFisico(movimentoFisico);

        documento = vendaDAO.save(documento);

        carregarTabela();

        int index = tblItens.getRowCount() - 1;
        tblItens.setRowSelectionInterval(index, index);

        //rolar para o item (forçar visibilidade)
        tblItens.scrollRectToVisible(tblItens.getCellRect(index, 0, true));

        exibirTotais();

        //resetar campos
        txtCodigo.setText("");
        txtItemNome.setText("");
        txtQuantidade.setText("1,000");
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

    
    private void exibirTotais() {
        txtTotalItens.setText(Decimal.toString(documento.getTotalItens()));
        txtAcrescimoPercentual.setText(Decimal.toString(documento.getAcrescimoPercentualProdutos()));
        txtAcrescimo.setText(Decimal.toString(documento.getAcrescimoMonetarioProdutos()));
        txtDescontoPercentual.setText(Decimal.toString(documento.getDescontoPercentualProdutos()));
        txtDesconto.setText(Decimal.toString(documento.getDescontoMonetarioProdutos()));
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
        CriarPDF.gerarVenda(documento, pdfFilePath);
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
            configurarPorTipo();

        }
    }

    private void aceitarOrcamento() {
        int resposta = JOptionPane.showConfirmDialog(MAIN_VIEW, "Aceitar orçamento? Este procedimento é irreversível.", "Atenção", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resposta == JOptionPane.OK_OPTION) {
            documento.setOrcamento(false);
            documento = vendaDAO.save(documento);
            configurarPorTipo();
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

        jPanel5 = new javax.swing.JPanel();
        txtTipo = new javax.swing.JTextField();
        txtInativo = new javax.swing.JTextField();
        btnAceitarOrcamento = new javax.swing.JButton();
        btnCancelarDocumento = new javax.swing.JButton();
        txtDocumentoId = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblItens = new javax.swing.JTable();
        pnlInserirProduto = new javax.swing.JPanel();
        txtCodigo = new javax.swing.JTextField();
        txtQuantidade = new javax.swing.JFormattedTextField();
        btnInserir = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtValor = new javax.swing.JFormattedTextField();
        jLabel15 = new javax.swing.JLabel();
        txtDescontoPercentualItem = new javax.swing.JFormattedTextField();
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
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtTotalItens = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JFormattedTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtAcrescimoPercentual = new javax.swing.JFormattedTextField();
        txtDescontoPercentual = new javax.swing.JFormattedTextField();
        txtAcrescimo = new javax.swing.JFormattedTextField();
        txtDesconto = new javax.swing.JFormattedTextField();
        jLabel20 = new javax.swing.JLabel();
        txtRecebido = new javax.swing.JFormattedTextField();
        txtFaturado = new javax.swing.JFormattedTextField();
        btnPagar = new javax.swing.JButton();
        btnReceber1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        txtEmAberto = new javax.swing.JFormattedTextField();
        pnlObservacao = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtObservacao = new javax.swing.JTextArea();
        jLabel37 = new javax.swing.JLabel();

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
                .addComponent(txtTipo, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
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
                    .addComponent(txtTipo))
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

        txtDescontoPercentualItem.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDescontoPercentualItem.setText("0,00");
        txtDescontoPercentualItem.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDescontoPercentualItem.setName("decimal"); // NOI18N
        txtDescontoPercentualItem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDescontoPercentualItemKeyReleased(evt);
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
                        .addGap(0, 59, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtItemNome, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDescontoPercentualItem))
                .addGap(18, 18, 18)
                .addComponent(btnInserir, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
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
                            .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDescontoPercentualItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(txtPessoaNome, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
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

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("TOTAL ITENS");

        txtTotalItens.setEditable(false);
        txtTotalItens.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalItens.setText("0,00");
        txtTotalItens.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("+");
        jLabel12.setToolTipText("ACRÉSCIMO");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("-");
        jLabel13.setToolTipText("DESCONTO");

        txtTotal.setEditable(false);
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotal.setText("0,00");
        txtTotal.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("TOTAL");

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
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

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("$");

        txtRecebido.setEditable(false);
        txtRecebido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecebido.setText("0,00");
        txtRecebido.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        txtFaturado.setEditable(false);
        txtFaturado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFaturado.setText("0,00");
        txtFaturado.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        btnPagar.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnPagar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-note-and-coin-20.png"))); // NOI18N
        btnPagar.setText("F7 PAGA");
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
        btnReceber1.setText("F8 FATURA");
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

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel38.setBackground(new java.awt.Color(122, 138, 153));
        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel38.setForeground(java.awt.Color.white);
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("EM ABERTO");
        jLabel38.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel38.setOpaque(true);

        txtEmAberto.setEditable(false);
        txtEmAberto.setBorder(null);
        txtEmAberto.setForeground(java.awt.Color.red);
        txtEmAberto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtEmAberto.setText("0,00");
        txtEmAberto.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtEmAberto)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel38)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtEmAberto, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTotalItens, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAcrescimoPercentual, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDescontoPercentual, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtAcrescimo)
                    .addComponent(txtDesconto)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnReceber1, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                    .addComponent(btnPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFaturado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel17)
                                .addComponent(jLabel20))
                            .addComponent(jLabel14))
                        .addGap(11, 11, 11)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTotalItens, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel12)
                                    .addComponent(txtAcrescimo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtAcrescimoPercentual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtDesconto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDescontoPercentual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13)))
                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel7)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtRecebido)
                            .addComponent(btnPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtFaturado)
                            .addComponent(btnReceber1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
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
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlInserirProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(pnlObservacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlObservacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        setBounds(5, 25, 1200, 775);
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        

        txtCodigo.requestFocus();

        txtQuantidade.setText("1,000");
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
                        inserirItem(Decimal.fromString(txtQuantidade.getText()));
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

    private void txtQuantidadeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtQuantidadeFocusGained
        txtQuantidade.setText("0");
    }//GEN-LAST:event_txtQuantidadeFocusGained

    private void txtQuantidadeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtQuantidadeFocusLost
        if (txtQuantidade.getText().equals("0,000")) {
            txtQuantidade.setText("1,000");
        }
    }//GEN-LAST:event_txtQuantidadeFocusLost

    private void txtQuantidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQuantidadeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQuantidadeActionPerformed

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

    private void btnInserirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInserirActionPerformed
        produto = validarInsercaoItem();
        if (produto != null) {
            inserirItem(Decimal.fromString(txtQuantidade.getText()));
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
            txtDescontoPercentualItem.requestFocus();
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

    private void txtDescontoPercentualItemKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescontoPercentualItemKeyReleased
        int index;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
            produto = validarInsercaoItem();
            if (produto != null) {
                inserirItem(Decimal.fromString(txtQuantidade.getText()));
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
    }//GEN-LAST:event_txtDescontoPercentualItemKeyReleased

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

    private void txtAcrescimoPercentualKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAcrescimoPercentualKeyReleased
        System.out.println("txtAcrescimoPercentualKeyReleased...");
        if (Decimal.fromString(txtAcrescimoPercentual.getText()).compareTo(BigDecimal.ZERO) > 0) {
            txtAcrescimo.setText("0");
        }
        salvar();
    }//GEN-LAST:event_txtAcrescimoPercentualKeyReleased

    private void txtDescontoPercentualKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescontoPercentualKeyReleased
        System.out.println("txtDescontoPercentualKeyReleased...");
        if (Decimal.fromString(txtDescontoPercentual.getText()).compareTo(BigDecimal.ZERO) > 0) {
            txtDesconto.setText("0");
        }
        salvar();
    }//GEN-LAST:event_txtDescontoPercentualKeyReleased

    private void txtAcrescimoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAcrescimoKeyReleased
        System.out.println("txtAcrescimoKeyReleased...");
        if (Decimal.fromString(txtAcrescimo.getText()).compareTo(BigDecimal.ZERO) > 0) {
            txtAcrescimoPercentual.setText("0");
        }
        salvar();
    }//GEN-LAST:event_txtAcrescimoKeyReleased

    private void txtDescontoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescontoKeyReleased
        System.out.println("txtDescontoKeyReleased...");
        if (Decimal.fromString(txtDesconto.getText()).compareTo(BigDecimal.ZERO) > 0) {
            txtDescontoPercentual.setText("0");
        }
        salvar();
    }//GEN-LAST:event_txtDescontoKeyReleased

    private void btnPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPagarActionPerformed
        pagar();
    }//GEN-LAST:event_btnPagarActionPerformed

    private void btnReceber1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReceber1ActionPerformed
        parcelar();
    }//GEN-LAST:event_btnReceber1ActionPerformed

    private void txtObservacaoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtObservacaoFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtObservacaoFocusLost

    private void txtObservacaoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtObservacaoPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtObservacaoPropertyChange


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceitarOrcamento;
    private javax.swing.JButton btnCancelarDocumento;
    private javax.swing.JButton btnCliente;
    private javax.swing.JButton btnFuncionario;
    private javax.swing.JButton btnInserir;
    private javax.swing.JButton btnPagar;
    private javax.swing.JButton btnPesquisar;
    private javax.swing.JButton btnReceber1;
    private javax.swing.JButton btnRemoverCliente;
    private javax.swing.JButton btnRemoverFuncionario;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JPanel pnlInserirProduto;
    private javax.swing.JPanel pnlObservacao;
    private javax.swing.JTable tblItens;
    private javax.swing.JFormattedTextField txtAcrescimo;
    private javax.swing.JFormattedTextField txtAcrescimoPercentual;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JFormattedTextField txtDesconto;
    private javax.swing.JFormattedTextField txtDescontoPercentual;
    private javax.swing.JFormattedTextField txtDescontoPercentualItem;
    private javax.swing.JTextField txtDocumentoId;
    private javax.swing.JFormattedTextField txtEmAberto;
    private javax.swing.JFormattedTextField txtFaturado;
    private javax.swing.JTextField txtFuncionario;
    private javax.swing.JTextField txtInativo;
    private javax.swing.JTextField txtItemNome;
    private javax.swing.JTextArea txtObservacao;
    private javax.swing.JTextField txtPessoaNome;
    private javax.swing.JTextField txtPessoaTelefone;
    private javax.swing.JFormattedTextField txtQuantidade;
    private javax.swing.JFormattedTextField txtRecebido;
    private javax.swing.JTextField txtTipo;
    private javax.swing.JFormattedTextField txtTotal;
    private javax.swing.JFormattedTextField txtTotalItens;
    private javax.swing.JFormattedTextField txtValor;
    // End of variables declaration//GEN-END:variables
}
