/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.pessoa;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import model.mysql.bean.principal.financeiro.Caixa;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.FinanceiroStatus;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.principal.CaixaDAO;
import model.mysql.dao.principal.ParcelaDAO;
import model.jtable.pessoa.CrediarioJTableModel;
import model.jtable.ParcelasPagarJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.TO_PRINTER_PATH;
import static ouroboros.Ouroboros.em;
import printing.TermicaPrint;
import printing.PrintPDFBox;
import util.DateTime;
import util.Decimal;
import util.jTableFormat.CrediarioRenderer;
import view.Toast;
import view.documentoSaida.VendaView;
import static ouroboros.Ouroboros.IMPRESSORA_CUPOM;
import view.documentoEntrada.DocumentoEntradaView;

/**
 *
 * @author ivand
 */
public class PessoaParcelasPagarView extends javax.swing.JInternalFrame {

    private static List<PessoaParcelasPagarView> clienteCrediarioViews = new ArrayList<>(); //instâncias

    ParcelasPagarJTableModel parcelasPagarJTableModel = new ParcelasPagarJTableModel();
    private Pessoa cliente;

    private List<Parcela> parcelas = new ArrayList<>();
    
    TipoOperacao tipoOperacao = TipoOperacao.ENTRADA;

    public static PessoaParcelasPagarView getInstance(Pessoa cliente) {
        for (PessoaParcelasPagarView clienteCrediarioView : clienteCrediarioViews) {
            if (clienteCrediarioView.cliente == cliente) {
                return clienteCrediarioView;
            }
        }
        clienteCrediarioViews.add(new PessoaParcelasPagarView(cliente));
        return clienteCrediarioViews.get(clienteCrediarioViews.size() - 1);
    }

    private PessoaParcelasPagarView() {
        initComponents();
    }

    private PessoaParcelasPagarView(Pessoa cliente) {
        initComponents();
        //JSwing.startComponentsBehavior(this);
        em.refresh(cliente);
        this.cliente = cliente;

        cboSituacao.setSelectedIndex(1);
        
        formatarTabela();

        carregarTabela();
    }

    private void formatarTabela() {
        tblParcela.setModel(parcelasPagarJTableModel);
        
        tblParcela.setRowHeight(24);
        tblParcela.setIntercellSpacing(new Dimension(10, 10));
        
        tblParcela.getColumn("Status").setPreferredWidth(120);
        CrediarioRenderer crediarioRenderer = new CrediarioRenderer();
        crediarioRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblParcela.getColumnModel().getColumn(0).setCellRenderer(crediarioRenderer);
        
        tblParcela.getColumn("Vencimento").setPreferredWidth(120);
        tblParcela.getColumn("Vencimento").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblParcela.getColumn("Doc.Id").setPreferredWidth(100);
        tblParcela.getColumn("Doc.Id").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblParcela.getColumn("Parcela").setPreferredWidth(100);
        tblParcela.getColumn("Parcela").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblParcela.getColumn("Valor").setPreferredWidth(120);
        tblParcela.getColumn("Valor").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblParcela.getColumn("Dias Atraso").setPreferredWidth(120);
        tblParcela.getColumn("Dias Atraso").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        /*tblParcela.getColumn("Multa %").setPreferredWidth(100);
        tblParcela.getColumn("Multa %").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcela.getColumn("M. Calc.").setPreferredWidth(100);
        tblParcela.getColumn("M. Calc.").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcela.getColumn("Juros").setPreferredWidth(100);
        tblParcela.getColumn("Juros").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcela.getColumn("J. Calc.").setPreferredWidth(100);
        tblParcela.getColumn("J. Calc.").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);*/
        
        tblParcela.getColumn("Valor Atual").setPreferredWidth(120);
        tblParcela.getColumn("Valor Atual").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblParcela.getColumn("Acrésc %").setPreferredWidth(120);
        tblParcela.getColumn("Acrésc %").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblParcela.getColumn("Desc %").setPreferredWidth(120);
        tblParcela.getColumn("Desc %").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcela.getColumn("Valor Pago").setPreferredWidth(120);
        tblParcela.getColumn("Valor Pago").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcela.getColumn("Data Pagto").setPreferredWidth(120);
        tblParcela.getColumn("Data Pagto").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblParcela.getColumn("Meio Pagto").setPreferredWidth(120);

        tblParcela.getColumn("Observação").setPreferredWidth(180);
    }
    
