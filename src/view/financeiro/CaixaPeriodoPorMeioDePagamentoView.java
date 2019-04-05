/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.financeiro;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.bean.fiscal.MeioDePagamento;
import model.bean.principal.Caixa;
import model.bean.principal.CaixaItem;
import model.bean.principal.CaixaItemTipo;
import model.bean.principal.Categoria;
import model.bean.principal.Venda;
import model.bean.relatorio.CaixaPeriodoPorMeioDePagamentoReport;
import model.bean.relatorio.CaixaResumoPorMeioDePagamentoReport;
import model.bean.temp.CaixaResumoPorMeioDePagamento;
import model.dao.principal.CaixaDAO;
import model.dao.principal.CaixaItemDAO;
import model.dao.principal.CaixaItemTipoDAO;
import model.dao.principal.CategoriaDAO;
import model.jtable.financeiro.CaixaJTableModel;
import model.jtable.financeiro.CaixaPeriodoPorMeioDePagamentoJTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.APP_PATH;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;
import util.Decimal;
import util.JSwing;
import view.venda.CompraView;
import view.venda.VendaView;

/**
 *
 * @author ivand
 */
public class CaixaPeriodoPorMeioDePagamentoView extends javax.swing.JInternalFrame {

    private static CaixaPeriodoPorMeioDePagamentoView singleInstance = null;
    
    LocalDate dataInicial, dataFinal;

    CaixaPeriodoPorMeioDePagamentoJTableModel caixaPeriodoPorMeioDePagamentoJTableModel = new CaixaPeriodoPorMeioDePagamentoJTableModel();

    CaixaDAO caixaDAO = new CaixaDAO();
    List<CaixaItem> caixaItens = new ArrayList<>();

    public static CaixaPeriodoPorMeioDePagamentoView getSingleInstance() {
        if (singleInstance == null) {
            singleInstance = new CaixaPeriodoPorMeioDePagamentoView();
        }
        return singleInstance;
    }

    /**
     * Creates new form CaixaView
     */
    private CaixaPeriodoPorMeioDePagamentoView() {
        initComponents();
        JSwing.startComponentsBehavior(this);

        txtDataInicial.setText(DateTime.toStringDate(LocalDate.now().minusMonths(1)));
        txtDataFinal.setText(DateTime.toStringDate(LocalDate.now()));

        formatarTabela();

        carregarTabela();
    }

    private void formatarTabela() {
        tblResumo.setModel(caixaPeriodoPorMeioDePagamentoJTableModel);

        tblResumo.setRowHeight(24);
        tblResumo.setIntercellSpacing(new Dimension(10, 10));

        tblResumo.getColumn("Crédito").setPreferredWidth(120);
        tblResumo.getColumn("Crédito").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblResumo.getColumn("Débito").setPreferredWidth(120);
        tblResumo.getColumn("Débito").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblResumo.getColumn("Saldo CD").setPreferredWidth(120);
        tblResumo.getColumn("Saldo CD").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblResumo.getColumn("Suprimento").setPreferredWidth(120);
        tblResumo.getColumn("Suprimento").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblResumo.getColumn("Sangria").setPreferredWidth(120);
        tblResumo.getColumn("Sangria").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblResumo.getColumn("Saldo SS").setPreferredWidth(120);
        tblResumo.getColumn("Saldo SS").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblResumo.getColumn("Saldo Final").setPreferredWidth(120);
        tblResumo.getColumn("Saldo Final").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
    }

    private void carregarTabela() {
        dataInicial = DateTime.fromStringDateLDT(txtDataInicial.getText());
        dataFinal = DateTime.fromStringDateLDT(txtDataFinal.getText());

        caixaItens = new CaixaItemDAO().findByCriteria(dataInicial, dataFinal);

        caixaPeriodoPorMeioDePagamentoJTableModel.clear();
        caixaPeriodoPorMeioDePagamentoJTableModel.addList(caixaDAO.getResumoPorMeioDePagamento(caixaItens));
        
        exibirTotal();

    }
    
