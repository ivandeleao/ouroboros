/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.pessoa;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.bean.endereco.Cidade;
import model.bean.endereco.Endereco;
import model.bean.principal.Pessoa;
import model.dao.endereco.CidadeDAO;
import model.dao.endereco.EnderecoDAO;
import model.dao.principal.PessoaDAO;
import util.JSwing;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;
import util.MwString;
import view.endereco.EnderecoPesquisaView;

/**
 *
 * @author ivand
 */
public class PessoaCadastroView extends javax.swing.JInternalFrame {

    private static List<PessoaCadastroView> clienteCadastroViews = new ArrayList<>(); //instâncias

    private PessoaDAO clienteDAO = new PessoaDAO();
    private Pessoa cliente;

    public static PessoaCadastroView getInstance(Pessoa cliente) {
        for (PessoaCadastroView clienteCadastroView : clienteCadastroViews) {
            if (clienteCadastroView.cliente == cliente) {
                return clienteCadastroView;
            }
        }
        clienteCadastroViews.add(new PessoaCadastroView(cliente));
        return clienteCadastroViews.get(clienteCadastroViews.size() - 1);
    }

    private PessoaCadastroView() {
        initComponents();
        JSwing.startComponentsBehavior(this);
    }

    private PessoaCadastroView(Pessoa cliente) {
        initComponents();
        JSwing.startComponentsBehavior(this);

        /*
        txtTelefone1.setDocument(new TelefoneDocument());
        txtCpf.setDocument(new CpfDocument());
        txtCnpj.setDocument(new CnpjDocument());
        //txtCep.setDocument(new CepDocument());
        txtNascimento.setDocument(new DataDocument());*/
        txtNome.requestFocus();
        

        this.cliente = cliente;

        carregarDados();

    }

    private void carregarDados() {
        if (cliente.getId() != null) {
            txtDataCadastro.setText(DateTime.toStringDate(cliente.getCriacao()));
            
            if(!cliente.getCnpj().isEmpty()) {
               tabTipo.setSelectedIndex(1);
               txtRazaoSocial.setText(cliente.getNome());
            } else {
                txtNome.setText(cliente.getNome());
            }
            
            txtId.setText(cliente.getId().toString());

            chkCliente.setSelected(cliente.isCliente());
            chkFornecedor.setSelected(cliente.isFornecedor());
            
            txtCpf.setText(MwString.soNumeros(cliente.getCpf()));
            txtRg.setText(cliente.getRg());
            String nascimento = DateTime.toStringDate(cliente.getNascimento());
            txtNascimento.setText(nascimento);

            txtNomeFantasia.setText(cliente.getNomeFantasia());
            txtCnpj.setText(cliente.getCnpj());
            txtIe.setText(cliente.getIe());
            chkIeIsento.setSelected(cliente.isIeIsento());

            txtTelefone1.setText(cliente.getTelefone1());
            txtTelefone2.setText(cliente.getTelefone2());
            txtTelefoneRecado.setText(cliente.getTelefoneRecado());
            txtContato.setText(cliente.getContato());
            
            txtEmail.setText(cliente.getEmail());

            txtCep.setText(cliente.getCep());
            txtEndereco.setText(cliente.getEndereco());
            txtNumero.setText(cliente.getNumero());
            txtComplemento.setText(cliente.getComplemento());
            txtBairro.setText(cliente.getBairro());
            txtCodigoMunicipio.setText(cliente.getCodigoMunicipio());
            buscarMunicipio();
            
            txtResponsavelNome.setText(cliente.getResponsavelNome());
            txtResponsavelCpf.setText(cliente.getResponsavelCpf());
            txtResponsavelRg.setText(cliente.getResponsavelRg());
            txtResponsavelNascimento.setText(DateTime.toStringDate(cliente.getResponsavelNascimento()));
            txtResponsavelEmail.setText(cliente.getResponsavelEmail());
            txtResponsavelParentesco.setText(cliente.getResponsavelParentesco());

            txtObservacao.setText(cliente.getObservacao());

        }
    }

