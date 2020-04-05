/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.financeiro.conta;

import java.awt.Color;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.jtable.financeiro.CaixaJTableModel;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.principal.financeiro.CaixaItemTipo;
import model.mysql.bean.principal.financeiro.Conta;
import model.mysql.dao.principal.financeiro.CaixaDAO;
import model.mysql.dao.principal.financeiro.CaixaItemDAO;
import model.mysql.dao.principal.financeiro.ContaDAO;
import model.nosql.ContaTipoEnum;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import printing.financeiro.CaixaPorPeriodoReport;
import util.DateTime;
import util.Decimal;
import util.JSwing;

/**
 *
 * @author ivand
 */
public class ContaCorrenteView extends javax.swing.JInternalFrame {

    private static ContaCorrenteView singleInstance = null;

    LocalDate dataInicial, dataFinal;

    BigDecimal totalCredito, totalDebito, saldo, saldoGeral;

    CaixaJTableModel caixaJTableModel = new CaixaJTableModel();

    ContaDAO contaDAO = new ContaDAO();
    CaixaItemDAO caixaItemDAO = new CaixaItemDAO();

    Conta conta;

    CaixaDAO caixaDAO = new CaixaDAO();
    List<CaixaItem> caixaItens = new ArrayList<>();

    public static ContaCorrenteView getSingleInstance() {
        if (singleInstance == null) {
            singleInstance = new ContaCorrenteView();
        }
        return singleInstance;
    }

    /**
     * Creates new form CaixaView
     */
    private ContaCorrenteView() {
        initComponents();
        JSwing.startComponentsBehavior(this);

        txtDataInicial.setText(DateTime.toString(LocalDate.now()));
        txtDataFinal.setText(DateTime.toString(LocalDate.now()));

        carregarContas();

        formatarTabela();

        //carregarTabela();
    }

    private void carregarContas() {
        List<Conta> contas = contaDAO.findByTipo(ContaTipoEnum.CONTA_CORRENTE);

        if (contas.isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Ainda não existem contas.", "Atenção", JOptionPane.INFORMATION_MESSAGE);

        } else {
            Conta contaSelecionada = null;
            if (cboConta.getSelectedItem() != null) {
                contaSelecionada = (Conta) cboConta.getSelectedItem();
            }

            cboConta.removeAllItems();
            for (Conta c : contas) {
                cboConta.addItem(c);
            }

            if (contaSelecionada != null) {
                cboConta.setSelectedItem(contaSelecionada);
            }
        }
    }

    private void formatarTabela() {
        tblItens.setModel(caixaJTableModel);

        tblItens.setRowHeight(30);
        tblItens.setIntercellSpacing(new Dimension(10, 10));

        tblItens.getColumn("Id").setPreferredWidth(100);
        tblItens.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblItens.getColumn("Data").setPreferredWidth(180);
        tblItens.getColumn("Data").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblItens.getColumn("Descrição").setPreferredWidth(480);

        tblItens.getColumn("Observação").setPreferredWidth(480);

        tblItens.getColumn("MP").setPreferredWidth(80);
        tblItens.getColumn("MP").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblItens.getColumn("Crédito").setPreferredWidth(120);
        tblItens.getColumn("Crédito").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblItens.getColumn("Débito").setPreferredWidth(120);
        tblItens.getColumn("Débito").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblItens.getColumn("Saldo").setPreferredWidth(120);
        tblItens.getColumn("Saldo").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
    }

    private void carregarTabela() {
        dataInicial = DateTime.fromStringToLocalDate(txtDataInicial.getText());
        dataFinal = DateTime.fromStringToLocalDate(txtDataFinal.getText());

        if (conta != null) {
            conta = contaDAO.findById(conta.getId()); //2020-01-30 atualizar o objeto para enxergar os caixaItens e conseguir obter o Saldo Geral correto
        }
        
        caixaItens = new CaixaItemDAO().findByCriteria(dataInicial, dataFinal, conta);

        caixaJTableModel.clear();
        caixaJTableModel.addList(caixaItens);

        if (tblItens.getRowCount() > 0) {
            int index = tblItens.getRowCount() - 1;
            tblItens.setRowSelectionInterval(index, index);
            tblItens.scrollRectToVisible(tblItens.getCellRect(index, 0, true));
        }

        exibirTotal();

    }

    private void clickConta() {
        conta = (Conta) cboConta.getSelectedItem();
        //System.out.println("act conta");

        if (conta != null) {
            txtData.setText(DateTime.toString(conta.getData()));

            if (conta.getData().compareTo(LocalDate.now()) != 0) {
                txtData.setForeground(Color.RED);
            } else {
                txtData.setForeground(Color.BLUE);
            }
        }

        carregarTabela();
    }

