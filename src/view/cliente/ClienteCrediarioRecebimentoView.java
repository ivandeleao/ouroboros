/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.cliente;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import model.bean.principal.Parcela;
import model.bean.fiscal.MeioDePagamento;
import model.bean.principal.Caixa;
import model.bean.principal.CaixaItem;
import model.bean.principal.CaixaItemTipo;
import model.dao.principal.ParcelaDAO;
import model.dao.fiscal.MeioDePagamentoDAO;
import model.dao.principal.CaixaDAO;
import model.dao.principal.CaixaItemDAO;
import model.jtable.CrediarioRecebimentoJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.TO_PRINTER_PATH;
import static ouroboros.Ouroboros.em;
import printing.CriarPDF;
import printing.PrintPDFBox;
import util.Decimal;
import util.JSwing;
import view.Toast;
import static ouroboros.Ouroboros.IMPRESSORA_CUPOM;

/**
 *
 * @author ivand
 *
 * ESTE FOI CRIADO COMO JDIALOG DESDE O INÍCIO - USAR ESTE COMO REFERÊNCIA PARA
 * OS DEMAIS
 *
 *
 */
public class ClienteCrediarioRecebimentoView extends javax.swing.JDialog {
    Caixa caixa = new CaixaDAO().getLastCaixa();
    List<Parcela> parcelaList = new ArrayList<>();
    ParcelaDAO parcelaDAO = new ParcelaDAO();
    CrediarioRecebimentoJTableModel crediarioRecebimentoJTableModel = new CrediarioRecebimentoJTableModel();
    CaixaItemDAO recebimentoDAO = new CaixaItemDAO();
    List<JFormattedTextField> txtRecebimentoList = new ArrayList<>();
    BigDecimal total, multa, juros, totalAtual;

    /**
     * Creates new form ParcelamentoView
     */
    public ClienteCrediarioRecebimentoView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public ClienteCrediarioRecebimentoView(java.awt.Frame parent, List<Parcela> parcelaList) {
        super(parent, true);
        initComponents();
        
        gerarCamposDePagamento();

        JSwing.startComponentsBehavior(this);
        
        this.parcelaList = parcelaList;
        
        tblParcelas.setModel(crediarioRecebimentoJTableModel);

        formatarTabela();
        
        carregarDados();
        

        this.setLocationRelativeTo(this); //centralizar
        this.setVisible(true);
    }
    
    private void carregarDados() {
        exibirTotais();
        
        carregarTabela();
    }
    
