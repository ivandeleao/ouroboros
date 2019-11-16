/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.pessoa;

import java.awt.Dimension;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.mysql.bean.endereco.Cidade;
import model.mysql.bean.endereco.Endereco;
import model.mysql.bean.principal.pessoa.Grupo;
import model.mysql.bean.principal.pessoa.Perfil;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.dao.endereco.CidadeDAO;
import model.mysql.dao.endereco.EnderecoDAO;
import model.mysql.dao.principal.pessoa.PerfilDAO;
import model.mysql.dao.principal.pessoa.PessoaDAO;
import model.jtable.pessoa.PerfilJTableModel;
import model.mysql.dao.principal.UsuarioDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import ouroboros.Ouroboros;
import util.JSwing;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;
import util.Decimal;
import util.Texto;
import view.endereco.EnderecoPesquisaView;
import view.grupo.GrupoPesquisaView;
import view.grupo.PerfilCadastroView;

/**
 *
 * @author ivand
 */
public class PessoaCadastroView extends javax.swing.JInternalFrame {

    private static List<PessoaCadastroView> clienteCadastroViews = new ArrayList<>(); //instâncias

    private PessoaDAO pessoaDAO = new PessoaDAO();
    private Pessoa pessoa;
    
    PerfilJTableModel perfilJTableModel = new PerfilJTableModel();

    public static PessoaCadastroView getInstance(Pessoa cliente) {
        for (PessoaCadastroView clienteCadastroView : clienteCadastroViews) {
            if (clienteCadastroView.pessoa == cliente) {
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
        

        this.pessoa = cliente;

        formatarPerfis();
        carregarDados();

    }

    private void carregarDados() {
        
        txtLimiteCredito.setEditable(UsuarioDAO.validarAdministrador());
        
        if (pessoa.getId() == null) {
            chkCliente.setSelected(true);
            txtLimiteCredito.setText(Decimal.toString(Ouroboros.CLIENTE_LIMITE_CREDITO));
            JSwing.setComponentesHabilitados(pnlPerfis, false);
            
        } else {
            txtDataCadastro.setText(DateTime.toStringDate(pessoa.getCriacao()));
            
            if(!pessoa.getCnpj().isEmpty() || !pessoa.getNomeFantasia().isEmpty()) {
               tabTipo.setSelectedIndex(1);
               txtRazaoSocial.setText(pessoa.getNome());
            } else {
                txtNome.setText(pessoa.getNome());
            }
            
            txtId.setText(pessoa.getId().toString());

            chkCliente.setSelected(pessoa.isCliente());
            chkFornecedor.setSelected(pessoa.isFornecedor());
            
            txtCpf.setText(Texto.soNumeros(pessoa.getCpf()));
            txtRg.setText(pessoa.getRg());
            String nascimento = DateTime.toStringDate(pessoa.getNascimento());
            txtNascimento.setText(nascimento);

            chkMei.setSelected(pessoa.isMei());
            txtCnpj.setText(pessoa.getCnpj());
            
            txtNomeFantasia.setText(pessoa.getNomeFantasia());
            txtIe.setText(pessoa.getIe());
            chkIeIsento.setSelected(pessoa.isIeIsento());
            txtIm.setText(pessoa.getIm());
            txtSuframa.setText(pessoa.getSuframa());

            txtTelefone1.setText(pessoa.getTelefone1());
            txtTelefone2.setText(pessoa.getTelefone2());
            txtTelefoneRecado.setText(pessoa.getTelefoneRecado());
            txtContato.setText(pessoa.getContato());
            
            txtEmail.setText(pessoa.getEmail());

            txtCep.setText(pessoa.getCep());
            txtEndereco.setText(pessoa.getEndereco());
            txtNumero.setText(pessoa.getNumero());
            txtComplemento.setText(pessoa.getComplemento());
            txtBairro.setText(pessoa.getBairro());
            txtCodigoMunicipio.setText(pessoa.getCodigoMunicipio());
            buscarMunicipio();
            
            txtLimiteCredito.setText(Decimal.toString(pessoa.getLimiteCredito()));
            
            
            txtResponsavelNome.setText(pessoa.getResponsavelNome());
            txtResponsavelCpf.setText(pessoa.getResponsavelCpf());
            txtResponsavelRg.setText(pessoa.getResponsavelRg());
            txtResponsavelNascimento.setText(DateTime.toString(pessoa.getResponsavelNascimento()));
            txtResponsavelEmail.setText(pessoa.getResponsavelEmail());
            txtResponsavelParentesco.setText(pessoa.getResponsavelParentesco());

            txtObservacao.setText(pessoa.getObservacao());
            
            carregarPerfis();

        }
    }
    
    private void formatarPerfis() {
        tblPerfil.setModel(perfilJTableModel);

        tblPerfil.setRowHeight(24);
        tblPerfil.setIntercellSpacing(new Dimension(10, 10));
        
        tblPerfil.getColumn("Grupo").setPreferredWidth(800);
        
        tblPerfil.getColumn("Vencimento").setPreferredWidth(200);
        tblPerfil.getColumn("Vencimento").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);
    }
    
    private void carregarPerfis() {
        perfilJTableModel.clear();
        perfilJTableModel.addList(pessoa.getPerfis());
        
    }

    private boolean validar() {
        boolean valido = true;

        txtCpf.setText(txtCpf.getText().trim());
        txtCnpj.setText(txtCnpj.getText().trim());
        
        Boolean isCliente = chkCliente.isSelected();
        Boolean isFornecedor = chkFornecedor.isSelected();
        String nome = txtNome.getText();
        String razaoSocial = txtRazaoSocial.getText();
        String cpf = txtCpf.getText();
        String cnpj = txtCnpj.getText();
        
        if(!isCliente && !isFornecedor) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Indique o(s) perfil(is) desta pessoa","Atenção", JOptionPane.WARNING_MESSAGE);
            valido = false;
        }
        if (nome.length() < 3 && razaoSocial.length() < 3) {
            if(nome.length() < 3) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Nome deve ter no mínimo 3 caracteres","Atenção", JOptionPane.WARNING_MESSAGE);
                txtNome.requestFocus();
            } else {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Razão social deve ter no mínimo 3 caracteres","Atenção", JOptionPane.WARNING_MESSAGE);
                txtRazaoSocial.requestFocus();
            }
            valido = false;
        }
        
