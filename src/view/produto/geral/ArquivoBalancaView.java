/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.produto.geral;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.bean.principal.Produto;
import model.dao.principal.ProdutoDAO;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.Decimal;
import util.JSwing;
import util.MwIOFile;
import util.MwString;

/**
 *
 * @author ivand
 *
 * ESTE FOI CRIADO COMO JDIALOG DESDE O INÍCIO - USAR ESTE COMO REFERÊNCIA PARA
 * OS DEMAIS
 *
 *
 */
public class ArquivoBalancaView extends javax.swing.JDialog {

    ProdutoDAO produtoDAO = new ProdutoDAO();

    private ArquivoBalancaView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public ArquivoBalancaView() {
        super(MAIN_VIEW, true);
        initComponents();
        JSwing.startComponentsBehavior(this);

        this.setLocationRelativeTo(this);
        this.setVisible(true);
        
    }


    private void confirmar() {
        //PADRÃO MGV5
        //***MGV6 ESTÁ LOGO ABAIXO COMENTADO
        //DD(2)T(1)CCCCCC(6)PPPPPP(6)VVV(3)D1(25)D2(25)RRRRRR(6)FFFF(4)IIIIII(6)DV(1)DE(1)CF(4)L(12)G(11)Z(1)CS(4)CT(4)FR(4)CE1(4)CE2(4)CON(4)EAN(12)GL(6)|DA|D3(35)D4(35) CE3(6)CE4(6)MIDIA(6) (+CR+LF) 
        //DD(2)T(1)CCCCCC(6)PPPPPP(6)VVV(3)D1(25)D2(25)RRRRRR(6)FFF (3)IIII  (4)DV(1)DE(1)CF (4)L(12)G(11)Z(1)R(2) 
        
        List<String> linhas = new ArrayList<>();
        System.out.println("Exportar para balança...");
        for (Produto produto : produtoDAO.findByCriteria(null, null, true, false)) {
            System.out.println("item balança: " + produto.getNome());
            String item = "01" + //codigoDepartamento
            "0" + //tipo de produto 0 - venda por peso
                    //" ## " +
            String.format( "%06d", produto.getId()) + //codigoItem pad 6
                    //" ## " +
            String.format( "%06d", Integer.parseInt( MwString.soNumeros( Decimal.toString(produto.getValorVenda())) ) ) + //preco pad 6 bytes
            "000" + //diasValidade pad 3
            MwString.padRight( MwString.substring( produto.getNome(), 0, 25), 25 ) + //descritivoPrimeiraLinha pad 25
            MwString.padRight( MwString.substring( produto.getNome(), 25, 50), 25 ) + //descritivoSegundaLinha pad 25
            "000000" + //codigoInformacaoExtra
            "000" + //codigoImagem
            "0000" + //codigoInformacaoNutricional
            "0" + //impressaoDataValidade 0 - Não imprime
            "0" + //impressaoDataEmbalagem 0 - Não imprime
            "0000" + //codigoFornecedor
            "000000000000" + //lote pad 12
            "00000000000" + //codigoEAN pad 11
            "0" + //versaoPreco
            "00"; //Bytes reservados
            
            item = MwString.removeAccents(item);
            linhas.add(item);
            
        }

        String caminho = "balanca//itensmgv.txt";
        
        MwIOFile.writeFile(linhas, caminho);
        
        JOptionPane.showMessageDialog(MAIN_VIEW, "Arquivo gerado", "Arquivo gerado", JOptionPane.INFORMATION_MESSAGE);
        
        dispose();
    }
    
