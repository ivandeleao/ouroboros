/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import br.com.swconsultoria.nfe.Nfe;
import br.com.swconsultoria.nfe.dom.ConfiguracoesNfe;
import br.com.swconsultoria.nfe.dom.enuns.DocumentoEnum;
import br.com.swconsultoria.nfe.schema_4.retConsStatServ.TRetConsStatServ;
import nfe.ConfigNFe;

/**
 *
 * @author ivand
 */
public class StatusServico {
    
    public static void main(String[] args) {
        try {
            ConfiguracoesNfe configuracoes = ConfigNFe.iniciarConfiguracoes();
            
            TRetConsStatServ retorno = Nfe.statusServico(configuracoes, DocumentoEnum.NFE);
            
            System.out.println("Status: " + retorno.getCStat());
            System.out.println("Motivo: " + retorno.getXMotivo());
            
        } catch (Exception e) {
            System.err.println("Erro " + e);
            
        }
    }
    
}
