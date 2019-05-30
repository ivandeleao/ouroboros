/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.nosql.LogAtualizacaoItem;

/**
 *
 * @author ivand
 */
public class Atualizacao {
    static List<LogAtualizacaoItem> logs = new ArrayList<>();

    
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
                + "Adcionada validação do meio de pagamento no parcelamento com entrada. Impede receber com Crédito Loja\r\n"
                + "Refatorado busca de produto por id para não trazer produtos excluídos\r\n"
                + "Adicionados meios de pagamento 15-Boleto Bancário e 90-Sem Pagamento\r\n"
                + "Bloqueada tela de faturamento quando já recebido a vista e não há valor faturado\r\n"
                + "Refatorado exibição do valor recebido (a vista) em documentos de saídas"));
        
        logs.add(new LogAtualizacaoItem(LocalDate.parse("2019-05-29"),
                "Adicionado id do cliente na impressão térmica\r\n"));
        
        return logs;
    }
}
