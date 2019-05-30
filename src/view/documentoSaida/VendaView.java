/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoSaida;

import view.sat.SatEmitirCupomView;
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
import model.mysql.bean.principal.pessoa.PessoaTipo;
import model.mysql.bean.principal.Recurso;
import model.mysql.bean.principal.documento.VendaTipo;
import model.mysql.dao.principal.CaixaDAO;
import model.mysql.dao.principal.VendaDAO;
import model.mysql.dao.principal.ProdutoDAO;
import model.jtable.documento.DocumentoSaidaJTableModel;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.dao.principal.UsuarioDAO;
import static ouroboros.Constants.*;
import ouroboros.Ouroboros;
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
import printing.PrintPDFBox;
import view.produto.item.ConfirmarEntregaDevolucaoView;
import static ouroboros.Ouroboros.IMPRESSORA_CUPOM;
import static ouroboros.Ouroboros.USUARIO;
import printing.RelatorioPdf;

/**
 *
 * @author ivand
 */
import printing.Carne;
import printing.DocumentoSaidaPrint;
import printing.Promissoria;
import printing.TicketCozinhaPrint;
import sat.MwSat;
import view.funcionario.FuncionarioPesquisaView;
import view.pessoa.PessoaPesquisaView;
import view.veiculo.VeiculoPesquisaView;

public class VendaView extends javax.swing.JInternalFrame {

    private int id;
    private Integer comanda;
    private static List<VendaView> vendaViews = new ArrayList<>(); //instâncias

    //private static VendaView vendaView;
    private Venda venda;
    private VendaDAO vendaDAO = new VendaDAO();
    private MovimentoFisicoDAO movimentoFisicoDAO = new MovimentoFisicoDAO();
    private ProdutoDAO produtoDAO = new ProdutoDAO();

    //private List<MovimentoFisico> vendaItens = new ArrayList<>();
    private List<Parcela> parcelas = new ArrayList<>();

