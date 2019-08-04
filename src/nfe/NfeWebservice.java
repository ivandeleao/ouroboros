/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nfe;

import br.com.swconsultoria.nfe.Nfe;
import br.com.swconsultoria.nfe.dom.ConfiguracoesNfe;
import br.com.swconsultoria.nfe.dom.enuns.DocumentoEnum;
import br.com.swconsultoria.nfe.exception.NfeException;
import br.com.swconsultoria.nfe.schema_4.retConsStatServ.TRetConsStatServ;

/**
 *
 * @author ivand
 */
public class NfeWebservice {

    public static TRetConsStatServ statusServico() {

        try {
            ConfiguracoesNfe configuracoes = ConfigNFe.iniciarConfiguracoes();
            return Nfe.statusServico(configuracoes, DocumentoEnum.NFE);

        } catch (NfeException e) {
            System.err.println("Erro " + e);
            return null;

        }
    }
    
    
}
