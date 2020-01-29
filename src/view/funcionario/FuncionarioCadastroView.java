/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.funcionario;

import view.funcionario.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.mysql.bean.endereco.Cidade;
import model.mysql.bean.endereco.Endereco;
import model.mysql.bean.principal.Funcionario;
import model.mysql.dao.endereco.CidadeDAO;
import model.mysql.dao.endereco.EnderecoDAO;
import model.mysql.dao.principal.FuncionarioDAO;
import util.JSwing;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;
import util.Texto;
import view.endereco.EnderecoPesquisaView;
import view.endereco.MunicipioPesquisaView;

/**
 *
 * @author ivand
 */
public class FuncionarioCadastroView extends javax.swing.JInternalFrame {

    private static List<FuncionarioCadastroView> funcionarioCadastroViews = new ArrayList<>(); //instâncias

    private FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private Funcionario funcionario;

    public static FuncionarioCadastroView getInstance(Funcionario funcionario) {
        for (FuncionarioCadastroView funcionarioCadastroView : funcionarioCadastroViews) {
            if (funcionarioCadastroView.funcionario == funcionario) {
                return funcionarioCadastroView;
            }
        }
        funcionarioCadastroViews.add(new FuncionarioCadastroView(funcionario));
        return funcionarioCadastroViews.get(funcionarioCadastroViews.size() - 1);
    }

    private FuncionarioCadastroView() {
        initComponents();
        JSwing.startComponentsBehavior(this);
    }

    private FuncionarioCadastroView(Funcionario funcionario) {
        initComponents();
        JSwing.startComponentsBehavior(this);

        /*
        txtTelefone1.setDocument(new TelefoneDocument());
        txtCpf.setDocument(new CpfDocument());
        txtCnpj.setDocument(new CnpjDocument());
        //txtCep.setDocument(new CepDocument());
        txtNascimento.setDocument(new DataDocument());*/
        txtNome.requestFocus();

        this.funcionario = funcionario;

        carregarDados();

    }

    private void carregarDados() {
        if (funcionario.getId() != null) {
            txtNome.setText(funcionario.getNome());

            txtId.setText(funcionario.getId().toString());

            txtCpf.setText(Texto.soNumeros(funcionario.getCpf()));
            txtRg.setText(funcionario.getRg());
            String nascimento = DateTime.toStringDate(funcionario.getNascimento());
            txtNascimento.setText(nascimento);

            txtTelefone1.setText(funcionario.getTelefone1());
            txtTelefone2.setText(funcionario.getTelefone2());
            txtTelefoneRecado.setText(funcionario.getTelefoneRecado());
            txtContato.setText(funcionario.getContato());

            txtEmail.setText(funcionario.getEmail());

            txtCep.setText(funcionario.getCep());
            txtEndereco.setText(funcionario.getEndereco());
            txtNumero.setText(funcionario.getNumero());
            txtComplemento.setText(funcionario.getComplemento());
            txtBairro.setText(funcionario.getBairro());
            txtCodigoMunicipio.setText(funcionario.getCodigoMunicipio());
            buscarMunicipio();

            txtObservacao.setText(funcionario.getObservacao());

        }
    }

    private boolean validar() {
        boolean valido = true;

        txtCpf.setText(txtCpf.getText().trim());

        String nome = txtNome.getText();
        String cpf = txtCpf.getText();

        if (nome.length() < 3) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Nome deve ter no mínimo 3 caracteres", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtNome.requestFocus();
            valido = false;
        }

