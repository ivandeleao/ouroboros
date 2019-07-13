/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoSaida;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import model.jtable.documento.ComandasJTableModel;
import model.mysql.bean.principal.Recurso;
import model.mysql.bean.principal.documento.ComandaSnapshot;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.documento.VendaTipo;
import model.mysql.dao.principal.VendaDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import util.DateTime;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.USUARIO;
import static ouroboros.Ouroboros.VENDA_NUMERO_COMANDAS;
import static ouroboros.Ouroboros.em;
import util.MwString;
import util.ScrollablePanel;

/**
 *
 * @author ivand
 */
public class ComandasViewNew extends javax.swing.JInternalFrame {

    private static ComandasViewNew singleInstance = null;
    VendaDAO vendaDAO = new VendaDAO();
    ComandasJTableModel comandaJTableModel = new ComandasJTableModel();
    List<ComandaSnapshot> comandas = new ArrayList<>();

    public static ComandasViewNew getSingleInstance() {
        if(!USUARIO.autorizarAcesso(Recurso.COMANDAS)) {
            return null;
        }
        
        if (singleInstance == null) {
            singleInstance = new ComandasViewNew();
        }
        return singleInstance;
    }

    /**
     * Creates new form ComandasView
     */
    private ComandasViewNew() {
        initComponents();

        carregarDados();
        formatarTabela();
        carregarTabela();
        

    }
    
    private void carregarDados() {
        
        comandas = vendaDAO.getComandasAbertasSnapshot();
        
        txtComandasAbertas.setText(String.valueOf(comandas.size()));
    }
    
    private void formatarTabela() {
        tblComandas.setModel(comandaJTableModel);

        tblComandas.setRowHeight(30);

        tblComandas.getColumn("Id").setPreferredWidth(1);
        tblComandas.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblComandas.getColumn("Número").setPreferredWidth(100);
        tblComandas.getColumn("Número").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblComandas.getColumn("Início").setPreferredWidth(100);
        tblComandas.getColumn("Início").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblComandas.getColumn("Itens").setPreferredWidth(100);
        tblComandas.getColumn("Itens").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblComandas.getColumn("Valor").setPreferredWidth(900);
        tblComandas.getColumn("Valor").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        
        if (comandaJTableModel.getRowCount() > 0) {
            tblComandas.setRowSelectionInterval(0, 0);
        }
    }

    private void carregarTabela() {
        
        comandaJTableModel.clear();
        comandaJTableModel.addList(comandas);
    }
    
    private void abrirComanda(int numero) {
        Venda venda = vendaDAO.getComandaAberta(numero);
        
        if(venda == null) {
            venda = new Venda(VendaTipo.COMANDA);
            venda.setComanda(numero);
        }

        MAIN_VIEW.addView(VendaView.getInstance(venda));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtComanda = new javax.swing.JTextField();
        txtComandasAbertas = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblComandas = new javax.swing.JTable();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();

        setTitle("Comandas");
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                formFocusLost(evt);
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
                formInternalFrameDeactivated(evt);
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        txtComanda.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        txtComanda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtComanda.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtComandaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtComandaFocusLost(evt);
            }
        });
        txtComanda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtComandaKeyReleased(evt);
            }
        });

        txtComandasAbertas.setEditable(false);
        txtComandasAbertas.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtComandasAbertas.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Comandas abertas");

        tblComandas.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tblComandas.setIntercellSpacing(new java.awt.Dimension(10, 10));
        tblComandas.setRowHeight(24);
        tblComandas.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblComandasFocusGained(evt);
            }
        });
        tblComandas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblComandasMouseClicked(evt);
            }
        });
        tblComandas.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tblComandasPropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(tblComandas);

        jLabel37.setBackground(new java.awt.Color(122, 138, 153));
        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel37.setForeground(java.awt.Color.white);
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText("Nº Comanda + ENTER");
        jLabel37.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        jLabel37.setOpaque(true);

        jLabel38.setBackground(new java.awt.Color(122, 138, 153));
        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel38.setForeground(java.awt.Color.white);
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("Comandas Abertas");
        jLabel38.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        jLabel38.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtComanda, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(txtComandasAbertas, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 883, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37)
                    .addComponent(jLabel38))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtComanda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtComandasAbertas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(0, 538, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        txtComanda.requestFocus();
    }//GEN-LAST:event_formFocusGained

    private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost

    }//GEN-LAST:event_formFocusLost

    private void formInternalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameDeactivated

    }//GEN-LAST:event_formInternalFrameDeactivated

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated

    }//GEN-LAST:event_formInternalFrameActivated

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        singleInstance = null;
    }//GEN-LAST:event_formInternalFrameClosed

    private void txtComandaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtComandaKeyReleased
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:

                String numero = MwString.soNumeros(txtComanda.getText().trim());
                int comanda = Integer.valueOf(numero);
                
                Venda venda = vendaDAO.getComandaAberta(comanda);
                //System.out.println("venda comanda " + venda.getComanda());
                if(venda == null) {
                    venda = new Venda(VendaTipo.COMANDA);
                    venda.setComanda(comanda);
                    //venda.setVendaTipo(VendaTipo.COMANDA);
                }

                //int id = venda != null ? venda.getId() : 0;
                txtComanda.setText("");

                //MAIN_VIEW.addView(VendaView.getInstance(venda, comanda, false));
                MAIN_VIEW.addView(VendaView.getInstance(venda));
                break;
        }
    }//GEN-LAST:event_txtComandaKeyReleased

    private void txtComandaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtComandaFocusGained
        carregarDados();
        carregarTabela();
    }//GEN-LAST:event_txtComandaFocusGained

    private void txtComandaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtComandaFocusLost
        //threadBotoes.stop();
    }//GEN-LAST:event_txtComandaFocusLost

    private void tblComandasFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblComandasFocusGained
    }//GEN-LAST:event_tblComandasFocusGained

    private void tblComandasPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tblComandasPropertyChange

    }//GEN-LAST:event_tblComandasPropertyChange

    private void tblComandasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblComandasMouseClicked
        if(evt.getClickCount() == 2) {
            int numero = comandaJTableModel.getRow(tblComandas.getSelectedRow()).getNumero();
            abrirComanda(numero);
        }
    }//GEN-LAST:event_tblComandasMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblComandas;
    private javax.swing.JTextField txtComanda;
    private javax.swing.JTextField txtComandasAbertas;
    // End of variables declaration//GEN-END:variables
}
