/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.financeiro;

import java.awt.Dimension;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import model.bean.principal.Caixa;
import model.bean.principal.CaixaItem;
import model.bean.principal.CaixaItemTipo;
import model.bean.principal.Venda;
import model.dao.principal.CaixaDAO;
import model.dao.principal.CaixaItemDAO;
import model.jtable.CaixaJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;
import view.venda.CompraView;
import view.venda.VendaView;

/**
 *
 * @author ivand
 */
public class CaixaView extends javax.swing.JInternalFrame {
    private static CaixaView singleInstance = null;
    CaixaJTableModel caixaJTableModel = new CaixaJTableModel();
    CaixaDAO caixaDAO = new CaixaDAO();
    CaixaItemDAO caixaItemDAO = new CaixaItemDAO();

    List<CaixaItem> caixaItens;
    Caixa caixa;
    
    public static CaixaView getSingleInstance(){
        if(singleInstance == null){
            singleInstance = new CaixaView();
        }
        return singleInstance;
    }
    
    /**
     * Creates new form CaixaView
     */
    private CaixaView() {
        initComponents();
        
        txtDataInicial.setVisible(false);
        txtDataFinal.setVisible(false);
        //btnFiltrar.setVisible(false);
        
        //Formatar tabela
        tblCaixaItens.setModel(caixaJTableModel);
        
        tblCaixaItens.setRowHeight(24);
        tblCaixaItens.setIntercellSpacing(new Dimension(10, 10));
        //id
        tblCaixaItens.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblCaixaItens.getColumnModel().getColumn(0).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //data
        tblCaixaItens.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblCaixaItens.getColumnModel().getColumn(1).setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        //tipo
        tblCaixaItens.getColumnModel().getColumn(2).setPreferredWidth(500);
        //observacao
        tblCaixaItens.getColumnModel().getColumn(3).setPreferredWidth(300);
        
        //meio de pagamento
        tblCaixaItens.getColumnModel().getColumn(4).setPreferredWidth(200);
        
        //crédito
        tblCaixaItens.getColumnModel().getColumn(5).setPreferredWidth(120);
        tblCaixaItens.getColumnModel().getColumn(5).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //débito
        tblCaixaItens.getColumnModel().getColumn(6).setPreferredWidth(120);
        tblCaixaItens.getColumnModel().getColumn(6).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //saldo
        tblCaixaItens.getColumnModel().getColumn(7).setPreferredWidth(120);
        tblCaixaItens.getColumnModel().getColumn(7).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        
        
        txtDataFinal.setText(DateTime.toStringDate(DateTime.getNow()));
        
        Calendar calendar = Calendar.getInstance(); //data e hora atual
        calendar.add(Calendar.DAY_OF_YEAR, -10); //adicionando dois dias
        String inicial = DateTime.toStringDate(new Timestamp(calendar.getTimeInMillis()));
        txtDataInicial.setText(inicial);
        
        caixa = caixaDAO.getLastCaixa();
        
        carregarTabela();
    }
    
