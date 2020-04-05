/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.catalogo.geral;

import java.awt.GridLayout;
import java.util.Arrays;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import model.mysql.bean.principal.Recurso;
import view.catalogo.geral.ProdutoListaView;
import static ouroboros.Ouroboros.MENU_MIN_WIDTH;
import static ouroboros.Ouroboros.SCREEN_HEIGHT;
import static ouroboros.Ouroboros.SCREEN_WIDTH;
import static ouroboros.Ouroboros.TOOLBAR_HEIGHT;
import static ouroboros.Ouroboros.USUARIO;

/**
 *
 * @author ivand
 */
public class ProdutoGeralContainerView extends javax.swing.JInternalFrame {
    private static ProdutoGeralContainerView singleInstance = null;
    //private static List<ProdutoGeralContainerView> produtoGeralContainerViews = new ArrayList<>();
    ProdutoListaView produtoListaView;
    CategoriaListaView categoriaListaView;
    MarcaListaView marcaListaView;
    TabelaPrecoListaView tabelaPrecoListaView;
    
    public static ProdutoGeralContainerView getSingleInstance(){
        if(!USUARIO.autorizarAcesso(Recurso.PRODUTOS)) {
            return null;
        }
        
        if(singleInstance == null){
            singleInstance = new ProdutoGeralContainerView();
        }
        
        return singleInstance;
    }
    
    private ProdutoGeralContainerView() {
        initComponents();
        
        produtoListaView = ProdutoListaView.getSingleInstance();
        
        gerarTabs();
        
        ChangeListener changeListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                
                JInternalFrame iFrame = null;
                switch(index){
                    case 0:
                        iFrame = produtoListaView;
                        break;
                    case 1:
                        iFrame = CategoriaListaView.getSingleInstance();
                        break;
                    case 2:
                        iFrame = MarcaListaView.getSingleInstance();
                        break;
                    case 3:
                        iFrame = TabelaPrecoListaView.getSingleInstance();
                        break;
                }
                
                exibirFrame(iFrame);
            }
        };
        
        tabPane.addChangeListener(changeListener);
        
        exibirFrame(produtoListaView);
        
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
        adicionarTab("Categorias");
        adicionarTab("Marcas");
        adicionarTab("Tabelas de Preços");
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

        setTitle("Produtos");
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

        tabPane.addTab("Lista", jPanel1);

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
