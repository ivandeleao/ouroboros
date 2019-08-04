/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nfe;

import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.exception.CertificadoException;
import java.io.FileNotFoundException;
import ouroboros.Ouroboros;

/**
 *
 * @author ivand
 */
public class A1Pfx {

    public static Certificado getCertificado() {

        try {
            String caminho = Ouroboros.APP_PATH + "\\custom\\nfe-certs\\certificado_a1.pfx";
            String senha = "753951";
            
            return CertificadoService.certificadoPfx(caminho, senha);
            
        } catch (CertificadoException | FileNotFoundException e) {
            System.err.println("Erro: " + e);
        }
        
        return null;
    }
}
