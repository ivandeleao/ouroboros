/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.venda.geral;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.JOptionPane;
import model.bean.principal.Parcela;
import model.bean.principal.Venda;
import model.dao.principal.VendaDAO;
import model.jtable.VendaListaJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import util.DateTime;
import static ouroboros.Ouroboros.MAIN_VIEW;
import printing.Carne;
import util.Decimal;
import util.JSwing;
import util.jTableFormat.VendasRenderer;
import view.Toast;
import view.venda.VendaView;

/**
 *
 * @author ivand
 */
public class VendaListaView extends javax.swing.JInternalFrame {
    private static VendaListaView singleInstance = null;
    VendaListaJTableModel vendaJTableModel = new VendaListaJTableModel();
    VendaDAO vendaDAO = new VendaDAO();

    List<Venda> listVenda;
    
    public static VendaListaView getSingleInstance(){
        if(singleInstance == null){
            singleInstance = new VendaListaView();
        }
        return singleInstance;
    }
    
    /**
     * Creates new form VendaListaView
     */
    private VendaListaView() {
        initComponents();
        
        JSwing.startComponentsBehavior(this);
        
        txtDataFinal.setText(DateTime.toStringDate(DateTime.getNow()));
        
        Calendar calendar = Calendar.getInstance(); //data e hora atual
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        String inicial = DateTime.toStringDate(new Timestamp(calendar.getTimeInMillis()));
        txtDataInicial.setText(inicial);
        
        formatarTabela();
        
        carregarTabela();
    }
    
    private void formatarTabela() {
        tblVendas.setModel(vendaJTableModel);

        tblVendas.setRowHeight(24);
        tblVendas.setIntercellSpacing(new Dimension(10, 10));

        tblVendas.getColumn("Id").setPreferredWidth(60);
        tblVendas.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblVendas.getColumn("Tipo").setPreferredWidth(160);
        
        tblVendas.getColumn("Status").setPreferredWidth(240);
        tblVendas.getColumn("Status").setCellRenderer(new VendasRenderer());
        
        tblVendas.getColumn("Data").setPreferredWidth(160);
        tblVendas.getColumn("Data").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblVendas.getColumn("Entrega").setPreferredWidth(200);
        tblVendas.getColumn("Entrega").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblVendas.getColumn("Cliente").setPreferredWidth(500);

        tblVendas.getColumn("Itens").setPreferredWidth(50);
        tblVendas.getColumn("Itens").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblVendas.getColumn("Total").setPreferredWidth(120);
        tblVendas.getColumn("Total").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
    }
    
    private void carregarTabela() {
        long start = System.currentTimeMillis();
        
        LocalDateTime dataInicial = DateTime.fromStringLDT(txtDataInicial.getText());
        LocalDateTime dataFinal = DateTime.fromStringLDT(txtDataFinal.getText() + " 23:59:59");
        boolean exibirCanceladas = chkCanceladas.isSelected();
        
        if(cboPeriodo.getSelectedItem().toString().equals("Emissão")) {
            
            listVenda = vendaDAO.findByCriteria(dataInicial, dataFinal, exibirCanceladas);
            
        } else if(cboPeriodo.getSelectedItem().toString().equals("Entrega")) {
            
            listVenda = vendaDAO.findPorPeriodoEntrega(dataInicial, dataFinal);
            
        } else if(cboPeriodo.getSelectedItem().toString().equals("Devolução")) {
            System.out.println("devolução");
            listVenda = vendaDAO.findPorPeriodoDevolucao(dataInicial, dataFinal);
            
        }
        
        
        BigDecimal total = BigDecimal.ZERO;
        if(!listVenda.isEmpty()) {
            total = listVenda.stream().map(Venda::getTotal).reduce(BigDecimal::add).get();
        }
        
        
        txtTotal.setText(Decimal.toString(total));
        
        vendaJTableModel.clear();
        vendaJTableModel.addList(listVenda);

        long elapsed = System.currentTimeMillis() - start;
        lblMensagem.setText("Consulta realizada em " + elapsed + "ms");

        lblRegistrosExibidos.setText(String.valueOf(listVenda.size()));
    }
    
    private void gerarCarne() {
        List<Parcela> parcelas = new ArrayList<>();
        for(int rowIndex : tblVendas.getSelectedRows()) {
            Venda venda = vendaJTableModel.getRow(rowIndex);
            if(!venda.getParcelasAPrazo().isEmpty()) {
                parcelas.addAll(venda.getParcelasAPrazo());
            }
        }
        
        if(parcelas.isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem parcelas para gerar carnê. Selecione vendas parceladas para gerar.", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            new Toast("Gerando carnê...");
            Carne.gerarCarne(parcelas);
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
        lblRegistrosExibidos = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblMensagem = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnFiltrar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cboPeriodo = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        txtDataInicial = new javax.swing.JFormattedTextField();
        txtDataFinal = new javax.swing.JFormattedTextField();
        chkCanceladas = new javax.swing.JCheckBox();
        txtTotal = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnCarne = new javax.swing.JButton();

        setTitle("Lista de Vendas");
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

        lblRegistrosExibidos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRegistrosExibidos.setText("0");

        jLabel4.setText("Registros exibidos:");

        lblMensagem.setText("...");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        jLabel1.setText("Data Inicial");

        jLabel2.setText("Data Final");

        cboPeriodo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Emissão", "Entrega", "Devolução" }));

        jLabel5.setText("Período");

        txtDataInicial.setName("data"); // NOI18N

        txtDataFinal.setName("data"); // NOI18N

        chkCanceladas.setText("Exibir vendas canceladas");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(cboPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(chkCanceladas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(btnFiltrar)
                    .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCanceladas))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtTotal.setEditable(false);
        txtTotal.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Total");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnCarne.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/vcard.png"))); // NOI18N
        btnCarne.setText("Gerar Carnê");
        btnCarne.setContentAreaFilled(false);
        btnCarne.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCarne.setIconTextGap(10);
        btnCarne.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCarne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCarneActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCarne, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCarne, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMensagem)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblRegistrosExibidos, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(11, 11, 11))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1266, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblRegistrosExibidos)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblMensagem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblVendasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVendasMouseClicked
        if (evt.getClickCount() == 2) {
            //int id = vendaJTableModel.getRow(tblVendas.getSelectedRow()).getId();
            Venda venda = vendaJTableModel.getRow(tblVendas.getSelectedRow());
            MAIN_VIEW.addView(VendaView.getInstance(venda));
        }
    }//GEN-LAST:event_tblVendasMouseClicked

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        singleInstance = null;
    }//GEN-LAST:event_formInternalFrameClosed

    private void btnCarneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCarneActionPerformed
        gerarCarne();
    }//GEN-LAST:event_btnCarneActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCarne;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JComboBox<String> cboPeriodo;
    private javax.swing.JCheckBox chkCanceladas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMensagem;
    private javax.swing.JLabel lblRegistrosExibidos;
    private javax.swing.JTable tblVendas;
    private javax.swing.JFormattedTextField txtDataFinal;
    private javax.swing.JFormattedTextField txtDataInicial;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