    private void exibirTotal() {
        totalCredito = BigDecimal.ZERO;
        totalDebito = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
        saldoGeral = BigDecimal.ZERO;

        if (!caixaItens.isEmpty()) {
            totalCredito = caixaItens.stream().map(CaixaItem::getCredito).reduce(BigDecimal::add).get();
            totalDebito = caixaItens.stream().map(CaixaItem::getDebito).reduce(BigDecimal::add).get();
            saldo = totalCredito.subtract(totalDebito);
        }
        if (conta != null) {
            saldoGeral = conta.getSaldo();
        }

        txtTotalCredito.setText(Decimal.toString(totalCredito));
        txtTotalDebito.setText(Decimal.toString(totalDebito));
        txtSaldo.setText(Decimal.toString(saldo));
        
        txtSaldoGeral.setText(Decimal.toString(saldoGeral));
    }

    private void entrada() {
        if (conta == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione uma conta!", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {
            new ContaSuprimentoView(conta);
            carregarTabela();
        }
    }

    private void saida() {
        if (conta == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione uma conta!", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {
            new ContaSangriaView(conta);
            carregarTabela();
        }
    }
    
    private void transferir() {
        if (conta == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione uma conta!", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {
            new ContaTransferirView(conta);
            carregarTabela();
        }
    }

