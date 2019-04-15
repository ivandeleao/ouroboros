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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import model.mysql.bean.principal.ContaPagar;
import model.mysql.bean.principal.ContaPagarStatus;
import model.mysql.dao.principal.ContaPagarDAO;
import model.jtable.financeiro.ContasPagarJTableModel;
import static ouroboros.Constants.*;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.JSwing;
import util.DateTime;
import util.Decimal;


/**
 *
 * @author ivand
 */
public class ContasPagarView extends javax.swing.JInternalFrame {
    private static ContasPagarView singleInstance = null;
    ContasPagarJTableModel contasPagarJTableModel = new ContasPagarJTableModel();
    ContaPagarDAO contaPagarDAO = new ContaPagarDAO();
    List<ContaPagar> contas = new ArrayList<>();
    
    

    public static ContasPagarView getSingleInstance(){
        if(singleInstance == null){
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
        
        cboSituacao.setSelectedIndex(1);
        
        txtDataInicial.setText(DateTime.toStringDate(LocalDate.now().minusMonths(1)));
        txtDataFinal.setText(DateTime.toStringDate(LocalDate.now().plusMonths(4)));
        
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
        tblContasPagar.setModel(contasPagarJTableModel);
        
        //tblCrediario.setDefaultRenderer(Object.class, new CrediarioRenderer());

        tblContasPagar.setRowHeight(24);
        tblContasPagar.setIntercellSpacing(new Dimension(10, 10));
        
        tblContasPagar.getColumn("Status").setPreferredWidth(120);
        //CrediarioRenderer crediarioRenderer = new CrediarioRenderer();
        //crediarioRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        //tblContasPagar.getColumn("Status").setCellRenderer(crediarioRenderer);
        
        tblContasPagar.getColumn("Vencimento").setPreferredWidth(120);
        tblContasPagar.getColumn("Vencimento").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblContasPagar.getColumn("Nome").setPreferredWidth(300);
        
        tblContasPagar.getColumn("Valor").setPreferredWidth(100);
        tblContasPagar.getColumn("Valor").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblContasPagar.getColumn("Data Pagto").setPreferredWidth(100);
        tblContasPagar.getColumn("Data Pagto").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblContasPagar.getColumn("Valor Pago").setPreferredWidth(100);
        tblContasPagar.getColumn("Valor Pago").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblContasPagar.getColumn("Observação").setPreferredWidth(200);
    }
    
    private void novo() {
        //CategoriaCadastro categoriaCadastro = new CategoriaCadastro(MAIN_VIEW, new Categoria());
        //carregarTabela();
    }
    
    private void editar() {
        /*ContaPagar p = contasPagarJTableModel.getRow(tblContasPagar.getSelectedRow());

        ClienteContaPagarEditarView edtView  = new ClienteContaPagarEditarView(MAIN_VIEW, p);
        carregarTabela();*/
    }

    private void catchClick() {
        /*int indices[] = tblContasPagar.getSelectedRows();

        ArrayList<Integer> ids = new ArrayList<>();
        for (int index : indices) {
            ids.add(contasPagarJTableModel.getRow(index).getId());
        }
        System.out.println("index: " + tblContasPagar.getSelectedRow());*/
    }

    private void carregarTabela() {
        
        LocalDate dataInicial = DateTime.fromStringDateLDT(txtDataInicial.getText());
        LocalDate dataFinal = DateTime.fromStringDateLDT(txtDataFinal.getText());
        
        List<ContaPagarStatus> listStatus = new ArrayList<>();
        
        
        switch (cboSituacao.getSelectedIndex()) {
            case 0: //Todos
                contas = contaPagarDAO.findPorPeriodo(dataInicial, dataFinal, null);
                break;
            case 1: //Em aberto + Vencido
                listStatus.add(ContaPagarStatus.ABERTO);
                listStatus.add(ContaPagarStatus.VENCIDO);
                contas = contaPagarDAO.findPorPeriodo(dataInicial, dataFinal, listStatus);
                break;
            case 2: //Em aberto
                listStatus.add(ContaPagarStatus.ABERTO);
                contas = contaPagarDAO.findPorPeriodo(dataInicial, dataFinal, listStatus);
                break;
            case 3: //Vencido
                listStatus.add(ContaPagarStatus.VENCIDO);
                contas = contaPagarDAO.findPorPeriodo(dataInicial, dataFinal, listStatus);
                break;
            case 4: //Quitado
                listStatus.add(ContaPagarStatus.QUITADO);
                contas = contaPagarDAO.findPorPeriodo(dataInicial, dataFinal, listStatus);
                break;
        }
        
        
                
        // modelo para manter posição da tabela - melhorar: caso altere o vencimento, muda a ordem! :<
        int rowIndex = tblContasPagar.getSelectedRow();
        
        contasPagarJTableModel.clear();
        contasPagarJTableModel.addList(contas);

        //posicionar na última linha
        if(tblContasPagar.getRowCount() > 0) {
            if(rowIndex < 0 || rowIndex >= tblContasPagar.getRowCount()) {
                rowIndex = 0;
            }
            //JOptionPane.showMessageDialog(rootPane, rowIndex);
            tblContasPagar.setRowSelectionInterval(rowIndex, rowIndex);
            tblContasPagar.scrollRectToVisible(tblContasPagar.getCellRect(rowIndex, 0, true));
        }
        //------------------------------------------
        
        //totais
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalPago = BigDecimal.ZERO;
        BigDecimal totalPagar = BigDecimal.ZERO;
        if(!contas.isEmpty()) {
            total = contas.stream().map(ContaPagar::getValor).reduce(BigDecimal::add).get();
            totalPago = contas.stream().map(ContaPagar::getValorPago).reduce(BigDecimal::add).get();
            totalPagar = total.subtract(totalPago);
        }
        txtTotal.setText(Decimal.toString(total));
        txtTotalPago.setText(Decimal.toString(totalPago));
        txtTotalPagar.setText(Decimal.toString(totalPagar));
    }
    
    private void contasProgramadas() {
        ContaProgramadaListaView contaProgramadaListaView = new ContaProgramadaListaView();
        carregarTabela();
    }
    
    
    private void pagar() {
        ContaPagar contaPagar = contasPagarJTableModel.getRow(tblContasPagar.getSelectedRow());
        
        if(contaPagar.getContaProgramadaBaixa() != null && contaPagar.getContaProgramadaBaixa().getCaixaItem() != null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Esta conta já foi paga.", "Atenção", JOptionPane.WARNING_MESSAGE);
            
        } else {
            ContaProgramadaPagarView pagar = new ContaProgramadaPagarView(contaPagar);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblContasPagar = new javax.swing.JTable();
        lblMensagem = new javax.swing.JLabel();
        lblRegistrosExibidos = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnContasProgramadas = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTotalPago = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtTotalPagar = new javax.swing.JTextField();
        btnContasProgramadas1 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        cboSituacao = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        btnFiltrar = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        txtDataInicial = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        txtDataFinal = new javax.swing.JFormattedTextField();

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
        jScrollPane1.setViewportView(tblContasPagar);

        lblMensagem.setText("...");

        lblRegistrosExibidos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRegistrosExibidos.setText("0");

        jLabel4.setText("Registros exibidos:");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnContasProgramadas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/date.png"))); // NOI18N
        btnContasProgramadas.setText("Contas Programadas");
        btnContasProgramadas.setContentAreaFilled(false);
        btnContasProgramadas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnContasProgramadas.setIconTextGap(10);
        btnContasProgramadas.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnContasProgramadas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContasProgramadasActionPerformed(evt);
            }
        });

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

