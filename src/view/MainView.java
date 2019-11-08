/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.net.URL;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.MENU_MAX_WIDTH;
import static ouroboros.Ouroboros.MENU_MIN_WIDTH;
import static ouroboros.Ouroboros.SCREEN_HEIGHT;
import static ouroboros.Ouroboros.SCREEN_WIDTH;
import static ouroboros.Ouroboros.TOOLBAR_HEIGHT;
import static ouroboros.Ouroboros.USUARIO;
import util.Cor;
import view.documentoSaida.ComandasLadrilhoView;
import view.documentoSaida.VendaView;
import view.sistema.BackupView;
import view.sistema.LogAtualizacao;

/**
 *
 * @author ivand
 */
public class MainView extends javax.swing.JFrame {

    MainMenuView mainMenuView = new MainMenuView();
    ImageIcon imagemFundo = new ImageIcon();
    JLabel fundo = new JLabel();

    public MainView() {
        initComponents();
        
        btnMindware.setBackground(Cor.CINZA);

        ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/res/img/money.png"));
        setIconImage(icon.getImage());

        URL iconURL = getClass().getResource("/res/img/logo.png");
        imagemFundo = new ImageIcon(iconURL);
        fundo = new JLabel(imagemFundo);
        jDesktopPane1.add(fundo);
        
        jDesktopPane1.add(mainMenuView);
        
        
        redimensionarMenu();
    }
    
    public void carregarMenu() {
        mainMenuView.setVisible(true);
        mainMenuView.setBotoes();
    }
    
    public void setMensagem(String msg) {
        txtMensagem.setText(msg);
        repaint();
    }

