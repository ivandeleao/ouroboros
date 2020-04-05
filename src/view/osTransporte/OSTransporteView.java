package view.osTransporte;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JFormattedTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import model.mysql.bean.principal.pessoa.PessoaTipo;
import model.jtable.documento.OSTItensJTableModel;
import model.mysql.bean.principal.documento.OSTransporte;
import model.mysql.bean.principal.documento.OSTransporteItem;
import model.mysql.dao.principal.OSTransporteDAO;
import model.mysql.dao.principal.OSTransporteItemDAO;
import static ouroboros.Constants.*;
import util.JSwing;
import static ouroboros.Ouroboros.MAIN_VIEW;
import printing.documento.OSTransportePrint;
import util.Cor;
import util.Decimal;
import util.Document.MonetarioDocument;
import util.jTableFormat.LineWrapCellRenderer;
import view.funcionario.FuncionarioPesquisaView;
import view.pessoa.PessoaPesquisaView;
import view.veiculo.VeiculoPesquisaView;

public class OSTransporteView extends javax.swing.JInternalFrame {

    long start;

    private static List<OSTransporteView> ostViews = new ArrayList<>(); //instâncias

    private OSTransporte ost;
    private OSTransporteDAO ostDAO = new OSTransporteDAO();
    private OSTransporteItemDAO ostItemDAO = new OSTransporteItemDAO();

    private final OSTItensJTableModel ostItensJTableModel = new OSTItensJTableModel();

    /**
     * Creates new form Venda
     */
    public OSTransporteView() {
        initComponents();
        JSwing.startComponentsBehavior(this);
    }

    public OSTransporteView(OSTransporte ost) {
        start = System.currentTimeMillis();
        initComponents();
        JSwing.startComponentsBehavior(this);

        this.ost = ost;

        if (ost.getId() != null) {

            txtOSTId.setText(ost.getId().toString());

            txtSolicitanteNome.setText(ost.getSolicitanteNome());
            txtSolicitanteSetor.setText(ost.getSolicitanteSetor());

            txtObservacao.setText(ost.getObservacao());

            carregarDesconto();
            exibirTotais();

            carregarTabela();

        }

        formatarBotoesAcrescimoDesconto();

        formatarTabela();

        exibirRemetente();
        exibirMotorista();

        exibirVeiculo();

        definirAtalhos();

        long elapsed = System.currentTimeMillis() - start;
        lblMensagem.setText("Consulta realizada em " + elapsed + "ms");
    }

    private void formatarBotoesAcrescimoDesconto() {

        if (ost.getId() == null) {
            btnDescontoTipo.setBackground(Cor.AZUL);
            btnDescontoTipo.setBackground(Cor.AZUL);

        } else {

            if (ost.getDescontoMonetario().compareTo(BigDecimal.ZERO) > 0) {
                btnDescontoTipo.setText("$");
                btnDescontoTipo.setBackground(Cor.LARANJA);
            } else {
                btnDescontoTipo.setText("%");
                btnDescontoTipo.setBackground(Cor.AZUL);
            }

        }
    }