    private final DocumentoSaidaJTableModel vendaJTableModel = new DocumentoSaidaJTableModel();

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
                em.refresh(venda); //para uso em várias estações
            }

            venda.setOrcamento(orcamento);

            configurarPorTipo();

            exibirTipo();

            formatarTabela();

            definirAtalhos();
        }
    }

    public VendaView(Venda venda, Integer comanda) {
        if (venda.getVendaTipo().equals(VendaTipo.VENDA) && USUARIO.autorizarAcesso(Recurso.VENDA)
                || venda.getVendaTipo().equals(VendaTipo.PEDIDO) && USUARIO.autorizarAcesso(Recurso.PEDIDO)
                || venda.getVendaTipo().equals(VendaTipo.ORDEM_DE_SERVICO) && USUARIO.autorizarAcesso(Recurso.ORDEM_DE_SERVICO)
                || venda.getVendaTipo().equals(VendaTipo.LOCAÇÃO) && USUARIO.autorizarAcesso(Recurso.LOCACAO)
                || venda.getVendaTipo().equals(VendaTipo.COMANDA) && USUARIO.autorizarAcesso(Recurso.COMANDAS)) {

            initComponents();
            JSwing.startComponentsBehavior(this);

            //btnOs.setVisible(false);
            //this.id = id;
            if (venda.getId() != null) {

                //Desativei em 2019-05-10
                /////em.refresh(venda); //para uso em várias estações 
            }
            this.venda = venda;
            this.comanda = venda.getComanda();

            /*
        if (comanda != null) {
            venda.setComanda(comanda);
        }*/
            //if (id != 0) {
            if (venda.getId() != null && venda.getId() != 0) {
                //venda = vendaDAO.findById(id);

                if (venda.getComanda() != null) {
                    this.comanda = venda.getComanda();
                }

                txtVendaId.setText(venda.getId().toString());
                //txtAbertura.setText(DateTime.toString(venda.getCriacao()));
                /*if (venda.getEncerramento() != null) {
                    txtEncerramento.setText(DateTime.toString(venda.getEncerramento()));
                }*/

                txtRelato.setText(venda.getRelato());

                txtObservacao.setText(venda.getObservacao());

                parcelas = venda.getParcelas();

                exibirTotais();

            }

            carregarTabela(); //2019-03-30

            configurarPorTipo();

            exibirTipo();

            formatarTabela();

            exibirFuncionario();
            exibirCliente();
            exibirVeiculo();

            definirAtalhos();
        }

    }

    private void configurarPorTipo() {
        txtTipo.setText(venda.getVendaTipo().getNome());

        txtInativo.setVisible(false);
        btnReceber.setEnabled(false);
        btnAceitarOrcamento.setVisible(false);
        //pnlEntregaDevolucao.setVisible(false);
        btnEncerrarVenda.setVisible(false);
        btnTransferirComanda.setVisible(false);
        btnImprimirSat.setVisible(false);
        pnlRelato.setVisible(false);
        //pnlCancelamento.setVisible(false);

        btnImprimirCarne.setVisible(false);
        
        btnImprimirTicketComanda.setVisible(false);
        btnImprimirTicketCozinha.setVisible(false);

        btnVeiculo.setVisible(Ouroboros.VENDA_EXIBIR_VEICULO);
        txtVeiculo.setVisible(Ouroboros.VENDA_EXIBIR_VEICULO);
        btnRemoverVeiculo.setVisible(Ouroboros.VENDA_EXIBIR_VEICULO);

        /*if (venda.getCancelamento() != null) {
            txtMotivoCancelamento.setText(venda.getMotivoCancelamento());
            txtDataCancelamento.setText(DateTime.toString(venda.getCancelamento()));
            pnlInserirProduto.setVisible(false);
            pnlCancelamento.setVisible(true);
            txtInativo.setText("CANCELADO");
            txtInativo.setVisible(true);
            btnAceitarOrcamento.setEnabled(true);
            btnCancelar.setEnabled(false);
            btnAceitarOrcamento.setEnabled(false);

        } else*/ if (venda.isOrcamento()) {
            txtInativo.setText("ORÇAMENTO");
            txtInativo.setVisible(true);
            btnAceitarOrcamento.setVisible(true);

            if (venda.getVendaTipo().equals(VendaTipo.ORDEM_DE_SERVICO)) {
                pnlRelato.setVisible(true);
            }

        } else {
            if (venda.getVendaTipo().equals(VendaTipo.VENDA)) {
                btnReceber.setEnabled(true);
                btnImprimirCarne.setVisible(true);
                btnImprimirSat.setVisible(Ouroboros.SAT_HABILITAR);

            } else if (venda.getVendaTipo().equals(VendaTipo.PEDIDO)) {
                btnReceber.setEnabled(true);
                btnImprimirSat.setVisible(Ouroboros.SAT_HABILITAR);

            } else if (venda.getVendaTipo().equals(VendaTipo.ORDEM_DE_SERVICO)) {
                btnReceber.setEnabled(true);
                pnlRelato.setVisible(true);

            } else if (venda.getVendaTipo().equals(VendaTipo.LOCAÇÃO)) {
                btnReceber.setEnabled(true);
                //pnlEntregaDevolucao.setVisible(true);

            } else if (venda.getVendaTipo().equals(VendaTipo.COMANDA)) {
                txtTipo.setText("COMANDA " + comanda);
                btnEncerrarVenda.setVisible(true);
                btnTransferirComanda.setVisible(true);
                btnImprimirSat.setVisible(Ouroboros.SAT_HABILITAR);
                btnImprimirTicketComanda.setVisible(true);
                btnImprimirTicketCozinha.setVisible(true);
                btnReceber.setEnabled(true);
            }
        }
    }

    private void formatarTabela() {
        tblItens.setModel(vendaJTableModel);

        tblItens.setRowHeight(24);
        //id
        tblItens.getColumnModel().getColumn(0).setPreferredWidth(1);
        //tableItens.getColumnModel().getColumn(0).setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        //número
        tblItens.getColumnModel().getColumn(1).setPreferredWidth(40);
        tblItens.getColumnModel().getColumn(1).setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        //código
        tblItens.getColumnModel().getColumn(2).setPreferredWidth(80);
        tblItens.getColumnModel().getColumn(2).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //nome
        tblItens.getColumn("Descrição").setPreferredWidth(400);
        //quantidade
        tblItens.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblItens.getColumnModel().getColumn(4).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //unidade comercial de venda
        tblItens.getColumnModel().getColumn(5).setPreferredWidth(60);

        tblItens.getColumn("-%").setPreferredWidth(80);
        tblItens.getColumn("-%").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblItens.getColumn("Valor").setPreferredWidth(90);
        tblItens.getColumn("Valor").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblItens.getColumn("Subtotal").setPreferredWidth(100);
        tblItens.getColumn("Subtotal").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblItens.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                exibirItemAtual();
            }

        });

        if (vendaJTableModel.getRowCount() > 0) {
            tblItens.setRowSelectionInterval(0, 0);
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

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), "pesquisarProdutos");
        am.put("pesquisarProdutos", new FormKeyStroke("F9"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), "imprimir");
        am.put("imprimir", new FormKeyStroke("F10"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, KeyEvent.CTRL_DOWN_MASK), "imprimirTicketComanda");
        am.put("imprimirTicketComanda", new FormKeyStroke("CtrlF10"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "cfe");
        am.put("cfe", new FormKeyStroke("F11"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, KeyEvent.CTRL_DOWN_MASK), "imprimirTicketCozinha");
        am.put("imprimirTicketCozinha", new FormKeyStroke("CtrlF11"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "encerrar");
        am.put("encerrar", new FormKeyStroke("F12"));
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
                    exibirComandas();
                    break;
                case "F2":
                    txtCodigo.requestFocus();
                    break;
                case "F3":
                    txtQuantidade.requestFocus();
                    break;
                case "F4":
                    pesquisarFuncionario();
                    break;
                case "F5":
                    pesquisarCliente();
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
                    pesquisarProduto(null);
                    break;
                case "F10":
                    imprimirA4();
                    break;
                case "ShiftF10":
                    escolherImpressao();
                    break;
                case "CtrlF10":
                    imprimirTicketComanda();
                    break;
                case "F11":
                    gerarCupomSat();
                    break;
                case "CtrlF11":
                    imprimirTicketCozinha();
                    break;
                case "F12":
                    //encerrarVenda();
                    break;
            }
        }
    }

    public static VendaView getInstance(Venda venda) {
        return getInstance(venda, null, false);
    }

    public static VendaView getInstance(Venda venda, Integer comanda, boolean orcamento) {
        for (VendaView vendaView : vendaViews) {
            //if (vendaView.id == id && vendaView.comanda == comanda) {
            if (vendaView.venda.getId() != null && vendaView.venda.getId().equals(venda.getId())) {
                return vendaView;
            }
        }
        if (orcamento) {
            vendaViews.add(new VendaView(venda, true));
        } else {
            //vendaViews.add(new VendaView(venda, comanda));
            vendaViews.add(new VendaView(venda, comanda));
        }

        System.out.println("Instances of VendaView: " + vendaViews.size());

        return vendaViews.get(vendaViews.size() - 1);
    }

    private void exibirTipo() {

    }

    private void exibirComandas() {
        MAIN_VIEW.removeTab(this.getName());
        MAIN_VIEW.removeView(this.getName());
        if (comanda != null) {
            MAIN_VIEW.addView(ComandasView.getSingleInstance());
        }
    }

    private void salvar() {
        System.out.println("salvar...");
        //venda.setComanda(comanda);
        venda.setAcrescimoPercentual(Decimal.fromString(txtAcrescimoPercentual.getText()));
        venda.setAcrescimoMonetario(Decimal.fromString(txtAcrescimo.getText()));
        venda.setDescontoPercentual(Decimal.fromString(txtDescontoPercentual.getText()));
        venda.setDescontoMonetario(Decimal.fromString(txtDesconto.getText()));
        venda.setRelato(txtRelato.getText());
        venda.setObservacao(txtObservacao.getText());

        venda = vendaDAO.save(venda);

        txtVendaId.setText(venda.getId().toString());
        //txtAbertura.setText(DateTime.toString(venda.getCriacao()));

        exibirFuncionario();
        exibirCliente();
        exibirVeiculo();
        exibirTotais();
    }

    private void pesquisarProduto(String buscar) {
        ProdutoPesquisaView produtoPesquisaView = new ProdutoPesquisaView(buscar);

        produto = produtoPesquisaView.getProduto();
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

            //códigos de barras gerados para etiquetas comuns (modelo Oficina das Artes)
            if (produtos.isEmpty()) {
                codigo = codigo.replace(" ", ""); //código preenchido com espaços por questão estética
                produtos = produtoDAO.findByCodigo(codigo);
            }

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
        txtValor.setText(Decimal.toString(produto.getValorVenda()));
        //txtItemCodigo.setText(produto.getCodigo());
        txtItemNome.setText(produto.getNome());
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
        BigDecimal valorVenda = Decimal.fromString(txtValor.getText());
        if (valorVenda.compareTo(BigDecimal.ZERO) == 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Produto com valor igual a zero. Não é possível inserir.", "Erro", JOptionPane.ERROR_MESSAGE);
            txtCodigo.setText("");
            txtCodigo.requestFocus();
        } else {
            //Se não houver venda, criar nova
            if (venda.getId() == null) {
                salvar();
            }
            //inserir item
            //Integer numero = vendaItens.size() + 1;
            String codigo = produto.getCodigo();
            BigDecimal descontoPercentualItem = Decimal.fromString(txtDescontoPercentualItem.getText());
            UnidadeComercial unidadeComercialVenda = produto.getUnidadeComercialVenda();

            MovimentoFisico movimentoFisico = new MovimentoFisico(produto,
                    codigo,
                    BigDecimal.ZERO,
                    quantidade,
                    valorVenda,
                    descontoPercentualItem,
                    unidadeComercialVenda,
                    MovimentoFisicoTipo.VENDA,
                    null);

            if (venda.getVendaTipo().equals(VendaTipo.VENDA)
                    || venda.getVendaTipo().equals(VendaTipo.ORDEM_DE_SERVICO)
                    || venda.getVendaTipo().equals(VendaTipo.COMANDA)) {
                movimentoFisico.setDataSaida(LocalDateTime.now());
            }

            movimentoFisico = movimentoFisicoDAO.save(movimentoFisico);

            venda.addMovimentoFisico(movimentoFisico);

            venda = vendaDAO.save(venda);

            carregarTabela();

            int index = tblItens.getRowCount() - 1;
            tblItens.setRowSelectionInterval(index, index);

            //rolar para o item (forçar visibilidade)
            tblItens.scrollRectToVisible(tblItens.getCellRect(index, 0, true));

            exibirItemAtual();

            //resetar campos
            txtCodigo.setText("");
            txtItemNome.setText("");
            txtQuantidade.setText("1,000");
            txtValor.setText("0");
            txtDescontoPercentualItem.setText("0");
            txtCodigo.requestFocus();

        }

        produto = null;

    }

    private void excluirItem() {
        int index = tblItens.getSelectedRow();
        if (index > -1) {
            MovimentoFisico itemExcluir = vendaJTableModel.getRow(index);

            //verificar valores antes de excluir
            //2019-04-05 arredondar para comparar
            if (itemExcluir.getSubtotal().setScale(2, RoundingMode.HALF_UP).compareTo(venda.getTotalEmAberto()) > 0) {
                JOptionPane.showMessageDialog(rootPane, "Subtotal do item a ser excluído é maior que o total em aberto");

            } else {

                //2019-01-24 venda.setMovimentosFisicos(vendaItens);
                //Marcar item como excluído
                itemExcluir = movimentoFisicoDAO.remove(itemExcluir);

                venda.addMovimentoFisico(itemExcluir);

                venda = vendaDAO.save(venda);

                //em.refresh(venda);
                //2019-01-24 vendaItens = venda.getMovimentosFisicosSaida();
                carregarTabela();

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
        //em.getTransaction().begin();
        vendaJTableModel.clear();
        //2019-01-24 vendaJTableModel.addList(vendaItens);
        vendaJTableModel.addList(venda.getMovimentosFisicosSaida());
        //em.getTransaction().commit();
    }

    private void exibirItemAtual() {
        try {
            /*if (tblItens.getSelectedRow() > -1) {
                int index = tblItens.getSelectedRow();
                
                String item = vendaJTableModel.getValueAt(index, 1).toString() + " "
                        + vendaJTableModel.getValueAt(index, 2).toString() + " "
                        + vendaJTableModel.getValueAt(index, 3).toString() + " "
                        + vendaJTableModel.getValueAt(index, 4).toString() + " "
                        + vendaJTableModel.getValueAt(index, 5).toString() + " X "
                        + vendaJTableModel.getValueAt(index, 6).toString() + " - "
                        + vendaJTableModel.getValueAt(index, 7).toString() + "% = "
                        + vendaJTableModel.getValueAt(index, 8).toString();
                txtItemPosicionado.setText(item);

            } else {
                
                txtItemPosicionado.setText("");
            }*/

            exibirTotais();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e, "Erro ao exibir item atual", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exibirTotais() {
        //em.refresh(venda);
        txtTotalItens.setText(Decimal.toString(venda.getTotalItens()));
        txtAcrescimoPercentual.setText(Decimal.toString(venda.getAcrescimoPercentual()));
        txtAcrescimo.setText(Decimal.toString(venda.getAcrescimoMonetario()));
        txtDescontoPercentual.setText(Decimal.toString(venda.getDescontoPercentual()));
        txtDesconto.setText(Decimal.toString(venda.getDescontoMonetario()));
        txtTotal.setText(Decimal.toString(venda.getTotal()));

        txtRecebido.setText(Decimal.toString(venda.getTotalRecebidoAVista()));

        txtFaturado.setText(Decimal.toString(venda.getTotalAPrazo()));

        txtEmAberto.setText(Decimal.toString(venda.getTotalEmAberto()));

    }

    private void encerrarComanda() {
        if (venda.getEncerramento() != null) {
            JOptionPane.showMessageDialog(rootPane, "Este documento já foi encerrado.");
        } else if (venda.getId() == null) {
            MAIN_VIEW.removeTab(this.getName());
            MAIN_VIEW.removeView(this.getName());
        } else if (venda.getTotalEmAberto().compareTo(BigDecimal.ZERO) > 0) {
            JOptionPane.showMessageDialog(rootPane, "Ainda há valor em aberto. Não é possível encerrar.");
        } else {
            venda.setEncerramento(DateTime.getNow());
            venda = vendaDAO.save(venda);
            MAIN_VIEW.removeTab(this.getName());
            MAIN_VIEW.removeView(this.getName());
        }
    }

    private void receber() {
        if (venda.isOrcamento()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não é possível receber em ORÇAMENTO.", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            Caixa lastCaixa = new CaixaDAO().getLastCaixa();
            if (lastCaixa == null || lastCaixa.getEncerramento() != null) {
                JOptionPane.showMessageDialog(rootPane, "Não há turno de caixa aberto. Não é possível realizar recebimentos.", "Atenção", JOptionPane.WARNING_MESSAGE);
            } else if (venda.getTotalEmAberto().compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(rootPane, "Não há valor em aberto.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                validarCredito(false);
                RecebimentoView recebimentoView = new RecebimentoView(venda);

                exibirTotais();
            }
        }
    }

    private void parcelar() {
        if (validarCredito(true)) {
            ParcelamentoView parcelamentoView = new ParcelamentoView(venda);
            exibirTotais();
            exibirCliente();
        }
    }

    private void imprimirA4() {
        salvar();

        if (venda.getVendaTipo().equals(VendaTipo.LOCAÇÃO)) {
            RelatorioPdf.gerarLocacaoOS(venda);

        } else {
            DocumentoSaidaPrint.gerarA4(venda);

        }
    }

    private void imprimirTermica() {
        salvar();

        PrintPDFBox pPDF = new PrintPDFBox();

        String pdfFilePath = TO_PRINTER_PATH + venda.getTitulo() + " " + venda.getId() + "_" + System.currentTimeMillis() + ".pdf";
        CriarPDF.gerarVenda(venda, pdfFilePath);
        pPDF.print(pdfFilePath, IMPRESSORA_CUPOM);
    }

    private void imprimirTicketComanda() {
        salvar();

        String pdfFilePath = TO_PRINTER_PATH + "TICKET_COMANDA " + venda.getId() + "_" + System.currentTimeMillis() + ".pdf";
        CriarPDF.gerarTicketComanda(venda, pdfFilePath);

        PrintPDFBox pPDF = new PrintPDFBox();
        pPDF.print(pdfFilePath, IMPRESSORA_CUPOM);
    }

    private void imprimirTicketCozinha() {
        salvar();

        TicketCozinhaPrint.imprimirCupom(venda);
    }

    private void escolherImpressao() {
        salvar();

        EscolherImpressao escolherImpressao = new EscolherImpressao(venda);
    }

    private void gerarCupomSat() {
        if (validarCupomSat()) {
            SatEmitirCupomView satCpf = new SatEmitirCupomView(venda);

        }
    }

    private boolean validarCupomSat() {
        List<String> erros = MwSat.validar(venda);

        if (!erros.isEmpty()) {
            String mensagem = String.join("\r\n", erros);
            JOptionPane.showMessageDialog(MAIN_VIEW, mensagem, "Erro ao validar o documento. Verifique os erros:", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void exibirRecebimentos() {
        if (venda == null || venda.getRecebimentos().isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, "Não há recebimentos", "Atenção", JOptionPane.INFORMATION_MESSAGE);
        } else {
            RecebimentoListaView recebimentoListaView = new RecebimentoListaView(MAIN_VIEW, true, venda);
            recebimentoListaView.setLocationRelativeTo(this);
            recebimentoListaView.setVisible(true);
        }
    }

    private void transferirComanda() {
        TransferirComandaView transferirComandaView = new TransferirComandaView(MAIN_VIEW, venda);
        //em.refresh(venda);
        comanda = venda.getComanda();
        txtTipo.setText("COMANDA " + comanda);
    }

    private void entrega() {
        EntregaDevolucaoView entregaDevolucaoView = new EntregaDevolucaoView(venda);
    }

    private void confirmarEntrega() {
        if (venda.isOrcamento()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não é possível confirmar entrega para orçamentos", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            //2019-01-24 ConfirmarEntregaDevolucaoView confirmar = new ConfirmarEntregaDevolucaoView(vendaItens);
            ConfirmarEntregaDevolucaoView confirmar = new ConfirmarEntregaDevolucaoView(venda.getMovimentosFisicosSaida());
        }
    }

    private void confirmarDevolucao() {
        if (venda.isOrcamento()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não é possível confirmar devolução para orçamentos", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            ConfirmarEntregaDevolucaoView confirmar = new ConfirmarEntregaDevolucaoView(venda.getMovimentosFisicosDevolucao());
        }
    }

    private void cancelarVenda() {
        if (venda.getId() == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Documento vazio. Não é possível cancelar.", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {
            CancelarDocumentoView cancelarVenda = new CancelarDocumentoView(venda);
            configurarPorTipo();

        }
    }

    private void aceitarOrçamento() {
        int resposta = JOptionPane.showConfirmDialog(MAIN_VIEW, "Aceitar orçamento? Este procedimento é irreversível.", "Atenção", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resposta == JOptionPane.OK_OPTION) {
            venda.setOrcamento(false);
            venda = vendaDAO.save(venda);
            configurarPorTipo();
        }
    }

    private void pesquisarCliente() {
        PessoaPesquisaView pesquisa = new PessoaPesquisaView(PessoaTipo.CLIENTE);

        if (pesquisa.getPessoa() != null) {
            venda.setPessoa(pesquisa.getPessoa());
            salvar();
        }
    }

    private void exibirCliente() {
        if (venda.getPessoa() != null) {
            txtClienteNome.setText(venda.getPessoa().getId() + " - " + venda.getPessoa().getNome());
            txtClienteTelefone.setText(venda.getPessoa().getTelefone1());
        } else {
            txtClienteNome.setText("NÃO INFORMADO");
            txtClienteTelefone.setText("");
        }
    }

    private void removerCliente() {
        if (venda.getTotalRecebidoAPrazo().compareTo(BigDecimal.ZERO) > 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Já existem parcelas recebidas. Não é possível remover o cliente.", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else if (!venda.getParcelasAPrazo().isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Já existe valor faturado. Não é possível remover o cliente.", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {
            venda.setPessoa(null);
            salvar();
        }
    }

    private void pesquisarFuncionario() {
        FuncionarioPesquisaView pesquisa = new FuncionarioPesquisaView();

        if (pesquisa.getFuncionario() != null) {
            venda.setFuncionario(pesquisa.getFuncionario());
            salvar();
        }
    }

    private void exibirFuncionario() {
        if (venda.getFuncionario() != null) {
            txtFuncionario.setText(venda.getFuncionario().getId() + " - " + venda.getFuncionario().getNome());
        } else {
            txtFuncionario.setText("NÃO INFORMADO");
        }
    }

    private void removerFuncionario() {
        venda.setFuncionario(null);
        salvar();
    }

    private void pesquisarVeiculo() {
        VeiculoPesquisaView pesquisa = new VeiculoPesquisaView();

        if (pesquisa.getVeiculo() != null) {
            venda.setVeiculo(pesquisa.getVeiculo());
            salvar();
        }
    }

    private void exibirVeiculo() {
        if (venda.getVeiculo() != null) {
            txtVeiculo.setText(venda.getVeiculo().getPlaca() + " - " + venda.getVeiculo().getModelo());

        } else {
            txtVeiculo.setText("NÃO INFORMADO");

        }
    }

    private void removerVeiculo() {
        if (venda.getTotalRecebidoAPrazo().compareTo(BigDecimal.ZERO) > 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Já existem parcelas recebidas. Não é possível remover o veiculo.", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else if (!venda.getParcelasAPrazo().isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Já existe valor faturado. Não é possível remover o veiculo.", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {
            venda.setVeiculo(null);
            salvar();
        }
    }

    private void requisicaoMaterial() {
        if (venda.getPessoa() == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione uma pessoa antes de gerar", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            RelatorioPdf.gerarRequisicaoMaterial(venda);
        }

    }

    private void gerarCarne() {
        if (venda.getParcelasAPrazo().isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem parcelas para gerar carnê", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            Carne.gerarCarne(venda.getParcelasAPrazo());
        }
    }

    private void promissoria() {
        if (venda.getParcelasAPrazo().isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem parcelas para gerar promissória", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            Promissoria.gerar(venda.getParcelasAPrazo());
        }
    }

    public boolean validarCredito(boolean bloquear) {
        Pessoa pessoa = venda.getPessoa();

        if (pessoa != null) {

            BigDecimal limiteCredito = pessoa.getLimiteCredito();
            BigDecimal totalExcedido = (pessoa.getTotalComprometido().add(venda.getTotal())).subtract(limiteCredito);

            if (totalExcedido.compareTo(BigDecimal.ZERO) > 0) {
                String msg = "Limite de crédito: " + Decimal.toString(limiteCredito)
                        + "\nTotal em aberto + vencido: " + Decimal.toString(pessoa.getTotalComprometido())
                        + "\nTotal deste documento: " + Decimal.toString(venda.getTotal())
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        btnReceber = new javax.swing.JButton();
        btnReceber1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        txtEmAberto = new javax.swing.JFormattedTextField();
        jPanel3 = new javax.swing.JPanel();
        txtClienteNome = new javax.swing.JTextField();
        btnCliente = new javax.swing.JButton();
        btnRemoverCliente = new javax.swing.JButton();
        txtClienteTelefone = new javax.swing.JTextField();
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
        jPanel5 = new javax.swing.JPanel();
        txtTipo = new javax.swing.JTextField();
        txtInativo = new javax.swing.JTextField();
        btnAceitarOrcamento = new javax.swing.JButton();
        btnCancelarDocumento = new javax.swing.JButton();
        txtVendaId = new javax.swing.JTextField();
        btnEncerrarVenda = new javax.swing.JButton();
        btnTransferirComanda = new javax.swing.JButton();
        btnImprimirA4 = new javax.swing.JButton();
        btnImprimirTermica = new javax.swing.JButton();
        btnImprimirTicketComanda = new javax.swing.JButton();
        btnImprimirTicketCozinha = new javax.swing.JButton();
        btnImprimirSat = new javax.swing.JButton();
        btnImprimirCarne = new javax.swing.JButton();

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

        btnReceber.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnReceber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-note-and-coin-20.png"))); // NOI18N
        btnReceber.setText("F7 RECEBE");
        btnReceber.setContentAreaFilled(false);
        btnReceber.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnReceber.setIconTextGap(10);
        btnReceber.setPreferredSize(new java.awt.Dimension(180, 49));
        btnReceber.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnReceber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReceberActionPerformed(evt);
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
                    .addComponent(btnReceber, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
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
                            .addComponent(btnReceber, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtFaturado)
                            .addComponent(btnReceber1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtClienteNome.setEditable(false);
        txtClienteNome.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtClienteNome.setText("NÃO INFORMADO");

        btnCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/user.png"))); // NOI18N
        btnCliente.setText("F5 CLIENTE");
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

        txtClienteTelefone.setEditable(false);
        txtClienteTelefone.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

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

        txtVeiculo.setEditable(false);
        txtVeiculo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtVeiculo.setText("NÃO INFORMADO");

        btnVeiculo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-car-20.png"))); // NOI18N
        btnVeiculo.setText("VEÍCULO");
        btnVeiculo.setContentAreaFilled(false);
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
        btnRemoverVeiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverVeiculoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnFuncionario, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(txtFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverFuncionario)
                        .addGap(18, 18, 18)
                        .addComponent(btnCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClienteNome, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClienteTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverCliente))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(txtVeiculo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverVeiculo)
                        .addGap(553, 553, 553)))
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
                    .addComponent(txtClienteNome)
                    .addComponent(btnCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtClienteTelefone))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtVeiculo)
                    .addComponent(btnRemoverVeiculo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
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
        jLabel35.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel35.setOpaque(true);

        javax.swing.GroupLayout pnlRelatoLayout = new javax.swing.GroupLayout(pnlRelato);
        pnlRelato.setLayout(pnlRelatoLayout);
        pnlRelatoLayout.setHorizontalGroup(
            pnlRelatoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlRelatoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4)
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        txtVendaId.setEditable(false);
        txtVendaId.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtVendaId.setForeground(java.awt.Color.red);
        txtVendaId.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtVendaId.setText("NOVO");

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
        btnImprimirTicketCozinha.setToolTipText("F12 IMPRIMIR TICKET COZINHA");
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
        btnImprimirSat.setToolTipText("CTRL+ F11 IMPRIMIR CUPOM SAT");
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
                .addComponent(txtVendaId, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelarDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtTipo, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnEncerrarVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTransferirComanda, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnImprimirA4, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtInativo)
                    .addComponent(btnAceitarOrcamento, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtVendaId)
                    .addComponent(btnCancelarDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtTipo)
                    .addComponent(btnEncerrarVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnTransferirComanda, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnImprimirA4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnImprimirTermica, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnImprimirTicketComanda, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnImprimirTicketCozinha, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnImprimirSat, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnImprimirCarne, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlRelato, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlObservacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlInserirProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlRelato, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlObservacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void tblItensPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tblItensPropertyChange

    }//GEN-LAST:event_tblItensPropertyChange

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

        venda = null;
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
        pesquisarProduto(null);
    }//GEN-LAST:event_btnPesquisarActionPerformed

    private void tblItensFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblItensFocusGained
        txtCodigo.requestFocus();
    }//GEN-LAST:event_tblItensFocusGained

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoActionPerformed

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

    private void btnTransferirComandaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransferirComandaActionPerformed
        transferirComanda();
    }//GEN-LAST:event_btnTransferirComandaActionPerformed

    private void btnClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClienteActionPerformed
        pesquisarCliente();
    }//GEN-LAST:event_btnClienteActionPerformed

    private void btnRemoverClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverClienteActionPerformed
        removerCliente();
    }//GEN-LAST:event_btnRemoverClienteActionPerformed

    private void btnFuncionarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFuncionarioActionPerformed
        pesquisarFuncionario();
    }//GEN-LAST:event_btnFuncionarioActionPerformed

    private void btnRemoverFuncionarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverFuncionarioActionPerformed
        removerFuncionario();
    }//GEN-LAST:event_btnRemoverFuncionarioActionPerformed

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

    private void txtRelatoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRelatoFocusLost
        salvar();
    }//GEN-LAST:event_txtRelatoFocusLost

    private void txtRelatoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtRelatoPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRelatoPropertyChange

    private void txtObservacaoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtObservacaoFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtObservacaoFocusLost

    private void txtObservacaoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtObservacaoPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtObservacaoPropertyChange

    private void btnRemoverVeiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverVeiculoActionPerformed
        removerVeiculo();
    }//GEN-LAST:event_btnRemoverVeiculoActionPerformed

    private void btnAceitarOrcamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceitarOrcamentoActionPerformed
        aceitarOrçamento();
    }//GEN-LAST:event_btnAceitarOrcamentoActionPerformed

    private void btnCancelarDocumentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarDocumentoActionPerformed
        cancelarVenda();
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceitarOrcamento;
    private javax.swing.JButton btnCancelarDocumento;
    private javax.swing.JButton btnCliente;
    private javax.swing.JButton btnEncerrarVenda;
    private javax.swing.JButton btnFuncionario;
    private javax.swing.JButton btnImprimirA4;
    private javax.swing.JButton btnImprimirCarne;
    private javax.swing.JButton btnImprimirSat;
    private javax.swing.JButton btnImprimirTermica;
    private javax.swing.JButton btnImprimirTicketComanda;
    private javax.swing.JButton btnImprimirTicketCozinha;
    private javax.swing.JButton btnInserir;
    private javax.swing.JButton btnPesquisar;
    private javax.swing.JButton btnReceber;
    private javax.swing.JButton btnReceber1;
    private javax.swing.JButton btnRemoverCliente;
    private javax.swing.JButton btnRemoverFuncionario;
    private javax.swing.JButton btnRemoverVeiculo;
    private javax.swing.JButton btnTransferirComanda;
    private javax.swing.JButton btnVeiculo;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel35;
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
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JPanel pnlInserirProduto;
    private javax.swing.JPanel pnlObservacao;
    private javax.swing.JPanel pnlRelato;
    private javax.swing.JTable tblItens;
    private javax.swing.JFormattedTextField txtAcrescimo;
    private javax.swing.JFormattedTextField txtAcrescimoPercentual;
    private javax.swing.JTextField txtClienteNome;
    private javax.swing.JTextField txtClienteTelefone;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JFormattedTextField txtDesconto;
    private javax.swing.JFormattedTextField txtDescontoPercentual;
    private javax.swing.JFormattedTextField txtDescontoPercentualItem;
    private javax.swing.JFormattedTextField txtEmAberto;
    private javax.swing.JFormattedTextField txtFaturado;
    private javax.swing.JTextField txtFuncionario;
    private javax.swing.JTextField txtInativo;
    private javax.swing.JTextField txtItemNome;
    private javax.swing.JTextArea txtObservacao;
    private javax.swing.JFormattedTextField txtQuantidade;
    private javax.swing.JFormattedTextField txtRecebido;
    private javax.swing.JTextArea txtRelato;
    private javax.swing.JTextField txtTipo;
    private javax.swing.JFormattedTextField txtTotal;
    private javax.swing.JFormattedTextField txtTotalItens;
    private javax.swing.JFormattedTextField txtValor;
    private javax.swing.JTextField txtVeiculo;
    private javax.swing.JTextField txtVendaId;
    // End of variables declaration//GEN-END:variables
}
