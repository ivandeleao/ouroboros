/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.financeiro.contasPagar;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import model.mysql.bean.principal.financeiro.ContaPagar;
import model.nosql.FinanceiroStatusEnum;
import model.mysql.dao.principal.financeiro.ContaPagarDAO;
import model.jtable.financeiro.ContasPagarJTableModel;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import static ouroboros.Constants.*;
import static ouroboros.Ouroboros.MAIN_VIEW;
import printing.financeiro.ContasPagarReport;
import util.JSwing;
import util.DateTime;
import util.Decimal;
import util.jTableFormat.FinanceiroStatusRenderer;
import view.documentoEntrada.DocumentoEntradaView;
import view.financeiro.RecebimentoParcelaNovoView;
import view.pessoa.ParcelaPagarView;

/**
 *
 * @author ivand
 */
public class ContasPagarView extends javax.swing.JInternalFrame {

    private static ContasPagarView singleInstance = null;
    ContasPagarJTableModel contasPagarJTableModel = new ContasPagarJTableModel();
    ContaPagarDAO contaPagarDAO = new ContaPagarDAO();
    List<ContaPagar> contas = new ArrayList<>();
    
    String status;
    LocalDate dataInicial, dataFinal;
    BigDecimal total, totalPago, totalPagar;

    public static ContasPagarView getSingleInstance() {
        if (singleInstance == null) {
            singleInstance = new ContasPagarView();
        }
        return singleInstance;
    }