    private void carregarTabela() {
        //em.refresh(cliente);
        
        LocalDate dataInicial = DateTime.fromStringToLocalDate(txtDataInicial.getText());
        LocalDate dataFinal = DateTime.fromStringToLocalDate(txtDataFinal.getText());

        List<FinanceiroStatus> listStatus = new ArrayList<>();
        switch (cboSituacao.getSelectedIndex()) {
            case 0: //Todos
                //parcelaList = cliente.getParcelaListAPrazo();
                parcelas = new ParcelaDAO().findByCriteria(cliente, dataInicial, dataFinal, tipoOperacao);
                break;
            case 1: //Em aberto + Vencido
                listStatus.add(FinanceiroStatus.ABERTO);
                listStatus.add(FinanceiroStatus.VENCIDO);
                parcelas = new ParcelaDAO().findPorStatus(cliente, listStatus, dataInicial, dataFinal, tipoOperacao);
                break;
            case 2: //Em aberto
                listStatus.add(FinanceiroStatus.ABERTO);
                parcelas = new ParcelaDAO().findPorStatus(cliente, listStatus, dataInicial, dataFinal, tipoOperacao);
                break;
            case 3: //Vencido
                listStatus.add(FinanceiroStatus.VENCIDO);
                parcelas = new ParcelaDAO().findPorStatus(cliente, listStatus, dataInicial, dataFinal, tipoOperacao);
                break;
            case 4: //Quitado
                listStatus.add(FinanceiroStatus.QUITADO);
                parcelas = new ParcelaDAO().findPorStatus(cliente, listStatus, dataInicial, dataFinal, tipoOperacao);
                break;
        }
        
        // modelo para manter posição da tabela - melhorar... caso mude o vencimento, muda a ordem! :<
        int rowIndex = tblParcela.getSelectedRow();
        
        parcelasPagarJTableModel.clear();
        parcelasPagarJTableModel.addList(parcelas);

        //posicionar na última linha
        if(tblParcela.getRowCount() > 0) {
            if(rowIndex < 0 || rowIndex >= tblParcela.getRowCount()) {
                rowIndex = tblParcela.getRowCount() - 1;
            }
            //JOptionPane.showMessageDialog(rootPane, rowIndex);
            tblParcela.setRowSelectionInterval(rowIndex, rowIndex);
            tblParcela.scrollRectToVisible(tblParcela.getCellRect(rowIndex, 0, true));
        }
        //------------------------------------------
        
        //totais
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalRecebido = BigDecimal.ZERO;
        BigDecimal totalReceber = BigDecimal.ZERO;
        if(!parcelas.isEmpty()) {
            total = parcelas.stream().map(Parcela::getValor).reduce(BigDecimal::add).get();
            totalRecebido = parcelas.stream().map(Parcela::getValorQuitado).reduce(BigDecimal::add).get();
            totalReceber = total.subtract(totalRecebido);
        }
        txtTotal.setText(Decimal.toString(total));
        txtTotalPago.setText(Decimal.toString(totalRecebido));
        txtTotalPagar.setText(Decimal.toString(totalReceber));
    }
    
    private void receber() {
        Caixa lastCaixa = new CaixaDAO().getLastCaixa();
        if (lastCaixa == null || lastCaixa.getEncerramento() != null) {
            JOptionPane.showMessageDialog(rootPane, "Não há turno de caixa aberto. Não é possível realizar pagamentos.", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            boolean parcelaRecebida = false;
            List<Parcela> parcelas = new ArrayList<>();
            for(int index : tblParcela.getSelectedRows()) {
                Parcela p = parcelasPagarJTableModel.getRow(index);
                if(p.getStatus() == FinanceiroStatus.QUITADO) {
                    parcelaRecebida = true;
                    break;
                }
                parcelas.add(p);
            }
        
            if(parcelaRecebida) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Você selecionou uma ou mais parcelas já recebidas", "Atenção", JOptionPane.WARNING_MESSAGE);
            } else {
                ParcelaPagarView r = new ParcelaPagarView(parcelas);
                for(Parcela p : parcelas) {
                     em.refresh(p);
                }
                carregarTabela();
            }
        }
    }
    
    private void editar() {
        Parcela p = parcelasPagarJTableModel.getRow(tblParcela.getSelectedRow());

        PessoaParcelaEditarView edtView  = new PessoaParcelaEditarView(MAIN_VIEW, p);
        carregarTabela();
    }
    
