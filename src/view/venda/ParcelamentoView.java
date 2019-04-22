/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.venda;

import com.sun.glass.events.KeyEvent;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.financeiro.Caixa;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.principal.financeiro.CaixaItemTipo;
import model.mysql.dao.principal.pessoa.PessoaDAO;
import model.mysql.dao.principal.ParcelaDAO;
import model.mysql.dao.fiscal.MeioDePagamentoDAO;
import model.mysql.dao.principal.CaixaDAO;
import model.mysql.dao.principal.CaixaItemDAO;
import model.mysql.dao.principal.VendaDAO;
import model.jtable.ParcelamentoJTableModel;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.PARCELA_JUROS_MONETARIO_MENSAL;
import static ouroboros.Ouroboros.PARCELA_JUROS_PERCENTUAL_MENSAL;
import static ouroboros.Ouroboros.PARCELA_MULTA;
import static ouroboros.Ouroboros.em;
import util.Decimal;
import util.JSwing;
import util.jTableFormat.TableRenderer;
import view.pessoa.PessoaPesquisaView;

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
    List<Parcela> parcelasAPrazo = new ArrayList<>();

    /**
     * Creates new form ParcelamentoView
     */
    public ParcelamentoView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public ParcelamentoView(java.awt.Frame parent, Venda venda) {
        super(parent, true);
        initComponents();

        this.venda = venda;
        
        btnPesquisarPessoa.setVisible(false);
        btnRemoverCliente.setVisible(false);

        exibirTotais();

        tblParcelasAPrazo.setModel(parcelamentoJTableModel);

        formatarTabela();

        calcularSimulacao();

        carregarDados();
        
        definirAtalhos();
        
        if(venda.isOrcamento()) {
            chkEntrada.setEnabled(false);
        }

        this.setLocationRelativeTo(this); //centralizar
        this.setVisible(true);
    }
    
    private void definirAtalhos() {
        InputMap im = rootPane.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = rootPane.getActionMap();

        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "fechar");
        am.put("fechar", new FormKeyStroke("ESC"));

        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0), "pesquisarCliente");
        am.put("pesquisarCliente", new FormKeyStroke("F5"));
        
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
                case "F5":
                    pesquisarCliente();
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
        lblSimulacao.setText(opcoes);
    }

    private void carregarDados() {
        if (venda.getPessoa() != null) {
            txtCliente.setText(venda.getPessoa().getId() + " - " + venda.getPessoa().getNome() + " - " + venda.getPessoa().getEnderecoCompleto());
            txtCliente.setCaretPosition(0);

        //    JSwing.setComponentesHabilitados(pnlParcelamento, true);
        } //else {
            //JSwing.setComponentesHabilitados(pnlParcelamento, false);
        //}
        /*
        if (venda.getTotalRecebidoAPrazo().compareTo(BigDecimal.ZERO) > 0) {
            JSwing.setComponentesHabilitados(pnlCliente, false);
            JSwing.setComponentesHabilitados(pnlParcelamento, false);
        }*/

        carregarMeioDePagamento();

        carregarTabela();
    }

    private void carregarMeioDePagamento() {
        List<MeioDePagamento> mpList = new MeioDePagamentoDAO().findAllEnabled();

        cboMeioDePagamento.addItem(MeioDePagamento.CREDITO_LOJA);
        for (MeioDePagamento mp : mpList) {
            cboMeioDePagamento.addItem(mp);
        }
    }

    private void formatarTabela() {
        tblParcelasAPrazo.setDefaultRenderer(Object.class, new TableRenderer());

        tblParcelasAPrazo.setRowHeight(24);
        tblParcelasAPrazo.setIntercellSpacing(new Dimension(10, 10));
        //id
        tblParcelasAPrazo.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblParcelasAPrazo.getColumnModel().getColumn(0).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //número
        tblParcelasAPrazo.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblParcelasAPrazo.getColumnModel().getColumn(1).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //vencimento
        tblParcelasAPrazo.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblParcelasAPrazo.getColumnModel().getColumn(2).setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        //valor
        tblParcelasAPrazo.getColumnModel().getColumn(3).setPreferredWidth(120);
        tblParcelasAPrazo.getColumnModel().getColumn(3).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //valor
        tblParcelasAPrazo.getColumnModel().getColumn(4).setPreferredWidth(120);
        tblParcelasAPrazo.getColumnModel().getColumn(4).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        //meio de pagamento
        tblParcelasAPrazo.getColumnModel().getColumn(5).setPreferredWidth(200);

    }

    private void carregarTabela() {
        //em.refresh(venda);
        parcelasAPrazo = venda.getParcelasAPrazo();

        parcelamentoJTableModel.clear();
        parcelamentoJTableModel.addList(parcelasAPrazo);

        if (parcelasAPrazo.size() > 0) {
            int index = tblParcelasAPrazo.getRowCount() - 1;
            tblParcelasAPrazo.setRowSelectionInterval(index, index);
            tblParcelasAPrazo.scrollRectToVisible(tblParcelasAPrazo.getCellRect(index, 0, true));
        }
        chkEntrada.setEnabled(!venda.isOrcamento() && parcelasAPrazo.isEmpty());

        exibirTotais();
    }

    private void adicionarParcela() {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        if (parcelasAPrazo.size() > 0) {
            java.sql.Date ultimoVencimento = parcelasAPrazo.get(parcelasAPrazo.size() - 1).getVencimento();
            c.setTimeInMillis(ultimoVencimento.getTime());
        }
        if (!chkEntrada.isSelected() || parcelasAPrazo.size() > 0) {
            c.add(Calendar.MONTH, 1);
        }

        java.sql.Date vencimento = new Date(c.getTimeInMillis());
        BigDecimal totalReceber = venda.getTotalReceber();

        //adicionar parcela
        MeioDePagamento mp = (MeioDePagamento) cboMeioDePagamento.getSelectedItem();
        Parcela novaParcela = new Parcela(vencimento, BigDecimal.ZERO, PARCELA_MULTA, PARCELA_JUROS_MONETARIO_MENSAL, PARCELA_JUROS_PERCENTUAL_MENSAL, mp);
        
        //ai que medo de mexer no que tá funcionando :<
        novaParcela = parcelaDAO.save(novaParcela);
        venda.addParcela(novaParcela);
        venda = vendaDAO.save(venda);
        parcelasAPrazo = venda.getParcelasAPrazo();
        //parcelasAPrazo.add(novaParcela);

        //dividir valor pelo número de parcelas
        BigDecimal quantidade = new BigDecimal(parcelasAPrazo.size());
        BigDecimal valorParcela = totalReceber.divide(quantidade, 2, RoundingMode.DOWN);

        //obter a diferença entre o valor total e a soma das parcelas
        BigDecimal totalParcelas = valorParcela.multiply(quantidade);
        BigDecimal resto = totalReceber.subtract(totalParcelas);

        //distribuir os valores
        for (Parcela parcela : parcelasAPrazo) {
            if (parcelasAPrazo.indexOf(parcela) == 0) {
                //atualizar o valor da primeira com o resto
                parcela.setValor(valorParcela.add(resto));
            } else {
                parcela.setValor(valorParcela);
            }
            parcela.setNumero(parcelasAPrazo.indexOf(parcela) + 1);
            parcela = parcelaDAO.save(parcela);
            venda.addParcela(parcela);
            //parcelaDAO.save(parcela);
            venda = vendaDAO.save(venda);
        }
        
        

        carregarTabela();
    }

    private void removerParcela() {
        if (parcelasAPrazo.size() > 0) {
            
            Parcela parcelaRemover = parcelasAPrazo.get(parcelasAPrazo.size() - 1);
            
            venda.removeParcela(parcelaRemover);
            
            venda = vendaDAO.save(venda);
            ////parcelaDAO.remove(parcelasAPrazo.get(parcelasAPrazo.size() - 1));
            ////em.refresh(venda);

            parcelasAPrazo = venda.getParcelasAPrazo();

            BigDecimal totalReceber = venda.getTotalReceber();

            //dividir valor pelo número de parcelas
            if (parcelasAPrazo.size() > 0) {
                BigDecimal quantidade = new BigDecimal(parcelasAPrazo.size());
                BigDecimal valorParcela = totalReceber.divide(quantidade, 2, RoundingMode.DOWN);

                //obter a diferença entre o valor total e a soma das parcelas
                BigDecimal totalParcelas = valorParcela.multiply(quantidade);
                BigDecimal resto = totalReceber.subtract(totalParcelas);

                //distribuir os valores
                for (Parcela parcela : parcelasAPrazo) {
                    if (parcelasAPrazo.indexOf(parcela) == 0) {
                        //atualizar o valor da primeira com o resto
                        parcela.setValor(valorParcela.add(resto));
                    } else {
                        parcela.setValor(valorParcela);
                    }
                    parcela = parcelaDAO.save(parcela);
                    venda.addParcela(parcela);
                    vendaDAO.save(venda);
                }

            }
        }
        //vendaDAO.save(venda);

        carregarTabela();
    }


    private void pesquisarCliente() {
        PessoaPesquisaView pesquisa = new PessoaPesquisaView(null);

        if (pesquisa.getPessoa() != null) {
            venda.setCliente(pesquisa.getPessoa());
            vendaDAO.save(venda);
            txtCliente.setText(venda.getPessoa().getId() + " - " + venda.getPessoa().getNome() + " - " + venda.getPessoa().getEnderecoCompleto());
            txtCliente.setCaretPosition(0);
            //JSwing.setComponentesHabilitados(pnlParcelamento, true);
        }
    }

    private void removerCliente() {
        //if (!venda.getParcelasAPrazo().isEmpty()) {
        //    JOptionPane.showMessageDialog(MAIN_VIEW, "Remova todas as parcelas antes de remover o cliente.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        //} else {
            venda.setCliente(null);
            vendaDAO.save(venda);
            txtCliente.setText("");

            //JSwing.setComponentesHabilitados(pnlParcelamento, false);
        //}
    }

    private void confirmar() {
        if (!parcelasAPrazo.isEmpty()) {
            Parcela entrada = parcelasAPrazo.get(0);

            //System.out.println("vencimento: " + entrada.getVencimento().toLocalDate());
            //System.out.println("recebido: " + entrada.getValorQuitado());
            //se a data for hoje e não foi recebido ainda
            if (entrada.getVencimento().toLocalDate().compareTo(LocalDate.now()) == 0
                    && entrada.getValorQuitado().compareTo(BigDecimal.ZERO) <= 0) {
                
                int resposta = JOptionPane.showConfirmDialog(this, "Confirma recebimento da primeira parcela?", "Atenção", JOptionPane.OK_CANCEL_OPTION);
                
                if(resposta == JOptionPane.OK_OPTION) {
                    Caixa caixa = new CaixaDAO().getLastCaixa();
                    CaixaItem recebimento = new CaixaItem(caixa, CaixaItemTipo.RECEBIMENTO_DOCUMENTO, entrada.getMeioDePagamento(), null, entrada.getValor(), BigDecimal.ZERO);
                    recebimento = new CaixaItemDAO().save(recebimento);
                    //venda.getRecebimentos().add(recebimento);
                    
                    entrada.addRecebimento(recebimento);
                    
                    venda.addParcela(entrada);
                    
                    vendaDAO.save(venda);
                    
                    //em.refresh(recebimento);
                    //em.refresh(entrada);
                    
                    
                    //em.refresh(venda);
                    dispose();
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
        chkEntrada = new javax.swing.JCheckBox();
        cboMeioDePagamento = new javax.swing.JComboBox<>();
        btnRemover = new javax.swing.JButton();
        btnAdicionar = new javax.swing.JButton();
        btnPesquisarPessoa = new javax.swing.JButton();
        pnlParcelamento = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblParcelasAPrazo = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblSimulacao = new javax.swing.JLabel();
        btnOk = new javax.swing.JButton();
        txtCliente = new javax.swing.JTextField();
        btnRemoverCliente = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Parcelamento");
        setResizable(false);
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

        chkEntrada.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        chkEntrada.setText("Entrada");
        chkEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkEntradaActionPerformed(evt);
            }
        });

        cboMeioDePagamento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        btnRemover.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnRemover.setText("-");
        btnRemover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverActionPerformed(evt);
            }
        });

        btnAdicionar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnAdicionar.setText("+");
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        btnPesquisarPessoa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/user.png"))); // NOI18N
        btnPesquisarPessoa.setText("F5 CLIENTE:");
        btnPesquisarPessoa.setContentAreaFilled(false);
        btnPesquisarPessoa.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPesquisarPessoa.setIconTextGap(10);
        btnPesquisarPessoa.setPreferredSize(new java.awt.Dimension(180, 49));
        btnPesquisarPessoa.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPesquisarPessoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarPessoaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel9))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTotal)
                            .addComponent(txtEmAberto)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(chkEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnPesquisarPessoa, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnRemover, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cboMeioDePagamento, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
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
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkEntrada)
                    .addComponent(btnAdicionar)
                    .addComponent(cboMeioDePagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnRemover)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(btnPesquisarPessoa, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        pnlParcelamento.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlParcelamentoLayout.setVerticalGroup(
            pnlParcelamentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlParcelamentoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Simulação:");

        lblSimulacao.setText("...");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(lblSimulacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(lblSimulacao)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnOk.setText("OK");
        btnOk.setContentAreaFilled(false);
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        txtCliente.setEditable(false);
        txtCliente.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtCliente.setText("NÃO INFORMADO");

        btnRemoverCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/resource/img/cancel.png"))); // NOI18N
        btnRemoverCliente.setToolTipText("Remover Cliente");
        btnRemoverCliente.setContentAreaFilled(false);
        btnRemoverCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverClienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(txtCliente)
                        .addGap(18, 18, 18)
                        .addComponent(btnRemoverCliente))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(pnlParcelamento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtCliente)
                    .addComponent(btnRemoverCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlParcelamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnOk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

            if (venda.getTotalRecebidoAPrazo().compareTo(BigDecimal.ZERO) == 0) {
                Parcela parcela = parcelamentoJTableModel.getRow(tblParcelasAPrazo.getSelectedRow());
                ParcelamentoEditarView peView = new ParcelamentoEditarView(MAIN_VIEW, parcela);

                parcelamentoJTableModel.fireTableDataChanged();
                tblParcelasAPrazo.repaint();
            }
        }
    }//GEN-LAST:event_tblParcelasAPrazoMouseClicked

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        confirmar();
    }//GEN-LAST:event_btnOkActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        confirmar();
    }//GEN-LAST:event_formWindowClosing

    private void btnPesquisarPessoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarPessoaActionPerformed
        pesquisarCliente();
    }//GEN-LAST:event_btnPesquisarPessoaActionPerformed

    private void btnRemoverClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverClienteActionPerformed
        removerCliente();
    }//GEN-LAST:event_btnRemoverClienteActionPerformed

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
    private javax.swing.JButton btnPesquisarPessoa;
    private javax.swing.JButton btnRemover;
    private javax.swing.JButton btnRemoverCliente;
    private javax.swing.JComboBox<Object> cboMeioDePagamento;
    private javax.swing.JCheckBox chkEntrada;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblSimulacao;
    private javax.swing.JPanel pnlParcelamento;
    private javax.swing.JTable tblParcelasAPrazo;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JFormattedTextField txtEmAberto;
    private javax.swing.JFormattedTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
