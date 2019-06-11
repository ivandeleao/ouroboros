/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.financeiro;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.financeiro.Caixa;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.principal.financeiro.CaixaItemTipo;
import model.mysql.bean.principal.catalogo.Categoria;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.principal.CaixaDAO;
import model.mysql.dao.principal.CaixaItemDAO;
import model.mysql.dao.principal.CaixaItemTipoDAO;
import model.mysql.dao.principal.CategoriaDAO;
import model.jtable.financeiro.CaixaJTableModel;
import model.mysql.bean.principal.documento.TipoOperacao;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;
import util.Decimal;
import view.documentoEntrada.DocumentoEntradaView;
import view.documentoSaida.VendaView;

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
        
        //btnResumoPorMeioDePagamento.setVisible(false);
        
        formatarTabela();
        
        carregarTipo();
        
        caixa = caixaDAO.getLastCaixa();
        
        carregarTabela();
    }
    
    private void formatarTabela() {
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
    }
    
    private void carregarTabela() {
        long start = System.currentTimeMillis();
        
        CaixaItemTipo caixaItemTipo = (CaixaItemTipo) cboTipo.getSelectedItem();
        
        if(caixaDAO.getLastCaixa() == null){
            JOptionPane.showMessageDialog(rootPane, "Ainda não foi criado um turno de caixa. Crie um para começar a movimentar.", "Atenção", JOptionPane.INFORMATION_MESSAGE);
            
        } else {
            System.out.println("lastCaixa id: " + caixaDAO.getLastCaixa().getId());

            txtAbertura.setText(DateTime.toString(caixa.getCriacao()));
            txtEncerramento.setText(DateTime.toString(caixa.getEncerramento()));

            //caixaItens = caixaItemDAO.findByCriteria(dataInicial, dataFinal);
            caixaItens = caixaItemDAO.findByCaixa(caixa, caixaItemTipo, null);

            
            
            caixaJTableModel.clear();
            caixaJTableModel.addList(caixaItens);

            long elapsed = System.currentTimeMillis() - start;
            lblMensagem.setText("Consulta realizada em " + elapsed + "ms");

            lblRegistrosExibidos.setText(String.valueOf(caixaItens.size()));
            
            exibirTotais();
        }
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
        BigDecimal totalCredito = BigDecimal.ZERO;
        BigDecimal totalDebito = BigDecimal.ZERO;
                
        if(!caixaItens.isEmpty()) {
            totalCredito = caixaItens.stream().map(CaixaItem::getCredito).reduce(BigDecimal::add).get();
            totalDebito = caixaItens.stream().map(CaixaItem::getDebito).reduce(BigDecimal::add).get();
        }
        
        txtTotalCredito.setText(Decimal.toString(totalCredito));
        txtTotalDebito.setText(Decimal.toString(totalDebito));
    }
    
    private void estornar() {
        int rowIndex = tblCaixaItens.getSelectedRow();
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
    
    private void abrirDocumento() {
        if(tblCaixaItens.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione um registro na tabela", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            CaixaItem caixaItem = caixaJTableModel.getRow(tblCaixaItens.getSelectedRow());

            if(caixaItem.getCaixaItemTipo().equals(CaixaItemTipo.DOCUMENTO)
                    || caixaItem.getCaixaItemTipo().equals(CaixaItemTipo.TROCO)) {
                Venda documento = caixaItem.getParcela().getVenda();
                if(documento.getTipoOperacao().equals(TipoOperacao.ENTRADA)) {
                    MAIN_VIEW.addView(DocumentoEntradaView.getInstance(documento));
                } else {
                    MAIN_VIEW.addView(VendaView.getInstance(documento));
                }
            /*
            } else if(caixaItem.getCaixaItemTipo().equals(CaixaItemTipo.PAGAMENTO_DOCUMENTO)) {
                Venda documento = caixaItem.getParcela().getVenda();
                MAIN_VIEW.addView(DocumentoEntradaView.getInstance(documento));
            */
            } else {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Não é possível abrir documento deste tipo registro. Apenas documentos de compra, venda e suas variantes são suportadas.", "Informação", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        
    }
    
    private void resumoPorMeioDePagamento() {
        for(CaixaItem ci : caixaItens) {
            System.out.println("ci: " + ci.getId() + " credito: " + ci.getCredito());
        }
        
        
        CaixaResumoPorMeioDePagamentoView caixaResumoMP = new CaixaResumoPorMeioDePagamentoView(caixaItens);
        
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
        btnFiltrar = new javax.swing.JButton();
        btnConsultarTurno = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtAbertura = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtEncerramento = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cboTipo = new javax.swing.JComboBox<>();
        txtTotalCredito = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtTotalDebito = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
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
        btnResumoPorMeioDePagamento = new javax.swing.JButton();

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

        jLabel3.setText("Tipo");

        txtTotalCredito.setEditable(false);
        txtTotalCredito.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTotalCredito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalCredito.setText("0,00");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Total Crédito");

        txtTotalDebito.setEditable(false);
        txtTotalDebito.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTotalDebito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalDebito.setText("0,00");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Total Débito");

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
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(cboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 208, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTotalDebito, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                    .addComponent(txtTotalCredito))
                .addContainerGap())
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
                            .addComponent(jLabel3)
                            .addComponent(cboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTotalCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtEncerramento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2)
                                    .addComponent(btnFiltrar)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtTotalDebito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6))
                                .addGap(0, 0, Short.MAX_VALUE)))))
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

        btnResumoPorMeioDePagamento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/application_view_detail.png"))); // NOI18N
        btnResumoPorMeioDePagamento.setText("Resumo por Meio de Pagamento");
        btnResumoPorMeioDePagamento.setContentAreaFilled(false);
        btnResumoPorMeioDePagamento.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnResumoPorMeioDePagamento.setPreferredSize(new java.awt.Dimension(120, 23));
        btnResumoPorMeioDePagamento.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnResumoPorMeioDePagamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResumoPorMeioDePagamentoActionPerformed(evt);
            }
        });

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
                .addComponent(btnAbrirDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(194, 194, 194)
                .addComponent(btnResumoPorMeioDePagamento, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                .addContainerGap())
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
                    .addComponent(jSeparator1)
                    .addComponent(btnResumoPorMeioDePagamento, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblMensagem, javax.swing.GroupLayout.PREFERRED_SIZE, 477, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRegistrosExibidos, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
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
        if(caixaDAO.findAll().isEmpty()){
            JOptionPane.showMessageDialog(rootPane, "Não existem caixas para selecionar");
        } else {
            CaixaListaView caixaListaView = new CaixaListaView(MAIN_VIEW, true);
            caixaListaView.setLocationRelativeTo(this); //centralizar
            caixaListaView.setVisible(true);
            if(caixaListaView.getCaixa() != null){
                caixa = caixaListaView.getCaixa();
                //caixaItens = caixaItemDAO.findByCaixa(caixa, (CaixaItemTipo) cboTipo.getSelectedItem());
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
            //this.caixa = caixaEncerrarView.getCaixa();
            carregarTabela();
        }
    }//GEN-LAST:event_btnEncerrarTurnoActionPerformed

    private void btnSuprimentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuprimentoActionPerformed
        if(caixa == null || caixa.getEncerramento() != null){
            JOptionPane.showMessageDialog(rootPane, "Não há turno aberto", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            CaixaSuprimentoView caixaSuprimentoView = new CaixaSuprimentoView(caixa);
            //2019-03-23
            //caixa = caixaSuprimentoView.getCaixa();
            //caixaItens = caixaItemDAO.findByCaixa(caixa, (CaixaItemTipo) cboTipo.getSelectedItem(), null);
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
            //caixa = caixaSangriaView.getCaixa();
            //caixaItens = caixaItemDAO.findByCaixa(caixa, (CaixaItemTipo) cboTipo.getSelectedItem());
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
            if(caixaCriarTurnoView.getCaixa() != null) {
                caixa = caixaCriarTurnoView.getCaixa();
                carregarTabela();
            }
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

    private void btnResumoPorMeioDePagamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResumoPorMeioDePagamentoActionPerformed
        resumoPorMeioDePagamento();
    }//GEN-LAST:event_btnResumoPorMeioDePagamentoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrirDocumento;
    private javax.swing.JButton btnConsultarTurno;
    private javax.swing.JButton btnCriarTurno;
    private javax.swing.JButton btnEncerrarTurno;
    private javax.swing.JButton btnEstornar;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnResumoPorMeioDePagamento;
    private javax.swing.JButton btnSangria;
    private javax.swing.JButton btnSuprimento;
    private javax.swing.JComboBox<Object> cboTipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblMensagem;
    private javax.swing.JLabel lblRegistrosExibidos;
    private javax.swing.JTable tblCaixaItens;
    private javax.swing.JTextField txtAbertura;
    private javax.swing.JTextField txtEncerramento;
    private javax.swing.JTextField txtTotalCredito;
    private javax.swing.JTextField txtTotalDebito;
    // End of variables declaration//GEN-END:variables
}
