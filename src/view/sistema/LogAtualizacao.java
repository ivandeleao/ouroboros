/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.sistema;

import java.awt.Dimension;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.jtable.LogAtualizacaoJTableModel;
import model.nosql.LogAtualizacaoItem;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_CENTER;
import static ouroboros.Constants.CELL_RENDERER_ALIGN_RIGHT;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.jTableFormat.LineWrapCellRenderer;
import util.jTableFormat.TableRenderer;

/**
 *
 * @author ivand
 *
 * ESTE FOI CRIADO COMO JDIALOG DESDE O INÍCIO - USAR ESTE COMO REFERÊNCIA PARA
 * OS DEMAIS
 *
 *
 */
public class LogAtualizacao extends javax.swing.JDialog {

    LogAtualizacaoJTableModel logAtualizacaoJTableModel = new LogAtualizacaoJTableModel();

    /**
     * Creates new form ParcelamentoView
     */
    private LogAtualizacao(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

    }

    public LogAtualizacao() {
        super(MAIN_VIEW, true);
        initComponents();

        formatarTabela();
        carregarTabela();

        this.setLocationRelativeTo(this); //centralizar
        this.setVisible(true);
    }

    private void formatarTabela() {
        tblLog.setModel(logAtualizacaoJTableModel);

        tblLog.setRowHeight(24);
        tblLog.setIntercellSpacing(new Dimension(10, 10));
        tblLog.setDefaultRenderer(String.class, new LineWrapCellRenderer());

        tblLog.getColumn("Data").setPreferredWidth(100);
        tblLog.getColumn("Data").setCellRenderer(CELL_RENDERER_ALIGN_CENTER);

        tblLog.getColumn("Descrição").setPreferredWidth(1100);

    }

