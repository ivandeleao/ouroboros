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
import model.mysql.bean.fiscal.nfe.FinalidadeEmissao;
import model.mysql.bean.principal.Constante;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.dao.endereco.CidadeDAO;
import model.mysql.dao.principal.ConstanteDAO;
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
        }

        //tem itens?
        if (documento.getMovimentosFisicos().isEmpty()) {
            mensagens.add("Adicione itens.");
        }

        //dados dos itens...
        //getUnidadeTributavel
        //getOrigem
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
            nNf = Ouroboros.NFE_PROXIMO_NUMERO;
            tipoEmissao = "1";
            cNf = Texto.padLeft(String.valueOf(new Random().nextInt(99999999)), 8, '0'); //8 - Código numérico que compõe a Chave de Acesso. Número aleatório gerado pelo emitente para cada NF-e para evitar acessos indevidos da NF-e. (v2.0) 
            dataHoraEmissao = ZonedDateTime.now();
            chave = new ChaveUtil(cUF, cnpjEmitente, modelo, serie, nNf, tipoEmissao, cNf, dataHoraEmissao.toLocalDateTime());
            cDV = chave.getDigitoVerificador();

            TEnviNFe enviNFe = new TEnviNFe();
            enviNFe.setVersao("4.00");
            enviNFe.setIdLote("1");
            enviNFe.setIndSinc("1");
            enviNFe.getNFe().add(montarTnfe());

            enviNFe = Nfe.montaNfe(NfeConfig.iniciarConfiguracoes(), enviNFe, true);

            //gravar próximo número da Nfe
            ConstanteDAO.save(new Constante("NFE_PROXIMO_NUMERO", String.valueOf(nNf + 1)));
            Ouroboros.NFE_PROXIMO_NUMERO = nNf + 1;

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
        for(DocumentoReferenciado docRef : documento.getDocumentosReferenciados()) {
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
        emit.setXNome(EMPRESA_RAZAO_SOCIAL);
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
            dest.setXNome(Texto.substring(d.getNome(), 0, 60));
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
        if(!d.getTelefone1().isEmpty()) {
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

        entrega.setXNome(documento.getEntregaNome());

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
            prod.setXProd(mf.getDescricao());
            prod.setNCM(mf.getNcm().getCodigo());

            if(!mf.getCest().isEmpty()) {
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

            if (mf.getDesconto().compareTo(BigDecimal.ZERO) > 0) {
                prod.setVDesc(Decimal.toStringComPonto(mf.getDesconto())); //I17 (13v2) Valor do desconto
            }

            if (mf.getAcrescimo().compareTo(BigDecimal.ZERO) > 0) {
                prod.setVOutro(Decimal.toStringComPonto(mf.getAcrescimo())); //I17a (13v2) Outras despesas acessórias
            }

            prod.setIndTot("1");
            det.setProd(prod);

            //Impostos
            TNFe.InfNFe.Det.Imposto imposto = new TNFe.InfNFe.Det.Imposto();

            
            
            //icms
            TNFe.InfNFe.Det.Imposto.ICMS icms = montarIcms(mf);
            JAXBElement<TNFe.InfNFe.Det.Imposto.ICMS> icmsElement = new JAXBElement<>(new QName("ICMS"), TNFe.InfNFe.Det.Imposto.ICMS.class, icms);
            imposto.getContent().add(icmsElement);

            //pis
            TNFe.InfNFe.Det.Imposto.PIS pis = new TNFe.InfNFe.Det.Imposto.PIS();
            TNFe.InfNFe.Det.Imposto.PIS.PISNT pisNT = new TNFe.InfNFe.Det.Imposto.PIS.PISNT();
            pisNT.setCST("08");
            pis.setPISNT(pisNT);

            //cofins
            TNFe.InfNFe.Det.Imposto.COFINS cofins = new TNFe.InfNFe.Det.Imposto.COFINS();
            TNFe.InfNFe.Det.Imposto.COFINS.COFINSNT cofinsNT = new TNFe.InfNFe.Det.Imposto.COFINS.COFINSNT();
            cofinsNT.setCST("08");
            cofins.setCOFINSNT(cofinsNT);

            JAXBElement<TNFe.InfNFe.Det.Imposto.PIS> pisElement = new JAXBElement<>(new QName("PIS"), TNFe.InfNFe.Det.Imposto.PIS.class, pis);
            imposto.getContent().add(pisElement);

            JAXBElement<TNFe.InfNFe.Det.Imposto.COFINS> cofinsElement = new JAXBElement<>(new QName("COFINS"), TNFe.InfNFe.Det.Imposto.COFINS.class, cofins);
            imposto.getContent().add(cofinsElement);

            det.setImposto(imposto);

            dets.add(det);
        }

        return dets;

    }

    private static ICMS montarIcms(MovimentoFisico mf) {
        TNFe.InfNFe.Det.Imposto.ICMS icms = new TNFe.InfNFe.Det.Imposto.ICMS();

        switch (mf.getIcms().getCodigo()) {
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
                icms102.setCSOSN("102");
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
                icms202.setCSOSN("202");
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
        icmstot.setVDesc(Decimal.toStringComPonto(documento.getTotalDescontoProdutos())); //W10 (13v2) Valor total do desconto
        icmstot.setVII("0.00");
        icmstot.setVIPI("0.00");
        icmstot.setVIPIDevol("0.00");
        icmstot.setVPIS(Decimal.toStringComPonto(documento.getTotalPis()));
        icmstot.setVCOFINS(Decimal.toStringComPonto(documento.getTotalCofins()));
        icmstot.setVOutro(Decimal.toStringComPonto(documento.getTotalOutros())); //W15 (13v2) Outras despesas acessórias
        icmstot.setVNF(Decimal.toStringComPonto(documento.getTotal())); //W16 (13v2) Valor total da NF-e
        //icmstot.setVTotTrib(---);
        
        total.setICMSTot(icmstot);

        return total;
    }

    private static Transp montarTransp() {

        TNFe.InfNFe.Transp transp = new TNFe.InfNFe.Transp();
        transp.setModFrete(documento.getModalidadeFrete().getId().toString());

        //TNFe.InfNFe.Transp.Transporta transporta = new TNFe.InfNFe.Transp.Transporta();
        //transporta.setCNPJ();
        return transp;
    }

    private static InfAdic montarInfAdic() {

        InfAdic infAdic = new InfAdic();

        
        if (!documento.getInformacoesAdicionaisFisco().isEmpty()) {
            infAdic.setInfAdFisco(documento.getInformacoesAdicionaisFisco());
        }

        String infoContribuinte = FiscalUtil.getMensagemValorAproximadoTributos(documento) + " " +
                documento.getInformacoesComplementaresContribuinte();
        
        infAdic.setInfCpl(infoContribuinte);

        return infAdic;
    }

    private static Cobr montarCobr() {

        Cobr cobr = new Cobr();

        BigDecimal valorFatura = documento.getParcelasAPrazo().stream().map(Parcela::getValor).reduce(BigDecimal::add).get();

        Fat fat = new Fat(); //único
        fat.setNFat(documento.getId().toString()); //Y03 (1-60) Número da Fatura
        fat.setVOrig(Decimal.toStringComPonto(valorFatura)); //Y03 (13v2) Valor original da fatura
        fat.setVDesc("0.00"); //Y04 (13v2) Valor do desconto
        fat.setVLiq(Decimal.toStringComPonto(valorFatura)); //Y05 (13v2) Valor líquido da fatura
        cobr.setFat(fat);

        for (Parcela parcela : documento.getParcelasAPrazo()) { //coleção
            Dup dup = new Dup();
            dup.setNDup(Texto.padLeft(parcela.getNumero().toString(), 3, '0'));
            dup.setDVenc(parcela.getVencimento().toString());
            dup.setVDup(Decimal.toStringComPonto(parcela.getValor())); //Y10 13v2 Valor da duplicata
            cobr.getDup().add(dup);
        }

        return cobr;
    }

    private static Pag montarPag() {

        Pag pag = new Pag();
        TNFe.InfNFe.Pag.DetPag detPag = new TNFe.InfNFe.Pag.DetPag();
        
        if(documento.getFinalidadeEmissao().getId() == 4) { //4 - Devolução de mercadoria
            detPag.setTPag("90"); //Sem pagamento
            detPag.setVPag("0.00");
        } else {
            detPag.setTPag("99"); //Outros
            detPag.setVPag(Decimal.toStringComPonto(documento.getTotal()));
        }
        
        pag.getDetPag().add(detPag);

        return pag;
    }

}
