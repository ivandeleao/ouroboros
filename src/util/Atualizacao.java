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

/**
 *
 * @author ivand
 */
public class Atualizacao {

    private final static List<LogAtualizacaoItem> logs = new ArrayList<>();

    private static void carregar() {

        if (logs.isEmpty()) {
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

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-07-15"),
                    "Adicionado campo de dias de validade no cadastro de produtos\r\n"
                    + "Refatorado arquivo de balança para informar validade do produto\r\n"
                    + "Adicionado atalhos para venda usando apenas teclado numérico\r\n"
                    + "Otimizado modo de inserção do item para inserir no campo de quantidade quando em modo balcão\r\n"
                    + "Adicionado atalhos na pesquisa de produtos quando em modo balcão"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-07-16"),
                    "Refatorado caixas de valores decimas\r\n"
                    + "Corrigido cálculo de desconto e subtotal reverso ao inserir item na venda"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-07-17"),
                    "Removida instrução para refletir o estoque ao inserir item na venda pois causava cascata de consultas no movimentoFisico"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-07-18"),
                    "Adicionado campo de frete nos itens de compra e na importação por XML\r\n"
                    + "Adicionado opções para layout de comandas em lista ou ladrilho"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-07-19"),
                    "Removida instrução para refletir o estoque ao remover item na venda pois causava cascata de consultas no movimentoFisico"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-07-22"),
                    "Adicionado cadastro de novo produto em modal na importação de XML de compra\r\n"
                    + "Adicionado campo da localização nos detalhes do item na lista de produtos e serviços"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-07-25"),
                    "Corrigido exibir itens com código repetido\r\n"
                    + "Corrigido formatação do CNPJ para importar XML de compra\r\n"
                    + "Adicionado CEP na importação do XML de compra\r\n"
                    + "Alterada busca de produto na importação de XML para exibir valor de compra e pré inserir o nome do produto"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-07-26"),
                    "Refatorada tela de comandas em lista para atualizar automaticamente\r\n"
                    + "Refatorado relatórios de produtos para aparecer na frente do programa\r\n"
                    + "Corrigido conversão de LocalDateTimeOffsetZone para ignorar em caso de campo ausente na importação de XML\r\n"
                    + "Adicionados campos valorSeguro (vSeg), acrescimoMonetario (vOutro), descontoMonetario (vDesc) no movimentoFisico e importação de NFe"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-07-27"),
                    "Corrigido atualização da tela de comandas em lista para encerrar o ciclo ao fechar a tela"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-07-30"),
                    "Otimizado processo de atualização da tela de comandas em lista\r\n"
                    + "Removido salvar individual do movimentoFisico ao inserir item na venda"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-07-31"),
                    "Corrigido acentuação na impressão de etiquetas para GP2120"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-08-02"),
                    "Refatorado acréscimo e desconto para rateio no padrão da NFe\r\n"
                    + "Melhorias no cupom não fiscal: refatorado lógica de acréscimos e descontos; \r\n"
                    + "adicionado funcionário, veículo e assinatura do cliente;\r\n"
                    + "chaveado exibição dos campos referentes ao cliente\r\n"
                    + "Adicionado campo rodapé de impressão em configurações do sistema\r\n"
                    + "Adicionado coluna acréscimo na impressão de venda A4"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-08-03"),
                    "Refatorado cupom Sat para nova nova lógica de acréscimo e desconto"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-08-05"),
                    "Tranferido botão de ativação do sistema da tela de sistema para a tela de log"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-08-07"),
                    "Liberado patch de atualização para acréscimos e descontos"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-08-08"),
                    "Adicionado desconto por item na compra\r\n"
                    + "Adicionada impressão de lista de parcelas em formato cupom"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-08-15"),
                    "Adicionado memorização do modelo de etiqueta usado em produtos"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-08-21"),
                    "Adicionado campo para memorizar produtoTipo no movimentoFisico e refatorado sistema para obter dado a partir do movimentoFisico"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-08-27"),
                    "Adicionado modelo de etiqueta de produtos Zebra 60mm x 35mm\r\n"
                    + "Adicionado amostra dos modelos de etiqueta"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-08-28"),
                    "Corrigido status de documentos gerados por grupo\r\n"
                    + "Alterada definição do campo unidadeComercialVenda em movimentoFisico para não permitir nulo"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-08-29"),
                    "Adicionado tipo de documento DELIVERY\r\n"
                    + "Adicionado campos de pagamento, troco e endereço de delivery\r\n"
                    + "Adicionado botão para confirmação de entrega na lista de documentos de saída\r\n"
                    + "Refatorada busca de pessoas para procurar também no telefone\r\n"
                    + "Refatorada aparência das telas de lista de documentos de saída, itens e busca de pessoas"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-08-30"),
                    "Refatorada lista de produtos e pesquisa para exibir valores dos tamanhos\r\n"
                    + "Adicionada opção de exclusão de categoria\r\n"
                    + "Refatorada impressão Térmica para exibir Funcionário como Entregador quando for Delivery"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-09-03"),
                    "Adicionado orphanRemoval nas parcelas de venda para impedir fatasmas ao remover parcelas"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-09-04"),
                    "Adicionado motivos de desoneração do ICMS\r\n"
                    + "Adicionado tipo de produto Unitário/Peso na exportação para balanças"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-09-05"),
                    "Corrigido carregamento do log de atualizações"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-09-06"),
                    "Refatorado método Venda.getTotalReceberProdutos para arredondar, pois gerava MP de saldo pendente na emissão do Sat com valor zero\r\n"
                    + "Adicionada tabela de situação tributária do PIS"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-09-10"),
                    "Adicionada tabela de situação tributária do COFINS"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-09-11"),
                    "Adicionado campos de ICMS, PIS e COFINS em MovimentoFisico"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-09-12"),
                    "Refatorado inserir item de venda para permitir edição da descrição do produto"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-09-18"),
                    "Refatorado excluir na lista de produtos para manter posicionada a tabela"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-09-19"),
                    "Otimizado redimensionamento automático das listas de produtos e itens de venda com variação de tamanho do produto"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-09-19"),
                    "Adicionado modalidades de frete"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-09-20"),
                    "Adicionado campo nome nas comandas"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-09-26"),
                    "Adicionado parâmetros de informações adicionais e complementares da NFe"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-09-27"),
                    "Atualizada tabela IBPT: 19.2.A\r\n"
                    + "Adicionado informação automática sobre impostos IBPT na NFe"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-09-30"),
                    "Refatorado consulta por critério em ProdutoDAO pois congelava na busca"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-10-02"),
                    "Corrigido baixa de parcela a vista na compra. Lançava como crédito ao invés de débito"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-10-04"),
                    "Refatorado backup para funcionar nas estações também"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-10-08"),
                    "Refatorado método Parcela.getValorAtual() para arredondar, "
                    + "pois gerava status incorreto (QUITADO/ABERTO) para valores com mais casas decimais\r\n"
                    + "Refatorado método VendaDAO.getComandasAbertasSnapshot() para considerar acréscimos e descontos\r\n"
                    + "Melhorado design da impressão de ticket de cozinha\r\n"
                    + "Adicionado o campo com nome para cliente avulso e impressão de ticket de cozinha no Delivery"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-10-10"),
                    "Refatorado métodos getAcresimoPercentualEmMonetario e getDescontoPercentualEmMonetario da Parcela para arredondar"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-10-11"),
                    "Corrigido máscara para placa de veículos\r\n"
                            + "Adicionado histórico de veículo\r\n"
                            + "Refatorada busca de pessoa procurando também por endereço\r\n"
                            + "Refatorado método getEnderecoCompleto() para ignorar endereços em branco"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-10-12"),
                    "Corrigida impressão de ticket de cozinha para itens montados\r\n"
                            + "Adicionada coluna do meio de pagamento em contas a pagar"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-10-15"),
                    "Corrigido estorno do caixa para lançar registro no caixa atual e não no caixa da origem"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-10-17"),
                    "Adicionada exportação para Nota Fiscal de Serviços Eletrônica no padrão SIGISS\r\n"
                            + "Adicionados filtros para NFSe e NFe em documentos de saída"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-10-18"),
                    "Simplificado método de entrada para valores decimais, permitindo digitar em qualquer parte do campo\r\n"
                            + "Refatorado grade de itens na venda permitindo edição direta de valores\r\n"
                            + "Adicionada coluna para editar item NFe"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-10-22"),
                    "Adicionado chaveamento entre ambiente de homologação e produção para NFe"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-10-23"),
                    "Adicionado documentos referenciados na NFe\r\n"
                            + "Adicionado status da NFe"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-10-30"),
                    "Adicionado parâmetro para status inicial de venda\r\n"
                            + "Refatorada interface para status das vendas\r\n"
                            + "Movido painel de totais da lista de documentos de saída para tela separada"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-11-01"),
                    "Refatorada impressão A4 para esticar as linhas de descrição\r\n"
                            + "Liberada edição da descrição no grid do documento de saída"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-11-02"),
                    "Adicionado acesso de certificado digital por repositório do Windows\r\n"
                            + "Adicionada consulta ao certificado em Sistema"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-11-04"),
                    "Adicionado parâmetro de sistema para habilitar OS Transporte\r\n"
                            + "Renomeada constante para habilitar veículos e movido parâmetro em sistema para a guia Mindware\r\n"
                            + "Adicionado chaveamento de exibição de veículos no Menu Principal"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-11-06"),
                    "Refatorado sessões do JPA Hibernate e cache como BYPASS para sincronizar dados entre estações\r\n"
                            + "Corrigido salvar do campo de observação da compra\r\n"
                            + "Adicionado campo de aplicação na busca rápida de produtos"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-11-08"),
                    "Adicionada Ordem de Serviço de Transporte"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-11-11"),
                    "Refatorada montagem do xml da NFe definindo valor da fatura pelo total e não pelo parcelamento\r\n"
                            + "Adicionada mensagem de erro detalhada na consulta de certificado"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-11-12"),
                    "Refatorada lista de documentos de entrada para exibir o número da nota fiscal\r\n"
                            + "Adicionado processo para agrupar documentos de saída"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-11-13"),
                    "Removido CascadeType.ALL entre Parcela e CaixaItem e alterada ordem de salvamento nos procedimentos de baixa\r\n"
                            + "*A alteração de conexão única para unitária causou mal comportamento e lentidão nestes procedimentos\r\n"
                            + "Refatorada emissão de NFe para ignorar itens do tipo SERVIÇO\r\n"
                            + "Adicionado botão na guia de Informações adicionais para inserir os números dos documentos agrupados automaticamente\r\n"
                            + "Refatorada comandas em lista para obter valor total dos campos de cache\r\n"
                            + "*O sql anterior não considerava corretamente os descontos e acréscimos\r\n"
                            + "*A alteração da configuração do JPA deve resolver os erros ao reabrir comandas\r\n"
                            + "Refatorado Danfe para expandir campo das informações adicionais"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-11-14"),
                    "Refatorada tela de parcelamento\r\n"
                            + "Refatorado processo parcelamento e edição de parcela para melhorar o desempenho\r\n"
                            + "Refatorado processo para gerar NFe ignorando parcelas na presença de itens de SERVIÇO\r\n"
                            + "Adicionado parâmetro para alíquota da NFS-e"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-11-16"),
                    "Adicionada validação de dados fiscais ao emitir NF-e\r\n"
                            + "Adicionados parâmetros para personalizar impressão de cupom não fiscal:\r\n"
                            + "Exibir em itens: cabeçalho, número, código e unidade de medida\r\n"
                            + "Refatorada impressão da quantidade em cupom não fiscal para omitir casas decimais automaticamente\r\n"
                            + "Adicionado registro da hora de impressão do cupom não fiscal\r\n"
                            + "Adicionado botão de informações do documento na tela de documento de saída:\r\n"
                            + "Contendo data de criação, número de itens e data/hora da última impressão de cupom"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-11-18"),
                    "Adicionado campo necesidade de compra na tela de estoque do produto\r\n"
                            + "Adicionado filtro necessidade de compra na lista de produtos e serviços\r\n"
                            + "Refatorado comportamento dos filtros na lista de produtos, filtrando ao clicar em cada opção\r\n"
                            + "Adicionado filtro por status em documentos de saída\r\n"
                            + "Adicionada verificação de estoque na inserção de produtos em documentos de saída\r\n"
                            + "Adicionado parâmetro de sistema para habilitar verificação de estoque em documentos de saída\r\n"
                            + "Adicionado parâmetro para tamanho da fonte na impressão de cupom não fiscal\r\n"
                            + "Otimizadas guias nas áreas de documentos de saída e produtos e serviços, carregando dados apenas ao clicar"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-11-19"),
                    "Adicionada verificação de cadastro da Inscrição Municipal e alíquota ao gerar arquivo de NFS-e\r\n"
                            + "Corrigido agrupamento na lista de Itens Vendidos\r\n"
                            + "Adicionado nome da comanda na impressão de ticket de cozinha"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-11-21"),
                    "Adicionada tabela de preços com variação\r\n"
                            + "Adicionado campo tabela de preços no cadastro do cliente\r\n"
                            + "Adicionada automação de tabela de preços ao lançar itens em documentos de saída\r\n"
                            + "Adicionado parâmetro de sistema para o código de servido da NFS-e"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-11-22"),
                    "Refatorada emissão de Cupom Sat, impedindo reemissão indevida\r\n"
                            + "Adicionado campo Dias de Garantia em produto\r\n"
                            + "Refatorada impressão do Ticket de Cozinha para ignorar itens em status ENTREGUE"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-11-25"),
                    "Adicionado alerta de garantia de produto por veículo\r\n"
                            + "Adicionado parâmetro de sistema para alertar garantia por veículo\r\n"
                            + "Adicionada ajuda para tela de sistema sobre alguns parâmetros de venda\r\n"
                            + "Corrigida inserção de item montado:\r\n"
                            + "Removido CascadeType.ALL de movimentoFisico para montagemItens e alterada ordem das instruções para salvar a relação"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-11-26"),
                    "Refatorada exportação de NFS-e para usar tipo T (Tributado pelo Prestador) quando pessoa física\r\n"
                            + "Adicionada impressão do histórico de veículo"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-11-27"),
                    "Adicionada impressão da lista de itens vendidos\r\n"
                            + "Adicionada opção para valor fixo (ignorar tabelas de preço) no cadastro de produto"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-11-28"),
                    "Corrigido problema de gravação/exibição do horário por conta do extinto horário de verão. Foi fixado para GMT-3\r\n"
                            + "Adicionada tela de caixa por período em financeiro\r\n"
                            + "Adicionada impressão de caixa por período"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-11-29"),
                    "Adicionadas Contas Financeiras (múltiplos caixas)"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-12-02"),
                    "Adicionada validação dos campos Unidade comercial, Unidade Tributável e igualdade dos Valores Comercial e Tributável ao emitir NF-e"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-12-04"),
                    "Melhorias diversas em Contas Financeiras"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-12-05"),
                    "Melhorias no design da tela de Crediário\r\n"
                            + "Corrigido coluna de valor total do Danfe. Estava exibindo o valor unitário"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-12-06"),
                    "Corrigido campo de total de desconto ao montar XML da NF-e. Estava informando valor em percentual\r\n"
                            + "Adicionada Inutilização de Numeração de NF-e em Sistema\r\n"
                            + "Corrigido bootstrap de IBPT e CFOP"));
            
        }

        //fim
    }

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

        carregar();

        /*
        Adicionando campos da NFe: RegimeTributario, NaturezaOperacao, TipoAtendimento, ConsumidorFinal, 
        DestinoOperacao, FinalidadeEmissao
        
        Preparados campos de identificação, Emitente e Destinatário da NFe para montar XML
        
         */
        return logs;
    }
}
