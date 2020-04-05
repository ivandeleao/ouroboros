/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.financeiro.caixa;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.financeiro.Caixa;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.principal.financeiro.CaixaItemTipo;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.principal.financeiro.CaixaDAO;
import model.mysql.dao.principal.financeiro.CaixaItemDAO;
import model.mysql.dao.principal.financeiro.CaixaItemTipoDAO;
import model.jtable.financeiro.CaixaJTableModel;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.financeiro.Conta;
import model.mysql.dao.principal.financeiro.ContaDAO;
import model.nosql.ContaTipoEnum;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import printing.financeiro.CaixaPorTurnoReport;
import util.DateTime;
import util.Decimal;
import view.documentoEntrada.DocumentoEntradaView;
import view.documentoSaida.item.VendaView;
import view.financeiro.conta.ContaListaView;
import view.financeiro.conta.ContaTransferirView;

/**
 *
 * @author ivand
 */
public class CaixaView extends javax.swing.JInternalFrame {
    private static CaixaView singleInstance = null;
    CaixaJTableModel caixaJTableModel = new CaixaJTableModel();
    
    ContaDAO contaDAO = new ContaDAO();
    Conta conta;
    
    CaixaDAO caixaDAO = new CaixaDAO();
    CaixaItemDAO caixaItemDAO = new CaixaItemDAO();

    List<CaixaItem> caixaItens;
    Caixa caixa;
    
    LocalDateTime abertura;
    LocalDateTime encerramento;
    
    BigDecimal totalCredito = BigDecimal.ZERO;
    BigDecimal totalDebito = BigDecimal.ZERO;
    BigDecimal saldo = BigDecimal.ZERO;
    
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
        
        //conta = contaDAO.findById(1); //2020-02-20 - refatorar para múltiplos caixas (PDVs)
        
        //btnResumoPorMeioDePagamento.setVisible(false);
        
        carregarContas();
        
        cboConta.setSelectedItem(Ouroboros.FINANCEIRO_CAIXA_PRINCIPAL);
        
        formatarTabela();
        
        carregarTipo();
        
        //caixa = caixaDAO.getLastCaixa(conta);
        
