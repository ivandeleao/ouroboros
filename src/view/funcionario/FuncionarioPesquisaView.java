/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.funcionario;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.List;
import model.mysql.bean.principal.Funcionario;
import model.mysql.dao.principal.FuncionarioDAO;
import model.jtable.funcionario.FuncionarioJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;

/**
 *
 * @author ivand
 */
public class FuncionarioPesquisaView extends javax.swing.JDialog {

    //private static FuncionarioPesquisaView clientePesquisaView;
    FuncionarioJTableModel funcionarioJTableModel = new FuncionarioJTableModel();
    FuncionarioDAO funcionarioDAO = new FuncionarioDAO();

    List<Funcionario> funcionarios;
    
    Funcionario funcionario = null;

    /**
     * Creates new form FuncionarioPesquisa
     * @param funcionarioTipo
     */
    public FuncionarioPesquisaView() {
        initComponents();

        formatarTabela();
        carregarTabela();
        
        this.setLocationRelativeTo(MAIN_VIEW);
        this.setModal(true);
        this.setVisible(true);
    }
    
    public Funcionario getFuncionario(){
        return funcionario;
    }
    
    private void formatarTabela() {
        tblFuncionario.setModel(funcionarioJTableModel);

        tblFuncionario.setRowHeight(30);
        tblFuncionario.setIntercellSpacing(new Dimension(10, 10));
        //id
        tblFuncionario.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblFuncionario.getColumnModel().getColumn(0).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //nome
        tblFuncionario.getColumnModel().getColumn(1).setPreferredWidth(800);
        tblFuncionario.getColumn("Endereço").setPreferredWidth(400);
        tblFuncionario.getColumn("Telefone").setPreferredWidth(120);
        tblFuncionario.getColumn("Telefone").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        tblFuncionario.getColumn("CPF").setPreferredWidth(200);
    }
    
    private void carregarTabela() {
        String buscaRapida = txtBuscaRapida.getText();

        funcionarios = funcionarioDAO.findByNome(buscaRapida);
        

        funcionarioJTableModel.clear();
        funcionarioJTableModel.addList(funcionarios);
        
        if(tblFuncionario.getRowCount() > 0 && !buscaRapida.isEmpty()){
            tblFuncionario.setRowSelectionInterval(0, 0);
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

        txtBuscaRapida = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblFuncionario = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        setTitle("Pesquisar Funcionário");

        txtBuscaRapida.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtBuscaRapida.setToolTipText("");
        txtBuscaRapida.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscaRapidaKeyReleased(evt);
            }
        });

        tblFuncionario.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tblFuncionario.setModel(new javax.swing.table.DefaultTableModel(
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
        tblFuncionario.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblFuncionarioFocusGained(evt);
            }
        });
        tblFuncionario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblFuncionarioMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblFuncionario);

        jLabel1.setForeground(java.awt.Color.blue);
        jLabel1.setText("Rolar: PageUp e PageDown | Confirmar: Enter | Cancelar: Esc");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtBuscaRapida, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 980, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtBuscaRapida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscaRapidaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscaRapidaKeyReleased
        int index;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                funcionario = null;
                dispose();
                break;
            case KeyEvent.VK_ENTER:
                if(tblFuncionario.getSelectedRow() != -1) {
                    funcionario = funcionarioJTableModel.getRow(tblFuncionario.getSelectedRow());
                    dispose();
                }
                break;
            case KeyEvent.VK_DOWN:
                index = tblFuncionario.getSelectedRow() + 1;
                if (index < tblFuncionario.getRowCount()) {
                    tblFuncionario.setRowSelectionInterval(index, index);
                    tblFuncionario.scrollRectToVisible(tblFuncionario.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_UP:
                index = tblFuncionario.getSelectedRow() - 1;
                if (index > -1) {
                    tblFuncionario.setRowSelectionInterval(index, index);
                    tblFuncionario.scrollRectToVisible(tblFuncionario.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_PAGE_DOWN:
                index = tblFuncionario.getSelectedRow() + 10;
                if (index > tblFuncionario.getRowCount() -1) {
                    index = tblFuncionario.getRowCount() -1;
                }
                tblFuncionario.setRowSelectionInterval(index, index);
                tblFuncionario.scrollRectToVisible(tblFuncionario.getCellRect(index, 0, true));
                break;
            case KeyEvent.VK_PAGE_UP:
                index = tblFuncionario.getSelectedRow() - 10;
                if (index < 0) {
                    index = 0;
                }
                tblFuncionario.setRowSelectionInterval(index, index);
                tblFuncionario.scrollRectToVisible(tblFuncionario.getCellRect(index, 0, true));
                break;
            default:
                carregarTabela();
        }
    }//GEN-LAST:event_txtBuscaRapidaKeyReleased

    private void tblFuncionarioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblFuncionarioMouseClicked
        if(evt.getClickCount() == 2) {
            funcionario = funcionarioJTableModel.getRow(tblFuncionario.getSelectedRow());
            dispose();
        }
    }//GEN-LAST:event_tblFuncionarioMouseClicked

    private void tblFuncionarioFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblFuncionarioFocusGained
        txtBuscaRapida.requestFocus();
    }//GEN-LAST:event_tblFuncionarioFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblFuncionario;
    private javax.swing.JTextField txtBuscaRapida;
    // End of variables declaration//GEN-END:variables

    
}
