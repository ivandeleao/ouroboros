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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import model.jtable.documento.DocumentoSaidaPesquisaJTableModel;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.principal.VendaDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;
import util.Decimal;
import util.JSwing;
import util.jTableFormat.VendasRenderer;

/**
 *
 * @author ivand
 */
public class DocumentoSaidaPesquisaView extends javax.swing.JDialog {

    Venda documento;

    DocumentoSaidaPesquisaJTableModel documentoSaidaPesquisaJTableModel = new DocumentoSaidaPesquisaJTableModel();
    VendaDAO vendaDAO = new VendaDAO();

    List<Venda> documentos = new ArrayList<>();
    List<Venda> documentosSelecionados = new ArrayList();

    private DocumentoSaidaPesquisaView() {
        super(MAIN_VIEW, true);
        initComponents();
    }

    public DocumentoSaidaPesquisaView(Venda documento) {
        super(MAIN_VIEW, true);
        initComponents();
        definirAtalhos();
        JSwing.startComponentsBehavior(this);

        this.documento = documento;

        txtDataFinal.setText(DateTime.toString(LocalDate.now()));
        txtDataInicial.setText(DateTime.toString(LocalDate.now().minusDays(10)));

        carregarDados();

        formatarTabela();

        carregarTabela();

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

        public FormKeyStroke(String key) {
            this.key = key;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (key) {
                case "ESC":
                    dispose();
                    break;
            }
        }
    }

    public List<Venda> getDocumentos() {
        return documentosSelecionados;
    }

    private void carregarDados() {
        txtCliente.setText(documento.getPessoa().getNome());
    }

    private void carregarTabela() {
        long start = System.currentTimeMillis();

        LocalDateTime dataInicial = DateTime.fromStringLDT(txtDataInicial.getText());
        LocalDateTime dataFinal = DateTime.fromStringLDT(txtDataFinal.getText() + " 23:59:59");

        documentos = vendaDAO.findByCriteria(TipoOperacao.SAIDA, dataInicial, dataFinal, null, documento.getPessoa(), null, false, null, null, Optional.of(false), Optional.of(false), false, null);

        //Remover o próprio documento da lista
        documentos = documentos.stream().filter(doc -> !doc.getId().equals(documento.getId())).collect(Collectors.toList());

        //Remover documentos com parcelas
        documentos = documentos.stream().filter(doc -> doc.getParcelas().isEmpty()).collect(Collectors.toList());

        documentoSaidaPesquisaJTableModel.clear();
        documentoSaidaPesquisaJTableModel.addList(documentos);

        if (tblDocumento.getRowCount() > 0) {
            tblDocumento.setRowSelectionInterval(0, 0);

            exibirTotalSelecionados();
        }

        long elapsed = System.currentTimeMillis() - start;
        lblMensagem.setText("Consulta realizada em " + elapsed + "ms");
    }

    private void formatarTabela() {
        tblDocumento.setModel(documentoSaidaPesquisaJTableModel);

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

    private void selecionarTodos() {
        if (tblDocumento.getRowCount() > 0) {
            if (chkSelecionarTodos.isSelected()) {
                tblDocumento.setRowSelectionInterval(0, tblDocumento.getRowCount() - 1);
            } else {
                tblDocumento.clearSelection();
            }
            exibirTotalSelecionados();
        }
    }

    private void exibirDocumentosAgrupados() {
        new DocumentosAgrupadosView(documento);
    }

    private void exibirTotalSelecionados() {
        List<Venda> totalSelecionados = new ArrayList<>();

        for (int rowIndex : tblDocumento.getSelectedRows()) {
            totalSelecionados.add(documentoSaidaPesquisaJTableModel.getRow(rowIndex));
        }

        if (totalSelecionados.isEmpty()) {
            txtSelecionados.setText("Nenhum");

        } else {
            Currency currency = Currency.getInstance(new Locale("pt", "BR"));

            String simboloMonetario = currency.getSymbol();

            txtSelecionados.setText(totalSelecionados.size() + " (" + simboloMonetario + Decimal.toString(totalSelecionados.stream().map(Venda::getTotal).reduce(BigDecimal::add).get()) + ")");
        }
    }

    private void confirmar() {
        for (int rowIndex : tblDocumento.getSelectedRows()) {
            documentosSelecionados.add(documentoSaidaPesquisaJTableModel.getRow(rowIndex));
        }

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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblDocumento = new javax.swing.JTable();
        lblMensagem = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtDataInicial = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtDataFinal = new javax.swing.JFormattedTextField();
        txtCliente = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btnFiltrar = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        txtSelecionados = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnAgrupados = new javax.swing.JButton();
        chkSelecionarTodos = new javax.swing.JCheckBox();

        setTitle("Pesquisar Documentos");

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

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Data Inicial");

        txtDataInicial.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDataInicial.setName("data"); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Data Final");

        txtDataFinal.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDataFinal.setName("data"); // NOI18N

        txtCliente.setEditable(false);
        txtCliente.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtCliente.setText("TODOS");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Cliente");

        btnFiltrar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnOk.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnOk.setText("OK");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        btnCancelar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        txtSelecionados.setEditable(false);
        txtSelecionados.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtSelecionados.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Selecionados");

        jLabel3.setForeground(java.awt.Color.blue);
        jLabel3.setText("*Apenas documentos sem recebimento ou faturamento podem ser agrupados. Os documentos devem ser todos do mesmo cliente. Notas Fiscais não podem ser agrupadas.");

        btnAgrupados.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnAgrupados.setText("Consultar documentos já agrupados");
        btnAgrupados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgrupadosActionPerformed(evt);
            }
        });

        chkSelecionarTodos.setText("Selecionar Todos");
        chkSelecionarTodos.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        chkSelecionarTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSelecionarTodosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAgrupados)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 268, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(txtSelecionados, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(txtCliente))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(lblMensagem))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chkSelecionarTodos)))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(jLabel5)
                        .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkSelecionarTodos)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSelecionados)
                    .addComponent(jLabel7)
                    .addComponent(btnAgrupados, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblDocumentoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDocumentoMouseClicked
        exibirTotalSelecionados();

    }//GEN-LAST:event_tblDocumentoMouseClicked

    private void tblDocumentoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblDocumentoFocusGained

    }//GEN-LAST:event_tblDocumentoFocusGained

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        confirmar();
    }//GEN-LAST:event_btnOkActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void tblDocumentoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblDocumentoKeyReleased
        exibirTotalSelecionados();
    }//GEN-LAST:event_tblDocumentoKeyReleased

    private void btnAgrupadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgrupadosActionPerformed
        if (documento.getDocumentosFilho().isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem documentos agrupados", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            exibirDocumentosAgrupados();
        }
    }//GEN-LAST:event_btnAgrupadosActionPerformed

    private void chkSelecionarTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSelecionarTodosActionPerformed
        selecionarTodos();
    }//GEN-LAST:event_chkSelecionarTodosActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgrupados;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnOk;
    private javax.swing.JCheckBox chkSelecionarTodos;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMensagem;
    private javax.swing.JTable tblDocumento;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JFormattedTextField txtDataFinal;
    private javax.swing.JFormattedTextField txtDataInicial;
    private javax.swing.JTextField txtSelecionados;
    // End of variables declaration//GEN-END:variables

}