    private void exibirTotais() {
        total = parcelaList.stream().map(Parcela::getValor).reduce(BigDecimal::add).get();
        multa = parcelaList.stream().map(Parcela::getMultaCalculada).reduce(BigDecimal::add).get();
        juros = parcelaList.stream().map(Parcela::getJurosCalculado).reduce(BigDecimal::add).get();
        totalAtual = parcelaList.stream().map(Parcela::getValorAtual).reduce(BigDecimal::add).get();
        totalAtual = totalAtual.setScale(2, RoundingMode.HALF_UP);
        if(chkNaoCobrarMulta.isSelected()) {
            totalAtual = totalAtual.subtract(multa);
        }
        if(chkNaoCobrarJuros.isSelected()) {
            totalAtual = totalAtual.subtract(juros);
        }
        
        txtTotal.setText(Decimal.toString(total));
        txtMulta.setText(Decimal.toString(multa));
        txtJuros.setText(Decimal.toString(juros));
        txtTotalAtual.setText(Decimal.toString(totalAtual));
    }

    
    private void formatarTabela() {
        tblParcelas.setRowHeight(24);
        tblParcelas.setIntercellSpacing(new Dimension(10, 10));
        //id
        tblParcelas.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblParcelas.getColumnModel().getColumn(0).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //venda
        tblParcelas.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblParcelas.getColumnModel().getColumn(1).setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        //número
        tblParcelas.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblParcelas.getColumnModel().getColumn(2).setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        //vencimento
        tblParcelas.getColumnModel().getColumn(3).setPreferredWidth(120);
        tblParcelas.getColumnModel().getColumn(3).setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        //valor
        tblParcelas.getColumnModel().getColumn(4).setPreferredWidth(120);
        tblParcelas.getColumnModel().getColumn(4).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //dias em atraso
        tblParcelas.getColumnModel().getColumn(5).setPreferredWidth(120);
        tblParcelas.getColumnModel().getColumn(5).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //multa
        tblParcelas.getColumnModel().getColumn(6).setPreferredWidth(120);
        tblParcelas.getColumnModel().getColumn(6).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //multa calculada
        tblParcelas.getColumnModel().getColumn(7).setPreferredWidth(120);
        tblParcelas.getColumnModel().getColumn(7).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //juros
        tblParcelas.getColumnModel().getColumn(8).setPreferredWidth(120);
        tblParcelas.getColumnModel().getColumn(8).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //juros calculado
        tblParcelas.getColumnModel().getColumn(9).setPreferredWidth(120);
        tblParcelas.getColumnModel().getColumn(9).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //valor atual
        tblParcelas.getColumnModel().getColumn(10).setPreferredWidth(120);
        tblParcelas.getColumnModel().getColumn(10).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //meio de pagamento
        tblParcelas.getColumnModel().getColumn(11).setPreferredWidth(180);
    }

    private void carregarTabela() {
        int index = tblParcelas.getSelectedRow();
        
        crediarioRecebimentoJTableModel.clear();
        crediarioRecebimentoJTableModel.addList(parcelaList);

        if (index >= 0) {
            tblParcelas.setRowSelectionInterval(index, index);
        } else if (parcelaList.size() > 0) {
            index = tblParcelas.getRowCount() - 1;
            tblParcelas.setRowSelectionInterval(index, index);
            tblParcelas.scrollRectToVisible(tblParcelas.getCellRect(index, 0, true));
        }
        /*
        if (parcelaList.size() > 0) {
            int index = tblParcelas.getRowCount() - 1;
            tblParcelas.setRowSelectionInterval(index, index);
            tblParcelas.scrollRectToVisible(tblParcelas.getCellRect(index, 0, true));
        }*/
        
        exibirTotais();
    }
    
    private void somarPagamentos(){
        BigDecimal totalRecebido = new BigDecimal(BigInteger.ZERO);
        for(JFormattedTextField txtRecebimento : txtRecebimentoList){
            BigDecimal valorRecebido = Decimal.fromString(txtRecebimento.getText());
            
            //System.out.println("valorRecebido: " + valorRecebido);
            
            totalRecebido = totalRecebido.add(valorRecebido);
            //System.out.println("total:" + totalRecebido);
        }
        txtTotalRecebido.setText(Decimal.toString(totalRecebido));
        
        //BigDecimal total = venda.getTotal();
        BigDecimal troco = totalRecebido.subtract(totalAtual);
        
        //txtTotal.setText(Decimal.toString(total));
        txtTroco.setText(Decimal.toString(troco));
    }

    KeyListener keyListenerRecebido = new KeyListener() {
        @Override
        public void keyTyped(java.awt.event.KeyEvent e) {
            //do nothing
        }

        @Override
        public void keyPressed(java.awt.event.KeyEvent e) {
            //do nothing
        }

        @Override
        public void keyReleased(java.awt.event.KeyEvent e) {
            somarPagamentos();
        }

    };
    
