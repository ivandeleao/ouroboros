/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoSaida;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.financeiro.Caixa;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.principal.financeiro.CaixaItemTipo;
import model.mysql.dao.principal.ParcelaDAO;
import model.mysql.dao.principal.financeiro.CaixaDAO;
import model.mysql.dao.principal.financeiro.CaixaItemDAO;
import model.mysql.dao.principal.VendaDAO;
import model.jtable.ParcelamentoJTableModel;
import model.jtable.RecebimentoPorCartaoJTableModel;
import model.mysql.bean.principal.documento.FinanceiroStatus;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.financeiro.Cartao;
import model.mysql.bean.principal.financeiro.CartaoTaxa;
import model.mysql.dao.principal.financeiro.CartaoDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.Decimal;
import util.jTableFormat.TableRenderer;

/**
 *
 * @author ivand
 *
 * ESTE FOI CRIADO COMO JDIALOG DESDE O INÍCIO - USAR ESTE COMO REFERÊNCIA PARA
 * OS DEMAIS
 *
 *
 */
public class RecebimentoPorCartaoView extends javax.swing.JDialog {

    private Venda venda;
    VendaDAO vendaDAO = new VendaDAO();
    ParcelaDAO parcelaDAO = new ParcelaDAO();
    RecebimentoPorCartaoJTableModel recebimentoPorCartaoJTableModel = new RecebimentoPorCartaoJTableModel();

    CartaoDAO cartaoDAO = new CartaoDAO();
    
    BigDecimal valorFaturar, taxaValor = BigDecimal.ZERO;
    CartaoTaxa cartaoTaxa;

