/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import br.com.swconsultoria.nfe.Nfe;
import br.com.swconsultoria.nfe.dom.ConfiguracoesNfe;
import br.com.swconsultoria.nfe.dom.enuns.DocumentoEnum;
import br.com.swconsultoria.nfe.dom.enuns.EstadosEnum;
import br.com.swconsultoria.nfe.dom.enuns.StatusEnum;
import br.com.swconsultoria.nfe.exception.NfeException;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TEnderEmi;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TEndereco;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TEnviNFe;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TNFe;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TRetEnviNFe;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TUf;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TUfEmi;
import br.com.swconsultoria.nfe.util.ChaveUtil;
import br.com.swconsultoria.nfe.util.XmlNfeUtil;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import nfe.NfeConfig;

/**
 *
 * @author ivand
 */
public class EnvioNfeAssincrono {

    public static void main(String[] args) {

        try {

            ConfiguracoesNfe configNfe = NfeConfig.iniciarConfiguracoes();

            TNFe nfe = new TNFe();
            TNFe.InfNFe infNFe = new TNFe.InfNFe();

            EstadosEnum cUF = EstadosEnum.SP;

            String cnpjEmitente = "04615918000104";
            String modelo = "55";
            Integer serie = 1;
            Integer nNf = 6001;
            String tipoEmissao = "1";
            String cNf = "32123002"; //8 - Código numérico que compõe a Chave de Acesso. Número aleatório gerado pelo emitente para cada NF-e para evitar acessos indevidos da NF-e. (v2.0) 
            ZonedDateTime dataHoraEmissao = ZonedDateTime.now();

            ChaveUtil chave = new ChaveUtil(cUF, cnpjEmitente, modelo, serie, nNf, tipoEmissao, cNf, dataHoraEmissao.toLocalDateTime());

            String cDV = chave.getDigitoVerificador();

            System.out.println("Chave: " + chave.getChaveNF() + " - " + cDV);

            infNFe.setId(chave.getChaveNF());
            infNFe.setVersao("4.00");

            //Identificação---------------------------------------------------------
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
            ide.setCMunFG("3550308"); //Código do Município de Ocorrência do Fato Gerador 
            ide.setTpImp("1"); //Formato de Impressão do DANFE 0-Sem geração de DANFE ... até 5
            ide.setTpEmis(tipoEmissao); //Tipo de Emissão da NF-e 1-Normal 2-Contingência ... até 7 
            ide.setCDV(cDV); //Dígito verificador da chave de acesso
            ide.setTpAmb(NfeConfig.AMBIENTE.getCodigo());
            ide.setFinNFe("1"); //Finalidade de emissão da NF-e 1-NF-e normal  ... até 4
            ide.setIndFinal("1"); //Indica operação com Consumidor final 0-Normal 1-Consumidor final
            ide.setIndPres("1"); //Indicador de presença do comprador no estabelecimento comercial no momento da operação 0-Não se aplica... 1-Operação presencial ... até 9
            ide.setProcEmi("3"); //Processo de emissão 0-Emissão de NF-e com aplicativo do contribuinte ... até 3
            ide.setVerProc("1.0"); //20 Versão do Processo de emissão da NF-e //Informar a versão do aplicativo emissor de NF-e. 
            infNFe.setIde(ide);

            //Emitente--------------------------------------------------------------
            TNFe.InfNFe.Emit emit = new TNFe.InfNFe.Emit();
            emit.setCNPJ(cnpjEmitente);
            emit.setXNome("Plotag Sistemas e Suprimentos Ltda");
            emit.setXFant("Plotag Sistemas e Suprimentos Ltda");
            TEnderEmi enderEmit = new TEnderEmi();
            enderEmit.setXLgr("Rua Maria Luiza");
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
            infNFe.setEmit(emit);

            //Destinatário----------------------------------------------------------
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
            infNFe.setDest(dest);

            //Itens-----------------------------------------------------------------
            TNFe.InfNFe.Det det = new TNFe.InfNFe.Det();
            det.setNItem("1");

            //Produto
            TNFe.InfNFe.Det.Prod prod = new TNFe.InfNFe.Det.Prod();
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
            TNFe.InfNFe.Det.Imposto imposto = new TNFe.InfNFe.Det.Imposto();
            TNFe.InfNFe.Det.Imposto.ICMS icms = new TNFe.InfNFe.Det.Imposto.ICMS();
            TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN102 icms102 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN102();
            icms102.setOrig("0");
            icms102.setCSOSN("102");
            icms.setICMSSN102(icms102);

            TNFe.InfNFe.Det.Imposto.PIS pis = new TNFe.InfNFe.Det.Imposto.PIS();
            TNFe.InfNFe.Det.Imposto.PIS.PISNT pisNT = new TNFe.InfNFe.Det.Imposto.PIS.PISNT();
            pisNT.setCST("08");
            pis.setPISNT(pisNT);
            
            //TNFe.InfNFe.Det.Imposto.PIS.PISAliq pisAliq = new TNFe.InfNFe.Det.Imposto.PIS.PISAliq();
            
            /*
            pisAliq.setCST("01");
            pisAliq.setVBC("0");
            pisAliq.setPPIS("0");
            pisAliq.setVPIS("0");
            pis.setPISAliq(pisAliq);*/

            TNFe.InfNFe.Det.Imposto.COFINS cofins = new TNFe.InfNFe.Det.Imposto.COFINS();
            TNFe.InfNFe.Det.Imposto.COFINS.COFINSNT cofinsNT = new TNFe.InfNFe.Det.Imposto.COFINS.COFINSNT();
            cofinsNT.setCST("08");
            cofins.setCOFINSNT(cofinsNT);
            
            /*TNFe.InfNFe.Det.Imposto.COFINS.COFINSAliq cofinsAliq = new TNFe.InfNFe.Det.Imposto.COFINS.COFINSAliq();
            cofinsAliq.setCST("01");
            cofinsAliq.setVBC("0");
            cofinsAliq.setPCOFINS("0");
            cofinsAliq.setVCOFINS("0");
            cofins.setCOFINSAliq(cofinsAliq);*/

            JAXBElement<TNFe.InfNFe.Det.Imposto.ICMS> icmsElement = new JAXBElement<TNFe.InfNFe.Det.Imposto.ICMS>(new QName("ICMS"), TNFe.InfNFe.Det.Imposto.ICMS.class, icms);
            imposto.getContent().add(icmsElement);

            JAXBElement<TNFe.InfNFe.Det.Imposto.PIS> pisElement = new JAXBElement<TNFe.InfNFe.Det.Imposto.PIS>(new QName("PIS"), TNFe.InfNFe.Det.Imposto.PIS.class, pis);
            imposto.getContent().add(pisElement);

            JAXBElement<TNFe.InfNFe.Det.Imposto.COFINS> cofinsElement = new JAXBElement<TNFe.InfNFe.Det.Imposto.COFINS>(new QName("COFINS"), TNFe.InfNFe.Det.Imposto.COFINS.class, cofins);
            imposto.getContent().add(cofinsElement);

            det.setImposto(imposto);
            infNFe.getDet().add(det);

            //Fim Itens-------------------------------------------------------------
            //Totais----------------------------------------------------------------
            TNFe.InfNFe.Total total = new TNFe.InfNFe.Total();

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
            infNFe.setTotal(total);

            TNFe.InfNFe.Transp transp = new TNFe.InfNFe.Transp();
            transp.setModFrete("9");
            infNFe.setTransp(transp);

            TNFe.InfNFe.InfAdic infAdic = new TNFe.InfNFe.InfAdic();
            infAdic.setInfCpl("DOCUMENTO EMITIDO POR EMPRESA OPTANTE PELO SIMPLES NACIONAL;NAO GERA DIREITO A CREDITO FISCAL DE IPI\";\"PERMITE O APROVEITAMENTO DE CREDITO DE ICMS NO VALOR DE: R$17,66 CORRESPONDENTE A ALIQUOTA DE 2.56%\";Vendedor:1 - Guilherme Kavedikado;Valor Aproximado dos Tributos : R$ 206,97. Fonte IBPT (Instituto Brasileiro de Planejamento Tributario)");
            infNFe.setInfAdic(infAdic);

            
            TNFe.InfNFe.Cobr cobr = new TNFe.InfNFe.Cobr();
            TNFe.InfNFe.Cobr.Fat fat = new TNFe.InfNFe.Cobr.Fat();
            fat.setNFat("1");
            fat.setVOrig("100.00");
            fat.setVDesc("0.00");
            fat.setVLiq("100.00");
            cobr.setFat(fat);
            
            TNFe.InfNFe.Cobr.Dup dup = new TNFe.InfNFe.Cobr.Dup();
            dup.setNDup("001");
            dup.setDVenc("2019-10-01");
            dup.setVDup("100.00");
            cobr.getDup().add(dup);
            
            infNFe.setCobr(cobr);
            
            
            
            
            TNFe.InfNFe.Pag pag = new TNFe.InfNFe.Pag();
            TNFe.InfNFe.Pag.DetPag detPag = new TNFe.InfNFe.Pag.DetPag();
            detPag.setTPag("15");
            detPag.setVPag("100.00");
            pag.getDetPag().add(detPag);
            infNFe.setPag(pag);

            nfe.setInfNFe(infNFe);

            // Monta EnviNfe
            TEnviNFe enviNFe = new TEnviNFe();
            enviNFe.setVersao("4.00");
            enviNFe.setIdLote("1");
            enviNFe.setIndSinc("1");
            enviNFe.getNFe().add(nfe);

            enviNFe = Nfe.montaNfe(configNfe, enviNFe, true);

            // Envia a Nfe para a Sefaz
            TRetEnviNFe retorno = Nfe.enviarNfe(configNfe, enviNFe, DocumentoEnum.NFE);

            if (!retorno.getCStat().equals(StatusEnum.LOTE_RECEBIDO.getCodigo())) {
                throw new NfeException("Status:" + retorno.getCStat() + " - Motivo:" + retorno.getXMotivo());
            }

            String recibo = retorno.getInfRec().getNRec();

            br.com.swconsultoria.nfe.schema_4.retConsReciNFe.TRetConsReciNFe retornoNfe;
            while (true) {
                retornoNfe = Nfe.consultaRecibo(configNfe, recibo, DocumentoEnum.NFE);
                if (retornoNfe.getCStat().equals(StatusEnum.LOTE_EM_PROCESSAMENTO.getCodigo())) {
                    System.out.println("Lote Em Processamento, vai tentar novamente apos 2 Segundo.");
                    Thread.sleep(2000);
                    continue;
                } else {
                    break;
                }
            }

            if (!retornoNfe.getCStat().equals(StatusEnum.LOTE_PROCESSADO.getCodigo())) {
                throw new NfeException("Status:" + retornoNfe.getCStat() + " - " + retornoNfe.getXMotivo());
            }
            if (!retornoNfe.getProtNFe().get(0).getInfProt().getCStat().equals(StatusEnum.AUTORIZADO.getCodigo())) {
                throw new NfeException("Status:" + retornoNfe.getProtNFe().get(0).getInfProt().getCStat() + " - " + retornoNfe.getProtNFe().get(0).getInfProt().getXMotivo());
            }

            System.out.println("Status: " + retornoNfe.getProtNFe().get(0).getInfProt().getCStat() + " - " + retornoNfe.getProtNFe().get(0).getInfProt().getXMotivo());
            System.out.println("Data: " + retornoNfe.getProtNFe().get(0).getInfProt().getDhRecbto());
            System.out.println("Protocolo: " + retornoNfe.getProtNFe().get(0).getInfProt().getNProt());

            System.out.println("XML Final: " + XmlNfeUtil.criaNfeProc(enviNFe, retornoNfe.getProtNFe().get(0)));

        } catch (Exception e) {
            System.err.println("Erro " + e);

        }

    }

}
