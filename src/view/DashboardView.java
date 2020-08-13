/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import model.jtable.agenda.TarefaJTableModel;
import model.mysql.bean.principal.Funcionario;
import model.mysql.bean.principal.agenda.Tarefa;
import model.mysql.dao.principal.ConstanteDAO;
import model.mysql.dao.principal.FuncionarioDAO;
import model.mysql.dao.principal.agenda.TarefaDAO;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import static ouroboros.Ouroboros.MENU_MIN_WIDTH;
import static ouroboros.Ouroboros.SCREEN_HEIGHT;
import static ouroboros.Ouroboros.SCREEN_WIDTH;
import util.DateTime;
import view.agenda.TarefaCadastroView;
import view.sistema.BackupView;
import view.sistema.ConfguracaoSistema;

/**
 *
 * @author ivand
 */
public class DashboardView extends javax.swing.JInternalFrame {
    final TarefaDAO tarefaDAO = new TarefaDAO();
    final TarefaJTableModel tarefaJTableModel = new TarefaJTableModel();
    
    List<Tarefa> tarefas = new ArrayList<>();
    
    ImageIcon imagemLogo = new ImageIcon();
    JLabel logo = new JLabel();
    
    Optional<Boolean> tarefaConcluida;
    Funcionario funcionario;
    
    /**
     * Creates new form MainMenuView
     */
    public DashboardView() {
        //set apearence
        ((BasicInternalFrameUI)this.getUI()).setNorthPane(null);
        this.setBorder(null);
        this.setBounds(Ouroboros.MENU_MAX_WIDTH, 0, Ouroboros.SCREEN_WIDTH - MENU_MIN_WIDTH, SCREEN_HEIGHT);
        
        initComponents();
        
        
        URL iconURL = getClass().getResource("/res/img/logo.png");
        imagemLogo = new ImageIcon(iconURL);
        logo = new JLabel(imagemLogo);
        add(logo);
        
        formatarTabelaAgenda();
        //carregarTabelaAgenda();
        
        carregarFuncionario();
        cboTarefaConcluida.setSelectedIndex(2);
        
        
        
        carregarDados();
        
        jScrollPane1.setOpaque(false);
        jScrollPane1.getViewport().setOpaque(false);
        //jScrollPane1.setBackground(new Color(0f, 0f, 0f, 0.5f));
        
        //jPanel1.setOpaque(false);
        pnlAgendaFiltro.setOpaque(false);
        pnlAgenda.setBackground(new Color(0f, 0f, 0f, 0.05f));
        
        
        ajustarFundo();
    }
    
    private void ajustarFundo() {
        logo.setSize(imagemLogo.getIconWidth(), imagemLogo.getIconHeight());

        //int x = MENU_MAX_WIDTH + (SCREEN_WIDTH - MENU_MAX_WIDTH) / 2 - imagemLogo.getIconWidth() / 2;
        
        int x = (SCREEN_WIDTH - MENU_MIN_WIDTH) / 2 - imagemLogo.getIconWidth() / 2;
        
        int y = SCREEN_HEIGHT / 2 - imagemLogo.getIconHeight() / 2;
        logo.setLocation(x, y);
        
        this.repaint();
    }
    
    private void carregarFuncionario() {
        Funcionario todos = new Funcionario();
        todos.setId(0);
        todos.setNome("Todos");
        cboFuncionario.addItem(todos);
        
        Funcionario nenhum = new Funcionario();
        nenhum.setId(-1);
        nenhum.setNome("Nenhum");
        cboFuncionario.addItem(nenhum);
        
        for (Funcionario f : new FuncionarioDAO().findAll(false)) {
            cboFuncionario.addItem(f);
        }
    }
    
