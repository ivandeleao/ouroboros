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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
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
import model.mysql.dao.fiscal.MeioDePagamentoDAO;
import model.mysql.dao.principal.financeiro.CaixaDAO;
import model.mysql.dao.principal.financeiro.CaixaItemDAO;
import model.mysql.dao.principal.VendaDAO;
import model.jtable.ParcelamentoJTableModel;
import model.mysql.bean.principal.documento.FinanceiroStatus;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.financeiro.Cartao;
import model.mysql.bean.principal.financeiro.CartaoTaxa;
import model.mysql.dao.principal.financeiro.CartaoDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.PARCELA_JUROS_MONETARIO_MENSAL;
import static ouroboros.Ouroboros.PARCELA_JUROS_PERCENTUAL_MENSAL;
import static ouroboros.Ouroboros.PARCELA_MULTA;
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
public class ParcelamentoView extends javax.swing.JDialog {

    private Venda venda;
    VendaDAO vendaDAO = new VendaDAO();
    ParcelaDAO parcelaDAO = new ParcelaDAO();
    ParcelamentoJTableModel parcelamentoJTableModel = new ParcelamentoJTableModel();

    CartaoDAO cartaoDAO = new CartaoDAO();

    /**
     * Creates new form ParcelamentoView
     */
    public ParcelamentoView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public ParcelamentoView(Venda venda) {
        super(MAIN_VIEW, true);

        this.venda = venda;
        
        

        //transferindo a validação para dentro e não na tela que chama
        if (venda.getTotalAPrazo().compareTo(BigDecimal.ZERO) == 0
                && venda.getTotalEmAberto().compareTo(BigDecimal.ZERO) == 0) {
            JOptionPane.showMessageDialog(rootPane, "Não há valor para faturar.", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else if (venda.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(rootPane, "Não há valor para faturar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);

        } else if (!venda.isOrcamento() && venda.getPessoa() == null) {
            JOptionPane.showMessageDialog(rootPane, "Identifique o cliente/fornecedor antes de faturar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);

        } else {
            initComponents();

            exibirTotais();

            formatarTabela();

            calcularSimulacao();

            carregarCartao();
            
            carregarCartaoTaxa();
            
            carregarDados();

            definirAtalhos();

            if (venda.isOrcamento()) {
                chkEntrada.setEnabled(false);
            }

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

        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_PLUS, 0), "adicionarParcela");
        am.put("adicionarParcela", new FormKeyStroke("+"));

        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_EQUALS, 0), "adicionarParcela");
        am.put("adicionarParcela", new FormKeyStroke("+"));

        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_MINUS, 0), "removerParcela");
        am.put("removerParcela", new FormKeyStroke("-"));
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
                case "+":
                    adicionarParcela();
                    break;
                case "-":
                    removerParcela();
                    break;
            }
        }
    }

    private void exibirTotais() {
        txtTotal.setText(Decimal.toString(venda.getTotal()));
        txtEmAberto.setText(Decimal.toString(venda.getTotalEmAberto()));
    }

    private void calcularSimulacao() {
        BigDecimal valorAParcelar = (venda.getTotalEmAberto().compareTo(BigDecimal.ZERO) > 0) ? venda.getTotalEmAberto() : venda.getTotalAPrazo();
        String opcoes = "";
        for (int n = 2; n <= 12; n++) {
            BigDecimal valor = valorAParcelar.divide(new BigDecimal(n), 2, RoundingMode.HALF_DOWN);
            opcoes += n + " x " + Decimal.toString(valor) + "   ";

        }
        lblSimulacao.setText("Simulação: " + opcoes);
    }

    private void carregarCartao() {
        for (Cartao c  : cartaoDAO.findAll()) {
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

        carregarMeioDePagamento();

        carregarTabela();
    }

    private void carregarMeioDePagamento() {
        List<MeioDePagamento> mpList = new MeioDePagamentoDAO().findAllEnabled();

        cboMeioDePagamento.addItem(MeioDePagamento.DINHEIRO);
        for (MeioDePagamento mp : mpList) {
            cboMeioDePagamento.addItem(mp);

            if (mp.equals(MeioDePagamento.BOLETO_BANCARIO) && venda.getTipoOperacao().equals(TipoOperacao.ENTRADA)) {
                cboMeioDePagamento.setSelectedItem(mp);
            }
        }
    }

    private void formatarTabela() {
        tblParcelasAPrazo.setModel(parcelamentoJTableModel);

        tblParcelasAPrazo.setDefaultRenderer(Object.class, new TableRenderer());

        tblParcelasAPrazo.setRowHeight(30);
        tblParcelasAPrazo.setIntercellSpacing(new Dimension(10, 10));

        tblParcelasAPrazo.getColumn("Id").setPreferredWidth(100);
        tblParcelasAPrazo.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcelasAPrazo.getColumn("Núm").setPreferredWidth(60);
        tblParcelasAPrazo.getColumn("Núm").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcelasAPrazo.getColumn("Vencimento").setPreferredWidth(120);
        tblParcelasAPrazo.getColumn("Vencimento").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblParcelasAPrazo.getColumn("Valor").setPreferredWidth(120);
        tblParcelasAPrazo.getColumn("Valor").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcelasAPrazo.getColumn("Recebido").setPreferredWidth(120);
        tblParcelasAPrazo.getColumn("Recebido").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblParcelasAPrazo.getColumn("Meio de Pagamento").setPreferredWidth(180);

    }

    private void carregarTabela() {
        //em.refresh(venda);

        parcelamentoJTableModel.clear();
        parcelamentoJTableModel.addList(venda.getParcelasAPrazo());

        if (venda.getParcelasAPrazo().size() > 0) {
            int index = tblParcelasAPrazo.getRowCount() - 1;
            tblParcelasAPrazo.setRowSelectionInterval(index, index);
            tblParcelasAPrazo.scrollRectToVisible(tblParcelasAPrazo.getCellRect(index, 0, true));
        }
        chkEntrada.setEnabled(!venda.isOrcamento() && venda.getParcelasAPrazo().isEmpty());

        exibirTotais();
    }

    private void adicionarParcela() {
        if (venda.getTotalRecebidoAPrazo().compareTo(BigDecimal.ZERO) > 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Já há valor recebido. Não é possível reparcelar.", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            //Calendar c = Calendar.getInstance();

            LocalDate vencimento = LocalDate.now();

            if (venda.getParcelasAPrazo().size() > 0) {
                LocalDate ultimoVencimento = venda.getParcelasAPrazo().get(venda.getParcelasAPrazo().size() - 1).getVencimento();
                //c.setTimeInMillis(ultimoVencimento.toEpochDay());
                vencimento = ultimoVencimento;
            }
            if (!chkEntrada.isSelected() || venda.getParcelasAPrazo().size() > 0) {
                //c.add(Calendar.MONTH, 1);
                vencimento = vencimento.plusMonths(1);
            }

            //LocalDate vencimento = LocalDate.ofEpochDay(c.getTimeInMillis());
            BigDecimal totalReceber = venda.getTotalReceber();

            //adicionar parcela
            MeioDePagamento mp = (MeioDePagamento) cboMeioDePagamento.getSelectedItem();
            Parcela novaParcela = new Parcela(vencimento, BigDecimal.ZERO, PARCELA_MULTA, PARCELA_JUROS_MONETARIO_MENSAL, PARCELA_JUROS_PERCENTUAL_MENSAL, mp);

            novaParcela.setNumero(venda.getParcelasAPrazo().size() + 1);
            //ai que medo de mexer no que tá funcionando :<
            ////novaParcela = parcelaDAO.save(novaParcela);
            venda.addParcela(novaParcela);
            parcelaDAO.save(novaParcela); //2019-11-13
            ////venda = vendaDAO.save(venda);
            ////parcelasAPrazo = venda.getParcelasAPrazo();
            //parcelasAPrazo.add(novaParcela);

            //dividir valor pelo número de parcelas
            BigDecimal quantidade = new BigDecimal(venda.getParcelasAPrazo().size());
            BigDecimal valorParcela = totalReceber.divide(quantidade, 2, RoundingMode.DOWN);

            //obter a diferença entre o valor total e a soma das parcelas
            BigDecimal totalParcelas = valorParcela.multiply(quantidade);
            BigDecimal resto = totalReceber.subtract(totalParcelas);

            //distribuir os valores
            for (Parcela parcela : venda.getParcelasAPrazo()) {
                //System.out.println("parcela index: " + venda.getParcelasAPrazo().indexOf(parcela));
                //System.out.println("parcela vencimento: " + parcela.getVencimento());
                if (parcela.getNumero() == 1) {
                    //atualizar o valor da primeira com o resto
                    parcela.setValor(valorParcela.add(resto));
                } else {
                    parcela.setValor(valorParcela);
                }
                //parcela.setNumero(venda.getParcelasAPrazo().indexOf(parcela) + 1);

                ////parcela = parcelaDAO.save(parcela);
                venda.addParcela(parcela);
                parcelaDAO.save(parcela); //2019-11-13
                ////venda = vendaDAO.save(venda);
            }

            ////venda = vendaDAO.save(venda); 2019-11-13
            carregarTabela();
        }
    }

    private void removerParcela() {
        if (venda.getTotalRecebidoAPrazo().compareTo(BigDecimal.ZERO) > 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Já há valor recebido. Não é possível reparcelar.", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else if (venda.getParcelasAPrazo().size() > 0) {

            Parcela parcelaRemover = venda.getParcelasAPrazo().get(venda.getParcelasAPrazo().size() - 1);

            parcelaDAO.remove(parcelaRemover);

            venda.removeParcela(parcelaRemover);

            ////venda = vendaDAO.save(venda); 2019-11-13
            BigDecimal totalReceber = venda.getTotalReceber();

            //dividir valor pelo número de parcelas
            if (venda.getParcelasAPrazo().size() > 0) {
                BigDecimal quantidade = new BigDecimal(venda.getParcelasAPrazo().size());
                BigDecimal valorParcela = totalReceber.divide(quantidade, 2, RoundingMode.DOWN);

                //obter a diferença entre o valor total e a soma das parcelas
                BigDecimal totalParcelas = valorParcela.multiply(quantidade);
                BigDecimal resto = totalReceber.subtract(totalParcelas);

                //distribuir os valores
                for (Parcela parcela : venda.getParcelasAPrazo()) {
                    if (parcela.getNumero() == 1) {
                        //atualizar o valor da primeira com o resto
                        parcela.setValor(valorParcela.add(resto));
                    } else {
                        parcela.setValor(valorParcela);
                    }
                    parcela = parcelaDAO.save(parcela);
                    venda.addParcela(parcela);
                    ////vendaDAO.save(venda); 2019-11-13
                }

            }

            carregarTabela();
        }

    }
    
    private void exibirTaxa() {
        BigDecimal totalItens = venda.getTotalEmAberto().subtract(venda.getAcrescimoCartaoTotal());
        
        CartaoTaxa cartaoTaxa = (CartaoTaxa) cboTaxa.getSelectedItem();
        
        if(cartaoTaxa == null) {
            txtTaxa.setText("");
            
        } else {
            BigDecimal valorTaxa = totalItens.multiply(cartaoTaxa.getTaxa()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

            BigDecimal parcelas = new BigDecimal(cartaoTaxa.getParcelas());
            
            BigDecimal valorParcela = totalItens.add(valorTaxa).divide(parcelas, 2, RoundingMode.HALF_UP);
            
            String taxa = Decimal.toString(valorParcela) + " (" + Decimal.toString(totalItens.add(valorTaxa)) + ")";

            txtTaxa.setText(taxa);
        }
    }
    
    private void parcelarPorCartao() {
        
        venda.limparTaxaCartao();
        venda.setTotalProdutos();
        venda.setTotalServicos();
        
        BigDecimal totalItens = venda.getTotalEmAberto().subtract(venda.getAcrescimoCartaoTotal()); ////terminar aqui - tem que pegar o total, mas sem o acréscimo do cartão
        
        CartaoTaxa cartaoTaxa = (CartaoTaxa) cboTaxa.getSelectedItem();
        
        BigDecimal valorTaxa = totalItens.multiply(cartaoTaxa.getTaxa()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        
        LocalDate vencimento = LocalDate.now();
            
        vencimento = vencimento.plusMonths(1);

        Parcela p = new Parcela(vencimento, totalItens.add(valorTaxa), Ouroboros.PARCELA_MULTA, Ouroboros.PARCELA_JUROS_MONETARIO_MENSAL, Ouroboros.PARCELA_JUROS_PERCENTUAL_MENSAL, MeioDePagamento.CARTAO_DE_CREDITO);
        p.setNumero(1);

        venda.distribuirTaxaCartao(valorTaxa);
        
        venda.addParcela(p);

        parcelaDAO.save(p);
        
        venda.setTotalProdutos();
        venda.setTotalServicos();

        exibirTotais();

        /*dar a baixa da parcela em uma conta desta credenciadora (pagseguro)

        lançar a taxa correspondente no mesmo caixa

                ter opção de várias maquininhas para a mesma credenciadora?*/
        
        
        carregarTabela();
        
    }

    private void confirmar() {
        if (!venda.getParcelasAPrazo().isEmpty()) {
            Parcela parcelaEntrada = venda.getParcelasAPrazo().get(0);

            //se a data for hoje e não foi recebido ainda
            if (parcelaEntrada.getVencimento().compareTo(LocalDate.now()) == 0
                    && parcelaEntrada.getValorQuitado().compareTo(BigDecimal.ZERO) <= 0) {

                //Não receber com meio de pagamento Crédito Loja
                if (parcelaEntrada.getMeioDePagamento().equals(MeioDePagamento.CREDITO_LOJA)) {
                    JOptionPane.showMessageDialog(MAIN_VIEW, "Não é possível receber com meio de pagamento Crédito Loja. Altere o meio de pagamento.", "Atenção", JOptionPane.WARNING_MESSAGE);

                } else {
                    int resposta = JOptionPane.showConfirmDialog(this, "Confirma quitação da primeira parcela?", "Atenção", JOptionPane.OK_CANCEL_OPTION);

                    if (resposta == JOptionPane.OK_OPTION) {
                        Caixa caixa = new CaixaDAO().getLastCaixa();
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
        tblParcelasAPrazo = new javax.swing.JTable();
        btnOk = new javax.swing.JButton();
        txtCliente = new javax.swing.JTextField();
        lblSimulacao = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        chkEntrada = new javax.swing.JCheckBox();
        cboMeioDePagamento = new javax.swing.JComboBox<>();
        btnAdicionar = new javax.swing.JButton();
        btnRemover = new javax.swing.JButton();
        jLabel35 = new javax.swing.JLabel();
        pnlCartao = new javax.swing.JPanel();
        cboCartao = new javax.swing.JComboBox<>();
        jLabel36 = new javax.swing.JLabel();
        cboTaxa = new javax.swing.JComboBox<>();
        btnParcelarCartao = new javax.swing.JButton();
        txtTaxa = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Parcelamento");
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
                    .addComponent(txtTotal)
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

        tblParcelasAPrazo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tblParcelasAPrazo.setModel(new javax.swing.table.DefaultTableModel(
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
        tblParcelasAPrazo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblParcelasAPrazoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblParcelasAPrazo);

        javax.swing.GroupLayout pnlParcelamentoLayout = new javax.swing.GroupLayout(pnlParcelamento);
        pnlParcelamento.setLayout(pnlParcelamentoLayout);
        pnlParcelamentoLayout.setHorizontalGroup(
            pnlParcelamentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlParcelamentoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 688, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlParcelamentoLayout.setVerticalGroup(
            pnlParcelamentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlParcelamentoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
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

        lblSimulacao.setText("...");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        chkEntrada.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        chkEntrada.setText("Entrada");
        chkEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkEntradaActionPerformed(evt);
            }
        });

        cboMeioDePagamento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        btnAdicionar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnAdicionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-add-20.png"))); // NOI18N
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        btnRemover.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnRemover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-do-not-disturb-20.png"))); // NOI18N
        btnRemover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverActionPerformed(evt);
            }
        });

        jLabel35.setBackground(new java.awt.Color(122, 138, 153));
        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel35.setForeground(java.awt.Color.white);
        jLabel35.setText("Adicionar / Remover");
        jLabel35.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        jLabel35.setOpaque(true);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(cboMeioDePagamento, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnRemover)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAdicionar)
                .addContainerGap())
            .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdicionar)
                    .addComponent(cboMeioDePagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkEntrada)
                    .addComponent(btnRemover))
                .addContainerGap())
        );

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
        btnParcelarCartao.setText("OK");
        btnParcelarCartao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnParcelarCartaoActionPerformed(evt);
            }
        });

        txtTaxa.setEditable(false);
        txtTaxa.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        javax.swing.GroupLayout pnlCartaoLayout = new javax.swing.GroupLayout(pnlCartao);
        pnlCartao.setLayout(pnlCartaoLayout);
        pnlCartaoLayout.setHorizontalGroup(
            pnlCartaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlCartaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCartaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboCartao, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlCartaoLayout.createSequentialGroup()
                        .addComponent(cboTaxa, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtTaxa)))
                .addGap(18, 18, 18)
                .addComponent(btnParcelarCartao)
                .addContainerGap())
        );
        pnlCartaoLayout.setVerticalGroup(
            pnlCartaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCartaoLayout.createSequentialGroup()
                .addComponent(jLabel36)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCartaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCartaoLayout.createSequentialGroup()
                        .addComponent(cboCartao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                        .addGroup(pnlCartaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboTaxa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTaxa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnParcelarCartao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtCliente)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlCartao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(pnlParcelamento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(10, 10, 10))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblSimulacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(198, 198, 198)
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlCartao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(pnlParcelamento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk)
                    .addComponent(lblSimulacao))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        adicionarParcela();
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void btnRemoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverActionPerformed
        removerParcela();
    }//GEN-LAST:event_btnRemoverActionPerformed

    private void chkEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkEntradaActionPerformed

    }//GEN-LAST:event_chkEntradaActionPerformed

    private void tblParcelasAPrazoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblParcelasAPrazoMouseClicked
        if (evt.getClickCount() == 2) {

            //if (venda.getTotalRecebidoAPrazo().compareTo(BigDecimal.ZERO) == 0) {
            Parcela parcela = parcelamentoJTableModel.getRow(tblParcelasAPrazo.getSelectedRow());

            if (!parcela.getStatus().equals(FinanceiroStatus.QUITADO)) {
                ParcelamentoEditarView peView = new ParcelamentoEditarView(MAIN_VIEW, parcela);

                parcelamentoJTableModel.fireTableDataChanged();
                tblParcelasAPrazo.repaint();
            }
            //}
        }
    }//GEN-LAST:event_tblParcelasAPrazoMouseClicked

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        confirmar();
    }//GEN-LAST:event_btnOkActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        confirmar();
    }//GEN-LAST:event_formWindowClosing

    private void btnParcelarCartaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnParcelarCartaoActionPerformed
        parcelarPorCartao();
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
            java.util.logging.Logger.getLogger(ParcelamentoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ParcelamentoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ParcelamentoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ParcelamentoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ParcelamentoView dialog = new ParcelamentoView(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnParcelarCartao;
    private javax.swing.JButton btnRemover;
    private javax.swing.JComboBox<Object> cboCartao;
    private javax.swing.JComboBox<Object> cboMeioDePagamento;
    private javax.swing.JComboBox<Object> cboTaxa;
    private javax.swing.JCheckBox chkEntrada;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblSimulacao;
    private javax.swing.JPanel pnlCartao;
    private javax.swing.JPanel pnlParcelamento;
    private javax.swing.JTable tblParcelasAPrazo;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JFormattedTextField txtEmAberto;
    private javax.swing.JTextField txtTaxa;
    private javax.swing.JFormattedTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
