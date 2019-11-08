/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.osTransporte;

import java.awt.Dimension;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import model.jtable.documento.OSTransporteListaJTableModel;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.bean.principal.Funcionario;
import model.mysql.bean.principal.documento.OSTransporte;
import model.mysql.bean.principal.pessoa.PessoaTipo;
import model.mysql.dao.principal.OSTransporteDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;
import util.JSwing;
import view.funcionario.FuncionarioPesquisaView;
import view.pessoa.PessoaPesquisaView;

/**
 *
 * @author ivand
 */
public class OstListaView extends javax.swing.JInternalFrame {

    private static OstListaView singleInstance = null;
    OSTransporteListaJTableModel osTransporteListaJTableModel = new OSTransporteListaJTableModel();
    OSTransporteDAO ostDAO = new OSTransporteDAO();

    List<OSTransporte> listOst = new ArrayList<>();

    Pessoa remetente;
    Funcionario motorista;

    public static OstListaView getSingleInstance() {
        /*if (!USUARIO.autorizarAcesso(Recurso.FATURAMENTO)) {
            return null;
        }*/

        if (singleInstance == null) {
            singleInstance = new OstListaView();
        }
        return singleInstance;
    }

    /**
     * Creates new form VendaListaView
     */
    private OstListaView() {
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
        tblOst.setModel(osTransporteListaJTableModel);

        tblOst.setRowHeight(30);
        tblOst.setIntercellSpacing(new Dimension(10, 10));

        tblOst.getColumn("Id").setPreferredWidth(60);
        tblOst.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblOst.getColumn("Data").setPreferredWidth(160);
        tblOst.getColumn("Data").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblOst.getColumn("Remetente").setPreferredWidth(400);

        tblOst.getColumn("Motorista").setPreferredWidth(200);

        tblOst.getColumn("Total").setPreferredWidth(120);
        tblOst.getColumn("Total").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblOst.getColumn("Em aberto").setPreferredWidth(120);
        tblOst.getColumn("Em aberto").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
    }

    private void carregarTabela() {
        long start = System.currentTimeMillis();

        LocalDateTime dataInicial = DateTime.fromStringLDT(txtDataInicial.getText());
        LocalDateTime dataFinal = DateTime.fromStringLDT(txtDataFinal.getText() + " 23:59:59");
        
        boolean exibirCanceladas = chkCanceladas.isSelected();

        listOst = ostDAO.findAll();
        //listOst = ostDAO.findByCriteria(dataInicial, dataFinal, motorista, remetente, exibirCanceladas);

        osTransporteListaJTableModel.clear();
        osTransporteListaJTableModel.addList(listOst);

        long elapsed = System.currentTimeMillis() - start;
        lblMensagem.setText("Consulta realizada em " + elapsed + "ms");

        lblRegistrosExibidos.setText(String.valueOf(listOst.size()));
    }

    private void pesquisarRemetente() {
        PessoaPesquisaView pesquisa = new PessoaPesquisaView(PessoaTipo.CLIENTE);

        if (pesquisa.getPessoa() != null) {
            remetente = pesquisa.getPessoa();
            exibirRemetente();
        }
    }

    private void exibirRemetente() {
        if (remetente != null) {
            txtCliente.setText(remetente.getId() + " - " + remetente.getNome());
            txtCliente.setCaretPosition(0);
        } else {
            txtCliente.setText("TODOS");
        }
    }

    private void removerRemetente() {
        remetente = null;
        txtCliente.setText("TODOS");
    }

    private void pesquisarMotorista() {
        FuncionarioPesquisaView pesquisa = new FuncionarioPesquisaView();

        if (pesquisa.getFuncionario() != null) {
            motorista = pesquisa.getFuncionario();
            exibirMotorista();
        }
    }

    private void exibirMotorista() {
        if (motorista != null) {
            txtMotorista.setText(motorista.getId() + " - " + motorista.getNome());
            txtMotorista.setCaretPosition(0);
            
        } else {
            txtMotorista.setText("TODOS");
        }
    }