    private void carregarTabela() {

        List<LogAtualizacaoItem> logs = new ArrayList<>();

        logs.add(new LogAtualizacaoItem(LocalDate.parse("2018-08-22"),
                "Adicionado lançamento manual de estoque direto na tela de lista de produtos \r\n"
                + "Adicionado foco na caixa de texto de código na tela de venda ao alternar entre telas \r\n"
                + "Adicionado botão para abrir a venda na tela de crediário do cliente"));

        logs.add(new LogAtualizacaoItem(LocalDate.parse("2018-08-24"),
                "Adicionado menu de configuração da impressora em configurações do sistema\r\n"
                + "Adicionada opção para desativar a impressão (para testes)\r\n"
                + "Adicionada impressão de ticket de comanda na tela de venda"));

        List<String> log = new ArrayList<>();

        log.add("2018-08-27");
        log.add("Adicionada escolha de item caso encontre código duplicado na venda");
        log.add("2018-08-29");
        log.add("Adicionado arquivo de configuração (config.txt) para definir o servidor");
        log.add("2018-09-03");
        log.add("Adicionado estorno no caixa");
        log.add("2018-09-06");
        log.add("Adicionado filtro por status no crediário do cliente");
        log.add("2018-09-10");
        log.add("Adicionada coluna observação da venda na tela de crediário do cliente");
        log.add("2018-09-11");
        log.add("Adicionado cadastro de categorias");
        log.add("2018-09-12");
        log.add("Corrigido recibo quando não tem cliente");
        log.add("Melhorada busca rápida na lista de clientes para achar com partes do nome");
        log.add("2018-09-13");
        log.add("Adicionada tela de vendas agrupadas por item");
        log.add("2018-09-14");
        log.add("Adicionada tela de vendas agrupadas por categoria");
        log.add("2018-09-19");
        log.add("Adicionado botão transferir comanda na venda");
        log.add("Refatorado identificação da comanda na venda");
        log.add("Adicionado atalho (ESC) para voltar para comandas na venda");
        log.add("2018-09-20");
        log.add("Adicionado atalho para finalizar recebimento na venda");
        log.add("2018-09-21");
        log.add("Rediagramada tela de venda");
        log.add("Parametrizado uso do SAT");
        log.add("2018-09-24");
        log.add("Parametrizado número de comandas");
        log.add("Melhorado layout da tela de comandas");
        log.add("2018-09-26");
        log.add("Adicionada pesquisa de NCM no cadstro de produto");
        log.add("2018-09-27");
        log.add("Refatorado cliente para pessoa, permitindo ser cliente e ou fornecedor");
        log.add("2018-10-04");
        log.add("Adicionado CEST (Código Especificador da Substituição Tributária)");
        log.add("2018-10-08");
        log.add("Adicionado Contas a Receber");
        log.add("2018-10-15");
        log.add("Renomeada classe VendaItemDAO para MovimentoFisicoDAO");
        log.add("2018-10-25");
        log.add("Adicionado ordem cronológica relevante no Estoque");
        log.add("2018-10-26");
        log.add("Adicionado componentes do produto");
        log.add("2018-10-29");
        log.add("Adicionado movimento de estoque dos componentes");
        log.add("2018-10-31");
        log.add("Adicionado Código de Ativação e Chave de Acesso (Sign AC) na configuração do SAT");
        log.add("2018-11-09");
        log.add("Corrigido cabeçalho XML do arquivo de cancelamento");
        log.add("Adicionado Margens da Impressão do CFe na configuração do SAT");
        log.add("Adicionado filtro por entrega e devolução na lista de vendas");
        log.add("2018-11-15");
        log.add("Refatorado e corrigido devolução e remoção de item da venda");
        log.add("2018-11-16");
        log.add("Adicionado indicador de item de balança no cadastro de produto");
        log.add("Adicionada exportação de arquivo de texto ITENSMGV.TXT para balança Toledo MGV5");
        log.add("Refatorado posicionamento do menu principal e imagem de fundo");
        log.add("Adicionado número da venda nos movimentos do estoque");
        log.add("2018-11-21");
        log.add("Refatorado exclusão de movimentoFisico como estorno");
        log.add("Permitida baixa parcial das parcelas do cliente");
        log.add("Remodelado recibo de parcelas");
        log.add("2018-11-22");
        log.add("Corrigido busca de itens, já excluídos, com código duplicado na venda");
        log.add("2018-11-29");
        log.add("Adicionado cadastro de usuários");
        log.add("Adicionado controle de acesso por usuário");
        log.add("2018-12-14");
        log.add("Removida tag troco no xml de venda do CF-e SAT (tag é gerada pelo SAT, não pelo AC)");
        log.add("Adicionada coluna de status no crediário do cliente e em contas a receber");
        log.add("Corrigido comparador para o status da parcela por conta de baixas parcias");
        log.add("2018-12-24");
        log.add("Subtituído tipo de movimentoFisico por documentoOrigem (extraindo do documento de origem)");
        log.add("2018-12-28");
        log.add("Corrigido bug de duplicidade de itens ao agendar locação");
        log.add("Adicionado tipo e status da venda na lista de venda");
        log.add("Configurado elementos a serem exibidos por tipo de venda");
        log.add("Filtrado itens de movimentoFisico e parcela que estão em orçamento");
        log.add("2019-01-08");
        log.add("Adicionados campos Outros Códgos e Localização no cadastro de produto");
        log.add("2019-01-09");
        log.add("Adicionados campos para Responsável no cadastro de cliente");
        log.add("2019-01-11");
        log.add("Adicionado campo Estoque Inicial no cadastro de Produto");
        log.add("Adicionado Formato Padrão de Impressão para CUPOM E A4");
        log.add("2019-01-15");
        log.add("Adicionada Contas Programadas");
        log.add("Adicionada verificação de CPF ou CNPJ já existente no cadastro de pessoas");
        log.add("Adicionados dados do cliente na impressão de venda em A4 e Cupom");
        log.add("2019-01-16");
        log.add("Corrigido filtro de parcelas no crediário para ignorar orçamento e cancelado");
        log.add("2019-01-17");
        log.add("Adicionado filtro de status em Contas a Pagar");
        log.add("Adicionado filtro de categoria em Produtos");
        log.add("2019-01-18");
        log.add("Adicionados campos de acréscimo e desconto no recebimento de parcela");
        log.add("Adicionada verificação de código de barras existente no cadastro de produtos");
        log.add("2019-01-21");
        log.add("Adicionado gerar carnê em Vendas (modelo Danila Sabadini)");
        log.add("2019-01-24");
        log.add("Refatorado cliente na venda");
        log.add("Refatorada tela de recebimento na venda");
        log.add("Alterada configuração de impressão para salvar por estação de trabalho (config local)");
        log.add("Corrigido bug de duplicidade de estornos na venda");
        log.add("2019-01-28");
        log.add("Corrigido bug de duplicidade no recebimento de venda com mais de um meio de pagamento");
        log.add("2019-01-29");
        log.add("Adicionado refresh no carregamento da venda para uso em múltiplas estações");
        log.add("Refatorada lista de comandas abertas para ignorar canceladas");
        log.add("Adicionado número da versão do sistema");
        log.add("Exibido servidor no título do sistema");
        log.add("Pequenas melhorias e correções em comandas");
        log.add("2019-01-31");
        log.add("Adicionado botão de exclusão para pessoa");
        log.add("Adicionada observação na impressão do carnê modelo Danila Sabadini");
        log.add("2019-02-01");
        log.add("Adicionado TipoOperacao para diferenciar notas de entrada e saída");
        log.add("Refatorado carregamento de guias em pessoa");
        log.add("Adicionada tela de compras");
        log.add("Adicionada tela de parcela a pagar em pessoa");
        log.add("2019-02-04");
        log.add("Adicionado auto-bootstrap para NCM");
        log.add("2019-02-08");
        log.add("Adicionado pagamento de parcela de compra");
        log.add("2019-02-15");
        log.add("Renomeadas constantes RECEBIMENTO_DE_VENDA para RECEBIMENTO_DOCUMENTO e PAGAMENTO_DE_COMPRA para PAGAMENTO_DOCUMENTO");
        log.add("2019-02-18");
        log.add("Corrigidos métodos para adicionar e remover componente em produto");
        log.add("2019-02-19");
        log.add("Corrigidos códigos de NCM no bootstrap");
        log.add("2019-02-20");
        log.add("Refatorado/Corrigido ações de movimentoFisico com componentes");
        log.add("2019-02-22");
        log.add("Separada observação no carnê modelo Danila Sabadini");
        log.add("Adicionado gerar promissória");
        log.add("Adicionada pesquisa de endereços no cadastro de clientes");
        log.add("Corrigido: após tentativa de inserção de produto com valor zero na venda, não aceitava outro produto");
        log.add("Refatorado: inserção de produto na venda");
        log.add("2019-02-27");
        log.add("Adicionado impressão para cupom de 58mm");
        log.add("2019-02-28");
        log.add("Refatorado cálculo getTotal() getTotalEmAberto() em venda para arrendodar para 2 casas");
        log.add("getSubtotal em MovimentoFisico");
        log.add("2019-03-01");
        log.add("Corrigido parcelamento na venda - gerava parcela fantasma por não salvar a venda no loop");
        log.add("Alterada definição dos campos entrada e saída do movimentoFisico para decimal(20,3)");
        log.add("Substituido deepClone() do produto para copiar()");
        log.add("2019-03-07");
        log.add("Corrigido no cupom Sat: im e endereço do emitente, data do cupom");
        log.add("2019-03-11");
        log.add("Adicionado filtro de documentos cancelados no faturamento");
        log.add("Adicionado totais de crédito e débito no caixa");
        log.add("Adicionado filtro por tipo no caixa");
        log.add("Parametrizado números de comandas disponíveis em transferir comanda");
        log.add("Refatorada rotina para gerar parcela e recebimentos em RecebimentoView");
        log.add("2019-03-14");
        log.add("Adicionada impressão de Tag de produtos modelo Oficina da Artes");
        log.add("2019-03-15");
        log.add("Adicionada validação dos itens para gerar o cupom sat");
        log.add("2019-03-18");
        log.add("Adicionados mais recursos nas diretivas do usuário");

        log.add("2019-03-18");
        log.add("Adicionado cadastro de Funcionários");
        log.add("2019-03-19");
        log.add("Adicionado funcionário nos documentos de saída (vendas)");
        log.add("Adicionado filtro por funcionário em faturamento");
        log.add("Removida coluna estoque na lista de produtos - causava travamentos");

        log.add("2019-03-21");
        log.add("Adicionado totais separados em faturamento");
        log.add("Adicionado meios de pagamento Cartão de Crédito e Débito para sangria e suprimento no caixa");
        log.add("Removido complemento do endereço no cupom fiscal quando não informado");

        log.add("2019-03-22");
        log.add("Adicionado campo para exibir a data do cadastro de cliente e fornecedor");
        log.add("Bloqueado faturamento no dcumento de saída quando não tem cliente informado");
        log.add("Aumentado o tamanho da fonte dos botões no documento de saída");
        log.add("Adicionado resumo por meio de pagamento no caixa");

        log.add("2019-03-23");
        log.add("Refatorado rotinas de suprimento e sangria usando a cascata de salvamento Caixa -> CaixaItem");
        log.add("Refatorado método do resumo por meio de pagamento, recebendo List<CaixaItem> para refletir o filtro do caixa");
        log.add("Adicionado método equals na classe MeioDePagamento pois não comparava corretamente os novos itens do caixa ao passar para o resumo");

        log.add("2019-03-25");
        log.add("Adicionado registro dos cupons emitidos no documento de saída");
        log.add("Refatorada tela de emissão de cupom Sat");
        log.add("Adicionada opção de reimpressão de cupons no documento de saída");

        log.add("Refatorado cancelamento de cupom, sendo possível selecionar a partir do documento de saída");
        log.add("Adicionado filtro para cupons Sat em faturamento");

        log.add("2019-03-29");
        log.add("Adicionado grupo de pessoas");
        log.add("Redesenhado cadastro de pessoa");

        log.add("2019-03-31");
        log.add("Adicionado itens no grupo");
        log.add("Reorganizando pacotes model-bean-principal");

        log.add("2019-04-01");
        log.add("Removida obrigatoriedade do nome fantasia no cadastro de pessoa");

        log.add("2019-04-03");
        log.add("Adicionada coluna com a data de lançamento em pessoas por grupo");
        log.add("Refatorado gerarDocumento para agrupar itens de múltiplos grupos em pessoas por grupo");
        log.add("Adicionada validação para gerar documento em pessoas por grupo");

        log.add("2019-04-04");
        log.add("Refatorado datas de criação e atualização em CaixaItem de Timestamp para LocalDateTime");

        log.add("2019-04-05");
        log.add("Adicionado resumo de caixa por período por meio de pagamento");
        log.add("Adicionado relatório para impressão no resumo de caixa por período por meio de pagamento");

        log.add("Refatorada impressão(cupom não fiscal) de venda para exibir o desconto percentual sobre item");
        log.add("Refatorada impressão(cupom fiscal) de venda para exibir o desconto percentual sobre item");
        log.add("Corrigido comparativo de subtotal ao remover item - arredondado para comparar");

        log.add("2019-04-06");
        log.add("Refeita impressão de Ordem de Serviço no padrão Jasper");

        log.add("2019-04-07");
        log.add("Adicionado filtro de aniversário na lista de pessoas");

        log.add("2019-04-09");
        log.add("Adicionada validação do sistema");

        log.add("2019-04-10");
        log.add("Refatorado para recarregar combo dos grupos em Pessoas por Grupo");
        log.add("Adicionado valor do item no cadastro de perfil");

        log.add("2019-04-11");
        log.add("Refatorado cadastro da empresa em sistema");

        log.add("2019-04-12");
        log.add("Adicionado Perfil de NFe em sistema");
        log.add("Adicionado regime tributário e tipo de atendimento para o perfil de NFe");

        log.add("2019-04-13");
        log.add("Adicionado tipo de emissão para o perfil de NFe");

        //XXX---------XXX---------XXX---------XXX---------XXX---------XXX---------
        log.add("2019-04-15");
        log.add("Renomeado DocumentoTipo para TipoOperacao");
        //XXX---------XXX---------XXX---------XXX---------XXX---------XXX---------

        log.add("2019-04-18");
        log.add("Refatorado contrutores da classe Venda corrigindo orçamento sem tipoOperacao");

        log.add("2019-04-19");
        log.add("Refatorada lista de Pessoas por Grupo de último vencimento para último id dos documentos");
        log.add("Removido recarregar automático pois gerava erro quando editado o documento gerado");

        log.add("Adicionado campo quantidade para item de perfil");
        log.add("Refatorado gerar documento em Pessoas por Grupo para considerar a quantidade do item de perfil");

        log.add("Adicionado destinoOperacao para o perfil de NFe");
        log.add("Adicionado consumidorFinal para o perfil de NFe");

        log.add("2019-04-20");
        log.add("Adicionado tipoContribuinte para NFe");
        log.add("Adicionado campo suframa e im em pessoa");

        log.add("Redesenhado cadastro de produto");
        log.add("Refatorado carregamento das guias de produto");

        log.add("Adicionado modalidadeBcIcms para NFe");
        log.add("Adicionado modalidadeBcIcmsSt para NFe");

        log.add("2019-04-22");
        log.add("Refatorado carnê Danila Sabadini para exibir quantidade dos itens, desconto e acréscimo geral");
        log.add("Refatorado remover, adicionar e editar parcelas - problema de parcelas fantasma");

        log.add("Adicionado botão para informar chave de ativação dentro da tela sistema");

        log.add("2019-04-24");
        log.add("Adicionado aviso sobre parcelas em atraso ao inserir cliente nos documentos de saída");
        log.add("Corrigido atalho para chamar funcionário nos documentos de saída");

        log.add("2019-04-26");
        log.add("Corrigido alinhamento das colunas no crediário em pessoa");
        log.add("Corrigido exibição do troco em recebimento de parcelas");

        log.add("Adicionado marca de administrador nos usuários");
        log.add("Adicionado aviso e bloqueio para limite de crédito");
        log.add("Adicionado opção de revalidação de administrador já logado");
        log.add("Adicionado validação de administrador para limite de crédito");

        log.add("2019-04-29");
        log.add("Refatorada liberação de Sistema e Usuários para apenas Administradores");
        log.add("Adicionado bootstrap automático para remover os recursos SISTEMA e USUARIOS");
        log.add("Refatorado impressão de OS A4 - estender o campo de observação automaticamente");

        log.add("2019-05-02");
        log.add("Adicionado e-mail, linha de assinatura e data na impressão da Ordem de Serviço A4");

        log.add("2019-05-03");
        log.add("Adicionado campo de relato/solicitação do cliente em ordem de serviço");
        log.add("Refatorado ordem de serviço A4 para esticar e ou ocultar os campos relato e observação");
        log.add("Adicionado impressão de ticket para cozinha");
        log.add("Adicionado motivo de cancelamento em documentos de saída");

        logs.add(new LogAtualizacaoItem(LocalDate.parse("2018-05-09"),
                "...Log sendo refatorado..."));

        logs.add(new LogAtualizacaoItem(LocalDate.parse("2018-05-10"),
                "Refatorado parcelamento novamente - parcelas fantasma e erro na distribuição de valores\r\n"
                + "Refatorado parcelamento com primeira parcela para o dia (a vista). Adicionado método para salvar o item no caixa\r\n"
                + "Refatorado abrir e encerrar caixa para validar o status do mesmo"));

        logs.add(new LogAtualizacaoItem(LocalDate.parse("2018-05-14"),
                "Refatorado exibição do log de atualizações"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2018-05-15"),
                "Adicionado cadastro de veículos"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2018-05-21"),
                "Rediagramada telas de cadastro de produto e pessoa para resoluções menores (1280)"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2018-05-22"),
                "Rediagramada tela de documento de saída\r\n"
                + "Adcionado veículo nos documentos de saída\r\n"
                + "Corrigido limite de crédito com valor zero era ignorado"));

        logAtualizacaoJTableModel.clear();
        logAtualizacaoJTableModel.addList(logs);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        tblLog = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Log de Atualização");
        setResizable(false);

        tblLog.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tblLog);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1030, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(LogAtualizacao.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LogAtualizacao.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LogAtualizacao.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LogAtualizacao.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
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
                LogAtualizacao dialog = new LogAtualizacao(new javax.swing.JFrame(), true);
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
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tblLog;
    // End of variables declaration//GEN-END:variables
}
