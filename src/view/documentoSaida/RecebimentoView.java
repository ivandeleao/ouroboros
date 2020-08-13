/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoSaida;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import model.jtable.RecebimentoListaSimplesJTableModel;
import model.mysql.bean.principal.financeiro.Caixa;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.principal.financeiro.CaixaItemTipo;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.financeiro.Cheque;
import model.mysql.dao.principal.financeiro.CaixaDAO;
import model.mysql.dao.fiscal.MeioDePagamentoDAO;
import model.mysql.dao.principal.ParcelaDAO;
import model.mysql.dao.principal.financeiro.CaixaItemDAO;
import model.mysql.dao.principal.VendaDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.PARCELA_JUROS_MONETARIO_MENSAL;
import static ouroboros.Ouroboros.PARCELA_JUROS_PERCENTUAL_MENSAL;
import static ouroboros.Ouroboros.PARCELA_MULTA;
import static ouroboros.Ouroboros.TO_PRINTER_PATH;
import printing.documento.TermicaPrint;
import printing.PrintPDFBox;
import util.Decimal;
import util.JSwing;
import view.Toast;
import static ouroboros.Ouroboros.IMPRESSORA_CUPOM;
import util.DateTime;
import view.financeiro.cheque.ChequeCadastroView;
import view.financeiro.cheque.ChequePesquisaView;

/**
 *
 * @author ivand
 */
public class RecebimentoView extends javax.swing.JDialog {
    Venda documento;
    Caixa caixa = Ouroboros.FINANCEIRO_CAIXA_PRINCIPAL.getLastCaixa(); //2020-02-28
    Parcela parcela = new Parcela();
    CaixaItemDAO caixaItemDAO = new CaixaItemDAO();
    List<JFormattedTextField> txtRecebimentoList = new ArrayList<>();
    
    BigDecimal totalRecebido = new BigDecimal(BigInteger.ZERO);
    
    RecebimentoListaSimplesJTableModel recebimentoListaSimplesJTableModel = new RecebimentoListaSimplesJTableModel();
    
    protected RecebimentoView(java.awt.Frame parent, boolean modal) {
        super(MAIN_VIEW, modal);
        initComponents();
    }
    
    public RecebimentoView(Venda documento) {
        super(MAIN_VIEW, true);
        initComponents();
        
        definirAtalhos();
        
        this.setTitle(this.getTitle() + " (" + this.getClass().getCanonicalName() + ")");
        
        gerarCamposDePagamento();
        
        JSwing.startComponentsBehavior(this);
        
        this.documento = documento;
        
        formatarTabela();
        
        carregarTabela();
        
        txtTotal.setText(Decimal.toString(documento.getTotal()));
        txtEmAberto.setText(Decimal.toString(documento.getTotalEmAberto()));
        
        
        
        this.setLocationRelativeTo(this);
        this.setVisible(true);
        
        
    }
    
    public Parcela getParcela(){
        return parcela;
    }
    
