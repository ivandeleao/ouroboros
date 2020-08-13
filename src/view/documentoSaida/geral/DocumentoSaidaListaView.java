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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.documento.VendaItemConsolidado;
import model.mysql.bean.principal.documento.VendaStatus;
import model.mysql.bean.principal.financeiro.Conta;
import model.mysql.bean.principal.pessoa.PessoaTipo;
import model.mysql.dao.principal.financeiro.ContaDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;
import static ouroboros.Ouroboros.USUARIO;
import printing.documento.BoletoPrint;
import printing.documento.CarnePrint;
import printing.documento.DocumentoSaidaListaReport;
import printing.documento.VendasProdutosPorVendedorPrint;
import util.Decimal;
import util.JSwing;
import util.MwIOFile;
import util.Texto;
import util.jTableFormat.VendasRenderer;
import view.Toast;
import view.documentoSaida.DocumentoStatusView;
import view.documentoSaida.item.VendaView;
import view.pessoa.PessoaPesquisaView;
import view.veiculo.VeiculoPesquisaView;

/**
 *
 * @author ivand
 */
public class DocumentoSaidaListaView extends javax.swing.JInternalFrame {

    private static DocumentoSaidaListaView singleInstance = null;
    VendaListaJTableModel vendaListaJTableModel = new VendaListaJTableModel();
    VendaDAO vendaDAO = new VendaDAO();
    
    FuncionarioDAO funcionarioDAO = new FuncionarioDAO();

    List<Venda> listVenda = new ArrayList<>();

    LocalDateTime dataInicial, dataFinal;
    VendaStatus vendaStatus;
    Optional<Boolean> nfseEmitido, satEmitido, nfeEmitido;
    Pessoa pessoa;
    Funcionario funcionario;
    Veiculo veiculo;
    
    boolean exibirCancelados, exibirOriginais;
    
    public static DocumentoSaidaListaView getSingleInstance() {
        if (!USUARIO.autorizarAcesso(Recurso.DOCUMENTOS_DE_SAIDA)) {
            return null;
        }

        if (singleInstance == null) {
            singleInstance = new DocumentoSaidaListaView();
        }
        return singleInstance;
    }

    /**
     * Creates new form VendaListaView
     */
    private DocumentoSaidaListaView() {
        initComponents();

        JSwing.startComponentsBehavior(this);
        btnBoleto.setVisible(false);
        txtDataFinal.setText(DateTime.toStringDate(DateTime.getNow()));

        Calendar calendar = Calendar.getInstance(); //data e hora atual
        calendar.add(Calendar.DAY_OF_YEAR, -10);
        String inicial = DateTime.toStringDate(new Timestamp(calendar.getTimeInMillis()));
        txtDataInicial.setText(inicial);

        //btnExportarNotaServico.setVisible(false);
        configurarTela();

        formatarTabela();

        carregarStatus();

        carregarFuncionarios();

        carregarTabela();

    }

    private void configurarTela() {
        btnVeiculo.setVisible(Ouroboros.VEICULO_HABILITAR);
        txtVeiculo.setVisible(Ouroboros.VEICULO_HABILITAR);
        btnRemoverVeiculo.setVisible(Ouroboros.VEICULO_HABILITAR);
    }

