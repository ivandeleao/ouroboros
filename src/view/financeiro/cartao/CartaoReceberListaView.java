/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.financeiro.cartao;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import model.jtable.financeiro.CartaoReceberListaJTableModel;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.documento.Parcela;
import model.nosql.FinanceiroStatusEnum;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.principal.ParcelaDAO;
import static ouroboros.Constants.*;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.JSwing;
import util.DateTime;
import util.Decimal;
import util.jTableFormat.FinanceiroStatusRenderer;
import view.pessoa.PessoaParcelaEditarView;
import view.documentoSaida.item.VendaView;

/**
 *
 * @author ivand
 */
public class CartaoReceberListaView extends javax.swing.JInternalFrame {

    private static CartaoReceberListaView singleInstance = null;
    CartaoReceberListaJTableModel cartaoReceberListaJTableModel = new CartaoReceberListaJTableModel();
    ParcelaDAO parcelaDAO = new ParcelaDAO();

    List<Parcela> parcelaList = new ArrayList<>();

    TipoOperacao tipoOperacao = TipoOperacao.SAIDA;

    public static CartaoReceberListaView getSingleInstance() {
        if (singleInstance == null) {
            singleInstance = new CartaoReceberListaView();
        }
        return singleInstance;
    }

    /**
     * Creates new form CategoriaCadastroView
     */
    private CartaoReceberListaView() {
        initComponents();
        JSwing.startComponentsBehavior(this);

        cboSituacao.setSelectedIndex(1);

        txtDataInicial.setText(DateTime.toString(LocalDate.now().minusMonths(1)));
        txtDataFinal.setText(DateTime.toString(LocalDate.now().plusMonths(1)));

        formatarTabela();

        carregarTabela();

        definirAtalhos();

    }