        //carregarTabela();
    }
    
    private void carregarContas() {
        List<Conta> contas = contaDAO.findByTipo(ContaTipoEnum.CAIXA);

        if (contas.isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Ainda não existem contas.", "Atenção", JOptionPane.INFORMATION_MESSAGE);

        } else {
            Conta contaSelecionada = null;
            if (cboConta.getSelectedItem() != null) {
                contaSelecionada = (Conta) cboConta.getSelectedItem();
            }

            cboConta.removeAllItems();
            for (Conta c : contas) {
                cboConta.addItem(c);
            }

            if (contaSelecionada != null) {
                cboConta.setSelectedItem(contaSelecionada);
            }
        }
    }
    
    private void clickConta() {
        conta = (Conta) cboConta.getSelectedItem();

        if (conta != null) {
            if (caixa == null || !caixa.getConta().equals(conta)) {
                caixa = caixaDAO.getLastCaixa(conta);
            }
        }

        carregarTabela();
    }
    
    private void consultarTurno() {
        if(caixaDAO.findAll(conta).isEmpty()){
            JOptionPane.showMessageDialog(rootPane, "Não existem caixas para selecionar");
        } else {
            CaixaListaView caixaListaView = new CaixaListaView(conta);
            
            if(caixaListaView.getCaixa() != null){
                caixa = caixaListaView.getCaixa();
                System.out.println("caixaid: " + caixa.getId());
                //caixaItens = caixaItemDAO.findByCaixa(caixa, (CaixaItemTipo) cboTipo.getSelectedItem());
                carregarTabela();
            }
        }
    }
    
    private void formatarTabela() {
        tblItens.setModel(caixaJTableModel);

        tblItens.setRowHeight(30);
        tblItens.setIntercellSpacing(new Dimension(10, 10));

        tblItens.getColumn("Id").setPreferredWidth(100);
        tblItens.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblItens.getColumn("Data").setPreferredWidth(180);
        tblItens.getColumn("Data").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblItens.getColumn("Descrição").setPreferredWidth(480);

        tblItens.getColumn("Observação").setPreferredWidth(480);

        tblItens.getColumn("MP").setPreferredWidth(80);
        tblItens.getColumn("MP").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblItens.getColumn("Crédito").setPreferredWidth(120);
        tblItens.getColumn("Crédito").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblItens.getColumn("Débito").setPreferredWidth(120);
        tblItens.getColumn("Débito").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblItens.getColumn("Saldo").setPreferredWidth(120);
        tblItens.getColumn("Saldo").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
    }
    
    private void carregarTabela() {
        long start = System.currentTimeMillis();
        
        CaixaItemTipo caixaItemTipo = (CaixaItemTipo) cboTipo.getSelectedItem();
        
        
        if(caixa == null){
            JOptionPane.showMessageDialog(rootPane, "Ainda não foi criado um turno neste caixa. Crie um para começar a movimentar.", "Atenção", JOptionPane.INFORMATION_MESSAGE);
            
            caixaItens = new ArrayList<>();
            
            txtAbertura.setText("");
            txtEncerramento.setText("");
            
        } else {
            
            abertura = caixa.getCriacao();
            encerramento = caixa.getEncerramento();
            
            txtAbertura.setText(DateTime.toString(abertura));
            txtEncerramento.setText(DateTime.toString(encerramento));

            caixaItens = caixaItemDAO.findByCaixa(caixa, caixaItemTipo, null);

        }
        
        caixaJTableModel.clear();
        caixaJTableModel.addList(caixaItens);

        long elapsed = System.currentTimeMillis() - start;
        lblMensagem.setText("Consulta realizada em " + elapsed + "ms");

        lblRegistrosExibidos.setText(String.valueOf(caixaItens.size()));
        
        exibirTotais();
    }
    
    private void carregarTipo() {
        List<CaixaItemTipo> tipos = new CaixaItemTipoDAO().findAll();
        CaixaItemTipo noFilter = new CaixaItemTipo();
        noFilter.setId(0);
        noFilter.setNome("Todos");
        cboTipo.addItem(noFilter);
        for (CaixaItemTipo t : tipos) {
            cboTipo.addItem(t);
        }
    }
    
    private void exibirTotais() {
        
                
        if(!caixaItens.isEmpty()) {
            totalCredito = caixaItens.stream().map(CaixaItem::getCredito).reduce(BigDecimal::add).get();
            totalDebito = caixaItens.stream().map(CaixaItem::getDebito).reduce(BigDecimal::add).get();
            saldo = totalCredito.subtract(totalDebito);
        }
        
        txtTotalCredito.setText(Decimal.toString(totalCredito));
        txtTotalDebito.setText(Decimal.toString(totalDebito));
    }
    
    private void criarTurno() {
        if(caixa != null && caixaDAO.getLastCaixa(conta).getEncerramento() == null){
            JOptionPane.showMessageDialog(rootPane, "Já existe um turno em aberto. Encerre-o antes de criar outro", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            CaixaCriarTurnoView caixaCriarTurnoView = new CaixaCriarTurnoView(conta);
            caixaCriarTurnoView.setLocationRelativeTo(this);
            caixaCriarTurnoView.setVisible(true);
            if(caixaCriarTurnoView.getCaixa() != null) {
                caixa = caixaCriarTurnoView.getCaixa();
                carregarTabela();
            }
        }
    }
    
    private void encerrarTurno() {
        if(caixa == null || caixa.getEncerramento() != null){
            JOptionPane.showMessageDialog(rootPane, "Não há turno aberto", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            CaixaEncerrarView caixaEncerrarView = new CaixaEncerrarView(caixa, MAIN_VIEW, true);
            caixaEncerrarView.setLocationRelativeTo(this);
            caixaEncerrarView.setVisible(true);
            carregarTabela();
        }
    }
    
    private void suprimento() {
        if(caixa == null || caixa.getEncerramento() != null){
            JOptionPane.showMessageDialog(rootPane, "Não há turno aberto", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            new CaixaSuprimentoView(caixa);
            carregarTabela();
        }
    }
    
    private void sangria() {
        if(caixa == null || caixa.getEncerramento() != null){
            JOptionPane.showMessageDialog(rootPane, "Não há turno aberto", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            CaixaSangriaView caixaSangriaView = new CaixaSangriaView(MAIN_VIEW, true, caixa);
            caixaSangriaView.setLocationRelativeTo(this);
            caixaSangriaView.setVisible(true);
            carregarTabela();
        }
    }
    
    private void transferir() {
        if(caixa == null || caixa.getEncerramento() != null){
            JOptionPane.showMessageDialog(rootPane, "Não há turno aberto", "Atenção", JOptionPane.WARNING_MESSAGE);
            
        } else {
            new ContaTransferirView(conta);
            carregarTabela();
        }
    }
    
    private void estornar() {
        if(conta.getLastCaixa() == null || conta.getLastCaixa().getEncerramento() != null){
            JOptionPane.showMessageDialog(rootPane, "Não há turno aberto", "Atenção", JOptionPane.WARNING_MESSAGE);
            
        } else {
            int rowIndex = tblItens.getSelectedRow();
            if(rowIndex < 0) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione um registro", "Atenção", JOptionPane.WARNING_MESSAGE);

            } else {
                System.out.println("rowIndex " + rowIndex);
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
        }
    }
    
    private void abrirDocumento() {
        if(tblItens.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione um registro na tabela", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            CaixaItem caixaItem = caixaJTableModel.getRow(tblItens.getSelectedRow());

            if(caixaItem.getCaixaItemTipo().equals(CaixaItemTipo.DOCUMENTO)
                    || caixaItem.getCaixaItemTipo().equals(CaixaItemTipo.TROCO)) {
                Venda documento = caixaItem.getParcela().getVenda();
                if(documento.getTipoOperacao().equals(TipoOperacao.ENTRADA)) {
                    MAIN_VIEW.addView(DocumentoEntradaView.getInstance(documento));
                } else {
                    MAIN_VIEW.addView(VendaView.getInstance(documento));
                }
                
            } else {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Não é possível abrir documento deste tipo registro. Apenas documentos de compra, venda e suas variantes são suportadas.", "Informação", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        
    }
    
    private void imprimir() {
        if (caixa == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione ou abra um novo turno!", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {
            CaixaPorTurnoReport.gerar(caixaItens, abertura, encerramento, totalCredito, totalDebito, saldo);
        }
    }
    
    private void contas() {
        new ContaListaView(ContaTipoEnum.CAIXA);
        carregarContas();
    }
    
    private void resumoPorMeioDePagamento() {
        new CaixaResumoPorMeioDePagamentoView(caixaItens);
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
        tblItens = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        btnFiltrar = new javax.swing.JButton();
        btnConsultarTurno = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtAbertura = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtEncerramento = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cboTipo = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        cboConta = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        btnResumoPorMeioDePagamento = new javax.swing.JButton();
        btnImprimir = new javax.swing.JButton();
        btnEstornar = new javax.swing.JButton();
        btnSangria = new javax.swing.JButton();
        btnSuprimento = new javax.swing.JButton();
        btnAbrirDocumento = new javax.swing.JButton();
        btnEncerrarTurno = new javax.swing.JButton();
        btnCriarTurno = new javax.swing.JButton();
        btnSangria1 = new javax.swing.JButton();
        btnContas = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        txtSaldoGeral = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtTotalDebito = new javax.swing.JTextField();
        txtTotalCredito = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        lblMensagem = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblRegistrosExibidos = new javax.swing.JLabel();

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

        tblItens.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblItens.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblItens);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnFiltrar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnFiltrar.setText("Atualizar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnConsultarTurno.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnConsultarTurno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-calendar-with-clock-20.png"))); // NOI18N
        btnConsultarTurno.setToolTipText("Consultar Turno");
        btnConsultarTurno.setContentAreaFilled(false);
        btnConsultarTurno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultarTurnoActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Abertura");

        txtAbertura.setEditable(false);
        txtAbertura.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Encerramento");

        txtEncerramento.setEditable(false);
        txtEncerramento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Tipo");

        cboTipo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setForeground(java.awt.Color.blue);
        jLabel6.setText("Caixa");
        jLabel6.setToolTipText("Duplo clique para atualizar");

        cboConta.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cboConta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboContaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(cboConta, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnConsultarTurno)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtAbertura, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtEncerramento, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(cboTipo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(cboConta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnConsultarTurno))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAbertura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(txtEncerramento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3)
                        .addComponent(cboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(btnFiltrar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnResumoPorMeioDePagamento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-ledger-20.png"))); // NOI18N
        btnResumoPorMeioDePagamento.setText("Resumo por MP");
        btnResumoPorMeioDePagamento.setContentAreaFilled(false);
        btnResumoPorMeioDePagamento.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnResumoPorMeioDePagamento.setPreferredSize(new java.awt.Dimension(120, 23));
        btnResumoPorMeioDePagamento.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnResumoPorMeioDePagamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResumoPorMeioDePagamentoActionPerformed(evt);
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

        btnEstornar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-undo-20.png"))); // NOI18N
        btnEstornar.setText("Estornar");
        btnEstornar.setContentAreaFilled(false);
        btnEstornar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEstornar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEstornar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEstornarActionPerformed(evt);
            }
        });

        btnSangria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-do-not-disturb-20.png"))); // NOI18N
        btnSangria.setText("Sangria");
        btnSangria.setContentAreaFilled(false);
        btnSangria.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSangria.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSangria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSangriaActionPerformed(evt);
            }
        });

        btnSuprimento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-add-20.png"))); // NOI18N
        btnSuprimento.setText("Suprimento");
        btnSuprimento.setContentAreaFilled(false);
        btnSuprimento.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSuprimento.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSuprimento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuprimentoActionPerformed(evt);
            }
        });

        btnAbrirDocumento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-external-link-20.png"))); // NOI18N
        btnAbrirDocumento.setText("Documento");
        btnAbrirDocumento.setContentAreaFilled(false);
        btnAbrirDocumento.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAbrirDocumento.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAbrirDocumento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirDocumentoActionPerformed(evt);
            }
        });

        btnEncerrarTurno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-finish-flag-20.png"))); // NOI18N
        btnEncerrarTurno.setText("Encerrar Turno");
        btnEncerrarTurno.setContentAreaFilled(false);
        btnEncerrarTurno.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEncerrarTurno.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEncerrarTurno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEncerrarTurnoActionPerformed(evt);
            }
        });

        btnCriarTurno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-time-card-20.png"))); // NOI18N
        btnCriarTurno.setText("Criar Turno");
        btnCriarTurno.setContentAreaFilled(false);
        btnCriarTurno.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCriarTurno.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCriarTurno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCriarTurnoActionPerformed(evt);
            }
        });

        btnSangria1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-money-transfer-20.png"))); // NOI18N
        btnSangria1.setText("Transferir");
        btnSangria1.setContentAreaFilled(false);
        btnSangria1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSangria1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSangria1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSangria1ActionPerformed(evt);
            }
        });

        btnContas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-money-bag-20.png"))); // NOI18N
        btnContas.setText("Contas");
        btnContas.setContentAreaFilled(false);
        btnContas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnContas.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnContas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCriarTurno)
                .addGap(18, 18, 18)
                .addComponent(btnEncerrarTurno)
                .addGap(18, 18, 18)
                .addComponent(btnSuprimento)
                .addGap(18, 18, 18)
                .addComponent(btnSangria)
                .addGap(18, 18, 18)
                .addComponent(btnSangria1)
                .addGap(18, 18, 18)
                .addComponent(btnEstornar)
                .addGap(18, 18, 18)
                .addComponent(btnAbrirDocumento)
                .addGap(18, 18, 18)
                .addComponent(btnImprimir)
                .addGap(18, 18, 18)
                .addComponent(btnContas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnResumoPorMeioDePagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnContas)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnResumoPorMeioDePagamento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnImprimir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSuprimento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSangria, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnEstornar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAbrirDocumento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnEncerrarTurno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCriarTurno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSangria1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtSaldoGeral.setEditable(false);
        txtSaldoGeral.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtSaldoGeral.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Saldo");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("Total Débito");

        txtTotalDebito.setEditable(false);
        txtTotalDebito.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalDebito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtTotalCredito.setEditable(false);
        txtTotalCredito.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalCredito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setText("Total Crédito");

        lblMensagem.setText("...");

        jLabel4.setText("Registros exibidos:");

        lblRegistrosExibidos.setText("0");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblMensagem, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(lblRegistrosExibidos, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(txtTotalCredito, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(txtTotalDebito, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(txtSaldoGeral, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtSaldoGeral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtTotalDebito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(lblMensagem)
                    .addComponent(jLabel4)
                    .addComponent(lblRegistrosExibidos))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnConsultarTurnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarTurnoActionPerformed
        consultarTurno();
    }//GEN-LAST:event_btnConsultarTurnoActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        singleInstance = null;
    }//GEN-LAST:event_formInternalFrameClosed

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

    private void btnResumoPorMeioDePagamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResumoPorMeioDePagamentoActionPerformed
        resumoPorMeioDePagamento();
    }//GEN-LAST:event_btnResumoPorMeioDePagamentoActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        imprimir();
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void btnEstornarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEstornarActionPerformed
        estornar();
    }//GEN-LAST:event_btnEstornarActionPerformed

    private void btnSangriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSangriaActionPerformed
        sangria();
    }//GEN-LAST:event_btnSangriaActionPerformed

    private void btnSuprimentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuprimentoActionPerformed
        suprimento();
    }//GEN-LAST:event_btnSuprimentoActionPerformed

    private void btnAbrirDocumentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirDocumentoActionPerformed
        abrirDocumento();
    }//GEN-LAST:event_btnAbrirDocumentoActionPerformed

    private void btnEncerrarTurnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEncerrarTurnoActionPerformed
        encerrarTurno();
    }//GEN-LAST:event_btnEncerrarTurnoActionPerformed

    private void btnCriarTurnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCriarTurnoActionPerformed
        criarTurno();
    }//GEN-LAST:event_btnCriarTurnoActionPerformed

    private void btnSangria1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSangria1ActionPerformed
        transferir();
    }//GEN-LAST:event_btnSangria1ActionPerformed

    private void cboContaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboContaActionPerformed
        clickConta();
    }//GEN-LAST:event_cboContaActionPerformed

    private void btnContasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContasActionPerformed
        contas();
    }//GEN-LAST:event_btnContasActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrirDocumento;
    private javax.swing.JButton btnConsultarTurno;
    private javax.swing.JButton btnContas;
    private javax.swing.JButton btnCriarTurno;
    private javax.swing.JButton btnEncerrarTurno;
    private javax.swing.JButton btnEstornar;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnResumoPorMeioDePagamento;
    private javax.swing.JButton btnSangria;
    private javax.swing.JButton btnSangria1;
    private javax.swing.JButton btnSuprimento;
    private javax.swing.JComboBox<Object> cboConta;
    private javax.swing.JComboBox<Object> cboTipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMensagem;
    private javax.swing.JLabel lblRegistrosExibidos;
    private javax.swing.JTable tblItens;
    private javax.swing.JTextField txtAbertura;
    private javax.swing.JTextField txtEncerramento;
    private javax.swing.JTextField txtSaldoGeral;
    private javax.swing.JTextField txtTotalCredito;
    private javax.swing.JTextField txtTotalDebito;
    // End of variables declaration//GEN-END:variables
}
