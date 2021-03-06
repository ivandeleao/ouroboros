/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nfe;

import br.com.swconsultoria.nfe.Nfe;
import br.com.swconsultoria.nfe.dom.enuns.AmbienteEnum;
import br.com.swconsultoria.nfe.dom.enuns.EstadosEnum;
import br.com.swconsultoria.nfe.exception.NfeException;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TEnderEmi;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TEndereco;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TEnviNFe;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TIpi;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TLocal;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TNFe;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TNFe.InfNFe;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TNFe.InfNFe.Cobr;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TNFe.InfNFe.Cobr.Dup;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TNFe.InfNFe.Cobr.Fat;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TNFe.InfNFe.Dest;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TNFe.InfNFe.Det;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TNFe.InfNFe.Det.Imposto.ICMS;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TNFe.InfNFe.Det.Prod;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TNFe.InfNFe.Emit;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TNFe.InfNFe.Ide;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TNFe.InfNFe.InfAdic;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TNFe.InfNFe.Pag;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TNFe.InfNFe.Total;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TNFe.InfNFe.Transp;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TUf;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TUfEmi;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TVeiculo;
import br.com.swconsultoria.nfe.util.ChaveUtil;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import model.mysql.bean.endereco.Cidade;
import model.mysql.bean.fiscal.nfe.DocumentoReferenciado;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.dao.endereco.CidadeDAO;
import model.mysql.dao.principal.ConstanteDAO;
import model.nosql.TipoCalculoEnum;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.*;
import util.Decimal;
import util.FiscalUtil;
import util.Texto;

/**
 *
 * @author ivand
 */
public class MontarXml {

    static Venda documento;
    static EstadosEnum cUF;
    static String cNf;
    static String cnpjEmitente;
    static String modelo;
    static Integer serie;
    static Integer nNf;
    static String tipoEmissao;
    static ZonedDateTime dataHoraEmissao;
    static ChaveUtil chave;
    static String cDV;

