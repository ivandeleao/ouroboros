/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.sistema;

import java.util.ArrayList;
import java.util.List;
import static ouroboros.Ouroboros.MAIN_VIEW;

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

    /**
     * Creates new form ParcelamentoView
     */
    public LogAtualizacao(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        carregarDados();
    }

    public LogAtualizacao() {
        super(MAIN_VIEW, true);
        initComponents();

        carregarDados();

        this.setLocationRelativeTo(this); //centralizar
        this.setVisible(true);
    }

    private void carregarDados() {

        List<String> log = new ArrayList<>();

        log.add("2018-08-22");
        log.add("Adicionado lançamento manual de estoque direto na tela de lista de produtos");
        log.add("Adicionado foco na caixa de texto de código na tela de venda ao alternar entre telas");
        log.add("Adicionado botão para abrir a venda na tela de crediário do cliente");
        log.add("2018-08-24");
        log.add("Adicionado menu de configuração da impressora em configurações do sistema");
        log.add("Adicionada opção para desativar a impressão (para testes)");
        log.add("Adicionada impressão de ticket de comanda na tela de venda");
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
        log.add("Adicionado DocumentoTipo para diferenciar notas de entrada e saída");
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

        
        String logString = String.join("\r\n", log);
        
        System.out.println(logString);
        
        txtLog.setText(logString);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Log de Atualização");
        setResizable(false);

        txtLog.setColumns(20);
        txtLog.setRows(5);
        jScrollPane2.setViewportView(txtLog);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1030, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 636, Short.MAX_VALUE)
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea txtLog;
    // End of variables declaration//GEN-END:variables
}