    private void exibirTotal() {
        BigDecimal total = caixaDAO.getResumoPorMeioDePagamento(caixaItens).stream().map(CaixaResumoPorMeioDePagamento::getSaldoFinal).reduce(BigDecimal::add).get();
        txtTotal.setText(Decimal.toString(total));
        //valorQuitado = getRecebimentos().stream().map(CaixaItem::getSaldoLinear).reduce(BigDecimal::add).get().abs();
    }

    private void imprimir() {

        try {
            
            List<CaixaResumoPorMeioDePagamentoReport> resumoReport = new ArrayList<>();
            
            for(CaixaResumoPorMeioDePagamento resumo : caixaDAO.getResumoPorMeioDePagamento(caixaItens)) {
                resumoReport.add(
                        new CaixaResumoPorMeioDePagamentoReport(
                                resumo.getMeioDePagamento().toString(),
                                Decimal.toString(resumo.getCreditoTotal()),
                                Decimal.toString(resumo.getDebitoTotal()),
                                Decimal.toString(resumo.getSaldoCreditoDebito()),

                                Decimal.toString(resumo.getSuprimentoTotal()),
                                Decimal.toString(resumo.getSangriaTotal()),
                                Decimal.toString(resumo.getSaldoSuprimentoSangria()),

                                Decimal.toString(resumo.getSaldoFinal())
                        )
                );
            }
            
            JRBeanCollectionDataSource data = new JRBeanCollectionDataSource(resumoReport);

            
            
            String relatorio = (APP_PATH + "\\reports\\CaixaResumoPorMeioDePagamento.jasper");
            
            HashMap map = new HashMap();  
            map.put("dataInicial", DateTime.toStringDate(dataInicial));
            map.put("dataFinal", DateTime.toStringDate(dataFinal));
            map.put("itens", data);
            map.put("total", txtTotal.getText());
                    
            List<CaixaPeriodoPorMeioDePagamentoReport> dadosBase = new ArrayList<>();
            
            CaixaPeriodoPorMeioDePagamentoReport dado = new CaixaPeriodoPorMeioDePagamentoReport();
            
            
            dadosBase.add(dado);
            JRBeanCollectionDataSource jrSource = new JRBeanCollectionDataSource(dadosBase);
            
            JasperPrint jp = JasperFillManager.fillReport(relatorio, map, jrSource);
            JasperViewer jv = new JasperViewer(jp, false);

            jv.setTitle("Resumo de Caixa por Meio de Pagamento");  
            jv.setVisible(true);   
            
            
        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório " + e);
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

        jPanel1 = new javax.swing.JPanel();
        btnFiltrar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtDataInicial = new javax.swing.JFormattedTextField();
        txtDataFinal = new javax.swing.JFormattedTextField();
        jPanel2 = new javax.swing.JPanel();
        btnCriarTurno = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblResumo = new javax.swing.JTable();

        setTitle("Caixa por Período - Resumo por Meio de Pagamento");
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

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnFiltrar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnFiltrar.setText("Atualizar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Data Inicial");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Data Final");

        txtDataInicial.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDataInicial.setName("data"); // NOI18N

        txtDataFinal.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDataFinal.setName("data"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(673, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(btnFiltrar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnCriarTurno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/printer.png"))); // NOI18N
        btnCriarTurno.setText("Imprimir");
        btnCriarTurno.setContentAreaFilled(false);
        btnCriarTurno.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCriarTurno.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCriarTurno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCriarTurnoActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Total");

        txtTotal.setEditable(false);
        txtTotal.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCriarTurno, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCriarTurno, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        tblResumo.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblResumo);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

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

    private void btnCriarTurnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCriarTurnoActionPerformed
        imprimir();
    }//GEN-LAST:event_btnCriarTurnoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCriarTurno;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblResumo;
    private javax.swing.JFormattedTextField txtDataFinal;
    private javax.swing.JFormattedTextField txtDataInicial;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
