/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.financeiro;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.Timestamp;
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
import model.bean.principal.Caixa;
import model.bean.principal.DocumentoTipo;
import model.bean.principal.Parcela;
import model.bean.principal.ParcelaStatus;
import model.bean.principal.Venda;
import model.dao.principal.ParcelaDAO;
import model.dao.principal.CaixaDAO;
import model.jtable.ContasReceberJTableModel;
import static ouroboros.Constants.*;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.TO_PRINTER_PATH;
import static ouroboros.Ouroboros.em;
import printing.CriarPDF;
import printing.PrintPDFBox;
import util.JSwing;
import util.DateTime;
import util.Decimal;
import util.jTableFormat.CrediarioRenderer;
import view.Toast;
import view.pessoa.PessoaCrediarioRecebimentoView;
import view.pessoa.PessoaParcelaEditarView;
import view.venda.VendaView;
import static ouroboros.Ouroboros.IMPRESSORA_CUPOM;


/**
 *
 * @author ivand
 */
public class ContasReceberView extends javax.swing.JInternalFrame {
    private static ContasReceberView singleInstance = null;
    ContasReceberJTableModel contasReceberJTableModel = new ContasReceberJTableModel();
    ParcelaDAO parcelaDAO = new ParcelaDAO();

    List<Parcela> parcelaList = new ArrayList<>();
    
    DocumentoTipo documentoTipo = DocumentoTipo.SAIDA;

    public static ContasReceberView getSingleInstance(){
        if(singleInstance == null){
            singleInstance = new ContasReceberView();
        }
        return singleInstance;
    }
    
    /**
     * Creates new form CategoriaCadastroView
     */
    private ContasReceberView() {
        initComponents();
        JSwing.startComponentsBehavior(this);
        
        cboSituacao.setSelectedIndex(1);
        
        txtDataInicial.setText(DateTime.toStringDate(LocalDate.now().minusMonths(1)));
        txtDataFinal.setText(DateTime.toStringDate(LocalDate.now().plusMonths(1)));
        
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
        tblCrediario.setModel(contasReceberJTableModel);
        
        //tblCrediario.setDefaultRenderer(Object.class, new CrediarioRenderer());

        tblCrediario.setRowHeight(24);
        tblCrediario.setIntercellSpacing(new Dimension(10, 10));
        
        tblCrediario.getColumn("Status").setPreferredWidth(120);
        CrediarioRenderer crediarioRenderer = new CrediarioRenderer();
        crediarioRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblCrediario.getColumn("Status").setCellRenderer(crediarioRenderer);
        
        tblCrediario.getColumn("Vencimento").setPreferredWidth(120);
        tblCrediario.getColumn("Vencimento").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblCrediario.getColumn("Venda").setPreferredWidth(100);
        tblCrediario.getColumn("Venda").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblCrediario.getColumn("Parcela").setPreferredWidth(100);
        tblCrediario.getColumn("Parcela").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblCrediario.getColumn("Cliente").setPreferredWidth(300);
        
        tblCrediario.getColumn("Valor").setPreferredWidth(120);
        tblCrediario.getColumn("Valor").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblCrediario.getColumn("Dias Atraso").setPreferredWidth(120);
        tblCrediario.getColumn("Dias Atraso").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblCrediario.getColumn("Multa %").setPreferredWidth(100);
        tblCrediario.getColumn("Multa %").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblCrediario.getColumn("M. Calc.").setPreferredWidth(100);
        tblCrediario.getColumn("M. Calc.").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblCrediario.getColumn("Juros").setPreferredWidth(100);
        tblCrediario.getColumn("Juros").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblCrediario.getColumn("J. Calc.").setPreferredWidth(100);
        tblCrediario.getColumn("J. Calc.").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblCrediario.getColumn("Valor Atual").setPreferredWidth(120);
        tblCrediario.getColumn("Valor Atual").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblCrediario.getColumn("Acrésc %").setPreferredWidth(120);
        tblCrediario.getColumn("Acrésc %").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblCrediario.getColumn("Desc %").setPreferredWidth(120);
        tblCrediario.getColumn("Desc %").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblCrediario.getColumn("Valor Recebido").setPreferredWidth(120);
        tblCrediario.getColumn("Valor Recebido").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblCrediario.getColumn("Data Recebido").setPreferredWidth(120);
        tblCrediario.getColumn("Data Recebido").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblCrediario.getColumn("Meio Pagto").setPreferredWidth(120);
        
        tblCrediario.getColumn("Observação").setPreferredWidth(160);
    }
    
    private void novo() {
        //CategoriaCadastro categoriaCadastro = new CategoriaCadastro(MAIN_VIEW, new Categoria());
        //carregarTabela();
    }
    