    private void carregarTabela() {
        long start = System.currentTimeMillis();
        
        Timestamp dataInicial = DateTime.fromString(txtDataInicial.getText());
        Timestamp dataFinal = DateTime.fromString(txtDataFinal.getText());
        
        if(caixaDAO.getLastCaixa() != null){
            System.out.println("lastCaixa id: " + caixaDAO.getLastCaixa().getId());

            txtAbertura.setText(DateTime.toString(caixa.getCriacao()));
            txtEncerramento.setText(DateTime.toString(caixa.getEncerramento()));

            //caixaItens = caixaItemDAO.findByCriteria(dataInicial, dataFinal);
            caixaItens = caixaItemDAO.findByCaixa(caixa);

            caixaJTableModel.clear();
            caixaJTableModel.addList(caixaItens);

            long elapsed = System.currentTimeMillis() - start;
            lblMensagem.setText("Consulta realizada em " + elapsed + "ms");

            lblRegistrosExibidos.setText(String.valueOf(caixaItens.size()));
        } else {
            JOptionPane.showMessageDialog(rootPane, "Ainda não foi criado um turno de caixa. Crie um para começar a movimentar.", "Atenção", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void estornar() {
        int rowIndex = tblCaixaItens.getSelectedRow();
        CaixaItem itemEstornar = caixaJTableModel.getRow(rowIndex);
        
        if(itemEstornar.getEstorno() != null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Este item já foi estornado.", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else if(itemEstornar.getCaixaItemTipo().equals(CaixaItemTipo.ESTORNO)) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Este item já é um estorno.", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            int resposta = JOptionPane.showConfirmDialog(MAIN_VIEW, "Estornar o item selecionado?", "Atenção", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(resposta == JOptionPane.OK_OPTION) {
                caixaItemDAO.estornar(itemEstornar);
                carregarTabela();
            }
        }
    }
    
    private void abrirDocumento() {
        if(tblCaixaItens.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione um registro na tabela", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            CaixaItem caixaItem = caixaJTableModel.getRow(tblCaixaItens.getSelectedRow());

            if(caixaItem.getCaixaItemTipo().equals(CaixaItemTipo.RECEBIMENTO_DOCUMENTO)) {
                Venda documento = caixaItem.getParcela().getVenda();
                MAIN_VIEW.addView(VendaView.getInstance(documento));

            } else if(caixaItem.getCaixaItemTipo().equals(CaixaItemTipo.PAGAMENTO_DOCUMENTO)) {
                Venda documento = caixaItem.getParcela().getVenda();
                MAIN_VIEW.addView(CompraView.getInstance(documento));

            } else {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Não é possível abrir documento deste tipo registro. Apenas documentos de compra, venda e suas variantes são suportadas.", "Informação", JOptionPane.INFORMATION_MESSAGE);
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
        tblCaixaItens = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtDataInicial = new javax.swing.JFormattedTextField();
        txtDataFinal = new javax.swing.JFormattedTextField();
        btnFiltrar = new javax.swing.JButton();
        btnConsultarTurno = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtAbertura = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtEncerramento = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        lblRegistrosExibidos = new javax.swing.JLabel();
        lblMensagem = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnEncerrarTurno = new javax.swing.JButton();
        btnSuprimento = new javax.swing.JButton();
        btnSangria = new javax.swing.JButton();
        btnCriarTurno = new javax.swing.JButton();
        btnEstornar = new javax.swing.JButton();
        btnAbrirDocumento = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();

        setTitle("Caixa");
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
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                formMouseEntered(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblCaixaItens.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblCaixaItens);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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

        btnFiltrar.setText("Atualizar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnConsultarTurno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/date.png"))); // NOI18N
        btnConsultarTurno.setText("Consultar Turno");
        btnConsultarTurno.setContentAreaFilled(false);
        btnConsultarTurno.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnConsultarTurno.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnConsultarTurno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultarTurnoActionPerformed(evt);
            }
        });

        jLabel1.setText("Abertura");

        txtAbertura.setEditable(false);

        jLabel2.setText("Encerramento");

        txtEncerramento.setEditable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnConsultarTurno, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtAbertura, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEncerramento, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(58, 58, 58)
                        .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnConsultarTurno, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtAbertura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtEncerramento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(btnFiltrar))))
                .addContainerGap())
        );

        jLabel4.setText("Registros exibidos:");

        lblRegistrosExibidos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRegistrosExibidos.setText("0");

        lblMensagem.setText("...");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnEncerrarTurno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/lock.png"))); // NOI18N
        btnEncerrarTurno.setText("Encerrar Turno");
        btnEncerrarTurno.setContentAreaFilled(false);
        btnEncerrarTurno.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEncerrarTurno.setPreferredSize(new java.awt.Dimension(120, 23));
        btnEncerrarTurno.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEncerrarTurno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEncerrarTurnoActionPerformed(evt);
            }
        });

        btnSuprimento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/add.png"))); // NOI18N
        btnSuprimento.setText("Suprimento");
        btnSuprimento.setContentAreaFilled(false);
        btnSuprimento.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSuprimento.setPreferredSize(new java.awt.Dimension(120, 23));
        btnSuprimento.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSuprimento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuprimentoActionPerformed(evt);
            }
        });

        btnSangria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/delete.png"))); // NOI18N
        btnSangria.setText("Sangria");
        btnSangria.setContentAreaFilled(false);
        btnSangria.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSangria.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSangria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSangriaActionPerformed(evt);
            }
        });

        btnCriarTurno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/flag_blue.png"))); // NOI18N
        btnCriarTurno.setText("Criar Turno");
        btnCriarTurno.setContentAreaFilled(false);
        btnCriarTurno.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCriarTurno.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCriarTurno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCriarTurnoActionPerformed(evt);
            }
        });

        btnEstornar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/arrow_undo.png"))); // NOI18N
        btnEstornar.setText("Estornar");
        btnEstornar.setContentAreaFilled(false);
        btnEstornar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEstornar.setPreferredSize(new java.awt.Dimension(120, 23));
        btnEstornar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEstornar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEstornarActionPerformed(evt);
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

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCriarTurno, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnEncerrarTurno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSuprimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSangria, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnEstornar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnAbrirDocumento, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                .addGap(344, 344, 344))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2)
                    .addComponent(btnSuprimento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSangria, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                    .addComponent(btnCriarTurno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEncerrarTurno, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEstornar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAbrirDocumento, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1))
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
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblMensagem, javax.swing.GroupLayout.PREFERRED_SIZE, 477, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(57, 57, 57)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRegistrosExibidos, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRegistrosExibidos)
                    .addComponent(jLabel4)
                    .addComponent(lblMensagem))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnConsultarTurnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarTurnoActionPerformed
        if(caixaDAO.findAll().isEmpty()){
            JOptionPane.showMessageDialog(rootPane, "Não existem caixas para selecionar");
        } else {
            CaixaListaView caixaListaView = new CaixaListaView(MAIN_VIEW, true);
            caixaListaView.setLocationRelativeTo(this); //centralizar
            caixaListaView.setVisible(true);
            if(caixaListaView.getCaixa() != null){
                caixa = caixaListaView.getCaixa();
                caixaItens = caixaItemDAO.findByCaixa(caixa);
                carregarTabela();
            }
        }
    }//GEN-LAST:event_btnConsultarTurnoActionPerformed

    private void btnEncerrarTurnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEncerrarTurnoActionPerformed
        if(caixa == null || caixa.getEncerramento() != null){
            JOptionPane.showMessageDialog(rootPane, "Não há turno aberto", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            CaixaEncerrarView caixaEncerrarView = new CaixaEncerrarView(caixa, MAIN_VIEW, true);
            caixaEncerrarView.setLocationRelativeTo(this);
            caixaEncerrarView.setVisible(true);
            this.caixa = caixaEncerrarView.getCaixa();
            carregarTabela();
        }
    }//GEN-LAST:event_btnEncerrarTurnoActionPerformed

    private void btnSuprimentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuprimentoActionPerformed
        if(caixa == null || caixa.getEncerramento() != null){
            JOptionPane.showMessageDialog(rootPane, "Não há turno aberto", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            CaixaSuprimentoView caixaSuprimentoView = new CaixaSuprimentoView(MAIN_VIEW, true, caixa);
            caixaSuprimentoView.setLocationRelativeTo(this);
            caixaSuprimentoView.setVisible(true);
            caixa = caixaSuprimentoView.getCaixa();
            caixaItens = caixaItemDAO.findByCaixa(caixa);
            carregarTabela();
        }
    }//GEN-LAST:event_btnSuprimentoActionPerformed

    private void btnSangriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSangriaActionPerformed
        if(caixa == null || caixa.getEncerramento() != null){
            JOptionPane.showMessageDialog(rootPane, "Não há turno aberto", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            CaixaSangriaView caixaSangriaView = new CaixaSangriaView(MAIN_VIEW, true, caixa);
            caixaSangriaView.setLocationRelativeTo(this);
            caixaSangriaView.setVisible(true);
            caixa = caixaSangriaView.getCaixa();
            caixaItens = caixaItemDAO.findByCaixa(caixa);
            carregarTabela();
        }
    }//GEN-LAST:event_btnSangriaActionPerformed

    private void btnCriarTurnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCriarTurnoActionPerformed
        if(caixa != null && caixaDAO.getLastCaixa().getEncerramento() == null){
            JOptionPane.showMessageDialog(rootPane, "Já existe um turno em aberto. Encerre-o antes de criar outro", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            CaixaCriarTurnoView caixaCriarTurnoView = new CaixaCriarTurnoView(MAIN_VIEW, true);
            caixaCriarTurnoView.setLocationRelativeTo(this);
            caixaCriarTurnoView.setVisible(true);
            caixa = caixaCriarTurnoView.getCaixa();
            caixaItens = caixaItemDAO.findByCaixa(caixa);
            carregarTabela();
        }
    }//GEN-LAST:event_btnCriarTurnoActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        singleInstance = null;
    }//GEN-LAST:event_formInternalFrameClosed

    private void btnEstornarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEstornarActionPerformed
        estornar();
    }//GEN-LAST:event_btnEstornarActionPerformed

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
        //System.out.println("activated...");
    }//GEN-LAST:event_formInternalFrameActivated

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        //System.out.println("shown...");
    }//GEN-LAST:event_formComponentShown

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        //System.out.println("focus gained...");
    }//GEN-LAST:event_formFocusGained

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        //System.out.println("opened...");
    }//GEN-LAST:event_formInternalFrameOpened

    private void formMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseEntered
        //System.out.println("mouse entered...");
    }//GEN-LAST:event_formMouseEntered

    private void btnAbrirDocumentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirDocumentoActionPerformed
        abrirDocumento();
    }//GEN-LAST:event_btnAbrirDocumentoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrirDocumento;
    private javax.swing.JButton btnConsultarTurno;
    private javax.swing.JButton btnCriarTurno;
    private javax.swing.JButton btnEncerrarTurno;
    private javax.swing.JButton btnEstornar;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnSangria;
    private javax.swing.JButton btnSuprimento;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblMensagem;
    private javax.swing.JLabel lblRegistrosExibidos;
    private javax.swing.JTable tblCaixaItens;
    private javax.swing.JTextField txtAbertura;
    private javax.swing.JFormattedTextField txtDataFinal;
    private javax.swing.JFormattedTextField txtDataInicial;
    private javax.swing.JTextField txtEncerramento;
    // End of variables declaration//GEN-END:variables
}
