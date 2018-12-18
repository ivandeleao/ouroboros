/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.sistema;

import java.awt.HeadlessException;
import java.awt.print.PrinterJob;
import java.math.BigDecimal;
import javax.print.PrintService;
import javax.swing.JOptionPane;
import model.bean.principal.Constante;
import model.bean.principal.Recurso;
import model.dao.principal.CaixaItemTipoDAO;
import model.dao.principal.CaixaPeriodoDAO;
import model.dao.principal.ConstanteDAO;
import model.dao.principal.MovimentoFisicoTipoDAO;
import model.dao.fiscal.CfopDAO;
import model.dao.fiscal.IbptDAO;
import model.dao.fiscal.IcmsDAO;
import model.dao.fiscal.MeioDePagamentoDAO;
import model.dao.fiscal.NcmDAO;
import model.dao.fiscal.ProdutoOrigemDAO;
import model.dao.fiscal.SatErroOuAlertaDAO;
import model.dao.fiscal.SatEstadoDAO;
import model.dao.fiscal.UnidadeComercialDAO;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.PARCELA_MULTA;
import static ouroboros.Ouroboros.IMPRESSORA_PADRAO;
import static ouroboros.Ouroboros.USUARIO;
import util.Decimal;
import util.JSwing;
import view.Toast;
import view.sat.SATSetup;
import view.sat.SATStatusOperacionalView;

/**
 *
 * @author ivand
 */
public class ConfguracaoSistema extends javax.swing.JInternalFrame {
    private static ConfguracaoSistema singleInstance = null;
    
    public static ConfguracaoSistema getSingleInstance(){
        if(!USUARIO.autorizarAcesso(Recurso.SISTEMA)) {
            return null;
        }
        
        if(singleInstance == null){
            singleInstance = new ConfguracaoSistema();
        }
        return singleInstance;
    }
    /**
     * Creates new form DadosEmpresa
     */
    public ConfguracaoSistema() {
        initComponents();
        JSwing.startComponentsBehavior(this);
        
        carregarDados();
    }
    
    private void carregarDados() {
        txtNomeFantasia.setText(Ouroboros.EMPRESA_NOME_FANTASIA);
        txtRazaoSocial.setText(Ouroboros.EMPRESA_RAZAO_SOCIAL);
        txtCNPJ.setText(Ouroboros.EMPRESA_CNPJ);
        txtIE.setText(Ouroboros.EMPRESA_IE);
        txtEndereco.setText(Ouroboros.EMPRESA_ENDERECO);
        
        //Venda
        chkInsercaoDireta.setSelected(Ouroboros.VENDA_INSERCAO_DIRETA);
        txtMulta.setText(Decimal.toString(PARCELA_MULTA));
        
        if(Ouroboros.PARCELA_JUROS_MONETARIO_MENSAL.compareTo(BigDecimal.ZERO) > 0) {
            cboJurosTipo.setSelectedIndex(1);
            txtJuros.setText(Decimal.toString(Ouroboros.PARCELA_JUROS_MONETARIO_MENSAL));
        } else {
            cboJurosTipo.setSelectedIndex(0);
            txtJuros.setText(Decimal.toString(Ouroboros.PARCELA_JUROS_PERCENTUAL_MENSAL));
        }
        
        txtNumeroComandas.setText(Ouroboros.VENDA_NUMERO_COMANDAS.toString());
        
        //Impressão
        cboImpressoras.addItem("Não definida");
        PrintService[] pservices = PrinterJob.lookupPrintServices();
        for (PrintService ps : pservices) {
            cboImpressoras.addItem(ps.getName());
            //System.out.println(ps.getName());
            if (ps.getName().equals(IMPRESSORA_PADRAO)) {
                cboImpressoras.setSelectedItem(ps.getName());
            }
        }
        
        chkDesativarImpressao.setSelected(Ouroboros.IMPRESSORA_DESATIVAR);
        
        //Sistema
        chkHabilitarSat.setSelected(Ouroboros.SAT_HABILITAR);
        
    }
    
