/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoEntrada;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import javax.swing.JOptionPane;
import model.jtable.documento.DocumentoEntradaListaJTableModel;
import model.mysql.bean.principal.Funcionario;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.Recurso;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.principal.FuncionarioDAO;
import model.mysql.dao.principal.VendaDAO;
import model.jtable.documento.VendaListaJTableModel;
import model.mysql.bean.principal.documento.TipoOperacao;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import util.DateTime;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.USUARIO;
import printing.Carne;
import util.Decimal;
import util.JSwing;
import util.jTableFormat.VendasRenderer;
import view.Toast;
import view.documentoSaida.VendaView;

/**
 *
 * @author ivand
 */
public class DocumentoEntradaListaView extends javax.swing.JInternalFrame {
    private static DocumentoEntradaListaView singleInstance = null;
    DocumentoEntradaListaJTableModel documentoEntradaListaJTableModel = new DocumentoEntradaListaJTableModel();
    VendaDAO vendaDAO = new VendaDAO();

    List<Venda> listVenda = new ArrayList<>();
    
    public static DocumentoEntradaListaView getSingleInstance(){
        if(!USUARIO.autorizarAcesso(Recurso.FATURAMENTO)) {
            return null;
        }
        
        if(singleInstance == null){
            singleInstance = new DocumentoEntradaListaView();
        }
        return singleInstance;
    }
    
    /**
     * Creates new form VendaListaView
     */
    private DocumentoEntradaListaView() {
        initComponents();
        
        JSwing.startComponentsBehavior(this);
        
        txtDataFinal.setText(DateTime.toStringDate(DateTime.getNow()));
        
        Calendar calendar = Calendar.getInstance(); //data e hora atual
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        String inicial = DateTime.toStringDate(new Timestamp(calendar.getTimeInMillis()));
        txtDataInicial.setText(inicial);
        
        formatarTabela();
        
        carregarTabela();
        
        carregarFuncionarios();
        
    }
    
    private void formatarTabela() {
        tblVendas.setModel(documentoEntradaListaJTableModel);

        tblVendas.setRowHeight(24);
        tblVendas.setIntercellSpacing(new Dimension(10, 10));

        tblVendas.getColumn("Id").setPreferredWidth(60);
        tblVendas.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblVendas.getColumn("Data").setPreferredWidth(160);
        tblVendas.getColumn("Data").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblVendas.getColumn("Fornecedor").setPreferredWidth(400);

        tblVendas.getColumn("Funcionário").setPreferredWidth(120);

        tblVendas.getColumn("Total").setPreferredWidth(120);
        tblVendas.getColumn("Total").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
    }
    
    private void carregarTabela() {
        long start = System.currentTimeMillis();
        
        LocalDateTime dataInicial = DateTime.fromStringLDT(txtDataInicial.getText());
        LocalDateTime dataFinal = DateTime.fromStringLDT(txtDataFinal.getText() + " 23:59:59");
        Funcionario funcionario = (Funcionario) cboFuncionario.getSelectedItem();
        boolean exibirCanceladas = chkCanceladas.isSelected();
        
        
        listVenda = vendaDAO.findByCriteria(TipoOperacao.ENTRADA, dataInicial, dataFinal, funcionario, null,null, exibirCanceladas, Optional.empty());
        
        
        BigDecimal totalGeral = BigDecimal.ZERO;
        BigDecimal totalEfetivo = BigDecimal.ZERO;
        BigDecimal totalOrcamento = BigDecimal.ZERO;
        BigDecimal totalCancelado = BigDecimal.ZERO;
        
        for (Venda venda : listVenda) {
            totalGeral = totalGeral.add(venda.getTotal());
            if(venda.isOrcamento()) {
                totalOrcamento = totalOrcamento.add(venda.getTotal());
            } else if(venda.getCancelamento() != null) {
                totalCancelado = totalCancelado.add(venda.getTotal());
            } else {
                totalEfetivo = totalEfetivo.add(venda.getTotal());
            }
        }
        
        txtTotalCancelado.setText(Decimal.toString(totalCancelado));
        txtTotalOrcamento.setText(Decimal.toString(totalOrcamento));
        txtTotalEfetivo.setText(Decimal.toString(totalEfetivo));
        txtTotalGeral.setText(Decimal.toString(totalGeral));
        
        documentoEntradaListaJTableModel.clear();
        documentoEntradaListaJTableModel.addList(listVenda);

        long elapsed = System.currentTimeMillis() - start;
        lblMensagem.setText("Consulta realizada em " + elapsed + "ms");

        lblRegistrosExibidos.setText(String.valueOf(listVenda.size()));
    }
    