    private boolean validar() {
        boolean valido = true;

        txtCpf.setText(txtCpf.getText().trim());
        txtCnpj.setText(txtCnpj.getText().trim());
        
        Boolean isCliente = chkCliente.isSelected();
        Boolean isFornecedor = chkFornecedor.isSelected();
        String nome = txtNome.getText();
        String nomeFantasia = txtNomeFantasia.getText();
        String cpf = txtCpf.getText();
        String cnpj = txtCnpj.getText();
        
        if(!isCliente && !isFornecedor) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Indique o(s) perfil(is) desta pessoa","Atenção", JOptionPane.WARNING_MESSAGE);
            valido = false;
        }
        if (nome.length() < 3 && nomeFantasia.length() < 3) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Nome deve ter no mínimo 3 caracteres","Atenção", JOptionPane.WARNING_MESSAGE);
            txtNome.requestFocus();
            valido = false;
        }
        
        //CPF ou CNPJ já existente
        if(cpf.length() > 0) {
            Pessoa p = clienteDAO.findByCpfCnpj(cpf);
            if(p != null && !p.getId().equals(cliente.getId())) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Já existe cadastro com este CPF.\n" + p.getNome(), "Atenção", JOptionPane.WARNING_MESSAGE);
                txtCpf.requestFocus();
                valido = false;
            }
        }
        
        if(cnpj.length() > 0) {
            Pessoa p = clienteDAO.findByCpfCnpj(cnpj);
            if(p != null && !p.getId().equals(cliente.getId())) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Já existe cadastro com este CNPJ.\n" + p.getNome(), "Atenção", JOptionPane.WARNING_MESSAGE);
                txtCnpj.requestFocus();
                valido = false;
            }
        }
        
        return valido;
    }

    private void salvar() {
        boolean isCliente = chkCliente.isSelected();
        boolean isFornecedor = chkFornecedor.isSelected();
        String nome;
        String cpf = txtCpf.getText();
        String rg = txtRg.getText();
        Date nascimento = DateTime.toSqlDate(txtNascimento.getText());

        String nomeFantasia = txtNomeFantasia.getText();
        String cnpj = txtCnpj.getText();
        String ie = txtIe.getText();
        boolean ieIsento = chkIeIsento.isSelected();

        String observacao = txtObservacao.getText();

        if (cliente != null) {
            cliente.setId(cliente.getId());
        }

        if(!txtRazaoSocial.getText().isEmpty()) {
            nome = txtRazaoSocial.getText();
        } else {
            nome = txtNome.getText();
        }
        
        cliente.setCliente(isCliente);
        cliente.setFornecedor(isFornecedor);
        cliente.setNome(nome);
        cliente.setCpf(cpf);
        cliente.setRg(rg);
        cliente.setNascimento(nascimento);

        cliente.setNomeFantasia(nomeFantasia);
        cliente.setCnpj(cnpj);
        cliente.setIe(ie);
        cliente.setIeIsento(ieIsento);

        cliente.setTelefone1(txtTelefone1.getText());
        cliente.setTelefone2(txtTelefone2.getText());
        cliente.setTelefoneRecado(txtTelefoneRecado.getText());
        cliente.setContato(txtContato.getText());
        
        cliente.setEmail(txtEmail.getText());

        cliente.setCep(txtCep.getText());
        cliente.setEndereco(txtEndereco.getText());
        cliente.setNumero(txtNumero.getText());
        cliente.setComplemento(txtComplemento.getText());
        cliente.setBairro(txtBairro.getText());
        cliente.setCodigoMunicipio(txtCodigoMunicipio.getText());

        cliente.setObservacao(observacao);
        
        cliente.setResponsavelNome(txtResponsavelNome.getText());
        cliente.setResponsavelCpf(txtResponsavelCpf.getText());
        cliente.setResponsavelRg(txtResponsavelRg.getText());
        cliente.setResponsavelNascimento(DateTime.fromStringDateLDT(txtResponsavelNascimento.getText()));
        cliente.setResponsavelEmail(txtResponsavelEmail.getText());
        cliente.setResponsavelParentesco(txtResponsavelParentesco.getText());

        cliente = clienteDAO.save(cliente);

        txtDataCadastro.setText(DateTime.toStringDate(cliente.getCriacao()));
        txtId.setText(cliente.getId().toString());

        JOptionPane.showMessageDialog(rootPane, "Dados salvos com sucesso");

        PessoaContainerView.getInstance(cliente).gerarTabs();

    }

    private void buscarEndereco() {
        String cep = MwString.soNumeros(txtCep.getText());
        EnderecoDAO enderecoDAO = new EnderecoDAO();
        Endereco endereco = enderecoDAO.findByCep(cep);
        if (endereco != null) {
            txtEndereco.setText(endereco.getEnderecoCompleto());
            txtBairro.setText(endereco.getBairro().getNome());
            txtCodigoMunicipio.setText(endereco.getCidade().getCodigoIbgeCompleto());
            txtMunicipio.setText(endereco.getCidade().getNome());
            txtUF.setText(endereco.getCidade().getEstado().getSigla());
            txtNumero.requestFocus();
        } else {
            JOptionPane.showMessageDialog(rootPane, "CEP não encontrado", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            txtEndereco.setText("");
        }
    }

    private void buscarMunicipio() {
        String codigoIbge = txtCodigoMunicipio.getText().trim();
        if (!codigoIbge.isEmpty()) {
            CidadeDAO cidadeDAO = new CidadeDAO();
            Cidade cidade = cidadeDAO.findByCodigoIbge(codigoIbge);
            if (cidade != null) {
                txtMunicipio.setText(cidade.getNome());
                txtUF.setText(cidade.getEstado().getSigla());
            } else {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Município não encontrado", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                txtMunicipio.setText("");
            }
        }
    }
    
    private void pesquisarCep() {
        EnderecoPesquisaView enderecoPesquisaView = new EnderecoPesquisaView();
        Endereco endereco = enderecoPesquisaView.getEndereco();
        
        if(endereco != null) {
            txtCep.setText(endereco.getCep());
            txtEndereco.setText(endereco.getEnderecoCompleto());
            txtBairro.setText(endereco.getBairro().getNome());
            txtCodigoMunicipio.setText(endereco.getCidade().getCodigoIbgeCompleto());
            txtMunicipio.setText(endereco.getCidade().getNome());
            txtUF.setText(endereco.getCidade().getEstado().getSigla());
            txtNumero.requestFocus();
        } else {
            txtCep.requestFocus();
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
        jLabel1 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        txtTelefone1 = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtTelefone2 = new javax.swing.JFormattedTextField();
        txtCep = new javax.swing.JFormattedTextField();
        txtEndereco = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtNumero = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtComplemento = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        tabTipo = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        txtNome = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtCpf = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        txtRg = new javax.swing.JTextField();
        txtNascimento = new javax.swing.JFormattedTextField();
        jLabel17 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtRazaoSocial = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtCnpj = new javax.swing.JFormattedTextField();
        jLabel15 = new javax.swing.JLabel();
        txtIe = new javax.swing.JTextField();
        txtNomeFantasia = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        chkIeIsento = new javax.swing.JCheckBox();
        txtBairro = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtCodigoMunicipio = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtMunicipio = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtUF = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        chkCliente = new javax.swing.JCheckBox();
        chkFornecedor = new javax.swing.JCheckBox();
        jLabel21 = new javax.swing.JLabel();
        txtTelefoneRecado = new javax.swing.JFormattedTextField();
        jLabel22 = new javax.swing.JLabel();
        txtContato = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        btnCep = new javax.swing.JButton();
        txtDataCadastro = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        btnSalvar = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        txtResponsavelNome = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtResponsavelCpf = new javax.swing.JFormattedTextField();
        jLabel26 = new javax.swing.JLabel();
        txtResponsavelRg = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        txtResponsavelNascimento = new javax.swing.JFormattedTextField();
        jLabel28 = new javax.swing.JLabel();
        txtResponsavelEmail = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        txtResponsavelParentesco = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtObservacao = new javax.swing.JTextArea();
        jLabel30 = new javax.swing.JLabel();

        setClosable(true);
        setTitle("Cadastro de Pessoa");
        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(1300, 600));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
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
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Id");

        txtId.setEditable(false);
        txtId.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtId.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdActionPerformed(evt);
            }
        });

        txtTelefone1.setName("telefone"); // NOI18N

        jLabel3.setText("Telefone");

        jLabel4.setText("Telefone 2");

        txtTelefone2.setName("telefone"); // NOI18N

        txtCep.setName("cep"); // NOI18N
        txtCep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCepActionPerformed(evt);
            }
        });

        jLabel8.setText("Endereço");

        jLabel10.setText("Número");

        jLabel11.setText("Complemento");

        jLabel2.setText("Nome");

        jLabel5.setText("CPF");

        txtCpf.setName("cpf"); // NOI18N

        jLabel12.setText("RG");

        txtNascimento.setName("data"); // NOI18N

        jLabel17.setText("Nascimento");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 553, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(txtCpf, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addGap(18, 18, 18)
                        .addComponent(txtRg, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(18, 18, 18)
                        .addComponent(txtNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(316, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5)
                    .addComponent(txtCpf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(txtRg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        tabTipo.addTab("Física", jPanel2);

        jLabel13.setText("Razão Social");

        jLabel14.setText("CNPJ");

        txtCnpj.setName("cnpj"); // NOI18N

        jLabel15.setText("IE");

        jLabel6.setText("Nome Fantasia");

        chkIeIsento.setText("IE Isento");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtNomeFantasia, javax.swing.GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
                    .addComponent(txtRazaoSocial))
                .addGap(18, 18, 18)
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addComponent(txtCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel15)
                .addGap(18, 18, 18)
                .addComponent(txtIe, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(chkIeIsento)
                .addContainerGap(137, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRazaoSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(txtCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(txtIe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkIeIsento))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNomeFantasia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        tabTipo.addTab("Jurídica", jPanel3);

        jLabel16.setText("Bairro");

        txtCodigoMunicipio.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCodigoMunicipio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoMunicipioActionPerformed(evt);
            }
        });

        jLabel18.setText("Cód. Município");

        txtMunicipio.setEditable(false);

        jLabel19.setText("Município");

        txtUF.setEditable(false);

        jLabel20.setText("UF");

        chkCliente.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkCliente.setText("Cliente");

        chkFornecedor.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkFornecedor.setText("Fornecedor");

        jLabel21.setText("Recado");

        txtTelefoneRecado.setName("telefone"); // NOI18N

        jLabel22.setText("Contato");

        jLabel23.setText("E-mail");

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel25.setText("Perfil:");

        btnCep.setText("Cep");
        btnCep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCepActionPerformed(evt);
            }
        });

        txtDataCadastro.setEditable(false);
        txtDataCadastro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDataCadastro.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("Data do cadastro");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabTipo, javax.swing.GroupLayout.DEFAULT_SIZE, 1240, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel25)
                        .addGap(18, 18, 18)
                        .addComponent(chkCliente)
                        .addGap(18, 18, 18)
                        .addComponent(chkFornecedor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(txtDataCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel16)
                            .addComponent(btnCep))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 408, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel18)
                                .addGap(18, 18, 18)
                                .addComponent(txtCodigoMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel19)
                                .addGap(18, 18, 18)
                                .addComponent(txtMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel20)
                                .addGap(18, 18, 18)
                                .addComponent(txtUF, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtCep, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtTelefone1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(txtTelefone2, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel21)
                                .addGap(18, 18, 18)
                                .addComponent(txtTelefoneRecado, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel22)
                                .addGap(18, 18, 18)
                                .addComponent(txtContato, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel23)
                                .addGap(18, 18, 18)
                                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(122, 122, 122)
                                .addComponent(jLabel8)
                                .addGap(18, 18, 18)
                                .addComponent(txtEndereco)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel10)
                                .addGap(18, 18, 18)
                                .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel11)
                                .addGap(18, 18, 18)
                                .addComponent(txtComplemento, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(127, 127, 127)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCliente)
                    .addComponent(chkFornecedor)
                    .addComponent(jLabel25)
                    .addComponent(txtDataCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTelefone1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(txtTelefone2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(txtTelefoneRecado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)
                    .addComponent(txtContato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtComplemento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtCep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCep))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(txtCodigoMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(txtMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(txtUF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addGap(125, 125, 125))
        );

        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Dados do Responsável"));

        jLabel24.setText("Nome");

        jLabel9.setText("CPF");

        txtResponsavelCpf.setName("cpf"); // NOI18N

        jLabel26.setText("RG");

        jLabel27.setText("Nascimento");

        txtResponsavelNascimento.setName("data"); // NOI18N

        jLabel28.setText("E-mail");

        jLabel29.setText("Parentesco");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addGap(18, 18, 18)
                        .addComponent(txtResponsavelNome, javax.swing.GroupLayout.PREFERRED_SIZE, 564, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(txtResponsavelCpf, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel26)
                        .addGap(18, 18, 18)
                        .addComponent(txtResponsavelRg, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addGap(18, 18, 18)
                        .addComponent(txtResponsavelNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel28)
                        .addGap(18, 18, 18)
                        .addComponent(txtResponsavelEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel29)
                        .addGap(18, 18, 18)
                        .addComponent(txtResponsavelParentesco, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(txtResponsavelNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txtResponsavelCpf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26)
                    .addComponent(txtResponsavelRg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtResponsavelNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27)
                    .addComponent(jLabel28)
                    .addComponent(txtResponsavelEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29)
                    .addComponent(txtResponsavelParentesco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 15, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Observação"));

        txtObservacao.setColumns(20);
        txtObservacao.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtObservacao.setRows(5);
        jScrollPane1.setViewportView(txtObservacao);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel30.setForeground(java.awt.Color.blue);
        jLabel30.setText("Para completar endereço, pressione ENTER após digitar o CEP");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvar)
                    .addComponent(jLabel30))
                .addGap(19, 19, 19))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        clienteCadastroViews.remove(this);

    }//GEN-LAST:event_formInternalFrameClosed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        MAIN_VIEW.removeTab(this.getName());
    }//GEN-LAST:event_formInternalFrameClosing

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown

    }//GEN-LAST:event_formComponentShown

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        if (validar()) {
            salvar();
        }
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void txtCepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCepActionPerformed
        buscarEndereco();
    }//GEN-LAST:event_txtCepActionPerformed

    private void txtIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdActionPerformed

    private void txtCodigoMunicipioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoMunicipioActionPerformed
        buscarMunicipio();
    }//GEN-LAST:event_txtCodigoMunicipioActionPerformed

    private void btnCepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCepActionPerformed
        pesquisarCep();
    }//GEN-LAST:event_btnCepActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCep;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JCheckBox chkCliente;
    private javax.swing.JCheckBox chkFornecedor;
    private javax.swing.JCheckBox chkIeIsento;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane tabTipo;
    private javax.swing.JTextField txtBairro;
    private javax.swing.JFormattedTextField txtCep;
    private javax.swing.JFormattedTextField txtCnpj;
    private javax.swing.JTextField txtCodigoMunicipio;
    private javax.swing.JTextField txtComplemento;
    private javax.swing.JTextField txtContato;
    private javax.swing.JFormattedTextField txtCpf;
    private javax.swing.JTextField txtDataCadastro;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtEndereco;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtIe;
    private javax.swing.JTextField txtMunicipio;
    private javax.swing.JFormattedTextField txtNascimento;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtNomeFantasia;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JTextArea txtObservacao;
    private javax.swing.JTextField txtRazaoSocial;
    private javax.swing.JFormattedTextField txtResponsavelCpf;
    private javax.swing.JTextField txtResponsavelEmail;
    private javax.swing.JFormattedTextField txtResponsavelNascimento;
    private javax.swing.JTextField txtResponsavelNome;
    private javax.swing.JTextField txtResponsavelParentesco;
    private javax.swing.JTextField txtResponsavelRg;
    private javax.swing.JTextField txtRg;
    private javax.swing.JFormattedTextField txtTelefone1;
    private javax.swing.JFormattedTextField txtTelefone2;
    private javax.swing.JFormattedTextField txtTelefoneRecado;
    private javax.swing.JTextField txtUF;
    // End of variables declaration//GEN-END:variables
}
