/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.pessoa;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import model.mysql.bean.principal.financeiro.Caixa;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.FinanceiroStatus;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.principal.CaixaDAO;
import model.mysql.dao.principal.ParcelaDAO;
import model.jtable.pessoa.CrediarioJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.TO_PRINTER_PATH;
import static ouroboros.Ouroboros.em;
import printing.CriarPDF;
import printing.PrintPDFBox;
import util.DateTime;
import util.Decimal;
import util.jTableFormat.CrediarioRenderer;
import view.Toast;
import view.documentoSaida.VendaView;
import static ouroboros.Ouroboros.IMPRESSORA_CUPOM;
import printing.ListaParcelasPrint;

/**
 *
 * @author ivand
 */
public class PessoaCrediarioView extends javax.swing.JInternalFrame {

    private static List<PessoaCrediarioView> clienteCrediarioViews = new ArrayList<>(); //instâncias

    CrediarioJTableModel crediarioJTableModel = new CrediarioJTableModel();
    private Pessoa cliente;

    private List<Parcela> parcelaList = new ArrayList<>();

    TipoOperacao tipoOperacao = TipoOperacao.SAIDA;

    public static PessoaCrediarioView getInstance(Pessoa cliente) {
        for (PessoaCrediarioView clienteCrediarioView : clienteCrediarioViews) {
            if (clienteCrediarioView.cliente == cliente) {
                return clienteCrediarioView;
            }
        }
        clienteCrediarioViews.add(new PessoaCrediarioView(cliente));
        return clienteCrediarioViews.get(clienteCrediarioViews.size() - 1);
    }

    private PessoaCrediarioView() {
        initComponents();
    }

    private PessoaCrediarioView(Pessoa cliente) {
        initComponents();
        //JSwing.startComponentsBehavior(this);
        //em.refresh(cliente);
        this.cliente = cliente;

        cboSituacao.setSelectedIndex(1);

        formatarTabela();

        carregarTabela();
    }

    private void formatarTabela() {
        tblParcela.setModel(crediarioJTableModel);

        tblParcela.setRowHeight(24);
        tblParcela.setIntercellSpacing(new Dimension(10, 10));

        tblParcela.getColumn("Status").setPreferredWidth(120);
        CrediarioRenderer crediarioRenderer = new CrediarioRenderer();
        crediarioRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblParcela.getColumnModel().getColumn(0).setCellRenderer(crediarioRenderer);

        tblParcela.getColumn("Vencimento").setPreferredWidth(120);
        tblParcela.getColumn("Vencimento").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblParcela.getColumn("Venda").setPreferredWidth(100);
        tblParcela.getColumn("Venda").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblParcela.getColumn("Parcela").setPreferredWidth(100);
        tblParcela.getColumn("Parcela").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblParcela.getColumn("Valor").setPreferredWidth(120);
        tblParcela.getColumn("Valor").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcela.getColumn("Dias Atraso").setPreferredWidth(120);
        tblParcela.getColumn("Dias Atraso").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcela.getColumn("Multa %").setPreferredWidth(100);
        tblParcela.getColumn("Multa %").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcela.getColumn("M. Calc.").setPreferredWidth(100);
        tblParcela.getColumn("M. Calc.").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcela.getColumn("Juros").setPreferredWidth(100);
        tblParcela.getColumn("Juros").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcela.getColumn("J. Calc.").setPreferredWidth(100);
        tblParcela.getColumn("J. Calc.").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcela.getColumn("Valor Atual").setPreferredWidth(120);
        tblParcela.getColumn("Valor Atual").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcela.getColumn("Acrésc %").setPreferredWidth(120);
        tblParcela.getColumn("Acrésc %").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcela.getColumn("Desc %").setPreferredWidth(120);
        tblParcela.getColumn("Desc %").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcela.getColumn("Valor Recebido").setPreferredWidth(120);
        tblParcela.getColumn("Valor Recebido").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcela.getColumn("Recebimento").setPreferredWidth(120);
        tblParcela.getColumn("Recebimento").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

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
                parcelaList = new ParcelaDAO().findByCriteria(cliente, dataInicial, dataFinal, tipoOperacao);
                break;
            case 1: //Em aberto + Vencido
                listStatus.add(FinanceiroStatus.ABERTO);
                listStatus.add(FinanceiroStatus.VENCIDO);
                parcelaList = new ParcelaDAO().findPorStatus(cliente, listStatus, dataInicial, dataFinal, tipoOperacao);
                break;
            case 2: //Em aberto
                listStatus.add(FinanceiroStatus.ABERTO);
                parcelaList = new ParcelaDAO().findPorStatus(cliente, listStatus, dataInicial, dataFinal, tipoOperacao);
                break;
            case 3: //Vencido
                listStatus.add(FinanceiroStatus.VENCIDO);
                parcelaList = new ParcelaDAO().findPorStatus(cliente, listStatus, dataInicial, dataFinal, tipoOperacao);
                break;
            case 4: //Quitado
                listStatus.add(FinanceiroStatus.QUITADO);
                parcelaList = new ParcelaDAO().findPorStatus(cliente, listStatus, dataInicial, dataFinal, tipoOperacao);
                break;
        }

