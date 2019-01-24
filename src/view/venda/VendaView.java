/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.venda;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import model.dao.principal.MovimentoFisicoDAO;
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
import model.bean.principal.Caixa;
import model.bean.principal.MovimentoFisicoTipo;
import model.bean.principal.Parcela;
import model.bean.principal.Venda;
import model.bean.principal.MovimentoFisico;
import model.bean.principal.Produto;
import model.bean.fiscal.UnidadeComercial;
import model.bean.principal.ImpressoraFormato;
import model.bean.principal.VendaTipo;
import model.dao.principal.CaixaDAO;
import model.dao.principal.VendaDAO;
import model.dao.principal.ProdutoDAO;
import model.jtable.VendaJTableModel;
import static ouroboros.Constants.*;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.IMPRESSORA_A4;
import static ouroboros.Ouroboros.VENDA_INSERCAO_DIRETA;
import printing.CriarPDF;
import util.DateTime;
import util.Decimal;
import util.JSwing;
import view.Toast;
import view.produto.ProdutoPesquisaView;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.SAT_HABILITAR;
import static ouroboros.Ouroboros.TO_PRINTER_PATH;
import static ouroboros.Ouroboros.em;
import printing.CriarPdfA4;
import printing.PrintPDFBox;
import view.produto.item.ConfirmarEntregaDevolucaoView;
import view.sat.SATCancelarUltimoCupom;
import static ouroboros.Ouroboros.IMPRESSORA_CUPOM;
import static ouroboros.Ouroboros.IMPRESSORA_FORMATO_PADRAO;
import printing.RelatorioPdf;

/**
 *
 * @author ivand
 */
import printing.Carne;
import view.cliente.PessoaPesquisaView;
public class VendaView extends javax.swing.JInternalFrame {

    private int id;
    private Integer comanda;
    private static List<VendaView> vendaViews = new ArrayList<>(); //instâncias

    //private static VendaView vendaView;
    private Venda venda = new Venda();
    private VendaDAO vendaDAO = new VendaDAO();
    private MovimentoFisicoDAO movimentoFisicoDAO = new MovimentoFisicoDAO();
    private ProdutoDAO produtoDAO = new ProdutoDAO();

    private List<MovimentoFisico> vendaItens = new ArrayList<>();
    private List<Parcela> parcelas = new ArrayList<>();

    private final VendaJTableModel vendaJTableModel = new VendaJTableModel();

    private Produto produto = null;

    /**
     * Creates new form Venda
     */
    public VendaView() {
        initComponents();
        JSwing.startComponentsBehavior(this);
    }
    
    public VendaView(Venda venda, boolean orcamento) {
        initComponents();
        JSwing.startComponentsBehavior(this);
        
        venda.setOrcamento(orcamento);
        
        configurarPorTipo();
        
        exibirTipo();
        
        formatarTabela();

        definirAtalhos();
    }

    public VendaView(Venda venda, Integer comanda) {
        initComponents();
        JSwing.startComponentsBehavior(this);
        
        //btnOs.setVisible(false);

        //this.id = id;
        this.venda = venda;
        this.comanda = venda.getComanda();

        /*
        if (comanda != null) {
            venda.setComanda(comanda);
        }*/

        //if (id != 0) {
        if(venda.getId() != null && venda.getId() != 0) {
            //venda = vendaDAO.findById(id);

            if (venda.getComanda() != null) {
                this.comanda = venda.getComanda();
            }
            
            txtVendaId.setText(venda.getId().toString());
            txtAbertura.setText(DateTime.toString(venda.getCriacao()));
            if (venda.getEncerramento() != null) {
                txtEncerramento.setText(DateTime.toString(venda.getEncerramento()));
            }

            txtObservacao.setText(venda.getObservacao());

            //em.refresh(venda);
            vendaItens = venda.getMovimentosFisicosSaida();
            /*
            for(MovimentoFisico mf : venda.getMovimentosFisicosSaida()) {
                em.refresh(mf);
                vendaItens.add(mf);
            }
            
            venda.setMovimentoFisicoList(vendaItens);
            em.refresh(venda);
             */

            parcelas = venda.getParcelas();

            exibirTotais();

            vendaJTableModel.addList(vendaItens);

        }

        configurarPorTipo();
        
        exibirTipo();

        formatarTabela();
        
        exibirCliente();

        definirAtalhos();

    }
    
