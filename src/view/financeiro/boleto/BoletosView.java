/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.financeiro.boleto;

import boleto.Boleto;
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
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import model.jtable.financeiro.BoletosJTableModel;
import model.mysql.bean.principal.financeiro.Caixa;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.documento.Parcela;
import model.nosql.FinanceiroStatusEnum;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.principal.ParcelaDAO;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.financeiro.Conta;
import model.mysql.dao.principal.financeiro.ContaDAO;
import static ouroboros.Constants.*;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.TO_PRINTER_PATH;
import printing.documento.TermicaPrint;
import printing.PrintPDFBox;
import util.JSwing;
import util.DateTime;
import util.Decimal;
import util.jTableFormat.FinanceiroStatusRenderer;
import view.Toast;
import view.pessoa.PessoaCrediarioRecebimentoView;
import view.pessoa.PessoaParcelaEditarView;
import view.documentoSaida.item.VendaView;
import static ouroboros.Ouroboros.IMPRESSORA_CUPOM;
import static ouroboros.Ouroboros.MAIN_VIEW;
import printing.documento.BoletoPrint;
import view.financeiro.RecebimentoParcelaNovoView;

/**
 *
 * @author ivand
 */
public class BoletosView extends javax.swing.JInternalFrame {

    private static BoletosView singleInstance = null;
    BoletosJTableModel boletosJTableModel = new BoletosJTableModel();
    ParcelaDAO parcelaDAO = new ParcelaDAO();

    List<Parcela> parcelas = new ArrayList<>();

    TipoOperacao tipoOperacao = TipoOperacao.SAIDA;
    
    private String status;
    private LocalDate dataInicial, dataFinal;
    private Optional<Boolean> impressao, remessa;
    private BigDecimal total, totalAtualizado, totalRecebido;

    public static BoletosView getSingleInstance() {
        if (singleInstance == null) {
            singleInstance = new BoletosView();
        }
        return singleInstance;
    }

    /**
     * Creates new form CategoriaCadastroView
     */
    private BoletosView() {
        initComponents();
        JSwing.startComponentsBehavior(this);

        txtDataInicial.setText(DateTime.toString(LocalDate.now().minusMonths(1)));
        txtDataFinal.setText(DateTime.toString(LocalDate.now().plusMonths(1)));

        cboImpressao.setSelectedIndex(2);
        cboRemessa.setSelectedIndex(2);
        
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
        tblBoletos.setModel(boletosJTableModel);

        tblBoletos.setRowHeight(30);
        tblBoletos.setIntercellSpacing(new Dimension(10, 10));

        tblBoletos.getColumn("Status").setPreferredWidth(120);
        FinanceiroStatusRenderer crediarioRenderer = new FinanceiroStatusRenderer();
        crediarioRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblBoletos.getColumn("Status").setCellRenderer(crediarioRenderer);

        tblBoletos.getColumn("Vencimento").setPreferredWidth(120);
        tblBoletos.getColumn("Vencimento").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblBoletos.getColumn("Venda").setPreferredWidth(100);
        tblBoletos.getColumn("Venda").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblBoletos.getColumn("Parcela").setPreferredWidth(100);
        tblBoletos.getColumn("Parcela").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblBoletos.getColumn("Cliente").setPreferredWidth(300);

        tblBoletos.getColumn("Valor").setPreferredWidth(120);
        tblBoletos.getColumn("Valor").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblBoletos.getColumn("Valor Atual").setPreferredWidth(120);
        tblBoletos.getColumn("Valor Atual").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblBoletos.getColumn("Valor Recebido").setPreferredWidth(120);
        tblBoletos.getColumn("Valor Recebido").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblBoletos.getColumn("Data Recebido").setPreferredWidth(120);
        tblBoletos.getColumn("Data Recebido").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblBoletos.getColumn("Observação").setPreferredWidth(120);
        
        tblBoletos.getColumn("Impressão").setPreferredWidth(120);
        tblBoletos.getColumn("Impressão").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblBoletos.getColumn("Remessa").setPreferredWidth(120);
        tblBoletos.getColumn("Remessa").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblBoletos.getColumn("Nosso Número").setPreferredWidth(140);
        tblBoletos.getColumn("Nosso Número").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

    }

