/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.documentoSaida;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import model.mysql.bean.fiscal.UnidadeComercial;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.MovimentoFisicoTipo;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.catalogo.ProdutoTamanho;
import model.mysql.bean.principal.catalogo.ProdutoTipo;
import model.mysql.bean.principal.catalogo.Tamanho;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.documento.VendaTipo;
import model.mysql.dao.principal.MovimentoFisicoDAO;
import model.mysql.dao.principal.VendaDAO;
import model.mysql.dao.principal.catalogo.ProdutoDAO;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.VENDA_INSERCAO_DIRETA;
import util.Decimal;
import view.Toast;
import view.catalogo.geral.ProdutoPesquisaView;

/**
 *
 * @author ivand
 *
 * ESTE FOI CRIADO COMO JDIALOG DESDE O INÍCIO - USAR ESTE COMO REFERÊNCIA PARA
 * OS DEMAIS
 *
 *
 */
public class VendaMontarItemView extends javax.swing.JDialog {

    private Venda documento;
    Tamanho tamanho;
    ProdutoDAO produtoDAO = new ProdutoDAO();
    MovimentoFisicoDAO movimentoFisicoDAO = new MovimentoFisicoDAO();
    VendaDAO vendaDAO = new VendaDAO();

    List<Produto> produtos = new ArrayList<>();

    BigDecimal valorCobrado = BigDecimal.ZERO;
    
    MovimentoFisico movimentoFisico;

    /**
     * Creates new form ParcelamentoView
     */
    public VendaMontarItemView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public VendaMontarItemView(Venda documento, ProdutoTamanho produtoTamanho) {
        super(MAIN_VIEW, true);
        initComponents();
        definirAtalhos();

        this.documento = documento;

        this.tamanho = produtoTamanho.getTamanho();
        this.produtos.add(produtoTamanho.getProduto());
        this.produtos.add(null);
        this.produtos.add(null);
        this.produtos.add(null);

        carregarDados();

        this.setLocationRelativeTo(this);
        this.setVisible(true);
    }
    
    public MovimentoFisico getMovimentoFisico() {
        return movimentoFisico;
    }
    
