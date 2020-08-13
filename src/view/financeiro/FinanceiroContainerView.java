/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.financeiro;

import view.financeiro.contasPagar.ContasPagarView;
import view.financeiro.contasReceber.ContasReceberView;
import view.financeiro.caixa.CaixaView;
import view.financeiro.contaCorrente.ContaCorrenteView;
import java.awt.GridLayout;
import java.util.Arrays;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import model.mysql.bean.principal.Recurso;
import static ouroboros.Ouroboros.MENU_MIN_WIDTH;
import static ouroboros.Ouroboros.SCREEN_HEIGHT;
import static ouroboros.Ouroboros.SCREEN_WIDTH;
import static ouroboros.Ouroboros.TOOLBAR_HEIGHT;
import static ouroboros.Ouroboros.USUARIO;
import view.financeiro.boleto.BoletosView;
import view.financeiro.cartao.CartaoReceberListaView;
import view.financeiro.cheque.ChequesView;

/**
 *
 * @author ivand
 */
public class FinanceiroContainerView extends javax.swing.JInternalFrame {
    private static FinanceiroContainerView singleInstance = null;
    CaixaView caixaView;
    
    public static FinanceiroContainerView getSingleInstance(){
        if(!USUARIO.autorizarAcesso(Recurso.FINANCEIRO)) {
            return null;
        }
        
        if(singleInstance == null){
            singleInstance = new FinanceiroContainerView();
        }
        return singleInstance;
    }
    
    private FinanceiroContainerView() {
        initComponents();
        System.out.println("novo caixa geral container view...");
        
        caixaView = CaixaView.getSingleInstance();
        
        gerarTabs();
        
        ChangeListener changeListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                
                JInternalFrame iFrame = null;
                switch(index){
                    case 0:
                        iFrame = caixaView;
                        break;
                    case 1:
                        iFrame = ContaCorrenteView.getSingleInstance();
                        break;
                    case 2:
                        iFrame = ContasReceberView.getSingleInstance();
                        break;
                    case 3:
                        iFrame = CartaoReceberListaView.getSingleInstance();
                        break;
                    case 4:
                        iFrame = BoletosView.getSingleInstance();
                        break;
                    case 5:
                        iFrame = ChequesView.getSingleInstance();
                        break;
                    case 6:
                        iFrame = ContasPagarView.getSingleInstance();
                        break;
                    case 7:
                        iFrame = CaixaPeriodoPorMeioDePagamentoView.getSingleInstance();
                        break;
                    case 8:
                        iFrame = CaixaPorPeriodoView.getSingleInstance();
                        break;
                    
                }
                
                exibirFrame(iFrame);
            }
        };
        
        tabPane.addChangeListener(changeListener);
        
        exibirFrame(caixaView);
        
    }
    
    private void exibirFrame(JInternalFrame iFrame){
        if(!Arrays.asList(dpContainer.getComponents()).contains(iFrame)){
            dpContainer.add(iFrame);
            ((BasicInternalFrameUI) iFrame.getUI()).setNorthPane(null);
            iFrame.setBorder(null);
            iFrame.setBounds(0, 0, SCREEN_WIDTH - MENU_MIN_WIDTH, SCREEN_HEIGHT - TOOLBAR_HEIGHT * 2);
            iFrame.setVisible(true);
            dpContainer.repaint();
        }
        iFrame.toFront();
    }
    
    public void gerarTabs(){
        adicionarTab("Conta Corrente");
        adicionarTab("Contas a Receber");
        adicionarTab("Cartões a Receber");
        adicionarTab("Boletos");
        adicionarTab("Cheques");
        adicionarTab("Contas a Pagar");
        adicionarTab("Caixa Período/Meio de Pagamento");
        adicionarTab("Caixa por Período");
        
    }
    
    private void adicionarTab(String nome){
        JPanel jPanel = new javax.swing.JPanel();
        jPanel.setLayout(new GridLayout(1, 1));
        tabPane.addTab(nome, jPanel);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        dpContainer = new javax.swing.JDesktopPane();

        setTitle("Financeiro");
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

        tabPane.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tabPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabPaneStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 917, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        tabPane.addTab("Caixa", jPanel1);

        javax.swing.GroupLayout dpContainerLayout = new javax.swing.GroupLayout(dpContainer);
        dpContainer.setLayout(dpContainerLayout);
        dpContainerLayout.setHorizontalGroup(
            dpContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        dpContainerLayout.setVerticalGroup(
            dpContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 392, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPane)
            .addComponent(dpContainer)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tabPane, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dpContainer))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tabPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabPaneStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tabPaneStateChanged

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        singleInstance = null;
    }//GEN-LAST:event_formInternalFrameClosed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane dpContainer;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTabbedPane tabPane;
    // End of variables declaration//GEN-END:variables
}
