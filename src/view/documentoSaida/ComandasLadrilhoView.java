/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoSaida;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JButton;
import model.mysql.bean.principal.Recurso;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.documento.VendaTipo;
import model.mysql.dao.principal.VendaDAO;
import util.DateTime;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.USUARIO;
import static ouroboros.Ouroboros.VENDA_NUMERO_COMANDAS;
import static ouroboros.Ouroboros.em;
import util.Cor;
import util.MwString;

/**
 *
 * @author ivand
 */
public class ComandasLadrilhoView extends javax.swing.JInternalFrame {

    private static ComandasLadrilhoView singleInstance = null;
    Timestamp lastTimestamp = null;
    ControlSubThread threadBotoes = new ControlSubThread(500);
    VendaDAO vendaDAO = new VendaDAO();
    Map<Integer, Venda> mapVendas = new HashMap<>();
    List<Venda> vendas = new ArrayList<>();

    Integer contador = 0;
    int limite = VENDA_NUMERO_COMANDAS;
    int limiteHorizontal = 5;

    public static ComandasLadrilhoView getSingleInstance() {
        if(!USUARIO.autorizarAcesso(Recurso.COMANDAS)) {
            return null;
        }
        
        if (singleInstance == null) {
            singleInstance = new ComandasLadrilhoView();
        }
        return singleInstance;
    }

    /**
     * Creates new form ComandasView
     */
    private ComandasLadrilhoView() {
        initComponents();

        long start = System.currentTimeMillis();

        lastTimestamp = DateTime.fromString("00/00/0000");

        gerarBotoes();

        long elapsed = System.currentTimeMillis() - start;

    }
    
    private void carregarDados() {
        txtComandasAbertas.setText(String.valueOf(vendaDAO.getComandasAbertas().size()));
    }

    private void removerBotoes() {
        pnlComandas.removeAll();
        pnlComandas.repaint();
    }

    private void gerarBotoes() {
        int x = 0;
        int y = 0;

        int larguraTotal = pnlComandas.getWidth();

        int width = Math.round((larguraTotal - 30) / limiteHorizontal);
        int height = 50;
        JButton btn = null;

        vendas = vendaDAO.getComandasAbertas();

        for (int c = 1; c <= limite; c++) {
            mapVendas.put(c, new Venda(VendaTipo.COMANDA));
        }

        if (vendas != null) {
            for (Venda v : vendas) {
                //em.refresh(v);
                mapVendas.replace(v.getComanda(), v);
            }
        }

        for (Map.Entry<Integer, Venda> entry : mapVendas.entrySet()) {

            int comanda = entry.getKey();
            final Venda venda = entry.getValue();

            btn = new JButton(String.valueOf(comanda));

            btn.setFont(new Font(btn.getFont().getFontName(), btn.getFont().getStyle(), 24));
            btn.setName(String.valueOf(comanda));
            
            venda.setComanda(comanda);
            //venda.setVendaTipo(VendaTipo.COMANDA);

            btn.setMaximumSize(new Dimension(50, 50));
            btn.setBounds(x * width, y * height, width, height);

            ActionListener actionListenerButton = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    JButton b = (JButton) actionEvent.getSource();

                    
                    //2019-03-15 - tentando impedir duplicidade de venda que ocorreu no Bombocado
                    if(venda.getId() != null) {
                        em.refresh(venda);
                    }
                    MAIN_VIEW.addView(VendaView.getInstance(venda));
                }
            };

            btn.addActionListener(actionListenerButton);

            //System.out.println("venda Encerramento: " + venda.getEncerramento());
            if (venda.getId() != null) {
                btn.setBackground(Cor.VERMELHO);
                btn.setForeground(Color.WHITE);

            } else {
                btn.setBackground(Cor.VERDE);
            }

            x++;
            if (limite > 60) {
                limiteHorizontal = 10;
            }
            if (x == limiteHorizontal) {
                x = 0;
                y++;
            }

