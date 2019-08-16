/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.produto;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import model.mysql.bean.fiscal.Cest;
import model.mysql.dao.fiscal.CestDAO;
import model.jtable.CestJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;

/**
 *
 * @author ivand
 */
public class CestPesquisaView extends javax.swing.JDialog {

    //private static CestPesquisaView cestPesquisaView;
    CestJTableModel cestJTableModel = new CestJTableModel();
    CestDAO cestDAO = new CestDAO();

    List<Cest> listCest;
    
    Cest cest = null;

    /**
     * Creates new form CestPesquisa
     * @param parent
     */
    private CestPesquisaView(java.awt.Frame parent) {
        super(parent, true);
        initComponents();

        
    }
    
    public CestPesquisaView(String buscar) {
        super(MAIN_VIEW, true);
        initComponents();
        txtBuscaRapida.setText(buscar);
        
        formatarTabela();
        carregarTabela();
        
        this.setLocationRelativeTo(MAIN_VIEW);
        this.setVisible(true);
        
    }
    
    public Cest getCest(){
        return cest;
    }
    
    private void formatarTabela() {
        tableCest.setModel(cestJTableModel);

        tableCest.setRowHeight(24);
        tableCest.setIntercellSpacing(new Dimension(10, 10));
        //codigo
        tableCest.getColumnModel().getColumn(0).setPreferredWidth(120);
        tableCest.getColumnModel().getColumn(0).setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        //ncm
        tableCest.getColumnModel().getColumn(1).setPreferredWidth(120);
        tableCest.getColumnModel().getColumn(1).setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        //descrição
        tableCest.getColumnModel().getColumn(2).setPreferredWidth(800);
        
        tableCest.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                exibirItemAtual();
            }

        });
    }
    
    private void confirmar() {
        cest = cestJTableModel.getRow(tableCest.getSelectedRow());
        dispose();
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
        tableCest = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescricao = new javax.swing.JTextArea();

        setTitle("Pesquisar CEST (Código Especificador da Substituição Tributária.)");

        txtBuscaRapida.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtBuscaRapida.setToolTipText("");
        txtBuscaRapida.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscaRapidaKeyReleased(evt);
            }
        });

        tableCest.setModel(new javax.swing.table.DefaultTableModel(
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
        tableCest.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tableCestFocusGained(evt);
            }
        });
        tableCest.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableCestMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableCest);

        jLabel1.setForeground(java.awt.Color.blue);
        jLabel1.setText("Rolar: PageUp e PageDown | Confirmar: Enter | Cancelar: Esc");

        jLabel2.setText("Pesquise por NCM ou Descrição");

        txtDescricao.setEditable(false);
        txtDescricao.setBackground(javax.swing.UIManager.getDefaults().getColor("TextField.disabledBackground"));
        txtDescricao.setColumns(20);
        txtDescricao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDescricao.setLineWrap(true);
        txtDescricao.setRows(5);
        txtDescricao.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jScrollPane2.setViewportView(txtDescricao);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtBuscaRapida)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 964, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBuscaRapida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscaRapidaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscaRapidaKeyReleased
        int index;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                cest = null;
                dispose();
                break;
            case KeyEvent.VK_ENTER:
                confirmar();
                break;
            case KeyEvent.VK_DOWN:
                index = tableCest.getSelectedRow() + 1;
                if (index < tableCest.getRowCount()) {
                    tableCest.setRowSelectionInterval(index, index);
                    tableCest.scrollRectToVisible(tableCest.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_UP:
                index = tableCest.getSelectedRow() - 1;
                if (index > -1) {
                    tableCest.setRowSelectionInterval(index, index);
                    tableCest.scrollRectToVisible(tableCest.getCellRect(index, 0, true));
                }
                break;
            case KeyEvent.VK_PAGE_DOWN:
                index = tableCest.getSelectedRow() + 10;
                if (index > tableCest.getRowCount() -1) {
                    index = tableCest.getRowCount() -1;
                }
                tableCest.setRowSelectionInterval(index, index);
                tableCest.scrollRectToVisible(tableCest.getCellRect(index, 0, true));
                break;
            case KeyEvent.VK_PAGE_UP:
                index = tableCest.getSelectedRow() - 10;
                if (index < 0) {
                    index = 0;
                }
                tableCest.setRowSelectionInterval(index, index);
                tableCest.scrollRectToVisible(tableCest.getCellRect(index, 0, true));
                break;
            default:
                carregarTabela();
        }
    }//GEN-LAST:event_txtBuscaRapidaKeyReleased

    private void carregarTabela() {
        String buscaRapida = txtBuscaRapida.getText();

        listCest = cestDAO.findByCriteria(null, buscaRapida, buscaRapida);

        cestJTableModel.clear();
        cestJTableModel.addList(listCest);
        
        if(tableCest.getRowCount() > 0){
            tableCest.setRowSelectionInterval(0, 0);
        }
    }
    
    private void exibirItemAtual() {
        try {
            if (tableCest.getSelectedRow() > -1) {
                int index = tableCest.getSelectedRow();
                txtDescricao.setText(cestJTableModel.getRow(index).getDescricao());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e, "Erro ao exibir item atual", JOptionPane.ERROR_MESSAGE);
        }
    }
        
    
    private void tableCestMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableCestMouseClicked
        if(evt.getClickCount() == 2) {
            confirmar();
        }
    }//GEN-LAST:event_tableCestMouseClicked

    private void tableCestFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tableCestFocusGained
        txtBuscaRapida.requestFocus();
    }//GEN-LAST:event_tableCestFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tableCest;
    private javax.swing.JTextField txtBuscaRapida;
    private javax.swing.JTextArea txtDescricao;
    // End of variables declaration//GEN-END:variables

    
}