    private void carregarFuncionarios(){
        List<Funcionario> funcionarios = new FuncionarioDAO().findAll(false);
        Funcionario noFilter = new Funcionario();
        noFilter.setId(0);
        noFilter.setNome("Todos");
        cboFuncionario.addItem(noFilter);
        for (Funcionario f : funcionarios) {
            cboFuncionario.addItem(f);
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
        tblVendas = new javax.swing.JTable();
        lblMensagem = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnFiltrar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtDataInicial = new javax.swing.JFormattedTextField();
        txtDataFinal = new javax.swing.JFormattedTextField();
        chkCanceladas = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        cboFuncionario = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txtTotalEfetivo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtTotalOrcamento = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtTotalCancelado = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtTotalGeral = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblRegistrosExibidos = new javax.swing.JLabel();

        setTitle("Faturamento");
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

        tblVendas.setModel(new javax.swing.table.DefaultTableModel(
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
        tblVendas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVendasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblVendas);

        lblMensagem.setText("...");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnFiltrar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Data Inicial");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Data Final");

        txtDataInicial.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDataInicial.setName("data"); // NOI18N

        txtDataFinal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDataFinal.setName("data"); // NOI18N

        chkCanceladas.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkCanceladas.setText("Exibir documentos cancelados");
        chkCanceladas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCanceladasActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Funcionário");

        cboFuncionario.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

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
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(cboFuncionario, 0, 296, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(chkCanceladas)
                .addGap(18, 18, 18)
                .addComponent(btnFiltrar)
                .addGap(235, 235, 235))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(btnFiltrar)
                    .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCanceladas)
                    .addComponent(jLabel6)
                    .addComponent(cboFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(60, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 829, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 101, Short.MAX_VALUE)
        );

        jLabel7.setForeground(java.awt.Color.blue);
        jLabel7.setText("Duplo clique para abrir o documento");

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtTotalEfetivo.setEditable(false);
        txtTotalEfetivo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalEfetivo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Efetivo");

        txtTotalOrcamento.setEditable(false);
        txtTotalOrcamento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalOrcamento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("Orçamento");

        txtTotalCancelado.setEditable(false);
        txtTotalCancelado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalCancelado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setText("Cancelado");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setText("Geral");

        txtTotalGeral.setEditable(false);
        txtTotalGeral.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalGeral.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTotalCancelado, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                    .addComponent(txtTotalOrcamento))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(18, 18, 18)
                        .addComponent(txtTotalGeral, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(txtTotalEfetivo, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtTotalEfetivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(txtTotalCancelado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtTotalGeral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(txtTotalOrcamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jLabel10.setText("Totais");

        jLabel4.setText("Registros exibidos:");

        lblRegistrosExibidos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRegistrosExibidos.setText("0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(lblMensagem))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(lblRegistrosExibidos)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(lblMensagem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblRegistrosExibidos)
                        .addComponent(jLabel4))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(jLabel10)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblVendasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVendasMouseClicked
        if (evt.getClickCount() == 2) {
            //int id = documentoEntradaListaJTableModel.getRow(tblVendas.getSelectedRow()).getId();
            Venda venda = documentoEntradaListaJTableModel.getRow(tblVendas.getSelectedRow());
            MAIN_VIEW.addView(DocumentoEntradaView.getInstance(venda));
        }
    }//GEN-LAST:event_tblVendasMouseClicked

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        singleInstance = null;
    }//GEN-LAST:event_formInternalFrameClosed

    private void chkCanceladasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCanceladasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCanceladasActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JComboBox<Object> cboFuncionario;
    private javax.swing.JCheckBox chkCanceladas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMensagem;
    private javax.swing.JLabel lblRegistrosExibidos;
    private javax.swing.JTable tblVendas;
    private javax.swing.JFormattedTextField txtDataFinal;
    private javax.swing.JFormattedTextField txtDataInicial;
    private javax.swing.JTextField txtTotalCancelado;
    private javax.swing.JTextField txtTotalEfetivo;
    private javax.swing.JTextField txtTotalGeral;
    private javax.swing.JTextField txtTotalOrcamento;
    // End of variables declaration//GEN-END:variables
}