    private void formatarTabelaAgenda() {
        tblAgenda.setModel(tarefaJTableModel);

        tblAgenda.setRowHeight(30);
        tblAgenda.setIntercellSpacing(new Dimension(10, 10));

        tblAgenda.getColumn("Data").setPreferredWidth(80);
        tblAgenda.getColumn("Data").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblAgenda.getColumn("Hora").setPreferredWidth(40);
        tblAgenda.getColumn("Hora").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblAgenda.getColumn("Descrição").setPreferredWidth(540);

        tblAgenda.getColumn("Funcionário").setPreferredWidth(100);

        tblAgenda.getColumn("Cliente/Fornecedor").setPreferredWidth(240);

    }

    private void carregarTabelaAgenda() {
        tarefaConcluida = cboTarefaConcluida.getSelectedIndex() == 0 ? Optional.empty()
                : (cboTarefaConcluida.getSelectedIndex() == 1 ? Optional.of(true) : Optional.of(false));
        
        funcionario = (Funcionario) cboFuncionario.getSelectedItem();
        
        tarefas = tarefaDAO.findByCriteria(null, tarefaConcluida, funcionario, false);

        tarefaJTableModel.clear();
        tarefaJTableModel.addList(tarefas);

    }
    
    private void editarTarefa() {
        int rowIndex = tblAgenda.getSelectedRow();
        Tarefa tarefa = tarefaJTableModel.getRow(rowIndex);
        new TarefaCadastroView(tarefa);
        tarefaJTableModel.fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    private void excluirTarefa() {
        int rowIndex = tblAgenda.getSelectedRow();
        if (rowIndex < 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione uma tarefa", "Atenção", JOptionPane.WARNING_MESSAGE);
            
        } else {
            if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(MAIN_VIEW, "Excluir a tarefa selecionada?", "Atenção", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE)) {
                Tarefa tarefa = tarefaJTableModel.getRow(rowIndex);
                tarefa.setExclusao(LocalDateTime.now());
                tarefaDAO.save(tarefa);
                carregarTabelaAgenda();
                repaint();
            }
        }
    }
    