    private void estornar() {
        int rowIndex = tblItens.getSelectedRow();
        if (rowIndex < 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione um registro", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {
            System.out.println("rowIndex " + rowIndex);
            CaixaItem itemEstornar = caixaJTableModel.getRow(rowIndex);

            if (itemEstornar.getEstorno() != null) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Este item já foi estornado.", "Atenção", JOptionPane.WARNING_MESSAGE);

            } else if (itemEstornar.getCaixaItemTipo().equals(CaixaItemTipo.ESTORNO)) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Este item já é um estorno.", "Atenção", JOptionPane.WARNING_MESSAGE);

            } else {
                int resposta = JOptionPane.showConfirmDialog(MAIN_VIEW, "Estornar o item selecionado?", "Atenção", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (resposta == JOptionPane.OK_OPTION) {
                    caixaItemDAO.estornar(itemEstornar);
                    carregarTabela();
                }
            }
        }
    }

    private void imprimir() {
        if (conta == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione uma conta!", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {
            CaixaPorPeriodoReport.gerar(caixaItens, dataInicial, dataFinal, totalCredito, totalDebito, saldo);
        }
    }

    private void contas() {
        new ContaListaView(ContaTipoEnum.CONTA_CORRENTE);
        carregarContas();
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
        btnFiltrar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtDataInicial = new javax.swing.JFormattedTextField();
        txtDataFinal = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        cboConta = new javax.swing.JComboBox<>();
        txtData = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        btnImprimir = new javax.swing.JButton();
        btnEntrada = new javax.swing.JButton();
        btnSaida = new javax.swing.JButton();
        btnContas = new javax.swing.JButton();
        btnEstornar = new javax.swing.JButton();
        btnTransferir = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblItens = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtTotalCredito = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtTotalDebito = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtSaldo = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        txtSaldoGeral = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();

        setTitle("Caixa por Período");
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
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
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                formMouseEntered(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnFiltrar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnFiltrar.setText("Atualizar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Data Inicial");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Data Final");

        txtDataInicial.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDataInicial.setName("data"); // NOI18N

        txtDataFinal.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDataFinal.setName("data"); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Conta");

        cboConta.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cboConta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboContaActionPerformed(evt);
            }
        });

        txtData.setEditable(false);
        txtData.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtData.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtData.setText("--/--/----");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(cboConta, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(129, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(btnFiltrar)
                    .addComponent(jLabel6)
                    .addComponent(cboConta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-printer-20.png"))); // NOI18N
        btnImprimir.setText("Imprimir");
        btnImprimir.setContentAreaFilled(false);
        btnImprimir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnImprimir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        btnEntrada.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-add-20.png"))); // NOI18N
        btnEntrada.setText("Entrada");
        btnEntrada.setContentAreaFilled(false);
        btnEntrada.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEntrada.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntradaActionPerformed(evt);
            }
        });

        btnSaida.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-do-not-disturb-20.png"))); // NOI18N
        btnSaida.setText("Saída");
        btnSaida.setContentAreaFilled(false);
        btnSaida.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSaida.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSaida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaidaActionPerformed(evt);
            }
        });

        btnContas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-money-bag-20.png"))); // NOI18N
        btnContas.setText("Contas");
        btnContas.setContentAreaFilled(false);
        btnContas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnContas.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnContas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContasActionPerformed(evt);
            }
        });

        btnEstornar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-undo-20.png"))); // NOI18N
        btnEstornar.setText("Estornar");
        btnEstornar.setContentAreaFilled(false);
        btnEstornar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEstornar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEstornar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEstornarActionPerformed(evt);
            }
        });

        btnTransferir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-money-transfer-20.png"))); // NOI18N
        btnTransferir.setText("Tranferir");
        btnTransferir.setContentAreaFilled(false);
        btnTransferir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTransferir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnTransferir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransferirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnEntrada)
                .addGap(18, 18, 18)
                .addComponent(btnSaida)
                .addGap(18, 18, 18)
                .addComponent(btnTransferir)
                .addGap(18, 18, 18)
                .addComponent(btnEstornar)
                .addGap(18, 18, 18)
                .addComponent(btnImprimir)
                .addGap(18, 18, 18)
                .addComponent(btnContas)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnImprimir)
                    .addComponent(btnEntrada)
                    .addComponent(btnSaida)
                    .addComponent(btnContas)
                    .addComponent(btnEstornar)
                    .addComponent(btnTransferir))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblItens.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblItens.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblItens);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Total Crédito");

        txtTotalCredito.setEditable(false);
        txtTotalCredito.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalCredito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Total Débito");

        txtTotalDebito.setEditable(false);
        txtTotalDebito.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalDebito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Saldo");

        txtSaldo.setEditable(false);
        txtSaldo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtSaldo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel37.setBackground(javax.swing.UIManager.getDefaults().getColor("InternalFrame.activeTitleBackground"));
        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel37.setForeground(java.awt.Color.white);
        jLabel37.setText("Período");
        jLabel37.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel37.setOpaque(true);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel37)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtTotalCredito, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(23, 23, 23)
                .addComponent(txtTotalDebito, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(txtSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalDebito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtTotalCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap())
            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtSaldoGeral.setEditable(false);
        txtSaldoGeral.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtSaldoGeral.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Saldo");

        jLabel38.setBackground(javax.swing.UIManager.getDefaults().getColor("InternalFrame.activeTitleBackground"));
        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel38.setForeground(java.awt.Color.white);
        jLabel38.setText("Geral");
        jLabel38.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel38.setOpaque(true);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel38)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(txtSaldoGeral, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtSaldoGeral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        singleInstance = null;
    }//GEN-LAST:event_formInternalFrameClosed

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
        //System.out.println("activated...");
    }//GEN-LAST:event_formInternalFrameActivated

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        //System.out.println("shown...");
    }//GEN-LAST:event_formComponentShown

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        //System.out.println("focus gained...");
    }//GEN-LAST:event_formFocusGained

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        //System.out.println("opened...");
    }//GEN-LAST:event_formInternalFrameOpened

    private void formMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseEntered
        //System.out.println("mouse entered...");
    }//GEN-LAST:event_formMouseEntered

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        imprimir();
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void btnEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntradaActionPerformed
        entrada();
    }//GEN-LAST:event_btnEntradaActionPerformed

    private void btnSaidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaidaActionPerformed
        saida();
    }//GEN-LAST:event_btnSaidaActionPerformed

    private void btnContasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContasActionPerformed
        contas();
    }//GEN-LAST:event_btnContasActionPerformed

    private void cboContaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboContaActionPerformed
        clickConta();
    }//GEN-LAST:event_cboContaActionPerformed

    private void btnEstornarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEstornarActionPerformed
        estornar();
    }//GEN-LAST:event_btnEstornarActionPerformed

    private void btnTransferirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransferirActionPerformed
        transferir();
    }//GEN-LAST:event_btnTransferirActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnContas;
    private javax.swing.JButton btnEntrada;
    private javax.swing.JButton btnEstornar;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnSaida;
    private javax.swing.JButton btnTransferir;
    private javax.swing.JComboBox<Object> cboConta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblItens;
    private javax.swing.JTextField txtData;
    private javax.swing.JFormattedTextField txtDataFinal;
    private javax.swing.JFormattedTextField txtDataInicial;
    private javax.swing.JTextField txtSaldo;
    private javax.swing.JTextField txtSaldoGeral;
    private javax.swing.JTextField txtTotalCredito;
    private javax.swing.JTextField txtTotalDebito;
    // End of variables declaration//GEN-END:variables
}