    private void formatarTabela() {

        JFormattedTextField txt = new JFormattedTextField();
        txt.setHorizontalAlignment(SwingConstants.RIGHT);
        txt.setFont(tblItens.getFont());
        txt.setDocument(new MonetarioDocument());
        DefaultCellEditor decimalEditor = new DefaultCellEditor(txt);

        tblItens.setModel(ostItensJTableModel);

        tblItens.setRowHeight(30);
        tblItens.setIntercellSpacing(new Dimension(10, 10));
        tblItens.setDefaultRenderer(String.class, new LineWrapCellRenderer());
        //id
        tblItens.getColumnModel().getColumn(0).setPreferredWidth(1);

        tblItens.getColumn("Id").setPreferredWidth(40);
        tblItens.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblItens.getColumn("Descrição").setPreferredWidth(200);

        tblItens.getColumn("Valor").setPreferredWidth(100);
        tblItens.getColumn("Valor").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        tblItens.getColumn("Valor").setCellEditor(decimalEditor);

        tblItens.getColumn("Motorista").setPreferredWidth(100);
        tblItens.getColumn("Motorista").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        tblItens.getColumn("Motorista").setCellEditor(decimalEditor);

        tblItens.getColumn("Pedágio").setPreferredWidth(100);
        tblItens.getColumn("Pedágio").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        tblItens.getColumn("Pedágio").setCellEditor(decimalEditor);

        tblItens.getColumn("% Pedágio").setPreferredWidth(100);
        tblItens.getColumn("% Pedágio").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        tblItens.getColumn("% Pedágio").setCellEditor(decimalEditor);

        tblItens.getColumn("Motorista").setPreferredWidth(100);
        tblItens.getColumn("Motorista").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        tblItens.getColumn("Motorista").setCellEditor(decimalEditor);

        tblItens.getColumn("% Motorista").setPreferredWidth(100);
        tblItens.getColumn("% Motorista").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        tblItens.getColumn("% Motorista").setCellEditor(decimalEditor);
        
        tblItens.getColumn("Adicional").setPreferredWidth(100);
        tblItens.getColumn("Adicional").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        tblItens.getColumn("Adicional").setCellEditor(decimalEditor);

        tblItens.getColumn("% Adicional").setPreferredWidth(100);
        tblItens.getColumn("% Adicional").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        tblItens.getColumn("% Adicional").setCellEditor(decimalEditor);
        
        tblItens.getColumn("Total").setPreferredWidth(100);
        tblItens.getColumn("Total").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblItens.getColumn("Destinatário").setPreferredWidth(300);
        tblItens.getColumn("Telefone").setPreferredWidth(100);
        tblItens.getColumn("Cidade").setPreferredWidth(100);
        tblItens.getColumn("Endereço").setPreferredWidth(400);

        tblItens.getColumn("Editar").setPreferredWidth(50);

        if (ostItensJTableModel.getRowCount() > 0) {
            tblItens.setRowSelectionInterval(0, 0);
        }

        tblItens.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getOldValue() != null && !evt.getOldValue().equals(evt.getNewValue())) {
                    salvar();
                }
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
                /*                case "F2":
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
                    break;*/
            }
        }
    }

    public static OSTransporteView getInstance(OSTransporte osTransporte) {
        for (OSTransporteView ostView : ostViews) {
            if (ostView.ost.getId() != null && ostView.ost.getId().equals(osTransporte.getId())) {
                return ostView;
            }
        }

        ostViews.add(new OSTransporteView(osTransporte));

        return ostViews.get(ostViews.size() - 1);
    }

    private void fechar() {
        salvar();
        MAIN_VIEW.removeTab(this.getName());
        MAIN_VIEW.removeView(this.getName());
    }

    private void salvar() {
        for (OSTransporteItem i : ost.getOsTranporteItens()) {
            System.out.println("item: " + i.getId() + " - " + i.getDescricao());
        }

        ost.setSolicitanteNome(txtSolicitanteNome.getText());
        ost.setSolicitanteSetor(txtSolicitanteSetor.getText());

        ost.setObservacao(txtObservacao.getText());

        ost = ostDAO.save(ost);

        //txtStatus.setText(osTransporte.getVendaStatus().toString());
        //txtStatus.setBackground(osTransporte.getVendaStatus().getCor());
        txtOSTId.setText(ost.getId().toString());

        exibirRemetente();
        exibirMotorista();
        exibirVeiculo();

        exibirTotais();
    }

    private void carregarTabela() {

        ostItensJTableModel.clear();
        ostItensJTableModel.addList(ost.getOsTranporteItens());

        if (tblItens.getRowCount() > 0) {
            int index = tblItens.getRowCount() - 1;
            tblItens.setRowSelectionInterval(index, index);

            //rolar para o item (forçar visibilidade)
            tblItens.scrollRectToVisible(tblItens.getCellRect(index, 0, true));
        }

        //lblRegistros.setText("Itens: " + String.valueOf(osTransporte.getMovimentosFisicosSaida().size()));
    }

    private void atualizarTabela() {
        int row = tblItens.getSelectedRow();
        ostItensJTableModel.fireTableDataChanged();
        if (row > -1) {
            tblItens.addRowSelectionInterval(row, row);
        }
    }

    private void carregarDesconto() {
        if (ost.getDescontoMonetario().compareTo(BigDecimal.ZERO) > 0) {
            txtDesconto.setText(Decimal.toString(ost.getDescontoMonetario()));
        } else {
            txtDesconto.setText(Decimal.toString(ost.getDescontoPercentual()));
        }
    }

    private void definirDesconto() {
        BigDecimal descontoMonetario = BigDecimal.ZERO;
        BigDecimal descontoPercentual = BigDecimal.ZERO;

        if (btnDescontoTipo.getText().equals("%")) {
            descontoPercentual = Decimal.fromString(txtDesconto.getText());
        } else {
            descontoMonetario = Decimal.fromString(txtDesconto.getText());
        }

        ost.setDescontoMonetario(descontoMonetario);
        ost.setDescontoPercentual(descontoPercentual);

        salvar();

    }

    private void exibirTotais() {
        //ost.setTotal();

        txtTotalItens.setText(Decimal.toString(ost.getTotalItens()));
        txtTotal.setText(Decimal.toString(ost.getTotal()));

    }

    private void receber() {
        /*if (osTransporte.isOrcamento()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não é possível receber em ORÇAMENTO.", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            Caixa lastCaixa = new CaixaDAO().getLastCaixa();
            if (lastCaixa == null || lastCaixa.getEncerramento() != null) {
                JOptionPane.showMessageDialog(rootPane, "Não há turno de caixa aberto. Não é possível realizar recebimentos.", "Atenção", JOptionPane.WARNING_MESSAGE);
            } else if (osTransporte.getTotalEmAberto().compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(rootPane, "Não há valor em aberto.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                validarCredito(false);
                RecebimentoView recebimentoView = new RecebimentoView(osTransporte);
                exibirTotais();
            }
        }*/
    }

    private void parcelar() {
        /*if (validarCredito(true)) {
            ParcelamentoView parcelamentoView = new ParcelamentoView(osTransporte);
            exibirTotais();
            exibirRemetente();
        }*/
    }

    private void imprimirA4() {
        salvar();

        OSTransportePrint.gerarA4(ost);

    }

    private void exibirRecebimentos() {
        /*if (osTransporte == null || osTransporte.getRecebimentos().isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, "Não há recebimentos", "Atenção", JOptionPane.INFORMATION_MESSAGE);
        } else {
            RecebimentoListaView recebimentoListaView = new RecebimentoListaView(MAIN_VIEW, true, osTransporte);
            recebimentoListaView.setLocationRelativeTo(this);
            recebimentoListaView.setVisible(true);
        }*/
    }

    private void pesquisarRemetente() {
        PessoaPesquisaView pesquisa = new PessoaPesquisaView(PessoaTipo.CLIENTE);

        if (pesquisa.getPessoa() != null) {
            ost.setRemetente(pesquisa.getPessoa());
            salvar();
        }
    }

    private void exibirRemetente() {
        if (ost.getRemetente() != null) {
            txtRemetenteNome.setText(ost.getRemetente().getId() + " - " + ost.getRemetente().getNome());
            txtRemetenteNome.setCaretPosition(0);

        } else {
            txtRemetenteNome.setText("NÃO INFORMADO");
        }
    }

    private void pesquisarFuncionario() {
        FuncionarioPesquisaView pesquisa = new FuncionarioPesquisaView();

        if (pesquisa.getFuncionario() != null) {
            ost.setMotorista(pesquisa.getFuncionario());
            salvar();
        }
    }

    private void exibirMotorista() {
        System.out.println("motorista: " + ost.getMotorista());

        if (ost.getMotorista() != null) {
            txtMotoristaNome.setText(ost.getMotorista().getId() + " - " + ost.getMotorista().getNome());
        } else {
            txtMotoristaNome.setText("NÃO INFORMADO");
        }
    }

    private void removerMotorista() {
        ost.setMotorista(null);
        salvar();
    }

    private void pesquisarVeiculo() {
        VeiculoPesquisaView pesquisa = new VeiculoPesquisaView();

        if (pesquisa.getVeiculo() != null) {
            ost.setVeiculo(pesquisa.getVeiculo());
            salvar();
        }
    }

    private void exibirVeiculo() {
        if (ost.getVeiculo() != null) {
            txtVeiculo.setText(ost.getVeiculo().getPlaca() + " - " + ost.getVeiculo().getModelo());

        } else {
            txtVeiculo.setText("NÃO INFORMADO");

        }
    }

    private void removerVeiculo() {
        ost.setVeiculo(null);
        salvar();
    }

    private void adicionarItem() {

        OstItemView ostItemView = new OstItemView(new OSTransporteItem());

        OSTransporteItem ostItem = ostItemView.getOSTransporteItem();

        if (ostItem.getId() != null) {
            ost.addOSTransporteItem(ostItem);
            salvar();
            carregarTabela();
        }

    }

    private void removerItem() {
        int index = tblItens.getSelectedRow();
        if (index > -1) {
            ost.removeOSTransporteItem(ostItensJTableModel.getRow(index));
            ostItemDAO.remove(ostItensJTableModel.getRow(index));
            salvar();

            carregarTabela();
            //ostItensJTableModel.removeRow(index);
        }

    }

    private void alternarDescontoTipo() {

        if (btnDescontoTipo.getText().equals("%")) {
            btnDescontoTipo.setText("$");
            btnDescontoTipo.setBackground(Cor.LARANJA);

        } else {
            btnDescontoTipo.setText("%");
            btnDescontoTipo.setBackground(Cor.AZUL);

        }

        btnDescontoTipo.repaint();
        
        definirDesconto();

        salvar();
    }

    private void processo() {
        /*new DocumentoStatusView(osTransporte);
        salvar();*/
    }

    private void editarItem() {
        if (tblItens.getSelectedRow() > -1) {
            OSTransporteItem ostItem = ostItensJTableModel.getRow(tblItens.getSelectedRow());
            new OstItemView(ostItem);

            salvar();
            carregarTabela();
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

        pnlPessoas = new javax.swing.JPanel();
        txtRemetenteNome = new javax.swing.JTextField();
        btnCliente = new javax.swing.JButton();
        btnMotorista = new javax.swing.JButton();
        btnRemoverMotorista = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        txtSolicitanteNome = new javax.swing.JTextField();
        txtSolicitanteSetor = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        btnRemoverRemetente = new javax.swing.JButton();
        btnVeiculo = new javax.swing.JButton();
        txtVeiculo = new javax.swing.JTextField();
        btnRemoverMotorista1 = new javax.swing.JButton();
        txtMotoristaNome = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblItens = new javax.swing.JTable();
        pnlObservacao = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtObservacao = new javax.swing.JTextArea();
        jLabel37 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        btnCancelarDocumento = new javax.swing.JButton();
        txtOSTId = new javax.swing.JTextField();
        btnImprimirA4 = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        pnlValores = new javax.swing.JPanel();
        txtTotalItens = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        btnDescontoTipo = new javax.swing.JButton();
        txtDesconto = new javax.swing.JFormattedTextField();
        jLabel39 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JFormattedTextField();
        pnlRecebimento = new javax.swing.JPanel();
        btnReceber = new javax.swing.JButton();
        txtRecebido = new javax.swing.JFormattedTextField();
        btnReceber1 = new javax.swing.JButton();
        txtFaturado = new javax.swing.JFormattedTextField();
        pnlEmAberto = new javax.swing.JPanel();
        txtEmAberto = new javax.swing.JFormattedTextField();
        jLabel38 = new javax.swing.JLabel();
        lblMensagem = new javax.swing.JLabel();
        lblRegistros = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        btnAdicionarItem = new javax.swing.JButton();
        btnRemoverItem = new javax.swing.JButton();

        setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setResizable(true);
        setTitle("OS Transporte");
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

        pnlPessoas.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtRemetenteNome.setEditable(false);
        txtRemetenteNome.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtRemetenteNome.setText("NÃO INFORMADO");
        txtRemetenteNome.setFocusable(false);

        btnCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/user.png"))); // NOI18N
        btnCliente.setText("F5 REMETENTE");
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

        btnMotorista.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-car-20.png"))); // NOI18N
        btnMotorista.setText("MOTORISTA");
        btnMotorista.setContentAreaFilled(false);
        btnMotorista.setFocusable(false);
        btnMotorista.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMotorista.setIconTextGap(10);
        btnMotorista.setPreferredSize(new java.awt.Dimension(180, 49));
        btnMotorista.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMotorista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMotoristaActionPerformed(evt);
            }
        });

        btnRemoverMotorista.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-close-button-20.png"))); // NOI18N
        btnRemoverMotorista.setToolTipText("Remover Veículo");
        btnRemoverMotorista.setContentAreaFilled(false);
        btnRemoverMotorista.setFocusable(false);
        btnRemoverMotorista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverMotoristaActionPerformed(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel25.setText("Solicitante");

        txtSolicitanteNome.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtSolicitanteNome.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSolicitanteNome.setToolTipText("F9 PARA PESQUISAR");
        txtSolicitanteNome.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSolicitanteNomeFocusLost(evt);
            }
        });
        txtSolicitanteNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSolicitanteNomeActionPerformed(evt);
            }
        });
        txtSolicitanteNome.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSolicitanteNomeKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSolicitanteNomeKeyReleased(evt);
            }
        });

        txtSolicitanteSetor.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtSolicitanteSetor.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSolicitanteSetor.setToolTipText("F9 PARA PESQUISAR");
        txtSolicitanteSetor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSolicitanteSetorFocusLost(evt);
            }
        });
        txtSolicitanteSetor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSolicitanteSetorActionPerformed(evt);
            }
        });
        txtSolicitanteSetor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSolicitanteSetorKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSolicitanteSetorKeyReleased(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel26.setText("Setor");

        btnRemoverRemetente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-close-button-20.png"))); // NOI18N
        btnRemoverRemetente.setToolTipText("Remover Veículo");
        btnRemoverRemetente.setContentAreaFilled(false);
        btnRemoverRemetente.setFocusable(false);
        btnRemoverRemetente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverRemetenteActionPerformed(evt);
            }
        });

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

        txtVeiculo.setEditable(false);
        txtVeiculo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtVeiculo.setText("NÃO INFORMADO");
        txtVeiculo.setFocusable(false);

        btnRemoverMotorista1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-close-button-20.png"))); // NOI18N
        btnRemoverMotorista1.setToolTipText("Remover Veículo");
        btnRemoverMotorista1.setContentAreaFilled(false);
        btnRemoverMotorista1.setFocusable(false);
        btnRemoverMotorista1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverMotorista1ActionPerformed(evt);
            }
        });

        txtMotoristaNome.setEditable(false);
        txtMotoristaNome.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtMotoristaNome.setText("NÃO INFORMADO");
        txtMotoristaNome.setFocusable(false);

        javax.swing.GroupLayout pnlPessoasLayout = new javax.swing.GroupLayout(pnlPessoas);
        pnlPessoas.setLayout(pnlPessoasLayout);
        pnlPessoasLayout.setHorizontalGroup(
            pnlPessoasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPessoasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPessoasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnMotorista, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                    .addComponent(btnCliente, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlPessoasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPessoasLayout.createSequentialGroup()
                        .addComponent(txtRemetenteNome, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverRemetente)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtSolicitanteNome, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtSolicitanteSetor, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPessoasLayout.createSequentialGroup()
                        .addComponent(txtMotoristaNome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverMotorista)
                        .addGap(18, 18, 18)
                        .addComponent(btnVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverMotorista1)))
                .addContainerGap())
        );
        pnlPessoasLayout.setVerticalGroup(
            pnlPessoasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPessoasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPessoasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPessoasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(pnlPessoasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtRemetenteNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25)
                            .addComponent(txtSolicitanteNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSolicitanteSetor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26)))
                    .addComponent(btnRemoverRemetente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPessoasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPessoasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnRemoverMotorista, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnMotorista, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnRemoverMotorista1)
                    .addGroup(pnlPessoasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(pnlPessoasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMotoristaNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
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

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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

        txtOSTId.setEditable(false);
        txtOSTId.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtOSTId.setForeground(java.awt.Color.red);
        txtOSTId.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtOSTId.setText("NOVO");

        btnImprimirA4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnImprimirA4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-document-20.png"))); // NOI18N
        btnImprimirA4.setText("IMPRIMIR");
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

        btnSalvar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnSalvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-save-button-20.png"))); // NOI18N
        btnSalvar.setText("SALVAR");
        btnSalvar.setToolTipText("F10 IMPRIMIR A4");
        btnSalvar.setContentAreaFilled(false);
        btnSalvar.setIconTextGap(10);
        btnSalvar.setPreferredSize(new java.awt.Dimension(180, 49));
        btnSalvar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtOSTId, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelarDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnImprimirA4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtOSTId)
                    .addComponent(btnCancelarDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnImprimirA4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        pnlValores.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtTotalItens.setEditable(false);
        txtTotalItens.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalItens.setText("0,00");
        txtTotalItens.setFocusable(false);
        txtTotalItens.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("SUBTOTAL BRUTO");
        jLabel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("DESCONTO");
        jLabel23.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnDescontoTipo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnDescontoTipo.setText("%");
        btnDescontoTipo.setFocusable(false);
        btnDescontoTipo.setPreferredSize(new java.awt.Dimension(55, 25));
        btnDescontoTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDescontoTipoActionPerformed(evt);
            }
        });

        txtDesconto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDesconto.setText("0,00");
        txtDesconto.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtDesconto.setName("decimal"); // NOI18N
        txtDesconto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDescontoKeyReleased(evt);
            }
        });

        jLabel39.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("TOTAL");
        jLabel39.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel39.setOpaque(true);

        txtTotal.setEditable(false);
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtTotal.setText("0,00");
        txtTotal.setFocusable(false);
        txtTotal.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N

        javax.swing.GroupLayout pnlValoresLayout = new javax.swing.GroupLayout(pnlValores);
        pnlValores.setLayout(pnlValoresLayout);
        pnlValoresLayout.setHorizontalGroup(
            pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlValoresLayout.createSequentialGroup()
                .addContainerGap(394, Short.MAX_VALUE)
                .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTotalItens, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlValoresLayout.createSequentialGroup()
                        .addComponent(btnDescontoTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDesconto))
                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlValoresLayout.setVerticalGroup(
            pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlValoresLayout.createSequentialGroup()
                .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlValoresLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(txtTotalItens, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlValoresLayout.createSequentialGroup()
                        .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel23)
                                .addComponent(jLabel16))
                            .addComponent(jLabel39))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnDescontoTipo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtDesconto))
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
                    .addComponent(txtEmAberto, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE))
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

        lblMensagem.setText("Consulta realizada em Xms");

        lblRegistros.setText("Registros exibidos:");

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnAdicionarItem.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnAdicionarItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-add-20.png"))); // NOI18N
        btnAdicionarItem.setText("ADICIONAR");
        btnAdicionarItem.setToolTipText("F10 IMPRIMIR A4");
        btnAdicionarItem.setContentAreaFilled(false);
        btnAdicionarItem.setIconTextGap(10);
        btnAdicionarItem.setPreferredSize(new java.awt.Dimension(180, 49));
        btnAdicionarItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAdicionarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarItemActionPerformed(evt);
            }
        });

        btnRemoverItem.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnRemoverItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-do-not-disturb-20.png"))); // NOI18N
        btnRemoverItem.setText("REMOVER");
        btnRemoverItem.setToolTipText("F10 IMPRIMIR A4");
        btnRemoverItem.setContentAreaFilled(false);
        btnRemoverItem.setIconTextGap(10);
        btnRemoverItem.setPreferredSize(new java.awt.Dimension(180, 49));
        btnRemoverItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemoverItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverItemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(794, Short.MAX_VALUE)
                .addComponent(btnAdicionarItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRemoverItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAdicionarItem, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemoverItem, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlObservacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlRecebimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlPessoas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlValores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlEmAberto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblMensagem)
                        .addGap(18, 18, 18)
                        .addComponent(lblRegistros)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMensagem)
                    .addComponent(lblRegistros))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPessoas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlRecebimento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlObservacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlEmAberto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlValores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        setBounds(5, 25, 1200, 775);
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown


    }//GEN-LAST:event_formComponentShown


    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased

    }//GEN-LAST:event_formKeyReleased

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed

        ost = null;
        ostDAO = null;
        //movimentoFisicoDAO = null;
        //produtoDAO = null;

        ostViews.remove(this);


    }//GEN-LAST:event_formInternalFrameClosed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing

    }//GEN-LAST:event_formInternalFrameClosing

    private void btnReceberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReceberActionPerformed
        receber();
    }//GEN-LAST:event_btnReceberActionPerformed

    private void btnReceber1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReceber1ActionPerformed
        parcelar();
    }//GEN-LAST:event_btnReceber1ActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained

    }//GEN-LAST:event_formFocusGained

    private void btnClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClienteActionPerformed
        pesquisarRemetente();
    }//GEN-LAST:event_btnClienteActionPerformed

    private void txtObservacaoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtObservacaoFocusLost
        salvar();
    }//GEN-LAST:event_txtObservacaoFocusLost

    private void txtObservacaoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtObservacaoPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtObservacaoPropertyChange

    private void btnRemoverMotoristaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverMotoristaActionPerformed
        removerMotorista();
    }//GEN-LAST:event_btnRemoverMotoristaActionPerformed

    private void btnCancelarDocumentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarDocumentoActionPerformed
        //cancelarOrcamento();
    }//GEN-LAST:event_btnCancelarDocumentoActionPerformed

    private void btnImprimirA4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirA4ActionPerformed
        imprimirA4();
    }//GEN-LAST:event_btnImprimirA4ActionPerformed

    private void btnMotoristaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMotoristaActionPerformed
        pesquisarFuncionario();
    }//GEN-LAST:event_btnMotoristaActionPerformed

    private void btnDescontoTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDescontoTipoActionPerformed
        alternarDescontoTipo();
    }//GEN-LAST:event_btnDescontoTipoActionPerformed

    private void txtDescontoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescontoKeyReleased
        definirDesconto();
    }//GEN-LAST:event_txtDescontoKeyReleased

    private void tblItensPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tblItensPropertyChange

    }//GEN-LAST:event_tblItensPropertyChange

    private void tblItensFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblItensFocusGained
        //txtItemCodigo.requestFocus();
    }//GEN-LAST:event_tblItensFocusGained

    private void tblItensMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblItensMouseClicked
        if (tblItens.getSelectedColumn() == 14) {
            editarItem();
            ostItensJTableModel.fireTableRowsUpdated(tblItens.getSelectedRow(), tblItens.getSelectedRow());
            carregarDesconto();
            exibirTotais();
        } else {
            //txtItemCodigo.requestFocus();
        }
    }//GEN-LAST:event_tblItensMouseClicked

    private void txtSolicitanteNomeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSolicitanteNomeFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSolicitanteNomeFocusLost

    private void txtSolicitanteNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSolicitanteNomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSolicitanteNomeActionPerformed

    private void txtSolicitanteNomeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSolicitanteNomeKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSolicitanteNomeKeyPressed

    private void txtSolicitanteNomeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSolicitanteNomeKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSolicitanteNomeKeyReleased

    private void txtSolicitanteSetorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSolicitanteSetorFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSolicitanteSetorFocusLost

    private void txtSolicitanteSetorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSolicitanteSetorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSolicitanteSetorActionPerformed

    private void txtSolicitanteSetorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSolicitanteSetorKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSolicitanteSetorKeyPressed

    private void txtSolicitanteSetorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSolicitanteSetorKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSolicitanteSetorKeyReleased

    private void btnRemoverRemetenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverRemetenteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRemoverRemetenteActionPerformed

    private void btnVeiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVeiculoActionPerformed
        pesquisarVeiculo();
    }//GEN-LAST:event_btnVeiculoActionPerformed

    private void btnRemoverMotorista1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverMotorista1ActionPerformed
        removerVeiculo();
    }//GEN-LAST:event_btnRemoverMotorista1ActionPerformed

    private void btnAdicionarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarItemActionPerformed
        adicionarItem();
    }//GEN-LAST:event_btnAdicionarItemActionPerformed

    private void btnRemoverItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverItemActionPerformed
        removerItem();
    }//GEN-LAST:event_btnRemoverItemActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        salvar();
    }//GEN-LAST:event_btnSalvarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionarItem;
    private javax.swing.JButton btnCancelarDocumento;
    private javax.swing.JButton btnCliente;
    private javax.swing.JButton btnDescontoTipo;
    private javax.swing.JButton btnImprimirA4;
    private javax.swing.JButton btnMotorista;
    private javax.swing.JButton btnReceber;
    private javax.swing.JButton btnReceber1;
    private javax.swing.JButton btnRemoverItem;
    private javax.swing.JButton btnRemoverMotorista;
    private javax.swing.JButton btnRemoverMotorista1;
    private javax.swing.JButton btnRemoverRemetente;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnVeiculo;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel lblMensagem;
    private javax.swing.JLabel lblRegistros;
    private javax.swing.JPanel pnlEmAberto;
    private javax.swing.JPanel pnlObservacao;
    private javax.swing.JPanel pnlPessoas;
    private javax.swing.JPanel pnlRecebimento;
    private javax.swing.JPanel pnlValores;
    private javax.swing.JTable tblItens;
    private javax.swing.JFormattedTextField txtDesconto;
    private javax.swing.JFormattedTextField txtEmAberto;
    private javax.swing.JFormattedTextField txtFaturado;
    private javax.swing.JTextField txtMotoristaNome;
    private javax.swing.JTextField txtOSTId;
    private javax.swing.JTextArea txtObservacao;
    private javax.swing.JFormattedTextField txtRecebido;
    private javax.swing.JTextField txtRemetenteNome;
    private javax.swing.JTextField txtSolicitanteNome;
    private javax.swing.JTextField txtSolicitanteSetor;
    private javax.swing.JFormattedTextField txtTotal;
    private javax.swing.JFormattedTextField txtTotalItens;
    private javax.swing.JTextField txtVeiculo;
    // End of variables declaration//GEN-END:variables
}