    private void imprimir() {
        boolean parcelaNaoRecebida = false;
        List<Parcela> parcelaReceberList = new ArrayList<>();
        for(int index : tblParcela.getSelectedRows()) {
            Parcela p = parcelasPagarJTableModel.getRow(index);
            if(p.getValorQuitado().compareTo(BigDecimal.ZERO) <= 0) {
                parcelaNaoRecebida = true;
                break;
            }
            parcelaReceberList.add(p);
        }
        
        if(parcelaNaoRecebida) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Você selecionou uma ou mais parcelas não recebidas", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            String pdfFilePath = TO_PRINTER_PATH + "RECIBO DE PAGAMENTO_" + System.currentTimeMillis() + ".pdf";
            TermicaPrint.gerarRecibo(parcelaReceberList, pdfFilePath);

            new Toast("Imprimindo...");

            PrintPDFBox pPDF = new PrintPDFBox();
            pPDF.print(pdfFilePath, IMPRESSORA_CUPOM);
        }
    }
    
    private void abrirDocumento() {
        //Set<Integer> setIds = new HashSet<>();
        Set<Venda> setVendas = new HashSet<>();
        int[] rowIndices = tblParcela.getSelectedRows();
        for (int rowIndex : rowIndices) {
            //int id = parcelasPagarJTableModel.getRow(rowIndex).getVenda().getId();
            Venda venda = parcelasPagarJTableModel.getRow(rowIndex).getVenda();
            setVendas.add(venda);
        }
        
        for(Venda venda : setVendas) {
            System.out.println("venda id: " + venda.getId());
            MAIN_VIEW.addView(DocumentoEntradaView.getInstance(venda));
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblParcela = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btnPagar = new javax.swing.JButton();
        txtTotal = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtTotalPago = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTotalPagar = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnAbrirDocumento = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        cboSituacao = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        btnFiltrar = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtDataInicial = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtDataFinal = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();

        setTitle("Parcelas a Pagar");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameActivated(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
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

        tblParcela.setModel(new javax.swing.table.DefaultTableModel(
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
        tblParcela.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblParcelaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblParcela);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnPagar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/money_delete.png"))); // NOI18N
        btnPagar.setText("Pagar");
        btnPagar.setContentAreaFilled(false);
        btnPagar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPagar.setPreferredSize(new java.awt.Dimension(120, 23));
        btnPagar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPagarActionPerformed(evt);
            }
        });

        txtTotal.setEditable(false);
        txtTotal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Total");

        txtTotalPago.setEditable(false);
        txtTotalPago.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTotalPago.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Total Pago");

        txtTotalPagar.setEditable(false);
        txtTotalPagar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTotalPagar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Total a Pagar");

        btnAbrirDocumento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/folder_page_white.png"))); // NOI18N
        btnAbrirDocumento.setText("Abrir Documento");
        btnAbrirDocumento.setContentAreaFilled(false);
        btnAbrirDocumento.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAbrirDocumento.setPreferredSize(new java.awt.Dimension(120, 23));
        btnAbrirDocumento.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAbrirDocumento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirDocumentoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnAbrirDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTotal)
                    .addComponent(txtTotalPago)
                    .addComponent(txtTotalPagar, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(btnPagar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAbrirDocumento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cboSituacao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Em aberto + Vencido", "Em aberto", "Vencido", "Quitado" }));

        jLabel4.setText("Situação");

        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        jLabel5.setText("Data Inicial");

        try {
            txtDataInicial.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtDataInicial.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDataInicial.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDataInicialFocusLost(evt);
            }
        });

        jLabel6.setText("Data Final");

        try {
            txtDataFinal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtDataFinal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDataFinal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDataFinalFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(cboSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnFiltrar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(jLabel6)
                        .addComponent(btnFiltrar))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jLabel7.setForeground(java.awt.Color.blue);
        jLabel7.setText("Para selecionar linhas separadas em uma tabela, utilize CTRL");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1267, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown

    }//GEN-LAST:event_formComponentShown

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
    }//GEN-LAST:event_formInternalFrameActivated

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        clienteCrediarioViews.remove(this);
    }//GEN-LAST:event_formInternalFrameClosed

    private void btnPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPagarActionPerformed
        receber();
        
    }//GEN-LAST:event_btnPagarActionPerformed

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void txtDataInicialFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDataInicialFocusLost
        if(txtDataInicial.getText().contains("/  /")){
            txtDataInicial.setValue(null);
        }
    }//GEN-LAST:event_txtDataInicialFocusLost

    private void txtDataFinalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDataFinalFocusLost
        if(txtDataFinal.getText().contains("/  /")){
            txtDataFinal.setValue(null);
        }
    }//GEN-LAST:event_txtDataFinalFocusLost

    private void tblParcelaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblParcelaMouseClicked
        if(evt.getClickCount() == 2) {
            editar();
        }
    }//GEN-LAST:event_tblParcelaMouseClicked

    private void btnAbrirDocumentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirDocumentoActionPerformed
        abrirDocumento();
    }//GEN-LAST:event_btnAbrirDocumentoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrirDocumento;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnPagar;
    private javax.swing.JComboBox<String> cboSituacao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblParcela;
    private javax.swing.JFormattedTextField txtDataFinal;
    private javax.swing.JFormattedTextField txtDataInicial;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtTotalPagar;
    private javax.swing.JTextField txtTotalPago;
    // End of variables declaration//GEN-END:variables
}
