/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.catalogo.item;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.catalogo.ProdutoImagem;
import model.mysql.dao.principal.catalogo.ProdutoImagemDAO;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import ouroboros.Ouroboros;

/**
 *
 * @author ivand
 */
public class ProdutoImagensView extends javax.swing.JInternalFrame {

    private static List<ProdutoImagensView> produtoComponenteViews = new ArrayList<>(); //instâncias

    private Produto produto;
    
    ProdutoImagemDAO produtoImagemDAO = new ProdutoImagemDAO();
    
    String caminhoPasta;
    
    int largura, altura;

    public static ProdutoImagensView getInstance(Produto produto) {
        for (ProdutoImagensView produtoComponenteView : produtoComponenteViews) {
            if (produtoComponenteView.produto == produto) {
                return produtoComponenteView;
            }
        }
        produtoComponenteViews.add(new ProdutoImagensView(produto));
        return produtoComponenteViews.get(produtoComponenteViews.size() - 1);
    }

    private ProdutoImagensView() {
        initComponents();
    }

    private ProdutoImagensView(Produto produto) {
        initComponents();

        this.produto = produto;
        
        caminhoPasta = Ouroboros.PRODUTO_IMAGENS_PATH + produto.getId() + "/";

        dimensionarGaleria();
        carregarGaleria();

    }

    
    private void dimensionarGaleria() {
        List<ProdutoImagem> imagens = produto.getProdutoImagens();

        System.out.println("imagens.size(): " + imagens.size());
        
        int colunas = 4;
        int linhas = (imagens.size() + colunas - 1) / colunas;

        System.out.println("colunas: " + colunas);
        System.out.println("linhas: " + linhas);

        System.out.println("pnlGaleria.getPreferredSize().getWidth(): " + pnlGaleria.getPreferredSize().getWidth());

        //TODO - pegar o tamanho após renderizada a tela para manter a proporção em monitores diferentes
        largura = (int) pnlGaleria.getPreferredSize().getWidth() / colunas; //pnlGaleria.getWidth() / colunas;
        altura = largura;

        System.out.println("largura: " + largura);
        System.out.println("altura: " + altura);

        pnlGaleria.setLayout(new GridLayout(0, colunas));
        pnlGaleria.setPreferredSize(new Dimension(largura * colunas, altura * linhas));
        //pnlGaleria.revalidate();
        //pnlGaleria.repaint();
        //pnlGaleria.setBounds(0, 0, largura * colunas, altura * linhas);
    }

    private void carregarGaleria() {
        for (ProdutoImagem imagem : produto.getProdutoImagens()) {

            carregarGaleriaItem(imagem);

        }
    }
    
    private void carregarGaleriaItem(ProdutoImagem produtoImagem) {

        JButton btn = new JButton();

            String caminhoArquivo = caminhoPasta + produtoImagem.getArquivo();
            System.out.println("path: " + caminhoArquivo);

            try {
                BufferedImage img = ImageIO.read(new File(caminhoArquivo));
                
                Double imgLargura = Double.valueOf(img.getWidth());
                Double imgAltura = Double.valueOf(img.getHeight());
                
                System.out.println("imgLargura: " + imgLargura);
                System.out.println("imgAltura: " + imgAltura);
                
                Double proporcao;
                if (imgLargura > imgAltura) {
                    proporcao = imgLargura / largura;
                    System.out.println("largura");
                } else {
                    proporcao = imgAltura / altura;
                    System.out.println("altura");
                }
                
                System.out.println("proporcao: " + proporcao);
                
                
                int novaLargura = (int) (imgLargura / proporcao);
                int novaAltura = (int) (imgAltura / proporcao);
                

                Image dimg = img.getScaledInstance(novaLargura, novaAltura, Image.SCALE_FAST);

                ImageIcon imageIcon = new ImageIcon(dimg);

                System.out.println("imagem: " + produtoImagem.getDescricao());
                
                ActionListener actionListenerSlide = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {

                        new ProdutoImagensSlideView(produtoImagem);
                    }
                };
                
                ActionListener actionListenerClose = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {

                        produto.removeProdutoImagem(produtoImagem);
                        produtoImagemDAO.remove(produtoImagem);
                        pnlGaleria.remove(btn);
                        
                        dimensionarGaleria();
                        
                        pnlGaleria.revalidate();
                        pnlGaleria.repaint();
                    }
                };