    private void salvar(){
        //validar
        
        try{
            ConstanteDAO cDAO = new ConstanteDAO();
            
            //Empresa
            Ouroboros.EMPRESA_NOME_FANTASIA = txtNomeFantasia.getText();
            Ouroboros.EMPRESA_RAZAO_SOCIAL = txtRazaoSocial.getText();
            Ouroboros.EMPRESA_CNPJ = txtCNPJ.getText();
            Ouroboros.EMPRESA_IE = txtIE.getText();
            Ouroboros.EMPRESA_ENDERECO = txtEndereco.getText();
            
            cDAO.save(new Constante("EMPRESA_NOME_FANTASIA", Ouroboros.EMPRESA_NOME_FANTASIA));
            cDAO.save(new Constante("EMPRESA_RAZAO_SOCIAL", Ouroboros.EMPRESA_RAZAO_SOCIAL));
            cDAO.save(new Constante("EMPRESA_CNPJ", Ouroboros.EMPRESA_CNPJ));
            cDAO.save(new Constante("EMPRESA_IE", Ouroboros.EMPRESA_IE));
            cDAO.save(new Constante("EMPRESA_ENDERECO", Ouroboros.EMPRESA_ENDERECO));
            
            
            //Venda
            Ouroboros.VENDA_INSERCAO_DIRETA = chkInsercaoDireta.isSelected();
            Ouroboros.PARCELA_MULTA = Decimal.fromString(txtMulta.getText());
            
            BigDecimal juros = Decimal.fromString(txtJuros.getText());
            if(cboJurosTipo.getSelectedIndex() == 0) {
                Ouroboros.PARCELA_JUROS_MONETARIO_MENSAL = BigDecimal.ZERO;
                Ouroboros.PARCELA_JUROS_PERCENTUAL_MENSAL = juros;
            } else {
                Ouroboros.PARCELA_JUROS_MONETARIO_MENSAL = juros;
                Ouroboros.PARCELA_JUROS_PERCENTUAL_MENSAL = BigDecimal.ZERO;
            }
            
            Ouroboros.VENDA_NUMERO_COMANDAS = Integer.valueOf(txtNumeroComandas.getText());
            
            cDAO.save(new Constante("VENDA_INSERCAO_DIRETA", String.valueOf(Ouroboros.VENDA_INSERCAO_DIRETA)));
            cDAO.save(new Constante("PARCELA_MULTA", String.valueOf(Ouroboros.PARCELA_MULTA)));
            cDAO.save(new Constante("PARCELA_JUROS_MONETARIO_MENSAL", String.valueOf(Ouroboros.PARCELA_JUROS_MONETARIO_MENSAL)));
            cDAO.save(new Constante("PARCELA_JUROS_PERCENTUAL_MENSAL", String.valueOf(Ouroboros.PARCELA_JUROS_PERCENTUAL_MENSAL)));
            cDAO.save(new Constante("VENDA_NUMERO_COMANDAS", String.valueOf(Ouroboros.VENDA_NUMERO_COMANDAS)));
            
            
            //Impressão
            Ouroboros.IMPRESSORA_PADRAO = cboImpressoras.getSelectedItem().toString();
            Ouroboros.IMPRESSORA_DESATIVAR = chkDesativarImpressao.isSelected();
            
            cDAO.save(new Constante("IMPRESSORA_PADRAO", Ouroboros.IMPRESSORA_PADRAO));
            cDAO.save(new Constante("IMPRESSORA_DESATIVAR", String.valueOf(Ouroboros.IMPRESSORA_DESATIVAR)));
            
            //Diversos
            Ouroboros.SAT_HABILITAR = chkHabilitarSat.isSelected();
            cDAO.save(new Constante("SAT_HABILITAR", String.valueOf(Ouroboros.SAT_HABILITAR)));
            
            
            JOptionPane.showMessageDialog(rootPane, "Dados salvos", null, JOptionPane.INFORMATION_MESSAGE);
        } catch(HeadlessException e){
            JOptionPane.showMessageDialog(rootPane, e, "Erro", JOptionPane.ERROR_MESSAGE);
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

        btnSalvar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNomeFantasia = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtRazaoSocial = new javax.swing.JTextField();
        txtCNPJ = new javax.swing.JFormattedTextField();
        txtIE = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtEndereco = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        chkInsercaoDireta = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        txtMulta = new javax.swing.JFormattedTextField();
        txtJuros = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        cboJurosTipo = new javax.swing.JComboBox<>();
        txtNumeroComandas = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        cboImpressoras = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        chkDesativarImpressao = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        btnBootstrap = new javax.swing.JButton();
        btnSat = new javax.swing.JButton();
        btnStatuSat = new javax.swing.JButton();
        chkHabilitarSat = new javax.swing.JCheckBox();

        setTitle("Configuração do Sistema");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Nome Fantasia");

        jLabel2.setText("Razão Social");

        jLabel3.setText("CNPJ");

        jLabel4.setText("IE");

        jLabel5.setText("Endereço");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGap(41, 41, 41)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtRazaoSocial, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                    .addComponent(txtNomeFantasia)
                    .addComponent(txtIE)
                    .addComponent(txtCNPJ)
                    .addComponent(txtEndereco))
                .addContainerGap(131, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNomeFantasia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRazaoSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCNPJ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addContainerGap(71, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Dados da Empresa", jPanel1);

        chkInsercaoDireta.setText("Inserir item direto (se desabilitado, permite alterar o valor do produto)");

        jLabel7.setText("Multa");

        txtMulta.setName("decimal"); // NOI18N

        txtJuros.setName("decimal"); // NOI18N

        jLabel6.setText("Juros (% a.m.)");

        cboJurosTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "%", "R$" }));