    private void configurarPorTipo() {
        txtTipo.setText(venda.getVendaTipo().getNome());
        
        //JSwing.setComponentesHabilitados(pnlComanda, false);
        //JSwing.setComponentesHabilitados(pnlSat, SAT_HABILITAR);
        
        txtInativo.setVisible(false);
        btnReceber.setEnabled(false);
        btnAceitarOrçamento.setEnabled(false);
        pnlEntregaDevolucao.setVisible(false);
        pnlComanda.setVisible(false);
        pnlSat.setVisible(false);

        
        if(venda.getCancelamento() != null) {
            txtInativo.setText("CANCELADO");
            txtInativo.setVisible(true);
            btnAceitarOrçamento.setEnabled(true);
            
        } else if(venda.isOrcamento()) {
            txtInativo.setText("ORÇAMENTO");
            txtInativo.setVisible(true);
            btnAceitarOrçamento.setEnabled(true);
            
        } else {
            if(venda.getVendaTipo().equals(VendaTipo.VENDA)) {
                btnReceber.setEnabled(true);
                pnlSat.setVisible(SAT_HABILITAR);
                
            } else if(venda.getVendaTipo().equals(VendaTipo.PEDIDO)) {
                btnReceber.setEnabled(true);
                pnlSat.setVisible(SAT_HABILITAR);
                
            } else if(venda.getVendaTipo().equals(VendaTipo.ORDEM_DE_SERVICO)) {
                btnReceber.setEnabled(true);
                
            } else if(venda.getVendaTipo().equals(VendaTipo.LOCAÇÃO)) {
                btnReceber.setEnabled(true);
                pnlEntregaDevolucao.setVisible(true);
                
            } else if(venda.getVendaTipo().equals(VendaTipo.COMANDA)) {
                txtTipo.setText("COMANDA " + comanda);
                pnlComanda.setVisible(true);
                pnlSat.setVisible(SAT_HABILITAR);
            }
        }
    }
    
