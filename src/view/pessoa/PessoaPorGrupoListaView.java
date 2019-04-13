/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.pessoa;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.bean.fiscal.MeioDePagamento;
import model.bean.principal.pessoa.Grupo;
import model.bean.principal.MovimentoFisico;
import model.bean.principal.MovimentoFisicoTipo;
import model.bean.principal.Parcela;
import model.bean.principal.pessoa.Pessoa;
import model.bean.principal.catalogo.Produto;
import model.bean.principal.Venda;
import model.bean.principal.VendaTipo;
import model.bean.principal.pessoa.Perfil;
import model.bean.principal.pessoa.PerfilItem;
import model.bean.temp.PessoaPorGrupo;
import model.dao.principal.pessoa.GrupoDAO;
import model.dao.principal.pessoa.PessoaDAO;
import model.dao.principal.VendaDAO;
import model.jtable.PessoaPorGrupoJTableModel;
import static ouroboros.Constants.*;
import static ouroboros.Ouroboros.MAIN_VIEW;
import printing.Carne;
import util.JSwing;
import view.Toast;
import view.venda.VendaView;

/**
 *
 * @author ivand
 */
public class PessoaPorGrupoListaView extends javax.swing.JInternalFrame {

    private static PessoaPorGrupoListaView singleInstance = null;
    PessoaPorGrupoJTableModel pessoaPorGrupoJTableModel = new PessoaPorGrupoJTableModel();
    PessoaDAO clienteDAO = new PessoaDAO();

    List<PessoaPorGrupo> pessoasPorGrupo;

    public static PessoaPorGrupoListaView getSingleInstance() {
        /*if(!USUARIO.autorizarAcesso(Recurso.PESSOAS)) {
            return null;
        }*/

        if (singleInstance == null) {
            singleInstance = new PessoaPorGrupoListaView();
        }
        return singleInstance;
    }

    private PessoaPorGrupoListaView() {
        initComponents();
        JSwing.startComponentsBehavior(this);

        formatarTabela();

        carregarGrupos();

        carregarTabela();

    }

    private void catchClick() {
        int indices[] = tblClientes.getSelectedRows();

        ArrayList<Integer> ids = new ArrayList<>();
        for (int index : indices) {
            ids.add(pessoaPorGrupoJTableModel.getRow(index).getPessoa().getId());
        }
        System.out.println("index: " + tblClientes.getSelectedRow());
    }

    private void carregarGrupos() {
        cboGrupo.removeAllItems();
        List<Grupo> grupos = new GrupoDAO().findAll();
        
        for (Grupo g : grupos) {
            cboGrupo.addItem(g);
        }

    }

    private void formatarTabela() {
        tblClientes.setModel(pessoaPorGrupoJTableModel);

        tblClientes.setRowHeight(24);
        tblClientes.setIntercellSpacing(new Dimension(10, 10));

        tblClientes.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblClientes.getColumnModel().getColumn(0).setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

        tblClientes.getColumn("Nome").setPreferredWidth(300);

        tblClientes.getColumn("Dia de Vencimento").setPreferredWidth(100);

        tblClientes.getColumn("Observação").setPreferredWidth(300);

        tblClientes.getColumn("Lançamento").setPreferredWidth(130);
        tblClientes.getColumn("Lançamento").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblClientes.getColumn("Último Vencimento").setPreferredWidth(130);
        tblClientes.getColumn("Último Vencimento").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblClientes.getColumn("Último Valor").setPreferredWidth(130);
        tblClientes.getColumn("Último Valor").setCellRenderer(CELL_RENDERER_ALIGN_RIGHT);

    }

    private void carregarTabela() {
        long start = System.currentTimeMillis();

        String nome = txtBuscaRapida.getText();
        Grupo grupo = (Grupo) cboGrupo.getSelectedItem();

        pessoasPorGrupo = clienteDAO.findByGrupo(nome, grupo);

        pessoaPorGrupoJTableModel.clear();
        pessoaPorGrupoJTableModel.addList(pessoasPorGrupo);

        long elapsed = System.currentTimeMillis() - start;
        lblMensagem.setText("Consulta realizada em " + elapsed + "ms");

        lblRegistrosExibidos.setText(String.valueOf(pessoasPorGrupo.size()));
    }

