/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.financeiro;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.financeiro.Caixa;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.principal.financeiro.CaixaItemTipo;
import model.mysql.dao.principal.ParcelaDAO;
import model.mysql.dao.fiscal.MeioDePagamentoDAO;
import model.mysql.dao.principal.financeiro.CaixaDAO;
import model.mysql.dao.principal.financeiro.CaixaItemDAO;
import model.jtable.pessoa.CrediarioRecebimentoJTableModel;
import model.mysql.bean.principal.financeiro.Conta;
import model.mysql.dao.principal.financeiro.ContaDAO;
import model.nosql.ContaTipoEnum;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.TO_PRINTER_PATH;
import printing.documento.TermicaPrint;
import printing.PrintPDFBox;
import util.Decimal;
import util.JSwing;
import view.Toast;
import static ouroboros.Ouroboros.IMPRESSORA_CUPOM;
import util.Cor;
import util.DateTime;
import view.pessoa.PessoaParcelaEditarView;

/**
 *
 * @author ivand
 *
 * ESTE FOI CRIADO COMO JDIALOG DESDE O INÍCIO - USAR ESTE COMO REFERÊNCIA PARA
 * OS DEMAIS
 *
 *
 */
public class RecebimentoNovoView extends javax.swing.JDialog {
    Caixa caixa = Ouroboros.FINANCEIRO_CAIXA_PRINCIPAL.getLastCaixa(); //2020-02-28
    List<Parcela> parcelas = new ArrayList<>();
    ParcelaDAO parcelaDAO = new ParcelaDAO();
    CrediarioRecebimentoJTableModel crediarioRecebimentoJTableModel = new CrediarioRecebimentoJTableModel();
    CaixaItemDAO caixaItemDAO = new CaixaItemDAO();
    List<JFormattedTextField> txtRecebimentoList = new ArrayList<>();
    BigDecimal total, recebidoParcial, multa, juros, totalAtual, acrescimoPercentual, acrescimoMonetario, descontoPercentual, descontoMonetario, recebido, troco;

    /**
     * Creates new form ParcelamentoView
     */
    private RecebimentoNovoView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public RecebimentoNovoView(List<Parcela> parcelaList) {
        super(MAIN_VIEW, true);
        initComponents();
        definirAtalhos();
        
        JSwing.startComponentsBehavior(this);
        
        this.parcelas = parcelaList;
        
        tblParcelas.setModel(crediarioRecebimentoJTableModel);

        formatarTabela();
        
        carregarDados();
        
        formatarAcrescimoTipo();
        formatarDescontoTipo();


        this.setLocationRelativeTo(this); //centralizar
        this.setVisible(true);
    }
    