    private void formatarTabela() {
        tblVendas.setModel(vendaListaJTableModel);

        tblVendas.setRowHeight(30);
        tblVendas.setIntercellSpacing(new Dimension(10, 10));

        tblVendas.getColumn("Id").setPreferredWidth(60);
        tblVendas.getColumn("Id").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblVendas.getColumn("Tipo").setPreferredWidth(160);

        tblVendas.getColumn("Status").setPreferredWidth(160);
        tblVendas.getColumn("Status").setCellRenderer(new VendasRenderer());

        tblVendas.getColumn("Data").setPreferredWidth(160);
        tblVendas.getColumn("Data").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblVendas.getColumn("Cliente").setPreferredWidth(480);

        tblVendas.getColumn("Funcionário").setPreferredWidth(120);
        //tblVendas.getColumn("Funcionário").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblVendas.getColumn("NFSe").setPreferredWidth(60);

        tblVendas.getColumn("Sat").setPreferredWidth(60);

        tblVendas.getColumn("NFe").setPreferredWidth(100);
        tblVendas.getColumn("NFe").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblVendas.getColumn("Total").setPreferredWidth(120);
        tblVendas.getColumn("Total").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblVendas.getColumn("Em aberto").setPreferredWidth(120);
        tblVendas.getColumn("Em aberto").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);
    }

    private void carregarTabela() {
        long start = System.currentTimeMillis();

        //String tipoData = cboPeriodo.getSelectedItem().toString();
        dataInicial = DateTime.fromStringLDT(txtDataInicial.getText());
        dataFinal = DateTime.fromStringLDT(txtDataFinal.getText() + " 23:59:59");
        funcionario = (Funcionario) cboFuncionario.getSelectedItem();

        vendaStatus = cboStatus.getSelectedIndex() == 0 ? null : (VendaStatus) cboStatus.getSelectedItem();

        nfseEmitido = cboNfse.getSelectedIndex() == 0 ? Optional.empty()
                : (cboNfse.getSelectedIndex() == 1 ? Optional.of(true) : Optional.of(false));

        satEmitido = cboSat.getSelectedIndex() == 0 ? Optional.empty()
                : (cboSat.getSelectedIndex() == 1 ? Optional.of(true) : Optional.of(false));

        nfeEmitido = cboNfe.getSelectedIndex() == 0 ? Optional.empty()
                : (cboNfe.getSelectedIndex() == 1 ? Optional.of(true) : Optional.of(false));

        
        
        exibirCancelados = chkCanceladas.isSelected();
        exibirOriginais = chkAgrupados.isSelected();
        
        //Optional<Boolean> hasDocumentosFilhos = Optional.of(!exibirOriginais);

        //switch (tipoData) { ...
            //case "Emissão":
                listVenda = vendaDAO.findByCriteria(TipoOperacao.SAIDA, dataInicial, dataFinal, funcionario, pessoa, veiculo, exibirCancelados, nfseEmitido, satEmitido, nfeEmitido, null, exibirOriginais, vendaStatus);
                //break;
        /*    case "Entrega":
                //listVenda = vendaDAO.findPorPeriodoEntrega(TipoOperacao.SAIDA, dataInicial, dataFinal, funcionario, pessoa, veiculo, exibirCanceladas);
                //break;
            case "Devolução":
                //listVenda = vendaDAO.findPorPeriodoDevolucao(TipoOperacao.SAIDA, dataInicial, dataFinal, funcionario, pessoa, veiculo, exibirCanceladas);
                //break;
        }*/

        vendaListaJTableModel.clear();
        vendaListaJTableModel.addList(listVenda);

        long elapsed = System.currentTimeMillis() - start;
        lblMensagem.setText("Consulta realizada em " + elapsed + "ms");

        lblRegistrosExibidos.setText(String.valueOf(listVenda.size()));
    }
    
    private void atualizarLinha() {
        Venda venda = vendaListaJTableModel.getRow(tblVendas.getSelectedRow());
        vendaListaJTableModel.setValueAt(vendaDAO.findById(venda.getId()), tblVendas.getSelectedRow());
    }

    private void carregarStatus() {
        cboStatus.addItem("Todos");
        cboStatus.addItem(VendaStatus.ORÇAMENTO);
        cboStatus.addItem(VendaStatus.AGUARDANDO);
        cboStatus.addItem(VendaStatus.ANDAMENTO);
        cboStatus.addItem(VendaStatus.PREPARAÇÃO_CONCLUÍDA);
        cboStatus.addItem(VendaStatus.LIBERADO);
        cboStatus.addItem(VendaStatus.ENTREGA_CONCLUÍDA);
    }

    private void carregarFuncionarios() {
        List<Funcionario> funcionarios = new FuncionarioDAO().findAll(false);
        Funcionario noFilter = new Funcionario();
        noFilter.setId(0);
        noFilter.setNome("Todos");
        cboFuncionario.addItem(noFilter);
        
        Funcionario nenhum = new Funcionario();
        nenhum.setId(-1);
        nenhum.setNome("Nenhum");
        cboFuncionario.addItem(nenhum);
        
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
            CarnePrint.gerarCarne(parcelas);
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

        if (Ouroboros.EMPRESA_IM.isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Inscrição Municipal não cadastrada no sistema", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else if (Ouroboros.NFSE_ALIQUOTA.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Valor da alíquota não cadastrada no sistema", "Atenção", JOptionPane.WARNING_MESSAGE);
        
        } else if (Ouroboros.NFSE_CODIGO_SERVICO.trim().isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Código do Serviço não cadastrado no sistema", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {

            BigDecimal totalServicos = BigDecimal.ZERO;
            BigDecimal totalValorBase = BigDecimal.ZERO;

            new Toast("Gerando arquivo. Aguarde...\r\n"
                    + "A pasta com o arquivo será aberta a seguir.");

            List<String> linhas = new ArrayList<>();

            String hoje = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            //Cabeçalho
            String cabecalho = "1" //1 indica linha do cabeçalho
                    + "NFE_LOTE    "
                    + Texto.padLeftAndCut(Texto.soNumeros(Ouroboros.EMPRESA_IM), 15) //15 Inscrição Municipal do Prestador com 15 caracteres. 
                    + "030" //3 Indica a versão do layout a ser utilizada. A versão utiliza é "030". 
                    + hoje;                             //8 YYYYMMDD

            cabecalho = Texto.removerAcentos(cabecalho);
            linhas.add(cabecalho);

            //Itens
            for (int rowIndex : tblVendas.getSelectedRows()) {
                Venda venda = vendaListaJTableModel.getRow(rowIndex);
                BigDecimal valorItensServicos = venda.getTotalServicos().add(venda.getValorAliquotaNfse());
                totalServicos = totalServicos.add(valorItensServicos);
                totalValorBase = totalValorBase.add(valorItensServicos);

                String situacao = "R"; //Situação da Nota Fiscal -> R = Retida (Tributada pelo tomador)
                if (!venda.getPessoa().getCodigoMunicipio().equals(Ouroboros.EMPRESA_ENDERECO_CODIGO_MUNICIPIO)
                        || venda.getPessoa().isMei() 
                        || venda.getPessoa().getCnpj().isEmpty()) {
                    situacao = "T"; //T - Tributado prestador (cliente fora do município, MEI ou Pessoa Física)
                }

                String item = "2" //1 indica linha de nota fiscal
                        + "            " //12 Identificador Sistema Legado - Não pode ser repetido
                        + "1" //Informe o tipo de codificação utilizada para descrever o serviço. 1 - Lei 116;
                        + Texto.padRightAndCut(Ouroboros.NFSE_CODIGO_SERVICO, 7) //TO DO 7 código do serviço
                        + situacao
                        //15 Valor dos serviços
                        + Texto.padLeftAndCut(Texto.soNumeros(Decimal.toString(valorItensServicos)), 15, '0')
                        //15 Valor da base de cálculo
                        + Texto.padLeftAndCut(Texto.soNumeros(Decimal.toString(valorItensServicos)), 15, '0')
                        //3 Alíquota Simples Nacional
                        + Texto.soNumeros(Decimal.toString(Ouroboros.NFSE_ALIQUOTA))
                        //15 Valor Retenção ISS
                        + Texto.padLeftAndCut(Texto.soNumeros(Decimal.toString(new BigDecimal(0.00))), 15, '0')
                        //15 Valor Retenção INSS
                        + Texto.padLeftAndCut(Texto.soNumeros(Decimal.toString(new BigDecimal(0.00))), 15, '0')
                        //15 Valor Retenção COFINS
                        + Texto.padLeftAndCut(Texto.soNumeros(Decimal.toString(new BigDecimal(0.00))), 15, '0')
                        //15 Valor Retenção PIS
                        + Texto.padLeftAndCut(Texto.soNumeros(Decimal.toString(new BigDecimal(0.00))), 15, '0')
                        //15 Valor Retenção IR
                        + Texto.padLeftAndCut(Texto.soNumeros(Decimal.toString(new BigDecimal(0.00))), 15, '0')
                        //15 Valor Retenção CSLL
                        + Texto.padLeftAndCut(Texto.soNumeros(Decimal.toString(new BigDecimal(0.00))), 15, '0')
                        //15 Valor aproximado tributos
                        + Texto.padLeftAndCut(Texto.soNumeros(Decimal.toString(new BigDecimal(0.00))), 15, '0');

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

                item += Texto.padRightAndCut(tomadorCpfCnpj, 15);
                item += Texto.padRightAndCut(tomadorIM, 15);
                item += Texto.padRightAndCut(tomadorIE, 15);
                item += Texto.padRightAndCut(tomadorNome, 100);
                item += Texto.padRightAndCut(tomadorEndereco, 50);
                item += Texto.padRightAndCut(tomadorEnderecoNumero, 10);
                item += Texto.padRightAndCut(tomadorEnderecoComplemento, 30);
                item += Texto.padRightAndCut(tomadorEnderecoBairro, 30);
                item += Texto.padRightAndCut(tomadorEnderecoCodigoCidade, 7);
                item += Texto.padRightAndCut(tomadorEnderecoUf, 2);
                item += Texto.padRightAndCut(tomadorEnderecoCep, 8);
                item += Texto.padRightAndCut(tomadorEmail, 100);
                ;
                //Fim Dados do tomador -------------------------------------

                item += "       ";  //Código da Cidade onde o serviço foi prestado. local (Apenas para o caso de notas Não Tributadas)

                //Dados dos serviços----------------------------------------
                String discriminacao = "";

                discriminacao += "D" + venda.getId() + " ";
                discriminacao += venda.hasDocumentosFilho() ? "(AGRUPADOS) " : "";
                discriminacao += venda.getVeiculo() != null ? "PLACA " + venda.getVeiculo().getPlaca() : "";
                discriminacao += " ";

                //Itens lançados diretamente neste documento (não agrupados)
                for (MovimentoFisico mf : venda.getMovimentosFisicosServicos().stream().filter(mf -> !mf.isAgrupado()).collect(Collectors.toList())) {
                    discriminacao += mf.getDescricao().replace(System.lineSeparator(), "").replace("\r\n", "").replace("\n", "");
                    discriminacao += " ";
                }

                discriminacao += venda.getObservacao()
                        .replace(System.lineSeparator(), " ").replace("\r\n", " ").replace("\n", " ") + "|";

                //Itens agrupados
                for (Venda docFilho : venda.getDocumentosFilho()) {
                    if (!docFilho.getMovimentosFisicosServicos().isEmpty()) {
                        discriminacao += "D" + docFilho.getId() + " ";
                        discriminacao += docFilho.getVeiculo() != null ? "PLACA " + docFilho.getVeiculo().getPlaca() : "";
                        discriminacao += " ";

                        for (MovimentoFisico mf : docFilho.getMovimentosFisicosServicos()) {
                            discriminacao += mf.getDescricao().replace(System.lineSeparator(), "").replace("\r\n", "").replace("\n", "");
                            discriminacao += " ";
                        }
                        discriminacao += " ";
                        discriminacao += docFilho.getObservacao()
                                .replace(System.lineSeparator(), " ").replace("\r\n", " ").replace("\n", " ") + "|";
                    }
                }

                item += discriminacao;

                //Fim Dados dos serviços------------------------------------
                item = Texto.removerAcentos(item);
                linhas.add(item);

                venda.setNfseDataHora(LocalDateTime.now());
                vendaDAO.save(venda);

                vendaListaJTableModel.fireTableRowsUpdated(rowIndex, rowIndex);

            }
            //Fim itens-------------------------------------------------------------

            //Rodapé----------------------------------------------------------------
            String rodape = "9" //1 indica linha do rodapé
                    + Texto.padLeftAndCut(String.valueOf(tblVendas.getSelectedRows().length), 10, '0') //10 Número de linhas detalhe contidas no arquivo
                    + Texto.padLeftAndCut(Texto.soNumeros(Decimal.toString(totalServicos)), 15, '0') //15 Valor total dos serviços contidos no arquivo
                    + Texto.padLeftAndCut(Texto.soNumeros(Decimal.toString(totalValorBase)), 15, '0') //15 Valor total do valor base contido no arquivo
                    ;

            rodape = Texto.removerAcentos(rodape);
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
    
    private void gerarBoleto() {
        List<Parcela> parcelas = new ArrayList<>();
        for (int rowIndex : tblVendas.getSelectedRows()) {
            Venda venda = vendaListaJTableModel.getRow(rowIndex);
            if (!venda.getParcelasAPrazo().isEmpty()) {
                parcelas.addAll(venda.getParcelasAPrazo());
            }
        }

        if (parcelas.isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem parcelas para gerar boleto. Selecione documentos parcelados para gerar.", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            Conta conta = new ContaDAO().findAllBoleto().get(0);
            BoletoPrint.gerarBoleto(conta, parcelas);
        }
    }
    
    private void imprimir() {
        
        String filtroDataInicial = DateTime.toStringDate(dataInicial);
        String filtroDataFinal = DateTime.toStringDate(dataFinal);
        String statusNome = cboStatus.getSelectedItem().toString();
        String filtroNfse = nfseEmitido.equals(Optional.empty()) ? "S/N" : (nfseEmitido.equals(Optional.of(true)) ? "SIM" : "NÃO");
        String filtroSat = satEmitido.equals(Optional.empty()) ? "S/N" : (satEmitido.equals(Optional.of(true)) ? "SIM" : "NÃO");
        String filtroNfe = nfeEmitido.equals(Optional.empty()) ? "S/N" : (nfeEmitido.equals(Optional.of(true)) ? "SIM" : "NÃO");
        
        String pessoaNome = pessoa != null ? pessoa.getId() + " - " + pessoa.getNome() : "TODOS";
        String veiculoNome = veiculo != null ? veiculo.getPlaca() + " - " + veiculo.getModelo(): "TODOS";
        String funcionarioNome = funcionario != null ? funcionario.getNome() : "TODOS";
        
        String filtroCancelados = exibirCancelados ? "SIM" : "NÃO";
        String filtroOriginais = exibirOriginais ? "SIM" : "NÃO";
        
        DocumentoSaidaListaReport.gerar(listVenda, statusNome, filtroNfse, filtroSat, filtroNfe, filtroDataInicial, filtroDataFinal, pessoaNome, veiculoNome, funcionarioNome, filtroCancelados, filtroOriginais, BigDecimal.ZERO);
    }
    
    private void relatorios() {
        new DocumentosSaidaRelatorios();
        
        //ProdutosPorVendedorPrint.gerarA4(listVenda, dataInicial.toLocalDate(), dataFinal.toLocalDate());
    }
    
    
    
    private void relatorioTeste() {
        
        Long start = System.currentTimeMillis();
        
        List<Funcionario> vendedores =  funcionarioDAO.findAll(true);
        Map<Funcionario, List<Venda>> mapFD = new LinkedHashMap<>();
        
        vendedores.sort(Comparator.comparing(Funcionario::getNome));
        for (Funcionario f : vendedores) {
            mapFD.put(f, listVenda.stream().filter(d -> d.getFuncionario() != null).filter(d -> d.getFuncionario().equals(f)).collect(Collectors.toList()));
        }
        
        
        
        System.out.println("-------------------------------------------------");
        
        for (Map.Entry<Funcionario, List<Venda>> fdocs : mapFD.entrySet()) {
            Funcionario f = fdocs.getKey();
            List<Venda> docs = fdocs.getValue();
            System.out.println("vendedor: " + f);
            
            List<Produto> produtos = new ArrayList<>();
            
            List<VendaItemConsolidado> itensConsolidados = new ArrayList<>();
            
            List<MovimentoFisico> mfs = new ArrayList<>();
            
            for (Venda doc : docs) {
                mfs.addAll(doc.getMovimentosFisicos());
            }
            
            //List<VendaItemConsolidado> itens = new ArrayList<>();
            
            for (MovimentoFisico mf : mfs.stream().filter(mf -> mf.getProduto() != null).collect(Collectors.toList())) {
                //if (mf.getProduto() != null) {
                    Produto p = mf.getProduto();

                    /*if (!produtos.contains(p)) {
                        produtos.add(p);
                    }*/

                    
                    List<VendaItemConsolidado> itensTemp = itensConsolidados.stream().filter(i -> i.getProduto().equals(mf.getProduto())).collect(Collectors.toList());
                    if (itensTemp.isEmpty()) {
                        
                        VendaItemConsolidado item = new VendaItemConsolidado(p, mf.getSaida(), mf.getSubtotal());
                        
                        itensConsolidados.add(item);
                    } else {
                        int index = itensConsolidados.indexOf(itensTemp.get(0));
                        itensConsolidados.get(index).setQuantidade(
                                itensConsolidados.get(index).getQuantidade().add(mf.getSaida())
                        );
                        itensConsolidados.get(index).setTotal(
                                itensConsolidados.get(index).getTotal().add(mf.getSubtotal())
                        );
                        
                    }

                //}
            }
            
            for (VendaItemConsolidado i : itensConsolidados) {
                System.out.println(
                        "\t produto: " + i.getProduto().getNome()
                        + "\n\t - " + i.getQuantidade()
                        + "\t - " + i.getTotal()
                );
            }
            
            
        
            //aqui - verificar quantidade e total
            
            
            
            /*for  (Venda doc : docs) {
                for (MovimentoFisico mf : doc.getMovimentosFisicos()) {
                    if (mf.getProduto() != null) {
                        Produto p = mf.getProduto();
                        
                        if (!produtos.contains(p)) {
                            produtos.add(p);
                        }
                        
                        VendaItemConsolidado item = new VendaItemConsolidado(p, BigDecimal.ONE, BigDecimal.ONE);
                        
                        itensConsolidados.stream().filter(predicate)
                        
                    }
                }
            }*/
            
            
            
            /*for  (Produto p : produtos) {
                VendaItemConsolidado item = new VendaItemConsolidado(p, BigDecimal.ONE, BigDecimal.ONE)
                
                
                System.out.println("\t produto: " + p.getNome());
            }*/
            
            
            
            /*for (Venda d : docs) {
                System.out.println("\t" + d.getId() + " - " + d.getTotal());
                
                itensConsolidados.se
                        
                
            }*/
            
            
            //List<VendaItemConsolidado> itensConsolidados = new ArrayList<>();
            
            
        }
        
        
        Long end = System.currentTimeMillis();
        
        System.out.println("time: " + String.valueOf(end - start));
        
        System.out.println("-------------------------------------------------");
        
        /*for (Venda documento : listVenda) {
            if (documento.getFuncionario().equals(ui))
        }*/
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
        chkAgrupados = new javax.swing.JCheckBox();
        cboStatus = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnCarne = new javax.swing.JButton();
        btnExportarNotaServico = new javax.swing.JButton();
        btnConfirmarEntrega = new javax.swing.JButton();
        btnTotais = new javax.swing.JButton();
        btnBoleto = new javax.swing.JButton();
        btnImprimir = new javax.swing.JButton();
        btnRelatorios = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblRegistrosExibidos = new javax.swing.JLabel();

        setTitle("Documentos de Saída");
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
        tblVendas.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblVendasFocusGained(evt);
            }
        });
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

        txtDataInicial.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDataInicial.setName("data"); // NOI18N

        txtDataFinal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDataFinal.setName("data"); // NOI18N

        chkCanceladas.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkCanceladas.setText("Exibir cancelados");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Funcionário");

        cboFuncionario.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        cboSat.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboSat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "S/N", "Sim", "Não" }));

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
        cboNfse.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "S/N", "Sim", "Não" }));

        cboNfe.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboNfe.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "S/N", "Sim", "Não" }));

        chkAgrupados.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkAgrupados.setText("Exibir originais (de agrupamento)");

        cboStatus.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Status");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("NFS-e");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("Sat");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("NF-e");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(chkCanceladas)
                        .addGap(18, 18, 18)
                        .addComponent(chkAgrupados)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(btnCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnRemoverCliente)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(btnVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnRemoverVeiculo)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addComponent(cboFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(cboStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(cboNfse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel8)
                                .addGap(18, 18, 18)
                                .addComponent(cboSat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18)
                                .addComponent(cboNfe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboSat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboNfse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboNfe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnRemoverCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCliente)
                    .addComponent(btnCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtVeiculo)
                    .addComponent(btnRemoverVeiculo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboFuncionario)
                        .addComponent(jLabel6)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chkCanceladas)
                        .addComponent(chkAgrupados))
                    .addComponent(btnFiltrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnCarne.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/vcard.png"))); // NOI18N
        btnCarne.setText("Gerar Carnê");
        btnCarne.setIconTextGap(10);
        btnCarne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCarneActionPerformed(evt);
            }
        });

        btnExportarNotaServico.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-document-20.png"))); // NOI18N
        btnExportarNotaServico.setText("NFS-e");
        btnExportarNotaServico.setIconTextGap(10);
        btnExportarNotaServico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarNotaServicoActionPerformed(evt);
            }
        });

        btnConfirmarEntrega.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-timer-20.png"))); // NOI18N
        btnConfirmarEntrega.setText("Status");
        btnConfirmarEntrega.setIconTextGap(10);
        btnConfirmarEntrega.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarEntregaActionPerformed(evt);
            }
        });

        btnTotais.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-bank-20.png"))); // NOI18N
        btnTotais.setText("Totais");
        btnTotais.setIconTextGap(10);
        btnTotais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTotaisActionPerformed(evt);
            }
        });

        btnBoleto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-boleto-bankario-20.png"))); // NOI18N
        btnBoleto.setText("Boleto");
        btnBoleto.setIconTextGap(10);
        btnBoleto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBoletoActionPerformed(evt);
            }
        });

        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-printer-20.png"))); // NOI18N
        btnImprimir.setText("Imprimir");
        btnImprimir.setIconTextGap(10);
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        btnRelatorios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-printer-20.png"))); // NOI18N
        btnRelatorios.setText("Relatórios");
        btnRelatorios.setIconTextGap(10);
        btnRelatorios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRelatoriosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCarne)
                .addGap(18, 18, 18)
                .addComponent(btnExportarNotaServico)
                .addGap(18, 18, 18)
                .addComponent(btnConfirmarEntrega, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnTotais, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnImprimir)
                .addGap(18, 18, 18)
                .addComponent(btnRelatorios)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnBoleto, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnBoleto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnTotais, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnImprimir)
                        .addComponent(btnRelatorios))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnExportarNotaServico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnConfirmarEntrega))
                            .addComponent(btnCarne, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
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
        } else {
            atualizarLinha(); //2020-04-30
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

    private void btnBoletoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBoletoActionPerformed
        gerarBoleto();
    }//GEN-LAST:event_btnBoletoActionPerformed

    private void tblVendasFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblVendasFocusGained
        atualizarLinha(); //2020-04-30
    }//GEN-LAST:event_tblVendasFocusGained

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        imprimir();
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void btnRelatoriosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRelatoriosActionPerformed
        relatorios();
    }//GEN-LAST:event_btnRelatoriosActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBoleto;
    private javax.swing.JButton btnCarne;
    private javax.swing.JButton btnCliente;
    private javax.swing.JButton btnConfirmarEntrega;
    private javax.swing.JButton btnExportarNotaServico;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnRelatorios;
    private javax.swing.JButton btnRemoverCliente;
    private javax.swing.JButton btnRemoverVeiculo;
    private javax.swing.JButton btnTotais;
    private javax.swing.JButton btnVeiculo;
    private javax.swing.JComboBox<Object> cboFuncionario;
    private javax.swing.JComboBox<String> cboNfe;
    private javax.swing.JComboBox<String> cboNfse;
    private javax.swing.JComboBox<String> cboSat;
    private javax.swing.JComboBox<Object> cboStatus;
    private javax.swing.JCheckBox chkAgrupados;
    private javax.swing.JCheckBox chkCanceladas;
    private javax.swing.JLabel jLabel1;
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