    /**
     * Creates new form CategoriaCadastroView
     */
    private ContasPagarView() {
        initComponents();
        JSwing.startComponentsBehavior(this);
        
        btnPagarAntigo.setVisible(false);

        cboSituacao.setSelectedIndex(1);

        txtDataInicial.setText(DateTime.toString(LocalDate.now().minusMonths(1).withDayOfMonth(1)));
        txtDataFinal.setText(DateTime.toString(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())));

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
                    //novo();
                    break;

            }
        }
    }

    private void formatarTabela() {
        tblContasPagar.setModel(contasPagarJTableModel);

        tblContasPagar.setRowHeight(30);
        tblContasPagar.setIntercellSpacing(new Dimension(10, 10));

        tblContasPagar.getColumn("Status").setPreferredWidth(100);
        FinanceiroStatusRenderer crediarioRenderer = new FinanceiroStatusRenderer();
        crediarioRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblContasPagar.getColumn("Status").setCellRenderer(crediarioRenderer);

        tblContasPagar.getColumn("Vencimento").setPreferredWidth(100);
        tblContasPagar.getColumn("Vencimento").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblContasPagar.getColumn("Descrição").setPreferredWidth(400);

        tblContasPagar.getColumn("Valor").setPreferredWidth(100);
        tblContasPagar.getColumn("Valor").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblContasPagar.getColumn("Data Pagto").setPreferredWidth(100);
        tblContasPagar.getColumn("Data Pagto").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblContasPagar.getColumn("Valor Pago").setPreferredWidth(100);
        tblContasPagar.getColumn("Valor Pago").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblContasPagar.getColumn("MP").setPreferredWidth(40);
        tblContasPagar.getColumn("MP").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblContasPagar.getColumn("Observação").setPreferredWidth(150);

    }

    private void carregarTabela() {

        status = cboSituacao.getSelectedItem().toString();
        dataInicial = DateTime.fromStringToLocalDate(txtDataInicial.getText());
        dataFinal = DateTime.fromStringToLocalDate(txtDataFinal.getText());

        //2020-04-25 hack - conta programada precisa das datas pois são virtuais
        dataInicial = dataInicial != null ? dataInicial : LocalDate.of(2019, 1, 1);
        dataFinal = dataFinal != null ? dataFinal : LocalDate.now().plusYears(10);
        
        List<FinanceiroStatusEnum> listStatus = new ArrayList<>();

        switch (cboSituacao.getSelectedIndex()) {
            case 0: //Todos
                contas = contaPagarDAO.findPorPeriodo(dataInicial, dataFinal, null);
                break;
            case 1: //Em aberto + Vencido
                listStatus.add(FinanceiroStatusEnum.ABERTO);
                listStatus.add(FinanceiroStatusEnum.VENCIDO);
                contas = contaPagarDAO.findPorPeriodo(dataInicial, dataFinal, listStatus);
                break;
            case 2: //Em aberto
                listStatus.add(FinanceiroStatusEnum.ABERTO);
                contas = contaPagarDAO.findPorPeriodo(dataInicial, dataFinal, listStatus);
                break;
            case 3: //Vencido
                listStatus.add(FinanceiroStatusEnum.VENCIDO);
                contas = contaPagarDAO.findPorPeriodo(dataInicial, dataFinal, listStatus);
                break;
            case 4: //Quitado
                listStatus.add(FinanceiroStatusEnum.QUITADO);
                contas = contaPagarDAO.findPorPeriodo(dataInicial, dataFinal, listStatus);
                break;
        }

        // modelo para manter posição da tabela - melhorar: caso altere o vencimento, muda a ordem! :<
        int rowIndex = tblContasPagar.getSelectedRow();

        contasPagarJTableModel.clear();
        contasPagarJTableModel.addList(contas);

        //posicionar na última linha
        if (tblContasPagar.getRowCount() > 0) {
            if (rowIndex < 0 || rowIndex >= tblContasPagar.getRowCount()) {
                rowIndex = 0;
            }
            //JOptionPane.showMessageDialog(rootPane, rowIndex);
            tblContasPagar.setRowSelectionInterval(rowIndex, rowIndex);
            tblContasPagar.scrollRectToVisible(tblContasPagar.getCellRect(rowIndex, 0, true));
        }
        //------------------------------------------

        //totais
        total = BigDecimal.ZERO;
        totalPago = BigDecimal.ZERO;
        totalPagar = BigDecimal.ZERO;
        if (!contas.isEmpty()) {
            total = contas.stream().map(ContaPagar::getValor).reduce(BigDecimal::add).get();
            totalPago = contas.stream().map(ContaPagar::getValorPago).reduce(BigDecimal::add).get();
            totalPagar = total.subtract(totalPago);
        }
        txtTotal.setText(Decimal.toString(total));
        txtTotalPago.setText(Decimal.toString(totalPago));
        txtTotalPagar.setText(Decimal.toString(totalPagar));
    }

    private void pagar() {
        ContaPagar contaPagar = contasPagarJTableModel.getRow(tblContasPagar.getSelectedRow());

        if (contaPagar.getParcela() != null) {
            if (contaPagar.getStatus().equals(FinanceiroStatusEnum.QUITADO)) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Esta conta já foi paga.", "Atenção", JOptionPane.WARNING_MESSAGE);

            } else {
                List<Parcela> parcelas = new ArrayList<>();
                parcelas.add(contaPagar.getParcela());
                //ParcelaPagarView r = new ParcelaPagarView(parcelas);
                new RecebimentoParcelaNovoView(parcelas);
            }

        } else {
            if (contaPagar.getStatus().equals(FinanceiroStatusEnum.QUITADO)) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Esta conta já foi paga.", "Atenção", JOptionPane.WARNING_MESSAGE);

            } else {
                new ContaProgramadaPagarView(contaPagar);

            }
        }

        carregarTabela();
    }
    
    private void pagarAntigo() {
        ContaPagar contaPagar = contasPagarJTableModel.getRow(tblContasPagar.getSelectedRow());

        if (contaPagar.getParcela() != null) {
            if (contaPagar.getStatus().equals(FinanceiroStatusEnum.QUITADO)) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Esta conta já foi paga.", "Atenção", JOptionPane.WARNING_MESSAGE);

            } else {
                List<Parcela> parcelas = new ArrayList<>();
                parcelas.add(contaPagar.getParcela());
                ParcelaPagarView r = new ParcelaPagarView(parcelas);

            }

        } else {
            if (contaPagar.getStatus().equals(FinanceiroStatusEnum.QUITADO)) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Esta conta já foi paga.", "Atenção", JOptionPane.WARNING_MESSAGE);

            } else {
                ContaProgramadaPagarView pagar = new ContaProgramadaPagarView(contaPagar);

            }
        }

        carregarTabela();
    }
    
    
    
    private void abrirDocumento() {
        Set<Venda> setDocumentos = new HashSet<>();
        int[] rowIndices = tblContasPagar.getSelectedRows();
        for (int rowIndex : rowIndices) {
            if (contasPagarJTableModel.getRow(rowIndex).getParcela()!= null) {
                Venda documento = contasPagarJTableModel.getRow(rowIndex).getParcela().getVenda();
                setDocumentos.add(documento);
            }
        }

        setDocumentos.forEach((documento) -> {
            MAIN_VIEW.addView(DocumentoEntradaView.getInstance(documento));
        });
    }
    
    private void contasProgramadas() {
        new ContaProgramadaListaView();
        carregarTabela();
    }
    
    private void imprimir() {
        ContasPagarReport.gerar(contas, status, dataInicial, dataFinal, total, totalPago, totalPagar);
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
        tblContasPagar = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTotalPago = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtTotalPagar = new javax.swing.JTextField();
        btnImprimir = new javax.swing.JButton();
        btnDocumento = new javax.swing.JButton();
        btnPagar = new javax.swing.JButton();
        btnContasProgramadas = new javax.swing.JButton();
        btnPagarAntigo = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        cboSituacao = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        btnFiltrar = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtDataInicial = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtDataFinal = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();

        setClosable(true);
        setTitle("Contas a Pagar");
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

        tblContasPagar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblContasPagar.setModel(new javax.swing.table.DefaultTableModel(
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
        tblContasPagar.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblContasPagar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblContasPagarFocusGained(evt);
            }
        });
        tblContasPagar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblContasPagarMouseClicked(evt);
            }
        });
        tblContasPagar.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tblContasPagarPropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(tblContasPagar);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Total");

        txtTotal.setEditable(false);
        txtTotal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Total Pago");

        txtTotalPago.setEditable(false);
        txtTotalPago.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTotalPago.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Total Pagar");

        txtTotalPagar.setEditable(false);
        txtTotalPagar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTotalPagar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-printer-20.png"))); // NOI18N
        btnImprimir.setText("Imprimir");
        btnImprimir.setContentAreaFilled(false);
        btnImprimir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnImprimir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        btnDocumento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-external-link-20.png"))); // NOI18N
        btnDocumento.setText("Documento");
        btnDocumento.setContentAreaFilled(false);
        btnDocumento.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDocumento.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDocumento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDocumentoActionPerformed(evt);
            }
        });

        btnPagar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-initiate-money-transfer-20.png"))); // NOI18N
        btnPagar.setText("Pagar");
        btnPagar.setContentAreaFilled(false);
        btnPagar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPagar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPagarActionPerformed(evt);
            }
        });

        btnContasProgramadas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-calendar-20.png"))); // NOI18N
        btnContasProgramadas.setText("Contas Programadas");
        btnContasProgramadas.setContentAreaFilled(false);
        btnContasProgramadas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnContasProgramadas.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnContasProgramadas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContasProgramadasActionPerformed(evt);
            }
        });

        btnPagarAntigo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-initiate-money-transfer-20.png"))); // NOI18N
        btnPagarAntigo.setText("Antigo");
        btnPagarAntigo.setContentAreaFilled(false);
        btnPagarAntigo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPagarAntigo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPagarAntigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPagarAntigoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnPagar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPagarAntigo)
                .addGap(18, 18, 18)
                .addComponent(btnDocumento)
                .addGap(18, 18, 18)
                .addComponent(btnContasProgramadas)
                .addGap(18, 18, 18)
                .addComponent(btnImprimir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTotal)
                    .addComponent(txtTotalPago)
                    .addComponent(txtTotalPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotalPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotalPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)))
                    .addComponent(btnPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnContasProgramadas, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPagarAntigo, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cboSituacao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cboSituacao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Em aberto + Vencido", "Em aberto", "Vencido", "Quitado" }));

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel14.setText("Status");

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
                .addContainerGap(394, Short.MAX_VALUE))
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

        jLabel4.setForeground(java.awt.Color.blue);
        jLabel4.setText("Contas a pagar exibem contas programadas (fixas e semifixas) + parcelas provenientes de compras");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void tblContasPagarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblContasPagarMouseClicked
        //catchClick();
        if (evt.getClickCount() == 2) {
            //editar();
        }
    }//GEN-LAST:event_tblContasPagarMouseClicked

    private void tblContasPagarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblContasPagarFocusGained
        //tableCategoriasUpdateRow();

    }//GEN-LAST:event_tblContasPagarFocusGained

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void tblContasPagarPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tblContasPagarPropertyChange

    }//GEN-LAST:event_tblContasPagarPropertyChange

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        imprimir();
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void btnDocumentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDocumentoActionPerformed
        abrirDocumento();
    }//GEN-LAST:event_btnDocumentoActionPerformed

    private void btnPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPagarActionPerformed
        pagar();
    }//GEN-LAST:event_btnPagarActionPerformed

    private void btnContasProgramadasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContasProgramadasActionPerformed
        contasProgramadas();
    }//GEN-LAST:event_btnContasProgramadasActionPerformed

    private void btnPagarAntigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPagarAntigoActionPerformed
        pagarAntigo();
    }//GEN-LAST:event_btnPagarAntigoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnContasProgramadas;
    private javax.swing.JButton btnDocumento;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnPagar;
    private javax.swing.JButton btnPagarAntigo;
    private javax.swing.JComboBox<String> cboSituacao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblContasPagar;
    private javax.swing.JFormattedTextField txtDataFinal;
    private javax.swing.JFormattedTextField txtDataInicial;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtTotalPagar;
    private javax.swing.JTextField txtTotalPago;
    // End of variables declaration//GEN-END:variables
}
