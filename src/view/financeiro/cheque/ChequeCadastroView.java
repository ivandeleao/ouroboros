/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.financeiro.cheque;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import model.jtable.financeiro.cheque.ChequeCaixaItensJTableModel;
import model.jtable.financeiro.cheque.ChequeParcelasJTableModel;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.principal.financeiro.Cheque;
import model.mysql.dao.principal.ParcelaDAO;
import model.mysql.dao.principal.financeiro.CaixaItemDAO;
import model.mysql.dao.principal.financeiro.ChequeDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;
import util.Decimal;
import util.JSwing;

/**
 *
 * @author ivand
 */
public class ChequeCadastroView extends javax.swing.JDialog {

    private Cheque cheque;
    private final ChequeDAO chequeDAO = new ChequeDAO();
    private final ChequeCaixaItensJTableModel chequeCaixaItensJTableModel = new ChequeCaixaItensJTableModel();
    private final ChequeParcelasJTableModel chequeParcelasJTableModel = new ChequeParcelasJTableModel();
    
    private List<CaixaItem> caixaItens = new ArrayList<>();
    private final CaixaItemDAO caixaItemDAO = new CaixaItemDAO();
    
    private Parcela parcela;
    private final ParcelaDAO parcelaDAO = new ParcelaDAO();