            pnlComandas.add(btn);
        }

        repaint();
    }

    public class ControlSubThread implements Runnable {

        private Thread worker;
        private final AtomicBoolean running = new AtomicBoolean(false);
        private int interval;

        public ControlSubThread(int sleepInterval) {
            interval = sleepInterval;
        }

        public void start() {
            worker = new Thread(this);
            worker.start();
        }

        public void stop() {
            running.set(false);
        }

        public void interrupt() {
            running.set(false);
            worker.interrupt();
        }

        boolean isRunning() {
            return running.get();
        }

        @Override
        public void run() {
            running.set(true);
            while (running.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    System.err.println(e);
                }
                removerBotoes();
                gerarBotoes();
                
                carregarDados();

            }
        }
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtComanda = new javax.swing.JTextField();
        pnlComandas = new javax.swing.JPanel();
        txtComandasAbertas = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();

        setTitle("Comandas");
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                formFocusLost(evt);
            }
        });
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameActivated(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameDeactivated(evt);
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        txtComanda.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        txtComanda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtComanda.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtComandaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtComandaFocusLost(evt);
            }
        });
        txtComanda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtComandaKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout pnlComandasLayout = new javax.swing.GroupLayout(pnlComandas);
        pnlComandas.setLayout(pnlComandasLayout);
        pnlComandasLayout.setHorizontalGroup(
            pnlComandasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlComandasLayout.setVerticalGroup(
            pnlComandasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 525, Short.MAX_VALUE)
        );

        txtComandasAbertas.setEditable(false);
        txtComandasAbertas.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtComandasAbertas.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Comandas abertas");

        jLabel37.setBackground(new java.awt.Color(122, 138, 153));
        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel37.setForeground(java.awt.Color.white);
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText("Nº Comanda + ENTER");
        jLabel37.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        jLabel37.setOpaque(true);

        jLabel38.setBackground(new java.awt.Color(122, 138, 153));
        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel38.setForeground(java.awt.Color.white);
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("Comandas");
        jLabel38.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        jLabel38.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, 883, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(txtComandasAbertas, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtComanda, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(pnlComandas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37)
                    .addComponent(jLabel38))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtComanda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtComandasAbertas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)))
                    .addComponent(pnlComandas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        txtComanda.requestFocus();
    }//GEN-LAST:event_formFocusGained

    private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost

    }//GEN-LAST:event_formFocusLost

    private void formInternalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameDeactivated

    }//GEN-LAST:event_formInternalFrameDeactivated

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated

    }//GEN-LAST:event_formInternalFrameActivated

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        singleInstance = null;
    }//GEN-LAST:event_formInternalFrameClosed

    private void txtComandaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtComandaKeyReleased
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:

                String numero = MwString.soNumeros(txtComanda.getText().trim());
                int comanda = Integer.valueOf(numero);
                //VendaDAO vendaDAO = new VendaDAO();
                Venda venda = vendaDAO.getComandaAberta(comanda);
                //System.out.println("venda comanda " + venda.getComanda());
                if(venda == null) {
                    venda = new Venda(VendaTipo.COMANDA);
                    venda.setComanda(comanda);
                    //venda.setVendaTipo(VendaTipo.COMANDA);
                }

                //int id = venda != null ? venda.getId() : 0;
                txtComanda.setText("");

                //MAIN_VIEW.addView(VendaView.getInstance(venda, comanda, false));
                if(venda.getId() != null) {
                    em.refresh(venda);
                }
                MAIN_VIEW.addView(VendaView.getInstance(venda));
                break;
        }
    }//GEN-LAST:event_txtComandaKeyReleased

    private void txtComandaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtComandaFocusGained
        //threadBotoes.start();
        System.out.println("FocusGained");
        removerBotoes();
        gerarBotoes();
        carregarDados();

    }//GEN-LAST:event_txtComandaFocusGained

    private void txtComandaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtComandaFocusLost
        //threadBotoes.stop();
    }//GEN-LAST:event_txtComandaFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JPanel pnlComandas;
    private javax.swing.JTextField txtComanda;
    private javax.swing.JTextField txtComandasAbertas;
    // End of variables declaration//GEN-END:variables
}