        jLabel9.setText("Número de Comandas");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkInsercaoDireta)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6)
                            .addComponent(jLabel9))
                        .addGap(41, 41, 41)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtMulta, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNumeroComandas, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtJuros, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(cboJurosTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(261, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkInsercaoDireta)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtMulta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cboJurosTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtJuros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNumeroComandas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addContainerGap(130, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Venda", jPanel3);

        jLabel8.setText("Impressora");

        chkDesativarImpressao.setText("Desativar impressão para testes");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(chkDesativarImpressao)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboImpressoras, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(0, 326, Short.MAX_VALUE)))
                        .addGap(244, 244, 244))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboImpressoras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(chkDesativarImpressao)
                .addContainerGap(174, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Impressão", jPanel4);

        btnBootstrap.setText("Bootstrap");
        btnBootstrap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBootstrapActionPerformed(evt);
            }
        });

        btnSat.setText("Configurar SAT");
        btnSat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSatActionPerformed(evt);
            }
        });

        btnStatuSat.setText("Status SAT");
        btnStatuSat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStatuSatActionPerformed(evt);
            }
        });

        chkHabilitarSat.setText("Habilitar SAT");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkHabilitarSat)
                    .addComponent(btnBootstrap)
                    .addComponent(btnSat)
                    .addComponent(btnStatuSat))
                .addContainerGap(519, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnBootstrap)
                .addGap(18, 18, 18)
                .addComponent(btnSat)
                .addGap(18, 18, 18)
                .addComponent(btnStatuSat)
                .addGap(18, 18, 18)
                .addComponent(chkHabilitarSat)
                .addContainerGap(113, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Diversos", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnCancelar)
                        .addGap(18, 18, 18)
                        .addComponent(btnSalvar))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 639, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 635, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvar)
                    .addComponent(btnCancelar))
                .addGap(121, 121, 121))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBootstrapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBootstrapActionPerformed
        String senha = JOptionPane.showInputDialog("Senha para bootstrap");
        
        if(senha.equals("753951")){
        
            new Toast("Dados sendo carregados... Aguarde a mensagem de término");
            new MeioDePagamentoDAO().bootstrap();

            new CaixaItemTipoDAO().bootstrap();
            new ProdutoOrigemDAO().bootstrap();
            new CfopDAO().bootstrap();
            new IcmsDAO().bootstrap();
            new IbptDAO().bootstrap();
            new NcmDAO().bootstrap();
            new CaixaPeriodoDAO().bootstrap();
            new SatErroOuAlertaDAO().bootstrap();
            new SatEstadoDAO().bootstrap();
            new UnidadeComercialDAO().bootstrap();
            new MovimentoFisicoTipoDAO().bootstrap();

            JOptionPane.showMessageDialog(rootPane, "Concluído");
        }
    }//GEN-LAST:event_btnBootstrapActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        salvar();
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        singleInstance = null;
    }//GEN-LAST:event_formInternalFrameClosed

    private void btnSatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSatActionPerformed
        SATSetup satSetup = new SATSetup(MAIN_VIEW);
    }//GEN-LAST:event_btnSatActionPerformed

    private void btnStatuSatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStatuSatActionPerformed
        SATStatusOperacionalView satStatus = new SATStatusOperacionalView(MAIN_VIEW);
    }//GEN-LAST:event_btnStatuSatActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBootstrap;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnSat;
    private javax.swing.JButton btnStatuSat;
    private javax.swing.JComboBox<String> cboImpressoras;
    private javax.swing.JComboBox<String> cboJurosTipo;
    private javax.swing.JCheckBox chkDesativarImpressao;
    private javax.swing.JCheckBox chkHabilitarSat;
    private javax.swing.JCheckBox chkInsercaoDireta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JFormattedTextField txtCNPJ;
    private javax.swing.JTextField txtEndereco;
    private javax.swing.JTextField txtIE;
    private javax.swing.JFormattedTextField txtJuros;
    private javax.swing.JFormattedTextField txtMulta;
    private javax.swing.JTextField txtNomeFantasia;
    private javax.swing.JFormattedTextField txtNumeroComandas;
    private javax.swing.JTextField txtRazaoSocial;
    // End of variables declaration//GEN-END:variables
}
