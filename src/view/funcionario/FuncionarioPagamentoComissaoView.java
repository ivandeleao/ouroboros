/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.funcionario;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import model.jtable.funcionario.FuncionarioHistoricoPorDocumentoTableModel;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.ComissaoPagamento;
import model.mysql.bean.principal.financeiro.Caixa;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.dao.fiscal.MeioDePagamentoDAO;
import model.mysql.dao.principal.financeiro.CaixaItemDAO;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.financeiro.CaixaItemTipo;
import model.mysql.bean.principal.financeiro.Conta;
import model.mysql.dao.principal.ComissaoPagamentoDAO;
import model.mysql.dao.principal.VendaDAO;
import model.mysql.dao.principal.financeiro.ContaDAO;
import model.nosql.ContaTipoEnum;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.JSwing;
import printing.documento.BobinaReciboIndividualPrint;
import util.DateTime;
import util.Decimal;
import util.jTableFormat.FinanceiroStatusRenderer;

/**
 *
 * @author ivand
 *
 * ESTE FOI CRIADO COMO JDIALOG DESDE O INÍCIO - USAR ESTE COMO REFERÊNCIA PARA
 * OS DEMAIS
 *
 *
 */
public class FuncionarioPagamentoComissaoView extends javax.swing.JDialog {
    Caixa caixa = Ouroboros.FINANCEIRO_CAIXA_PRINCIPAL.getLastCaixa(); //2020-02-28
    List<Venda> documentos = new ArrayList<>();
    VendaDAO vendaDAO = new VendaDAO();
    ComissaoPagamentoDAO comissaoPagamentoDAO = new ComissaoPagamentoDAO();
    
    FuncionarioHistoricoPorDocumentoTableModel funcionarioHistoricoPorDocumentoJTableModel;
    CaixaItemDAO caixaItemDAO = new CaixaItemDAO();
    BigDecimal total, pagoParcial, pagar, pago, troco;

    List<CaixaItem> caixaItens = new ArrayList<>();

