/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.sat;

import java.awt.HeadlessException;
import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.xml.bind.DatatypeConverter;
import org.w3c.dom.Document;
import static ouroboros.Ouroboros.FROM_SAT_PATH;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.SAT_CODIGO_ATIVACAO;
import static ouroboros.Ouroboros.SAT_PRINTER;
import static ouroboros.Ouroboros.SAT_SIGN_AC;
import static ouroboros.Ouroboros.TO_PRINTER_PATH;
import printing.PrintPDFBox;
import sat.MwSat;
import sat.SAT;
import util.MwIOFile;
import util.MwXML;

/**
 *
 * @author ivand
 */
public class SATCancelarUltimoCupom extends javax.swing.JDialog {

    /**
     * Creates new form SATCancelarUltimoCupom
     */
    private SATCancelarUltimoCupom(java.awt.Frame parent, boolean modal){
        super(parent, modal);
        initComponents();
    }
    
    public SATCancelarUltimoCupom(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        buttonConfirmar = new javax.swing.JButton();
        textUltimoCFe = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        textRetorno = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtOutro = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cancelar Último Cupom");
        setResizable(false);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel1.setText("Último cupom:");

        buttonConfirmar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        buttonConfirmar.setForeground(javax.swing.UIManager.getDefaults().getColor("nb.errorForeground"));
        buttonConfirmar.setText("Confirmar cancelamento");
        buttonConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonConfirmarActionPerformed(evt);
            }
        });

        textUltimoCFe.setText("Consultando SAT...");

        textRetorno.setColumns(20);
        textRetorno.setRows(5);
        jScrollPane1.setViewportView(textRetorno);

        txtOutro.setColumns(20);
        txtOutro.setRows(5);
        jScrollPane2.setViewportView(txtOutro);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonConfirmar, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                    .addComponent(textUltimoCFe)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(3, 3, 3)
                .addComponent(textUltimoCFe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonConfirmar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        textUltimoCFe.setText(obterUltimoCFe());
    }//GEN-LAST:event_formComponentShown

    private void buttonConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonConfirmarActionPerformed
        try {
            Random gerador = new Random();
        
            
            int sessao = gerador.nextInt(999999);
            String chave = "CFe" + textUltimoCFe.getText();

            
            String xmlCancelamento = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<CFeCanc>\n" +
            "<infCFe chCanc=\""+chave+"\">\n" +
            "<ide>\n" +
            "<CNPJ>04615918000104</CNPJ>\n" +
            "<signAC>"+SAT_SIGN_AC+"</signAC>\n" +
            "<numeroCaixa>001</numeroCaixa>\n" +
            "</ide>\n" +
            "<emit/>\n" +
            "<dest/>\n" +
            "<total/>\n" +
            "<infAdic/>\n" +
            "</infCFe>\n" +
            "</CFeCanc>";
            
            System.out.println("XML Cancelamento:");
            System.out.println(xmlCancelamento);


            String retorno = SAT.INSTANCE.CancelarUltimaVenda(sessao, SAT_CODIGO_ATIVACAO, chave, xmlCancelamento);
            
            txtOutro.setText(retorno);
            
            
            String[] retornoSplit = retorno.split(Pattern.quote("|"));
            JOptionPane.showMessageDialog(MAIN_VIEW, retornoSplit[3]);

            
            
            String base64String = retornoSplit[6];
            byte[] byteArray = DatatypeConverter.parseBase64Binary(base64String);
            String decodedString = new String(byteArray);

            textRetorno.setText(decodedString);

            //save returned CFe
            ArrayList<String> lines = new ArrayList<>();
            lines.add(decodedString);

            //Chave de acesso = chave consulta =(
            Document doc = MwXML.convertStringToDocument(decodedString);
            String chaveDeAcesso = MwXML.getAttributeValue(doc, "infCFe", "Id").substring(3);

            //check and create directory of current month
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String yearMonth = simpleDateFormat.format(timestamp) + "/";
            //mainView.fromSatAppendText("Criando arquivo XML de CANCELAMENTO na pasta processados/" + yearMonth);

            String pathYearMonth = FROM_SAT_PATH + "/processados/" + yearMonth;
            new File(pathYearMonth).mkdir();
            
            String xmlCancelFilePath = pathYearMonth + "ADC" + chaveDeAcesso + ".xml";
            MwIOFile.writeFile(lines, xmlCancelFilePath);

            //pdf to print
            String pdfCancelFilePath = TO_PRINTER_PATH + "ADC" + chaveDeAcesso + ".pdf";

            MwSat.createCancelCoupon80(xmlCancelFilePath, pdfCancelFilePath);
            
            Thread.sleep(1000);
            //print coupon
            //mainView.fromSatAppendText("Enviando cupom de CANCELAMENTO para impressora...");
            PrintPDFBox pPDF = new PrintPDFBox();
            pPDF.print(pdfCancelFilePath, SAT_PRINTER);
            
        } catch (HeadlessException | InterruptedException ex) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Erro ao cancelar cupom. " + ex, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_buttonConfirmarActionPerformed

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
            java.util.logging.Logger.getLogger(SATCancelarUltimoCupom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SATCancelarUltimoCupom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SATCancelarUltimoCupom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SATCancelarUltimoCupom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SATCancelarUltimoCupom dialog = new SATCancelarUltimoCupom(new javax.swing.JFrame(), true);
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
    
    private static String obterUltimoCFe(){
        Random gerador = new Random();
        
        
       
        int sessao = gerador.nextInt(999999);
        String retorno = SAT.INSTANCE.ConsultarStatusOperacional(sessao, SAT_CODIGO_ATIVACAO);
        
        String[] retornoSplit = retorno.split(Pattern.quote("|"));
        
        return retornoSplit[20];
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonConfirmar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea textRetorno;
    private javax.swing.JTextField textUltimoCFe;
    private javax.swing.JTextArea txtOutro;
    // End of variables declaration//GEN-END:variables
}
