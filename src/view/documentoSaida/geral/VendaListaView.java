/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoSaida.geral;

import java.awt.Dimension;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.Veiculo;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.pessoa.PessoaTipo;
import model.mysql.dao.endereco.CidadeDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;
import static ouroboros.Ouroboros.USUARIO;
import printing.Carne;
import util.Decimal;
import util.JSwing;
import util.MwIOFile;
import util.Texto;
import util.jTableFormat.VendasRenderer;
import view.Toast;
import view.documentoSaida.ConfirmarEntregaView;
import view.documentoSaida.DocumentoStatusView;
import view.documentoSaida.VendaView;
import view.pessoa.PessoaPesquisaView;
import view.veiculo.VeiculoPesquisaView;

/**
 *
 * @author ivand
 */
public class VendaListaView extends javax.swing.JInternalFrame {

    private static VendaListaView singleInstance = null;
    VendaListaJTableModel vendaListaJTableModel = new VendaListaJTableModel();
    VendaDAO vendaDAO = new VendaDAO();

    List<Venda> listVenda = new ArrayList<>();

    Pessoa pessoa;
    Veiculo veiculo;

    public static VendaListaView getSingleInstance() {
        if (!USUARIO.autorizarAcesso(Recurso.FATURAMENTO)) {
            return null;
        }

        if (singleInstance == null) {
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

        //btnExportarNotaServico.setVisible(false);
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
        tblVendas.setModel(vendaListaJTableModel);

        tblVendas.setRowHeight(30);
        tblVendas.setIntercellSpacing(new Dimension(10, 10));

        tblVendas.getColumn("Id").setPreferredWidth(60);
        tblVendas.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblVendas.getColumn("Tipo").setPreferredWidth(160);

        tblVendas.getColumn("Status").setPreferredWidth(240);
        tblVendas.getColumn("Status").setCellRenderer(new VendasRenderer());

        tblVendas.getColumn("Data").setPreferredWidth(160);
        tblVendas.getColumn("Data").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblVendas.getColumn("Cliente").setPreferredWidth(400);

        tblVendas.getColumn("Funcionário").setPreferredWidth(120);
        //tblVendas.getColumn("Funcionário").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblVendas.getColumn("NFSe").setPreferredWidth(60);

        tblVendas.getColumn("Sat").setPreferredWidth(60);

        tblVendas.getColumn("NFe").setPreferredWidth(60);

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
        boolean exibirCanceladas = chkCanceladas.isSelected();

        Optional<Boolean> nfseEmitido = cboNfse.getSelectedIndex() == 0 ? Optional.empty() : 
                (cboNfse.getSelectedIndex() == 1 ? Optional.of(true) : Optional.of(false));

        Optional<Boolean> satEmitido = cboSat.getSelectedIndex() == 0 ? Optional.empty() : 
                (cboSat.getSelectedIndex() == 1 ? Optional.of(true) : Optional.of(false));
        
        Optional<Boolean> nfeEmitido = cboNfe.getSelectedIndex() == 0 ? Optional.empty() : 
                (cboNfe.getSelectedIndex() == 1 ? Optional.of(true) : Optional.of(false));

        switch (tipoData) {
            case "Emissão":
                listVenda = vendaDAO.findByCriteria(TipoOperacao.SAIDA, dataInicial, dataFinal, funcionario, pessoa, veiculo, exibirCanceladas, nfseEmitido, satEmitido, nfeEmitido);
                break;
            case "Entrega":
                listVenda = vendaDAO.findPorPeriodoEntrega(TipoOperacao.SAIDA, dataInicial, dataFinal, funcionario, pessoa, veiculo, exibirCanceladas);
                break;
            case "Devolução":
                listVenda = vendaDAO.findPorPeriodoDevolucao(TipoOperacao.SAIDA, dataInicial, dataFinal, funcionario, pessoa, veiculo, exibirCanceladas);
                break;
        }

        

        vendaListaJTableModel.clear();
        vendaListaJTableModel.addList(listVenda);

        long elapsed = System.currentTimeMillis() - start;
        lblMensagem.setText("Consulta realizada em " + elapsed + "ms");

        lblRegistrosExibidos.setText(String.valueOf(listVenda.size()));
    }

    private void carregarFuncionarios() {
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
        for (int rowIndex : tblVendas.getSelectedRows()) {
            Venda venda = vendaListaJTableModel.getRow(rowIndex);
            if (!venda.getParcelasAPrazo().isEmpty()) {
                parcelas.addAll(venda.getParcelasAPrazo());
            }
        }

        if (parcelas.isEmpty()) {
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

    private void exportarNFSe() {
        BigDecimal totalServicos = BigDecimal.ZERO;
        BigDecimal totalValorBase = BigDecimal.ZERO;

        new Toast("Gerando arquivo. Aguarde...\r\n"
                + "A pasta com o arquivo será aberta a seguir.");

        List<String> linhas = new ArrayList<>();

        String hoje = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        //Cabeçalho
        String cabecalho = "1" //1 indica linha do cabeçalho
                + "NFE_LOTE    "
                + Texto.padLeft(Texto.soNumeros(Ouroboros.EMPRESA_IM), 15) //15 Inscrição Municipal do Prestador com 15 caracteres. 
                + "030" //3 Indica a versão do layout a ser utilizada. A versão utiliza é "030". 
                + hoje;                             //8 YYYYMMDD

        cabecalho = Texto.removeAccents(cabecalho);
        linhas.add(cabecalho);

        //Itens
        for (int rowIndex : tblVendas.getSelectedRows()) {
            Venda venda = vendaListaJTableModel.getRow(rowIndex);

            totalServicos = totalServicos.add(venda.getTotalItensServicos());
            totalValorBase = totalValorBase.add(venda.getTotalItensServicos());
            
            String situacao = "R"; //Situação da Nota Fiscal -> R = Retida (Tributada pelo tomador)
            if(!venda.getPessoa().getCodigoMunicipio().equals(Ouroboros.EMPRESA_ENDERECO_CODIGO_MUNICIPIO)) {
                situacao = "T"; //T - Tributado prestador (cliente fora do município)
            }

            String item = "2" //1 indica linha de nota fiscal
                    + "            " //12 Identificador Sistema Legado - Não pode ser repetido
                    + "1" //Informe o tipo de codificação utilizada para descrever o serviço. 1 - Lei 116;
                    + Texto.padRight("107", 7) //TO DO 7 código do serviço
                    + situacao

                    //15 Valor dos serviços
                    + Texto.padLeft(Texto.soNumeros(Decimal.toString(venda.getTotalItensServicos())), 15, '0')
                    //15 Valor da base de cálculo
                    + Texto.padLeft(Texto.soNumeros(Decimal.toString(venda.getTotalItensServicos())), 15, '0')
                    //3 Alíquota Simples Nacional
                    + Texto.soNumeros(Decimal.toString(new BigDecimal(2.01)))
                    //15 Valor Retenção ISS
                    + Texto.padLeft(Texto.soNumeros(Decimal.toString(new BigDecimal(0.00))), 15, '0')
                    //15 Valor Retenção INSS
                    + Texto.padLeft(Texto.soNumeros(Decimal.toString(new BigDecimal(0.00))), 15, '0')
                    //15 Valor Retenção COFINS
                    + Texto.padLeft(Texto.soNumeros(Decimal.toString(new BigDecimal(0.00))), 15, '0')
                    //15 Valor Retenção PIS
                    + Texto.padLeft(Texto.soNumeros(Decimal.toString(new BigDecimal(0.00))), 15, '0')
                    //15 Valor Retenção IR
                    + Texto.padLeft(Texto.soNumeros(Decimal.toString(new BigDecimal(0.00))), 15, '0')
                    //15 Valor Retenção CSLL
                    + Texto.padLeft(Texto.soNumeros(Decimal.toString(new BigDecimal(0.00))), 15, '0')
                    //15 Valor aproximado tributos
                    + Texto.padLeft(Texto.soNumeros(Decimal.toString(new BigDecimal(0.00))), 15, '0');

            //Dados do tomador -----------------------------------------
            String tomadorCpfCnpj = "";
            String tomadorIM = "";
            String tomadorIE = "";
            String tomadorNome = "";
            String tomadorEndereco = "";
            String tomadorEnderecoNumero = "";
            String tomadorEnderecoComplemento = "";
            String tomadorEnderecoBairro = "";
            String tomadorEnderecoCodigoCidade = "";
            String tomadorEnderecoUf = "";
            String tomadorEnderecoCep = "";
            String tomadorEmail = "";

            if (venda.getPessoa() != null) {
                Pessoa p = venda.getPessoa();
                tomadorCpfCnpj = p.getCpfOuCnpjSoNumeros();
                if (tomadorCpfCnpj.isEmpty()) {
                    tomadorCpfCnpj = "PFNI";
                }

                tomadorIM = Texto.soNumeros(p.getIm());
                tomadorIE = Texto.soNumeros(p.getIe());
                tomadorNome = p.getNome();
                tomadorEndereco = p.getEndereco();
                tomadorEnderecoNumero = p.getNumero();
                tomadorEnderecoComplemento = p.getComplemento();
                tomadorEnderecoBairro = p.getBairro();
                tomadorEnderecoCodigoCidade = p.getCodigoMunicipio();
                tomadorEnderecoUf = p.getUf();
                tomadorEnderecoCep = p.getCepSoNumeros();
                tomadorEmail = p.getEmail();
            }

            item += Texto.padRight(tomadorCpfCnpj, 15);
            item += Texto.padRight(tomadorIM, 15);
            item += Texto.padRight(tomadorIE, 15);
            item += Texto.padRight(tomadorNome, 100);
            item += Texto.padRight(tomadorEndereco, 50);
            item += Texto.padRight(tomadorEnderecoNumero, 10);
            item += Texto.padRight(tomadorEnderecoComplemento, 30);
            item += Texto.padRight(tomadorEnderecoBairro, 30);
            item += Texto.padRight(tomadorEnderecoCodigoCidade, 7);
            item += Texto.padRight(tomadorEnderecoUf, 2);
            item += Texto.padRight(tomadorEnderecoCep, 8);
            item += Texto.padRight(tomadorEmail, 100);
            ;
            //Fim Dados do tomador -------------------------------------

            item += "       ";  //Código da Cidade onde o serviço foi prestado. local (Apenas para o caso de notas Não Tributadas)

            //Dados dos serviços----------------------------------------
            String discriminacao = "";
            for (MovimentoFisico mf : venda.getMovimentosFisicosServicos()) {
                discriminacao += mf.getDescricao().replace(System.lineSeparator(), "|");
                discriminacao += "|";
            }

            discriminacao += venda.getObservacao().replace(System.lineSeparator(), "|") + "|";

            item += discriminacao;

            //Fim Dados dos serviços------------------------------------
            item = Texto.removeAccents(item);
            linhas.add(item);

            venda.setNfseDataHora(LocalDateTime.now());
            vendaDAO.save(venda);

            vendaListaJTableModel.fireTableRowsUpdated(rowIndex, rowIndex);

        }
        //Fim itens-------------------------------------------------------------

        //Rodapé----------------------------------------------------------------
        String rodape = "9" //1 indica linha do rodapé
                + Texto.padLeft(String.valueOf(tblVendas.getSelectedRows().length), 10, '0') //10 Número de linhas detalhe contidas no arquivo
                + Texto.padLeft(Texto.soNumeros(Decimal.toString(totalServicos)), 15, '0') //15 Valor total dos serviços contidos no arquivo
                + Texto.padLeft(Texto.soNumeros(Decimal.toString(totalValorBase)), 15, '0') //15 Valor total do valor base contido no arquivo
                ;

        rodape = Texto.removeAccents(rodape);
        linhas.add(rodape);

        //Fim Rodapé------------------------------------------------------------
        String caminho = "nfse//NFE_LOTE-" + hoje + ".txt";

        MwIOFile.writeFile(linhas, caminho, StandardCharsets.ISO_8859_1);

        try {
            System.out.println("app path: " + Ouroboros.APP_PATH);
            Runtime.getRuntime().exec("explorer.exe " + Ouroboros.APP_PATH + "nfse\\");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Erro ao salvar o arquivo " + e, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmarEntrega() {
        if (tblVendas.getSelectedRow() > -1) {
            //ConfirmarEntregaView confirmarEntregaView = new ConfirmarEntregaView(vendaListaJTableModel.getRow(tblVendas.getSelectedRow()));
            new DocumentoStatusView(vendaListaJTableModel.getRow(tblVendas.getSelectedRow()));
            vendaListaJTableModel.fireTableRowsUpdated(tblVendas.getSelectedRow(), tblVendas.getSelectedRow());
            
        }
    }
    
    private void totais() {
        new VendaListaTotaisView(listVenda);
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
        cboSat = new javax.swing.JComboBox<>();
        btnCliente = new javax.swing.JButton();
        txtCliente = new javax.swing.JTextField();
        btnRemoverCliente = new javax.swing.JButton();
        btnVeiculo = new javax.swing.JButton();
        txtVeiculo = new javax.swing.JTextField();
        btnRemoverVeiculo = new javax.swing.JButton();
        cboNfse = new javax.swing.JComboBox<>();
        cboNfe = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        btnCarne = new javax.swing.JButton();
        btnExportarNotaServico = new javax.swing.JButton();
        btnConfirmarEntrega = new javax.swing.JButton();
        btnTotais = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
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

        tblVendas.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
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

        lblMensagem.setText("Consulta realizada em Xms");

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

        cboSat.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboSat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sat ---", "Sat Sim", "Sat Não" }));

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
        txtVeiculo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtVeiculo.setText("NÃO INFORMADO");

        btnRemoverVeiculo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-close-button-20.png"))); // NOI18N
        btnRemoverVeiculo.setToolTipText("Remover Veículo");
        btnRemoverVeiculo.setContentAreaFilled(false);
        btnRemoverVeiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverVeiculoActionPerformed(evt);
            }
        });

        cboNfse.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboNfse.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NFSe ---", "NFSe Sim", "NFSe Não" }));

        cboNfe.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboNfe.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NFe ---", "NFe Sim", "NFe Não" }));

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
                        .addComponent(cboFuncionario, 0, 115, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(chkCanceladas)
                        .addGap(18, 18, 18))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCliente)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverCliente)
                        .addGap(18, 18, 18)
                        .addComponent(btnVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverVeiculo)
                        .addGap(18, 18, 18)
                        .addComponent(cboNfse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cboSat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cboNfe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(63, 63, 63)))
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
                                .addComponent(cboSat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cboNfse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cboNfe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

        btnExportarNotaServico.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-document-20.png"))); // NOI18N
        btnExportarNotaServico.setText("NFS-e");
        btnExportarNotaServico.setContentAreaFilled(false);
        btnExportarNotaServico.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExportarNotaServico.setIconTextGap(10);
        btnExportarNotaServico.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExportarNotaServico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarNotaServicoActionPerformed(evt);
            }
        });

        btnConfirmarEntrega.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-timer-20.png"))); // NOI18N
        btnConfirmarEntrega.setText("Status");
        btnConfirmarEntrega.setContentAreaFilled(false);
        btnConfirmarEntrega.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnConfirmarEntrega.setIconTextGap(10);
        btnConfirmarEntrega.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnConfirmarEntrega.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarEntregaActionPerformed(evt);
            }
        });

        btnTotais.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-bank-20.png"))); // NOI18N
        btnTotais.setText("Totais");
        btnTotais.setContentAreaFilled(false);
        btnTotais.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTotais.setIconTextGap(10);
        btnTotais.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnTotais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTotaisActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCarne, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnExportarNotaServico, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnConfirmarEntrega, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnTotais, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCarne, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExportarNotaServico, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConfirmarEntrega, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTotais, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel7.setForeground(java.awt.Color.blue);
        jLabel7.setText("Duplo clique para abrir o documento");

        jLabel4.setText("Registros exibidos:");

        lblRegistrosExibidos.setText("0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblMensagem)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRegistrosExibidos, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMensagem)
                    .addComponent(jLabel4)
                    .addComponent(lblRegistrosExibidos)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblVendasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVendasMouseClicked
        if (evt.getClickCount() == 2) {
            //int id = vendaListaJTableModel.getRow(tblVendas.getSelectedRow()).getId();
            Venda venda = vendaListaJTableModel.getRow(tblVendas.getSelectedRow());
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

    private void btnExportarNotaServicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarNotaServicoActionPerformed
        exportarNFSe();
    }//GEN-LAST:event_btnExportarNotaServicoActionPerformed

    private void btnConfirmarEntregaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarEntregaActionPerformed
        confirmarEntrega();
    }//GEN-LAST:event_btnConfirmarEntregaActionPerformed

    private void btnTotaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTotaisActionPerformed
        totais();
    }//GEN-LAST:event_btnTotaisActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCarne;
    private javax.swing.JButton btnCliente;
    private javax.swing.JButton btnConfirmarEntrega;
    private javax.swing.JButton btnExportarNotaServico;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnRemoverCliente;
    private javax.swing.JButton btnRemoverVeiculo;
    private javax.swing.JButton btnTotais;
    private javax.swing.JButton btnVeiculo;
    private javax.swing.JComboBox<Object> cboFuncionario;
    private javax.swing.JComboBox<String> cboNfe;
    private javax.swing.JComboBox<String> cboNfse;
    private javax.swing.JComboBox<String> cboPeriodo;
    private javax.swing.JComboBox<String> cboSat;
    private javax.swing.JCheckBox chkCanceladas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMensagem;
    private javax.swing.JLabel lblRegistrosExibidos;
    private javax.swing.JTable tblVendas;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JFormattedTextField txtDataFinal;
    private javax.swing.JFormattedTextField txtDataInicial;
    private javax.swing.JTextField txtVeiculo;
    // End of variables declaration//GEN-END:variables
}