    public static boolean validarDocumento(Venda documento) {
        List<String> mensagens = new ArrayList<>();

        //tem cliente?
        if (documento.getPessoa() == null) {
            mensagens.add("Selecione um cliente.");
        } else {
            Pessoa pessoa = documento.getPessoa();
            if (pessoa.getCpfOuCnpj().isEmpty()) {
                mensagens.add("Cliente sem CPF ou CNPJ");
            }
            if (pessoa.getCep().isEmpty()) {
                mensagens.add("Cliente sem CEP");
            }
            if (pessoa.getEndereco().isEmpty()) {
                mensagens.add("Cliente sem endereço");
            }
            if (pessoa.getNumero().isEmpty()) {
                mensagens.add("Cliente sem número no endereço");
            }
            if (pessoa.getBairro().isEmpty()) {
                mensagens.add("Cliente sem bairro");
            }
            if (pessoa.getCodigoMunicipio().isEmpty()) {
                mensagens.add("Cliente sem código do município");
            }
            if (pessoa.getUf().isEmpty()) {
                mensagens.add("Cliente sem UF");
            }
        }

        //tem itens?
        if (documento.getMovimentosFisicos().isEmpty()) {
            mensagens.add("Adicione itens.");
        }

        //dados dos itens...
        List<String> mensagensItens = new ArrayList<>();
        for (MovimentoFisico mf : documento.getMovimentosFisicosProdutos()) {
            List<String> msgPartes = new ArrayList<>();

            if (mf.getCodigo().isEmpty()) {
                msgPartes.add("Código");
            }
            if (mf.getDescricao().isEmpty()) {
                msgPartes.add("Descrição");
            }
            if (mf.getNcm() == null) {
                msgPartes.add("NCM");
            }

            if (mf.getCfop() == null) {
                msgPartes.add("CFOP");
            }

            if (mf.getIcms() == null) {
                msgPartes.add("ICMS");
                
            } else {
                if (mf.getIcms().getCodigo().equals("900")) {
                    if (mf.getModalidadeBcIcmsSt() == null) {
                        msgPartes.add("Modalidade BC ICMS");
                    }
                }

                if (mf.getIcms().getCodigo().equals("201") || mf.getIcms().getCodigo().equals("202") || mf.getIcms().getCodigo().equals("203") || mf.getIcms().getCodigo().equals("900")) {
                    if (mf.getModalidadeBcIcmsSt() == null) {
                        msgPartes.add("Modalidade BC ICMS ST");
                    }
                }
            }

            if (mf.getOrigem() == null) {
                msgPartes.add("Origem");
            }

            if (mf.getPis() == null) {
                msgPartes.add("PIS");
            }

            if (mf.getCofins() == null) {
                msgPartes.add("COFINS");
            }

            if (mf.getUnidadeComercialVenda() == null) {
                msgPartes.add("Unidade Comercial");
            }

            if (mf.getUnidadeTributavel() == null) {
                msgPartes.add("Unidade Tributável");
            }

            if ((mf.getValor().multiply(mf.getSaida())).compareTo(mf.getValorTributavel().multiply(mf.getQuantidadeTributavel())) != 0) {
                msgPartes.add("Comercial difere de Tributável");
            }

            //validar igualdade do comercial x tributado
            if (!msgPartes.isEmpty()) {
                String mensagemItem = "id " + mf.getId() + " " + mf.getDescricao() + ": ";
                //mensagemItem = msgPartes.stream().map((msg) -> msg + ", ").reduce(mensagemItem, String::concat);
                mensagemItem += String.join(", ", msgPartes);
                mensagensItens.add(mensagemItem);
                System.out.println("add " + mensagemItem);
            }
        }

        if (!mensagensItens.isEmpty()) {
            mensagens.add("Itens com campos irregulares:");
            mensagens.addAll(mensagensItens);
        }

        if (!mensagens.isEmpty()) {
            String mensagem = "";

            mensagem = mensagens.stream().map((msg) -> msg + "\r\n").reduce(mensagem, String::concat);

            JOptionPane.showMessageDialog(MAIN_VIEW, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
        }

        return mensagens.isEmpty();
    }

    public static TEnviNFe montarEnviNfe(Venda doc) {

        try {
            documento = doc;
            cUF = EstadosEnum.getByCodigoIbge(Ouroboros.EMPRESA_ENDERECO_CODIGO_MUNICIPIO.substring(0, 2)); //Código IBGE do estado
            cnpjEmitente = Texto.soNumeros(Ouroboros.EMPRESA_CNPJ);
            modelo = "55";
            serie = Ouroboros.NFE_SERIE;
            //nNf = Ouroboros.NFE_PROXIMO_NUMERO; //2020-02-11 não pode manter em sessão
            nNf = Integer.parseInt(ConstanteDAO.getValor("NFE_PROXIMO_NUMERO"));
            tipoEmissao = "1";
            cNf = Texto.padLeftAndCut(String.valueOf(new Random().nextInt(99999999)), 8, '0'); //8 - Código numérico que compõe a Chave de Acesso. Número aleatório gerado pelo emitente para cada NF-e para evitar acessos indevidos da NF-e. (v2.0) 
            dataHoraEmissao = ZonedDateTime.now();
            chave = new ChaveUtil(cUF, cnpjEmitente, modelo, serie, nNf, tipoEmissao, cNf, dataHoraEmissao.toLocalDateTime());
            cDV = chave.getDigitoVerificador();

            TEnviNFe enviNFe = new TEnviNFe();
            enviNFe.setVersao("4.00");
            enviNFe.setIdLote("1");
            enviNFe.setIndSinc("1");
            enviNFe.getNFe().add(montarTnfe());

            enviNFe = Nfe.montaNfe(NfeConfig.iniciarConfiguracoes(), enviNFe, true);

            return enviNFe;

        } catch (NfeException e) {
            JOptionPane.showMessageDialog(MAIN_VIEW, e, "Erro ao montar xml NFe", JOptionPane.ERROR_MESSAGE);

            return null;

        }
    }

    private static TNFe montarTnfe() {

        InfNFe infNFe = montarInfNfe();

        infNFe.setIde(montarIde());
        infNFe.setEmit(montarEmit());
        infNFe.setDest(montarDest());

        if (documento.isEntregaDiferente()) {
            infNFe.setEntrega(montarEntrega());
        }

        infNFe.getDet().addAll(montarDets());

        infNFe.setTotal(montarTotal());
        infNFe.setTransp(montarTransp());

        //if (!documento.getInformacoesAdicionaisFisco().isEmpty() || !documento.getInformacoesComplementaresContribuinte().isEmpty()) {
        infNFe.setInfAdic(montarInfAdic());
        //}

        if (!documento.getParcelas().isEmpty()) {
            infNFe.setCobr(montarCobr());
        }

        infNFe.setPag(montarPag());

        TNFe tnfe = new TNFe();
        tnfe.setInfNFe(infNFe);

        return tnfe;
    }

    private static InfNFe montarInfNfe() {

        InfNFe infNFe = new InfNFe();
        infNFe.setId(chave.getChaveNF());
        infNFe.setVersao("4.00");

        return infNFe;

    }

    private static Ide montarIde() {

        TNFe.InfNFe.Ide ide = new TNFe.InfNFe.Ide();
        ide.setCUF(cUF.getCodigoUF());
        ide.setCNF(cNf); //8 - Código numérico que compõe a Chave de Acesso. Número aleatório gerado pelo emitente para cada NF-e para evitar acessos indevidos da NF-e. (v2.0) 
        ide.setNatOp(documento.getNaturezaOperacao().getNome());
        ide.setMod(modelo);
        ide.setSerie(serie.toString());
        ide.setNNF(nNf.toString());
        ide.setDhEmi(dataHoraEmissao.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ssXXX")));
        ide.setTpNF("1"); //Tipo de Operação 0-Entrada 1-Saída
        ide.setIdDest(documento.getDestinoOperacao().getId().toString()); //Identificador de local de destino da operação 0-Operação interna 1-Operação interestadual 2-Operação com exterior
        ide.setCMunFG(Ouroboros.EMPRESA_ENDERECO_CODIGO_MUNICIPIO); //Código do Município de Ocorrência do Fato Gerador 
        ide.setTpImp("1"); //Formato de Impressão do DANFE 0-Sem geração de DANFE ... até 5
        ide.setTpEmis(tipoEmissao); //Tipo de Emissão da NF-e 1-Normal 2-Contingência ... até 7 
        ide.setCDV(cDV); //Dígito verificador da chave de acesso
        ide.setTpAmb(NfeConfig.AMBIENTE.getCodigo());
        ide.setFinNFe(documento.getFinalidadeEmissao().getId().toString()); //Finalidade de emissão da NF-e 1-NF-e parse  ... até 4
        ide.setIndFinal(documento.getConsumidorFinal().getId().toString()); //Indica operação com Consumidor final 0-Normal 1-Consumidor final
        ide.setIndPres(documento.getTipoAtendimento().getId().toString()); //Indicador de presença do comprador no estabelecimento comercial no momento da operação 0-Não se aplica... 1-Operação presencial ... até 9
        ide.setProcEmi("0"); //Processo de emissão 0-Emissão de NF-e com aplicativo do contribuinte ... até 3
        ide.setVerProc("MindwareB3" + Ouroboros.APP_VERSION); //20 Versão do Processo de emissão da NF-e //Informar a versão do aplicativo emissor de NF-e. 

        //Documentos Referenciados----------------------------------------------
        for (DocumentoReferenciado docRef : documento.getDocumentosReferenciados()) {
            Ide.NFref nfRef = new TNFe.InfNFe.Ide.NFref();
            nfRef.setRefNFe(docRef.getChave());

            ide.getNFref().add(nfRef);
        }
        //Fim Documentos Referenciados------------------------------------------

        return ide;
    }

    private static Emit montarEmit() {

        Emit emit = new Emit();

        emit.setCNPJ(cnpjEmitente);
        emit.setXNome(Texto.substring(Texto.removerEspeciais(EMPRESA_RAZAO_SOCIAL), 0, 60));
        emit.setXFant(EMPRESA_NOME_FANTASIA);
        TEnderEmi enderEmit = new TEnderEmi();
        enderEmit.setXLgr(EMPRESA_ENDERECO);
        enderEmit.setNro(Ouroboros.EMPRESA_ENDERECO_NUMERO);

        if (!Ouroboros.EMPRESA_ENDERECO_COMPLEMENTO.trim().isEmpty()) {
            enderEmit.setXCpl(Ouroboros.EMPRESA_ENDERECO_COMPLEMENTO);
        }

        enderEmit.setXBairro(Ouroboros.EMPRESA_ENDERECO_BAIRRO);
        enderEmit.setCMun(Ouroboros.EMPRESA_ENDERECO_CODIGO_MUNICIPIO);

        Cidade cidade = new CidadeDAO().findByCodigoIbge(Ouroboros.EMPRESA_ENDERECO_CODIGO_MUNICIPIO);
        enderEmit.setXMun(cidade.getNome());
        enderEmit.setUF(TUfEmi.valueOf(cidade.getEstado().getSigla()));
        enderEmit.setCEP(Texto.soNumeros(Ouroboros.EMPRESA_ENDERECO_CEP));
        enderEmit.setCPais("1058");
        enderEmit.setXPais("BRASIL");
        enderEmit.setFone(Texto.soNumeros(Ouroboros.EMPRESA_TELEFONE));
        emit.setEnderEmit(enderEmit);

        //email??
        emit.setIE(Texto.soNumeros(Ouroboros.EMPRESA_IE));
        emit.setCRT(documento.getRegimeTributario().getId().toString()); //Código de Regime Tributário

        return emit;
    }

    private static Dest montarDest() {
        Pessoa d = documento.getPessoa();

        TNFe.InfNFe.Dest dest = new TNFe.InfNFe.Dest();

        if (!d.getCpf().isEmpty()) {
            dest.setCPF(Texto.soNumeros(d.getCpf()));
        } else {
            dest.setCNPJ(Texto.soNumeros(d.getCnpj()));
        }

        if (NfeConfig.AMBIENTE.equals(AmbienteEnum.HOMOLOGACAO)) {
            dest.setXNome("NF-E EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL");
        } else {
            dest.setXNome(Texto.substring(Texto.removerEspeciais(d.getNome()), 0, 60));
        }

        TEndereco enderDest = new TEndereco();
        enderDest.setXLgr(d.getEndereco());
        enderDest.setNro(d.getNumero());
        enderDest.setXBairro(d.getBairro());
        enderDest.setCMun(d.getCodigoMunicipio());
        enderDest.setXMun(Texto.substring(d.getMunicipio(), 0, 60));
        enderDest.setUF(TUf.fromValue(d.getUf()));
        enderDest.setCEP(d.getCepSoNumeros());
        enderDest.setCPais("1058");
        enderDest.setXPais("BRASIL");
        if (!d.getTelefone1().isEmpty()) {
            enderDest.setFone(Texto.soNumeros(d.getTelefone1()));
        }
        dest.setEnderDest(enderDest);

        if (!d.getEmail().isEmpty()) {
            dest.setEmail(d.getEmail());
        }

        dest.setIndIEDest(!d.isIeIsento() && !d.getIe().isEmpty() ? "1" : "2"); //Indicador da IE do Destinatário //1=Contribuinte ICMS (informar a IE do destinatário) ... até 9

        if (!d.isIeIsento() && !d.getIe().isEmpty()) {
            dest.setIE(Texto.soNumeros(d.getIe()));
        }

        return dest;
    }

    private static TLocal montarEntrega() {
        TLocal entrega = new TLocal();

        if (!documento.getEntregaCnpj().isEmpty()) {
            entrega.setCNPJ(Texto.soNumeros(documento.getEntregaCnpj()));
        } else {
            entrega.setCPF(Texto.soNumeros(documento.getEntregaCpf()));
        }

        if (!documento.getEntregaIe().isEmpty()) {
            entrega.setIE(Texto.soNumeros(documento.getEntregaIe()));
        }

        entrega.setXNome(Texto.substring(Texto.removerEspeciais(documento.getEntregaNome()), 0, 60));

        entrega.setCEP(Texto.soNumeros(documento.getEntregaCep()));
        entrega.setXLgr(documento.getEntregaEndereco());
        entrega.setNro(documento.getEntregaNumero());
        entrega.setXCpl(documento.getEntregaComplemento());
        entrega.setXBairro(documento.getEntregaBairro());
        entrega.setCMun(documento.getEntregaCodigoMunicipio());
        Cidade cidade = new CidadeDAO().findByCodigoIbge(documento.getEntregaCodigoMunicipio());
        entrega.setXMun(cidade.getNome());
        entrega.setUF(TUf.fromValue(cidade.getEstado().getSigla()));

        entrega.setFone(Texto.soNumeros(documento.getEntregaTelefone()));
        entrega.setEmail(documento.getEntregaEmail());

        return entrega;
    }

    private static List<Det> montarDets() {

        List<Det> dets = new ArrayList<>();

        for (MovimentoFisico mf : documento.getMovimentosFisicosProdutos()) {
            Det det = new Det();
            det.setNItem(String.valueOf(documento.getMovimentosFisicosProdutos().indexOf(mf) + 1));

            //Produto
            Produto p = mf.getProduto();
            Prod prod = new Prod();

            prod.setCProd(mf.getCodigo());
            prod.setCEAN("SEM GTIN");
            prod.setXProd(Texto.substring(mf.getDescricao(), 0, 120));
            prod.setNCM(mf.getNcm().getCodigo());

            if (!mf.getCest().isEmpty()) {
                prod.setCEST(mf.getCest());
            }
            //prod.setIndEscala("S"); //???????????????????????????

            prod.setCFOP(mf.getCfop().getCodigo().toString());
            prod.setUCom(mf.getUnidadeComercialVenda().getNome());
            prod.setQCom(Decimal.toStringComPonto(mf.getSaldoLinearAbsoluto()));
            prod.setVUnCom(Decimal.toStringComPonto(mf.getValor()));
            prod.setVProd(Decimal.toStringComPonto(mf.getSubtotalItem()));
            prod.setCEANTrib("SEM GTIN");
            prod.setUTrib(mf.getUnidadeTributavel().getNome());
            prod.setQTrib(Decimal.toStringComPonto(mf.getQuantidadeTributavel()));
            prod.setVUnTrib(Decimal.toStringComPonto(mf.getValorTributavel()));

            if (mf.getValorFrete().compareTo(BigDecimal.ZERO) > 0) {
                prod.setVFrete(Decimal.toStringComPonto(mf.getValorFrete())); //I15 (13v2) Valor do frete
            }

            if (mf.getValorSeguro().compareTo(BigDecimal.ZERO) > 0) {
                prod.setVSeg(Decimal.toStringComPonto(mf.getValorSeguro())); //I16 (13v2) Valor do seguro
            }

            if (mf.getDescontoConsolidado().compareTo(BigDecimal.ZERO) > 0) {
                prod.setVDesc(Decimal.toStringComPonto(mf.getDescontoConsolidado())); //I17 (13v2) Valor do desconto
            }

            if (mf.getAcrescimoConsolidado().compareTo(BigDecimal.ZERO) > 0) {
                prod.setVOutro(Decimal.toStringComPonto(mf.getAcrescimoConsolidado())); //I17a (13v2) Outras despesas acessórias
            }

            prod.setIndTot("1");

            //J - Produto Específico -------------------------------------------
            if (mf.getAnp() != null) {
                TNFe.InfNFe.Det.Prod.Comb comb = new TNFe.InfNFe.Det.Prod.Comb(); //LA01

                comb.setCProdANP(mf.getAnp().getCodigo()); //LA02 Código de produto da ANP
                if (!mf.getCodif().isEmpty()) {
                    comb.setCODIF(mf.getCodif()); //LA04 Código de autorização / registro do CODIF
                }

                comb.setUFCons(TUf.fromValue(mf.getCombustivelUf())); //LA06 Sigla da UF de consumo
                comb.setDescANP(mf.getAnp().getDescricao()); //Descrição do produto conforme ANP

                if (mf.getCombustivelQuantidade().compareTo(BigDecimal.ZERO) > 0) {
                    comb.setQTemp(Decimal.toStringComPonto(mf.getCombustivelQuantidade(), 4)); //LA05 Quantidade de combustível faturada à temperatura ambiente.
                }
                //comb.setPGLP(""); //Percentual do GLP derivado do petróleo no produto GLP
                //comb.setPGNn(""); //Percentual de Gás Natural Nacional
                //comb.setPGNn(""); //Percentual de Gás Natural Importado
                //comb.setVPart(""); //Valor de Partida

                /*TNFe.InfNFe.Det.Prod.Comb.CIDE cide = new TNFe.InfNFe.Det.Prod.Comb.CIDE(); //LA07 - CIDE

                cide.setQBCProd(""); //LA08 BC da CIDE
                cide.setVAliqProd(""); //LA09 Valor da Alíquota da CIDE
                cide.setVCIDE(""); //LA10 Valor da CIDE
                
                comb.setCIDE(cide);
                 */
                prod.setComb(comb);

            }

            //Fim J - Produto Específico ---------------------------------------
            det.setProd(prod);

            //Impostos----------------------------------------------------------
            TNFe.InfNFe.Det.Imposto imposto = new TNFe.InfNFe.Det.Imposto();

            //icms
            TNFe.InfNFe.Det.Imposto.ICMS icms = montarIcms(mf);
            JAXBElement<TNFe.InfNFe.Det.Imposto.ICMS> icmsElement = new JAXBElement<>(new QName("ICMS"), TNFe.InfNFe.Det.Imposto.ICMS.class, icms);
            imposto.getContent().add(icmsElement);

            //ipi
            if (mf.getIpi() != null) {
                TIpi ipi =  montarIpi(mf);
                JAXBElement<TIpi> ipiElement = new JAXBElement<>(new QName("IPI"), TIpi.class, ipi);
                imposto.getContent().add(ipiElement);
            }
            
            //pis
            TNFe.InfNFe.Det.Imposto.PIS pis =  montarPis(mf);
            JAXBElement<TNFe.InfNFe.Det.Imposto.PIS> pisElement = new JAXBElement<>(new QName("PIS"), TNFe.InfNFe.Det.Imposto.PIS.class, pis);
            imposto.getContent().add(pisElement);
            
            //pis ST
            if (mf.getPis().getCodigo().equals("05")) {
                TNFe.InfNFe.Det.Imposto.PISST pisST =  montarPisSt(mf);
                JAXBElement<TNFe.InfNFe.Det.Imposto.PISST> pisSTElement = new JAXBElement<>(new QName("PISST"), TNFe.InfNFe.Det.Imposto.PISST.class, pisST);
                imposto.getContent().add(pisSTElement);
            }

            //cofins
            TNFe.InfNFe.Det.Imposto.COFINS cofins = montarCofins(mf);
            JAXBElement<TNFe.InfNFe.Det.Imposto.COFINS> cofinsElement = new JAXBElement<>(new QName("COFINS"), TNFe.InfNFe.Det.Imposto.COFINS.class, cofins);
            imposto.getContent().add(cofinsElement);
            
            //cofins ST
            if (mf.getCofins().getCodigo().equals("05")) {
                TNFe.InfNFe.Det.Imposto.COFINSST cofinsST =  montarCofinsSt(mf);
                JAXBElement<TNFe.InfNFe.Det.Imposto.COFINSST> cofinsSTElement = new JAXBElement<>(new QName("COFINSST"), TNFe.InfNFe.Det.Imposto.COFINSST.class, cofinsST);
                imposto.getContent().add(cofinsSTElement);
            }

            det.setImposto(imposto);

            //Fim Impostos------------------------------------------------------
            
            dets.add(det);
        }

        return dets;

    }

    private static ICMS montarIcms(MovimentoFisico mf) {
        TNFe.InfNFe.Det.Imposto.ICMS icms = new TNFe.InfNFe.Det.Imposto.ICMS();

        switch (mf.getIcms().getCodigo()) {
            case "00":
                TNFe.InfNFe.Det.Imposto.ICMS.ICMS00 icms00 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS00();
                icms00.setOrig(mf.getOrigem().getId().toString());
                icms00.setCST(mf.getIcms().getCodigo());
                icms00.setModBC(mf.getModalidadeBcIcms().getId().toString());
                icms00.setVBC(Decimal.toStringComPonto(mf.getValorBcIcms()));
                icms00.setPICMS(Decimal.toStringComPonto(mf.getAliquotaIcms(), 4));
                icms00.setVICMS(Decimal.toStringComPonto(mf.getValorIcms()));
                icms.setICMS00(icms00);
                break;
                
            case "10": 
                if (mf.getIcms().getId() == 2) { //10 (normal)
                    TNFe.InfNFe.Det.Imposto.ICMS.ICMS10 icms10 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS10();
                    icms10.setOrig(mf.getOrigem().getId().toString());
                    icms10.setCST(mf.getIcms().getCodigo());
                    icms10.setModBC(mf.getModalidadeBcIcms().getId().toString());
                    icms10.setVBC(Decimal.toStringComPonto(mf.getValorBcIcms()));
                    icms10.setPICMS(Decimal.toStringComPonto(mf.getAliquotaIcms(), 4));
                    icms10.setVICMS(Decimal.toStringComPonto(mf.getValorIcms()));

                    icms10.setModBCST(mf.getModalidadeBcIcmsSt().getId().toString());
                    icms10.setPMVAST(Decimal.toStringComPonto(mf.getPercentualMargemValorAdicionadoIcmsSt(), 4));
                    icms10.setPRedBCST(Decimal.toStringComPonto(mf.getPercentualReducaoBcIcmsSt(), 4));
                    icms10.setVBCST(Decimal.toStringComPonto(mf.getValorBcIcmsSt()));
                    icms10.setPICMSST(Decimal.toStringComPonto(mf.getAliquotaIcmsSt(), 4));
                    icms10.setVICMSST(Decimal.toStringComPonto(mf.getValorIcmsSt()));

                    icms.setICMS10(icms10);
                
                } else if (mf.getIcms().getId() == 3) { //10 (com partilha ...)
                    TNFe.InfNFe.Det.Imposto.ICMS.ICMSPart icmsPart = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSPart();
                    icmsPart.setOrig(mf.getOrigem().getId().toString());
                    icmsPart.setCST(mf.getIcms().getCodigo());
                    
                    icmsPart.setModBC(mf.getModalidadeBcIcms().getId().toString()); //N13
                    icmsPart.setPRedBC(Decimal.toStringComPonto(mf.getPercentualReducaoBcIcms(), 4)); //N14
                    icmsPart.setVBC(Decimal.toStringComPonto(mf.getValorBcIcms())); //N15
                    icmsPart.setPICMS(Decimal.toStringComPonto(mf.getAliquotaIcms(), 4)); //16
                    icmsPart.setVICMS(Decimal.toStringComPonto(mf.getValorIcms())); //N17

                    icmsPart.setModBCST(mf.getModalidadeBcIcmsSt().getId().toString()); //N18 Modalidade de determinação da BC do ICMS ST
                    icmsPart.setPMVAST(Decimal.toStringComPonto(mf.getPercentualMargemValorAdicionadoIcmsSt(), 4)); //N19 3v2-4 Percentual da margem de valor Adicionado do ICMS ST
                    icmsPart.setPRedBCST(Decimal.toStringComPonto(mf.getPercentualReducaoBcIcmsSt(), 4)); //N20 3v2-4 Percentual da Redução de BC do ICMS ST
                    icmsPart.setVBCST(Decimal.toStringComPonto(mf.getValorBcIcmsSt())); //N21 13v2 Valor da BC do ICMS ST
                    icmsPart.setPICMSST(Decimal.toStringComPonto(mf.getAliquotaIcmsSt())); //N22 3v2 Alíquota do imposto do ICMS ST
                    icmsPart.setVICMSST(Decimal.toStringComPonto(mf.getValorIcmsSt())); //N23 13v2 Valor do ICMS ST retido

                    icmsPart.setPBCOp(Decimal.toStringComPonto(mf.getPercentualBcOperacaoPropria(), 4));
                    icmsPart.setUFST(TUf.fromValue(mf.getIcmsStUf()));
                    
                    icms.setICMSPart(icmsPart);
                    
                }
                
                break;
                
            case "20":
                TNFe.InfNFe.Det.Imposto.ICMS.ICMS20 icms20 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS20();
                icms20.setOrig(mf.getOrigem().getId().toString());
                icms20.setCST(mf.getIcms().getCodigo());
                icms20.setModBC(mf.getModalidadeBcIcms().getId().toString());
                icms20.setPRedBC(Decimal.toStringComPonto(mf.getPercentualReducaoBcIcms(), 4));
                icms20.setVBC(Decimal.toStringComPonto(mf.getValorBcIcms()));
                icms20.setPICMS(Decimal.toStringComPonto(mf.getAliquotaIcms(), 4));
                icms20.setVICMS(Decimal.toStringComPonto(mf.getValorIcms()));

                if (mf.getValorIcmsDesonerado().compareTo(BigDecimal.ZERO) > 0) {
                    icms20.setVICMSDeson(Decimal.toStringComPonto(mf.getValorIcmsDesonerado()));
                    icms20.setMotDesICMS(mf.getMotivoDesoneracao().getId().toString());
                }

                icms.setICMS20(icms20);
                    
                
                
                
                
            
                break;
                
            case "30":
                TNFe.InfNFe.Det.Imposto.ICMS.ICMS30 icms30 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS30();
                icms30.setOrig(mf.getOrigem().getId().toString());
                icms30.setCST(mf.getIcms().getCodigo());
                icms30.setModBCST(mf.getModalidadeBcIcmsSt().getId().toString());
                icms30.setPMVAST(Decimal.toStringComPonto(mf.getPercentualMargemValorAdicionadoIcmsSt(), 4));
                icms30.setPRedBCST(Decimal.toStringComPonto(mf.getPercentualReducaoBcIcmsSt(), 4));
                icms30.setVBCST(Decimal.toStringComPonto(mf.getValorBcIcmsSt()));
                icms30.setPICMSST(Decimal.toStringComPonto(mf.getAliquotaIcmsSt(), 4));
                icms30.setVICMSST(Decimal.toStringComPonto(mf.getValorIcmsSt()));
                
                if (mf.getValorIcmsDesonerado().compareTo(BigDecimal.ZERO) > 0) {
                    icms30.setVICMSDeson(Decimal.toStringComPonto(mf.getValorIcmsDesonerado()));
                    icms30.setMotDesICMS(mf.getMotivoDesoneracao().getId().toString());
                }
                
                icms.setICMS30(icms30);
                
                break;
                
            case "40":
            case "41":
            case "50":
                if (mf.getIcms().getId() == 7) { //41
                    TNFe.InfNFe.Det.Imposto.ICMS.ICMS40 icms40 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS40();
                    icms40.setCST(mf.getIcms().getCodigo());
                    icms40.setOrig(mf.getOrigem().getId().toString());
                    icms40.setVICMSDeson(Decimal.toStringComPonto(documento.getTotalIcmsDesonerado()));
                    icms40.setMotDesICMS(mf.getMotivoDesoneracao().getId().toString());

                    icms.setICMS40(icms40);
                    
                } else if (mf.getIcms().getId() == 8) { //41 (icms devido...)
                    TNFe.InfNFe.Det.Imposto.ICMS.ICMSST icmsST = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSST();
                    icmsST.setCST(mf.getIcms().getCodigo());
                    icmsST.setOrig(mf.getOrigem().getId().toString());
                    icmsST.setVBCSTRet(Decimal.toStringComPonto(mf.getValorBcIcmsStRetido())); //N26 13v2 vBCSTRet Valor da BC do ICMS ST retido
                    icmsST.setVICMSSTRet(Decimal.toStringComPonto(mf.getValorIcmsStRetido())); //N27 13v2 vICMSSTRet Valor do ICMS ST retido
                    icmsST.setVBCSTDest(Decimal.toStringComPonto(mf.getIcmsStValorBcUfDestino()));
                    icmsST.setVICMSSTDest(Decimal.toStringComPonto(mf.getIcmsStValorUfDestino()));
                    
                    icmsST.setPST(Decimal.toStringComPonto(mf.getAliquotaSuportadaConsumidorFinal()));
                    icmsST.setVICMSSubstituto(Decimal.toStringComPonto(mf.getValorIcmsProprioSubstituto()));
                    
                    icms.setICMSST(icmsST);
                }
                
                break;
                
            case "51":
                TNFe.InfNFe.Det.Imposto.ICMS.ICMS51 icms51 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS51();
                icms51.setOrig(mf.getOrigem().getId().toString());
                icms51.setCST(mf.getIcms().getCodigo());
                
                icms51.setModBC(mf.getModalidadeBcIcms().getId().toString());
                icms51.setPRedBC(Decimal.toStringComPonto(mf.getPercentualReducaoBcIcms(), 4));
                icms51.setVBC(Decimal.toStringComPonto(mf.getValorBcIcms()));
                icms51.setPICMS(Decimal.toStringComPonto(mf.getAliquotaIcms(), 4));
                icms51.setVICMSOp(Decimal.toStringComPonto(mf.getIcmsValorOperacao()));
                icms51.setPDif(Decimal.toStringComPonto(mf.getIcmsPercentualDiferimento(), 4));
                icms51.setVICMSDif(Decimal.toStringComPonto(mf.getIcmsValorDiferido()));
                
                icms51.setVICMS(Decimal.toStringComPonto(mf.getValorIcms()));
                
                icms.setICMS51(icms51);
                
                break;
                
            case "60":
                if (mf.getIcms().getId() == 11) { //60 (sem comentários)
                    TNFe.InfNFe.Det.Imposto.ICMS.ICMS60 icms60 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS60();
                    icms60.setOrig(mf.getOrigem().getId().toString());
                    icms60.setCST(mf.getIcms().getCodigo());
                    icms60.setVBCSTRet(Decimal.toStringComPonto(mf.getValorBcIcmsStRetido())); //N26 13v2 vBCSTRet Valor da BC do ICMS ST retido
                    icms60.setVICMSSTRet(Decimal.toStringComPonto(mf.getValorIcmsStRetido())); //N27 13v2 vICMSSTRet Valor do ICMS ST retido

                    icms60.setPST(Decimal.toStringComPonto(mf.getAliquotaSuportadaConsumidorFinal())); //N26a Alíquota suportada pelo consumidor final
                    icms60.setVICMSSubstituto(Decimal.toStringComPonto(mf.getValorIcmsProprioSubstituto())); //N26b Valor do ICMS próprio do substituto

                    icms.setICMS60(icms60);
                
                } else if (mf.getIcms().getId() == 12) { //60 (icms devido...)
                    TNFe.InfNFe.Det.Imposto.ICMS.ICMSST icmsST = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSST();
                    icmsST.setOrig(mf.getOrigem().getId().toString());
                    icmsST.setCST(mf.getIcms().getCodigo());
                    
                    icmsST.setVBCSTRet(Decimal.toStringComPonto(mf.getValorBcIcmsStRetido())); //N26 13v2 vBCSTRet Valor da BC do ICMS ST retido
                    icmsST.setVICMSSTRet(Decimal.toStringComPonto(mf.getValorIcmsStRetido())); //N27 13v2 vICMSSTRet Valor do ICMS ST retido
                    icmsST.setVBCSTDest(Decimal.toStringComPonto(mf.getIcmsStValorBcUfDestino()));
                    icmsST.setVICMSSTDest(Decimal.toStringComPonto(mf.getIcmsStValorUfDestino()));
                    
                    icmsST.setPST(Decimal.toStringComPonto(mf.getAliquotaSuportadaConsumidorFinal()));
                    icmsST.setVICMSSubstituto(Decimal.toStringComPonto(mf.getValorIcmsProprioSubstituto()));
                    
                    icms.setICMSST(icmsST);
                }
                
                break;
                
            case "70":
                TNFe.InfNFe.Det.Imposto.ICMS.ICMS70 icms70 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS70();
                icms70.setOrig(mf.getOrigem().getId().toString());
                icms70.setCST(mf.getIcms().getCodigo());
                icms70.setModBC(mf.getModalidadeBcIcms().getId().toString()); //N13
                icms70.setPRedBC(Decimal.toStringComPonto(mf.getPercentualReducaoBcIcms())); //N14
                icms70.setVBC(Decimal.toStringComPonto(mf.getValorBcIcms())); //N15
                icms70.setPICMS(Decimal.toStringComPonto(mf.getAliquotaIcms())); //16
                icms70.setVICMS(Decimal.toStringComPonto(mf.getValorIcms())); //N17
                
                
                icms70.setModBCST(mf.getModalidadeBcIcmsSt().getId().toString()); //N18 Modalidade de determinação da BC do ICMS ST
                icms70.setPMVAST(Decimal.toStringComPonto(mf.getPercentualMargemValorAdicionadoIcmsSt())); //N19 3v2-4 Percentual da margem de valor Adicionado do ICMS ST
                icms70.setPRedBCST(Decimal.toStringComPonto(mf.getPercentualReducaoBcIcmsSt())); //N20 3v2-4 Percentual da Redução de BC do ICMS ST
                icms70.setVBCST(Decimal.toStringComPonto(mf.getValorBcIcmsSt())); //N21 13v2 Valor da BC do ICMS ST
                icms70.setPICMSST(Decimal.toStringComPonto(mf.getAliquotaIcmsSt())); //N22 3v2 Alíquota do imposto do ICMS ST
                icms70.setVICMSST(Decimal.toStringComPonto(mf.getValorIcmsSt())); //N23 13v2 Valor do ICMS ST retido

                if (mf.getValorIcmsDesonerado().compareTo(BigDecimal.ZERO) > 0) {
                    icms70.setVICMSDeson(Decimal.toStringComPonto(mf.getValorIcmsDesonerado()));
                    icms70.setMotDesICMS(mf.getMotivoDesoneracao().getId().toString());
                }

                icms.setICMS70(icms70);
                break;
                
            case "90":
                if (mf.getIcms().getId() == 14) { //90 (com partilha ...)
                    TNFe.InfNFe.Det.Imposto.ICMS.ICMSPart icmsPart = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSPart();
                    icmsPart.setOrig(mf.getOrigem().getId().toString());
                    icmsPart.setCST(mf.getIcms().getCodigo());
                    
                    icmsPart.setModBC(mf.getModalidadeBcIcms().getId().toString()); //N13
                    icmsPart.setPRedBC(Decimal.toStringComPonto(mf.getPercentualReducaoBcIcms(), 4)); //N14
                    icmsPart.setVBC(Decimal.toStringComPonto(mf.getValorBcIcms())); //N15
                    icmsPart.setPICMS(Decimal.toStringComPonto(mf.getAliquotaIcms(), 4)); //16
                    icmsPart.setVICMS(Decimal.toStringComPonto(mf.getValorIcms())); //N17

                    icmsPart.setModBCST(mf.getModalidadeBcIcmsSt().getId().toString()); //N18 Modalidade de determinação da BC do ICMS ST
                    icmsPart.setPMVAST(Decimal.toStringComPonto(mf.getPercentualMargemValorAdicionadoIcmsSt(), 4)); //N19 3v2-4 Percentual da margem de valor Adicionado do ICMS ST
                    icmsPart.setPRedBCST(Decimal.toStringComPonto(mf.getPercentualReducaoBcIcmsSt(), 4)); //N20 3v2-4 Percentual da Redução de BC do ICMS ST
                    icmsPart.setVBCST(Decimal.toStringComPonto(mf.getValorBcIcmsSt())); //N21 13v2 Valor da BC do ICMS ST
                    icmsPart.setPICMSST(Decimal.toStringComPonto(mf.getAliquotaIcmsSt())); //N22 3v2 Alíquota do imposto do ICMS ST
                    icmsPart.setVICMSST(Decimal.toStringComPonto(mf.getValorIcmsSt())); //N23 13v2 Valor do ICMS ST retido

                    icmsPart.setPBCOp(Decimal.toStringComPonto(mf.getPercentualBcOperacaoPropria(), 4));
                    icmsPart.setUFST(TUf.fromValue(mf.getIcmsStUf()));
                    
                    icms.setICMSPart(icmsPart);
                    
                } else if (mf.getIcms().getId() == 15) { //90 (sem comentários)
                    TNFe.InfNFe.Det.Imposto.ICMS.ICMS90 icms90 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS90();
                    icms90.setOrig(mf.getOrigem().getId().toString());
                    icms90.setCST(mf.getIcms().getCodigo());

                    if (mf.getModalidadeBcIcms() != null) {
                        icms90.setModBC(mf.getModalidadeBcIcms().getId().toString()); //N13
                        icms90.setPRedBC(Decimal.toStringComPonto(mf.getPercentualReducaoBcIcms(), 4)); //N14
                        icms90.setVBC(Decimal.toStringComPonto(mf.getValorBcIcms())); //N15
                        icms90.setPICMS(Decimal.toStringComPonto(mf.getAliquotaIcms(), 4)); //16
                        icms90.setVICMS(Decimal.toStringComPonto(mf.getValorIcms())); //N17
                    }

                    if (mf.getModalidadeBcIcmsSt() != null) {
                        icms90.setModBCST(mf.getModalidadeBcIcmsSt().getId().toString()); //N18 Modalidade de determinação da BC do ICMS ST
                        icms90.setPMVAST(Decimal.toStringComPonto(mf.getPercentualMargemValorAdicionadoIcmsSt(), 4)); //N19 3v2-4 Percentual da margem de valor Adicionado do ICMS ST
                        icms90.setPRedBCST(Decimal.toStringComPonto(mf.getPercentualReducaoBcIcmsSt(), 4)); //N20 3v2-4 Percentual da Redução de BC do ICMS ST
                        icms90.setVBCST(Decimal.toStringComPonto(mf.getValorBcIcmsSt())); //N21 13v2 Valor da BC do ICMS ST
                        icms90.setPICMSST(Decimal.toStringComPonto(mf.getAliquotaIcmsSt())); //N22 3v2 Alíquota do imposto do ICMS ST
                        icms90.setVICMSST(Decimal.toStringComPonto(mf.getValorIcmsSt())); //N23 13v2 Valor do ICMS ST retido
                    }

                    if (mf.getValorIcmsDesonerado().compareTo(BigDecimal.ZERO) > 0) {
                        icms90.setVICMSDeson(Decimal.toStringComPonto(mf.getValorIcmsDesonerado()));
                        icms90.setMotDesICMS(mf.getMotivoDesoneracao().getId().toString());
                    }

                    icms.setICMS90(icms90);
                }
                    
                    
                break;
                
                
            case "101":
                TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN101 icms101 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN101();
                icms101.setOrig(mf.getOrigem().getId().toString());
                icms101.setCSOSN("101");
                icms101.setPCredSN(Decimal.toStringComPonto(mf.getAliquotaAplicavelCalculoCreditoIcms())); //N29 3v2-4
                icms101.setVCredICMSSN(Decimal.toStringComPonto(mf.getValorCreditoIcms())); //N30 13v2 Valor crédito do icms que pode ser aproveitado nos termos do art. 23 da LC 123 (Simples Nacional)
                icms.setICMSSN101(icms101);
                break;

            case "102":
            case "103":
            case "300":
            case "400":
                TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN102 icms102 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN102();
                icms102.setOrig(mf.getOrigem().getId().toString());
                icms102.setCSOSN(mf.getIcms().getCodigo());
                icms.setICMSSN102(icms102);
                break;

            case "201":
                TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN201 icms201 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN201();
                icms201.setOrig(mf.getOrigem().getId().toString());
                icms201.setCSOSN("201");
                icms201.setModBCST(mf.getModalidadeBcIcmsSt().getId().toString()); //N18 Modalidade de determinação da BC do ICMS ST
                icms201.setPMVAST(Decimal.toStringComPonto(mf.getPercentualMargemValorAdicionadoIcmsSt())); //N19 3v2-4 Percentual da margem de valor Adicionado do ICMS ST
                icms201.setPRedBCST(Decimal.toStringComPonto(mf.getPercentualReducaoBcIcmsSt())); //N20 3v2-4 Percentual da Redução de BC do ICMS ST
                icms201.setVBCST(Decimal.toStringComPonto(mf.getValorBcIcmsSt())); //N21 13v2 Valor da BC do ICMS ST
                icms201.setPICMSST(Decimal.toStringComPonto(mf.getAliquotaIcmsSt())); //N22 3v2 Alíquota do imposto do ICMS ST
                icms201.setVICMSST(Decimal.toStringComPonto(mf.getValorIcmsSt())); //N23 13v2 Valor do ICMS ST retido
                icms201.setPCredSN(Decimal.toStringComPonto(mf.getAliquotaAplicavelCalculoCreditoIcms())); //N29 3v2-4 Alíquota aplicável de cálculo do crédito (SIMPLES NACIONAL)
                icms201.setVCredICMSSN(Decimal.toStringComPonto(mf.getValorCreditoIcms())); //N30 13v2 Valor crédito do ICMS que pode ser aproveitado nos termos do art. 23 da LC 123 (SIMPLES NACIONAL)
                icms.setICMSSN201(icms201);
                break;

            case "202":
            case "203":
                TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN202 icms202 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN202();
                icms202.setOrig(mf.getOrigem().getId().toString());
                icms202.setCSOSN(mf.getIcms().getCodigo());
                icms202.setModBCST(mf.getModalidadeBcIcmsSt().getId().toString()); //N18 Modalidade de determinação da BC do ICMS ST
                icms202.setPMVAST(Decimal.toStringComPonto(mf.getPercentualMargemValorAdicionadoIcmsSt())); //N19 3v2-4 Percentual da margem de valor Adicionado do ICMS ST
                icms202.setPRedBCST(Decimal.toStringComPonto(mf.getPercentualReducaoBcIcmsSt())); //N20 3v2-4 Percentual da Redução de BC do ICMS ST
                icms202.setVBCST(Decimal.toStringComPonto(mf.getValorBcIcmsSt())); //N21 13v2 Valor da BC do ICMS ST
                icms202.setPICMSST(Decimal.toStringComPonto(mf.getAliquotaIcmsSt())); //N22 3v2 Alíquota do imposto do ICMS ST
                icms202.setVICMSST(Decimal.toStringComPonto(mf.getValorIcmsSt())); //N23 13v2 Valor do ICMS ST retido
                icms.setICMSSN202(icms202);
                break;

            //case "300": Imune - não tem nenhum campo
            case "500":
                TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN500 icms500 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN500();
                icms500.setOrig(mf.getOrigem().getId().toString());
                icms500.setCSOSN("500");
                icms500.setVBCSTRet(Decimal.toStringComPonto(mf.getValorBcIcmsStRetido())); //N26 13v2 vBCSTRet Valor da BC do ICMS ST retido
                icms500.setPST(Decimal.toStringComPonto(mf.getAliquotaSuportadaConsumidorFinal())); //N26a Alíquota suportada pelo consumidor final
                icms500.setVICMSSubstituto(Decimal.toStringComPonto(mf.getValorIcmsProprioSubstituto())); //N26b Valor do ICMS próprio do substituto
                icms500.setVICMSSTRet(Decimal.toStringComPonto(mf.getValorIcmsStRetido())); //N27 13v2 vICMSSTRet Valor do ICMS ST retido
                icms.setICMSSN500(icms500);
                break;

            case "900":
                TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN900 icms900 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN900();
                icms900.setOrig(mf.getOrigem().getId().toString());
                icms900.setCSOSN("900");

                icms900.setModBC(mf.getModalidadeBcIcms().getId().toString()); //N13 Modalidade de determinação da BC do ICMS
                icms900.setVBC(Decimal.toStringComPonto(mf.getValorBcIcms())); //N15 13v2 Valor da BC do ICMS
                icms900.setPRedBC(Decimal.toStringComPonto(mf.getPercentualReducaoBcIcms())); //N14 3v2-4 Percentual da Redução de BC
                icms900.setPICMS(Decimal.toStringComPonto(mf.getAliquotaIcms())); //N16 3v2-4 Alíquota do imposto
                icms900.setVICMS(Decimal.toStringComPonto(mf.getValorIcms())); //N17 13v2 Valor do ICMS

                icms900.setModBCST(mf.getModalidadeBcIcmsSt().getId().toString()); //N18 Modalidade de determinação da BC do ICMS ST
                icms900.setPMVAST(Decimal.toStringComPonto(mf.getPercentualMargemValorAdicionadoIcmsSt())); //N19 3v2-4 Percentual da margem de valor Adicionado do ICMS ST
                icms900.setPRedBCST(Decimal.toStringComPonto(mf.getPercentualReducaoBcIcmsSt())); //N20 3v2-4 Percentual da Redução de BC do ICMS ST
                icms900.setVBCST(Decimal.toStringComPonto(mf.getValorBcIcmsSt())); //N21 13v2 Valor da BC do ICMS ST
                icms900.setPICMSST(Decimal.toStringComPonto(mf.getAliquotaIcmsSt())); //N22 3v2 Alíquota do imposto do ICMS ST
                icms900.setVICMSST(Decimal.toStringComPonto(mf.getValorIcmsSt())); //N23 13v2 Valor do ICMS ST retido
                icms900.setPCredSN(Decimal.toStringComPonto(mf.getAliquotaAplicavelCalculoCreditoIcms())); //N29 3v2-4 Alíquota aplicável de cálculo do crédito (SIMPLES NACIONAL)
                icms900.setVCredICMSSN(Decimal.toStringComPonto(mf.getValorCreditoIcms())); //N30 13v2 Valor crédito do ICMS que pode ser aproveitado nos termos do art. 23 da LC 123 (SIMPLES NACIONAL)
                icms.setICMSSN900(icms900);
                break;

        }

        return icms;
    }
    /* private static Det.Imposto.PISST montarPisSt(MovimentoFisico mf) {
        TNFe.InfNFe.Det.Imposto.PISST pisSt = new TNFe.InfNFe.Det.Imposto.PISST();*/
    private static TIpi montarIpi(MovimentoFisico mf) {
        TIpi ipi = new TIpi();

        if (!mf.getIpiCnpjProdutor().isEmpty()) {
            ipi.setCNPJProd(Texto.soNumeros(mf.getIpiCnpjProdutor()));
        }
        //ipi.setCSelo(mf.getipi);
        //ipi.setQSelo(cNf);
        ipi.setCEnq(mf.getIpiCodigoEnquadramento());
                
        switch (mf.getIpi().getCodigo()) {
            case "00":
            case "49":
            case "50":
            case "99":
                TIpi.IPITrib ipiTrib = new TIpi.IPITrib();
                ipiTrib.setCST(mf.getIpi().getCodigo()); //O09 CST
                
                if (mf.getIpiTipoCalculo().equals(TipoCalculoEnum.PERCENTUAL)) {
                    ipiTrib.setVBC(Decimal.toStringComPonto(mf.getIpiValorBc())); //O10 vBC
                    ipiTrib.setPIPI(Decimal.toStringComPonto(mf.getIpiAliquota(), 4)); //O13 pIPI
                
                } else {
                    ipiTrib.setQUnid(Decimal.toStringComPonto(mf.getIpiQuantidadeTotalUnidadePadrao(), 4)); //O11 qUnid
                    ipiTrib.setVUnid(Decimal.toStringComPonto(mf.getIpiValorUnidadeTributavel(), 4)); //O12 vUnid
                }
                
                ipiTrib.setVIPI(Decimal.toStringComPonto(mf.getIpiValor())); //O14 vIPI
                
                ipi.setIPITrib(ipiTrib);
                break;
            
            default:
                TIpi.IPINT ipiNt = new TIpi.IPINT();
                ipiNt.setCST(mf.getIpi().getCodigo()); //O09 CST
                
                ipi.setIPINT(ipiNt);
                break;
        }

        return ipi;
    }
    
    private static Det.Imposto.PIS montarPis(MovimentoFisico mf) {
        TNFe.InfNFe.Det.Imposto.PIS pis = new TNFe.InfNFe.Det.Imposto.PIS();

        switch (mf.getPis().getCodigo()) {
            case "01":
            case "02":
                TNFe.InfNFe.Det.Imposto.PIS.PISAliq pisAliq = new TNFe.InfNFe.Det.Imposto.PIS.PISAliq();
                pisAliq.setCST(mf.getPis().getCodigo()); //Q06 CST
                pisAliq.setVBC(Decimal.toStringComPonto(mf.getValorBcPis())); //Q07 vBC
                pisAliq.setPPIS(Decimal.toStringComPonto(mf.getAliquotaPis(), 4)); //Q08 pPIS
                pisAliq.setVPIS(Decimal.toStringComPonto(mf.getValorPis())); //Q09 vPIS
                pis.setPISAliq(pisAliq);
                break;
                
            case "03":
                TNFe.InfNFe.Det.Imposto.PIS.PISQtde pisQtde = new TNFe.InfNFe.Det.Imposto.PIS.PISQtde();
                pisQtde.setCST(mf.getPis().getCodigo()); //Q06 CST
                pisQtde.setQBCProd(Decimal.toStringComPonto(mf.getQuantidadeVendidaPis(), 4)); //Q10 qBCProd
                pisQtde.setVAliqProd(Decimal.toStringComPonto(mf.getAliquotaPisReais(), 4)); //Q11 vAliqProd
                pisQtde.setVPIS(Decimal.toStringComPonto(mf.getValorPis())); //Q09 vPIS
                pis.setPISQtde(pisQtde);
                break;
                
            case "04":
            case "05":
            case "06":
            case "07":
            case "08":
            case "09":
                TNFe.InfNFe.Det.Imposto.PIS.PISNT pisNT = new TNFe.InfNFe.Det.Imposto.PIS.PISNT();
                pisNT.setCST(mf.getPis().getCodigo());
                pis.setPISNT(pisNT);
                break;
                
            default:
                TNFe.InfNFe.Det.Imposto.PIS.PISOutr pisOutr = new TNFe.InfNFe.Det.Imposto.PIS.PISOutr();
                pisOutr.setCST(mf.getPis().getCodigo()); //Q06 CST
                
                if (mf.getPisTipoCalculo() != null && mf.getPisTipoCalculo().equals(TipoCalculoEnum.PERCENTUAL)) {
                    pisOutr.setVBC(Decimal.toStringComPonto(mf.getValorBcPis())); //Q07 vBC
                    pisOutr.setPPIS(Decimal.toStringComPonto(mf.getAliquotaPis(), 4)); //Q08 pPIS
                
                } else {
                    pisOutr.setQBCProd(Decimal.toStringComPonto(mf.getQuantidadeVendidaPis(), 4)); //Q10 qBCProd
                    pisOutr.setVAliqProd(Decimal.toStringComPonto(mf.getAliquotaPisReais(), 4)); //Q11 vAliqProd
                }
                
                pisOutr.setVPIS(Decimal.toStringComPonto(mf.getValorPis())); //Q09 vPIS
                pis.setPISOutr(pisOutr);
                break;
                
        }

        return pis;
    }
    
    private static Det.Imposto.PISST montarPisSt(MovimentoFisico mf) {
        TNFe.InfNFe.Det.Imposto.PISST pisSt = new TNFe.InfNFe.Det.Imposto.PISST();

        if (mf.getPisStTipoCalculo().equals(TipoCalculoEnum.PERCENTUAL)) {
            pisSt.setVBC(Decimal.toStringComPonto(mf.getValorBcPisSt())); //R02 vBC
            pisSt.setPPIS(Decimal.toStringComPonto(mf.getAliquotaPisSt(), 4)); //R03 pPIS

        } else {
            pisSt.setQBCProd(Decimal.toStringComPonto(mf.getQuantidadeVendidaPisSt(), 4)); //R04 qBCProd
            pisSt.setVAliqProd(Decimal.toStringComPonto(mf.getAliquotaPisStReais(), 4)); //R05 vAliqProd
        }

        pisSt.setVPIS(Decimal.toStringComPonto(mf.getValorPisSt())); //R06 vPIS
                

        return pisSt;
    }
    
    private static Det.Imposto.COFINS montarCofins(MovimentoFisico mf) {
        TNFe.InfNFe.Det.Imposto.COFINS cofins = new TNFe.InfNFe.Det.Imposto.COFINS();

        switch (mf.getCofins().getCodigo()) {
            case "01":
            case "02":
                TNFe.InfNFe.Det.Imposto.COFINS.COFINSAliq cofinsAliq = new TNFe.InfNFe.Det.Imposto.COFINS.COFINSAliq();
                cofinsAliq.setCST(mf.getCofins().getCodigo()); //S06 CST
                cofinsAliq.setVBC(Decimal.toStringComPonto(mf.getValorBcCofins())); //S07 vBC
                cofinsAliq.setPCOFINS(Decimal.toStringComPonto(mf.getAliquotaCofins(), 4)); //S08 pCOFINS
                cofinsAliq.setVCOFINS(Decimal.toStringComPonto(mf.getValorCofins())); //S11 vCOFINS
                cofins.setCOFINSAliq(cofinsAliq);
                break;
                
            case "03":
                TNFe.InfNFe.Det.Imposto.COFINS.COFINSQtde cofinsQtde = new TNFe.InfNFe.Det.Imposto.COFINS.COFINSQtde();
                cofinsQtde.setCST(mf.getCofins().getCodigo()); //S06 CST
                cofinsQtde.setQBCProd(Decimal.toStringComPonto(mf.getQuantidadeVendidaCofins(), 4)); //S09 qBCProd
                cofinsQtde.setVAliqProd(Decimal.toStringComPonto(mf.getAliquotaCofinsReais(), 4)); //S10 vAliqProd
                cofinsQtde.setVCOFINS(Decimal.toStringComPonto(mf.getValorCofins())); //S11 vCOFINS
                cofins.setCOFINSQtde(cofinsQtde);
                break;
                
            case "04":
            case "05":
            case "06":
            case "07":
            case "08":
            case "09":
                TNFe.InfNFe.Det.Imposto.COFINS.COFINSNT cofinsNT = new TNFe.InfNFe.Det.Imposto.COFINS.COFINSNT();
                cofinsNT.setCST(mf.getCofins().getCodigo()); //S06 CST
                cofins.setCOFINSNT(cofinsNT);
                break;
                
            default:
                TNFe.InfNFe.Det.Imposto.COFINS.COFINSOutr cofinsOutr = new TNFe.InfNFe.Det.Imposto.COFINS.COFINSOutr();
                cofinsOutr.setCST(mf.getCofins().getCodigo()); //S06 CST
                
                if (mf.getCofinsTipoCalculo() != null && mf.getCofinsTipoCalculo().equals(TipoCalculoEnum.PERCENTUAL)) {
                    cofinsOutr.setVBC(Decimal.toStringComPonto(mf.getValorBcCofins())); //S07 vBC
                    cofinsOutr.setPCOFINS(Decimal.toStringComPonto(mf.getAliquotaCofins(), 4)); //S08 pCOFINS
                
                } else {
                    cofinsOutr.setQBCProd(Decimal.toStringComPonto(mf.getQuantidadeVendidaCofins(), 4)); //S09 qBCProd
                    cofinsOutr.setVAliqProd(Decimal.toStringComPonto(mf.getAliquotaCofinsReais(), 4)); //S10 vAliqProd
                }
                
                cofinsOutr.setVCOFINS(Decimal.toStringComPonto(mf.getValorCofins())); //S11 vCOFINS
                cofins.setCOFINSOutr(cofinsOutr);
                break;
                
        }

        return cofins;
    }
    
    private static Det.Imposto.COFINSST montarCofinsSt(MovimentoFisico mf) {
        TNFe.InfNFe.Det.Imposto.COFINSST cofinsSt = new TNFe.InfNFe.Det.Imposto.COFINSST();

        if (mf.getCofinsStTipoCalculo().equals(TipoCalculoEnum.PERCENTUAL)) {
            cofinsSt.setVBC(Decimal.toStringComPonto(mf.getValorBcCofinsSt())); //T02 vBC
            cofinsSt.setPCOFINS(Decimal.toStringComPonto(mf.getAliquotaCofinsSt(), 4)); //T03 pCOFINS

        } else {
            cofinsSt.setQBCProd(Decimal.toStringComPonto(mf.getQuantidadeVendidaCofinsSt(), 4)); //T04 qBCProd
            cofinsSt.setVAliqProd(Decimal.toStringComPonto(mf.getAliquotaCofinsStReais(), 4)); //T05 vAliqProd
        }

        cofinsSt.setVCOFINS(Decimal.toStringComPonto(mf.getValorCofinsSt())); //T06 vCOFINS
                

        return cofinsSt;
    }

    private static Total montarTotal() {

        Total total = new Total();

        TNFe.InfNFe.Total.ICMSTot icmstot = new TNFe.InfNFe.Total.ICMSTot();
        icmstot.setVBC(Decimal.toStringComPonto(documento.getTotalBcIcms()));
        icmstot.setVICMS(Decimal.toStringComPonto(documento.getTotalIcms()));
        icmstot.setVICMSDeson(Decimal.toStringComPonto(documento.getTotalIcmsDesonerado()));
        icmstot.setVFCP("0.00");
        icmstot.setVFCPST("0.00");
        icmstot.setVFCPSTRet("0.00");
        icmstot.setVBCST(Decimal.toStringComPonto(documento.getTotalBcIcmsSt()));
        icmstot.setVST(Decimal.toStringComPonto(documento.getTotalIcmsSt()));
        icmstot.setVProd(Decimal.toStringComPonto(documento.getTotalItensProdutos())); //W07 (13v2) Valor total dos produtos e serviços
        icmstot.setVFrete(Decimal.toStringComPonto(documento.getTotalFreteProdutos())); //W08 (13v2) Valor total do frete
        icmstot.setVSeg(Decimal.toStringComPonto(documento.getTotalSeguroProdutos())); //W09 (13v2) Valor total do seguro
        icmstot.setVDesc(Decimal.toStringComPonto(documento.getTotalDescontoProdutosEmMonetario())); //W10 (13v2) Valor total do desconto
        icmstot.setVII("0.00");
        icmstot.setVIPI(Decimal.toStringComPonto(documento.getTotalIpi()));
        icmstot.setVIPIDevol("0.00");
        icmstot.setVPIS(Decimal.toStringComPonto(documento.getTotalPis()));
        icmstot.setVCOFINS(Decimal.toStringComPonto(documento.getTotalCofins()));
        icmstot.setVOutro(Decimal.toStringComPonto(documento.getTotalOutrosProdutos())); //W15 (13v2) Outras despesas acessórias
        icmstot.setVNF(Decimal.toStringComPonto(documento.getTotalProdutos())); //W16 (13v2) Valor total da NF-e
        //icmstot.setVTotTrib(---);

        total.setICMSTot(icmstot);

        return total;
    }

    private static Transp montarTransp() {

        TNFe.InfNFe.Transp transp = new TNFe.InfNFe.Transp();
        transp.setModFrete(documento.getModalidadeFrete().getId().toString());

        //Transportador
        if (!documento.getTransportadorCpfOuCnpj().isEmpty()) {
            TNFe.InfNFe.Transp.Transporta transporta = new TNFe.InfNFe.Transp.Transporta();

            if (!documento.getTransportadorCpf().isEmpty()) {
                transporta.setCPF(Texto.soNumeros(documento.getTransportadorCpf()));
            } else {
                transporta.setCNPJ(Texto.soNumeros(documento.getTransportadorCnpj()));
            }

            if (documento.isTransportadorIeIsento()) {
                transporta.setIE("ISENTO");
            } else {
                transporta.setIE(Texto.soNumeros(documento.getTransportadorIe()));
            }

            transporta.setXNome(Texto.substring(Texto.removerEspeciais(documento.getTransportadorNome()), 0, 60));

            String xEnder = documento.getTransportadorEndereco() + " " + documento.getTransportadorNumero() + " " + documento.getTransportadorComplemento() + " " + documento.getTransportadorBairro();
            transporta.setXEnder(Texto.substring(xEnder, 0, 60));
            transporta.setXMun(documento.getTransportadorMunicipio().getNome());
            transporta.setUF(TUf.valueOf(documento.getTransportadorMunicipio().getEstado().getSigla()));

            transp.setTransporta(transporta);
            
        }
        
        //Veículo
        if (!documento.getTransporteVeiculoPlaca().isEmpty()) {
            TVeiculo veiculo = new TVeiculo();
            veiculo.setPlaca(Texto.removerEspeciais(documento.getTransporteVeiculoPlaca()));
            veiculo.setUF(TUf.fromValue(documento.getTransporteVeiculoUf()));
            
            if (!documento.getTransporteVeiculoRntc().isEmpty()) {
                veiculo.setRNTC(Texto.removerAcentos(documento.getTransporteVeiculoRntc()));
            }

            transp.setVeicTransp(veiculo);
        }

        return transp;
    }

    private static InfAdic montarInfAdic() {

        InfAdic infAdic = new InfAdic();

        if (!documento.getInformacoesAdicionaisFisco().isEmpty()) {
            infAdic.setInfAdFisco(documento.getInformacoesAdicionaisFisco());
        }

        String infoContribuinte = FiscalUtil.getMensagemValorAproximadoTributos(documento) + " "
                + documento.getInformacoesComplementaresContribuinte();

        infAdic.setInfCpl(infoContribuinte);

        return infAdic;
    }

    private static Cobr montarCobr() {

        Cobr cobr = new Cobr();

        //BigDecimal valorFatura = documento.getParcelasAPrazo().stream().map(Parcela::getValor).reduce(BigDecimal::add).get();
        BigDecimal valorFatura = documento.getTotalProdutos();

        Fat fat = new Fat(); //único
        fat.setNFat(documento.getId().toString()); //Y03 (1-60) Número da Fatura
        fat.setVOrig(Decimal.toStringComPonto(valorFatura)); //Y03 (13v2) Valor original da fatura
        fat.setVDesc("0.00"); //Y04 (13v2) Valor do desconto
        fat.setVLiq(Decimal.toStringComPonto(valorFatura)); //Y05 (13v2) Valor líquido da fatura
        cobr.setFat(fat);

        if (documento.getMovimentosFisicosServicos().isEmpty()) { //só exibe parcelas se não houver serviços no documento

            for (Parcela parcela : documento.getParcelas()) {
                Dup dup = new Dup();
                dup.setNDup(Texto.padLeftAndCut(String.valueOf(documento.getParcelas().indexOf(parcela) + 1), 3, '0'));

                String vencimento = parcela.getVencimento() != null ? parcela.getVencimento().toString() : parcela.getCriacao().toLocalDate().toString();

                dup.setDVenc(vencimento);
                dup.setVDup(Decimal.toStringComPonto(parcela.getValor())); //Y10 13v2 Valor da duplicata
                cobr.getDup().add(dup);
            }
        }

        return cobr;
    }

    private static Pag montarPag() {

        Pag pag = new Pag();
        TNFe.InfNFe.Pag.DetPag detPag = new TNFe.InfNFe.Pag.DetPag();

        if (documento.getFinalidadeEmissao().getId() == 4) { //4 - Devolução de mercadoria
            detPag.setTPag("90"); //Sem pagamento
            detPag.setVPag("0.00");
        } else {
            detPag.setTPag("99"); //Outros
            detPag.setVPag(Decimal.toStringComPonto(documento.getTotalProdutos()));
        }

        pag.getDetPag().add(detPag);

        return pag;
    }

}