    private void editar() {
        if(boletosJTableModel.getRow(tblBoletos.getSelectedRow()).getVenda() != null) {
            Parcela p = boletosJTableModel.getRow(tblBoletos.getSelectedRow());
            PessoaParcelaEditarView edtView = new PessoaParcelaEditarView(MAIN_VIEW, p);
        }
        carregarTabela();
    }

    private void catchClick() {
        int indices[] = tblBoletos.getSelectedRows();

        ArrayList<Integer> ids = new ArrayList<>();
        for (int index : indices) {
            ids.add(boletosJTableModel.getRow(index).getId());
        }
        //System.out.println("index: " + tblCrediario.getSelectedRow());
    }

    private void carregarTabela() {
        dataInicial = DateTime.fromStringToLocalDate(txtDataInicial.getText());
        dataFinal = DateTime.fromStringToLocalDate(txtDataFinal.getText());
        impressao = cboImpressao.getSelectedIndex() == 0 ? Optional.empty()
                : (cboImpressao.getSelectedIndex() == 1 ? Optional.of(true) : Optional.of(false));
        remessa = cboRemessa.getSelectedIndex() == 0 ? Optional.empty()
                : (cboRemessa.getSelectedIndex() == 1 ? Optional.of(true) : Optional.of(false));

        parcelas = new ParcelaDAO().findByCriteria(null, dataInicial, dataFinal, TipoOperacao.SAIDA, null, MeioDePagamento.BOLETO_BANCARIO, impressao, remessa);
        
        /*List<FinanceiroStatus> listStatus = new ArrayList<>();
        switch (cboSituacao.getSelectedIndex()) {
            case 0: //Todos
                parcelas = new ParcelaDAO().findPorData(dataInicial, dataFinal, tipoOperacao, Optional.of(false));
                break;
            case 1: //Em aberto + Vencido
                listStatus.add(FinanceiroStatusEnum.ABERTO);
                listStatus.add(FinanceiroStatusEnum.VENCIDO);
                parcelas = new ParcelaDAO().findPorStatus(null, listStatus, dataInicial, dataFinal, tipoOperacao, Optional.of(false));
                break;
            case 2: //Em aberto
                listStatus.add(FinanceiroStatusEnum.ABERTO);
                parcelas = new ParcelaDAO().findPorStatus(null, listStatus, dataInicial, dataFinal, tipoOperacao, Optional.of(false));
                break;
            case 3: //Vencido
                listStatus.add(FinanceiroStatusEnum.VENCIDO);
                parcelas = new ParcelaDAO().findPorStatus(null, listStatus, dataInicial, dataFinal, tipoOperacao, Optional.of(false));
                break;
            case 4: //Quitado
                listStatus.add(FinanceiroStatusEnum.QUITADO);
                parcelas = new ParcelaDAO().findPorStatus(null, listStatus, dataInicial, dataFinal, tipoOperacao, Optional.of(false));
                break;
        }*/

        // modelo para manter posição da tabela - melhorar: caso altere o vencimento, muda a ordem! :<
        int rowIndex = tblBoletos.getSelectedRow();

        boletosJTableModel.clear();
        boletosJTableModel.addList(parcelas);

        //posicionar na última linha
        if (tblBoletos.getRowCount() > 0) {
            if (rowIndex < 0 || rowIndex >= tblBoletos.getRowCount()) {
                rowIndex = 0;
            }
            //JOptionPane.showMessageDialog(rootPane, rowIndex);
            tblBoletos.setRowSelectionInterval(rowIndex, rowIndex);
            tblBoletos.scrollRectToVisible(tblBoletos.getCellRect(rowIndex, 0, true));
        }
        //------------------------------------------

        //totais
        total = BigDecimal.ZERO;
        totalRecebido = BigDecimal.ZERO;
        totalAtualizado = BigDecimal.ZERO;
        
        if (!parcelas.isEmpty()) {
            total = parcelas.stream().map(Parcela::getValor).reduce(BigDecimal::add).get();
            totalRecebido = parcelas.stream().map(Parcela::getValorQuitado).reduce(BigDecimal::add).get();
            totalAtualizado = parcelas.stream().map(Parcela::getValorAtual).reduce(BigDecimal::add).get();
        }
        txtTotal.setText(Decimal.toString(total));
        txtTotalRecebido.setText(Decimal.toString(totalRecebido));
        txtTotalAtualizado.setText(Decimal.toString(totalAtualizado));
    }

