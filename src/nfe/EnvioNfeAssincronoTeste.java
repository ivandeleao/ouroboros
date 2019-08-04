package nfe;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import br.com.swconsultoria.nfe.Nfe;
import br.com.swconsultoria.nfe.dom.ConfiguracoesNfe;
import br.com.swconsultoria.nfe.dom.enuns.DocumentoEnum;
import br.com.swconsultoria.nfe.dom.enuns.StatusEnum;
import br.com.swconsultoria.nfe.exception.NfeException;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TEnviNFe;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TRetEnviNFe;
import br.com.swconsultoria.nfe.util.XmlNfeUtil;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.principal.VendaDAO;
import nfe.ConfigNFe;
import nfe.MontarXml;

/**
 *
 * @author ivand
 */
public class EnvioNfeAssincronoTeste {

    public static void main(String[] args) {

        try {

            ConfiguracoesNfe configNfe = ConfigNFe.iniciarConfiguracoes();
            
            //VENDA PARA TESTE: 306

            Venda venda = new VendaDAO().findById(306);
            
            TEnviNFe enviNFe = MontarXml.montarEnviNfe(venda);

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
            System.err.println("Erro aqui " + e);

        }

    }

}
