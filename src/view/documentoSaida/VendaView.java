package view.documentoSaida;

import java.awt.Dimension;
import view.sat.SatEmitirCupomView;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import model.mysql.dao.principal.MovimentoFisicoDAO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import model.mysql.bean.principal.financeiro.Caixa;
import model.mysql.bean.principal.MovimentoFisicoTipo;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.fiscal.UnidadeComercial;
import model.mysql.bean.principal.pessoa.PessoaTipo;
import model.mysql.bean.principal.Recurso;
import model.mysql.bean.principal.documento.VendaTipo;
import model.mysql.dao.principal.CaixaDAO;
import model.mysql.dao.principal.VendaDAO;
import model.mysql.dao.principal.catalogo.ProdutoDAO;
import model.jtable.documento.DocumentoSaidaItensJTableModel;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.catalogo.ProdutoTamanho;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.dao.fiscal.MeioDePagamentoDAO;
import model.mysql.dao.principal.UsuarioDAO;
import static ouroboros.Constants.*;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.VENDA_INSERCAO_DIRETA;
import printing.TermicaPrint;
import util.DateTime;
import util.Decimal;
import util.JSwing;
import view.Toast;
import view.produto.geral.ProdutoPesquisaView;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.TO_PRINTER_PATH;
import printing.PrintPDFBox;
import view.produto.item.ConfirmarEntregaDevolucaoView;
import static ouroboros.Ouroboros.IMPRESSORA_CUPOM;
import static ouroboros.Ouroboros.USUARIO;
import printing.RelatorioPdf;
import printing.Carne;
import printing.DocumentoSaidaPrint;
import printing.Promissoria;
import printing.TicketCozinhaPrint;
import sat.MwSat;
import util.Cor;
import util.Document.MonetarioDocument;
import util.FiscalUtil;
import util.jTableFormat.LineWrapCellRenderer;
import view.documentoSaida.geral.DocumentoSaidaPesquisaView;
import view.funcionario.FuncionarioPesquisaView;
import view.nfe.NfeDetalheView;
import view.pessoa.PessoaPesquisaView;
import view.veiculo.VeiculoPesquisaView;

public class VendaView extends javax.swing.JInternalFrame {
    long start;
    
    private Integer comanda;
    private static List<VendaView> vendaViews = new ArrayList<>(); //instâncias

    private Venda documento;
    private VendaDAO vendaDAO = new VendaDAO();
    private MovimentoFisicoDAO movimentoFisicoDAO = new MovimentoFisicoDAO();
    private ProdutoDAO produtoDAO = new ProdutoDAO();

    
    private final DocumentoSaidaItensJTableModel documentoSaidaItensJTableModel = new DocumentoSaidaItensJTableModel();

    private Produto produto = null;

    /**
     * Creates new form Venda
     */
    public VendaView() {
        initComponents();
        JSwing.startComponentsBehavior(this);
    }

    public VendaView(Venda venda, boolean orcamento) {
        if (USUARIO.autorizarAcesso(Recurso.ORCAMENTO)) {

            initComponents();
            JSwing.startComponentsBehavior(this);

            if (venda.getId() != null) {
                ////em.refresh(venda); //para uso em várias estações
            }

            venda.setOrcamento(orcamento);

            configurarTela();
            formatarBotoesAcrescimoDesconto();

            formatarTabela();

            definirAtalhos();
        }
    }

    public VendaView(Venda venda, Integer comanda) {
        start = System.currentTimeMillis();
        if (venda.getVendaTipo().equals(VendaTipo.DELIVERY)
                || venda.getVendaTipo().equals(VendaTipo.VENDA) && USUARIO.autorizarAcesso(Recurso.VENDA)
                || venda.getVendaTipo().equals(VendaTipo.PEDIDO) && USUARIO.autorizarAcesso(Recurso.PEDIDO)
                || venda.getVendaTipo().equals(VendaTipo.ORDEM_DE_SERVICO) && USUARIO.autorizarAcesso(Recurso.ORDEM_DE_SERVICO)
                || venda.getVendaTipo().equals(VendaTipo.LOCAÇÃO) && USUARIO.autorizarAcesso(Recurso.LOCACAO)
                || venda.getVendaTipo().equals(VendaTipo.COMANDA) && USUARIO.autorizarAcesso(Recurso.COMANDAS)) {

            initComponents();
            JSwing.startComponentsBehavior(this);

            ////btnMesclarDocumento.setVisible(false);
            
            
            if (venda.getId() != null) {
                //Desativei em 2019-05-10
                ////Ouroboros.em.refresh(venda); //para uso em várias estações 
                venda = vendaDAO.findById(venda.getId());
            }
            this.documento = venda;
            this.comanda = venda.getComanda();

            //Delivery
            carregarFormaPagamento();
            //
            
            
            if (venda.getId() != null && venda.getId() != 0) {

                if (venda.getComanda() != null) {
                    this.comanda = venda.getComanda();
                }
                
                txtComandaNome.setText(venda.getComandaNome());

                txtDocumentoId.setText(venda.getId().toString());

                txtRelato.setText(venda.getRelato());

                txtObservacao.setText(venda.getObservacao());

                //Delivery------------------------------------------------------
                cboMeioDePagamento.setSelectedItem(venda.getMeioDePagamento());
                txtReceber.setText(Decimal.toString(venda.getValorReceber()));
                txtTroco.setText(Decimal.toString(venda.getValorTroco()));
                txtEnderecoEntrega.setText(venda.getEnderecoEntrega());
                txtEnderecoEntrega.setCaretPosition(0);
                //Fim Delivery--------------------------------------------------


                carregarAcrescimosDescontos();
                exibirTotais();

                carregarTabela();

            } else {
                //Valores padrão para nova venda

            }

            configurarTela();
            formatarBotoesAcrescimoDesconto();

            formatarTabela(); //tem que formatar antes de carregar

            exibirFuncionario();
            exibirPessoa();
            exibirVeiculo();

            
            
            if(!documento.hasDocumentoPai()) {
                definirAtalhos();
                
            } else {
                
                JOptionPane.showMessageDialog(MAIN_VIEW, "Este documento foi agrupado no documento de id " + documento.getDocumentoPai().getId(), "Atenção", JOptionPane.WARNING_MESSAGE);
                JSwing.setComponentesHabilitados(pnlSuperior, false);
                JSwing.setComponentesHabilitados(pnlInserirProduto, false);
                tblItens.setEnabled(false);
                JSwing.setComponentesHabilitados(pnlPessoas, false);
                JSwing.setComponentesHabilitados(pnlObservacao, false);
                JSwing.setComponentesHabilitados(pnlRecebimento, false);
                JSwing.setComponentesHabilitados(pnlTotais, false);
            }

        }

        long elapsed = System.currentTimeMillis() - start;
        lblMensagem.setText("Consulta realizada em " + elapsed + "ms");
    }

    private void configurarTela() {
        txtTipo.setText(documento.getVendaTipo().getNome());
        
        btnReceber.setEnabled(false);
        btnAceitarOrcamento.setVisible(false);
        btnProcesso.setVisible(true);
        
        btnEncerrarVenda.setVisible(false);
        
        txtComandaNome.setVisible(false);
        
        btnTransferirComanda.setVisible(false);
        btnNFe.setVisible(false);
        btnImprimirSat.setVisible(false);
        pnlRelato.setVisible(false);
        //pnlCancelamento.setVisible(false);

        btnImprimirCarne.setVisible(false);

        btnImprimirTicketComanda.setVisible(false);
        btnImprimirTicketCozinha.setVisible(false);

        btnVeiculo.setVisible(Ouroboros.VEICULO_HABILITAR);
        txtVeiculo.setVisible(Ouroboros.VEICULO_HABILITAR);
        btnRemoverVeiculo.setVisible(Ouroboros.VEICULO_HABILITAR);

        pnlDeliveryRecebimento.setVisible(false);

        /*if (venda.getCancelamento() != null) {
            txtMotivoCancelamento.setText(venda.getMotivoCancelamento());
            txtDataCancelamento.setText(DateTime.toString(venda.getCancelamento()));
            pnlInserirProduto.setVisible(false);
            pnlCancelamento.setVisible(true);
            btnAceitarOrcamento.setEnabled(true);
            btnCancelar.setEnabled(false);
            btnAceitarOrcamento.setEnabled(false);

        } else*/ if (documento.isOrcamento()) {
            btnAceitarOrcamento.setVisible(true);
            btnProcesso.setVisible(false);

            if (documento.getVendaTipo().equals(VendaTipo.ORDEM_DE_SERVICO)) {
                pnlRelato.setVisible(true);
            }

        } else {
            if (documento.getVendaTipo().equals(VendaTipo.DELIVERY)) { //2019-09-29
                txtComandaNome.setVisible(true);
                btnReceber.setEnabled(true);
                //btnImprimirSat.setVisible(Ouroboros.SAT_HABILITAR);
                btnImprimirTicketCozinha.setVisible(true);
                pnlDeliveryRecebimento.setVisible(true);

            } else if (documento.getVendaTipo().equals(VendaTipo.VENDA)) {
                btnReceber.setEnabled(true);
                btnImprimirCarne.setVisible(true);
                btnImprimirSat.setVisible(Ouroboros.SAT_HABILITAR);
                btnNFe.setVisible(Ouroboros.NFE_HABILITAR);

            } else if (documento.getVendaTipo().equals(VendaTipo.PEDIDO)) {
                btnReceber.setEnabled(true);
                btnImprimirSat.setVisible(Ouroboros.SAT_HABILITAR);
                btnNFe.setVisible(Ouroboros.NFE_HABILITAR);

            } else if (documento.getVendaTipo().equals(VendaTipo.ORDEM_DE_SERVICO)) {
                btnReceber.setEnabled(true);
                btnNFe.setVisible(Ouroboros.NFE_HABILITAR);
                pnlRelato.setVisible(true);

            } else if (documento.getVendaTipo().equals(VendaTipo.LOCAÇÃO)) {
                btnReceber.setEnabled(true);
                //pnlEntregaDevolucao.setVisible(true);

            } else if (documento.getVendaTipo().equals(VendaTipo.COMANDA)) {
                txtTipo.setText("COMANDA " + comanda);
                txtComandaNome.setVisible(true);
                btnEncerrarVenda.setVisible(true);
                btnTransferirComanda.setVisible(true);
                btnImprimirSat.setVisible(Ouroboros.SAT_HABILITAR);
                btnNFe.setVisible(Ouroboros.NFE_HABILITAR);
                btnImprimirTicketComanda.setVisible(true);
                btnImprimirTicketCozinha.setVisible(true);
                btnReceber.setEnabled(true);
            }
        }

        //modo Balcão
        if (Ouroboros.SISTEMA_MODO_BALCAO) {
            pnlPessoas.setVisible(false);
            pnlRelato.setVisible(false);
            pnlObservacao.setVisible(false);
            pnlRecebimento.setVisible(false);
            pnlTotais.setVisible(false);

            btnEncerrarVenda.setVisible(false);
            btnImprimirA4.setVisible(false);
            btnImprimirTermica.setVisible(false);
        }

    }

