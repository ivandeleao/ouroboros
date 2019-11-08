/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.osTransporte;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import model.mysql.bean.principal.documento.OSTransporteItem;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.bean.principal.pessoa.PessoaTipo;
import model.mysql.dao.principal.OSTransporteItemDAO;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.Decimal;
import util.JSwing;
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
public class OstItemView extends javax.swing.JDialog {

    OSTransporteItem ostItem;
    OSTransporteItemDAO ostItemDAO = new OSTransporteItemDAO();
    
    Pessoa destinatario;

    private OstItemView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public OstItemView(OSTransporteItem ostItem) {
        super(MAIN_VIEW, true);
        initComponents();
        JSwing.startComponentsBehavior(this);
        definirAtalhos();

        this.ostItem = ostItem;

        carregarDados();

        this.setLocationRelativeTo(this);
        this.setVisible(true);
    }

    private void definirAtalhos() {
        //JRootPane rootPane = this.getRootPane();
        InputMap im = rootPane.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = rootPane.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "fechar");
        am.put("fechar", new FormKeyStroke("ESC"));

    }

    protected class FormKeyStroke extends AbstractAction {

        private final String key;

        public FormKeyStroke(String key) {
            this.key = key;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (key) {
                case "ESC":
                    dispose();
                    break;
            }
        }
    }

    public OSTransporteItem getOSTransporteItem() {
        return ostItem;
    }

    private void carregarDados() {
        if (ostItem.getId() == null) {
            ostItem.setDescricao("ENTREGA");
            ostItem.setValor(new BigDecimal(50));
            ostItem.setMotoristaValor(new BigDecimal(50));
            ostItem.setMotoristaPercentual(new BigDecimal(90));
            ostItem.setPedagioValor(new BigDecimal(50));
            ostItem.setPedagioPercentual(new BigDecimal(90));
            ostItem.setAdicionalValor(new BigDecimal(50));
            ostItem.setAdicionalPercentual(new BigDecimal(90));
        }

        exibirDestinatario();

        txtEndereco.setText(ostItem.getEndereco());
        txtCidade.setText(ostItem.getCidade());

        txtDescricao.setText(ostItem.getDescricao());
        txtValor.setText(Decimal.toString(ostItem.getValor()));
        txtMotoristaValor.setText(Decimal.toString(ostItem.getMotoristaValor()));
        txtMotoristaPercentual.setText(Decimal.toString(ostItem.getMotoristaPercentual()));
        txtPedagioValor.setText(Decimal.toString(ostItem.getPedagioValor()));
        txtPedagioPercentual.setText(Decimal.toString(ostItem.getPedagioPercentual()));
        txtAdicionalValor.setText(Decimal.toString(ostItem.getAdicionalValor()));
        txtAdicionalPercentual.setText(Decimal.toString(ostItem.getAdicionalPercentual()));

        calcularTotal();
        
        txtDescricao.requestFocus();
    }

    private void pesquisarDestinatario() {
        PessoaPesquisaView pesquisa = new PessoaPesquisaView(PessoaTipo.CLIENTE);

        if (pesquisa.getPessoa() != null) {
            ostItem.setDestinatario(pesquisa.getPessoa());
            exibirDestinatario();
            txtEndereco.setText(pesquisa.getPessoa().getEnderecoCompleto());
            txtCidade.setText(pesquisa.getPessoa().getMunicipio());
            txtUf.setText(pesquisa.getPessoa().getUf());
        }
    }

    private void exibirDestinatario() {
        if (ostItem.getDestinatario() != null) {
            txtDestinatarioNome.setText(ostItem.getDestinatario().getId() + " - " + ostItem.getDestinatario().getNome());
            txtDestinatarioNome.setCaretPosition(0);
            txtDestinatarioTelefone.setText(ostItem.getDestinatario().getTelefone1());

        } else {
            txtDestinatarioNome.setText("NÃO INFORMADO");
        }
    }

    
    private void calcularTotal() {

        BigDecimal valor = Decimal.fromString(txtValor.getText());
        BigDecimal pedagioValor = Decimal.fromString(txtPedagioValor.getText());
        BigDecimal adicionalValor = Decimal.fromString(txtAdicionalValor.getText());

        BigDecimal total = valor.add(pedagioValor).add(adicionalValor);
        
        txtSubtotal.setText(Decimal.toString(total));
    }
    
    private boolean validar() {
        boolean valido = true;
        
        if(ostItem.getDestinatario() == null) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Informe o destinatário", "Atenção", JOptionPane.WARNING_MESSAGE);
            valido = false;
        }
        
        if(txtDescricao.getText().trim().length() < 3) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "A descrição deve ter no mínimo 3 caracteres", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtDescricao.requestFocus();
            valido = false;
        }
        
        
        return valido;
    }
    
    private void salvar() {

        ostItem.setEndereco(txtEndereco.getText());
        ostItem.setCidade(txtCidade.getText());
        ostItem.setUf(txtUf.getText());

        ostItem.setDescricao(txtDescricao.getText());
        ostItem.setValor(Decimal.fromString(txtValor.getText()));
        ostItem.setMotoristaValor(Decimal.fromString(txtMotoristaValor.getText()));
        ostItem.setMotoristaPercentual(Decimal.fromString(txtMotoristaPercentual.getText()));
        ostItem.setPedagioValor(Decimal.fromString(txtPedagioValor.getText()));
        ostItem.setPedagioPercentual(Decimal.fromString(txtPedagioPercentual.getText()));
        ostItem.setAdicionalValor(Decimal.fromString(txtAdicionalValor.getText()));
        ostItem.setAdicionalPercentual(Decimal.fromString(txtAdicionalPercentual.getText()));

        ostItem = ostItemDAO.save(ostItem);

        //ost.addOSTransporteItem(ostItem);
    }


    private void confirmar() {
        if(validar()) {
            salvar();
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

        btnCancelar = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        btnFuncionario = new javax.swing.JButton();
        txtDestinatarioNome = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        txtDestinatarioTelefone = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        txtEndereco = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        txtCidade = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        txtUf = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        txtDescricao = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtAdicionalPercentual = new javax.swing.JFormattedTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtPedagioValor = new javax.swing.JFormattedTextField();
        txtMotoristaPercentual = new javax.swing.JFormattedTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txtMotoristaValor = new javax.swing.JFormattedTextField();
        txtValor = new javax.swing.JFormattedTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtAdicionalValor = new javax.swing.JFormattedTextField();
        txtPedagioPercentual = new javax.swing.JFormattedTextField();
        jLabel31 = new javax.swing.JLabel();
        txtSubtotal = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Dados do Item");
        setResizable(false);

        btnCancelar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnOk.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnOk.setText("Ok");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnFuncionario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/user_red.png"))); // NOI18N
        btnFuncionario.setText("DESTINATÁRIO");
        btnFuncionario.setContentAreaFilled(false);
        btnFuncionario.setFocusable(false);
        btnFuncionario.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFuncionario.setIconTextGap(10);
        btnFuncionario.setPreferredSize(new java.awt.Dimension(180, 49));
        btnFuncionario.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFuncionario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFuncionarioActionPerformed(evt);
            }
        });

        txtDestinatarioNome.setEditable(false);
        txtDestinatarioNome.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDestinatarioNome.setText("NÃO INFORMADO");
        txtDestinatarioNome.setFocusable(false);

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel29.setText("Telefone");

        txtDestinatarioTelefone.setEditable(false);
        txtDestinatarioTelefone.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel28.setText("Endereço");

        txtEndereco.setEditable(false);
        txtEndereco.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel30.setText("Cidade");

        txtCidade.setEditable(false);
        txtCidade.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtCidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCidadeActionPerformed(evt);
            }
        });

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel32.setText("UF");

        txtUf.setEditable(false);
        txtUf.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtUf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUfActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btnFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtDestinatarioNome, javax.swing.GroupLayout.PREFERRED_SIZE, 588, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel29)
                        .addGap(18, 18, 18)
                        .addComponent(txtDestinatarioTelefone))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addGap(18, 18, 18)
                        .addComponent(txtEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 554, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel30)
                        .addGap(18, 18, 18)
                        .addComponent(txtCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel32)
                        .addGap(18, 18, 18)
                        .addComponent(txtUf, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtDestinatarioNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel29)
                        .addComponent(txtDestinatarioTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel32)
                        .addComponent(txtUf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel28)
                        .addComponent(txtEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel30)
                        .addComponent(txtCidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel27.setText("Descrição do Serviço");

        txtDescricao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDescricao.setText("ENTREGA");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("% Adicional");

        txtAdicionalPercentual.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAdicionalPercentual.setText("0,00");
        txtAdicionalPercentual.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAdicionalPercentual.setName("decimal"); // NOI18N
        txtAdicionalPercentual.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAdicionalPercentualKeyReleased(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel18.setText("% Pedágio");

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel19.setText("Pedágio");

        txtPedagioValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPedagioValor.setText("0,00");
        txtPedagioValor.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPedagioValor.setName("decimal"); // NOI18N
        txtPedagioValor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPedagioValorKeyReleased(evt);
            }
        });

        txtMotoristaPercentual.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMotoristaPercentual.setText("0,00");
        txtMotoristaPercentual.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtMotoristaPercentual.setName("decimal"); // NOI18N
        txtMotoristaPercentual.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMotoristaPercentualKeyReleased(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel20.setText("% Motorista");

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel22.setText("Motorista");

        txtMotoristaValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMotoristaValor.setText("0,00");
        txtMotoristaValor.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtMotoristaValor.setName("decimal"); // NOI18N
        txtMotoristaValor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMotoristaValorKeyReleased(evt);
            }
        });

        txtValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValor.setText("0,00");
        txtValor.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtValor.setName("decimal"); // NOI18N
        txtValor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorKeyReleased(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel23.setText("Valor");

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel17.setText("Adicional");

        txtAdicionalValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAdicionalValor.setText("0,00");
        txtAdicionalValor.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAdicionalValor.setName("decimal"); // NOI18N
        txtAdicionalValor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAdicionalValorKeyReleased(evt);
            }
        });

        txtPedagioPercentual.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPedagioPercentual.setText("0,00");
        txtPedagioPercentual.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPedagioPercentual.setName("decimal"); // NOI18N
        txtPedagioPercentual.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPedagioPercentualKeyReleased(evt);
            }
        });

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel31.setText("Subtotal");

        txtSubtotal.setEditable(false);
        txtSubtotal.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtSubtotal.setForeground(java.awt.Color.red);
        txtSubtotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSubtotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSubtotalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel27)
                .addGap(18, 18, 18)
                .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addGap(18, 18, 18)
                        .addComponent(txtPedagioPercentual, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addGap(18, 18, 18)
                                .addComponent(txtPedagioValor, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addGap(18, 18, 18)
                                .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel31)
                                    .addComponent(jLabel17))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtSubtotal)
                                    .addComponent(txtAdicionalValor, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addGap(18, 18, 18)
                                .addComponent(txtMotoristaValor, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel20)
                                .addGap(18, 18, 18)
                                .addComponent(txtMotoristaPercentual, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addComponent(txtAdicionalPercentual, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22)
                            .addComponent(txtMotoristaValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20)
                            .addComponent(txtMotoristaPercentual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(txtPedagioValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18)
                            .addComponent(txtPedagioPercentual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(txtAdicionalValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtAdicionalPercentual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel27)
                        .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(txtSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar)
                    .addComponent(btnOk))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        confirmar();
    }//GEN-LAST:event_btnOkActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void txtAdicionalPercentualKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAdicionalPercentualKeyReleased
        calcularTotal();
    }//GEN-LAST:event_txtAdicionalPercentualKeyReleased

    private void txtAdicionalValorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAdicionalValorKeyReleased
        calcularTotal();
    }//GEN-LAST:event_txtAdicionalValorKeyReleased

    private void txtPedagioPercentualKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPedagioPercentualKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPedagioPercentualKeyReleased

    private void txtPedagioValorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPedagioValorKeyReleased
        calcularTotal();
    }//GEN-LAST:event_txtPedagioValorKeyReleased

    private void txtMotoristaPercentualKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMotoristaPercentualKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMotoristaPercentualKeyReleased

    private void txtMotoristaValorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMotoristaValorKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMotoristaValorKeyReleased

    private void txtValorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorKeyReleased
        calcularTotal();
    }//GEN-LAST:event_txtValorKeyReleased

    private void btnFuncionarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFuncionarioActionPerformed
        pesquisarDestinatario();
    }//GEN-LAST:event_btnFuncionarioActionPerformed

    private void txtCidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCidadeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCidadeActionPerformed

    private void txtSubtotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSubtotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSubtotalActionPerformed

    private void txtUfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUfActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUfActionPerformed

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
            java.util.logging.Logger.getLogger(OstItemView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OstItemView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OstItemView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OstItemView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                OstItemView dialog = new OstItemView(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnFuncionario;
    private javax.swing.JButton btnOk;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JFormattedTextField txtAdicionalPercentual;
    private javax.swing.JFormattedTextField txtAdicionalValor;
    private javax.swing.JTextField txtCidade;
    private javax.swing.JTextField txtDescricao;
    private javax.swing.JTextField txtDestinatarioNome;
    private javax.swing.JTextField txtDestinatarioTelefone;
    private javax.swing.JTextField txtEndereco;
    private javax.swing.JFormattedTextField txtMotoristaPercentual;
    private javax.swing.JFormattedTextField txtMotoristaValor;
    private javax.swing.JFormattedTextField txtPedagioPercentual;
    private javax.swing.JFormattedTextField txtPedagioValor;
    private javax.swing.JTextField txtSubtotal;
    private javax.swing.JTextField txtUf;
    private javax.swing.JFormattedTextField txtValor;
    // End of variables declaration//GEN-END:variables
}