    private void gerarCamposDePagamento(){
        MeioDePagamentoDAO mpDAO = new MeioDePagamentoDAO();
        List<MeioDePagamento> mps = mpDAO.findAllEnabled();
        
        int x = 0;
        int y = 0;
        
        int width = 400;
        int height = 52;
        
        for(MeioDePagamento mp : mps){
            
            JLabel label = new JLabel(mp.getNome());
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setPreferredSize(new Dimension(150, 30));
            //label.setBorder(BorderFactory.createEtchedBorder());
            Font labelFont = new Font("Tahoma", Font.BOLD, 14);
            label.setFont(labelFont);
            
            JFormattedTextField text = new JFormattedTextField();
            text.setName("decimal");
            text.setToolTipText(mp.getId().toString()); //usado ao gravar os pagamentos
            text.setPreferredSize(new Dimension(200, 48));
            Font font = new Font("Tahoma", Font.BOLD, 36);
            text.setFont(font);
            //text.setText("5000");
            txtRecebimentoList.add(text);
            
            

            text.addKeyListener(keyListenerRecebido);
            
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT)); //Alinhar elementos a esquerda
            //panel.setBorder(BorderFactory.createEtchedBorder());
            panel.setBounds(5, 2 + y * height, width, height);
            panel.add(label);
            panel.add(text);
            
            pnlMPs.add(panel);