    private boolean validar() {
        boolean valido = true;
        if (tblClientes.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione um registro.", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {
            PessoaPorGrupo pessoaPorGrupo = pessoaPorGrupoJTableModel.getRow(tblClientes.getSelectedRow());

            //N Grupos
            if (pessoaPorGrupo.getPessoa().getPerfis().size() > 1) {
                int resposta = JOptionPane.showConfirmDialog(MAIN_VIEW, "Esta pessoa está em mais de um grupo. Verifique se já não foi gerado documento para ela. Deseja continuar?", "Atenção", JOptionPane.WARNING_MESSAGE);
                if (resposta == JOptionPane.OK_OPTION) {
                    valido = valido && true;
                } else {
                    valido = false;
                }
            }
            
            //vencimento igual
            LocalDate dataBase = LocalDate.now();
            Integer diaVencimento = pessoaPorGrupo.getPerfil().getDiaVencimento();
            LocalDate vencimento;
            if (diaVencimento == 0) { //apenas somar 1 mês
                vencimento = dataBase.plusMonths(1);

            } else { //usar dia programado
                //avançar o mês se já houver passado o dia programado dentro do mês corrente
                if (diaVencimento < dataBase.getDayOfMonth()) {
                    dataBase = dataBase.plusMonths(1);
                }

                //ajustar último dia do mês: 29, 30, 31
                diaVencimento = dataBase.lengthOfMonth() > diaVencimento ? diaVencimento : dataBase.lengthOfMonth();
                vencimento = LocalDate.of(dataBase.getYear(), dataBase.getMonth(), diaVencimento);
            }

            if(vencimento.compareTo(pessoaPorGrupo.getParcela().getVencimento().toLocalDate()) == 0){
                int resposta = JOptionPane.showConfirmDialog(MAIN_VIEW, "Já existe vencimento para a data programada. Deseja continuar?", "Atenção", JOptionPane.WARNING_MESSAGE);
                if (resposta == JOptionPane.OK_OPTION) {
                    valido = valido && true;
                } else {
                    valido = false;
                }
            }
            
        }
        return valido;
    }

    private void gerarDocumento() {
        PessoaPorGrupo pessoaPorGrupo = pessoaPorGrupoJTableModel.getRow(tblClientes.getSelectedRow());
        
        Venda documento = new Venda(VendaTipo.VENDA);
        documento.setCliente(pessoaPorGrupo.getPessoa());
        documento.setObservacao(pessoaPorGrupo.getPerfil().getObservacao());

        //MovimentoFisico - gerar itens de todos os perfis da pessoa
        for (Perfil perfil : pessoaPorGrupo.getPessoa().getPerfis()) {
            for (PerfilItem perfilItem : perfil.getPerfilItens()) {
                Produto produto = perfilItem.getGrupoItem().getProduto();

                MovimentoFisico mf = new MovimentoFisico(produto,
                        produto.getCodigo(),
                        BigDecimal.ZERO,
                        BigDecimal.ONE,
                        produto.getValorVenda(),
                        perfilItem.getDescontoPercentual(),
                        produto.getUnidadeComercialVenda(),
                        MovimentoFisicoTipo.VENDA, null);
                documento.addMovimentoFisico(mf);
            }
        }

        //Parcela
        LocalDate dataBase = LocalDate.now();
        Integer diaVencimento = pessoaPorGrupo.getPerfil().getDiaVencimento();
        LocalDate vencimento;
        if (diaVencimento == 0) { //apenas somar 1 mês
            vencimento = dataBase.plusMonths(1);

        } else { //usar dia programado
            //avançar o mês se já houver passado o dia programado dentro do mês corrente
            if (diaVencimento < dataBase.getDayOfMonth()) {
                dataBase = dataBase.plusMonths(1);
            }

            //ajustar último dia do mês: 29, 30, 31
            diaVencimento = dataBase.lengthOfMonth() > diaVencimento ? diaVencimento : dataBase.lengthOfMonth();
            vencimento = LocalDate.of(dataBase.getYear(), dataBase.getMonth(), diaVencimento);
        }

        BigDecimal valor = documento.getTotal();

        Parcela parcela = new Parcela(Date.valueOf(vencimento), valor, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, MeioDePagamento.CREDITO_LOJA);
        parcela.setNumero(1);
        documento.addParcela(parcela);

        //documento.setObservacao(vencimento.toString());
        documento = new VendaDAO().save(documento);
        
        carregarTabela();

        MAIN_VIEW.addView(VendaView.getInstance(documento));
        
        
    }

    private void abrirDocumento() {
        if (tblClientes.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione um registro.", "Atenção", JOptionPane.WARNING_MESSAGE);

        } else {
            Parcela parcela = pessoaPorGrupoJTableModel.getRow(tblClientes.getSelectedRow()).getParcela();
            if (parcela == null) {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Não há documento para o registro selecionado.", "Atenção", JOptionPane.WARNING_MESSAGE);

            } else {
                MAIN_VIEW.addView(VendaView.getInstance(parcela.getVenda()));

            }
        }
    }
    
