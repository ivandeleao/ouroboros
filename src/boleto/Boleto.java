/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boleto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import util.DateTime;
import util.Texto;

/**
 *
 * @author ivand
 */
public class Boleto {
    
    public static String gerarArquivo() {
        /*
        Nomenclatura do Arquivo: CCCCCMDD.XXX
        CCCCC - código beneficiário
        MDD - código do mês e número do dia da data de geração do arquivo
        XXX - Extensão irrelevante - Não usar CRT pois são usadas em arquivos de retorno
        Codificação dos meses: Jan =1, Fev = 2, ... Out = O, Nov = N, Dez = D 
        Linha Finalizador - "enter"
         */
        
        String data = Texto.soNumeros(DateTime.toString(LocalDate.now()));
        String hora = Texto.soNumeros(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")));
        
        
        String str = "";
        str += gerarHeaderArquivo();
        
        //
        String agencia = "0718";
        String posto = "81";
        String cedente = "60840";
        String ano = "20";
        String b = "2";
        String sequencialBoleto = "00001"; //sequencial globalString 
        String documentoId = "777";
        String vencimento = "10052020";
        String valor = "99988";
        String dataEmissaoTitulo = "10032020";
        
        String dv = calcularDv11(agencia, posto, cedente, ano, b, sequencialBoleto);
        
        String lote = "0001"; //sequencial dentro do arquivo
        
        
        String nossoNumero = gerarNossoNumero(ano, b, sequencialBoleto, dv); //sequencial global
        str += gerarHeaderLote(lote, data, hora);
        
        String sequencialRegistro = "";
        
        sequencialRegistro = "00001"; //sequencial do registro
        str += gerarRegistroDetalheSegmentoP(lote, dataEmissaoTitulo, hora, sequencialRegistro, nossoNumero, documentoId, vencimento, valor);
        
        sequencialRegistro = "00002";
        str += gerarRegistroDetalheSegmentoQ(lote, dataEmissaoTitulo, hora, sequencialRegistro, nossoNumero, documentoId, vencimento, valor);
        
        //
        sequencialRegistro = "00003"; //sequencial do registro
        str += gerarRegistroDetalheSegmentoP(lote, dataEmissaoTitulo, hora, sequencialRegistro, nossoNumero, documentoId, vencimento, valor);
        
        sequencialRegistro = "00004";
        str += gerarRegistroDetalheSegmentoQ(lote, dataEmissaoTitulo, hora, sequencialRegistro, nossoNumero, documentoId, vencimento, valor);
        
        //
        str += gerarTrailerLote(lote, "6");
        
        //
        str += gerarTrailerArquivo("1", "8");
        
        return str;
    }
    
    public static String gerarHeaderArquivo() {
        String str = "";
        
        //Controle
        str += "748";                                           //3 - Banco
        str += "0000";                                          //4 - Lote
        str += "0";                                             //1 - Registro - 0=header do arquivo
        
        //CNAB
        str += Texto.padRight("", 9);                           //9 - Texto de observações destinado para uso exclusivo do SICREDI
        
        //Empresa
        str += "2";                                             //1 - Incrição - Tipo - CNPJ
        String empresaCnpj = "04615918000104"; //Ouroboros.EMPRESA_CNPJ;
        str += Texto.padLeft(Texto.soNumeros(empresaCnpj), 14, '0'); //14 - Incrição - Número do CNPJ
        str += Texto.padRight("", 20);                          //20 - Convênio

        str += Texto.padLeft("0718", 5, '0');                   //5 - Conta Corrente - Agência
        str += " ";                                             //1 - Conta Corrente - Agência- DV
        str += Texto.padLeft("60840", 12, '0');                 //12 - Conta Corrente - Beneficiário - Código
        str += "1";                                             //1 - Conta Corrente - Beneficiário - DV
        str += " ";                                             //1 - Não utilizado
        String empresaNome = "Leao teste"; //Ouroboros.EMPRESA_RAZAO_SOCIAL
        str += Texto.padRight(Texto.substring(empresaNome, 0, 30), 30); //30 - Nome da Empresa
        
        
        //Nome do Banco
        str += Texto.padRight("SICREDI", 30);                   //30 - Nome do Banco
        
        //CNAB
        str += Texto.padRight("", 10);                          //10 - Texto de observações destinado para uso exclusivo do SICREDI
        
        //Arquivo
        str += "1";                                             //1 - Código remessa / retorno
        str += Texto.soNumeros(DateTime.toString(LocalDate.now())); //8 - Data de Geração - DDMMAAAA
        str += Texto.soNumeros(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"))); //8 - Hora de Geração - HHMMSS
        str += "000001";                                        //6 - Sequência (NSA) Núemro sequencial do arquivo
        str += "081";                                           //3 - Leiaute do Arquivo
        str += "01600";                                         //5 - Densidade
        
        //Reservado Banco
        str += Texto.padRight("", 20);                          //20 - Texto de observações destinado para uso exclusivo do banco
        
        //Reservado Empresa
        str += Texto.padRight("", 20);                          //20 - Texto de observações destinado para uso exclusivo da empresa
        
        //CNAB
        str += Texto.padRight("", 29);                          //29 - Texto de observações destinado para uso exclusivo do banco
        
        str += System.lineSeparator();
        
        return str;
    }
    
    
    public static String gerarHeaderLote(String lote, String data, String hora) {
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
        String empresaCnpj = "04615918000104"; //Ouroboros.EMPRESA_CNPJ;
        str += Texto.padLeft(Texto.soNumeros(empresaCnpj), 15, '0'); //15 - (*É DIFERENTE MESMO) Incrição - Número do CNPJ
        str += Texto.padRight("", 20);                          //20 - Convênio

        str += Texto.padLeft("0718", 5, '0');                   //5 - Conta Corrente - Agência
        str += " ";                                             //1 - Conta Corrente - Agência- DV
        str += Texto.padLeft("60840", 12, '0');                 //12 - Conta Corrente - Beneficiário - Código
        str += "1";                                             //1 - Conta Corrente - Beneficiário - DV
        str += " ";                                             //1 - DV - Não utilizado
        String empresaNome = "Leao teste"; //Ouroboros.EMPRESA_RAZAO_SOCIAL
        str += Texto.padRight(Texto.substring(empresaNome, 0, 30), 30); //30 - Nome da Empresa
        
        //Informação 1
        str += Texto.padRight("", 40);                          //40 - (C073) O Sicredi atualmente não utiliza este campo 
        
        //Informação 2
        str += Texto.padRight("", 40);                          //40 - (C073) O Sicredi atualmente não utiliza este campo 
        
        //Controle da Cobrança
        str += Texto.padLeft("", 8, '0');                       //8 - Número remessa / retorno
        str += data;                                            //8 - Data de gravação remessa / retorno DDMMAAAA
        str += Texto.padLeft("", 8, '0');                       //8 - Data do crédito - Sicredi não utilizará esse campo
        
        //CNAB
        str += Texto.padRight("", 33);                          //33 - Texto de observações destinado para uso exclusivo do SICREDI

        str += System.lineSeparator();
        
        return str;
    }
    
    
    public static String gerarRegistroDetalheSegmentoP(String lote, String dataEmissaoTitulo, String hora, String sequencial, String nossoNumero, String documentoId, String vencimento, String valor) {
        String str = "";
        
        //Controle
        str += "748";                                           //3 - Banco
        str += lote;                                            //4 - Lote de serviço (Sequencial)
        str += "3";                                             //1 - Registro - 3=detalhe
        
        //Serviço
        str += sequencial;                                      //5 - Número do registro (sequencial)
        str += "P";                                             //1 - Segmento
        str += " ";                                             //1 - CNAB Texto de observações destinado para uso exclusivo do SICREDI
        str += "01";                                            //2 - Código de movimento remessa 01=entrada de títulos
        
        //CC
        str += Texto.padLeft("0718", 5, '0');                   //5 - Conta Corrente - Agência
        str += " ";                                             //1 - Conta Corrente - Agência- DV
        str += Texto.padLeft("60840", 12, '0');                 //12 - Conta Corrente - Beneficiário - Código
        str += "1";                                             //1 - Conta Corrente - Beneficiário - DV
        str += " ";                                             //1 - DV - Não utilizado

        //Caracterísitca Cobrança
        str += Texto.padRight(nossoNumero, 20);                 //20 - Nosso número AABXXXXXD
        str += "1";                                             //1 - Carteira 1=cobrança simples
        str += "1";                                             //1 - Cadastramento 1=cobrança registrada
        str += "2";                                             //1 - Documento 2=escritural
        str += "2";                                             //1 - Emissão boleto 2=beneficiário emite
        str += "2";                                             //1 - Distrib. boleto 2=beneficiário distribui
        
        //Número do documento de cobrança (identificação do emitente - não é do sicredi)
        str += Texto.padRight(documentoId, 15);                 //15 - Alpha da esquerda para direita
        
        //Vencimento
        str += vencimento;                                      //8 - DDMMAAAA
        
        //Valor do título
        str += Texto.padLeft(valor, 15, '0');                   //13v2 - Apenas números, sendo duas casas decimais
        
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
        str += "2";                                             //1 - Código juros mora 1=valor por dia
        str += "00000000";                                      //8 - Data de juros
        str += Texto.padLeft("0", 15, '0');                     //13v2 - Valor juros de mora
        
        //Desc 1
        str += "2";                                             //1 - Código do desconto 2=percentual atá a data informada
        str += "00000000";                                      //8 - Data de juros
        str += Texto.padLeft("0", 15, '0');                     //13v2 - Valor juros de mora
        
        //Valor do IOF
        str += Texto.padLeft("0", 15, '0');                     //13v2 - O Sicredi atualmente não utiliza este campo
        
        //Valor do abatimento
        str += Texto.padLeft("0", 15, '0');                     //13v2
        
        //Uso empresa beneficiária - identificação do título na empresa
        str += Texto.padRight("", 25);                          //25 - O Sicredi atualmente não utiliza este campo
        
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
        str += Texto.padLeft("0", 10, '0');                     //10 - (C030) O Sicredi atualmente não utiliza este campo
        
        //CNAB
        str += " ";                                             //1 - Texto de observações destinado para uso exclusivo do SICREDI
        
        
        
        
        
        str += System.lineSeparator();
        
        return str;
    }
    
    public static String gerarRegistroDetalheSegmentoQ(String lote, String dataEmissaoTitulo, String hora, String sequencialRegistro, String nossoNumero, String documentoId, String vencimento, String valor) {
        String str = "";
        
        //Controle
        str += "748";                                           //3 - Banco
        str += lote;                                            //4 - Lote de serviço (Sequencial)
        str += "3";                                             //1 - Registro - 3=detalhe
        
        //Serviço
        str += sequencialRegistro;                                      //5 - Número do registro (sequencial)
        str += "Q";                                             //1 - Segmento
        str += " ";                                             //1 - CNAB Texto de observações destinado para uso exclusivo do SICREDI
        str += "01";                                            //2 - Código de movimento remessa 01=entrada de títulos
        
        //Dados do pagador
        str += "1";                                             //1 - Inscrição - Tipo 1=cpf, 2=cnpj
        str += Texto.padLeft("28002020847", 15, '0');           //15 - Inscrição - Número
        str += Texto.padRight("Ivan Luis Carneiro Leao Arrudad de F Torres", 40);  //40 - Nome
        str += Texto.padRight("Rua Dr. Hortencio Pereira da Silva, 125", 40);  //40 - Endereço: Rua, Número, Complemento
        str += Texto.padRight("Vila Pereira", 15);              //15 - Bairro
        str += Texto.padLeft("13970", 5, '0');                  //5 - CEP
        str += Texto.padLeft("246", 3, '0');                    //3 - Sufixo do CEP
        str += Texto.padRight("Itapira", 15);                   //15 - Cidade
        str += "SP";                                            //2 - UF
        
        //Sacador Avalista
        str += "0";                                             //1 - Inscrição - Tipo 0=Não informado
        str += Texto.padLeft("", 15, '0');                      //15 - Inscrição - Número
        str += Texto.padRight("", 40);                          //40 - Nome
        
        //Banco correspondente - Código do banco correspondente na compensação
        str += Texto.padLeft("", 3, '0');                       //3 - Somente para troca de arquivos entre bancos
        
        //Nosso número Banco correspondente
        str += Texto.padRight("", 20);                          //20 - Somente para troca de arquivos entre bancos
        
        //CNAB
        str += Texto.padRight("", 8);                           //7 - Texto de observações destinado para uso exclusivo do SICREDI
        
        
        
        
        
        
        str += System.lineSeparator();
        
        return str;
    }
    
    
    public static String gerarTrailerLote(String lote, String quantidadeRegistros) {
        String str = "";
        
        //Controle
        str += "748";                                           //3 - Banco
        str += lote;                                            //4 - Lote de serviço (Sequencial)
        str += "5";                                             //1 - Registro - 5=trailer de lote
        
        //CNAB
        str += Texto.padRight("", 9);                           //9 - Texto de observações destinado para uso exclusivo do SICREDI
        
        //Quantidade de registros - no lote
        str += Texto.padLeft(quantidadeRegistros, 6, '0');      //6 - Somatória dos registros de tipo 1, 2, 3, 4 e 5
        
        //Totalização da cobrança simp. ------------------------Só serão utilizados para informação do arquivo retorno.
        str += Texto.padLeft("", 6, '0');                       //6 - Quantidade de títulos em cobrança
        str += Texto.padLeft("", 17, '0');                      //15v2 - Valor total dos títulos em carteiras
        
        //Totalização da cobrança vinculada---------------------Só serão utilizados para informação do arquivo retorno.
        str += Texto.padLeft("", 6, '0');                       //6 - Quantidade de títulos em cobrança
        str += Texto.padLeft("", 17, '0');                      //15v2 - Valor total dos títulos em carteiras
        
        //Totalização da cobrança caucionada--------------------Só serão utilizados para informação do arquivo retorno.
        str += Texto.padLeft("", 6, '0');                       //6 - Quantidade de títulos em cobrança
        str += Texto.padLeft("", 17, '0');                      //15v2 - Valor total dos títulos em carteiras
        
        //Totalização da cobrança descontada--------------------Só serão utilizados para informação do arquivo retorno.
        str += Texto.padLeft("", 6, '0');                       //6 - Quantidade de títulos em cobrança
        str += Texto.padLeft("", 17, '0');                      //15v2 - Valor total dos títulos em carteiras
        
        //Número do aviso de lançamento
        str += Texto.padRight("", 8);                           //8 - O Sicredi atualmente não utiliza este campo
        
        //CNAB
        str += Texto.padRight("", 117);                         //117 - Texto de observações destinado para uso exclusivo do SICREDI
        
        
        
        
        str += System.lineSeparator();
        
        return str;
    }
    
    
    public static String gerarTrailerArquivo(String quantidadeLotes, String quantidadeRegistrosArquivo) {
        String str = "";
        
        //Controle
        str += "748";                                           //3 - Banco
        str += "9999";                                          //4 - Lote de serviço 9999=trailer do arquivo
        str += "9";                                             //1 - Registro - 9=trailer de arquivo
        
        //CNAB
        str += Texto.padRight("", 9);                           //9 - Texto de observações destinado para uso exclusivo do SICREDI
        
        //Totais
        str += Texto.padLeft(quantidadeLotes, 6, '0');          //6 - Quantidade de lotes - Número obtido pela contagem dos lotes no arquivo
        str += Texto.padLeft(quantidadeRegistrosArquivo, 6, '0'); //6 - Quantidade de registros do arquivo - Somatória dos registros de tipo 0, 1, 3, 5 e 9.
        str += Texto.padLeft("", 6, '0');                       //6 - Número indicativo de lotes de conciliação bancária enviados no arquivo
        
        //CNAB
        str += Texto.padRight("", 205);                         //9 - Texto de observações destinado para uso exclusivo do SICREDI
        
        
        
        
        str += System.lineSeparator();
        
        return str;
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
        
        sequencial = Texto.padLeft(sequencial, 5, '0');
        
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
     * @param valor 7v2
     * @param nossoNumero 9
     * @param agenciaBenefiaria 4
     * @param posto 2
     * @param codigoBeneficiario 5
     * @return 
     */
    public static String gerarCodigoBarras(String vencimento, String valor, String nossoNumero, String agenciaBenefiaria, String posto, String codigoBeneficiario) {
        String str = "";
        
        str += "748";                                           //3 - Identificação do banco
        str += "9";                                             //1 - Código da moeda
        //str += "1";                                           //1 - Dígito verificador geral do código de barras
        str += calcularFatorVencimento(vencimento);             //4 - Fator de vencimento
        str += Texto.padLeft(valor, 10, '0');                   //10 - Valor
        
        //Campo livre 25
        String campoLivre = "";
        campoLivre += "1";                                             //1 - Tipo de cobrança 1=Com registro
        campoLivre += "1";                                             //1 - Tipo de carteira 1=carteira simples
        campoLivre += nossoNumero;                                     //9 - Nosso número
        campoLivre += agenciaBenefiaria;                               //4 - Cooperativa de crédito / agência beneficiária
        campoLivre += posto;                                           //2 - Posto da cooperativa
        campoLivre += codigoBeneficiario;                              //5 - Código do beneficiário
        campoLivre += "1";                                             //1 - 1=quando houver valor expresso
        campoLivre += "0";                                             //1 - Filler - zeros
        
        str += " " + campoLivre + " " + calcularDv11CampoLivre(campoLivre);        //1 - DV do campo livre - mod 11 com aproveitamento total
        
        //dígito verificador geral
        return str.substring(0, 4) + " " + calcularDv11Geral(str) + " " + str.substring(4);
        
    }
    
    public static String gerarLinhaDigitavel(String codigoBarras) {
        codigoBarras = codigoBarras.replace(" ", "");
        
        String str;
        
        String campo1 = codigoBarras.substring(0, 4) 
                + codigoBarras.substring(19, 19+5); //cinco primeiras posições do campo livre
        String campo1dv = calcularDv10(campo1);
        
        String campo2 = codigoBarras.substring(19+5, 19+15);
        String campo2dv = calcularDv10(campo2);
        
        String campo3 = codigoBarras.substring(19+15, 19+25);
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
    
    public static String calcularDv11(String agencia, String posto, String cedente, String ano, String b, String sequencial) {
        //Ex:
        //Agência Posto Cedente Ano Byte Sequencial
        //0718    81    60840   19  1    00398
        
        //String dados = "0718 81 60840 19 1 00398";
        String dados = agencia + posto + cedente + ano + b + sequencial;
        dados = dados.replace(" ", "");
        
        int peso = 2;
        int soma = 0;
        for(int i = dados.length() - 1; i >= 0; i--) {
            int digito = Integer.parseInt(dados.substring(i, i+1));
            
            System.out.print("i: " + digito);
            
            System.out.print("\t peso: " + peso);

            int multiplicacaoDoDigito = digito * peso;
            
            soma += multiplicacaoDoDigito;
            
            System.out.println("\t mult: " + multiplicacaoDoDigito);
            
            System.out.println("");
            
            peso++;
            peso = peso == 10 ? 2: peso;
            
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
        for(int i = dados.length() - 1; i >= 0; i--) {
            int digito = Integer.parseInt(dados.substring(i, i+1));
            int multiplicacaoDoDigito = digito * peso;
            soma += multiplicacaoDoDigito;
            peso++;
            peso = peso == 10 ? 2: peso;
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
        for(int i = dados.length() - 1; i >= 0; i--) {
            int digito = Integer.parseInt(dados.substring(i, i+1));
            int multiplicacaoDoDigito = digito * peso;
            soma += multiplicacaoDoDigito;
            peso++;
            peso = peso == 10 ? 2: peso;
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
        for(int i = dados.length() - 1; i >= 0; i--) {
            int digito = Integer.parseInt(dados.substring(i, i+1));
            int multiplicacaoDoDigito = digito * peso;
            
            System.out.print("digito: " + digito);
            System.out.print("\t x peso: " + peso);
            
            System.out.print("\t = multiplicacaoDoDigito: " + multiplicacaoDoDigito);
            
            if (multiplicacaoDoDigito > 9) { //se maior que 9 soma os dígitos e obtém número menor que 10 (1 dígito)
                String mult = String.valueOf(multiplicacaoDoDigito);
                int multSub = 0;
                for (int m = 0; m < mult.length(); m++) {
                    multSub += Integer.parseInt(mult.substring(m, m+1));
                }
                multiplicacaoDoDigito = multSub;
                System.out.print("\t x novo mult: " + multiplicacaoDoDigito);
            }
            
            soma += multiplicacaoDoDigito;
            peso = peso == 2 ? 1: 2; //peso alterna entre 2 e 1 sucessivamente
            
            System.out.println("");
        }
        
        int multiplo10 = (soma / 10 + 1) * 10;
        System.out.println("soma: " + soma);
        System.out.println("m10: " + multiplo10);
        
        
        return String.valueOf(multiplo10 - soma);
    }
}
