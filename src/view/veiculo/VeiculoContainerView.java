/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.veiculo;

import view.veiculo.*;
import view.veiculo.*;
import java.awt.Component;
import java.awt.GridLayout;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import model.mysql.bean.principal.Veiculo;
import static ouroboros.Ouroboros.MENU_MIN_WIDTH;
import static ouroboros.Ouroboros.SCREEN_HEIGHT;
import static ouroboros.Ouroboros.SCREEN_WIDTH;
import static ouroboros.Ouroboros.TOOLBAR_HEIGHT;

/**
 *
 * @author ivand
 */
public class VeiculoContainerView extends javax.swing.JInternalFrame {
    private Veiculo veiculo;
    private static List<VeiculoContainerView> veiculoContainerViews = new ArrayList<>();
    VeiculoCadastroView veiculoCadastroView;
    VeiculoHistoricoView veiculoHistoricoView;
    
    public static VeiculoContainerView getInstance(Veiculo veiculo) {
        for (VeiculoContainerView veiculoContainerView : veiculoContainerViews) {
            if (veiculoContainerView.veiculo == veiculo) {
                return veiculoContainerView;
            }
        }
        veiculoContainerViews.add(new VeiculoContainerView(veiculo));
        return veiculoContainerViews.get(veiculoContainerViews.size() - 1);
    }
    
    private VeiculoContainerView() {
        initComponents();
    }
    
    private VeiculoContainerView(Veiculo veiculo) {
        initComponents();
        System.out.println("novo VeiculoContainerView...");
        this.veiculo = veiculo;
        
        if(veiculo.getId() != null) {
            txtIdentificacao.setText(veiculo.getId() + " - " + veiculo.getPlaca() + " " + veiculo.getModelo());
        }
        
        veiculoCadastroView = VeiculoCadastroView.getInstance(veiculo);
        
        gerarTabs();
        
        ChangeListener changeListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                
                JInternalFrame iFrame = null;
                switch(index){
                    case 0: //cadastro
                        iFrame = veiculoCadastroView;
                        break;
                    case 1:
                        veiculoHistoricoView = VeiculoHistoricoView.getInstance(veiculo);
                        iFrame = veiculoHistoricoView;
                        break;
                }
                
                exibirFrame(iFrame);
            }
        };
        
        tabPane.addChangeListener(changeListener);
        
        exibirFrame(veiculoCadastroView);
        
    }
    
    private void exibirFrame(JInternalFrame iFrame){
        if(!Arrays.asList(dpContainer.getComponents()).contains(iFrame)){
            dpContainer.add(iFrame);
            ((BasicInternalFrameUI) iFrame.getUI()).setNorthPane(null);
            iFrame.setBorder(null);
            iFrame.setBounds(0, 0, SCREEN_WIDTH - MENU_MIN_WIDTH, SCREEN_HEIGHT - TOOLBAR_HEIGHT * 3);
            iFrame.setVisible(true);
            dpContainer.repaint();
        }
        iFrame.toFront();
    }
    
    public void gerarTabs(){
        if(veiculo.getId() != null && tabPane.getTabCount() == 1){
            adicionarTab("Histórico");
        }
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
        txtIdentificacao = new javax.swing.JTextField();

        setTitle("Veículo");
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

        tabPane.addTab("Cadastro", jPanel1);

        javax.swing.GroupLayout dpContainerLayout = new javax.swing.GroupLayout(dpContainer);
        dpContainer.setLayout(dpContainerLayout);
        dpContainerLayout.setHorizontalGroup(
            dpContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        dpContainerLayout.setVerticalGroup(
            dpContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 371, Short.MAX_VALUE)
        );

        txtIdentificacao.setEditable(false);
        txtIdentificacao.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtIdentificacao.setText("Novo Cadastro");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPane)
            .addComponent(dpContainer)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtIdentificacao)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tabPane, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtIdentificacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dpContainer))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tabPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabPaneStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tabPaneStateChanged

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        veiculoContainerViews.remove(this);
        for(Component c : getComponents()){
            try {
                veiculoCadastroView.setClosed(true);
                /*if(veiculoCrediarioView != null) {
                    veiculoCrediarioView.setClosed(true);
                }*/
            } catch (PropertyVetoException e) {
                System.err.println(e);
            }
        }
    }//GEN-LAST:event_formInternalFrameClosed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane dpContainer;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTabbedPane tabPane;
    private javax.swing.JTextField txtIdentificacao;
    // End of variables declaration//GEN-END:variables
}