    private FuncionarioPagamentoComissaoView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        System.out.println(" ------- mdf --------");
    }

    public FuncionarioPagamentoComissaoView(List<Venda> documentos) {
        super(MAIN_VIEW, true);
        initComponents();
        
        definirAtalhos();
        
        JSwing.startComponentsBehavior(this);
        
        this.documentos = documentos;
        
        funcionarioHistoricoPorDocumentoJTableModel = new FuncionarioHistoricoPorDocumentoTableModel();
        
        //tblVendas.setModel(funcionarioHistoricoPorDocumentoJTableModel);

        formatarTabela();
        
        System.out.println("this.documentos size antes: " + this.documentos.size());
        
        //teste
        funcionarioHistoricoPorDocumentoJTableModel.clear();
        
        System.out.println("this.documentos size: " + this.documentos.size());
        
        funcionarioHistoricoPorDocumentoJTableModel.addList(this.documentos);
        
        
        
        
        
        carregarDados();
        
        this.setLocationRelativeTo(this); //centralizar
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
        public FormKeyStroke(String key){
            this.key = key;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            switch(key){
                case "ESC":
                    dispose();
                    break;
            }
        }
    }
    
    private void carregarDados() {
        carregarTabela();
        
        carregarContas();
        carregarMeioPagamento();
        exibirTotais();
        
    }
    
    private void exibirTotais() {
        
        total = documentos.stream().map(Venda::getTotalComissaoDocumento).reduce(BigDecimal::add).get();
        pagoParcial = documentos.stream().map(Venda::getTotalComissaoDocumentoPago).reduce(BigDecimal::add).get(); // documentos.stream().map(Parcela::getValorQuitado).reduce(BigDecimal::add).get();
        pagar = total.subtract(pagoParcial);
        
        
        txtTotal.setText(Decimal.toString(total));
        txtPagoParcial.setText(Decimal.toString(pagoParcial));
        txtPagar.setText(Decimal.toString(pagar));
        
        pago = Decimal.fromString(txtPago.getText());
        troco = pago.subtract(pagar);
        
        if (troco.compareTo(BigDecimal.ZERO) > 0) {
            txtTroco.setForeground(Color.BLUE);
        } else {
            txtTroco.setForeground(Color.RED);
        }
        
        txtTroco.setText(Decimal.toString(troco));
    }

    
    private void formatarTabela() {
        tblDocumentos.setModel(funcionarioHistoricoPorDocumentoJTableModel);

        tblDocumentos.setRowHeight(30);
        tblDocumentos.setIntercellSpacing(new Dimension(10, 10));

        tblDocumentos.getColumn("Status").setPreferredWidth(60);
        FinanceiroStatusRenderer statusRenderer = new FinanceiroStatusRenderer();
        statusRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblDocumentos.getColumn("Status").setCellRenderer(statusRenderer);
        
        tblDocumentos.getColumn("Documento").setPreferredWidth(60);
        tblDocumentos.getColumn("Documento").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblDocumentos.getColumn("Data").setPreferredWidth(60);
        tblDocumentos.getColumn("Data").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblDocumentos.getColumn("Cliente").setPreferredWidth(300);

        tblDocumentos.getColumn("Produtos").setPreferredWidth(50);
        tblDocumentos.getColumn("Produtos").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblDocumentos.getColumn("Serviços").setPreferredWidth(50);
        tblDocumentos.getColumn("Serviços").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblDocumentos.getColumn("Com.Prod").setPreferredWidth(50);
        tblDocumentos.getColumn("Com.Prod").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblDocumentos.getColumn("Com.Serv").setPreferredWidth(50);
        tblDocumentos.getColumn("Com.Serv").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblDocumentos.getColumn("Total Com").setPreferredWidth(50);
        tblDocumentos.getColumn("Total Com").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblDocumentos.getColumn("Pago").setPreferredWidth(50);
        tblDocumentos.getColumn("Pago").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblDocumentos.getColumn("Receber").setPreferredWidth(50);
        tblDocumentos.getColumn("Receber").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
    }

    private void carregarTabela() {
        System.out.println("carregarTabela...");
        int index = tblDocumentos.getSelectedRow();
        
        System.out.println("documentos size: " + documentos.size());
        
        funcionarioHistoricoPorDocumentoJTableModel.clear();
        funcionarioHistoricoPorDocumentoJTableModel.addList(documentos);

        if (index >= 0) {
            tblDocumentos.setRowSelectionInterval(index, index);
        } else if (documentos.size() > 0) {
            index = tblDocumentos.getRowCount() - 1;
            tblDocumentos.setRowSelectionInterval(index, index);
            tblDocumentos.scrollRectToVisible(tblDocumentos.getCellRect(index, 0, true));
        }
        
        exibirTotais();
    }
    
    private void carregarMeioPagamento() {
        for(MeioDePagamento mp : new MeioDePagamentoDAO().findAll()) {
            cboMeioPagamento.addItem(mp);
        }
    }
    
    private void carregarContas() {
        for(Conta mp : new ContaDAO().findAll()) {
            cboConta.addItem(mp);
        }
    }
    
    private void carregarDataConta() {
        Conta conta = (Conta)cboConta.getSelectedItem();
        if(conta.getContaTipo().equals(ContaTipoEnum.CAIXA)) {
            txtData.setText("--/--/----");
            
        } else {
        
            LocalDate dataConta = (conta).getData();
            txtData.setText(DateTime.toString(dataConta));

            if(dataConta.compareTo(LocalDate.now()) != 0) {
                txtData.setForeground(Color.RED);
            } else {
                txtData.setForeground(Color.BLUE);
            }
        }
    }
    
    private void editar() {
        /*Parcela p = funcionarioHistoricoPorDocumentoJTableModel.getRow(tblDocumentos.getSelectedRow());
        new PessoaParcelaEditarView(MAIN_VIEW, p);
        carregarTabela();*/
    }
    
    
    private void confirmar() {
        BigDecimal valorPago = Decimal.fromString(txtPago.getText());
        
        
        MeioDePagamento meioPagamento = (MeioDePagamento) cboMeioPagamento.getSelectedItem();
        
        Conta conta = (Conta) cboConta.getSelectedItem();
        
        //LocalDate dataRecebimento = DateTime.fromStringToLocalDate(txtDataRecebimento.getText());
        
        ////BigDecimal totalParcelas = documentos.stream().map(Parcela::getValor).reduce(BigDecimal::add).get();
        
        
        if(valorPago.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não há valor pago.", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtPago.requestFocus();
            
        /*} else if (chkAlterarDataRecebimento.isSelected() && dataRecebimento == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Data inválida.", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtDataRecebimento.requestFocus();
            
        } else if (chkAlterarDataRecebimento.isSelected() && dataRecebimento.compareTo(LocalDate.now()) > 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Data não pode ser futura.", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtDataRecebimento.requestFocus();
        */
        } else {
            
            ////List<CaixaItem> caixaItensCheque = new ArrayList<>();
            
            
            
            BigDecimal valorEfetivo = troco.compareTo(BigDecimal.ZERO) > 0 ? valorPago.subtract(troco) : valorPago;
            CaixaItem caixaItem = new CaixaItem(CaixaItemTipo.FUNCIONARIO, meioPagamento, "", BigDecimal.ZERO, valorEfetivo);
            
            BigDecimal valorRestante = valorEfetivo;
            
            for(Venda documento : documentos) {
                
                if(valorRestante.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal valorLancamento; //doc saída => crédito // doc entrada => débito
                    
                    if(valorRestante.compareTo(documento.getTotalComissaoDocumentoReceber()) >= 0) {
                        valorLancamento = documento.getTotalComissaoDocumentoReceber();
                    } else {
                        valorLancamento = valorRestante;
                    }
                    System.out.println("valorLancamento: " + valorLancamento);
                    
                    valorRestante = valorRestante.subtract(valorLancamento);
                    ////CaixaItem caixaItem;
                    
                    ////caixaItem = new CaixaItem(CaixaItemTipo.FUNCIONARIO, meioPagamento, "", BigDecimal.ZERO, valorLancamento);
                    
                    /*if (chkAlterarDataRecebimento.isSelected()) {
                        caixaItem.setDataHoraRecebimento(dataRecebimento.atTime(LocalTime.MIN));
                    }*/
                    
                    ////documento.addCaixaItem(caixaItem);
                    
                    //--
                    
                    
                    if(conta.getContaTipo().equals(ContaTipoEnum.CAIXA)) {
                        caixa.addCaixaItem(caixaItem);
                    } else {
                        conta.addCaixaItem(caixaItem);
                    }
                    
                    caixaItemDAO.save(caixaItem);

                    caixaItens.add(caixaItem);
                    
                    //caixaItensCheque.add(caixaItem); ??
                    
                    ComissaoPagamento comissaoPagamento = new ComissaoPagamento(documento, caixaItem, valorLancamento);
                    comissaoPagamento = comissaoPagamentoDAO.save(comissaoPagamento);
                    
                    caixaItem.addComissaoPagamento(comissaoPagamento);
                    documento.addComissaoPagamento(comissaoPagamento);
                    
                    
                }
                
                

            }
            
            
            /*
            if (documentos.get(0).getVenda().getTipoOperacao().equals(TipoOperacao.SAIDA)) {
                int resposta = JOptionPane.showConfirmDialog(MAIN_VIEW, "Imprimir recibo?", "Imprimir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(resposta == JOptionPane.YES_OPTION) {
                    imprimir();
                }
            }*/
            
            dispose();
            
            /*if (meioPagamento.equals(MeioDePagamento.CHEQUE)) {
                Cheque cheque = new Cheque();
                cheque.setValor(valorPago);
                new ChequeCadastroView(cheque, caixaItensCheque);
            }*/
            
            
        }
    }
    
    private void imprimir() {
        BobinaReciboIndividualPrint.imprimirRecibo(caixaItens);
        
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
        tblDocumentos = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtPago = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        cboMeioPagamento = new javax.swing.JComboBox<>();
        cboConta = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtData = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtTroco = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        txtPagoParcial = new javax.swing.JTextField();
        txtPagar = new javax.swing.JTextField();
        btnOk = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pagamento de Comissão");
        setResizable(false);

        tblDocumentos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblDocumentos.setModel(new javax.swing.table.DefaultTableModel(
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
        tblDocumentos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDocumentosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblDocumentos);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Valor Pago");

        txtPago.setForeground(java.awt.Color.blue);
        txtPago.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPago.setText("0,00");
        txtPago.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtPago.setName("decimal"); // NOI18N
        txtPago.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPagoKeyReleased(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Meio de Pagamento");

        cboMeioPagamento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        cboConta.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cboConta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboContaActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Conta");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Data Conta");

        txtData.setEditable(false);
        txtData.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtData.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtData.setText("--/--/----");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Troco");

        txtTroco.setEditable(false);
        txtTroco.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtTroco.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTroco.setText("0,00");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 113, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cboConta, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboMeioPagamento, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtData, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPago, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                            .addComponent(txtTroco))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboConta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboMeioPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTroco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setText("Pago Parcial");

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel16.setText("Total");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setText("À pagar");

        txtTotal.setEditable(false);
        txtTotal.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotal.setText("0,00");

        txtPagoParcial.setEditable(false);
        txtPagoParcial.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtPagoParcial.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPagoParcial.setText("0,00");

        txtPagar.setEditable(false);
        txtPagar.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtPagar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPagar.setText("0,00");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 150, Short.MAX_VALUE)
                        .addComponent(txtPagoParcial, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPagoParcial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(112, Short.MAX_VALUE))
        );

        btnOk.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnOk.setText("Confirmar");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        btnCancelar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setForeground(java.awt.Color.red);
        jLabel2.setText("*Data de Recebimento não altera a data de registro financeiro (caixa/conta)");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnCancelar)
                                .addGap(18, 18, 18)
                                .addComponent(btnOk))
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk)
                    .addComponent(btnCancelar)
                    .addComponent(jLabel2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblDocumentosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDocumentosMouseClicked
        if(evt.getClickCount() == 2) {
            editar();
        }
    }//GEN-LAST:event_tblDocumentosMouseClicked

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        confirmar();
    }//GEN-LAST:event_btnOkActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void txtPagoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPagoKeyReleased
        exibirTotais();
    }//GEN-LAST:event_txtPagoKeyReleased

    private void cboContaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboContaActionPerformed
        carregarDataConta();
    }//GEN-LAST:event_cboContaActionPerformed

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
            java.util.logging.Logger.getLogger(FuncionarioPagamentoComissaoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FuncionarioPagamentoComissaoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FuncionarioPagamentoComissaoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FuncionarioPagamentoComissaoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FuncionarioPagamentoComissaoView dialog = new FuncionarioPagamentoComissaoView(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnOk;
    private javax.swing.JComboBox<Object> cboConta;
    private javax.swing.JComboBox<Object> cboMeioPagamento;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblDocumentos;
    private javax.swing.JTextField txtData;
    private javax.swing.JTextField txtPagar;
    private javax.swing.JFormattedTextField txtPago;
    private javax.swing.JTextField txtPagoParcial;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtTroco;
    // End of variables declaration//GEN-END:variables
}