    private void removerMotorista() {
        motorista = null;
        txtMotorista.setText("TODOS");
    }

    
    private void totais() {
        //new VendaListaTotaisView(listOst);
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
        tblOst = new javax.swing.JTable();
        lblMensagem = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnFiltrar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtDataInicial = new javax.swing.JFormattedTextField();
        txtDataFinal = new javax.swing.JFormattedTextField();
        chkCanceladas = new javax.swing.JCheckBox();
        btnCliente = new javax.swing.JButton();
        txtCliente = new javax.swing.JTextField();
        btnRemoverCliente = new javax.swing.JButton();
        btnMotorista = new javax.swing.JButton();
        txtMotorista = new javax.swing.JTextField();
        btnRemoverMotorista = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btnTotais = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblRegistrosExibidos = new javax.swing.JLabel();

        setTitle("Documentos de Transporte");
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

        tblOst.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblOst.setModel(new javax.swing.table.DefaultTableModel(
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
        tblOst.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblOstMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblOst);

        lblMensagem.setText("Consulta realizada em Xms");

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

        btnCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/user.png"))); // NOI18N
        btnCliente.setText("REMETENTE");
        btnCliente.setContentAreaFilled(false);
        btnCliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCliente.setIconTextGap(10);
        btnCliente.setPreferredSize(new java.awt.Dimension(180, 49));
        btnCliente.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClienteActionPerformed(evt);
            }
        });

        txtCliente.setEditable(false);
        txtCliente.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCliente.setText("TODOS");

        btnRemoverCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-close-button-20.png"))); // NOI18N
        btnRemoverCliente.setToolTipText("Remover Cliente");
        btnRemoverCliente.setContentAreaFilled(false);
        btnRemoverCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverClienteActionPerformed(evt);
            }
        });

        btnMotorista.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-car-20.png"))); // NOI18N
        btnMotorista.setText("MOTORISTA");
        btnMotorista.setContentAreaFilled(false);
        btnMotorista.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMotorista.setIconTextGap(10);
        btnMotorista.setPreferredSize(new java.awt.Dimension(180, 49));
        btnMotorista.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMotorista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMotoristaActionPerformed(evt);
            }
        });

        txtMotorista.setEditable(false);
        txtMotorista.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtMotorista.setText("NÃO INFORMADO");

        btnRemoverMotorista.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-close-button-20.png"))); // NOI18N
        btnRemoverMotorista.setToolTipText("Remover Veículo");
        btnRemoverMotorista.setContentAreaFilled(false);
        btnRemoverMotorista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverMotoristaActionPerformed(evt);
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
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(chkCanceladas)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btnCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverCliente)
                        .addGap(18, 18, 18)
                        .addComponent(btnMotorista, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMotorista, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverMotorista)))
                .addGap(18, 18, 18)
                .addComponent(btnFiltrar)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFiltrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkCanceladas)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(btnRemoverCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txtCliente)
                                        .addComponent(btnCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtMotorista, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(btnMotorista, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnRemoverMotorista)))))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnTotais.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-bank-20.png"))); // NOI18N
        btnTotais.setText("Totais");
        btnTotais.setContentAreaFilled(false);
        btnTotais.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTotais.setIconTextGap(10);
        btnTotais.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnTotais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTotaisActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnTotais, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnTotais, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel7.setForeground(java.awt.Color.blue);
        jLabel7.setText("Duplo clique para abrir o documento");

        jLabel4.setText("Registros exibidos:");

        lblRegistrosExibidos.setText("0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblMensagem)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRegistrosExibidos, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblOstMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOstMouseClicked
        if (evt.getClickCount() == 2) {
            //int id = osTransporteListaJTableModel.getRow(tblVendas.getSelectedRow()).gtblOst           
            OSTransporte ost = osTransporteListaJTableModel.getRow(tblOst.getSelectedRow());
            MAIN_VIEW.addView(OSTransporteView.getInstance(ost));
        }
    }//GEN-LAST:event_tblOstMouseClicked

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        singleInstance = null;
    }//GEN-LAST:event_formInternalFrameClosed

    private void btnClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClienteActionPerformed
        pesquisarRemetente();
    }//GEN-LAST:event_btnClienteActionPerformed

    private void btnRemoverClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverClienteActionPerformed
        removerRemetente();
    }//GEN-LAST:event_btnRemoverClienteActionPerformed

    private void btnMotoristaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMotoristaActionPerformed
        pesquisarMotorista();
    }//GEN-LAST:event_btnMotoristaActionPerformed

    private void btnRemoverMotoristaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverMotoristaActionPerformed
        removerMotorista();
    }//GEN-LAST:event_btnRemoverMotoristaActionPerformed

    private void btnTotaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTotaisActionPerformed
        totais();
    }//GEN-LAST:event_btnTotaisActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCliente;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnMotorista;
    private javax.swing.JButton btnRemoverCliente;
    private javax.swing.JButton btnRemoverMotorista;
    private javax.swing.JButton btnTotais;
    private javax.swing.JCheckBox chkCanceladas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMensagem;
    private javax.swing.JLabel lblRegistrosExibidos;
    private javax.swing.JTable tblOst;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JFormattedTextField txtDataFinal;
    private javax.swing.JFormattedTextField txtDataInicial;
    private javax.swing.JTextField txtMotorista;
    // End of variables declaration//GEN-END:variables
}
