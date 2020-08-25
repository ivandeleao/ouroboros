/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.funcionario;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import model.jtable.funcionario.FuncionarioHistoricoPorDocumentoTableModel;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.principal.VendaDAO;
import model.mysql.bean.principal.Funcionario;
import model.mysql.bean.principal.documento.VendaStatus;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;
import util.Decimal;
import util.JSwing;
import util.jTableFormat.FinanceiroStatusRenderer;
import view.documentoSaida.item.VendaView;

/**
 *
 * @author ivand
 */
public class FuncionarioHistoricoPorDocumentoView extends javax.swing.JInternalFrame {

    private static final List<FuncionarioHistoricoPorDocumentoView> funcionarioHistoricoPorDocumentoViews = new ArrayList<>(); //instâncias

    FuncionarioHistoricoPorDocumentoTableModel funcionarioHistoricoPorDocumentoJTableModel = new FuncionarioHistoricoPorDocumentoTableModel();
    VendaDAO vendaDAO = new VendaDAO();

    List<Venda> documentos = new ArrayList<>();

    Funcionario funcionario;

    public static FuncionarioHistoricoPorDocumentoView getInstance(Funcionario funcionario) {
        for (FuncionarioHistoricoPorDocumentoView funcionarioHistoricoPorDocumentoView : funcionarioHistoricoPorDocumentoViews) {
            if (funcionarioHistoricoPorDocumentoView.funcionario == funcionario) {
                return funcionarioHistoricoPorDocumentoView;
            }
        }
        funcionarioHistoricoPorDocumentoViews.add(new FuncionarioHistoricoPorDocumentoView(funcionario));
        return funcionarioHistoricoPorDocumentoViews.get(funcionarioHistoricoPorDocumentoViews.size() - 1);
    }

    /**
     * Creates new form VendaListaView
     */
    private FuncionarioHistoricoPorDocumentoView(Funcionario funcionario) {
        initComponents();
        JSwing.startComponentsBehavior(this);

        this.funcionario = funcionario;

        txtDataFinal.setText(DateTime.toString(LocalDate.now()));
        txtDataInicial.setText(DateTime.toString(LocalDate.now().minusDays(10)));

        carregarStatus();

        formatarTabela();
        carregarTabela();
    }

    private void carregarStatus() {
        cboStatus.addItem("Todos");
        cboStatus.addItem(VendaStatus.AGUARDANDO);
        cboStatus.addItem(VendaStatus.ANDAMENTO);
        cboStatus.addItem(VendaStatus.PREPARAÇÃO_CONCLUÍDA);
        cboStatus.addItem(VendaStatus.ENTREGA_CONCLUÍDA);
    }

    private void formatarTabela() {
        tblDocumentos.setModel(funcionarioHistoricoPorDocumentoJTableModel);

        tblDocumentos.setRowHeight(30);
        tblDocumentos.setIntercellSpacing(new Dimension(10, 10));

        tblDocumentos.getColumn("Status").setPreferredWidth(60);
        FinanceiroStatusRenderer statusRenderer = new FinanceiroStatusRenderer();
        statusRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblDocumentos.getColumn("Status").setCellRenderer(statusRenderer);
        
        tblDocumentos.getColumn("Data").setPreferredWidth(60);
        tblDocumentos.getColumn("Data").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblDocumentos.getColumn("Documento").setPreferredWidth(60);
        tblDocumentos.getColumn("Documento").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblDocumentos.getColumn("Cliente").setPreferredWidth(330);

        tblDocumentos.getColumn("Produtos").setPreferredWidth(50);
        tblDocumentos.getColumn("Produtos").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblDocumentos.getColumn("Serviços").setPreferredWidth(50);
        tblDocumentos.getColumn("Serviços").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblDocumentos.getColumn("Com.Prod").setPreferredWidth(50);
        tblDocumentos.getColumn("Com.Prod").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblDocumentos.getColumn("Com.Serv").setPreferredWidth(50);
        tblDocumentos.getColumn("Com.Serv").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblDocumentos.getColumn("Total Com").setPreferredWidth(50);
        tblDocumentos.getColumn("Total Com").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblDocumentos.getColumn("Pago").setPreferredWidth(50);
        tblDocumentos.getColumn("Pago").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblDocumentos.getColumn("Receber").setPreferredWidth(50);
        tblDocumentos.getColumn("Receber").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

    }

    private void carregarTabela() {
        long start = System.currentTimeMillis();

        LocalDateTime dataInicial = DateTime.fromStringLDT(txtDataInicial.getText());
        LocalDateTime dataFinal = DateTime.fromStringLDT(txtDataFinal.getText() + " 23:59:59");

        VendaStatus vendaStatus = cboStatus.getSelectedIndex() == 0 ? null : (VendaStatus) cboStatus.getSelectedItem();

        documentos = vendaDAO.findByFuncionario(funcionario, dataInicial, dataFinal, vendaStatus);

        funcionarioHistoricoPorDocumentoJTableModel.clear();
        funcionarioHistoricoPorDocumentoJTableModel.addList(documentos);

        long elapsed = System.currentTimeMillis() - start;
        lblMensagem.setText("Consulta realizada em " + elapsed + "ms");

        lblRegistrosExibidos.setText(String.valueOf(documentos.size()));
        
        exibirTotais();
    }
    
