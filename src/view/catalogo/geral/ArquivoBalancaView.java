/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.catalogo.geral;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.dao.principal.catalogo.ProdutoDAO;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.Decimal;
import util.JSwing;
import util.MwIOFile;
import util.Texto;

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

        cboTipo.addItem("Toledo MGV6 - arquivo: itensmgv.txt");
        cboTipo.addItem("Toledo MGV5 - arquivo: itensmgv.txt");

        this.setLocationRelativeTo(this);
        this.setVisible(true);

    }

    private void gerar() {

        switch (cboTipo.getSelectedIndex()) {
            case 0:
                gerarMGV6();
                break;
            case 1:
                gerarMGV5();
                break;
        }

        JOptionPane.showMessageDialog(MAIN_VIEW, "Arquivo gerado", "Arquivo gerado", JOptionPane.INFORMATION_MESSAGE);

        dispose();

    }

    private void gerarMGV5() {
        //DD(2)T(1)CCCCCC(6)PPPPPP(6)VVV(3)D1(25)D2(25)RRRRRR(6)FFFF(4)IIIIII(6)DV(1)DE(1)CF(4)L(12)G(11)Z(1)CS(4)CT(4)FR(4)CE1(4)CE2(4)CON(4)EAN(12)GL(6)|DA|D3(35)D4(35) CE3(6)CE4(6)MIDIA(6) (+CR+LF) 
        //DD(2)T(1)CCCCCC(6)PPPPPP(6)VVV(3)D1(25)D2(25)RRRRRR(6)FFF (3)IIII  (4)DV(1)DE(1)CF (4)L(12)G(11)Z(1)R(2) 

        List<String> linhas = new ArrayList<>();
        System.out.println("Exportar para balança...");
        //for (Produto produto : produtoDAO.findByCriteria(null, null, null, null, null, true, null, null, Optional.of(false))) {
        for (Produto produto : produtoDAO.findItensDeBalanca()){
            System.out.println("item balança: " + produto.getNome());

            String tipo = produto.getUnidadeComercialVenda().getId() == 35 ? "0" : "1";

            String item = "01"
                    + //codigoDepartamento
                    tipo
                    + //tipo de produto 0 - venda por peso
                    //" ## " +
                    String.format("%06d", produto.getId())
                    + //codigoItem pad 6
                    //" ## " +
                    String.format("%06d", Integer.parseInt(Texto.soNumeros(Decimal.toString(produto.getValorVenda()))))
                    + //preco pad 6 bytes
                    "000"
                    + //diasValidade pad 3
                    Texto.padRight(Texto.substring(produto.getNome(), 0, 25), 25)
                    + //descritivoPrimeiraLinha pad 25
                    Texto.padRight(Texto.substring(produto.getNome(), 25, 50), 25)
                    + //descritivoSegundaLinha pad 25
                    "000000"
                    + //codigoInformacaoExtra
                    "000"
                    + //codigoImagem
                    "0000"
                    + //codigoInformacaoNutricional
                    "0"
                    + //impressaoDataValidade 0 - Não imprime
                    "0"
                    + //impressaoDataEmbalagem 0 - Não imprime
                    "0000"
                    + //codigoFornecedor
                    "000000000000"
                    + //lote pad 12
                    "00000000000"
                    + //codigoEAN pad 11
                    "0"
                    + //versaoPreco
                    "00"; //Bytes reservados

            item = Texto.removerAcentos(item);
            linhas.add(item);

        }

        String caminho = "balanca//itensmgv.txt";

        MwIOFile.writeFile(linhas, caminho);

    }

    private void gerarMGV6() {
        //PADRÃO MGV6 - NÃO FOI TESTADO
        //DD(2)T(1)CCCCCC(6)PPPPPP(6)VVV(3)D1(25)D2(25)RRRRRR(6)FFFF(4)IIIIII(6)DV(1)DE(1)CF(4)L(12)G(11)Z(1)CS(4)CT(4)FR(4)CE1(4)CE2(4)CON(4)EAN(12)GL(6)|DA|D3(35)D4(35) CE3(6)CE4(6)MIDIA(6) (+CR+LF) 

        List<String> linhas = new ArrayList<>();

        //for (Produto produto : produtoDAO.findByCriteria(null, null, null, null, null, true, null, null, Optional.of(false))) {
        for (Produto produto : produtoDAO.findItensDeBalanca()) {

            String tipo = produto.getUnidadeComercialVenda().getId() == 35 ? "0" : "1";

            String item = "01"
                    + //codigoDepartamento
                    tipo
                    + //tipo de produto 0 - venda por peso
                    //" ## " +
                    String.format("%06d", produto.getId())
                    + //codigoItem pad 6
                    //" ## " +
                    String.format("%06d", Integer.parseInt(Texto.soNumeros(Decimal.toString(produto.getValorVenda()))))
                    + //preco pad 6 bytes
                    String.format("%03d", produto.getDiasValidade())
                    + //diasValidade pad 3
                    Texto.padRight(Texto.substring(produto.getNome(), 0, 25), 25)
                    + //descritivoPrimeiraLinha pad 25
                    Texto.padRight(Texto.substring(produto.getNome(), 25, 50), 25)
                    + //descritivoSegundaLinha pad 25
                    "000000"
                    + //codigoInformacaoExtra
                    "0000"
                    + //codigoImagem
                    "000000"
                    + //codigoInformacaoNutricional
                    "1"
                    + //impressaoDataValidade 0 - Não imprime
                    "1"
                    + //impressaoDataEmbalagem 0 - Não imprime
                    "0001"
                    + //codigoFornecedor
                    "000000000000"
                    + //lote pad 12
                    "00000000000"
                    + //codigoEAN pad 11
                    "0"
                    + //versaoPreco
                    "0000"
                    + //codigoSom
                    "0000"
                    + //codigoTaraPreDeterminada
                    "0000"
                    + //codigoFracionador
                    "0000"
                    + //codigoCampoExtra1
                    "0000"
                    + //codigoCampoExtra2
                    "0000"
                    + //codigoConservacao
                    "000000000000"
                    + //ean13Fornecedor 12 bytes
                    "000000"
                    + //percentualGlaciamento
                    "|00|"
                    + //sequenciaDepartamentosAssociados 2 bytes por departamento
                    Texto.padRight(Texto.substring(produto.getNome(), 50, 85), 35)
                    + //descritivoTerceiraLinha 35 bytes
                    Texto.padRight(Texto.substring(produto.getNome(), 85, 105), 35)
                    + //descritivoQuartaLinha 35 byte
                    "000000"
                    + //codigoCampoExtra3 6 bytes
                    "000000"
                    + //codigoCampoExtra4 6 bytes
                    "000000"
                    + //codigoMidia Prix 6 Touch
                    "000000"
                    + //precoPromocional
                    "0"
                    + //solicitaFornecedor
                    "|0001|"
                    + //codigoFornecedorAssociado
                    "0"
                    + //solicitaTara
                    "|00|"; //sequenciaBalancasItemNaoAtivo

            linhas.add(Texto.removerAcentos(item));

        }

        String caminho = "balanca//itensmgv.txt";

        MwIOFile.writeFile(linhas, caminho);

    }

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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cboTipo = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Gerar arquivo para Balança");
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

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("O arquivo é gerado na pasta \"balanca\"");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setForeground(java.awt.Color.red);
        jLabel2.setText("O programa MGV5 deve ter cadastrado um departamento com código 1");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setForeground(java.awt.Color.red);
        jLabel4.setText("Apenas os produtos marcados como item de balança são exportados");

        cboTipo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setForeground(java.awt.Color.red);
        jLabel3.setText("O programa MGV6 deve ter cadastrado um departamento e um fornecedor com código 1");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Após gerado o arquivo importe no programa da balança");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setForeground(java.awt.Color.red);
        jLabel6.setText("Produtos com unidade de medida diferente de KG são exportados como Unitário");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cboTipo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
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
        gerar();
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
    private javax.swing.JComboBox<String> cboTipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    // End of variables declaration//GEN-END:variables
}