    private void formatarBotoesAcrescimoDesconto() {

        if (documento.getId() == null) {
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
        
        JTextField celTexto = new JTextField();
        celTexto.setFont(tblItens.getFont());
        DefaultCellEditor textoEditor = new DefaultCellEditor(celTexto);
        
        JFormattedTextField celDecimal = new JFormattedTextField();
        celDecimal.setHorizontalAlignment(SwingConstants.RIGHT);
        celDecimal.setFont(tblItens.getFont());
        celDecimal.setDocument(new MonetarioDocument());
        DefaultCellEditor decimalEditor = new DefaultCellEditor(celDecimal);
        
        tblItens.setModel(documentoSaidaItensJTableModel);

        tblItens.setRowHeight(30);
        tblItens.setIntercellSpacing(new Dimension(10, 10));
        tblItens.setDefaultRenderer(String.class, new LineWrapCellRenderer());
        //id
        tblItens.getColumnModel().getColumn(0).setPreferredWidth(1);
        //tableItens.getColumnModel().getColumn(0).setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        //número
        tblItens.getColumn("#").setPreferredWidth(60);
        tblItens.getColumn("#").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblItens.getColumn("Código").setPreferredWidth(80);
        tblItens.getColumn("Código").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblItens.getColumn("Descrição").setPreferredWidth(600);
        tblItens.getColumn("Descrição").setCellEditor(textoEditor);

        tblItens.getColumn("Quantidade").setPreferredWidth(100);
        tblItens.getColumn("Quantidade").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        tblItens.getColumn("Quantidade").setCellEditor(decimalEditor);

        tblItens.getColumn("UM").setPreferredWidth(60);

        tblItens.getColumn("Tipo").setPreferredWidth(30);
        tblItens.getColumn("Tipo").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblItens.getColumn("Valor").setPreferredWidth(100);
        tblItens.getColumn("Valor").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        tblItens.getColumn("Valor").setCellEditor(decimalEditor);

        tblItens.getColumn("Acréscimo").setPreferredWidth(90);
        tblItens.getColumn("Acréscimo").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        tblItens.getColumn("Acréscimo").setCellEditor(decimalEditor);

        tblItens.getColumn("Desconto").setPreferredWidth(90);
        tblItens.getColumn("Desconto").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        //tblItens.getColumn("Frete").setPreferredWidth(100);
        //tblItens.getColumn("Frete").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //tblItens.getColumn("Seguro").setPreferredWidth(100);
        //tblItens.getColumn("Seguro").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        tblItens.getColumn("Subtotal").setPreferredWidth(100);
        tblItens.getColumn("Subtotal").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblItens.getColumn("Editar").setPreferredWidth(50);

        if (documentoSaidaItensJTableModel.getRowCount() > 0) {
            tblItens.setRowSelectionInterval(0, 0);
        }
        
        tblItens.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                salvar();
            }

            
        });
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

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "pesquisarPessoa");
        am.put("pesquisarPessoa", new FormKeyStroke("F5"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), "encerrarComanda");
        am.put("encerrarComanda", new FormKeyStroke("F6"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, KeyEvent.CTRL_DOWN_MASK), "transferirComanda");
        am.put("transferirComanda", new FormKeyStroke("CtrlF6"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), "receber");
        am.put("receber", new FormKeyStroke("F7"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0), "parcelar");
        am.put("parcelar", new FormKeyStroke("F8"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, KeyEvent.SHIFT_DOWN_MASK), "exibirRecebimentos");
        am.put("exibirRecebimentos", new FormKeyStroke("ShiftF8"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), "pesquisarProduto");
        am.put("pesquisarProduto", new FormKeyStroke("F9"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), "imprimirA4");
        am.put("imprimirA4", new FormKeyStroke("F10"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "imprimirTermica");
        am.put("imprimirTermica", new FormKeyStroke("F11"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, KeyEvent.CTRL_DOWN_MASK), "imprimirTicketComanda");
        am.put("imprimirTicketComanda", new FormKeyStroke("CtrlF11"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, KeyEvent.SHIFT_DOWN_MASK), "imprimirTicketCozinha");
        am.put("imprimirTicketCozinha", new FormKeyStroke("ShiftF11"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "cfe");
        am.put("cfe", new FormKeyStroke("F12"));
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
                    txtItemCodigo.requestFocus();
                    break;
                case "F3":
                    txtItemQuantidade.requestFocus();
                    break;
                case "F4":
                    pesquisarFuncionario();
                    break;
                case "F5":
                    pesquisarPessoa();
                    break;
                case "F6":
                    encerrarComanda();
                    break;
                case "CtrlF6":
                    transferirComanda();
                    break;
                case "F7":
                    receber();
                    break;
                case "F8":
                    parcelar();
                    break;
                case "ShiftF8":
                    exibirRecebimentos();
                    break;
                case "F9":
                    pesquisarProduto(null, false);
                    break;
                case "F10":
                    imprimirA4();
                    break;
                case "ShiftF10":
                    //escolherImpressao();
                    break;
                case "CtrlF10":
                    imprimirTicketComanda();
                    break;
                case "F11":
                    imprimirTermica();
                    break;
                case "CtrlF11":
                    imprimirTicketComanda();
                    break;
                case "ShiftF11":
                    imprimirTicketCozinha();
                    break;
                case "F12":
                    gerarCupomSat();
                    break;
            }
        }
    }

    public static VendaView getInstance(Venda documento) {
        return getInstance(documento, null, false);
    }

    public static VendaView getInstance(Venda documento, Integer comanda, boolean orcamento) {
        for (VendaView vendaView : vendaViews) {
            if (vendaView.documento.getId() != null && vendaView.documento.getId().equals(documento.getId())) {
                return vendaView;
            }
        }
        if (orcamento) {
            vendaViews.add(new VendaView(documento, true));

        } else {
            vendaViews.add(new VendaView(documento, comanda));

        }

        return vendaViews.get(vendaViews.size() - 1);
    }

    private void carregarFormaPagamento() {
        for (MeioDePagamento mp : new MeioDePagamentoDAO().findAllEnabled()) {
            cboMeioDePagamento.addItem(mp);
        }
    }

    private void fechar() {
        salvar();
        MAIN_VIEW.removeTab(this.getName());
        MAIN_VIEW.removeView(this.getName());
        //if (documento.getVendaTipo().equals(VendaTipo.COMANDA)) {
        //    MAIN_VIEW.addView(ComandasView.getSingleInstance());
        //}
    }

    private void salvar() {
        if(!documento.hasDocumentoPai()) {
            documento.setComandaNome(txtComandaNome.getText());

            documento.setRelato(txtRelato.getText());
            documento.setObservacao(txtObservacao.getText());

            documento.setMeioDePagamento((MeioDePagamento) cboMeioDePagamento.getSelectedItem());
            documento.setValorReceber(Decimal.fromString(txtReceber.getText()));
            documento.setEnderecoEntrega(txtEnderecoEntrega.getText());

            //documento.setVendaStatus();

            documento = vendaDAO.save(documento);

            txtStatus.setText(documento.getVendaStatus().toString());
            txtStatus.setBackground(documento.getVendaStatus().getCor());

            txtDocumentoId.setText(documento.getId().toString());

            configurarTela();
            exibirFuncionario();
            exibirPessoa();
            exibirVeiculo();
            exibirTotais();
            //carregarAcrescimosDescontos();
        }
    }

    private void pesquisarProduto(String buscar, boolean codigoRepetido) {
        ProdutoPesquisaView produtoPesquisaView = new ProdutoPesquisaView(buscar, codigoRepetido);

        produto = produtoPesquisaView.getProduto();
        if (produto != null) {
            if (VENDA_INSERCAO_DIRETA) {
                preCarregarItem();
                inserirItem(Decimal.fromString(txtItemQuantidade.getText()));
            } else {
                preCarregarItem();
            }
        }
    }

    private Produto validarInsercaoItem() {

        txtItemCodigo.setText(txtItemCodigo.getText().trim());
        String codigo = txtItemCodigo.getText();
        BigDecimal quantidade = Decimal.fromString(txtItemQuantidade.getText());

        if (produto != null && produto.getId() != null) {
            return produto;

        } else if (codigo.equals("")) {
            txtItemCodigo.requestFocus();

        } else if (quantidade.compareTo(BigDecimal.ZERO) == 0) {
            txtItemQuantidade.requestFocus();

        } else {
            List<Produto> produtos = produtoDAO.findByCodigo(codigo);

            //códigos de barras gerados para etiquetas comuns (modelo Oficina das Artes)
            if (produtos.isEmpty()) {
                codigo = codigo.replace(" ", ""); //código preenchido com espaços por questão estética
                produtos = produtoDAO.findByCodigo(codigo);
            }

            if (produtos.isEmpty()) {
                if (!codigo.matches("[0-9]*")) {
                    JOptionPane.showMessageDialog(rootPane, "Código não encontrado", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                    txtItemCodigo.setText("");
                    txtItemCodigo.requestFocus();
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
                                txtItemCodigo.setText("");
                                txtItemCodigo.requestFocus();
                            }
                        } else {
                            JOptionPane.showMessageDialog(MAIN_VIEW, "Código não encontrado");
                            txtItemCodigo.setText("");
                            txtItemCodigo.requestFocus();
                        }
                    }
                }

            } else if (produtos.size() > 1) {
                new Toast("Código repetido encontrado. Escolha o produto na lista...");
                pesquisarProduto(codigo, true);
            } else {
                produto = produtos.get(0);
            }
        }

        return produto;
    }

    private void preCarregarItem() {
        txtItemCodigo.setText(produto.getCodigo());
        txtItemDescricao.setText(produto.getNome());
        //txtItemDescricao.setCaretPosition(0);
        txtItemValor.setText(Decimal.toString(produto.getValorVenda()));

        txtItemDescricao.requestFocus();

        //tamanho
        if (!produto.getProdutoTamanhos().isEmpty()) {
            VendaItemSelecionarTamanhoView selecionarTamanho = new VendaItemSelecionarTamanhoView(documento, produto);

            ProdutoTamanho produtoTamanho = selecionarTamanho.getProdutoTamanho();

            /*
            if(produtoTamanho != null) {
                if(produto.isMontavel()) {
                    VendaMontarItemView montarItem = new VendaMontarItemView(documento, produtoTamanho);
                    
                    MovimentoFisico mfMontado = montarItem.getMovimentoFisico();
                }
                
                salvar();
                carregarTabela();
            }*/
            salvar();
            carregarTabela();

            limparCamposDeInsercao();
            txtItemCodigo.requestFocus();
        }

        calcularSubtotal();
    }

    private void inserirItem(BigDecimal quantidade) {
        BigDecimal valorVenda = Decimal.fromString(txtItemValor.getText());

        txtItemDescricao.setText(txtItemDescricao.getText().trim());
        String descricao = txtItemDescricao.getText();

        if (valorVenda.compareTo(BigDecimal.ZERO) == 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Produto com valor igual a zero. Não é possível inserir.", "Erro", JOptionPane.ERROR_MESSAGE);
            //txtCodigo.setText("");
            //txtCodigo.requestFocus();
            if (txtItemValor.isEditable()) {
                txtItemValor.requestFocus();
            }
        } else if (descricao.isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Preencha a descrição do item.", "Erro", JOptionPane.ERROR_MESSAGE);
            txtItemCodigo.requestFocus();

        } else {
            //Se não houver venda, criar nova
            if (documento.getId() == null) {
                salvar();
            }
            //inserir item 

            String codigo = produto.getCodigo();
            BigDecimal descontoPercentualItem = Decimal.fromString(txtItemDescontoPercentual.getText());
            UnidadeComercial unidadeComercialVenda = produto.getUnidadeComercialVenda();
            
            MovimentoFisico movimentoFisico = new MovimentoFisico(produto,
                    codigo,
                    descricao,
                    produto.getProdutoTipo(),
                    BigDecimal.ZERO,
                    quantidade,
                    valorVenda,
                    descontoPercentualItem,
                    unidadeComercialVenda,
                    MovimentoFisicoTipo.VENDA,
                    null,
                    produto.getExTipi(),
                    produto.getNcm(),
                    produto.getCest(),
                    produto.getCfopSaidaDentroDoEstado(), //to do - chavear
                    produto.getEan(),
                    produto.getUnidadeTributavel(),
                    //(produto.getValorTributavel().compareTo(BigDecimal.ZERO) > 0) ? quantidade.multiply(valorVenda).divide(produto.getValorTributavel(), 2, RoundingMode.HALF_UP) : quantidade, //tributável
                    quantidade,
                    produto.getValorTributavel(),
                    produto.getEanTributavel(),
                    BigDecimal.ZERO, //acrescimoMonetario
                    BigDecimal.ZERO, //descontoMonetario
                    BigDecimal.ZERO, //valorFrete
                    BigDecimal.ZERO, //valorSeguro
                    true, //valorCompoeTotal
                    produto.getIcms(),
                    produto.getOrigem(),
                    produto.getModalidadeBcIcms(),
                    produto.getPercentualReducaoBcIcms(),
                    
                    produto.getPis(),
                    produto.getPisTipoCalculo(),
                    produto.getAliquotaPis(),
                    produto.getAliquotaPisReais(),
                    produto.getPisStTipoCalculo(),
                    produto.getAliquotaPisSt(),
                    produto.getAliquotaPisStReais(),

                    produto.getCofins(),
                    produto.getCofinsTipoCalculo(),
                    produto.getAliquotaCofins(),
                    produto.getAliquotaCofinsReais(),
                    produto.getCofinsStTipoCalculo(),
                    produto.getAliquotaCofinsSt(),
                    produto.getAliquotaCofinsStReais()
            
            );
            
            if(Ouroboros.NFE_HABILITAR) {
                movimentoFisico = FiscalUtil.calcularTributos(movimentoFisico);
            }

            /*if (documento.getVendaTipo().equals(VendaTipo.VENDA)
                    || documento.getVendaTipo().equals(VendaTipo.ORDEM_DE_SERVICO)
                    || documento.getVendaTipo().equals(VendaTipo.COMANDA)) {
*/
                LocalDateTime timeStamp = LocalDateTime.now();
                switch(Ouroboros.VENDA_STATUS_INICIAL) {
                    case ENTREGA_CONCLUÍDA:
                        movimentoFisico.setDataSaida(timeStamp);
                    case PREPARAÇÃO_CONCLUÍDA:
                        movimentoFisico.setDataPronto(timeStamp);
                    case ANDAMENTO:
                        movimentoFisico.setDataAndamento(timeStamp);
                }
                
/*            }*/

            movimentoFisico = movimentoFisicoDAO.save(movimentoFisico);

            //2019-07-17 Causava centenas de consultas ao movimentoFisico
            //Aparentemente o estoque está refletindo normalmente mesmo sem isso
            //produto.addMovimentoFisico(movimentoFisico); //2019-06-10 - atualizar estoque
            documento.addMovimentoFisico(movimentoFisico);

            //documento = vendaDAO.save(documento);
            salvar();

            carregarTabela();

            //exibirTotais();
            //carregarAcrescimosDescontos();
            formatarBotoesAcrescimoDesconto();

            limparCamposDeInsercao();

        }

        produto = null;
        //txtItemDescricao.setEditable(false);
    }

    private void limparCamposDeInsercao() {
        txtItemCodigo.setText("");
        txtItemDescricao.setText("");
        txtItemQuantidade.setText("1,000");
        txtItemValor.setText("0,00");
        txtItemDescontoPercentual.setText("0,00");
        txtItemSubtotal.setText("0,00");
        txtItemCodigo.requestFocus();
    }

    private void excluirItemPorNumero() {
        ExcluirItemView excluirItem = new ExcluirItemView();

        if (excluirItem.getNumero() != null) {
            int numero = excluirItem.getNumero() - 1;

            if (numero < tblItens.getRowCount()) {
                tblItens.setRowSelectionInterval(numero, numero);
                removerItem();
            }
        }

    }

    private void removerItem() {
        int index = tblItens.getSelectedRow();
        
        if (index > -1) {
            MovimentoFisico itemExcluir = documentoSaidaItensJTableModel.getRow(index);

            if(itemExcluir.isAgrupado()) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Este item pertence ao documento de id " + itemExcluir.getVenda().getId() + ". Não é possível remove-lo", title, 0);
                
            } else if (itemExcluir.getSubtotal().setScale(2, RoundingMode.HALF_UP).compareTo(documento.getTotalEmAberto()) > 0) {
                //2019-04-05 arredondar para comparar
                JOptionPane.showMessageDialog(rootPane, "Subtotal do item a ser excluído é maior que o total em aberto");

            } else {

                //2019-01-24 venda.setMovimentosFisicos(vendaItens);
                //Marcar item como excluído
                itemExcluir = movimentoFisicoDAO.remove(itemExcluir);

                documento.addMovimentoFisico(itemExcluir);

                //documento = vendaDAO.save(documento);
                salvar();

                carregarTabela();
                //exibirTotais();
                //carregarAcrescimosDescontos();
                formatarBotoesAcrescimoDesconto();

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

    private void calcularSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;

        BigDecimal quantidade = Decimal.fromString(txtItemQuantidade.getText());
        BigDecimal valor = Decimal.fromString(txtItemValor.getText());
        BigDecimal desconto = Decimal.fromString(txtItemDescontoPercentual.getText());

        subtotal = quantidade.multiply(valor.subtract(valor.multiply(desconto).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)));

        txtItemSubtotal.setText(Decimal.toString(subtotal));
    }

    private void calcularSubtotalReverso() {
        BigDecimal quantidade = BigDecimal.ZERO;

        BigDecimal subtotal = Decimal.fromString(txtItemSubtotal.getText());
        BigDecimal valor = Decimal.fromString(txtItemValor.getText());
        BigDecimal desconto = Decimal.fromString(txtItemDescontoPercentual.getText());

        if (desconto.compareTo(BigDecimal.ZERO) > 0) {
            valor = valor.subtract(valor.multiply(desconto).divide(new BigDecimal(100), 10, RoundingMode.HALF_UP));
        }

        quantidade = subtotal.divide(valor, 3, RoundingMode.HALF_UP);

        System.out.println("desconto: " + desconto);
        System.out.println("subtotal: " + subtotal);
        System.out.println("valor: " + valor);
        System.out.println("quantidade: " + quantidade);

        txtItemQuantidade.setText(Decimal.toString(quantidade, 3));
    }

    private void carregarTabela() {
        
        
        documentoSaidaItensJTableModel.clear();
        documentoSaidaItensJTableModel.addList(documento.getMovimentosFisicosSaida());

        if (tblItens.getRowCount() > 0) {
            int index = tblItens.getRowCount() - 1;
            tblItens.setRowSelectionInterval(index, index);

            //rolar para o item (forçar visibilidade)
            tblItens.scrollRectToVisible(tblItens.getCellRect(index, 0, true));
        }
        
        
        lblRegistros.setText("Itens: " + String.valueOf(documento.getMovimentosFisicosSaida().size()));
    }

    private void atualizarTabela() {
        int row = tblItens.getSelectedRow();
        documentoSaidaItensJTableModel.fireTableDataChanged();
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

        txtTotalProdutos.setText(Decimal.toString(documento.getTotalProdutos()));
        txtTotalServicos.setText(Decimal.toString(documento.getTotalServicos()));
        txtTotal.setText(Decimal.toString(documento.getTotal()));

        txtRecebido.setText(Decimal.toString(documento.getTotalRecebidoAVista()));

        txtFaturado.setText(Decimal.toString(documento.getTotalAPrazo()));

        txtEmAberto.setText(Decimal.toString(documento.getTotalEmAberto()));

    }

    private void encerrarComanda() {
        if (documento.getEncerramento() != null) {
            JOptionPane.showMessageDialog(rootPane, "Este documento já foi encerrado.");
        } else if (documento.getId() == null) {
            MAIN_VIEW.removeTab(this.getName());
            MAIN_VIEW.removeView(this.getName());
        } else if (documento.getTotalEmAberto().compareTo(BigDecimal.ZERO) > 0) {
            JOptionPane.showMessageDialog(rootPane, "Ainda há valor em aberto. Não é possível encerrar.");
        } else {
            documento.setEncerramento(DateTime.getNow());
            documento = vendaDAO.save(documento);
            MAIN_VIEW.removeTab(this.getName());
            MAIN_VIEW.removeView(this.getName());
        }
    }

    private void receber() {
        if (documento.isOrcamento()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não é possível receber em ORÇAMENTO.", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            Caixa lastCaixa = new CaixaDAO().getLastCaixa();
            if (lastCaixa == null || lastCaixa.getEncerramento() != null) {
                JOptionPane.showMessageDialog(rootPane, "Não há turno de caixa aberto. Não é possível realizar recebimentos.", "Atenção", JOptionPane.WARNING_MESSAGE);
            } else if (documento.getTotalEmAberto().compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(rootPane, "Não há valor em aberto.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                validarCredito(false);
                RecebimentoView recebimentoView = new RecebimentoView(documento);
                exibirTotais();
            }
        }
    }

    private void parcelar() {
        if (validarCredito(true)) {
            ParcelamentoView parcelamentoView = new ParcelamentoView(documento);
            exibirTotais();
            exibirPessoa();
        }
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

    private void imprimirTicketComanda() {
        salvar();

        String pdfFilePath = TO_PRINTER_PATH + "TICKET_COMANDA " + documento.getId() + "_" + System.currentTimeMillis() + ".pdf";
        TermicaPrint.gerarTicketComanda(documento, pdfFilePath);

        PrintPDFBox pPDF = new PrintPDFBox();
        pPDF.print(pdfFilePath, IMPRESSORA_CUPOM);
    }

    private void imprimirTicketCozinha() {
        salvar();

        TicketCozinhaPrint.imprimirCupom(documento);
    }

    private void agruparDocumentos() {
        if (documento.getPessoa() == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione um cliente antes de agrupar documentos", "Atenção", JOptionPane.WARNING_MESSAGE);
            
        } else {
            DocumentoSaidaPesquisaView docPesquisa = new DocumentoSaidaPesquisaView(documento);
            
            if(!docPesquisa.getDocumentos().isEmpty()) {
            
                for(Venda doc : docPesquisa.getDocumentos()) {
                    documento.addDocumentoFilho(doc);
                    //salvar();
                    //vendaDAO.save(doc);
                }
            }
                //documento.getMovimentosFisicos();

                salvar();

                /*for(Venda doc : docPesquisa.getDocumentos()) {
                    doc.setDocumentoPai(documento);
                }*/

                carregarTabela();
            
        }
        
    }
    
    private void detalheNfe() {
        NfeDetalheView nfeDetalheView = new NfeDetalheView(documento);
        if (nfeDetalheView.getSalvar()) {
            /////salvar();
        }
    }

    private void gerarCupomSat() {
        if (validarCupomSat()) {
            SatEmitirCupomView satCpf = new SatEmitirCupomView(documento);

        }
    }

    private boolean validarCupomSat() {
        List<String> erros = MwSat.validar(documento);

        if (!erros.isEmpty()) {
            String mensagem = String.join("\r\n", erros);
            JOptionPane.showMessageDialog(MAIN_VIEW, mensagem, "Erro ao validar o documento. Verifique os erros:", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void exibirRecebimentos() {
        if (documento == null || documento.getRecebimentos().isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, "Não há recebimentos", "Atenção", JOptionPane.INFORMATION_MESSAGE);
        } else {
            RecebimentoListaView recebimentoListaView = new RecebimentoListaView(MAIN_VIEW, true, documento);
            recebimentoListaView.setLocationRelativeTo(this);
            recebimentoListaView.setVisible(true);
        }
    }

    private void transferirComanda() {
        TransferirComandaView transferirComandaView = new TransferirComandaView(MAIN_VIEW, documento);
        //em.refresh(venda);
        comanda = documento.getComanda();
        txtTipo.setText("COMANDA " + comanda);
    }

    private void entrega() {
        EntregaDevolucaoView entregaDevolucaoView = new EntregaDevolucaoView(documento);
    }

    private void cancelarDocumentp() {
        if (documento.getId() == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Documento vazio. Não é possível cancelar.", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {
            CancelarDocumentoView cancelarVenda = new CancelarDocumentoView(documento);
            configurarTela();

        }
    }
    
    private void informacoes() {
        new InformacoesDocumentoView(documento);
    }
    

    private void aceitarOrcamento() {
        int resposta = JOptionPane.showConfirmDialog(MAIN_VIEW, "Aceitar orçamento? Este procedimento é irreversível.", "Atenção", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resposta == JOptionPane.OK_OPTION) {
            documento.setOrcamento(false);
            //documento = vendaDAO.save(documento);
            //configurarTela();
            salvar();
        }
    }

    private void pesquisarPessoa() {
        PessoaPesquisaView pesquisa = new PessoaPesquisaView(PessoaTipo.CLIENTE);

        if (pesquisa.getPessoa() != null) {
            documento.setPessoa(pesquisa.getPessoa());
            txtEnderecoEntrega.setText(documento.getPessoa().getEnderecoCompleto()); //Delivery
            txtEnderecoEntrega.setCaretPosition(0);
            salvar();
        }
    }

    private void exibirPessoa() {
        if (documento.getPessoa() != null) {
            txtPessoaNome.setText(documento.getPessoa().getId() + " - " + documento.getPessoa().getNome());
            txtPessoaNome.setCaretPosition(0);
            txtPessoaTelefone.setText(documento.getPessoa().getTelefone1());

        } else {
            txtPessoaNome.setText("NÃO INFORMADO");
            txtPessoaTelefone.setText("");
        }
    }

    private void removerPessoa() {
        if (documento.getTotalRecebidoAPrazo().compareTo(BigDecimal.ZERO) > 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Já existem parcelas recebidas. Não é possível remover o cliente.", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else if (!documento.getParcelasAPrazo().isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Já existe valor faturado. Não é possível remover o cliente.", "Atenção", JOptionPane.WARNING_MESSAGE);

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

    private void pesquisarVeiculo() {
        VeiculoPesquisaView pesquisa = new VeiculoPesquisaView();

        if (pesquisa.getVeiculo() != null) {
            documento.setVeiculo(pesquisa.getVeiculo());
            salvar();
        }
    }

    private void exibirVeiculo() {
        if (documento.getVeiculo() != null) {
            txtVeiculo.setText(documento.getVeiculo().getPlaca() + " - " + documento.getVeiculo().getModelo());

        } else {
            txtVeiculo.setText("NÃO INFORMADO");

        }
    }

    private void removerVeiculo() {
        if (documento.getTotalRecebidoAPrazo().compareTo(BigDecimal.ZERO) > 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Já existem parcelas recebidas. Não é possível remover o veiculo.", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else if (!documento.getParcelasAPrazo().isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Já existe valor faturado. Não é possível remover o veiculo.", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {
            documento.setVeiculo(null);
            salvar();
        }
    }

    private void requisicaoMaterial() {
        if (documento.getPessoa() == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione uma pessoa antes de gerar", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            RelatorioPdf.gerarRequisicaoMaterial(documento);
        }

    }

    private void gerarCarne() {
        if (documento.getParcelasAPrazo().isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem parcelas para gerar carnê", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            Carne.gerarCarne(documento.getParcelasAPrazo());
        }
    }

    private void promissoria() {
        if (documento.getParcelasAPrazo().isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem parcelas para gerar promissória", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            Promissoria.gerar(documento.getParcelasAPrazo());
        }
    }

    public boolean validarCredito(boolean bloquear) {
        Pessoa pessoa = documento.getPessoa();

        if (pessoa != null) {

            BigDecimal limiteCredito = pessoa.getLimiteCredito();
            BigDecimal totalExcedido = (pessoa.getTotalComprometido().add(documento.getTotal())).subtract(limiteCredito);

            if (totalExcedido.compareTo(BigDecimal.ZERO) > 0) {
                String msg = "Limite de crédito: " + Decimal.toString(limiteCredito)
                        + "\nTotal em aberto + vencido: " + Decimal.toString(pessoa.getTotalComprometido())
                        + "\nTotal deste documento: " + Decimal.toString(documento.getTotal())
                        + "\nValor excedido: " + Decimal.toString(totalExcedido);

                JOptionPane.showMessageDialog(MAIN_VIEW, msg, "Atenção", JOptionPane.WARNING_MESSAGE);
                if (bloquear && Ouroboros.VENDA_BLOQUEAR_CREDITO_EXCEDIDO) {
                    return UsuarioDAO.validarAdministradorComLogin();

                }

            }

            BigDecimal totalAtrasado = pessoa.getTotalEmAtraso();

            if (totalAtrasado.compareTo(BigDecimal.ZERO) > 0) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Cliente com valor em atraso: " + Decimal.toString(totalAtrasado), "Atenção", JOptionPane.WARNING_MESSAGE);
                if (bloquear && Ouroboros.VENDA_BLOQUEAR_PARCELAS_EM_ATRASO) {
                    return UsuarioDAO.validarAdministradorComLogin();
                }

            }
        }

        return true;
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

    private void processo() {
        //VendaProcessoView p = new VendaProcessoView(documento.getMovimentosFisicos());
        //ConfirmarEntregaView confirmarEntregaView = new ConfirmarEntregaView(documento);
        new DocumentoStatusView(documento);
        salvar();
    }

    private void calcularTroco() {
        BigDecimal valorReceber = Decimal.fromString(txtReceber.getText());

        BigDecimal valorTroco = valorReceber.subtract(documento.getTotal());

        valorTroco = valorTroco.compareTo(BigDecimal.ZERO) > 0 ? valorTroco : BigDecimal.ZERO;

        txtTroco.setText(Decimal.toString(valorTroco));
    }

    private void tblClick() {
        if (tblItens.getSelectedColumn() == 11) {
            editarItem();
            documentoSaidaItensJTableModel.fireTableRowsUpdated(tblItens.getSelectedRow(), tblItens.getSelectedRow());
            carregarAcrescimosDescontos();
            exibirTotais();
        } else {
            txtItemCodigo.requestFocus();
        }
    }
    
    private void editarItem() {
        if (tblItens.getSelectedRow() > -1 && Ouroboros.NFE_HABILITAR) {
            MovimentoFisico mf = documentoSaidaItensJTableModel.getRow(tblItens.getSelectedRow());
            VendaItemView vendaItemView = new VendaItemView(mf);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlInserirProduto = new javax.swing.JPanel();
        txtItemCodigo = new javax.swing.JTextField();
        txtItemQuantidade = new javax.swing.JFormattedTextField();
        btnInserir = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtItemValor = new javax.swing.JFormattedTextField();
        jLabel15 = new javax.swing.JLabel();
        txtItemDescontoPercentual = new javax.swing.JFormattedTextField();
        jLabel18 = new javax.swing.JLabel();
        txtItemDescricao = new javax.swing.JTextField();
        btnPesquisar = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtItemSubtotal = new javax.swing.JFormattedTextField();
        jLabel19 = new javax.swing.JLabel();
        pnlPessoas = new javax.swing.JPanel();
        txtPessoaNome = new javax.swing.JTextField();
        btnCliente = new javax.swing.JButton();
        btnRemoverCliente = new javax.swing.JButton();
        txtPessoaTelefone = new javax.swing.JTextField();
        btnFuncionario = new javax.swing.JButton();
        btnRemoverFuncionario = new javax.swing.JButton();
        txtFuncionario = new javax.swing.JTextField();
        txtVeiculo = new javax.swing.JTextField();
        btnVeiculo = new javax.swing.JButton();
        btnRemoverVeiculo = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblItens = new javax.swing.JTable();
        pnlRelato = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtRelato = new javax.swing.JTextArea();
        jLabel35 = new javax.swing.JLabel();
        pnlObservacao = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtObservacao = new javax.swing.JTextArea();
        jLabel37 = new javax.swing.JLabel();
        pnlSuperior = new javax.swing.JPanel();
        txtTipo = new javax.swing.JTextField();
        txtStatus = new javax.swing.JTextField();
        btnAceitarOrcamento = new javax.swing.JButton();
        btnCancelarDocumento = new javax.swing.JButton();
        txtDocumentoId = new javax.swing.JTextField();
        btnEncerrarVenda = new javax.swing.JButton();
        btnTransferirComanda = new javax.swing.JButton();
        btnImprimirA4 = new javax.swing.JButton();
        btnImprimirTermica = new javax.swing.JButton();
        btnImprimirTicketComanda = new javax.swing.JButton();
        btnImprimirTicketCozinha = new javax.swing.JButton();
        btnImprimirSat = new javax.swing.JButton();
        btnImprimirCarne = new javax.swing.JButton();
        btnProcesso = new javax.swing.JButton();
        btnNFe = new javax.swing.JButton();
        txtComandaNome = new javax.swing.JTextField();
        btnAgruparDocumentos = new javax.swing.JButton();
        btnInfo = new javax.swing.JButton();
        pnlTotais = new javax.swing.JPanel();
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
        pnlRecebimento = new javax.swing.JPanel();
        btnReceber = new javax.swing.JButton();
        txtRecebido = new javax.swing.JFormattedTextField();
        btnReceber1 = new javax.swing.JButton();
        txtFaturado = new javax.swing.JFormattedTextField();
        pnlEmAberto = new javax.swing.JPanel();
        txtEmAberto = new javax.swing.JFormattedTextField();
        jLabel38 = new javax.swing.JLabel();
        pnlDeliveryRecebimento = new javax.swing.JPanel();
        txtReceber = new javax.swing.JFormattedTextField();
        jLabel17 = new javax.swing.JLabel();
        cboMeioDePagamento = new javax.swing.JComboBox<>();
        txtTroco = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txtEnderecoEntrega = new javax.swing.JTextField();
        lblMensagem = new javax.swing.JLabel();
        lblRegistros = new javax.swing.JLabel();

        setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setResizable(true);
        setTitle("Doc.Saída");
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

        pnlInserirProduto.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtItemCodigo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtItemCodigo.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtItemCodigo.setToolTipText("F9 PARA PESQUISAR");
        txtItemCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtItemCodigoActionPerformed(evt);
            }
        });
        txtItemCodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtItemCodigoKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtItemCodigoKeyReleased(evt);
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

        txtItemValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtItemValor.setText("0,00");
        txtItemValor.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtItemValor.setName("decimal"); // NOI18N
        txtItemValor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtItemValorKeyReleased(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel15.setText("VALOR");

        txtItemDescontoPercentual.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtItemDescontoPercentual.setText("0,00");
        txtItemDescontoPercentual.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtItemDescontoPercentual.setName("decimal"); // NOI18N
        txtItemDescontoPercentual.setPreferredSize(new java.awt.Dimension(80, 28));
        txtItemDescontoPercentual.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtItemDescontoPercentualKeyReleased(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel18.setText("DESC. %");

        txtItemDescricao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtItemDescricao.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtItemDescricaoKeyReleased(evt);
            }
        });

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

        txtItemSubtotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtItemSubtotal.setText("0,00");
        txtItemSubtotal.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtItemSubtotal.setName("decimal"); // NOI18N
        txtItemSubtotal.setPreferredSize(new java.awt.Dimension(100, 28));
        txtItemSubtotal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtItemSubtotalFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtItemSubtotalFocusLost(evt);
            }
        });
        txtItemSubtotal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtItemSubtotalKeyReleased(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel19.setText("SUBTOTAL");

        javax.swing.GroupLayout pnlInserirProdutoLayout = new javax.swing.GroupLayout(pnlInserirProduto);
        pnlInserirProduto.setLayout(pnlInserirProdutoLayout);
        pnlInserirProdutoLayout.setHorizontalGroup(
            pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInserirProdutoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(txtItemCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtItemDescricao)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtItemQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(txtItemValor, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtItemDescontoPercentual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtItemSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addGap(18, 18, 18)
                .addComponent(btnInserir, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                            .addComponent(jLabel18)
                            .addComponent(jLabel19))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlInserirProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtItemCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtItemQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtItemValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtItemDescontoPercentual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtItemDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtItemSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(btnPesquisar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pnlPessoas.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtPessoaNome.setEditable(false);
        txtPessoaNome.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPessoaNome.setText("NÃO INFORMADO");
        txtPessoaNome.setFocusable(false);

        btnCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/user.png"))); // NOI18N
        btnCliente.setText("F5 CLIENTE");
        btnCliente.setContentAreaFilled(false);
        btnCliente.setFocusable(false);
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
        btnRemoverCliente.setFocusable(false);
        btnRemoverCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverClienteActionPerformed(evt);
            }
        });

        txtPessoaTelefone.setEditable(false);
        txtPessoaTelefone.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPessoaTelefone.setFocusable(false);

        btnFuncionario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/user_red.png"))); // NOI18N
        btnFuncionario.setText("F4 FUNCIONÁRIO");
        btnFuncionario.setContentAreaFilled(false);
        btnFuncionario.setFocusable(false);
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
        btnRemoverFuncionario.setFocusable(false);
        btnRemoverFuncionario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverFuncionarioActionPerformed(evt);
            }
        });

        txtFuncionario.setEditable(false);
        txtFuncionario.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtFuncionario.setText("NÃO INFORMADO");
        txtFuncionario.setFocusable(false);

        txtVeiculo.setEditable(false);
        txtVeiculo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtVeiculo.setText("NÃO INFORMADO");
        txtVeiculo.setFocusable(false);

        btnVeiculo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-car-20.png"))); // NOI18N
        btnVeiculo.setText("VEÍCULO");
        btnVeiculo.setContentAreaFilled(false);
        btnVeiculo.setFocusable(false);
        btnVeiculo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnVeiculo.setIconTextGap(10);
        btnVeiculo.setPreferredSize(new java.awt.Dimension(180, 49));
        btnVeiculo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnVeiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVeiculoActionPerformed(evt);
            }
        });

        btnRemoverVeiculo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-close-button-20.png"))); // NOI18N
        btnRemoverVeiculo.setToolTipText("Remover Veículo");
        btnRemoverVeiculo.setContentAreaFilled(false);
        btnRemoverVeiculo.setFocusable(false);
        btnRemoverVeiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverVeiculoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlPessoasLayout = new javax.swing.GroupLayout(pnlPessoas);
        pnlPessoas.setLayout(pnlPessoasLayout);
        pnlPessoasLayout.setHorizontalGroup(
            pnlPessoasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPessoasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtFuncionario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRemoverFuncionario)
                .addGap(18, 18, 18)
                .addGroup(pnlPessoasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPessoasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPessoasLayout.createSequentialGroup()
                        .addComponent(txtPessoaNome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPessoaTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverCliente))
                    .addGroup(pnlPessoasLayout.createSequentialGroup()
                        .addComponent(txtVeiculo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverVeiculo)))
                .addContainerGap())
        );
        pnlPessoasLayout.setVerticalGroup(
            pnlPessoasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPessoasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPessoasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtFuncionario)
                    .addComponent(btnRemoverCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRemoverFuncionario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtPessoaNome)
                    .addComponent(btnCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtPessoaTelefone))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(pnlPessoasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtVeiculo)
                    .addComponent(btnRemoverVeiculo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblItens.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tblItens.setIntercellSpacing(new java.awt.Dimension(10, 10));
        tblItens.setRowHeight(24);
        tblItens.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblItensFocusGained(evt);
            }
        });
        tblItens.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblItensMouseClicked(evt);
            }
        });
        tblItens.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tblItensPropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(tblItens);

        pnlRelato.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtRelato.setColumns(20);
        txtRelato.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtRelato.setLineWrap(true);
        txtRelato.setRows(5);
        txtRelato.setMargin(new java.awt.Insets(4, 4, 4, 4));
        txtRelato.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRelatoFocusLost(evt);
            }
        });
        txtRelato.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtRelatoPropertyChange(evt);
            }
        });
        jScrollPane4.setViewportView(txtRelato);

        jLabel35.setBackground(new java.awt.Color(122, 138, 153));
        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel35.setForeground(java.awt.Color.white);
        jLabel35.setText("Relato / Solicitação do Cliente");
        jLabel35.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        jLabel35.setOpaque(true);

        javax.swing.GroupLayout pnlRelatoLayout = new javax.swing.GroupLayout(pnlRelato);
        pnlRelato.setLayout(pnlRelatoLayout);
        pnlRelatoLayout.setHorizontalGroup(
            pnlRelatoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
            .addGroup(pnlRelatoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlRelatoLayout.setVerticalGroup(
            pnlRelatoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRelatoLayout.createSequentialGroup()
                .addComponent(jLabel35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
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
        jLabel37.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        jLabel37.setOpaque(true);

        javax.swing.GroupLayout pnlObservacaoLayout = new javax.swing.GroupLayout(pnlObservacao);
        pnlObservacao.setLayout(pnlObservacaoLayout);
        pnlObservacaoLayout.setHorizontalGroup(
            pnlObservacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
            .addGroup(pnlObservacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
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

        pnlSuperior.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtTipo.setEditable(false);
        txtTipo.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtTipo.setForeground(java.awt.Color.red);
        txtTipo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtTipo.setText("COM. 3");

        txtStatus.setEditable(false);
        txtStatus.setBackground(java.awt.Color.orange);
        txtStatus.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtStatus.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtStatus.setText("ORÇAMENTO");

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

        btnEncerrarVenda.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnEncerrarVenda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-finish-flag-20.png"))); // NOI18N
        btnEncerrarVenda.setToolTipText("F6 ENCERRAR COMANDA");
        btnEncerrarVenda.setContentAreaFilled(false);
        btnEncerrarVenda.setIconTextGap(10);
        btnEncerrarVenda.setPreferredSize(new java.awt.Dimension(180, 49));
        btnEncerrarVenda.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEncerrarVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEncerrarVendaActionPerformed(evt);
            }
        });

        btnTransferirComanda.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnTransferirComanda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-numbers-20.png"))); // NOI18N
        btnTransferirComanda.setToolTipText("CTRL+F6 TRANSFERIR COMANDA");
        btnTransferirComanda.setContentAreaFilled(false);
        btnTransferirComanda.setIconTextGap(10);
        btnTransferirComanda.setPreferredSize(new java.awt.Dimension(180, 49));
        btnTransferirComanda.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnTransferirComanda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransferirComandaActionPerformed(evt);
            }
        });

        btnImprimirA4.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnImprimirA4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-document-20.png"))); // NOI18N
        btnImprimirA4.setToolTipText("F10 IMPRIMIR A4");
        btnImprimirA4.setContentAreaFilled(false);
        btnImprimirA4.setIconTextGap(10);
        btnImprimirA4.setPreferredSize(new java.awt.Dimension(180, 49));
        btnImprimirA4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimirA4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirA4ActionPerformed(evt);
            }
        });

        btnImprimirTermica.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnImprimirTermica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-magical-scroll-20.png"))); // NOI18N
        btnImprimirTermica.setToolTipText("F11 IMPRIMIR TÉRMICA");
        btnImprimirTermica.setContentAreaFilled(false);
        btnImprimirTermica.setIconTextGap(10);
        btnImprimirTermica.setPreferredSize(new java.awt.Dimension(180, 49));
        btnImprimirTermica.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimirTermica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirTermicaActionPerformed(evt);
            }
        });

        btnImprimirTicketComanda.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnImprimirTicketComanda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-ticket-20.png"))); // NOI18N
        btnImprimirTicketComanda.setToolTipText("CTRL+F11 IMPRIMIR TICKET COMANDA");
        btnImprimirTicketComanda.setContentAreaFilled(false);
        btnImprimirTicketComanda.setIconTextGap(10);
        btnImprimirTicketComanda.setPreferredSize(new java.awt.Dimension(180, 49));
        btnImprimirTicketComanda.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimirTicketComanda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirTicketComandaActionPerformed(evt);
            }
        });

        btnImprimirTicketCozinha.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnImprimirTicketCozinha.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-gas-industry-20.png"))); // NOI18N
        btnImprimirTicketCozinha.setToolTipText("SHIFT+F11 IMPRIMIR TICKET COZINHA");
        btnImprimirTicketCozinha.setContentAreaFilled(false);
        btnImprimirTicketCozinha.setIconTextGap(10);
        btnImprimirTicketCozinha.setPreferredSize(new java.awt.Dimension(180, 49));
        btnImprimirTicketCozinha.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimirTicketCozinha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirTicketCozinhaActionPerformed(evt);
            }
        });

        btnImprimirSat.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnImprimirSat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-bill-20.png"))); // NOI18N
        btnImprimirSat.setToolTipText("F12 IMPRIMIR CUPOM SAT");
        btnImprimirSat.setContentAreaFilled(false);
        btnImprimirSat.setIconTextGap(10);
        btnImprimirSat.setPreferredSize(new java.awt.Dimension(180, 49));
        btnImprimirSat.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimirSat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirSatActionPerformed(evt);
            }
        });

        btnImprimirCarne.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnImprimirCarne.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-paycheque-20.png"))); // NOI18N
        btnImprimirCarne.setToolTipText("IMPRIMIR CARNÊ");
        btnImprimirCarne.setContentAreaFilled(false);
        btnImprimirCarne.setIconTextGap(10);
        btnImprimirCarne.setPreferredSize(new java.awt.Dimension(180, 49));
        btnImprimirCarne.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimirCarne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirCarneActionPerformed(evt);
            }
        });

        btnProcesso.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnProcesso.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-timer-20.png"))); // NOI18N
        btnProcesso.setToolTipText("CONFIRMAR ENTREGA");
        btnProcesso.setContentAreaFilled(false);
        btnProcesso.setIconTextGap(10);
        btnProcesso.setPreferredSize(new java.awt.Dimension(180, 49));
        btnProcesso.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnProcesso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcessoActionPerformed(evt);
            }
        });

        btnNFe.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnNFe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-nota-fiscal-eletronica-20.png"))); // NOI18N
        btnNFe.setToolTipText("NFe");
        btnNFe.setContentAreaFilled(false);
        btnNFe.setIconTextGap(10);
        btnNFe.setPreferredSize(new java.awt.Dimension(180, 49));
        btnNFe.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNFe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNFeActionPerformed(evt);
            }
        });

        txtComandaNome.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtComandaNome.setForeground(java.awt.Color.red);
        txtComandaNome.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtComandaNome.setToolTipText("NOME PARA IDENTIFICAÇÃO");
        txtComandaNome.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtComandaNomeFocusLost(evt);
            }
        });

        btnAgruparDocumentos.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnAgruparDocumentos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-merge-files-20.png"))); // NOI18N
        btnAgruparDocumentos.setToolTipText("Agrupar Documentos");
        btnAgruparDocumentos.setContentAreaFilled(false);
        btnAgruparDocumentos.setIconTextGap(10);
        btnAgruparDocumentos.setPreferredSize(new java.awt.Dimension(180, 49));
        btnAgruparDocumentos.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAgruparDocumentos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgruparDocumentosActionPerformed(evt);
            }
        });

        btnInfo.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-info-20.png"))); // NOI18N
        btnInfo.setToolTipText("INFORMAÇÕES DO DOCUMENTO");
        btnInfo.setContentAreaFilled(false);
        btnInfo.setIconTextGap(10);
        btnInfo.setPreferredSize(new java.awt.Dimension(180, 49));
        btnInfo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInfoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSuperiorLayout = new javax.swing.GroupLayout(pnlSuperior);
        pnlSuperior.setLayout(pnlSuperiorLayout);
        pnlSuperiorLayout.setHorizontalGroup(
            pnlSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSuperiorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAceitarOrcamento, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnProcesso, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDocumentoId, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelarDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTipo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtComandaNome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEncerrarVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTransferirComanda, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnImprimirA4, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAgruparDocumentos, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNFe, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnImprimirTermica, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnImprimirCarne, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnImprimirSat, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnImprimirTicketComanda, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnImprimirTicketCozinha, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlSuperiorLayout.setVerticalGroup(
            pnlSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSuperiorLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtStatus)
                    .addComponent(btnAceitarOrcamento, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtDocumentoId)
                    .addComponent(btnCancelarDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtComandaNome)
                    .addComponent(txtTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEncerrarVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnTransferirComanda, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnImprimirA4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnImprimirTermica, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnImprimirTicketComanda, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnImprimirTicketCozinha, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnImprimirSat, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnImprimirCarne, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnProcesso, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnNFe, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnAgruparDocumentos, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        pnlTotais.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtTotalItensProdutos.setEditable(false);
        txtTotalItensProdutos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalItensProdutos.setText("0,00");
        txtTotalItensProdutos.setFocusable(false);
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

        javax.swing.GroupLayout pnlTotaisLayout = new javax.swing.GroupLayout(pnlTotais);
        pnlTotais.setLayout(pnlTotaisLayout);
        pnlTotaisLayout.setHorizontalGroup(
            pnlTotaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTotaisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTotaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlTotaisLayout.createSequentialGroup()
                        .addGroup(pnlTotaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlTotaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTotalItensProdutos)
                            .addComponent(txtTotalItensServicos, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlTotaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlTotaisLayout.createSequentialGroup()
                        .addGroup(pnlTotaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnAcrescimoProdutosTipo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnAcrescimoServicosTipo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlTotaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtAcrescimoProdutos, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                            .addComponent(txtAcrescimoServicos)))
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlTotaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlTotaisLayout.createSequentialGroup()
                        .addComponent(btnDescontoServicosTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDescontoServicos))
                    .addGroup(pnlTotaisLayout.createSequentialGroup()
                        .addComponent(btnDescontoProdutosTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDescontoProdutos))
                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlTotaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTotalServicos, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTotalProdutos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlTotaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTotal)
                    .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlTotaisLayout.setVerticalGroup(
            pnlTotaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTotaisLayout.createSequentialGroup()
                .addGroup(pnlTotaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTotaisLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(pnlTotaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTotalItensProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTotaisLayout.createSequentialGroup()
                        .addGroup(pnlTotaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlTotaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel22)
                                .addComponent(jLabel23)
                                .addComponent(jLabel40)
                                .addComponent(jLabel16))
                            .addComponent(jLabel39))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlTotaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlTotaisLayout.createSequentialGroup()
                                .addGroup(pnlTotaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnAcrescimoProdutosTipo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtAcrescimoProdutos)
                                    .addComponent(btnDescontoProdutosTipo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtDescontoProdutos)
                                    .addComponent(txtTotalProdutos))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(pnlTotaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtTotalItensServicos)
                                    .addComponent(btnDescontoServicosTipo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtAcrescimoServicos)
                                    .addComponent(txtDescontoServicos)
                                    .addComponent(txtTotalServicos)
                                    .addComponent(btnAcrescimoServicosTipo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        pnlRecebimento.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnReceber.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnReceber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-note-and-coin-20.png"))); // NOI18N
        btnReceber.setText("F7 RECEBIMENTO");
        btnReceber.setContentAreaFilled(false);
        btnReceber.setFocusable(false);
        btnReceber.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnReceber.setIconTextGap(10);
        btnReceber.setPreferredSize(new java.awt.Dimension(180, 49));
        btnReceber.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnReceber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReceberActionPerformed(evt);
            }
        });

        txtRecebido.setEditable(false);
        txtRecebido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecebido.setText("0,00");
        txtRecebido.setFocusable(false);
        txtRecebido.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        btnReceber1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnReceber1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-boleto-bankario-20.png"))); // NOI18N
        btnReceber1.setText("F8 FATURAMENTO");
        btnReceber1.setContentAreaFilled(false);
        btnReceber1.setFocusable(false);
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
        txtFaturado.setFocusable(false);
        txtFaturado.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        javax.swing.GroupLayout pnlRecebimentoLayout = new javax.swing.GroupLayout(pnlRecebimento);
        pnlRecebimento.setLayout(pnlRecebimentoLayout);
        pnlRecebimentoLayout.setHorizontalGroup(
            pnlRecebimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRecebimentoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlRecebimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnReceber1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnReceber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(pnlRecebimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtRecebido, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                    .addComponent(txtFaturado))
                .addContainerGap())
        );
        pnlRecebimentoLayout.setVerticalGroup(
            pnlRecebimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRecebimentoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRecebimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReceber, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlRecebimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnReceber1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFaturado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnlEmAberto.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtEmAberto.setEditable(false);
        txtEmAberto.setForeground(java.awt.Color.red);
        txtEmAberto.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtEmAberto.setText("0,00");
        txtEmAberto.setFocusable(false);
        txtEmAberto.setFont(new java.awt.Font("Tahoma", 1, 42)); // NOI18N

        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("EM ABERTO");
        jLabel38.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel38.setOpaque(true);

        javax.swing.GroupLayout pnlEmAbertoLayout = new javax.swing.GroupLayout(pnlEmAberto);
        pnlEmAberto.setLayout(pnlEmAbertoLayout);
        pnlEmAbertoLayout.setHorizontalGroup(
            pnlEmAbertoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEmAbertoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlEmAbertoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtEmAberto))
                .addContainerGap())
        );
        pnlEmAbertoLayout.setVerticalGroup(
            pnlEmAbertoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEmAbertoLayout.createSequentialGroup()
                .addComponent(jLabel38)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmAberto)
                .addContainerGap())
        );

        pnlDeliveryRecebimento.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtReceber.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtReceber.setText("0,00");
        txtReceber.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtReceber.setName("decimal"); // NOI18N
        txtReceber.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtReceberFocusLost(evt);
            }
        });
        txtReceber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtReceberActionPerformed(evt);
            }
        });
        txtReceber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtReceberKeyReleased(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel17.setText("Troco");

        cboMeioDePagamento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboMeioDePagamento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cboMeioDePagamentoFocusLost(evt);
            }
        });

        txtTroco.setEditable(false);
        txtTroco.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTroco.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTroco.setText("0,00");
        txtTroco.setToolTipText("F9 PARA PESQUISAR");
        txtTroco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTrocoActionPerformed(evt);
            }
        });
        txtTroco.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTrocoKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTrocoKeyReleased(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel20.setText("Receber");

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel24.setText("Endereço");

        txtEnderecoEntrega.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtEnderecoEntrega.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtEnderecoEntrega.setToolTipText("F9 PARA PESQUISAR");
        txtEnderecoEntrega.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtEnderecoEntregaFocusLost(evt);
            }
        });
        txtEnderecoEntrega.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEnderecoEntregaActionPerformed(evt);
            }
        });
        txtEnderecoEntrega.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtEnderecoEntregaKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtEnderecoEntregaKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout pnlDeliveryRecebimentoLayout = new javax.swing.GroupLayout(pnlDeliveryRecebimento);
        pnlDeliveryRecebimento.setLayout(pnlDeliveryRecebimentoLayout);
        pnlDeliveryRecebimentoLayout.setHorizontalGroup(
            pnlDeliveryRecebimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDeliveryRecebimentoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDeliveryRecebimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDeliveryRecebimentoLayout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtReceber, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboMeioDePagamento, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTroco, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlDeliveryRecebimentoLayout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtEnderecoEntrega)))
                .addContainerGap())
        );
        pnlDeliveryRecebimentoLayout.setVerticalGroup(
            pnlDeliveryRecebimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDeliveryRecebimentoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDeliveryRecebimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboMeioDePagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtReceber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel17)
                    .addComponent(txtTroco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlDeliveryRecebimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(txtEnderecoEntrega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblMensagem.setText("Consulta realizada em Xms");

        lblRegistros.setText("Registros exibidos:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlSuperior, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlInserirProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlRelato, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlObservacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlDeliveryRecebimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlRecebimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlPessoas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlTotais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlEmAberto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblMensagem)
                        .addGap(18, 18, 18)
                        .addComponent(lblRegistros)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMensagem)
                    .addComponent(lblRegistros))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlSuperior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlInserirProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPessoas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlRecebimento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlObservacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlRelato, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDeliveryRecebimento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlEmAberto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlTotais, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        setBounds(5, 25, 1200, 775);
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown

        txtItemCodigo.requestFocus();

        txtItemQuantidade.setText("1,000");
        txtItemValor.setText("0,00");
        txtItemDescontoPercentual.setText("0,00");
        txtItemSubtotal.setText("0,00");
    }//GEN-LAST:event_formComponentShown


    private void btnInserirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInserirActionPerformed
        produto = validarInsercaoItem();
        if (produto != null) {
            inserirItem(Decimal.fromString(txtItemQuantidade.getText()));
        }
    }//GEN-LAST:event_btnInserirActionPerformed

    private void txtItemCodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtItemCodigoKeyReleased
        int index;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                if (!txtItemCodigo.getText().trim().equals("")) {
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
                removerItem();
                break;

            //Atalhos modo balcão (uso apenas do teclado numérico)
            case KeyEvent.VK_DIVIDE: // barra do teclado numérico ( / )
            case KeyEvent.VK_ADD: // mais do teclado numérico ( + )
            case KeyEvent.VK_SUBTRACT: // menos do teclado numérico ( - )
                txtItemCodigo.setText("");
                break;
        }


    }//GEN-LAST:event_txtItemCodigoKeyReleased

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased

    }//GEN-LAST:event_formKeyReleased

    private void txtItemQuantidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtItemQuantidadeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtItemQuantidadeActionPerformed

    private void txtItemQuantidadeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtItemQuantidadeFocusGained
    }//GEN-LAST:event_txtItemQuantidadeFocusGained

    private void txtItemQuantidadeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtItemQuantidadeFocusLost
        if (Decimal.fromString(txtItemQuantidade.getText()).compareTo(BigDecimal.ZERO) <= 0) {
            txtItemQuantidade.setText("1,000");
            calcularSubtotal();
        }
    }//GEN-LAST:event_txtItemQuantidadeFocusLost

    private void txtItemQuantidadeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtItemQuantidadeKeyReleased
        System.out.println("quantidade enter...");
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (txtItemCodigo.getText().equals("")) {
                System.out.println("código em branco...");
                txtItemCodigo.requestFocus();
            } else {
                System.out.println("código ok...");
                produto = validarInsercaoItem();
                if (produto != null) {
                    //preCarregarItem();
                    //if (VENDA_INSERCAO_DIRETA) {
                    //inserirItem(Decimal.fromString(txtQuantidade.getText()));
                    //}
                    if (Ouroboros.SISTEMA_MODO_BALCAO) {
                        if (Decimal.fromString(txtItemQuantidade.getText()).compareTo(BigDecimal.ZERO) <= 0) {
                            txtItemQuantidade.setText("1,000");
                        }
                        inserirItem(Decimal.fromString(txtItemQuantidade.getText()));
                    } else {
                        txtItemValor.requestFocus();
                    }

                }
            }
        }

        calcularSubtotal();

    }//GEN-LAST:event_txtItemQuantidadeKeyReleased

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed

        System.out.println("fechando tela da venda id: " + documento.getId());
        
        documento = null;
        //2019-01-24 vendaItens = null;
        vendaDAO = null;
        movimentoFisicoDAO = null;
        produtoDAO = null;

        vendaViews.remove(this);


    }//GEN-LAST:event_formInternalFrameClosed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing

    }//GEN-LAST:event_formInternalFrameClosing

    private void btnEncerrarVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEncerrarVendaActionPerformed
        encerrarComanda();
    }//GEN-LAST:event_btnEncerrarVendaActionPerformed

    private void btnReceberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReceberActionPerformed
        receber();
    }//GEN-LAST:event_btnReceberActionPerformed

    private void btnPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarActionPerformed
        pesquisarProduto(null, false);
    }//GEN-LAST:event_btnPesquisarActionPerformed

    private void txtItemCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtItemCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtItemCodigoActionPerformed

    private void txtItemValorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtItemValorKeyReleased
        int index;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                //2019-04-02 - adicionado desconto
                /*produto = validarInsercaoItem();
                if (produto != null) {
                    inserirItem(Decimal.fromString(txtQuantidade.getText()));
                }*/
                if (Decimal.fromString(txtItemValor.getText()).compareTo(BigDecimal.ZERO) > 0) {
                    txtItemDescontoPercentual.requestFocus();
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
                removerItem();
                break;

        }

        calcularSubtotal();
    }//GEN-LAST:event_txtItemValorKeyReleased

    private void btnReceber1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReceber1ActionPerformed
        parcelar();
    }//GEN-LAST:event_btnReceber1ActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        txtItemCodigo.requestFocus();
    }//GEN-LAST:event_formFocusGained

    private void btnTransferirComandaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransferirComandaActionPerformed
        transferirComanda();
    }//GEN-LAST:event_btnTransferirComandaActionPerformed

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

    private void txtItemDescontoPercentualKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtItemDescontoPercentualKeyReleased
        int index;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                if (!txtItemSubtotal.isEditable()) {
                    produto = validarInsercaoItem();
                    if (produto != null) {
                        inserirItem(Decimal.fromString(txtItemQuantidade.getText()));
                    }

                } else {
                    txtItemSubtotal.requestFocus();

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
                removerItem();
                break;

        }
        calcularSubtotal();
    }//GEN-LAST:event_txtItemDescontoPercentualKeyReleased

    private void txtRelatoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRelatoFocusLost
        salvar();
    }//GEN-LAST:event_txtRelatoFocusLost

    private void txtRelatoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtRelatoPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRelatoPropertyChange

    private void txtObservacaoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtObservacaoFocusLost
        salvar();
    }//GEN-LAST:event_txtObservacaoFocusLost

    private void txtObservacaoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtObservacaoPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtObservacaoPropertyChange

    private void btnRemoverVeiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverVeiculoActionPerformed
        removerVeiculo();
    }//GEN-LAST:event_btnRemoverVeiculoActionPerformed

    private void btnAceitarOrcamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceitarOrcamentoActionPerformed
        aceitarOrcamento();
    }//GEN-LAST:event_btnAceitarOrcamentoActionPerformed

    private void btnCancelarDocumentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarDocumentoActionPerformed
        cancelarDocumentp();
    }//GEN-LAST:event_btnCancelarDocumentoActionPerformed

    private void btnImprimirA4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirA4ActionPerformed
        imprimirA4();
    }//GEN-LAST:event_btnImprimirA4ActionPerformed

    private void btnImprimirTermicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirTermicaActionPerformed
        imprimirTermica();
    }//GEN-LAST:event_btnImprimirTermicaActionPerformed

    private void btnImprimirTicketComandaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirTicketComandaActionPerformed
        imprimirTicketComanda();
    }//GEN-LAST:event_btnImprimirTicketComandaActionPerformed

    private void btnImprimirTicketCozinhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirTicketCozinhaActionPerformed
        imprimirTicketCozinha();
    }//GEN-LAST:event_btnImprimirTicketCozinhaActionPerformed

    private void btnImprimirSatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirSatActionPerformed
        gerarCupomSat();
    }//GEN-LAST:event_btnImprimirSatActionPerformed

    private void btnVeiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVeiculoActionPerformed
        pesquisarVeiculo();
    }//GEN-LAST:event_btnVeiculoActionPerformed

    private void btnImprimirCarneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirCarneActionPerformed
        gerarCarne();
    }//GEN-LAST:event_btnImprimirCarneActionPerformed

    private void btnAcrescimoProdutosTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAcrescimoProdutosTipoActionPerformed
        alternarAcrescimoProdutosTipo();
    }//GEN-LAST:event_btnAcrescimoProdutosTipoActionPerformed

    private void btnDescontoProdutosTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDescontoProdutosTipoActionPerformed
        alternarDescontoProdutosTipo();
    }//GEN-LAST:event_btnDescontoProdutosTipoActionPerformed

    private void btnAcrescimoServicosTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAcrescimoServicosTipoActionPerformed
        alternarAcrescimoServicosTipo();
    }//GEN-LAST:event_btnAcrescimoServicosTipoActionPerformed

    private void btnDescontoServicosTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDescontoServicosTipoActionPerformed
        alternarDescontoServicosTipo();
    }//GEN-LAST:event_btnDescontoServicosTipoActionPerformed

    private void txtAcrescimoProdutosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAcrescimoProdutosKeyReleased
        distribuirAcrescimoProdutos();
    }//GEN-LAST:event_txtAcrescimoProdutosKeyReleased

    private void txtDescontoProdutosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescontoProdutosKeyReleased
        distribuirDescontoProdutos();
    }//GEN-LAST:event_txtDescontoProdutosKeyReleased

    private void txtAcrescimoServicosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAcrescimoServicosKeyReleased
        distribuirAcrescimoServicos();
    }//GEN-LAST:event_txtAcrescimoServicosKeyReleased

    private void txtDescontoServicosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescontoServicosKeyReleased
        distribuirDescontoServicos();
    }//GEN-LAST:event_txtDescontoServicosKeyReleased

    private void txtItemDescricaoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtItemDescricaoKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (txtItemCodigo.getText().equals("")) {
                txtItemCodigo.requestFocus();
            } else {
                /*produto = validarInsercaoItem();
                if (produto != null) {
                    preCarregarItem();
                    txtItemQuantidade.requestFocus();
                }*/
                txtItemQuantidade.requestFocus();
            }
        }
    }//GEN-LAST:event_txtItemDescricaoKeyReleased

    private void txtItemSubtotalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtItemSubtotalKeyReleased
        System.out.println("aqui...");

        int index;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                produto = validarInsercaoItem();
                if (produto != null) {
                    if (Decimal.fromString(txtItemSubtotal.getText()).compareTo(BigDecimal.ZERO) > 0) {
                        calcularSubtotalReverso();
                        System.out.println("txtItemSubtotalKeyReleased: " + txtItemDescricao.getText());
                        inserirItem(Decimal.fromString(txtItemQuantidade.getText()));
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
                removerItem();
                break;

            default:
                calcularSubtotalReverso();

        }


    }//GEN-LAST:event_txtItemSubtotalKeyReleased

    private void txtItemSubtotalFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtItemSubtotalFocusGained
    }//GEN-LAST:event_txtItemSubtotalFocusGained

    private void txtItemSubtotalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtItemSubtotalFocusLost
    }//GEN-LAST:event_txtItemSubtotalFocusLost

    private void btnProcessoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcessoActionPerformed
        processo();
    }//GEN-LAST:event_btnProcessoActionPerformed

    private void txtItemCodigoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtItemCodigoKeyPressed
        switch (evt.getKeyCode()) {
            //Atalhos modo balcão (uso apenas do teclado numérico)
            case KeyEvent.VK_DIVIDE: // barra do teclado numérico ( / )
                txtItemCodigo.setText("");
                fechar();
                break;
            case KeyEvent.VK_ADD: // mais do teclado numérico ( + )
                txtItemCodigo.setText("");
                pesquisarProduto("", false);
                break;
            case KeyEvent.VK_SUBTRACT: // menos do teclado numérico ( - )
                excluirItemPorNumero();
                txtItemCodigo.setText("");
                break;
        }
    }//GEN-LAST:event_txtItemCodigoKeyPressed

    private void btnNFeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNFeActionPerformed
        detalheNfe();
    }//GEN-LAST:event_btnNFeActionPerformed

    private void tblItensPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tblItensPropertyChange

    }//GEN-LAST:event_tblItensPropertyChange

    private void tblItensFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblItensFocusGained
        //txtItemCodigo.requestFocus();
    }//GEN-LAST:event_tblItensFocusGained

    private void txtReceberKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtReceberKeyReleased
        calcularTroco();
    }//GEN-LAST:event_txtReceberKeyReleased

    private void cboMeioDePagamentoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboMeioDePagamentoFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_cboMeioDePagamentoFocusLost

    private void txtTrocoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTrocoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTrocoActionPerformed

    private void txtTrocoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTrocoKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTrocoKeyPressed

    private void txtTrocoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTrocoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTrocoKeyReleased

    private void txtReceberFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtReceberFocusLost
        salvar();
    }//GEN-LAST:event_txtReceberFocusLost

    private void txtReceberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtReceberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtReceberActionPerformed

    private void txtEnderecoEntregaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEnderecoEntregaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEnderecoEntregaActionPerformed

    private void txtEnderecoEntregaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEnderecoEntregaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEnderecoEntregaKeyPressed

    private void txtEnderecoEntregaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEnderecoEntregaKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEnderecoEntregaKeyReleased

    private void tblItensMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblItensMouseClicked
        tblClick();
    }//GEN-LAST:event_tblItensMouseClicked

    private void txtEnderecoEntregaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtEnderecoEntregaFocusLost
        salvar();
    }//GEN-LAST:event_txtEnderecoEntregaFocusLost

    private void txtComandaNomeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtComandaNomeFocusLost
    }//GEN-LAST:event_txtComandaNomeFocusLost

    private void btnAgruparDocumentosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgruparDocumentosActionPerformed
        agruparDocumentos();
    }//GEN-LAST:event_btnAgruparDocumentosActionPerformed

    private void btnInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInfoActionPerformed
        informacoes();
    }//GEN-LAST:event_btnInfoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceitarOrcamento;
    private javax.swing.JButton btnAcrescimoProdutosTipo;
    private javax.swing.JButton btnAcrescimoServicosTipo;
    private javax.swing.JButton btnAgruparDocumentos;
    private javax.swing.JButton btnCancelarDocumento;
    private javax.swing.JButton btnCliente;
    private javax.swing.JButton btnDescontoProdutosTipo;
    private javax.swing.JButton btnDescontoServicosTipo;
    private javax.swing.JButton btnEncerrarVenda;
    private javax.swing.JButton btnFuncionario;
    private javax.swing.JButton btnImprimirA4;
    private javax.swing.JButton btnImprimirCarne;
    private javax.swing.JButton btnImprimirSat;
    private javax.swing.JButton btnImprimirTermica;
    private javax.swing.JButton btnImprimirTicketComanda;
    private javax.swing.JButton btnImprimirTicketCozinha;
    private javax.swing.JButton btnInfo;
    private javax.swing.JButton btnInserir;
    private javax.swing.JButton btnNFe;
    private javax.swing.JButton btnPesquisar;
    private javax.swing.JButton btnProcesso;
    private javax.swing.JButton btnReceber;
    private javax.swing.JButton btnReceber1;
    private javax.swing.JButton btnRemoverCliente;
    private javax.swing.JButton btnRemoverFuncionario;
    private javax.swing.JButton btnRemoverVeiculo;
    private javax.swing.JButton btnTransferirComanda;
    private javax.swing.JButton btnVeiculo;
    private javax.swing.JComboBox<Object> cboMeioDePagamento;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel lblMensagem;
    private javax.swing.JLabel lblRegistros;
    private javax.swing.JPanel pnlDeliveryRecebimento;
    private javax.swing.JPanel pnlEmAberto;
    private javax.swing.JPanel pnlInserirProduto;
    private javax.swing.JPanel pnlObservacao;
    private javax.swing.JPanel pnlPessoas;
    private javax.swing.JPanel pnlRecebimento;
    private javax.swing.JPanel pnlRelato;
    private javax.swing.JPanel pnlSuperior;
    private javax.swing.JPanel pnlTotais;
    private javax.swing.JTable tblItens;
    private javax.swing.JFormattedTextField txtAcrescimoProdutos;
    private javax.swing.JFormattedTextField txtAcrescimoServicos;
    private javax.swing.JTextField txtComandaNome;
    private javax.swing.JFormattedTextField txtDescontoProdutos;
    private javax.swing.JFormattedTextField txtDescontoServicos;
    private javax.swing.JTextField txtDocumentoId;
    private javax.swing.JFormattedTextField txtEmAberto;
    private javax.swing.JTextField txtEnderecoEntrega;
    private javax.swing.JFormattedTextField txtFaturado;
    private javax.swing.JTextField txtFuncionario;
    private javax.swing.JTextField txtItemCodigo;
    private javax.swing.JFormattedTextField txtItemDescontoPercentual;
    private javax.swing.JTextField txtItemDescricao;
    private javax.swing.JFormattedTextField txtItemQuantidade;
    private javax.swing.JFormattedTextField txtItemSubtotal;
    private javax.swing.JFormattedTextField txtItemValor;
    private javax.swing.JTextArea txtObservacao;
    private javax.swing.JTextField txtPessoaNome;
    private javax.swing.JTextField txtPessoaTelefone;
    private javax.swing.JFormattedTextField txtReceber;
    private javax.swing.JFormattedTextField txtRecebido;
    private javax.swing.JTextArea txtRelato;
    private javax.swing.JTextField txtStatus;
    private javax.swing.JTextField txtTipo;
    private javax.swing.JFormattedTextField txtTotal;
    private javax.swing.JFormattedTextField txtTotalItensProdutos;
    private javax.swing.JFormattedTextField txtTotalItensServicos;
    private javax.swing.JFormattedTextField txtTotalProdutos;
    private javax.swing.JFormattedTextField txtTotalServicos;
    private javax.swing.JTextField txtTroco;
    private javax.swing.JTextField txtVeiculo;
    // End of variables declaration//GEN-END:variables
}