    /**
     * Creates new form ParcelamentoView
     */
    public RecebimentoPorCartaoView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public RecebimentoPorCartaoView(Venda venda) {
        super(MAIN_VIEW, true);

        this.venda = venda;

        //transferindo a validação para dentro e não na tela que chama
        if (venda.getTotalAPrazo().compareTo(BigDecimal.ZERO) == 0
                && venda.getTotalEmAberto().compareTo(BigDecimal.ZERO) == 0) {
            JOptionPane.showMessageDialog(rootPane, "Não há valor para faturar.", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else if (venda.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(rootPane, "Não há valor para faturar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);

        } else {
            initComponents();

            exibirTotais();

            formatarTabela();

            carregarCartao();

            carregarDados();

            carregarCartaoTaxa();

            definirAtalhos();

            this.setLocationRelativeTo(this); //centralizar
            this.setVisible(true);
        }

        super.dispose();

    }

    private void definirAtalhos() {
        InputMap im = rootPane.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = rootPane.getActionMap();

        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "fechar");
        am.put("fechar", new FormKeyStroke("ESC"));

    }

    protected class FormKeyStroke extends AbstractAction {

        private final String key;

        public FormKeyStroke(String key) {
            this.key = key;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("key " + key);
            switch (key) {
                case "ESC":
                    dispose();
                    break;
            }
        }
    }

    private void exibirTotais() {
        txtTotal.setText(Decimal.toString(venda.getTotal()));
        txtEmAberto.setText(Decimal.toString(venda.getTotalEmAberto()));
    }

    private void carregarCartao() {
        for (Cartao c : cartaoDAO.findAll()) {
            cboCartao.addItem(c);
        }
    }

    private void carregarCartaoTaxa() {
        Cartao cartao = (Cartao) cboCartao.getSelectedItem();
        cboTaxa.removeAllItems();

        if (cartao != null) {
            for (CartaoTaxa t : cartao.getCartaoTaxas()) {
                cboTaxa.addItem(t);
            }
        }
    }

    private void carregarDados() {
        if (venda.getPessoa() != null) {
            txtCliente.setText(venda.getPessoa().getId() + " - " + venda.getPessoa().getNome() + " - " + venda.getPessoa().getEnderecoCompleto());
            txtCliente.setCaretPosition(0);

        }

        txtValorFaturar.setText(Decimal.toString(venda.getTotalEmAberto()));

        carregarTabela();
    }

    private void formatarTabela() {
        tblParcelas.setModel(recebimentoPorCartaoJTableModel);

        tblParcelas.setDefaultRenderer(Object.class, new TableRenderer());

        tblParcelas.setRowHeight(30);
        tblParcelas.setIntercellSpacing(new Dimension(10, 10));

        tblParcelas.getColumn("Id").setPreferredWidth(100);
        tblParcelas.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcelas.getColumn("Vencimento").setPreferredWidth(120);
        tblParcelas.getColumn("Vencimento").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblParcelas.getColumn("Valor").setPreferredWidth(120);
        tblParcelas.getColumn("Valor").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblParcelas.getColumn("Taxa").setPreferredWidth(120);
        tblParcelas.getColumn("Taxa").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcelas.getColumn("Recebido").setPreferredWidth(120);
        tblParcelas.getColumn("Recebido").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcelas.getColumn("Cartão").setPreferredWidth(180);
        
        tblParcelas.getColumn("Parcelas").setPreferredWidth(100);
        
        tblParcelas.getColumn("Remover").setPreferredWidth(100);

    }

    private void carregarTabela() {
        recebimentoPorCartaoJTableModel.clear();
        recebimentoPorCartaoJTableModel.addList(venda.getParcelasComCartao());

        if (venda.getParcelasComCartao().size() > 0) {
            int index = tblParcelas.getRowCount() - 1;
            tblParcelas.setRowSelectionInterval(index, index);
            tblParcelas.scrollRectToVisible(tblParcelas.getCellRect(index, 0, true));
        }

        exibirTotais();
    }

    private void removerParcela() {
        if (JOptionPane.showConfirmDialog(MAIN_VIEW, "Confirma exclusão?", "Atenção", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
            Parcela parcelaRemover = recebimentoPorCartaoJTableModel.getRow(tblParcelas.getSelectedRow());

            venda.removeParcela(parcelaRemover);
            parcelaDAO.remove(parcelaRemover);
            

            carregarTabela();
            
            txtValorFaturar.setText(Decimal.toString(venda.getTotalEmAberto()));
            exibirTaxa();
            txtValorFaturar.requestFocus();
        }
    }

    private void exibirTaxa() {
        //BigDecimal totalItens = venda.getTotalEmAberto().subtract(venda.getAcrescimoCartaoTotal());

        valorFaturar = Decimal.fromString(txtValorFaturar.getText());

        cartaoTaxa = (CartaoTaxa) cboTaxa.getSelectedItem();

        if (cartaoTaxa == null) {
            txtTaxa.setText("");

        } else {
            taxaValor = valorFaturar.multiply(cartaoTaxa.getTaxa()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            
            

            BigDecimal parcelas = new BigDecimal(cartaoTaxa.getParcelas());
            
            if (cartaoTaxa.isCartaoTaxaInclusa()) { //cobrar do consumidor
                valorFaturar = valorFaturar.add(taxaValor);
            }
            
            BigDecimal valorParcela = valorFaturar.divide(parcelas, 2, RoundingMode.HALF_UP);

            String taxa = Decimal.toString(valorParcela) + " (" + Decimal.toString(valorFaturar) + ")";

            txtTaxa.setText(taxa);
        }
    }

    private void inserir() {

        //BigDecimal valorFaturar = Decimal.fromString(txtValorFaturar.getText());
        
        BigDecimal totalEmAberto = venda.getTotalEmAberto(); //total sem o acréscimo do cartão
        System.out.println("totalEmAberto: " + totalEmAberto);
        
        if (totalEmAberto.compareTo(BigDecimal.ZERO) == 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não há valor em aberto!", "Atenção", JOptionPane.WARNING_MESSAGE);
            
        } else if (Decimal.fromString(txtValorFaturar.getText()).compareTo(totalEmAberto) > 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Valor maior que o valor em aberto", "Atenção", JOptionPane.WARNING_MESSAGE);
            
        } else {

            //CartaoTaxa cartaoTaxa = (CartaoTaxa) cboTaxa.getSelectedItem();

            //BigDecimal taxaValor = valorFaturar.multiply(cartaoTaxa.getTaxa()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

            LocalDate vencimento = LocalDate.now();

            vencimento = vencimento.plusDays(cartaoTaxa.getCartao().getDiasRecebimento());

            Parcela p = new Parcela(vencimento, valorFaturar, Ouroboros.PARCELA_MULTA, Ouroboros.PARCELA_JUROS_MONETARIO_MENSAL, Ouroboros.PARCELA_JUROS_PERCENTUAL_MENSAL, MeioDePagamento.CARTAO_DE_CREDITO);
            p.setNumero(1);
            p.setCartaoTaxa(cartaoTaxa);
            p.setCartaoTaxaValor(taxaValor);
            p.setCartaoTaxaInclusa(cartaoTaxa.isCartaoTaxaInclusa());

            venda.addParcela(p);

            parcelaDAO.save(p);

            exibirTotais();

            /*dar a baixa da parcela em uma conta desta credenciadora (pagseguro)

            lançar a taxa correspondente no mesmo caixa

                    ter opção de várias maquininhas para a mesma credenciadora?*/
            carregarTabela();
            formatarTabela();
            
            txtValorFaturar.setText(Decimal.toString(venda.getTotalEmAberto()));
            exibirTaxa();
        }
        
        txtValorFaturar.requestFocus();
    }

    private void tblClick(int selectedColumn) {

        switch (tblParcelas.getColumnName(selectedColumn)) {
            case "Remover":
                removerParcela();
                recebimentoPorCartaoJTableModel.fireTableRowsUpdated(tblParcelas.getSelectedRow(), tblParcelas.getSelectedRow());
                exibirTotais();
                break;

            default:
                Parcela parcela = recebimentoPorCartaoJTableModel.getRow(tblParcelas.getSelectedRow());

                if (!parcela.getStatus().equals(FinanceiroStatus.QUITADO)) {
                    ParcelamentoEditarView peView = new ParcelamentoEditarView(MAIN_VIEW, parcela);

                    recebimentoPorCartaoJTableModel.fireTableDataChanged();
                    tblParcelas.repaint();
                }
        }

    }

    private void confirmar() {
        if (!venda.getParcelasComCartao().isEmpty()) {
            Parcela parcelaEntrada = venda.getParcelasComCartao().get(0);

            //se a data for hoje e não foi recebido ainda
            if (parcelaEntrada.getVencimento().compareTo(LocalDate.now()) == 0
                    && parcelaEntrada.getValorQuitado().compareTo(BigDecimal.ZERO) <= 0) {

                //Não receber com meio de pagamento Crédito Loja
                if (parcelaEntrada.getMeioDePagamento().equals(MeioDePagamento.CREDITO_LOJA)) {
                    JOptionPane.showMessageDialog(MAIN_VIEW, "Não é possível receber com meio de pagamento Crédito Loja. Altere o meio de pagamento.", "Atenção", JOptionPane.WARNING_MESSAGE);

                } else {
                    int resposta = JOptionPane.showConfirmDialog(this, "Confirma quitação da primeira parcela?", "Atenção", JOptionPane.OK_CANCEL_OPTION);

                    if (resposta == JOptionPane.OK_OPTION) {
                        Caixa caixa = Ouroboros.FINANCEIRO_CAIXA_PRINCIPAL.getLastCaixa(); //2020-02-28
                        CaixaItem recebimento;
                        if (venda.getTipoOperacao().equals(TipoOperacao.SAIDA)) {
                            recebimento = new CaixaItem(caixa, CaixaItemTipo.DOCUMENTO, parcelaEntrada.getMeioDePagamento(), null, parcelaEntrada.getValor(), BigDecimal.ZERO);
                        } else {
                            recebimento = new CaixaItem(caixa, CaixaItemTipo.DOCUMENTO, parcelaEntrada.getMeioDePagamento(), null, BigDecimal.ZERO, parcelaEntrada.getValor());
                        }

                        ////recebimento = new CaixaItemDAO().save(recebimento); 2019-11-14
                        caixa.addCaixaItem(recebimento);

                        parcelaEntrada.addRecebimento(recebimento);

                        new CaixaItemDAO().save(recebimento); //2019-11-14

                        ////new CaixaDAO().save(caixa);
                        venda.addParcela(parcelaEntrada);

                        parcelaDAO.save(parcelaEntrada);

                        ////vendaDAO.save(venda); 2019-11-14
                        dispose();
                    }
                }
            } else {
                dispose();
            }

        } else {
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

        jPanel1 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        txtEmAberto = new javax.swing.JFormattedTextField();
        pnlParcelamento = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblParcelas = new javax.swing.JTable();
        btnOk = new javax.swing.JButton();
        txtCliente = new javax.swing.JTextField();
        pnlCartao = new javax.swing.JPanel();
        cboCartao = new javax.swing.JComboBox<>();
        jLabel36 = new javax.swing.JLabel();
        cboTaxa = new javax.swing.JComboBox<>();
        btnParcelarCartao = new javax.swing.JButton();
        txtTaxa = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtValorFaturar = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Recebimento por Cartão");
        setResizable(false);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel9.setText("TOTAL");

        txtTotal.setEditable(false);
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotal.setText("0,00");
        txtTotal.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel7.setText("EM ABERTO");

        txtEmAberto.setEditable(false);
        txtEmAberto.setForeground(java.awt.Color.red);
        txtEmAberto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtEmAberto.setText("0,00");
        txtEmAberto.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                    .addComponent(txtEmAberto))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEmAberto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlParcelamento.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tblParcelas.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
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

        javax.swing.GroupLayout pnlParcelamentoLayout = new javax.swing.GroupLayout(pnlParcelamento);
        pnlParcelamento.setLayout(pnlParcelamentoLayout);
        pnlParcelamentoLayout.setHorizontalGroup(
            pnlParcelamentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlParcelamentoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        pnlParcelamentoLayout.setVerticalGroup(
            pnlParcelamentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlParcelamentoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnOk.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnOk.setText("Fechar");
        btnOk.setContentAreaFilled(false);
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        txtCliente.setEditable(false);
        txtCliente.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtCliente.setText("NÃO INFORMADO");

        pnlCartao.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cboCartao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cboCartao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCartaoActionPerformed(evt);
            }
        });

        jLabel36.setBackground(new java.awt.Color(122, 138, 153));
        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel36.setForeground(java.awt.Color.white);
        jLabel36.setText("Cartão de Crédito");
        jLabel36.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        jLabel36.setOpaque(true);

        cboTaxa.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cboTaxa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTaxaActionPerformed(evt);
            }
        });

        btnParcelarCartao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnParcelarCartao.setText("Inserir");
        btnParcelarCartao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnParcelarCartaoActionPerformed(evt);
            }
        });

        txtTaxa.setEditable(false);
        txtTaxa.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Valor");

        txtValorFaturar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorFaturar.setText("0,00");
        txtValorFaturar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtValorFaturar.setName("decimal"); // NOI18N
        txtValorFaturar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorFaturarKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout pnlCartaoLayout = new javax.swing.GroupLayout(pnlCartao);
        pnlCartao.setLayout(pnlCartaoLayout);
        pnlCartaoLayout.setHorizontalGroup(
            pnlCartaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlCartaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCartaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlCartaoLayout.createSequentialGroup()
                        .addComponent(cboTaxa, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtTaxa, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnParcelarCartao))
                    .addGroup(pnlCartaoLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtValorFaturar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cboCartao, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlCartaoLayout.setVerticalGroup(
            pnlCartaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCartaoLayout.createSequentialGroup()
                .addComponent(jLabel36)
                .addGap(18, 18, 18)
                .addGroup(pnlCartaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtValorFaturar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboCartao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(pnlCartaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboTaxa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTaxa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnParcelarCartao))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCliente)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(pnlParcelamento, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(pnlCartao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlCartao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(pnlParcelamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnOk)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblParcelasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblParcelasMouseClicked
        if (evt.getClickCount() == 1) {

            tblClick(tblParcelas.getSelectedColumn());

        }
    }//GEN-LAST:event_tblParcelasMouseClicked

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        confirmar();
    }//GEN-LAST:event_btnOkActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        confirmar();
    }//GEN-LAST:event_formWindowClosing

    private void btnParcelarCartaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnParcelarCartaoActionPerformed
        inserir();
    }//GEN-LAST:event_btnParcelarCartaoActionPerformed

    private void cboCartaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCartaoActionPerformed
        carregarCartaoTaxa();
    }//GEN-LAST:event_cboCartaoActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        //pnlCartao.setVisible(false);
    }//GEN-LAST:event_formComponentShown

    private void cboTaxaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTaxaActionPerformed
        exibirTaxa();
    }//GEN-LAST:event_cboTaxaActionPerformed

    private void txtValorFaturarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorFaturarKeyReleased
        exibirTaxa();
    }//GEN-LAST:event_txtValorFaturarKeyReleased

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
            java.util.logging.Logger.getLogger(RecebimentoPorCartaoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RecebimentoPorCartaoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RecebimentoPorCartaoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RecebimentoPorCartaoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RecebimentoPorCartaoView dialog = new RecebimentoPorCartaoView(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnParcelarCartao;
    private javax.swing.JComboBox<Object> cboCartao;
    private javax.swing.JComboBox<Object> cboTaxa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlCartao;
    private javax.swing.JPanel pnlParcelamento;
    private javax.swing.JTable tblParcelas;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JFormattedTextField txtEmAberto;
    private javax.swing.JTextField txtTaxa;
    private javax.swing.JFormattedTextField txtTotal;
    private javax.swing.JFormattedTextField txtValorFaturar;
    // End of variables declaration//GEN-END:variables
}
