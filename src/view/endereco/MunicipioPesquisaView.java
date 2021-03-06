/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.endereco;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.List;
import model.mysql.bean.endereco.Cidade;
import model.mysql.dao.endereco.CidadeDAO;
import model.jtable.endereco.MunicipioJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;

/**
 *
 * @author ivand
 */
public class MunicipioPesquisaView extends javax.swing.JDialog {

    MunicipioJTableModel municipioJTableModel = new MunicipioJTableModel();
    CidadeDAO cidadeDAO = new CidadeDAO();

    List<Cidade> cidades;
    
    Cidade cidade = null;

    public MunicipioPesquisaView() {
        super(MAIN_VIEW, true);
        initComponents();

        formatarTabela();

        this.setLocationRelativeTo(MAIN_VIEW);
        this.setVisible(true);
        
    }
    
    public MunicipioPesquisaView(String buscar) {
        super(MAIN_VIEW, true);
        initComponents();

        formatarTabela();

        txtBuscaRapida.setText(buscar);
        carregarTabela();
        
        this.setLocationRelativeTo(MAIN_VIEW);
        this.setVisible(true);
    }
    
    
    public Cidade getCidade(){
        return cidade;
    }
    
    private void carregarTabela() {
        String buscaRapida = txtBuscaRapida.getText();

        cidades = cidadeDAO.findByNome(buscaRapida);

        municipioJTableModel.clear();
        municipioJTableModel.addList(cidades);
        
        if(tblCidade.getRowCount() > 0){
            tblCidade.setRowSelectionInterval(0, 0);
        }
    }
    
    private void formatarTabela() {
        tblCidade.setModel(municipioJTableModel);

        tblCidade.setRowHeight(30);
        tblCidade.setIntercellSpacing(new Dimension(10, 10));

        tblCidade.getColumn("Município").setPreferredWidth(600);
        
        tblCidade.getColumn("Estado").setPreferredWidth(300);
        
        tblCidade.getColumn("Código").setPreferredWidth(100);
        

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
        tblCidade = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setTitle("Pesquisar Endereço");

        txtBuscaRapida.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtBuscaRapida.setToolTipText("");
        txtBuscaRapida.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscaRapidaKeyReleased(evt);
            }
        });

        tblCidade.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblCidade.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCidade.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblCidadeFocusGained(evt);
            }
        });
        tblCidade.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCidadeMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCidade);

        jLabel1.setForeground(java.awt.Color.blue);
        jLabel1.setText("Rolar: PageUp e PageDown | Confirmar: Enter | Cancelar: Esc");

        jLabel2.setForeground(java.awt.Color.red);
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Consulta sensível aos acentos");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 502, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtBuscaRapida)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 964, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtBuscaRapida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscaRapidaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscaRapidaKeyReleased
        int index;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                cidade = null;
                dispose();
                break;
            case KeyEvent.VK_ENTER:
                cidade = municipioJTableModel.getRow(tblCidade.getSelectedRow());
                dispose();
                break;
            case KeyEvent.VK_DOWN:
                index = tblCidade.getSelectedRow() + 1;
                if (index < tblCidade.getRowCount()) {
                    tblCidade.setRowSelectionInterval(index, index);
                    tblCidade.scrollRectToVisible(tblCidade.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_UP:
                index = tblCidade.getSelectedRow() - 1;
                if (index > -1) {
                    tblCidade.setRowSelectionInterval(index, index);
                    tblCidade.scrollRectToVisible(tblCidade.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_PAGE_DOWN:
                index = tblCidade.getSelectedRow() + 10;
                if (index > tblCidade.getRowCount() -1) {
                    index = tblCidade.getRowCount() -1;
                }
                tblCidade.setRowSelectionInterval(index, index);
                tblCidade.scrollRectToVisible(tblCidade.getCellRect(index, 0, true));
                break;
            case KeyEvent.VK_PAGE_UP:
                index = tblCidade.getSelectedRow() - 10;
                if (index < 0) {
                    index = 0;
                }
                tblCidade.setRowSelectionInterval(index, index);
                tblCidade.scrollRectToVisible(tblCidade.getCellRect(index, 0, true));
                break;
            default:
                carregarTabela();
        }
    }//GEN-LAST:event_txtBuscaRapidaKeyReleased

    private void tblCidadeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCidadeMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblCidadeMouseClicked

    private void tblCidadeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblCidadeFocusGained
        txtBuscaRapida.requestFocus();
    }//GEN-LAST:event_tblCidadeFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblCidade;
    private javax.swing.JTextField txtBuscaRapida;
    // End of variables declaration//GEN-END:variables

    
}