        //CPF ou CNPJ já existente
        if(cpf.length() > 0) {
            Pessoa p = pessoaDAO.findByCpfCnpj(cpf);
            if(p != null && !p.getId().equals(pessoa.getId())) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Já existe cadastro com este CPF.\n" + p.getNome(), "Atenção", JOptionPane.WARNING_MESSAGE);
                txtCpf.requestFocus();
                valido = false;
            }
        }
        
        if(cnpj.length() > 0) {
            Pessoa p = pessoaDAO.findByCpfCnpj(cnpj);
            if(p != null && !p.getId().equals(pessoa.getId())) {
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

        boolean mei = chkMei.isSelected();
        String cnpj = txtCnpj.getText();
        
        String nomeFantasia = txtNomeFantasia.getText();
        String ie = txtIe.getText();
        boolean ieIsento = chkIeIsento.isSelected();
        String im = txtIm.getText();
        String suframa = txtSuframa.getText();

        String observacao = txtObservacao.getText();

        if (pessoa != null) {
            pessoa.setId(pessoa.getId());
        }

        if(!txtRazaoSocial.getText().isEmpty()) {
            nome = txtRazaoSocial.getText();
        } else {
            nome = txtNome.getText();
        }
        
        pessoa.setCliente(isCliente);
        pessoa.setFornecedor(isFornecedor);
        pessoa.setNome(nome);
        pessoa.setCpf(cpf);
        pessoa.setRg(rg);
        pessoa.setNascimento(nascimento);

        pessoa.setMei(mei);
        pessoa.setCnpj(cnpj);
        
        pessoa.setNomeFantasia(nomeFantasia);
        pessoa.setIe(ie);
        pessoa.setIeIsento(ieIsento);
        pessoa.setIm(im);
        pessoa.setSuframa(suframa);

        pessoa.setTelefone1(txtTelefone1.getText());
        pessoa.setTelefone2(txtTelefone2.getText());
        pessoa.setTelefoneRecado(txtTelefoneRecado.getText());
        pessoa.setContato(txtContato.getText());
        
        pessoa.setEmail(txtEmail.getText());

        pessoa.setCep(txtCep.getText());
        pessoa.setEndereco(txtEndereco.getText());
        pessoa.setNumero(txtNumero.getText());
        pessoa.setComplemento(txtComplemento.getText());
        pessoa.setBairro(txtBairro.getText());
        pessoa.setCodigoMunicipio(txtCodigoMunicipio.getText());
        
        pessoa.setLimiteCredito(Decimal.fromString(txtLimiteCredito.getText()));

        pessoa.setObservacao(observacao);
        
        pessoa.setResponsavelNome(txtResponsavelNome.getText());
        pessoa.setResponsavelCpf(txtResponsavelCpf.getText());
        pessoa.setResponsavelRg(txtResponsavelRg.getText());
        pessoa.setResponsavelNascimento(DateTime.fromStringToLocalDate(txtResponsavelNascimento.getText()));
        pessoa.setResponsavelEmail(txtResponsavelEmail.getText());
        pessoa.setResponsavelParentesco(txtResponsavelParentesco.getText());

        pessoa = pessoaDAO.save(pessoa);

        txtDataCadastro.setText(DateTime.toStringDate(pessoa.getCriacao()));
        txtId.setText(pessoa.getId().toString());

        JOptionPane.showMessageDialog(rootPane, "Dados salvos com sucesso");

        PessoaContainerView.getInstance(pessoa).gerarTabs();

        JSwing.setComponentesHabilitados(pnlPerfis, true);
        
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
    
    private void adicionarPerfil() {
        GrupoPesquisaView gpv = new GrupoPesquisaView();
        Grupo grupo = gpv.getGrupo();
        
        if(grupo != null) {
            //Verificar se já existe o grupo
            if(pessoa.getPerfis().stream().filter((p) -> (grupo.equals(p.getGrupo()))).count() > 0) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Grupo já existente", "Atenção", JOptionPane.WARNING_MESSAGE);
                
            } else {
                PerfilCadastroView pcv = new PerfilCadastroView(new Perfil(pessoa, grupo));
                carregarPerfis();
            }
            
            
        }
        
        
    }
    
    private void removerPerfil() {
        if(tblPerfil.getSelectedRow() > -1) {
            Perfil perfil = perfilJTableModel.getRow(tblPerfil.getSelectedRow());
            
            pessoa.removePerfil(perfil);
            new PerfilDAO().remove(perfil);
            
            carregarPerfis();
        }
    }
    
    private void editarPerfil() {
        if(tblPerfil.getSelectedRow() > -1) {
            Perfil perfil = perfilJTableModel.getRow(tblPerfil.getSelectedRow());
            PerfilCadastroView pcv = new PerfilCadastroView(perfil);
            carregarPerfis();
        }
    }
    
    private void editarLimiteCredito() {
        if(UsuarioDAO.validarAdministradorComLogin()) {
            txtLimiteCredito.setEditable(true);
            txtLimiteCredito.requestFocus();
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
        jLabel35 = new javax.swing.JLabel();
        txtSuframa = new javax.swing.JFormattedTextField();
        txtIm = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        chkMei = new javax.swing.JCheckBox();
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
        jLabel34 = new javax.swing.JLabel();
        txtLimiteCredito = new javax.swing.JFormattedTextField();
        jLabel37 = new javax.swing.JLabel();
        btnLimiteCreditoEditar = new javax.swing.JButton();
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
        jLabel33 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtObservacao = new javax.swing.JTextArea();
        jLabel32 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        pnlPerfis = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        btnAdicionarPerfil = new javax.swing.JButton();
        btnRemoverGrupo = new javax.swing.JButton();
        btnEditarPerfil = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblPerfil = new javax.swing.JTable();

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

        txtNome.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Nome");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("CPF");

        txtCpf.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCpf.setName("cpf"); // NOI18N

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel12.setText("RG");

        txtRg.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtNascimento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNascimento.setName("data"); // NOI18N

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel17.setText("Nascimento");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNome)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 535, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel12))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtRg)
                    .addComponent(txtCpf, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(txtCpf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12)
                        .addComponent(txtRg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17)))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        tabTipo.addTab("Física", jPanel2);

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel13.setText("Razão Social");

        txtRazaoSocial.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel14.setText("CNPJ");

        txtCnpj.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCnpj.setName("cnpj"); // NOI18N

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel15.setText("IE");

        txtIe.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtNomeFantasia.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Nome Fantasia");

        chkIeIsento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkIeIsento.setText("IE Isento");

        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel35.setText("Suframa");

        txtSuframa.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtSuframa.setName("inteiro"); // NOI18N

        txtIm.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel36.setText("IM");

        chkMei.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkMei.setText("MEI");

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
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtNomeFantasia, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel15)
                        .addGap(18, 18, 18)
                        .addComponent(txtIe, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(chkIeIsento))
                    .addComponent(txtRazaoSocial))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel36)
                        .addGap(18, 18, 18)
                        .addComponent(txtIm, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(chkMei)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel14)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addGap(18, 18, 18)
                        .addComponent(txtSuframa))
                    .addComponent(txtCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
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
                    .addComponent(chkMei))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkIeIsento)
                    .addComponent(jLabel36)
                    .addComponent(txtIm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35)
                    .addComponent(txtSuframa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(txtNomeFantasia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        tabTipo.addTab("Jurídica", jPanel3);

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

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel18.setText("Cód. Município");

        txtMunicipio.setEditable(false);
        txtMunicipio.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel19.setText("Município");

        txtUF.setEditable(false);
        txtUF.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel20.setText("UF");

        chkCliente.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkCliente.setText("Cliente");

        chkFornecedor.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkFornecedor.setText("Fornecedor");

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

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel25.setText("Perfil:");

        btnCep.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnCep.setText("Cep");
        btnCep.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCep.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnCep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCepActionPerformed(evt);
            }
        });

        txtDataCadastro.setEditable(false);
        txtDataCadastro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDataCadastro.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("Cadastro");

        jLabel34.setBackground(new java.awt.Color(122, 138, 153));
        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel34.setForeground(java.awt.Color.white);
        jLabel34.setText("Dados Principais");
        jLabel34.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel34.setOpaque(true);

        txtLimiteCredito.setEditable(false);
        txtLimiteCredito.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtLimiteCredito.setName("decimal"); // NOI18N

        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel37.setForeground(java.awt.Color.red);
        jLabel37.setText("Limite de Crédito");

        btnLimiteCreditoEditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/lock.png"))); // NOI18N
        btnLimiteCreditoEditar.setToolTipText("Editar limite de crédito");
        btnLimiteCreditoEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimiteCreditoEditarActionPerformed(evt);
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
                        .addComponent(tabTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 933, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addComponent(txtDataCadastro))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel25))
                                .addGap(40, 40, 40)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(chkCliente)
                                            .addComponent(chkFornecedor))
                                        .addGap(14, 14, 14))
                                    .addComponent(txtId)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel16)
                            .addComponent(btnCep, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtBairro)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCodigoMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMunicipio)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtUF, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel37)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnLimiteCreditoEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtLimiteCredito, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtCep, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                                    .addComponent(txtTelefone1))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtEndereco)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtComplemento, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtTelefone2, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel21)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtTelefoneRecado, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel22)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtContato, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel23)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addContainerGap())
            .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDataCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkCliente)
                            .addComponent(jLabel25))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFornecedor))
                    .addComponent(tabTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtBairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel16)
                        .addComponent(txtCodigoMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18)
                        .addComponent(txtMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel19)
                        .addComponent(txtUF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel20)
                        .addComponent(jLabel37)
                        .addComponent(txtLimiteCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnLimiteCreditoEditar))
                .addGap(123, 123, 123))
        );

        btnSalvar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel24.setText("Nome");

        txtResponsavelNome.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("CPF");

        txtResponsavelCpf.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtResponsavelCpf.setName("cpf"); // NOI18N

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel26.setText("RG");

        txtResponsavelRg.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel27.setText("Nascimento");

        txtResponsavelNascimento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtResponsavelNascimento.setName("data"); // NOI18N

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel28.setText("E-mail");

        txtResponsavelEmail.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel29.setText("Parentesco");

        txtResponsavelParentesco.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel33.setBackground(new java.awt.Color(122, 138, 153));
        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel33.setForeground(java.awt.Color.white);
        jLabel33.setText("Dados do Responsável");
        jLabel33.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel33.setOpaque(true);

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
                        .addComponent(txtResponsavelNome)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(txtResponsavelCpf, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel26)
                        .addGap(18, 18, 18)
                        .addComponent(txtResponsavelRg, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                        .addComponent(txtResponsavelParentesco, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtObservacao.setColumns(20);
        txtObservacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtObservacao.setRows(5);
        txtObservacao.setMargin(new java.awt.Insets(4, 4, 4, 4));
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
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        jLabel30.setForeground(java.awt.Color.blue);
        jLabel30.setText("Para completar endereço, pressione ENTER após digitar o CEP");

        pnlPerfis.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel31.setBackground(new java.awt.Color(122, 138, 153));
        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel31.setForeground(java.awt.Color.white);
        jLabel31.setText("Perfis");
        jLabel31.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10)));
        jLabel31.setOpaque(true);

        btnAdicionarPerfil.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/add.png"))); // NOI18N
        btnAdicionarPerfil.setToolTipText("Adicionar perfil");
        btnAdicionarPerfil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarPerfilActionPerformed(evt);
            }
        });

        btnRemoverGrupo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/delete.png"))); // NOI18N
        btnRemoverGrupo.setToolTipText("Remover perfil");
        btnRemoverGrupo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverGrupoActionPerformed(evt);
            }
        });

        btnEditarPerfil.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/pencil.png"))); // NOI18N
        btnEditarPerfil.setToolTipText("Editar perfil");
        btnEditarPerfil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarPerfilActionPerformed(evt);
            }
        });

        tblPerfil.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblPerfil.setModel(new javax.swing.table.DefaultTableModel(
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
        tblPerfil.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblPerfilFocusGained(evt);
            }
        });
        tblPerfil.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPerfilMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblPerfil);

        javax.swing.GroupLayout pnlPerfisLayout = new javax.swing.GroupLayout(pnlPerfis);
        pnlPerfis.setLayout(pnlPerfisLayout);
        pnlPerfisLayout.setHorizontalGroup(
            pnlPerfisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlPerfisLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPerfisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAdicionarPerfil)
                    .addComponent(btnRemoverGrupo, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnEditarPerfil, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        pnlPerfisLayout.setVerticalGroup(
            pnlPerfisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPerfisLayout.createSequentialGroup()
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPerfisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPerfisLayout.createSequentialGroup()
                        .addComponent(btnAdicionarPerfil)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverGrupo)
                        .addGap(9, 9, 9)
                        .addComponent(btnEditarPerfil)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
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
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlPerfis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlPerfis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvar)
                    .addComponent(jLabel30))
                .addGap(34, 34, 34))
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

    private void btnAdicionarPerfilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarPerfilActionPerformed
        adicionarPerfil();
    }//GEN-LAST:event_btnAdicionarPerfilActionPerformed

    private void btnRemoverGrupoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverGrupoActionPerformed
        removerPerfil();
    }//GEN-LAST:event_btnRemoverGrupoActionPerformed

    private void btnEditarPerfilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarPerfilActionPerformed
        editarPerfil();
    }//GEN-LAST:event_btnEditarPerfilActionPerformed

    private void tblPerfilFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblPerfilFocusGained
    }//GEN-LAST:event_tblPerfilFocusGained

    private void tblPerfilMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPerfilMouseClicked
        if(evt.getClickCount() == 2) {
            editarPerfil();
        }
    }//GEN-LAST:event_tblPerfilMouseClicked

    private void btnLimiteCreditoEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimiteCreditoEditarActionPerformed
        editarLimiteCredito();
    }//GEN-LAST:event_btnLimiteCreditoEditarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionarPerfil;
    private javax.swing.JButton btnCep;
    private javax.swing.JButton btnEditarPerfil;
    private javax.swing.JButton btnLimiteCreditoEditar;
    private javax.swing.JButton btnRemoverGrupo;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JCheckBox chkCliente;
    private javax.swing.JCheckBox chkFornecedor;
    private javax.swing.JCheckBox chkIeIsento;
    private javax.swing.JCheckBox chkMei;
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
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
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
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel pnlPerfis;
    private javax.swing.JTabbedPane tabTipo;
    private javax.swing.JTable tblPerfil;
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
    private javax.swing.JTextField txtIm;
    private javax.swing.JFormattedTextField txtLimiteCredito;
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
    private javax.swing.JFormattedTextField txtSuframa;
    private javax.swing.JFormattedTextField txtTelefone1;
    private javax.swing.JFormattedTextField txtTelefone2;
    private javax.swing.JFormattedTextField txtTelefoneRecado;
    private javax.swing.JTextField txtUF;
    // End of variables declaration//GEN-END:variables
}
