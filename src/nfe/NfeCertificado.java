/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nfe;

import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.TipoCertificadoA3;
import br.com.swconsultoria.certificado.exception.CertificadoException;
import java.io.FileNotFoundException;
import javax.swing.JOptionPane;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.Texto;

/**
 *
 * @author ivand
 */
public class NfeCertificado {

    public static Certificado getCertificado() {

        try {
            switch (Ouroboros.NFE_CERTIFICADO_TIPO) {
                case "A1":
                    return CertificadoService.certificadoPfx(Ouroboros.APP_PATH + "\\custom\\nfe-certs\\certificado_a1.pfx",
                            Ouroboros.NFE_CERTIFICADO_PIN);

                case "A3":
                    switch (Ouroboros.NFE_CERTIFICADO_MARCA) {
                        case "SmartCard":
                            return CertificadoService.certificadoA3(TipoCertificadoA3.TOKEN_ALADDIN.getMarca(),
                                    TipoCertificadoA3.TOKEN_ALADDIN.getDll(), Ouroboros.NFE_CERTIFICADO_PIN);

                        case "SafeWeb":
                            return CertificadoService.certificadoA3(TipoCertificadoA3.LEITOR_GEMPC_PERTO.getMarca(),
                                    TipoCertificadoA3.LEITOR_GEMPC_PERTO.getDll(), Ouroboros.NFE_CERTIFICADO_PIN);

                        case "Oberthur":
                            return CertificadoService.certificadoA3(TipoCertificadoA3.OBERTHUR.getMarca(),
                                    TipoCertificadoA3.OBERTHUR.getDll(), Ouroboros.NFE_CERTIFICADO_PIN);

                        case "eToken":
                            return CertificadoService.certificadoA3(TipoCertificadoA3.TOKEN_ALADDIN.getMarca(),
                                    TipoCertificadoA3.TOKEN_ALADDIN.getDll(), Ouroboros.NFE_CERTIFICADO_PIN);

                    }
                    break;

                case "REPOSITORIO_CNPJ":
                    String cnpj = Texto.soNumeros(Ouroboros.EMPRESA_CNPJ);

                    return CertificadoService.getCertificadoByCnpjCpf(cnpj);

            }
        } catch (CertificadoException | FileNotFoundException e) {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Erro ao iniciar certificado. " + e, "Erro", JOptionPane.ERROR_MESSAGE);

        }

        return null;
    }

}