    private void definirAtalhos() {
        InputMap im = rootPane.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = rootPane.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "fechar");
        am.put("fechar", new FormKeyStroke("ESC"));
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "confirmarComRecibo");
        am.put("confirmarComRecibo", new FormKeyStroke("F11"));
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "confirmar");
        am.put("confirmar", new FormKeyStroke("F12"));
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
                    //confirmar(true);
                    break;
                case "F12":
                    //confirmar(false);
                    break;
            }
        }
    }
    
    private void carregarDados() {
        carregarTabela();
        
        BigDecimal acrescimo = parcelas.stream().map(Parcela::getAcrescimoSemTipo).reduce(BigDecimal::add).get();
        BigDecimal desconto = parcelas.stream().map(Parcela::getDescontoSemTipo).reduce(BigDecimal::add).get();
        
        System.out.println("acrescimo: " + acrescimo);
        System.out.println("desconto: " + desconto);
        
        if(acrescimo.compareTo(BigDecimal.ZERO) > 0 || desconto.compareTo(BigDecimal.ZERO) > 0) {
            btnAcrescimoTipo.setEnabled(false);
            btnDescontoTipo.setEnabled(false);
            
            txtAcrescimo.setEnabled(false);
            txtDesconto.setEnabled(false);
        }
        
        
        /*
        btnAcrescimoTipo.setText(parcela.getAcrescimoTipo());
        btnDescontoTipo.setText(tabelaPrecoVariacao.getDescontoTipo());
        
        txtAcrescimo.setText(Decimal.toString(tabelaPrecoVariacao.getAcrescimoSemTipo()));
        txtDesconto.setText(Decimal.toString(tabelaPrecoVariacao.getDescontoSemTipo()));
        */
        
        
        exibirTotais();
        carregarMeioPagamento();
        carregarContas();
    }
    
    private void exibirTotais() {
        
        total = parcelas.stream().map(Parcela::getValor).reduce(BigDecimal::add).get();
        
        recebidoParcial = parcelas.stream().map(Parcela::getValorQuitado).reduce(BigDecimal::add).get();
        
        multa = parcelas.stream().map(Parcela::getMultaCalculada).reduce(BigDecimal::add).get().setScale(2, RoundingMode.HALF_UP);
        
        juros = parcelas.stream().map(Parcela::getJurosCalculado).reduce(BigDecimal::add).get().setScale(2, RoundingMode.HALF_UP);
        
        BigDecimal acrescimo = Decimal.fromString(txtAcrescimo.getText());
        BigDecimal desconto = Decimal.fromString(txtDesconto.getText());
        
        if(btnAcrescimoTipo.getText().equals("%")) {
            acrescimoMonetario = BigDecimal.ZERO;
            acrescimoPercentual = acrescimo;
        } else {
            acrescimoMonetario = acrescimo;
            acrescimoPercentual = BigDecimal.ZERO;
        }
        
        if(btnDescontoTipo.getText().equals("%")) {
            descontoMonetario = BigDecimal.ZERO;
            descontoPercentual = desconto;
        } else {
            descontoMonetario = desconto;
            descontoPercentual = BigDecimal.ZERO;
        }
        
        
        
        totalAtual = parcelas.stream().map(Parcela::getValorAtual).reduce(BigDecimal::add).get();
        totalAtual = totalAtual.setScale(2, RoundingMode.HALF_UP);
        if(chkNaoCobrarMulta.isSelected()) {
            totalAtual = totalAtual.subtract(multa);
        }
        if(chkNaoCobrarJuros.isSelected()) {
            totalAtual = totalAtual.subtract(juros);
        }
        //Acréscimo e desconto
        totalAtual = totalAtual
                .add(totalAtual.multiply(acrescimoPercentual).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP))
                .add(acrescimoMonetario)
                .setScale(2, RoundingMode.HALF_UP);
        
        totalAtual = totalAtual
                .subtract(totalAtual.multiply(descontoPercentual).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP))
                .subtract(descontoMonetario)
                .setScale(2, RoundingMode.HALF_UP);
        
        txtTotal.setText(Decimal.toString(total));
        txtRecebidoParcial.setText(Decimal.toString(recebidoParcial));
        txtMulta.setText(Decimal.toString(multa));
        txtJuros.setText(Decimal.toString(juros));
        txtTotalAtual.setText(Decimal.toString(totalAtual));
        
        recebido = Decimal.fromString(txtRecebido.getText());
        troco = recebido.subtract(totalAtual);
        
        if (troco.compareTo(BigDecimal.ZERO) > 0) {
            txtTroco.setForeground(Color.BLUE);
        } else {
            txtTroco.setForeground(Color.RED);
        }
        
        txtTroco.setText(Decimal.toString(troco));
    }

    
    private void formatarTabela() {
        tblParcelas.setRowHeight(30);
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
        
        tblParcelas.getColumn("D.Atraso").setPreferredWidth(120);
        tblParcelas.getColumn("D.Atraso").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
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
        
        tblParcelas.getColumn("Valor Atual").setPreferredWidth(120);
        tblParcelas.getColumn("Valor Atual").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblParcelas.getColumn("Acrésc").setPreferredWidth(120);
        tblParcelas.getColumn("Acrésc").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblParcelas.getColumn("Desc").setPreferredWidth(120);
        tblParcelas.getColumn("Desc").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblParcelas.getColumn("MP").setPreferredWidth(100);
    }

    private void carregarTabela() {
        int index = tblParcelas.getSelectedRow();
        
        crediarioRecebimentoJTableModel.clear();
        crediarioRecebimentoJTableModel.addList(parcelas);

        if (index >= 0) {
            tblParcelas.setRowSelectionInterval(index, index);
        } else if (parcelas.size() > 0) {
            index = tblParcelas.getRowCount() - 1;
            tblParcelas.setRowSelectionInterval(index, index);
            tblParcelas.scrollRectToVisible(tblParcelas.getCellRect(index, 0, true));
        }
        /*
        if (parcelas.size() > 0) {
            int index = tblParcelas.getRowCount() - 1;
            tblParcelas.setRowSelectionInterval(index, index);
            tblParcelas.scrollRectToVisible(tblParcelas.getCellRect(index, 0, true));
        }*/
        
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
        Parcela p = crediarioRecebimentoJTableModel.getRow(tblParcelas.getSelectedRow());
        /*if(p.getValorQuitado().compareTo(BigDecimal.ZERO) > 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Parcela quitada. Não é possível editar.", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {*/
            new PessoaParcelaEditarView(MAIN_VIEW, p);
            carregarTabela();
        //}
    }
    
    private void alternarAcrescimoTipo() {
        btnAcrescimoTipo.setText(btnAcrescimoTipo.getText().equals("%") ? "$" : "%");

        formatarAcrescimoTipo();
        
        exibirTotais();
    }
    
    private void formatarAcrescimoTipo() {
        btnAcrescimoTipo.setBackground(btnAcrescimoTipo.getText().equals("%") ? Cor.AZUL : Cor.LARANJA);

        btnAcrescimoTipo.repaint();
    }
    
    
    private void alternarDescontoTipo() {
        btnDescontoTipo.setText(btnDescontoTipo.getText().equals("%") ? "$" : "%");

        formatarDescontoTipo();
        
        exibirTotais();
    }
    
    private void formatarDescontoTipo() {
        btnDescontoTipo.setBackground(btnDescontoTipo.getText().equals("%") ? Cor.AZUL : Cor.LARANJA);

        btnDescontoTipo.repaint();
    }
    
    private void editarAcrescimo() {
        acrescimoMonetario = Decimal.fromString(txtAcrescimo.getText());
        if (acrescimoMonetario.compareTo(BigDecimal.ZERO) > 0) {
            txtDesconto.setText("0,00");
        }
        exibirTotais();
    }
    
    private void editarDesconto() {
        descontoMonetario = Decimal.fromString(txtDesconto.getText());
        if (descontoMonetario.compareTo(BigDecimal.ZERO) > 0) {
            txtAcrescimo.setText("0,00");
        }
        exibirTotais();
    }
    
    private void confirmar() {
        BigDecimal valorRecebido = Decimal.fromString(txtRecebido.getText());
        BigDecimal valorRestante = valorRecebido;
        
        MeioDePagamento meioPagamento = (MeioDePagamento) cboMeioPagamento.getSelectedItem();
        
        Conta conta = (Conta) cboConta.getSelectedItem();
        
        ////BigDecimal totalParcelas = parcelas.stream().map(Parcela::getValor).reduce(BigDecimal::add).get();
        
        if(valorRecebido.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não há valor recebido.", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtRecebido.requestFocus();
            
        } else {
            
            //Distribuir Acréscimo----------------------------------------------
            if(btnAcrescimoTipo.isEnabled() && acrescimoMonetario.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal totalReverso = BigDecimal.ZERO;
                for (Parcela parcela : parcelas) {

                        BigDecimal valorRateio = (new BigDecimal(100)).divide(total, 2, RoundingMode.HALF_UP).multiply(parcela.getValorAtual()).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal valorAcrescimo = (acrescimoMonetario).multiply(valorRateio).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                        totalReverso = totalReverso.add(valorAcrescimo);

                        if (parcelas.lastIndexOf(parcela) == parcelas.size() - 1) {
                            valorAcrescimo = valorAcrescimo.add(acrescimoMonetario).subtract(totalReverso);

                            /*if (valorAcrescimo.compareTo(BigDecimal.ZERO) < 0) {
                                for ( Parcela parcelaCorrigir : parcelas) {
                                    parcelaCorrigir.setAcrescimoMonetario(parcelaCorrigir.getAcrescimoMonetario().add(valorAcrescimo));
                                    valorAcrescimo = BigDecimal.ZERO;
                                    break;
                                }
                            }*/

                        }
                        parcela.setAcrescimoPercentual(BigDecimal.ZERO);
                        parcela.setAcrescimoMonetario(valorAcrescimo);

                }
            }
            //Fim Distribuir Acréscimo------------------------------------------
            
            //Distribuir Desconto-----------------------------------------------
            if(btnDescontoTipo.isEnabled() && descontoMonetario.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal totalReverso = BigDecimal.ZERO;
                for (Parcela parcela : parcelas) {

                        BigDecimal valorRateio = (new BigDecimal(100)).divide(total, 2, RoundingMode.HALF_UP).multiply(parcela.getValorAtual()).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal valorDesconto = (descontoMonetario).multiply(valorRateio).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                        totalReverso = totalReverso.add(valorDesconto);

                        if (parcelas.lastIndexOf(parcela) == parcelas.size() - 1) {
                            valorDesconto = valorDesconto.add(descontoMonetario).subtract(totalReverso);

                            if (valorDesconto.compareTo(BigDecimal.ZERO) < 0) {
                                for ( Parcela parcelaCorrigir : parcelas) {
                                    parcelaCorrigir.setDescontoMonetario(parcelaCorrigir.getDescontoMonetario().add(valorDesconto));
                                    valorDesconto = BigDecimal.ZERO;
                                    break;
                                }
                            }

                        }
                        parcela.setDescontoPercentual(BigDecimal.ZERO);
                        parcela.setDescontoMonetario(valorDesconto);

                }
            }
            //Fim Distribuir Desconto-------------------------------------------
            

            for(Parcela parcela : parcelas) {
                
                //zerar multa e juros
                if(chkNaoCobrarMulta.isSelected()) {
                    parcela.setMulta(BigDecimal.ZERO);
                }
                if(chkNaoCobrarJuros.isSelected()) {
                    parcela.setJurosMonetario(BigDecimal.ZERO);
                    parcela.setJurosPercentual(BigDecimal.ZERO);
                }
                
                if(btnAcrescimoTipo.isEnabled()) {
                    parcela.setAcrescimoPercentual(acrescimoPercentual);
                    parcela.setDescontoPercentual(descontoPercentual);

                    //parcela.setAcrescimoMonetario(acrescimoMonetario);
                    //parcela.setDescontoMonetario(descontoMonetario);
                }
                
                
                parcela = parcelaDAO.save(parcela);
                

                if(valorRestante.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal credito;
                    
                    if(valorRestante.compareTo(parcela.getValorAtual()) >= 0) {
                        credito = parcela.getValorAtual();
                    } else {
                        credito = valorRestante;
                    }
                    
                    valorRestante = valorRestante.subtract(credito);
                
                    CaixaItem caixaItem = new CaixaItem(CaixaItemTipo.DOCUMENTO, meioPagamento, "", credito, BigDecimal.ZERO);
                    
                    parcela.addRecebimento(caixaItem);
                    
                    if(conta.getContaTipo().equals(ContaTipoEnum.CAIXA)) {
                        System.out.println("caixa");
                        caixa.addCaixaItem(caixaItem);
                    } else {
                        System.out.println("conta");
                        conta.addCaixaItem(caixaItem);
                    }
                    
                    caixaItemDAO.save(caixaItem);

                    
                    
                }
                
                /*
                if(parcelas.indexOf(parcela) == parcelas.size() -1 && valorRestante.compareTo(BigDecimal.ZERO) > 0) {
                    System.out.println("troco...");
                    //Troco-------------------------------------------------------------
                    CaixaItem caixaItem = new CaixaItem(CaixaItemTipo.TROCO, meioPagamento, "", BigDecimal.ZERO, valorRestante);

                    parcela.addRecebimento(caixaItem);

                    if(conta.getContaTipo().equals(ContaTipoEnum.CAIXA)) {
                        caixa.addCaixaItem(caixaItem);
                    } else {
                        conta.addCaixaItem(caixaItem);
                    }

                    caixaItemDAO.save(caixaItem);
                    //Fim Troco---------------------------------------------------------
                }*/
                

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
        TermicaPrint.gerarRecibo(parcelas, pdfFilePath);

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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblParcelas = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtRecebido = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        cboMeioPagamento = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        txtTotalAtual = new javax.swing.JFormattedTextField();
        cboConta = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtTroco = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtData = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        txtTotal = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        txtMulta = new javax.swing.JFormattedTextField();
        txtJuros = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        chkNaoCobrarMulta = new javax.swing.JCheckBox();
        chkNaoCobrarJuros = new javax.swing.JCheckBox();
        jLabel15 = new javax.swing.JLabel();
        btnAcrescimoTipo = new javax.swing.JButton();
        txtAcrescimo = new javax.swing.JFormattedTextField();
        Desconto = new javax.swing.JLabel();
        btnDescontoTipo = new javax.swing.JButton();
        txtDesconto = new javax.swing.JFormattedTextField();
        txtRecebidoParcial = new javax.swing.JFormattedTextField();
        jLabel14 = new javax.swing.JLabel();
        btnOk = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Recebimento de Parcelas");
        setResizable(false);

        tblParcelas.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
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

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Valor Recebido");

        txtRecebido.setForeground(java.awt.Color.blue);
        txtRecebido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecebido.setText("0,00");
        txtRecebido.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtRecebido.setName("decimal"); // NOI18N
        txtRecebido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtRecebidoKeyReleased(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Meio de Pagamento");

        cboMeioPagamento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setText("Valor Atualizado");

        txtTotalAtual.setEditable(false);
        txtTotalAtual.setForeground(java.awt.Color.red);
        txtTotalAtual.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalAtual.setText("0,00");
        txtTotalAtual.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        cboConta.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cboConta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboContaActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Conta");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setText("Troco");

        txtTroco.setEditable(false);
        txtTroco.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTroco.setText("0,00");
        txtTroco.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Data Conta");

        txtData.setEditable(false);
        txtData.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtData.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtData.setText("--/--/----");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 106, Short.MAX_VALUE)
                        .addComponent(txtRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTotalAtual, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTroco, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cboConta, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboMeioPagamento, 0, 229, Short.MAX_VALUE)
                            .addComponent(txtData))))
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
                    .addComponent(txtTotalAtual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTroco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtTotal.setEditable(false);
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotal.setText("0,00");
        txtTotal.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setText("Total");

        txtMulta.setEditable(false);
        txtMulta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMulta.setText("0,00");
        txtMulta.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        txtJuros.setEditable(false);
        txtJuros.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtJuros.setText("0,00");
        txtJuros.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setText("Multa");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel13.setText("Juros");

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

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel15.setText("Acréscimo");

        btnAcrescimoTipo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnAcrescimoTipo.setText("$");
        btnAcrescimoTipo.setFocusable(false);
        btnAcrescimoTipo.setPreferredSize(new java.awt.Dimension(55, 25));
        btnAcrescimoTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAcrescimoTipoActionPerformed(evt);
            }
        });

        txtAcrescimo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAcrescimo.setText("0,00");
        txtAcrescimo.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtAcrescimo.setName("decimal"); // NOI18N
        txtAcrescimo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAcrescimoActionPerformed(evt);
            }
        });
        txtAcrescimo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAcrescimoKeyReleased(evt);
            }
        });

        Desconto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Desconto.setText("Desconto");

        btnDescontoTipo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnDescontoTipo.setText("$");
        btnDescontoTipo.setFocusable(false);
        btnDescontoTipo.setPreferredSize(new java.awt.Dimension(55, 25));
        btnDescontoTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDescontoTipoActionPerformed(evt);
            }
        });

        txtDesconto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDesconto.setText("0,00");
        txtDesconto.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtDesconto.setName("decimal"); // NOI18N
        txtDesconto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDescontoActionPerformed(evt);
            }
        });
        txtDesconto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDescontoKeyReleased(evt);
            }
        });

        txtRecebidoParcial.setEditable(false);
        txtRecebidoParcial.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecebidoParcial.setText("0,00");
        txtRecebidoParcial.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel14.setText("Recebido Parcial");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 91, Short.MAX_VALUE)
                                        .addComponent(chkNaoCobrarMulta))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel13)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(chkNaoCobrarJuros))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(btnDescontoTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnAcrescimoTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtMulta, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtJuros, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                                    .addComponent(txtAcrescimo)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtRecebidoParcial, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(Desconto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtDesconto, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRecebidoParcial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMulta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkNaoCobrarMulta))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtJuros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkNaoCobrarJuros))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtAcrescimo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAcrescimoTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtDesconto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDescontoTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Desconto))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setForeground(java.awt.Color.red);
        jLabel1.setText("*Acréscimo e Desconto são editávies apenas quando não definido anteriormente");

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
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btnCancelar)
                                .addGap(18, 18, 18)
                                .addComponent(btnOk)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk)
                    .addComponent(btnCancelar)
                    .addComponent(jLabel1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblParcelasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblParcelasMouseClicked
        if(evt.getClickCount() == 2) {
            editar();
        }
    }//GEN-LAST:event_tblParcelasMouseClicked

    private void chkNaoCobrarMultaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkNaoCobrarMultaActionPerformed
        exibirTotais();
    }//GEN-LAST:event_chkNaoCobrarMultaActionPerformed

    private void chkNaoCobrarJurosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkNaoCobrarJurosActionPerformed
        exibirTotais();
    }//GEN-LAST:event_chkNaoCobrarJurosActionPerformed

    private void btnAcrescimoTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAcrescimoTipoActionPerformed
        alternarAcrescimoTipo();
    }//GEN-LAST:event_btnAcrescimoTipoActionPerformed

    private void txtAcrescimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAcrescimoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAcrescimoActionPerformed

    private void txtAcrescimoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAcrescimoKeyReleased
        editarAcrescimo();
    }//GEN-LAST:event_txtAcrescimoKeyReleased

    private void btnDescontoTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDescontoTipoActionPerformed
        alternarDescontoTipo();
    }//GEN-LAST:event_btnDescontoTipoActionPerformed

    private void txtDescontoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDescontoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDescontoActionPerformed

    private void txtDescontoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescontoKeyReleased
        editarDesconto();
    }//GEN-LAST:event_txtDescontoKeyReleased

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        confirmar();
    }//GEN-LAST:event_btnOkActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void txtRecebidoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRecebidoKeyReleased
        exibirTotais();
    }//GEN-LAST:event_txtRecebidoKeyReleased

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
            java.util.logging.Logger.getLogger(RecebimentoNovoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RecebimentoNovoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RecebimentoNovoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RecebimentoNovoView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
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
                RecebimentoNovoView dialog = new RecebimentoNovoView(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel Desconto;
    private javax.swing.JButton btnAcrescimoTipo;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnDescontoTipo;
    private javax.swing.JButton btnOk;
    private javax.swing.JComboBox<Object> cboConta;
    private javax.swing.JComboBox<Object> cboMeioPagamento;
    private javax.swing.JCheckBox chkNaoCobrarJuros;
    private javax.swing.JCheckBox chkNaoCobrarMulta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblParcelas;
    private javax.swing.JFormattedTextField txtAcrescimo;
    private javax.swing.JTextField txtData;
    private javax.swing.JFormattedTextField txtDesconto;
    private javax.swing.JFormattedTextField txtJuros;
    private javax.swing.JFormattedTextField txtMulta;
    private javax.swing.JFormattedTextField txtRecebido;
    private javax.swing.JFormattedTextField txtRecebidoParcial;
    private javax.swing.JFormattedTextField txtTotal;
    private javax.swing.JFormattedTextField txtTotalAtual;
    private javax.swing.JFormattedTextField txtTroco;
    // End of variables declaration//GEN-END:variables
}