    private void exibirTotais() {
        BigDecimal totalProdutos = BigDecimal.ZERO;
        BigDecimal totalServicos = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;

        BigDecimal totalComissaoProduto = BigDecimal.ZERO;
        BigDecimal totalComissaoServico = BigDecimal.ZERO;
        BigDecimal totalComissao = BigDecimal.ZERO;
        BigDecimal totalComissaoPago = BigDecimal.ZERO;
        BigDecimal totalComissaoReceber = BigDecimal.ZERO;

        for (Venda venda : documentos) {
            totalProdutos = totalProdutos.add(venda.getTotalProdutos());
            totalServicos = totalServicos.add(venda.getTotalServicos());
            total = total.add(venda.getTotal());

            totalComissaoProduto = totalComissaoProduto.add(venda.getTotalComissaoDocumentoProduto());
            totalComissaoServico = totalComissaoServico.add(venda.getTotalComissaoDocumentoServico());
            totalComissao = totalComissao.add(venda.getTotalComissaoDocumento());
            totalComissaoPago = totalComissaoPago.add(venda.getTotalComissaoDocumentoPago());
            totalComissaoReceber = totalComissaoReceber.add(venda.getTotalComissaoDocumentoReceber());
        }

        txtTotalProdutos.setText(Decimal.toString(totalProdutos));
        txtTotalServicos.setText(Decimal.toString(totalServicos));
        txtTotal.setText(Decimal.toString(total));

        txtTotalComissaoProduto.setText(Decimal.toString(totalComissaoProduto));
        txtTotalComissaoServico.setText(Decimal.toString(totalComissaoServico));
        txtTotalComissao.setText(Decimal.toString(totalComissao));
        txtTotalComissaoPago.setText(Decimal.toString(totalComissaoPago));
        txtTotalComissaoReceber.setText(Decimal.toString(totalComissaoReceber));
    }

