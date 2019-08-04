/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nfe;

import br.com.swconsultoria.nfe.Nfe;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import model.mysql.bean.principal.documento.Venda;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.*;
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
            cUF = EstadosEnum.SP;
            cnpjEmitente = MwString.soNumeros(Ouroboros.EMPRESA_CNPJ);
            modelo = "55";
            serie = 1;
            nNf = 6008;
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
            return enviNFe;

        } catch (NfeException e) {
            System.err.println("Erro ao montar a NFe " + e);
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
        infNFe.setCobr(montarCobr());
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
        ide.setNatOp("Revenda de Mercadorias");
        ide.setMod(modelo);
        ide.setSerie(serie.toString());
        ide.setNNF(nNf.toString());
        ide.setDhEmi(dataHoraEmissao.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ssXXX")));
        ide.setTpNF("1"); //Tipo de Operação 0-Entrada 1-Saída
        ide.setIdDest("1"); //Identificador de local de destino da operação 0-Operação interna 1-Operação interestadual 2-Operação com exterior
        ide.setCMunFG(Ouroboros.EMPRESA_ENDERECO_CODIGO_MUNICIPIO); //Código do Município de Ocorrência do Fato Gerador 
        ide.setTpImp("1"); //Formato de Impressão do DANFE 0-Sem geração de DANFE ... até 5
        ide.setTpEmis(tipoEmissao); //Tipo de Emissão da NF-e 1-Normal 2-Contingência ... até 7 
        ide.setCDV(cDV); //Dígito verificador da chave de acesso
        ide.setTpAmb(ConfigNFe.AMBIENTE.getCodigo());
        ide.setFinNFe("1"); //Finalidade de emissão da NF-e 1-NF-e normal  ... até 4
        ide.setIndFinal("1"); //Indica operação com Consumidor final 0-Normal 1-Consumidor final
        ide.setIndPres("1"); //Indicador de presença do comprador no estabelecimento comercial no momento da operação 0-Não se aplica... 1-Operação presencial ... até 9
        ide.setProcEmi("3"); //Processo de emissão 0-Emissão de NF-e com aplicativo do contribuinte ... até 3
        ide.setVerProc("1.0"); //20 Versão do Processo de emissão da NF-e //Informar a versão do aplicativo emissor de NF-e. 

        return ide;
    }

    private static Emit montarEmit() {

        Emit emit = new Emit();

        emit.setCNPJ(cnpjEmitente);
        emit.setXNome(EMPRESA_RAZAO_SOCIAL);
        emit.setXFant(EMPRESA_NOME_FANTASIA);
        TEnderEmi enderEmit = new TEnderEmi();
        enderEmit.setXLgr(EMPRESA_ENDERECO);
        enderEmit.setNro("321");
        enderEmit.setXCpl("4");
        enderEmit.setXBairro("Vila Pereira");
        enderEmit.setCMun("3550308");
        enderEmit.setXMun("São Paulo");
        enderEmit.setUF(TUfEmi.valueOf("SP"));
        enderEmit.setCEP("01127010");
        enderEmit.setCPais("1058");
        enderEmit.setXPais("BRASIL");
        enderEmit.setFone("1123587604");
        emit.setEnderEmit(enderEmit);
        emit.setIE("374053100113");
        emit.setCRT("1"); //Código de Regime Tributário

        return emit;
    }

    private static Dest montarDest() {

        TNFe.InfNFe.Dest dest = new TNFe.InfNFe.Dest();
        dest.setCNPJ("07133133000185");
        dest.setXNome("NF-E EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL");
        TEndereco enderDest = new TEndereco();
        enderDest.setXLgr("Rua Jaragua");
        enderDest.setNro("774");
        enderDest.setXBairro("Bom Retiro");
        enderDest.setCMun("3550308");
        enderDest.setXMun("Sao Paulo");
        enderDest.setUF(TUf.valueOf("SP"));
        enderDest.setCEP("01129000");
        enderDest.setCPais("1058");
        enderDest.setXPais("BRASIL");
        enderDest.setFone("33933501");
        dest.setEnderDest(enderDest);
        dest.setEmail("gui_calabria@yahoo.com.br");
        dest.setIndIEDest("1"); //Indicador da IE do Destinatário //1=Contribuinte ICMS (informar a IE do destinatário) ... até 9
        dest.setIE("374117970113");

        return dest;
    }
    
    private static List<Det> montarDets() {
        
        List<Det> dets = new ArrayList<>();
        
        Det det = new Det();
        det.setNItem("1");

        //Produto
        Prod prod = new Prod();
        
        prod.setCProd("B17025056");
        prod.setCEAN("SEM GTIN");
        prod.setXProd("PAPEL MAXPLOT- 170MX250MX56GRS 3");
        prod.setNCM("48025599");
        prod.setCEST("0000003");
        prod.setIndEscala("S");
        prod.setCFOP("5102");
        prod.setUCom("Rl");
        prod.setQCom("1.0000");
        prod.setVUnCom("100.0000000000");
        prod.setVProd("100.00");
        prod.setCEANTrib("SEM GTIN");
        prod.setUTrib("RL");
        prod.setQTrib("1.0000");
        prod.setVUnTrib("100.0000000000");
        prod.setIndTot("1");
        det.setProd(prod);

        //Impostos
        //icms
        TNFe.InfNFe.Det.Imposto imposto = new TNFe.InfNFe.Det.Imposto();
        TNFe.InfNFe.Det.Imposto.ICMS icms = new TNFe.InfNFe.Det.Imposto.ICMS();
        TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN102 icms102 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN102();
        icms102.setOrig("0");
        icms102.setCSOSN("102");
        icms.setICMSSN102(icms102);
        
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

        JAXBElement<TNFe.InfNFe.Det.Imposto.ICMS> icmsElement = new JAXBElement<>(new QName("ICMS"), TNFe.InfNFe.Det.Imposto.ICMS.class, icms);
        imposto.getContent().add(icmsElement);

        JAXBElement<TNFe.InfNFe.Det.Imposto.PIS> pisElement = new JAXBElement<>(new QName("PIS"), TNFe.InfNFe.Det.Imposto.PIS.class, pis);
        imposto.getContent().add(pisElement);

        JAXBElement<TNFe.InfNFe.Det.Imposto.COFINS> cofinsElement = new JAXBElement<>(new QName("COFINS"), TNFe.InfNFe.Det.Imposto.COFINS.class, cofins);
        imposto.getContent().add(cofinsElement);

        det.setImposto(imposto);
        
        dets.add(det);
        
        return dets;
        
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
        icmstot.setVProd("100.00");
        icmstot.setVFrete("0.00");
        icmstot.setVSeg("0.00");
        icmstot.setVDesc("0.00");
        icmstot.setVII("0.00");
        icmstot.setVIPI("0.00");
        icmstot.setVIPIDevol("0.00");
        icmstot.setVPIS("0.00");
        icmstot.setVCOFINS("0.00");
        icmstot.setVOutro("0.00");
        icmstot.setVNF("100.00");

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

        Fat fat = new Fat(); //único
        fat.setNFat("1");
        fat.setVOrig("100.00");
        fat.setVDesc("0.00");
        fat.setVLiq("100.00");
        cobr.setFat(fat);

        Dup dup = new Dup(); //coleção
        dup.setNDup("001");
        dup.setDVenc("2019-10-01");
        dup.setVDup("100.00");
        cobr.getDup().add(dup);

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
