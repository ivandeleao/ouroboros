/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoSaida.geral;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.bean.principal.Funcionario;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.Recurso;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.principal.FuncionarioDAO;
import model.mysql.dao.principal.VendaDAO;
import model.jtable.documento.VendaListaJTableModel;
import model.mysql.bean.principal.Veiculo;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.pessoa.PessoaTipo;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;
import static ouroboros.Ouroboros.USUARIO;
import printing.Carne;
import util.Decimal;
import util.JSwing;
import util.jTableFormat.VendasRenderer;
import view.Toast;
import view.documentoSaida.VendaView;
import view.pessoa.PessoaPesquisaView;
import view.veiculo.VeiculoPesquisaView;

/**
 *
 * @author ivand
 */
public class VendaListaView extends javax.swing.JInternalFrame {
    private static VendaListaView singleInstance = null;
    VendaListaJTableModel vendaJTableModel = new VendaListaJTableModel();
    VendaDAO vendaDAO = new VendaDAO();

    List<Venda> listVenda = new ArrayList<>();
    
    Pessoa pessoa;
    Veiculo veiculo;
    
    public static VendaListaView getSingleInstance(){
        if(!USUARIO.autorizarAcesso(Recurso.FATURAMENTO)) {
            return null;
        }
        
        if(singleInstance == null){
            singleInstance = new VendaListaView();
        }
        return singleInstance;
    }
    
    /**
     * Creates new form VendaListaView
     */
    private VendaListaView() {
        initComponents();
        
        JSwing.startComponentsBehavior(this);
        
        txtDataFinal.setText(DateTime.toStringDate(DateTime.getNow()));
        
        Calendar calendar = Calendar.getInstance(); //data e hora atual
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        String inicial = DateTime.toStringDate(new Timestamp(calendar.getTimeInMillis()));
        txtDataInicial.setText(inicial);
        
        configurarTela();
        
        formatarTabela();
        
        carregarTabela();
        
        carregarFuncionarios();
    }
    
    private void configurarTela() {
        btnVeiculo.setVisible(Ouroboros.VENDA_EXIBIR_VEICULO);
        txtVeiculo.setVisible(Ouroboros.VENDA_EXIBIR_VEICULO);
        btnRemoverVeiculo.setVisible(Ouroboros.VENDA_EXIBIR_VEICULO);
    }
    
    private void formatarTabela() {
        tblVendas.setModel(vendaJTableModel);

        tblVendas.setRowHeight(24);
        tblVendas.setIntercellSpacing(new Dimension(10, 10));

        tblVendas.getColumn("Id").setPreferredWidth(60);
        tblVendas.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblVendas.getColumn("Tipo").setPreferredWidth(160);
        
        tblVendas.getColumn("Status").setPreferredWidth(240);
        tblVendas.getColumn("Status").setCellRenderer(new VendasRenderer());
        
        tblVendas.getColumn("Data").setPreferredWidth(160);
        tblVendas.getColumn("Data").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
        
        tblVendas.getColumn("Entrega").setPreferredWidth(160);
        tblVendas.getColumn("Entrega").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblVendas.getColumn("Cliente").setPreferredWidth(400);

        tblVendas.getColumn("Funcionário").setPreferredWidth(120);
        //tblVendas.getColumn("Funcionário").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblVendas.getColumn("Sat").setPreferredWidth(60);
        //tblVendas.getColumn("Sat").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblVendas.getColumn("Total").setPreferredWidth(120);
        tblVendas.getColumn("Total").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
        
        tblVendas.getColumn("Em aberto").setPreferredWidth(120);
        tblVendas.getColumn("Em aberto").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
    }
    
