/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoSaida.geral;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.MovimentoFisicoTipo;
import model.mysql.bean.principal.Recurso;
import model.mysql.bean.principal.catalogo.Categoria;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.documento.VendaTipo;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.principal.financeiro.CaixaItemTipo;
import model.mysql.dao.principal.MovimentoFisicoDAO;
import model.mysql.dao.principal.ParcelaDAO;
import model.mysql.dao.principal.VendaDAO;
import model.mysql.dao.principal.catalogo.CategoriaDAO;
import model.mysql.dao.principal.catalogo.ProdutoDAO;
import model.mysql.dao.principal.financeiro.CaixaItemDAO;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.USUARIO;
import printing.documento.VendaPorTicketPrint;
import util.Cor;
import util.Decimal;

/**
 *
 * @author ivand
 */
public class VendaPorFichaView extends javax.swing.JInternalFrame {

    private static VendaPorFichaView singleInstance = null;
    
    CategoriaDAO categoriaDAO = new CategoriaDAO();
    VendaDAO vendaDAO = new VendaDAO();
    MovimentoFisicoDAO movimentoFisicoDAO = new MovimentoFisicoDAO();
    ParcelaDAO parcelaDAO = new ParcelaDAO();
    CaixaItemDAO caixaItemDAO = new CaixaItemDAO();
    
    ProdutoDAO produtoDAO = new ProdutoDAO();
    
    private BigDecimal total = BigDecimal.ZERO;
    

    public static VendaPorFichaView getSingleInstance() {
        if(!USUARIO.autorizarAcesso(Recurso.COMANDAS)) {
            return null;
        }
        
        if (singleInstance == null) {
            singleInstance = new VendaPorFichaView();
        }
        return singleInstance;
    }

    /**
     * Creates new form ComandasView
     */
    private VendaPorFichaView() {
        initComponents();

        carregarDados();
        
    }
    
    private void carregarDados() {
        
        for (Categoria categoria : categoriaDAO.findAll()) {
        
            JScrollPane scrollPane = new JScrollPane();
            
            scrollPane.setBackground(Color.RED);
            
            //scrollPane.setPreferredSize(new Dimension(500, 1500));
            //scrollPane.setSize(new Dimension(500, 1500));
            
            JPanel panel = new JPanel(new GridLayout(0, 4, 20, 10));
            //panel.setBackground(Color.YELLOW);
            
            /*panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLACK), 
                    BorderFactory.createEmptyBorder(16, 16, 16, 16)
            ));*/

            panel.setBorder(
                    BorderFactory.createEmptyBorder(16, 16, 16, 16)
            );

            
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            
            scrollPane.setViewportView(panel);
            
            //tabCategorias.setPreferredSize(new Dimension(1800, 300));
            //tabCategorias.setSize(new Dimension(1800, 300));
            tabCategorias.setMinimumSize(new Dimension(500, 300)); //forçar a se encaixar na janela
            
            tabCategorias.add(categoria.getNome(), scrollPane);
            
            /*JTextField jTextField = new JTextField(categoria.getNome());
            jTextField.setMargin(new Insets(16, 16, 16, 16));
            jTextField.setEditable(false);
            tabCategorias.setTabComponentAt(tabCategorias.getTabCount() - 1, jTextField);*/
            
            JLabel jLabel = new JLabel(categoria.getNome());
            jLabel.setHorizontalAlignment(JLabel.CENTER);
            jLabel.setPreferredSize(new Dimension(Double.valueOf(jLabel.getPreferredSize().getWidth()).intValue() + 40, Double.valueOf(jLabel.getPreferredSize().getHeight()).intValue() + 40));
            tabCategorias.setTabComponentAt(tabCategorias.getTabCount() - 1, jLabel);
            
            //for (Produto produto : categoria.getProdutoList()) {
            for (Produto produto : produtoDAO.findPorCategoria(categoria)) {

                MouseListener mouseListener = new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        gerarVenda(produto);
                        
                        totalizar(produto);
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        ((JTextArea) e.getSource()).setBackground(Cor.AZUL);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        //
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        ((JTextArea) e.getSource()).setBackground(Cor.VERDE);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        ((JTextArea) e.getSource()).setBackground(Cor.AMARELO);
                    }
                };
                
                JTextArea txtArea = new JTextArea();
                
                txtArea.setFont(new Font("Tahoma", Font.BOLD, 12));
                
                txtArea.setColumns(20);
                txtArea.setLineWrap(true);
                txtArea.setRows(3);
                
                String descricao = produto.getNome() + " - R$" + produto.getValorVenda() + " (" + produto.getCodigo() + ")";
                txtArea.setText(descricao);
                
                txtArea.setWrapStyleWord(true);
                txtArea.setMargin(new Insets(16, 32, 16, 32));
                
                txtArea.setEditable(false);
                
                
                Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED);
                Border emptyBorder = BorderFactory.createEmptyBorder(16, 32, 16, 32);
                txtArea.setBorder(BorderFactory.createCompoundBorder(border, emptyBorder));