    private void pagar() {
        List<Venda> documentosPagar = new ArrayList<>();
        
        documentosPagar = funcionarioHistoricoPorDocumentoJTableModel.getRows(tblDocumentos.getSelectedRows());
        
        if (documentosPagar.isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione algum registro!", "Atenção", JOptionPane.WARNING_MESSAGE);
            //carregarTabela();

        } else if (documentosPagar.stream().anyMatch(d -> d.getTotalComissaoDocumentoReceber().compareTo(BigDecimal.ZERO) <= 0)) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Você selecionou uma ou mais parcelas já recebidas", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {
            new FuncionarioPagamentoComissaoView(documentosPagar);
            
            funcionarioHistoricoPorDocumentoJTableModel.refreshRows(tblDocumentos.getSelectedRows());
            
            //funcionarioHistoricoPorDocumentoJTableModel.updateRow(this, this);
            
            exibirTotais();
        }
    }
    
    private void historico() {
        new FuncionarioPagamentoComissaoHistoricoView((Venda) funcionarioHistoricoPorDocumentoJTableModel.getRow(tblDocumentos.getSelectedRow()));
        
        funcionarioHistoricoPorDocumentoJTableModel.refreshRows(tblDocumentos.getSelectedRows());
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
        tblDocumentos = new javax.swing.JTable();
        lblMensagem = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txtTotalProdutos = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtTotalServicos = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblRegistrosExibidos = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtDataInicial = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDataFinal = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        cboStatus = new javax.swing.JComboBox<>();
        btnFiltrar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnPagar = new javax.swing.JButton();
        btnHistorico = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        txtTotalComissaoProduto = new javax.swing.JTextField();
        txtTotalComissaoServico = new javax.swing.JTextField();
        txtTotalComissao = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtTotalComissaoPago = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtTotalComissaoReceber = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();

        setTitle("Histórico por Item");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
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

        tblDocumentos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblDocumentos.setModel(new javax.swing.table.DefaultTableModel(
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
        tblDocumentos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDocumentosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblDocumentos);

        lblMensagem.setText("Consulta realizada em Xms");

        jLabel7.setForeground(java.awt.Color.blue);
        jLabel7.setText("Duplo clique para abrir o documento");

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("Produtos");

        txtTotalProdutos.setEditable(false);
        txtTotalProdutos.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalProdutos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("Serviços");

        txtTotalServicos.setEditable(false);
        txtTotalServicos.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalServicos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("Total");

        txtTotal.setEditable(false);
        txtTotal.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel38.setBackground(javax.swing.UIManager.getDefaults().getColor("InternalFrame.activeTitleBackground"));
        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel38.setForeground(java.awt.Color.white);
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("Soma");
        jLabel38.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel38.setOpaque(true);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(txtTotalProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel13)
                .addGap(18, 18, 18)
                .addComponent(txtTotalServicos, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtTotal, txtTotalProdutos, txtTotalServicos});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtTotalServicos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel13)
                .addComponent(txtTotalProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel10))
            .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jLabel4.setText("Registros exibidos:");

        lblRegistrosExibidos.setText("0");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Data Inicial");

        txtDataInicial.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDataInicial.setName("data"); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Data Final");

        txtDataFinal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDataFinal.setName("data"); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Status");

        cboStatus.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        btnFiltrar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        jLabel3.setForeground(java.awt.Color.red);
        jLabel3.setText("*Não são exibidos documentos cancelados e agrupamentos");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(cboStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(101, 101, 101))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(btnFiltrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnPagar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-initiate-money-transfer-20.png"))); // NOI18N
        btnPagar.setText("Pagar");
        btnPagar.setIconTextGap(10);
        btnPagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPagarActionPerformed(evt);
            }
        });

        btnHistorico.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-ledger-20.png"))); // NOI18N
        btnHistorico.setText("Histórico");
        btnHistorico.setIconTextGap(10);
        btnHistorico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHistoricoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnHistorico)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPagar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnHistorico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setText("Produtos");

        txtTotalComissaoProduto.setEditable(false);
        txtTotalComissaoProduto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalComissaoProduto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtTotalComissaoServico.setEditable(false);
        txtTotalComissaoServico.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalComissaoServico.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtTotalComissao.setEditable(false);
        txtTotalComissao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalComissao.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Total");

        txtTotalComissaoPago.setEditable(false);
        txtTotalComissaoPago.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalComissaoPago.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel18.setText("Pago");

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel19.setText("Receber");

        txtTotalComissaoReceber.setEditable(false);
        txtTotalComissaoReceber.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalComissaoReceber.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel20.setText("Serviços");

        jLabel37.setBackground(javax.swing.UIManager.getDefaults().getColor("InternalFrame.activeTitleBackground"));
        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel37.setForeground(java.awt.Color.white);
        jLabel37.setText("Comissão");
        jLabel37.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel37.setOpaque(true);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel37)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addComponent(txtTotalComissaoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel20)
                .addGap(18, 18, 18)
                .addComponent(txtTotalComissaoServico, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel17)
                .addGap(18, 18, 18)
                .addComponent(txtTotalComissao, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel18)
                .addGap(18, 18, 18)
                .addComponent(txtTotalComissaoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel19)
                .addGap(18, 18, 18)
                .addComponent(txtTotalComissaoReceber, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtTotalComissao, txtTotalComissaoPago, txtTotalComissaoProduto, txtTotalComissaoReceber, txtTotalComissaoServico});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(txtTotalComissaoReceber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(txtTotalComissaoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(txtTotalComissaoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(txtTotalComissaoServico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(txtTotalComissao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(lblMensagem)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRegistrosExibidos, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMensagem)
                    .addComponent(jLabel4)
                    .addComponent(lblRegistrosExibidos)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblDocumentosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDocumentosMouseClicked
        if (evt.getClickCount() == 2) {
            //int id = vendaListaJTableModel.getRow(tblVendas.getSelectedRow()).getId();
            Venda venda = (Venda) funcionarioHistoricoPorDocumentoJTableModel.getRow(tblDocumentos.getSelectedRow());
            MAIN_VIEW.addView(VendaView.getInstance(venda));
        }
    }//GEN-LAST:event_tblDocumentosMouseClicked

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        funcionarioHistoricoPorDocumentoViews.remove(this);
    }//GEN-LAST:event_formInternalFrameClosed

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPagarActionPerformed
        pagar();
    }//GEN-LAST:event_btnPagarActionPerformed

    private void btnHistoricoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHistoricoActionPerformed
        historico();
    }//GEN-LAST:event_btnHistoricoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnHistorico;
    private javax.swing.JButton btnPagar;
    private javax.swing.JComboBox<Object> cboStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMensagem;
    private javax.swing.JLabel lblRegistrosExibidos;
    private javax.swing.JTable tblDocumentos;
    private javax.swing.JFormattedTextField txtDataFinal;
    private javax.swing.JFormattedTextField txtDataInicial;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtTotalComissao;
    private javax.swing.JTextField txtTotalComissaoPago;
    private javax.swing.JTextField txtTotalComissaoProduto;
    private javax.swing.JTextField txtTotalComissaoReceber;
    private javax.swing.JTextField txtTotalComissaoServico;
    private javax.swing.JTextField txtTotalProdutos;
    private javax.swing.JTextField txtTotalServicos;
    // End of variables declaration//GEN-END:variables
}