        // modelo para manter posição da tabela - melhorar... caso mude o vencimento, muda a ordem! :<
        int rowIndex = tblParcela.getSelectedRow();

        crediarioJTableModel.clear();
        crediarioJTableModel.addList(parcelaList);

        //posicionar na última linha
        if (tblParcela.getRowCount() > 0) {
            if (rowIndex < 0 || rowIndex >= tblParcela.getRowCount()) {
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
        if (!parcelaList.isEmpty()) {
            total = parcelaList.stream().map(Parcela::getValor).reduce(BigDecimal::add).get();
            totalRecebido = parcelaList.stream().map(Parcela::getValorQuitado).reduce(BigDecimal::add).get();
            totalReceber = total.subtract(totalRecebido);
        }
        txtTotal.setText(Decimal.toString(total));
        txtTotalRecebido.setText(Decimal.toString(totalRecebido));
        txtTotalReceber.setText(Decimal.toString(totalReceber));
    }

    private void receber() {
        Caixa lastCaixa = new CaixaDAO().getLastCaixa();
        if (lastCaixa == null || lastCaixa.getEncerramento() != null) {
            JOptionPane.showMessageDialog(rootPane, "Não há turno de caixa aberto. Não é possível realizar recebimentos.", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            boolean parcelaRecebida = false;
            List<Parcela> parcelaReceberList = new ArrayList<>();
            for (int index : tblParcela.getSelectedRows()) {
                if(crediarioJTableModel.getRow(index).getVenda() != null) {
                    Parcela p = crediarioJTableModel.getRow(index);
                    if (p.getStatus() == FinanceiroStatus.QUITADO) {
                        parcelaRecebida = true;
                        break;
                    }
                    parcelaReceberList.add(p);
                }
            }

            if(parcelaReceberList.isEmpty()) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione algum registro!", "Atenção", JOptionPane.WARNING_MESSAGE);
                carregarTabela();
                
            } else if (parcelaRecebida) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Você selecionou uma ou mais parcelas já recebidas", "Atenção", JOptionPane.WARNING_MESSAGE);
                
            } else {
                PessoaCrediarioRecebimentoView r = new PessoaCrediarioRecebimentoView(MAIN_VIEW, parcelaReceberList);
                for (Parcela p : parcelaReceberList) {
                    em.refresh(p);
                }
                carregarTabela();
            }
        }
    }

    private void editar() {
        if(crediarioJTableModel.getRow(tblParcela.getSelectedRow()).getVenda() != null) {
            Parcela p = crediarioJTableModel.getRow(tblParcela.getSelectedRow());
            PessoaParcelaEditarView edtView = new PessoaParcelaEditarView(MAIN_VIEW, p);
        }
        carregarTabela();
    }

