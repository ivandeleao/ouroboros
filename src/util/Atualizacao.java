/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.mysql.bean.principal.Constante;
import model.mysql.dao.principal.ConstanteDAO;
import model.nosql.LogAtualizacaoItem;
import ouroboros.Ouroboros;

/**
 *
 * @author ivand
 */
public class Atualizacao {
    static List<LogAtualizacaoItem> logs = new ArrayList<>();

    
    public static LocalDate getVersaoAtual() {
        return LocalDate.parse(ConstanteDAO.getValor("SISTEMA_VERSAO"));
    }
    
    public static void setVersaoAtual(LocalDate data) {
        ConstanteDAO.save(new Constante("SISTEMA_VERSAO", data.toString()));
    }
    
    public static LocalDate getUltimaData() {
        return getLista().get(getLista().size() - 1).getData();
    }
    
    public static List<LogAtualizacaoItem> getLista() {
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2018-08-22"),
                "Adicionado lançamento manual de estoque direto na tela de lista de produtos \r\n"
                + "Adicionado foco na caixa de texto de código na tela de venda ao alternar entre telas \r\n"
                + "Adicionado botão para abrir a venda na tela de crediário do cliente"));

        logs.add(new LogAtualizacaoItem(LocalDate.parse("2018-08-24"),
                "Adicionado menu de configuração da impressora em configurações do sistema\r\n"
                + "Adicionada opção para desativar a impressão (para testes)\r\n"
                + "Adicionada impressão de ticket de comanda na tela de venda"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-05-09"),
                "...Log sendo refatorado..."));

        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-05-10"),
                "Refatorado parcelamento novamente - parcelas fantasma e erro na distribuição de valores\r\n"
                + "Refatorado parcelamento com primeira parcela para o dia (a vista). Adicionado método para salvar o item no caixa\r\n"
                + "Refatorado abrir e encerrar caixa para validar o status do mesmo"));

        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-05-14"),
                "Refatorado exibição do log de atualizações"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-05-15"),
                "Adicionado cadastro de veículos"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-05-21"),
                "Rediagramada telas de cadastro de produto e pessoa para resoluções menores (1280)"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-05-22"),
                "Rediagramada tela de documento de saída\r\n"
                + "Padronizada impressão no formato A4 para todos os tipos de documento\r\n"
                + "Adicionado veículo nos documentos de saída\r\n"
                + "Corrigido limite de crédito com valor zero era ignorado\r\n"
                + "Adicionada validação para cadastro de veículo com placa já cadastrada"));

        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-05-23"),
                "Adicionado filtro para cliente e veículo na lista de documentos de saída\r\n"
                + "Adicionada coluna com valor em aberto na lista de documentos de saída\r\n"
                + "Adicionado id do cliente na impressão A4 de documentos de saída"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-05-24"),
                "Adicionada máscara de entrada para placa de veículo"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-05-27"),
                "Adicionada validação ao adicionar e remover parcelas. Impede reparcelamento se já houver valor recebido\r\n"
                + "Adcionada validação do meio de pagamento no parcelamento com entrada. Impede receber com Crédito Loja\r\n"
                + "Refatorado busca de produto por id para não trazer produtos excluídos\r\n"
                + "Adicionados meios de pagamento 15-Boleto Bancário e 90-Sem Pagamento\r\n"
                + "Bloqueada tela de faturamento quando já recebido a vista e não há valor faturado\r\n"
                + "Refatorado exibição do valor recebido (a vista) em documentos de saídas"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-05-28"),
                "Adicionado id do cliente na impressão térmica\r\n"
                + "Adicionada validação do meio de pagamento no parcelamento com entrada. Impede receber com Crédito Loja\r\n"
                + "Refatorado busca de produto por id para não trazer produtos excluídos\r\n"
                + "Adicionados meios de pagamento 15-Boleto Bancário e 90-Sem Pagamento\r\n"
                + "Bloqueada tela de faturamento quando já recebido a vista e não há valor faturado\r\n"
                + "Refatorado exibição do valor recebido (a vista) em documentos de saídas"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-05-29"),
                "Adicionado id do cliente na impressão térmica\r\n"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-05-31"),
                "Refatorado CaixaItemTipo TROCO tornando genérico para entrada ou saída\r\n"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-06-10"),
                "Refatorado CaixaItemTipo DOCUMENTO_RECEBIMENTO E DOCUMENTO_PAGAMENTO tornando genérico para DOCUMENTO\r\n"
                + "Refatorado descrição dos itens do caixa\r\n"
                + "Refatorado para abrir documentos de origem dos trocos no caixa\r\n"
                + "Corrigido atualização do estoque do produto"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-06-13"),
                "Alterada fonte na impressão térmica de COURIER para UNDEFINED, melhorando a legibilidade\r\n"
                + "Adicionado campo de estoque atual como detalhe na lista de produtos\r\n"
                + "Refatorado tipo dos campos criacao e atualizacao de Timestamp para LocalDateTime e do campo vencimento de Date para LocalDate na classe Parcela\r\n"
                + "Integrado parcelas de compra junto com contas programadas na tela de contas a pagar"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-06-14"),
                "Corrigido bug ao remover parcelas de um documento e voltar para uma lista de parcelas a receber (Crediário e Contas a Receber)\r\n"
                + "Adicionada opção de remover conta programada"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-06-15"),
                "Adicionado tipo produto/serviço no cadastro\r\n"
                + "Adicionada coluna indicando produto/serviço nos documentos de saída\r\n"
                + "Adicionado filtro por tipo produto/serviço na lista de produtos"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-06-16"),
                "Separado totais por produto/serviço nos documentos de saída\r\n"
                + "Adicionado filtro por tipo produto/serviço na lista de produtos\r\n"
                + "Refatorado emissão de Cupom Fiscal Sat para ignorar serviços do documento\r\n"
                + "Adicionados totais de produtos e serviços na lista de documentos de saída"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-06-16"),
                "Separado totais por produto/serviço nos documentos de saída\r\n"
                + "Adicionado filtro por tipo produto/serviço na lista de produtos\r\n"
                + "Refatorado emissão de Cupom Fiscal Sat para ignorar serviços do documento\r\n"
                + "Adicionados totais de produtos e serviços na lista de documentos de saída"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-06-17"),
                "Redimensionada tela do caixa para baixa resolução"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-06-18"),
                "Corrigido bug para baixa parcial de parcela. Entrava em um loop infinito e não associava o id da parcela no itemCaixa\r\n"
                + "Adicionado campo de estoque atual como detalhe na pesquisa de produtos\r\n"
                + "Refatorado Acréscimos e Descontos nos documentos de saída para Produtos e Serviços\r\n"
                + "Refatorado Acréscimos e Descontos nos documentos de saída para Produtos e Serviços na impressão A4"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-06-19"),
                "Refatorada inserção de item na venda para permitir edição de serviços\r\n"
                + "Adicionado telefone da empresa na impressão térmica\r\n"
                + "Adicionado campo subtotal no lançamento do item na venda, e permissão de cálculo reverso para quantidade\r\n"
                + "Adicionado parâmetro de limite de crédito inicial para cliente\r\n"
                + "Liberada edição de parcelas não recebidas da venda que já contém recebimento"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-06-20"),
                "Adicionado padrão Toledo MGV6 na exportação de produtos para balança"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-06-21"),
                "Adicionada impressão A4 para lista de produtos\r\n"
                + "Adicionado filtro para itens de balança na lista de produtos"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-06-24"),
                "Adicionada impressão A4 para lista de produtos com saldo de estoque"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-06-26"),
                "Corrigido multa e juros que não eram lançados nos documentos gerados na tela de grupos"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-06-27"),
                "Adicionado campos de quantidade e unidade de medida do conteúdo de um produto\r\n"
                + "Refatorado cálculo de estoque dos componentes para usar a informação de conteúdo\r\n"
                + "Refatorada inclusão de componente\r\n"
                + "Refatorada exibição do estoque atual para considerar o conteúdo\r\n"
                + "Definido como obrigatório o cadastro da unidade de medidade de um produto\r\n"
                + "Criada ajuda em html"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-07-01"),
                "Adicionado campo andamentoPrevisao em movimentoFisico\r\n"
                + "Refatorado cálculo do estoque para melhorar o desempenho. Substituído hierarquia JPA por NativeQuery\r\n"
                + "Refatorado exibição do estoque na lista e pesquisa de produtos\r\n"
                + "Removida coluna da unidade de medida na pesquisa de produtos"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-07-08"),
                "Refatorada tela de comandas. Substituído botões por lista das comandas ativas\r\n"
                + "Adicionado modo balcão (venda simplificada e sem menu principal)\r\n"
                + "Adicionado opção para carregar tela de comandas ao iniciar"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-07-10"),
                "Adicionada etiqueta de produtos modelo GPrinter G2120 - 50mm x 30mm\r\n"
                + "Adicionada impressão de etiquetas a partir da compra\r\n"
                + "Adicionado parâmetro de sistema para impressora de etiqueta\r\n"
                + "Removida constante ImpressoraFormato.A4"));
        
        return logs;
    }
}