    private void definirAtalhos() {
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

    private void carregarDados() {
        txtMontar.setText(tamanho.getCategoria() + " " + tamanho.getNome());

        txtProduto1Codigo.setText(produtos.get(0).getCodigo());
        carregarProduto1();

        txtQuantidade.setText("1");

    }

    private void carregarProduto1() {
        if (produtos.get(0) != null) {
            txtProduto1Codigo.setText(produtos.get(0).getCodigo());
            txtProduto1Descricao.setText(produtos.get(0).getNome());
            txtProduto1Valor.setText(Decimal.toString(produtos.get(0).getProdutoTamanho(tamanho).getValorVenda()));
            calcularValor();
        }
    }

    private void carregarProduto2() {
        if (produtos.get(1) != null) {
            txtProduto2Codigo.setText(produtos.get(1).getCodigo());
            txtProduto2Descricao.setText(produtos.get(1).getNome());
            txtProduto2Valor.setText(Decimal.toString(produtos.get(1).getProdutoTamanho(tamanho).getValorVenda()));
            calcularValor();
        }
    }

    private void carregarProduto3() {
        if (produtos.get(2) != null) {
            txtProduto3Codigo.setText(produtos.get(2).getCodigo());
            txtProduto3Descricao.setText(produtos.get(2).getNome());
            txtProduto3Valor.setText(Decimal.toString(produtos.get(2).getProdutoTamanho(tamanho).getValorVenda()));
            calcularValor();
        }
    }

    private void carregarProduto4() {
        if (produtos.get(3) != null) {
            txtProduto4Codigo.setText(produtos.get(3).getCodigo());
            txtProduto4Descricao.setText(produtos.get(3).getNome());
            txtProduto4Valor.setText(Decimal.toString(produtos.get(3).getProdutoTamanho(tamanho).getValorVenda()));
            calcularValor();
        }
    }

    private void removerProduto1() {
        produtos.set(0, null);
        txtProduto1Codigo.setText("");
        txtProduto1Descricao.setText("");
        txtProduto1Valor.setText("");
        calcularValor();
    }

    private void removerProduto2() {
        produtos.set(1, null);
        txtProduto2Codigo.setText("");
        txtProduto2Descricao.setText("");
        txtProduto2Valor.setText("");
        calcularValor();
    }

    private void removerProduto3() {
        produtos.set(2, null);
        txtProduto3Codigo.setText("");
        txtProduto3Descricao.setText("");
        txtProduto3Valor.setText("");
        calcularValor();
    }

    private void removerProduto4() {
        produtos.set(3, null);
        txtProduto4Codigo.setText("");
        txtProduto4Descricao.setText("");
        txtProduto4Valor.setText("");
        calcularValor();
    }

    private Produto findProduto(String codigo) {
        if (produtoDAO.findByCodigo(codigo).isEmpty()) {
            new Toast("Código não encontrado");
            return null;

        } else {
            Produto p = produtoDAO.findByCodigo(codigo).get(0);
            if (p.getProdutoTamanho(tamanho) == null) {
                new Toast("Este produto não tem tamanho compatível cadastrado");
                return null;
            }

            return p;

        }
    }

    private void pesquisarProduto(int produtoNumero) {
        ProdutoPesquisaView produtoPesquisaView = new ProdutoPesquisaView();

        Produto produto = produtoPesquisaView.getProduto();
        if (produto != null) {
            if (produto.getProdutoTamanho(tamanho) == null) {
                new Toast("Este produto não tem tamanho compatível cadastrado");
            } else {
                produtos.set(produtoNumero - 1, produto);
                switch (produtoNumero) {
                    case 1:
                        carregarProduto1();
                        break;
                    case 2:
                        carregarProduto2();
                        break;
                    case 3:
                        carregarProduto3();
                        break;
                    case 4:
                        carregarProduto4();
                        break;
                }

            }
        }
    }

    private void calcularValor() {
        List<BigDecimal> valores = new ArrayList<>();
        /*
        valores.add(produto1 != null ? produto1.getProdutoTamanho(tamanho).getValorVenda() : BigDecimal.ZERO);
        valores.add(produto2 != null ? produto2.getProdutoTamanho(tamanho).getValorVenda() : BigDecimal.ZERO);
        valores.add(produto3 != null ? produto3.getProdutoTamanho(tamanho).getValorVenda() : BigDecimal.ZERO);
        valores.add(produto4 != null ? produto4.getProdutoTamanho(tamanho).getValorVenda() : BigDecimal.ZERO);
         */
        produtos.forEach((p) -> {
            if (p != null) {
                valores.add(p.getProdutoTamanho(tamanho).getValorVenda());
            }
        });

        Optional<BigDecimal> max = valores.stream().max(Comparator.naturalOrder());

        valorCobrado = max.isPresent() ? max.get() : BigDecimal.ZERO;

        txtValorCobrado.setText(Decimal.toString(valorCobrado));
    }

    private void confirmar() {
        //verificar se contém itens não nulos
        long naoNulos = produtos.stream().filter((p) -> p != null).count();

        //validar
        if (naoNulos < 2) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem produtos para montar este item", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtProduto1Codigo.requestFocus();

        } else {

            //criar movimento físico primário
            UnidadeComercial unidadeComercialVenda = produtos.get(0).getUnidadeComercialVenda();

            BigDecimal quantidade = Decimal.fromString(txtQuantidade.getText());

            MovimentoFisico movimentoFisico = new MovimentoFisico(null,
                    "*", //código
                    produtos.get(0).getCategoria() + " " + tamanho.getNome(), //descrição
                    ProdutoTipo.PRODUTO, //tipo
                    BigDecimal.ZERO, //entrada
                    quantidade, //saída
                    valorCobrado, //valor
                    BigDecimal.ZERO, //desconto
                    unidadeComercialVenda,
                    MovimentoFisicoTipo.VENDA,
                    null);
            
            movimentoFisico.setTamanho(tamanho);

            if (documento.getVendaTipo().equals(VendaTipo.VENDA)
                    || documento.getVendaTipo().equals(VendaTipo.ORDEM_DE_SERVICO)
                    || documento.getVendaTipo().equals(VendaTipo.COMANDA)) {

                //adicionar parametro de sistema
                movimentoFisico.setDataSaida(LocalDateTime.now());
                //
            }

            movimentoFisico = movimentoFisicoDAO.save(movimentoFisico);
            documento.addMovimentoFisico(movimentoFisico);

            documento = vendaDAO.save(documento);

            //remover nulos
            produtos.removeAll(Collections.singleton(null));
            
            BigDecimal quantidadeItem = quantidade.divide(new BigDecimal(produtos.size()), 3, RoundingMode.HALF_UP);

            //criar mfs que montam o item
            for (Produto p : produtos) {
                MovimentoFisico montagemItem = new MovimentoFisico(p,
                        p.getCodigo(),
                        p.getNome(),
                        p.getProdutoTipo(),
                        BigDecimal.ZERO,
                        quantidadeItem,
                        valorCobrado,
                        BigDecimal.ZERO,
                        p.getUnidadeComercialVenda(),
                        MovimentoFisicoTipo.VENDA,
                        null);
                
                montagemItem.setTamanho(tamanho);
                
                montagemItem.setDataSaida(movimentoFisico.getDataSaida());
                montagemItem.setVenda(documento);

                movimentoFisico.addMontagemItem(montagemItem); //2019-11-25
                montagemItem = movimentoFisicoDAO.save(montagemItem);
                

                movimentoFisico = movimentoFisicoDAO.save(movimentoFisico);
                documento.addMovimentoFisico(movimentoFisico);

                documento = vendaDAO.save(documento);
            }
            
            this.movimentoFisico = movimentoFisico;

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
        txtProduto1Codigo = new javax.swing.JTextField();
        txtProduto1Descricao = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtProduto2Descricao = new javax.swing.JTextField();
        txtProduto2Codigo = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtProduto3Descricao = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtProduto3Codigo = new javax.swing.JTextField();
        txtProduto4Descricao = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtProduto4Codigo = new javax.swing.JTextField();
        txtProduto1Valor = new javax.swing.JTextField();
        txtProduto2Valor = new javax.swing.JTextField();
        txtProduto3Valor = new javax.swing.JTextField();
        txtProduto4Valor = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtValorCobrado = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtQuantidade = new javax.swing.JFormattedTextField();
        jLabel17 = new javax.swing.JLabel();
        txtMontar = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Montar Item");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

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

        txtProduto1Codigo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtProduto1Codigo.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtProduto1Codigo.setToolTipText("F9 PARA PESQUISAR");
        txtProduto1Codigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtProduto1CodigoKeyReleased(evt);
            }
        });

        txtProduto1Descricao.setEditable(false);
        txtProduto1Descricao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtProduto1Descricao.setFocusable(false);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setText("1");

        txtProduto2Descricao.setEditable(false);
        txtProduto2Descricao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtProduto2Descricao.setFocusable(false);

        txtProduto2Codigo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtProduto2Codigo.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtProduto2Codigo.setToolTipText("F9 PARA PESQUISAR");
        txtProduto2Codigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtProduto2CodigoKeyReleased(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setText("2");

        txtProduto3Descricao.setEditable(false);
        txtProduto3Descricao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtProduto3Descricao.setFocusable(false);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setText("3");

        txtProduto3Codigo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtProduto3Codigo.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtProduto3Codigo.setToolTipText("F9 PARA PESQUISAR");
        txtProduto3Codigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtProduto3CodigoKeyReleased(evt);
            }
        });

        txtProduto4Descricao.setEditable(false);
        txtProduto4Descricao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtProduto4Descricao.setFocusable(false);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setText("4");

        txtProduto4Codigo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtProduto4Codigo.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtProduto4Codigo.setToolTipText("F9 PARA PESQUISAR");
        txtProduto4Codigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtProduto4CodigoKeyReleased(evt);
            }
        });

        txtProduto1Valor.setEditable(false);
        txtProduto1Valor.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtProduto1Valor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtProduto1Valor.setFocusable(false);

        txtProduto2Valor.setEditable(false);
        txtProduto2Valor.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtProduto2Valor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtProduto2Valor.setFocusable(false);

        txtProduto3Valor.setEditable(false);
        txtProduto3Valor.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtProduto3Valor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtProduto3Valor.setFocusable(false);

        txtProduto4Valor.setEditable(false);
        txtProduto4Valor.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtProduto4Valor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtProduto4Valor.setFocusable(false);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel12.setText("Valor cobrado");

        txtValorCobrado.setEditable(false);
        txtValorCobrado.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtValorCobrado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValorCobrado.setFocusable(false);
        txtValorCobrado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorCobradoKeyReleased(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel13.setForeground(java.awt.Color.blue);
        jLabel13.setText("F9 para pesquisar produto | DEL para excluir");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setText("Código");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setText("Descrição");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel16.setText("Valor");

        txtQuantidade.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQuantidade.setText("0,000");
        txtQuantidade.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtQuantidade.setName("decimal(3)"); // NOI18N
        txtQuantidade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtQuantidadeKeyReleased(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel17.setText("Quantidade");

        txtMontar.setEditable(false);
        txtMontar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtMontar.setFocusable(false);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel18.setText("Montar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtProduto2Codigo, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtProduto2Descricao))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel10)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtProduto3Codigo, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtProduto3Descricao))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtProduto4Codigo, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtProduto4Descricao))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel17)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel12))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtProduto1Codigo, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel14))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel15)
                                            .addComponent(txtProduto1Descricao, javax.swing.GroupLayout.PREFERRED_SIZE, 478, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtProduto1Valor, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtProduto2Valor, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtProduto3Valor, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtProduto4Valor, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtValorCobrado, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addGap(18, 18, 18)
                        .addComponent(txtMontar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtMontar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtProduto1Codigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProduto1Descricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProduto1Valor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtProduto2Codigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProduto2Descricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProduto2Valor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtProduto3Codigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProduto3Descricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProduto3Valor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtProduto4Codigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProduto4Descricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProduto4Valor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtValorCobrado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar)
                    .addComponent(btnOk)
                    .addComponent(jLabel13))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    }//GEN-LAST:event_formWindowClosing

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        confirmar();
    }//GEN-LAST:event_btnOkActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void txtProduto1CodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProduto1CodigoKeyReleased
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                if (!txtProduto1Codigo.getText().trim().equals("")) {
                    produtos.set(0, findProduto(txtProduto1Codigo.getText()));
                    carregarProduto1();
                }
                txtProduto2Codigo.requestFocus();
                break;
            case KeyEvent.VK_DOWN:
                txtProduto2Codigo.requestFocus();
                break;
            case KeyEvent.VK_DELETE:
                removerProduto1();
                break;
            case KeyEvent.VK_F9:
                pesquisarProduto(1);
        }
    }//GEN-LAST:event_txtProduto1CodigoKeyReleased

    private void txtProduto2CodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProduto2CodigoKeyReleased
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                if (!txtProduto2Codigo.getText().trim().equals("")) {
                    produtos.set(1, findProduto(txtProduto2Codigo.getText()));
                    carregarProduto2();
                }
                txtProduto3Codigo.requestFocus();
                break;
            case KeyEvent.VK_DOWN:
                txtProduto3Codigo.requestFocus();
                break;
            case KeyEvent.VK_UP:
                txtProduto1Codigo.requestFocus();
                break;
            case KeyEvent.VK_DELETE:
                removerProduto2();
                break;
            case KeyEvent.VK_F9:
                pesquisarProduto(2);
        }
    }//GEN-LAST:event_txtProduto2CodigoKeyReleased

    private void txtProduto3CodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProduto3CodigoKeyReleased
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                if (!txtProduto3Codigo.getText().trim().equals("")) {
                    produtos.set(2, findProduto(txtProduto3Codigo.getText()));
                    carregarProduto3();
                }
                txtProduto4Codigo.requestFocus();
                break;
            case KeyEvent.VK_DOWN:
                txtProduto4Codigo.requestFocus();
                break;
            case KeyEvent.VK_UP:
                txtProduto2Codigo.requestFocus();
                break;
            case KeyEvent.VK_DELETE:
                removerProduto3();
                break;
            case KeyEvent.VK_F9:
                pesquisarProduto(3);
        }
    }//GEN-LAST:event_txtProduto3CodigoKeyReleased

    private void txtProduto4CodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProduto4CodigoKeyReleased
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                if (!txtProduto4Codigo.getText().trim().equals("")) {
                    produtos.set(3, findProduto(txtProduto4Codigo.getText()));
                    carregarProduto4();
                }
                txtQuantidade.requestFocus();
                break;
            case KeyEvent.VK_UP:
                txtProduto3Codigo.requestFocus();
                break;
            case KeyEvent.VK_DELETE:
                removerProduto4();
                break;
            case KeyEvent.VK_F9:
                pesquisarProduto(4);
        }
    }//GEN-LAST:event_txtProduto4CodigoKeyReleased

    private void txtValorCobradoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorCobradoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValorCobradoKeyReleased

    private void txtQuantidadeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeKeyReleased
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                confirmar();
                break;
            case KeyEvent.VK_UP:
                txtProduto4Codigo.requestFocus();
                break;
        }
    }//GEN-LAST:event_txtQuantidadeKeyReleased

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
            java.util.logging.Logger.getLogger(VendaMontarItemView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VendaMontarItemView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VendaMontarItemView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VendaMontarItemView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                VendaMontarItemView dialog = new VendaMontarItemView(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField txtMontar;
    private javax.swing.JTextField txtProduto1Codigo;
    private javax.swing.JTextField txtProduto1Descricao;
    private javax.swing.JTextField txtProduto1Valor;
    private javax.swing.JTextField txtProduto2Codigo;
    private javax.swing.JTextField txtProduto2Descricao;
    private javax.swing.JTextField txtProduto2Valor;
    private javax.swing.JTextField txtProduto3Codigo;
    private javax.swing.JTextField txtProduto3Descricao;
    private javax.swing.JTextField txtProduto3Valor;
    private javax.swing.JTextField txtProduto4Codigo;
    private javax.swing.JTextField txtProduto4Descricao;
    private javax.swing.JTextField txtProduto4Valor;
    private javax.swing.JFormattedTextField txtQuantidade;
    private javax.swing.JTextField txtValorCobrado;
    // End of variables declaration//GEN-END:variables
}
