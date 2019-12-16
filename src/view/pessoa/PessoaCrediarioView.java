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
import printing.TermicaPrint;
import printing.PrintPDFBox;
import util.DateTime;
import util.Decimal;
import util.jTableFormat.CrediarioRenderer;
import view.Toast;
import view.documentoSaida.VendaView;
import static ouroboros.Ouroboros.IMPRESSORA_CUPOM;
import printing.ListaParcelasPrint;
import util.JSwing;

/**
 *
 * @author ivand
 */
public class PessoaCrediarioView extends javax.swing.JInternalFrame {

    private static List<PessoaCrediarioView> clienteCrediarioViews = new ArrayList<>(); //instâncias

    CrediarioJTableModel crediarioJTableModel = new CrediarioJTableModel();
    private Pessoa cliente;

    private List<Parcela> parcelas = new ArrayList<>();

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
        JSwing.startComponentsBehavior(this);
        
        this.cliente = cliente;

        cboSituacao.setSelectedIndex(1);

        formatarTabela();

        carregarTabela();
    }

    private void formatarTabela() {
        tblParcela.setModel(crediarioJTableModel);

        tblParcela.setRowHeight(30);
        tblParcela.setIntercellSpacing(new Dimension(10, 10));

        tblParcela.getColumn("Status").setPreferredWidth(120);
        CrediarioRenderer crediarioRenderer = new CrediarioRenderer();
        crediarioRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblParcela.getColumnModel().getColumn(0).setCellRenderer(crediarioRenderer);

        tblParcela.getColumn("Vencimento").setPreferredWidth(120);
        tblParcela.getColumn("Vencimento").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblParcela.getColumn("Doc. Parc.").setPreferredWidth(160);
        tblParcela.getColumn("Doc. Parc.").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblParcela.getColumn("Valor").setPreferredWidth(120);
        tblParcela.getColumn("Valor").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcela.getColumn("D.Atraso").setPreferredWidth(80);
        tblParcela.getColumn("D.Atraso").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

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

        tblParcela.getColumn("Acrésc").setPreferredWidth(120);
        tblParcela.getColumn("Acrésc").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcela.getColumn("Desc").setPreferredWidth(120);
        tblParcela.getColumn("Desc").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcela.getColumn("Valor Recebido").setPreferredWidth(120);
        tblParcela.getColumn("Valor Recebido").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcela.getColumn("Recebimento").setPreferredWidth(120);
        tblParcela.getColumn("Recebimento").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblParcela.getColumn("MP").setPreferredWidth(80);
        tblParcela.getColumn("MP").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

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

        crediarioJTableModel.clear();
        crediarioJTableModel.addList(parcelas);

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
        if (!parcelas.isEmpty()) {
            total = parcelas.stream().map(Parcela::getValor).reduce(BigDecimal::add).get();
            totalRecebido = parcelas.stream().map(Parcela::getValorQuitado).reduce(BigDecimal::add).get();
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

            if(parcelaReceberList.isEmpty() || parcelaRecebida) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Você selecionou uma ou mais parcelas já quitadas", "Atenção", JOptionPane.WARNING_MESSAGE);
                
            } else {
                ///PessoaCrediarioRecebimentoView r = new PessoaCrediarioRecebimentoView(MAIN_VIEW, parcelaReceberList);
                
                new RecebimentoNovoView(parcelaReceberList);
                
                carregarTabela();
            }
        }
    }
    
    private void receberAntigo() {
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

            if(parcelaReceberList.isEmpty() || parcelaRecebida) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Você selecionou uma ou mais parcelas já quitadas", "Atenção", JOptionPane.WARNING_MESSAGE);
                
            } else {
                new PessoaCrediarioRecebimentoView(MAIN_VIEW, parcelaReceberList);
                
                //new RecebimentoNovoView(parcelaReceberList);
                
                carregarTabela();
            }
        }
    }

    private void editar() {
        if(crediarioJTableModel.getRow(tblParcela.getSelectedRow()).getVenda() != null) {
            Parcela p = crediarioJTableModel.getRow(tblParcela.getSelectedRow());
            new PessoaParcelaEditarView(MAIN_VIEW, p);
        }
        carregarTabela();
    }

    private void imprimirRecibo() {
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
            TermicaPrint.gerarRecibo(parcelaReceberList, pdfFilePath);

            new Toast("Imprimindo...");

            PrintPDFBox pPDF = new PrintPDFBox();
            pPDF.print(pdfFilePath, IMPRESSORA_CUPOM);
        }
    }
    
    private void imprimirLista() {
        ListaParcelasPrint.imprimirCupom(cliente, parcelas);
    }

    private void abrirDocumento() {
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
        btnEntrada = new javax.swing.JButton();
        btnEntrada1 = new javax.swing.JButton();
        btnImprimirRecibo = new javax.swing.JButton();
        btnImprimirRecibo1 = new javax.swing.JButton();
        btnImprimirRecibo2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtTotalRecebido = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtTotalReceber = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        cboSituacao = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        btnAtualizar = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtDataInicial = new javax.swing.JFormattedTextField();
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

        tblParcela.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
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

        btnEntrada.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-request-money-20.png"))); // NOI18N
        btnEntrada.setText("Receber");
        btnEntrada.setContentAreaFilled(false);
        btnEntrada.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEntrada.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntradaActionPerformed(evt);
            }
        });

        btnEntrada1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-close-button-20.png"))); // NOI18N
        btnEntrada1.setText("Antigo");
        btnEntrada1.setContentAreaFilled(false);
        btnEntrada1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEntrada1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEntrada1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntrada1ActionPerformed(evt);
            }
        });

        btnImprimirRecibo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-printer-20.png"))); // NOI18N
        btnImprimirRecibo.setText("Recibo");
        btnImprimirRecibo.setContentAreaFilled(false);
        btnImprimirRecibo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnImprimirRecibo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimirRecibo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirReciboActionPerformed(evt);
            }
        });

        btnImprimirRecibo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-printer-20.png"))); // NOI18N
        btnImprimirRecibo1.setText("Lista");
        btnImprimirRecibo1.setContentAreaFilled(false);
        btnImprimirRecibo1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnImprimirRecibo1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimirRecibo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirRecibo1ActionPerformed(evt);
            }
        });

        btnImprimirRecibo2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-external-link-20.png"))); // NOI18N
        btnImprimirRecibo2.setText("Abrir Documento");
        btnImprimirRecibo2.setContentAreaFilled(false);
        btnImprimirRecibo2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnImprimirRecibo2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimirRecibo2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirRecibo2ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Total");

        txtTotal.setEditable(false);
        txtTotal.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("Recebido");

        txtTotalRecebido.setEditable(false);
        txtTotalRecebido.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalRecebido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setText("Receber");

        txtTotalReceber.setEditable(false);
        txtTotalReceber.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalReceber.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnEntrada)
                .addGap(18, 18, 18)
                .addComponent(btnEntrada1)
                .addGap(18, 18, 18)
                .addComponent(btnImprimirRecibo)
                .addGap(18, 18, 18)
                .addComponent(btnImprimirRecibo1)
                .addGap(18, 18, 18)
                .addComponent(btnImprimirRecibo2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addGap(23, 23, 23)
                .addComponent(txtTotalRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(txtTotalReceber, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(txtTotalReceber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTotalRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addComponent(btnEntrada)
                    .addComponent(btnEntrada1)
                    .addComponent(btnImprimirRecibo)
                    .addComponent(btnImprimirRecibo1)
                    .addComponent(btnImprimirRecibo2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cboSituacao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cboSituacao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Em aberto + Vencido", "Em aberto", "Vencido", "Quitado" }));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Situação");

        btnAtualizar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnAtualizar.setText("Atualizar");
        btnAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Data Inicial");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Data Final");

        txtDataInicial.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDataInicial.setName("data"); // NOI18N

        txtDataFinal.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDataFinal.setName("data"); // NOI18N

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
                .addGap(18, 18, 18)
                .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnAtualizar)
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
                        .addComponent(jLabel6))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(btnAtualizar))
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
                    .addComponent(jScrollPane1)
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
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

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnAtualizarActionPerformed

    private void tblParcelaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblParcelaMouseClicked
        if (evt.getClickCount() == 2) {
            editar();
        }
    }//GEN-LAST:event_tblParcelaMouseClicked

    private void btnEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntradaActionPerformed
        receber();
    }//GEN-LAST:event_btnEntradaActionPerformed

    private void btnEntrada1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntrada1ActionPerformed
        receberAntigo();
    }//GEN-LAST:event_btnEntrada1ActionPerformed

    private void btnImprimirReciboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirReciboActionPerformed
        imprimirRecibo();
    }//GEN-LAST:event_btnImprimirReciboActionPerformed

    private void btnImprimirRecibo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirRecibo1ActionPerformed
        imprimirLista();
    }//GEN-LAST:event_btnImprimirRecibo1ActionPerformed

    private void btnImprimirRecibo2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirRecibo2ActionPerformed
        abrirDocumento();
    }//GEN-LAST:event_btnImprimirRecibo2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton btnEntrada;
    private javax.swing.JButton btnEntrada1;
    private javax.swing.JButton btnImprimirRecibo;
    private javax.swing.JButton btnImprimirRecibo1;
    private javax.swing.JButton btnImprimirRecibo2;
    private javax.swing.JComboBox<String> cboSituacao;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
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