    private void concluirTarefa() {
        int rowIndex = tblAgenda.getSelectedRow();
        if (rowIndex < 0) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Selecione uma tarefa", "Atenção", JOptionPane.WARNING_MESSAGE);
            
        } else {
            if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(MAIN_VIEW, "Concluir a tarefa selecionada?", "Atenção", JOptionPane.OK_CANCEL_OPTION)) {
                Tarefa tarefa = tarefaJTableModel.getRow(rowIndex);
                tarefa.setConclusao(LocalDateTime.now());
                tarefaDAO.save(tarefa);
                carregarTabelaAgenda();
                repaint();
            }
        }
    }
    
    private void novaTarefa(){ 
        new TarefaCadastroView(new Tarefa());
        carregarTabelaAgenda();
    }
    
    private void carregarDados() {
        LocalDateTime backup = DateTime.fromStringLDT(ConstanteDAO.getValor("BACKUP_DATA_HORA"));
        txtBackup.setText(DateTime.toString(backup));
        
        if (backup == null) {
            txtBackup.setForeground(Color.RED);
            txtBackup.setText("Sem backup!");
            
        } else {
            long backupDias = DateTime.diasDepoisDeHoje(backup.toLocalDate());
            System.out.println("backupDias " + backupDias);
            if (backupDias < -3l) { //3 dias atrasado
                txtBackup.setForeground(Color.RED);
                JOptionPane.showMessageDialog(MAIN_VIEW, "Não há registro de backup há " + Math.abs(backupDias) + " dias!", "Atenção", JOptionPane.ERROR_MESSAGE );
            } else {
                txtBackup.setForeground(Color.BLACK);
            }
        }
        
        
        //txtCertificado.setText("teste");
    }
    
    private void backup() {
        new BackupView();
        carregarDados();
    }
    
    
    private void configuracao() {
        MAIN_VIEW.addView(ConfguracaoSistema.getSingleInstance());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlAgenda = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAgenda = new javax.swing.JTable();
        btnAgendaNovo = new javax.swing.JButton();
        btnAgendaConcluido = new javax.swing.JButton();
        btnAgendaAtualizar = new javax.swing.JButton();
        pnlAgendaFiltro = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        cboTarefaConcluida = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        cboFuncionario = new javax.swing.JComboBox<>();
        btnAgendaExcluir = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btnBackup = new javax.swing.JButton();
        txtBackup = new javax.swing.JTextField();
        btnConfiguracao = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                formMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                formMouseExited(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        pnlAgenda.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel35.setBackground(new java.awt.Color(122, 138, 153));
        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel35.setForeground(java.awt.Color.white);
        jLabel35.setText("Agenda");
        jLabel35.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        jLabel35.setOpaque(true);

        jScrollPane1.setOpaque(false);

        tblAgenda.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tblAgenda.setIntercellSpacing(new java.awt.Dimension(10, 10));
        tblAgenda.setRowHeight(24);
        tblAgenda.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblAgendaFocusGained(evt);
            }
        });
        tblAgenda.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblAgendaMouseClicked(evt);
            }
        });
        tblAgenda.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tblAgendaPropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(tblAgenda);

        btnAgendaNovo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-add-20.png"))); // NOI18N
        btnAgendaNovo.setText("Novo");
        btnAgendaNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgendaNovoActionPerformed(evt);
            }
        });

        btnAgendaConcluido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-checkmark-20.png"))); // NOI18N
        btnAgendaConcluido.setText("Concluir");
        btnAgendaConcluido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgendaConcluidoActionPerformed(evt);
            }
        });

        btnAgendaAtualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-sync-20.png"))); // NOI18N
        btnAgendaAtualizar.setText("Atualizar");
        btnAgendaAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgendaAtualizarActionPerformed(evt);
            }
        });

        pnlAgendaFiltro.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Exibir Tarefas Concluídas");

        cboTarefaConcluida.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboTarefaConcluida.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "S/N", "Sim", "Não" }));
        cboTarefaConcluida.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboTarefaConcluidaItemStateChanged(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Funcionário");

        cboFuncionario.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboFuncionario.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboFuncionarioItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout pnlAgendaFiltroLayout = new javax.swing.GroupLayout(pnlAgendaFiltro);
        pnlAgendaFiltro.setLayout(pnlAgendaFiltroLayout);
        pnlAgendaFiltroLayout.setHorizontalGroup(
            pnlAgendaFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAgendaFiltroLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(cboTarefaConcluida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(cboFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAgendaFiltroLayout.setVerticalGroup(
            pnlAgendaFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAgendaFiltroLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAgendaFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAgendaFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboFuncionario)
                        .addComponent(jLabel6))
                    .addGroup(pnlAgendaFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboTarefaConcluida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnAgendaExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-close-button-20.png"))); // NOI18N
        btnAgendaExcluir.setText("Excluir");
        btnAgendaExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgendaExcluirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlAgendaLayout = new javax.swing.GroupLayout(pnlAgenda);
        pnlAgenda.setLayout(pnlAgendaLayout);
        pnlAgendaLayout.setHorizontalGroup(
            pnlAgendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, 961, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAgendaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAgendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAgendaLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnAgendaExcluir)
                        .addGap(18, 18, 18)
                        .addComponent(btnAgendaAtualizar)
                        .addGap(18, 18, 18)
                        .addComponent(btnAgendaConcluido)
                        .addGap(18, 18, 18)
                        .addComponent(btnAgendaNovo)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAgendaLayout.createSequentialGroup()
                        .addComponent(pnlAgendaFiltro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAgendaLayout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addGap(10, 10, 10))))
        );
        pnlAgendaLayout.setVerticalGroup(
            pnlAgendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAgendaLayout.createSequentialGroup()
                .addComponent(jLabel35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlAgendaFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlAgendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAgendaNovo)
                    .addComponent(btnAgendaConcluido)
                    .addComponent(btnAgendaAtualizar)
                    .addComponent(btnAgendaExcluir))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnBackup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-save-20.png"))); // NOI18N
        btnBackup.setText("Backup");
        btnBackup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackupActionPerformed(evt);
            }
        });

        txtBackup.setEditable(false);
        txtBackup.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtBackup.setText("...");

        btnConfiguracao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-config-20.png"))); // NOI18N
        btnConfiguracao.setText("Configuração");
        btnConfiguracao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfiguracaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnBackup)
                .addGap(18, 18, 18)
                .addComponent(txtBackup, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnConfiguracao)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtBackup)
                        .addComponent(btnConfiguracao, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(btnBackup, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTextField1.setEditable(false);
        jTextField1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jTextField1.setForeground(java.awt.Color.red);
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField1.setText("Nosso número 3813.2888 será desativado. Utilize o 3913.5762");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlAgenda, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlAgenda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseEntered
        //this.toFront();
    }//GEN-LAST:event_formMouseEntered

    private void formMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseExited
        //this.toBack();
    }//GEN-LAST:event_formMouseExited

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        
    }//GEN-LAST:event_formComponentShown

    private void tblAgendaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblAgendaFocusGained
        //txtItemCodigo.requestFocus();
    }//GEN-LAST:event_tblAgendaFocusGained

    private void tblAgendaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAgendaMouseClicked
        if (evt.getClickCount() == 2) {
            editarTarefa();
            
        } else {
            //atualizarLinha(); //2020-04-30
        }
    }//GEN-LAST:event_tblAgendaMouseClicked

    private void tblAgendaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tblAgendaPropertyChange

    }//GEN-LAST:event_tblAgendaPropertyChange

    private void btnAgendaNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgendaNovoActionPerformed
        novaTarefa();
    }//GEN-LAST:event_btnAgendaNovoActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        ajustarFundo();
    }//GEN-LAST:event_formComponentResized

    private void btnAgendaConcluidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgendaConcluidoActionPerformed
        concluirTarefa();
    }//GEN-LAST:event_btnAgendaConcluidoActionPerformed

    private void cboTarefaConcluidaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboTarefaConcluidaItemStateChanged
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
            carregarTabelaAgenda();
        }
    }//GEN-LAST:event_cboTarefaConcluidaItemStateChanged

    private void btnAgendaAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgendaAtualizarActionPerformed
        carregarTabelaAgenda();
    }//GEN-LAST:event_btnAgendaAtualizarActionPerformed

    private void cboFuncionarioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboFuncionarioItemStateChanged
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
            carregarTabelaAgenda();
        }
    }//GEN-LAST:event_cboFuncionarioItemStateChanged

    private void btnAgendaExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgendaExcluirActionPerformed
        excluirTarefa();
    }//GEN-LAST:event_btnAgendaExcluirActionPerformed

    private void btnBackupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackupActionPerformed
        backup();
    }//GEN-LAST:event_btnBackupActionPerformed

    private void btnConfiguracaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfiguracaoActionPerformed
        configuracao();
    }//GEN-LAST:event_btnConfiguracaoActionPerformed

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
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DashboardView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DashboardView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DashboardView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DashboardView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DashboardView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgendaAtualizar;
    private javax.swing.JButton btnAgendaConcluido;
    private javax.swing.JButton btnAgendaExcluir;
    private javax.swing.JButton btnAgendaNovo;
    private javax.swing.JButton btnBackup;
    private javax.swing.JButton btnConfiguracao;
    private javax.swing.JComboBox<Object> cboFuncionario;
    private javax.swing.JComboBox<String> cboTarefaConcluida;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JPanel pnlAgenda;
    private javax.swing.JPanel pnlAgendaFiltro;
    private javax.swing.JTable tblAgenda;
    private javax.swing.JTextField txtBackup;
    // End of variables declaration//GEN-END:variables
}
