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
                    + "Refatorado parcelamento com primeira parcela para o dia (à vista). Adicionado método para salvar o item no caixa\r\n"
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
                    + "Bloqueada tela de faturamento quando já recebido à vista e não há valor faturado\r\n"
                    + "Refatorado exibição do valor recebido (à vista) em documentos de saídas"));

            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-05-28"),
                    "Adicionado id do cliente na impressão térmica"));

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
                    "Corrigido baixa de parcela à vista na compra. Lançava como crédito ao invés de débito"));
            
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
                    "Adicionadas Contas Financeiras"));
            
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
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-12-19"),
                    "Criada nova tela de recebimento de parcelas com opção de desconto e acréscimo em percentual e monetário\r\n"
                            + "Definido arredondamento no cálculo de acréscimo e desconto em percentual nas parcelas\r\n"
                            + "Melhorias diversas em Contas Financeiras. Adicionada data base das contas\r\n"
                            + "Adicionado número da comanda na impressão de cupom não fiscal\r\n"
                            + "Adicionado parâmetro para personalizar impressão de cupom não fiscal:\r\n"
                            + "Exibir em itens: acréscimo/desconto"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-12-20"),
                    "Adicionada opção (em informações do documento) para alterar a data dos documentos de saída"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-12-23"),
                    "Refatorada criação de conta financeira definindo data inicial\r\n"
                            + "Adicionado funcionário em item de documento de saída (movimentoFisico)\r\n"
                            + "Refatorada formatação da coluna quantidade em documento de saída para exibir inteiro ou decimal dinamicamente"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-01-03"),
                    "Adicionado Histórico por Item em Funcionário"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-01-05"),
                    "Adicionados parâmetros para funcionário em item de documento de saída\r\n"
                            + "Melhorias diversas para funcionário por item"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-01-06"),
                    "Adicionado filtro por datas e check para selecionar todos na pesquisa de documentos de saída\r\n"
                            + "Melhoria no desempenho ao remover documento agrupado:\r\n"
                            + "Foi removido da rotina o salvamento do documento pai, pois era desnecessário\r\n"
                            + "Melhoria no desempenho ao abrir documento:\r\n"
                            + "Foi refatorado listener da tabela de itens para salvar apenas ao encerrar edição de célula"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-01-07"),
                    "Adicionado Histórico por Documento em Funcionário"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-01-08"),
                    "Adicionado campo de estoque mínimo em produto\r\n"
                            + "Adicionado campo de cache do estoque atual dos produtos e refatorados métodos relacionados\r\n"
                            + "Definidos os arredondamentos nos métodos em Venda para não dar diferença nos totais ao reabrir o documento: \r\n"
                            + "setTotalProdutos, setTotalServicos, getTotalItensProdutos, getTotalItensServicos\r\n"
                            + "Adicionado filtro de estoque mínimo na lista de produtos\r\n"
                            + "Redesenhada tela lista de produtos e serviços\r\n"
                            + "Refatorado método Produto.getEstoqueAtualComUnidade para ignorar itens do tipo SERVIÇO"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-01-13"),
                    "Adicionado dados de combustível em item da NF-e"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-01-14"),
                    "Corrigido acréscimo e desconto fantasma ao editar item de venda: criava valor monetário erroneamente\r\n"
                            + "Refatorado parcelas da NF-e para incluir os recebimentos à vista"
                            + "Adicionada correção paleativa para atualizar estoque de componente (precisa clicar em atualizar no estoque)"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-01-15"),
                    "Adicionados dados do transportador na NF-e\r\n"
                            + "Corrigido tratamento de caracteres especiais para os campos xNome da NF-e\r\n"
                            + "Corrigido carregamento dos campos de base de cálculo de ICMS E ICMS ST em itens da NF-e\r\n"
                            + "Adicionada coluna de estoque na pesquisa para entrada de produto"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-01-16"),
                    "Removido CascadeType.ALL da relação Venda MovimentoFisico\r\n"
                            + "Adicionado botão do Cupom Sat na tela de Ordem de Serviço\r\n"
                            + "Redesenhada tela de configuração de Impressão\r\n"
                            + "Adicionado parâmetro para exibir/ocultar acréscimo na impressão A4\r\n"
                            + "Refatorada pesquisa de Endereço para listar 100 registros (antes eram 50)\r\n"
                            + "Adicionada pesquisa de município no cadastro de clientes e fornecedores"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-01-17"),
                    "Invertida a ordem de salvamento do item/venda em compra para estabelecer a relação\r\n"
                            + "Alterado tipo Timestamp para LocalDateTime em Caixa\r\n"
                            + "Adicionada impressão do caixa\r\n"
                            + "Redesenhados botões no caixa"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-01-18"),
                    "Adicionado parâmetro para exibir/ocultar observação na impressão A4\r\n"
                            + "Adicionado parâmetro para exibir/ocultar assinatura do cliente na impressão de Cupom não Fiscal"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-01-19"),
                    "Adicionada coluna número da NF-e na lista de Documentos de Entrada\r\n"
                            + "Refatorada definição de tamanho dos campos:\r\n"
                            + "conteudoQuantidade em produto para 20,3\r\n"
                            + "valor em movimentoFisico para 21,10\r\n"
                            + "Definido arredondamento em MovimentoFisico.getSubtotalItem()"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-01-20"),
                    "Adicionado saldo geral em conta fluxo\r\n"
                            + "Refatorada definição dos campos saida e entrada para 20,4\r\n"
                            + "Redefinido arredondamento no método VendaView().calcularSubtotalReverso para 4 casas\r\n"
                            + "Removido Texto.parse() do método Texto.removerAcentos()"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-01-21"),
                    "Corrigido salvamento de item de documento ao editar na grade\r\n"
                            + "Adicionados dados de combustível no cadastro do produto"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-01-22"),
                    "Corrigido salvamento de status no documento de saída executando save nos movimentosFisicos\r\n"
                            + "Corrigido salvar Destino da Operação nas configurações da NF-e\r\n"
                            + "Corrigido recarregar Finalidadeda de Emissão no Detalhe da NF-e\r\n"
                            + "Refatorado método venda.getTotalItens() para retornar o valor bruto (sem acréscimos e descontos)"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-01-23"),
                    "Corrigido exibir Saldo Geral em Conta Fluxo após fechar contas\r\n"
                            + "Adicionado coluna saldo e campo total na tela de Contas\r\n"
                            + "Reduzido período para 1 dia ao abrir tela de Conta Fluxo"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-01-24"),
                    "Adicionada validação para emitr NF-e dos campos Modalidade BC ICMS e ICMS ST\r\n"
                            + "Adicionado cadastro de Cartões e Taxas em Financeiro\r\n"
                            + "Corrigido código IBGE do município Campo Grande - MS\r\n"
                            + "Aumentada precisão para calcular proporção de produto componente de 3 para 10 casas\r\n"
                            + "Adicionada coluna da data de criação na tela de componentes\r\n"
                            + "Adicionado campo para pré-visualizar o novo saldo em lançamento manual de estoque "));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-01-27"),
                    "Refatorado método para obter itens de documento de saída mesmo que a quantidade do mesmo seja zero\r\n"
                            + "Refatorado MontarXml para cortar descrição do produto em 120 caracteres\r\n"
                            + "Redefinido tamanho da descrição em item de documento de saída para 1000 caracteres\r\n"
                            + "Adicionada validação para o tamanho da descrição ao inserir item no documento de saída\r\n"
                            + "Refatorado método Venda.getMovimentosFisicosSaida() para ignorar origem de componente"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-01-29"),
                    "Adicionado parâmetro da Versão do Layout do Sat\r\n"
                            + "Adicionada opção para incluir a taxa de cartão ou não no valor do documento"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-01-30"),
                    "Refatorado método carregarTabela() em Conta Fluxo para atualizar corretamente o saldo geral"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-01-31"),
                    "Liberados botões de impressão em documentos agrupados"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-02-05"),
                    "Refatorados métodos de distribuição de acréscimos e desccontos em documentos para salvar atomicamente\r\n"
                            + "Refatorada busca de produto em documentos de saída para identificar corretamente itens do tipo balança"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-02-10"),
                    "Corrigido código IBGE do município Amparo - SP"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-02-11"),
                    "Corrigido o sequenciamento da NFe (múltiplas estações), removendo a constante de sessão do número"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-02-13"),
                    "Adicionado Cartões a Receber em Financeiro\r\n"
                            + "Adicionado botão para abrir documento de origem em estoque"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-02-14"),
                    "Adicionado totalizador para itens selecionados em Cartões a Receber"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-02-18"),
                    "Refatorado método venda.getParcelas() ordenando por id"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-02-19"),
                    "Adicionado campo Tipo de Operação em Detalhe da NF-e\r\n"
                            + "Adicionado botão para transferir em Conta Fluxo"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-02-20"),
                    "Adicionado botão para transferir em Caixa\r\n"
                            + "Rediagramada tabela da tela de Caixa\r\n"
                            + "Adicionada seleção da Conta para pagamento de Conta Programada\r\n"
                            + "Rediagramada tela de pagamento de Conta Programada"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-02-21"),
                    "Adicionado campo de Total Atualizado no crediário do cliente"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-02-22"),
                    "Refatorada validação da NF-e para os campos Modalidade BC ICMS e Modalidade BC ICMS ST\r\n"
                            + "Adicionado filtro por descrição em Itens Vendidos"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-02-23"),
                    "Redesenhados botões em Contas a Pagar\r\n"
                            + "Adicionado botão para abrir documento em Contas a Pagar\r\n"
                            + "Adicionado botão para imprimir em Contas a Pagar\r\n"
                            + "Refatorado método Parcela.getDescricao() para exibir corretamente o tipo de documento"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-02-24"),
                    "Refatorados totais nas telas de Crediário e Contas a Receber\r\n"
                            + "Adicionado botão para imprimir em Crediário e Contas a Receber"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-02-25"),
                    "Refatorado método Parcela.getStatus() para manter como vencido mesmo com baixa parcial"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-02-26"),
                    "Adicionado parâmetro para exibir/ocultar meios de pagamento na impressão de Cupom não Fiscal\r\n"
                            + "Refatorada impressão de item de cupom não fiscal colocando a descrição na mesma linha dos outros dados\r\n"
                            + "Corrigida validação de estoque ao inserir item em documento de saída\r\n"
                            + "Refatorada validação de estoque para ignorar quando for Orçamento\r\n"
                            + "Removida impressão em console da quantidade de tamanhos de um produto, acelerando a busca e listagem de produtos e serviços"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-02-26"),
                    "Refatorada Lista de Contas Fluxo para exibir também contas do tipo Caixa\r\n"
                            + "Refatorado método Parcela.getValorAtual() para exibir corretamente status de parcela de cartão"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-02-27"),
                    "Refatorado método Pessoa.getTotalEmAtraso() para usar valorAtual() ao invés de valor() das parcelas\r\n"
                            + "Refatorado método Parcela.getValorAtual() para exibir corretamente status de parcela de cartão\r\n"
                            + "Redesenhado carnê e incluído valor em atraso\r\n"
                            + "Renomeado arquivo report DanilaCarne.jasper para Carne.jasper"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-02-28"),
                    "Refatorada tela de Caixa e métodos relacionados para suportar múltiplos Caixas\r\n"
                            + "Redesenhada tela de Caixas (Turnos)\r\n"
                            + "Adicionado parâmetro de configuração para determinar o Caixa Principal\r\n"
                            + "Refatorado método CaixaDAO.getLastCaixa() para usar o Caixa Principal\r\n"
                            + "Refatorado método ContaDAO.getSaldo() para obter saldo do último turno em contas do tipo Caixa\r\n"
                            + "Adicionado filtro por tipo na lista de Contas\r\n"
                            + "Adicionado botão para Contas no Caixa"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-02-29"),
                    "Refatorado método MeioDePagamentoDAO.bootstrap() para usar id ao invés de codigoSAT"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-03-02"),
                    "Adicionada entidade ProdutoImagem"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-03-04"),
                    "Adicionada tela de Imagens de Produto"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-03-05"),
                    "Adicionado filtro e coluna indicadora para item excluído em Produtos e Serviços\r\n"
                            + "Adicionado botão para desfazer exclusão no cadastro de Produtos e Serviços\r\n"
                            + "Otimizados filtros da tela de Produtos e Serviços"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-03-06"),
                    "Adicionada Marca de Produto"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-03-07"),
                    "Removido CascadeType.ALL e orphanRemoval na relação Categoria e Tamanho\r\n"
                            + "Adicionada Subcategoria de Produto\r\n"
                            + "Refatorada lista de produtos e serviços"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-03-09"),
                    "Adicionada entidade IPI"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-03-10"),
                    "Adicionados campos de IPI no Cadastro de Produto"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-03-11"),
                    "Refatorado método Texto.removerAcentos para substituir caractere º por o:\r\n"
                            + "Anteriormente removia o caracter, dando problema no arquivo de NFS-e"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-03-12"),
                    "Adicionada VENDA POR FICHA"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-03-13"),
                    "Refatorada relação Usuario-Diretiva trocando de Set para List\r\n"
                            + "Melhorias diversas no cadastro de usuário e diretivas\r\n"
                            + "Removidos recursos de usuário: SISTEMA E USUÁRIOS\r\n"
                            + "Refatorado método de liberação de usuário para solicitar login administrativo em caso de recurso bloqueado\r\n"
                            + "Adicionados recursos de usuário: DELIVERY, FUNCIONÁRIO, VENDA POR FICHA E VEÍCULOS"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-03-15"),
                    "Adicionada impressão para bobina em Itens Vendidos"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-03-18"),
                    "Adicionados campos de IPI em Item de Documento de Saída\r\n"
                            + "Refatorados métodos para montar XML da NFe referentes a PIS e COFINS"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-03-19"),
                    "Adicionados campos de IPI na NF-e\r\n"
                            + "Melhorias diversas referentes a dados fiscais"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-03-21"),
                    "Refatorado montarXml para aparar endereço do transportador em 60 caracteres\r\n"
                            + "Adicionados Motivos de Desoneração códigos 12, 16 e 90 da NFe\r\n"
                            + "Adicionados campos de repasse do ICMS: vBCSTDest e vICMSSTDest"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-03-25"),
                    "Refatorados métodos relacionados aos códigos de ICMS para contemplar a Tributação Normal"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-03-26"),
                    "Refatorado procedimento para agrupar documentos para referenciar automaticamente chave de documentos com Cupom Sat\r\n"
                            + "Refatorado procedimento para remover documento agrupado desreferenciar automaticamente chave de Cupom Sat\r\n"
                            + "Removido CascadeType.ALL da relação Venda-DocumentoReferenciado\r\n"
                            + "Refatorado procedimento para remoção de documentos referenciados"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-03-27"),
                    "Adicionados dados do Transportador no Danfe\r\n"
                            + "Adicionados dados do Veículo de Transporte na NF-e\r\n"
                            + "Adicionado campo id no filtro da lista de clientes e fornecedores\r\n"
                            + "Refatorado o campo busca rápida na lista de clientes e fornecedores\r\n"
                            + "Corrigido método de estorno de caixa/conta\r\n"
                            + "Adicionado botão de informações do documento com a opção de alteração de data em Documentos de Entrada\r\n"
                            + "Adicionada informação de Valor em Atraso na impressão de bobina de Documentos de Saída"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-03-28"),
                    "Refatorado Documento de Entrada para permitir edição de Valor e Quantidade direto na grade de itens"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-03-30"),
                    "Corrigida importação de Compra por XML para registrar a unidade de medida"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-04-03"),
                    "Corrigido histórico do veículo para exibir documentos agrupados também"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-04-06"),
                    "Adicionado parâmetro IMPRESSORA_CUPOM_MARGEM_CORTE para garantir espaço inferior em impressoras térmicas"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-04-07"),
                    "Refatorado método Parcela.getDiasEmAtraso() para considerar acréscimos e descontos da parcela"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-04-09"),
                    "Corrigido novamente problema de horário de verão. Linha da correção estava comentada desde fevereiro por testes"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-04-13"),
                    "Adicionada integração de boletos no padrão Sicredi"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-04-14"),
                    "Alterado título dos total em aberto para total vencido na impressão de documentos de saída\r\n"
                            + "Criado novo recibo exibindo apenas os valores recebidos no momento"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-04-15"),
                    "Corrigido salvamento de acréscimos e descontos de serviços em documentos de saída"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-04-23"),
                    "Adicionada validação dos dados de endereço do cliente ao emitir NF-e\r\n"
                            + "Refatorado emitissão de NF-e para recarregar o cliente, validando eventuais alterações no cadastro\r\n"
                            + "Refatorada inserção de item em documentos de saída para carregar UF padrão de combustível\r\n"
                            + "Adicionado cadastro de cheques\r\n"
                            + "Melhorias na integração de boletos"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-04-24"),
                    "Adicionada lista de lançamentos anteriores na tela de recebimento/pagamento\r\n"
                            + "Melhorias diversas para cheques\r\n"
                            + "Renomeada a guia Conta Fluxo para Conta Corrente"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-04-25"),
                    "Adicionado campo de cache do saldo das contas e refatorados métodos relacionados\r\n"
                            + "Refatorado pagamento em contas a pagar para nova tela com seleção de conta/caixa (já usada em contas a receber)"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-04-26"),
                    "Adicionado parâmetro para imprimir produtos e serviços separados em documentos de saída"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-04-27"),
                    "Adicionada nota promissória em documentos de saída\r\n"
                            + "Adicionado parâmetro de funcionário obrigatório para documentos de saída"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-04-28"),
                    "Adicionado parâmetro para tipo de nota promissória"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-04-29"),
                    "Adicionada opção de vínculo de cheque em parcela\r\n"
                            + "Melhorias em cheques\r\n"
                            + "Aumentada linha de assinatura e espaçamento do endereço em promissórias"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-04-30"),
                    "Otimizado processo de lançamento de descontos e acréscimos em documentos de saída\r\n"
                            + "Adicionado processo para atualizar linha da tabela em documentos de saída"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-05-04"),
                    "Adicionada impressão de documentos de saída em A4\r\n"
                            + "Redesenhada tela de documentos de saída"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-05-06"),
                    "Refatorado pagamento em parcelas a pagar (fornecedor) para nova tela com seleção de conta/caixa"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-05-07"),
                    "Adicionado campo de Alíquota NFS-e em documento de saída\r\n"
                            + "Refatorada exportação do arquivo NFS-e"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-05-11"),
                    "Criado dashboard com agenda\r\n"
                            + "Movido botão de backup para o dashboard\r\n"
                            + "Adicionado registro e alerta de backup"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-05-12"),
                    "Movido botão de configuração do sistema para o dashboard\r\n"
                            + "Adicionado vendedor padrão no cadastro de cliente\r\n"
                            + "Adicionada data do documento de saída na impressão de bobina térmica\r\n"
                            + "Adicionado o termo 'impresso em' no rodapé da impressão de bobina térmica do docucmento de saída"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-05-21"),
                    "Adicionado relatório de Vendas - Produtos por Vendedor em Documentos de Saída"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-05-22"),
                    "Adicionado relatório de Vendas - Vendedores por Produto em Documentos de Saída\r\n"
                            + "Refatorado método de atualização do saldo para melhorar desempenho ao lançar registros nas contas\r\n"
                            + "refatorado processo de atualização da tela de contas a receber após recebimento ou edição para mehorar o desempenho"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-05-22"),
                    "Corrigido campo bairro do fornecedor na importação de XML de compra"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-06-02"),
                    "Melhorias de desempenho nos procedimentos de suprimento, sangria, transferência e estorno do caixa"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-06-03"),
                    "Melhorias de desempenho nos procedimentos de suprimento, sangria, transferência e estorno da conta corrente"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-06-04"),
                    "Melhorias nos procedimentos de suprimento, sangria, transferência e estorno do caixa e da conta corrente\r\n"
                            + "Adicionado campo para informar data real do pagamento/recebimento\r\n"
                            + "Adicionada visualização de diversos campos na edição da parcela"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-06-05"),
                    "Adicionado modelo de etiqueta de produtos A4 - 63,5mm x 46,5mm"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-06-15"),
                    "Refatorado procedimento para gerar parcela de recebimento à vista para registrar o meio de pagamento\r\n"
                            + "Adicionado relatório de Vendas - Faturamento por Período por Vendedor em Documentos de Saída\r\n"
                            + "Corrigido cáculo DV10 do boleto Sicredi"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-06-17"),
                    "Refatorado filtro de crediário para exibir as parcelas à vista"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-06-20"),
                    "Adicionado campo bonificação para itens de venda"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-06-29"),
                    "Adicionado relatório de Vendas Diárias por Vendedor em Documentos de Saída"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-07-01"),
                    "Adicionado relatório de Bonificações por Cidade por Vendedor em Documentos de Saída"
                            + "Adicionado suporte (poi-3.10.1) para exportação dos relatórios para Excel"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-07-02"),
                    "Adicionado parâmetro para Bonificação em Documentos de Saída\r\n"
                            + "Adicionado trim no método movimentoFisico.setDescricao, pois espaço em branco acidental no início causava erro eo emitir NF-e\r\n"
                            + "Adicionado preenchimento automático de CPF/CNPJ ao emitir um Cupom Sat em documento com cliente selecionado\r\n"
                            + "Corrigida formatação ao colar CNPJ em caixas de texto para CPF/CNPJ"
                            + "Adicionadas colunas de Marca e Estoque na Lista e na Busca de Produtos e Serviços"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-07-06"),
                    "Ajustes diversos nos relatórios em Documentos de saída"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-07-07"),
                    "Adicionado totais no relatório de Vendas - Faturamento por Período por Vendedor"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-07-08"),
                    "Refatorado método Venda.getParcelasAVista() para considerar apenas parcelas com status QUITADO"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-07-10"),
                    "Atualizado campo dataRecebimento para dataHoraRecebimento\r\n"
                            + "Refatorado método CaixaItemDAO.save para preencher dataRecebimento padrão\r\n"
                            + "Adicionada opção de alteração da data de recebimento na tela de recebimento à vista\r\n"
                            + "Adicionado botão para fechar e abrir novo em documento de saída"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-07-15"),
                    "Corrigido campo usado para data de recebimento no relatório de Vendas - Faturamento por Período por Vendedor"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-07-16"),
                    "Corrigido valores à vista de cheque e dinheiro (com estornos) no relatório de Vendas - Faturamento por Período por Vendedor\r\n"
                            + "Refatorado campos com valores decimais para formatar dinamicamente mais casas"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-07-22"),
                    "Adicionado relatório de Faturamento por Período no Caixa"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-07-23"),
                    "Adicionado padrão Filizola Smart na exportação de produtos para balança\r\n"
                            + "Adicionado ícone para identificar item de balança nas listas de catálogo"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-07-27"),
                    "Corrigdo relatório Faturamento por Período para gerar mesmo sem registros em alguma seção"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-07-29"),
                    "Adicionado botão de impressão personalizada em Documento de Saída\r\n"
                            + "Renomeado ORDEM DE SERVIÇO para apenas OS"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-07-31"),
                    "Adicionado botão de impressão TXT em Documento de Saída\r\n"
                            + "Alterado relatório de saldo em Clientes e Fornecedores para exibir apenas com saldo devedor\r\n"
                            + "Alterado nome de exibição para nome fantasia nos relatório de Documentos de Saída e Saldo de Cliente"));
            
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-08-03"),
                    "Refatorado relatório de Saldo de Cliente para exibir valor atualizado (considerando baixas parciais, juros e multa)\r\n"
                            + "Adicionada etiqueta modelo A4 - 50,0mm x 25,0mm em Produtos e Serviços"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-08-04"),
                    "Adicionado parâmetro IMPRESSORA_RECIBO_VIAS"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-08-05"),
                    "Adicionado campo para editar número da NF-e em Informações do Documento de Entrada\r\n"
                            + "Refatorado campo descrição do item em Documento de Entrada permitindo a edição antes de inserir\r\n"
                            + "Melhorado o método para ajustar as casas decimais"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-08-06"),
                    "Redesenhada etiqueta modelo A4 - 50,0mm x 25,0mm adicionando unidade de medida\r\n"
                            + "Corrigido relatório VendasProdutosPorCidade.jasper pois não exibia algumas células ao exportar para Excel\r\n"
                            + "Adicionado status ORÇAMENTO no filtro em Documentos de Saída"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-08-07"),
                    "Adicionado status LIBERADO em Documento de Saída\r\n"
                            + "Refatorada tela de status do Documento de Saída\r\n"
                            + "Corrigido controle de acesso para a tela de Funcionários\r\n"
                            + "Adicionada restrição de acesso apenas por administrador para Cancelamento de Documento"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-08-12"),
                    "Adicionado índice para o campo MovimentoFisico.dataLiberado\r\n"
                            + "Corrigido método de pesquisa de produto - não estava encerrando a conexão com o banco de dados"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-08-17"),
                    "Adicionado CaixaItemTipo FUNCIONÁRIO\r\n"
                            + "Refatorado backup para nova credencial do banco de dados"));
            
            logs.add(new LogAtualizacaoItem(LocalDate.parse("2020-08-21"),
                    "Refatorado exibição de endereço de funcionário\r\n"
                            + "Renomeada tela Recebimento/Pagamento para Lançamento\r\n"
                            + "Adicionada coluna Conta/Caixa na lista de Lançamentos Anteriores"));
            
            
            
            //Adicionados campos de comissão em funcionário
            //Refatorado Histórico por Documento em Funcionário com informações de comissão
            
            //this.setTitle(this.getTitle() + " (" + this.getClass().getCanonicalName() + ")");
            
            
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