    private void definirAtalhos() {
        //JRootPane rootPane = this.getRootPane();
        InputMap im = rootPane.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = rootPane.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "fechar");
        am.put("fechar", new RecebimentoView.FormKeyStroke("ESC"));
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "confirmarComRecibo");
        am.put("confirmarComRecibo", new RecebimentoView.FormKeyStroke("F11"));
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "confirmar");
        am.put("confirmar", new RecebimentoView.FormKeyStroke("F12"));
    }
    
    protected class FormKeyStroke extends AbstractAction {
        private final String key;
        public FormKeyStroke(String key){
            this.key = key;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            switch(key){
                case "ESC":
                    dispose();
                    break;
                case "F11":
                    confirmar(true);
                    break;
                case "F12":
                    confirmar(false);
                    break;
            }
        }
    }
    
    KeyListener keyListenerRecebido = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {
            //do nothing
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                dispose();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            somarPagamentos();
        }

    };
    
    private void formatarTabela() {
        tblRecebimentos.setModel(recebimentoListaSimplesJTableModel);

        tblRecebimentos.setRowHeight(30);
        tblRecebimentos.setIntercellSpacing(new Dimension(10, 10));

        tblRecebimentos.getColumn("Id").setPreferredWidth(100);
        tblRecebimentos.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblRecebimentos.getColumn("Data Hora").setPreferredWidth(180);
        tblRecebimentos.getColumn("Data Hora").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblRecebimentos.getColumn("MP").setPreferredWidth(100);
        tblRecebimentos.getColumn("MP").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblRecebimentos.getColumn("Crédito").setPreferredWidth(100);
        tblRecebimentos.getColumn("Crédito").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblRecebimentos.getColumn("Débito").setPreferredWidth(100);
        tblRecebimentos.getColumn("Débito").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

    }

    private void carregarTabela() {
        recebimentoListaSimplesJTableModel.clear();
        recebimentoListaSimplesJTableModel.addList(documento.getRecebimentos());
    }
    
    private void clickTabela() {
        CaixaItem caixaItem = recebimentoListaSimplesJTableModel.getRow(tblRecebimentos.getSelectedRow());
        if (caixaItem.getMeioDePagamento().equals(MeioDePagamento.CHEQUE)) {
            if (caixaItem.getCheque() != null) {
                new ChequeCadastroView(caixaItem.getCheque());

            } else {
                ChequePesquisaView chequePesquisaView = new ChequePesquisaView();
                Cheque cheque = chequePesquisaView.getCheque();
                if (cheque != null) {
                    //cheque.setUtilizado(LocalDateTime.now());
                    new ChequeCadastroView(cheque, caixaItem);
                }
            }
            carregarTabela();
        }
        
    }
    
    private void proximoCampo() {
        if(totalRecebido.compareTo(documento.getTotalEmAberto()) >= 0) {
            confirmar(false);
        }
        System.out.println("recebido: " + totalRecebido);
    }
    
    
    private void gerarCamposDePagamento(){
        MeioDePagamentoDAO mpDAO = new MeioDePagamentoDAO();
        List<MeioDePagamento> mps = mpDAO.findAllEnabled();
        
        int x = 0;
        int y = 0;
        
        int width = 500;
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
            text.setPreferredSize(new Dimension(300, 48));
            Font font = new Font("Tahoma", Font.BOLD, 36);
            text.setFont(font);
            text.setText("0");
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
    
    private void somarPagamentos(){
        totalRecebido = BigDecimal.ZERO;
        for(JFormattedTextField txtRecebimento : txtRecebimentoList){
            BigDecimal valorRecebido = Decimal.fromString(txtRecebimento.getText());
            
            
            totalRecebido = totalRecebido.add(valorRecebido);
        }
        txtTotalRecebido.setText(Decimal.toString(totalRecebido));
        
        System.out.println("totalRecebido: " + totalRecebido);
        System.out.println("venda.getTotalEmAberto(): " + documento.getTotalEmAberto());
        
        BigDecimal troco = totalRecebido.subtract(documento.getTotalEmAberto());
        
        System.out.println("troco: " + troco);
        
        txtTroco.setText(Decimal.toString(troco));
    }
    
    private void alterarDataRecebimento() {
        txtDataRecebimento.setEditable(chkAlterarDataRecebimento.isSelected());
        if (chkAlterarDataRecebimento.isSelected()) {
            txtDataRecebimento.requestFocus();
        }
    }
    
    
    private void confirmar(boolean imprimir){
        
        LocalDate dataRecebimento = DateTime.fromStringToLocalDate(txtDataRecebimento.getText());
        
        
        if (documento.getTotalEmAberto().compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(rootPane, "Não há valor em aberto.", "Atenção", JOptionPane.WARNING_MESSAGE);
            
        } else if (chkAlterarDataRecebimento.isSelected() && dataRecebimento == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Data inválida.", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtDataRecebimento.requestFocus();
            
        } else if (chkAlterarDataRecebimento.isSelected() && dataRecebimento.compareTo(LocalDate.now()) > 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Data não pode ser futura.", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtDataRecebimento.requestFocus();
            
        } else {
        
            CaixaItem caixaItemCheque = null; //2020-04-20 para cadastrar se houver cheque

            somarPagamentos();
            if(totalRecebido.compareTo(BigDecimal.ZERO) > 0 ) {

                parcela = new Parcela(null, BigDecimal.ZERO, PARCELA_MULTA, PARCELA_JUROS_MONETARIO_MENSAL, PARCELA_JUROS_PERCENTUAL_MENSAL, null);

                //2019-03-11 - tentando corrigir o bug de parcelas e recebimentos fantasmas
                parcela = new ParcelaDAO().save(parcela);
                documento.addParcela(parcela);
                documento = new VendaDAO().save(documento);
                //

                //BigDecimal totalRecebido = BigDecimal.ZERO;
                for(JFormattedTextField txtRecebimento : txtRecebimentoList){
                    BigDecimal valor = Decimal.fromString(txtRecebimento.getText());
                    BigDecimal valorRecebido = BigDecimal.ZERO;
                    BigDecimal valorPago = BigDecimal.ZERO;
                    CaixaItemTipo caixaItemTipo;

                    if(documento.getTipoOperacao().equals(TipoOperacao.SAIDA)) {
                        //venda
                        valorRecebido = valor;
                        //caixaItemTipo = CaixaItemTipo.DOCUMENTO;
                    } else {
                        //compra
                        valorPago = valor;
                        //caixaItemTipo = CaixaItemTipo.PAGAMENTO_DOCUMENTO;
                    }
                    caixaItemTipo = CaixaItemTipo.DOCUMENTO;


                    if(valor.compareTo(BigDecimal.ZERO) > 0){
                        MeioDePagamento mp = new MeioDePagamentoDAO().findById(Integer.valueOf(txtRecebimento.getToolTipText()));

                        CaixaItem caixaItem = new CaixaItem(caixa, caixaItemTipo, mp, null, valorRecebido, valorPago);
                        
                        //2020-07-10
                        if (chkAlterarDataRecebimento.isSelected()) {
                            caixaItem.setDataHoraRecebimento(dataRecebimento.atTime(LocalTime.MIN));
                        }


                        if (mp.equals(MeioDePagamento.CHEQUE)) { //2020-04-20 cadastrar cheque
                            caixaItemCheque = caixaItem;
                        }

                        parcela.setMeioDePagamento(mp); //2020-06-12
                        
                        parcela.addRecebimento(caixaItem);

                        caixaItemDAO.save(caixaItem);

                        parcela = new ParcelaDAO().save(parcela); //tem que salvar antes para conseguir calcular o saldo na sequência

                        documento.addParcela(parcela);

                        documento = new VendaDAO().save(documento);
                    }
                }

                //parcela = new ParcelaDAO().save(parcela); //tem que salvar antes para conseguir calcular o saldo na sequência

                BigDecimal troco = Decimal.fromString(txtTroco.getText());
                if(troco.compareTo(BigDecimal.ZERO) > 0){
                    totalRecebido = totalRecebido.subtract(troco);

                    BigDecimal trocoRecebido = BigDecimal.ZERO;
                    BigDecimal trocoPago = BigDecimal.ZERO;

                    if(documento.getTipoOperacao().equals(TipoOperacao.SAIDA)) {
                        trocoPago = troco; //venda
                    } else {
                        trocoRecebido = troco; //compra
                    }

                    CaixaItem r = new CaixaItem(caixa, CaixaItemTipo.TROCO, MeioDePagamento.DINHEIRO, null, trocoRecebido, trocoPago);

                    ////r = caixaItemDAO.save(r);

                    parcela.addRecebimento(r);

                    caixaItemDAO.save(r);
                }
                /*
                foi corrigido recebimento colocando em cascadeType All a relação do recebimento com a parcela
                        adicionando os recebimentos na mesma
                                adicionando a parcela na documento
                                        e finalmente salvando a documento!!!
                */

                //parcela.setRecebimentos(recebimentos);
                parcela.setValor(totalRecebido);
                parcela = new ParcelaDAO().save(parcela); //sem o save, ao imprimir, a data de criação estava nula

                documento.addParcela(parcela);

                documento = new VendaDAO().save(documento);



                if(imprimir) {
                    imprimir();
                }


                dispose();

                if (caixaItemCheque != null) {
                    
                    if (documento.getTipoOperacao().equals(TipoOperacao.SAIDA)) {
                        Cheque cheque = new Cheque();
                        cheque.setValor(caixaItemCheque.getCredito());
                        new ChequeCadastroView(cheque, caixaItemCheque);

                    } else {
                        ChequePesquisaView chequePesquisaView = new ChequePesquisaView();
                        Cheque cheque = chequePesquisaView.getCheque();
                        cheque.setUtilizado(LocalDateTime.now());
                        new ChequeCadastroView(cheque, caixaItemCheque);
                    }
                    
                }
            }
        }
    }
    
    private void imprimir() {
        String pdfFilePath = TO_PRINTER_PATH + "RECIBO DE PAGAMENTO_" + System.currentTimeMillis() + ".pdf";
        List<Parcela> parcelaList = new ArrayList<>();
        parcelaList.add(parcela);
        //System.out.println("parcela: " + parcela.getValor());
        TermicaPrint.gerarRecibo(parcelaList, pdfFilePath);

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
        jLabel7 = new javax.swing.JLabel();
        txtEmAberto = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txtTotalRecebido = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        txtTroco = new javax.swing.JFormattedTextField();
        txtTotal = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        btnConfirmarComRecibo = new javax.swing.JButton();
        btnConfirmar1 = new javax.swing.JButton();
        btnConfirmar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRecebimentos = new javax.swing.JTable();
        jLabel37 = new javax.swing.JLabel();
        chkAlterarDataRecebimento = new javax.swing.JCheckBox();
        txtDataRecebimento = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Recebimento / Pagamento");

        pnlMPs.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout pnlMPsLayout = new javax.swing.GroupLayout(pnlMPs);
        pnlMPs.setLayout(pnlMPsLayout);
        pnlMPsLayout.setHorizontalGroup(
            pnlMPsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 668, Short.MAX_VALUE)
        );
        pnlMPsLayout.setVerticalGroup(
            pnlMPsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 575, Short.MAX_VALUE)
        );

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel7.setText("EM ABERTO");

        txtEmAberto.setEditable(false);
        txtEmAberto.setForeground(java.awt.Color.red);
        txtEmAberto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtEmAberto.setText("0,00");
        txtEmAberto.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel8.setText("RECEBIDO");

        txtTotalRecebido.setEditable(false);
        txtTotalRecebido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalRecebido.setText("0,00");
        txtTotalRecebido.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel9.setText("TOTAL");

        txtTroco.setEditable(false);
        txtTroco.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTroco.setText("0,00");
        txtTroco.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N

        txtTotal.setEditable(false);
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotal.setText("0,00");
        txtTotal.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel11.setText("TROCO");

        btnConfirmarComRecibo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnConfirmarComRecibo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-printer-20.png"))); // NOI18N
        btnConfirmarComRecibo.setText("F11  RECIBO");
        btnConfirmarComRecibo.setContentAreaFilled(false);
        btnConfirmarComRecibo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnConfirmarComRecibo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarComReciboActionPerformed(evt);
            }
        });

        btnConfirmar1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnConfirmar1.setText("ESC FECHAR");
        btnConfirmar1.setContentAreaFilled(false);
        btnConfirmar1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnConfirmar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmar1ActionPerformed(evt);
            }
        });

        btnConfirmar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnConfirmar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-checkmark-20.png"))); // NOI18N
        btnConfirmar.setText("F12 CONFIRMAR");
        btnConfirmar.setContentAreaFilled(false);
        btnConfirmar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarActionPerformed(evt);
            }
        });

        tblRecebimentos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblRecebimentos.setModel(new javax.swing.table.DefaultTableModel(
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
        tblRecebimentos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblRecebimentosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblRecebimentos);

        jLabel37.setBackground(new java.awt.Color(122, 138, 153));
        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel37.setForeground(java.awt.Color.white);
        jLabel37.setText("Lançamentos Anteriores");
        jLabel37.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel37.setOpaque(true);

        chkAlterarDataRecebimento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        chkAlterarDataRecebimento.setText("Alterar Data do Evento");
        chkAlterarDataRecebimento.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        chkAlterarDataRecebimento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAlterarDataRecebimentoActionPerformed(evt);
            }
        });

        txtDataRecebimento.setEditable(false);
        txtDataRecebimento.setForeground(java.awt.Color.blue);
        txtDataRecebimento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDataRecebimento.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtDataRecebimento.setName("data"); // NOI18N
        txtDataRecebimento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDataRecebimentoKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlMPs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTotalRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTroco, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnConfirmar1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnConfirmarComRecibo)
                        .addGap(18, 18, 18)
                        .addComponent(btnConfirmar))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 63, Short.MAX_VALUE)
                        .addComponent(txtEmAberto, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1)
                    .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chkAlterarDataRecebimento)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtDataRecebimento, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlMPs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtEmAberto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTotalRecebido)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTroco)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDataRecebimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkAlterarDataRecebimento))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnConfirmarComRecibo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnConfirmar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                            .addComponent(btnConfirmar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmarComReciboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarComReciboActionPerformed
        confirmar(true);
    }//GEN-LAST:event_btnConfirmarComReciboActionPerformed

    private void btnConfirmar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmar1ActionPerformed
        dispose();
    }//GEN-LAST:event_btnConfirmar1ActionPerformed

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        confirmar(false);
    }//GEN-LAST:event_btnConfirmarActionPerformed

    private void tblRecebimentosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRecebimentosMouseClicked
        if (evt.getClickCount() == 2) {
            clickTabela();
        }
    }//GEN-LAST:event_tblRecebimentosMouseClicked

    private void chkAlterarDataRecebimentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAlterarDataRecebimentoActionPerformed
        alterarDataRecebimento();
    }//GEN-LAST:event_chkAlterarDataRecebimentoActionPerformed

    private void txtDataRecebimentoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDataRecebimentoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDataRecebimentoKeyReleased

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
            java.util.logging.Logger.getLogger(RecebimentoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RecebimentoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RecebimentoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RecebimentoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RecebimentoView dialog = new RecebimentoView(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnConfirmar1;
    private javax.swing.JButton btnConfirmarComRecibo;
    private javax.swing.JCheckBox chkAlterarDataRecebimento;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlMPs;
    private javax.swing.JTable tblRecebimentos;
    private javax.swing.JFormattedTextField txtDataRecebimento;
    private javax.swing.JFormattedTextField txtEmAberto;
    private javax.swing.JFormattedTextField txtTotal;
    private javax.swing.JFormattedTextField txtTotalRecebido;
    private javax.swing.JFormattedTextField txtTroco;
    // End of variables declaration//GEN-END:variables
}
