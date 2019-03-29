/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.sat;

import com.itextpdf.text.BadElementException;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.xml.bind.DatatypeConverter;
import model.bean.fiscal.SatCupom;
import model.bean.fiscal.SatCupomTipo;
import model.bean.principal.Venda;
import model.dao.fiscal.SatCupomDAO;
import model.dao.fiscal.SatErroOuAlertaDAO;
import model.dao.principal.VendaDAO;
import model.jtable.SatCupomJTableModel;
import org.w3c.dom.Document;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.FROM_SAT_PATH;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.SAT_CODIGO_ATIVACAO;
import static ouroboros.Ouroboros.SAT_PRINTER;
import static ouroboros.Ouroboros.TO_PRINTER_PATH;
import static ouroboros.Ouroboros.TO_SAT_PATH;
import printing.PrintPDFBox;
import sat.MwSat;
import sat.SAT;
import util.JSwing;
import util.MwIOFile;
import util.MwString;
import util.MwXML;
import view.Toast;

/**
 *
 * @author ivand
 */
public class SatEmitirCupomView extends javax.swing.JDialog {
    private Venda venda;
    SatCupomJTableModel satCupomJTableModel = new SatCupomJTableModel();
    
    /**
     * Creates new form SatInformarCpfView
     */
    private SatEmitirCupomView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public SatEmitirCupomView(Venda venda) {
        super(MAIN_VIEW, true);
        initComponents();
        JSwing.startComponentsBehavior(this);
        txtCpfCnpj.setHorizontalAlignment(JTextField.CENTER);
        definirAtalhos();
        
        this.venda = venda;
        
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
        am.put("fechar", new SatEmitirCupomView.FormKeyStroke("ESC"));
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "confirmar");
        am.put("confirmar", new SatEmitirCupomView.FormKeyStroke("F12"));
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
                case "F12":
                    confirmar();
                    break;
            }
        }
    }
    
    private void formatarTabela() {
        tblCupons.setModel(satCupomJTableModel);
        
        tblCupons.setRowHeight(24);
        tblCupons.setIntercellSpacing(new Dimension(10, 10));
        
        tblCupons.getColumn("Id").setPreferredWidth(60);
        tblCupons.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblCupons.getColumn("Data").setPreferredWidth(100);
        tblCupons.getColumn("Data").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblCupons.getColumn("Chave").setPreferredWidth(340);
        
        tblCupons.getColumn("Tipo").setPreferredWidth(100);
    }
    
    private void carregarTabela() {
        satCupomJTableModel.clear();
        satCupomJTableModel.addList(venda.getSatCupons());
    }
    
    public Venda getVenda(){
        return venda;
    }
    
    private void confirmar() {
        String destCpfCnpj = MwString.soNumeros( txtCpfCnpj.getText() );
        
        //validar cpfCnpj - TODO: validar de vdd ;) 
        if(destCpfCnpj.length()!= 0 && destCpfCnpj.length() != 11 && destCpfCnpj.length() != 14) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "CPF ou CNPJ inválido", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtCpfCnpj.requestFocus();
            
        } else {

            venda.setDestCpfCnpj(destCpfCnpj);

            Document doc = MwSat.prepareDocument(venda);

            String docString = MwXML.convertDocumentToString(doc) ;
            docString = MwString.removeAccents(docString);

            System.out.println("doc: " + docString);
            doc = MwXML.convertStringToDocument(docString);

            int numeroSessao = new Random().nextInt(999999);

            String toSatXmlFile = TO_SAT_PATH + numeroSessao + ".xml";
            MwXML.createFile(doc, toSatXmlFile);

            new Toast("Gerando Cupom SAT...");

            File file = new File(toSatXmlFile);

            String cf_e = MwIOFile.readFullContent(toSatXmlFile);

            String ret = SAT.INSTANCE.EnviarDadosVenda(numeroSessao, SAT_CODIGO_ATIVACAO, cf_e);
            String[] reto = ret.split(Pattern.quote("|"));
            //mainView.detalheSetText(ret);
            //mainView.toSatAppendText("Resposta do SAT: " + reto[3]);

            txtRetorno.setText(ret);
            txtRetorno.append("Resposta do SAT: " + reto[3]);

            String returnCode = reto[1];
            String errorCode = reto[2];
            //JOptionPane.showMessageDialog(MAIN_VIEW, errorCode);
            if(!returnCode.equals("06000") || !errorCode.equals("0000")){
                //On error
                SatErroOuAlertaDAO eaDao = new SatErroOuAlertaDAO();
                String descricao = eaDao.findByCodigo(errorCode).getDescricao();
                String errorMessage = "Erro: " + errorCode + " " + descricao;

                txtRetorno.append(errorMessage);

                //move fileFromApp to processed subfolder
                //mainView.toSatAppendText("Movendo arquivo com erro para pasta rejeitados...");
                file.renameTo(new File(TO_SAT_PATH + "/rejeitados/" + file.getName()));
                //mainView.toSatAppendText("Pronto");
                //mainView.toSatAppendText("");

            }else{
                //Accepted
                /*
                String a = reto[6]; // string "a" recebe vetor com a base64
                byte[] byteArray = Base64.decode(a);// byteArray descodifica "a" 
                String decodedString = new String(byteArray);// String decodedString recebe byteArray.
                */

                String base64String = reto[6]; // string "a" recebe vetor com a base64
                byte[] byteArray = DatatypeConverter.parseBase64Binary(base64String);
                String decodedString = new String(byteArray);

                //save returned CFe
                List<String> lines = new ArrayList<>();
                lines.add(decodedString);

                //Chave de acesso = chave consulta =(
                doc = MwXML.convertStringToDocument(decodedString);
                String chaveDeAcesso = MwXML.getAttributeValue(doc, "infCFe", "Id").substring(3);

                //2019-03-23 registrar cupom emitido
                SatCupom cupom = new SatCupom(chaveDeAcesso, venda, SatCupomTipo.EMISSAO);
                cupom = new SatCupomDAO().save(cupom);
                venda.addSatCupom(cupom);
                venda = new VendaDAO().save(venda);


                String anoMes = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

                //mainView.toSatAppendText("Salvando arquivo CFe de retorno");
                String xmlFileFromSat = "AD" + chaveDeAcesso + ".xml";
                String pathXmlFileFromSat = FROM_SAT_PATH + xmlFileFromSat;
                MwIOFile.writeFile(lines, pathXmlFileFromSat);
                //mainView.detalheSetText(decodedString);

                //move fileFromApp to processed subfolder
                //mainView.toSatAppendText("Movendo arquivo para pasta processados...");
                file.renameTo(new File(TO_SAT_PATH + "/processados/" + file.getName()));
                //mainView.toSatAppendText("Pronto");
                //mainView.toSatAppendText("");

                //toolBar.setStateToIdle();



                //PREPARAR PDF PARA IMPRESSÃO --------------------------------------
                //receive xml from Sat
                //mainView.fromSatAppendText("Gerando cupom...");
                String pdfFileToPrint = xmlFileFromSat.substring(0, xmlFileFromSat.length()-3) + "pdf";
                try {
                    MwSat.gerarCupom(FROM_SAT_PATH + xmlFileFromSat, TO_PRINTER_PATH + pdfFileToPrint);
                } catch (BadElementException | IOException e) {
                    JOptionPane.showMessageDialog(rootPane, e, "Erro", JOptionPane.ERROR_MESSAGE);
                }

                //mainView.fromSatAppendText("Enviando cupom para impressora...");
                PrintPDFBox pPDF = new PrintPDFBox();
                System.out.println("SAT_PRINTER: " + SAT_PRINTER);
                System.out.println("pdfFileToPrint: " + TO_PRINTER_PATH + pdfFileToPrint);
                pPDF.print(TO_PRINTER_PATH + pdfFileToPrint, SAT_PRINTER);


                //Mover arquivo processado

                String pathYearMonth = FROM_SAT_PATH + "/processados/" + anoMes + "/";
                new File(pathYearMonth).mkdir();
                File fileXmlFromSat = new File(pathXmlFileFromSat);
                fileXmlFromSat.renameTo(new File(pathYearMonth + xmlFileFromSat));

                dispose();
            }
        }
    }
    
    private void cancelarCupom() {
        if(tblCupons.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione um cupom para cancelar", "Atenção", JOptionPane.WARNING_MESSAGE);
            
        } else {
            SatCupom satCupom = satCupomJTableModel.getRow( tblCupons.getSelectedRow() );
            
            if(satCupom.getSatCupomTipo().equals(SatCupomTipo.CANCELAMENTO)) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Atenção", "Selecione um cupom de emissão", JOptionPane.WARNING_MESSAGE);
                
            } else {
                SATCancelarUltimoCupom satCancelar = new SATCancelarUltimoCupom(satCupom.getChave());
                
            }
            
        }
    }
    
    private void reimprimirCupom() {
        if(tblCupons.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione um cupom para reimprimir", "Atenção", JOptionPane.WARNING_MESSAGE);
            
        } else {
            SatCupom satCupom = satCupomJTableModel.getRow( tblCupons.getSelectedRow() );

            String pdfFileToPrint = satCupom.getChave();
            if(satCupom.getSatCupomTipo().equals(SatCupomTipo.EMISSAO)) {
                pdfFileToPrint = "AD" + pdfFileToPrint + ".pdf";
            } else {
                pdfFileToPrint = "ADC" + pdfFileToPrint + ".pdf";
            }
            System.out.println("pdfFileToPrint: " + TO_PRINTER_PATH + pdfFileToPrint);

            PrintPDFBox.print(TO_PRINTER_PATH + pdfFileToPrint, SAT_PRINTER);
            
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

        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblCupons = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        btnReimprimir = new javax.swing.JButton();
        btnCancelarCupom = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtRetorno = new javax.swing.JTextArea();
        txtCpfCnpj = new javax.swing.JFormattedTextField();
        btnConfirmar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cupom Sat");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tblCupons.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tblCupons);

        jLabel2.setBackground(java.awt.Color.darkGray);
        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setForeground(java.awt.Color.white);
        jLabel2.setText("Cupons já emitidos para este documento");
        jLabel2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel2.setOpaque(true);

        btnReimprimir.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnReimprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/printer.png"))); // NOI18N
        btnReimprimir.setText("Reimprimir");
        btnReimprimir.setContentAreaFilled(false);
        btnReimprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReimprimirActionPerformed(evt);
            }
        });

        btnCancelarCupom.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnCancelarCupom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/cancel.png"))); // NOI18N
        btnCancelarCupom.setText("Cancelar cupom");
        btnCancelarCupom.setContentAreaFilled(false);
        btnCancelarCupom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarCupomActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setForeground(java.awt.Color.red);
        jLabel6.setText("Cancelamento só pode ser realizado até 30 minutos após emissão do cupom");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnCancelarCupom)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnReimprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(108, 108, 108)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnReimprimir)
                    .addComponent(btnCancelarCupom))
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtRetorno.setEditable(false);
        txtRetorno.setBackground(javax.swing.UIManager.getDefaults().getColor("TextPane.disabledBackground"));
        txtRetorno.setColumns(20);
        txtRetorno.setLineWrap(true);
        txtRetorno.setRows(5);
        txtRetorno.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jScrollPane1.setViewportView(txtRetorno);

        txtCpfCnpj.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCpfCnpj.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtCpfCnpj.setName("cpfCnpj"); // NOI18N

        btnConfirmar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnConfirmar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/flag_green.png"))); // NOI18N
        btnConfirmar.setText("F12 Confirmar");
        btnConfirmar.setContentAreaFilled(false);
        btnConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarActionPerformed(evt);
            }
        });

        jLabel1.setText("Retorno SAT");

        jLabel3.setBackground(java.awt.Color.darkGray);
        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setForeground(java.awt.Color.white);
        jLabel3.setText("Emitir novo cupom SAT");
        jLabel3.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel3.setOpaque(true);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Informe o CPF ou CNPJ (opcional)");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(txtCpfCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnConfirmar))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCpfCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConfirmar)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        confirmar();
    }//GEN-LAST:event_btnConfirmarActionPerformed

    private void btnReimprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReimprimirActionPerformed
        reimprimirCupom();
    }//GEN-LAST:event_btnReimprimirActionPerformed

    private void btnCancelarCupomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarCupomActionPerformed
        cancelarCupom();
    }//GEN-LAST:event_btnCancelarCupomActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SatEmitirCupomView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SatEmitirCupomView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SatEmitirCupomView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SatEmitirCupomView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SatEmitirCupomView dialog = new SatEmitirCupomView(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelarCupom;
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnReimprimir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable tblCupons;
    private javax.swing.JFormattedTextField txtCpfCnpj;
    private javax.swing.JTextArea txtRetorno;
    // End of variables declaration//GEN-END:variables
}