        return valido;
    }

    private void salvar() {
        String nome;
        String cpf = txtCpf.getText();
        String rg = txtRg.getText();
        Date nascimento = DateTime.toSqlDate(txtNascimento.getText());

        String observacao = txtObservacao.getText();

        if (funcionario != null) {
            funcionario.setId(funcionario.getId());
        }

        nome = txtNome.getText();

        funcionario.setNome(nome);
        funcionario.setCpf(cpf);
        funcionario.setRg(rg);
        funcionario.setNascimento(nascimento);

        funcionario.setTelefone1(txtTelefone1.getText());
        funcionario.setTelefone2(txtTelefone2.getText());
        funcionario.setTelefoneRecado(txtTelefoneRecado.getText());
        funcionario.setContato(txtContato.getText());

        funcionario.setEmail(txtEmail.getText());

        funcionario.setCep(txtCep.getText());
        funcionario.setEndereco(txtEndereco.getText());
        funcionario.setNumero(txtNumero.getText());
        funcionario.setComplemento(txtComplemento.getText());
        funcionario.setBairro(txtBairro.getText());
        funcionario.setCodigoMunicipio(txtCodigoMunicipio.getText());

        funcionario.setObservacao(observacao);

        funcionario = funcionarioDAO.save(funcionario);

        txtId.setText(funcionario.getId().toString());

        JOptionPane.showMessageDialog(rootPane, "Dados salvos com sucesso");

        FuncionarioContainerView.getInstance(funcionario).gerarTabs();

    }

    private void buscarEndereco() {
        String cep = Texto.soNumeros(txtCep.getText());
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

        if (endereco != null) {
            txtCep.setText(endereco.getCep());
            txtEndereco.setText(endereco.getEnderecoCompleto());
            txtBairro.setText(endereco.getBairro().getNome());
            txtCodigoMunicipio.setText(endereco.getCidade().getCodigoIbgeCompleto());
            txtMunicipio.setText(endereco.getCidade().getNome());
            txtUF.setText(endereco.getCidade().getEstado().getSigla());
            txtNumero.requestFocus();
        }
        txtCep.requestFocus();
    }

    private void pesquisarMunicipio() {
        MunicipioPesquisaView cidadePesquisaView = new MunicipioPesquisaView();
        Cidade cidade = cidadePesquisaView.getCidade();

        if (cidade != null) {
            txtCodigoMunicipio.setText(cidade.getCodigoIbgeCompleto());
            txtMunicipio.setText(cidade.getNome());
            txtUF.setText(cidade.getEstado().getSigla());
        }

        txtCodigoMunicipio.requestFocus();
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
        txtBairro = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtCodigoMunicipio = new javax.swing.JTextField();
        txtMunicipio = new javax.swing.JTextField();
        txtUF = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtTelefoneRecado = new javax.swing.JFormattedTextField();
        jLabel22 = new javax.swing.JLabel();
        txtContato = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        btnCep = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtCpf = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        txtRg = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtNascimento = new javax.swing.JFormattedTextField();
        jLabel34 = new javax.swing.JLabel();
        btnMunicipio = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtObservacao = new javax.swing.JTextArea();
        jLabel32 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();

        setClosable(true);
        setTitle("Cadastro de Funcionário");
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

        txtTelefone1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTelefone1.setName("telefone"); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Telefone");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("Telefone 2");

        txtTelefone2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTelefone2.setName("telefone"); // NOI18N

        txtCep.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCep.setName("cep"); // NOI18N
        txtCep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCepActionPerformed(evt);
            }
        });

        txtEndereco.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("Endereço");

        txtNumero.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel10.setText("Número");

        txtComplemento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setText("Complemento");

        txtBairro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel16.setText("Bairro");

        txtCodigoMunicipio.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCodigoMunicipio.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCodigoMunicipio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoMunicipioActionPerformed(evt);
            }
        });

        txtMunicipio.setEditable(false);
        txtMunicipio.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtUF.setEditable(false);
        txtUF.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel20.setText("UF");

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel21.setText("Recado");

        txtTelefoneRecado.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTelefoneRecado.setName("telefone"); // NOI18N

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel22.setText("Contato");

        txtContato.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel23.setText("E-mail");

        txtEmail.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        btnCep.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnCep.setText("Cep");
        btnCep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCepActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Nome");

        txtNome.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("CPF");

        txtCpf.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCpf.setName("cpf"); // NOI18N

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel12.setText("RG");

        txtRg.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel17.setText("Nascimento");

        txtNascimento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNascimento.setName("data"); // NOI18N

        jLabel34.setBackground(new java.awt.Color(122, 138, 153));
        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel34.setForeground(java.awt.Color.white);
        jLabel34.setText("Dados Principais");
        jLabel34.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel34.setOpaque(true);

        btnMunicipio.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnMunicipio.setText("Município");
        btnMunicipio.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMunicipio.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnMunicipio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMunicipioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel16)
                            .addComponent(btnCep))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(122, 122, 122)
                                .addComponent(jLabel8)
                                .addGap(18, 18, 18)
                                .addComponent(txtEndereco, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel10)
                                .addGap(18, 18, 18)
                                .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel11)
                                .addGap(18, 18, 18)
                                .addComponent(txtComplemento, javax.swing.GroupLayout.PREFERRED_SIZE, 462, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtCep, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(44, 1105, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                                        .addComponent(txtEmail))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txtBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnMunicipio)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtCodigoMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtMunicipio)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel20)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtUF, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(10, 10, 10))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5)
                            .addComponent(jLabel1))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtCpf, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel12)
                                .addGap(18, 18, 18)
                                .addComponent(txtRg, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel17)
                                .addGap(18, 18, 18)
                                .addComponent(txtNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(txtNome))
                                .addContainerGap())))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel34)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtCpf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(txtRg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addGap(18, 18, 18)
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
                    .addComponent(txtMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(btnMunicipio))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtObservacao.setColumns(20);
        txtObservacao.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtObservacao.setRows(5);
        jScrollPane1.setViewportView(txtObservacao);

        jLabel32.setBackground(new java.awt.Color(122, 138, 153));
        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel32.setForeground(java.awt.Color.white);
        jLabel32.setText("Observação");
        jLabel32.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel32.setOpaque(true);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1140, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
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
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvar)
                    .addComponent(jLabel30))
                .addGap(19, 19, 19))
        );

        jPanel5.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        funcionarioCadastroViews.remove(this);

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

    private void btnMunicipioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMunicipioActionPerformed
        pesquisarMunicipio();
    }//GEN-LAST:event_btnMunicipioActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCep;
    private javax.swing.JButton btnMunicipio;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtBairro;
    private javax.swing.JFormattedTextField txtCep;
    private javax.swing.JTextField txtCodigoMunicipio;
    private javax.swing.JTextField txtComplemento;
    private javax.swing.JTextField txtContato;
    private javax.swing.JFormattedTextField txtCpf;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtEndereco;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtMunicipio;
    private javax.swing.JFormattedTextField txtNascimento;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JTextArea txtObservacao;
    private javax.swing.JTextField txtRg;
    private javax.swing.JFormattedTextField txtTelefone1;
    private javax.swing.JFormattedTextField txtTelefone2;
    private javax.swing.JFormattedTextField txtTelefoneRecado;
    private javax.swing.JTextField txtUF;
    // End of variables declaration//GEN-END:variables
}