                btn.setContentAreaFilled(false);
                btn.setIcon(imageIcon);
                btn.setMargin(new Insets(3, 3, 3, 3));
                btn.addActionListener(actionListenerSlide);

                JButton btnClose = new JButton();
                //btnClose.setName(paringControl);
                btnClose.addActionListener(actionListenerClose);
                btnClose.setMaximumSize(new Dimension(40, 40));
                btnClose.setAlignmentX(RIGHT_ALIGNMENT);
                btnClose.setAlignmentY(TOP_ALIGNMENT);

                ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/res/img/error_blank.png"));
                ImageIcon iconRoll = new javax.swing.ImageIcon(getClass().getResource("/res/img/error_red.png"));

                btnClose.setIcon(icon);
                btnClose.setRolloverIcon(iconRoll);
                btnClose.setContentAreaFilled(false);
                btnClose.setBorder(null);

                btn.add(btnClose);
                
                pnlGaleria.add(btn);
                
                System.out.println("---------------------------");
                
            } catch (Exception e) {
                System.err.println("Erro ao ler imagem " + e);
            }
    }
    

    private void adicionar() {
        FileDialog fd = new FileDialog(Ouroboros.MAIN_VIEW, "Choose a file", FileDialog.LOAD);
        String[] extensoes = new String[] {".jpg", ".jpeg", ".png", ".gif"}; //Não está funcionando!!!!
        fd.setFilenameFilter(new SuffixFileFilter(extensoes));
        //fd.pack();
        fd.setVisible(true);
        String caminhoOrigem = fd.getDirectory() + fd.getFile();
        if (fd.getFile() != null) {
            
            //Criar pasta e copiar arquivo
            new File(caminhoPasta).mkdir();
            
            String extensao = fd.getFile().substring( fd.getFile().lastIndexOf(".") );
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String nomeArquivoDestino = LocalDateTime.now().format(formatter) + extensao;
            
            File arquivoOrigem = new File(caminhoOrigem);
            
            File arquivoDestino = new File(caminhoPasta + nomeArquivoDestino);
            
            try {
                Files.copy(arquivoOrigem.toPath(), arquivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(rootPane, "Erro ao copiar o arquivo " + e, "Erro", JOptionPane.ERROR_MESSAGE);
            }
            
            //salvar
            ProdutoImagem produtoImagem = new ProdutoImagem();
            produtoImagem.setArquivo(nomeArquivoDestino);
            
            produto.addProdutoImagem(produtoImagem);
            
            produtoImagem = produtoImagemDAO.save(produtoImagem);
            
            carregarGaleriaItem(produtoImagem);
            
            dimensionarGaleria();
            
            pnlGaleria.revalidate();
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

        jScrollPane1 = new javax.swing.JScrollPane();
        pnlGaleria = new javax.swing.JPanel();
        btnAdicionar = new javax.swing.JButton();

        setTitle("Imagens");
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
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        pnlGaleria.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout pnlGaleriaLayout = new javax.swing.GroupLayout(pnlGaleria);
        pnlGaleria.setLayout(pnlGaleriaLayout);
        pnlGaleriaLayout.setHorizontalGroup(
            pnlGaleriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1158, Short.MAX_VALUE)
        );
        pnlGaleriaLayout.setVerticalGroup(
            pnlGaleriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 469, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(pnlGaleria);

        btnAdicionar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnAdicionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-add-20.png"))); // NOI18N
        btnAdicionar.setText("Adicionar");
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAdicionar)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnAdicionar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown

    }//GEN-LAST:event_formComponentShown

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
    }//GEN-LAST:event_formInternalFrameActivated

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        produtoComponenteViews.remove(this);
    }//GEN-LAST:event_formInternalFrameClosed

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        adicionar();
    }//GEN-LAST:event_btnAdicionarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlGaleria;
    // End of variables declaration//GEN-END:variables
}