    private void definirAtalhos() {
        InputMap im = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "novo");
        am.put("novo", new FormKeyStroke("F1"));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "estoqueManual");
        am.put("estoqueManual", new FormKeyStroke("F2"));
    }

    protected class FormKeyStroke extends AbstractAction {

        private final String key;

        public FormKeyStroke(String key) {
            this.key = key;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (key) {
                case "F1":
                    novo();
                    break;

            }
        }
    }

    private void formatarTabela() {
        tblParcelas.setModel(cartaoReceberListaJTableModel);

        tblParcelas.setRowHeight(30);
        tblParcelas.setIntercellSpacing(new Dimension(10, 10));

        tblParcelas.getColumn("Status").setPreferredWidth(120);
        FinanceiroStatusRenderer crediarioRenderer = new FinanceiroStatusRenderer();
        crediarioRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblParcelas.getColumn("Status").setCellRenderer(crediarioRenderer);

        tblParcelas.getColumn("Vencimento").setPreferredWidth(120);
        tblParcelas.getColumn("Vencimento").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblParcelas.getColumn("Venda").setPreferredWidth(100);
        tblParcelas.getColumn("Venda").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblParcelas.getColumn("Parcela").setPreferredWidth(100);
        tblParcelas.getColumn("Parcela").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblParcelas.getColumn("Cliente").setPreferredWidth(300);

        tblParcelas.getColumn("Valor").setPreferredWidth(120);
        tblParcelas.getColumn("Valor").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcelas.getColumn("Taxa").setPreferredWidth(120);
        tblParcelas.getColumn("Taxa").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcelas.getColumn("Líquido").setPreferredWidth(120);
        tblParcelas.getColumn("Líquido").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcelas.getColumn("Valor Recebido").setPreferredWidth(120);
        tblParcelas.getColumn("Valor Recebido").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcelas.getColumn("Data Recebido").setPreferredWidth(120);
        tblParcelas.getColumn("Data Recebido").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblParcelas.getColumn("Observação").setPreferredWidth(160);
        
        
        ListSelectionModel cellSelectionModel = tblParcelas.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selecionarLinhas();
            }
        });
    }

    private void novo() {
        //CategoriaCadastro categoriaCadastro = new CategoriaCadastro(MAIN_VIEW, new Categoria());
        //carregarTabela();
    }

    private void editar() {
        if (cartaoReceberListaJTableModel.getRow(tblParcelas.getSelectedRow()).getVenda() != null) {
            Parcela p = cartaoReceberListaJTableModel.getRow(tblParcelas.getSelectedRow());
            PessoaParcelaEditarView edtView = new PessoaParcelaEditarView(MAIN_VIEW, p);
        }
        carregarTabela();
    }


    private void carregarTabela() {

        //Timestamp dataInicial = DateTime.fromString(txtDataInicial.getText());
        //Timestamp dataFinal = DateTime.fromString(txtDataFinal.getText() + " 23:59:59");
        LocalDate dataInicial = DateTime.fromStringToLocalDate(txtDataInicial.getText());
        LocalDate dataFinal = DateTime.fromStringToLocalDate(txtDataFinal.getText());

        List<FinanceiroStatusEnum> listStatus = new ArrayList<>();
        switch (cboSituacao.getSelectedIndex()) {
            case 0: //Todos
                parcelaList = new ParcelaDAO().findByCriteria(null, dataInicial, dataFinal, tipoOperacao, Optional.of(true), null, null, null);
                break;
            case 1: //Em aberto + Vencido
                listStatus.add(FinanceiroStatusEnum.ABERTO);
                listStatus.add(FinanceiroStatusEnum.VENCIDO);
                parcelaList = new ParcelaDAO().findPorStatus(null, listStatus, dataInicial, dataFinal, tipoOperacao, Optional.of(true));
                break;
            case 2: //Em aberto
                listStatus.add(FinanceiroStatusEnum.ABERTO);
                parcelaList = new ParcelaDAO().findPorStatus(null, listStatus, dataInicial, dataFinal, tipoOperacao, Optional.of(true));
                break;
            case 3: //Vencido
                listStatus.add(FinanceiroStatusEnum.VENCIDO);
                parcelaList = new ParcelaDAO().findPorStatus(null, listStatus, dataInicial, dataFinal, tipoOperacao, Optional.of(true));
                break;
            case 4: //Quitado
                listStatus.add(FinanceiroStatusEnum.QUITADO);
                parcelaList = new ParcelaDAO().findPorStatus(null, listStatus, dataInicial, dataFinal, tipoOperacao, Optional.of(true));
                break;
        }

        // modelo para manter posição da tabela - melhorar: caso altere o vencimento, muda a ordem! :<
        int rowIndex = tblParcelas.getSelectedRow();

        cartaoReceberListaJTableModel.clear();
        cartaoReceberListaJTableModel.addList(parcelaList);

        //posicionar na última linha
        if (tblParcelas.getRowCount() > 0) {
            if (rowIndex < 0 || rowIndex >= tblParcelas.getRowCount()) {
                rowIndex = 0;
            }
            //JOptionPane.showMessageDialog(rootPane, rowIndex);
            tblParcelas.setRowSelectionInterval(rowIndex, rowIndex);
            tblParcelas.scrollRectToVisible(tblParcelas.getCellRect(rowIndex, 0, true));
        }
        //------------------------------------------

        //totais
        BigDecimal totalBruto = BigDecimal.ZERO;
        BigDecimal totalTaxas = BigDecimal.ZERO;
        BigDecimal totalLiquido = BigDecimal.ZERO;

        BigDecimal recebidoBruto = BigDecimal.ZERO;
        BigDecimal recebidoTaxas = BigDecimal.ZERO;
        BigDecimal recebidoLiquido = BigDecimal.ZERO;

        BigDecimal receberBruto = BigDecimal.ZERO;
        BigDecimal receberTaxas = BigDecimal.ZERO;
        BigDecimal receberLiquido = BigDecimal.ZERO;

        if (!parcelaList.isEmpty()) {
            totalBruto = parcelaList.stream().map(Parcela::getValor).reduce(BigDecimal::add).get();
            totalTaxas = parcelaList.stream().map(Parcela::getCartaoTaxaValor).reduce(BigDecimal::add).get();
            totalLiquido = parcelaList.stream().map(Parcela::getCartaoValorLiquido).reduce(BigDecimal::add).get();

            recebidoBruto = parcelaList.stream().map(Parcela::getCartaoValorRecebido).reduce(BigDecimal::add).get();

            //aqui
            List<Parcela> parcelasComRecebimento = parcelaList.stream().filter(p -> p.getValorQuitado() != null).filter(p -> p.getValorQuitado().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());

            if (!parcelasComRecebimento.isEmpty()) {
                recebidoTaxas = parcelasComRecebimento.stream().map(Parcela::getCartaoTaxaValor).reduce(BigDecimal::add).get();
            }

            recebidoLiquido = recebidoBruto.subtract(recebidoTaxas);

            receberBruto = totalBruto.subtract(recebidoBruto);
            receberTaxas = totalTaxas.subtract(recebidoTaxas);
            receberLiquido = totalLiquido.subtract(recebidoLiquido);
        }
        txtTotalBruto.setText(Decimal.toString(totalBruto));
        txtTotalTaxas.setText(Decimal.toString(totalTaxas));
        txtTotalLiquido.setText(Decimal.toString(totalLiquido));

        txtRecebidoBruto.setText(Decimal.toString(recebidoBruto));
        txtRecebidoTaxas.setText(Decimal.toString(recebidoTaxas));
        txtRecebidoLiquido.setText(Decimal.toString(recebidoLiquido));

        txtReceberBruto.setText(Decimal.toString(receberBruto));
        txtReceberTaxas.setText(Decimal.toString(receberTaxas));
        txtReceberLiquido.setText(Decimal.toString(receberLiquido));
    }
    
    private void selecionarLinhas() {
        BigDecimal bruto = BigDecimal.ZERO;
        BigDecimal taxas = BigDecimal.ZERO;
        BigDecimal liquido = BigDecimal.ZERO;
        int[] rowIndices = tblParcelas.getSelectedRows();

        for (int rowIndex : rowIndices) {
            bruto = bruto.add(cartaoReceberListaJTableModel.getRow(rowIndex).getValor());
            taxas = taxas.add(cartaoReceberListaJTableModel.getRow(rowIndex).getCartaoTaxaValor());
            liquido = liquido.add(cartaoReceberListaJTableModel.getRow(rowIndex).getCartaoValorLiquido());
        }

        String selecao = "";
        if (rowIndices.length == 0) {
            selecao = "Nenhuma parcela selecionada";
            
        } else {
            selecao = rowIndices.length + " parcela(s) selecionada(s). Bruto: " + Decimal.toString(bruto) 
                    + " | Taxas: " + Decimal.toString(taxas) 
                    + " | Líquido: " + Decimal.toString(liquido);
            
        }
        
        
        txtSelecao.setText(selecao);;
    }

    private void receber() {
        boolean parcelaRecebida = false;
        List<Parcela> parcelaReceberList = new ArrayList<>();
        for (int index : tblParcelas.getSelectedRows()) {
            if (cartaoReceberListaJTableModel.getRow(index).getVenda() != null) {
                Parcela p = cartaoReceberListaJTableModel.getRow(index);
                if (p.getStatus() == FinanceiroStatusEnum.QUITADO) {
                    parcelaRecebida = true;
                    break;
                }
                parcelaReceberList.add(p);
            }
        }

        if (parcelaReceberList.isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione registro(s) não quitado(s)!", "Atenção", JOptionPane.WARNING_MESSAGE);
            carregarTabela();

        } else if (parcelaRecebida) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Você selecionou uma ou mais parcelas já recebidas", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {
            new CartaoLiquidacaoView(parcelaReceberList);
            carregarTabela();
        }
    }

    private void abrirVenda() {
        Set<Venda> setVendas = new HashSet<>();
        int[] rowIndices = tblParcelas.getSelectedRows();
        for (int rowIndex : rowIndices) {
            if (cartaoReceberListaJTableModel.getRow(rowIndex).getVenda() != null) {
                Venda venda = cartaoReceberListaJTableModel.getRow(rowIndex).getVenda();
                setVendas.add(venda);
            }
        }

        for (Venda venda : setVendas) {
            System.out.println("venda id: " + venda.getId());
            MAIN_VIEW.addView(VendaView.getInstance(venda));
        }
    }
    
    private void cartoes() {
        new CartaoListaView();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblParcelas = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        txtTotalBruto = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtRecebidoBruto = new javax.swing.JTextField();
        txtReceberBruto = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtTotalTaxas = new javax.swing.JTextField();
        txtRecebidoTaxas = new javax.swing.JTextField();
        txtReceberTaxas = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtTotalLiquido = new javax.swing.JTextField();
        txtRecebidoLiquido = new javax.swing.JTextField();
        txtReceberLiquido = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        cboSituacao = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        btnFiltrar = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtDataInicial = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtDataFinal = new javax.swing.JFormattedTextField();
        jPanel1 = new javax.swing.JPanel();
        btnReceber = new javax.swing.JButton();
        btnAbrirVenda = new javax.swing.JButton();
        btnContas1 = new javax.swing.JButton();
        txtSelecao = new javax.swing.JTextField();

        setClosable(true);
        setTitle("Cartões a Receber");
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameActivated(evt);
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

        tblParcelas.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblParcelas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblParcelas.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblParcelas.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblParcelasFocusGained(evt);
            }
        });
        tblParcelas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblParcelasMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblParcelasMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblParcelas);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtTotalBruto.setEditable(false);
        txtTotalBruto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalBruto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Bruto");

        txtRecebidoBruto.setEditable(false);
        txtRecebidoBruto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtRecebidoBruto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtReceberBruto.setEditable(false);
        txtReceberBruto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtReceberBruto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel15.setText("Taxas");

        txtTotalTaxas.setEditable(false);
        txtTotalTaxas.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalTaxas.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtRecebidoTaxas.setEditable(false);
        txtRecebidoTaxas.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtRecebidoTaxas.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtReceberTaxas.setEditable(false);
        txtReceberTaxas.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtReceberTaxas.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel16.setText("Líquido");

        txtTotalLiquido.setEditable(false);
        txtTotalLiquido.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalLiquido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtRecebidoLiquido.setEditable(false);
        txtRecebidoLiquido.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtRecebidoLiquido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtReceberLiquido.setEditable(false);
        txtReceberLiquido.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtReceberLiquido.setForeground(java.awt.Color.blue);
        txtReceberLiquido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Total");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Recebido");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Receber");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTotalTaxas, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtRecebidoTaxas, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtReceberTaxas, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(18, 18, 18)
                        .addComponent(txtTotalLiquido, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtRecebidoLiquido, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtReceberLiquido, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(32, 32, 32)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTotalBruto, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(47, 47, 47)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addComponent(jLabel12))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(txtRecebidoBruto, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtReceberBruto, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(35, 35, 35)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addGap(11, 11, 11)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotalBruto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(txtRecebidoBruto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtReceberBruto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotalTaxas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(txtRecebidoTaxas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtReceberTaxas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotalLiquido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(txtRecebidoLiquido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtReceberLiquido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cboSituacao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cboSituacao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Em aberto + Vencido", "Em aberto", "Vencido", "Quitado" }));

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel14.setText("Situação");

        btnFiltrar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnFiltrar.setText("Atualizar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Data Inicial");

        txtDataInicial.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDataInicial.setName("data"); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Data Final");

        txtDataFinal.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDataFinal.setName("data"); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addComponent(cboSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnFiltrar)
                .addContainerGap(277, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)
                        .addComponent(btnFiltrar))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnReceber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-request-money-20.png"))); // NOI18N
        btnReceber.setText("Receber");
        btnReceber.setContentAreaFilled(false);
        btnReceber.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnReceber.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnReceber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReceberActionPerformed(evt);
            }
        });

        btnAbrirVenda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-external-link-20.png"))); // NOI18N
        btnAbrirVenda.setText("Documento");
        btnAbrirVenda.setContentAreaFilled(false);
        btnAbrirVenda.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAbrirVenda.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAbrirVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirVendaActionPerformed(evt);
            }
        });

        btnContas1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-mastercard-credit-card-20.png"))); // NOI18N
        btnContas1.setText("Cartões");
        btnContas1.setContentAreaFilled(false);
        btnContas1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnContas1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnContas1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContas1ActionPerformed(evt);
            }
        });

        txtSelecao.setEditable(false);
        txtSelecao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSelecao)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnReceber)
                        .addGap(18, 18, 18)
                        .addComponent(btnAbrirVenda)
                        .addGap(18, 18, 18)
                        .addComponent(btnContas1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtSelecao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnAbrirVenda, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnContas1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnReceber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
    }//GEN-LAST:event_formComponentShown

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        MAIN_VIEW.removeTab(this.getName());
    }//GEN-LAST:event_formInternalFrameClosing

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        //System.out.println("focus");
        //tableCategoriasUpdateRow();
    }//GEN-LAST:event_formFocusGained

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated

    }//GEN-LAST:event_formInternalFrameActivated

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        singleInstance = null;
    }//GEN-LAST:event_formInternalFrameClosed

    private void btnReceberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReceberActionPerformed
        receber();
    }//GEN-LAST:event_btnReceberActionPerformed

    private void tblParcelasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblParcelasMouseClicked
        
        /*if (evt.getClickCount() == 2) {
            editar();
        }*/
        
    }//GEN-LAST:event_tblParcelasMouseClicked

    private void tblParcelasFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblParcelasFocusGained
        //tableCategoriasUpdateRow();

    }//GEN-LAST:event_tblParcelasFocusGained

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnAbrirVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirVendaActionPerformed
        abrirVenda();
    }//GEN-LAST:event_btnAbrirVendaActionPerformed

    private void btnContas1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContas1ActionPerformed
        cartoes();
    }//GEN-LAST:event_btnContas1ActionPerformed

    private void tblParcelasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblParcelasMouseReleased
        //selecionarLinhas();
    }//GEN-LAST:event_tblParcelasMouseReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrirVenda;
    private javax.swing.JButton btnContas1;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnReceber;
    private javax.swing.JComboBox<String> cboSituacao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblParcelas;
    private javax.swing.JFormattedTextField txtDataFinal;
    private javax.swing.JFormattedTextField txtDataInicial;
    private javax.swing.JTextField txtReceberBruto;
    private javax.swing.JTextField txtReceberLiquido;
    private javax.swing.JTextField txtReceberTaxas;
    private javax.swing.JTextField txtRecebidoBruto;
    private javax.swing.JTextField txtRecebidoLiquido;
    private javax.swing.JTextField txtRecebidoTaxas;
    private javax.swing.JTextField txtSelecao;
    private javax.swing.JTextField txtTotalBruto;
    private javax.swing.JTextField txtTotalLiquido;
    private javax.swing.JTextField txtTotalTaxas;
    // End of variables declaration//GEN-END:variables
}
