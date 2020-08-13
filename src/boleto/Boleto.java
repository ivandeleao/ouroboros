/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boleto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JOptionPane;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.financeiro.Conta;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.dao.principal.ParcelaDAO;
import model.mysql.dao.principal.financeiro.ContaDAO;
import model.nosql.TipoCalculoEnum;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.DateTime;
import util.Decimal;
import util.MwIOFile;
import util.Texto;

/**
 *
 * @author ivand
 */
public class Boleto {

    public static Parcela prepararBoleto(Conta conta, Parcela parcela) {
        ParcelaDAO parcelaDAO = new ParcelaDAO();
        
        if (parcela.getBoletoCodigoBarras().isEmpty()) { //Não sobrescrever
            String boletoByte = conta.getBoletoByte().toString();

            String sequencial = Texto.padLeftAndCut((conta.getBoletoSequencial()).toString(), 5, '0'); //5 dígitos
            conta.setBoletoSequencial(conta.getBoletoSequencial() + 1);
            new ContaDAO().save(conta);

            String ano = String.valueOf(LocalDate.now().getYear()).substring(2); // dois dígitos
            String dv = Boleto.calcularDv11(conta, ano, boletoByte, sequencial);

            String vencimento = Texto.soNumeros(DateTime.toString(parcela.getVencimento())); //sem barras
            String valor = Texto.soNumeros(Decimal.toString(parcela.getValor())); //sem vírgula

            String agencia = conta.getAgencia();
            String posto = conta.getPosto();
            String cedente = conta.getCedente();

            String nossoNumero = Boleto.gerarNossoNumero(ano, boletoByte, sequencial, dv);

            String codigoBarras = Boleto.gerarCodigoBarras(vencimento, valor, nossoNumero, agencia, posto, cedente);

            parcela.setBoletoSequencial(sequencial);
            parcela.setBoletoAno(ano);
            parcela.setBoletoByte(boletoByte);
            parcela.setBoletoDv(dv);
            parcela.setBoletoNossoNumero(formatarNossoNumero(nossoNumero));
            parcela.setBoletoCodigoBarras(codigoBarras);
        }
        
        return parcelaDAO.save(parcela);

    }
    
