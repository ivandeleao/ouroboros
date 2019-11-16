/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.nfe;

import br.com.swconsultoria.nfe.Nfe;
import br.com.swconsultoria.nfe.dom.ConfiguracoesNfe;
import br.com.swconsultoria.nfe.dom.enuns.DocumentoEnum;
import br.com.swconsultoria.nfe.dom.enuns.StatusEnum;
import br.com.swconsultoria.nfe.exception.NfeException;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TEnviNFe;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TRetEnviNFe;
import br.com.swconsultoria.nfe.util.XmlNfeUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.principal.VendaDAO;
import nfe.NfeConfig;
import nfe.MontarXml;
import org.w3c.dom.Document;
import static ouroboros.Ouroboros.FROM_SAT_PATH;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.NFE_PATH;
import util.DateTime;
import util.MwIOFile;
import util.MwXML;

/**
 *
 * @author ivand
 *
 * ESTE FOI CRIADO COMO JDIALOG DESDE O INÍCIO - USAR ESTE COMO REFERÊNCIA PARA
 * OS DEMAIS
 *
 *
 */
public class NfeEmitirView extends javax.swing.JDialog {

    Venda documento;
    
    /**
     * Creates new form ParcelamentoView
     */
    private NfeEmitirView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
    }

    public NfeEmitirView(Venda documento) {
        super(MAIN_VIEW, true);
        initComponents();

        this.documento = documento;
        
        this.setLocationRelativeTo(this); //centralizar
        this.setVisible(true);
        
    }
    

    private void gerar() {
        
        try {

            ConfiguracoesNfe configNfe = NfeConfig.iniciarConfiguracoes();
            
            TEnviNFe enviNFe = MontarXml.montarEnviNfe(documento);

            // Envia a Nfe para a Sefaz
            TRetEnviNFe retorno = Nfe.enviarNfe(configNfe, enviNFe, DocumentoEnum.NFE);

            if (!retorno.getCStat().equals(StatusEnum.LOTE_RECEBIDO.getCodigo())) {
                String strRetorno = "Status:" + retorno.getCStat() + " - Motivo:" + retorno.getXMotivo() + "\n";
                txtRetorno.append(strRetorno);
                throw new NfeException(strRetorno);
            }

            String recibo = retorno.getInfRec().getNRec();

            br.com.swconsultoria.nfe.schema_4.retConsReciNFe.TRetConsReciNFe retornoNfe;
            while (true) {
                retornoNfe = Nfe.consultaRecibo(configNfe, recibo, DocumentoEnum.NFE);
                if (retornoNfe.getCStat().equals(StatusEnum.LOTE_EM_PROCESSAMENTO.getCodigo())) {
                    String strRetorno = "Status:" + retorno.getCStat() + " - Motivo:" + retorno.getXMotivo() + "\n";
                    txtRetorno.append(strRetorno);
                    txtRetorno.append("Lote Em Processamento, tentará novamente após 2 segundos.\n");
                    
                    Thread.sleep(2000);
                    continue;
                } else {
                    break;
                }
            }

            if (!retornoNfe.getCStat().equals(StatusEnum.LOTE_PROCESSADO.getCodigo())) {
                String strRetorno = "Status:" + retorno.getCStat() + " - Motivo:" + retorno.getXMotivo() + "\n";
                txtRetorno.append(strRetorno);
                throw new NfeException(strRetorno);
            }
            if (!retornoNfe.getProtNFe().get(0).getInfProt().getCStat().equals(StatusEnum.AUTORIZADO.getCodigo())) {
                String strRetorno = "Status:" + retornoNfe.getProtNFe().get(0).getInfProt().getCStat() + " - " + retornoNfe.getProtNFe().get(0).getInfProt().getXMotivo() + "\n";
                txtRetorno.append(strRetorno);
                throw new NfeException(strRetorno);
            }

            txtRetorno.append("Status: " + retornoNfe.getProtNFe().get(0).getInfProt().getCStat() + " - " + retornoNfe.getProtNFe().get(0).getInfProt().getXMotivo() + "\n");
            
            txtRetorno.append("Data: " + retornoNfe.getProtNFe().get(0).getInfProt().getDhRecbto() + "\n");
            txtRetorno.append("Protocolo: " + retornoNfe.getProtNFe().get(0).getInfProt().getNProt() + "\n");
            

            String xmlFinal = XmlNfeUtil.criaNfeProc(enviNFe, retornoNfe.getProtNFe().get(0));
            
            Document doc = MwXML.convertStringToDocument(xmlFinal);
            String chaveDeAcesso = MwXML.getValue(doc, "chNFe");
            String protocolo = MwXML.getValue(doc, "nProt");
            
            documento.setChaveAcessoNfe(chaveDeAcesso);
            documento.setProtocoloNfe(protocolo);
            documento.setNumeroNfe(Integer.valueOf(MwXML.getValue(doc, "nNF")));
            documento.setSerieNfe(Integer.valueOf(MwXML.getValue(doc, "serie")));
            documento.setDataHoraEmissaoNfe(DateTime.fromStringToLDTOffsetZone(MwXML.getValue(doc, "dhEmi")));
            
            documento = new VendaDAO().save(documento);
            
            //Salvar xml
            String xmlFileName = chaveDeAcesso + "-nfe.xml";
            String pathXmlFile = NFE_PATH + "/enviados/" + xmlFileName;
            MwIOFile.writeFile(xmlFinal, pathXmlFile);
            
            JOptionPane.showMessageDialog(MAIN_VIEW, "Nota Fiscal emitida", "Nota Fiscal emitida", JOptionPane.INFORMATION_MESSAGE);
            
            dispose();

        } catch (NfeException | InterruptedException e) {
            System.err.println("Erro aqui " + e);

        } catch (JAXBException ex) {
            Logger.getLogger(NfeEmitirView.class.getName()).log(Level.SEVERE, null, ex);
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

        btnGerar = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtRetorno = new javax.swing.JTextArea();
        jLabel35 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Nota Fiscal Eletrônica");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        btnGerar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnGerar.setText("Fechar");
        btnGerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGerarActionPerformed(evt);
            }
        });

        txtRetorno.setEditable(false);
        txtRetorno.setColumns(20);
        txtRetorno.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtRetorno.setLineWrap(true);
        txtRetorno.setRows(5);
        txtRetorno.setText("Aguarde...\n");
        txtRetorno.setMargin(new java.awt.Insets(4, 4, 4, 4));
        txtRetorno.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRetornoFocusLost(evt);
            }
        });
        txtRetorno.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtRetornoPropertyChange(evt);
            }
        });
        jScrollPane4.setViewportView(txtRetorno);

        jLabel35.setBackground(new java.awt.Color(122, 138, 153));
        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel35.setForeground(java.awt.Color.white);
        jLabel35.setText("Mensagens de Retorno");
        jLabel35.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        jLabel35.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 754, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnGerar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                .addGap(32, 32, 32)
                .addComponent(btnGerar)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGerarActionPerformed
        dispose();
    }//GEN-LAST:event_btnGerarActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    }//GEN-LAST:event_formWindowClosing

    private void txtRetornoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRetornoFocusLost
    }//GEN-LAST:event_txtRetornoFocusLost

    private void txtRetornoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtRetornoPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRetornoPropertyChange

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        gerar();
    }//GEN-LAST:event_formWindowOpened

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
            java.util.logging.Logger.getLogger(NfeEmitirView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NfeEmitirView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NfeEmitirView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NfeEmitirView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NfeEmitirView dialog = new NfeEmitirView(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnGerar;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea txtRetorno;
    // End of variables declaration//GEN-END:variables
}