            y++;

        }
        
        txtRecebimentoList.get(0).requestFocus();
    }
    
    private void editar() {
        Parcela p = crediarioRecebimentoJTableModel.getRow(tblParcelas.getSelectedRow());
        if(p.getRecebido().compareTo(BigDecimal.ZERO) > 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Parcela quitada. Não é possível editar.", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            ClienteParcelaEditarView edtView  = new ClienteParcelaEditarView(MAIN_VIEW, p);
            carregarTabela();
        }
    }
    
    private void confirmar() {
        Map<Parcela,List<CaixaItem>> mapParcelaRecebimentos = new HashMap<>();
        
        //Pegar valores de cada meio de pagamento
        Map<MeioDePagamento, BigDecimal> mapMpValor = new HashMap<>();
        
        BigDecimal totalRecebido = BigDecimal.ZERO;
        BigDecimal totalRecGeral = BigDecimal.ZERO;
                
        for(JFormattedTextField txtRecebimento : txtRecebimentoList){
            BigDecimal valorRecebido = Decimal.fromString(txtRecebimento.getText());
            if(valorRecebido.compareTo(BigDecimal.ZERO) > 0){
                totalRecebido = totalRecebido.add(valorRecebido);

                MeioDePagamento mp = new MeioDePagamentoDAO().findById(Integer.valueOf(txtRecebimento.getToolTipText()));
                
                mapMpValor.put(mp, valorRecebido);
            }
        }
        
        
        
        //----------
        BigDecimal totalParcelas = parcelaList.stream().map(Parcela::getValor).reduce(BigDecimal::add).get();
        
        if(mapMpValor.isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não há valor recebido.", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtRecebimentoList.get(0).requestFocus();
        /*} else if (totalRecebido.compareTo(totalAtual) < 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "O valor recebido " + totalRecebido +" é menor que o total atualizado " + totalAtual, "Atenção", JOptionPane.WARNING_MESSAGE);
            txtRecebimentoList.get(0).requestFocus();*/
        } else {

            //Preparar map com as parcelas para inserir recebimentos
            for(Parcela parcela : parcelaList) {
                
                //zerar multa e juros
                if(chkNaoCobrarMulta.isSelected()) {
                    parcela.setMulta(BigDecimal.ZERO);
                    parcelaDAO.save(parcela);
                }
                if(chkNaoCobrarJuros.isSelected()) {
                    parcela.setJurosMonetario(BigDecimal.ZERO);
                    parcela.setJurosPercentual(BigDecimal.ZERO);
                    parcelaDAO.save(parcela);
                }
                
                
                List<CaixaItem> recebimentos = new ArrayList<>();

                mapParcelaRecebimentos.put(parcela, recebimentos);
                
                
                System.out.println("totalRecebido: " + totalRecebido);
                System.out.println("totalRecGeral: " + totalRecGeral);
                
                

                BigDecimal totalRecParcela = BigDecimal.ZERO;

                //enquanto valor da parcela for maior que soma dos recebimentos dados a ela
                do {
                    //System.out.println("------------");
                    //System.out.println("parcela: " + parcela.getNumero() + " - " + parcela.getValor());

                    for(Map.Entry<MeioDePagamento, BigDecimal> mpValor : mapMpValor.entrySet()){
                        if(mpValor.getValue().compareTo(BigDecimal.ZERO) > 0) {
                            MeioDePagamento mp = mpValor.getKey();
                            BigDecimal credito = mpValor.getValue();

                            BigDecimal restante = parcela.getValorAtual().subtract(totalRecParcela);

                            if(mpValor.getValue().compareTo(parcela.getValorAtual()) > 0) {
                                credito = parcela.getValorAtual();
                                if(mpValor.getValue().compareTo(restante) > 0) {
                                    credito = restante;
                                }
                            }
                            mpValor.setValue(mpValor.getValue().subtract(credito));
                        
                        
                            System.out.println("recebimento: " + mp + " - " + credito);
                            CaixaItem recebimento = new CaixaItem(caixa, CaixaItemTipo.RECEBIMENTO_DE_VENDA, mp, "", credito, BigDecimal.ZERO);
                            //recebimentos.add(recebimento);
                            recebimento = recebimentoDAO.save(recebimento);
                            parcela.addRecebimento(recebimento);
                            
                            totalRecParcela = totalRecParcela.add(credito);
                        }
                        
                        //if(!recebimentos.isEmpty()){
                        if(!parcela.getRecebimentos().isEmpty()) {
                            //BigDecimal parcial = recebimentos.stream().map(CaixaItem::getCredito).reduce(BigDecimal::add).get();
                            
                            //totalRecGeral = totalRecGeral.add(parcial);
                            
                            totalRecGeral = parcela.getRecebido();
                        }
                        
                        
                        if(totalRecParcela.compareTo(parcela.getValor()) >= 0) {
                            break;
                        }
                        
                        
                        
                    }

                    
                    
                    System.out.println("totalRecebimentos: " + totalRecGeral);
                    System.out.println("------------------------------------");
                    
                    
                } while(parcela.getValorAtual().compareTo(totalRecGeral) > 0
                        &&
                        totalRecebido.compareTo(totalRecGeral) < 0
                        );

                parcela = parcelaDAO.save(parcela);
                
                //lançar no caixa
                //for(CaixaItem ci : recebimentos) {
                //    recebimentoDAO.save(ci);
                    //em.refresh(ci);
                //}
                
                
                
                //em.refresh(parcela);

            }
            
            int resposta = JOptionPane.showConfirmDialog(MAIN_VIEW, "Imprimir recibo?", "Imprimir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(resposta == JOptionPane.YES_OPTION) {
                imprimir();
            }
            
            dispose();
        }
    }
    
    private void imprimir() {
        String pdfFilePath = TO_PRINTER_PATH + "RECIBO DE PAGAMENTO_" + System.currentTimeMillis() + ".pdf";
        CriarPDF.criarRecibo80mm(parcelaList, pdfFilePath);

        new Toast("Imprimindo...");

        PrintPDFBox pPDF = new PrintPDFBox();
        pPDF.print(pdfFilePath, IMPRESSORA_CUPOM);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMPs = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtTotalRecebido = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        txtTroco = new javax.swing.JFormattedTextField();
        txtTotal = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblParcelas = new javax.swing.JTable();
        txtMulta = new javax.swing.JFormattedTextField();
        txtJuros = new javax.swing.JFormattedTextField();
        txtTotalAtual = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        chkNaoCobrarMulta = new javax.swing.JCheckBox();
        chkNaoCobrarJuros = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Recebimento de Parcelas");
        setResizable(false);

        pnlMPs.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout pnlMPsLayout = new javax.swing.GroupLayout(pnlMPs);
        pnlMPs.setLayout(pnlMPsLayout);
        pnlMPsLayout.setHorizontalGroup(
            pnlMPsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 477, Short.MAX_VALUE)
        );
        pnlMPsLayout.setVerticalGroup(
            pnlMPsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 303, Short.MAX_VALUE)
        );

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel8.setText("RECEBIDO");

        txtTotalRecebido.setEditable(false);
        txtTotalRecebido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalRecebido.setText("0,00");
        txtTotalRecebido.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel11.setText("TROCO");

        txtTroco.setEditable(false);
        txtTroco.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTroco.setText("0,00");
        txtTroco.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        txtTotal.setEditable(false);
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotal.setText("0,00");
        txtTotal.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel9.setText("TOTAL");

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/resource/img/flag_green.png"))); // NOI18N
        jButton1.setText("OK");
        jButton1.setContentAreaFilled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnCancelar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/resource/img/flag_red.png"))); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.setContentAreaFilled(false);
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        tblParcelas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblParcelas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblParcelasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblParcelas);

        txtMulta.setEditable(false);
        txtMulta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMulta.setText("0,00");
        txtMulta.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        txtJuros.setEditable(false);
        txtJuros.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtJuros.setText("0,00");
        txtJuros.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        txtTotalAtual.setEditable(false);
        txtTotalAtual.setForeground(java.awt.Color.red);
        txtTotalAtual.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalAtual.setText("0,00");
        txtTotalAtual.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel10.setText("VALOR ATUALIZADO");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel12.setText("MULTA");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel13.setText("JUROS");

        chkNaoCobrarMulta.setText("NÃO COBRAR");
        chkNaoCobrarMulta.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        chkNaoCobrarMulta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkNaoCobrarMultaActionPerformed(evt);
            }
        });

        chkNaoCobrarJuros.setText("NÃO COBRAR");
        chkNaoCobrarJuros.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        chkNaoCobrarJuros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkNaoCobrarJurosActionPerformed(evt);
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
                        .addComponent(pnlMPs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtTroco, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtTotalRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtTotalAtual, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 95, Short.MAX_VALUE)
                                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(chkNaoCobrarMulta))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel13)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(chkNaoCobrarJuros)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtMulta, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtJuros, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE))))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMulta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkNaoCobrarMulta))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtJuros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkNaoCobrarJuros))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotalAtual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotalRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTroco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(pnlMPs, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblParcelasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblParcelasMouseClicked
        if(evt.getClickCount() == 2) {
            editar();
        }
    }//GEN-LAST:event_tblParcelasMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        confirmar();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void chkNaoCobrarMultaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkNaoCobrarMultaActionPerformed
        exibirTotais();
    }//GEN-LAST:event_chkNaoCobrarMultaActionPerformed

    private void chkNaoCobrarJurosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkNaoCobrarJurosActionPerformed
        exibirTotais();
    }//GEN-LAST:event_chkNaoCobrarJurosActionPerformed

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
            java.util.logging.Logger.getLogger(ClienteCrediarioRecebimentoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClienteCrediarioRecebimentoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClienteCrediarioRecebimentoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClienteCrediarioRecebimentoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ClienteCrediarioRecebimentoView dialog = new ClienteCrediarioRecebimentoView(new javax.swing.JFrame(), true);
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
    private javax.swing.JCheckBox chkNaoCobrarJuros;
    private javax.swing.JCheckBox chkNaoCobrarMulta;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlMPs;
    private javax.swing.JTable tblParcelas;
    private javax.swing.JFormattedTextField txtJuros;
    private javax.swing.JFormattedTextField txtMulta;
    private javax.swing.JFormattedTextField txtTotal;
    private javax.swing.JFormattedTextField txtTotalAtual;
    private javax.swing.JFormattedTextField txtTotalRecebido;
    private javax.swing.JFormattedTextField txtTroco;
    // End of variables declaration//GEN-END:variables
}