    private void editar() {
        Parcela p = contasReceberJTableModel.getRow(tblCrediario.getSelectedRow());

        PessoaParcelaEditarView edtView  = new PessoaParcelaEditarView(MAIN_VIEW, p);
        carregarTabela();
    }

    private void catchClick() {
        int indices[] = tblCrediario.getSelectedRows();

        ArrayList<Integer> ids = new ArrayList<>();
        for (int index : indices) {
            ids.add(contasReceberJTableModel.getRow(index).getId());
        }
        System.out.println("index: " + tblCrediario.getSelectedRow());
    }

    private void carregarTabela() {
        
        Timestamp dataInicial = DateTime.fromString(txtDataInicial.getText());
        Timestamp dataFinal = DateTime.fromString(txtDataFinal.getText() + " 23:59:59");

        List<ParcelaStatus> listStatus = new ArrayList<>();
        switch (cboSituacao.getSelectedIndex()) {
            case 0: //Todos
                parcelaList = new ParcelaDAO().findPorData(dataInicial, dataFinal, documentoTipo);
                break;
            case 1: //Em aberto + Vencido
                listStatus.add(ParcelaStatus.ABERTO);
                listStatus.add(ParcelaStatus.VENCIDO);
                parcelaList = new ParcelaDAO().findPorStatus(null, listStatus, dataInicial, dataFinal, documentoTipo);
                break;
            case 2: //Em aberto
                listStatus.add(ParcelaStatus.ABERTO);
                parcelaList = new ParcelaDAO().findPorStatus(null, listStatus, dataInicial, dataFinal, documentoTipo);
                break;
            case 3: //Vencido
                listStatus.add(ParcelaStatus.VENCIDO);
                parcelaList = new ParcelaDAO().findPorStatus(null, listStatus, dataInicial, dataFinal, documentoTipo);
                break;
            case 4: //Quitado
                listStatus.add(ParcelaStatus.QUITADO);
                parcelaList = new ParcelaDAO().findPorStatus(null, listStatus, dataInicial, dataFinal, documentoTipo);
                break;
        }
        
        // modelo para manter posição da tabela - melhorar: caso altere o vencimento, muda a ordem! :<
        int rowIndex = tblCrediario.getSelectedRow();
        
        contasReceberJTableModel.clear();
        contasReceberJTableModel.addList(parcelaList);

        //posicionar na última linha
        if(tblCrediario.getRowCount() > 0) {
            if(rowIndex < 0 || rowIndex >= tblCrediario.getRowCount()) {
                rowIndex = 0;
            }
            //JOptionPane.showMessageDialog(rootPane, rowIndex);
            tblCrediario.setRowSelectionInterval(rowIndex, rowIndex);
            tblCrediario.scrollRectToVisible(tblCrediario.getCellRect(rowIndex, 0, true));
        }
        //------------------------------------------
        
        //totais
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalRecebido = BigDecimal.ZERO;
        BigDecimal totalReceber = BigDecimal.ZERO;
        if(!parcelaList.isEmpty()) {
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
            for(int index : tblCrediario.getSelectedRows()) {
                Parcela p = contasReceberJTableModel.getRow(index);
                if(p.getStatus() == ParcelaStatus.QUITADO) {
                    parcelaRecebida = true;
                    break;
                }
                parcelaReceberList.add(p);
            }
        
            if(parcelaRecebida) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Você selecionou uma ou mais parcelas já recebidas", "Atenção", JOptionPane.WARNING_MESSAGE);
            } else {
                PessoaCrediarioRecebimentoView r = new PessoaCrediarioRecebimentoView(MAIN_VIEW, parcelaReceberList);
                for(Parcela p : parcelaReceberList) {
                     em.refresh(p);
                }
                carregarTabela();
            }
        }
    }
    
    private void imprimir() {
        boolean parcelaNaoRecebida = false;
        List<Parcela> parcelaReceberList = new ArrayList<>();
        for(int index : tblCrediario.getSelectedRows()) {
            Parcela p = contasReceberJTableModel.getRow(index);
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
            CriarPDF.criarRecibo80mm(parcelaReceberList, pdfFilePath);

            new Toast("Imprimindo...");

            PrintPDFBox pPDF = new PrintPDFBox();
            pPDF.print(pdfFilePath, IMPRESSORA_CUPOM);
        }
    }
    
    private void abrirVenda() {
        Set<Venda> setVendas = new HashSet<>();
        int[] rowIndices = tblCrediario.getSelectedRows();
        for (int rowIndex : rowIndices) {
            Venda venda = contasReceberJTableModel.getRow(rowIndex).getVenda();
            setVendas.add(venda);
        }
        
        for(Venda venda : setVendas) {
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
        tblCrediario = new javax.swing.JTable();
        lblMensagem = new javax.swing.JLabel();
        lblRegistrosExibidos = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnReceber = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTotalRecebido = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtTotalReceber = new javax.swing.JTextField();
        btnRecibo = new javax.swing.JButton();
        btnAbrirVenda = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        cboSituacao = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        btnFiltrar = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        txtDataInicial = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        txtDataFinal = new javax.swing.JFormattedTextField();

        setClosable(true);
        setTitle("Categorias");
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

        tblCrediario.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCrediario.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblCrediario.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblCrediarioFocusGained(evt);
            }
        });
        tblCrediario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCrediarioMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCrediario);

        lblMensagem.setText("...");

        lblRegistrosExibidos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRegistrosExibidos.setText("0");

        jLabel4.setText("Registros exibidos:");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnReceber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/creditcards.png"))); // NOI18N
        btnReceber.setText("Receber");
        btnReceber.setContentAreaFilled(false);
        btnReceber.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnReceber.setIconTextGap(10);
        btnReceber.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnReceber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReceberActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Total");

        txtTotal.setEditable(false);
        txtTotal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Total Recebido");

        txtTotalRecebido.setEditable(false);
        txtTotalRecebido.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTotalRecebido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Total Receber");

        txtTotalReceber.setEditable(false);
        txtTotalReceber.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTotalReceber.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        btnRecibo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/printer.png"))); // NOI18N
        btnRecibo.setText("Imprimir Recibo");
        btnRecibo.setContentAreaFilled(false);
        btnRecibo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRecibo.setIconTextGap(10);
        btnRecibo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRecibo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReciboActionPerformed(evt);
            }
        });

        btnAbrirVenda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/money.png"))); // NOI18N
        btnAbrirVenda.setText("Abrir Venda");
        btnAbrirVenda.setContentAreaFilled(false);
        btnAbrirVenda.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAbrirVenda.setIconTextGap(10);
        btnAbrirVenda.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAbrirVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirVendaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnReceber, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnAbrirVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTotal)
                    .addComponent(txtTotalRecebido)
                    .addComponent(txtTotalReceber, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(btnRecibo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAbrirVenda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cboSituacao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Em aberto + Vencido", "Em aberto", "Vencido", "Quitado" }));

        jLabel14.setText("Situação");

        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        jLabel15.setText("Data Inicial");

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

        jLabel16.setText("Data Final");

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

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addComponent(cboSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel15)
                .addGap(18, 18, 18)
                .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel16)
                .addGap(18, 18, 18)
                .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnFiltrar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15)
                    .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 1264, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRegistrosExibidos, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(lblMensagem, javax.swing.GroupLayout.PREFERRED_SIZE, 533, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblMensagem)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblRegistrosExibidos)
                            .addComponent(jLabel4)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                .addGap(18, 18, 18)
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

    private void btnReceberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReceberActionPerformed
        receber();
    }//GEN-LAST:event_btnReceberActionPerformed

    private void tblCrediarioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCrediarioMouseClicked
        //catchClick();
        if (evt.getClickCount() == 2) {
            editar();
        }

    }//GEN-LAST:event_tblCrediarioMouseClicked

    private void tblCrediarioFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblCrediarioFocusGained
        //tableCategoriasUpdateRow();

    }//GEN-LAST:event_tblCrediarioFocusGained

    private void txtDataFinalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDataFinalFocusLost
        if(txtDataFinal.getText().contains("/  /")){
            txtDataFinal.setValue(null);
        }
    }//GEN-LAST:event_txtDataFinalFocusLost

    private void txtDataInicialFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDataInicialFocusLost
        if(txtDataInicial.getText().contains("/  /")){
            txtDataInicial.setValue(null);
        }
    }//GEN-LAST:event_txtDataInicialFocusLost

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnReciboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReciboActionPerformed
        imprimir();
    }//GEN-LAST:event_btnReciboActionPerformed

    private void btnAbrirVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirVendaActionPerformed
        abrirVenda();
    }//GEN-LAST:event_btnAbrirVendaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrirVenda;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnReceber;
    private javax.swing.JButton btnRecibo;
    private javax.swing.JComboBox<String> cboSituacao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMensagem;
    private javax.swing.JLabel lblRegistrosExibidos;
    private javax.swing.JTable tblCrediario;
    private javax.swing.JFormattedTextField txtDataFinal;
    private javax.swing.JFormattedTextField txtDataInicial;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtTotalReceber;
    private javax.swing.JTextField txtTotalRecebido;
    // End of variables declaration//GEN-END:variables
}