    private void imprimir() {
        boolean parcelaNaoRecebida = false;
        List<Parcela> parcelaReceberList = new ArrayList<>();
        for (int index : tblParcela.getSelectedRows()) {
            Parcela p = crediarioJTableModel.getRow(index);
            if (p.getValorQuitado().compareTo(BigDecimal.ZERO) <= 0) {
                parcelaNaoRecebida = true;
                break;
            }
            parcelaReceberList.add(p);
        }

        if (parcelaNaoRecebida) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Você selecionou uma ou mais parcelas não recebidas", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            String pdfFilePath = TO_PRINTER_PATH + "RECIBO DE PAGAMENTO_" + System.currentTimeMillis() + ".pdf";
            CriarPDF.gerarRecibo(parcelaReceberList, pdfFilePath);

            new Toast("Imprimindo...");

            PrintPDFBox pPDF = new PrintPDFBox();
            pPDF.print(pdfFilePath, IMPRESSORA_CUPOM);
        }
    }
    
    private void imprimirLista() {
        ListaParcelasPrint.imprimirCupom(cliente, parcelaList);
    }

    private void abrirVenda() {
        //Set<Integer> setIds = new HashSet<>();
        Set<Venda> setVendas = new HashSet<>();
        int[] rowIndices = tblParcela.getSelectedRows();
        for (int rowIndex : rowIndices) {
            //int id = crediarioJTableModel.getRow(rowIndex).getVenda().getId();
            if(crediarioJTableModel.getRow(rowIndex).getVenda() != null) {
                Venda venda = crediarioJTableModel.getRow(rowIndex).getVenda();
                setVendas.add(venda);
            }
        }

        for (Venda venda : setVendas) {
            System.out.println("venda id: " + venda.getId());
            MAIN_VIEW.addView(VendaView.getInstance(venda));
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
        btnReceber = new javax.swing.JButton();
        txtTotal = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtTotalRecebido = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTotalReceber = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnImprimirRecibo = new javax.swing.JButton();
        btnAbrirDocumento = new javax.swing.JButton();
        btnImprimirLista = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        cboSituacao = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        btnFiltrar = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtDataInicial = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtDataFinal = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();

        setTitle("Crediário");
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

        btnReceber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/money_add.png"))); // NOI18N
        btnReceber.setText("Receber");
        btnReceber.setContentAreaFilled(false);
        btnReceber.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnReceber.setPreferredSize(new java.awt.Dimension(120, 23));
        btnReceber.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnReceber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReceberActionPerformed(evt);
            }
        });

        txtTotal.setEditable(false);
        txtTotal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Total");

        txtTotalRecebido.setEditable(false);
        txtTotalRecebido.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTotalRecebido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Total Recebido");

        txtTotalReceber.setEditable(false);
        txtTotalReceber.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTotalReceber.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Total Receber");

        btnImprimirRecibo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/printer.png"))); // NOI18N
        btnImprimirRecibo.setText("Imprimir Recibo");
        btnImprimirRecibo.setContentAreaFilled(false);
        btnImprimirRecibo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnImprimirRecibo.setPreferredSize(new java.awt.Dimension(120, 23));
        btnImprimirRecibo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimirRecibo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirReciboActionPerformed(evt);
            }
        });

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

        btnImprimirLista.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/printer.png"))); // NOI18N
        btnImprimirLista.setText("Imprimir Lista");
        btnImprimirLista.setContentAreaFilled(false);
        btnImprimirLista.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnImprimirLista.setPreferredSize(new java.awt.Dimension(120, 23));
        btnImprimirLista.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimirLista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirListaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnReceber, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnImprimirRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnImprimirLista, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnAbrirDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 236, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTotal)
                    .addComponent(txtTotalRecebido)
                    .addComponent(txtTotalReceber, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE))
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
                            .addComponent(txtTotalRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotalReceber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(btnReceber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnImprimirRecibo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAbrirDocumento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnImprimirLista, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addContainerGap(15, Short.MAX_VALUE))
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
                        .addGap(0, 976, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
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

    private void btnReceberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReceberActionPerformed
        receber();

    }//GEN-LAST:event_btnReceberActionPerformed

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void txtDataInicialFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDataInicialFocusLost
        if (txtDataInicial.getText().contains("/  /")) {
            txtDataInicial.setValue(null);
        }
    }//GEN-LAST:event_txtDataInicialFocusLost

    private void txtDataFinalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDataFinalFocusLost
        if (txtDataFinal.getText().contains("/  /")) {
            txtDataFinal.setValue(null);
        }
    }//GEN-LAST:event_txtDataFinalFocusLost

    private void tblParcelaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblParcelaMouseClicked
        if (evt.getClickCount() == 2) {
            editar();
        }
    }//GEN-LAST:event_tblParcelaMouseClicked

    private void btnImprimirReciboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirReciboActionPerformed
        imprimir();
    }//GEN-LAST:event_btnImprimirReciboActionPerformed

    private void btnAbrirDocumentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirDocumentoActionPerformed
        abrirVenda();
    }//GEN-LAST:event_btnAbrirDocumentoActionPerformed

    private void btnImprimirListaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirListaActionPerformed
        imprimirLista();
    }//GEN-LAST:event_btnImprimirListaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrirDocumento;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnImprimirLista;
    private javax.swing.JButton btnImprimirRecibo;
    private javax.swing.JButton btnReceber;
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
    private javax.swing.JTextField txtTotalReceber;
    private javax.swing.JTextField txtTotalRecebido;
    // End of variables declaration//GEN-END:variables
}