                txtArea.setBackground(Cor.AMARELO);
                
                
                txtArea.addMouseListener(mouseListener);
                
                panel.add(txtArea);
                
                revalidate();
                repaint();
                
            }
        
        }
        
        revalidate();
        repaint();
        
        
    }
    
    private void gerarVenda(Produto produto) {
        Venda documento = new Venda(VendaTipo.VENDA);
        
        vendaDAO.save(documento);
        
        MovimentoFisico movimentoFisico = new MovimentoFisico(
                produto, 
                produto.getCodigo(), 
                produto.getNome(), 
                produto.getProdutoTipo(), 
                BigDecimal.ZERO, 
                BigDecimal.ONE, 
                produto.getValorVenda(), 
                BigDecimal.ZERO, 
                produto.getUnidadeComercialVenda(), 
                MovimentoFisicoTipo.VENDA, 
                "");
        movimentoFisico.setDataSaida(LocalDateTime.now());
        
        documento.addMovimentoFisico(movimentoFisico);
        
        movimentoFisicoDAO.save(movimentoFisico);
        
        Parcela parcela = new Parcela(
                null, 
                produto.getValorVenda(), 
                BigDecimal.ZERO, 
                BigDecimal.ZERO, 
                BigDecimal.ZERO, 
                MeioDePagamento.DINHEIRO);
        
        documento.addParcela(parcela);
        
        parcelaDAO.save(parcela);
        
        
        CaixaItem caixaItem = new CaixaItem(
                CaixaItemTipo.DOCUMENTO, 
                MeioDePagamento.DINHEIRO, 
                "", 
                produto.getValorVenda(), 
                BigDecimal.ZERO);
        
        Ouroboros.FINANCEIRO_CAIXA_PRINCIPAL.getLastCaixa().addCaixaItem(caixaItem);
        parcela.addRecebimento(caixaItem);
        
        caixaItemDAO.save(caixaItem);
        
        vendaDAO.save(documento); // para calcular o total
        
        
        VendaPorTicketPrint.imprimirCupom(produto);
    }
    
    private void totalizar(Produto produto) {
        total = total.add(produto.getValorVenda());
        
        txtTotal.setText(Decimal.toString(total));
    }
    
    private void zerarTotal() {
        total = BigDecimal.ZERO;
        
        txtTotal.setText(Decimal.toString(total));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabCategorias = new javax.swing.JTabbedPane();
        jLabel1 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        btnZerarTotal = new javax.swing.JButton();

        setTitle("VENDA POR FICHA");
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

        tabCategorias.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tabCategorias.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("Total");

        txtTotal.setEditable(false);
        txtTotal.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtTotal.setForeground(java.awt.Color.red);
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotal.setText("0,00");

        btnZerarTotal.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnZerarTotal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-close-button-20.png"))); // NOI18N
        btnZerarTotal.setText("Zerar Total");
        btnZerarTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZerarTotalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabCategorias, javax.swing.GroupLayout.DEFAULT_SIZE, 1264, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnZerarTotal)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabCategorias, javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnZerarTotal))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
    }//GEN-LAST:event_formFocusGained

    private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost
    }//GEN-LAST:event_formFocusLost

    private void formInternalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameDeactivated

    }//GEN-LAST:event_formInternalFrameDeactivated

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated

    }//GEN-LAST:event_formInternalFrameActivated

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
    }//GEN-LAST:event_formInternalFrameClosed

    private void btnZerarTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZerarTotalActionPerformed
        zerarTotal();
    }//GEN-LAST:event_btnZerarTotalActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnZerarTotal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTabbedPane tabCategorias;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