    private void carregarTabela() {
        long start = System.currentTimeMillis();
        
        String tipoData = cboPeriodo.getSelectedItem().toString();
        LocalDateTime dataInicial = DateTime.fromStringLDT(txtDataInicial.getText());
        LocalDateTime dataFinal = DateTime.fromStringLDT(txtDataFinal.getText() + " 23:59:59");
        Funcionario funcionario = (Funcionario) cboFuncionario.getSelectedItem();
        String cboSatEmitidoValor = cboSatEmitido.getSelectedItem().toString();
        boolean exibirCanceladas = chkCanceladas.isSelected();
        
        Optional<Boolean> satEmitido = Optional.empty();
        switch (cboSatEmitidoValor) {
            case "Sim":
                satEmitido = Optional.of(true);
                break;
            case "Não":
                satEmitido = Optional.of(false);
                break;
        }
        
        switch (tipoData) {
            case "Emissão":
                listVenda = vendaDAO.findByCriteria(TipoOperacao.SAIDA, dataInicial, dataFinal, funcionario, pessoa, veiculo, exibirCanceladas, satEmitido);
                break;
            case "Entrega":
                listVenda = vendaDAO.findPorPeriodoEntrega(TipoOperacao.SAIDA, dataInicial, dataFinal, funcionario, pessoa, veiculo, exibirCanceladas);
                break;
            case "Devolução":
                listVenda = vendaDAO.findPorPeriodoDevolucao(TipoOperacao.SAIDA, dataInicial, dataFinal, funcionario, pessoa, veiculo, exibirCanceladas);
                break;
        }
        
        
        BigDecimal totalGeral = BigDecimal.ZERO;
        BigDecimal totalEfetivo = BigDecimal.ZERO;
        BigDecimal totalOrcamento = BigDecimal.ZERO;
        BigDecimal totalCancelado = BigDecimal.ZERO;
        /*if(!listVenda.isEmpty()) {
            total = listVenda.stream().map(Venda::getTotal).reduce(BigDecimal::add).filter(Venda::isOrcamento).get();
        }*/
        
        for (Venda venda : listVenda) {
            totalGeral = totalGeral.add(venda.getTotal());
            if(venda.isOrcamento()) {
                totalOrcamento = totalOrcamento.add(venda.getTotal());
            } else if(venda.getCancelamento() != null) {
                totalCancelado = totalCancelado.add(venda.getTotal());
            } else {
                totalEfetivo = totalEfetivo.add(venda.getTotal());
            }
        }
        
        txtTotalCancelado.setText(Decimal.toString(totalCancelado));
        txtTotalOrcamento.setText(Decimal.toString(totalOrcamento));
        txtTotalEfetivo.setText(Decimal.toString(totalEfetivo));
        txtTotalGeral.setText(Decimal.toString(totalGeral));
        
        vendaJTableModel.clear();
        vendaJTableModel.addList(listVenda);

        long elapsed = System.currentTimeMillis() - start;
        lblMensagem.setText("Consulta realizada em " + elapsed + "ms");

        lblRegistrosExibidos.setText(String.valueOf(listVenda.size()));
    }
    
    private void carregarFuncionarios(){
        List<Funcionario> funcionarios = new FuncionarioDAO().findAll(false);
        Funcionario noFilter = new Funcionario();
        noFilter.setId(0);
        noFilter.setNome("Todos");
        cboFuncionario.addItem(noFilter);
        for (Funcionario f : funcionarios) {
            cboFuncionario.addItem(f);
        }
    }
    
    private void gerarCarne() {
        List<Parcela> parcelas = new ArrayList<>();
        for(int rowIndex : tblVendas.getSelectedRows()) {
            Venda venda = vendaJTableModel.getRow(rowIndex);
            if(!venda.getParcelasAPrazo().isEmpty()) {
                parcelas.addAll(venda.getParcelasAPrazo());
            }
        }
        
        if(parcelas.isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem parcelas para gerar carnê. Selecione vendas parceladas para gerar.", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            new Toast("Gerando carnê...");
            Carne.gerarCarne(parcelas);
        }
    }
    
    private void pesquisarCliente() {
        PessoaPesquisaView pesquisa = new PessoaPesquisaView(PessoaTipo.CLIENTE);

        if (pesquisa.getPessoa() != null) {
            pessoa = pesquisa.getPessoa();
            exibirCliente();
        }
    }

    private void exibirCliente() {
        if (pessoa != null) {
            txtCliente.setText(pessoa.getId() + " - " + pessoa.getNome());
            txtCliente.setCaretPosition(0);
        } else {
            txtCliente.setText("TODOS");
        }
    }