        btnContasProgramadas1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/creditcards.png"))); // NOI18N
        btnContasProgramadas1.setText("Pagar");
        btnContasProgramadas1.setContentAreaFilled(false);
        btnContasProgramadas1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnContasProgramadas1.setIconTextGap(10);
        btnContasProgramadas1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnContasProgramadas1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContasProgramadas1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnContasProgramadas, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnContasProgramadas1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(btnContasProgramadas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnContasProgramadas1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
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

    private void btnContasProgramadasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContasProgramadasActionPerformed
        contasProgramadas();
    }//GEN-LAST:event_btnContasProgramadasActionPerformed

    private void tblContasPagarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblContasPagarMouseClicked
        //catchClick();
        if (evt.getClickCount() == 2) {
            editar();
        }
    }//GEN-LAST:event_tblContasPagarMouseClicked

    private void tblContasPagarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblContasPagarFocusGained
        //tableCategoriasUpdateRow();

    }//GEN-LAST:event_tblContasPagarFocusGained

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

    private void btnContasProgramadas1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContasProgramadas1ActionPerformed
        pagar();
    }//GEN-LAST:event_btnContasProgramadas1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnContasProgramadas;
    private javax.swing.JButton btnContasProgramadas1;
    private javax.swing.JButton btnFiltrar;
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
    private javax.swing.JTable tblContasPagar;
    private javax.swing.JFormattedTextField txtDataFinal;
    private javax.swing.JFormattedTextField txtDataInicial;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtTotalPagar;
    private javax.swing.JTextField txtTotalPago;
    // End of variables declaration//GEN-END:variables
}
