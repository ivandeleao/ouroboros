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
import util.MwString;

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

    public static TEnviNFe montarEnviNfe(Venda doc) {
        
        try {
            documento = doc;
            cUF = EstadosEnum.getByCodigoIbge(Ouroboros.EMPRESA_ENDERECO_CODIGO_MUNICIPIO.substring(0, 2)); //Código IBGE do estado
            cnpjEmitente = MwString.soNumeros(Ouroboros.EMPRESA_CNPJ);
            modelo = "55";
            serie = Ouroboros.NFE_SERIE;
            nNf = Ouroboros.NFE_PROXIMO_NUMERO;
            tipoEmissao = "1";
            cNf = MwString.padLeft(String.valueOf(new Random().nextInt(99999999)), 8, '0'); //8 - Código numérico que compõe a Chave de Acesso. Número aleatório gerado pelo emitente para cada NF-e para evitar acessos indevidos da NF-e. (v2.0) 
            dataHoraEmissao = ZonedDateTime.now();
            chave = new ChaveUtil(cUF, cnpjEmitente, modelo, serie, nNf, tipoEmissao, cNf, dataHoraEmissao.toLocalDateTime());
            cDV = chave.getDigitoVerificador();
        

            TEnviNFe enviNFe = new TEnviNFe();
            enviNFe.setVersao("4.00");
            enviNFe.setIdLote("1");
            enviNFe.setIndSinc("1");
            enviNFe.getNFe().add(montarTnfe());

            
            enviNFe = Nfe.montaNfe(ConfigNFe.iniciarConfiguracoes(), enviNFe, true);
            
            //gravar próximo número da Nfe
            ConstanteDAO.save(new Constante("NFE_PROXIMO_NUMERO", String.valueOf(nNf + 1)));
            
            
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

        infNFe.getDet().addAll(montarDets());
        
        infNFe.setTotal(montarTotal());
        infNFe.setTransp(montarTransp());
        infNFe.setInfAdic(montarInfAdic());
        
        if(!documento.getParcelas().isEmpty()) {
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
        ide.setTpAmb(ConfigNFe.AMBIENTE.getCodigo());
        ide.setFinNFe(documento.getFinalidadeEmissao().getId().toString()); //Finalidade de emissão da NF-e 1-NF-e normal  ... até 4
        ide.setIndFinal(documento.getConsumidorFinal().getId().toString()); //Indica operação com Consumidor final 0-Normal 1-Consumidor final
        ide.setIndPres(documento.getTipoAtendimento().getId().toString()); //Indicador de presença do comprador no estabelecimento comercial no momento da operação 0-Não se aplica... 1-Operação presencial ... até 9
        ide.setProcEmi("0"); //Processo de emissão 0-Emissão de NF-e com aplicativo do contribuinte ... até 3
        ide.setVerProc(Ouroboros.APP_VERSION); //20 Versão do Processo de emissão da NF-e //Informar a versão do aplicativo emissor de NF-e. 

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
        
        if(!Ouroboros.EMPRESA_ENDERECO_COMPLEMENTO.trim().isEmpty()) {
            enderEmit.setXCpl(Ouroboros.EMPRESA_ENDERECO_COMPLEMENTO);
        }
        
        enderEmit.setXBairro(Ouroboros.EMPRESA_ENDERECO_BAIRRO);
        enderEmit.setCMun(Ouroboros.EMPRESA_ENDERECO_CODIGO_MUNICIPIO);
        
        Cidade cidade = new CidadeDAO().findByCodigoIbge(Ouroboros.EMPRESA_ENDERECO_CODIGO_MUNICIPIO);
        enderEmit.setXMun(cidade.getNome());
        enderEmit.setUF(TUfEmi.valueOf(cidade.getEstado().getSigla()));
        enderEmit.setCEP(MwString.soNumeros(Ouroboros.EMPRESA_ENDERECO_CEP));
        enderEmit.setCPais("1058");
        enderEmit.setXPais("BRASIL");
        enderEmit.setFone(MwString.soNumeros(Ouroboros.EMPRESA_TELEFONE));
        emit.setEnderEmit(enderEmit);
        emit.setIE(MwString.soNumeros(Ouroboros.EMPRESA_IE));
        emit.setCRT(documento.getRegimeTributario().getId().toString()); //Código de Regime Tributário

        return emit;
    }

    private static Dest montarDest() {
        Pessoa d = documento.getPessoa();
        
        TNFe.InfNFe.Dest dest = new TNFe.InfNFe.Dest();
        
        if(!d.getCpf().isEmpty()) {
            dest.setCPF(MwString.soNumeros(d.getCpf()));
        } else {
            dest.setCNPJ(MwString.soNumeros(d.getCnpj()));
        }
        
        if(ConfigNFe.AMBIENTE.equals(AmbienteEnum.HOMOLOGACAO)) {
            dest.setXNome("NF-E EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL");
        } else {
            dest.setXNome(MwString.substring(d.getNome(), 0, 60));
        }
        
        TEndereco enderDest = new TEndereco();
        enderDest.setXLgr(d.getEndereco());
        enderDest.setNro(d.getNumero());
        enderDest.setXBairro(d.getBairro());
        enderDest.setCMun(d.getCodigoMunicipio());
        enderDest.setXMun(MwString.substring(d.getMunicipio(), 0, 60));
        enderDest.setUF(TUf.fromValue(d.getUf()));
        enderDest.setCEP(d.getCepSoNumeros());
        enderDest.setCPais("1058");
        enderDest.setXPais("BRASIL");
        enderDest.setFone(MwString.soNumeros(d.getTelefone1()));
        dest.setEnderDest(enderDest);
        
        if(!d.getEmail().trim().isEmpty()) {
            dest.setEmail(d.getEmail());
        }
        
        dest.setIndIEDest(!d.isIeIsento() && !d.getIe().isEmpty() ? "1" : "2"); //Indicador da IE do Destinatário //1=Contribuinte ICMS (informar a IE do destinatário) ... até 9
        
        if(!d.isIeIsento() && !d.getIe().isEmpty()) {
            dest.setIE(MwString.soNumeros(d.getIe()));
        }

        return dest;
    }
    
    private static List<Det> montarDets() {
        
        List<Det> dets = new ArrayList<>();
        
        for(MovimentoFisico mf : documento.getMovimentosFisicosProdutos()) {
            Det det = new Det();
            det.setNItem(String.valueOf(documento.getMovimentosFisicosProdutos().indexOf(mf) + 1));

            //Produto
            Produto p = mf.getProduto();
            Prod prod = new Prod();

            prod.setCProd(mf.getCodigo());
            prod.setCEAN("SEM GTIN");
            prod.setXProd(mf.getDescricao());
            prod.setNCM(p.getNcm().getCodigo());
            
            //prod.setCEST(p.getCest());
            //prod.setIndEscala("S"); //???????????????????????????
            
            prod.setCFOP(p.getCfopSaidaDentroDoEstado().getCodigo().toString());
            prod.setUCom(mf.getUnidadeComercialVenda().toString());
            prod.setQCom(Decimal.toStringComPonto(mf.getSaldoLinearAbsoluto()));
            prod.setVUnCom(Decimal.toStringComPonto(mf.getValor()));
            prod.setVProd(Decimal.toStringComPonto(mf.getSubtotalItem()));
            prod.setCEANTrib("SEM GTIN");
            prod.setUTrib(mf.getUnidadeComercialVenda().toString());
            prod.setQTrib(Decimal.toStringComPonto(mf.getSaldoLinearAbsoluto()));
            prod.setVUnTrib(Decimal.toStringComPonto(mf.getValor()));
            
            if(mf.getValorFrete().compareTo(BigDecimal.ZERO) > 0) {
                prod.setVFrete(Decimal.toStringComPonto(mf.getValorFrete())); //I15 (13v2) Valor do frete
            }
            
            if(mf.getValorSeguro().compareTo(BigDecimal.ZERO) > 0) {
                prod.setVSeg(Decimal.toStringComPonto(mf.getValorSeguro())); //I16 (13v2) Valor do seguro
            }
            
            if(mf.getDesconto().compareTo(BigDecimal.ZERO) > 0) {
                prod.setVDesc(Decimal.toStringComPonto(mf.getDesconto())); //I17 (13v2) Valor do desconto
            }
            
            if(mf.getAcrescimo().compareTo(BigDecimal.ZERO) > 0) {
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
        
        switch(mf.getProduto().getIcms().getCodigo()) {
            case "101":
                TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN101 icms101 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN101();
                icms101.setOrig(mf.getProduto().getOrigem().getId().toString());
                icms101.setCSOSN("101");
                icms101.setPCredSN(Decimal.toStringComPonto(mf.getProduto().getAliquotaIcms())); //N29 3v2-4
                icms101.setVCredICMSSN("0.00"); //N30 13v2 Valor crédito do icms que pode ser aproveitado nos termos do art. 23 da LC 123 (Simples Nacional)
                icms.setICMSSN101(icms101);
                break;
                
            case "102":
            case "103":
            case "300":
            case "400":
                TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN102 icms102 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN102();
                icms102.setOrig(mf.getProduto().getOrigem().getId().toString());
                icms102.setCSOSN("102");
                icms.setICMSSN102(icms102);
                break;
                
            case "201":
                TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN201 icms201 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN201();
                icms201.setOrig(mf.getProduto().getOrigem().getId().toString());
                icms201.setCSOSN("201");
                icms201.setModBCST("0"); //N18 to do Modalidade de determinação da BC do ICMS ST
                icms201.setPMVAST("0.00"); //N19 3v2-4 to do Percentual da margem de valor Adicionado do ICMS ST
                icms201.setPRedBCST("0.00"); //N20 3v2-4 to do Percentual da Redução de BC do ICMS ST
                icms201.setVBCST("0.00"); //N21 13v2 to do Valor da BC do ICMS ST
                icms201.setPICMSST("0.00"); //N22 3v2 to do Alíquota do imposto do ICMS ST
                icms201.setVICMSST("0.00"); //N23 13v2 to do Valor do ICMS ST retido
                icms201.setPCredSN("0.00"); //N29 3v2-4 to do Alíquota aplicável de cálculo do crédito (SIMPLES NACIONAL)
                icms201.setVCredICMSSN("0.00"); //N30 13v2 to do Valor crédito do ICMS que pode ser aproveitado nos termos do art. 23 da LC 123 (SIMPLES NACIONAL)
                break;
                
            case "202":
            case "203":
                TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN202 icms202 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN202();
                icms202.setOrig(mf.getProduto().getOrigem().getId().toString());
                icms202.setCSOSN("202");
                icms202.setModBCST("0"); //N18 to do Modalidade de determinação da BC do ICMS ST
                icms202.setPMVAST("0.00"); //N19 3v2-4 to do Percentual da margem de valor Adicionado do ICMS ST
                icms202.setPRedBCST("0.00"); //N20 3v2-4 to do Percentual da Redução de BC do ICMS ST
                icms202.setVBCST("0.00"); //N21 13v2 to do Valor da BC do ICMS ST
                icms202.setPICMSST("0.00"); //N22 3v2 to do Alíquota do imposto do ICMS ST
                icms202.setVICMSST("0.00"); //N23 13v2 to do Valor do ICMS ST retido
                break;
                
            case "500":
                TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN500 icms500 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN500();
                icms500.setOrig(mf.getProduto().getOrigem().getId().toString());
                icms500.setCSOSN("500");
                icms500.setVBCSTRet("0.00"); //N26 13v2 to do Valor da BC do ICMS ST retido
                icms500.setVICMSSTRet("0.00"); //N19 13v2 to do Valor do ICMS ST retido E
                break;
                
            case "900":
                TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN900 icms900 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN900();
                icms900.setOrig(mf.getProduto().getOrigem().getId().toString());
                icms900.setCSOSN("900");
                icms900.setModBC("1"); //N13 to do Modalidade de determinação da BC do ICMS
                icms900.setVBC("0.00"); //N15 13v2 to do Valor da BC do ICMS
                icms900.setPRedBC("0.00"); //N14 3v2-4 to do Percentual da Redução de BC
                icms900.setPICMS("0.00"); //N16 3v2-4 to do Alíquota do imposto
                icms900.setVICMS("0.00"); //N17 13v2 to do Valor do ICMS E
                
                icms900.setModBCST("0.00"); //N18 to do Modalidade de determinação da BC do ICMS ST
                
                icms900.setPMVAST("0.00"); //N19 3v2-4 to do Percentual da margem de valor Adicionado do ICMS ST
                icms900.setPRedBCST("0.00"); //N20 3v2-4 to do Percentual da Redução de BC do ICMS ST
                icms900.setVBCST("0.00"); //N21 13v2 to do Valor da BC do ICMS ST
                icms900.setPICMSST("0.00"); //N22 3v2 to do Alíquota do imposto do ICMS ST
                icms900.setVICMSST("0.00"); //N23 13v2 to do Valor do ICMS ST retido
                icms900.setPCredSN("0.00"); //N29 3v2-4 to do Alíquota aplicável de cálculo do crédito (SIMPLES NACIONAL)
                icms900.setVCredICMSSN("0.00"); //N30 13v2 to do Valor crédito do ICMS que pode ser aproveitado nos termos do art. 23 da LC 123 (SIMPLES NACIONAL)
                break;
                
        }
        
        
        
        return icms;
    }

    private static Total montarTotal() {

        Total total = new Total();

        TNFe.InfNFe.Total.ICMSTot icmstot = new TNFe.InfNFe.Total.ICMSTot();
        icmstot.setVBC("0.00");
        icmstot.setVICMS("0.00");
        icmstot.setVICMSDeson("0.00");
        icmstot.setVFCP("0.00");
        icmstot.setVFCPST("0.00");
        icmstot.setVFCPSTRet("0.00");
        icmstot.setVBCST("0.00");
        icmstot.setVST("0.00");
        icmstot.setVProd(Decimal.toStringComPonto(documento.getTotalItensProdutos())); //W07 (13v2) Valor total dos produtos e serviços
        icmstot.setVFrete(Decimal.toStringComPonto(documento.getTotalFreteProdutos())); //W08 (13v2) Valor total do frete
        icmstot.setVSeg(Decimal.toStringComPonto(documento.getTotalSeguroProdutos())); //W09 (13v2) Valor total do seguro
        icmstot.setVDesc(Decimal.toStringComPonto(documento.getTotalDescontoProdutos())); //W10 (13v2) Valor total do desconto
        icmstot.setVII("0.00");
        icmstot.setVIPI("0.00");
        icmstot.setVIPIDevol("0.00");
        icmstot.setVPIS("0.00");
        icmstot.setVCOFINS("0.00");
        icmstot.setVOutro(Decimal.toStringComPonto(documento.getTotalAcrescimoProdutos())); //W15 (13v2) Outras despesas acessórias
        icmstot.setVNF(Decimal.toStringComPonto(documento.getTotal())); //W16 (13v2) Valor total da NF-e

        total.setICMSTot(icmstot);

        return total;
    }

    private static Transp montarTransp() {

        TNFe.InfNFe.Transp transp = new TNFe.InfNFe.Transp();
        transp.setModFrete("9");

        return transp;
    }

    private static InfAdic montarInfAdic() {

        InfAdic infAdic = new InfAdic();
        infAdic.setInfCpl("DOCUMENTO EMITIDO POR EMPRESA OPTANTE PELO SIMPLES NACIONAL;NAO GERA DIREITO A CREDITO FISCAL DE IPI\";\"PERMITE O APROVEITAMENTO DE CREDITO DE ICMS NO VALOR DE: R$17,66 CORRESPONDENTE A ALIQUOTA DE 2.56%\";Vendedor:1 - Guilherme Kavedikado;Valor Aproximado dos Tributos : R$ 206,97. Fonte IBPT (Instituto Brasileiro de Planejamento Tributario)");

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

        for(Parcela parcela : documento.getParcelasAPrazo()) { //coleção
            Dup dup = new Dup();
            dup.setNDup(MwString.padLeft(parcela.getNumero().toString(), 3, '0'));
            dup.setDVenc(parcela.getVencimento().toString());
            dup.setVDup(Decimal.toStringComPonto(parcela.getValor())); //Y10 13v2 Valor da duplicata
            cobr.getDup().add(dup);
        }
        
        return cobr;
    }

    private static Pag montarPag() {

        Pag pag = new Pag();
        TNFe.InfNFe.Pag.DetPag detPag = new TNFe.InfNFe.Pag.DetPag();
        detPag.setTPag("15");
        detPag.setVPag("100.00");
        pag.getDetPag().add(detPag);

        return pag;
    }

}