    private void removerCliente() {
        pessoa = null;
        txtCliente.setText("TODOS");
    }
    
    private void pesquisarVeiculo() {
        VeiculoPesquisaView pesquisa = new VeiculoPesquisaView();

        if (pesquisa.getVeiculo() != null) {
            veiculo = pesquisa.getVeiculo();
            exibirVeiculo();
        }
    }

    private void exibirVeiculo() {
        if (veiculo != null) {
            txtVeiculo.setText(veiculo.getPlaca() + " - " + veiculo.getModelo());
            txtVeiculo.setCaretPosition(0);
        } else {
            txtVeiculo.setText("TODOS");
        }
    }

    private void removerVeiculo() {
        veiculo = null;
        txtVeiculo.setText("TODOS");
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
        tblVendas = new javax.swing.JTable();
        lblMensagem = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnFiltrar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cboPeriodo = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        txtDataInicial = new javax.swing.JFormattedTextField();
        txtDataFinal = new javax.swing.JFormattedTextField();
        chkCanceladas = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        cboFuncionario = new javax.swing.JComboBox<>();
        cboSatEmitido = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        btnCliente = new javax.swing.JButton();
        txtCliente = new javax.swing.JTextField();
        btnRemoverCliente = new javax.swing.JButton();
        btnVeiculo = new javax.swing.JButton();
        txtVeiculo = new javax.swing.JTextField();
        btnRemoverVeiculo = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btnCarne = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txtTotalEfetivo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtTotalOrcamento = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtTotalCancelado = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtTotalGeral = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblRegistrosExibidos = new javax.swing.JLabel();

        setTitle("Faturamento");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
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

        tblVendas.setModel(new javax.swing.table.DefaultTableModel(
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
        tblVendas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVendasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblVendas);

        lblMensagem.setText("...");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnFiltrar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Data Inicial");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Data Final");

        cboPeriodo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboPeriodo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Emissão", "Entrega", "Devolução" }));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Período");

        txtDataInicial.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDataInicial.setName("data"); // NOI18N

        txtDataFinal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDataFinal.setName("data"); // NOI18N

        chkCanceladas.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkCanceladas.setText("Exibir documentos cancelados");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Funcionário");

        cboFuncionario.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        cboSatEmitido.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboSatEmitido.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Não definido", "Sim", "Não" }));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("Sat emitido");

        btnCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/user.png"))); // NOI18N
        btnCliente.setText("CLIENTE");
        btnCliente.setContentAreaFilled(false);
        btnCliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCliente.setIconTextGap(10);
        btnCliente.setPreferredSize(new java.awt.Dimension(180, 49));
        btnCliente.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClienteActionPerformed(evt);
            }
        });

        txtCliente.setEditable(false);
        txtCliente.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCliente.setText("TODOS");

        btnRemoverCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-close-button-20.png"))); // NOI18N
        btnRemoverCliente.setToolTipText("Remover Cliente");
        btnRemoverCliente.setContentAreaFilled(false);
        btnRemoverCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverClienteActionPerformed(evt);
            }
        });

        btnVeiculo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-car-20.png"))); // NOI18N
        btnVeiculo.setText("VEÍCULO");
        btnVeiculo.setContentAreaFilled(false);
        btnVeiculo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnVeiculo.setIconTextGap(10);
        btnVeiculo.setPreferredSize(new java.awt.Dimension(180, 49));
        btnVeiculo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnVeiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVeiculoActionPerformed(evt);
            }
        });

        txtVeiculo.setEditable(false);
        txtVeiculo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtVeiculo.setText("NÃO INFORMADO");

        btnRemoverVeiculo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-close-button-20.png"))); // NOI18N
        btnRemoverVeiculo.setToolTipText("Remover Veículo");
        btnRemoverVeiculo.setContentAreaFilled(false);
        btnRemoverVeiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverVeiculoActionPerformed(evt);
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
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cboPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(cboFuncionario, 0, 278, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(chkCanceladas)
                        .addGap(18, 18, 18))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverCliente)
                        .addGap(18, 18, 18)
                        .addComponent(btnVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtVeiculo, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverVeiculo)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboSatEmitido, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(49, 49, 49)))
                .addComponent(btnFiltrar)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFiltrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkCanceladas)
                            .addComponent(jLabel6)
                            .addComponent(cboFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnRemoverCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtCliente)
                                .addComponent(btnCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtVeiculo)
                                .addComponent(btnRemoverVeiculo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cboSatEmitido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel9)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnCarne.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/vcard.png"))); // NOI18N
        btnCarne.setText("Gerar Carnê");
        btnCarne.setContentAreaFilled(false);
        btnCarne.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCarne.setIconTextGap(10);
        btnCarne.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCarne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCarneActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCarne, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCarne, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel7.setForeground(java.awt.Color.blue);
        jLabel7.setText("Duplo clique para abrir o documento");

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtTotalEfetivo.setEditable(false);
        txtTotalEfetivo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalEfetivo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Efetivo");

        txtTotalOrcamento.setEditable(false);
        txtTotalOrcamento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalOrcamento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("Orçamento");

        txtTotalCancelado.setEditable(false);
        txtTotalCancelado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalCancelado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setText("Cancelado");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setText("Geral");

        txtTotalGeral.setEditable(false);
        txtTotalGeral.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalGeral.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTotalCancelado, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                    .addComponent(txtTotalOrcamento))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(18, 18, 18)
                        .addComponent(txtTotalGeral, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(txtTotalEfetivo, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtTotalEfetivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(txtTotalCancelado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtTotalGeral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(txtTotalOrcamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jLabel10.setText("Totais");

        jLabel4.setText("Registros exibidos:");

        lblRegistrosExibidos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRegistrosExibidos.setText("0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(lblMensagem))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(lblRegistrosExibidos)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(lblMensagem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblRegistrosExibidos)
                        .addComponent(jLabel4))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(jLabel10)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblVendasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVendasMouseClicked
        if (evt.getClickCount() == 2) {
            //int id = vendaJTableModel.getRow(tblVendas.getSelectedRow()).getId();
            Venda venda = vendaJTableModel.getRow(tblVendas.getSelectedRow());
            MAIN_VIEW.addView(VendaView.getInstance(venda));
        }
    }//GEN-LAST:event_tblVendasMouseClicked

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        singleInstance = null;
    }//GEN-LAST:event_formInternalFrameClosed

    private void btnCarneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCarneActionPerformed
        gerarCarne();
    }//GEN-LAST:event_btnCarneActionPerformed

    private void btnClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClienteActionPerformed
        pesquisarCliente();
    }//GEN-LAST:event_btnClienteActionPerformed

    private void btnRemoverClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverClienteActionPerformed
        removerCliente();
    }//GEN-LAST:event_btnRemoverClienteActionPerformed

    private void btnVeiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVeiculoActionPerformed
        pesquisarVeiculo();
    }//GEN-LAST:event_btnVeiculoActionPerformed

    private void btnRemoverVeiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverVeiculoActionPerformed
        removerVeiculo();
    }//GEN-LAST:event_btnRemoverVeiculoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCarne;
    private javax.swing.JButton btnCliente;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnRemoverCliente;
    private javax.swing.JButton btnRemoverVeiculo;
    private javax.swing.JButton btnVeiculo;
    private javax.swing.JComboBox<Object> cboFuncionario;
    private javax.swing.JComboBox<String> cboPeriodo;
    private javax.swing.JComboBox<String> cboSatEmitido;
    private javax.swing.JCheckBox chkCanceladas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMensagem;
    private javax.swing.JLabel lblRegistrosExibidos;
    private javax.swing.JTable tblVendas;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JFormattedTextField txtDataFinal;
    private javax.swing.JFormattedTextField txtDataInicial;
    private javax.swing.JTextField txtTotalCancelado;
    private javax.swing.JTextField txtTotalEfetivo;
    private javax.swing.JTextField txtTotalGeral;
    private javax.swing.JTextField txtTotalOrcamento;
    private javax.swing.JTextField txtVeiculo;
    // End of variables declaration//GEN-END:variables
}