    private void formatarTabela() {
        tableItens.setModel(vendaJTableModel);

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

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "observacao");
        am.put("observacao", new FormKeyStroke("F4"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "pesquisarCliente");
        am.put("pesquisarCliente", new FormKeyStroke("F5"));
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), "transferir");
        am.put("transferir", new FormKeyStroke("F6"));

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

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, KeyEvent.SHIFT_DOWN_MASK), "escolherImpressao");
        am.put("escolherImpressao", new FormKeyStroke("ShiftF10"));
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, KeyEvent.CTRL_DOWN_MASK), "imprimirTicketComanda");
        am.put("imprimirTicketComanda", new FormKeyStroke("CtrlF10"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "cfe");
        am.put("cfe", new FormKeyStroke("F11"));

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
                    observacao();
                    break;
                case "F5":
                    pesquisarCliente();
                    break;
                case "F6":
                    transferir();
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
                    imprimir();
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
                case "F12":
                    encerrarVenda();
                    break;
            }
        }
    }

    public static VendaView getInstance(Venda venda) {
        return getInstance(venda, null, false);
    }
    
    public static VendaView getInstanceOrcamento() {
        return getInstance(new Venda(), null, true);
    }

    public static VendaView getInstance(Venda venda, Integer comanda, boolean orcamento) {
        for (VendaView vendaView : vendaViews) {
            //if (vendaView.id == id && vendaView.comanda == comanda) {
            if (vendaView.venda.getId() != null && vendaView.venda.getId().equals(venda.getId())) {
                return vendaView;
            }
        }
        if(orcamento) {
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
        venda.setObservacao(txtObservacao.getText());

        venda = vendaDAO.save(venda);
        
        txtVendaId.setText(venda.getId().toString());
        txtAbertura.setText(DateTime.toString(venda.getCriacao()));
        exibirCliente();
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
        //txtItemNome.setText(produto.getNome());
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
            UnidadeComercial unidadeComercialVenda = produto.getUnidadeComercialVenda();

            MovimentoFisico movimentoFisico = new MovimentoFisico(produto, codigo, BigDecimal.ZERO, quantidade, valorVenda, unidadeComercialVenda, MovimentoFisicoTipo.VENDA, null);

            if(venda.getVendaTipo().equals(VendaTipo.VENDA) || venda.getVendaTipo().equals(VendaTipo.ORDEM_DE_SERVICO)) {
                movimentoFisico.setDataSaida(LocalDateTime.now());
            }
            
            movimentoFisico = movimentoFisicoDAO.save(movimentoFisico);

            venda.addMovimentoFisico(movimentoFisico);

            venda = vendaDAO.save(venda);

            //venda.setMovimentoFisicoList(vendaItens);
            vendaItens = venda.getMovimentosFisicosSaida();

            carregarTabela();

            int index = tableItens.getRowCount() - 1;
            tableItens.setRowSelectionInterval(index, index);

            //rolar para o item (forçar visibilidade)
            tableItens.scrollRectToVisible(tableItens.getCellRect(index, 0, true));

            exibirItemAtual();

            //resetar campos
            txtCodigo.setText("");
            txtQuantidade.setText("1,000");
            txtValor.setText("0");
            txtCodigo.requestFocus();

            produto = null;

        }
    }

    private void excluirItem() {
        int index = tableItens.getSelectedRow();
        if (index > -1) {
            MovimentoFisico itemExcluir = vendaJTableModel.getRow(index);

            //verificar valores antes de excluir
            if (itemExcluir.getSubtotal().compareTo(venda.getTotalEmAberto()) > 0) {
                JOptionPane.showMessageDialog(rootPane, "Subtotal do item a ser excluído é maior que o total em aberto");
            } else {

                
                venda.setMovimentosFisicos(vendaItens);

                //Marcar item como excluído
                itemExcluir = movimentoFisicoDAO.remove(itemExcluir);

                venda.addMovimentoFisico(itemExcluir);

                venda = vendaDAO.save(venda);
                
                //em.refresh(venda);

                vendaItens = venda.getMovimentosFisicosSaida();

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
        em.getTransaction().begin();
        vendaJTableModel.clear();
        vendaJTableModel.addList(vendaItens);
        em.getTransaction().commit();
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
                txtItemPosicionado.setText(item);

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
                txtItemPosicionado.setText("");
            }

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

        txtRecebido.setText(Decimal.toString(venda.getTotalRecebido().add(venda.getTotalAPrazo())));

        txtEmAberto.setText(Decimal.toString(venda.getTotalEmAberto()));

    }

    private void encerrarVenda() {
        if (venda.getEncerramento() != null) {
            JOptionPane.showMessageDialog(rootPane, "Esta venda já foi encerrada.");
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
        if(venda.isOrcamento()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não é possível receber em ORÇAMENTO.", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            Caixa lastCaixa = new CaixaDAO().getLastCaixa();
            if (lastCaixa == null || lastCaixa.getEncerramento() != null) {
                JOptionPane.showMessageDialog(rootPane, "Não há turno de caixa aberto. Não é possível realizar recebimentos.", "Atenção", JOptionPane.WARNING_MESSAGE);
            } else if (venda.getTotalEmAberto().compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(rootPane, "Não há valor em aberto.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                RecebimentoView recebimentoView = new RecebimentoView(venda);
                /*
                if (recebimentoView.getParcela() != null) {
                    parcelas.add(recebimentoView.getParcela());

                    venda.setParcelas(parcelas);

                    exibirTotais();
                }*/

                exibirTotais();
            }
        }
    }

    private void parcelar() {
        if (venda.getTotalEmAberto().compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(rootPane, "Não há valor em aberto.","Aviso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            ParcelamentoView parcelamentoView = new ParcelamentoView(MAIN_VIEW, venda);
            exibirTotais();
            exibirCliente();
        }
    }

    private void imprimir() {
        salvar();

        PrintPDFBox pPDF = new PrintPDFBox();
        //VENDA, PEDIDO, COMANDA, ORDEM_DE_SERVICO, LOCAÇÃO
        if(venda.getVendaTipo().equals(VendaTipo.ORDEM_DE_SERVICO)) {
            if(IMPRESSORA_FORMATO_PADRAO.equals(ImpressoraFormato.CUPOM.toString())) {
                String pdfFilePath = TO_PRINTER_PATH + "ORDEM DE SERVIÇO " + venda.getId() + "_" + System.currentTimeMillis() + ".pdf";
                CriarPDF.criarVenda80mm(venda, pdfFilePath);
                pPDF.print(pdfFilePath, IMPRESSORA_A4);
            } else {
                pPDF.print(new CriarPdfA4().gerarOrdemDeServico(venda), IMPRESSORA_A4);
            }
            
        } else if(venda.getVendaTipo().equals(VendaTipo.LOCAÇÃO)) {
            RelatorioPdf.geraRelatorio(venda);
            //pPDF.print(new CriarPdfA4().gerarLocacao(venda), IMPRESSORA_A4);
        
        } else if(venda.getVendaTipo().equals(VendaTipo.VENDA)) {
            if(IMPRESSORA_FORMATO_PADRAO.equals(ImpressoraFormato.CUPOM.toString())) {
                String pdfFilePath = TO_PRINTER_PATH + "VENDA " + venda.getId() + "_" + System.currentTimeMillis() + ".pdf";
                CriarPDF.criarVenda80mm(venda, pdfFilePath);
                pPDF.print(pdfFilePath, IMPRESSORA_A4);
            } else {
                pPDF.print(new CriarPdfA4().gerarOrdemDeServico(venda), IMPRESSORA_A4);
            }
            
        } else if(venda.getVendaTipo().equals(VendaTipo.PEDIDO)) {
            if(IMPRESSORA_FORMATO_PADRAO.equals(ImpressoraFormato.CUPOM.toString())) {
                String pdfFilePath = TO_PRINTER_PATH + "PEDIDO " + venda.getId() + "_" + System.currentTimeMillis() + ".pdf";
                CriarPDF.criarVenda80mm(venda, pdfFilePath);
                pPDF.print(pdfFilePath, IMPRESSORA_A4);
            } else {
                pPDF.print(new CriarPdfA4().gerarOrdemDeServico(venda), IMPRESSORA_A4);
            }
            
        } else {
            if(IMPRESSORA_FORMATO_PADRAO.equals(ImpressoraFormato.CUPOM.toString())) {
                String pdfFilePath = TO_PRINTER_PATH + "ELSE " + venda.getId() + "_" + System.currentTimeMillis() + ".pdf";
                CriarPDF.criarVenda80mm(venda, pdfFilePath);
                pPDF.print(pdfFilePath, IMPRESSORA_A4);
            } else {
                pPDF.print(new CriarPdfA4().gerarOrdemDeServico(venda), IMPRESSORA_A4);
            }
        }

        new Toast("Gerando documento para impressão...");
        
        
    }

    private void imprimirTicketComanda() {
        salvar();

        String pdfFilePath = TO_PRINTER_PATH + "TICKET_COMANDA " + venda.getId() + "_" + System.currentTimeMillis() + ".pdf";
        CriarPDF.criarTicketComanda(venda, pdfFilePath);

        new Toast("Imprimindo...");

        PrintPDFBox pPDF = new PrintPDFBox();
        pPDF.print(pdfFilePath, IMPRESSORA_CUPOM);
    }
    
    private void escolherImpressao() {
        salvar();

        EscolherImpressao escolherImpressao = new EscolherImpressao(venda);
    }

    private void gerarCupomSat() {
        SatInformarCpfView satCpf = new SatInformarCpfView(MAIN_VIEW, venda);
        satCpf.setVisible(true);
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

    private void transferir() {
        TransferirComandaView transferirComandaView = new TransferirComandaView(MAIN_VIEW, venda);
        em.refresh(venda);
        comanda = venda.getComanda();
        exibirTipo();
    }

    private void entrega() {
        EntregaDevolucaoView entregaDevolucaoView = new EntregaDevolucaoView(venda);
    }

    private void confirmarEntrega() {
        if(venda.isOrcamento()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não é possível confirmar entrega para orçamentos", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            ConfirmarEntregaDevolucaoView confirmar = new ConfirmarEntregaDevolucaoView(vendaItens);
        }
    }
    
    private void confirmarDevolucao() {
        if(venda.isOrcamento()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não é possível confirmar devolução para orçamentos", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            ConfirmarEntregaDevolucaoView confirmar = new ConfirmarEntregaDevolucaoView(venda.getMovimentosFisicosDevolucao());
        }
    }
    
    private void cancelarVenda() {
        CancelarVenda cancelarVenda = new CancelarVenda(venda);
    }
    
    private void aceitarOrçamento() {
        int resposta = JOptionPane.showConfirmDialog(MAIN_VIEW, "Aceitar orçamento? Este procedimento é irreversível.", "Atenção", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(resposta == JOptionPane.OK_OPTION) {
            venda.setOrcamento(false);
            venda = vendaDAO.save(venda);
            configurarPorTipo();
        }
    }
    
    private void pesquisarCliente() {
        PessoaPesquisaView pesquisa = new PessoaPesquisaView();

        if (pesquisa.getCliente() != null) {
            venda.setCliente(pesquisa.getCliente());
            salvar();
        }
    }
    
    private void exibirCliente() {
        if(venda.getCliente() != null) {
            txtCliente.setText(venda.getCliente().getId() + " - " + venda.getCliente().getNome());
            txtClienteEndereco.setText(venda.getCliente().getEnderecoCompleto());
        } else {
            txtCliente.setText("NÃO INFORMADO");
            txtClienteEndereco.setText("");
        }
    }
    
    private void removerCliente() {
        if (venda.getTotalRecebidoAPrazo().compareTo(BigDecimal.ZERO) > 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Já existem parcelas recebidas. Não é possível remover o cliente.", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            venda.setCliente(null);
            salvar();
        }
    }
    
    private void observacao() {
        JOptionPane.showMessageDialog(MAIN_VIEW, "OBS");
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
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtObservacao = new javax.swing.JTextArea();
        txtCliente = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        btnRemoverCliente = new javax.swing.JButton();
        txtClienteEndereco = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        pnlGeral = new javax.swing.JPanel();
        btnPesquisar = new javax.swing.JButton();
        btnReceber = new javax.swing.JButton();
        btnReceber1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        btnAceitarOrçamento = new javax.swing.JButton();
        btnGerarCarne = new javax.swing.JButton();
        pnlComanda = new javax.swing.JPanel();
        btnImprimirTicket = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        btnEncerrarVenda = new javax.swing.JButton();
        pnlSat = new javax.swing.JPanel();
        jButton6 = new javax.swing.JButton();
        btnCancelarCupom = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        txtTipo = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtEncerramento = new javax.swing.JTextField();
        txtVendaId = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtAbertura = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtInativo = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtItemPosicionado = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableItens = new javax.swing.JTable();
        pnlEntregaDevolucao = new javax.swing.JPanel();
        btnEntregaDevolucao = new javax.swing.JButton();
        btnConfirmarEntrega = new javax.swing.JButton();
        btnConfirmarDevolucao = new javax.swing.JButton();

        setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setResizable(true);
        setTitle("Venda");
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

        jLabel5.setText("Código [foco F2] | Excluir item [DEL]");

        jLabel6.setText("Quantidade [foco F3]");

        txtValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValor.setText("0,00");
        txtValor.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtValor.setName("decimal"); // NOI18N
        txtValor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorKeyReleased(evt);
            }
        });

        jLabel15.setText("Valor Unitário");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(0, 116, Short.MAX_VALUE))
                    .addComponent(txtQuantidade))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addGap(10, 10, 10)
                .addComponent(btnInserir, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(btnInserir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
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
                        .addGap(0, 0, Short.MAX_VALUE)))
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

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtObservacao.setColumns(20);
        txtObservacao.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
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

        txtCliente.setEditable(false);
        txtCliente.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtCliente.setText("NÃO INFORMADO");

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/user.png"))); // NOI18N
        jButton7.setText("F5 CLIENTE:");
        jButton7.setContentAreaFilled(false);
        jButton7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton7.setIconTextGap(10);
        jButton7.setPreferredSize(new java.awt.Dimension(180, 49));
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        btnRemoverCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/resource/img/cancel.png"))); // NOI18N
        btnRemoverCliente.setToolTipText("Remover Cliente");
        btnRemoverCliente.setContentAreaFilled(false);
        btnRemoverCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverClienteActionPerformed(evt);
            }
        });

        txtClienteEndereco.setEditable(false);
        txtClienteEndereco.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel2.setText("Observação");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 731, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCliente)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnRemoverCliente))
                    .addComponent(txtClienteEndereco)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemoverCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtClienteEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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

        btnReceber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/creditcards.png"))); // NOI18N
        btnReceber.setText("F7 RECEBER");
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
        btnCancelar.setText("CANCELAR VENDA");
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

        btnGerarCarne.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/printer.png"))); // NOI18N
        btnGerarCarne.setText("GERAR CARNÊ");
        btnGerarCarne.setContentAreaFilled(false);
        btnGerarCarne.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGerarCarne.setIconTextGap(10);
        btnGerarCarne.setPreferredSize(new java.awt.Dimension(180, 49));
        btnGerarCarne.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGerarCarne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGerarCarneActionPerformed(evt);
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
                    .addComponent(btnReceber, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                    .addComponent(btnReceber1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAceitarOrçamento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGerarCarne, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlGeralLayout.setVerticalGroup(
            pnlGeralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReceber, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReceber1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGerarCarne, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAceitarOrçamento, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlComanda.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnImprimirTicket.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/printer.png"))); // NOI18N
        btnImprimirTicket.setText("Ctrl+F10 TICKET");
        btnImprimirTicket.setContentAreaFilled(false);
        btnImprimirTicket.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnImprimirTicket.setIconTextGap(10);
        btnImprimirTicket.setPreferredSize(new java.awt.Dimension(180, 49));
        btnImprimirTicket.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimirTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirTicketActionPerformed(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/wand.png"))); // NOI18N
        jButton5.setText("F6 TRANSFERIR");
        jButton5.setContentAreaFilled(false);
        jButton5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton5.setIconTextGap(10);
        jButton5.setPreferredSize(new java.awt.Dimension(180, 49));
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        btnEncerrarVenda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/flag_green.png"))); // NOI18N
        btnEncerrarVenda.setText("F12 ENCERRAR");
        btnEncerrarVenda.setContentAreaFilled(false);
        btnEncerrarVenda.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEncerrarVenda.setIconTextGap(10);
        btnEncerrarVenda.setPreferredSize(new java.awt.Dimension(180, 49));
        btnEncerrarVenda.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEncerrarVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEncerrarVendaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlComandaLayout = new javax.swing.GroupLayout(pnlComanda);
        pnlComanda.setLayout(pnlComandaLayout);
        pnlComandaLayout.setHorizontalGroup(
            pnlComandaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlComandaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlComandaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnImprimirTicket, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEncerrarVenda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlComandaLayout.setVerticalGroup(
            pnlComandaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlComandaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnImprimirTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEncerrarVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlSat.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/printer.png"))); // NOI18N
        jButton6.setText("F11 CFe SAT");
        jButton6.setContentAreaFilled(false);
        jButton6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton6.setIconTextGap(10);
        jButton6.setPreferredSize(new java.awt.Dimension(180, 49));
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        btnCancelarCupom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/cancel.png"))); // NOI18N
        btnCancelarCupom.setText("CANCELAR ÚLTIMO CUPOM");
        btnCancelarCupom.setContentAreaFilled(false);
        btnCancelarCupom.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCancelarCupom.setIconTextGap(10);
        btnCancelarCupom.setPreferredSize(new java.awt.Dimension(180, 49));
        btnCancelarCupom.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancelarCupom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarCupomActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSatLayout = new javax.swing.GroupLayout(pnlSat);
        pnlSat.setLayout(pnlSatLayout);
        pnlSatLayout.setHorizontalGroup(
            pnlSatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSatLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                    .addComponent(btnCancelarCupom, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlSatLayout.setVerticalGroup(
            pnlSatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSatLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelarCupom, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtTipo.setEditable(false);
        txtTipo.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtTipo.setForeground(java.awt.Color.red);
        txtTipo.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setText("Encerramento");

        txtEncerramento.setEditable(false);
        txtEncerramento.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtEncerramento.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        txtVendaId.setEditable(false);
        txtVendaId.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtVendaId.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtVendaId.setText("NOVA");

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
                            .addComponent(jLabel9)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(txtEncerramento, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtVendaId)
                            .addComponent(txtAbertura)))
                    .addComponent(txtInativo))
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
                    .addComponent(txtVendaId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAbertura)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEncerramento)
                    .addComponent(jLabel9))
                .addContainerGap())
        );

        txtItemPosicionado.setEditable(false);
        txtItemPosicionado.setBackground(javax.swing.UIManager.getDefaults().getColor("TextField.disabledBackground"));
        txtItemPosicionado.setColumns(20);
        txtItemPosicionado.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtItemPosicionado.setLineWrap(true);
        txtItemPosicionado.setRows(2);
        txtItemPosicionado.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jScrollPane3.setViewportView(txtItemPosicionado);

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlGeral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pnlComanda, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(pnlSat, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlEntregaDevolucao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {pnlComanda, pnlGeral, pnlSat});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlGeral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlComanda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlSat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlEntregaDevolucao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(45, Short.MAX_VALUE))
        );

        setBounds(5, 25, 1300, 724);
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        //id
        tableItens.getColumnModel().getColumn(0).setPreferredWidth(1);
        //tableItens.getColumnModel().getColumn(0).setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        //número
        tableItens.getColumnModel().getColumn(1).setPreferredWidth(40);
        tableItens.getColumnModel().getColumn(1).setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        //código
        tableItens.getColumnModel().getColumn(2).setPreferredWidth(80);
        tableItens.getColumnModel().getColumn(2).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //nome
        tableItens.getColumnModel().getColumn(3).setPreferredWidth(300);
        //quantidade
        tableItens.getColumnModel().getColumn(4).setPreferredWidth(100);
        tableItens.getColumnModel().getColumn(4).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //unidade comercial de venda
        tableItens.getColumnModel().getColumn(5).setPreferredWidth(60);
        //valor
        tableItens.getColumnModel().getColumn(6).setPreferredWidth(100);
        tableItens.getColumnModel().getColumn(6).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //subtotal
        tableItens.getColumnModel().getColumn(7).setPreferredWidth(100);
        tableItens.getColumnModel().getColumn(7).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tableItens.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                exibirItemAtual();
            }

        });

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
        
        
        venda = null;
        vendaItens = null;
        vendaDAO = null;
        movimentoFisicoDAO = null;
        produtoDAO = null;

        vendaViews.remove(this);
        
        
    }//GEN-LAST:event_formInternalFrameClosed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing

    }//GEN-LAST:event_formInternalFrameClosing

    private void btnEncerrarVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEncerrarVendaActionPerformed
        encerrarVenda();
    }//GEN-LAST:event_btnEncerrarVendaActionPerformed

    private void btnReceberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReceberActionPerformed
        receber();
    }//GEN-LAST:event_btnReceberActionPerformed

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
        exibirRecebimentos();
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

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        gerarCupomSat();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void btnCancelarCupomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarCupomActionPerformed
        SATCancelarUltimoCupom satCancelar = new SATCancelarUltimoCupom(MAIN_VIEW);
    }//GEN-LAST:event_btnCancelarCupomActionPerformed

    private void btnReceber1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReceber1ActionPerformed
        parcelar();
    }//GEN-LAST:event_btnReceber1ActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        txtCodigo.requestFocus();
    }//GEN-LAST:event_formFocusGained

    private void txtObservacaoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtObservacaoPropertyChange

    }//GEN-LAST:event_txtObservacaoPropertyChange

    private void btnImprimirTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirTicketActionPerformed
        imprimirTicketComanda();
    }//GEN-LAST:event_btnImprimirTicketActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        transferir();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void txtObservacaoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtObservacaoFocusLost
        System.out.println("txtObservacaoFocusLost...");
        salvar();
    }//GEN-LAST:event_txtObservacaoFocusLost

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        exibirComandas();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void btnEntregaDevolucaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntregaDevolucaoActionPerformed
        entrega();
    }//GEN-LAST:event_btnEntregaDevolucaoActionPerformed

    private void btnConfirmarEntregaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarEntregaActionPerformed
        confirmarEntrega();
    }//GEN-LAST:event_btnConfirmarEntregaActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        cancelarVenda();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnConfirmarDevolucaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarDevolucaoActionPerformed
        confirmarDevolucao();
    }//GEN-LAST:event_btnConfirmarDevolucaoActionPerformed

    private void btnAceitarOrçamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceitarOrçamentoActionPerformed
        aceitarOrçamento();
    }//GEN-LAST:event_btnAceitarOrçamentoActionPerformed

    private void btnGerarCarneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGerarCarneActionPerformed
        if(venda.getParcelasAPrazo().isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem parcelas para gerar carnê", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            new Toast("Gerando carnê...");
            Carne.gerarCarne(venda.getParcelasAPrazo());
        }
    }//GEN-LAST:event_btnGerarCarneActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        pesquisarCliente();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void btnRemoverClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverClienteActionPerformed
        removerCliente();
    }//GEN-LAST:event_btnRemoverClienteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceitarOrçamento;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnCancelarCupom;
    private javax.swing.JButton btnConfirmarDevolucao;
    private javax.swing.JButton btnConfirmarEntrega;
    private javax.swing.JButton btnEncerrarVenda;
    private javax.swing.JButton btnEntregaDevolucao;
    private javax.swing.JButton btnGerarCarne;
    private javax.swing.JButton btnImprimirTicket;
    private javax.swing.JButton btnInserir;
    private javax.swing.JButton btnPesquisar;
    private javax.swing.JButton btnReceber;
    private javax.swing.JButton btnReceber1;
    private javax.swing.JButton btnRecebimentoLista;
    private javax.swing.JButton btnRemoverCliente;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
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
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel pnlComanda;
    private javax.swing.JPanel pnlEntregaDevolucao;
    private javax.swing.JPanel pnlGeral;
    private javax.swing.JPanel pnlSat;
    private javax.swing.JTable tableItens;
    private javax.swing.JTextField txtAbertura;
    private javax.swing.JFormattedTextField txtAcrescimo;
    private javax.swing.JFormattedTextField txtAcrescimoPercentual;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JTextField txtClienteEndereco;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JFormattedTextField txtDesconto;
    private javax.swing.JFormattedTextField txtDescontoPercentual;
    private javax.swing.JFormattedTextField txtEmAberto;
    private javax.swing.JTextField txtEncerramento;
    private javax.swing.JTextField txtInativo;
    private javax.swing.JTextArea txtItemPosicionado;
    private javax.swing.JTextArea txtObservacao;
    private javax.swing.JFormattedTextField txtQuantidade;
    private javax.swing.JFormattedTextField txtRecebido;
    private javax.swing.JTextField txtTipo;
    private javax.swing.JFormattedTextField txtTotal;
    private javax.swing.JFormattedTextField txtTotalItens;
    private javax.swing.JFormattedTextField txtValor;
    private javax.swing.JTextField txtVendaId;
    // End of variables declaration//GEN-END:variables
}