    private void gerarCarne() {
        List<Parcela> parcelas = new ArrayList<>();
        for(int rowIndex : tblClientes.getSelectedRows()) {
            PessoaPorGrupo pessoaPorGrupo = pessoaPorGrupoJTableModel.getRow(rowIndex);
            if(pessoaPorGrupo.getParcela() != null) {
            
                parcelas.addAll(pessoaPorGrupo.getParcela().getVenda().getParcelas());
            }
        }
        
        if(parcelas.isEmpty()) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Não existem parcelas para gerar carnê. Selecione pessoas com parcelas geradas", "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            new Toast("Gerando carnê...");
            Carne.gerarCarne(parcelas);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        lblMensagem = new javax.swing.JLabel();
        lblRegistrosExibidos = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        txtBuscaRapida = new javax.swing.JTextField();
        btnFiltrar = new javax.swing.JButton();
        btnRemoverFiltro = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cboGrupo = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnNovo = new javax.swing.JButton();
        btnNovo1 = new javax.swing.JButton();
        btnCarne = new javax.swing.JButton();

        setClosable(true);
        setTitle("Pessoas por Grupo");
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameActivated(evt);
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

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
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
        tblClientes.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblClientes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblClientesFocusGained(evt);
            }
        });
        tblClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblClientesMouseClicked(evt);
            }
        });
        tblClientes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblClientesKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblClientes);

        lblMensagem.setText("...");

        lblRegistrosExibidos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRegistrosExibidos.setText("0");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtBuscaRapida.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtBuscaRapida.setToolTipText("");
        txtBuscaRapida.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscaRapidaKeyReleased(evt);
            }
        });

        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnRemoverFiltro.setText("Remover Filtro");
        btnRemoverFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverFiltroActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Busca rápida");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Grupo");

        cboGrupo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cboGrupo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboGrupoFocusGained(evt);
            }
        });
        cboGrupo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboGrupoActionPerformed(evt);
            }
        });
        cboGrupo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cboGrupoPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(cboGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtBuscaRapida, javax.swing.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnRemoverFiltro)
                        .addGap(18, 18, 18)
                        .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(cboGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtBuscaRapida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFiltrar)
                    .addComponent(btnRemoverFiltro))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setText("Registros exibidos:");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnNovo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/wand.png"))); // NOI18N
        btnNovo.setText("Gerar documento");
        btnNovo.setContentAreaFilled(false);
        btnNovo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNovo.setIconTextGap(10);
        btnNovo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoActionPerformed(evt);
            }
        });

        btnNovo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/folder_page_white.png"))); // NOI18N
        btnNovo1.setText("Abrir último documento");
        btnNovo1.setContentAreaFilled(false);
        btnNovo1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNovo1.setIconTextGap(10);
        btnNovo1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNovo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovo1ActionPerformed(evt);
            }
        });

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
                .addComponent(btnNovo)
                .addGap(18, 18, 18)
                .addComponent(btnNovo1)
                .addGap(18, 18, 18)
                .addComponent(btnCarne, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnCarne, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnNovo1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 11, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(lblRegistrosExibidos, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(lblMensagem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblMensagem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRegistrosExibidos)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        txtBuscaRapida.requestFocus();
    }//GEN-LAST:event_formComponentShown

    private void btnNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoActionPerformed
        if(validar()) {
            gerarDocumento();
        }
    }//GEN-LAST:event_btnNovoActionPerformed

    private void tblClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClientesMouseClicked
        catchClick();
        if (evt.getClickCount() == 2) {
            Pessoa pessoa = pessoaPorGrupoJTableModel.getRow(tblClientes.getSelectedRow()).getPessoa();
            MAIN_VIEW.addView(PessoaContainerView.getInstance(pessoa));
        }

    }//GEN-LAST:event_tblClientesMouseClicked

    private void tblClientesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblClientesKeyReleased
        catchClick();
    }//GEN-LAST:event_tblClientesKeyReleased

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        MAIN_VIEW.removeTab(this.getName());
    }//GEN-LAST:event_formInternalFrameClosing

    private void txtBuscaRapidaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscaRapidaKeyReleased
        carregarTabela();
    }//GEN-LAST:event_txtBuscaRapidaKeyReleased

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnRemoverFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverFiltroActionPerformed
        txtBuscaRapida.setText("");
        carregarTabela();
        txtBuscaRapida.requestFocus();
    }//GEN-LAST:event_btnRemoverFiltroActionPerformed

    private void tblClientesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblClientesFocusGained
        //tableClientesUpdateRow();

    }//GEN-LAST:event_tblClientesFocusGained

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        System.out.println("focus");
        //tableClientesUpdateRow();
    }//GEN-LAST:event_formFocusGained

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated

    }//GEN-LAST:event_formInternalFrameActivated

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        singleInstance = null;
    }//GEN-LAST:event_formInternalFrameClosed

    private void cboGrupoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cboGrupoPropertyChange

    }//GEN-LAST:event_cboGrupoPropertyChange

    private void cboGrupoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboGrupoActionPerformed
        carregarTabela();
    }//GEN-LAST:event_cboGrupoActionPerformed

    private void btnNovo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovo1ActionPerformed
        abrirDocumento();
    }//GEN-LAST:event_btnNovo1ActionPerformed

    private void btnCarneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCarneActionPerformed
        gerarCarne();
    }//GEN-LAST:event_btnCarneActionPerformed

    private void cboGrupoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboGrupoFocusGained
        carregarGrupos();
    }//GEN-LAST:event_cboGrupoFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCarne;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton btnNovo1;
    private javax.swing.JButton btnRemoverFiltro;
    private javax.swing.JComboBox<Object> cboGrupo;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMensagem;
    private javax.swing.JLabel lblRegistrosExibidos;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtBuscaRapida;
    // End of variables declaration//GEN-END:variables
}