    protected ChequeCadastroView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        JSwing.startComponentsBehavior(this);
    }

    public ChequeCadastroView(Cheque cheque) {
        this(cheque, new ArrayList<>(), null);
    }

    public ChequeCadastroView(CaixaItem caixaItem) {
        this(caixaItem.getCheque(), new ArrayList<>(Arrays.asList(caixaItem)), null);
    }
    
    public ChequeCadastroView(Cheque cheque, CaixaItem caixaItem) {
        this(cheque, new ArrayList<>(Arrays.asList(caixaItem)), null);
    }
    
    public ChequeCadastroView(List<CaixaItem> caixaItens) {
        this(null, caixaItens, null);
    }
    
    public ChequeCadastroView(Cheque cheque, List<CaixaItem> caixaItens) {
        this(cheque, caixaItens, null);
    }
    
    public ChequeCadastroView(Parcela parcela) {
        this(parcela.getCheque(), null, parcela);
    }
    
    public ChequeCadastroView(Cheque cheque, Parcela parcela) {
        this(cheque, null, parcela);
    }

    public ChequeCadastroView(Cheque cheque, List<CaixaItem> caixaItens, Parcela parcela) {
        super(MAIN_VIEW, true);
        initComponents();
        JSwing.startComponentsBehavior(this);

        definirAtalhos();

        
        this.cheque = cheque != null ? cheque : new Cheque();
        this.caixaItens = caixaItens;
        this.parcela = parcela;

        carregarDados();

        formatarTabelaCaixaItens();
        carregarTabelaCaixaItens();
        
        formatarTabelaParcelas();
        carregarTabelaParcelas();

        this.setLocationRelativeTo(MAIN_VIEW);
        this.setVisible(true);
    }

    private void definirAtalhos() {
        InputMap im = rootPane.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = rootPane.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "fechar");
        am.put("fechar", new FormKeyStroke("ESC"));

    }

    protected class FormKeyStroke extends AbstractAction {

        private final String key;

        public FormKeyStroke(String key) {
            this.key = key;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (key) {
                case "ESC":
                    dispose();
                    break;
            }
        }
    }

    public Cheque getCheque() {
        return cheque;
    }

    private void carregarDados() {

        txtId.setText(cheque.getId() != null ? cheque.getId().toString() : "Novo");
        
        txtBanco.setText(cheque.getBanco());
        txtAgencia.setText(cheque.getAgencia());
        txtConta.setText(cheque.getConta());
        txtNumero.setText(cheque.getNumero());

        txtCorrentista.setText(cheque.getCorrentista());
        txtCpfCnpj.setText(cheque.getCpfCnpj());

        txtValor.setText(Decimal.toString(cheque.getValor()));
        txtVencimento.setText(DateTime.toString(cheque.getVencimento()));

        txtObservacao.setText(cheque.getObservacao());
        
        txtUtilizado.setText(DateTime.toString(cheque.getUtilizado()));

    }

    private void formatarTabelaCaixaItens() {
        tblCaixaItens.setModel(chequeCaixaItensJTableModel);

        tblCaixaItens.setRowHeight(30);
        tblCaixaItens.setIntercellSpacing(new Dimension(10, 10));

        tblCaixaItens.getColumn("Id").setPreferredWidth(120);
        tblCaixaItens.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblCaixaItens.getColumn("Data").setPreferredWidth(180);
        tblCaixaItens.getColumn("Data").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblCaixaItens.getColumn("Descrição").setPreferredWidth(800);
        
        tblCaixaItens.getColumn("Desvincular").setPreferredWidth(100);

    }

    private void carregarTabelaCaixaItens() {
        chequeCaixaItensJTableModel.clear();
        chequeCaixaItensJTableModel.addList(cheque.getCaixaItens());
    }
    
    private void clickTabelaCaixaItens() {
        if(tblCaixaItens.getSelectedColumn() == 3) {
            CaixaItem ci = chequeCaixaItensJTableModel.getRow(tblCaixaItens.getSelectedRow());
            cheque.removeCaixaItem(ci);
            ci = caixaItemDAO.save(ci);
            carregarTabelaCaixaItens();
        }
    }
    
    private void formatarTabelaParcelas() {
        tblParcelas.setModel(chequeParcelasJTableModel);

        tblParcelas.setRowHeight(30);
        tblParcelas.setIntercellSpacing(new Dimension(10, 10));

        tblParcelas.getColumn("Id").setPreferredWidth(120);
        tblParcelas.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcelas.getColumn("Data").setPreferredWidth(180);
        tblParcelas.getColumn("Data").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblParcelas.getColumn("Descrição").setPreferredWidth(800);

        tblParcelas.getColumn("Desvincular").setPreferredWidth(100);
    }

    private void carregarTabelaParcelas() {
        chequeParcelasJTableModel.clear();
        chequeParcelasJTableModel.addList(cheque.getParcelas());
    }
    
    private void clickTabelaParcelas() {
        if(tblParcelas.getSelectedColumn() == 3) {
            Parcela p = chequeParcelasJTableModel.getRow(tblParcelas.getSelectedRow());
            cheque.removeParcela(p);
            p = parcelaDAO.save(p);
            carregarTabelaParcelas();
        }
    }
    
    private void utilizado() {
        if (txtUtilizado.getText().isEmpty()) {
            txtUtilizado.setText(DateTime.toString(LocalDateTime.now()));
        } else {
            txtUtilizado.setText("");
        }
        
    }

    private void gravar() {

        String banco = txtBanco.getText().trim();
        String agencia = txtAgencia.getText().trim();
        String conta = txtConta.getText().trim();
        String numero = txtNumero.getText().trim();

        String correntista = txtCorrentista.getText().trim();
        String cpfCnpj = txtCpfCnpj.getText().trim();

        BigDecimal valor = Decimal.fromString(txtValor.getText());
        LocalDate vencimento = DateTime.fromStringToLocalDate(txtVencimento.getText());

        String observacao = txtObservacao.getText();

        if (banco.isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Preencha o banco", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtBanco.requestFocus();

        } else if (agencia.isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Preencha a agência", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtAgencia.requestFocus();

        } else if (conta.isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Preencha a conta", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtConta.requestFocus();

        } else if (numero.isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Preencha o número", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtNumero.requestFocus();

        } else if (valor.compareTo(BigDecimal.ZERO) == 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Preencha o valor", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtValor.requestFocus();

        } else if (vencimento == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Preencha o vencimento", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtVencimento.requestFocus();

        } else {
            cheque.setBanco(banco);
            cheque.setAgencia(agencia);
            cheque.setConta(conta);
            cheque.setNumero(numero);

            cheque.setCorrentista(correntista);
            cheque.setCpfCnpj(cpfCnpj);

            cheque.setValor(valor);
            cheque.setVencimento(vencimento);

            cheque.setObservacao(observacao);
            
            cheque.setUtilizado(DateTime.fromStringLDT(txtUtilizado.getText()));

            cheque = chequeDAO.save(cheque);

            if (caixaItens != null && !caixaItens.isEmpty()) {
                for (CaixaItem caixaItem : caixaItens) {
                    cheque.addCaixaItem(caixaItem);
                    caixaItemDAO.save(caixaItem);
                }
                
            }
            
            if (parcela != null) {
                cheque.addParcela(parcela);
                parcelaDAO.save(parcela);
            }

            dispose();
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

        btnFechar = new javax.swing.JButton();
        btnGravar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtValor = new javax.swing.JFormattedTextField();
        txtBanco = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtAgencia = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtConta = new javax.swing.JTextField();
        txtNumero = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtCorrentista = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtCpfCnpj = new javax.swing.JFormattedTextField();
        txtVencimento = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtObservacao = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCaixaItens = new javax.swing.JTable();
        jLabel37 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtUtilizado = new javax.swing.JTextField();
        btnUtilizado = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblParcelas = new javax.swing.JTable();
        jLabel38 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cadastro de Cheque");
        setResizable(false);

        btnFechar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnFechar.setText("Fechar");
        btnFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFecharActionPerformed(evt);
            }
        });

        btnGravar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnGravar.setText("Gravar");
        btnGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Valor");

        txtValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValor.setText("0,00");
        txtValor.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtValor.setName("decimal"); // NOI18N
        txtValor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorKeyReleased(evt);
            }
        });

        txtBanco.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtBanco.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Banco");

        txtAgencia.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAgencia.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Agência");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Conta");

        txtConta.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtConta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtNumero.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtNumero.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Nº Cheque");

        txtCorrentista.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("Correntista");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setText("CPF/CNPJ");

        txtCpfCnpj.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCpfCnpj.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtCpfCnpj.setName("cpfCnpj"); // NOI18N
        txtCpfCnpj.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCpfCnpjKeyReleased(evt);
            }
        });

        txtVencimento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVencimento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtVencimento.setName("data"); // NOI18N
        txtVencimento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtVencimentoKeyReleased(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setText("Vencimento");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setText("Observação");

        txtObservacao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        tblCaixaItens.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCaixaItens.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCaixaItensMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCaixaItens);

        jLabel37.setBackground(new java.awt.Color(122, 138, 153));
        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel37.setForeground(java.awt.Color.white);
        jLabel37.setText("Itens de Caixa");
        jLabel37.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel37.setOpaque(true);

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setText("Utilizado");

        txtUtilizado.setEditable(false);
        txtUtilizado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtUtilizado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        btnUtilizado.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnUtilizado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-data-transfer-20.png"))); // NOI18N
        btnUtilizado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUtilizadoActionPerformed(evt);
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
        jScrollPane2.setViewportView(tblParcelas);

        jLabel38.setBackground(new java.awt.Color(122, 138, 153));
        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel38.setForeground(java.awt.Color.white);
        jLabel38.setText("Parcelas");
        jLabel38.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel38.setOpaque(true);

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel13.setText("Id");

        txtId.setEditable(false);
        txtId.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtId.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtId.setFocusable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(18, 18, 18)
                        .addComponent(txtUtilizado, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUtilizado)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFechar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnGravar))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(txtBanco, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(txtAgencia, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(txtConta, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel13)
                        .addGap(18, 18, 18)
                        .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(txtCorrentista)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(txtCpfCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10)
                        .addGap(18, 18, 18)
                        .addComponent(txtVencimento, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11)
                        .addGap(18, 18, 18)
                        .addComponent(txtObservacao))
                    .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBanco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtAgencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(txtConta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel13)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCorrentista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtCpfCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtObservacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtVencimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)))
                .addGap(18, 18, 18)
                .addComponent(jLabel37)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel38)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGravar)
                    .addComponent(btnFechar)
                    .addComponent(txtUtilizado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(btnUtilizado))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFecharActionPerformed
        dispose();
    }//GEN-LAST:event_btnFecharActionPerformed

    private void btnGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarActionPerformed
        gravar();
    }//GEN-LAST:event_btnGravarActionPerformed

    private void txtValorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorKeyReleased

    }//GEN-LAST:event_txtValorKeyReleased

    private void txtCpfCnpjKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCpfCnpjKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCpfCnpjKeyReleased

    private void txtVencimentoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtVencimentoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVencimentoKeyReleased

    private void btnUtilizadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUtilizadoActionPerformed
        utilizado();
    }//GEN-LAST:event_btnUtilizadoActionPerformed

    private void tblParcelasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblParcelasMouseClicked
        clickTabelaParcelas();
    }//GEN-LAST:event_tblParcelasMouseClicked

    private void tblCaixaItensMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCaixaItensMouseClicked
        clickTabelaCaixaItens();
    }//GEN-LAST:event_tblCaixaItensMouseClicked

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
            java.util.logging.Logger.getLogger(ChequeCadastroView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChequeCadastroView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChequeCadastroView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChequeCadastroView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ChequeCadastroView dialog = new ChequeCadastroView(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnFechar;
    private javax.swing.JButton btnGravar;
    private javax.swing.JButton btnUtilizado;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblCaixaItens;
    private javax.swing.JTable tblParcelas;
    private javax.swing.JTextField txtAgencia;
    private javax.swing.JTextField txtBanco;
    private javax.swing.JTextField txtConta;
    private javax.swing.JTextField txtCorrentista;
    private javax.swing.JFormattedTextField txtCpfCnpj;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JTextField txtObservacao;
    private javax.swing.JTextField txtUtilizado;
    private javax.swing.JFormattedTextField txtValor;
    private javax.swing.JFormattedTextField txtVencimento;
    // End of variables declaration//GEN-END:variables
}
