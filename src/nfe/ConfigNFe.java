/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nfe;

import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.exception.CertificadoException;
import br.com.swconsultoria.nfe.dom.ConfiguracoesNfe;
import br.com.swconsultoria.nfe.dom.enuns.AmbienteEnum;
import br.com.swconsultoria.nfe.dom.enuns.EstadosEnum;
import ouroboros.Ouroboros;

/**
 *
 * @author ivand
 */
public class ConfigNFe {

    public static AmbienteEnum AMBIENTE = AmbienteEnum.HOMOLOGACAO;
    
    public static ConfiguracoesNfe iniciarConfiguracoes() {

        try {
            Certificado certificado = A1Pfx.getCertificado();
            
            return ConfiguracoesNfe.criarConfiguracoes(EstadosEnum.SP, AMBIENTE, certificado, Ouroboros.APP_PATH + "\\nfe\\schemas");

        } catch (CertificadoException e) {
            System.err.println("Erro: " + e);
        }

        return null;

    }
}