    public static void gerarArquivo(Conta conta, List<Parcela> parcelas) {
        /* Padrão Sicredi CNAB 240
        Nomenclatura do Arquivo: CCCCCMDD.XXX
        CCCCC - código beneficiário
        MDD - código do mês e número do dia da data de geração do arquivo
        XXX - Extensão irrelevante - Não usar CRT pois são usadas em arquivos de retorno
        Codificação dos meses: Jan =1, Fev = 2, ... Out = O, Nov = N, Dez = D 
        Linha Finalizador - "enter"
         */
        String mesCodigo = LocalDate.now().getMonthValue() < 10 ? 
                String.valueOf(LocalDate.now().getMonthValue()) :
                LocalDate.now().getMonth().toString().substring(0, 1);
        
        String arquivoNome = Texto.padLeftAndCut(conta.getCedente(), 5, '0') + mesCodigo + Texto.padLeftAndCut(String.valueOf(LocalDate.now().getDayOfMonth()), 2, '0');
        String caminho = "custom//boleto//remessa//" + arquivoNome + ".txt";

        MwIOFile.writeFile(gerarConteudo(conta, parcelas), caminho);
        
        //avançar NSA
        conta.setBoletoSequencialArquivo(conta.getBoletoSequencialArquivo() + 1);
        new ContaDAO().save(conta);

        try {
            System.out.println("app path: " + Ouroboros.APP_PATH);
            Runtime.getRuntime().exec("explorer.exe " + Ouroboros.APP_PATH + "custom\\boleto\\remessa\\");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Erro ao salvar o arquivo " + e, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static String gerarConteudo(Conta conta, List<Parcela> parcelas) {
        ParcelaDAO parcelaDAO = new ParcelaDAO();

        String data = Texto.soNumeros(DateTime.toString(LocalDate.now()));
        String hora = Texto.soNumeros(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")));

        String str = "";
        str += gerarHeaderArquivo(conta, data, hora);

        //String agencia = conta.getAgencia();
        //String posto = conta.getPosto();
        //String contaCorrente = conta.getContaCorrente();
        //String ano = String.valueOf(LocalDate.now().getYear()).substring(2); // dois dígitos
        //String b = conta.getBoletoByte().toString();
        
        String lote = "0001"; //sequencial dentro do arquivo - aparentemente posso deixar fixo

        
        str += gerarHeaderLote(conta, lote, data);

        int sequencialRegistro = 0;
        for (Parcela parcela : parcelas) {
            parcela = Boleto.prepararBoleto(conta, parcela);
            
            String sequencial = parcela.getBoletoSequencial();
            String ano = parcela.getBoletoAno();
            String b = parcela.getBoletoByte();
            String dv = parcela.getBoletoDv();
            String nossoNumero = Boleto.gerarNossoNumero(ano, b, sequencial, dv);
            
            Venda documento = parcela.getVenda();
            String documentoId = documento.getId().toString();
            String vencimento = Texto.soNumeros(DateTime.toString(parcela.getVencimento())); //DDMMAAAA
            String valor = Texto.soNumeros(Decimal.toString(parcela.getValor())); //Nv2 sem vírgula
            String dataEmissaoTitulo = Texto.soNumeros(DateTime.toStringDate(documento.getCriacao())); //DDMMAAAA;
            String jurosCodigo = parcela.getJurosTipo().equals(TipoCalculoEnum.VALOR) ? "1" : "2";
            String jurosValor;
            if (parcela.getJurosTipo().equals(TipoCalculoEnum.VALOR)) {
                System.out.println("valor: " + parcela.getJurosMonetarioDiario());
                jurosValor = Texto.soNumeros(Decimal.toString(parcela.getJurosMonetarioDiario()));
            } else {
                System.out.println("percentual: " + parcela.getJurosPercentual());
                jurosValor = Texto.soNumeros(Decimal.toString(parcela.getJurosPercentual()));
            }
            
            String multaValor = Texto.soNumeros(Decimal.toString(parcela.getMulta()));
            
            sequencialRegistro++;
            str += gerarRegistroDetalheSegmentoP(conta, lote, dataEmissaoTitulo, sequencialRegistro, nossoNumero, documentoId, vencimento, valor, jurosCodigo, jurosValor);
            
            sequencialRegistro++;
            str += gerarRegistroDetalheSegmentoQ(documento.getPessoa(), lote, dataEmissaoTitulo, sequencialRegistro, nossoNumero, documentoId, vencimento, valor);
        
            sequencialRegistro++;
            str += gerarRegistroDetalheSegmentoR(conta, lote, sequencialRegistro, multaValor);
            
            parcela.setBoletoRemessa(LocalDateTime.now());
            parcelaDAO.save(parcela);
        }
        
        
        str += gerarTrailerLote(lote, sequencialRegistro + 2);

        //
        str += gerarTrailerArquivo("1", sequencialRegistro + 4);

        return Texto.removerAcentos(str);
    }

    public static String gerarHeaderArquivo(Conta conta, String data, String hora) {
        String str = "";

        //Controle
        str += "748";                                           //3 - Banco
        str += "0000";                                          //4 - Lote
        str += "0";                                             //1 - Registro - 0=header do arquivo

        //CNAB
        str += Texto.padRightAndCut("", 9);                           //9 - Texto de observações destinado para uso exclusivo do SICREDI

        //Empresa
        str += "2";                                             //1 - Incrição - Tipo - CNPJ
        str += Texto.padLeftAndCut(Texto.soNumeros(Ouroboros.EMPRESA_CNPJ), 14, '0'); //14 - Incrição - Número do CNPJ
        str += Texto.padRightAndCut("", 20);                          //20 - Convênio

        str += Texto.padLeftAndCut(conta.getAgencia(), 5, '0');       //5 - Agência
        str += Texto.padLeftAndCut(conta.getAgenciaDv(), 1);          //1 - Agência- DV
        str += Texto.padLeftAndCut(conta.getContaCorrente(), 12, '0');    //12 - Beneficiário - Código
        str += Texto.padLeftAndCut(conta.getContaCorrenteDv(), 1);    //1 - Beneficiário - DV
        str += " ";                                             //1 - DV - Não utilizado
        str += Texto.padRightAndCut(Texto.substring(Ouroboros.EMPRESA_RAZAO_SOCIAL, 0, 30), 30); //30 - Nome da Empresa

        //Nome do Banco
        str += Texto.padRightAndCut("SICREDI", 30);                   //30 - Nome do Banco

        //CNAB
        str += Texto.padRightAndCut("", 10);                          //10 - Texto de observações destinado para uso exclusivo do SICREDI

        //Arquivo
        str += "1";                                             //1 - Código remessa / retorno
        str += data;                                            //8 - Data de Geração - DDMMAAAA
        str += hora;                                            //8 - Hora de Geração - HHMMSS
        str += Texto.padLeftAndCut(conta.getBoletoSequencialArquivo().toString(), 6, '0'); //6 - Sequência (NSA) Número sequencial do arquivo
        str += "081";                                           //3 - Leiaute do Arquivo
        str += "01600";                                         //5 - Densidade

        //Reservado Banco
        str += Texto.padRightAndCut("", 20);                          //20 - Texto de observações destinado para uso exclusivo do banco

        //Reservado Empresa
        str += Texto.padRightAndCut("", 20);                          //20 - Texto de observações destinado para uso exclusivo da empresa

        //CNAB
        str += Texto.padRightAndCut("", 29);                          //29 - Texto de observações destinado para uso exclusivo do banco

        str += System.lineSeparator();

        return str;
    }

    public static String gerarHeaderLote(Conta conta, String lote, String data) {
        String str = "";

        //Controle
        str += "748";                                           //3 - Banco
        str += lote;                                            //4 - Lote de serviço (Sequencial)
        str += "1";                                             //1 - Registro - 1=header de lote

        //Serviço
        str += "R";                                             //1 - Operação R=Remessa ou T=Retorno
        str += "01";                                            //2 - Serviço 01=Cobrança
        str += "  ";                                            //2 - Texto de observações destinado para uso exclusivo do SICREDI
        str += "040";                                           //3 - Leiaute do Lote

        //CNAB
        str += " ";                                             //1 - Texto de observações destinado para uso exclusivo do SICREDI

        //Empresa
        str += "2";                                             //1 - Incrição - Tipo - CNPJ
        str += Texto.padLeftAndCut(Texto.soNumeros(Ouroboros.EMPRESA_CNPJ), 15, '0'); //15 - (*É DIFERENTE MESMO) Incrição - Número do CNPJ
        str += Texto.padRightAndCut("", 20);                          //20 - Convênio

        //Conta Corrente
        str += Texto.padLeftAndCut(conta.getAgencia(), 5, '0');       //5 - Agência
        str += Texto.padLeftAndCut(conta.getAgenciaDv(), 1);          //1 - Agência- DV
        str += Texto.padLeftAndCut(conta.getContaCorrente(), 12, '0');    //12 - Beneficiário - Código
        str += Texto.padLeftAndCut(conta.getContaCorrenteDv(), 1);    //1 - Beneficiário - DV
        str += " ";                                             //1 - DV - Não utilizado
        str += Texto.padRightAndCut(Texto.substring(Ouroboros.EMPRESA_RAZAO_SOCIAL, 0, 30), 30); //30 - Nome da Empresa

        //Informação 1
        str += Texto.padRightAndCut("", 40);                          //40 - (C073) O Sicredi atualmente não utiliza este campo 

        //Informação 2
        str += Texto.padRightAndCut("", 40);                          //40 - (C073) O Sicredi atualmente não utiliza este campo 

        //Controle da Cobrança
        str += Texto.padLeftAndCut(conta.getBoletoSequencialArquivo().toString(), 8, '0');  //8 - Número remessa / retorno *** igual ao NSA
        str += data;                                            //8 - Data de gravação remessa / retorno DDMMAAAA
        str += Texto.padLeftAndCut("", 8, '0');                       //8 - Data do crédito - Sicredi não utilizará esse campo

        //CNAB
        str += Texto.padRightAndCut("", 33);                          //33 - Texto de observações destinado para uso exclusivo do SICREDI

        str += System.lineSeparator();

        return str;
    }

    public static String gerarRegistroDetalheSegmentoP(Conta conta, String lote, String dataEmissaoTitulo, Integer sequencialRegistro, String nossoNumero, String documentoId, String vencimento, String valor, String jurosCodigo, String jurosValor) {
        String str = "";

        //Controle
        str += "748";                                           //3 - Banco
        str += lote;                                            //4 - Lote de serviço (Sequencial)
        str += "3";                                             //1 - Registro - 3=detalhe

        //Serviço
        str += Texto.padLeftAndCut(sequencialRegistro.toString(), 5, '0');    //5 - Número do registro (sequencial)
        str += "P";                                             //1 - Segmento
        str += " ";                                             //1 - CNAB Texto de observações destinado para uso exclusivo do SICREDI
        str += "01";                                            //2 - Código de movimento remessa 01=entrada de títulos

        //Conta Corrente
        str += Texto.padLeftAndCut(conta.getAgencia(), 5, '0');       //5 - Agência
        str += Texto.padLeftAndCut(conta.getAgenciaDv(), 1);          //1 - Agência- DV
        str += Texto.padLeftAndCut(conta.getContaCorrente(), 12, '0');    //12 - Beneficiário - Código
        str += Texto.padLeftAndCut(conta.getContaCorrenteDv(), 1);    //1 - Beneficiário - DV
        str += " ";                                             //1 - DV - Não utilizado

        //Caracterísitca Cobrança
        str += Texto.padRightAndCut(nossoNumero, 20);                 //20 - Nosso número AABXXXXXD
        str += "1";                                             //1 - Carteira 1=cobrança simples
        str += "1";                                             //1 - Cadastramento 1=cobrança registrada
        str += "2";                                             //1 - Documento 2=escritural
        str += "2";                                             //1 - Emissão boleto 2=beneficiário emite
        str += "2";                                             //1 - Distrib. boleto 2=beneficiário distribui

        //Número do documento de cobrança (identificação do emitente - não é do sicredi)
        str += Texto.padRightAndCut(documentoId, 15);                 //15 - Alpha da esquerda para direita

        //Vencimento
        str += vencimento;                                      //8 - DDMMAAAA

        //Valor do título
        str += Texto.padLeftAndCut(valor, 15, '0');                   //13v2 - Apenas números, sendo duas casas decimais

        //Cooperativa / agência cobradora
        str += "00000";                                         //5 - Informação opcional. O Sicredi não utilizará este campo

        //DV
        str += " ";                                             //1

        //Espécie de título
        str += "03";                                            //2 - 03=DMI duplicata mercantil por indicação

        //Aceite
        str += "N";                                             //1 - N=Não aceite

        //Data emissão do título
        str += dataEmissaoTitulo;                               //8 - DDMMAAAA

        //Juros
        str += jurosCodigo;                                     //1 - Código juros mora 1=valor por dia, 2=taxa mensal
        str += "00000000";                                      //8 - Data de juros - não informada=data do vencimento
        str += Texto.padLeftAndCut(jurosValor, 15, '0');              //13v2 - Valor juros de mora

        //Desc 1
        str += "0";                                             //1 - Código do desconto 2=percentual atá a data informada
        str += "00000000";                                      //8 - Data do desconto
        str += Texto.padLeftAndCut("0", 15, '0');                     //13v2 - Valor juros de mora

        //Valor do IOF
        str += Texto.padLeftAndCut("0", 15, '0');                     //13v2 - O Sicredi atualmente não utiliza este campo

        //Valor do abatimento
        str += Texto.padLeftAndCut("0", 15, '0');                     //13v2

        //Uso empresa beneficiária - identificação do título na empresa
        str += Texto.padRightAndCut("", 25);                          //25 - O Sicredi atualmente não utiliza este campo

        //Código para protesto / negativação
        str += "3";                                             //1 - 3=não protestar/negativar

        //Prazo para protesto/negativação - número de dias
        str += "00";                                            //2

        //Código para baixa/devolução
        str += "1";                                             //1 - usar sempre 1

        //Prazo para baixa/devolução - número de dias
        str += "060";                                           //3 - usar sempre 60

        //Código da moeda
        str += "09";                                            //2 - usar sempre 09=Real

        //Número do contrato
        str += Texto.padLeftAndCut("0", 10, '0');                     //10 - (C030) O Sicredi atualmente não utiliza este campo

        //CNAB
        str += " ";                                             //1 - Texto de observações destinado para uso exclusivo do SICREDI

        str += System.lineSeparator();

        return str;
    }

    public static String gerarRegistroDetalheSegmentoQ(Pessoa pessoa, String lote, String dataEmissaoTitulo, Integer sequencialRegistro, String nossoNumero, String documentoId, String vencimento, String valor) {
        String str = "";

        //Controle
        str += "748";                                           //3 - Banco
        str += lote;                                            //4 - Lote de serviço (Sequencial)
        str += "3";                                             //1 - Registro - 3=detalhe

        //Serviço
        str += Texto.padLeftAndCut(sequencialRegistro.toString(), 5, '0');    //5 - Número do registro (sequencial)
        str += "Q";                                             //1 - Segmento
        str += " ";                                             //1 - CNAB Texto de observações destinado para uso exclusivo do SICREDI
        str += "01";                                            //2 - Código de movimento remessa 01=entrada de títulos

        //Dados do pagador
        str += pessoa.isPessoaFisica() ? "1" : "2";                                     //1 - Inscrição - Tipo 1=cpf, 2=cnpj
        str += Texto.padLeftAndCut(pessoa.getCpfOuCnpjSoNumeros(), 15, '0');                  //15 - Inscrição - Número
        str += Texto.padRightAndCut(Texto.substring(pessoa.getNome(), 0, 40), 40);            //40 - Nome
        str += Texto.padRightAndCut(Texto.substring(pessoa.getEnderecoSimples(), 0, 40), 40); //40 - Endereço: Rua, Número, Complemento
        str += Texto.padRightAndCut(Texto.substring(pessoa.getBairro(), 0, 15), 15);          //15 - Bairro
        str += Texto.padLeftAndCut(pessoa.getCepSoNumeros(), 8, '0');                         //5+3 - CEP + Sufixo do CEP
        str += Texto.padRightAndCut(Texto.substring(pessoa.getMunicipio(), 0, 15), 15);       //15 - Cidade
        str += pessoa.getUf();                                                          //2 - UF

        //Sacador Avalista
        str += "0";                                             //1 - Inscrição - Tipo 0=Não informado
        str += Texto.padLeftAndCut("", 15, '0');                      //15 - Inscrição - Número
        str += Texto.padRightAndCut("", 40);                          //40 - Nome

        //Banco correspondente - Código do banco correspondente na compensação
        str += Texto.padLeftAndCut("", 3, '0');                       //3 - Somente para troca de arquivos entre bancos

        //Nosso número Banco correspondente
        str += Texto.padRightAndCut("", 20);                          //20 - Somente para troca de arquivos entre bancos

        //CNAB
        str += Texto.padRightAndCut("", 8);                           //7 - Texto de observações destinado para uso exclusivo do SICREDI

        str += System.lineSeparator();

        return str;
    }
    
    public static String gerarRegistroDetalheSegmentoR(Conta conta, String lote, Integer sequencialRegistro, String multaValor) {
        String str = "";

        //Controle
        str += "748";                                           //3 - Banco
        str += lote;                                            //4 - Lote de serviço (Sequencial)
        str += "3";                                             //1 - Registro - 3=detalhe

        //Serviço
        str += Texto.padLeftAndCut(sequencialRegistro.toString(), 5, '0');    //5 - Número do registro (sequencial)
        str += "R";                                             //1 - Segmento
        str += " ";                                             //1 - CNAB Texto de observações destinado para uso exclusivo do SICREDI
        str += "01";                                            //2 - Código de movimento remessa 01=entrada de títulos

        //Desc 2
        str += "0";                                             //1 - Código do desconto 2=percentual atá a data informada
        str += "00000000";                                      //8 - Data do desconto
        str += Texto.padLeftAndCut("0", 15, '0');                     //13v2 - Valor juros de mora

        //Desc 3
        str += "0";                                             //1 - Código do desconto 2=percentual atá a data informada
        str += "00000000";                                      //8 - Data de juros
        str += Texto.padLeftAndCut("0", 15, '0');                     //13v2 - Valor juros de mora

        //Juros
        str += "2";                                             //1 - Código da multa - 2=percentual (só tem essa opção)
        str += "00000000";                                      //8 - Data da multa - para o sicredi data de cobrança da multa será sempre a data do vencimento
        str += Texto.padLeftAndCut(multaValor, 15, '0');              //13v2 - Valor da multa

        //Informação ao pagador
        str += Texto.padRightAndCut("", 10);                          //O Sicredi não utiliza atualmente este campo

        //Informação 3
        str += Texto.padRightAndCut("", 40);
        
        //Informação 4
        str += Texto.padRightAndCut("", 40);
        
        //CNAB
        str += Texto.padRightAndCut("", 20);                          //Texto de observações destinado para uso exclusivo do SICREDI

        //Cod ocor do pagador
        str += Texto.padLeftAndCut("", 8, '0');                       //O Sicredi não utiliza atualmente este campo
        
        //Dados para débito
        str += Texto.padLeftAndCut("", 3, '0');                       //3 Banco
        str += Texto.padLeftAndCut("", 5, '0');                       //5 Agência
        str += " ";                                             //1 Agência Dv
        str += Texto.padLeftAndCut("", 12, '0');                      //12 Conta Corrente
        str += " ";                                             //1 CC Dv
        str += " ";                                             //1 Dígito verificador agência/conta
        
        //Ident da emissão do aviso déb.
        str += "0";                                             //O Sicredi não utiliza atualmente este campo
        
        //CNAB
        str += Texto.padRightAndCut("", 9);                           //Texto de observações destinado para uso exclusivo do SICREDI

        
        
        str += System.lineSeparator();

        return str;
    }

    public static String gerarTrailerLote(String lote, Integer quantidadeRegistros) {
        String str = "";

        //Controle
        str += "748";                                           //3 - Banco
        str += lote;                                            //4 - Lote de serviço (Sequencial)
        str += "5";                                             //1 - Registro - 5=trailer de lote

        //CNAB
        str += Texto.padRightAndCut("", 9);                           //9 - Texto de observações destinado para uso exclusivo do SICREDI

        //Quantidade de registros - no lote
        str += Texto.padLeftAndCut(quantidadeRegistros.toString(), 6, '0');      //6 - Somatória dos registros de tipo 1, 2, 3, 4 e 5

        //Totalização da cobrança simp. ------------------------Só serão utilizados para informação do arquivo retorno.
        str += Texto.padLeftAndCut("", 6, '0');                       //6 - Quantidade de títulos em cobrança
        str += Texto.padLeftAndCut("", 17, '0');                      //15v2 - Valor total dos títulos em carteiras

        //Totalização da cobrança vinculada---------------------Só serão utilizados para informação do arquivo retorno.
        str += Texto.padLeftAndCut("", 6, '0');                       //6 - Quantidade de títulos em cobrança
        str += Texto.padLeftAndCut("", 17, '0');                      //15v2 - Valor total dos títulos em carteiras

        //Totalização da cobrança caucionada--------------------Só serão utilizados para informação do arquivo retorno.
        str += Texto.padLeftAndCut("", 6, '0');                       //6 - Quantidade de títulos em cobrança
        str += Texto.padLeftAndCut("", 17, '0');                      //15v2 - Valor total dos títulos em carteiras

        //Totalização da cobrança descontada--------------------Só serão utilizados para informação do arquivo retorno.
        str += Texto.padLeftAndCut("", 6, '0');                       //6 - Quantidade de títulos em cobrança
        str += Texto.padLeftAndCut("", 17, '0');                      //15v2 - Valor total dos títulos em carteiras

        //Número do aviso de lançamento
        str += Texto.padRightAndCut("", 8);                           //8 - O Sicredi atualmente não utiliza este campo

        //CNAB
        str += Texto.padRightAndCut("", 117);                         //117 - Texto de observações destinado para uso exclusivo do SICREDI

        str += System.lineSeparator();

        return str;
    }

    public static String gerarTrailerArquivo(String quantidadeLotes, Integer quantidadeRegistrosArquivo) {
        String str = "";

        //Controle
        str += "748";                                           //3 - Banco
        str += "9999";                                          //4 - Lote de serviço 9999=trailer do arquivo
        str += "9";                                             //1 - Registro - 9=trailer de arquivo

        //CNAB
        str += Texto.padRightAndCut("", 9);                           //9 - Texto de observações destinado para uso exclusivo do SICREDI

        //Totais
        str += Texto.padLeftAndCut(quantidadeLotes, 6, '0');          //6 - Quantidade de lotes - Número obtido pela contagem dos lotes no arquivo
        str += Texto.padLeftAndCut(quantidadeRegistrosArquivo.toString(), 6, '0'); //6 - Quantidade de registros do arquivo - Somatória dos registros de tipo 0, 1, 3, 5 e 9.
        str += Texto.padLeftAndCut("", 6, '0');                       //6 - Número indicativo de lotes de conciliação bancária enviados no arquivo

        //CNAB
        str += Texto.padRightAndCut("", 205);                         //9 - Texto de observações destinado para uso exclusivo do SICREDI

        ////str += System.lineSeparator();

        return str;
    }

    public static String formatarNossoNumero(String nossoNumero) {
        return nossoNumero.substring(0, 2) + "/" + nossoNumero.substring(2, 8) + "-" + nossoNumero.substring(8);
    }
    
    public static String gerarNossoNumeroFormatado(String ano, String b, String sequencial, String digitoVerificador) {
        String nn = gerarNossoNumero(ano, b, sequencial, digitoVerificador);

        return nn.substring(0, 2) + "/" + nn.substring(2, 8) + "-" + nn.substring(8);
    }

    public static String gerarNossoNumero(String ano, String b, String sequencial, String digitoVerificador) {
        /*nosso número AA/BXXXXX-D
        AA - ano
        B - byte (2 a 9). 1 só poderá ser utilizado pela cooperativa
        XXXXX - número livre de 00000 a 99999D - dígito verificador pelo módulo 11
        D - dígito verificador
        
        Ex: 18/200004-1
         */

        sequencial = Texto.padLeftAndCut(sequencial, 5, '0');

        String nossoNumero = ano + b + sequencial + digitoVerificador;

        if (nossoNumero.length() == 9) {
            return nossoNumero;
        } else {
            JOptionPane.showMessageDialog(null, "Erro em Boleto.gerarNossoNumero()");
            return null;
        }
    }

    /**
     *
     * @param vencimento DDMMAAAA
     * @param valor 7v2 99,88 => 9988 
     * @param nossoNumero 9 usar gerarNossoNumero()
     * @param agencia 4
     * @param posto 2
     * @param codigoCedente 5
     * @return
     */
    public static String gerarCodigoBarras(String vencimento, String valor, String nossoNumero, String agencia, String posto, String codigoCedente) {
        String str = "";

        str += "748";                                           //3 - Identificação do banco
        str += "9";                                             //1 - Código da moeda
        //str += "1";                                           //1 - Dígito verificador geral do código de barras
        str += calcularFatorVencimento(vencimento);             //4 - Fator de vencimento
        str += Texto.padLeftAndCut(valor, 10, '0');                   //10 - Valor

        //Campo livre 25
        String campoLivre = "";
        campoLivre += "1";                                      //1 - Tipo de cobrança 1=Com registro
        campoLivre += "1";                                      //1 - Tipo de carteira 1=carteira simples
        campoLivre += nossoNumero;                              //9 - Nosso número
        campoLivre += agencia;                                  //4 - Cooperativa de crédito / agência beneficiária
        campoLivre += posto;                                    //2 - Posto da cooperativa
        campoLivre += codigoCedente;                            //5 - Código do beneficiário
        campoLivre += "1";                                      //1 - 1=quando houver valor expresso
        campoLivre += "0";                                      //1 - Filler - zeros

        str += " " + campoLivre + " " + calcularDv11CampoLivre(campoLivre);        //1 - DV do campo livre - mod 11 com aproveitamento total

        //dígito verificador geral
        return str.substring(0, 4) + " " + calcularDv11Geral(str) + " " + str.substring(4);

    }

    public static String gerarLinhaDigitavel(String codigoBarras) {
        codigoBarras = codigoBarras.replace(" ", "");

        String str;

        String campo1 = codigoBarras.substring(0, 4)
                + codigoBarras.substring(19, 19 + 5); //cinco primeiras posições do campo livre
        String campo1dv = calcularDv10(campo1);

        String campo2 = codigoBarras.substring(19 + 5, 19 + 15);
        String campo2dv = calcularDv10(campo2);

        String campo3 = codigoBarras.substring(19 + 15, 19 + 25);
        String campo3dv = calcularDv10(campo3);

        String campo4 = codigoBarras.substring(4, 5); //dv geral

        String campo5 = codigoBarras.substring(5, 19); //fator de vencimento e valor do documento

        str = campo1.substring(0, 5) + "." + campo1.substring(5, 9) + campo1dv + " "
                + campo2.substring(0, 5) + "." + campo2.substring(5, 10) + campo2dv + " "
                + campo3.substring(0, 5) + "." + campo3.substring(5, 10) + campo3dv + " "
                + campo4 + " "
                + campo5;

        return str;
    }

    //--------------------------------------------------------------------------
    public static String calcularDv11(Conta conta, String ano, String b, String sequencial) {
        return calcularDv11(conta.getAgencia(), conta.getPosto(), conta.getCedente(), ano, b, sequencial);
        
    }
    
    public static String calcularDv11(String agencia, String posto, String codigoCedente, String ano, String b, String sequencial) {
        //Ex:
        //Agência Posto Cedente Ano Byte Sequencial
        //0718    81    03723   19  1    00398

        //String dados = "0718 81 03723 19 1 00398";
        
        String dados = agencia + posto + codigoCedente + ano + b + sequencial;
        dados = dados.replace(" ", "");
        System.out.println("dados: " + dados);
        int peso = 2;
        int soma = 0;
        for (int i = dados.length() - 1; i >= 0; i--) {
            int digito = Integer.parseInt(dados.substring(i, i + 1));

            System.out.print("i: " + digito);

            System.out.print("\t peso: " + peso);

            int multiplicacaoDoDigito = digito * peso;

            soma += multiplicacaoDoDigito;

            System.out.println("\t mult: " + multiplicacaoDoDigito);

            System.out.println("");

            peso++;
            peso = peso == 10 ? 2 : peso;

        }

        System.out.println("soma: " + soma);

        int divisao = soma / 11;

        System.out.println("divisao: " + divisao);

        int multiplicacao = divisao * 11;

        System.out.println("multiplicacao: " + multiplicacao);

        int diferenca = soma - multiplicacao;

        System.out.println("diferenca: " + diferenca);

        int resultado = 11 - diferenca;

        resultado = resultado > 9 ? 0 : resultado;

        System.out.println("resultado: " + resultado);

        return String.valueOf(resultado);
    }

    public static String calcularDv11CampoLivre(String dados) {
        dados = dados.replace(" ", "");

        int peso = 2;
        int soma = 0;
        for (int i = dados.length() - 1; i >= 0; i--) {
            int digito = Integer.parseInt(dados.substring(i, i + 1));
            int multiplicacaoDoDigito = digito * peso;
            soma += multiplicacaoDoDigito;
            peso++;
            peso = peso == 10 ? 2 : peso;
        }
        int divisao = soma / 11;
        int multiplicacao = divisao * 11;
        int diferenca = soma - multiplicacao;

        if (diferenca < 2) { //resto igual a 0 ou 1, dv cai para 0
            return "0";
        } else {
            int resultado = 11 - diferenca;

            resultado = resultado > 9 ? 0 : resultado;

            return String.valueOf(resultado);
        }
    }

    public static String calcularDv11Geral(String dados) {
        dados = dados.replace(" ", "");

        int peso = 2;
        int soma = 0;
        for (int i = dados.length() - 1; i >= 0; i--) {
            int digito = Integer.parseInt(dados.substring(i, i + 1));
            int multiplicacaoDoDigito = digito * peso;
            soma += multiplicacaoDoDigito;
            peso++;
            peso = peso == 10 ? 2 : peso;
        }
        int divisao = soma / 11;
        int multiplicacao = divisao * 11;
        int diferenca = soma - multiplicacao;

        int resultado = 11 - diferenca;

        resultado = (resultado < 2 || resultado > 9) ? 1 : resultado;

        return String.valueOf(resultado);
    }

    public static String calcularFatorVencimento(String vencimento) {
        int ano = Integer.parseInt(vencimento.substring(4));
        int mes = Integer.parseInt(vencimento.substring(2, 4));
        int dia = Integer.parseInt(vencimento.substring(0, 2));

        Long dias = DateTime.diasEntreDatas(LocalDate.of(ano, mes, dia), LocalDate.of(1997, 10, 7));

        dias = dias > 9999 ? dias - 9000 : dias; //fator volta para 1000 em 22/02/2025

        return dias.toString();

    }

    public static String calcularDv10(String dados) {
        dados = dados.replace(" ", "");

        int peso = 2;
        int soma = 0;
        for (int i = dados.length() - 1; i >= 0; i--) {
            int digito = Integer.parseInt(dados.substring(i, i + 1));
            int multiplicacaoDoDigito = digito * peso;

            //System.out.print("digito: " + digito);
            //System.out.print("\t x peso: " + peso);

            //System.out.print("\t = multiplicacaoDoDigito: " + multiplicacaoDoDigito);

            if (multiplicacaoDoDigito > 9) { //se maior que 9 soma os dígitos e obtém número menor que 10 (1 dígito)
                String mult = String.valueOf(multiplicacaoDoDigito);
                int multSub = 0;
                for (int m = 0; m < mult.length(); m++) {
                    multSub += Integer.parseInt(mult.substring(m, m + 1));
                }
                multiplicacaoDoDigito = multSub;
                //System.out.print("\t x novo mult: " + multiplicacaoDoDigito);
            }

            soma += multiplicacaoDoDigito;
            peso = peso == 2 ? 1 : 2; //peso alterna entre 2 e 1 sucessivamente

            //System.out.println("");
        }

        int multiplo10 = soma % 10 == 0 ? soma : (soma / 10 + 1) * 10; //2020-06-15 não considerava o multiplo igual a soma e gerava um dv = 10!
        //System.out.println("soma: " + soma);
        //System.out.println("m10: " + multiplo10);
        
        //System.out.println("dv10:" + String.valueOf(multiplo10 - soma));
        
        return String.valueOf(multiplo10 - soma);
    }
}