    public void addView(JInternalFrame jIFrame) {
        try {
            if(jIFrame != null) { //controle de permissões - diretiva
                if (jIFrame.isVisible()) {
                    jIFrame.toFront();
                    jIFrame.requestFocus();
                } else {
                    jDesktopPane1.remove(jIFrame);

                    //Create a random id to identify form and button(pseudo tab) as a group
                    String paringControl = String.valueOf(new Random().nextInt(999999));
                    //System.out.println("paringControl: " + paringControl);
                    //Set appearence
                    jIFrame.setName(paringControl);
                    ((BasicInternalFrameUI) jIFrame.getUI()).setNorthPane(null);
                    jIFrame.setBorder(null);
                    int frameX = Ouroboros.SISTEMA_MODO_BALCAO ? 0 : MENU_MIN_WIDTH;
                    jIFrame.setBounds(frameX, 0, SCREEN_WIDTH - frameX, SCREEN_HEIGHT - TOOLBAR_HEIGHT);

                    jDesktopPane1.add(jIFrame);
                    jIFrame.setVisible(true);

                    ActionListener actionListenerTab = new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {

                            jIFrame.toFront();
                            //jIFrame.requestFocus();

                            colorTabs(paringControl);
                            jIFrame.requestFocus();
                        }
                    };

                    ActionListener actionListenerClose = new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {

                            removeView(jIFrame.getName());
                            removeTab(jIFrame.getName());
                        }
                    };

                    JButton btn = new JButton(jIFrame.getTitle());
                    btn.setName(paringControl);
                    btn.addActionListener(actionListenerTab);
                    btn.setBorder(null);
                    btn.setToolTipText(jIFrame.getTitle());
                    btn.setMaximumSize(new Dimension(200, 30));

                    JButton btnClose = new JButton();
                    btnClose.setName(paringControl);
                    btnClose.addActionListener(actionListenerClose);
                    btnClose.setMaximumSize(new Dimension(20, 20));
                    btnClose.setAlignmentX(RIGHT_ALIGNMENT);

                    ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/res/img/error_blank.png"));
                    ImageIcon iconRoll = new javax.swing.ImageIcon(getClass().getResource("/res/img/error_red.png"));
                    //URL iconURL = getClass().getResource("resource/img/error_blank.png");
                    //URL iconRollURL = getClass().getResource("resource/img/error_red.png");
                    //ImageIcon imagemFundo = new ImageIcon(iconURL);
                    //ImageIcon iconRoll = new ImageIcon(iconRollURL);
                    btnClose.setIcon(icon);
                    btnClose.setRolloverIcon(iconRoll);
                    btnClose.setContentAreaFilled(false);
                    btnClose.setBorder(null);

                    btn.add(btnClose);

                    toolBarMain.add(btn);
                    JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
                    separator.setName(paringControl);
                    separator.setMaximumSize(new Dimension(2, 30));
                    toolBarMain.add(separator);

                    colorTabs(paringControl);

                    jDesktopPane1.getComponent(0).requestFocus();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(MAIN_VIEW, e, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void removeView(String name) {
        try {
            //System.out.println("removeView: " + name);
            for (Component component : jDesktopPane1.getComponents()) {
                if (component.getName() != null) {
                    if (component.getName().equals(name)) {
                        try {
                            jDesktopPane1.getComponent(1).requestFocus();
                            //((JInternalFrame) component).dispose();
                            ((JInternalFrame) component).setClosed(true);
                            //jDesktopPane1.remove(component);

                            //colorir a última tab
                            for (Component c : jDesktopPane1.getComponents()) {
                                //System.out.println("nn: " + c.getName() + " i: " + jDesktopPane1.getComponentZOrder(c));
                                if (c.getName() != null) {
                                    colorTabs(c.getName());
                                    break;
                                }
                            }

                        } catch (PropertyVetoException ex) {
                            System.err.println("Erro ao remover view: " + ex);
                        }
                    }
                }
            }
            //this.repaint();
            //jDesktopPane1.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(MAIN_VIEW, e, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void removeTab(String name) {
        try {
            for (Component component : toolBarMain.getComponents()) {
                if (component.getName() != null) {
                    if (component.getName().equals(name)) {
                        toolBarMain.remove(component);
                        toolBarMain.revalidate();
                        toolBarMain.repaint();
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(MAIN_VIEW, e, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void colorTabs(String paringControl) {
        for (Component component : toolBarMain.getComponents()) {
            if (component.getName() != null) {
                if (component.getClass() == JButton.class) {
                    ((JButton) component).setForeground(Color.BLACK);
                    ((JButton) component).setBorder(null);
                }
                if (component.getName().equals(paringControl)) {
                    if (component.getClass() == JButton.class) {
                        ((JButton) component).setForeground(Color.RED);
                        Border thickBorder = new LineBorder(Color.RED, 1);
                        ((JButton) component).setBorder(thickBorder);
                    }
                }

            }
        }
    }
    
    private void redimensionarMenu() {
        mainMenuView.setBounds(0, 0, MENU_MAX_WIDTH, SCREEN_HEIGHT - TOOLBAR_HEIGHT);
        
        fundo.setSize(imagemFundo.getIconWidth(), imagemFundo.getIconHeight());

        int x = MENU_MAX_WIDTH + (SCREEN_WIDTH - MENU_MAX_WIDTH) / 2 - imagemFundo.getIconWidth() / 2;
        
        int y = SCREEN_HEIGHT / 2 - imagemFundo.getIconHeight() / 2;
        fundo.setLocation(x, y);
        
        this.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDesktopPane1 = new javax.swing.JDesktopPane();
        txtMensagem = new javax.swing.JTextField();
        toolBarMain = new javax.swing.JToolBar();
        btnMindware = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("B3 - Mindware");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addWindowStateListener(new java.awt.event.WindowStateListener() {
            public void windowStateChanged(java.awt.event.WindowEvent evt) {
                formWindowStateChanged(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowDeiconified(java.awt.event.WindowEvent evt) {
                formWindowDeiconified(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jDesktopPane1.setOpaque(false);

        txtMensagem.setEditable(false);
        txtMensagem.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtMensagem.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMensagem.setText("Faça uma indicação e ganhe um bônus da Mindware");

        jDesktopPane1.setLayer(txtMensagem, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane1Layout.createSequentialGroup()
                .addGap(0, 448, Short.MAX_VALUE)
                .addComponent(txtMensagem, javax.swing.GroupLayout.PREFERRED_SIZE, 469, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDesktopPane1Layout.createSequentialGroup()
                .addGap(0, 325, Short.MAX_VALUE)
                .addComponent(txtMensagem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        toolBarMain.setFloatable(false);
        toolBarMain.setRollover(true);
        toolBarMain.setMaximumSize(new java.awt.Dimension(200, 30));
        toolBarMain.setMinimumSize(new java.awt.Dimension(167, 30));
        toolBarMain.setPreferredSize(new java.awt.Dimension(200, 30));

        btnMindware.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnMindware.setText("MINDWARE B3");
        btnMindware.setBorder(null);
        btnMindware.setBorderPainted(false);
        btnMindware.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMindware.setIconTextGap(10);
        btnMindware.setPreferredSize(new java.awt.Dimension(180, 49));
        btnMindware.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMindware.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnMindwareMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnMindwareMouseExited(evt);
            }
        });
        btnMindware.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMindwareActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane1)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolBarMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMindware, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(toolBarMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMindware, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDesktopPane1))
        );

        setBounds(0, 0, 933, 428);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        

        
    }//GEN-LAST:event_formWindowOpened

    private void formWindowStateChanged(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowStateChanged
        //SCREEN_WIDTH = this.getContentPane().getWidth();
        //SCREEN_HEIGHT = this.getContentPane().getHeight();
    }//GEN-LAST:event_formWindowStateChanged

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        //SCREEN_HEIGHT = this.getContentPane().getHeight();
        //SCREEN_WIDTH = this.getContentPane().getWidth();
        
        
    }//GEN-LAST:event_formWindowActivated

    private void formWindowDeiconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeiconified
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowDeiconified

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        //Obter medidas para referência dos outros elementos
        SCREEN_HEIGHT = this.getContentPane().getHeight();
        SCREEN_WIDTH = this.getContentPane().getWidth();
        
        redimensionarMenu();
    }//GEN-LAST:event_formComponentResized

    private void btnMindwareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMindwareActionPerformed
        LogAtualizacao log = new LogAtualizacao();
    }//GEN-LAST:event_btnMindwareActionPerformed

    private void btnMindwareMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMindwareMouseEntered
        btnMindware.setBackground(Cor.AZUL);
    }//GEN-LAST:event_btnMindwareMouseEntered

    private void btnMindwareMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMindwareMouseExited
        btnMindware.setBackground(Cor.CINZA);
    }//GEN-LAST:event_btnMindwareMouseExited

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
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMindware;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JToolBar toolBarMain;
    private javax.swing.JTextField txtMensagem;
    // End of variables declaration//GEN-END:variables
}
