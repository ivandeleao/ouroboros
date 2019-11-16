/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoSaida.geral;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.KeyStroke;
import model.jtable.documento.DocumentosAgrupadosJTableModel;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.principal.VendaDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.Decimal;
import util.JSwing;
import util.jTableFormat.VendasRenderer;

/**
 *
 * @author ivand
 */
public class DocumentosAgrupadosView extends javax.swing.JDialog {

    Venda documento;
    
    DocumentosAgrupadosJTableModel documentosAgrupadosJTableModel = new DocumentosAgrupadosJTableModel();
    VendaDAO vendaDAO = new VendaDAO();



    private DocumentosAgrupadosView() {
        super(MAIN_VIEW, true);
        initComponents();
    }

    public DocumentosAgrupadosView(Venda documento) {
        super(MAIN_VIEW, true);
        initComponents();
        definirAtalhos();
        JSwing.startComponentsBehavior(this);

        this.documento = documento;
        

        carregarDados();
        
        formatarTabela();

        carregarTabela();
        
        exibirTotal();

        this.setLocationRelativeTo(MAIN_VIEW);
        this.setVisible(true);
    }
    
    private void definirAtalhos() {
        //JRootPane rootPane = this.getRootPane();
        InputMap im = rootPane.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = rootPane.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "fechar");
        am.put("fechar", new FormKeyStroke("ESC"));
        
    }
    
    protected class FormKeyStroke extends AbstractAction {
        private final String key;
        public FormKeyStroke(String key){
            this.key = key;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            switch(key){
                case "ESC":
                    dispose();
                    break;
            }
        }
    }

    
    private void carregarDados() {
        txtCliente.setText(documento.getPessoa().getNome());
    }

    private void carregarTabela() {
        long start = System.currentTimeMillis();

        documentosAgrupadosJTableModel.clear();
        documentosAgrupadosJTableModel.addList(documento.getDocumentosFilho());

        if (tblDocumento.getRowCount() > 0) {
            tblDocumento.setRowSelectionInterval(0, 0);
        }

        long elapsed = System.currentTimeMillis() - start;
        lblMensagem.setText("Consulta realizada em " + elapsed + "ms");
    }

    private void formatarTabela() {
        tblDocumento.setModel(documentosAgrupadosJTableModel);

        tblDocumento.setRowHeight(30);
        tblDocumento.setIntercellSpacing(new Dimension(10, 10));

        tblDocumento.getColumn("Id").setPreferredWidth(60);
        tblDocumento.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblDocumento.getColumn("Tipo").setPreferredWidth(160);

        tblDocumento.getColumn("Status").setPreferredWidth(240);
        tblDocumento.getColumn("Status").setCellRenderer(new VendasRenderer());

        tblDocumento.getColumn("Data").setPreferredWidth(160);
        tblDocumento.getColumn("Data").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);


        tblDocumento.getColumn("Funcionário").setPreferredWidth(120);

        tblDocumento.getColumn("NFSe").setPreferredWidth(60);

        tblDocumento.getColumn("Sat").setPreferredWidth(60);

        tblDocumento.getColumn("Total").setPreferredWidth(120);
        tblDocumento.getColumn("Total").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblDocumento.getColumn("Em aberto").setPreferredWidth(120);
        tblDocumento.getColumn("Em aberto").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
    }
    
    private void tblClick() {
        System.out.println("tblDocumento.getSelectedColumn(): " + tblDocumento.getSelectedColumn());
        if(tblDocumento.getSelectedColumn()== 9) {
            Venda docRemover = documentosAgrupadosJTableModel.getRow(tblDocumento.getSelectedRow());

            documento.removeDocumentoFilho(docRemover);

            vendaDAO.save(docRemover);
            vendaDAO.save(documento);

            carregarTabela();
        }
    }
    
    private void exibirTotal() {
        Currency currency = Currency.getInstance(new Locale("pt", "BR"));

        String simboloMonetario = currency.getSymbol();

        txtSelecionados.setText(documento.getDocumentosFilho().size() + " (" + simboloMonetario + Decimal.toString(documento.getDocumentosFilho().stream().map(Venda::getTotal).reduce(BigDecimal::add).get()) + ")");
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
        tblDocumento = new javax.swing.JTable();
        lblMensagem = new javax.swing.JLabel();
        txtCliente = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btnCancelar = new javax.swing.JButton();
        txtSelecionados = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();

        setTitle("Documentos Agrupados");

        tblDocumento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tblDocumento.setModel(new javax.swing.table.DefaultTableModel(
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
        tblDocumento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblDocumentoFocusGained(evt);
            }
        });
        tblDocumento.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDocumentoMouseClicked(evt);
            }
        });
        tblDocumento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblDocumentoKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblDocumento);

        lblMensagem.setText("Consulta realizada em 0ms");

        txtCliente.setEditable(false);
        txtCliente.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtCliente.setText("TODOS");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Cliente");

        btnCancelar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnCancelar.setText("Fechar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        txtSelecionados.setEditable(false);
        txtSelecionados.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtSelecionados.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Total");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(txtSelecionados, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(txtCliente))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblMensagem)
                        .addGap(0, 1053, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblMensagem)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtCliente))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSelecionados)
                    .addComponent(jLabel7))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblDocumentoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDocumentoMouseClicked
        tblClick();
    }//GEN-LAST:event_tblDocumentoMouseClicked

    private void tblDocumentoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblDocumentoFocusGained
        
    }//GEN-LAST:event_tblDocumentoFocusGained

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void tblDocumentoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblDocumentoKeyReleased
    }//GEN-LAST:event_tblDocumentoKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMensagem;
    private javax.swing.JTable tblDocumento;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JTextField txtSelecionados;
    // End of variables declaration//GEN-END:variables

}