    private void receber() {
        Caixa lastCaixa = Ouroboros.FINANCEIRO_CAIXA_PRINCIPAL.getLastCaixa(); //2020-02-28
        if (lastCaixa == null || lastCaixa.getEncerramento() != null) {
            JOptionPane.showMessageDialog(rootPane, "Não há turno de caixa aberto. Não é possível realizar recebimentos.", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            boolean parcelaRecebida = false;
            List<Parcela> parcelaReceberList = new ArrayList<>();
            for (int index : tblBoletos.getSelectedRows()) {
                if (boletosJTableModel.getRow(index).getVenda() != null) {
                    Parcela p = boletosJTableModel.getRow(index);
                    if (p.getStatus() == FinanceiroStatusEnum.QUITADO) {
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
                new RecebimentoParcelaNovoView(parcelaReceberList);
                carregarTabela();
            }
        }
    }
    
    private void receberAntigo() {
        Caixa lastCaixa = Ouroboros.FINANCEIRO_CAIXA_PRINCIPAL.getLastCaixa(); //2020-02-28
        if (lastCaixa == null || lastCaixa.getEncerramento() != null) {
            JOptionPane.showMessageDialog(rootPane, "Não há turno de caixa aberto. Não é possível realizar recebimentos.", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            boolean parcelaRecebida = false;
            List<Parcela> parcelaReceberList = new ArrayList<>();
            for (int index : tblBoletos.getSelectedRows()) {
                if (boletosJTableModel.getRow(index).getVenda() != null) {
                    Parcela p = boletosJTableModel.getRow(index);
                    if (p.getStatus() == FinanceiroStatusEnum.QUITADO) {
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
                    ////em.refresh(p);
                }
                carregarTabela();
            }
        }
    }

    private void imprimirRecibo() {
        boolean parcelaNaoRecebida = false;
        List<Parcela> parcelaReceberList = new ArrayList<>();
        for (int index : tblBoletos.getSelectedRows()) {
            Parcela p = boletosJTableModel.getRow(index);
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

    private void abrirDocumento() {
        Set<Venda> setVendas = new HashSet<>();
        int[] rowIndices = tblBoletos.getSelectedRows();
        for (int rowIndex : rowIndices) {
            if (boletosJTableModel.getRow(rowIndex).getVenda() != null) {
                Venda venda = boletosJTableModel.getRow(rowIndex).getVenda();
                setVendas.add(venda);
            }
        }

        for (Venda venda : setVendas) {
            System.out.println("venda id: " + venda.getId());
            MAIN_VIEW.addView(VendaView.getInstance(venda));
        }
    }
    
    private void processar() {
        List<Parcela> parcelas = new ArrayList<>();
        for (int rowIndex : tblBoletos.getSelectedRows()) {
            parcelas.add(boletosJTableModel.getRow(rowIndex));
        }

        if (parcelas.isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem parcelas. Selecione registros para imprimir.", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            Conta conta = new ContaDAO().findAllBoleto().get(0); //to do - selecionar conta
            Boleto.gerarArquivo(conta, parcelas);
            
            //atualizar grid
            for (int rowIndex : tblBoletos.getSelectedRows()) {
                boletosJTableModel.fireTableRowsUpdated(rowIndex, rowIndex);
            }
            
        }
    }
    
    private void imprimir() {
        List<Parcela> parcelas = new ArrayList<>();
        for (int rowIndex : tblBoletos.getSelectedRows()) {
            parcelas.add(boletosJTableModel.getRow(rowIndex));
        }

        if (parcelas.isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem parcelas. Selecione registros para imprimir.", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            Conta conta = new ContaDAO().findAllBoleto().get(0); //to do - selecionar conta
            BoletoPrint.gerarBoleto(conta, parcelas);
            
            //atualizar grid
            for (int rowIndex : tblBoletos.getSelectedRows()) {
                boletosJTableModel.fireTableRowsUpdated(rowIndex, rowIndex);
            }
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
        tblBoletos = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btnReceber = new javax.swing.JButton();
        btnRecibo = new javax.swing.JButton();
        btnAbrirVenda = new javax.swing.JButton();
        btnImprimir = new javax.swing.JButton();
        btnRemessa = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        btnFiltrar = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtDataInicial = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtDataFinal = new javax.swing.JFormattedTextField();
        cboImpressao = new javax.swing.JComboBox<>();
        cboRemessa = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtTotalAtualizado = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtTotalRecebido = new javax.swing.JTextField();

        setClosable(true);
        setTitle("Boletos");
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

        tblBoletos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblBoletos.setModel(new javax.swing.table.DefaultTableModel(
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
        tblBoletos.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblBoletos.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblBoletosFocusGained(evt);
            }
        });
        tblBoletos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBoletosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblBoletos);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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

        btnRecibo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-printer-20.png"))); // NOI18N
        btnRecibo.setText("Recibo");
        btnRecibo.setContentAreaFilled(false);
        btnRecibo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRecibo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRecibo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReciboActionPerformed(evt);
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

        btnRemessa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-boleto-bankario-20.png"))); // NOI18N
        btnRemessa.setText("Remessa");
        btnRemessa.setContentAreaFilled(false);
        btnRemessa.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRemessa.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemessa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemessaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnReceber)
                .addGap(18, 18, 18)
                .addComponent(btnRecibo)
                .addGap(18, 18, 18)
                .addComponent(btnAbrirVenda)
                .addGap(18, 18, 18)
                .addComponent(btnRemessa)
                .addGap(18, 18, 18)
                .addComponent(btnImprimir)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnReceber, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRecibo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAbrirVenda, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnImprimir, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemessa, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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

        cboImpressao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cboImpressao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Impressão ---", "Impressão Sim", "Impressão Não" }));

        cboRemessa.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cboRemessa.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Remessa ---", "Remessa Sim", "Remessa Não" }));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cboImpressao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cboRemessa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnFiltrar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboImpressao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboRemessa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnFiltrar))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Total");

        txtTotal.setEditable(false);
        txtTotal.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setText("Total Atualizado (receber)");

        txtTotalAtualizado.setEditable(false);
        txtTotalAtualizado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalAtualizado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("Recebido");

        txtTotalRecebido.setEditable(false);
        txtTotalRecebido.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalRecebido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(txtTotalRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(txtTotalAtualizado, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtTotalRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtTotalAtualizado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1164, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void tblBoletosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBoletosMouseClicked
        //catchClick();
        if (evt.getClickCount() == 2) {
            editar();
        }

    }//GEN-LAST:event_tblBoletosMouseClicked

    private void tblBoletosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblBoletosFocusGained
        //tableCategoriasUpdateRow();

    }//GEN-LAST:event_tblBoletosFocusGained

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnReciboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReciboActionPerformed
        imprimirRecibo();
    }//GEN-LAST:event_btnReciboActionPerformed

    private void btnAbrirVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirVendaActionPerformed
        abrirDocumento();
    }//GEN-LAST:event_btnAbrirVendaActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        imprimir();
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void btnRemessaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemessaActionPerformed
        processar();
    }//GEN-LAST:event_btnRemessaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrirVenda;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnReceber;
    private javax.swing.JButton btnRecibo;
    private javax.swing.JButton btnRemessa;
    private javax.swing.JComboBox<String> cboImpressao;
    private javax.swing.JComboBox<String> cboRemessa;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblBoletos;
    private javax.swing.JFormattedTextField txtDataFinal;
    private javax.swing.JFormattedTextField txtDataInicial;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtTotalAtualizado;
    private javax.swing.JTextField txtTotalRecebido;
    // End of variables declaration//GEN-END:variables
}