    /*
    private void confirmar() {
        PADRÃO MGV6 - NÃO FOI TESTADO
        //DD(2)T(1)CCCCCC(6)PPPPPP(6)VVV(3)D1(25)D2(25)RRRRRR(6)FFFF(4)IIIIII(6)DV(1)DE(1)CF(4)L(12)G(11)Z(1)CS(4)CT(4)FR(4)CE1(4)CE2(4)CON(4)EAN(12)GL(6)|DA|D3(35)D4(35) CE3(6)CE4(6)MIDIA(6) (+CR+LF) 
        
        List<String> linhas = new ArrayList<>();

        for (Produto produto : produtoDAO.findByCriteria(null, null, false)) {
            
            String item = "01" + //codigoDepartamento
            "0" + //tipo de produto 0 - venda por peso
                    //" ## " +
            String.format( "%06d", produto.getId()) + //codigoItem pad 6
                    //" ## " +
            String.format( "%06d", Integer.parseInt( MwString.soNumeros( Decimal.toString(produto.getValorVenda())) ) ) + //preco pad 6 bytes
            "000" + //diasValidade pad 3
            MwString.padRight( MwString.substring( produto.getNome(), 0, 25), 25 ) + //descritivoPrimeiraLinha pad 25
            MwString.padRight( MwString.substring( produto.getNome(), 25, 50), 25 ) + //descritivoSegundaLinha pad 25
            "000000" + //codigoInformacaoExtra
            "0000" + //codigoImagem
            "000000" + //codigoInformacaoNutricional
            "0" + //impressaoDataValidade 0 - Não imprime
            "0" + //impressaoDataEmbalagem 0 - Não imprime
            "0000" + //codigoFornecedor
            "000000000000" + //lote pad 12
            "00000000000" + //codigoEAN pad 11
            "0" + //versaoPreco
            "0000" + //codigoSom
            "0000" + //codigoTaraPreDeterminada
            "0000" + //codigoFracionador
            "0000" + //codigoCampoExtra1
            "0000" + //codigoCampoExtra2
            "0000" + //codigoConservacao
            "000000000000" + //ean13Fornecedor 12 bytes
            "000000" + //percentualGlaciamento
            "|00|" + //sequenciaDepartamentosAssociados 2 bytes por departamento
            MwString.padRight( MwString.substring( produto.getNome(), 50, 85), 35 ) + //descritivoTerceiraLinha 35 bytes
            MwString.padRight( MwString.substring( produto.getNome(), 85, 105), 35 ) + //descritivoQuartaLinha 35 byte
            "000000" + //codigoCampoExtra3 6 bytes
            "000000" + //codigoCampoExtra4 6 bytes
            "000000" + //codigoMidia Prix 6 Touch
            "000000" + //precoPromocional
            "0" + //solicitaFornecedor
            "|000000|" + //codigoFornecedorAssociado 6 bytes por fornecedor
            "0" + //solicitaTara
            "|00|"; //sequenciaBalancasItemNaoAtivo
            
            linhas.add(item);
            
        }

        String caminho = "itensmgv.txt";
        
        MwIOFile.writeFile(linhas, caminho);
        
        dispose();
    }*/

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnCancelar = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Confirmação de Entrega e Devolução");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        btnCancelar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnOk.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnOk.setText("Ok");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Gerar arquivo ITENMGV.TXT para Toledo MGV5");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("O arquivo é gerado na pasta \"balanca\"");

        jLabel2.setForeground(javax.swing.UIManager.getDefaults().getColor("nb.errorForeground"));
        jLabel2.setText("O programa MGV5 deve ter cadastrado um departamento com código 1");

        jLabel4.setForeground(javax.swing.UIManager.getDefaults().getColor("nb.errorForeground"));
        jLabel4.setText("Apenas os produtos marcados como item de balança são exportados");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(154, 154, 154)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk)
                    .addComponent(btnCancelar))
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleName("Confirmação de Entrega ou Devolução");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    }//GEN-LAST:event_formWindowClosing

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        confirmar();
    }//GEN-LAST:event_btnOkActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

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
            java.util.logging.Logger.getLogger(ArquivoBalancaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ArquivoBalancaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ArquivoBalancaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ArquivoBalancaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ArquivoBalancaView dialog = new ArquivoBalancaView(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnOk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    // End of variables declaration//GEN-END:variables
}
